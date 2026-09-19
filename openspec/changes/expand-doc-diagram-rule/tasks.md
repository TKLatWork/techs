# Tasks: expand-doc-diagram-rule

(design.md deliberately skipped - see status note; no open questions)

## 1. Rename the rule doc

- [ ] 1.1 Rename `doc/rules/Doc-diagram.md` to `doc/rules/Doc-represent.md` (use `git mv` to preserve history) and verify the old path no longer exists
- [ ] 1.2 Update the doc header in `Doc-represent.md` to `Version: 2.0.0` + current date, and verify the major bump matches the `Doc-manage` versioning rule (restructure + rule change)

## 2. Expand the rule doc content

- [ ] 2.1 Update `# Purpose` to cover doc representation (diagrams + tables), keeping the existing applies-to line; verify the section states both scopes
- [ ] 2.2 Keep the existing `# Rule: Format` (Mermaid) and `# Standard diagram set` sections unchanged; verify all 4 Mermaid blocks still render (VS Code preview)
- [ ] 2.3 Add `# Rule: Representation choice` (diagram = structure/flow/state, table = tracking/comparison/status of parallel items, list = short points); verify it maps to the spec scenario "Author chooses a format"
- [ ] 2.4 Add `# Rule: Table format` (Markdown tables, header row, lean cells, one topic per table, real item names); verify wording is lean per `Doc-manage` wording rule
- [ ] 2.5 Add `# Standard table set` with the progress-tracking table (`Item | Status | Notes`) plus index-table and changelog-table examples; verify each example renders as a valid Markdown table
- [ ] 2.6 Add `# Rule: Change/diff representation` with two worked examples: a fenced ```` ```diff ```` block (text/code changes) and a diagram diff (Mermaid `classDef` highlight of added/removed/changed, noting before/after pair as the fallback); verify both examples render in VS Code preview
- [ ] 2.7 Add a 2.0.0 changelog row; verify the changelog keeps one lean row per version

## 3. Wire OpenSpec config

- [ ] 3.1 Update `openspec/config.yaml` project `context` to reference `doc/rules/Doc-represent.md` as the representation standard; verify via `openspec instructions proposal --change expand-doc-diagram-rule --json` that the context field includes it
- [ ] 3.2 Add per-artifact `rules` in `openspec/config.yaml` for proposal/specs/tasks (standard tables for tracking items, Mermaid for structure/flow/state, standard text/diagram diff forms when presenting changes); verify via `openspec instructions <id> --change expand-doc-diagram-rule --json` that each artifact's rules field carries them

## 4. Update references

- [ ] 4.1 Update the rules row in `doc/AGENTS.md` (Doc-diagram → Doc-represent, version 2.0.0, topic updated) and bump the index's own version/date; verify per the `Doc-manage` index rule that row version matches the doc
- [ ] 4.2 Search the repo for `Doc-diagram` and update any references outside changelog history; verify zero remaining hits outside changelog entries

## 5. Final validation

- [ ] 5.1 Run `openspec validate expand-doc-diagram-rule --strict` and verify it passes
