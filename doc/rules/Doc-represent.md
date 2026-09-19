# Doc represent
> Version: 2.0.0
> Updated: 2026-09-19

# Purpose
> Standard representation for project docs - diagrams and tables - to make component/project review easier and consistent.
> Applies to: any doc describing components, architecture, flows, states, or tracking/comparison of items.

# Rule: Format
> Mermaid only, in fenced ```mermaid blocks - renders in VS Code preview/GitHub, source stays reviewable
> One topic per diagram; keep node count lean (<= ~12 nodes)
> Label with real module/component names (e.g. `app-server`, `service-timezone`, `app-web`); keep labels short
> Diagrams version with their doc - bump the doc version on any diagram change

# Standard diagram set

## Component / module structure
> Shows modules/components and how they contain or depend on each other.
> Use when: project/module overview, architecture sections, hosting/dependency rules.

```mermaid
flowchart TD
  subgraph srv[app-server]
    Application
    SpaWebConfig
  end
  subgraph svc[service-timezone]
    TimezoneController
  end
  subgraph web[app-web]
    Layout --> TimezonePage
  end
  Application -->|hosts| TimezoneController
  SpaWebConfig -->|serves static| Layout
```

## Data / request flow
> Shows how a request/data moves between components end to end.
> Use when: explaining behavior, API usage, integration points.

```mermaid
flowchart LR
  Browser -->|"GET /web/timezone"| AppServer["app-server (SpaWebConfig)"]
  AppServer -->|"index.html + assets"| Browser
  Browser -->|"GET /api/timezone/convert"| Controller["service-timezone (TimezoneController)"]
  Controller -->|"JSON"| Browser
```

## Sequence
> Shows ordered interactions between participants over time.
> Use when: APIs, use cases, multi-step flows where order matters.

```mermaid
sequenceDiagram
  participant B as Browser (app-web)
  participant S as app-server
  participant T as service-timezone
  B->>S: GET /web/timezone
  S-->>B: index.html (SPA)
  B->>S: GET /api/timezone/zones
  S->>T: dispatch to TimezoneController
  T-->>B: zones JSON
  B->>S: GET /api/timezone/convert
  S->>T: dispatch to TimezoneController
  T-->>B: converted JSON
```

## State
> Shows lifecycle states and transitions of one component/entity.
> Use when: stateful UI, entity lifecycles, protocol/job states.

```mermaid
stateDiagram-v2
  [*] --> LoadingZones
  LoadingZones --> Idle: zones loaded
  LoadingZones --> Error: zones fetch failed
  Idle --> Converting: Convert clicked
  Converting --> Result: success
  Converting --> Error: request failed
  Result --> Converting: Convert clicked
  Error --> Converting: Convert clicked
```

# Rule: Representation choice
> Diagram: structure, flow, or state - relationships, order, lifecycles
> Table: tracking, comparison, or status of parallel items - rows of the same shape
> List: short unordered or sequential points
> Items share fields and carry per-item state -> table; relationships matter -> diagram

# Rule: Table format
> Markdown tables only, header row required
> One topic per table; lean cells - short phrases, no paragraphs
> Rows keyed by real item names (docs, modules, tasks)

# Standard table set

## Progress tracking
> Tracks progress of items in a doc or change artifact.
> Use when: task progress, rollout status, multi-item migrations.

| Item | Status | Notes |
|---|---|---|
| Rule doc rename | done | Doc-diagram -> Doc-represent |
| Config wiring | pending | openspec/config.yaml |

## Index
> Maps docs to version and topic; canonical example: the rules table in `doc/AGENTS.md`.

| Doc | Version | Topic |
|---|---|---|
| Doc-represent | 2.0.0 | representation standards for docs |

## Changelog
> One lean row per version at the end of every doc.

| Version | Date | Change |
|---|---|---|
| 2.0.0 | 2026-09-19 | Renamed to Doc-represent; added table + diff rules |

# Rule: Change/diff representation
> Present changes as diffs, not prose descriptions
> Text/code change: unified diff in a fenced ```diff block
> Diagram change: one Mermaid diagram with classDef highlight (added/removed/changed); use a before/after pair when the change is large

## Text diff example

```diff
- | [Doc-diagram](./rules/Doc-diagram.md) | 1.0.0 | diagram standards for docs |
+ | [Doc-represent](./rules/Doc-represent.md) | 2.0.0 | representation standards for docs |
```

## Diagram diff example
> Green = added, red = removed, orange = changed.

```mermaid
flowchart TD
  Old["Doc-diagram 1.0.0"]:::removed
  New["Doc-represent 2.0.0"]:::changed
  Tables["Table rules"]:::added
  Diffs["Diff rules"]:::added
  Diagrams["Diagram rules"]:::changed
  Old -.->|renamed| New
  New --> Tables
  New --> Diffs
  New --> Diagrams
  classDef added fill:#d4f7d4,stroke:#2a7f2a
  classDef removed fill:#f7d4d4,stroke:#a33
  classDef changed fill:#fdecc8,stroke:#a86a00
```

# Changelog
| Version | Date | Change |
|---|---|---|
| 1.0.0 | 2026-09-19 | Initial: Mermaid format rule + standard set (component, data flow, sequence, state) |
| 2.0.0 | 2026-09-19 | Renamed Doc-diagram -> Doc-represent; added representation choice, table format + standard set, change/diff representation |
