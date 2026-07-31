# Phase E 前置:把 android/ 稳定并入 MediaFetch31(公开 monorepo)

## Context(为什么做这件事)

TCPGYT 引擎基于 `io.github.junkfood02.youtubedl-android` 0.18.1(ADR-001),Phase B 工具链闸门已结论化(`docs/09`):MediaFetch30 现有工具链无需降级即可承载 0.18.1。

进入 Phase E(Android 编码)前,必须先打通**代码交付管线**。已查明并确认的事实:

- **Agent 只有 `MediaFetch31` 读写权限**,推不进 MediaFetch30 → 双仓方案不可行,唯一可行是单仓。
- **Figma 同步是全量镜像**:提交 `264cb22`(figma[bot])把 `android/` 全部文件标为 `removed`,原因是**我的工作区里没有 android/**,同步即等于删除。之前那个 agent 很可能是**有意**不把 Android 传进公开仓(治理上的谨慎)。
- **用户已拍板:接受公开,直接并入 android/,走 GPL-3.0 开源路线**(推翻此前"挡在公开仓外"的做法)。

推论:要让 android/ 在公开仓里"待得住",**必须把完整 android/ 放进我的工作区**,由 Figma 权威通道同步回远端;走 MCP 直推远端撑不住,下次同步照样删。

## 现状事实(已核实)

- 远端 `origin/main = 4ba4db5`,**无 android/**;用户本地 `b320a98` 是其祖先,**含完整 android/**(`git ls-files` 确认 43 个文件,包名已是 `com.tcpg007014.tcpgyt`)。
- android/ 完整内容仍在远端历史 `b320a98`,可经公开 raw 地址逐字节取回:
  `https://raw.githubusercontent.com/Joeytisaly/MediaFetch31/b320a98/<path>`
- android/ 文件清单(来自用户 `git ls-files android`,43 项),含二进制:
  `gradle/wrapper/gradle-wrapper.jar`、`app/src/main/res/mipmap-*/*.webp`(8 个 webp)。

## 方案:由工作区做权威源,一次性取回 android/

### 步骤 1 — 把完整 android/ 落进工作区(我执行)

- 用 Bash `curl` 从 `raw.githubusercontent.com/.../b320a98/<path>` **逐文件**取回上述 43 个文件到 `/workspaces/default/code/android/`,保持目录结构。
- 文本文件(`.kt`/`.kts`/`.xml`/`.toml`/`.properties`/`.gitignore`/`gradlew`/`gradlew.bat`/`rules.keep`)与二进制(`gradle-wrapper.jar`、`*.webp`)一律用 curl 取原始字节,避免文本工具破坏二进制。
- 逐一核对:每个文件本地 `wc -c` 与远端 `b320a98` 上对应 blob `size` 一致;`gradle-wrapper.jar` 用 `file`/`unzip -t` 校验为有效 zip;`gradlew` 保留可执行位。
- 不改 `.gitignore` 根规则(它只忽略 `build/` `node_modules/` 等,不挡 android/)。

### 步骤 2 — 经 Figma 同步回远端(自动)

- 工作区新增 android/ 后,Figma 会以"Update files from Figma Make"把它同步进 `MediaFetch31`(权威全量镜像)→ 远端 android/ 恢复且此后不再被自身同步删除。
- 我用 MCP `get_file_contents` 复核远端 `MediaFetch31/android` 关键文件回位(`settings.gradle.kts`、`app/build.gradle.kts`、`gradle/libs.versions.toml`、`gradle/wrapper/gradle-wrapper.jar`)。

### 步骤 3 — 用户拉取(用户执行,仅一次对齐)

```
cd D:\test\MediaFetch31
git fetch origin
git reset --hard origin/main        # 本地与远端对齐(本地无独有改动,git log origin/main..HEAD 为空,安全)
```
> 注:用户本地 android/ 里的 `.gradle/ build/ .idea/ .kotlin/ local.properties` 属未跟踪产物,`reset --hard` 不动它们;跟踪的 android/ 源码将与远端一致。之后正常 `cd android && .\gradlew.bat :app:assembleDebug`。

## 常态开发环(打通后,先审后写)

1. 每个 Phase E 切片,我先出**变更方案**(目标/完整文件路径 `android/...`/摘要/关联影响/依赖名+精确版本+来源+许可+兼容依据/回滚/验收)→ 等用户批。
2. 批准后我**在工作区**改 `android/...` → Figma 同步进远端。
3. 用户 `git pull` → `cd android` → `gradlew` → 反馈结果 → 迭代。

## 范围与约束

- 本步**只取回并落地既有 android/,不新增/不改 Kotlin 业务代码、不装依赖、不改 Gradle 配置**。
- Phase E 每个依赖(D-001 library → D-002 ffmpeg → Room/协程/DataStore/security-crypto)加入 build 仍逐项先审后写。
- 硬边界:本地优先、无服务端/账号/云同步、Cookie 明文不落普通存储/日志、不绕过 DRM/付费墙、不引入 aria2c/命令模板/二进制自更新。

## 验收

- 工作区 `/workspaces/default/code/android/` 含全部 43 个跟踪文件,字节数与 `b320a98` 一致,`gradle-wrapper.jar` 校验为有效 zip。
- Figma 同步后,MCP 复核远端 `MediaFetch31/android` 关键构建文件回位。
- 用户 `git fetch && git reset --hard origin/main` 后 `gradlew :app:assembleDebug` 可正常构建(用户反馈)。
- 之后首个 Phase E 切片(D-001 library 加入 build)以变更方案形式提交待审 —— 属下一轮。

## 不在本次范围(后续阶段,另行审批)

- Phase E 任何 Kotlin 业务代码;加 Gradle 依赖、建下载引擎适配层。
- ffmpeg 精确 Maven 版本 + FFmpeg 二进制许可(LGPL/GPL)与归属。
- Room/协程/DataStore/security-crypto 精确版本与许可登记。
