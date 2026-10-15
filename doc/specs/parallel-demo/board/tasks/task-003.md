---
id: task-003
title: 汇总调研与模板，产出 spec README.md（串行收尾）
status: open
assignee: team-worker-code
created_by: TeamLead
worktree: null          # 串行任务：直接在主工作副本执行
branch: null
ts: 2026-10-15T14:12:00Z
---

## 任务描述

spec 根：`E:\code\project\techs\doc\specs\parallel-demo`。仓库根：`E:\code\project\techs`。协议正本：`E:\code\project\techs\doc\skills\team-board\board-protocol.md`。

**本任务为串行收尾任务**：前置条件是 task-001 与 task-002 均已由 TeamLead merge 回主工作副本（TeamLead 分派本任务即代表前置已满足）。直接在主工作副本操作，无 worktree。

创建 `doc/specs/parallel-demo/README.md`，内容三节：
1. **模式对比结论**：引用 `research/patterns.md` 的三模式对比总表与关键结论
2. **模板清单**：`templates/` 三件套的用途与使用方法
3. **运行记录摘要**：从 `board/` 消息链与 state.json 提炼本 spec 的执行过程（含并行段与 merge 结果）

**授权白名单**：`doc/specs/parallel-demo/README.md`（新建）、`doc/specs/parallel-demo/board/**`、本任务文件 `## 结果` 段与 status。消息编号用全局 `msg-NNNN`（串行任务）。

## 验收标准

- README.md 含上述三节，引用路径真实有效
- status 流转 open → claimed → done；task.claim / task.result 消息符合 schema
- 白名单外零改动

## 结果
