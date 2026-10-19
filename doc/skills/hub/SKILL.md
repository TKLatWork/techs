---
name: hub
description: 单一对外入口 hub：NavTabs 聚合 SDD 交付（交付状态 / 模块树 / 模块文档）与 Team 执行（状态盘）。由 hub.json 驱动，引用 spec-doc-gen 与 team 的页面；只做入口与导航，不定义交付或执行规则。
---

# hub — SDD + Team 单一入口

> 状态：active v1.0.0 ｜ 日期：2026-10-19 ｜ 维护者：Kilo（用户指令）

## Terminology

| 术语 | 定义 |
|---|---|
| hub | 单一对外入口：一个 NavTabs 页面聚合 SDD 与 team |
| SDD 交付 | `spec-doc-gen` 的交付状态页与模块文档页 |
| Team 执行 | `team/board` 的执行状态盘 |
| 组件 | hub 通过 iframe 引用的页面；hub 不复制其规则 |
| hub.json | 入口数据：标题、组件路径、模块树 |

## 目录

| 文件 | 作用 |
|---|---|
| `template/hub-template.html` | 唯一入口页；NavTabs + 模块树 + 动态模块文档 tab + Team 执行 tab |
| `template/hub.json` | hub 数据源 |

## Tab 结构

| Tab | 内容 | 数据源 |
|---|---|---|
| SDD 交付状态 | 模块 × 阶段矩阵、下游锥、retry log | `components.status` → `process-status-template.html` → `status.json` |
| SDD 模块树 | 从左到右的模块树；点击节点开模块文档 tab | `hub.json.modules` + `components.moduleDoc` |
| 模块文档（动态） | 单个模块的五 tab 文档 | `components.moduleDoc` → `spec-doc-template.html` → `spec-doc.json` |
| Team 执行 | 任务/消息/agent 状态盘 | `components.teamBoard` → `board/view.html` → `board/state.json` |

## hub.json

| 字段 | 说明 |
|---|---|
| `title` / `version` / `baseline` / `updated` | 入口元信息 |
| `components.status` | SDD 交付状态页路径 |
| `components.moduleDoc` | 模块文档页路径（可带 `?data=`） |
| `components.teamBoard` | team 执行状态盘 `view.html` 路径（其自身读取 `board/state.json`） |
| `modules[]` | `id`、`name`、`parent`、`depth`、`status`、`doc`、`summary` |

- 所有 `components` / `doc` 路径均相对 `hub.json` 所在目录。
- `modules[].doc` 可写 `../delivery/spec-doc-template.html?data=../delivery/modules/<id>.json`，同一模板服务多个模块。
- 模块树状态是摘要，必须与 `status.json` 的矩阵状态同步。
- team 的执行状态来自 `board/state.json`；hub 不复制 task 状态。

## 推荐目录

```text
doc/spec-changes/<spec>/
├── hub/
│   ├── hub-template.html
│   └── hub.json
├── delivery/
│   ├── process-status-template.html
│   ├── spec-doc-template.html
│   ├── status.json
│   ├── spec-doc.json
│   └── modules/<module>.json
└── board/
    ├── state.json
    ├── view.html
    ├── tasks/
    └── messages/
```

`hub.json` 的组件路径按实际落点填写，例如：

```json
{
  "components": {
    "status": "../delivery/process-status-template.html",
    "moduleDoc": "../delivery/spec-doc-template.html",
    "teamBoard": "../board/view.html"
  }
}
```

## 启动

```powershell
npx -y http-server doc\spec-changes\<spec> -p 8788 -c-1
# → http://127.0.0.1:8788/hub/hub-template.html
```

`file://` 下 `fetch`/iframe 受限，hub 会显示加载错误；统一走 HTTP。

## 硬规则

1. **唯一入口**：对外只暴露 hub；SDD 状态页、模块文档、team board 都是组件。
2. **不复制规则**：hub 不定义 cell/task 状态机，只引用组件页面。
3. **JSON 驱动**：hub 只读 `hub.json`；不得内嵌模块树/组件数据。
4. **路径可配置**：组件路径写在 `hub.json.components`，不硬编码在渲染逻辑。
5. **零 CDN**：不引入外部脚本、样式、字体或图片。
6. **修改同步**：页面改动同步回本 skill 的模板正本。

## 依赖

- `doc/skills/spec-doc-gen/SKILL.md` — SDD 状态页与模块文档页
- `doc/skills/spec-driven-dev/SKILL.md` — SDD 交付状态与重试规则
- `doc/skills/team/SKILL.md` — team 执行器与 board
- `doc/rule/doc-format/SKILL.md` — 文档格式
- `doc/rule/doc-viz/SKILL.md` — 可视化规范