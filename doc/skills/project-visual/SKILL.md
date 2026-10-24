---
name: project-visual
description: Research a code scope into an architecture JSON and render it as a self-contained interactive building-style HTML map with 2D floor plans and a stacked 3D view. Vertical levels are inferred from the code and scope (names and count vary; top = higher-level abstraction, bottom = lower-level implementation), modules are horizontal scopes, and every block is a clickable HTML element with a detail drawer and dependency jumps. Use when the user asks to visualize a codebase, project structure, architecture map, module map, dependency map, API/service/class layers, or wants an interactive offline HTML view of code structure.
---

# project-visual — code scope → interactive building map

> 状态：active v1.0.0 ｜ 日期：2026-10-22 ｜ 维护者：Kilo（用户指令）

Given a scope, produce two artifacts:

| Artifact | Role |
|---|---|
| `<name>.architecture.json` | researched model; source of truth, rerenderable |
| `<name>.map.html` | one offline HTML file; no CDN, no server |

The map is a building: **levels are floors (vertical), scopes are rooms (horizontal), blocks are interactive HTML buttons**. The viewer switches between a 2D floor plan and a stacked 3D view.

## Terminology

| Term | Definition |
|---|---|
| scope | code range being mapped: repo, package, directory, or feature; plus exclusions |
| level | vertical tier / floor: names and count inferred from this code scope, ordered top→bottom high abstraction → low-level implementation |
| scope zone | horizontal module boundary drawn as a colored room on a floor |
| block | one node on the map: endpoint, service, class, module, or significant function |
| edge | evidenced dependency between blocks (`calls`, `imports`, `implements`, ...) |
| architecture JSON | renderer input; validated and auto-laid-out at render time |

## Workflow

1. **Fix the scope.** Resolve the user's path, package, or feature. Exclude `node_modules`, `vendor`, `dist`, `build`, generated code, fixtures unless they are the subject. State the scope back in the JSON `project.description`.
2. **Inventory.** Optionally run the helper to collect raw symbols/routes/imports:
   `node <skill-dir>/scripts/inventory.mjs <scope-dir> -o .tmp/project-visual-inventory.json`
   Then read the important files with normal search/read tools. The inventory is raw material, never the final model.
3. **Infer the level model.** Do not start from a fixed template. Read this scope's modules/packages, dependency direction, entry points, orchestration, domain types, adapters and configuration. Choose the number of levels the code actually separates (typically 1–5) and name them with the code's own vocabulary. Order floors top→bottom from higher-level abstraction / entry / policy to lower-level implementation / infrastructure. Recompute the model for each scope; do not reuse a global template. Record the evidence in `levels[].description`.
4. **Choose scopes.** 3–8 cohesive modules/features/packages. Every node gets exactly one scope. Scopes become the horizontal filter and the colored rooms.
5. **Model nodes with evidence.** One block per meaningful unit. Every node must cite at least one `files[]` entry with `path` and, when known, `line`. Put long explanation in `description`; keep `label` short. Do not dump every helper.
6. **Model edges with evidence.** Add an edge only when you saw the relationship in code (import, call, implementation, read/write, event). Edge endpoints must exist. Cross-floor edges are allowed and appear in the drawer.
7. **Render.**
   `node <skill-dir>/scripts/render.mjs <name>.architecture.json -o <name>.map.html --open`
   The renderer validates, normalizes, assigns colors, computes floor-plan layout, and embeds the data.
8. **Verify.** Fix every renderer error; review warnings. Open the map and test: switch floors, switch scopes, click blocks, open files, follow a cross-floor dependency, search, fit.

## JSON skeleton

```json
{
  "project": { "name": "orders", "root": "../", "description": "Order service, API → storage" },
  "levels": [
    { "id": "http", "label": "HTTP", "description": "REST boundary" },
    { "id": "application", "label": "Application", "description": "Use cases and orchestration" },
    { "id": "domain", "label": "Domain", "description": "Contracts and lifecycle types" },
    { "id": "infrastructure", "label": "Infrastructure", "description": "Adapters and configuration" }
  ],
  "scopes": [{ "id": "orders", "label": "Orders", "color": "#58a6ff" }],
  "nodes": [
    {
      "id": "http-create-order", "level": "http", "scope": "orders",
      "label": "POST /orders", "subtitle": "OrderController.create", "kind": "endpoint",
      "description": "Validates and creates an order.",
      "files": [{ "path": "src/orders/order.controller.ts", "line": 24 }],
      "symbols": ["OrderController.create"], "tags": ["http", "write"]
    },
    {
      "id": "app-order", "level": "application", "scope": "orders",
      "label": "OrderService", "kind": "service",
      "files": [{ "path": "src/orders/order.service.ts", "line": 12 }]
    }
  ],
  "edges": [{ "from": "http-create-order", "to": "app-order", "kind": "calls" }]
}
```

Full field contract: [REFERENCE.md](REFERENCE.md). Demo: [examples/todos-service.architecture.json](examples/todos-service.architecture.json).

## Modeling rules

- **Levels answer “which tier of this code, for this scope?”** Infer names and count from actual boundaries, not a fixed API/Service/Classes template. Keep each block on exactly one floor; merge levels that would be empty. Order top→bottom from high abstraction / entry / policy to low-level implementation / infrastructure.
- **Use the code's vocabulary.** A multi-module repo may yield module-named tiers (`web`, `core`, `api`); a layered service may yield `http`, `application`, `domain`, `infrastructure`; a frontend may yield `routes`, `features`, `shared`. These are examples, not presets.
- **Scopes answer “which module?”** Use domain/feature names, not file types. `auth`, `orders`, `billing` — not `controllers`, `utils`.
- **Block granularity:** assign by primary responsibility within the inferred model. Endpoints at the top tier, orchestration and ports in the middle, concrete types/adapters at the bottom. Collapse trivial getters/helpers.
- **Edges are directional** from caller/dependent to callee/dependency. Prefer `calls`, `imports`, `implements`, `reads`, `writes`, `emits`, `depends`.
- **IDs are stable kebab-case**, unique across all levels; edges reference them.
- **Layout is optional.** Omit `layout` and the renderer packs blocks into scope rooms. Provide `{x,y,w,h}` per level only when a hand-authored plan matters (all nodes on that level need coordinates).
- **No invented facts.** Unknown details go in `description` as open questions, not as fake files or edges.

## Viewer interactions

- Left rail = floors; select the inferred levels.
- `Plan` / `3D` toggle: 3D stacks all floors; drag orbits, scroll zooms, and cross-floor links are dashed. Blocks become tiles at overview zoom; hover for a label and zoom in to reveal labels.
- Top chips = scopes; selecting one filters every floor and refits.
- Drag to pan (2D) or orbit (3D); wheel to zoom, `Fit` to reset; double-click a block to focus it.
- Click a block → drawer with description, metrics, files, symbols, tags, and both dependency directions.
- Click a file row → `vscode://file/...:line`; `Copy` copies `path:line`.
- Click a dependency → switches floor/scope as needed and selects the target.
- `↕ n` on a block = cross-floor links. Search dims non-matches; Enter jumps to a match; `/` focuses search.
- Edge legend toggles link kinds. `1`–`9` switch floors, `V` toggles Plan/3D, `Esc` closes the drawer.

## Quality checklist

- [ ] Scope and exclusions are explicit in `project.description`.
- [ ] Level names and count are derived from this code scope; top→bottom runs high abstraction → low-level; no empty floors.
- [ ] 3–8 scopes; no “misc” dumping ground.
- [ ] Every node cites a real file (and line when practical).
- [ ] Every edge has a code-backed reason; no dangling endpoints.
- [ ] Renderer exits 0; remaining warnings are understood.
- [ ] Plan and 3D both render; 3D stacks all floors and shows dashed cross-floor links.
- [ ] All floors, scopes and the drawer work in a browser; cross-floor jumps work.
- [ ] Output HTML has no network URLs and opens by double-click.

See [REFERENCE.md](REFERENCE.md) for the schema, research recipes per stack, and troubleshooting.