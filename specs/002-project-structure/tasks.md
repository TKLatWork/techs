# Tasks: Project Structure Setup

**Input**: Design documents from `/specs/002-project-structure/`

**Prerequisites**: `spec.md`, `plan.md`

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Initialize the monorepo structure and create the four required modules.

- [ ] T001 Create the root feature directory layout with top-level modules: `domain/`, `api/`, `app/`, and `web/`.
- [ ] T002 Create a root-level Maven aggregator `pom.xml` that declares `domain`, `api`, and `app` as modules and provides shared Java build configuration.
- [ ] T003 Create a root-level npm/JavaScript workspace configuration (`package.json`) that includes `web` and supports a project-level validate/build command.
- [ ] T004 Create the `domain` module with its own `pom.xml` (including Lombok dependency and annotation processor configuration), `src/main/java/`, and `src/test/java/`.
- [ ] T005 Create the `api` module with its own `pom.xml` (including Lombok, Jackson, and typescript-generator plugin dependencies, plus annotation processor configuration), `src/main/java/`, `src/test/java/`, and TypeScript generation output directory (`target/generated-sources/typescript/`).
- [ ] T006 Create the `app` module with its own `pom.xml` (including Jackson dependency and Spring Boot starter), `src/main/java/`, `src/test/java/`, and a Spring Boot application entry point.
- [ ] T007 Create the `web` module with its own `package.json`, `tsconfig.json`, `vite.config.ts`, `src/`, `public/`, and a React/Vite TypeScript entry point.

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Wire module dependencies and establish the shared contract layer.

- [ ] T008 Configure `api` to depend on `domain` and declare its contract DTOs/interfaces as the single source of truth.
- [ ] T009 Configure `app` to depend on both `domain` and `api` and include a sample use of each.
- [ ] T010 Configure `web` to consume generated TypeScript contract output from `api` via relative path to `api/target/generated-sources/typescript/api-models.ts`.
- [ ] T011 Write a failing unit test for a sample shared domain entity, then implement the entity in `domain/src/main/java/com/techs/domain/model/`.
- [ ] T012 Write a failing unit test for a sample API contract DTO or interface, then implement it in `api/src/main/java/com/techs/api/model/` referencing the `domain` module.
- [ ] T013 Configure `api` to generate TypeScript types from Java definitions using the typescript-generator Maven plugin into `target/generated-sources/typescript/api-models.ts`.
- [ ] T014 Write a failing test for a simple `web` component, then implement the component that imports the generated TypeScript contract and references it in code.
- [ ] T015 Write a failing test for a simple `app` service, then implement the service that uses the `api` contract and a `domain` model.
- [ ] T016 Add a root-level build command that compiles and verifies the whole project across Maven and npm (e.g., `mvn verify` + `npm run build`).

---

## Phase 3: User Story 1 - Initialize Multi-Module Project (Priority: P1) 🎯

**Goal**: Deliver the complete initial project structure with all four modules ready to open and extend.

**Independent Test**: Verify the directories and module boundaries exist, and the root-level structure is navigable.

- [ ] T017 Confirm `domain/`, `api/`, `app/`, and `web/` exist with their own configuration and source directories.
- [ ] T018 Confirm each executable module has a clear entry point: Java application entry point for `app` (`Application.java`) and React entry point for `web` (`main.tsx`). Library modules (`domain`, `api`) do not require entry points.
- [ ] T019 Confirm the root-level project configuration ties the modules together and documents the build commands.
- [ ] T020 Add root README notes documenting how to open and verify the project structure (the feature-level `quickstart.md` already exists at `specs/002-project-structure/quickstart.md`).

---

## Phase 4: User Story 2 - Domain and API Modules as Shared Foundation (Priority: P2)

**Goal**: Ensure shared domain logic and API contract usage works across `app`, `api`, and `web`.

**Independent Test**: Confirm `app` references `domain` and `api`; confirm `web` references `api` only.

- [ ] T021 Add a `domain` entity test that proves `app` can compile against the shared domain module.
- [ ] T022 Add an `api` contract test that proves `app` can compile against the API contract module.
- [ ] T023 Add a `web` sample code path that imports only generated TypeScript from `api` and not `domain` directly.
- [ ] T024 Verify the dependency graph is enforced: `web` → `api` → `domain` ← `app`.
- [ ] T025 Verify `domain` has no dependencies on `web`, `app`, or `api`.

---

## Phase 5: User Story 3 - Independent Module Development (Priority: P3)

**Goal**: Make each module independently buildable and testable.

**Independent Test**: Run module-local tests and build each module without requiring others to be built first.

- [ ] T026 Add a standalone unit test for `domain` that runs with `mvn -pl domain test`.
- [ ] T027 Add a standalone unit test for `api` that runs with `mvn -pl api test`.
- [ ] T028 Add a standalone unit test for `app` that runs with `mvn -pl app test`.
- [ ] T029 Add a standalone frontend test for `web` that runs with `npm --prefix web test` or equivalent.
- [ ] T030 Confirm each module can be built independently from the root without requiring other module builds first.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Final validation and developer experience improvements.

- [ ] T031 Add or update documentation that explains how the `api` generated TypeScript output is consumed by `web`.
- [ ] T032 Add a Node.js structural validation script (`scripts/validate-structure.mjs`) that checks all four module directories exist, configs are valid, and the dependency graph (`web` → `api` → `domain` ← `app`) is correct — without performing a full build.
- [ ] T033 Review generated module configuration for consistency with the technology assumptions in the spec.
- [ ] T034 Ensure the project structure and tasks align with the drift noted in `DRIFT-LOG.md`.
- [ ] T035 Create or update `CHANGELOG.md` with a project structure setup entry per constitution requirement.
- [ ] T036 Update canonical `docs-canonical/DATA-MODEL.md` to add `API` to the Module type enum per drift noted in plan.md.
- [ ] T037 Run `npx docguard guard` and resolve all reported issues to ensure CDD compliance before merging.
