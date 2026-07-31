# TCPGYT 依赖决策记录（Dependency Decision Record v0.1）

**状态：草案（待审批）**
**关联：** `docs/00-project-charter.md`（§3 不变量、§5 专项约束）、`docs/02-architecture.md`（ADR-001）、`docs/04-api-contract.md`、`docs/05-data-model.md`
**核验日期：** 2026-07-31

> 本文件按章程 §3「决策与研究规则」记录每个**外部依赖**的确切来源、版本、许可证与兼容依据。
> 依赖事实一律来自官方来源（Maven Central / 源码仓库 / 官方 LICENSE），不臆测"最新版"。
> 未坐实处标 **[待审批]**，留待「工具链版本审批闸门」连同兼容矩阵一并定案，不在本文件擅自定死。

## 0. 决策原则（对齐章程 §3）

- 事实来源优先级：官方文档 / GitHub Release·Tag / Maven Central / 源码仓库。
- **禁止**动态版本、宽泛版本范围、未经来源核验的"最新版"。
- GPL/AGPL/商业等受限许可,引入前必须先说明许可影响并获批（章程 §3）。
- 下载能力必须经引擎适配层,UI/ViewModel 不得直接拼命令（章程 §5、ADR §3/§8-8）。
- **本文件只记录决策与事实,不等于批准引入**;实际加入 build 依赖属于 Phase E,须逐依赖单独审批。

## 1. 核心下载引擎

### D-001 youtubedl-android（yt-dlp 的 Android 封装）

| 项 | 值 | 来源/核验 |
|---|---|---|
| 用途 | 封装 yt-dlp,提供 Android 侧解析与下载能力（ADR-001 已选定） | `docs/02` ADR-001 |
| Maven 坐标 | `io.github.junkfood02.youtubedl-android:library` | Maven Central 坐标 `pkg:maven/io.github.junkfood02.youtubedl-android/library@0.18.1`（central.sonatype.com 确认存在） |
| 版本 | `0.18.1` | 同上（Maven Central 已发布）;原仓 `yausername/youtubedl-android` git tag 最新亦为 `0.18.1` |
| 许可证 | **GPL-3.0** | 源码 `yausername/youtubedl-android/LICENSE` 逐条确认为 GNU GPL v3 |
| 上游关系 | `yausername` 为原作者仓(git tag 最新到 0.18.1);`JunkFood02` fork 为 Maven 发布方(Seal 应用同源),两仓 git tag 存在滞后但 Maven 0.18.1 已发布 | 两仓 tag 列表对比 |

**兼容依据**：AGP / Kotlin / Compose / JDK / min&target SDK 的精确兼容矩阵 **[待审批]**（工具链闸门核验:MediaFetch30 现有 Kotlin 2.4.10 / AGP 9.1 能否承载 0.18.1）。

### D-002 ffmpeg（youtubedl-android 附属,后处理/合流）

| 项 | 值 | 来源/核验 |
|---|---|---|
| 用途 | 音视频合流、转封装等后处理(经引擎适配层封装,ADR §7) | ADR-001 |
| Maven 坐标 | `io.github.junkfood02.youtubedl-android:ffmpeg` | 与 D-001 同 group |
| 版本 | **[待审批]**(常规与 library 同步 `0.18.1`,但本次未逐一坐实 Maven 发布版本) | — |
| 许可证 | 随附二进制许可(FFmpeg 及其编译选项的 LGPL/GPL 影响) **[待审批]** | 需在引入前单列 FFmpeg 构建与归属核验(章程 §5) |

### D-003 aria2c / 其他下载加速二进制

- **明确不引入**(除非另行审批)。ADR §8-8、章程 §5 硬约束:不得自行加入 aria2c / 命令模板 / 二进制下载更新逻辑。

## 2. GPL-3.0 合规影响（章程 §3 受限许可说明）

- D-001(及可能的 D-002)为 **GPL-3.0 copyleft**:一旦静态/动态链接进 TCPGYT APK 并分发,**整个被分发的应用须以 GPL-3.0 兼容方式提供,并提供对应源码**。
- 影响面:
  1. TCPGYT 应用发布时须开源(或以 GPL-3.0 兼容方式提供 Corresponding Source);
  2. 分发渠道须满足 GPL §6 的源码获取途径;
  3. FFmpeg 二进制的编译选项决定其 LGPL/GPL 归属,须在引入前单独核验(D-002 [待审批])。
- **本项目定位为本地优先、无服务端的开源工具**,与 GPL-3.0 路线不冲突;但**正式发布/签名/分发属新阶段**,须重新获得书面批准(章程 §5、§6)。

## 3. 现有工程依赖（原型侧,已在用,仅登记）

> 以下为 `MediaFetch31`(React 原型)现有依赖,非本次新增决策,登记以备追溯。精确版本以 `package.json` 为准。

| 组件 | 用途 | 许可证 |
|---|---|---|
| React / React DOM 19 | 原型 UI | MIT |
| Vite 8 + `@vitejs/plugin-react` | 构建/开发服务 | MIT |
| Tailwind CSS v4 + `@tailwindcss/vite` | 样式 | MIT |
| TypeScript 5.7 | 类型 | Apache-2.0 |

- 原型阶段**不引入**任何下载引擎、FFmpeg、Cookie、分析或远程日志组件(章程 §2 审批闸门)。

## 4. 明确排除的依赖类别（章程 §1/§5 硬约束）

- 任何服务端 / 账号 / 云同步 / 行为分析 / 远程日志 SDK。
- 广告、付费墙、验证码、DRM 或访问控制绕过组件。
- 浏览器/WebView Cookie 自动抓取库。
- 未经来源核验、动态版本或宽泛范围的任何依赖。

## 5. 本文件未决项（[待审批]，留待工具链闸门）

1. D-001 与 MediaFetch30 现有 Kotlin 2.4.10 / AGP 9.1 的官方兼容矩阵结论;必要时回退到兼容的 AGP/Kotlin/Compose/JDK/SDK 组合。
2. D-002 ffmpeg 的精确 Maven 发布版本 + FFmpeg 二进制许可(LGPL/GPL)与归属文件。
3. Room / 协程 / DataStore / Security-crypto(Keystore)等 Phase E 支撑库的精确版本与许可(下批随架构落地登记)。
4. 是否需要 yt-dlp 自更新机制(章程 §5 禁止自行二进制下载更新;如需,须新方案审批)。

## 6. 结论

- 已核验事实:D-001 坐标 `io.github.junkfood02.youtubedl-android:library:0.18.1` 于 Maven Central 存在,许可证 GPL-3.0。
- 未坐实项均标 [待审批],不构成引入批准。
- **下一步**:进入「工具链版本审批闸门」,以官方兼容矩阵核验版本组合,再逐依赖申请加入 build(Phase E,先审后写)。
