# TCPGYT 项目交接笔记 (PROGRESS)

> 用途:对话被清理后,读取本文件即可恢复项目状态。每次有重要进展时更新本文件并推送 GitHub。
> 最近更新:2026-07-31(**Phase B 文档推进**:新增 `docs/05-data-model.md`(数据模型)与 `docs/04-api-contract.md`(接口契约)草案并推送 MediaFetch31。此前:前端原型阶段收工,三大主页面全部对齐画布)

## 0. ⚠️ 仓库与阶段真相(2026-07-31 订正,防再被带偏)

- **`MediaFetch31`(TypeScript,公开)= React 前端原型仓库**,与本地工作区完全同步(App.tsx/index.css/main.tsx blob SHA 一致)。构建环境依赖(package.json/pnpm-lock/vite)都在此。工作流:工作区改代码 → 推送 MediaFetch31 → 用户本地构建。
- **`MediaFetch30`(Kotlin,私有)= Android 目标工程**,但**目前只有默认 Compose 脚手架**(仅 `MainActivity.kt` + `ui/theme/`,包名还是默认 `com.example.mediafetch`)。Phase E 一行业务代码都没有;根目录有个 `MediaFetch.zip` 来历不明。
- **⚠️ 本文件第 5、6 节里那一堆 `.kt`(TcpgytTheme/MoreScreen/FilesScreen/TcpgytSegmented…)和"MediaFetch31/main 上的 Android 提交"是写歪的笔记 —— 那些文件在任何仓库里都不存在,是设计设想而非真实产物,已导致过一次跑偏。视为"未来 Android 端的设计参考约定",不要当成已完成代码。**
- 项目阶段(章程 §5):A 治理✅ / B 架构依赖⚠️(只有 ADR-001 + 新增 04/05,尚缺 03/06/07/08) / C 信息架构✅ / D 前端原型✅ / E Android 未开始。
- 引擎核心:**yt-dlp**(经 ADR-001 批准的 Android 封装 `youtubedl-android` 0.18.1 + ffmpeg;GPL-3.0 路线),允许引入成熟第三方库提效。

## 1. 项目概览

- 产品:**TCPGYT** —— Android 本地优先的音视频下载与下载管理产品。
- 当前仓库:React + Vite 8 + Tailwind CSS v4 + TypeScript 的**界面原型**,用作 Android(Kotlin/Jetpack Compose/Material 3)实现的设计参考。
- 开发者:TCPG007014 (YaR) · 邮箱 ChengYuan.tcpg@gnail.com · 包名 `com.tcpg007014.tcpgyt`。
- 治理约束见 `AGENTS.md`「TCPGYT 项目治理指令」:先审后写、本地优先、无服务端/注册/云同步、Cookie 严格隐私、下载须经引擎适配层。

## 2. 代码结构

- 原型是**单文件应用**:`src/App.tsx`(全部页面与弹层都在此)。
- 主题令牌在 `src/index.css`:5 套主题(blush/blue/mint/lavender/deep),通过 CSS 变量 `--tcp-primary` / `--tcp-primary-wash` / `--tcp-primary-soft` / `--tcp-primary-muted` 等驱动;玻璃拟态类 `tcp-glass` / `tcp-canvas`。
- 自定义内联 SVG `Icon` 组件(24×24 viewBox),按名称渲染。

## 3. 三个主页面(底部导航切换)

- **下载**:链接输入 → 模拟解析 → `FormatPicker` 底部弹层选格式 → 创建原型任务;任务卡片含状态机(排队中/下载中/已暂停/已完成/下载失败/已取消)。
- **文件**:已完成任务列表、搜索栏、分段筛选(全部/音频/视频)、排序、空状态。
- **更多(设置)**:下载偏好、保存位置、Cookie 管理(仅本地占位)、本地数据清理、外观(主题)、关于与支持。
- 所有操作均为**本地原型演示**,不执行真实下载,不读写设备文件。

## 4. 已确认的设计规则(重要,避免反复返工)

- **图标/组件必须对齐画布**(用户提供的 `src/imports/image-*.png`),不要做孤立的、破坏整体协调的局部微调。
- 底部导航:浮动玻璃胶囊,`w-[90%]`,`px-4 py-1.5`,`items-center justify-between`;三个图标 = `power`(下载)/`link`(文件,链条,**不是** infinity ∞)/`more`(更多,三点);激活态为浅粉底,无内描边。
- 文件页搜索栏左侧文件夹小标签 = **`primary-soft` 浅底 + 主色线条 `Folder`**(对齐画布 `<span bg-primary-soft text-primary><Icon folder/></span>`,与「保存位置」同图标)。⚠️ **已作废**旧规则「黄色双色实心 `FolderSolid`」——搜索栏本处不再用 `FolderSolid`(commit `51d5a7e`)。
- **分段控件(segmented control)是全局元素**(文件筛选、下载偏好·默认类型、格式选择·视频/音频),统一走 `ui/components/TcpgytSegmented.kt`:底槽 `themePrimaryMuted(theme)`(=`--tcp-primary-muted`)、选中项白底 + 主色文字 + 轻阴影、**全部 tab `FontWeight.Black`**。tab 文字用 `Box` 横纵居中 + `TextAlign.Center` + 垂直内边距 `8dp` + `PlatformTextStyle(includeFontPadding=false)`(否则安卓字形偏上、药丸显厚,不如画布协调)。不要在各处用 Material3 `FilterChip` 或自拼容器。
- 文件页排序 = **切换按钮**(点按在「最近添加↔名称」间切换,底色 `themePrimarySoft`),**不是**下拉弹窗/DropdownMenu。
- 文件操作弹层(打开文件/查看位置/移除记录/删除原型文件)= **纯文字行,无图标、无标题头**(画布如此):三行 `primary-pale` 底、删除行 `primary-soft` + 主色字。
- 文件详情/文件操作/删除确认等弹层的关闭键统一用 **共享 `SheetCloseButton`**(`primary-soft` 圆底 + 主色 ×,`ui/components/TcpgytBottomSheet.kt`)。删除确认 = **底部弹层**(取消/删除两栏,删除实心主色),不是 `AlertDialog`。
- 文件卡片第二行 = **仅格式**(`file.format`);完成勾选徽章 `#E8F5EC` 底 + `#7EBE9A` 图标。详情底部「打开文件」= `primary-wash` 底 + 主色字,「文件操作」= `surfaceVariant` 底。
- **「更多」页(已收尾,对齐画布 `settingPage` 分支)**:主列表 = 三组(下载/隐私/应用),每项圆形 `primary-soft` 图标底 + 17sp ExtraBold 文字 + chevron,**无副标题**;图标 下载偏好=Settings、保存位置=Folder、Cookie=Shield、本地数据=Sliders、外观=Spark、关于=Info。六子页统一结构:**单个外层玻璃卡**(`themeGlass` 底 + `rounded-26` + `p-20` + `themeHairline` 细边)包全部内容,内部分区 = `themeCardSurface`(=bg-white/70)`rounded-22` + 带下边框的 12sp Black `themeSectionLabel` 头。信息横幅 = `themePrimaryPale` 底 + `themePrimarySoft` 圆形图标底 + **线条图标**(Shield/Info,**不用 emoji**)。⚠️ 已确认删除的画布没有的元素:Cookie 页「关闭 Cookie」按钮、关于页「包名」行。自动开始下载 = **画布药丸**(开 `primary-wash`/关 `#EDF0F2`),**不是** Material Switch。外观主题卡强调色对齐画布精确值:blue `#3A8DCC`、lavender `#7B61B4`、deep `#83C8F2`。关于页 App 头 = **左对齐横排**(非居中)。

## 5. GitHub 同步约定(标准动作)

> ⚠️ 见第 0 节:下方"设计设想笔记"里的 `.kt` 条目是**未来 Android 设计参考,不对应任何真实仓库提交**,勿当已完成代码。真实的 MediaFetch31 提交是 React/文档类(如 PROGRESS、docs/04、docs/05)。

- 镜像仓库:`https://github.com/Joeytisaly/MediaFetch31`(默认分支 `main`)。
- **每次改完文件后,主动推送到该 GitHub 仓库**(用户长期指令)。
- 工作区的 git `origin` 是 Figma,不是 GitHub;GitHub 需通过 GitHub 连接器 / MCP 的 `create_or_update_file` 推送。
- 推送前用 `git rev-parse HEAD:<path>` 取 blob SHA 作为 `sha` 参数;推送后用返回的 `size` 与本地 `wc -c` 核对字节一致。
- 推送技巧:先确认本地文件 0 反斜杠,再用 `get_file_contents`(fields: sha)取当前 blob sha,直接把内容传给 `create_or_update_file`。
- 设计设想笔记(以下 `.kt` 均为未来 Android 设计参考,非真实提交):
  - TcpgytTheme 计划:`themePrimaryWash/Soft/Pale/Muted/SectionLabel/PrimaryMuted/NeutralAction/Glass/CardSurface/Hairline/Ink/InkText(theme)`。
  - TcpgytBottomSheet 计划:共享 `SheetSection`、`SheetCloseButton`。
  - FilesScreen / TasksScreen / MoreScreen 计划:详情三区、筛选共享控件、文件操作纯文字行、删除底部弹层。
  - TcpgytIcons 计划:Folder/Check/More/Link/Download/Settings/Shield/Sliders 等矢量。
  - TcpgytSegmented 计划:底槽 `themePrimaryMuted(theme)`、选中白底主色字、tab `Box` 横纵居中 + `includeFontPadding=false`。

## 6. Android 端设计参考约定(尚未实现,供 Phase E 落地时参考)

> ⚠️ 见第 0 节:以下 `.kt` 均**未在 MediaFetch30 中实现**(该工程目前仅默认脚手架)。这是"应该怎么建"的约定,不是"已经建好"。

- 主题辅助色计划走 `TcpgytTheme.kt` 按主题(Blush/Blue/Mint/Lavender/Night)分支函数,不散落硬编码,对应 index.css 同名 token。
- 「更多」子页计划共用私有积木(`MoreScreen.kt`):`GlassCard`、`WhiteSection(title?)`、`InfoBanner(icon,title,body)`、`FillButton`。
- 详情/弹层分区计划用 `TcpgytBottomSheet.kt` 的 `SheetSection(label, containerColor)`。
- 图标计划统一用 `TcpgytIcons.kt`(24×24 矢量);避免 emoji 字形。
- 分段控件计划统一用 `TcpgytSegmented.kt`,依赖 `themePrimaryMuted(theme)`。

## 7. 待办 / 下一步

- **前端原型阶段已收工(2026-07-31)**:三大主页面(下载 / 文件 / 更多)全部对齐画布并收尾,前端原型告一段落。
- **Phase B 文档(2026-07-31)**:已新增 `docs/05-data-model.md`(数据模型)+ `docs/04-api-contract.md`(接口契约)草案,均"待审批"状态。下一批文档待办:`docs/03`(依赖决策记录)、`docs/06`(隐私安全合规)、`docs/07`(测试验收)、`docs/08`(品牌捐赠占位)。
- **Phase E 前置闸门(未开始)**:①上述 Phase B 文档补齐;②工具链版本审批(核实 MediaFetch30 现有 Kotlin 2.4.10 / AGP 9.1 能否带 youtubedl-android 0.18.1,按官方兼容矩阵定 AGP/Kotlin/Compose/Room/JDK/SDK);③再进 Android:MediaFetch30 改包名 `com.example.mediafetch`→`com.tcpg007014.tcpgyt` + 移植三页面 + DownloadCoordinator 状态机 + 引擎适配层 + Room + Cookie Vault。全部需逐切片先审后写。
- **未落地(用户暂缓)**:文件操作弹层(打开文件 / 查看位置 / 移除记录 / 删除原型文件,`App.tsx` 第 179 行)文字居中。同类纯文字菜单还有下载偏好选项弹层(prefPicker,第 185 行)。带图标的格式选择(第 63 行)/ 筛选(第 177 行)弹层不做居中(会与行首图标冲突)。如需恢复此项:把这两处纯文字菜单按钮的 `text-left` 改为 `text-center` 即可。
- **空状态徽章配色**:画布用 `progress-soft/strong` 青绿,安卓无对应令牌,当前就近沿用 `primaryContainer`+主色。如需精确复刻青绿,须新增 progress 令牌到 `TcpgytTheme.kt`(待用户定夺)。
- 后续任何上线/发布/签名/数据收集/远端服务 = 新阶段,须重新获得书面批准。
