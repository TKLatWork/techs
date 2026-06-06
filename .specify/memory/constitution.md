<!--
SYNC IMPACT REPORT
==================
Version change: 2.0.1 → 2.1.0
Bump rationale: MINOR — Principle I redefined from "Test-First" to "TDD
(Test-Driven Development)" with explicit three-layer testing pyramid. The
testing discipline is materially expanded: domain unit tests, API E2E
tests, and web E2E tests are now mandated as the required test layers.

Modified principles:
- I. Test-First → I. TDD (Test-Driven Development):
  - Renamed from "Test-First" to "TDD" to emphasize the full
    Red-Green-Refactor cycle as a development methodology, not just
    a test-writing prerequisite
  - Added three-layer testing pyramid: domain (unit), API (E2E/
    integration), web (E2E/browser)
  - Each layer has explicit scope, tools, and responsibility

Added sections: None
Removed sections: None

Templates requiring updates:
- .specify/templates/plan-template.md: ⚠ Updated — Constitution Check
  table renamed "Test-First (I)" to "TDD (I)" and added testing layer
  verification notes
- .specify/templates/spec-template.md: ✅ No changes needed
- .specify/templates/tasks-template.md: ⚠ Updated — Test task sections
  now reference three-layer testing pyramid (domain unit, API E2E, web
  E2E) instead of generic contract/integration tests
- .specify/templates/commands/*.md: ✅ No command templates exist

Follow-up TODOs: None
-->

# Techs Constitution

## Core Principles

### I. TDD — Test-Driven Development (NON-NEGOTIABLE)

All production code MUST be developed using Test-Driven Development. The
Red-Green-Refactor cycle is strictly enforced:

- **Red**: Write failing tests that define the expected behavior. Tests MUST
  be written before any implementation code.
- **Green**: Implement the minimum code required to make tests pass.
- **Refactor**: Improve code structure while keeping tests green.

Non-negotiable rules:
- No code merges without passing tests at all three layers
- Test coverage MUST NOT decrease on any commit
- Tests MUST be independent, repeatable, and fast
- Test failures MUST be addressed before new feature work begins

**Three-Layer Testing Pyramid**:

Testing is organized across three layers, each with a distinct scope and
responsibility. Every feature MUST include tests at all three layers:

1. **Domain Layer — Unit Tests**
   - Scope: Pure business logic in the domain module (entities, value
     objects, domain services, repository interfaces via mocks/fakes)
   - Tools: JUnit 5 (Java)
   - Responsibility: Validate business rules, invariants, state transitions,
     and effective-rights computation in isolation from infrastructure
   - Characteristics: Fast, no Spring context, no I/O, no network

2. **API Layer — E2E / Integration Tests**
   - Scope: HTTP endpoints in the app module tested against a running
     Spring Boot context with in-memory repositories
   - Tools: JUnit 5 + Spring Boot Test + MockMvc or TestRestTemplate
   - Responsibility: Validate request/response contracts, authentication
     flows, authorization enforcement, error responses, and data seeding
   - Characteristics: Full stack from controller to repository; verifies
     that domain services, app services, and Spring Security filters
     integrate correctly

3. **Web Layer — E2E / Browser Tests**
   - Scope: User-facing interactions in the web module tested through
     browser automation or component-level rendering tests
   - Tools: Vitest + @testing-library/react (component tests); Playwright
     or Cypress (full E2E, optional for proof-of-concept phase)
   - Responsibility: Validate user journeys (login, registration, profile
     view, permission management), route guards, form validation, error
     display, and redirect behavior
   - Characteristics: Tests the frontend as the user experiences it;
     component tests are fast, full E2E tests exercise the complete
     web → api → domain chain

Non-negotiable rules per layer:
- Domain unit tests MUST NOT import Spring, HTTP, or I/O classes
- API E2E tests MUST exercise the full Spring context (controller →
  service → repository) without mocking internal layers
- Web tests MUST verify both happy paths and error/edge-case paths
- Each user story MUST have at least one test at each applicable layer
  before implementation begins

**Rationale**: TDD with a three-layer testing pyramid ensures business
logic is verified in isolation (domain), integration points work correctly
(API), and user-facing behavior matches expectations (web). This prevents
regression at every level and serves as living documentation of expected
behavior across the full stack.

### II. Canonical-Driven Documentation (NON-NEGOTIABLE)

All code changes MUST comply with Canonical-Driven Development (CDD) as
enforced by DocGuard. Documentation is the source of truth for design intent
and implementation state.

Non-negotiable rules:
- Canonical docs in `docs-canonical/` represent design intent and are READ-ONLY
- Implementation docs in `docs-implementation/` MUST reflect the current
  codebase state
- All deviations from canonical docs MUST be logged in `DRIFT-LOG.md`
- `npx docguard guard` MUST pass before any commit is merged
- All changes MUST include a `CHANGELOG.md` entry
- Schema or data model changes MUST update `DATA-MODEL.md`

Documentation workflow:
1. Run `npx docguard guard` before starting work to understand compliance state
2. Run `npx docguard fix --format prompt` after making changes to identify issues
3. Resolve all reported issues before committing
4. Run `npx docguard guard` again to verify compliance

**Rationale**: Documentation drift erodes team alignment and introduces hidden
complexity. CDD ensures design intent remains authoritative and implementation
state is always verifiable.

### III. Feature-Based Module Structure (NON-NEGOTIABLE)

The domain, api, and web modules MUST organize code by feature, not by
technical layer. Each feature is a self-contained unit within every module it
touches. Adding a new feature means adding a new feature package in each
relevant module, not scattering code across unrelated directories. The app
module is the exception — it uses a flat, shared structure for orchestration.

Non-negotiable rules:
- Every module MUST have a `shared` package/directory for cross-cutting code
  used by 2+ features
- Cross-feature imports of internal implementations within the domain, api,
  and web modules are FORBIDDEN; a feature's public contract consists of its
  aggregate roots, domain service interfaces, and value objects — features MAY
  reference these public contracts and API DTOs, but MUST NOT import internal
  entities (non-aggregate-root) or internal implementation classes directly
- The app module is exempt from the cross-feature import restriction: as the
  orchestration and wiring layer, app packages MAY import across feature
  boundaries to compose features into working endpoints and services
- Each feature MUST be independently buildable within its module; tests
  inherently carry their feature's dependencies and MAY reference the same
  cross-feature contracts permitted in production code
- Placeholder or orphaned code that does not belong to a feature MUST be
  deleted, not left in root packages
- The first feature package in each module serves as the reference
  implementation for the structure

Module-specific structure rules:

**Web** (TypeScript/React):
- Feature folders under `src/features/<feature>/` with standard internal
  structure: `components/`, `hooks/`, `services/`, `types/`, `__tests__/`
- Route-level page wrappers in `src/pages/`
- App shell (router, providers, theme) in `src/app/`
- Shared infrastructure in `src/shared/`

**Domain** (Java):
- Feature packages under `com.techs.domain.<feature>/` with DDD-style
  sub-packages: `entity/`, `valueobject/`, `service/`, `repository/`
- Shared code in `com.techs.domain.shared/`
- No infrastructure concerns (no Spring annotations, no database annotations)

**API** (Java):
- Feature packages under `com.techs.api.<feature>.model/` for DTOs
- Shared DTOs in `com.techs.api.shared.model/`

**App** (Java/Spring Boot) — *shared orchestration layer*:
- The app module is the orchestration layer that wires domain features into
  working endpoints. It is inherently cross-feature and does NOT use
  feature-based sub-packages.
- All code lives under `com.techs.app/` organized by technical concern:
  `controller/`, `service/`, `repository/`, `config/`, `security/`
- Cross-feature imports ARE permitted — app code composes domain services
  from multiple features
- App code MUST delegate business rules to domain services, never implement
  business logic directly

**Rationale**: Layer-based organization (all controllers together, all services
together) in domain, api, and web modules creates implicit coupling between
unrelated features and makes it impossible to understand or modify a single
feature without navigating across the entire codebase. Feature-based
organization ensures each feature is a cohesive, independently deployable unit.
Cross-feature communication via public contracts (aggregate roots, domain
service interfaces, value objects, and API DTOs) preserves encapsulation while
enabling composition. The app module uses a flat, shared structure because its
role is orchestration — it composes features into working endpoints, which
inherently requires cross-feature references.

### IV. Domain-Driven Design Code Rules (RECOMMENDED)

The domain and app modules SHOULD follow Domain-Driven Design (DDD)
conventions for naming, structure, and responsibility boundaries. These rules
are recommended (not non-negotiable) but deviations MUST be documented in the
plan's Complexity Tracking section.

Recommended rules:

**Entity classification**:
- **Entities** (`entity/`): Objects with identity that change state over time.
  MUST have a unique identifier. Examples: `User`, `Role`, `Session`.
- **Value Objects** (`valueobject/`): Immutable objects defined by their
  attributes, not identity. MUST be implemented as Java records or final
  classes. Examples: `RoleType`, `RightAction`, `Money`.
- **Aggregate Roots**: Entities that serve as the entry point for a cluster of
  related entities. External code MUST only reference aggregate roots, never
  internal entities directly.

**Service classification**:
- **Domain Services** (in domain module): Business logic that doesn't naturally
  belong to an entity or value object. MUST be stateless and operate on domain
  objects. Examples: `AuthService`, `AuthorizationService`.
- **Application Services** (in app module): Orchestrate domain objects and
  infrastructure. Per Principle III, MUST delegate business rules to domain
  services, never implement business logic directly. Examples: `UserService`,
  `SessionService`.

**Repository pattern**:
- Repositories MUST be defined as interfaces in the domain module
- Implementations MUST live in the app module
- Repositories MUST return entities, never raw data structures
- Each aggregate root SHOULD have exactly one repository

**Anti-patterns to avoid**:
- Anemic domain model: entities with only getters/setters and no behavior
- Smart UI: business logic in controllers or frontend components
- Infrastructure leakage: domain module importing Spring, JDBC, or HTTP classes
- God service: a single service class handling multiple unrelated features
- Primitive obsession: using `String` for domain concepts that should be
  value objects (e.g., email addresses, usernames)

**Rationale**: DDD conventions keep business logic in the domain layer where it
belongs, prevent infrastructure concerns from leaking into pure business code,
and make the codebase navigable by domain concept rather than by technical
concern.

## Additional Constraints

### Technology Agnostic Development

Technology choices MUST be justified by feature requirements, not personal
preference. Each feature plan MUST document:

- Selected technology and version
- Rationale for selection
- Alternatives considered and rejection criteria

**Rationale**: Prevents technology sprawl and ensures decisions are traceable
to business needs.

## Development Workflow

### Quality Gates

Every feature MUST pass these gates before merging:

1. **Specification Gate**: Feature spec exists with user scenarios, functional
   requirements, and measurable success criteria
2. **Plan Gate**: Technical plan exists with constitution compliance check
   passed
3. **Test Gate**: All tests pass at all three layers (domain unit, API E2E,
   web E2E); coverage meets or exceeds baseline
4. **Documentation Gate**: `npx docguard guard` passes with no errors,
   `CHANGELOG.md` updated, drift logged if applicable
5. **Structure Gate**: New code follows feature-based module structure
   (Principle III); deviations documented in Complexity Tracking
6. **Review Gate**: Code reviewed by at least one team member (or self-review
   documented for solo projects)

**Rationale**: Quality gates prevent incomplete or unverified work from
reaching the main branch.

## Governance

### Amendment Procedure

This constitution supersedes all other development practices. Amendments
require:

1. A proposed change with rationale
2. Impact analysis on existing principles and workflows
3. Approval from project maintainers
4. Migration plan for existing code if principle changes are
   backward-incompatible
5. Version increment per semantic versioning rules

### Versioning Policy

Constitution versions follow semantic versioning:

- **MAJOR**: Backward-incompatible principle removals or redefinitions
- **MINOR**: New principles added or existing guidance materially expanded
- **PATCH**: Clarifications, wording improvements, typo fixes

### Compliance Review

All PRs and code reviews MUST verify constitution compliance. Violations MUST
be either:

- Resolved before merging, OR
- Explicitly documented in the Complexity Tracking section of the plan with
  justification

**Version**: 2.1.0 | **Ratified**: 2026-05-31 | **Last Amended**: 2026-06-07
