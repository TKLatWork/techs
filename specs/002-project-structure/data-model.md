# Data Model: Project Structure Setup

**Feature**: 002-project-structure
**Date**: 2026-05-31

## Entities

### Project

Top-level container that aggregates all modules and provides root-level build orchestration.

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| projectId | String | Yes | Unique identifier for the project |
| name | String | Yes | Human-readable project name |
| modules | List\<Module\> | Yes | Ordered list of modules (domain, api, app, web) |
| rootConfig | RootConfig | Yes | Root-level configuration tying modules together |

### Module

Self-contained unit with its own source directory, configuration, and entry point.

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| moduleId | String | Yes | Unique identifier: "web", "app", "domain", or "api" |
| type | Enum | Yes | Module role: WEB, APP, DOMAIN, API |
| sourceDir | String | Yes | Path to module source directory |
| configFile | String | Yes | Path to module configuration file |
| entryPoint | String | Yes | Path to module entry point |
| dependencies | List\<String\> | No | Module IDs this module depends on |
| buildSystem | Enum | Yes | MAVEN or NPM |

### Module Types

| Type | Build System | Config File | Entry Point | Dependencies |
|------|-------------|-------------|-------------|--------------|
| DOMAIN | Maven | pom.xml | N/A (library) | none |
| API | Maven | pom.xml | N/A (library) | [domain] |
| APP | Maven | pom.xml | Application.java | [domain, api] |
| WEB | npm | package.json | src/main.tsx | [api] |

### Dependency Graph

```
web (npm) ──depends on──> api (Maven) ──depends on──> domain (Maven)
                                                          ^
app (Maven) ──────────depends on──────────────────────────┘
app (Maven) ──depends on──> api (Maven)
```

### Configuration Files by Module

| Module | Config File | Purpose |
|--------|------------|---------|
| domain | pom.xml | Maven library config, Lombok, JUnit 5 |
| api | pom.xml | Maven library config, typescript-generator plugin, depends on domain |
| app | pom.xml | Spring Boot application, depends on domain and api |
| web | package.json | npm project, React + Vite + TypeScript, Vitest |
| root | pom.xml | Maven parent/aggregator for domain, api, app |

## Relationships

| From | To | Type | Description |
|------|-----|------|-------------|
| Project | Module | 1:many | Project contains all modules |
| Module (api) | Module (domain) | many:1 | API depends on domain for type references |
| Module (app) | Module (domain) | many:1 | App depends on domain for business logic |
| Module (app) | Module (api) | many:1 | App depends on api for contracts |
| Module (web) | Module (api) | many:1 | Web depends on api for generated TypeScript types |

## Validation Rules

- Module IDs must be unique within a project
- Dependency references must point to existing module IDs
- Circular dependencies are not allowed
- Domain module must have no dependencies on other project modules
- Web module must not depend on domain directly (only through api)

## State Transitions

Not applicable — project structure is static configuration, not a stateful entity.

## Notes on Canonical Drift

The canonical DATA-MODEL.md defines Module type enum as `WEB, APP, DOMAIN` (3 values). This feature introduces `API` as a 4th value. The canonical DATA-MODEL.md will need updating to reflect this change. This drift is logged in DRIFT-LOG.md.
