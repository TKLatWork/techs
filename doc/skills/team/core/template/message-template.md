<!--
message-template.md — 消息文件模板（提取自 team/core/protocol.md 正本）
位置：doc/spec-changes/<spec>/board/messages/ 下，每消息一文件。
编号：串行/全局消息 msg-NNNN（NNNN 从 0001 递增，写前重新扫描目录取最大编号 + 1）；
并行任务消息用命名空间 msg-<taskid>-NN（如 msg-task-003-01），无锁不撞号。
铁律 append-only：消息写入后不得修改或删除；纠错用新消息 reply_to 指向原消息。
正文为自由 markdown。ts 用 ISO8601（UTC），不得留空。
-->

---
id: msg-0001                # 消息 id，与文件名一致；并行命名空间为 msg-<taskid>-NN
type: task.assign           # task.assign | task.claim | task.result | task.failed | task.cancelled | ask | info
from: TeamLead              # TeamLead | team-worker-research | team-worker-code
to: TeamLead                # TeamLead | team-worker-research | team-worker-code
task: task-001              # 关联任务 id，与任务无关时为 null
reply_to: null              # 可选，指向被回复的 msg id（纠错/答复时使用）
ts: 2026-10-15T12:00:00Z    # ISO8601（UTC），不得留空
---

（正文：自由 markdown）

<!--
七种消息类型的使用说明（发送方 | 时机 | 状态变化）：

1. task.assign  | TeamLead | 创建任务后             | → open
   正文：调用指令要点（spec 目录路径 + task id；并行任务含 worktree 绝对路径）。
2. task.claim   | worker   | 开始执行前             | open → claimed
   正文：确认领取，简述执行计划。
3. task.result  | worker   | 本次 attempt 成功，「## 结果」已填 | claimed → done（非 cell done）
   正文：摘要 + 产物/改动文件路径 + output_version；并行任务另含 commit hash。
4. task.failed  | worker   | 本次 attempt 失败      | claimed → failed
   正文：失败原因 + 已完成部分。TeamLead 可修订后重试（每任务最多 1 次，failed → open）。
5. task.cancelled | TeamLead | SDD 失效/取消该 cell | open / claimed → cancelled
   正文：取消原因、上游版本变化；终态，不重试，新版本另建 task。
6. ask          | worker   | 需要 TeamLead 决策      | 不变
   正文：问题与可选项；TeamLead 以 info 答复后 worker 续作（勿重复 claim）。
7. info         | 任意     | 答复 ask、补充说明、merge 记录 | 不变
-->
