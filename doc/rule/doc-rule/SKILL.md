---
name: doc-rule
description: 文档与会话信息展示规范：统一全仓库文档的结构与风格（配 template/ 模板目录，含 Terminology 必备规则与版本管理），并规定 agent 在 chat 会话中的信息展示格式（mermaid 图示优先、表格化、结论先行）。所有 agent 产出 .md 文档或向用户汇报时应用本规范；新建文档从 template/ 复制对应模板。
---

# doc-rule — 文档与信息展示规范

> 状态：active v0.5.0 ｜ 日期：2026-10-15 ｜ 维护者：Kilo（用户指令）

依赖技能：无（被所有 agent 与其他 skill 引用）。

**核心原则：最小化读者理解成本。** 表达手段优先级：① 图示化（mermaid）→ ② 表格化 → ③ 结论先行的短文。同一信息能画图不列表、能列表不写散文。

## Terminology

| 术语 | 定义 |
|---|---|
| 规范类文档 | `doc/rule/`、`doc/skills/` 下的 SKILL.md、协议正本、requirements/design 等约定性文档 |
| 元信息行 | 文档标题下 `> 状态 ｜ 版本 v a.b.c ｜ 日期 ｜ 维护者` 行，版本唯一落点 |
| Terminology section | 文档内以表格定义正文关键词的章节（术语｜定义），本规范强制要求 |
| 正本 | 某规则/schema 的唯一权威出处；其他文档只链接引用，不复制改写 |
| 图示化 | 用 mermaid 图块（sequence/state/flowchart）表达流程、状态、架构类信息 |
| skill | 带 SKILL.md 入口的能力/规范单元，目录名即 skill 名 |

## 0. 适用范围（全局）

本标准覆盖**仓库内全部文档**，包括规范与运行产物：

| 范围 | 适用方式 |
|---|---|
| `doc/skills/**`（规范 / skill 文档） | 全量适用（§1 结构 + §2 模板 + §4 版本管理） |
| `doc/specs/**`（spec 运行产物：README、调研/风险文档、模板产物等） | 全量适用；board 的任务/消息文件 **schema 以 `team-board/board-protocol.md` 正本优先**，正文部分适用本标准 |
| 仓库根及其他 `.md`（README、AGENTS.md 等） | 适用 |
| `.github/agents/*.agent.md` | frontmatter/tools 遵循宿主格式约定；正文文案适用 |
| 会话输出（chat） | 适用 §3 |

冲突裁决：格式/schema 正本（board-protocol、agent frontmatter 规范）> 本标准。

## 1. 文档统一规范

1. **结构**：首行 `# 标题`；紧接元信息行 `> 状态：… ｜ 日期：YYYY-MM-DD ｜ 维护者：…`（规范类文档必带）；正文章节编号（`## 1.` / `### 1.1`），层级不超过 4 级。
2. **命名**：文件名 kebab-case 英文（如 `board-protocol.md`）；目录名即 skill 名；标题可用中文。
3. **表格优先**：对比、清单、字段说明一律表格化；≥3 项的并列信息不用散文罗列。
4. **图**：流程/时序/状态一律 mermaid（sequenceDiagram / stateDiagram / flowchart），不新增 ASCII art 图；一图一主题。
5. **引用**：文件用仓库相对路径；代码位置用 `file:line`；同一规则只在一处写正本，其余位置链接引用（single source of truth）。
6. **排版**：中英文/数字之间加空格；中文语境用全角标点；文件名、命令、commit hash、字段名用反引号。
7. **篇幅**：单文档单主题；超过 ~300 行考虑拆分或收进 template。
8. **Terminology section（必备）**：每份文档必须含 `## Terminology` 章节（置于元信息行/适用范围之后、正文之前），以表格定义正文使用的关键词：`| 术语 | 定义 |`；同一术语全仓库定义一致，已有正本术语以正本为准，不得就地另造。

## 2. template/ 模板目录

| 模板 | 用途 |
|---|---|
| [template/skill-template.md](template/skill-template.md) | 新建 skill 的 SKILL.md 骨架（符合 `doc/skills/AGENTS.md` 规则） |
| [template/doc-template.md](template/doc-template.md) | 通用规范 / 设计 / 需求文档 |
| [template/notes-template.md](template/notes-template.md) | 运行观测记录（POC / 实验轮次） |
| [template/readme-template.md](template/readme-template.md) | 目录索引 / 入口文档 |

使用方式：复制对应模板 → 按占位符填写 → 按 `AGENTS.md` 规则 6 更新索引（清单 + requirements §8 / design §2 树）。

**分工边界**：本目录只放**文档类**模板；board 运行产物与 team 运行文案模板（task / message / state / view.html / lead-subagent-io / lead-spec-context / worker-reply）正本在 `doc/skills/team-board/template/`，两处不重复。本规范位于 `doc/rule/doc-rule/`（规则类，不在 doc/skills 下）。

## 3. 会话信息展示规范（agent chat 输出，必须遵守）

1. **图示优先（核心）**：流程、时序、状态、架构、依赖类信息在会话中输出 **mermaid 图块**（VS Code Chat / Kilo 均原生渲染），文字只做图外 1~3 行补充；能画图不列表、能列表不散文。
2. **图型选择**：

   | 信息类型 | 形式 |
   |---|---|
   | 多方交互 / 调用时序 | `sequenceDiagram` |
   | 状态流转 | `stateDiagram-v2` |
   | 结构 / 层级 / 依赖 | `flowchart LR/TD` |
   | 对比 / 字段 / 清单 | 表格（不强行画图） |
   | 进度排期 / 占比 | 简表优先，`gantt` / `pie` 慎用 |

3. **图幅**：单图 5~15 个节点，节点标签 ≤10 字；超出拆多张小图，一图一主题。
4. **结论先行**：第一行给结果或当前状态，图表细节在后。
5. **进度简报固定格式**：`[task-NNN <状态>] <一句话>`，状态 ∈ assigned / claimed / done / failed / ask / merged。
6. **汇总结构**：结果表 → 证据引用（消息 id / commit hash / 文件路径）→ 待决策项或下一步。
7. **决策请求**：给选项 + 推荐项 + 一句理由，不问开放式问题。
8. **错误报告**：现象 / 根因 / 修复动作各一行。
9. **禁止**：空洞客套与感叹（「好的！」「完成了！」）、大段粘贴文件内容、无信息量的过程叙述。
10. **粒度**：一次回复聚焦一个主题；长回复必须有小标题或表格分节。

## 4. 版本管理

1. **元信息行（版本唯一落点）**：规范类文档标题下必带 `> 状态：<draft|active|deprecated> v<a.b.c> ｜ 日期：YYYY-MM-DD ｜ 维护者：<agent/用户>`。**不设变更记录章节**，演进历史由 git log 承载。
2. **版本号语义（a.b.c）**：a = 不兼容变更（删除/修改规则、schema 字段变更、流程重排）；b = 向后兼容增补（新增章节/规则）；c = 措辞澄清、勘误、排版微调。
3. **联动一致**：协议正本升版时，同步更新引用其版本号的位置（design 标题、速查表等）；`chat-team/NOTES.md` 每轮运行记录注明该轮所用协议版本。
4. **git 维度**：升版单独 commit（前缀 `docs(<skill>):`）；spec 运行产物在 spec 结束时整体 commit，以该 commit 为产物版本锚点；board 消息历史 append-only（协议正本优先，不做版本回溯修改）。
5. **废弃**：deprecated 文档改元信息行状态并在文首标注替代者，不直接删除；删除仅限无引用价值且经用户确认。

## 5. 执行与约束

- TeamLead / worker 的 chat 输出遵守 §3；产出 `.md` 文档遵守 §0 适用范围 + §1 结构 + §4 版本管理 + 对应模板。
- 三个 agent 定义（`.github/agents/*.agent.md`）的硬规则已引用本技能。
- 展示规范违反情况记入 `doc/skills/chat-team/NOTES.md` 观测（与协议违反同表）。
