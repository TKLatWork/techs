# Proposal: add-diagram-standards

## Why

Docs that describe components or the project have no visual standard — review relies on prose alone, and every author invents their own diagrams (or none). A standard diagram set with ready examples makes component/project review faster and consistent across docs and authors (human or agent).

## What Changes

- Add **`doc/rules/Doc-diagram.md`** (based on `Doc-template.md`, version 1.0.0):
  - **Format standard: Mermaid** (user decision) in fenced ```` ```mermaid ```` blocks — renders in VS Code preview/GitHub, source stays reviewable in the markdown
  - **Standard diagram set** — each type gets a summary (what it shows, when to use it) + one copy-ready example, grounded in this project (app-server / service-timezone / app-web) so examples double as a project overview:
    - component/module structure (flowchart with subgraphs)
    - data/request flow (flowchart LR)
    - sequence (sequenceDiagram)
    - state (stateDiagram-v2)
  - **Usage rules**: when a diagram is required (docs describing components/architecture/flows/states), one topic per diagram, lean node count, label conventions, diagrams version with their doc
- Update **`doc/AGENTS.md`** index: add `Doc-diagram` row (index rule); index minor-bumped to 1.1.0 with changelog row
- No code, build, or dependency changes

## Capabilities

None — pure doc/process change, no system behavior changes. `skip_specs: true` is set in `.openspec.yaml`.

### New Capabilities

(none)

### Modified Capabilities

(none)

## Impact

- Files: new `doc/rules/Doc-diagram.md`, updated `doc/AGENTS.md`
- Governs all future docs containing diagrams; existing docs unaffected until next revision
