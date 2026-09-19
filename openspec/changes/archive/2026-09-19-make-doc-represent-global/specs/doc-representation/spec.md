# Spec: doc-representation (delta)

## MODIFIED Requirements

### Requirement: Representation choice
The rule doc SHALL state when to use a diagram vs a table vs a list - diagrams for structure/flow/state, tables for parallel tracking/comparison/status of items, lists for short unordered or sequential points - and SHALL define these choices as required forms with mandatory triggers: 3+ parallel items with per-item state require a table; structure/flow/state content requires a diagram; any presented set of changes requires a diff form (fenced ```` ```diff ```` block for text/code; diagram diff or before/after pair for added/removed/renamed elements). Prose-only output for content matching a trigger SHALL be treated as a rule violation.

#### Scenario: Author chooses a format
- **WHEN** an author represents a set of items with per-item state (e.g. progress, ownership)
- **THEN** the rule directs them to a table, not a diagram or prose

#### Scenario: Triggered form is mandatory
- **WHEN** any covered output contains 3+ parallel items with per-item state, structure/flow/state content, or a presented set of changes
- **THEN** the corresponding table, diagram, or diff form is present in that output

#### Scenario: Prose-only output is a violation
- **WHEN** output content matches a trigger but is delivered as prose without the required form
- **THEN** the output is non-conformant and must be corrected before delivery

## ADDED Requirements

### Requirement: Global applicability
The representation standards SHALL apply globally: to every Markdown file created at any path in the repo (governed docs, change artifacts, ad-hoc reports/reviews/summaries) and to agent chat responses. Chat responses SHALL follow the representation rules only; versioning, changelog, template, and index rules remain scoped to `doc/` per `doc/rules/Doc-manage.md`.

#### Scenario: Ad-hoc Markdown artifact
- **WHEN** an agent creates a Markdown file outside `doc/` (e.g. a report at the repo root)
- **THEN** the representation rules apply (required forms, table format, diff forms) without version-header, changelog, or index obligations

#### Scenario: Chat response with triggered content
- **WHEN** an agent's chat answer contains content matching a required-form trigger (parallel items with state, structure/flow/state, or a presented set of changes)
- **THEN** the response itself uses the required table, Mermaid diagram, or fenced diff form

#### Scenario: Chat carve-out
- **WHEN** an agent applies the rule in a chat response
- **THEN** no version header, changelog table, or template layout is required in the chat message

### Requirement: Delivery check
The rule doc SHALL define a pre-delivery check for any Markdown artifact or chat response: every triggered form is present, tables have header rows, changes are shown as diffs rather than prose, and Mermaid blocks render.

#### Scenario: Check before delivering
- **WHEN** an author is about to deliver a Markdown artifact or chat response containing triggered content
- **THEN** the delivery check has been run and any violation fixed before the output is handed over
