# Design: add-doc-management-rules

## Context

See proposal.md — Why. Current state shaping the approach:

- Doc tree is empty: root `AGENTS.md` exists but is empty; no `doc/` folder
- Prior experiment recoverable from git: `HEAD~1:doc/spec-driven/AGENTS.md` (terminology, versioning, wording rules) and `HEAD~1:doc/spec-driven/Frontend-feature-spec-template.md`
- OpenSpec initialized; `openspec/config.yaml` has empty `context:`/`rules:`
- Docs are consumed by humans (review) and agents (constraints via AGENTS.md scoping)

## Goals / Non-Goals

**Goals:**
- Self-contained doc system (rules + templates + entry/index) usable immediately by the next doc author, human or agent
- Rules written so an agent can verify compliance mechanically (header present, template sections present, index updated)

**Non-Goals:**
- Project content docs (project-basic, coding rules) — scope A, written later under these rules
- CI/tooling enforcement of doc rules (possible follow-up)
- OpenSpec spec baseline for existing code (separate future change)

## Decisions

1. **Location map** — rules and templates live in the doc tree they govern; entry files stay thin pointers.

   | Content | Location |
   |---|---|
   | Doc-management rules | `doc/rules/Doc-manage.md` |
   | Templates | `doc/template/Doc-template.md`, `doc/template/Frontend-feature-spec-template.md` |
   | Agent entry (scoping + pointer) | root `AGENTS.md` |
   | Doc index (rules/templates/living docs + versions) | `doc/AGENTS.md` |
   | OpenSpec distribution | one `context:` pointer line in `openspec/config.yaml` |

   Alternatives rejected: rules inline in root `AGENTS.md` (bloats the entry every agent loads; harder to version on its own); rules only in `openspec/config.yaml` (reaches OpenSpec workflows only, not general agent sessions).

2. **Versioning scheme** — every doc gets a header block (`Version: a.b.c`, `Updated: YYYY-MM-DD`) and a bottom changelog table (`Version | Date | Change`). Bump semantics: major = restructure or semantic/rule change; minor = added rules/sections; patch = wording, typos, fixes. Git remains the diff source of truth; the in-doc changelog is the human summary. Alternative rejected: git-only versioning (no visible version to reference in review or index).

3. **Template structure** — base template defines the mandatory skeleton: header block → Purpose → content sections → Changelog. Type templates extend the base with domain sections. `Frontend-feature-spec-template.md` restored from git history and adapted: Summary, Terminology, Biz requirements, Technical/Implement notes, Testing (UT-xx / SIT-xx case blocks). New doc types get new templates via reviewed change, not ad-hoc layouts.

4. **Scoping model** — restore the prior project rule: each folder's `AGENTS.md` applies to that folder and sub-folders; `doc/` rules apply project-wide; root `AGENTS.md` is the entry that points to the doc index. Agents discover rules top-down without loading everything.

5. **Rule style** — `>` quote-block rule statements plus tables/lists, matching the project's established doc style and the lean-wording rule itself.

6. **Dogfooding as acceptance** — `Doc-manage.md` and both templates carry the version header/changelog and follow the base layout; if the rules can't describe themselves cleanly, they're wrong.

## Risks / Trade-offs

- [Rules are advisory; nothing fails on violation] → write rules as mechanically checkable statements (agent can self-verify); CI lint deferred as non-goal
- [Version/changelog upkeep per edit] → patch bumps are one table row; keep semantics in git
- [Templates too rigid for unusual docs] → base template is minimal; rules allow new type templates via reviewed change
- [`doc/AGENTS.md` index drifts from actual files] → rule: any doc create/update must touch its index row in the same change

## Migration Plan

Greenfield doc tree — no migration. Rollback = delete `doc/`, empty root `AGENTS.md`, remove the `context:` line.
