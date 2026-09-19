# Proposal: expand-doc-diagram-rule

## Why

`doc/rules/Doc-diagram.md` only covers Mermaid diagrams, but project docs also need a standard for table-style representation (progress tracking, status matrices, comparisons) and for presenting changes/diffs (text diff vs diagram diff). Today there is no rule for when to use a table vs a diagram, no standard table or diff format, and OpenSpec artifacts (proposal/specs/tasks) are not wired to any of it - so doc authors (human or agent) improvise. Renaming and expanding the rule doc, then pointing OpenSpec config at it, closes that gap while keeping diagrams as the primary visual tool.

## What Changes

- Rename `doc/rules/Doc-diagram.md` → `doc/rules/Doc-represent.md`; scope widens from "diagrams only" to "doc representation" (diagrams + tables)
- Keep all existing Mermaid diagram rules unchanged (format, standard set: component / data flow / sequence / state)
- Add a "Rule: Representation choice" section: when to use a diagram vs a table vs a list
- Add a "Rule: Table format" section: standard Markdown table conventions (header row, lean cells, one topic per table)
- Add standard table set, including a **progress-tracking table** (item / status / notes) as a canonical pattern for tracking items in docs and change artifacts
- Add a "Rule: Change/diff representation" section with two standard forms and examples: **text-based diff** (unified diff in fenced ```` ```diff ```` blocks) for text/code changes, and **diagram diff** (Mermaid with added/removed/changed highlighting, or a before/after pair) for diagram changes
- Update `openspec/config.yaml`: reference `doc/rules/Doc-represent.md` in the project `context`, and add per-artifact `rules` so generated proposal/specs/tasks artifacts apply the standard tables, diagrams, and diff forms
- Bump doc version 1.0.0 → 2.0.0 (rename + scope change = major per `Doc-manage` versioning rule)
- Update the doc row in `doc/AGENTS.md` index (name + version) in the same change, per the index rule

## Capabilities

### New Capabilities

- `doc-representation`: Standards for how project docs represent information - Mermaid diagrams for structure/flow/state, Markdown tables for tracking/comparison/status, standard forms for presenting changes (text diff, diagram diff), the rules for choosing between them, and OpenSpec config wired to apply them.

### Modified Capabilities

(none - no existing specs)

## Impact

- **Docs**: `doc/rules/Doc-diagram.md` renamed/expanded; `doc/AGENTS.md` index row updated
- **Config**: `openspec/config.yaml` - context pointer + per-artifact rules for proposal/specs/tasks
- **Code/APIs/dependencies**: none
- **Consumers**: agents and humans writing docs or OpenSpec artifacts - future docs/artifacts gain table and diff-format guidance; existing diagram guidance unchanged
- **Assumptions recorded**: new file name `Doc-represent.md` follows the existing `Doc-<topic>.md` convention; version bump to 2.0.0 per the major-bump rule (restructure + semantic change)
