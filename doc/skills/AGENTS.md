# doc/skills 目录规则

1. **顶层只有本文件**：`doc/skills/` 顶层仅允许 `AGENTS.md`；其余任何文件必须归属某个 skill 目录，没有合适的就新建 skill。
2. **每个 skill 一个目录**：目录名即 skill 名（kebab-case），如 `team/`、`spec-doc-gen/`、`spec-driven-dev/`、`hub/`。
3. **SKILL.md 为入口**：每个顶层 skill 目录必有 `SKILL.md`，frontmatter 含 `name`（与目录名一致）与 `description`（何时使用）；正文为可执行流程/规则/索引。`team/` 下的 `core/`、`board/`、`parallel/` 是组件目录，可各带组件 `SKILL.md`，但不单独作为顶层 skill。
4. **资源同目录**：协议正本、模板、assets、公共文档等一律放所属 skill 目录内；skill 内部用相对路径引用，跨 skill/跨目录引用用仓库相对路径。
5. **运行时只读**：执行 agent（TeamLead / worker）对 `doc/skills/**` 一律只读；修订只能在 spec 结束后作为维护动作进行。
6. **增删 skill 同步索引**：更新本文件「当前 skill 清单」，以及 `team/core/design.md` §2 的目录树。
7. **skill 间依赖显式声明**：在 SKILL.md 开头写明依赖（如 team/parallel 依赖 team/core 协议正本；hub 依赖 spec-doc-gen / spec-driven-dev / team）；规则冲突时以被依赖的正本为准。

# do/don't

- 不允许未通过approve就修改rule/skills下的内容
- 不允许在 rule/skills 下加入没明确要求的/联想/幻想的内容 

## 当前 skill 清单

| skill | 目录 | 职责 |
|---|---|---|
| team | `team/` | **多 agent 执行器**：`core/` 协议与状态机、`board/` 执行状态盘、`parallel/` worktree 并行；与 SDD 通过 WorkOrder / AttemptResult 对接，只执行不判断交付完成 |
| spec-doc-gen | `spec-doc-gen/` | **代码 → SDD spec HTML + 状态页**：`spec-doc-template.html` + `process-status-template.html`，分别由 `spec-doc.json` / `status.json` 驱动；每条结论带 `path:line` 锚点，只生成文档与状态页、不实现 |
| spec-driven-dev | `spec-driven-dev/` | **SDD 二维交付**：模块/子模块 × 需求/Contract/实现/验证矩阵，逐格推进、下游锥失效与可重试；定义 Contract/版本/checkpoint/回滚，并向 team 发出 WorkOrder |
| hub | `hub/` | **单一入口**：NavTabs 聚合 SDD 交付状态、模块树、模块文档与 Team 执行；由 `hub.json` 驱动，不复制 SDD/team 规则 |

> 文档规范不在本目录：`doc/rule/doc-format/`（格式/结构/必含 Terminology/版本管理）+ `doc/rule/doc-viz/`（可视化/mermaid 优先/图型选择/view.html 渲染规则）。