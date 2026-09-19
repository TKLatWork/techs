# Doc manage
> Version: 1.0.0
> Updated: 2026-09-19

# Purpose
> Rules for creating and managing project docs.
> Applies to: all docs under `doc/` and all `AGENTS.md` files. Audience: agents and humans.

# Rule: Versioning
> Every doc carries a header block: `Version: a.b.c` + `Updated: YYYY-MM-DD`
> Every doc ends with a Changelog table: `Version | Date | Change`, one lean row per version
> Bump the version on every change:

| Bump | When |
|---|---|
| major (a) | restructure, or semantic/rule change |
| minor (b) | added rules/sections |
| patch (c) | wording, typos, fixes |

> Git is the diff source of truth; the changelog is the human summary

# Rule: Template
> Every doc is based on a template from `doc/template/` and follows its layout
> New doc types require a new template, added via a reviewed change - no ad-hoc layouts

| Template | Use for |
|---|---|
| `Doc-template.md` | base layout, general docs |
| `Frontend-feature-spec-template.md` | frontend feature specs |

# Rule: Wording
> Docs serve human review - keep wording/lines lean and clean
> Use lists/tables; group related content
> A doc serves its purpose only - no unrelated or over-detailed info

# Rule: Index
> Any doc create/update touches its row in `doc/AGENTS.md` in the same change
> Index rows carry the doc's current version

# Rule: Scoping
> Each folder's `AGENTS.md` applies to that folder and its sub-folders
> `doc/` rules apply project-wide
> Root `AGENTS.md` is the thin entry: version line + scoping rule + pointer to `doc/AGENTS.md` (changelog exempt)

# Changelog
| Version | Date | Change |
|---|---|---|
| 1.0.0 | 2026-09-19 | Initial rules: versioning, template, wording, index, scoping |
