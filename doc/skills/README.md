# chatTeam POC：主从 Agent Team（VSCode Chat / Copilot，纯 .md 实现）

**PROTOTYPE — 验证「仅用 .md + 文件 board 能否可靠执行主从编排协议」，验证后删除或吸收。**

## 目录约定

| 位置 | 用途 |
|---|---|
| `.github/agents/*.agent.md`（仓库根） | agent 定义：TeamLead + research/code worker；VS Code 以 `techs` 为工作区根即直接发现 |
| `doc/skills/AGENTS.md` | **skills 目录规则**：每个 skill 一个目录、SKILL.md 入口、资源同目录、运行时只读 |
| `doc/skills/team-board/` | **skill：board 编排协议（串行基线）**——SKILL.md 入口 + board-protocol.md（协议正本）+ assets/view.html（状态盘模板） |
| `doc/skills/team-parallel/` | **skill：worktree 并行编排**——git 分割 + 消息编号命名空间 + TeamLead merge 汇合；不依赖 runtime 专有功能（依赖 team-board） |
| `doc/skills/*.md`（顶层） | 公共文档：README（本索引）/ requirements / design / NOTES |
| `doc/specs/<spec>/` | **每个spec的运行工作目录**：TeamLead 初始化，内含 `board/`（tasks、messages、state.json、view.html）与任务产物 |

## 运行

1. 预检（`design.md` §7）：打开 `.github/agents/team-lead.agent.md` 确认 `agents` 工具无告警；Chat（Agent 模式）`@` 列表出现 TeamLead。
2. 发送spec：

   ```
   @TeamLead <spec描述>
   ```

   TeamLead 自动创建 `doc/specs/<spec>/` 并初始化 board。
3. 状态盘（可选）：

   ```powershell
   npx -y http-server doc\specs\<spec>\board -p 8787 -c-1
   ```

   打开 <http://127.0.0.1:8787/view.html>；或 VS Code Live Preview 右键 `board/view.html` → Show Preview。
4. 观察 chat 简报与 spec board 消息链，按 `design.md` §9 指标记录到 `NOTES.md`。

## 重置

删除对应 spec 目录即可（规范文档不受影响）：

```powershell
Remove-Item -Recurse -Force doc\specs\<spec>
```
