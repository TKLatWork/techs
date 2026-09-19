# doc-representation Specification

## Purpose

Defines how project docs represent information: Mermaid diagrams for structure, flow, and state; Markdown tables for tracking, status, and comparison; standard forms for presenting changes/diffs - plus the rules for choosing between them and the OpenSpec config that applies them.

## Requirements

### Requirement: Single representation rule doc
The project SHALL keep exactly one rule doc for doc representation at `doc/rules/Doc-represent.md`, following the `Doc-<topic>.md` naming convention, and the doc index in `doc/AGENTS.md` SHALL reference it by that name and current version.

#### Scenario: Doc is reachable via the index
- **WHEN** a reader opens the rules table in `doc/AGENTS.md`
- **THEN** there is a row linking to `rules/Doc-represent.md` and no row or link referencing the old `rules/Doc-diagram.md`

#### Scenario: Old name is fully retired
- **WHEN** the repo is searched for `Doc-diagram`
- **THEN** no references remain outside changelog history entries

### Requirement: Diagram format
Diagrams in project docs SHALL use Mermaid in fenced ```` ```mermaid ```` blocks, one topic per diagram, with lean node count (<= ~12 nodes) and labels using real module/component names. Diagrams SHALL version together with their containing doc.

#### Scenario: Diagram added to a doc
- **WHEN** a doc describing components, flows, or states is created or updated
- **THEN** its diagrams render as Mermaid in VS Code preview/GitHub and stay reviewable as source

### Requirement: Standard diagram set
The rule doc SHALL define a standard diagram set - component/module structure, data/request flow, sequence, and state - each with a stated "use when" condition.

#### Scenario: Author picks a diagram type
- **WHEN** an author needs to visualize architecture, behavior, interaction order, or lifecycle
- **THEN** the rule doc names exactly one standard diagram type for that purpose

### Requirement: Representation choice
The rule doc SHALL state when to use a diagram vs a table vs a list: diagrams for structure/flow/state, tables for parallel tracking/comparison/status of items, lists for short unordered or sequential points.

#### Scenario: Author chooses a format
- **WHEN** an author represents a set of items with per-item state (e.g. progress, ownership)
- **THEN** the rule directs them to a table, not a diagram or prose

### Requirement: Table format
Tables in project docs SHALL be Markdown tables with a header row, one topic per table, and lean cells (short phrases, no paragraphs). Every table SHALL carry the real item names it tracks.

#### Scenario: Table added to a doc
- **WHEN** a doc needs to track or compare items
- **THEN** the table renders in Markdown preview and stays diff-friendly in source

### Requirement: Standard table set
The rule doc SHALL define a standard table set, including a progress-tracking table (columns such as `Item | Status | Notes`) as the canonical pattern for tracking item progress in docs and change artifacts, alongside the existing index and changelog tables.

#### Scenario: Progress tracking in a doc
- **WHEN** a doc or change artifact tracks progress of multiple items
- **THEN** it uses the standard progress-tracking table columns instead of an ad-hoc layout

#### Scenario: Existing tables conform
- **WHEN** the doc index table and changelog tables are reviewed
- **THEN** they match the table format rule (header row, lean cells, one topic)

### Requirement: Change/diff representation
The rule doc SHALL define two standard forms for presenting a change/diff, each with a worked example: a text-based diff (unified diff in a fenced ```` ```diff ```` block) for text/code changes, and a diagram diff for diagram changes - either one Mermaid diagram with change highlighting (added/removed/changed styles, e.g. via `classDef`) or a before/after diagram pair.

#### Scenario: Presenting a text change
- **WHEN** an author presents a change to text or code in a doc
- **THEN** the standard form is a fenced ```` ```diff ```` block showing removed (`-`) and added (`+`) lines, not prose describing the change

#### Scenario: Presenting a diagram change
- **WHEN** an author presents a change to a diagram in a doc
- **THEN** the standard form is a diagram diff - a highlighted Mermaid diagram or a before/after pair - not prose describing the change

### Requirement: OpenSpec config applies the rule
`openspec/config.yaml` SHALL reference `doc/rules/Doc-represent.md` in its project `context` and SHALL carry per-artifact `rules` for proposal, specs, and tasks directing generated artifacts to use the standard tables, diagrams, and diff forms.

#### Scenario: Config reaches artifact generation
- **WHEN** `openspec instructions <artifact-id> --json` is run for proposal, specs, or tasks
- **THEN** the returned `context`/`rules` include the representation guidance from `openspec/config.yaml`

#### Scenario: Artifact uses standard forms
- **WHEN** a generated proposal, specs, or tasks artifact tracks items or presents a change
- **THEN** it uses the standard table and diff forms from `doc/rules/Doc-represent.md` instead of ad-hoc layouts
