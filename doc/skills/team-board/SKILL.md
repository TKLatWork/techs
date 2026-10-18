---
name: team-board
description: team 过程可视化渲染：view.html 状态盘页（NavTab 多标签、轮询 state.json、任务/消息 md 渲染）与启动方式。启动状态盘、排查渲染行为时使用；state.json 数据契约与维护职责正本在 team-core/protocol.md，本 skill 不定义数据规则、只消费。
---

# team-board — team 过程可视化（渲染）

> 状态：active v1.3.0 ｜ 日期：2026-10-18 ｜ 维护者：Kilo（用户指令）

依赖技能：team-core——state.json 的**数据契约（schema 正本）**与维护职责（TeamLead 单点写、更新时机、冲突以任务/消息文件为准）均在 `doc/skills/team-core/protocol.md`「state.json（数据契约与维护职责）」节；本 skill 只定义**渲染行为与启动方式**。

## Terminology

| 术语 | 定义 |
|---|---|
| 状态盘 | view.html 只读渲染页，数据源 `<spec>/board/state.json` |
| 数据契约 | state.json 字段/枚举定义，正本在 team-core/protocol.md（本 skill 仅消费） |

## view.html 状态盘

- 模板：[template/view.html](template/view.html)；TeamLead 初始化 spec 时复制到 `<spec>/board/view.html`。
- 零依赖单文件，NavTab 多标签：首页总视图每 2s `fetch('state.json')`；点击 task/消息 id 新开标签渲染对应 .md（frontmatter → 字段表；标签激活时重新拉取）。
- **只读**：不写任何文件。
- file:// 下 fetch 被 CORS 拦截，须经 http 打开：

  ```powershell
  npx -y http-server doc\spec-changes\<spec>\board -p 8787 -c-1   # → http://127.0.0.1:8787/view.html
  ```

  或用 VS Code Live Preview 右键 `board/view.html` → Show Preview。

## 渲染契约（消费 team-core 数据契约的字段映射）

| state.json 字段 | 渲染位置 |
|---|---|
| `flow.goal` / `flow.phase` / `flow.steps[]` | 页头 + Flow 图形（零依赖 SVG 横向步骤链：节点边框色 = state——灰 pending · 琥珀 running 闪烁 · 绿 done · 红 failed；箭头 = 顺序依赖；悬停看完整 desc，点击节点 → 对应 task md 标签；图下注脚） |
| `agents[]`（含 state） | 左侧 sidebar 状态灯（idle / working / waiting / failed） |
| `tasks[]` | Tasks 表（id 与消息 id 可点开 md 标签） |
| `messages[]` | 消息链时间线（**最新在上**，渲染时 reverse；state.json 内仍按时间序 append-only；id 可点开 md 标签） |

字段/枚举变更时：先改 team-core/protocol.md 正本，再同步本页映射与 `template/view.html` 渲染逻辑。
