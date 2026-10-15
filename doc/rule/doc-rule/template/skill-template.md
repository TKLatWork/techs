---
name: <kebab-case-skill-name>
description: <这个 skill 做什么 + 什么时候使用（触发条件）+ 依赖关系。写清楚，宿主 agent 靠它决定是否加载。>
---

# <skill-name> — <一句话定位>

依赖技能：<无 | skill-a（依赖点说明）>。
执行者：<哪些 agent / 角色使用本 skill>。

## 本 skill 内容

| 资源 | 说明 |
|---|---|
| [xxx.md](xxx.md) | <正本/规则/流程文档> |
| [assets/](assets/) | <模板、静态资源（可选）> |

## 流程 / 规则

<按步骤（### 0. / ### 1. …）或规则条目写；命令给完整可复制形式；每步写清产物与校验点。>

## 失败处理

<已知失败场景 → 处置方式；无法处置时如何上报。>

## 相关文档

<跨 skill 引用用仓库相对路径；公共文档指向 doc/skills/chat-team/。>

<!--
新建 skill 检查清单（提交前删除本注释）：
1. 目录名 = frontmatter name（kebab-case）
2. 资源全部在本目录内，跨目录用仓库相对路径
3. 已更新 doc/skills/AGENTS.md「当前 skill 清单」
4. 已更新 chat-team/requirements.md §8 与 chat-team/design.md §2 目录树
5. 依赖的 skill 已在开头显式声明
-->
