---
id: task-001
title: 调研对比三种多 agent 编排模式，产出 patterns.md
status: open
assignee: team-worker-research
created_by: TeamLead
worktree: null          # 执行时由 TeamLead 填入 ../.worktrees/parallel-demo/task-001 的绝对路径
branch: null            # 执行时填 spec/parallel-demo/task-001
ts: 2026-10-15T14:12:00Z
---

## 任务描述

spec 根：`E:\code\project\techs\doc\specs\parallel-demo`（board 位于其下）。仓库根：`E:\code\project\techs`。

**本任务为并行任务**（team-parallel 技能）：业务操作只在 frontmatter `worktree` 指定的目录内；board 与规范一律用绝对路径——协议正本 `E:\code\project\techs\doc\skills\team-board\board-protocol.md`，并行技能 `E:\code\project\techs\doc\skills\team-parallel\SKILL.md`（先读「worker 契约」节）。

调研并对比三种多 agent 编排模式：
1. 顺序 pipeline（固定次序接力）
2. 文件 board 主从（本仓库 team-board 串行模式）
3. worktree 并行分割（本仓库 team-parallel 模式）

每种模式分析：适用场景 / 隔离性 / 冲突面（文件写、编号、状态漂移）/ 恢复复杂度。结合本仓库规范文档给出依据引用（文件+章节）。

**唯一授权输出**：`doc/specs/parallel-demo/research/patterns.md`（worktree 内创建）。
消息编号用命名空间 `msg-task-001-NN`。完成后 `git add -A && git commit -m "task-001: <摘要>"`，commit hash 写入 `## 结果` 与 task.result 消息。

## 验收标准

- patterns.md 含三模式对比总表 + 逐模式分析 + 本仓库规范出处引用
- 并行 worker 契约齐全：命名空间消息（claim/result）、commit hash、白名单外零改动
- status 流转 open → claimed → done

## 结果
