# TCPGYT 数据模型（Data Model v0.1）

**状态：草案（待审批）**
**关联：** `docs/00-project-charter.md`、`docs/01-prd.md`、`docs/02-architecture.md`

> 本文件定义 TCPGYT 本地实体，作为 Phase E（Android）Room / Repository / 安全存储的直接蓝本。
> 所有内容均源自已批准的章程、PRD、ADR-001；凡涉及尚未拍板的点，标注 **[待审批]**，不擅自定死。
> 引擎核心为 yt-dlp（经 ADR-001 批准的 Android 封装 `youtubedl-android`）。

## 0. 建模原则

- **本地优先**：所有实体默认只存于用户设备；无任何实体上传到远端服务器（章程 §3）。
- **敏感数据隔离**：完整 Cookie、授权头、敏感 URL 参数**禁止**进入普通 Room 表、日志、通知、分享内容或异常堆栈（PRD §3.3、ADR §5）。
- **状态可追踪**：任务的每一次状态转换都记录时间戳与非敏感原因码（ADR §4）。
- **可替换品牌**：Logo / 图标 / 捐赠 / 开发者信息以配置形式存在，替换不影响下载业务逻辑（PRD §5.6、验收 11）。

## 1. 存储分区

| 分区 | 用途 | 存放实体 | 加密 |
|---|---|---|---|
| Room 私有库（应用私有目录） | 非敏感业务数据 | `DownloadTask`、`MediaFormat`、`StatusTransition`、`CookieEntry`（仅元数据） | 应用私有目录，随系统沙箱保护 |
| 安全私有存储 + Android Keystore | Cookie 密文 | `CookieSecret`（密文，与元数据分离） | Keystore 管理密钥；无法安全初始化则禁用 Cookie 功能，不回退明文（ADR §5） |
| SharedPreferences / DataStore（私有） | 应用偏好、品牌/捐赠配置 | `AppPreferences`、`BrandConfig`、`SupportConfig` | 私有；不存任何凭据 |
| MediaStore / 用户可访问目录 | 完成的媒体文件本体 | 由 `DownloadTask.output` 定位 | 由 Android 存储策略管理 |

## 2. 实体定义

### 2.1 DownloadTask（下载任务）

| 字段 | 类型 | 说明 | 敏感 |
|---|---|---|---|
| `id` | String (UUID) | 任务主键 | 否 |
| `sourceUrlDisplay` | String | 用于展示的来源 URL（去除敏感 query 参数后的脱敏版本） | 脱敏 |
| `sourceRef` | String | 重新解析所需的内部引用（不含授权头/凭据） | 否 |
| `title` | String | 资源标题 | 否 |
| `durationSec` | Int? | 时长（秒），未知为 null | 否 |
| `thumbnailRef` | String? | 缩略图本地缓存引用 | 否 |
| `sourceLabel` | String | 来源平台标识（展示用） | 否 |
| `selectedFormatId` | String | 关联 `MediaFormat.id` | 否 |
| `status` | TaskStatus | 当前状态（见 2.2） | 否 |
| `progressPercent` | Float | 0–100 | 否 |
| `speedBytesPerSec` | Long? | 当前速度，暂停/完成为 null | 否 |
| `totalBytes` | Long? | 总大小；未知为 null（不得伪造） | 否 |
| `downloadedBytes` | Long | 已下载字节 | 否 |
| `output` | FileLocator? | 完成文件定位（见 2.6），未完成为 null | 否 |
| `createdAt` / `updatedAt` | Long (epoch ms) | 时间戳 | 否 |
| `failureCode` | FailureCode? | 失败原因码（见 2.5） | 否 |
| `failureNote` | String? | 非敏感、可诊断的失败说明（不含 URL 参数/凭据） | 脱敏 |
| `usedCredentialRef` | String? | 本次是否关联某 `CookieEntry`（仅引用，非密文） | 引用 |

**禁止落库字段**：完整原始 URL（含 token/授权参数）、授权头、完整 Cookie 明文。

### 2.2 TaskStatus（状态枚举）

严格对齐 ADR §4 / PRD §5.3：

```
DRAFT → INSPECTING → READY → QUEUED → DOWNLOADING → POST_PROCESSING → COMPLETED
QUEUED | DOWNLOADING → PAUSED → (QUEUED | CANCELED)
任意非终态 → CANCELED
任意执行态 → FAILED → (INSPECTING 重解析 | QUEUED 格式仍有效 | CANCELED)
```

- 终态：`COMPLETED`、`CANCELED`、`FAILED`（可再流转）。
- 写入下载引擎前，任务必须已持久化为 `QUEUED`（ADR §4）。
- `CANCELED` 不得显示为完成。
- `POST_PROCESSING` 失败必须保留源下载结果与失败原因，不得静默删除文件。
- 中文展示映射：待解析/可选择格式(READY)/排队中/下载中/后处理中/已完成/已暂停/已取消/失败（对齐原型文案）。

### 2.3 StatusTransition（状态转换记录）

| 字段 | 类型 | 说明 |
|---|---|---|
| `id` | String | 主键 |
| `taskId` | String | 关联 `DownloadTask.id` |
| `from` / `to` | TaskStatus | 转换前后状态 |
| `at` | Long (epoch ms) | 时间戳 |
| `reasonCode` | String | 非敏感原因码 |

### 2.4 MediaFormat（可用格式）

| 字段 | 类型 | 说明 |
|---|---|---|
| `id` | String | 格式主键 |
| `kind` | FormatKind | `VIDEO` / `AUDIO` |
| `qualityLabel` | String | 清晰度（如 1080p）或音频码率（如 128kbps） |
| `container` | String | 容器（如 mp4、m4a） |
| `codecSummary` | String? | 编码摘要 |
| `estimatedBytes` | Long? | 预估大小；未知为 null（显示"大小未知"） |
| `isRecommended` | Boolean | 是否默认推荐 |
| `recommendReason` | String? | 推荐理由（推荐项必填，PRD §5.2） |

### 2.5 FailureCode（失败原因分类）

对齐 ADR §4 / PRD §5.3，供错误归一化：`ENGINE`、`NETWORK`、`STORAGE_FULL`、`FORMAT_UNAVAILABLE`、`PERMISSION`、`COOKIE`、`CANCELED_BY_USER`、`UNKNOWN`。每类附带面向用户的恢复建议（不泄露敏感信息，PRD 非功能·隐私）。

### 2.6 FileLocator（文件定位）

| 字段 | 类型 | 说明 |
|---|---|---|
| `mediaStoreUri` | String? | MediaStore/SAF 定位 |
| `displayPath` | String | 用户可理解的展示路径（如 Download / TCPGYT） |
| `fileName` | String | 文件名（首版是否允许自定义 [待审批]，PRD §8-7） |

### 2.7 CookieEntry（Cookie 元数据）与 CookieSecret（密文）

**两者分离存储。**

`CookieEntry`（Room，非敏感）：

| 字段 | 类型 | 说明 |
|---|---|---|
| `ref` | String | 引用键，供 `DownloadTask.usedCredentialRef` 关联 |
| `sourceLabel` | String | 来源标识（展示用） |
| `importedAt` | Long | 导入时间 |
| `lastUsedAt` | Long? | 最后使用时间 |
| `secretKeyAlias` | String | 指向 Keystore 密钥别名（非密钥本体） |

`CookieSecret`（安全私有存储，加密）：仅密文本体，经 Keystore 密钥加密。

**硬性约束（PRD §3.3、ADR §5）**：Cookie 明文只在内存中短暂存在，用后清理；绝不进入 Room 普通表、SharedPreferences、日志、通知、分享内容、崩溃报告或异常堆栈。删除后必须无法再被引擎读取（PRD 验收 9）。

### 2.8 AppPreferences（应用偏好）

| 字段 | 类型 | 默认 |
|---|---|---|
| `defaultType` | FormatKind | `VIDEO` |
| `defaultQuality` | String | `自动`/推荐 |
| `networkPref` | NetworkPref | `WIFI_ONLY` / `ANY` [默认值待审批] |
| `savePath` | String | `Download / TCPGYT` |

### 2.9 BrandConfig / SupportConfig（品牌与捐赠占位）

对齐 PRD §5.6，全部为可替换占位；替换不影响下载业务（验收 11）。

| 键 | 未配置时行为 |
|---|---|
| `brand.logo` | 统一视觉占位，不影响布局 |
| `brand.appIcon` | 默认占位或系统安全回退 |
| `brand.appName` | 默认 `TCPGYT` |
| `support.donationUrl` | 隐藏主操作或显示"暂未开放"（不出现失效链接，验收 10） |
| `support.donationQrCode` | 不渲染 |
| `developer.name` | `TCPG007014 (YaR)` |
| `developer.email` | `ChengYuan.tcpg@gnail.com` |

## 3. 关系概览

```
DownloadTask 1─* StatusTransition
DownloadTask *─1 MediaFormat（selectedFormatId）
DownloadTask 0..1─0..1 CookieEntry（usedCredentialRef → ref）
CookieEntry 1─1 CookieSecret（secretKeyAlias，跨存储分区）
AppPreferences / BrandConfig / SupportConfig：单例配置
```

## 4. 与验收标准的对应

- 状态机完整可恢复 → §2.2 / §2.3（ADR §8-4、PRD 验收 3）。
- 存储不足不产生假完成 → `STORAGE_FULL` + `totalBytes` 不伪造（PRD 验收 5）。
- Cookie 明文不入普通库/日志 → §2.7（ADR §8-2、PRD 验收 9）。
- 无 Cookie 也能下载公开资源 → `usedCredentialRef` 可空（ADR §8-6、PRD 验收 7）。
- 品牌/捐赠替换不改业务 → §2.9（PRD 验收 11）。

## 5. 本文件未决项（[待审批]）

1. Room 具体版本与 schema 迁移策略（工具链审批阶段）。
2. `networkPref` 默认值（仅 Wi-Fi vs 任意网络）。
3. 是否允许自定义文件名（PRD §8-7）。
4. 并发上限与断点续传字段（并发/恢复策略定案后补充，PRD §8-4）。
5. Cookie 文件支持格式与校验字段（PRD §8-3；详见后续 `docs/06`）。
