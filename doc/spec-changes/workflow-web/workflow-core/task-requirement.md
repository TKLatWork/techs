# Agent B — workflow-core 子项任务书

> 状态：draft v0.1.0 ｜ 日期：2026-10-22 ｜ 维护者：Kilo（用户指令）

## Terminology

| 术语 | 定义 |
|---|---|
| Agent B | 本子项的唯一执行者，只负责 `workflow-core` |
| 领域服务 | `DefaultWorkflowService`，实现 API 的 `WorkflowService` 端口 |
| 仓库端口 | `WorkflowRepository`，隔离实例存储 |
| 内存仓库 | `InMemoryWorkflowRepository`，`ConcurrentHashMap` 实现 |
| 状态转换 | `RUNNING → COMPLETED / CANCELLED` 的合法迁移 |
| 终态 | `COMPLETED` 或 `CANCELLED` |

## 1. 角色与目标

你是 Agent B，负责 `workflow-core`。目标是在不修改 API 契约的前提下，实现线程安全的内存工作流实例服务，并用单元测试覆盖所有状态转换与错误分支。

## 2. 只允许修改

```text
project/workflow-core/**
```

禁止修改：`project/pom.xml`、`project/workflow-api/**`、`project/workflow-web/**`、`project/README.md`、`doc/**`。

## 3. 输入

1. [../spec.md](../spec.md) §5、§6
2. [../task-requirement.md](../task-requirement.md)
3. [../workflow-api/task-requirement.md](../workflow-api/task-requirement.md)（只读，理解契约）
4. Phase 0 生成的 `project/workflow-core/` 骨架与已安装的 `workflow-api`

## 4. 交付物

包：`com.example.workflow.core`

| 文件 | 内容 |
|---|---|
| `WorkflowRepository.java` | `save(WorkflowInstance)`、`findById(String)`（返回 `Optional`） |
| `InMemoryWorkflowRepository.java` | `@Repository`，`ConcurrentHashMap` |
| `DefaultWorkflowService.java` | `@Service`，实现 API 的 `WorkflowService` |
| `WorkflowStateMachine.java` | 可选；把状态转换规则集中到一处 |
| `src/test/java/**` | 单元测试 |

## 5. 实现要求

1. `DefaultWorkflowService` 实现 `com.example.workflow.api.WorkflowService`，构造器注入 `WorkflowRepository`。
2. `start(StartWorkflowCommand command)`：
   - `command` 非 null；`workflowType` 非空白；`steps` 非空且元素非空白。
   - 生成 `UUID.randomUUID().toString()`。
   - `status=RUNNING`，`currentStep=0`，`createdAt=updatedAt=Instant.now()`。
   - 保存并返回。
3. `advance(String instanceId)`：
   - 找不到 → `WorkflowNotFoundException`。
   - 非 `RUNNING` → `WorkflowStateException`。
   - 非最后一步 → `currentStep+1`，状态保持 `RUNNING`。
   - 最后一步 → `status=COMPLETED`，`currentStep` 保持最后一步。
   - 更新 `updatedAt` 并保存。
4. `get(String instanceId)`：找不到 → `WorkflowNotFoundException`。
5. `cancel(String instanceId)`：
   - 找不到 → `WorkflowNotFoundException`。
   - `CANCELLED` → 幂等返回当前实例。
   - `COMPLETED` → `WorkflowStateException`。
   - `RUNNING` → `CANCELLED`，更新 `updatedAt` 并保存。
6. 线程安全：仓库使用 `ConcurrentHashMap`；服务方法不依赖可变共享状态。
7. 只使用 `org.springframework.stereotype.Service` / `Repository` 与 JDK；禁止 servlet、web、Spring Boot starter。
8. 不修改 API 签名；不新增 API 类型。如果确实需要，停止并报告 Lead。

## 6. 测试要求

至少覆盖：

| 场景 | 期望 |
|---|---|
| start 合法命令 | `RUNNING`、`currentStep=0`、id 非空 |
| start 空 steps / 空白 step | `WorkflowValidationException` |
| start null command | `WorkflowValidationException` |
| advance 非最后一步 | `currentStep+1`，仍 `RUNNING` |
| advance 最后一步 | `COMPLETED` |
| advance 不存在 | `WorkflowNotFoundException` |
| advance 终态 | `WorkflowStateException` |
| cancel 运行中 | `CANCELLED` |
| cancel 已取消 | 幂等返回 |
| cancel 已完成 | `WorkflowStateException` |
| get 不存在 | `WorkflowNotFoundException` |
| 并发 start/advance 基本冒烟 | 不丢实例、不抛意外异常 |

命令：

```powershell
cd project
mvn -pl workflow-core test
```

## 7. 完成标准

- [ ] `mvn -pl workflow-core test` 通过。
- [ ] 所有状态转换与错误分支有测试。
- [ ] 只用 `spring-context`，无 web/servlet 依赖。
- [ ] API 签名未改动。
- [ ] 只修改 `project/workflow-core/**`。
- [ ] 按共享任务书格式提交回执。

## 8. 禁止事项

- 不写 Controller、HTTP 错误码、`application.yml`。
- 不引入数据库、ORM、MQ、缓存框架。
- 不把 `WorkflowInstance` 改成可变类。
- 不修改 api/web 或父 POM。