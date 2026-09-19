# Report: Recent 3 Changes
> Date: 2026-09-19 | Source: `openspec/changes/` + git history

## Purpose

Summary of the 3 most recent OpenSpec changes in this repo, oldest to newest. All three landed on 2026-09-19 and together built the project's rule-driven doc system.

## Status overview

| Change | Status | Archived | Tasks |
|---|---|---|---|
| add-doc-management-rules | done | 2026-09-19 | 6/6 |
| add-diagram-standards | done | 2026-09-19 | 3/3 |
| expand-doc-diagram-rule | implemented, not yet archived | - | 12/12 |

## 1. add-doc-management-rules (archived)

**Why:** the doc system was wiped (commit `df46548`) to be rebuilt rule-driven; rules had to exist before any new doc content, or docs would drift (no versions, inconsistent layout, bloated wording).

**What changed:**
- Added `doc/rules/Doc-manage.md` 1.0.0 — versioning rule (`a.b.c` + Updated date + changelog; major/minor/patch semantics), template rule, wording rules (lean lines, lists/tables, purpose-only), index rule, scoping rule.
- Added `doc/template/Doc-template.md` (base layout) and `doc/template/Frontend-feature-spec-template.md` (restored from git history, adapted to base layout).
- Re-seeded root `AGENTS.md` (thin entry: scoping + pointer) and created `doc/AGENTS.md` index 1.0.0.
- Added a `context:` pointer line in `openspec/config.yaml` so OpenSpec artifacts follow the doc rules.

**Commits:** `4af5256` (implementation), `1c4b1ae` (archive).

## 2. add-diagram-standards (archived)

**Why:** docs describing components/architecture had no visual standard; every author improvised diagrams or used prose alone.

**What changed:**
- Added `doc/rules/Doc-diagram.md` 1.0.0 — Mermaid as the only format, in fenced ```` ```mermaid ```` blocks; usage rules (required for component/architecture/flow/state docs, one topic per diagram, lean node count, diagrams version with their doc).
- Standard diagram set with copy-ready examples grounded in the real modules (app-server / service-timezone / app-web): component structure (flowchart + subgraphs), data/request flow (flowchart LR), sequence, state.
- `doc/AGENTS.md` index bumped 1.0.0 → 1.1.0 with the new row.

**Commits:** `74bea5a` (planning), `0a4c719` (implementation), `1c4b1ae` (archive).

## 3. expand-doc-diagram-rule (active - all tasks complete, pending archive)

**Why:** `Doc-diagram.md` covered Mermaid only; no rule existed for tables (progress tracking, status matrices, comparisons) or for presenting changes/diffs, and OpenSpec artifacts were not wired to any of it.

**What changed:**
- Renamed `doc/rules/Doc-diagram.md` → `doc/rules/Doc-represent.md`; scope widened from "diagrams only" to doc representation (diagrams + tables + diffs); version 1.0.0 → 2.0.0 (major per the versioning rule).
- All existing Mermaid rules kept unchanged.
- Added: representation-choice rule (diagram vs table vs list), table-format rule, standard table set (progress-tracking `Item | Status | Notes`, index, changelog), change/diff representation rule (fenced ```` ```diff ```` blocks for text/code; Mermaid `classDef` highlight or before/after pair for diagrams).
- Wired `openspec/config.yaml`: context now points at `Doc-represent.md`; per-artifact `rules` added for proposal/specs/tasks.
- `doc/AGENTS.md` index bumped to 1.2.0 (row renamed, version 2.0.0); zero remaining `Doc-diagram` references outside changelogs.
- Passed `openspec validate expand-doc-diagram-rule --strict`.

**Commits:** `f96e392` (planning), `139fcb5` (implementation).

## Net effect

```diff
 doc/
+  AGENTS.md                      (index, now 1.2.0)
+  rules/Doc-manage.md            1.0.0
+  rules/Doc-represent.md         2.0.0  (was Doc-diagram.md 1.0.0)
+  template/Doc-template.md       1.0.0
+  template/Frontend-feature-spec-template.md  1.0.0
 AGENTS.md                        (thin entry point)
 openspec/config.yaml             (+ context pointer, + per-artifact rules)
```

The repo now has a complete doc governance chain: management rules → representation standards → templates → index, with OpenSpec config wired so future change artifacts follow the same standards. No code, API, build, or dependency changes were involved in any of the three.

## Next step

`expand-doc-diagram-rule` has 12/12 tasks complete and strict validation passing — it is ready to archive.
