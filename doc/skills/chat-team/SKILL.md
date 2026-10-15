---
name: chat-team
description: chatTeam 主从 agent team POC 总纲：需求范围、架构与协议设计、运行观测记录，以及运行/重置指引。了解 POC 全貌、查设计依据、记录观测结论时使用；协议细节正本在 team-board，并行编排在 team-parallel。
---

# chat-team — POC 总纲与公共文档

**PROTOTYPE — 验证「仅用 .md + 文件 board 能否可靠执行主从编排协议」，验证后删除或吸收。**

## Terminology

| 术语 | 定义 |
|---|---|
| spec | 一次大任务及其运行工作目录 `doc/specs/<spec>/` |
| skill | 带 SKILL.md 入口的能力/规范单元（`doc/skills/` 下，规则见其 AGENTS.md） |
| 状态盘 | spec board 下 state.json + view.html 组合 |

## 本目录文档

| 文件 | 内容 |
|---|---|
| [requirements.md](requirements.md) | 需求范围（v0.2 已确认：核心问题、约束、决议、验收标准、预检、交付物树） |
| [design.md](design.md) | 协议与 agent 设计（调用时序 §1.1、状态机 §1.2、线程模型 §1.3、目录布局 §2、观测指标 §9、并行模式 §12） |
| [NOTES.md](NOTES.md) | 运行观测记录与可行性结论 |

## 仓库目录约定

| 位置 | 用途 |
|---|---|
| `.github/agents/*.agent.md` | agent 定义：TeamLead + research/code worker（VS Code 以仓库根为工作区直接发现） |
| `doc/rule/doc-rule/` | **文档规范**（全局覆盖）：结构/风格/Terminology 必备/版本管理 + chat 展示格式 + template/ 文档模板 |
| `doc/skills/AGENTS.md` | skills 目录规则（顶层唯一文件） |
| `doc/skills/chat-team/` | 本 skill：POC 总纲与公共文档 |
| `doc/skills/team-board/` | skill：board 编排协议（正本 board-protocol.md + template/ 运行模板：schema、view.html 状态盘、运行文案） |
| `doc/skills/team-parallel/` | skill：worktree 并行编排（依赖 team-board） |
| `doc/specs/<spec>/` | spec 运行工作目录（TeamLead 初始化，含 board/ 与任务产物） |

## 运行

1. **预检**（design.md §7）：打开 `.github/agents/team-lead.agent.md` 确认 `agents` 工具无告警；Chat（Agent 模式）`@` 列表出现 TeamLead。
2. **发送 spec**：

   ```
   @TeamLead <spec描述>
   ```

   TeamLead 自动创建 `doc/specs/<spec>/` 并初始化 board；子任务 ≥2 且授权路径互不相交时按 team-parallel 技能做 worktree 并行分割。
3. **状态盘**（可选）：

   ```powershell
   npx -y http-server doc\specs\<spec>\board -p 8787 -c-1
   ```

   打开 <http://127.0.0.1:8787/view.html>；或 VS Code Live Preview 右键 `board/view.html` → Show Preview。
4. **观测**：按 design.md §9 指标记录到本目录 [NOTES.md](NOTES.md)。

## 重置

删除对应 spec 目录即可（规范文档不受影响）：

```powershell
Remove-Item -Recurse -Force doc\specs\<spec>
```
