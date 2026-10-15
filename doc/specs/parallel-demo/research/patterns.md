# 三种多 agent 编排模式对比调研

> task-001 产出 ｜ team-worker-research ｜ 2026-10-15
> 依据文档（均相对仓库根 `E:\code\project\techs`）：
> - 协议正本：`doc/skills/team-board/board-protocol.md`
> - 并行技能：`doc/skills/team-parallel/SKILL.md`
> - POC 设计：`doc/skills/chat-team/design.md`
> - 需求范围：`doc/skills/chat-team/requirements.md`
> - 运行观测：`doc/skills/chat-team/NOTES.md`

## 0. 模式定义

| 模式 | 一句话定义 | 本仓库对应 |
|---|---|---|
| A. 顺序 pipeline | 固定次序接力：前一个 agent 的产出是后一个的输入，次序在编排时写死 | design.md §1.1 时序图中 task-001(research) → task-002(code) 的串行接力即此形态 |
| B. 文件 board 主从（串行） | TeamLead 动态拆任务、经文件 board 分派，worker 无状态冷启动读 board 干活回帖，一次只跑一个 worker | team-board 协议正本（board-protocol.md 全文）+ design.md §1、§1.3 |
| C. worktree 并行分割 | 在 B 的基础上，授权路径互不相交的子任务各自分到一个 git worktree + 分支，批量发起、串行 merge 汇合 | team-parallel SKILL.md + board-protocol.md「并行模式（v0.2 增量）」节 |

三者是递进关系：A 是无调度器的静态特例，B 引入「board 为唯一事实源 + 动态分派」，C 在 B 上叠加 worktree 物理隔离。C 的适用判定不满足时回退 B（SKILL.md「适用判定」：「全部满足才并行，否则回退串行流程」）。

## 1. 对比总表

| 维度 | A. 顺序 pipeline | B. 文件 board 主从（串行） | C. worktree 并行分割 |
|---|---|---|---|
| 适用场景 | 阶段固定、依赖链清晰的流水任务（调研→写作→审校） | 任务拆解需运行时决定；子任务间有依赖或共享写目标；需要可审计消息链 | 子任务 ≥2 且**授权路径互不相交**；共享文件改动可独立为串行收尾任务；git 可用（SKILL.md「适用判定」三条全满足） |
| 隔离性 | 无隔离：同一工作区、同一分支，全靠次序保证 | 逻辑隔离：写权限白名单（board/** + 任务授权路径），但物理上同一工作副本，「写范围只由 prompt 约定（无代码强制）」（design.md §6） | 物理隔离：每任务一个 worktree + 分支，业务文件互不可见；board 不进 worktree、以绝对路径共享（SKILL.md §6「worker 契约」） |
| 冲突面—文件写 | 高：后段 agent 可覆盖前段产出，无 git 边界 | 中：白名单靠自觉；观测已见越权（NOTES.md「协议违反明细」：research 自报执行 shell；TeamLead state.json 漂移） | 低：worktree 间文件系统隔离，白名单外改动被 git status 暴露；冲突推迟到 merge 时由 TeamLead 串行处理（SKILL.md §7） |
| 冲突面—编号 | 不适用（无消息编号） | 中：`msg-NNNN` 全局递增，靠「写前必须重新扫描」防撞（board-protocol.md「board 目录」节）；串行下实际风险低 | 低：并行任务改用命名空间 `msg-<taskid>-NN`，「无锁不撞号」（SKILL.md §4）；TeamLead 全局消息仍 `msg-NNNN` |
| 冲突面—状态漂移 | 高：无集中状态机，进度只存在于调用方上下文 | 中：status 状态机 open→claimed→done/failed + state.json 单点维护；实测「连协议唯一写者也会漂移」（NOTES.md 违反明细 1） | 中：状态面与 B 相同（board 共享）；增量允许多 step 同时 running、多 worker 同时 working（SKILL.md §8），TeamLead 汇总时状态更复杂 |
| 恢复复杂度 | 低—中：重跑失败阶段即可，但可能需清理半成品 | 低：board 是事实源，worker 无状态，「读 board 重建状态」即可续作（design.md §1.3）；ask 流程支持跨实例续作 | 中：额外 git 现场——失败重试「仍在原 worktree 内进行」；worker 挂起可「重建 worktree 重试一次」；merge 冲突 abort 后「worktree 与分支保留现场，上报用户决策」（SKILL.md「失败处理」、§7） |
| 时效 | 串行，总时长 = 各阶段之和 | 串行，总时长 = 各任务之和 | 通道支持异步则真并行（Kilo task background）；不支持则「退化串行，隔离性与正确性不变，只损失时效」（SKILL.md「通道适配表」、「失败处理」） |
| 依赖 | 仅调用通道 | 文件 board + 调用通道 | B 的全部 + git CLI + worktree 操作纪律（SKILL.md description：「仅需 git CLI + 宿主提供的子代理调用通道」） |

## 2. 逐模式分析

### A. 顺序 pipeline（固定次序接力）

- **适用场景**：阶段划分在设计时已确定、每阶段输入输出明确的流水作业。本仓库 design.md §8 的端到端验证场景（research 先产出、code 再依据其结果写文件）本质是一条两节点 pipeline，只是分派由 TeamLead 经 board 驱动。
- **隔离性**：零物理隔离，所有 agent 写同一工作副本；正确性完全依赖「前一阶段已结束」的时序假设。
- **冲突面**：文件写冲突最高——无白名单机制时后段可任意覆盖前段；无编号/状态机概念，进度状态只存在于编排方上下文中，编排方会话丢失即全丢。
- **恢复复杂度**：逻辑上简单（重跑失败节点），但因无持久化事实源，判断「跑到哪了」要靠人工检查工作区。
- **与 B 的差别**：B 把 pipeline 的「次序」从硬编码变成 board 上的动态分派，并引入可审计消息链与状态机（requirements.md §3：「协议负责……消息 schema、board 目录布局、任务状态机」）。

### B. 文件 board 主从（本仓库 team-board 串行模式）

- **适用场景**：POC 核心问题即此模式——「仅用 .md 文件 + 文件共享 board，能否让 LLM 可靠执行主从编排消息协议」（requirements.md §1）。适合任务需运行时拆解、子任务有依赖或共享写目标（如都要改 README）、需要留痕审计的场景。
- **隔离性**：只有逻辑隔离。职责边界表（design.md §6）规定各 agent 可写范围，但「写范围只由 prompt 约定（无代码强制）——违反情况是 POC 观测对象」。board 目录铁律 3 收紧写权限：TeamLead 只写 `board/**`，worker 写 `board/**` + 任务授权路径，任何 agent 不得写 `doc/skills/**`（board-protocol.md「铁律」）。
- **冲突面**：
  - 文件写：同一工作副本内靠白名单自觉；NOTES.md 第 2 轮观测到 worker 越权（执行 shell）与 TeamLead 自身 state.json 漂移，证明「无代码强制」风险双向成立。
  - 编号：`msg-NNNN` 全局递增，规则是「新编号 = 扫描对应目录现有最大编号 + 1（写前必须重新扫描）」（board-protocol.md「board 目录」）；串行下两轮观测「无撞号」（NOTES.md 正面观测）。
  - 状态漂移：status 必须按状态机流转且「任务文件最小写入」（只许改 status 与 `## 结果`，铁律 2）；state.json 为 TeamLead 单点维护的汇总视图，「冲突时以文件为准」（board-protocol.md「state.json」节）。
- **恢复复杂度**：低。worker 无状态、无常驻进程，「跨调用的对话线程不在内存，而是 board 消息文件重建的逻辑线程」（design.md §1.3）；ask 中断后 TeamLead 写 info 回帖、重新调用全新 worker 实例即可续作。append-only 铁律（铁律 1）保证消息链可完整回放。
- **限制**：requirements.md §4 Out of scope 明确原版 POC 不含并行；串行「一次调用一个 worker，等其返回后再继续」（design.md §1）。

### C. worktree 并行分割（本仓库 team-parallel 模式）

- **适用场景**：三条判定全满足才启用（SKILL.md「适用判定」）——①可拆出 ≥2 个授权路径互不相交的子任务；②需共享修改的文件可独立为串行收尾任务；③git 可用且宿主有子代理调用通道。任一不满足回退 B。
- **隔离性**：物理级。每任务 `git worktree add ../.worktrees/<spec>/task-NNN -b spec/<spec>/task-NNN`（SKILL.md §3 步骤 2），业务文件互不可见；board 例外——「board 不进 worktree；所有 agent 以绝对路径读写主工作副本的 board」（board-protocol.md「并行模式」增量 2），因为「worktree 看不到主目录未提交文件」（SKILL.md §0 untracked 文件陷阱）。
- **冲突面**：
  - 文件写：worker 间零交集；worker 禁止 merge/push/切分支/触碰其他 worktree（SKILL.md §6），合并权收归 TeamLead 串行执行 `git merge --no-ff`（board-protocol.md 增量 4），把并发写冲突转化为串行的 merge 冲突处理。
  - 编号：命名空间 `msg-<taskid>-NN` 每任务独立，「无锁不撞号」（SKILL.md §4）——这是对 B 模式「写前重扫」竞态的结构性消除。
  - 状态漂移：board 状态机不变，但 state.json 允许多个 step `running`、多个 worker `working`（SKILL.md §8），TeamLead 汇总视图复杂度上升；基线检查要求工作区干净（SKILL.md §0），否则未提交内容不进 worktree，属另一类漂移源。
- **恢复复杂度**：中，高于 B。git 现场成为恢复对象：worker failed 按协议正本重试 ≤1 次且「重试仍在原 worktree 内进行」；worker 挂起则「任务重置 open，可重建 worktree 重试一次；再失败上报用户」；merge 冲突「`git merge --abort`……worktree 与分支保留现场」待用户决策（SKILL.md「失败处理」、§7）。已知协议矛盾（正本铁律 2 vs 修订任务描述）以「修订内容写入新 task.assign 消息、任务文件只重置 status」的方式绕过。
- **通道无关性**：真并行与否取决于宿主通道（Kilo task background=✅；Copilot agents 同步=❌退化串行），但「隔离性与正确性不随通道变化」（design.md §12、SKILL.md「通道适配表」）。

## 3. 选型建议

1. 依赖链固定且短 → A（无需 board 开销）；但本仓库实践中 A 总是被 B 包裹（分派与留痕走 board）。
2. 默认 → B：串行为默认（design.md §12），board 提供事实源、审计链与低恢复成本。
3. 子任务 ≥2、授权路径可做到互不相交、时效敏感 → C：用 git 换物理隔离与并行时效，代价是 merge 汇合纪律与更重的失败现场管理。
4. C 的硬前提是把「共享写目标」剥离为串行收尾任务（如本 spec 的 README 类任务），否则回退 B。

## 4. 引用出处汇总

| 引用点 | 文件 + 章节 |
|---|---|
| board 唯一事实源、只传任务 id | design.md §1；requirements.md §6 Q3 |
| worker 无状态、board 重建逻辑线程、ask 续作 | design.md §1.3 |
| 任务状态机 open→claimed→done/failed | board-protocol.md「消息类型与状态机」；design.md §1.2、§5 |
| append-only、任务文件最小写入、写权限、state.json 单点维护 | board-protocol.md「铁律」1/2/3/6、「state.json」节 |
| 消息编号扫描 +1 规则 | board-protocol.md「board 目录」 |
| 并行适用判定、worktree 创建、命名空间编号、worker 契约、merge 汇合、失败处理、通道适配 | team-parallel/SKILL.md「适用判定」「流程」§0/§2/§4/§6/§7、「失败处理」、「通道适配表」 |
| 并行模式协议增量（worktree 隔离、board 共享、合并只由 TeamLead 做） | board-protocol.md「并行模式（v0.2 增量）」1–5 |
| 无代码强制、越权与漂移实测 | design.md §6；NOTES.md「协议违反明细」1/2、「正面观测」 |
| 串行默认、并行为增量 | design.md §12；requirements.md §4 Out of scope |
