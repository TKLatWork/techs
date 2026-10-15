---
name: team-board
description: 主从 agent team 的文件 board 编排协议（串行基线）。TeamLead 与所有 worker 执行任何 spec 前必读；team-parallel 技能的并行增量也以本协议为基础。当需要初始化 spec 工作目录、查阅任务/消息 schema、状态机、state.json 状态盘规则时使用。
---

# team-board — 文件 board 编排协议（串行基线）

执行者：**TeamLead**（`.github/agents/team-lead.agent.md`）；参与者：team-worker-research / team-worker-code。

## 本 skill 内容

- **协议正本**：[board-protocol.md](board-protocol.md) —— spec 目录约定、任务/消息文件 schema、消息类型与状态机、铁律、state.json、并行模式增量。一切冲突以正本为准。
- **状态盘模板**：[assets/view.html](assets/view.html) —— TeamLead 初始化 spec 时复制到 `<spec>/board/view.html`；NavTab 多标签（总览 + md 渲染页），2s 轮询 state.json。

## 快速索引

| 要做什么 | 看哪里 |
|---|---|
| 初始化 spec 目录 | board-protocol.md「spec 目录」节 + team-lead.agent.md「启动时必做」 |
| 写任务/消息文件 | board-protocol.md 两个 schema 节 |
| 状态流转规则 | board-protocol.md「消息类型与状态机」+ 铁律 |
| state.json 维护 | board-protocol.md「state.json」节（TeamLead 单点写） |
| 并行执行 | `doc/skills/team-parallel/SKILL.md`（依赖本 skill） |

## 相关公共文档（doc/skills/ 顶层）

requirements.md（需求范围）· design.md（设计与线程模型）· NOTES.md（运行观测）· README.md（POC 入口）· AGENTS.md（目录规则）
