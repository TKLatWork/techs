---
name: spec-driven-dev
description: 面向给定 scope 产出可交互的模块可视化文档（spec HTML）。触发条件：制作模块文档、可视化 spec、交互式演示文档时使用；按 SDD spec 分步实现模块/Contract 先行实现时亦使用。产出零 CDN 单文件 HTML，五 tab 结构（概要/需求/接口契约/测试/架构）。
---

# spec-driven-dev — 模块可视化文档

> 状态：active v1.1.0 ｜ 日期：2026-10-18 ｜ 维护者：Kilo（用户指令）

## Terminology

| 术语 | 定义 |
|---|---|
| spec HTML | 面向一个 scope 的五 tab 可视化文档（零 CDN 单文件），覆盖概要/需求/接口契约/测试/架构 |
| 模板 | `template/spec-driven-dev-template.html`，占位骨架，直接复制填入内容即可 |
| impl-guide | 分步实现指南（skill 内资源 `impl-guide.md`）：定义 Contract 先行的四段主干工作流（起点为 spec-doc：Contract imple → Contract review → Full impl → Final review）；Contract 指 API定义/Interface/Abstract class/Model/test-case 这类切面契约 |

## 工作流

1. 确定 scope 与目标读者
2. 复制 `template/spec-driven-dev-template.html`，修改 `<title>` 和 `<h1>`
3. 按五 tab 逐一填充：概要 → 需求 → 接口/契约 → 测试 → 架构
4. 用表格/SVG 替代占位符；细则用 `<details>` 折叠
5. 浏览器直接打开验收：tab 切换正常、零 CDN 依赖

## 分步实现

按 **Contract 先行**四段主干分步实现模块，核心要求：**按当前的 spec 实现 Contract**。起点为 `doc/spec-doc/` 下的 **spec-doc**（source-of-truth）：`Contract imple（按当前 spec 提取 Contract——API定义/Interface/Abstract class/Model/test-case 这类切面契约——并落实 R1 Contract/实现分离且与 Impl 保持隔离（如分开在不同子包）、R2 Contract 带 version meta 并与 spec 一致、R3 公共方法提供主要流程注释引导实现）→ Contract review → Full impl（按 Contract 分步实现）→ Final review（终验）`；两个 review 段（Contract review / Final review）性质即验收，结构固定为「自动验收（仅限测试、spec/contract 对照两类）→ manual approve（人工明确批准）」。细则见 skill 内 `impl-guide.md`。

## 依赖

- `doc/rule/doc-viz/SKILL.md` — 图型选择（信息形态→表达形式）、颜色/注脚规范
- `doc/rule/doc-format/SKILL.md` — Terminology、元信息行约定
- `impl-guide.md` — 分步实现指南（Contract 先行四段工作流、R1/R2/R3），skill 内资源（相对路径）
