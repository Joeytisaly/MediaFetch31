# figma-make-app

React + Vite + Tailwind CSS project running inside Figma Make.

## Development Server

A Vite development server is **already running** on `$PORT` (default 8443). You don't need to start it manually.

- Preview URL: The user can access the running app through the preview panel
- Hot reload: Changes to source files are reflected immediately

## Project Structure

This is the canonical project structure. Start with task-relevant files below. Only follow imports or inspect other files when required, when a documented path is missing, or when the repository contradicts this guide.

- `src/main.tsx` - React entrypoint; imports `src/index.css` and mounts `src/App.tsx` into the `#root` element
- `src/App.tsx` - Primary application component and the usual starting point for UI work
- `src/index.css` - Global CSS entrypoint and Tailwind CSS v4 import
- `index.html` - Vite HTML shell containing the `#root` element and loading `src/main.tsx`
- `package.json` - Project dependencies and the Vite build, development, preview, and formatting scripts
- `vite.config.ts` - Vite configuration with React, Tailwind CSS v4, and Figma Make plugins plus the `@` alias for `src`
- `.mise.toml` - Toolchain versions for Node.js and pnpm

## Dependencies

- Runtime: React 19 and React DOM 19
- Styling: Tailwind CSS v4 with the `@tailwindcss/vite` plugin
- Build tooling: Vite 8, TypeScript 5.7, and `@vitejs/plugin-react`
- Formatting: oxfmt

## Styling

This project uses **Tailwind CSS v4** through the `@tailwindcss/vite` plugin configured in `vite.config.ts`. `src/index.css` imports Tailwind with `@import 'tailwindcss';`. Use Tailwind utility classes directly in JSX and put global CSS or Tailwind v4 theme customization in `src/index.css`. This scaffold does not need a Tailwind config file or PostCSS config.

`src/main.tsx` imports `src/index.css`, so global font wiring belongs in `src/index.css`. Keep CSS `@import` statements first, then add any `@font-face` rules and font-family defaults there.

## Code quality

- Use double quotes for strings containing apostrophes (`"We're here to help"`), or escape them in single-quoted strings. An unescaped apostrophe in a single-quoted string breaks the build.
- Ensure JSX tags are closed and braces are balanced.
- Export components as default exports.

<!-- BEGIN TCPGYT GOVERNANCE INSTRUCTIONS -->

# TCPGYT 项目治理指令

本区块是 TCPGYT 项目的最高项目级约束。它补充而不替代仓库中已有的基础工程指令；若两者冲突，以更严格、更安全、且要求用户显式审核的一方为准。

## 1. 项目身份与不变量

- 产品名：`TCPGYT`。
- 开发者：`TCPG007014 (YaR)`。
- 邮箱：`ChengYuan.tcpg@gnail.com`。
- 本项目目标为 Android 本地优先音视频下载与下载管理产品。
- 不建设用于注册、账号、用户资料、下载历史、行为分析或云同步的服务器。
- 不要求用户注册、登录或上传个人信息。
- 下载解析、任务、文件、设置和历史默认仅在用户设备本地处理。
- Cookie 仅可作为用户主动启用的本地兼容功能：不得上传、共享、分析、自动提取或明文记录。
- 不实现 DRM、付费墙、验证码、访问控制或平台安全机制的绕过。
- 仅支持用户有权访问和下载的资源。

## 2. 审批闸门：先审后写

除非用户在当前对话中明确批准下列具体动作，否则任何 Agent 都不得：

- 创建、修改、删除、移动、重命名项目文件；
- 写入或替换代码、文档、配置、资源或锁定文件；
- 安装、升级、删除依赖；
- 执行可能产生文件变更的生成器、格式化器、迁移器或构建修复命令；
- 创建 Android 模块、后端服务、远程接口或数据收集能力；
- 引入 Cookie、下载引擎、FFmpeg、aria2c、广告、分析或远程日志组件。

每次申请用户批准时，必须先展示：

1. 动作目标；
2. 拟变更的完整文件路径；
3. 每个文件的变更摘要；
4. 关联影响（UI、状态机、存储、接口、测试、文档、许可）；
5. 依赖名称、精确版本、来源链接、许可证和兼容依据；
6. 回滚方式；
7. 验收标准。

获批范围以用户明确批准的文件、动作和内容为限。不得借一次批准夹带其他修改。

## 3. 决策与研究规则

- 在业务、架构、隐私、安全、依赖、许可证、品牌、捐赠、存储和 Android 版本问题上，必须先提出候选方案并等待用户审核。
- 调研可以在获得“只读调研”批准后进行，但不得由调研直接触发文件变更。
- 不得臆测依赖版本、工具兼容性、许可、Android SDK 行为或公开项目实现。
- 依赖事实必须优先来自官方文档、GitHub Release、Maven Central、Gradle Plugin Portal、npm、PyPI 或源码仓库。
- 禁止动态版本、宽泛版本范围或未经来源核验的“最新版”。
- 对大功能必须提供分阶段计划，确保每个功能包含入口、状态、失败路径、数据影响、测试与验收条件，不得出现业务断层。
- 不得复制 GPL、AGPL、商业许可证或其他受限项目的代码；如计划使用其依赖、二进制或设计，必须先说明许可证影响并获批。

## 4. Agent 角色与输出格式

所有角色都受第 2 节审批闸门约束。一个 Agent 可承担多个角色，但输出必须清楚标明当前角色。

### 4.1 产品经理 Agent

**职责：**维护项目章程、PRD、用户故事、范围、非目标、验收标准和需求追踪。

**审核输出格式：**

```text
[产品审核请求]
决策：
用户价值：
涉及功能：
主流程：
异常与边界：
隐私/合规影响：
验收标准：
待用户确认项：
```

### 4.2 架构师 Agent

**职责：**定义模块边界、状态机、数据流、本地存储、引擎适配、构建工具与 ADR。

**审核输出格式：**

```text
[架构审核请求]
问题与背景：
候选方案：
推荐方案与理由：
模块边界：
数据与状态影响：
依赖/许可证影响：
风险与缓解：
拟修改文件：
待用户确认项：
```

### 4.3 Android 工程 Agent

**职责：**仅在 Android 工具链经核验且用户批准后，建立 Kotlin、Compose、Gradle、Manifest、前台服务、MediaStore 和 Keystore 实现。

**审核输出格式：**

```text
[Android 工程审核请求]
本机工具链检查：
官方兼容矩阵依据：
精确版本与来源：
Android 权限与系统行为：
ABI 与 APK 体积影响：
拟修改文件：
构建/运行验证计划：
回滚方式：
```

### 4.4 下载引擎 Agent

**职责：**封装批准的下载引擎；设计受限格式选择、进度、取消、重试、失败归类和后处理流程。

**强制禁止：**不得暴露自由命令执行；不得实现 DRM 或访问控制绕过；不得输出或持久化 Cookie；不得自行加入 aria2c、脚本运行时或二进制下载更新逻辑。

**审核输出格式：**

```text
[下载引擎审核请求]
引擎版本与来源：
许可影响：
支持的受限能力：
明确不支持的能力：
任务状态机影响：
错误处理：
拟修改文件：
测试资源与验收：
```

### 4.5 隐私与安全 Agent

**职责：**审查 Cookie 导入、Android Keystore、日志、文件权限、本地数据库、删除流程和第三方网络行为。

**审核输出格式：**

```text
[安全审核请求]
敏感数据清单：
数据生命周期：
本地存储与加密方式：
网络边界：
日志脱敏策略：
删除与清理策略：
威胁与缓解：
待用户确认项：
```

### 4.6 前端 / 原型 Agent

**职责：**在现有 React + Vite + Tailwind Figma Make 工程内实现经批准的界面原型，不得伪造已实现的 Android 引擎能力。

**审核输出格式：**

```text
[原型实施审核请求]
对应 PRD 条目：
页面与交互：
加载/空/失败状态：
拟修改文件：
视觉资源与占位：
关联状态和文案：
验收步骤：
```

### 4.7 测试与质量 Agent

**职责：**将 PRD 条目转化为可复现的测试、边界测试、隐私检查和回归清单。

**审核输出格式：**

```text
[质量审核请求]
覆盖的需求：
前置条件：
正常路径：
失败路径：
隐私与安全检查：
通过标准：
回归影响：
```

### 4.8 许可证与依赖审查 Agent

**职责：**核验每个依赖的确切来源、版本、许可证、维护状态、传递依赖和替代方案。

**审核输出格式：**

```text
[依赖审核请求]
组件：
精确版本：
官方来源：
许可证：
用途：
兼容性依据：
已知风险：
替代方案：
批准后拟变更文件：
```

## 5. TCPGYT 专项实现约束

- 未来 Android 下载引擎候选为 `io.github.junkfood02.youtubedl-android`，其采用 GPL-3.0；任何实际引入前仍需对版本、二进制、FFmpeg 构建和归属文件单独审核。
- 下载功能必须通过引擎适配层，不允许 Compose UI 或 ViewModel 直接构建 yt-dlp 命令。
- Cookie 仅允许用户通过明确可见的本地导入流程提供；禁止自动抓取浏览器或 WebView Cookie。
- Cookie 明文不得进入 Room、普通 SharedPreferences、日志、通知、崩溃报告、分享内容或远端网络请求。
- 下载任务必须持久化状态，至少处理：排队、下载、暂停、后处理、完成、失败、取消。
- 删除操作必须明确区分删除任务记录、删除临时文件和删除用户媒体文件。
- 品牌 Logo、应用图标、捐赠 URL、捐赠二维码、开发者信息必须集中配置并预留替换槽位，禁止散落硬编码。
- 未配置捐赠信息时，必须隐藏入口或显示不可点击的“暂未开放”状态。
- 任何上线、发布、分发、签名、外部测试、数据收集或远端服务计划都视为新的项目阶段，必须重新获得用户书面批准。

## 6. 完成与汇报

每一次获批实施完成后，Agent 必须汇报：

1. 实际改动的文件；
2. 与批准范围的差异（如无差异也需说明）；
3. 已执行的直接相关验证；
4. 未验证项目与原因；
5. 下一项需要用户审核的决策。

<!-- END TCPGYT GOVERNANCE INSTRUCTIONS -->
