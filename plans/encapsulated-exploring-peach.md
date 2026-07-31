# 计划：「更多」子页逐块复刻画布

## Context（为什么改）

用户以 `src/App.tsx` 画布为设计唯一真源，要求把安卓端（`Joeytisaly/MediaFetch31` `main`，`android/.../ui/more/MoreScreen.kt`）的「更多」页**逐块**对齐画布并「实现画布上的效果」。首页主列表已复刻推送（提交 `ff22882`）；本轮处理**六个子页**：下载偏好 / 保存位置 / Cookie 管理 / 本地数据 / 外观 / 关于与支持。

经用 Explore 逐块提取画布子页规格（`App.tsx` line173 内的 `settingPage` 分支）并与安卓现状比对，发现**系统性结构偏离**及多处细节差异。原则：以画布为准，安卓端不保留画布没有的元素，也不缺画布有的元素。

## 核心结构差异（六个子页共有）

1. **外层玻璃卡缺失**：画布每个子页把全部内容包在**单个** `tcp-glass rounded-[26px] p-5` 卡里；安卓现用多张独立 `SectionCard`，无外层玻璃卡。→ 每个子页内容统一包一层玻璃 Card（`RoundedCornerShape(26.dp)`、`padding 20.dp`、底色 `themeGlass(theme)` + 细边框）。
2. **内层分区卡**：画布内部分区是 `bg-white/70 rounded-[22px]` + 带下边框（`border-b border-white/70`）的 section 头，头文字 `text-[12px] font-black text-[var(--tcp-section-label)]`。→ 用 `themeCardSurface(theme)`（=white/70，深色翻深）+ `RoundedCornerShape(22.dp)`；分区头 12sp Black + `themeSectionLabel`。
3. **信息横幅**：画布用 `bg-[var(--tcp-primary-pale)]` + `primary-soft` 圆形图标底 + **线条图标**（shield/info）+ 标题 14px extrabold + 说明 12px muted；安卓现用 `surfaceVariant` + emoji（🔒/ℹ️）。→ 改 `themePrimaryPale` 底 + `themePrimarySoft` 圆底 + `TcpgytIcons.Shield/Info`（tint primary，size18，圆底 h9w9=36dp）。
4. **分隔线**：画布 `divide-y/border-* white/70`。→ 用 `themeHairline(theme)`（深色翻为浅蓝低透）。

## 新增主题辅助（`ui/theme/TcpgytTheme.kt`）

对照 `src/index.css` 每主题 token：
- `themeNeutralAction(theme)` = `--tcp-neutral-action`：淡色套 `#F1EDF0`、深色套 `#203C56`。
- `themeGlass(theme)` = `--tcp-glass`：淡色 `White.copy(0.58)`、深色 `Color(0xFF1A3650).copy(0.72)`。
- `themeCardSurface(theme)` = `bg-white/70`：淡色 `White.copy(0.70)`、深色 `Color(0xFF1F405C).copy(0.78)`（对应 css line160）。
- `themeHairline(theme)` = `border/divide-white/70`：淡色 `White.copy(0.70)`、深色 `Color(0xFFD2EDFF).copy(0.20)`（css line162）。
- off 态药丸底 `#EDF0F2` + muted 文字：画布硬编码，直接照搬（不新增令牌）。

## 逐页方案（画布为准，`ui/more/MoreScreen.kt`）

**下载偏好**：外层玻璃卡；「默认类型」分区内保留现有 `TcpgytSegmented`（14sp/py10 已对齐 py-2.5）；「格式与网络」三行 `justify-between`：标签 extrabold + 值 primary text-sm + `Chevron` size15，点击仍开 `TcpgytBottomSheet` 选择器；**「自动开始下载」由 Material `Switch` 改为画布药丸**（`rounded-full px3 py1.5 text-xs black`，开=primary-wash/primary、关=`#EDF0F2`/muted，可点切换）。

**保存位置**：外层玻璃卡；「当前原型位置」图标底改 `themePrimarySoft` + `RoundedCornerShape(14.dp)`（原 surfaceVariant/12dp）；「恢复默认位置」由 `TextButton` 改为**填充按钮**（`themePrimarySoft` 底 + primary 字 + `rounded-16` py3）。

**Cookie 管理**：外层玻璃卡；横幅改 primary-pale + `Shield` 图标（去 🔒）；徽章/删除文字用 primary（现删除用 error → 改 primary）；启用态按钮 `添加状态占位`(primary-wash 填充)+`清空全部`(neutral-action 填充)，未启用态整宽 `启用本地状态占位`(primary-wash)；**移除画布没有的「关闭 Cookie」按钮**。

**本地数据**：外层玻璃卡；横幅改 primary-pale + `Info` 图标（去 ℹ️）；三行 status 药丸改 `themePrimarySoft`+primary（现 surfaceVariant）；文案对齐画布（`低风险 · 仅清除原型临时标记` / `中风险 · 隐藏已结束的原型任务` / `低风险 · 恢复下载偏好默认值`）；右侧 `{action} 〉` primary。

**外观**：外层玻璃卡 `space-y-3`；主题卡 `rounded-[22px] border p-4`，选中=`border-primary` + `themePrimaryPale` 底（现 primaryContainer/0.5、rounded20）；色板 `48dp rounded-16`；勾选圈 24dp（选中 primary 底白勾、未选中灰边透明）；**强调色对齐画布精确值**：blue `#3A8DCC`、lavender `#7B61B4`、deep 高亮 `#83C8F2`（现分别为 287FBD/7659AD/76C2ED）。

**关于与支持**：外层玻璃卡；App 头改**左对齐横排**（现居中）：图标底 `themePrimarySoft` `rounded-20` 56dp + `Download` size25，`TCPGYT` 21sp black + `原型版` 药丸(primary-wash)，副文案 `本地优先的下载管理界面原型`；「应用信息」分区 4 项 = 开发者 / 联系邮箱(primary) / 末行 `grid-cols-2 divide-x`：隐私「数据仅保存在本机」+ 组件「后续显示第三方声明」（**移除画布没有的「包名」行**）；底部支持横幅 primary-pale + `Info`。

## 复用既有
- 主题辅助：`themePrimaryWash/Soft/Pale/Muted/SectionLabel(theme)`、`LocalAppTheme`（`TcpgytTheme.kt`）。
- 图标：`TcpgytIcons.Shield/Info/Folder/Chevron/Check/Download`（均已存在）。
- 组件：`TcpgytBottomSheet`（格式/网络选择器沿用）。

## 关键文件
- `ui/theme/TcpgytTheme.kt` — 新增 4 个辅助（neutralAction/glass/cardSurface/hairline）。
- `ui/more/MoreScreen.kt` — 六子页逐页改造（外层玻璃卡 + 内层 white/70 分区 + 横幅 + 药丸/按钮配色 + 移除多余元素 + About 布局 + 外观精确色）。

## 验证
- 无本地 Android SDK，仅代码级检查：导入齐全、辅助函数与 index.css token 数值逐一核对、括号/Composable 参数平衡、移除的元素无残留引用、`Switch`→药丸后相关 import 清理。
- 分两文件经 GitHub MCP（`push_files`）提交到 `Joeytisaly/MediaFetch31` `main`；由用户在设备预览逐子页比对。

## 同步与汇报
- 推送 GitHub 保持唯一真源；更新 `PROGRESS.md`（记录更多子页对齐 + 新增 4 主题辅助 + 移除关闭Cookie/包名行）；按治理 §6 汇报改动文件、与批准范围差异、已/未验证项、下一决策。
