# Agent C — workflow-web 子项任务书

> 状态：draft v0.1.0 ｜ 日期：2026-10-22 ｜ 维护者：Kilo（用户指令）

## Terminology

| 术语 | 定义 |
|---|---|
| Agent C | 本子项的唯一执行者，只负责 `workflow-web` |
| 接入层 | Spring Boot REST 模块，调用 API 的 `WorkflowService` 端口 |
| 切片测试 | `@WebMvcTest` + mock `WorkflowService`，不启动完整 core |
| 统一错误响应 | `code` / `message` / `timestamp` 三字段 JSON |
| 真实上下文测试 | 启动完整 Spring 容器并加载 core 的测试，由 Lead 在 Phase 2 执行 |

## 1. 角色与目标

你是 Agent C，负责 `workflow-web`。目标是把 API 端口暴露为 4 个 REST 端点，提供统一错误映射与 Spring Boot 启动配置，并用 Web 切片测试覆盖成功与错误状态码。

## 2. 只允许修改

```text
project/workflow-web/**
```

禁止修改：`project/pom.xml`、`project/workflow-api/**`、`project/workflow-core/**`、`project/README.md`、`doc/**`。

## 3. 输入

1. [../spec.md](../spec.md) §5.4、§7
2. [../task-requirement.md](../task-requirement.md)
3. Phase 0 生成的 `project/workflow-web/` 骨架
4. 已安装的 `workflow-api` 与 `workflow-core` 骨架

## 4. 交付物

包：`com.example.workflow.web`

| 文件 | 内容 |
|---|---|
| `WorkflowWebApplication.java` | `@SpringBootApplication(scanBasePackages = "com.example.workflow")` + `main` |
| `controller/WorkflowController.java` | 4 个端点，注入 `WorkflowService` |
| `error/ApiExceptionHandler.java` | `@RestControllerAdvice`，异常 → HTTP/code |
| `error/ApiError.java` | `record ApiError(String code, String message, Instant timestamp)` |
| `src/main/resources/application.yml` | 端口、应用名、actuator |
| `src/test/java/**` | `@WebMvcTest` 切片测试 |

## 5. 端点要求

| Method | Path | 成功 | 说明 |
|---|---|---|---|
| POST | `/api/workflows` | 201 | 请求体 `StartWorkflowCommand`，返回 `WorkflowInstance` |
| POST | `/api/workflows/{id}/advance` | 200 | 无请求体 |
| GET | `/api/workflows/{id}` | 200 | 无请求体 |
| POST | `/api/workflows/{id}/cancel` | 200 | 无请求体 |

- Controller 只依赖 `com.example.workflow.api.WorkflowService`，不直接依赖 core 实现类。
- `POST /api/workflows` 返回 `ResponseEntity.status(HttpStatus.CREATED).body(...)`。
- 不吞异常；交给 `ApiExceptionHandler`。

## 6. 错误映射

| 异常 | HTTP | code |
|---|---|---|
| `WorkflowValidationException` | 400 | `WORKFLOW_VALIDATION` |
| `WorkflowNotFoundException` | 404 | `WORKFLOW_NOT_FOUND` |
| `WorkflowStateException` | 409 | `WORKFLOW_STATE_CONFLICT` |
| 其他 `WorkflowException` | 500 | `WORKFLOW_INTERNAL` |
| 未捕获 `Exception` | 500 | `INTERNAL_ERROR` |

响应体：

```json
{
  "code": "WORKFLOW_NOT_FOUND",
  "message": "workflow instance not found: <id>",
  "timestamp": "2026-10-22T00:00:00Z"
}
```

`timestamp` 使用 `Instant.now()`；不要泄露堆栈或内部类名。

## 7. 配置要求

`application.yml` 至少包含：

```yaml
spring:
  application:
    name: workflow-web

server:
  port: 8080

management:
  endpoints:
    web:
      exposure:
        include: health,info
```

## 8. 测试要求

使用 `@WebMvcTest(WorkflowController.class)` + 当前 Spring Boot 版本支持的 mock bean 注解（如 `@MockitoBean`；若版本使用 `@MockBean` 则用 `@MockBean`）。不要依赖真实 core 实现。

至少覆盖：

| 场景 | 期望 |
|---|---|
| POST start 成功 | 201 + JSON 字段 |
| POST advance 成功 | 200 |
| GET 成功 | 200 |
| POST cancel 成功 | 200 |
| start 校验失败 | 400 + `WORKFLOW_VALIDATION` |
| get 不存在 | 404 + `WORKFLOW_NOT_FOUND` |
| advance 冲突 | 409 + `WORKFLOW_STATE_CONFLICT` |
| 未捕获异常 | 500 + `INTERNAL_ERROR` |

命令：

```powershell
cd project
mvn -pl workflow-web test
```

说明：本命令使用 Phase 0 安装的 core 骨架；不要因为 core 实现尚未完成而修改 core。完整 `@SpringBootTest` 由 Lead 在 Phase 2 执行。

## 9. 完成标准

- [ ] `mvn -pl workflow-web test` 通过。
- [ ] 4 个端点与 [spec.md](../spec.md) §5.4 一致。
- [ ] 错误响应统一为 `code/message/timestamp`。
- [ ] Controller 只依赖 API 端口。
- [ ] 只修改 `project/workflow-web/**`。
- [ ] 按共享任务书格式提交回执。

## 10. 禁止事项

- 不修改 api/core 的类或 POM。
- 不把业务状态机写进 Controller。
- 不引入数据库、鉴权、Swagger/OpenAPI、模板引擎。
- 不启动真实 core 做切片测试；真实上下文测试留给 Lead。
- 不修改父 POM 或 `doc/**`。