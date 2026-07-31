# TCPGYT 项目交接笔记 (PROGRESS)

> 用途:对话被清理后,读取本文件即可恢复项目状态。每次有重要进展时更新本文件并推送 GitHub。
> 最近更新:2026-07-31

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
- 文件页搜索栏左侧文件夹小标签 = **浅色底 + 黄色双色实心文件夹**(夹身 `#f7c94b` + 封盖 `#ffd76b`),不是粉色线条文件夹。
- 文件页筛选 = 分段控件(圆角胶囊,选中项白底 + 主色文字 + 轻阴影)。

## 5. GitHub 同步约定(标准动作)

- 镜像仓库:`https://github.com/Joeytisaly/MediaFetch31`(默认分支 `main`)。
- **每次改完文件后,主动推送到该 GitHub 仓库**(用户长期指令)。
- 工作区的 git `origin` 是 Figma,不是 GitHub;GitHub 需通过 GitHub 连接器 / MCP 的 `create_or_update_file` 推送。
- 推送前用 `git rev-parse HEAD:<path>` 取 blob SHA 作为 `sha` 参数;推送后用返回的 `size` 与本地 `wc -c` 核对字节一致。
- 最近提交:`2022711`(文件页文件夹图标改黄色双色)、`c1f75d1`(底部导航加宽 + 文件图标改 link)。

## 6. 待办 / 下一步

- (待用户决定)Android 端 Files 筛选的 Compose 分段控件,需对齐 `image-39`;涉及 Android 文件,须先走治理审批。
- 后续任何上线/发布/签名/数据收集/远端服务 = 新阶段,须重新获得书面批准。
