# doc/skills 目录规则

1. **顶层只有本文件**：`doc/skills/` 顶层仅允许 `AGENTS.md`；其余任何文件必须归属某个 skill 目录，没有合适的就新建 skill。
2. **每个 skill 一个目录**：目录名即 skill 名（kebab-case），如 `team-core/`、`team-board/`、`team-parallel/`。
3. **SKILL.md 为入口**：每个 skill 目录必有 `SKILL.md`，frontmatter 含 `name`（与目录名一致）与 `description`（何时使用）；正文为可执行流程/规则/索引。
4. **资源同目录**：协议正本、模板、assets、公共文档等一律放所属 skill 目录内；skill 内部用相对路径引用，跨 skill/跨目录引用用仓库相对路径。
5. **运行时只读**：执行 agent（TeamLead / worker）对 `doc/skills/**` 一律只读；修订只能在 spec 结束后作为维护动作进行。
6. **增删 skill 同步索引**：更新本文件「当前 skill 清单」，以及 `team-core/design.md` §2 的目录树。
7. **skill 间依赖显式声明**：在 SKILL.md 开头写明依赖（如 team-parallel 依赖 team-board 协议正本）；规则冲突时以被依赖的正本为准。

# do/don't

- 不允许未通过approve就修改rule/skills下的内容
- 不允许在 rule/skills 下加入没明确要求的/联想/幻想的内容 

## 当前 skill 清单

| skill | 目录 | 职责 |
|---|---|---|
| team-core | `team-core/` | **team 主运行框架**：protocol.md（协议正本：spec/task/message/状态机/manual task/铁律/并行增量/**state.json 数据契约**/**context.md spec 上下文**）+ template/（task · message · state-template · context-template · lead-subagent-io · lead-spec-context · worker-reply）+ design.md（架构依据）+ NOTES.md（运行观测） |
| team-board | `team-board/` | **过程可视化（渲染）**：template/view.html 状态盘渲染页 + 渲染契约/启动方式；state.json 数据契约正本在 team-core/protocol.md |
| team-parallel | `team-parallel/` | worktree 并行编排增量：git 分割、编号命名空间、merge 汇合（依赖 team-board） |
| spec-driven-dev | `spec-driven-dev/` | 模块可视化文档：五 tab 单文件 HTML（概要/需求/接口契约/测试/架构）+ template/ 占位骨架 |

> 文档规范不在本目录：`doc/rule/doc-format/`（格式/结构/必含 Terminology/版本管理）+ `doc/rule/doc-viz/`（可视化/mermaid 优先/图型选择/view.html 渲染规则）。
