---
id: task-001
title: 草案 SDD spec 分步实现指南 draft/impl-guide.md
status: done
assignee: team-worker-code
created_by: TeamLead
worktree: null
branch: null
ts: 2026-10-18T11:17:00Z
---

## 任务描述

spec 根：`E:\code\project\techs\doc\spec-changes\sdd-impl-guide`（board 位于其下）。仓库根：`E:\code\project\techs`。
先读：协议正本 `doc/skills/team-core/protocol.md`、回帖模板 `doc/skills/team-core/template/worker-reply.md`、文档规范 `doc/rule/doc-format/SKILL.md`、可视化规范 `doc/rule/doc-viz/SKILL.md`。
只读参考：`doc/skills/spec-driven-dev/SKILL.md`（本次被修订的 skill）与 `doc/skills/spec-driven-dev/template/spec-driven-dev-template.html`（五 tab 模板，重点「对外接口 / 契约」「实现设计 / 架构」两 tab 的结构）。

**背景**：spec-driven-dev 产出给定 scope 的 SDD spec（五 tab 可视化文档：概要/需求/接口契约/测试/架构）。本任务为其子项起草「给定一个 SDD spec，如何分步实现」的指南——契约先行（contract-first）。

**需求（用户原话）**：
- R1：要求把契约与具体实现分离，可以放到不同的包/路径时**必须**分开
- R2：契约实现内需要有和 SDD spec 一致的 version
- R3：契约应当能指导实现，应当以注释或其他方式提出主要流程

**已定决议**（gate① 批准，msg-0005/0007）：产物形态 = 独立指南文件（SKILL.md 以索引引用）；版本一致性深度 = 契约文件头版本为**必须**条款，实现侧版本常量/包元数据为「应尽可能」条款。

**唯一授权业务输出**：`draft/impl-guide.md`（spec 根内创建）。内容要求：
1. 遵循 doc-format：元信息行（draft v0.1.0）、`## Terminology`（契约、实现、版本锚定、SDD spec 等）
2. 分步实现工作流主干（建议步骤）：从 spec「对外接口/契约」tab 提取契约 → 契约落位（R1：分离规则 + 「可否分包/分路径」判定标准 + 无法分离时的处理）→ 版本锚定（R2：契约头版本标注格式，与 spec 元信息行 v a.b.c 对齐及升版联动规则）→ 契约内主流程呈现（R3：注释规范——接口签名 + 主要流程步骤注释/伪代码，步骤与 spec「实现设计/架构」tab 对应）→ 按契约分步实现（每步对照契约条目）→ 一致性验收（契约/实现/spec 三方版本与行为核对）
3. 每步给出可执行规则 + 示例（示例用伪代码或任意语言，不绑定具体技术栈）
4. 文末附「验收清单」（供 test 段 task-003 逐项核对）
5. 图示遵循 doc-viz（mermaid 优先，信息形态决定表达形式）

**board 义务**：执行前扫描 `board/messages/` 取全局最大编号 +1，写 task.claim 消息并将本文件 status → claimed；完成后将摘要与产物路径写入 `## 结果`，写 task.result 消息，status → done。除 `draft/impl-guide.md` 与 `board/**` 外零改动；禁写 `doc/skills/**`；禁 git commit。

**重做增量（第二轮，用户指令 2026-10-18T12:08Z，依据 msg-0019）**——在第一轮框架（六步工作流 / J1~J4 判定 / 版本头条款 / 注释模板 / 验收清单）基础上修正，非推倒重写；直接覆盖 `draft/impl-guide.md`：

1. **术语统一**：全文（正文/图表/示例/验收清单）「契约」一律以 **Contract** 替代；Terminology 条目采用用户原话定义——Contract 指 **API定义 / Interface / Abstract class / Model / test-case 这类切面契约**。仅在需要对照说明处可保留「契约」字样（如术语沿革注）。
2. **定义联动**：R1 落位规则须覆盖各类 Contract（Interface / Abstract class / Model(schema) / test-case / API 定义）的分离形态与落点；步骤① 条目清单的「类别」列与切面契约分类对齐；R3 注释规范说明对各类 Contract 的适用方式（如 test-case 的主流程 = 场景步骤描述）。
3. **流程图重绘**（用户反馈「流程图很奇怪」）：工作流总览 mermaid 严格按 `doc/rule/doc-viz/SKILL.md` §2「流程回路」范式重绘——阶段用 subgraph、阶段衔接用显式衔接点，**避免 subgraph 级边（C1-->C2）与节点级虚线回退边（S6-.->C1）混用**（渲染异常来源）；回退路径用独立衔接节点/边表达；每图单一视角，颜色 + 注脚。
4. **版本**：元信息行 draft v0.1.0 → **v0.2.0**。

**重做增量（第三轮，用户指令 2026-10-18T13:21Z「重做task01/02」，依据 msg-0031；输入 = task-003 `## 结果` 修正项 1~4，原文见该文件，只读禁改）**——在第二轮 v0.2.0 基础上修正，直接覆盖 `draft/impl-guide.md`：

1. **工作流起点 = spec-doc**（修正项 1）：总览图与步骤输入起点显式为 `doc/spec-doc/` 下的 spec-doc（项目当前文档 source-of-truth 的 SDD spec，非进行中 change 草稿）；R2 版本锚定基准 = spec-doc 元信息行 v a.b.c；Terminology 的 SDD spec 条目补充与 spec-doc 目录的关系。
2. **主干改五段**（修正项 2）：`spec → Contract imple → Contract review → Full impl → Final review`——spec（通读 spec-doc 定实现范围）；Contract imple（旧①~④归并：提取条目、R1 落位分离、R2 版本锚定、R3 主流程注释）；Contract review（实现前评审）；Full impl（旧⑤ 按条目分步实现）；Final review（旧⑥ 三方一致性终验）。正文章节与验收清单分组按五段重组；R1/R2/R3 条款内容保留、挂接对应段。
3. **review 默认 manual approve**（修正项 3）：两个 review 段通过 = 人工明确批准；agent 只准备评审材料，不得自行判定通过；总览图 review 衔接点按 gate 画法标注「manual approve（人工）」；正文定义批准方式与可追溯记录落点；验收清单加「存在人工明确批准记录」硬性条目。
4. **review = 验收，自动化限两类**（修正项 4）：每个 review 段 = 自动验收 → manual approve；自动验收仅限 ①测试（test-case 类 Contract 运行核查 / 实现行为测试）②spec/contract 对照（条目映射、版本一致、R1 落位合规、R3 注释齐备的机械核查），产出核查报告；两类之外（语义正确性、设计合理性、取舍）不得自动验收；自动部分不通过直接回上一段，无需人工介入。
5. **总览图重绘**：五段 + 两个 review 衔接点（manual approve 画法），doc-viz「流程回路」范式（显式衔接点、禁 subgraph 级边与节点级边混用、颜色 + 注脚）。
6. **版本**：draft v0.2.0 → **v0.3.0**。

## 验收标准

- impl-guide.md 含元信息行、Terminology、分步工作流、R1/R2/R3 逐条落地条款、验收清单；≥1 张 mermaid 图
- R1 有「可分包/分路径则必须分开」的强制条款与判定标准，及无法分离时的替代要求
- R2 明确契约版本标注格式、与 spec 元信息行 v a.b.c 的一致性规则与升版联动
- R3 给出契约注释模板/示例（接口 + 主要流程步骤）
- board 契约齐全：claim/result 消息合规（schema/编号扫描）、status 流转 open→claimed→done、白名单外零改动
- 【第二轮】全文无「契约」残留（对照说明除外）；Terminology 含 Contract 切面契约定义；R1/R3/示例/验收清单已按切面契约分类联动
- 【第二轮】工作流总览图符合 doc-viz「流程回路」范式：无 subgraph 级边与节点级边混用，回退路径经显式衔接点；元信息行 v0.2.0
- 【第三轮】主干为五段 spec→Contract imple→Contract review→Full impl→Final review，起点 = spec-doc（source-of-truth）；无旧六步结构残留
- 【第三轮】两个 review 段 = 自动验收（仅测试 / spec-contract 对照两类，出核查报告）→ manual approve（人工批准 + 记录落点）；两类之外无自动验收表述
- 【第三轮】总览图五段 + review 衔接点按 manual approve 画法；元信息行 v0.3.0

## 结果
<!-- 第三轮重做：第二轮结果存档于 msg-0022（task.result）；重置依据 msg-0031（2026-10-18T13:22Z） -->
<!-- 第二轮重做：第一轮结果存档于 msg-0019 第 4 条（2026-10-18T12:10Z 重置清空） -->

**摘要**（第三轮）：在第二轮 v0.2.0 基础上按「重做增量（第三轮）」6 条（输入 = task-003 `## 结果` 修正项 1~4）完成修订并覆盖 `draft/impl-guide.md`（draft v0.2.0 → v0.3.0）：①工作流起点显式为 spec-doc（`doc/spec-doc/` source-of-truth 的 SDD spec，change 草稿不得作起点），R2 锚定基准 = spec-doc 元信息行 `v a.b.c`，Terminology 新增 spec-doc/review 段/自动验收/核查报告/manual approve 条目并在 SDD spec 条目补充与 spec-doc 目录关系；②主干六步改五段 spec→Contract imple→Contract review→Full impl→Final review，旧①~④归并为段②（2.1~2.4），旧⑤/⑥改挂段④/段⑤，正文章节与验收清单按五段重组（新增 S/CR/F/FR 分组），R1/R2/R3 条款内容保留挂接段②；③④两个 review 段 = 自动验收（仅限测试 / spec-contract 对照两类，出核查报告，两类之外不得自动验收，自动不通过直接回上一段无需人工）→ manual approve（agent 只备材料不得自行判定通过，批准 = 人工明确表述，可追溯记录落点如 `contract/REVIEWS.md`/board info 消息，验收清单加「存在人工明确批准记录」硬性条目 CR6/FR4）；⑤总览图按 doc-viz §2「流程回路」范式重绘：五段 subgraph + 两个红色 gate 画法衔接点（标注 manual approve（人工）），全部边节点级、无 subgraph 级边混用，回退虚线挂衔接点/核查节点，颜色 + 注脚；⑥元信息行 v0.3.0。全文「契约」仅存 tab 专名/用户原话引文/术语沿革注三类允许处，无六步结构残留。

**产物/改动清单**：
- `doc/spec-changes/sdd-impl-guide/draft/impl-guide.md`（修改，覆盖）— SDD spec 分步实现指南 draft v0.3.0（第三轮重做）
- `doc/spec-changes/sdd-impl-guide/board/messages/msg-0033.md`（新建）— task.claim
- `doc/spec-changes/sdd-impl-guide/board/messages/msg-0034.md`（新建）— task.result
- `doc/spec-changes/sdd-impl-guide/board/tasks/task-001.md`（修改）— status open→claimed→done + 本节

**协议自检**：无越权。除 `draft/impl-guide.md` 与 `board/**` 外零改动；task-003.md 未改（只读）；未写 `doc/skills/**`；未 git commit；消息 append-only，编号写前重扫（msg-0033/0034）。

