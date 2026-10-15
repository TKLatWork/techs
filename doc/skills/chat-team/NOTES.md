# POC 观测记录

> 协议与设计见同目录 `design.md` 与 `../team-board/board-protocol.md`；spec 运行目录见 `doc/specs/<spec>/`。指标定义见 design.md §9。

## 预检结果（design.md §7）

**运行时说明**：两轮均在 Kilo CLI 会话内由主会话代行 TeamLead，跨 agent 通道 = Kilo `task` 子代理（真跨 agent、无状态实例、冷启动读 board，与 design §1.3 线程模型一致）；两次 worker 调用均为后台模式（不阻塞 chat）。Copilot `agents` 工具侧仍未验证。

- [ ] VS Code 以仓库根为工作区时 `.github/agents/` 被识别（@ 列表出现 TeamLead）（未验证）
- [ ] `tools` 中 `agents` 无 unknown-tool 告警（VS Code 版本：未验证）
- [x] TeamLead 可唤起并真跨 agent 调用 worker（Kilo runtime 下成立，两轮）

## 运行记录

| 轮次 | 日期 | spec | 场景 | 消息条数 | schema 符合 | 状态机遵守 | 越权行为 | 备注 |
|---|---|---|---|---|---|---|---|---|
| 1 | 2026-10-13 | （chatTeam 旧布局，数据已清） | design §8 旧场景 | 6 | 6/6 | 是 | 无 | 前台+后台各 1 次调用；发现 R2/R3c 文档矛盾（已修） |
| 2 | 2026-10-15 | skills-audit | design §8 spec 制场景 | 6 | 6/6 | 是（open→claimed→done ×2） | TeamLead 1 次（state.json 漂移，轮内自纠）；research 自报 2 条只读 shell | 两次调用均后台；msg-0005/0006 为真实秒级时间戳；spec 目录已于 2026-10-15 按要求删除（risks.md R1–R9/quickref.md 随之移除，关键发现已吸收进协议 v0.2 并行增量与 team-parallel SKILL.md） |

## 协议违反明细

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
