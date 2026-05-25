<!--
SYNC IMPACT REPORT
- Version change: N/A (template) → 1.0.0
- Added principles: Multi-Module Architecture, JWT Stateless Auth, Domain-Driven Design, Test-First (NON-NEGOTIABLE), Protected Routes & API, API Contract First, Independent Testability
- Removed sections: Template placeholders
- Templates requiring updates:
  - .specify/templates/plan-template.md: ✅ Constitution Check placeholder present (no principle-specific refs)
  - .specify/templates/spec-template.md: ✅ No constitution refs to update
  - .specify/templates/tasks-template.md: ✅ No constitution refs to update
- Follow-up TODOs: RATIFICATION_DATE - unknown, marked TODO
-->

# MyApp Constitution

## Core Principles

### I. Multi-Module Architecture

Parent POM aggregates backend and frontend modules; each module must be independently buildable, testable, and deployable. No circular dependencies between modules. Clear module boundaries enforced.

### II. JWT Stateless Auth

All authentication via JWT tokens; no server-side sessions. Tokens stored in localStorage, transmitted via Authorization header. Token generation, validation, and refresh handled by dedicated services. Stateless by design.

### III. Domain-Driven Design

Backend follows DDD patterns: Entities, Value Objects, Repositories, Domain Services, Application Services, and Aggregates. Bounded contexts define module boundaries. Domain logic lives in the domain layer, not in controllers or infrastructure. Ubiquitous language in code.

### IV. Test-First (NON-NEGOTIABLE)

TDD mandatory: Tests written → User approved → Tests fail → Then implement. Red-Green-Refactor cycle strictly enforced. Backend: JUnit 5 + Mockito + @SpringBootTest. Frontend: Vitest + React Testing Library. Tests verify external behavior, not implementation details. Cover happy paths and error cases.

### V. Protected Routes & API

Frontend: ProtectedRoute component wraps routes requiring authentication, redirecting unauthenticated users. Backend: Spring Security filter chain with JwtAuthenticationFilter. All sensitive endpoints require valid JWT. Defense in depth - both layers must enforce auth.

### VI. API Contract First

Backend endpoints and request/response contracts defined before frontend integration. Axios client configured with auth interceptor to attach Bearer tokens. Contracts are the source of truth for client-server communication.

### VII. Independent Testability

Each module, component, and domain aggregate must be testable in isolation. Unit tests for domain logic, integration tests for cross-cutting concerns, contract tests for API boundaries. Tests are independent and do not share state.

## Additional Constraints

**Technology Stack**: Java 21, Spring Boot 3.2.5, React 18, Node 20, Maven multi-module build.

**Database**: H2 in-memory for dev/test. Production database TBD - must support JPA/Hibernate.

**Build**: Maven parent POM orchestrates backend and frontend builds. Frontend built via frontend-maven-plugin during Maven lifecycle.

**Issue Tracking**: Local markdown under `.scratch/<feature-slug>/`. Triage labels: `needs-triage`, `needs-info`, `ready-for-agent`, `ready-for-human`, `wontfix`.

## Governance

This constitution supersedes all other development practices. Amendments require documentation of changes, version bump, and migration plan if applicable.

All PRs and code reviews must verify compliance with these principles. Any deviation must be justified with documented rationale. Complexity must be justified by domain requirements.

Use `AGENTS.md` and `.kilocode/rules/specify-rules.md` for runtime development guidance.

**Version**: 1.0.0 | **Ratified**: TODO(RATIFICATION_DATE): original adoption date unknown | **Last Amended**: 2026-05-25
