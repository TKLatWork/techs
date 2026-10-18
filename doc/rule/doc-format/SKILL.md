---
name: doc-format
description: 文档格式与必备 section 规范：结构原则（层级/表格优先/正本单一）、必含 Terminology 章节、版本 a.b.c 管理。产出任何 .md 文档时引用。
---

# doc-format — 文档格式规范

> 状态：active v1.1.0 ｜ 日期：2026-10-18 ｜ 维护者：Kilo（用户指令）

依赖技能：`doc/rule/doc-viz/SKILL.md`（图示规范，doc-format 不定义图型规则）。


# 1. 必含 section

- **元信息行**：`> 状态：<draft|active|deprecated> v<a.b.c> ｜ 日期：YYYY-MM-DD ｜ 维护者：<agent/用户>`
- **Terminology**：`## Terminology` 章，表格定义正文关键词（`｜术语｜定义｜`），全仓库定义一致

## Terminology

| 术语 | 定义 |
|---|---|
| 元信息行 | 标题下 `> 状态 ｜ 版本 v a.b.c ｜ 日期 ｜ 维护者` 行——版本唯一落点 |


## 版本 a.b.c

- **唯一落点**：元信息行。历史由 git log 承载，不设变更记录章。
- 在改动完成后增加版本号