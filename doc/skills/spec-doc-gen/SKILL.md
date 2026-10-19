---
name: spec-doc-gen
description: 给定代码 scope 生成 SDD 文档页与交付状态页：五 tab spec-doc-template（spec-doc.json 驱动）+ 模块 × 阶段 process-status-template（status.json 驱动）。只生成 SDD 文档与状态页，不实现、不拥有 hub；单一入口由 hub skill 聚合。
---

# spec-doc-gen — 代码 → SDD 文档页 + 状态页

> 状态：active v1.3.0 ｜ 日期：2026-10-19 ｜ 维护者：Kilo（用户指令）

## Terminology

| 术语 | 定义 |
|---|---|
| scope | 本次生成覆盖的代码范围：目录或文件 glob + 排除项 |
| SDD spec | 五 tab 单文件 HTML：概要 / 需求 / 对外接口 · 契约 / 测试 / 实现设计 · 架构 |
| 文档页 | `spec-doc-template.html`，由 `spec-doc.json` 驱动 |
| 状态页 | `process-status-template.html`，由 `status.json` 驱动 |
| 锚点 | 文档结论对应的代码位置 `path:line`；无锚点的内容只能标 TODO |
| 基线 | 本次生成所依据的 git commit；记入文档元信息 |

## 输入

| 项 | 必填 | 说明 |
|---|---|---|
| scope | 是 | 目录或 glob，附排除项（vendor、build、node_modules、生成物、fixture） |
| 目标读者 | 是 | 决定概要/需求的详略与术语密度 |
| 输出路径 | 否 | 默认 `doc/spec-changes/<spec>/delivery/` |
| 基线 | 否 | 默认当前 HEAD 的 commit hash |

## 产物

| 文件 | 作用 |
|---|---|
| `template/spec-doc-template.html` | 模块文档页：五 tab 单文件文档 |
| `template/spec-doc.json` | 文档页数据源；支持 `?data=` 指向模块专属 JSON |
| `template/process-status-template.html` | 状态页：模块 × 阶段矩阵、下游锥、retry log |
| `template/status.json` | 状态页数据源 |

> 单一入口 hub（SDD + team）不属于本 skill，见 `doc/skills/hub/SKILL.md`。

## 工作流

1. **解析 scope**：列文件、识别语言与包结构、定位入口/注册点/公共导出；排除非目标代码。
2. **采集事实**：只读代码与测试，按五 tab 证据映射收集内容。
3. **生成 `spec-doc.json`**：填 `meta` 与 `sections`；每条结论带 `path:line` 锚点。
4. **生成 `status.json`**：建模块/子模块 × 需求/Contract/实现/验证矩阵，填 cells、edges、retries。
5. **复制页面与 JSON**：把 `spec-doc-template.html` / `process-status-template.html` 与两份 JSON 复制到输出目录；模块多时用 `?data=modules/<id>.json` 复用文档页。
6. **本地 HTTP 验收**：文档五 tab、状态矩阵、下游锥、retry log 正常。
7. **交付**：先写 `doc/spec-changes/<spec>/delivery/`；人工审查通过后，由维护动作迁入 `doc/spec-doc/`。

## 两页与数据源

| 页面 | 数据源 | 作用 |
|---|---|---|
| `spec-doc-template.html` | `spec-doc.json`（默认，可用 `?data=` 覆盖） | 五 tab 模块文档 |
| `process-status-template.html` | `status.json`（默认，可用 `?data=` 覆盖） | 矩阵状态、下游锥、retry log |

两页都不内嵌状态数据；页面只负责渲染，JSON 是唯一数据源。对外入口由 `hub` skill 聚合。

## JSON 数据契约

### spec-doc.json

| 字段 | 说明 |
|---|---|
| `meta` | `title`、`scope`、`version`、`date`、`maintainer`、`baseline` |
| `sections` | 以 section id 为键的内容块；缺失时保留模板默认占位 |

section 类型：

| type | 字段 | 渲染 |
|---|---|---|
| `placeholder` | `content` | 虚线占位块（允许内联 HTML） |
| `text` | `content` | 保留换行的纯文本 |
| `html` | `content` | 原始 HTML |
| `list` | `items[]` | 无序列表 |
| `table` | `headers[]`、`rows[][]` | 表格 |

固定 section id：`overview.summary`、`overview.diagram`、`requirement.terminology`、`requirement.scenarios`、`requirement.behavior`、`requirement.details`、`contract.api`、`contract.data`、`test.cases`、`test.environment`、`test.fixtures`、`design.architecture`、`design.submodules`、`design.decisions`。

### status.json

| 字段 | 说明 |
|---|---|
| `module` / `version` / `baseline` / `updated` | 模块与版本元信息 |
| `stages[]` | 列定义：`id`、`label` |
| `modules[]` | 行定义：`id`、`name`、`parent`、`depth`、`path` |
| `cells[]` | 每格：`module`、`stage`、`status`、`version`、`inputs`、`updated`、`retries`、`checkpoint`、`note` |
| `edges[]` | Contract 依赖：`from`（提供方）、`to`（消费方）、`type`、`note` |
| `retries[]` | 重试记录：`cell`、`at`、`reason`、`fromVersion`、`toVersion` |

## 五 tab 证据映射

| tab | 必含内容 | 证据来源 |
|---|---|---|
| 模块概要 | 职责、边界、入口、关键依赖 | 入口文件、包声明、路由/注册点、构建配置 |
| 需求 | Terminology、用户场景、行为契约、边界与错误 | 测试、调用方、错误分支、配置项 |
| 对外接口 · 契约 | API 签名、数据契约、错误码/状态枚举 | public 声明、类型定义、schema、序列化代码 |
| 测试 | 用例表、测试环境、预设场景数据 | 测试文件、fixture、CI 配置 |
| 实现设计 · 架构 | 架构图、子模块、技术决策 | import 图、目录结构、ADR/关键注释 |

## 本地打开

`file://` 下 `fetch` 会被拦截；页面会显示错误与启动命令。使用 HTTP：

```powershell
npx -y http-server doc\spec-changes\<spec>\delivery -p 8788 -c-1
# → http://127.0.0.1:8788/spec-doc-template.html
# → http://127.0.0.1:8788/process-status-template.html
```

## 硬规则

1. **只读代码**：生成过程不得修改 scope 内任何文件。
2. **不编造**：代码与测试中不存在的行为写 `TODO`，不得写成确定结论。
3. **可追溯**：每条事实带 `path:line`；无锚点内容只能进入「待确认」。
4. **JSON 单一数据源**：两页各自只读 `spec-doc.json` / `status.json`；不得在 HTML 内嵌状态数据。
5. **不拥有 hub**：单一入口由 `hub` skill 聚合；本 skill 只提供两页与 JSON。
6. **零 CDN**：不得引入外部脚本、样式、字体或图片 URL。
7. **模板单一正本**：两页模板以本 skill `template/` 为准；使用方修改后必须同步回模板。
8. **落点受控**：默认写 change 的 `delivery/`；不得直接覆盖 `doc/spec-doc/` 的 source-of-truth。

## 输出自检

- [ ] `spec-doc.json` / `status.json` 均可解析
- [ ] 文档页五 tab 与 section 由 `spec-doc.json` 填充
- [ ] 状态页矩阵、下游锥、retry log 正常
- [ ] 两页均无内嵌状态数据、无外部 URL
- [ ] 通过 HTTP 打开无加载错误

## 依赖

- `doc/rule/doc-viz/SKILL.md` — 图型选择、颜色与注脚
- `doc/rule/doc-format/SKILL.md` — 元信息行、Terminology、版本 a.b.c
- `template/spec-doc-template.html` + `spec-doc.json` — 模块文档页
- `template/process-status-template.html` + `status.json` — 状态页