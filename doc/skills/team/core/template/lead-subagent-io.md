<!--
lead-subagent-io.md — TeamLead ↔ sub-agent 交互文案模板（正本，唯一出处）
覆盖：调用行 / task.assign 正文 / ask 答复 / 重试修订。复制后替换 <占位符>。
spec 运行上下文类文案（初始化/merge/汇总/决策请求）见 lead-spec-context.md。
-->

## 1. worker 调用行（唯一允许的内联信息 = 路径 + id）

plan worker：
> 你是 team-worker-plan。spec 目录 doc/spec-changes/<spec>，新任务 task-001。先读 doc/skills/team/core/protocol.md，再读 spec 目录下 board/tasks/task-001.md，按协议执行。任务描述含 spec 目标 + 用户原始需求 + 仓库上下文；产出拆解计划写入 ## 结果。完成后将拆解计划作为 gate 批准材料提交用户。

research / code（串行）：

> 你是 <assignee>。spec 目录 doc/spec-changes/<spec>，新任务 task-NNN。先读 doc/skills/team/core/protocol.md，再读 spec 目录下 board/tasks/task-NNN.md，按协议执行。

并行追加段：

> 你的 worktree：<绝对路径>（分支 <branch>，已建好）。业务文件操作只在 worktree 内；board 用主副本绝对路径 <board绝对路径>，勿写 worktree 内 board 副本。消息编号用命名空间 msg-task-NNN-NN。先读 doc/skills/team/parallel/SKILL.md「worker 契约」节。完成后 worktree 内 git add -A && git commit，hash 写入 ## 结果 与 task.result。

## 2. task.assign 消息正文

> 新任务 task-NNN 已分派给 <assignee>（spec: <spec>[，**并行模式**]）。
> worktree：<绝对路径>（分支 <branch>，已建好）。［并行时］
> board：<绝对路径>。
> cell：<module>:<stage>；input_versions：<...>；attempt：<N>。［SDD 任务必填；维护任务写 cell: null］
> 请读取 board/tasks/task-NNN.md（含授权白名单），按协议执行：领取（claimed + task.claim）→ 执行 → `## 结果` 填摘要与产物路径 + output_version → done + task.result。消息编号用 <全局 msg-NNNN｜命名空间 msg-task-NNN-NN>。

## 3. 重试修订（failed → open 时的新 assign 正文追加段）

> **重试说明**：上一次执行 failed（见 <msg-id>）。修订点：<针对失败原因的具体修订，写在正文，不改任务文件描述>。已完成部分：<可复用/需重做>。

## 4. ask 答复（info 消息正文）

> 答复 <msg-id>（task-NNN）：<决策/答案>。
> 续作指引：从 <步骤> 继续；任务已是 claimed，勿重复 claim；<其他必要上下文>。

（答复后重新调用该 worker，调用行追加：「任务 task-NNN 已 claimed，读消息链中 <info-msg-id> 的答复后续作，勿重复 claim」。）

## 5. task.cancelled 消息正文（SDD 失效/取消）

> task-NNN 取消（status: <open｜claimed> → cancelled）。
> **原因**：SDD cell `<module>:<stage>` 失效/取消（上游版本 `<old>` → `<new>`）。
> **处理**：本次 attempt 终止，不重试；新版本另建 task。cell 状态以 `delivery/status.json` 为准。
