---
id: task-003
title: 【manual】用户检验两份草案并产出测试结论
status: claimed
assignee: user
created_by: TeamLead
worktree: null
branch: null
ts: 2026-10-18T11:17:00Z
---

## 任务描述

**manual 任务**（用户指令 2026-10-18T11:16Z「修改03为manual」；task schema assignee 枚举例外，授权记录见 msg-0007）。执行者：**用户**；TeamLead 仅协助记录与状态流转。

**前置**：task-001、task-002 已 done。**检验对象**：`draft/impl-guide.md`、`draft/SKILL.md`（spec 根下）。

检验要点（建议，用户可自行增删）：
1. R1/R2/R3 逐条覆盖与条款强度：R1「可分包/分路径则必须分开」是否为硬约束且有判定标准；R2 版本一致性是否可机械校验；R3 契约注释是否足以呈现主要流程
2. SKILL.md ↔ impl-guide.md 一致性：引用路径、术语、步骤编号
3. 干跑：任选一个现有 SDD spec（或虚构一个小模块），按指南步骤走一遍契约先行流程，记录卡点
4. 对照 impl-guide.md 自带「验收清单」逐项核查

**产物**：测试结论（通过/不通过）+ 修正项清单（可为空），写入本文件 `## 结果` 段——用户自写，或口述由 TeamLead 代录（代录须注明「TeamLead 代录用户结论」）。status 流转由 TeamLead 代操作；**完成必须由用户明确指令「完成-003」触发**（protocol v0.5.0「manual task 增量」，2026-10-18 用户指令）——反馈/修正意见/含糊答复不构成完成，收到明确指令前不流转 claimed → done、不进入 gate③。

**后续**：本任务 done 后进入 **gate③**——用户对测试结论明确批准后方可分派 task-004；gate③ 批准同时构成 task-004 写 `doc/skills/**` 白名单的例外授权依据（msg-0006 第 1 条）。

## 验收标准

- `## 结果` 含测试结论与修正项清单
- gate③ 有用户明确批准记录（info 消息，reply_to 指向测试结论相关消息）

## 结果
<!-- 2026-10-18T11:47Z 依用户指令重置清空；原代录内容存档于 msg-0016 -->
<!-- 第二轮 manual 测试进行中；以下为 TeamLead 代录用户反馈（protocol v0.5.0：反馈只代录，完成仅认「完成-003」） -->

### 修正项清单（第二轮）

1. 【工作流起点】（TeamLead 代录用户指令，chat 2026-10-18T13:12Z，原话「impl-guide的工作流，起点应该是一个spec-doc」）：impl-guide 分步实现工作流的**起点 = 一个 spec-doc**——即 `doc/spec-doc/` 下作为项目当前文档 source-of-truth 的 SDD spec，而非进行中的 change 草稿。联动落点（task-004 核销时核对，解释如有偏差以用户为准）：
   - 工作流总览图：起点节点显式为「spec-doc（source-of-truth）」
   - 步骤① 输入：改为「给定 scope 对应的 spec-doc」；来源位置标注基于 spec-doc
   - R2 版本锚定：Contract 头版本对齐的基准 = spec-doc 元信息行 v a.b.c
   - Terminology：SDD spec 条目补充与 spec-doc 目录的关系（`doc/spec-changes/` 完成迁入后的正式文档）
2. 【工作流结构】（TeamLead 代录用户指令，chat 2026-10-18T13:16Z，原话「流程改为 spec>Contract imple>Contract review>Full impl>Final review」）：impl-guide 主干由现行六步（提取→落位→版本锚定→主流程呈现→分步实现→一致性验收）改为**五段**（解释如有偏差以用户为准）：
   - **spec**：起点 = 给定 scope 的 spec-doc（衔接修正项 1），通读 spec 确定实现范围
   - **Contract imple**：产出 Contract 工件——旧①~④ 归并（提取条目、R1 落位分离、R2 版本锚定、R3 主流程注释）
   - **Contract review**：实现前的显式评审段——Contract 对照 spec 核查（条目完整性/版本一致/主流程覆盖/落位合规），通过方可进入 Full impl，不通过回 Contract imple
   - **Full impl**：旧⑤——按 Contract 条目分步完整实现
   - **Final review**：旧⑥——Contract/实现/spec 三方一致性终验 + 报告
   - 联动：工作流总览图按五段 + 两个 review 衔接点重绘（doc-viz 流程回路范式）；正文章节结构、验收清单分组随五段重组；R1/R2/R3 条款内容不变、挂接到对应段
3. 【review 默认 manual approve】（TeamLead 代录用户指令，chat 2026-10-18T13:16:32Z，原话「review 部分默认都是manual approve」）：修正项 2 五段中的两个 review 段（**Contract review**、**Final review**）默认为 **manual approve**——人工批准节点（解释如有偏差以用户为准）：
   - review 的通过 = 用户/人工的明确批准；agent 只准备评审材料（对照核查清单、一致性报告），**不得自行判定通过**
   - 总览图：两个 review 衔接点标注「manual approve（人工）」，与 gate 画法一致
   - 正文两段：定义批准方式与批准记录落点（可追溯，如记录于实现仓库/spec 附注）
   - 验收清单：review 段各增加「存在人工明确批准记录」硬性条目
   - 与本协议 manual task 规则（v0.5.0）同精神：完成/通过仅认人工明确指令
4. 【review = 验收，自动化范围限定】（TeamLead 代录用户指令，chat 2026-10-18T13:19:34Z，原话「review既是验收，可以自动的验收只包括，测试或spec/contract对照」）：细化修正项 3——review 段的性质即**验收**，其中**可自动化的验收仅限两类**（解释如有偏差以用户为准）：
   - **测试**：运行 test-case 类 Contract 对应的测试（含 Contract review 段对 Contract 自身可测部分的核查、Final review 段对实现的行为测试）
   - **spec/contract 对照**：机械对照核查——条目清单完整性（spec 条目 ↔ Contract 条目一一映射）、版本一致性（spec-doc 元信息行 ↔ Contract 头 ↔ 实现侧）、落位合规（R1 分离形态）、注释齐备（R3 主流程呈现存在性）
   - 两类之外的任何判断（语义正确性、设计合理性、取舍接受）**不得自动验收**，归 manual approve（修正项 3）
   - 结构含义：每个 review 段 = 自动验收（agent 跑测试 + 对照，产出核查报告）→ manual approve（人工基于报告批准）；自动部分不通过则直接回上一段，无需人工介入
   - 联动：验收清单 review 段条目按「自动两类 + 人工批准记录」重组
5. 【总览图结构】（TeamLead 代录用户指令，chat 2026-10-18T14:17Z，原话「直接调整当前工作流的图，分为一个主步骤图，然后下面一个tab分别用flow图解释具体的步骤」）——**已按「直接调整」指令由 TeamLead 当场落地**（铁律 3 例外，授权依据 msg-0038），不再走 task-004 核销：
   - `draft/impl-guide.md` v0.3.0 → **v0.4.0**：「工作流总览」拆分为主步骤图（段级单一视角：五段 + 两个 manual approve 衔接点）+「分段 flow 图」（5 个 `<details>` tab，每段一张 flow 图对应正文规则；markdown 零依赖形态的 tab）
   - 验收清单 G3~G5 已随新结构改写；术语沿革补 v0.4.0 条目
6. 【清除幻想内容】（TeamLead 代录用户指令，chat 2026-10-18T14:23Z，原话「impl-guide 里面有一大堆的幻想内容，清掉」）——**已按指令由 TeamLead 直接落地**（铁律 3 例外，授权依据 msg-0039）：`draft/impl-guide.md` v0.4.0 → **v0.5.0** 全量精简重写，仅保留用户指令与既有规范可直接追溯的条款；清除清单见 msg-0039。`draft/SKILL.md` 同步清除传导措辞（「三方一致性」「条目」）。
7. 【sub-steps flow 收编】（TeamLead 代录用户指令，chat 2026-10-18T15:00Z 原话「分段 flow 图 改为'sub-steps flow'，改成类似Nav的tab显示。然后后面的'段*'那些的合并进去」+ 15:07Z 修订「当我没说，md没有Nav那些。用现在的展开收起就行，除非markdown真支持tabs。但依然需要把下面的内容收编进去」）——**已由 TeamLead 直接落地**（铁律 3 例外，授权依据 msg-0040）：`draft/impl-guide.md` v0.5.0 → **v0.6.0**：
   - 「分段 flow 图」更名 **sub-steps flow**；沿用 `<details>` 展开收起（markdown 无原生 tabs，用户确认；Nav tab 方案撤回）
   - 段①~⑤ 独立章节删除，规则正文收编进对应展开面板（flow 图 + 注脚 + 规则）
   - 交叉引用同步（总览 intro/注脚/验收清单末条）
8. 【验收清单收进折叠】（TeamLead 代录用户指令，chat 2026-10-18T15:12Z，原话「验收清单 应该收进去」）——**已由 TeamLead 直接落地**（铁律 3 例外，同 msg-0038/0039/0040 先例，记录 msg-0041）：`draft/impl-guide.md` v0.6.0 → **v0.6.1**，「## 验收清单」章节改为 `<details>` 折叠面板（summary=验收清单（逐项核对）），条目内容不变；沿革注同步。
9. 【合并段①/② + 移除规则细节】（TeamLead 代录用户指令，chat 2026-10-18T15:19Z，原话「合并步骤 1/2, 移除全部规则细节。只要求按当前的spec实现Contract」）——**已由 TeamLead 直接落地**（铁律 3 例外，同 msg-0038~0041 先例，记录 msg-0042）：`draft/impl-guide.md` v0.6.1 → **v0.7.0**：主干五段改**四段**（原段① spec + 段② Contract imple 合并为 ① Contract imple，起点 spec-doc）；全部面板**规则细节移除**（仅 flow 图 + 注脚）；核心要求显式声明「**按当前的 spec 实现 Contract**」；主步骤图/分段图/验收清单/Terminology（四段条目）段号同步。`draft/SKILL.md` 同步（五段→四段、合并措辞、核心要求）。
10. 【用户补充要求 3 条】（TeamLead 代录用户指令，chat 2026-10-18T15:25Z，原话「加入 1. 要求Contract有version meta，并和spec一致。2. Contract需要保持和Impl保持隔离，比如分开在不同的子包 3. 公共方法应当提供主要的流程注释来引导实现」）——**已由 TeamLead 直接落地**（铁律 3 例外，记录 msg-0043）：`draft/impl-guide.md` v0.7.0 → **v0.8.0**：intro 增加「用户补充要求」3 条原话（分别挂接 R2/R1/R3）；① Contract imple 面板 flow 图 D/E/F 节点同步（隔离如分子包 / version meta 与 spec 一致 / 公共方法主要流程注释）；Terminology「版本锚定」补 version meta；验收清单 R1/R2/R3 三条同步。`draft/SKILL.md`「分步实现」小节同步。
11. 【移除顶部原话块】（TeamLead 代录用户指令，chat 2026-10-18T15:26:43Z，原话「拿掉头顶那两段原话，确保内容在sub-steps对应的段内有就行」）——**已由 TeamLead 直接落地**（铁律 3 例外，记录 msg-0044）：`draft/impl-guide.md` v0.8.0 → **v0.9.0**：intro 的「三条硬约束（用户原话）」与「用户补充要求（原话）」两段移除；内容完整收入 sub-steps flow「① Contract imple」面板 flow 图节点 D/E/F（R1 分离+隔离如分子包 / R2 version meta 与 spec 一致 / R3 公共方法主要流程注释或其他方式引导实现）；intro 保留一句指向（「硬约束 R1/R2/R3 见 sub-steps flow ① 段」）。
12. 【移除验收清单】（TeamLead 代录用户指令，chat 2026-10-18T15:28:29Z，原话「拿掉'验收清单（逐项核对）'」）——**已由 TeamLead 直接落地**（铁律 3 例外，记录 msg-0045）：`draft/impl-guide.md` v0.9.0 → **v0.10.0**：「验收清单（逐项核对）」`<details>` 整节移除；`draft/SKILL.md` 同步去掉两处「验收清单」引用（Terminology impl-guide 条目、依赖节）。


### 测试结论

（待用户最终判定，完成指令「完成-003」；修正项由 task-004 在 gate③ 批准后逐项核销）

