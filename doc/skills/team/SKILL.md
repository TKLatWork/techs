---
name: team
description: 多 agent 执行器（单一 skill）：core/ 协议与状态机、board/ 执行状态盘、parallel/ worktree 并行编排。只负责执行，不定义 SDD 交付状态；与 spec-driven-dev 通过 WorkOrder / AttemptResult 对接。
---

# team — 多 agent 执行器

> 状态：active v2.0.0 ｜ 日期：2026-10-19 ｜ 维护者：Kilo（用户指令）

## Terminology

| 术语 | 定义 |
|---|---|
| team 执行器 | 本 skill：把工作分派给 agent、跟踪 attempt、合并产物；不判断交付是否完成 |
| SDD 交付 | `spec-driven-dev`：模块 × 阶段矩阵、Contract、版本、checkpoint、失效与回滚 |
| cell | SDD 的交付单元 `(module, stage)`；交付状态的唯一来源 |
| WorkOrder | SDD 给 team 的执行单：cell、输入版本、验收标准、输出路径 |
| Attempt | team 对某个 WorkOrder 的一次执行尝试；一个 cell 可有多次 attempt |
| 执行状态 | task 的 open/claimed/done/failed/cancelled；`done` = attempt succeeded，不等于 cell 状态 |

## 目录

| 子目录 | 职责 |
|---|---|
| `core/` | 协议正本：spec/task/message/状态机/铁律/state.json/context.md；运行模板；design 与 NOTES |
| `board/` | `template/view.html` 执行状态盘：轮询 `state.json`，渲染 task/message/agent |
| `parallel/` | worktree 分割、消息命名空间、merge 汇合 |

入口：`core/SKILL.md`；协议正本：`core/protocol.md`。执行任何 spec 前必读协议正本。

## 与 SDD 的边界

| 关注点 | SDD（spec-driven-dev） | team（本 skill） |
|---|---|---|
| 单元 | cell = module × stage | task = 针对 cell 的一次 attempt |
| 状态 | pending/active/done/invalid/blocked | open/claimed/done/failed/cancelled（done = attempt succeeded） |
| 版本 | Contract/输入/输出版本、checkpoint | worktree/branch/commit |
| 变更 | 失效下游锥、选择回滚点 | 取消/重跑 attempt、revert/重建 worktree |
| 验收 | Contract review / Final review（交付） | 规划批准 / 分派批准（执行许可） |
| 视图 | hub 的 SDD 交付页 | board 的 Team 执行页 |

## 接口

SDD 产出 WorkOrder：

```yaml
cell: api:contract
module: api
stage: contract
input_versions:
  api.requirement: v1.2.0
  root.contract: v1.1.0
acceptance: "tests + spec/contract 对照"
output_path: doc/spec-changes/<spec>/delivery/modules/api.json
```

team 返回 AttemptResult：

```yaml
task: task-007
cell: api:contract
attempt: 2
output_version: v1.2.1
commit: <hash>
artifacts: [contract/api.ts, tests/api.contract.spec.ts]
verification: "12/12 passed"
```

规则：

1. TeamLead 只把 task 标为 done/failed/cancelled；**不得把 cell 标 done**。
2. cell 的 done/invalid 由 SDD 验收和失效规则决定；team 只报告 attempt 结果。
3. SDD 失效某个 cell 时，TeamLead 取消该 cell 的未完成 task；新版本用新 attempt。
4. task 必须带 `cell`、`input_versions`、`attempt`；没有 cell 的任务只能作为不进入交付矩阵的维护任务。
5. team 不修改 Contract/版本；发现 Contract 问题写 `ask`，由 SDD 回退处理。

## 运行

1. 读 `core/protocol.md`。
2. 按 `core/SKILL.md` 初始化 board。
3. 并行时读 `parallel/SKILL.md`。
4. 状态盘：`npx -y http-server doc\spec-changes\<spec>\board -p 8787 -c-1` → `http://127.0.0.1:8787/view.html`。
5. 统一入口见 `doc/skills/hub/SKILL.md`（SDD 交付 + Team 执行）。

## 相关 skill

- `doc/skills/spec-driven-dev/SKILL.md` — SDD 交付协议；本 skill 的 cell/版本/验收来源
- `doc/skills/hub/SKILL.md` — 单一入口 hub，聚合 SDD 与 team
- `doc/rule/doc-format/SKILL.md` — 文档格式
- `doc/rule/doc-viz/SKILL.md` — 可视化规范