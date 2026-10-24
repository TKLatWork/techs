# workflow-web — 子代理共享任务要求

> 状态：draft v0.1.0 ｜ 日期：2026-10-22 ｜ 维护者：Kilo（用户指令）

## Terminology

| 术语 | 定义 |
|---|---|
| 共享要求 | 本文件，三个子代理都必须遵守的交付契约 |
| 子项任务书 | 各模块目录下的 `task-requirement.md`，只描述本模块范围 |
| 冻结契约 | [spec.md](spec.md) §5 的签名、包名与错误码，Phase 1 不得单方面修改 |
| 模块自测 | 只在本模块执行 `mvn -pl <module> test`，不跑全量 reactor |
| Lead | 负责 Phase 0 脚手架、契约冻结与 Phase 2 集成收口 |
| 报告 | 子代理完成后的结构化回执：改动、命令、结果、风险 |

## 1. 阅读顺序

每个子代理开始前按顺序阅读：

1. [spec.md](spec.md) — 目标、契约、模块边界、验收。
2. 本文件 — 共享规则与交付格式。
3. 本模块 `task-requirement.md` — 只属于你的任务细节。
4. `project/` 下 Phase 0 已生成的父 POM、模块 POM、契约骨架。

不要修改上述文档；它们由 Lead 维护。

## 2. 冻结契约摘要

完整定义以 [spec.md](spec.md) §5 为准，签名不得改动：

```java
package com.example.workflow.api;

public interface WorkflowService {
    WorkflowInstance start(StartWorkflowCommand command);
    WorkflowInstance advance(String instanceId);
    WorkflowInstance get(String instanceId);
    WorkflowInstance cancel(String instanceId);
}
```

```java
public record StartWorkflowCommand(String workflowType, List<String> steps, Map<String, Object> variables) {}
public record WorkflowInstance(String id, String workflowType, List<String> steps, int currentStep,
                               WorkflowStatus status, Map<String, Object> variables,
                               Instant createdAt, Instant updatedAt) {}
public enum WorkflowStatus { RUNNING, COMPLETED, CANCELLED }
```

异常：`WorkflowException`、`WorkflowValidationException`、`WorkflowNotFoundException`、`WorkflowStateException`。

状态规则：

- `start`：`RUNNING`，`currentStep=0`。
- `advance`：非最后一步 `currentStep+1`；最后一步 `COMPLETED`。
- `cancel`：`RUNNING → CANCELLED`；`CANCELLED` 幂等；`COMPLETED` 抛 `WorkflowStateException`。
- `currentStep` 为 0-based；`steps` 非空且不能有空白元素。

## 3. 文件所有权

| 角色 | 只允许修改 | 禁止修改 |
|---|---|---|
| Lead | `project/pom.xml`、`project/.mvn/**`、`project/mvnw*`、`project/README.md` | 三个模块的实现文件 |
| Agent A | `project/workflow-api/**` | 父 POM、core、web、doc |
| Agent B | `project/workflow-core/**` | 父 POM、api、web、doc |
| Agent C | `project/workflow-web/**` | 父 POM、api、core、doc |

模块内 POM 可以修改，但只能调整本模块依赖；新增第三方依赖或改父 POM 必须先报告 Lead。

## 4. 并行规则

1. **路径隔离**：只编辑自己的模块目录。发现需要改其他模块，停止并报告。
2. **契约冻结**：不得改 §2 的签名、包名、错误码。确需变更时写清原因、影响模块与建议签名，交 Lead 仲裁。
3. **构建隔离**：只跑本模块测试；不要并发执行 `mvn clean verify` 或全量 `mvn install`。
4. **不碰 git**：不 commit、不切分支、不 merge、不 push；工作区里的其他改动不要回滚。
5. **只读文档**：`doc/spec-changes/workflow-web/**` 只读；`doc/skills/**`、`doc/rule/**` 更不得修改。
6. **不做超范围设计**：不引入数据库、MQ、鉴权、BPMN、分布式调度。
7. **验证优先**：没有运行测试就不得声称完成；测试失败要如实报告。

## 5. 交付格式

每个子代理的最终回执必须包含：

```text
## 完成摘要
<3-5 行，说明实现了什么>

## 改动文件
- path:line — 作用

## 执行的命令
- `mvn ...` — 结果（通过/失败，测试数）

## 契约偏差
- 无 / 具体偏差与原因

## 风险与后续
- 无 / 需要 Lead 处理的事项
```

不要粘贴大段源码；用 `path:line` 指向关键位置。

## 6. Definition of Done

- [ ] 模块可编译。
- [ ] 本模块测试全部通过。
- [ ] 公开签名与 [spec.md](spec.md) §5 完全一致。
- [ ] 没有引入禁止依赖（api 无 Spring；core 无 web/servlet）。
- [ ] 只修改了本模块目录。
- [ ] 回执包含改动、命令、测试结果与风险。

## 7. Lead 集成命令

Phase 2 由 Lead 在 `project/` 执行：

```powershell
mvn clean install
mvn -pl workflow-web spring-boot:run
mvn -pl workflow-web dependency:tree
```

冒烟顺序：`POST /api/workflows` → `POST /api/workflows/{id}/advance` → `GET /api/workflows/{id}` → `POST /api/workflows/{id}/cancel` → `GET /actuator/health`。