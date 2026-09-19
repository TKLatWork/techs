# Tasks: add-diagram-standards

## 1. Diagram rules doc

- [ ] 1.1 Create `doc/rules/Doc-diagram.md` based on `doc/template/Doc-template.md` — Purpose (standard diagrams for easier component/project review); Format rule: Mermaid only, in fenced ```mermaid blocks; Usage rules: when a diagram is required (docs describing components/architecture/flows/states), one topic per diagram, lean node count, consistent labels, diagrams version with their doc. Header `Version: 1.0.0` + `Updated` date + Changelog row. Verify: section order matches base template (header → Purpose → content → Changelog); dogfooding holds.
- [ ] 1.2 Add the standard diagram set — per type: a summary explaining what the diagram does / when to use it, plus one copy-ready example grounded in this project's real modules (app-server, service-timezone, app-web): component/module structure (flowchart + subgraph), data/request flow (flowchart LR), sequence (sequenceDiagram), state (stateDiagram-v2). Verify: all four render without syntax errors in VS Code mermaid preview (or mermaid.live); each type section contains both summary and example; node names match actual module names.

## 2. Index update

- [ ] 2.1 Update `doc/AGENTS.md` — add `Doc-diagram | 1.0.0 | diagram standards for docs` row to the Rules table; bump index to `1.1.0` (minor: added row), update `Updated` date, add changelog row. Verify: row version matches `Doc-diagram.md` header; index changelog has the 1.1.0 row.

## 3. Final verification

- [ ] 3.1 Dogfooding pass against `doc/rules/Doc-manage.md` — check `Doc-diagram.md` and `doc/AGENTS.md`: header blocks, changelog tables, template layout, index rows match files/versions. Verify: zero violations; report result in the change before archive.
