# project-visual — reference

> 状态：active v1.0.0 ｜ 日期：2026-10-22 ｜ 维护者：Kilo（用户指令）

## Terminology

| Term | Definition |
|---|---|
| architecture JSON | input document describing project, levels, scopes, nodes and edges |
| floor | one level rendered as a selectable horizontal plane |
| room / zone | bounding rectangle of all blocks in one scope on one floor |
| block | a node rendered as an interactive `<button>` |
| layout unit | one grid cell; the viewer multiplies it by 44 px |

## Files

```
project-visual/
├── SKILL.md
├── REFERENCE.md
├── assets/viewer.html          # HTML/CSS/JS template; __PV_DATA__ is injected
├── scripts/render.mjs          # validate + layout + embed → one HTML file
├── scripts/inventory.mjs       # optional raw symbol/route/import inventory
└── examples/todos-service.architecture.json
```

## Commands

```powershell
# raw inventory (optional)
node <skill-dir>/scripts/inventory.mjs <scope-dir> -o .tmp/inventory.json

# validate, lay out and render
node <skill-dir>/scripts/render.mjs <architecture.json> -o <name>.map.html

# also open the default browser
node <skill-dir>/scripts/render.mjs <architecture.json> -o <name>.map.html --open
```

`render.mjs` exits with code 1 on schema errors and prints warnings for weak data (node without files, empty level/scope, duplicate ids). The output HTML is standalone: CSS, JS and the normalized JSON are embedded.

## Architecture JSON

```json
{
  "schemaVersion": "1.0",
  "project": {
    "name": "todos-service",
    "root": "../..",
    "description": "In-memory todo service; scope: src/todo, src/http",
    "generatedAt": "2026-10-22T12:00:00Z"
  },
  "levels": [
    { "id": "http", "label": "HTTP", "order": 0, "description": "REST entry points" },
    { "id": "application", "label": "Application", "order": 1, "description": "Use cases and orchestration" },
    { "id": "domain", "label": "Domain", "order": 2, "description": "Contracts and lifecycle types" },
    { "id": "infrastructure", "label": "Infrastructure", "order": 3, "description": "Adapters and configuration" }
  ],
  "scopes": [
    { "id": "http", "label": "HTTP", "color": "#58a6ff", "description": "Transport" },
    { "id": "todo", "label": "Todo", "color": "#3fb950" }
  ],
  "nodes": [
    {
      "id": "http-create-todo",
      "level": "http",
      "scope": "http",
      "label": "POST /todos",
      "subtitle": "TodoController.create",
      "kind": "endpoint",
      "description": "Creates a todo after validation.",
      "files": [{ "path": "src/http/todo-controller.ts", "line": 24 }],
      "symbols": ["TodoController.create"],
      "tags": ["http", "write"],
      "metrics": { "fanOut": 2, "loc": 20 },
      "layout": { "x": 2, "y": 3, "w": 3.2, "h": 2.1 }
    },
    {
      "id": "app-todo",
      "level": "application",
      "scope": "todo",
      "label": "TodoService",
      "kind": "service",
      "files": [{ "path": "src/todo/todo.service.ts", "line": 12 }]
    }
  ],
  "edges": [
    { "id": "e-create-service", "from": "http-create-todo", "to": "app-todo", "kind": "calls", "label": "delegates" }
  ]
}
```

### Required and optional fields

| Field | Required | Type | Notes |
|---|---|---|---|
| `project.name` | recommended | string | viewer title; defaults to input directory name |
| `project.root` | recommended | string | absolute or relative to the JSON file; used for editor links |
| `project.description` | recommended | string | shown in the top bar; declare scope/exclusions here |
| `levels[]` | **yes** | array | inferred for this code scope; at least one level |
| `levels[].id` | **yes** | string | unique; referenced by nodes |
| `levels[].label` | **yes** | string | floor name from the code's vocabulary |
| `levels[].description` | **required by quality rules** | string | code evidence / responsibility for this tier; renderer warns when empty |
| `levels[].order` | no | number | array order = top → bottom; higher abstraction first |
| `scopes[]` | no | array | derived from node scopes when omitted |
| `scopes[].id` | yes if `scopes` used | string | unique; referenced by nodes |
| `scopes[].color` | no | `#rrggbb` | palette assigned when missing/invalid |
| `nodes[]` | **yes** | array | at least one |
| `nodes[].id` | recommended | string | auto-slugged and de-duplicated when missing |
| `nodes[].level` | **yes** | string | must match a level id |
| `nodes[].scope` | recommended | string | assigned to first scope when missing |
| `nodes[].label` | **yes** | string | short block title |
| `nodes[].subtitle` | no | string | symbol/file hint under the title |
| `nodes[].kind` | no | string | drives badge color/icon text (`endpoint`, `service`, ...) |
| `nodes[].description` | no | string | drawer body |
| `nodes[].files[]` | **required by quality rules** | array | `{path, line?}` or string; renderer warns when empty |
| `nodes[].symbols[]` | no | string[] | drawer chips |
| `nodes[].tags[]` | no | string[] | drawer chips |
| `nodes[].metrics` | no | object | numeric values shown in drawer |
| `nodes[].layout` | no | `{x,y,w,h}` | grid units; omit for auto-layout |
| `edges[]` | no | array | directional dependencies |
| `edges[].from` / `.to` | **yes** | string | must reference node ids |
| `edges[].kind` | no | string | defaults to `depends`; drives color |
| `edges[].label` | no | string | optional; drawer tooltip |

### Normalization performed by the renderer

- Missing or empty `levels` → **error**; the level model must be inferred for the requested scope, not defaulted.
- Missing `scopes` → derived in node order from `node.scope`; missing scopes are added with a warning.
- Missing node ids → slug of level + label; duplicates get `-2`, `-3` suffixes.
- Duplicate edges (same from/to/kind) are dropped; self-edges are dropped with a warning.
- Unknown level ids and edge endpoints are **errors**.
- Missing node/scope colors → built-in palette.
- `project.generatedAt` is filled when absent.
- Mixed explicit and missing layouts on one level → auto-layout for that whole level with a warning.

## Modeling guidance

### Inferring levels

Levels are **per scope** and evidence-based. Do not copy a global template or force a fixed count.

1. List the natural boundaries visible in this scope: build modules, packages, entry points, orchestration, domain types, adapters, configuration and tests.
2. Cut where responsibility changes and dependency direction can be described as tiers. A cut with no blocks is not a level; merge it.
3. Name levels with the code's own vocabulary. If build modules define the structure, module names are valid; if responsibility cuts across modules, use responsibility names.
4. Order top→bottom from high abstraction / entry / policy to low-level implementation / infrastructure.
5. Record the evidence in each `levels[].description`, and confirm every level has at least one block.

| Scope shape | One possible inference (top → bottom) | Count |
|---|---|---|
| Multi-module app with one-way module deps | `web` → `core` → `api` | 3 |
| Layered HTTP service | `HTTP` → `Application` → `Domain` → `Infrastructure` | 4 |
| Clean / hexagonal | `Adapters` → `Application` → `Domain` | 3 |
| Frontend | `Routes` → `Features` → `Shared` | 3 |
| CLI tool | `Commands` → `Use cases` → `Domain` → `Infra` | 4 |
| Library | `Public API` → `Implementation` | 2 |

These are examples, not presets. A different scope of the same repository may need different names and a different count. Tests can form a `verification` scope and sit on the tier they verify; they do not justify inventing a separate "test layer".

Keep each block on the floor where its primary responsibility lives. Connect a block to related blocks on other floors with edges instead of duplicating it.

### Scopes

- Use 3–8 domain or feature boundaries.
- A scope is a room: all blocks in it should share a reason to change.
- Prefer `auth`, `orders`, `billing`, `platform` over `utils`, `misc`, `common` (a `platform` scope is acceptable for genuinely shared infrastructure).
- One scope per node. If a block truly spans scopes, pick its owning module and record the rest in tags.

### Granularity

| Level | Put here | Avoid |
|---|---|---|
| API | HTTP/RPC routes, CLI commands, public contracts | private helpers |
| Service | application services, use cases, orchestration, repositories/ports | getters, DTOs without behavior |
| Classes | entities, value objects, adapters, mappers, significant functions | every tiny utility |

Aim for 10–40 blocks in a normal scope. If a floor has 80 blocks, group related helpers into a module block and mention the members in `symbols`.

### Edges

Use directional, evidence-backed verbs:

| kind | Meaning | Typical source |
|---|---|---|
| `calls` | runtime call / delegation | function call, DI usage |
| `routes` | router/entry registration points at a block | route table, router setup |
| `imports` | module import | `import`, `require`, `use` |
| `implements` | class implements interface | `implements`, `: Interface` |
| `extends` | inheritance | `extends`, `: Base` |
| `reads` | reads data/state | repository query, store read |
| `writes` | mutates data/state | save/update/delete |
| `emits` / `publishes` | sends an event | event bus, queue |
| `handles` | entry point handled by a class | route → controller |
| `depends` | generic, last resort | config/tooling |

Cross-floor edges are valid. They are not drawn on a single floor; the drawer lists them and the block shows `↕ n`.

## Research recipes

Use `inventory.mjs` for a first pass, then inspect the real files. Useful greps:

### TypeScript / JavaScript
- Routes: `router.(get|post|put|patch|delete)`, `@Get(`, `@Post(`, `app.use(`
- Entry files: `src/main`, `src/index`, `server.ts`, `app.ts`
- Services/use cases: `*.service.ts`, `*.usecase.ts`, `application/`
- Classes: `class `, `interface `, `implements `
- Imports: `from '...'`, `require('...')`

### Python
- Routes: `@app.route`, `@router.get`, `@bp.post`, FastAPI `APIRouter`
- Services: `*_service.py`, `services/`, `use_cases/`
- Classes: `^class `, Pydantic models
- Imports: `from x import y`, `import x`

### Java / Kotlin
- Routes: `@RequestMapping`, `@GetMapping`, `@PostMapping`, `@RestController`
- Services: `@Service`, `*Service.java`, `*UseCase.java`
- Persistence: `@Repository`, `*Repository.java`
- Classes: `class`, `interface`, `record`, `enum`
- Imports: `^import `

### Go
- Routes: `HandleFunc`, `Handle(`, `r.GET(`, `mux.Handle`
- Services: packages named `service`, `usecase`, `domain`
- Types: `type X struct`, `type X interface`
- Imports: `import (` blocks

### C# / Rust
- C#: controllers, `[HttpGet]`, `[HttpPost]`, `class`, `interface`, DI registrations
- Rust: `mod`, `trait`, `impl`, `use`, `axum`/`actix` route macros

## Layout

Auto-layout is deterministic:

- Blocks are grouped by scope into rooms, rooms are shelved left-to-right and wrapped at 34 units.
- One grid unit = 44 px. Default block = 3.2 × 2.1 units (~141 × 92 px).
- Room width = columns × block width + gaps + padding; header adds 1.3 units.
- Explicit layout: set `layout` on **every** node of a level to use hand coordinates; otherwise auto-layout wins. Coordinates are grid units, and may be shifted so content starts at a positive margin.
- Floors are independent: the same node id lives on exactly one floor, and each floor gets its own coordinate space.

## Viewer behavior

- Initial floor: first level with blocks; otherwise first level.
- `Plan` / `3D` toggle (`V`): Plan shows one floor at a time; 3D stacks all floors in a rotatable projection.
- 3D: drag orbits, wheel zooms, `Fit` frames visible floors. Blocks render as tiles at overview zoom, grow into labels when zoomed in, and show a hover tooltip. Cross-floor links are dashed and always drawn, including when their endpoints are on different floors.
- Scope filter hides non-matching rooms/blocks and refits the view.
- Search matches labels, ids, subtitles, descriptions, scopes, tags, symbols and file paths; non-matches dim.
- `Enter` in search jumps to the first match on the current floor, then to the first match anywhere.
- Edge legend toggles each kind; hidden kinds are excluded from the link count.
- `Fit` fits visible blocks; double-click centers a block at ≥1.05× zoom.
- File links use `vscode://file/<abs-path>:<line>`; `Copy` falls back to `execCommand` when the Clipboard API is unavailable.
- All data is inserted with `textContent`; the JSON is `<`-escaped before embedding.
- Keyboard: `1`–`9` floors, `V` Plan/3D, `/` search, `F` fit, `Esc` close/clear.

## Inventory helper output

```json
{
  "scope": "C:/repo",
  "files": [
    {
      "path": "src/http/todo-controller.ts",
      "language": "typescript",
      "classes": ["TodoController"],
      "functions": ["createTodo"],
      "routes": [{ "method": "POST", "path": "/todos", "line": 24 }],
      "imports": ["../todo/todo.service"]
    }
  ],
  "summary": { "files": 42, "classes": 18, "functions": 61, "routes": 12, "imports": 140 }
}
```

The inventory is heuristic. Verify routes/symbols in source before placing them in the architecture JSON.

## Troubleshooting

| Symptom | Cause / fix |
|---|---|
| Renderer exits 1 with level/edge errors | Fix ids: `nodes[].level` and every edge endpoint must match |
| A floor is empty | You probably reused a template for another scope. Re-infer the level model for this scope, merge empty tiers, and check the per-level counts printed by the renderer |
| Node appears but no edges | Plan mode draws same-floor edges only; switch to 3D for dashed cross-floor links, or open the drawer and use `↕ n` |
| 3D labels are missing | Blocks are tiles at overview zoom; hover a tile or zoom in until labels appear |
| Nothing visible after opening | Open the generated `.map.html`, not the source template; the template placeholder is not data |
| Editor links do nothing | VS Code is not the registered `vscode://` handler, or `project.root` is wrong; use `Copy` instead |
| Blocks overlap with explicit layout | Use auto-layout (remove `layout`), or give every block on that level non-overlapping coordinates |
| Search finds nothing on this floor | Matches on other floors are counted in the status bar; press Enter to jump |
| Huge unreadable floor | Collapse helpers into module blocks, or split scopes; keep 10–40 blocks per scope |