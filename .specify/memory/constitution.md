<!--
SYNC IMPACT REPORT
==================
Version change: (none) → 1.0.0 (initial ratification)

Modified principles: N/A (initial version)
Added sections:
  - Core Principles (3): Spec-Driven, Domain-Driven, Test-Driven
  - Governance
Removed sections: N/A

Templates requiring updates:
  - .specify/templates/plan-template.md ✅ no changes needed (Constitution Check is generic)
  - .specify/templates/spec-template.md ✅ no changes needed (structure aligns with spec-driven)
  - .specify/templates/tasks-template.md ✅ no changes needed (test-first already enforced)
  - .specify/templates/checklist-template.md ✅ no changes needed (generic structure)

Follow-up TODOs:
  - TODO(DOCS_TEMPLATES): Create docs/template/ directory with document templates
    as required by Principle I (Spec-Driven)
  - TODO(DOMAIN_GLOSSARY): Create domain glossary document as required by
    Principle II (Domain-Driven)
-->

# techs Constitution

## Core Principles

### I. Spec-Driven

All documentation, specifications, API contracts, and test cases MUST be
authored before implementation begins.

- All project documentation MUST reside under the `docs/` directory.
- Every document MUST conform to a template located in `docs/template/`.
- Creating documents that do not follow an existing template is prohibited.
- If a needed document type has no template, one MUST be created in
  `docs/template/` before authoring the document.
- After each specification cycle (when a change is complete), a review
  MUST be conducted to close gaps between documentation and code.
- API contracts (OpenAPI, GraphQL schema, or equivalent) MUST be defined
  and agreed upon before implementing endpoints or integrations.

**Rationale**: Specifications serve as the single source of truth. Writing
specs first forces clarity of intent, reduces rework, and ensures the
codebase remains aligned with documented behavior over time.

### II. Domain-Driven

A shared ubiquitous language MUST be documented and consistently used
across all project artifacts to reduce communication noise.

- Domain terms, definitions, and boundaries MUST be documented in a
  glossary accessible to all contributors.
- The codebase MUST enforce separation into three layers:
  - **Domain**: Pure business logic, entities, value objects, and rules
    with no infrastructure dependencies.
  - **Infrastructure**: External concerns (databases, APIs, file systems,
    messaging) that implement domain interfaces.
  - **Implementation (Application)**: Orchestration layer that coordinates
    domain operations using infrastructure services.
- Code reviews MUST verify that domain logic does not leak into
  infrastructure and that infrastructure concerns do not pollute the
  domain layer.
- New features MUST be expressed in domain language before being
  translated into technical implementation.

**Rationale**: Clear domain boundaries and shared language prevent
architectural drift, make the system easier to reason about, and enable
new contributors to onboard quickly.

### III. Test-Driven

Tests MUST be written before implementation. Documentation MUST include
or reference the testing strategy for each feature.

- No production code MUST be written until a failing test exists that
  defines the desired behavior.
- The Red-Green-Refactor cycle MUST be followed: write a failing test
  (Red), implement the minimum code to pass (Green), then refactor
  while keeping tests passing.
- Test cases MUST be derived from specifications and acceptance criteria
  defined in the spec documents.
- Each specification document MUST include or reference test scenarios
  that validate the described behavior.
- Tests MUST be treated as living documentation and kept up to date
  when specifications change.

**Rationale**: Tests validate that implementation matches specification.
Writing tests first ensures every line of code has a defined purpose and
prevents untested behavior from entering the codebase.

## Governance

This constitution supersedes all other development practices within this
project. No workflow, tool, or convention may override these principles
without a formal amendment.

**Amendment Procedure**:
1. Propose the change in a document under `docs/` following the
   appropriate template.
2. Document the rationale, impact, and migration plan for existing work.
3. Obtain approval from project maintainers.
4. Update this constitution and increment the version number.

**Versioning Policy**:
- MAJOR: Backward-incompatible principle removals or redefinitions.
- MINOR: New principles or sections added, or materially expanded guidance.
- PATCH: Clarifications, wording improvements, or typo fixes.

**Compliance Review**:
- All code reviews MUST verify adherence to the three core principles.
- Complexity that violates Simplicity or Domain-Driven boundaries MUST
  be documented with justification in the relevant spec.
- The spec-driven review after each cycle MUST confirm documentation
  accuracy matches the implemented code.

**Version**: 1.0.0 | **Ratified**: 2026-06-22 | **Last Amended**: 2026-06-22
