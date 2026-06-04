# Implementation Plan: Project Structure Setup

**Branch**: `002-project-structure` | **Date**: 2026-05-31 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/002-project-structure/spec.md`

## Summary

Set up a 4-module monorepo project structure with `domain` (Java shared library), `api` (Java API contracts with TypeScript generation), `app` (Spring Boot backend), and `web` (React/Vite frontend). The dependency graph is `web` → `api` → `domain` ← `app`, with `app` also depending on `api`. The `api` module bridges Java and TypeScript ecosystems using typescript-generator Maven plugin to produce TypeScript types from Java DTOs.

## Technical Context

**Language/Version**: Java 21 (JDK 21) for domain/api/app; TypeScript 5.x for web

**Primary Dependencies**: Spring Boot 3.x (app), React 18.x + Vite 5.x (web), Lombok (domain, api), typescript-generator 4.1.1 (api), Jackson 2.x (api, app)

**Storage**: N/A (project structure setup, no persistence yet)

**Testing**: JUnit 5 (Java modules), Vitest (web)

**Target Platform**: Cross-platform development (Windows primary, Linux/macOS compatible)

**Project Type**: Web application (monorepo with frontend + backend + shared libraries)

**Performance Goals**: N/A for structure setup

**Constraints**: N/A for structure setup

**Scale/Scope**: 4 modules, initial scaffolding only

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### Pre-Design Check

| Gate | Status | Notes |
|------|--------|-------|
| Test-First | PASS | Test frameworks specified (JUnit 5, Vitest); tests will precede all implementation |
| CDD Compliance | DRIFT | 4th module (api) and changed dependency graph deviate from canonical ARCHITECTURE.md and DATA-MODEL.md. Drift logged in DRIFT-LOG.md |
| Technology Justified | PASS | All technologies specified in spec with rationale; research.md documents alternatives |
| Specification Gate | PASS | Spec exists with user scenarios, functional requirements, and measurable success criteria |

### Post-Design Re-Check

| Gate | Status | Notes |
|------|--------|-------|
| Test-First | PASS | Test frameworks and locations defined per module |
| CDD Compliance | DRIFT | Canonical docs (ARCHITECTURE.md, DATA-MODEL.md) need updating to reflect api module. Drift logged |
| Plan Gate | PASS | Technical plan exists with constitution compliance check |

### Drift Summary

The canonical ARCHITECTURE.md describes 3 modules with `web` depending on `domain` directly. This plan introduces a 4th module (`api`) and changes the dependency graph to `web` → `api` → `domain` ← `app`. The canonical DATA-MODEL.md Module type enum needs `API` added. Both drifts are logged in DRIFT-LOG.md.

## Project Structure

### Documentation (this feature)

```text
specs/002-project-structure/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output
│   └── module-contracts.md
└── tasks.md             # Phase 2 output (/speckit.tasks command)
```

### Source Code (repository root)

```text
techs/
├── domain/                          # Java shared library
│   ├── src/
│   │   ├── main/java/
│   │   │   └── com/techs/domain/
│   │   └── test/java/
│   │       └── com/techs/domain/
│   └── pom.xml
├── api/                             # API contracts (Java → TypeScript)
│   ├── src/
│   │   ├── main/java/
│   │   │   └── com/techs/api/
│   │   │       └── model/           # DTOs and interfaces
│   │   └── test/java/
│   │       └── com/techs/api/
│   ├── target/
│   │   └── generated-sources/
│   │       └── typescript/          # Generated TypeScript output
│   │           └── api-models.ts
│   └── pom.xml
├── app/                             # Spring Boot backend
│   ├── src/
│   │   ├── main/java/
│   │   │   └── com/techs/app/
│   │   │       └── Application.java
│   │   └── test/java/
│   │       └── com/techs/app/
│   └── pom.xml
├── web/                             # React + Vite frontend
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── services/
│   │   └── main.tsx
│   ├── public/
│   ├── package.json
│   ├── tsconfig.json
│   └── vite.config.ts
├── docs-canonical/                  # CDD canonical documents (READ-ONLY)
├── docs-implementation/             # CDD implementation docs
├── specs/                           # Feature specifications
├── pom.xml                          # Maven parent/aggregator (domain, api, app)
├── package.json                     # Root npm config (web workspace)
├── AGENTS.md                        # AI agent instructions
├── CHANGELOG.md                     # Change tracking
└── DRIFT-LOG.md                     # Canonical deviation tracking
```

**Structure Decision**: 4-module monorepo with Maven aggregator for Java modules (domain, api, app) and npm for the web module. The api module bridges ecosystems via typescript-generator. Root `pom.xml` aggregates Java modules; root `package.json` manages the web workspace.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| 4th module (api) | Web (npm/React) cannot directly depend on domain (Java/Maven) due to ecosystem differences | 3-module structure forces web to duplicate domain types or use fragile manual sync |
| TypeScript generation pipeline | Single source of truth for API contracts across Java and TypeScript | Manual type definitions drift over time and require dual maintenance |
