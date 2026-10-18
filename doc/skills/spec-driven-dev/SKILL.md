---
name: spec-driven-dev
description: 面向给定 scope 产出可交互的模块可视化文档（spec HTML）。触发条件：制作模块文档、可视化 spec、交互式演示文档时使用。产出零 CDN 单文件 HTML，五 tab 结构（概要/需求/接口契约/测试/架构）。
---

# spec-driven-dev — 模块可视化文档

> 状态：active v1.0.0 ｜ 日期：2026-10-18 ｜ 维护者：Kilo（用户指令）

## Terminology

| 术语 | 定义 |
|---|---|
| spec HTML | 面向一个 scope 的五 tab 可视化文档（零 CDN 单文件），覆盖概要/需求/接口契约/测试/架构 |
| 模板 | `template/spec-driven-dev-template.html`，占位骨架，直接复制填入内容即可 |

## 工作流

1. 确定 scope 与目标读者
2. 复制 `template/spec-driven-dev-template.html`，修改 `<title>` 和 `<h1>`
3. 按五 tab 逐一填充：概要 → 需求 → 接口/契约 → 测试 → 架构
4. 用表格/SVG 替代占位符；细则用 `<details>` 折叠
5. 浏览器直接打开验收：tab 切换正常、零 CDN 依赖

## 依赖

- `doc/rule/doc-viz/SKILL.md` — 图型选择（信息形态→表达形式）、颜色/注脚规范
- `doc/rule/doc-format/SKILL.md` — Terminology、元信息行约定