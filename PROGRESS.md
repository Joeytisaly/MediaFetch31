# TCPGYT 项目交接笔记 (PROGRESS)

> 用途:对话被清理后,读取本文件即可恢复项目状态。每次有重要进展时更新本文件并推送 GitHub。
> 最近更新:2026-07-31(**前端原型阶段收工**:三大主页面「下载 / 文件 / 更多」全部对齐画布并收尾。「更多」页:主列表 + 六子页 1:1 复刻画布;TcpgytIcons 新增 Settings/Shield/Sliders;TcpgytTheme 新增 Muted/NeutralAction/Glass/CardSurface/Hairline 五令牌)

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

- 镜像仓库:`https://github.com/Joeytisaly/MediaFetch31`(默认分支 `main`)。
- **每次改完文件后,主动推送到该 GitHub 仓库**(用户长期指令)。
- 工作区的 git `origin` 是 Figma,不是 GitHub;GitHub 需通过 GitHub 连接器 / MCP 的 `create_or_update_file` 推送。
- 推送前用 `git rev-parse HEAD:<path>` 取 blob SHA 作为 `sha` 参数;推送后用返回的 `size` 与本地 `wc -c` 核对字节一致。
- 推送技巧:先确认本地文件 0 反斜杠,再用 `get_file_contents`(fields: sha)取当前 blob sha,直接把内容传给 `create_or_update_file`。
- 最近提交(MediaFetch31/main):
  - `1b18c77` TcpgytTheme 新增 `themePrimaryWash/Soft/Pale/SectionLabel(theme)` + `LocalAppTheme`;TcpgytBottomSheet 新增共享 `SheetSection`。
  - `8a3a6fd` FilesScreen 详情页三区(任务/文件/来源)改用 `SheetSection`,标签与内容行统一 `horizontal=16.dp` 左对齐。
  - `70d0c16` TasksScreen 详情页同样对齐 + 「来源」补「复制链接」;`FilterSheet` 用 soft/pale/wash 令牌重做配色。
  - `318f708` FilesScreen 四处 emoji 换成 `TcpgytIcons` 矢量:📁/📂→Folder、✓→Check、⋯→More;空状态改为浅色圆形徽章。
  - `96ceb0c` 六处对齐画布(一次提交 6 文件):TcpgytTheme 新增 `themePrimaryMuted(theme)`;新建 `TcpgytSegmented`;TcpgytIcons 新增 `FolderSolid` 黄色双色实心;TcpgytApp nav 文件图标 `Files`(∞)→`Link`;FilesScreen 筛选用共享控件 + 排序改切换 + 文件操作去 emoji + 搜索栏改 `FolderSolid`;MoreScreen 默认类型 `FilterChip`→共享控件。
  - `51d5a7e` 文件页整页对齐画布 + 分段控件精修(一次提交 3 文件):TcpgytSegmented tab 改 `Box` 横纵居中 + `TextAlign.Center` + 垂直 `8dp`(后又补 `includeFontPadding=false`);TcpgytBottomSheet 新增共享 `SheetCloseButton`;FilesScreen 搜索栏 `FolderSolid`→主色 `Folder`、详情关闭键→×、文件操作去标题头、删除确认→底部弹层、卡片第二行→仅格式、完成勾选 `#7EBE9A`、详情底部按钮 `primary-wash`/`surfaceVariant`。
  - `ff22882` 「更多」主列表 1:1 复刻(圆形 primary-soft 图标 + 17sp ExtraBold + chevron,去副标题);TcpgytIcons 新增线条矢量 Settings(齿轮)/Shield/Sliders。
  - `b320a98` 「更多」六子页 1:1 复刻 + 主题令牌(一次提交 2 文件):TcpgytTheme 新增 `themeMuted/NeutralAction/Glass/CardSurface/Hairline(theme)`(逐一对照 index.css token);MoreScreen 六子页改外层玻璃卡 + 内层 white/70 分区 + primary-pale 横幅线条图标 + 自动开始药丸(非 Switch)+ 填充按钮配色 + 外观精确强调色,并移除画布没有的「关闭 Cookie」按钮与「包名」行。
  - 更早:`2022711`(文件页文件夹图标改黄色双色,搜索栏处已被 `51d5a7e` 作废)、`c1f75d1`(底部导航加宽 + 文件图标改 link)。

## 6. Android 端已建立的复用约定

- 主题辅助色一律走 `TcpgytTheme.kt` 里按主题(Blush/Blue/Mint/Lavender/Night 五分支)的函数,不散落硬编码。现有:`themePrimaryWash/Soft/Pale/Muted/SectionLabel/PrimaryMuted/NeutralAction/Glass/CardSurface/Hairline/Ink/InkText(theme)`(分别对应 index.css 同名 token;Glass=`--tcp-glass` 外层玻璃、CardSurface=`bg-white/70` 内层分区、Hairline=`border-white/70` 发丝线)。
- 「更多」子页共用私有积木(`MoreScreen.kt`):`GlassCard`(外层玻璃卡)、`WhiteSection(title?)`(内层 white/70 分区 + section 头)、`InfoBanner(icon,title,body)`(primary-pale 横幅)、`FillButton`(填充按钮);废弃的 `SectionCard` 已移除。
- 详情/弹层分区统一用 `ui/components/TcpgytBottomSheet.kt` 的 `SheetSection(label, containerColor)`:卡内表头 + 分隔线,内容行用 `horizontal=16.dp` 与标题左对齐。
- 图标统一用 `ui/components/TcpgytIcons.kt`(24×24 矢量):Folder/Check/More/Link/Download 等;`FolderSolid` 为黄色双色实心(自带配色);避免用 emoji 字形。
- 分段控件统一用 `ui/components/TcpgytSegmented.kt`,依赖 `themePrimaryMuted(theme)`。
- FilesScreen 文件操作弹层已去除 emoji + 去标题头,改为纯文字行(对齐画布无图标菜单)。
- 弹层关闭键统一 `SheetCloseButton`(共享)。⚠️ `TasksScreen.kt` 仍保留同名私有副本,暂未去重(避免大文件回归风险),待后续单独处理。

## 7. 待办 / 下一步

- **前端原型阶段已收工(2026-07-31)**:三大主页面(下载 / 文件 / 更多)全部对齐画布并收尾,前端原型告一段落。
- **未落地(用户暂缓)**:文件操作弹层(打开文件 / 查看位置 / 移除记录 / 删除原型文件,`App.tsx` 第 179 行)文字居中。同类纯文字菜单还有下载偏好选项弹层(prefPicker,第 185 行)。带图标的格式选择(第 63 行)/ 筛选(第 177 行)弹层不做居中(会与行首图标冲突)。如需恢复此项:把这两处纯文字菜单按钮的 `text-left` 改为 `text-center` 即可。

- 文件页整页对齐 + 分段控件精修已完成(commit `51d5a7e` 起),待用户在设备预览确认:分段控件 tab 文字横纵居中不偏上、搜索栏主色线条文件夹、详情 × 关闭、文件操作无头、删除底部弹层、卡片第二行=格式。
- 「更多」主列表 + 六子页对齐已完成(commit `ff22882`、`b320a98`),待用户在设备预览逐子页确认:外层玻璃卡、内层 white/70 分区、primary-pale 横幅线条图标、自动开始药丸、Cookie 无「关闭」按钮、关于无「包名」行且左对齐、外观强调色。三主页面(下载/文件/更多)整页对齐至此告一段落。
- **空状态徽章配色**:画布用 `progress-soft/strong` 青绿,安卓无对应令牌,当前就近沿用 `primaryContainer`+主色。如需精确复刻青绿,须新增 progress 令牌到 `TcpgytTheme.kt`(待用户定夺)。
- `TasksScreen.kt` 私有 `SheetCloseButton` 去重(待授权)。
- 后续任何上线/发布/签名/数据收集/远端服务 = 新阶段,须重新获得书面批准。
