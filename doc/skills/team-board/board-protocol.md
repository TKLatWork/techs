# board 消息协议 v0.1（agent 运行时必读）

正本位置：`doc/skills/team-board/board-protocol.md`。所有 agent（TeamLead、team-worker-research、team-worker-code）执行任务前后必须遵守本协议。

## spec 目录（运行工作目录）

- 每个「spec」一个运行工作目录：`doc/specs/<spec>/`，其 board 位于 `doc/specs/<spec>/board/`。
- spec 目录由 **TeamLead 创建并初始化**：建 `board/tasks/`、`board/messages/`，从 `doc/skills/team-board/assets/view.html` 复制状态盘到 `board/view.html`，写初始 `board/state.json`。
- TeamLead 调用 worker 的指令必须包含：**spec 目录路径 + task id**（worker 看不到 chat 上下文）。
- 任务产物写入 spec 目录内任务文件明确授权的路径。
- `doc/skills/**` 为规范目录：**所有 agent 只读，禁止写入**。

## board 目录

```
doc/specs/<spec>/board/
├── tasks/task-NNN.md       # 每任务一文件，NNN 从 001 递增
├── messages/msg-NNNN.md    # 每消息一文件，NNNN 从 0001 递增
├── state.json              # 状态汇总视图（TeamLead 单点维护，见末节）
└── view.html               # 只读状态盘（初始化时从 assets 复制）
```

新编号 = 扫描对应目录现有最大编号 + 1（写前必须重新扫描）。

## 任务文件 schema（tasks/task-NNN.md）

```yaml
---
id: task-001
title: 一句话标题
status: open            # open → claimed → done | failed；TeamLead 重试时 failed → open
assignee: team-worker-research | team-worker-code
created_by: TeamLead
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
type: task.assign | task.claim | task.result | task.failed | ask | info
from: TeamLead | team-worker-research | team-worker-code
to: TeamLead | team-worker-research | team-worker-code
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
| task.result | worker | 成功完成，`## 结果` 已填 | claimed → done |
| task.failed | worker | 失败，正文含原因 | claimed → failed |
| ask | worker | 需要 TeamLead 决策，正文含问题与选项 | 不变 |
| info | 任意 | 答复 ask、补充说明 | 不变 |

TeamLead 重试失败任务：修订任务描述后 `failed → open`，写新 task.assign 消息（每任务最多重试 1 次）。

## 并行模式（v0.2 增量）

子任务 ≥2 且授权路径互不相交时，TeamLead 可按 `doc/skills/team-parallel/SKILL.md`（流程正本）分割并行 worker。协议增量：

1. **worktree 隔离**：每个并行任务一个 git worktree + 一个分支；任务 frontmatter 填 `worktree`（绝对路径）与 `branch`；worker 的业务文件操作只在自己的 worktree 内。
2. **board 永远共享**：board 不进 worktree；所有 agent 以绝对路径读写主工作副本的 board（worktree 看不到主目录未提交文件）。
3. **消息编号命名空间**：并行任务的消息用 `msg-<taskid>-NN`（如 `msg-task-003-01`），无锁不撞号；TeamLead 的全局消息仍用 `msg-NNNN`。
4. **合并只由 TeamLead 做**：worker 在自己分支 commit 并在 task.result 报 hash；TeamLead 核验后串行 `git merge --no-ff`、清理 worktree/branch；worker 禁止 merge/push/切分支。
5. **通道无关**：不依赖任何 runtime 专有功能；宿主无异步通道时退化为串行调用，隔离性与正确性不变。

## 铁律

1. **append-only**：消息文件写入后不得修改或删除；纠错用新消息 `reply_to` 指向原消息。
2. **任务文件最小写入**：只有 frontmatter `status` 和 `## 结果` 段允许被更新，且只能按状态机流转。
3. **写权限**：TeamLead 只写 spec 的 `board/**`；worker 写 spec 的 `board/**` + 任务描述明确授权的路径；任何 agent 不得写 `doc/skills/**`。
4. **时间戳**：`ts` 用 ISO8601（UTC），不得留空。
5. **返回摘要**：worker 结束后向调用方返回一行 `task-NNN done|failed|ask: <一句话>`。
6. **state.json 单点维护**：唯一写者是 TeamLead，每次状态流转后立即写盘。

## state.json（状态汇总视图）

- 位置：`doc/specs/<spec>/board/state.json`。**唯一写者是 TeamLead**；worker 与 `view.html` 渲染页只读。
- 更新时机：创建任务、分派（assign）、确认 worker 回帖（claim/result/failed）、ask 处理、汇总完成——每次状态流转后立即写盘。spec 初始化时由 TeamLead 首次写入（phase: planning）。
- 一致性规则：state.json 只是汇总视图；任务文件与消息文件仍是事实源。冲突时以文件为准，TeamLead 负责修正 state.json。
- Schema：

```json
{
  "version": 1,
  "updated": "<ISO8601>",
  "spec": "<spec 名>",
  "runtime": "<copilot-agents | kilo-task-tool | ...>",
  "flow": {
    "goal": "<总任务一句话>",
    "phase": "planning | executing | aggregating | done | blocked",
    "steps": [ { "id": "step-1", "task": "task-001", "desc": "...", "assignee": "<agent>", "state": "pending | running | done | failed" } ]
  },
  "agents": [ { "name": "<agent>", "role": "lead | worker", "state": "idle | working | waiting | failed", "detail": "..." } ],
  "tasks": [ { "id": "task-001", "title": "...", "status": "open | claimed | done | failed", "assignee": "<agent>", "messages": ["msg-0001"] } ],
  "messages": [ { "id": "msg-0001", "type": "<消息类型>", "from": "<agent>", "to": "<agent>", "task": "task-001", "ts": "<ISO8601>" } ]
}
```

## view.html（状态盘）

- 模板：`doc/skills/team-board/assets/view.html`，TeamLead 初始化 spec 时复制到 `board/view.html`。
- NavTab 多标签：首页总视图（每 2s fetch `state.json`），点击 task/消息 id 新开标签渲染对应 .md。
- file:// 下 fetch 被 CORS 拦截，须经 http 打开：

  ```powershell
  npx -y http-server doc\specs\<spec>\board -p 8787 -c-1   # → http://127.0.0.1:8787/view.html
  ```

  或用 VS Code Live Preview 扩展右键 `board/view.html` → Show Preview。
