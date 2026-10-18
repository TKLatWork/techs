---
name: doc-viz
description: 全局可视化规范：信息形态决定表达形式——时序→序列/泳道，状态→有向边，结构→层级，清单→表。chat 与渲染页通用。
---

# doc-viz — 全局可视化规范

> 状态：active v2.0.0 ｜ 日期：2026-10-18 ｜ 维护者：Kilo（用户指令）

**原则：信息形态决定表达形式。** 能画图不列表，能列表不散文。

## Terminology

| 术语 | 定义 |
|---|---|
| 信息形态 | 时序 / 状态 / 结构 / 清单——选图型前先判形态 |
| 渲染页 | view.html / HTML 文档页——零外部依赖，自绘 |
| 泳道图 | 按角色分泳道展开事件——不用盒子 pipeline |

## 1. 原则

1. Markdown优先mermaid, HTML优先mermaid/html图示, 或者可交互HTML示例
2. 每个图应当保持单一视角/主题，避免混合多种信息形态
3. 图示内容太多(>10个单位)时，应当考虑按层级、分组、分页、折叠等方式拆分，拆分通常不超过3个层级。
4. 使用颜色和注脚
   - 颜色：用于区分不同类型的节点或状态，增强可读性。
   - 注脚：用于解释图中符号、缩写或特殊标记，确保图示易于理解。

## 2. 图示参考

### 流程回路

```mermaid
flowchart LR
    subgraph P1["规划阶段"]
        direction LR
        U(("用户")) --> TL1["TeamLead<br/>初始化"]
        TL1 --> P["plan worker<br/>拆解"]
        P --> TL1
    end
    G{{"gate 批准"}}
    subgraph P2["执行阶段"]
        direction LR
        R["research<br/>调研"]
        C["code<br/>实现"]
        R & C --> TL2["TeamLead<br/>汇总"]
    end
    subgraph P3["收尾阶段"]
        direction LR
        TL2 --> I["安装<br/>→ doc/skills"]
    end
    P1 --> G
    G == 通过 ==> R
    G == 通过 ==> C

    classDef ph1 fill:#0d1117,stroke:#d29922,color:#d29922
    classDef ph2 fill:#0d1117,stroke:#58a6ff,color:#58a6ff
    classDef ph3 fill:#0d1117,stroke:#3fb950,color:#3fb950
    class P1 ph1
    class P2 ph2
    class P3 ph3
```
> 注脚：三个 `subgraph` 分阶段——橙色=规划，蓝色=执行，绿色=收尾。gate 从子图内提到子图间作为阶段衔接点。

### 分层调用

```mermaid
flowchart LR
    A["路由层<br/>接收请求"] --> B["鉴权层<br/>验证身份"]
    B --> C["业务层<br/>处理逻辑"]

    class A in
    class B mid
    class C core
    classDef in fill:#0d1117,stroke:#58a6ff,color:#e6edf3
    classDef mid fill:#0d1117,stroke:#d29922,color:#e6edf3
    classDef core fill:#0d1117,stroke:#3fb950,color:#e6edf3
```
> 注脚：蓝色=入口，橙色=中间层，绿色=核心。分层从左往右递进。

#### 子图展开

```mermaid
flowchart LR
    subgraph Auth["鉴权层"]
        direction LR
        T["Token 校验"] --> P["权限查询"]
        P --> C["缓存命中"]
    end
    A["路由层"] --> Auth
    Auth --> B["业务层"]

    class A in
    class T,P,C sub
    class B core
    classDef in fill:#0d1117,stroke:#58a6ff,color:#e6edf3
    classDef sub fill:#0d1117,stroke:#d29922,color:#e6edf3
    classDef core fill:#0d1117,stroke:#3fb950,color:#e6edf3
```
> 注脚：第二张用 `subgraph` 展开鉴权层内部——Token 校验 → 权限查询 → 缓存命中。子图内遵循同样的横向分层节奏，颜色与第一张一致。

### 调用时序

```mermaid
sequenceDiagram
    participant C as 客户端
    participant A as 路由层
    participant B as 鉴权层
    participant S as 业务层
    participant D as 数据层

    C->>A: 请求
    A->>B: 校验身份
    B-->>A: 通过
    A->>S: 转发请求
    S->>D: 查询数据
    D-->>S: 返回结果
    S-->>A: 处理完成
    A-->>C: 响应
```
> 注脚：`->>` 实线调用，`-->>` 虚线返回。自上而下是时间轴，每列一个角色，交互清晰。