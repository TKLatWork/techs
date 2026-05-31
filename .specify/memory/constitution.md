<!--
SYNC IMPACT REPORT
==================
Version change: N/A → 1.0.0 (Initial constitution)
Bump rationale: MAJOR - First ratification of project constitution

Modified principles: N/A (new document)

Added sections:
- Core Principles: I. Test-First (NON-NEGOTIABLE)
- Additional Constraints: Technology Agnostic Development
- Development Workflow: Quality Gates & Review Process
- Governance: Amendment procedure, versioning policy, compliance review

Removed sections:
- Principle placeholders II-V (reduced from 5 to 1 principle per user request)
- Section 2 placeholder (replaced with concrete constraints)
- Section 3 placeholder (replaced with concrete workflow)

Templates requiring updates:
- .specify/templates/plan-template.md: ✅ No changes needed (Constitution Check section is generic)
- .specify/templates/spec-template.md: ✅ No changes needed (spec aligns with test-first via acceptance scenarios)
- .specify/templates/tasks-template.md: ✅ No changes needed (already supports TDD workflow)
- .specify/templates/commands/*.md: ⚠ Pending manual review for constitution references

Follow-up TODOs:
- TODO(TECH_STACK): Technology stack not specified - to be defined in first feature plan
- TODO(REVIEW_PROCESS): Code review requirements not specified - to be defined as project scales
-->

# Techs Constitution

## Core Principles

### I. Test-First (NON-NEGOTIABLE)

All production code MUST be preceded by failing tests that define the expected behavior. The Red-Green-Refactor cycle is strictly enforced:

- **Red**: Write tests that fail. Tests MUST be written before any implementation code.
- **Green**: Implement the minimum code required to make tests pass.
- **Refactor**: Improve code structure while keeping tests green.

Non-negotiable rules:
- No code merges without passing tests
- Test coverage MUST NOT decrease on any commit
- Tests MUST be independent, repeatable, and fast
- Test failures MUST be addressed before new feature work begins

**Rationale**: Test-first development ensures code is designed for testability, prevents regression, and serves as living documentation of expected behavior.

## Additional Constraints

### Technology Agnostic Development

Technology choices MUST be justified by feature requirements, not personal preference. Each feature plan MUST document:

- Selected technology and version
- Rationale for selection
- Alternatives considered and rejection criteria

**Rationale**: Prevents technology sprawl and ensures decisions are traceable to business needs.

## Development Workflow

### Quality Gates

Every feature MUST pass these gates before merging:

1. **Specification Gate**: Feature spec exists with user scenarios, functional requirements, and measurable success criteria
2. **Plan Gate**: Technical plan exists with constitution compliance check passed
3. **Test Gate**: All tests pass, coverage meets or exceeds baseline
4. **Review Gate**: Code reviewed by at least one team member (or self-review documented for solo projects)

**Rationale**: Quality gates prevent incomplete or unverified work from reaching the main branch.

## Governance

### Amendment Procedure

This constitution supersedes all other development practices. Amendments require:

1. A proposed change with rationale
2. Impact analysis on existing principles and workflows
3. Approval from project maintainers
4. Migration plan for existing code if principle changes are backward-incompatible
5. Version increment per semantic versioning rules

### Versioning Policy

Constitution versions follow semantic versioning:

- **MAJOR**: Backward-incompatible principle removals or redefinitions
- **MINOR**: New principles added or existing guidance materially expanded
- **PATCH**: Clarifications, wording improvements, typo fixes

### Compliance Review

All PRs and code reviews MUST verify constitution compliance. Violations MUST be either:

- Resolved before merging, OR
- Explicitly documented in the Complexity Tracking section of the plan with justification

**Version**: 1.0.0 | **Ratified**: 2026-05-31 | **Last Amended**: 2026-05-31
