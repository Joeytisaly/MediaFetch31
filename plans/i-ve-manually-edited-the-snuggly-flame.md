# 主线 A:下载落盘 MVP —— 分阶段拆分(逐片先审后写)

## Context

E-001~E-003 已让引擎(suspend + 结构化取消)+ yt-dlp + ffmpeg 在真机跑通「init + probe」。主线 A 把「能解析」推进到「能真正下载到设备并可见」。因涉及**存储模型、前台服务、权限、UI 状态机**,一次做完风险高,故拆为 A-1~A-4,每片独立可构建、可验收,逐片先审后写。

## 关键技术约束(决定分片方式)

- **yt-dlp 写的是真实文件路径**,不是 `content://`。→ 落盘分两步:先下到**应用专属目录**(真实路径,无需存储权限),完成后再**发布到 MediaStore**(公共 Movies/Music/Downloads,相册/文件可见)。
- **Android 10+ 分区存储**:用 MediaStore,不申请 `WRITE_EXTERNAL_STORAGE`;应用专属目录零权限。
- **长任务须前台服务 + 通知**:Android 13+ 需 `POST_NOTIFICATIONS`;Android 14+ 前台服务需 `FOREGROUND_SERVICE` + 具体类型(`FOREGROUND_SERVICE_DATA_SYNC`)。
- 下载必须经引擎适配层(已具备 `DownloadEngine.download` suspend);UI/ViewModel 不拼命令。
- 删除须区分:删任务记录 / 删临时文件 / 删用户媒体文件(章程 §5)。

## A-1 —— 最小真实落盘(应用专属目录,零新权限)

- 复用「引擎自检(开发)」屏:在 probe 之后加「下载到应用目录」按钮,调用 `engine.download(request, onProgress)`,`outputDir = context.getExternalFilesDir(...)`(真实路径,无需权限)。
- 进度回调驱动一个百分比文本;完成后显示落地文件路径。
- 验收:真机点下载,文件出现在应用专属目录,自检屏显示进度→完成+路径。
- 影响:无新依赖、无新权限、无服务;纯验证 download 链路 + 进度 + 取消。

## A-2 —— 前台服务 + 进度通知 + 可取消

- 新增 `DownloadService`(前台服务),把 A-1 的下载迁入;通知栏显示进度,通知上「取消」→ 结构化取消协程(→ 销毁 yt-dlp 进程)。
- Manifest:`POST_NOTIFICATIONS`(运行时请求)、`FOREGROUND_SERVICE`、`FOREGROUND_SERVICE_DATA_SYNC`;`<service>` 声明。
- 验收:息屏/切后台下载不中断,通知进度更新,通知取消生效。

## A-3 —— 发布到 MediaStore(设备可见)

- 下载完成后,把应用目录产物按类型 insert 进 MediaStore(视频→Movies、音频→Music,或统一 Downloads),写入后清理临时文件。
- 删除语义落地:删记录 / 删临时 / 删媒体三者分离(对齐 §5 与原型「本地数据」文案)。
- 验收:下载完成后在系统相册/文件看到媒体;三类删除行为正确区分。

## A-4 —— 接线到 UI 状态机(替换原型模拟)

- 引入 `ViewModel`,把下载页链接输入 → 真实 probe → 格式选择 → 真实 download,任务页反映真实状态机(排队/下载/暂停/完成/失败/取消)。
- 任务状态持久化的 Room 落地拆到后续(A-4 先内存态,Room 单列切片)。
- 验收:从下载页发起真实任务,任务页/文件页反映真实进度与结果。

## 版本与治理

- 每片一个 `versionName`/`versionCode` 是否 bump 由用户定(当前维持 1.0)。
- 每片:依赖(若有)先审、权限先审、构建/真机验收、§6 汇报。
- 发布/签名/分发仍属独立阶段,另行书面批准。

## A-1b —— yt-dlp 内核更新(已批准,解 YouTube 403)

真机验证发现内置 yt-dlp(2025.11.12,>90 天)解不开 YouTube n-challenge,所有下载 403 Forbidden。用 youtubedl-android 自带官方方法 `YoutubeDL.updateYoutubeDL(ctx, UpdateChannel.STABLE)`(拉 yt-dlp 官方 Release)更新;仅用户主动触发,无后台/自动更新。同时修 A-1 的进度 -1 显示与取消响应。

改 4 文件:
- `engine/EngineModels.kt` — 加 `enum EngineUpdateStatus { UPDATED, ALREADY_LATEST }` + `data class EngineUpdate(status, version)`
- `engine/DownloadEngine.kt` — 接口加 `suspend fun update(): EngineUpdate`
- `engine/YoutubeDlEngine.kt` — 实现 `update()`(IO 调 `updateYoutubeDL` + `version`)
- `ui/dev/EngineSmokeScreen.kt` — 加「更新 yt-dlp 内核」按钮;进度 -1→「准备中…」;取消改直接 `engine.cancel(taskId)`

无新依赖/权限,版本维持 1.0。

## 建议起点

先做 **A-1**(零权限、零依赖、复用现有自检屏),把 download+进度+取消在真机验证扎实,再逐步加服务与 MediaStore。
