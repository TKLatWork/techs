# Data Model

<!-- docguard:version 0.2.2 -->
<!-- docguard:status active -->
<!-- docguard:last-reviewed 2026-05-31 -->
<!-- docguard:owner @project-maintainer -->
<!-- docguard:quality negation-load off — data model constraints inherently describe what values are invalid -->

> **Canonical document** — Design intent. This file describes the data structures and their relationships.
> Schema changes require this doc to be updated FIRST.

| Metadata | Value |
|----------|-------|
| **Status** | ![Status](https://img.shields.io/badge/status-active-green) |
| **Version** | `0.2.2` |
| **Last Updated** | 2026-05-31 |
| **Owner** | @project-maintainer |

---

## Entities

| Entity | Storage | Primary Key | Description |
|--------|---------|-------------|-------------|
| Project | In-memory / config | projectId (String) | Top-level container holding module references and shared settings |
| Module | In-memory / config | moduleId (String) | Self-contained unit (web, app, domain) with source dir, config, entry point |

The project has no database entities yet. The data model will expand as features introduce persistence requirements.

## Schema Definitions

### Project

| Field | Type | Required | Default | Constraints | Description |
|-------|------|----------|---------|-------------|-------------|
| projectId | String | Yes | auto-generated | Unique | Identifier for the project |
| name | String | Yes | — | Non-empty | Human-readable project name |
| modules | List\<Module\> | Yes | empty | At least 1 | Ordered list of modules in the project |

### Module

| Field | Type | Required | Default | Constraints | Description |
|-------|------|----------|---------|-------------|-------------|
| moduleId | String | Yes | — | Unique within project | Identifier: "web", "app", or "domain" |
| type | Enum | Yes | — | WEB, APP, DOMAIN | Module role classification |
| sourceDir | String | Yes | — | Non-empty | Path to module source directory |
| configFile | String | Yes | — | File must exist | Path to module configuration file |
| entryPoint | String | Yes | — | File must exist | Path to module entry point |
| dependencies | List\<String\> | No | empty | Reference valid moduleIds only | Other modules this module depends on |

## Relationships

| From | To | Type | FK/Reference | Cascade |
|------|-----|------|-------------|---------|
| Project | Module | 1:many | modules list | N/A (in-memory) |
| Module (web) | Module (domain) | many:1 | dependencies list | N/A |
| Module (app) | Module (domain) | many:1 | dependencies list | N/A |

## Indexes

The project has no database storage yet. Document indexes when introducing persistence.

## Migration Strategy

| Strategy | Tool | Notes |
|----------|------|-------|
| Pending | TBD | Document when introducing a database |

---

## Revision History

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 0.1.0 | 2026-05-31 | DocGuard Init | Initial template |
| 0.2.0 | 2026-05-31 | Constitution update | Filled placeholders with project/module entities from spec |
| 0.2.1 | 2026-05-31 | Doc quality fix | Rewrote to active voice, reduced negation |
| 0.2.2 | 2026-05-31 | Doc quality fix | Added negation-load override for constraint descriptions |
