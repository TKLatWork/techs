# team 协议正本 v0.7（agent 运行时必读）

> 状态：active v0.7.0 ｜ 日期：2026-10-19 ｜ 维护者：Kilo（用户指令）

正本位置：`doc/skills/team/core/protocol.md`。所有 agent（TeamLead、team-worker-research、team-worker-code）执行任务前后必须遵守本协议。state.json 的 schema 数据契约见本节；状态盘渲染见 `doc/skills/team/board/SKILL.md`。

## Terminology

| 术语 | 定义 |
|---|---|
| spec | 一次大任务及其运行工作目录 `doc/spec-changes/<spec>/` |
| board | spec 下 `board/`：tasks/messages/state.json/context.md/view.html 的总称，team 执行状态的唯一事实源 |
| delivery | spec 下 `delivery/`：SDD 的 `status.json` / `spec-doc.json` 与模块文档；cell 状态唯一来源 |
| hub | spec 下 `hub/`：SDD + team 的单一对外入口（见 `doc/skills/hub/SKILL.md`） |
| cell | SDD 交付单元 `(module, stage)`；状态 pending/active/done/invalid/blocked |
| attempt | team 对某个 WorkOrder 的一次执行尝试；一个 cell 可有多次 attempt |
| 任务文件 | `board/tasks/task-NNN.md`，分派与验收单元（描述/验收标准/结果三段） |
| 消息 | `board/messages/msg-*.md`，agent 间 append-only 通信记录 |
| TeamLead / worker | 主 agent（分派/回收/合并）/ 执行 agent（research、code） |
| claim | worker 领取任务：open → claimed + task.claim 消息 |
| ask / info | worker 请求决策的消息 / 答复与补充说明消息 |
| append-only | 写入后不改不删；纠错发新消息 reply_to 指向原消息 |
| state.json | TeamLead 单点维护的汇总视图；非事实源，冲突以文件为准 |
| context.md | spec 上下文汇总视图（用户原始需求原话/决议记录/术语演进/关键约束）；TeamLead 单点维护，见「context.md」节 |
| worktree / 分支 | 并行任务的 git 隔离目录与分支（正本见 team/parallel） |
| 命名空间编号 | 并行任务消息编号 `msg-<taskid>-NN`，无锁防撞号 |

## spec 目录（运行工作目录）

- 目录语义（2026-10-18 用户指令）：`doc/spec-changes/` = **进行中的 change**；`doc/spec-doc/` = **项目的当前文档目录（source-of-truth）**。
- 完成迁移（2026-10-18 用户批准）：spec `phase: done` 后，正式产物迁入 `doc/spec-doc/`；board 运行记录留在 `doc/spec-changes/<spec>/`。
- 每个「spec」一个运行工作目录：`doc/spec-changes/<spec>/`，其 board 位于 `doc/spec-changes/<spec>/board/`。
- spec 目录由 **TeamLead 创建并初始化**：建 `board/tasks/`、`board/messages/`，从 `doc/skills/team/board/template/view.html` 复制状态盘到 `board/view.html`，写初始 `board/state.json`，从 `doc/skills/team/core/template/context-template.md` 落地 `board/context.md`（填入用户原始需求原话）。
- **SDD/team 分层目录（推荐）**：`board/` 只放 team 执行状态；`delivery/` 放 SDD 的 `status.json` / `spec-doc.json` 与模块文档；`hub/` 放 hub 入口。hub 是唯一对外入口，`board/state.json` 不写 SDD cell 状态。
- TeamLead 调用 worker 的指令必须包含：**spec 目录路径 + task id**（worker 看不到 chat 上下文）。
- 任务产物写入 spec 目录内任务文件明确授权的路径。
- `doc/skills/**` 为规范目录：**所有 agent 只读，禁止写入**。

## board 目录

```
doc/spec-changes/<spec>/board/
├── tasks/task-NNN.md       # 每任务一文件，NNN 从 001 递增
├── messages/msg-NNNN.md    # 每消息一文件，NNNN 从 0001 递增
├── state.json              # 状态汇总视图（TeamLead 单点维护，契约见「state.json」节）
├── context.md              # spec 上下文汇总视图（TeamLead 单点维护，见「context.md」节）
└── view.html               # 只读状态盘（初始化时从 team/board/template 复制）
```

新编号 = 扫描对应目录现有最大编号 + 1（写前必须重新扫描）。

## 任务文件 schema（tasks/task-NNN.md）

```yaml
---
id: task-001
title: 一句话标题
status: open            # open → claimed → done | failed | cancelled；failed 可重试回 open；done = 本次 attempt 成功，不等于 cell done
assignee: team-worker-plan | team-worker-research | team-worker-code | user（manual task，见「manual task 增量」）
created_by: TeamLead
cell: null              # SDD 单元 <module>:<stage>；维护任务可为 null
input_versions: {}      # SDD 输入版本，如 { "api.requirement": "v1.2.0" }
attempt: 1              # 针对该 cell/input_versions 的第几次尝试；retry 时 +1
output_version: null    # attempt 成功时由 worker 填写
worktree: null          # 并行模式：该任务 worktree 绝对路径（见「并行模式」节）
branch: null            # 并行模式：spec/<spec>/task-NNN
ts: 2026-10-15T12:00:00Z
---
```

正文固定三段：

```markdown
## 任务描述
（自包含：所需信息全部写在这里，含 spec 目录路径、输入文件、输出授权路径）

## 验收标准
- ...

## 结果
（worker 完成时填写：摘要 + 产物/改动文件路径；其余时间保持为空）
```

## 消息文件 schema（messages/msg-NNNN.md）

```yaml
---
id: msg-0001
type: task.assign | task.claim | task.result | task.failed | task.cancelled | ask | info
from: TeamLead | team-worker-plan | team-worker-research | team-worker-code | user（仅批准 gate 记录）
to: TeamLead | team-worker-plan | team-worker-research | team-worker-code | user（仅批准 gate 记录）
task: task-001          # 关联任务 id，与任务无关时为 null
reply_to: null          # 可选，指向被回复的 msg id
ts: 2026-10-15T12:00:00Z
---
```

正文为自由 markdown。

## 消息类型与状态机

| type | 发送方 | 时机 | 状态变化 |
|---|---|---|---|
| task.assign | TeamLead | 创建任务后 | → open |
| task.claim | worker | 开始执行前 | open → claimed |
| task.result | worker | 本次 attempt 成功，`## 结果` 已填 | claimed → done（attempt done，非 cell done） |
| task.failed | worker | 本次 attempt 失败，正文含原因 | claimed → failed |
| task.cancelled | TeamLead | SDD 失效/取消该 cell 的工作 | open / claimed → cancelled |
| ask | worker | 需要 TeamLead 决策，正文含问题与选项 | 不变 |
| info | 任意 | 答复 ask、补充说明、批准请求/批准结果（to/from 可为 `user`） | 不变 |

TeamLead 重试失败任务：修订任务描述后 `failed → open`，`attempt += 1`，写新 task.assign 消息（每任务最多重试 1 次）。
`cancelled` 是终态；SDD 变更新版本后应新建 task/attempt，不在原 task 上重试。

### manual task 增量（2026-10-18 用户指令）

- 需用户亲自执行/验收的环节可建 `assignee: user` 的 manual task（任务文件注明授权依据）。
- **manual task 的完成必须由用户明确指令触发**，格式如「完成-task-NNN」/「完成-NNN」；TeamLead 不得从测试反馈、修正意见、含糊或无关答复中推断完成，收到明确指令前不流转 `claimed → done`、不推进后续步骤（gate/分派）。
- manual task 的 status 流转与 `## 结果` 记录由 TeamLead 依用户指令/口述代操作（代录须注明来源）。

## SDD ↔ team 接口（2026-10-19）

team 是执行器，**不拥有交付状态**。SDD（`spec-driven-dev`）拥有 cell 矩阵、Contract 版本、checkpoint 与失效规则；team 只执行 WorkOrder 并报告 AttemptResult。

### WorkOrder（SDD → team）

```yaml
cell: api:contract
module: api
stage: contract
input_versions:
  api.requirement: v1.2.0
  root.contract: v1.1.0
acceptance: "tests + spec/contract 对照"
output_path: doc/spec-changes/<spec>/delivery/modules/api.json
```

### AttemptResult（team → SDD）

```yaml
task: task-007
cell: api:contract
attempt: 2
output_version: v1.2.1
commit: <hash>
artifacts: [contract/api.ts, tests/api.contract.spec.ts]
verification: "12/12 passed"
```

### 规则

1. TeamLead 建 SDD 任务时必须写 `cell`、`input_versions`、`attempt`；维护任务可 `cell: null`。
2. `task.result` 只表示 **attempt 成功**；TeamLead 不得据此把 SDD cell 标 `done`。cell 的 done/invalid 由 SDD 验收与失效规则决定。
3. SDD 把 cell 标 `invalid` 时，TeamLead 对该 cell 未完成 task 写 `task.cancelled`；新输入版本用新 task/attempt。
4. team 不得修改 Contract 或版本；发现 Contract 问题写 `ask`，由 SDD 回退到 Contract 段。
5. `delivery/status.json` 是 cell 状态唯一来源；`board/state.json` 是执行状态视图，不复制 cell 状态。
6. 回滚由 SDD 选 checkpoint；team 只执行 worktree/branch 的 revert/重建。

## 人工批准 gate（分派许可 / dispatch approval）

这是 **team 执行许可**，不是 SDD 的 Contract review / Final review（交付验收）。拆解完成后、任何分派之前，必须经过人工批准 gate：

1. **规划摘要**：TeamLead 在 chat 向用户输出：需求理解表、拆分结构（mermaid）、每任务 assignee/授权白名单/产物、风险与开放问题（含 gate 位置：研究类任务之后、实现类任务之前是否设第二批准点）。
2. **记录请求**：写 `info` 消息（`to: user`，task: null）正文为规划摘要要点；`state.json` 的 `flow.phase` → `awaiting-approval`。
3. **等待与修订**：用户要求修改 → 规划期内直接调整（**尚未写 assign 的任务文件允许 TeamLead 修订或删除重建**——铁律 2 的唯一例外，仅限未分派状态），更新摘要后再记一条 info。
4. **批准通过**：批准必须是**明确表述**（「同意 / 批准 / 开跑 / 执行」）；含糊或审查性答复（如「继续看看规划」「先不急」）一律视为**未批准**，phase 停留 `awaiting-approval`，不得分派任何任务。批准后写 info 消息（reply_to 指向请求消息）记录批准结论与范围 → phase → `executing` → 开始写 task.assign 分派。
5. **跳过 gate**：仅当用户明确表示（「直接跑/跳过批准」）；info 消息中记录用户原话。
6. **双 gate（可选）**：规划摘要中声明「实现任务前二次批准」；到达时重复 1–4（范围限实现段）。

## 并行模式（流程正本见 team/parallel）

子任务 ≥2 且授权路径互不相交时，TeamLead 可按 `doc/skills/team/parallel/SKILL.md`（流程正本）分割并行 worker。协议增量：

1. **worktree 隔离**：每个并行任务一个 git worktree + 一个分支；任务 frontmatter 填 `worktree`（绝对路径）与 `branch`；worker 的业务文件操作只在自己的 worktree 内。
2. **board 永远共享**：board 不进 worktree；所有 agent 以绝对路径读写主工作副本的 board（worktree 看不到主目录未提交文件）。
3. **消息编号命名空间**：并行任务的消息用 `msg-<taskid>-NN`（如 `msg-task-003-01`），无锁不撞号；TeamLead 的全局消息仍用 `msg-NNNN`。
4. **合并只由 TeamLead 做**：worker 在自己分支 commit 并在 task.result 报 hash；TeamLead 核验后串行 `git merge --no-ff`、清理 worktree/branch；worker 禁止 merge/push/切分支。
5. **通道无关**：不依赖任何 runtime 专有功能；宿主无异步通道时退化为串行调用，隔离性与正确性不变。

## 铁律

1. **append-only**：消息文件写入后不得修改或删除；纠错用新消息 `reply_to` 指向原消息。
2. **任务文件最小写入**：只有 frontmatter `status` 和 `## 结果` 段允许被更新，且只能按状态机流转（唯一例外：规划期尚未 assign 的任务文件可由 TeamLead 修订/删除重建，见「人工批准 gate」）。
3. **写权限**：TeamLead 只写 spec 的 `board/**`；worker 写 spec 的 `board/**` + 任务描述明确授权的路径；任何 agent 不得写 `doc/skills/**`。
4. **时间戳**：`ts` 用 ISO8601（UTC），不得留空。
5. **返回摘要**：worker 结束后向调用方返回一行 `task-NNN done|failed|ask: <一句话>`。
6. **state.json 单点维护**：唯一写者是 TeamLead，每次状态流转后立即写盘。

## state.json（数据契约与维护职责）

- 位置：`doc/spec-changes/<spec>/board/state.json`。**唯一写者是 TeamLead**；worker 与 `view.html` 渲染页只读（渲染行为与启动方式见 `doc/skills/team/board/SKILL.md`）。
- **SDD 交付状态在 `delivery/status.json`，不在本文件**；`state.json` 只记录执行 attempt，`tasks[].cell` 仅作引用。
- 更新时机：创建任务、分派（assign）、确认 worker 回帖（claim/result/failed）、ask 处理、汇总完成——每次状态流转后立即写盘。spec 初始化时由 TeamLead 首次写入（phase: planning）。
- 一致性规则：state.json 只是汇总视图；任务文件与消息文件仍是事实源。冲突时以文件为准，TeamLead 负责修正 state.json。
- 骨架模板：`doc/skills/team/core/template/state-template.json`。
- **数据契约（schema 正本）**：

```json
{
  "version": 1,
  "updated": "<ISO8601>",
  "spec": "<spec 名>",
  "runtime": "<copilot-agents | kilo-task-tool | ...>",
  "flow": {
    "goal": "<总任务一句话>",
    "phase": "planning | awaiting-approval | executing | aggregating | done | blocked",
    "steps": [ { "id": "step-1", "task": "task-001", "cell": "api:contract | null", "desc": "...", "assignee": "<agent>", "state": "pending | running | done | failed | cancelled" } ]
  },
  "agents": [ { "name": "<agent>", "role": "lead | worker", "state": "idle | working | waiting | failed", "detail": "..." } ],
  "tasks": [ { "id": "task-001", "title": "...", "status": "open | claimed | done | failed | cancelled", "cell": "api:contract | null", "input_versions": {}, "attempt": 1, "output_version": null, "assignee": "<agent>", "messages": ["msg-0001"] } ],
  "messages": [ { "id": "msg-0001", "type": "<消息类型>", "from": "<agent>", "to": "<agent>", "task": "task-001", "ts": "<ISO8601>" } ]
}
```

字段约束：`tasks[]` 只登记已建任务文件的 task（`flow.steps[]` 可含规划中步骤）；枚举值不得超出上表；`messages[]` 按时间序；并行增量——`flow.steps[]` 允许多个 `running`、`agents[]` 多个 worker 同时 `working`。`flow.phase` 是**执行阶段**，不是 SDD 交付阶段；`cell/input_versions/attempt/output_version` 是 SDD 对接字段，`status=done` 仅表示 attempt 成功。

## context.md（spec 上下文）

- 位置：`doc/spec-changes/<spec>/board/context.md`；模板 `doc/skills/team/core/template/context-template.md`。**唯一写者是 TeamLead**（同 state.json），worker 只读。
- 定位：人类可读的上下文汇总视图——**用户原始需求（原话）**、**决议记录**（gate 批准/Q&A/修订，每条注明日期 + 依据 msg/chat）、**术语演进**、**关键约束**。运行中随决议产生及时追加，不等到收尾。
- 与事实源的关系：context.md 是汇总，冲突时以消息/任务文件为准（同 state.json 定位）；chat 不持久化，用户指令与决议必须及时落 context.md 或消息文件。
- 任务描述**自包含**要求不变：worker 调用仍以任务文件为准，context.md 仅作补充背景（调用指令中可给出路径）。

## view.html（状态盘）

可视化契约（渲染行为、启动方式、模板位置）见 `doc/skills/team/board/SKILL.md`。TeamLead 初始化 spec 时复制 `doc/skills/team/board/template/view.html` 到 `board/view.html`。
