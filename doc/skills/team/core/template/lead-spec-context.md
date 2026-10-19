<!--
lead-spec-context.md — TeamLead 管理 spec 运行上下文的文案模板（正本，唯一出处）
覆盖：spec 初始化 / merge 汇合记录 / 对用户的汇总与决策请求。
与 sub-agent 的交互文案见 lead-subagent-io.md。
结构受 doc/rule/doc-viz/SKILL.md §1 约束（结论先行、表格化、mermaid 图示优先）。
-->

## 1. spec 初始化清单（按序执行）

- [ ] 读协议正本 `doc/skills/team/core/protocol.md`
- [ ] 确定 spec slug（用户指定或从任务提炼英文短横线名）→ `doc/spec-changes/<spec>/`
- [ ] 建 `board/tasks/`、`board/messages/`
- [ ] 复制 `doc/skills/team/board/template/view.html` → `board/view.html`
- [ ] 从 `doc/skills/team/core/template/state-template.json` 落地 `board/state.json`（phase: planning，只登记已建任务文件的 task）
- [ ] 从 `doc/skills/team/core/template/context-template.md` 落地 `board/context.md`（填入用户原始需求原话 + 已知决议；运行中随决议及时追加）
- [ ] 若使用 SDD：确认 `delivery/status.json` 与 `hub/hub.json` 存在（不存在则上报，不由 team 自造）；SDD 任务的 task 必须带 `cell`/`input_versions`/`attempt`
- [ ] 扫描 tasks/、messages/ 确定下一个可用编号
- [ ] （并行模式）`git status` 干净？`.gitignore` 含 `.worktrees/`？
- [ ] **委托拆解**：创建 task-001（assignee: team-worker-plan），任务描述含 spec 目标 + 用户原始需求 + 仓库上下文；完成后收回拆解计划（子任务清单：assignee / 白名单 / 产物 / 依赖 / 并行判定 / gate 位置）
- [ ] **批准 gate**：收到拆解计划后输出规划摘要（§6）请求用户批准，写 info 记录，phase → awaiting-approval；批准通过后才建 task-002 起的任务文件并分派

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
> state.json 已置 done；观测已记入 `team/core/NOTES.md`。
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

- 每次状态流转后立即更新 `board/state.json`（flow/steps、agents、tasks、messages 索引）；不写 SDD cell 状态
- chat 进度简报：`[task-NNN <状态>] <一句话>`，状态 ∈ assigned / claimed / done / failed / cancelled / ask / merged
- 汇总前终检：扫描全部任务文件终态 + 有无未答复 ask + 有无停留 claimed 的任务
- SDD 任务终检：task.done 只代表 attempt 成功；cell 仍需 SDD 验收；SDD 失效的 cell 对应 task 必须 cancelled

## 6. 规划批准请求（人工 gate）

> **spec <spec> 规划摘要 — 请批准**
>
> 需求理解：[R# ｜ 需求 ｜ 落点任务] 表
> 拆分结构：mermaid flowchart（依赖 + 串行/并行标注）
> 每任务：[task ｜ assignee ｜ 授权白名单 ｜ 产物 ｜ cell/input_versions（SDD 任务）]
> 风险/开放问题：[条目 + 推荐选项]
> gate 位置：[单 gate（批准后全程执行）｜ 双 gate（实现段前二次批准）]
>
> 回复「同意」执行，或提出修改点；回复「直接跑」跳过 gate。

批准/修订/跳过均以 info 消息落 board（`to/from: user`），批准消息 reply_to 指向请求消息。
