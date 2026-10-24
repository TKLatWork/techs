# Agent A — workflow-api 子项任务书

> 状态：draft v0.1.0 ｜ 日期：2026-10-22 ｜ 维护者：Kilo（用户指令）

## Terminology

| 术语 | 定义 |
|---|---|
| Agent A | 本子项的唯一执行者，只负责 `workflow-api` |
| 契约模块 | 对外暴露 DTO、端口、异常、枚举的纯 Java jar |
| 冻结签名 | [spec.md](../spec.md) §5 的接口与模型，不得单方面修改 |
| 防御性复制 | 构造 record 时复制 `List` / `Map`，避免外部可变引用 |
| 契约测试 | 验证不可变、空值校验、异常层级的最小测试集 |

## 1. 角色与目标

你是 Agent A，负责 `workflow-api`。目标是把 [spec.md](../spec.md) §5 的契约落地为可编译、可测试、无 Spring 依赖的 Java 21 模块。

## 2. 只允许修改

```text
project/workflow-api/**
```

禁止修改：`project/pom.xml`、`project/workflow-core/**`、`project/workflow-web/**`、`project/README.md`、`doc/**`。

## 3. 输入

1. [../spec.md](../spec.md)
2. [../task-requirement.md](../task-requirement.md)
3. Phase 0 生成的 `project/workflow-api/` 骨架

## 4. 交付物

包：`com.example.workflow.api`

| 文件 | 内容 |
|---|---|
| `WorkflowService.java` | 端口接口，4 个方法 |
| `WorkflowStatus.java` | `RUNNING` / `COMPLETED` / `CANCELLED` |
| `StartWorkflowCommand.java` | `workflowType`、`steps`、`variables` |
| `WorkflowInstance.java` | `id`、`workflowType`、`steps`、`currentStep`、`status`、`variables`、`createdAt`、`updatedAt` |
| `WorkflowException.java` | 基类，`RuntimeException` |
| `WorkflowValidationException.java` | 输入校验失败 |
| `WorkflowNotFoundException.java` | 实例不存在 |
| `WorkflowStateException.java` | 非法状态转换 |
| `src/test/java/**` | 契约测试 |

## 5. 实现要求

1. `WorkflowService` 签名必须与 [spec.md](../spec.md) §5.1 完全一致。
2. `StartWorkflowCommand` 与 `WorkflowInstance` 使用 Java `record`。
3. 两个 record 都写紧凑构造器：
   - 对 `steps` 做非 null、非空、元素非空白校验。
   - 对 `variables` 做 null 归一化（空 Map）或非 null 校验，并在文档中写明选择。
   - 使用 `List.copyOf` / `Map.copyOf` 防御性复制。
   - `WorkflowInstance` 校验 `currentStep >= 0` 且小于 `steps.size()`；`id`、`workflowType`、`status`、时间戳非 null。
4. 异常类：
   - `WorkflowException` 提供 `(String message)` 与 `(String message, Throwable cause)`。
   - 三个子类提供同样的构造器。
   - 不添加 Spring 或第三方依赖。
5. 公共 API 写 Javadoc：状态含义、`currentStep` 0-based、不可变语义。
6. 允许 `workflow-api/pom.xml` 保留 Phase 0 的最小配置；不要新增运行时依赖。

## 6. 测试要求

至少覆盖：

- `steps` 为空、包含空白元素时抛 `IllegalArgumentException` 或 `WorkflowValidationException`（二选一并保持一致）。
- `List` / `Map` 传入后修改不影响 record 内部值（防御性复制）。
- `WorkflowInstance` 的 `currentStep` 越界校验。
- 异常继承关系：三个子类均为 `WorkflowException`。
- `WorkflowStatus` 三个枚举值存在。

命令：

```powershell
cd project
mvn -pl workflow-api test
```

## 7. 完成标准

- [ ] `mvn -pl workflow-api test` 通过。
- [ ] `workflow-api` 无 Spring import、无第三方运行时依赖。
- [ ] 签名与 [spec.md](../spec.md) §5 一致。
- [ ] 只修改 `project/workflow-api/**`。
- [ ] 按共享任务书格式提交回执。

## 8. 禁止事项

- 不实现业务逻辑、状态机或 HTTP。
- 不新增 `WorkflowDefinition`、数据库、Spring 注解。
- 不修改 core/web 的代码或 POM。
- 不为了让测试通过而放宽冻结签名。