# Proposal: make-doc-represent-global

## Why

`doc/rules/Doc-represent.md` 2.0.0 is scoped to "docs", and its representation choice is passive guidance. Observed failure (2026-09-19, recent-changes report chat): the report was written outside `doc/` to dodge governance, contained no diagram/diagram-diff despite a rename chain that is exactly the diagram-diff case, and chat responses are outside the rule's scope entirely. The standards are fine - the activation path is missing. Raising Doc-represent to a global rule (any Markdown artifact at any path + chat responses) with mandatory triggers closes the gap.

## What Changes

- **BREAKING (scope)**: widen Doc-represent `Applies to` from docs to global - every Markdown file created at any path in the repo (docs, change artifacts, ad-hoc reports/reviews/summaries) **and** agent chat responses
- Add `# Rule: Required forms`: the representation choice becomes a requirement with triggers - 3+ parallel items with per-item state -> table; structure/flow/state content -> diagram; any presented set of changes -> diff form; added/removed/renamed elements -> diagram diff or before/after pair; prose-only output matching a trigger = rule violation
- Add `# Rule: Delivery check`: before delivering any Markdown artifact or chat response, verify triggered forms are present, tables have header rows, changes are diffs not prose, Mermaid blocks render
- Add chat carve-out: chat responses follow representation rules only - versioning/changelog/template/index rules stay scoped to `doc/` per `Doc-manage`
- Root `AGENTS.md`: add the global activation line so the rule fires on any chat request producing Markdown, not just docs
- `openspec/config.yaml` `context`: update the Doc-represent pointer wording to global scope (docs, artifacts, chat)
- Bump Doc-represent 2.0.0 -> 3.0.0 (scope/semantic change = major per `Doc-manage` versioning rule); update the `doc/AGENTS.md` row and bump the index 1.2.0 -> 1.3.0 in the same change, per the index rule

## Capabilities

### New Capabilities

(none)

### Modified Capabilities

- `doc-representation`: the applicability requirement changes - the standards and the choice rule move from doc-scoped guidance to a global requirement covering all Markdown artifacts and chat responses, with mandatory form triggers and a delivery check

## Impact

- **Docs**: `doc/rules/Doc-represent.md` 3.0.0 (applies-to + 2 new rules + chat carve-out); root `AGENTS.md` (one activation line); `doc/AGENTS.md` 1.3.0 (row version + changelog)
- **Config**: `openspec/config.yaml` - context pointer wording only; per-artifact rules unchanged
- **Code/APIs/dependencies**: none
- **Consumers**: agents first - every chat response and Markdown artifact gains the required-forms triggers; humans reviewing docs get consistent visuals/diffs
- **Dependency/assumption**: the `doc-representation` capability spec lives in the unarchived change `expand-doc-diagram-rule`; that change must be archived (specs synced) before this one, so the delta applies on top of it
