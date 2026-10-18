<!--
task-template.md — 任务文件模板（提取自 team-core/protocol.md 正本）
位置：doc/specs/<spec>/board/tasks/task-NNN.md，NNN 从 001 递增。
新编号 = 扫描 tasks/ 目录现有最大编号 + 1（写前必须重新扫描）。
创建者：TeamLead（worker 只允许更新 frontmatter status 与「## 结果」段）。
-->

---
id: task-001                      # 任务 id，与文件名一致（task-NNN）
title: 一句话标题                  # 任务的简短标题
status: open                      # open → claimed → done | failed；TeamLead 重试时 failed → open
assignee: team-worker-research    # team-worker-plan | team-worker-research | team-worker-code
created_by: TeamLead              # 创建者，固定为 TeamLead
worktree: null                    # 并行模式：该任务 worktree 绝对路径（见「并行模式」节）；串行任务为 null
branch: null                      # 并行模式：spec/<spec>/task-NNN；串行任务为 null
ts: 2026-10-15T12:00:00Z          # 创建时间，ISO8601（UTC），不得留空
---

## 任务描述
<!--
自包含：所需信息全部写在这里，含 spec 目录路径、输入文件、输出授权路径。
并行任务必须写全四要素 + 并行两要素：board 绝对路径、协议正本绝对路径、
worktree 内授权范围、验收标准，以及「完成后 git add -A && git commit -m
"task-NNN: <摘要>"，把 commit hash 写进 ## 结果 与 task.result 消息」。
-->

## 验收标准
- ...

## 结果
<!--
worker 完成时填写：摘要 + 产物/改动文件路径（并行任务另含 commit hash）；
其余时间保持为空。消息 append-only：写错不改不删，发新消息 reply_to 纠错。
-->
