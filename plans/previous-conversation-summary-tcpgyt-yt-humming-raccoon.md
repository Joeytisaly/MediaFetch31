# TCPGYT Android UI 精细化计划

## Context

用户选择方向：**Android UI 精细化** —— 把前端原型的三个屏幕逐步还原为真实 Android 界面。

前端原型已高度完整（React/App.tsx ~1900 行）。Android 工程三个屏幕框架存在，但与原型相比缺少：任务状态筛选 Tab、格式选择 BottomSheet 详细内容、进度条操作按钮、DataStore 主题持久化、Toast 通知、确认对话框等。

---

## Android 工程现状 vs 前端原型差距

| 功能 | 前端原型 | Android 现状 |
|------|---------|------------|
| URL 输入 + 解析 | ✅ 完整 | ✅ 有（模拟） |
| 格式选择 BottomSheet | ✅ 简单/高级两种模式 | ❌ 仅两个 FilterChip |
| 任务卡片（进度条+操作） | ✅ 进度条、暂停/取消按钮 | ❌ 仅静态演示卡片 |
| 任务状态筛选 Tab | ✅ 进行中/完成/失败/取消 | ❌ 无 |
| 文件库搜索/筛选 | ✅ 完整 | ✅ 有 |
| 文件操作菜单 | ✅ 分享/删除 | ❌ 仅"来源信息"弹窗 |
| 主题持久化 | ✅ localStorage | ❌ 无（重启后丢失） |
| Toast 通知 | ✅ 有 | ❌ 无 |
| 删除确认对话框 | ✅ 有 | ❌ 无 |
| 设置项占位路由 | ✅ 有 | ⚠ 有但无响应 |
| DataStore 偏好保存 | ✅ 模拟 | ❌ 无 |

---

## 推荐实施计划（分阶段，每阶段需单独审核）

### 第一阶段：TasksScreen 精细化

**目标：** 使任务页交互与前端原型对齐

**拟修改文件：**
- `android/app/src/main/java/com/tcpg007014/tcpgyt/ui/tasks/TasksScreen.kt`

**变更摘要：**
1. 格式选择改为 ModalBottomSheet，包含：
   - 简单模式：音频(MP3/M4A) / 视频(1080P/720P/480P) 选项
   - 高级模式：自定义格式码输入框
2. 任务卡片增加：
   - LinearProgressIndicator 进度条（0-100%）
   - 暂停/继续、取消操作按钮（图标按钮）
   - 状态标签（排队中 / 下载中 / 已暂停 / 已完成 / 失败）
3. 顶部增加状态筛选 Tab Row（全部 / 进行中 / 已完成 / 失败）
4. 解析失败状态：显示错误信息 + 重试按钮

**关联影响：** 仅 UI 层，无网络请求，无存储改动  
**回滚方式：** git revert 对应 commit  
**验收标准：** assembleDebug 通过；格式 Sheet 可展开/关闭；任务卡片显示进度条和操作按钮；筛选 Tab 切换可响应

---

### 第二阶段：FilesScreen 精细化

**目标：** 文件操作菜单与前端原型对齐

**拟修改文件：**
- `android/app/src/main/java/com/tcpg007014/tcpgyt/ui/files/FilesScreen.kt`

**变更摘要：**
1. 文件卡片长按或点击三点菜单：弹出 DropdownMenu（分享 / 删除）
2. 删除操作增加 AlertDialog 确认对话框
3. 筛选 Tab 与前端对齐：全部 / 音频 / 视频
4. 排序选项：最近添加 / 名称（DropdownMenu 实现）

**关联影响：** 仅 UI 层，演示数据不变  
**回滚方式：** git revert  
**验收标准：** assembleDebug 通过；长按文件显示操作菜单；删除前弹出确认对话框

---

### 第三阶段：主题 DataStore 持久化

**目标：** 主题选择在应用重启后保留

**拟新增文件：**
- `android/app/src/main/java/com/tcpg007014/tcpgyt/data/PreferencesDataStore.kt`

**拟修改文件：**
- `android/app/src/main/java/com/tcpg007014/tcpgyt/TcpgytApp.kt`
- `android/app/src/main/java/com/tcpg007014/tcpgyt/ui/more/MoreScreen.kt`
- `android/app/build.gradle.kts`（添加 DataStore 依赖）

**依赖：**
- `androidx.datastore:datastore-preferences:1.1.1`（Apache-2.0，来源：Google Maven）

**变更摘要：**
1. 新建 PreferencesDataStore.kt，封装主题 key 的读写
2. TcpgytApp 启动时读取保存的主题，写入时持久化
3. MoreScreen 主题选择后调用 DataStore 写入

**回滚方式：** git revert；去掉 DataStore 依赖恢复原内存状态  
**验收标准：** 选择主题 → 退出应用 → 重新打开 → 主题保持不变

---

### 第四阶段：Toast + 全局交互反馈

**目标：** 增加操作反馈，对齐前端原型的 Toast 通知

**拟修改文件：**
- `android/app/src/main/java/com/tcpg007014/tcpgyt/ui/tasks/TasksScreen.kt`
- `android/app/src/main/java/com/tcpg007014/tcpgyt/ui/files/FilesScreen.kt`

**变更摘要：**
1. 使用 Snackbar（Material 3 推荐）替代 Toast，在以下操作后显示：
   - 添加任务成功："已加入下载队列"
   - 删除文件成功："文件已删除"
   - 链接格式错误："链接格式无效，请检查"
2. SnackbarHostState 通过 ScaffoldState 管理

**回滚方式：** git revert  
**验收标准：** 上述三种操作均显示 Snackbar；2 秒后自动消失

---

## 实施顺序与用户审核节点

```
第一阶段（TasksScreen）
  → 用户审核批准 → 实施 → 构建验证
第二阶段（FilesScreen）
  → 用户审核批准 → 实施 → 构建验证
第三阶段（主题持久化）
  → 用户审核批准（包括依赖审核）→ 实施 → 构建验证
第四阶段（Toast/Snackbar）
  → 用户审核批准 → 实施 → 构建验证
```

每个阶段完成后，用户 git pull 并 assembleDebug 验证，再开始下一阶段。

---

## 验证方式

- 每次变更后：`.\gradlew.bat :app:assembleDebug` 通过
- 在 Android Studio Emulator 或真机上运行，逐项检查验收标准
- 无网络请求、无存储权限要求（第三阶段 DataStore 不需要额外权限）
