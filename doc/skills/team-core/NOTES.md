# 运行观测记录

> 协议与设计见同目录 `protocol.md` 与 `design.md`；spec 运行目录见 `doc/specs/<spec>/`。

## 预检结果（Copilot runtime，待验证）

**运行时说明**：两轮均在 Kilo CLI 会话内由主会话代行 TeamLead，跨 agent 通道 = Kilo `task` 子代理（真跨 agent、无状态实例、冷启动读 board，与 design §1.3 线程模型一致）；两次 worker 调用均为后台模式（不阻塞 chat）。Copilot `agents` 工具侧仍未验证。

- [ ] VS Code 以仓库根为工作区时 `.github/agents/` 被识别（@ 列表出现 TeamLead）（未验证）
- [ ] `tools` 中 `agents` 无 unknown-tool 告警（VS Code 版本：未验证）
- [x] TeamLead 可唤起并真跨 agent 调用 worker（Kilo runtime 下成立，两轮）

## 运行记录

| 轮次 | 日期 | spec | 场景 | 消息条数 | schema 符合 | 状态机遵守 | 越权行为 | 备注 |
|---|---|---|---|---|---|---|---|---|
| 1 | 2026-10-13 | （chatTeam 旧布局，数据已清） | design §8 旧场景 | 6 | 6/6 | 是 | 无 | 前台+后台各 1 次调用；发现 R2/R3c 文档矛盾（已修） |
| 2 | 2026-10-15 | skills-audit | design §8 spec 制场景 | 6 | 6/6 | 是（open→claimed→done ×2） | TeamLead 1 次（state.json 漂移，轮内自纠）；research 自报 2 条只读 shell | 两次调用均后台；msg-0005/0006 为真实秒级时间戳；spec 目录已于 2026-10-15 按要求删除（risks.md R1–R9/quickref.md 随之移除，关键发现已吸收进协议 v0.2 并行增量与 team-parallel SKILL.md） |
| 3 | 2026-10-15 | parallel-demo | team-parallel 首跑：双 worktree 并行 + merge + 串行收尾 | 10（全局 6 + 命名空间 4） | 10/10 | 是（open→claimed→done ×3） | 无 | 双后台 task 真并行；msg-task-NNN 命名空间零撞号；merge --no-ff ×2 零冲突；board 写主副本未混入分支 commit；worktree 当轮在仓库外，轮后按方案 B 改仓库内 `.worktrees/` + .gitignore；spec 目录运行后由用户删除，模板三件套已吸收进 `team-board/template/`（patterns.md 等产物存于 git `22df466`） |

| 4 | 2026-10-16 | module-viz-skill | 人工批准 gate 首用（规划段） | 5（含 1 条纠错） | 5/5 | 纠错回退 ×1（claimed→open，依据 msg-0005） | **TeamLead 1 次越权**：gate 答复「继续看看前半段的规划」被误读为执行批准，分派 task-001；用户手动终止 sub | 纠偏完整（msg-0005 撤回 + 状态回退 + 无效产物标记）；协议已补「批准须明确表述」条款 |

## 协议违反明细

第 4 轮（module-viz-skill）：

1. **TeamLead gate 语义误读（越权分派）**：用户在批准 gate Q1 的答复「插入实现gate，继续看看前半段的规划」被解读为「批准调研段执行」，随即分派 task-001 并写 msg-0002「批准通过」记录；用户实际意图是**审查规划、未批准执行**，手动终止了 worker。**处置**：msg-0005 撤回分派并作废 msg-0002 批准记录、task-001 status 回退 open、worker 遗留 claim（msg-0004）标记无效、`## 结果` 未写入无需清理。**根因**：协议未定义「批准」的表述标准，LLM 把倾向性答复当成许可。**修复**：protocol.md「人工批准 gate」第 4 条——批准必须是明确表述（同意/批准/开跑/执行），含糊或审查性答复一律视为未批准。**含义**：gate 机制本身有效（phase/消息/state 全程可回溯、可纠偏），但 gate 判定不能靠 LLM 语义推断，必须协议显式收紧。

第 3 轮（parallel-demo）：零违反。正面观测：① 并行命名空间编号（msg-task-001-NN / msg-task-002-NN）与全局编号（msg-NNNN）双轨并行无冲突；② worker 严格把 board 写在主副本、产物 commit 在各自分支，merge 干净；③ task-003 遵守「禁止 commit」附加约束；④ reply_to 链完整（assign→claim→result 逐级回指）。

第 2 轮（skills-audit）：

1. **TeamLead（state.json 单点维护者）漂移**：初始化时把尚无任务文件的 task-002 提前登记进 `tasks[]`，且 status 用了 schema 外枚举值 `pending`。由 task-001 调研发现（风险 R5），TeamLead 于 task-002 分派时按「先建任务文件再登记、枚举收紧」纠正。**含义：连协议唯一写者也会漂移，「无代码强制」风险在 TeamLead 侧同样成立；协议需补「state.json 只登记已有任务文件的 task + steps 可含规划中步骤」规则。**
2. **team-worker-research 越权（自报）**：执行 2 次 shell 命令（Get-Date 取时间、一次 rg 检索尝试），违反角色「不执行命令」硬规则；无文件改动，检索改用只读工具完成，并在任务结果 C 节如实自报。**含义：worker 自报机制有效；「取 UTC 时间戳」是纯 .md 环境的普遍越权诱因（对应风险 R7），协议应给出合规取时方式。**
3. **team-worker-code**：零违反；白名单外零改动（经 LastWriteTime 核查 doc/skills 与 .github 在运行窗口无写入）。

正面观测：第 2 轮消息 ts 为真实时钟秒级值（第 1 轮为分钟级推断值）；worker 编号写前重扫规则被两个 worker 遵守，无撞号。

## 结论

- [ ] 可行：纯 .md + 文件 board 协议可被 LLM 可靠执行
- [ ] 部分可行：需代码强制的点：＿＿＿
- [ ] 不可行，原因：＿＿＿

依据：
