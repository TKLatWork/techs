---
name: spec-driven-dev
description: 给定一个模块（含子模块）及其 SDD spec，按「模块 × 需求/Contract/实现/验证」二维矩阵渐进交付；每格可独立重试，变更只失效下游锥。过程/状态可视化使用 spec-doc-gen 提供的单一入口 hub 与 JSON 数据源，可按需修改。
---

# spec-driven-dev — 模块二维渐进交付

> 状态：active v2.2.0 ｜ 日期：2026-10-19 ｜ 维护者：Kilo（用户指令）

## Terminology

| 术语 | 定义 |
|---|---|
| 交付模块 | 输入的根模块及其子模块树；矩阵的行 |
| 阶段 | 需求 / Contract / 实现 / 验证；矩阵的列 |
| 格子 | `(module, stage)` 的产物，带 version / inputs / status / retries |
| 下游锥 | 某格变更后必须失效并重算的格子集合 |
| Contract 切割线 | Contract 左侧是需求与范围，右侧是实现；黑盒边界 |
| checkpoint | 格子 done 时的版本 + 验证结果；重试回退点 |
| 瞬态失败 | 同输入、同版本的失败；幂等重试，不升版本 |
| 输入变更 | 需求 / Contract / 模块边界变化；升版本并显式失效下游 |

## 输入

- 一个模块及其子模块树（根 + 子模块，给出路径与边界）
- 该模块的 SDD spec（`spec-doc-gen` 产出，或 `doc/spec-doc/` 中已有）
- 版本与基线 commit
- 输出目录（默认 `doc/spec-changes/<spec>/`）

## 二维矩阵

| | 需求 | Contract | 实现 | 验证 |
|---|---|---|---|---|
| 根模块 | R-root | C-root | I-root | V-root |
| 子模块 A | R-A | C-A | I-A | V-A |
| 子模块 B | R-B | C-B | I-B | V-B |

- **行 = 模块/子模块**，决定变更的影响范围。
- **列 = 需求 → Contract → 实现 → 验证**，决定重算的深度。
- 每格独立记录 `version`、`inputs`、`status`、`retries`；格子之间只允许从左向右依赖。

## 工作流

1. **建模块树**：确定根模块与子模块，给出路径/边界；拆到“可独立验证 + 可独立重试”为止，不按代码量拆。
2. **建矩阵**：每个模块一行、四个阶段一列；所有格子初始 `pending`，记录初始版本与上游 inputs。
3. **需求列**：把 spec 中该模块/子模块的需求落到 R 格；需求变更从这里进入，不允许直接改实现。
4. **Contract 列**：提取 API定义 / Interface / Abstract class / Model / test-case；Contract 与实现分离（R1），带与 spec 一致的 version meta（R2），公共方法提供主要流程注释（R3）。
5. **实现列**：按 Contract 实现；实现失败只重试 I 格，Contract 冻结。
6. **验证列**：测试 + spec/contract 对照 + manual approve；通过后标 `done` 并写 checkpoint。
7. **更新状态**：更新 `delivery/status.json`（矩阵）；更新 `hub/hub.json`（模块树摘要，归 hub skill 维护）；模块文档更新对应 `delivery/spec-doc.json`。team 的 `board/state.json` 只表示执行 attempt，不复制 cell 状态。
8. **变更/重试**：按下方规则失效下游锥，保留上游 checkpoint，并写 retry log；需要 team 执行时发出 WorkOrder。

## 与 team 执行器接口

SDD 决定 cell 的交付状态；team 只执行 attempt。二者通过 WorkOrder / AttemptResult 对接，细则见 `doc/skills/team/core/protocol.md`「SDD ↔ team 接口」。

- **WorkOrder（SDD → team）**：`cell`、`module`、`stage`、`input_versions`、`acceptance`、`output_path`。
- **AttemptResult（team → SDD）**：`task`、`cell`、`attempt`、`output_version`、`commit`、`artifacts`、`verification`。
- team 的 `task.done` 只表示 attempt 成功，**不等于 cell done**；cell 的 `done/invalid` 由本 skill 验收和失效规则决定。
- SDD 标记 cell `invalid` 时，team 取消该 cell 的未完成 task；新版本用新 task/attempt。
- 回滚：本 skill 选 checkpoint，team 执行 worktree/branch 的 revert/重建。

## Contract 段要求

- **R1 分离**：Contract 与实现隔离；可放不同包/路径时必须分开（如 `contract/` 与 `impl/`）。
- **R2 版本锚定**：Contract 带 version meta，并与 spec 元信息行 `v a.b.c` 一致。
- **R3 指导实现**：公共方法提供主要流程注释（或其他等效方式），让 Contract 能引导实现。
- **Contract review**：自动验收仅限测试或 spec/contract 对照；通过后必须 manual approve，批准记录可追溯。
- **黑盒边界**：Contract 是模块对外行为的唯一承诺；实现内部是白盒，只检查边界、依赖方向与测试，不逐行审实现。

## 重试规则

| 变更源 | 失效范围 | 保留 |
|---|---|---|
| 需求 | 本模块 Contract/实现/验证 + 依赖模块的 Contract/实现/验证 | 本模块需求、未受影响模块 |
| Contract | 本模块实现/验证 + 依赖该 Contract 的模块的 Contract/实现/验证 | 需求、未依赖的其他模块 |
| 实现 | 本模块验证 | 需求、Contract、其他模块 |
| 验证 | 本格 | 全部上游 |
| 模块边界 | 受影响的行 | 未受影响的行 |

- **只允许下游失效**：实现不得反推需求；需求变更必须重走 Contract，Contract 变更必须重走实现与验证。
- **瞬态失败**：同输入、同版本幂等重试，不升版本。
- **输入变更**：升版本、显式标记 `invalid`、保留旧 checkpoint；重算从最早失效格开始。
- **retry log**：每次失效记录格子、原因、旧版本 → 新版本、时间；供状态模板展示。

## 状态枚举

| 状态 | 含义 |
|---|---|
| pending | 未开始 |
| active | 进行中 |
| done | 已验收，存在 checkpoint |
| invalid | 上游变更，需重算 |
| blocked | 依赖未满足或等待人工决策 |

## 过程 / 状态可视化

- **单一入口**：`doc/skills/hub/template/hub-template.html`，NavTabs = 交付状态 / 模块树 / 动态模块 tab / Team 执行。
- **SDD 两页 + hub**：状态页读 `delivery/status.json`；模块文档页读 `delivery/spec-doc.json`（可用 `?data=` 指向模块专属 JSON）；hub 读 `hub/hub.json` 并聚合 SDD 与 team。
- **更新方式**：只改 JSON，不改 HTML；`delivery/status.json` 记录矩阵明细，`hub/hub.json` 记录模块树摘要，team board 读 `board/state.json`。
- **模块文档**：`hub.json` 的 `modules[].doc` 可写 `spec-doc-template.html?data=modules/<id>.json`，同一模板服务多个模块。
- **启动**：`npx -y http-server doc\spec-changes\<spec> -p 8788 -c-1` → `http://127.0.0.1:8788/hub/hub-template.html`。
- 本 skill 可更新 SDD 数据；页面修改需同步回 spec-doc-gen / hub 的模板正本。

## 硬规则

1. 一个模块一行；子模块独立行，不允许跨行合并。
2. 只能从左向右推进；不得跳过 Contract 直接实现。
3. 只允许下游失效；实现不得反推需求；Contract 变更必须回到 C 格。
4. 每格 `done` 必须有 checkpoint（版本 + 验证结果）。
5. 变更必须显式失效下游并写 retry log；不得静默改实现。
6. 子模块拆到可独立验证/重试为止；不按代码量或主观复杂度拆。
7. 对外入口只有 hub；状态页、模块文档页、team board 是组件，不作为入口。
8. SDD 数据只读 `status.json`/`spec-doc.json`；team 执行状态只读 `board/state.json`；不得把状态数据内嵌回 HTML。
9. team 的 `task.done` 不得直接推进 cell；只有本 skill 的验收/失效规则能改 cell 状态。

## 依赖

- `doc/skills/team/SKILL.md` + `team/core/protocol.md` — team 执行器与 SDD ↔ team 接口
- `doc/skills/hub/SKILL.md` + `template/hub-template.html` — 单一入口（SDD + team）
- `doc/skills/spec-doc-gen/template/process-status-template.html` + `status.json` — SDD 状态页
- `doc/skills/spec-doc-gen/template/spec-doc-template.html` + `spec-doc.json` — 模块文档页
- `doc/rule/doc-format/SKILL.md` — 元信息行、Terminology、版本 a.b.c
- `doc/rule/doc-viz/SKILL.md` — 图型选择、颜色与注脚
