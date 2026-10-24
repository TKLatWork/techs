# workflow-web

> 状态：draft v0.1.0 ｜ 日期：2026-10-22 ｜ 维护者：Kilo（用户指令）

## Terminology

| 术语 | 定义 |
|---|---|
| workflow-api | 契约模块：DTO、`WorkflowService` 端口、异常、状态枚举 |
| workflow-core | 领域与执行模块：状态转换、服务实现、内存仓库 |
| workflow-web | Spring Boot 接入模块：REST、错误映射、启动配置 |
| 模块自测 | `mvn -pl <module> test`，只构建当前模块 |

## 模块

| 模块 | 作用 | 依赖 |
|---|---|---|
| `workflow-api` | 纯 Java 契约 | 无 |
| `workflow-core` | 工作流引擎与内存仓库 | workflow-api、spring-context |
| `workflow-web` | Spring Boot REST 应用 | workflow-core、starter-web、starter-validation、starter-actuator |

依赖方向：`workflow-web → workflow-core → workflow-api`。

## 构建

```powershell
mvn clean verify   # 只验证
mvn clean install  # 验证并安装到本地仓库，供 web 模块单独启动
```

## 运行

```powershell
mvn -pl workflow-web spring-boot:run
```

如果刚改过 `workflow-api` / `workflow-core`，先执行 `mvn clean install`，否则 `spring-boot:run` 可能使用本地仓库里的旧 jar。

默认端口：`8080`。

## API

| Method | Path | 说明 |
|---|---|---|
| POST | `/api/workflows` | 启动工作流，201 |
| POST | `/api/workflows/{id}/advance` | 推进一步，200 |
| GET | `/api/workflows/{id}` | 查询实例，200 |
| POST | `/api/workflows/{id}/cancel` | 取消实例，200 |
| GET | `/actuator/health` | 健康检查 |

首版使用内存仓库，进程重启后实例丢失；持久化通过 `WorkflowRepository` 端口后续替换。