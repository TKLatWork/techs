---
name: team-board
description: 主从 agent team 的文件 board 编排协议（串行基线）。TeamLead 与所有 worker 执行任何 spec 前必读；team-parallel 技能的并行增量也以本协议为基础。当需要初始化 spec 工作目录、查阅任务/消息 schema、状态机、state.json 状态盘规则时使用。
---

# team-board — 文件 board 编排协议（串行基线）

> 状态：active v0.3.0 ｜ 日期：2026-10-15 ｜ 维护者：Kilo（用户指令）

执行者：**TeamLead**（`.github/agents/team-lead.agent.md`）；参与者：team-worker-research / team-worker-code。

## Terminology

| 术语 | 定义 |
|---|---|
| board | spec 目录下 tasks/messages/state.json/view.html 的总称，编排唯一事实源 |
| 正本 | board-protocol.md——协议规则的唯一权威出处 |
| 运行模板 | template/ 下复制即用的 schema 骨架与运行文案 |
| 状态盘 | view.html——state.json 的只读渲染页 |

## 本 skill 内容

- **协议正本**：[board-protocol.md](board-protocol.md) —— spec 目录约定、任务/消息文件 schema、消息类型与状态机、铁律、state.json、并行模式增量。一切冲突以正本为准。
- **运行模板**：[template/](template/) —— schema、状态盘与运行文案模板，复制即用：

| 模板 | 用途 | 使用者 |
|---|---|---|
| [task-template.md](template/task-template.md) | 任务文件（frontmatter + 三段正文） | TeamLead |
| [message-template.md](template/message-template.md) | 消息文件（含六种类型使用说明） | 所有 agent |
| [state-template.json](template/state-template.json) | state.json 骨架 | TeamLead |
| [lead-subagent-io.md](template/lead-subagent-io.md) | TeamLead ↔ sub-agent 交互文案：调用行 / task.assign / 重试修订 / ask 答复 | TeamLead |
| [lead-spec-context.md](template/lead-spec-context.md) | spec 运行上下文：初始化清单 / merge info / 汇总答复 / 决策请求 / 状态维护规则 | TeamLead |
| [worker-reply.md](template/worker-reply.md) | worker 回帖文案（**单一正本**，两 worker 共用）：claim / `## 结果` / result / failed / ask / 返回行 | worker |
| [view.html](template/view.html) | 状态盘渲染页（初始化时复制到 `<spec>/board/view.html`；NavTab 多标签，2s 轮询 state.json） | TeamLead（复制） |

## 快速索引

| 要做什么 | 看哪里 |
|---|---|
| 初始化 spec 目录 | board-protocol.md「spec 目录」节 + team-lead.agent.md「启动时必做」 |
| 写任务/消息文件 | board-protocol.md 两个 schema 节 + template/ 对应模板 |
| 状态流转规则 | board-protocol.md「消息类型与状态机」+ 铁律 |
| state.json 维护 | board-protocol.md「state.json」节（TeamLead 单点写） |
| 运行文案（调用/assign/汇总/回帖） | template/lead-subagent-io.md · lead-spec-context.md · worker-reply.md |
| 并行执行 | `doc/skills/team-parallel/SKILL.md`（依赖本 skill） |

## 相关公共文档

POC 总纲、需求范围（requirements.md）、设计与线程模型（design.md）、运行观测（NOTES.md）见 `doc/skills/chat-team/`；skills 目录规则见 `doc/skills/AGENTS.md`。
