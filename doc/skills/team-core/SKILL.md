---
name: team-core
description: 主从 agent team 的主运行框架：协议正本（spec/task/message/状态机/铁律/并行增量）、运行文案模板与设计依据、运行观测记录。TeamLead 与所有 worker 执行任何 spec 前必读 protocol.md；team-board（可视化）与 team-parallel（并行）均以本框架为基础。
---

# team-core — team 主运行框架

> 状态：active v1.0.0 ｜ 日期：2026-10-16 ｜ 维护者：Kilo（用户指令）

执行者：**TeamLead**（`.github/agents/team-lead.agent.md`）；参与者：team-worker-research / team-worker-code。

## Terminology

| 术语 | 定义 |
|---|---|
| spec | 一次大任务及其运行工作目录 `doc/specs/<spec>/` |
| board | spec 下 tasks/messages/state.json/view.html 的总称，编排唯一事实源 |
| 协议正本 | protocol.md——team 协作规则的唯一权威出处 |
| 运行模板 | template/ 下复制即用的任务/消息/文案骨架 |
| 状态盘 | state.json + view.html 组合（可视化契约见 team-board） |

## 本 skill 内容

| 资源 | 说明 |
|---|---|
| [protocol.md](protocol.md) | **协议正本**：spec 目录、任务/消息 schema、消息类型与状态机、铁律、并行增量、state.json 数据契约与维护职责 |
| [template/](template/) | 运行模板：task-template · message-template · state-template.json · lead-subagent-io · lead-spec-context · worker-reply |
| [design.md](design.md) | 架构与设计依据：调用时序、任务状态机、会话线程模型、目录布局、职责边界 |
| [NOTES.md](NOTES.md) | 运行观测记录（轮次数据、违反明细、结论） |

## 运行

1. VS Code 打开仓库根（agent 定义在 `.github/agents/` 直接被发现）；Chat → Agent 模式，`@` 列表出现 TeamLead。
2. 发送 `@TeamLead <spec描述>`：TeamLead 按 protocol.md 初始化 `doc/specs/<spec>/`（board 目录、state.json、复制 view.html），拆解并分派任务。
3. 子任务 ≥2 且授权路径互不相交 → 按 `doc/skills/team-parallel/SKILL.md` 做 worktree 并行分割。
4. 状态盘：`npx -y http-server doc\specs\<spec>\board -p 8787 -c-1` → <http://127.0.0.1:8787/view.html>（或 Live Preview 打开 `board/view.html`）。
5. 每轮运行按 [NOTES.md](NOTES.md) 表头口径记录观测。

## 重置

```powershell
Remove-Item -Recurse -Force doc\specs\<spec>
```

## 相关 skill

- `team-board`：过程可视化渲染（view.html 状态盘；数据契约正本在本 skill protocol.md）
- `team-parallel`：worktree 并行编排增量（依赖本 skill）
- `doc/rule/doc-rule`：文档与会话展示规范（全局约束所有产出）
