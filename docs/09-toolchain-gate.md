# TCPGYT 工具链版本审批闸门（Toolchain Gate v0.1）

**状态：草案（待审批）**
**关联：** `docs/00-project-charter.md`（§3 决策与研究规则、§5 专项约束）、`docs/02-architecture.md`（ADR-001）、`docs/03-dependency-decision.md`（§1 D-001、§5-1）
**核验日期：** 2026-07-31

> 本文件回答 `docs/03 §5-1` 挂起的 **[待审批]** 硬闸门:**Android 目标工程（MediaFetch30）现有工具链能否承载 `youtubedl-android` 0.18.1**。
> 一切版本事实来自官方源（git tag/commit、仓库内构建文件、版本目录），不臆测"最新版"（章程 §3）。
> **本闸门是决策记录,非引入批准** —— 实际加入 build 依赖属 Phase E,逐依赖先审后写（章程 §2/§6）。

## 0. 仓库关系（已核实）

- `MediaFetch31`（公开）:用户唯一 `git pull` / 构建源;含前端 `src/` 与全部文档 `docs/`。
- `MediaFetch30`（私有）:真实 Android（Kotlin/Compose）工程,是本闸门核验的**对象**;用户本地置于 `MediaFetch31/android` 下用 gradlew 构建。
- 本文件存放于 `MediaFetch31/docs`;引用 `MediaFetch30` 仅作 Android 工程对象,非推送目标。

## 1. 已核验的官方事实

### 1.1 youtubedl-android 0.18.1（依赖侧）

来源:上游 `yausername/youtubedl-android` git tag `0.18.1`（tree commit `864791a`）;Maven 发布方坐标 `io.github.junkfood02.youtubedl-android`（Maven Central 确认,见 03 §1）。

| 项 | 值 | 来源文件 |
|---|---|---|
| 发布形态 | AAR（Android Library） | `library/build.gradle.kts`（`com.android.library`） |
| `minSdk` | **24** | `library/build.gradle.kts` |
| `compileSdk` | **34** | `library/build.gradle.kts` |
| 库自身构建 AGP | `8.13.0`（`com.android.tools.build:gradle`） | 根 `build.gradle.kts` buildscript |
| 库自身构建 Kotlin | `1.7.22` | 根 `build.gradle.kts`（`kotlin_version`） |
| 传递依赖 | appcompat `1.4.2`、core-ktx `1.8.0`、jackson-databind/annotations `2.11.1`、commons-io `2.5` | `library/build.gradle.kts` + 根 extra |
| 原生库 | 打包 yt-dlp/python;ffmpeg、aria2c 为独立可选模块 | README |

> 库"自身构建"的 AGP/Kotlin 仅影响其编译产物,**不要求消费方版本一致**;消费方只依赖已发布的 AAR。

### 1.2 MediaFetch30 现有工具链（消费侧）

来源:`Joeytisaly/MediaFetch30` main 的 `gradle/libs.versions.toml`、`app/build.gradle.kts`、`gradle/wrapper/gradle-wrapper.properties`。

| 项 | 值 |
|---|---|
| Gradle wrapper | `9.3.1` |
| AGP | `9.1.0` |
| Kotlin | `2.4.10` |
| Compose BOM | `2026.02.01` |
| `compileSdk` | `36`（minorApiLevel 1） |
| `minSdk` | `24` |
| `targetSdk` | `36` |
| JDK / Java（compileOptions） | `11`（source/target compatibility） |

## 2. 兼容矩阵与结论

| 维度 | 依赖要求（0.18.1） | MediaFetch30 现状 | 判定 |
|---|---|---|---|
| `minSdk` | 24 | 24 | ✅ 相等 |
| `compileSdk` | 34（AAR 内） | 36 | ✅ 消费方 ≥ 库,兼容 |
| AGP | 库以 8.13 构建 | 消费方 9.1.0 | ✅ 新 AGP 消费旧 AAR 向后兼容 |
| Kotlin | 库以 1.7.22 构建 | 消费方 2.4.10 | ✅ Kotlin 消费方向后兼容旧库 |
| Gradle | —（不约束消费方） | 9.3.1 | ✅ 不冲突 |

**结论:MediaFetch30 现有工具链无需降级 AGP/Kotlin/SDK,即可承载 `youtubedl-android` 0.18.1（AAR）。**

## 3. 遗留注意点（不阻断本闸门,Phase E 处理）

1. **传递依赖偏旧**:appcompat 1.4.2 / core-ktx 1.8.0 等低于 MediaFetch30 当前 Compose 栈的常规版本;交由 Gradle 依赖解析或按需 `constraints` 对齐,不在本闸门定死。
2. **JDK 运行期**:AGP 9.x 运行 Gradle 通常需 JDK 17+;此为 MediaFetch30 既有构建环境事项,与本库无关。`compileOptions` 的 `sourceCompatibility 11` 为字节码目标,不冲突。
3. **原生库集成**:消费方需 `abiFilters`（x86/x86_64/armeabi-v7a/arm64-v8a）+ `android:extractNativeLibs="true"`;API 30+ 作用域存储限制(README)。属 Phase E 集成事项。
4. **ffmpeg 模块**:精确 Maven 发布版本 + FFmpeg 二进制许可(LGPL/GPL)与归属,仍 **[待审批]**(见 03 §5-2)。

## 4. 与 03 的联动

- 本文件**闭合** `docs/03 §5-1`(工具链兼容矩阵结论)。
- 仍保持 [待审批]:03 §5-2(ffmpeg 版本/许可)、03 §5-3(Room/协程/DataStore/security-crypto 支撑库版本)、03 §5-4(yt-dlp 自更新机制)。

## 5. 下一步

- 进入 Phase E:逐依赖申请加入 build（先审后写),先 D-001 library,再核验 D-002 ffmpeg。
- 解决 `android/` 交付路径:Kotlin 改动须能进用户 `git pull`(提交进 MediaFetch31/android vs 另行从 MediaFetch30 同步),Phase E 启动前拍板。
