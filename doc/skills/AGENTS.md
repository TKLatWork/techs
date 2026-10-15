# doc/skills 目录规则

1. **每个 skill 一个目录**：目录名即 skill 名（kebab-case），如 `team-board/`、`team-parallel/`。顶层不允许散落 skill 文件。
2. **SKILL.md 为入口**：每个 skill 目录必有 `SKILL.md`，frontmatter 含 `name`（与目录名一致）与 `description`（何时使用）；正文为可执行流程/规则。
3. **资源同目录**：协议正本、模板、assets 等放所属 skill 目录内，SKILL.md 用相对路径引用；跨目录引用用仓库相对路径。
4. **顶层只放公共文档**：`README.md`（索引）、`requirements.md`、`design.md`、`NOTES.md`、`AGENTS.md`（本文件）。公共文档不属于任何 skill。
5. **运行时只读**：执行 agent（TeamLead / worker）对 `doc/skills/**` 一律只读；修订只能在 spec 结束后作为维护动作进行。
6. **增删 skill 同步索引**：更新 `README.md` 目录约定表、`requirements.md` §8 与 `design.md` §2 的树。
7. **skill 间依赖显式声明**：在 SKILL.md 开头写明依赖（如 team-parallel 依赖 team-board 协议正本）；规则冲突时以被依赖的正本为准。

## 当前 skill 清单

| skill | 目录 | 职责 |
|---|---|---|
| team-board | `team-board/` | 文件 board 编排协议（串行基线）：spec 目录、任务/消息 schema、状态机、state.json 状态盘 |
| team-parallel | `team-parallel/` | worktree 并行编排增量：git 分割、编号命名空间、merge 汇合（依赖 team-board） |
