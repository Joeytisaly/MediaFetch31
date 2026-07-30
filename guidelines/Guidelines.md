# TCPGYT — Blush Glass UI Guidelines

## Visual stance

TCPGYT uses a quiet, feminine blush-glass interface inspired by the approved mobile reference. The product should feel soft, premium and simple: almost no explanatory text, generous vertical spacing, pink icon accents, translucent white pill surfaces, and a blurred pink light field behind the content.

## Palette

| Token | Value | Purpose |
|---|---:|---|
| Blush base | `#F9D4D8` | Main upper gradient |
| Blush deep | `#F2B6C1` | Soft color bloom |
| Milk | `#FFF9F8` | Lower canvas |
| Glass | `rgba(255,255,255,.58)` | Cards and navigation |
| Glass line | `rgba(255,255,255,.82)` | Glass borders |
| Rose | `#ED1D55` | Icons and active navigation |
| Ink | `#151521` | Main text |
| Muted | `#8D8A93` | Supporting text |
| Blue | `#AFCBE7` | Download progress only |
| Success | `#7EBE9A` | Completion state |

## Components

- Primary surfaces are 22–26px rounded transparent-white capsules with a one-pixel white border.
- Use blurred pink / white radial blooms behind cards. Shadows must remain subtle and diffuse.
- Place one rose icon in a softly tinted circular or square holder at the start of each menu item.
- Use black chevrons on the far right of navigable rows.
- Bottom navigation is a single translucent white capsule with three equal actions; the active item receives a rose-tinted circular backdrop.
- Keep labels short. Prefer “开始”, “下载中”, “文件”, “设置”, “关于”.

## Typography

- Use Nunito for all primary UI text; use a system fallback for CJK.
- Use bold, almost-black headings and medium-weight menu labels.
- Avoid technical all-caps labels, metadata stacks and dense explanatory copy.

## Accessibility

- Do not use color alone for download or completion status; pair it with a readable label or icon.
- Keep primary text at strong contrast over frosted surfaces.
- Respect reduced-motion preferences.

## Format picker

- Open the picker as a bottom sheet over a dimmed blush backdrop.
- The sheet is white with a 30px rounded top edge and a short pink drag handle.
- Video/audio switching uses a compact two-option pill.
- Format options are large, lightly bordered rows; selection uses a rose border, rose radio mark and a very pale pink fill.
- The create action is a single full-width rose button at the bottom of the sheet.
- Keep feedback concise: “正在准备…”, “可以选择格式了”, “暂时无法读取链接”.

## Queue management

- Queue filters are compact glass pills; the selected filter uses a blush fill and rose text.
- Completed cards use a soft green icon; failed cards use rose and must include “下载失败”.
- A task card exposes only one immediate action. Completed-task secondary actions live in a bottom sheet.
- Destructive file deletion always requires a separate confirmation sheet with a neutral cancel action and a rose delete action.

## Files and settings

- Completed media uses the same frosted task card, with a soft green completion mark.
- Settings are short rose-icon glass rows. Detail views reuse the same background and a single clear action.
- Cookie settings must state only the local-state placeholder; never display cookie values.
- “Remove record” and “Delete file” are distinct labels. File deletion requires a confirm sheet.
- Unconfigured donation support is informational only and must not navigate.

## State feedback

- Queue, file-library and settings feedback must come from the same local prototype state.
- “Remove record” affects the queue record only; “Delete file” removes both the completed record and library item after confirmation.
- Use a short, non-blocking glass toast for simulated “open file”, “view location” and cleanup results.
- Keep cancelled work out of the file library.

## Adaptive format choices

- The first view prioritizes simple intent: 推荐、高清、省空间，or 原始音频、MP3、M4A.
- “更多格式” reveals a separate list of source-available prototype formats.
- Use concise tags for “大小待确认” and “需要本地合并”; do not expose download commands or codecs as the primary decision.
- A selected choice keeps the same rose radio treatment used elsewhere.

## Task detail

- Task detail opens as a near-full-height frosted bottom sheet with a rose drag handle.
- Progress is pale blue, and the primary action reflects the task’s current state.
- Details use short labels: format, progress and save location. Dangerous actions stay visually secondary.

## Settings groups

- Group settings into 下载、隐私、应用 with small muted headings.
- Preference values use compact selectable pills; cleanup actions always show a confirmation before state is changed.
- Cookie views show only enablement and import count, never credential content.

## Usability states

- Empty, invalid, loading, failure and success states must use concise text plus an icon or structural change.
- Non-destructive sheets may close from their backdrop; destructive confirmation requires an explicit choice.
- Reserve space for bottom navigation and device safe area. Toast feedback is singular, dismissible and brief.

## Compact task filter

- Do not place every download state in the primary task header.
- Default to “进行中”, which groups queued, active and paused work.
- A single compact glass control opens a bottom sheet for 进行中、已完成、下载失败、已取消.

## Task preview and task center

- The download home shows no more than three active task cards.
- The status control is the entry to a task-center sheet, which owns full filtering and the complete task list.
- Keep the home focused on starting a download; do not let task history dominate it.

## File preview and file center

- The file home shows up to three recently completed items and a single “全部文件” entry.
- Full file filtering belongs in the file-center sheet, not in the primary header.
- File actions remain explicit: open, locate, remove record and delete file.

## More page grouping

- Group the More page into 下载、隐私、应用.
- Group labels are muted and unboxed; settings remain rose-icon glass rows.
- Use tighter spacing within groups and more whitespace between groups.

## Download preferences

- Preference rows show a concise current value and open a single-choice sheet.
- Defaults never replace a user’s explicit choice in the format picker.
- Auto-start controls only the initial local task state: downloading or queued.

## Toast lifecycle

- Toast feedback clears automatically after a short interval and should not leak into unrelated settings pages.
- Preference pages show concrete option rows rather than a generic summary plus placeholder copy.

## Interaction consistency

- Shortcut controls must not open their parent detail view.
- Non-destructive sheets close from their backdrop; destructive actions require explicit confirmation.
- Clearing local Cookie placeholders resets the visible import count only after confirmation.

## Save location

- Display a concise user-facing folder path rather than a device absolute path.
- Folder selection is an Android-system-picker placeholder in the prototype.
- Restoring the default location needs confirmation and never moves existing files.

## Cookie management

- Cookie prototype entries use anonymous labels and import time only.
- Never render credential values, source domains, usernames or paths.
- Single-item deletion and clear-all both require confirmation; clearing leaves local management enabled.

## Local data

- Temporary files, download history and app settings are separate cleanup scopes.
- Each cleanup action requires confirmation and must state what it does not affect.
