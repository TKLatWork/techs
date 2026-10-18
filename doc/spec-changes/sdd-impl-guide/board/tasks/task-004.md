---
id: task-004
title: 修正收口 + 安装 doc/skills/spec-driven-dev（例外授权）+ spec README
status: open
assignee: team-worker-code
created_by: TeamLead
worktree: null
branch: null
ts: 2026-10-18T11:17:00Z
---

## 任务描述

spec 根：`E:\code\project\techs\doc\spec-changes\sdd-impl-guide`（board 位于其下）。仓库根：`E:\code\project\techs`。
先读：协议正本 `doc/skills/team-core/protocol.md`、回帖模板 `doc/skills/team-core/template/worker-reply.md`、文档规范 `doc/rule/doc-format/SKILL.md`、目录规则 `doc/skills/AGENTS.md`。

**前置**：task-003 已 done **且 gate③ 已获用户明确批准**（board 中存在批准 info 消息）。gate③ 批准即本任务写 `doc/skills/**` 白名单路径的例外授权依据，铁律 3 对该白名单不适用；无 gate③ 批准消息时本任务不得开工（报 ask）。

工作内容：
1. 按 task-003 `## 结果` 的修正项清单修正 `draft/impl-guide.md`、`draft/SKILL.md`（逐项核销）
2. 安装：`draft/impl-guide.md` → `doc/skills/spec-driven-dev/impl-guide.md`；`draft/SKILL.md` → `doc/skills/spec-driven-dev/SKILL.md`（覆盖）；两者元信息行状态 draft → active
3. 索引同步：`doc/skills/AGENTS.md`「当前 skill 清单」spec-driven-dev 行职责描述更新（增补分步实现指南 impl-guide.md）；`doc/skills/team-core/design.md` §2 目录树若列有 spec-driven-dev 内容则同步加 impl-guide.md 行（仅 §2 树，其余禁动）
4. 自检：SKILL.md 对 `impl-guide.md`、`template/spec-driven-dev-template.html` 的引用均为 skill 内相对路径且文件存在；SKILL.md/impl-guide.md 符合 doc-format
5. 产出 spec `README.md`（spec 根下）：spec 目标、产物清单（draft/ 两件 + 安装件 + board）、运行记录摘要（从 board 消息链与 state.json 提炼，含 gate①/③ 与 manual task-003）——phase done 后由 TeamLead 按 protocol v0.4.0 迁入 `doc/spec-doc/`

**授权白名单**：`draft/**`（修正）、`doc/skills/spec-driven-dev/**`、`doc/skills/AGENTS.md`（仅清单行）、`doc/skills/team-core/design.md`（仅 §2 树）、spec `README.md`（新建）、`board/**`。禁止 git commit（主副本提交由 TeamLead/用户决定）。

**board 义务**：扫描 messages/ 取全局最大编号 +1，claim → `## 结果`（含修正项核销对照、安装/索引文件清单）→ result；status open→claimed→done。

## 验收标准

- 修正项全部核销（对照 task-003 清单逐条）；`doc/skills/spec-driven-dev/` 含 SKILL.md（active v1.1.0）+ impl-guide.md（active）+ template/
- 两处索引（AGENTS.md 清单、design.md §2 树）与实际目录一致
- spec README.md 运行记录与 board 实况一致
- board 契约齐全：claim/result 消息合规、status 流转正确、白名单外零改动

## 结果
