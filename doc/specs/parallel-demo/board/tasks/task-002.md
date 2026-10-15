---
id: task-002
title: 提取协议模板，产出 templates/ 三件套
status: open
assignee: team-worker-code
created_by: TeamLead
worktree: null          # 执行时由 TeamLead 填入 ../.worktrees/parallel-demo/task-002 的绝对路径
branch: null            # 执行时填 spec/parallel-demo/task-002
ts: 2026-10-15T14:12:00Z
---

## 任务描述

spec 根：`E:\code\project\techs\doc\specs\parallel-demo`（board 位于其下）。仓库根：`E:\code\project\techs`。

**本任务为并行任务**（team-parallel 技能）：业务操作只在 frontmatter `worktree` 指定的目录内；board 与规范用绝对路径——协议正本 `E:\code\project\techs\doc\skills\team-board\board-protocol.md`，并行技能 `E:\code\project\techs\doc\skills\team-parallel\SKILL.md`（先读「worker 契约」节）。

从协议正本提取可直接复制的模板到 `doc/specs/parallel-demo/templates/`（worktree 内创建，**唯一授权目录**）：

1. `task-template.md`：任务 frontmatter 骨架（含 worktree/branch 字段）+ 三段正文（任务描述/验收标准/结果），每字段带注释
2. `message-template.md`：六种消息类型（task.assign / task.claim / task.result / task.failed / ask / info）的 frontmatter 骨架 + 使用说明
3. `state-template.json`：state.json schema 骨架，字段与枚举值和协议正本完全一致

消息编号用命名空间 `msg-task-002-NN`。完成后 `git add -A && git commit -m "task-002: <摘要>"`，commit hash 写入 `## 结果` 与 task.result 消息。

## 验收标准

- 三个模板文件的字段/枚举与 board-protocol.md 正本一致，且各含使用注释
- 并行 worker 契约齐全：命名空间消息（claim/result）、commit hash、白名单外零改动
- status 流转 open → claimed → done

## 结果
