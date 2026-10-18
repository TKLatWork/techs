# sdd-impl-guide 实操测试计划（test/ 目录干跑）

> 状态：draft v0.1.0 ｜ 日期：2026-10-18 ｜ 维护者：TeamLead（用户指令）

## Terminology

| 术语 | 定义 |
|---|---|
| 干跑测试 | 在仓库根 `test/` 目录下按 impl-guide（v0.10.0）四段工作流做一次端到端实操验证 |
| 测试 scope | 干跑所选的小模块；其 spec-doc 是工作流起点 |

## 目标

1. 验证 impl-guide 四段工作流（Contract imple → Contract review → Full impl → Final review）可实操落地
2. 为 task-003（manual 测试）提供干跑证据；通过后进入 gate③

## 前置条件（待用户决策的开放项）

- [ ] **scope 选择**：`doc/spec-doc/` 当前为空——需先经 spec-driven-dev 产出一个小 scope 的五 tab spec HTML 并迁入 `doc/spec-doc/<scope>.html`（source-of-truth），或指定既有文档
- [ ] **技术栈**：待定（建议 Node.js——环境已具备 node，零依赖可跑测试）
- [ ] **执行者**：用户手动，或分派 worker 执行段①/③、两次 manual approve 仍归用户
- [ ] `test/<scope>/` 目录于测试执行时创建

## 步骤（对应 impl-guide 四段）

| 段 | 动作 | 落点 |
|---|---|---|
| ① Contract imple | 通读 spec-doc、记录版本基线；提取 Contract（API定义/Interface/Abstract class/Model/test-case）；Contract 与 Impl **隔离落位**（R1：如 `test/<scope>/contract/` vs `test/<scope>/impl/`）；Contract 带 **version meta** 与 spec-doc 一致（R2）；**公共方法主要流程注释**（R3） | `test/<scope>/contract/` |
| ② Contract review | 自动验收（仅两类：测试可测性核查 + spec/contract 对照——条目映射/版本一致/隔离/注释齐备）→ 核查报告 → **manual approve（用户）** | 核查报告 + 批准记录 |
| ③ Full impl | 按 Contract 分步实现 + 测试；发现 Contract 问题回报回①（修订后重新过②） | `test/<scope>/impl/`、`test/<scope>/tests/` |
| ④ Final review | 自动验收（测试 + spec/contract 对照）→ 一致性核查报告 → **manual approve（用户）**；通过后报告与两条批准记录存档 | 核查报告 + 批准记录 |

## 验收要点

- [ ] 起点为 spec-doc（`doc/spec-doc/`），非 change 草稿
- [ ] Contract 与 Impl 分离在不同子路径（R1）
- [ ] Contract version meta 与 spec-doc 元信息行 `v a.b.c` 一致（R2）
- [ ] 公共方法有主要流程注释（R3）
- [ ] 两个 review 段 = 自动验收（仅测试/对照两类）→ manual approve，均有可追溯批准记录

## 与 board 的关系

- 本计划 = task-003 干跑检验的延伸（task-003 保持 claimed，完成仅认「完成-003」）
- 干跑通过 + 「完成-003」→ gate③（测试结论批准，兼 task-004 安装例外授权）→ task-004（修正核销 + 安装 + spec README）
