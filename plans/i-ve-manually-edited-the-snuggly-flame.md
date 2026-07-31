# Phase E 切片 E-003(D-002):引入 ffmpeg 依赖 + ABI splits + 引擎 init 收纳 ffmpeg

## Context(为什么做)

E-002 已落地并**编译成功**(coroutines + 引擎 suspend + 冒烟入口)。用户选定推进 **D-002 ffmpeg**,并采纳我的建议:**加 ffmpeg 的同时开 ABI splits 控体积**;**版本号不改**(维持 `versionName="1.0"` / `versionCode=1`);ffmpeg 依赖已获批。

ffmpeg 用于下载 DASH 分离流后的音视频合并/格式转换。本切片只把依赖入 build、控住体积、并让引擎在 init 阶段一并初始化 ffmpeg,使冒烟入口能验证"ffmpeg 已就位"。真正的下载落盘/后处理调用属后续切片。

## 依赖核实(官方来源,已完成)

- 组件:`io.github.junkfood02.youtubedl-android:ffmpeg`,版本 **`0.18.1`**(复用现有 `youtubedlAndroid` 版本引用,与 library 同版)。
- 来源:Maven Central `.../youtubedl-android/ffmpeg/0.18.1/`(POM + AAR 均 HTTP 200;AAR ≈ **133 MB**,含 4 ABI 的 FFmpeg 原生库)。
- 许可:POM 声明 **GPL-3.0**;打包的 FFmpeg 二进制另受 FFmpeg 上游许可(LGPL/GPL)。与项目 GPL-3.0 开源路线兼容。**归属声明属后续必办项(见范围外)**。
- 新增传递依赖:`androidx.appcompat:1.4.2`、`commons-io:2.5`(纯 Compose 项目此前无 appcompat);core-ktx/kotlin-stdlib 由 Gradle 收敛到已有更高版本。
- FFmpeg API 核实(源码 tag 0.18.1):`object com.yausername.ffmpeg.FFmpeg`,`@Synchronized fun init(appContext: Context)`,失败抛 `YoutubeDLException`。

## 变更清单(4 个文件)

### 1. `android/gradle/libs.versions.toml`
- `[libraries]` 增:`ffmpeg = { group = "io.github.junkfood02.youtubedl-android", name = "ffmpeg", version.ref = "youtubedlAndroid" }`

### 2. `android/app/build.gradle.kts`
- `dependencies { }` 增:`implementation(libs.ffmpeg)`
- `android { }` 增 ABI 分包(控住 133MB AAR → 每 ABI 独立 APK):
  ```kotlin
  splits {
      abi {
          isEnable = true
          reset()
          include("arm64-v8a", "armeabi-v7a", "x86_64", "x86")
          isUniversalApk = false
      }
  }
  ```
  保留现有 `ndk { abiFilters ... }` 与 `packaging { jniLibs { useLegacyPackaging = true } }`(ffmpeg 同样在运行时解压 `libffmpeg.zip.so`,需 legacy packaging)。

### 3. `.../engine/YoutubeDlEngine.kt`(init 收纳 ffmpeg)
- `import com.yausername.ffmpeg.FFmpeg`
- `init()` 内,`YoutubeDL.init(appContext)` 之后加 `FFmpeg.init(appContext)`,同一 `try` 捕获 `YoutubeDLException` → `EngineInitException`。
- 边界:FFmpeg 完全封装在引擎实现内,UI/ViewModel 不接触(ADR §3/§8)。

### 4. `.../engine/EngineSmokeTest.kt`(文案)
- 成功文案由"✓ 初始化成功"改为"✓ 初始化成功(yt-dlp + ffmpeg)",使真机自检能确认 ffmpeg 已就位。逻辑不变(init 现已含 ffmpeg)。

> `ui/dev/EngineSmokeScreen.kt` 无需改动 —— 它经 `EngineSmokeTest.run` 间接触发。

## 关联影响

- 体积:开 ABI splits 后产出 4 个按 ABI 分包的 APK,单包只含该 ABI 的 ffmpeg(~30–40MB 量级),而非全量 130MB+。若用户只需 arm64,可后续进一步收窄。
- 依赖:+1 直接依赖(GPL-3.0)+ appcompat/commons-io 传递依赖。
- 运行时:init 多一步解压 ffmpeg;probe 不受影响;**本切片无下载/后处理调用路径**。
- 边界:无服务端/账号/云同步/远程日志;不涉及 Cookie。

## 回滚

- 移除 `implementation(libs.ffmpeg)` + `ffmpeg` 库项 + `splits{}` + `YoutubeDlEngine.init` 里的 `FFmpeg.init` + 文案改回即可。

## 验收(用户真机)

1. 工作区改完 → Make 手动推送 → `git fetch origin` / `git reset --hard origin/main`。
2. `cd android` → `.\gradlew.bat :app:assembleDebug` 构建成功;产物为按 ABI 分包的多个 APK(体积明显小于全量合包)。
3. 装机(选对应 ABI 的 APK)→「更多 → 引擎自检(开发)」运行:显示"✓ 初始化成功(yt-dlp + ffmpeg)"+ 标题/时长。

## 同批一并推送(非 D-002 代码,属收尾)

- `docs/03-dependency-decision.md`:标注 D-001 library「已入 build」、登记 D-002 ffmpeg(版本/来源/许可/传递依赖/体积/ABI 决策)。
- `PROGRESS.md`:同步 E-002 完成、E-003/D-002 进行中、版本号维持 1.0 的决定。

## 不在本切片范围(后续另审)

- **FFmpeg / 第三方 GPL 归属声明**:About 页「组件」区目前占位"后续显示第三方声明"——GPL-3.0 要求提供归属与源码获取途径,列为**必办的后续切片**(不阻塞本次构建,但发布前必须完成)。
- 下载落盘 MVP(存储权限/MediaStore/前台服务)、ffmpeg 后处理的真实调用、ViewModel、Room、DataStore 扩展、security-crypto。
- 进一步按需收窄 ABI(如仅 arm64-v8a)。
