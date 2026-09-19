# Doc diagram
> Version: 1.0.0
> Updated: 2026-09-19

# Purpose
> Standard diagrams for project docs, to make component/project review easier and consistent.
> Applies to: any doc describing components, architecture, flows, or states.

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

# Changelog
| Version | Date | Change |
|---|---|---|
| 1.0.0 | 2026-09-19 | Initial: Mermaid format rule + standard set (component, data flow, sequence, state) |
