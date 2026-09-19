# Tasks: make-doc-represent-global

(design.md deliberately skipped - doc rule edit, no architecture/dependency/ambiguity; no open questions)

Precondition: archive `expand-doc-diagram-rule` first (its `doc-representation` spec must be the main spec this delta stacks on).

## 1. Widen scope in Doc-represent

- [ ] 1.1 In `doc/rules/Doc-represent.md`, update the `Applies to` line in `# Purpose`: every Markdown file at any repo path (governed docs, change artifacts, ad-hoc reports/reviews/summaries) and agent chat responses; add the chat carve-out line (chat follows representation rules only - no version header/changelog/template/index). Verify: the section states both scopes and the carve-out explicitly.
- [ ] 1.2 Bump the header to `Version: 3.0.0` + current date. Verify: major bump matches the `Doc-manage` versioning rule (semantic/scope change of every rule).

## 2. Add mandatory-form rules

- [ ] 2.1 Add `# Rule: Required forms` after `# Rule: Representation choice`: the four triggers (3+ parallel items with state -> table; structure/flow/state -> diagram; presented change set -> diff; added/removed/renamed elements -> diagram diff or before/after pair) and the violation line (prose-only output matching a trigger = rule violation). Verify: wording maps to the spec scenarios "Triggered form is mandatory" and "Prose-only output is a violation".
- [ ] 2.2 Add `# Rule: Delivery check`: before delivering any Markdown artifact or chat response - triggered forms present, tables have header rows, changes are diffs not prose, Mermaid blocks render. Verify: it covers both files and chat per the spec.
- [ ] 2.3 Add a 3.0.0 changelog row. Verify: one lean row per version is kept.

## 3. Wire activation paths

- [ ] 3.1 Root `AGENTS.md`: add one line - creating any Markdown file at any path, and any chat response, applies `doc/rules/Doc-represent.md` required forms + delivery check. Verify: root AGENTS.md stays a thin entry (~under 12 lines, no doc content).
- [ ] 3.2 `openspec/config.yaml` `context`: update the Doc-represent pointer wording to global scope (all Markdown artifacts + chat). Verify via `openspec instructions proposal --change make-doc-represent-global --json` that the context field carries the new wording; per-artifact `rules` stay unchanged.

## 4. Update index

- [ ] 4.1 Update the Doc-represent row in `doc/AGENTS.md` (version 3.0.0, topic mentions global scope), bump the index to 1.3.0 + date, add changelog row. Verify per the `Doc-manage` index rule that the row version matches the doc header.

## 5. Final validation

- [ ] 5.1 Run `openspec validate make-doc-represent-global --strict` and verify it passes.
- [ ] 5.2 Dogfooding pass: apply the new delivery check to one real output produced during this change (e.g. the implementation summary chat response) and record the result in the change before archive.
