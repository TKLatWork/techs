# 需求范围：主从 Agent Team 工作模式 POC（VSCode Chat / Copilot）

> 状态：v0.2 已确认 ｜ 日期：2026-10-13 ｜ 阶段：范围已锁定，进入设计/实现

## 1. 背景与核心问题

在 VSCode Chat（GitHub Copilot）中实现类似 CLI 多 agent 工具的「主从 team 工作模式」：
一个 TeamLead agent 接收用户任务，拆解后分派给多个 worker agent，通过消息传递协作并回收结果。

**POC 要回答的唯一核心问题：**

> 仅用 .md 文件（Copilot custom agent 格式）+ 文件共享 board，
> 能否让 LLM 可靠地执行一套主从编排消息协议（分派 → 领任务 → 回消息 → 汇总）？

即验证「编排协议可行性」，而非产品化。

## 2. 实现约束

| 约束 | 说明 |
|---|---|
| 纯 .md 实现 | 不写任何脚本/扩展代码（无 TS/Python）。agent 定义用 Copilot custom agent 格式：`.github/agents/*.agent.md`（YAML frontmatter + system prompt 正文） |
| 消息传递 = 文件共享 board | 每个spec工作目录 `doc/specs/<spec>/` 下的 `board/`，结构化 .md 消息文件；协议完全由 prompt 约定，靠 LLM 自觉遵守 |
| 运行环境 | VSCode Chat，agent 模式，Copilot 订阅；TeamLead 需通过 `agents` 工具调用 worker（preview 特性，版本可用性是待验证风险） |
| 无持久化运行时 | 没有后台进程；一切发生在 chat 会话回合内 |
| 文档布局 | agent/team 规范文档在 `doc/skills/`（运行时只读）；spec运行工作目录在 `doc/specs/<spec>/`；agent 定义在仓库根 `.github/agents/`（VS Code 以 `techs` 为工作区根直接发现，无需另开文件夹） |

## 3. LLM vs 脚本的职责边界（术语澄清）

- **LLM 负责**：任务拆解、消息撰写、worker 执行具体工作（读代码/改文件/调研）、结果汇总。
- **协议负责（"脚本"的替代品）**：消息 schema、board 目录布局、任务状态机（`open → claimed → done/failed`）。这些是 .md 里的文字约定，无代码强制。
- **推论**：协议被违反（格式错、状态漏更新）是预期内的观测对象，POC 需记录违反频率与类型。

## 4. 范围

### In scope

1. **TeamLead agent**（`.github/agents/team-lead.agent.md`）
   - 接收用户任务，拆解为子任务写入 board
   - 通过 `agents` 工具逐个调用 worker，指向对应任务
   - 读取 worker 回帖，汇总为最终答复
2. **Worker agent ×2**（最小数量，验证协议而非分工），如：
   - `team-worker-research`：只读调研类任务
   - `team-worker-code`：改文件类任务
3. **Board 协议 v0.1**（正本 `doc/skills/team-board/board-protocol.md`）
   - 消息文件结构：frontmatter（id / type / from / to / task / reply_to / ts）+ 正文
   - 任务状态机与 spec 目录约定（`doc/specs/<spec>/board/`）
4. **一个端到端验证场景**：用户 @TeamLead 下达一个需要两个 worker 协作的小任务，观察协议是否走通
5. **观测记录**：协议违反情况、消息往返次数、可行性结论（写入 `doc/skills/NOTES.md`）

### Out of scope

- 并发/并行执行 worker（Copilot `agents` 工具调用为串行，本 POC 只验证串行编排）
- 跨会话持久化、断点恢复
- 真实工具能力扩展（MCP、外部 CLI 子进程）
- UX 打磨（进度可视化、多面板观察）——仅以 chat 输出 + board 文件为观察面
- 产品化路径对比（MCP server / 扩展方案）——结论中可提及但不实现

## 5. 验收标准

- [ ] `@TeamLead` 在 VSCode Chat 中可被调用，且能成功调用至少一个 worker agent
- [ ] 一次完整任务流转在 spec board 中留下可审计的消息链：分派 → 领取 → 结果 → 汇总
- [ ] 消息文件符合协议 schema 的比例有记录（不要求 100%，要求可观测）
- [ ] `doc/skills/NOTES.md` 给出结论：协议可行 / 需代码强制 / 不可行，及依据

## 6. 已决议（原开放问题）

| # | 问题 | 决议 |
|---|---|---|
| Q1 | 消息粒度 | **每消息一文件**：`board/messages/msg-NNNN.md`，frontmatter 存元数据，可审计、无写冲突 |
| Q2 | worker 分工 | **research + code 两个 worker**：`team-worker-research`（只读调研）、`team-worker-code`（改文件） |
| Q3 | 任务传递 | **只传任务 id**：worker 自己读 board 领任务，board 是唯一事实源 |
| Q4 | `agents` 工具不可用时 | **不接受降级，必须真跨 agent 调用**。若环境不支持，POC 阻塞并记录为结论（见 §7 预检） |

## 7. 预检项（实现前第一步）

- [ ] VS Code 以仓库根 `techs/` 为工作区时，`.github/agents/*.agent.md` 被识别（Chat @ 列表出现 TeamLead）
- [ ] frontmatter 中 `tools: ['agents']` 不被标记为未知工具（打开 agent 文件无告警）
- [ ] Chat agent 模式下 `@TeamLead` 可被唤起，且其能实际调用 worker agent

## 8. 交付物清单

```
techs/                                  # VS Code 工作区根
├── .github/agents/                     # agent 定义（仓库根直接被发现）
│   ├── team-lead.agent.md
│   ├── team-worker-research.agent.md
│   └── team-worker-code.agent.md
├── doc/skills/                         # agent/team 规范文档（运行时只读）
│   ├── AGENTS.md                       # 目录规则（每 skill 一个目录）
│   ├── README.md                       # POC 入口与索引
│   ├── requirements.md                 # 本文件（公共）
│   ├── design.md                       # 协议与 agent 设计（公共）
│   ├── NOTES.md                        # 运行观测与结论（公共）
│   ├── team-board/                     # skill：board 编排协议（串行基线）
│   │   ├── SKILL.md
│   │   ├── board-protocol.md           # 协议正本（含并行模式增量）
│   │   └── assets/view.html            # 状态盘模板
│   └── team-parallel/                  # skill：worktree 并行编排（TeamLead 执行）
│       └── SKILL.md
└── doc/specs/                          # spec运行工作目录（TeamLead 运行时创建）
    └── <spec>/
        ├── board/                      # tasks/ + messages/ + state.json + view.html
        └── （任务产物）
```
