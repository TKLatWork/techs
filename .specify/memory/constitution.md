<!--
SYNC IMPACT REPORT
==================
Version change: 1.0.0 → 1.1.0
Bump rationale: MINOR - New principle added (Canonical-Driven Documentation)

Modified principles: None

Added sections:
- Core Principles: II. Canonical-Driven Documentation (NON-NEGOTIABLE)

Removed sections: None

Templates requiring updates:
- .specify/templates/plan-template.md: ✅ No changes needed (Constitution Check section is generic)
- .specify/templates/spec-template.md: ✅ No changes needed (spec aligns with documentation via user scenarios)
- .specify/templates/tasks-template.md: ✅ No changes needed (already supports documentation tasks in Polish phase)
- .specify/templates/commands/*.md: ✅ No command templates exist

Follow-up TODOs: None
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

### II. Canonical-Driven Documentation (NON-NEGOTIABLE)

All code changes MUST comply with Canonical-Driven Development (CDD) as enforced by DocGuard. Documentation is the source of truth for design intent and implementation state.

Non-negotiable rules:
- Canonical docs in `docs-canonical/` represent design intent and are READ-ONLY
- Implementation docs in `docs-implementation/` MUST reflect the current codebase state
- All deviations from canonical docs MUST be logged in `DRIFT-LOG.md`
- `npx docguard guard` MUST pass before any commit is merged
- All changes MUST include a `CHANGELOG.md` entry
- Schema or data model changes MUST update `DATA-MODEL.md`

Documentation workflow:
1. Run `npx docguard guard` before starting work to understand compliance state
2. Run `npx docguard fix --format prompt` after making changes to identify issues
3. Resolve all reported issues before committing
4. Run `npx docguard guard` again to verify compliance

**Rationale**: Documentation drift erodes team alignment and introduces hidden complexity. CDD ensures design intent remains authoritative and implementation state is always verifiable.

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
4. **Documentation Gate**: `npx docguard guard` passes with no errors, `CHANGELOG.md` updated, drift logged if applicable
5. **Review Gate**: Code reviewed by at least one team member (or self-review documented for solo projects)

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

**Version**: 1.1.0 | **Ratified**: 2026-05-31 | **Last Amended**: 2026-05-31
