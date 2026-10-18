# sdd-impl-guide context

> 最后更新：2026-10-18T12:10:00Z ｜ 维护者：TeamLead

## 术语

| 术语 | 定义 |
|---|---|
| Contract | API定义 / Interface / Abstract class / Model / test-case 这类切面契约（用户原话，2026-10-18T11:43Z chat）；草案中一律替代「契约」 |
| SDD spec | spec-driven-dev 产出给定 scope 的五 tab 可视化文档（概要/需求/接口契约/测试/架构） |
| draft→test→accept | 本 spec 任务段结构：task-001/002（draft）→ task-003（manual test）→ gate③ → task-004（accept：修正+安装+README） |
| gate③ | task-003 后的人工批准点：用户审定测试结论后方可进入 accept 段；批准兼作 task-004 写 `doc/skills/**` 的例外授权依据 |
| manual task | `assignee: user` 的任务；完成仅认用户明确指令（本 spec task-003 为「完成-003」），协议 v0.5.0「manual task 增量」 |
| 重做增量 | 重做轮次追加在任务描述内的修订要求（task-001/002 第二轮，依据 msg-0019） |

## 用户原始需求

- （2026-10-18T10:05Z chat）「开个新的change spec，目标范围是spec-driven-dev 下的一个子项，目标是，如果给定一个SDD spec，如何分步的实现: 1. 要求把契约具体实现分离，可以放到不同的包/路时则必须分开 2. 契约实现内需要有和SDD spec一致的version 3. 契约应当能指导实现，应当以注释或其他方式提出主要流程」
- （2026-10-18T11:43Z chat）「Contract 替代'契约'，指 API定义/Interface/Abstract class/Model/test-case这类切面契约」
- （2026-10-18T12:08Z chat）「reload context-template，重写现在的spec context / 重做task01/02，/ 而且个流程图很奇怪」

## 关键约束

- gate③ 未批准：task-004 冻结（安装 `doc/skills/spec-driven-dev/**` 须以 gate③ 批准为授权依据）
- task-003 完成仅认用户明确指令「完成-003」（protocol v0.5.0）；反馈/修正意见只代录不流转
- 本 spec 对 `doc/skills/**` 的历次维护写入均以用户当次明确指令为据：msg-0002（view.html 模板同步）、msg-0004（目录重构）、msg-0017（protocol v0.5.0）、msg-0018（protocol v0.6.0 + context 制度）
- 待用户决策：protocol.md「context.md」节内容分类描述与用户修改后的 context-template.md（术语/用户原始需求/关键约束/决议记录）不一致，是否同步正本未定（TeamLead 未擅改）

## 决议记录

| 日期 | 决议 | 依据 |
|---|---|---|
| 2026-10-18 | Q1 独立 impl-guide.md + SKILL.md 索引；Q2 安装纳入本 spec（task-004 例外授权）；Q4 契约头版本必须、实现侧「应尽可能」 | msg-0001 / msg-0005 |
| 2026-10-18 | 任务结构调整为 draft → test → accept（task-001~004） | msg-0005 |
| 2026-10-18 | task-003 后新增 manual gate③（兼 task-004 安装例外授权依据） | msg-0006 |
| 2026-10-18 | gate① 批准（「修改03为manual，然后开始task」）；task-003 改 manual（assignee: user） | msg-0007 |
| 2026-10-18 | 用户叫停（「别」）→ 解除限 test 段；task-003 重置（open，结果段清空，原代录存档） | msg-0015 / msg-0016 |
| 2026-10-18 | manual task 完成须明确指令「完成-003」；protocol v0.5.0 | msg-0017 |
| 2026-10-18 | context.md 制度建立（protocol v0.6.0） | msg-0018 |
| 2026-10-18 | Contract 术语由「存档待定」转**正式需求**，纳入重做增量 | msg-0019 |
| 2026-10-18 | **重做 task-001/002**（done→open 状态机外纠错）：Contract 术语 + 切面契约定义联动 + 流程图按 doc-viz 重绘；impl-guide → draft v0.2.0 | msg-0019 |
| 2026-10-18 | context-template 经用户修改，context.md 按新模板重写（本文件） | msg-0019 |
