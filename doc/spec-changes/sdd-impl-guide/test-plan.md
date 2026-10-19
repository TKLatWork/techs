# sdd-impl-guide 实操测试计划（test/ 目录干跑）

> 状态：draft v0.2.0 ｜ 日期：2026-10-18 ｜ 维护者：TeamLead（用户指令）

## Terminology

| 术语 | 定义 |
|---|---|
| 干跑测试 | 在仓库根 `test/` 目录下按 impl-guide（v0.10.0）四段工作流做一次端到端实操验证 |
| 测试 scope | **todos-service**——纯 Java 待办事项服务（管理和操作待办事项）；用户 2026-10-18T15:50Z 指定 |

## 目标

1. 验证 impl-guide 四段工作流（Contract imple → Contract review → Full impl → Final review）可实操落地
2. 为 task-003（manual 测试）提供干跑证据；通过后进入 gate③

## 前置条件

- [x] **scope**：todos-service（纯 Java，管理和操作待办事项）——用户指定（15:50Z）
- [x] **spec-doc**：`doc/spec-doc/todos-service.html` **v1.0.0** 已产出（五 tab：概要/需求/对外接口 · 契约/测试/实现设计 · 架构；Contract 来源 = TodosService 接口 6 方法 + Todo/Status Model + TodoError/TodoException + T1~T12 test-case）
- [x] **技术栈**：纯 Java；本机 OpenJDK 24（javac 24.0.2 已验证）；零依赖优先（javac + java 直跑），JUnit 5 可选
- [ ] **执行者**：待定——用户手动，或分派 worker 执行段①/③（两次 manual approve 始终归用户）
- [ ] `test/todos-service/` 目录于测试执行时创建（contract 与 impl 分离子路径，R1）

## 步骤（对应 impl-guide 四段）

| 段 | 动作 | 落点 |
|---|---|---|
| ① Contract imple | 通读 `doc/spec-doc/todos-service.html`、记录版本基线 v1.0.0；提取 Contract（TodosService 接口 / Todo·Status Model / TodoError·TodoException / T1~T12 test-case）；Contract 与 Impl **隔离落位**（R1：`test/todos-service/contract/` vs `test/todos-service/impl/`）；Contract 带 **version meta** 与 spec-doc 一致（R2：v1.0.0）；**公共方法主要流程注释**（R3） | `test/todos-service/contract/` |
| ② Contract review | 自动验收（仅两类：测试可测性核查 + spec/contract 对照——方法/模型/错误/用例映射、版本一致、隔离、注释齐备）→ 核查报告 → **manual approve（用户）** | 核查报告 + 批准记录 |
| ③ Full impl | 按 Contract 分步实现 InMemoryTodosService + 测试（T1~T12）；发现 Contract 问题回报回① | `test/todos-service/impl/`、`test/todos-service/tests/` |
| ④ Final review | 自动验收（测试全量跑 + spec/contract 对照）→ 一致性核查报告 → **manual approve（用户）**；通过后报告与两条批准记录存档 | 核查报告 + 批准记录 |

## 验收要点

- [ ] 起点为 spec-doc（`doc/spec-doc/`），非 change 草稿
- [ ] Contract 与 Impl 分离在不同子路径（R1）
- [ ] Contract version meta 与 spec-doc 元信息行 `v a.b.c` 一致（R2）
- [ ] 公共方法有主要流程注释（R3）
- [ ] 两个 review 段 = 自动验收（仅测试/对照两类）→ manual approve，均有可追溯批准记录

## 与 board 的关系

- 本计划 = task-003 干跑检验的延伸（task-003 保持 claimed，完成仅认「完成-003」）
- 干跑通过 + 「完成-003」→ gate③（测试结论批准，兼 task-004 安装例外授权）→ task-004（修正核销 + 安装 + spec README）
