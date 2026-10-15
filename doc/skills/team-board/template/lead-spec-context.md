<!--
lead-spec-context.md — TeamLead 管理 spec 运行上下文的文案模板（正本，唯一出处）
覆盖：spec 初始化 / merge 汇合记录 / 对用户的汇总与决策请求。
与 sub-agent 的交互文案见 lead-subagent-io.md。
结构受 doc/rule/doc-rule/SKILL.md §3 约束（结论先行、表格化、mermaid 图示优先）。
-->

## 1. spec 初始化清单（按序执行）

- [ ] 读协议正本 `doc/skills/team-board/board-protocol.md`
- [ ] 确定 spec slug（用户指定或从任务提炼英文短横线名）→ `doc/specs/<spec>/`
- [ ] 建 `board/tasks/`、`board/messages/`
- [ ] 复制 `doc/skills/team-board/template/view.html` → `board/view.html`
- [ ] 从 `template/state-template.json` 落地 `board/state.json`（phase: planning，只登记已建任务文件的 task）
- [ ] 扫描 tasks/、messages/ 确定下一个可用编号
- [ ] （并行模式）`git status` 干净？`.gitignore` 含 `.worktrees/`？
- [ ] 拆解子任务（授权路径互不相交才可并行；共享文件改动独立为串行收尾任务）

## 2. merge 汇合 info 消息正文（并行段结束）

> 并行段汇合完成：
> - task-NNN：核验通过（<消息合规/白名单核验要点>）→ merge `<hash>`（--no-ff）
> - worktree ×N 已 remove，分支 ×N 已 delete，冲突：<无｜处理与上报>
>
> 进入下一步：<分派串行收尾 task-NNN｜汇总>。

## 3. 汇总答复用户（spec 结束，固定结构）

> **spec <spec> 完成（phase: done）。**
>
> [结果表：task ｜ 模式 ｜ 产物 ｜ 结果/commit]
>
> 消息链 N 条（全局 n + 命名空间 n），schema 符合 N/N，状态机 <遵守｜异常>，越权 <无｜明细>。
> state.json 已置 done；观测已记入 `chat-team/NOTES.md`。
> 待决策/下一步：<条目｜无>。

## 4. 决策请求（给用户）

> <一句话背景>。请决策：
>
> | 选项 | 说明 |
> |---|---|
> | A（推荐） | <方案 + 一句理由> |
> | B | <方案> |
>
> 未选择时默认：<默认动作｜等待>。

## 5. 运行中上下文维护规则

- 每次状态流转后立即更新 `board/state.json`（flow/steps、agents、tasks、messages 索引）
- chat 进度简报：`[task-NNN <状态>] <一句话>`，状态 ∈ assigned / claimed / done / failed / ask / merged
- 汇总前终检：扫描全部任务文件终态 + 有无未答复 ask + 有无停留 claimed 的任务
