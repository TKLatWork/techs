---
id: task-002
title: 草案 spec-driven-dev SKILL.md 修订版 draft/SKILL.md
status: done
assignee: team-worker-code
created_by: TeamLead
worktree: null
branch: null
ts: 2026-10-18T11:17:00Z
---

## 任务描述

spec 根：`E:\code\project\techs\doc\spec-changes\sdd-impl-guide`（board 位于其下）。仓库根：`E:\code\project\techs`。
先读：协议正本 `doc/skills/team-core/protocol.md`、回帖模板 `doc/skills/team-core/template/worker-reply.md`、文档规范 `doc/rule/doc-format/SKILL.md`。

**前置**：task-001 已 done。**输入**：读 `draft/impl-guide.md`（task-001 产物）+ 现行 `doc/skills/spec-driven-dev/SKILL.md`（只读参照，禁改）。

**唯一授权业务输出**：`draft/SKILL.md`（spec-driven-dev SKILL.md 的完整修订版，供 task-004 直接安装覆盖）。修订要求（仅做以下增补，无关内容保持原样，不加未明确要求的内容）：
1. frontmatter `description` 增补触发条件：按 SDD spec 分步实现模块/契约先行实现时亦使用
2. Terminology 表增加 `impl-guide`（分步实现指南）条目
3. 「工作流」节后新增「分步实现」小节：一句话概述契约先行 + R1（契约/实现分离）/R2（版本一致）/R3（契约注释呈现主流程），细节指向 `impl-guide.md`（安装后 skill 内相对路径）
4. 元信息行：v1.0.0 → v1.1.0，日期 2026-10-18
5. 「依赖」节按需补 `impl-guide.md` 为 skill 内资源（相对路径），不新增跨 skill 依赖

**board 义务**：同 task-001（扫描 messages/ 取全局最大编号 +1，claim → `## 结果` → result；status open→claimed→done）。除 `draft/SKILL.md` 与 `board/**` 外零改动；禁写 `doc/skills/**`；禁 git commit。

**重做增量（第二轮，用户指令 2026-10-18T12:08Z，依据 msg-0019）**：
1. 输入改为**重做后**的 `draft/impl-guide.md`（v0.2.0，task-001 第二轮产物）——须待 task-001 第二轮 done 后开工。
2. 术语同步：description / Terminology / 「分步实现」小节 / 依赖节中「契约」措辞一律用 **Contract**（切面契约定义：API定义/Interface/Abstract class/Model/test-case），与 impl-guide v0.2.0 的 Terminology 一致。
3. 其余第一轮要求（1~5 条目增补、无关内容一字不动）不变；`draft/SKILL.md` 全量覆盖产出。

**重做增量（第三轮，用户指令 2026-10-18T13:21Z「重做task01/02」，依据 msg-0031）**：
1. 输入改为**第三轮重做后**的 `draft/impl-guide.md`（**v0.3.0**，task-001 第三轮产物）——须待 task-001 第三轮 done 后开工。
2. 「分步实现」小节同步五段主干措辞：`spec（起点=spec-doc，source-of-truth）→ Contract imple → Contract review → Full impl → Final review`；两个 review 段 = 自动验收（仅测试 / spec-contract 对照）→ manual approve（人工批准）——一句话概述级别，细节仍指向 `impl-guide.md`。
3. Terminology / description 与 impl-guide v0.3.0 的 Terminology 一致（spec-doc 起点、五段结构如涉术语则同步）；其余第二轮要求（Contract 措辞、tab 专名例外）不变。
4. `draft/SKILL.md` 全量覆盖产出；仍仅限条目增补，无关内容一字不动。

## 验收标准

- draft/SKILL.md 与现行版 diff 仅限上述 1~5 条目 + 重做增量第 2 条；指南引用为 skill 内相对路径 `impl-guide.md`
- 元信息行 v1.1.0；符合 doc-format（frontmatter/Terminology/元信息行）
- 【第二轮】全文「契约」措辞已按 Contract 术语统一（与 impl-guide v0.2.0 Terminology 一致）
- 【第三轮】「分步实现」小节为五段主干 + review=自动验收两类+manual approve 的概述，与 impl-guide v0.3.0 一致；无旧六步措辞残留
- board 契约齐全：claim/result 消息合规、status 流转正确、白名单外零改动

## 结果
<!-- 第三轮重做：第二轮结果存档于 msg-0025（task.result）；重置依据 msg-0031（2026-10-18T13:22Z） -->

**摘要**（第三轮）：以现行 `doc/skills/spec-driven-dev/SKILL.md` 为基线全量覆盖产出 `draft/SKILL.md`。完成第一/二轮条目 1~5 增补：①description 增补触发条件（按 SDD spec 分步实现模块/Contract 先行实现时亦使用）②Terminology 增加 `impl-guide` 条目（同步五段主干 + spec-doc 起点，Contract 措辞与 impl-guide v0.3.0 一致）③「工作流」节后新增「分步实现」小节——一句话概述五段主干 `spec（起点=doc/spec-doc/ 下 spec-doc，source-of-truth）→ Contract imple（R1 Contract/实现分离、R2 版本一致、R3 Contract 注释呈现主流程）→ Contract review → Full impl → Final review`，两个 review 段=自动验收（仅限测试、spec/contract 对照两类）→ manual approve（人工明确批准），细节指向 skill 内相对路径 `impl-guide.md`，无旧六步措辞残留 ④元信息行 v1.0.0 → v1.1.0（日期 2026-10-18）⑤「依赖」节补 `impl-guide.md` 为 skill 内资源（相对路径），无新增跨 skill 依赖。tab 专名（概要/需求/接口契约/测试/架构）例外保持原文。`git diff --no-index` 自核：diff 仅限上述条目，无关内容一字不动。

**产物/改动清单**：
- `doc/spec-changes/sdd-impl-guide/draft/SKILL.md`（修改，全量覆盖）— spec-driven-dev SKILL.md 修订版 v1.1.0（供 task-004 直接安装覆盖）

**协议自检**：无（除 `draft/SKILL.md` 与 `board/**` 外零改动；未写 `doc/skills/**`；未 git commit；消息 append-only）


