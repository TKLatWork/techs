---
name: team-parallel
description: team 执行器的 parallel 组件：TeamLead 并行编排，git worktree 分割子任务、经文件 board 并行调度多个 worker、串行 merge 汇合。当子任务 ≥2 且授权路径互不相交、用户要求并行执行、或提到 worktree 分割时使用。不依赖任何 runtime 专有功能，仅需 git CLI + 宿主提供的子代理调用通道。
---

# team/parallel — TeamLead 并行编排（worktree 分割）

> 状态：active v1.4.0 ｜ 日期：2026-10-19 ｜ 维护者：Kilo（用户指令）

组件入口，隶属 [`team`](../SKILL.md)。执行者：**TeamLead**（`.github/agents/team-lead.agent.md`）。
协议基础：`doc/skills/team/core/protocol.md`（必读；本组件只规定并行增量，冲突时以协议正本为准）。

## Terminology

| 术语 | 定义 |
|---|---|
| worktree 分割 | 每个并行任务独占一个 git worktree + 分支，业务文件物理隔离 |
| 主工作副本 | 仓库原工作树；board 实体所在地，所有 agent 以绝对路径共享 |
| 命名空间编号 | 并行任务消息编号 `msg-<taskid>-NN`，无锁防撞号 |
| 串行收尾任务 | 修改共享文件的任务，在全部 merge 后于主副本执行 |
| merge 汇合 | TeamLead 核验后串行 `git merge --no-ff` 并清理 worktree/分支 |
| 通道 | 宿主的子代理调用机制（Copilot agents / Kilo task / 人肉多窗口） |

## 适用判定（全部满足才并行，否则回退串行流程）

1. 可拆出 ≥2 个**授权路径互不相交**的子任务
2. 需共享修改的文件（如 README）可独立为串行收尾任务
3. git 可用；宿主有子代理调用通道（任意：Copilot `agents` 工具 / Kilo `task` 工具 / 人肉多 chat 窗口）

## 流程

### 0. 基线检查

```
git status --short
```

工作区脏（有未提交改动）→ 请用户先 commit/stash 再继续：worktree 从 HEAD checkout，未提交内容不会进入新 worktree。

同时确认仓库根 `.gitignore` 含 `.worktrees/`（worktree 放仓库内，必须排除；缺失则先补上并提交）。

**untracked 文件陷阱**：`doc/skills/`、`doc/spec-changes/`、`.github/agents/` 等未提交文件在新 worktree 中**不可见**。因此所有 worker 对 board 与规范文档一律使用**绝对路径**（任务描述中写全）。

### 1. 拆分

拆为 N 个不相交并行子任务 + 可选串行收尾任务。每个并行任务确定：授权范围（worktree 内路径）、分支名、确认无相互依赖。SDD 任务在 frontmatter 写 `cell` / `input_versions` / `attempt`；worktree 只隔离实现，不改变 cell 的交付状态。

### 2. 创建 worktree（每并行任务一个）

```
git worktree add .worktrees/<spec>/task-NNN -b spec/<spec>/task-NNN
```

worktree 统一放**仓库内** `.worktrees/<spec>/` 下（IDE 文件树可见，便于观察与清理；已由 `.gitignore` 排除，不污染 git status）。搜索工具若扫到副本噪音，在编辑器排除设置中加 `.worktrees`。

### 3. 任务文件增量字段

`board/tasks/task-NNN.md` frontmatter 追加：

```yaml
worktree: <绝对路径>          # 串行任务为 null
branch: spec/<spec>/task-NNN  # 串行任务为 null
```

任务描述必须自包含四要素 + 并行两要素：**board 绝对路径、协议正本绝对路径、worktree 内授权范围、验收标准**；以及「完成后 `git add -A && git commit -m "task-NNN: <摘要>"`，把 commit hash 写进 `## 结果` 与 task.result 消息」。

### 4. 消息编号（并行命名空间）

- 并行任务的消息：`msg-<taskid>-NN`（如 `msg-task-003-01`、`-02`），每任务独立命名空间，**无锁不撞号**；worker 扫描编号只看自己任务的命名空间前缀
- TeamLead 的全局消息（assign / info / 汇总）仍用 `msg-NNNN`

### 5. 并行调用

一次性发起全部并行 worker（通道支持异步/后台就用异步；不支持就逐个同步调用——**worktree 隔离不依赖真并行，流程正确性不变，只损失时效**）。

调用行模板：

> 你是 <assignee>。spec 根 <spec 目录绝对路径>，新任务 task-NNN。先读 <仓库根>/doc/skills/team/parallel/SKILL.md 的「worker 契约」节与 <仓库根>/doc/skills/team/core/protocol.md，再读 spec 目录下 board/tasks/task-NNN.md 执行。业务文件操作一律在任务文件 `worktree` 字段指定的目录内；board 用绝对路径。

### 6. worker 契约（并行增量）

- 业务文件读写只在自己的 worktree 内；board（claim/result/ask 消息、任务文件 `## 结果`/status）用**绝对路径**跨 worktree 操作
- 消息编号用自己任务的命名空间（`msg-<taskid>-NN`）
- 完成后：`git add -A`；`git commit -m "task-NNN: <摘要>"`；commit hash 写入 `## 结果` 与 task.result 消息
- **禁止**：merge、push、切分支、触碰其他 worktree、写 `doc/skills/**`

### 7. 汇合与合并（TeamLead 串行执行）

对每个返回 done 的任务依次：

1. 核验 board：task.result 消息 + `## 结果`（含 commit hash）+ 授权范围
2. `git merge --no-ff spec/<spec>/task-NNN`
3. 成功 → `git worktree remove .worktrees/<spec>/task-NNN`；`git branch -d spec/<spec>/task-NNN`；写 `info` 消息记录 merge 结果
4. 冲突 → `git merge --abort`，写 `info` 消息记录，**上报用户决策**；worktree 与分支保留现场
5. 全部 merge 成功后，再执行串行收尾任务（如有）

### 8. state.json 增量

`flow.steps[]` 允许多个 `running`；`agents[]` 多个 worker 同时 `working`；tasks 仍「先建任务文件再登记」（协议正本规则）。

## 失败处理

- **worker failed**：按协议正本重试规则（每任务 ≤1 次）。已知正本 v0.1 铁律 2 与「修订任务描述」存在矛盾（skills-audit 轮观测发现，该 spec 已删除，见 `doc/skills/team/core/NOTES.md`）：执行时采用「修订内容写入新 task.assign 消息正文，任务文件只重置 status」的方式。重试仍在原 worktree 内进行
- **worker 无响应/挂起**：通道超时后 TeamLead 写 `info` 消息，任务重置 `open`，可重建 worktree 重试一次；再失败上报用户
- **通道无并行能力**：全流程退化为串行执行，隔离性与正确性不受影响

## 通道适配表

| Runtime | 调用方式 | 真并行 |
|---|---|---|
| Kilo CLI | `task` 工具 `background=true` ×N | ✅ |
| VS Code Copilot Chat | `agents` 工具（同步阻塞） | ❌ 退化串行，隔离仍成立 |
| 人肉多窗口 | 每 worker 一个 chat 窗口，TeamLead 窗口汇合 | ✅（手动投递） |
