<!--
worker-reply.md — worker 回帖文案模板（research / code 通用）
正本：doc/skills/team/core/template/。复制后替换 <占位符>；可选段按标注取舍。
-->

## 1. task.claim 正文

> 已领取 task-NNN（status: open → claimed）。
> 执行计划：<1~3 步>。产出：<授权路径>。<预期约束点：无则省略>

## 2. `## 结果` 段（任务文件内）

> **摘要**：<1~3 句：做了什么、结论要点>。
>
> **产物/改动清单**：
> - <路径>（新建｜修改）— <一句话>
>
> **commit**：`<hash>`（分支 <branch>）［并行任务必填］
> **output_version**：<vX.Y.Z>［任务含 cell 时必填；只写本次 attempt 的输出，不改 cell 状态］
> **协议自检**：<越权/违反项如实记录；无则写「无」>

## 3. task.result 正文

> task-NNN 已完成（status: claimed → done）。
> **结果摘要**：<与 ## 结果 摘要一致>。
> **产物路径**：<清单>；commit `<hash>`；output_version `<vX.Y.Z>`。［并行任务必填 hash；含 cell 任务必填 output_version］

## 4. task.failed 正文

> task-NNN 失败（status: claimed → failed）。
> **原因**：<根因一句话>。
> **已完成部分**：<清单｜无>。
> **建议**：<重试修订要点｜替代方案>。

## 5. ask 正文

> **问题**：<一句话>。
> **背景**：<为何需要决策，1~2 行>。
> **选项**：
> - A：<方案> — <影响>
> - B：<方案> — <影响>
>
> **推荐**：<A｜B>（<一句理由>）。

## 6. 返回行（给调用方的唯一输出）

> task-NNN done|failed|ask: <一句话摘要>
