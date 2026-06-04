# Feature Specification: Project Structure Setup

**Feature Branch**: `002-project-structure`

**Created**: 2026-05-31

**Status**: Draft

**Input**: User description: "I want to create a project structure with a 'web' frontend and 'app' backend and a 'domain' module and an 'api' module"

## Clarifications

### Session 2026-05-31

- Q: How should the `web` module reference domain entities and business rules? → A: Create a new `api` module that owns API/Model contracts connecting `web` and `app`. The `domain` module is a direct dependency of `app` only (both Java/Maven). The `web` module (npm/React) references the `api` module for contracts instead of depending on `domain` directly.
- Q: What format/technology should the `api` module use to bridge both Java and TypeScript ecosystems? → A: Java-first definitions (interfaces/DTOs) with TypeScript types generated from them.
- Q: What tool should generate TypeScript types from the Java `api` module? → A: typescript-generator Maven plugin (Vojtech Habarta).
- Q: Should the `api` module depend on the `domain` module? → A: Yes — `api` depends on `domain` (contracts reference domain types). Dependency graph: `web` → `api` → `domain` ← `app`.
- Q: How should the `web` module consume the generated TypeScript types from the `api` module? → A: `api` build outputs TypeScript to a known directory; `web` references it via relative path.

### Session 2026-06-04

- Q: What does SC-002 "single operation" mean — an automation script or the task list? → A: The task-based approach is sufficient; "single operation" means the developer follows the task list once without manual improvisation.
- Q: Does the Test-First constitution principle apply to module entry points (Application.java, main.tsx)? → A: No — entry points are minimal scaffolding (1-3 lines of wiring) exempt from test-first. They are covered indirectly by standalone module tests.
- Q: How should SC-003 "zero duplication" be enforced? → A: Softened to a guideline — shared business logic SHOULD reside in `domain` to minimize duplication, enforced by code review and dependency graph rather than automated detection.
- Q: What constitutes an "entry point" for library modules (`domain`, `api`)? → A: Library modules do not require entry points. The entry point requirement applies only to executable modules (`app`, `web`).
- Q: Should `web` consume generated TypeScript from `api` via relative path or npm workspace link? → A: Relative path only (`../api/target/generated-sources/typescript/`). No npm workspace coupling — `api` is Maven-managed and its TypeScript output is a build artifact, not a publishable package.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Initialize Multi-Module Project (Priority: P1)

A developer starts a new project and needs a clear, organized structure that separates the frontend (web), backend (app), shared domain logic (domain), and API contracts (api) into distinct modules. The developer runs a setup process and receives a complete directory layout with all four modules ready for development.

**Why this priority**: Without a proper project structure, no development can begin. This is the foundational step that all other work depends on.

**Independent Test**: Can be fully tested by verifying that the four module directories exist with their expected internal structure and that the project builds/runs from the root.

**Acceptance Scenarios**:

1. **Given** a new or empty project workspace, **When** the project structure is created, **Then** four top-level module directories exist: `web`, `app`, `domain`, and `api`.
2. **Given** the project structure is created, **When** a developer inspects each module, **Then** each module contains its own configuration and source directory, and executable modules (`app`, `web`) have an entry point appropriate to their role.
3. **Given** the project structure is created, **When** the project is opened in an IDE or editor, **Then** the module boundaries are clearly visible and navigable.

---

### User Story 2 - Domain and API Modules as Shared Foundation (Priority: P2)

A developer working on the backend needs to reference shared domain models, types, or business rules from the `domain` module directly. A developer working on either the frontend or backend needs to reference API contracts and data transfer models from the `api` module to ensure consistent communication between `web` and `app`.

**Why this priority**: The domain module's value comes from being a single source of truth for business logic consumed by `app`. The api module provides the contract layer that connects `web` and `app`, ensuring consistent data exchange without tight coupling.

**Independent Test**: Can be tested by confirming that `app` can reference `domain` definitions directly, and both `web` and `app` can reference `api` contracts without code duplication.

**Acceptance Scenarios**:

1. **Given** the `domain` module defines a shared entity or type, **When** the `app` module references it, **Then** the reference resolves correctly without copying the definition.
2. **Given** the `api` module defines a contract or data transfer model, **When** the `app` module references it, **Then** the reference resolves correctly.
3. **Given** the `api` module defines a contract or data transfer model, **When** the `web` module references it, **Then** the reference resolves correctly.
4. **Given** a change is made to a definition in `domain`, **When** `app` is rebuilt, **Then** the updated definition is reflected automatically.
5. **Given** a change is made to a contract in `api`, **When** either `web` or `app` is rebuilt, **Then** the updated contract is reflected automatically.

---

### User Story 3 - Independent Module Development (Priority: P3)

A developer wants to work on one module (e.g., `web`) without needing to understand or modify the internals of the other modules. Each module should be independently buildable and testable.

**Why this priority**: Independent module development improves developer productivity and enables parallel work streams, but is an enhancement on top of the basic structure.

**Independent Test**: Can be tested by building and running tests within a single module without requiring the other modules to be built first.

**Acceptance Scenarios**:

1. **Given** the project structure exists, **When** a developer runs tests within the `web` module only, **Then** the tests execute successfully without requiring `app`, `domain`, or `api` to be pre-built.
2. **Given** the project structure exists, **When** a developer runs tests within the `app` module only, **Then** the tests execute successfully without requiring `web` to be pre-built.
3. **Given** the project structure exists, **When** a developer runs tests within the `domain` module only, **Then** the tests execute successfully in isolation.
4. **Given** the project structure exists, **When** a developer runs tests within the `api` module only, **Then** the tests execute successfully in isolation.

---

### Edge Cases

- What happens when the project directory already contains files or directories with conflicting names (`web`, `app`, `domain`, `api`)?
- How does the system handle creating the structure in a directory that is not empty?
- What happens when a developer accidentally deletes or renames one of the module directories?

**Out of Scope**: These edge cases apply to automated scaffolding tools, not a one-time manual structure setup. Conflicting names are resolved by the developer before setup. Directory recovery is handled by version control (git restore). No automated guard-rails are required for this feature.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST create four distinct top-level module directories named `web`, `app`, `domain`, and `api`.
- **FR-002**: System MUST provide each module with its own source code directory for organizing implementation files.
- **FR-003**: System MUST provide each module with its own configuration file appropriate to its role (frontend config for `web`, backend config for `app`, library config for `domain`, contract/schema config for `api`).
- **FR-004**: System MUST establish a dependency relationship where `app` depends on `domain` and `api`, `web` depends on `api`, `api` depends on `domain`, but `domain` does not depend on `web`, `app`, or `api`.
- **FR-005**: System MUST provide a root-level project configuration that ties all four modules together as a cohesive project.
- **FR-006**: System MUST ensure each executable module (`app`, `web`) has a clearly defined entry point or main file. Library modules (`domain`, `api`) do not require entry points.
- **FR-007**: System MUST provide a root-level command to build or validate the entire project across all modules.

### Key Entities

- **Project**: The top-level container that holds all modules together, with a root configuration defining the module list and shared settings.
- **Module**: A self-contained unit of the project (`web`, `app`, `domain`, or `api`) with its own source directory and configuration. Executable modules (`app`, `web`) additionally have an entry point.
- **Domain Module**: A module that contains shared business logic, data models, validation rules, and shared types consumed directly by `app` only.
- **API Module**: A module that defines API contracts, data transfer objects, and interface specifications consumed by both `web` and `app` to ensure consistent communication.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Each executable module (`app`, `web`) contains a documented entry point, and all modules include quickstart instructions enabling a developer to locate where to begin writing code immediately after project creation.
- **SC-002**: The project structure is fully set up by executing the task list sequentially without manual improvisation — no additional directory creation or configuration editing beyond what the tasks prescribe.
- **SC-003**: Shared business logic SHOULD reside in the `domain` module to minimize duplication across `web` and `app`.
- **SC-004**: Each module can be independently tested without requiring the full project to be built first.
- **SC-005**: Root-level documentation (README or quickstart) exists that describes each module's purpose, boundaries, and responsibilities in a reviewable format.

## Assumptions

- The project targets a monorepo-style layout where all modules live in a single repository.
- The `domain` module is the foundational layer with no dependencies on other modules.
- The `api` module depends on `domain` (contracts reference domain types), serving as the contract layer for communication between `web` and `app`.
- The `app` module depends on both `domain` and `api`.
- The `web` module (npm/React) depends on `api` only (not `domain` directly) due to ecosystem differences; it references generated TypeScript types from `api` via relative path to the `api` build output directory (`../api/target/generated-sources/typescript/`).
- The dependency graph is: `web` → `api` → `domain` ← `app`.
- Standard industry conventions are followed for directory naming and configuration patterns within each module.
- The project will use a single version control repository for all four modules.
- Each module uses different tooling appropriate to its role (see Technology Assumptions below).
- The `web` module is responsible for user-facing presentation and interaction.
- The `app` module is responsible for server-side logic, API endpoints, and data processing.
- The `domain` module contains pure business logic, data models, validation rules, and shared types with no infrastructure concerns.
- The `api` module contains API contracts, data transfer objects, and interface specifications defined in Java as the single source of truth, with TypeScript types generated for `web` consumption.
- Module entry points (e.g., `Application.java`, `main.tsx`) are minimal scaffolding exempt from the Test-First principle; they are validated indirectly through standalone module tests.

### Technology Assumptions

- **web**: React with Vite and TypeScript
- **app**: Java 21 (JDK 21), Spring Boot, Lombok, Maven
- **domain**: Java 21 (JDK 21), Lombok, Maven (shared as a library consumed by `app`)
- **api**: Java 21 (JDK 21), Maven — defines interfaces and DTOs as the single source of truth; TypeScript types are generated from Java definitions using typescript-generator Maven plugin (Vojtech Habarta) into `target/generated-sources/typescript/api-models.ts`, consumed by `web` via relative path
