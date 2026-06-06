# Review: Constitution Sections III & IV — Internal Conflict Analysis

## Verdict: No direct conflicts. Two gaps and one redundancy need attention.

Sections III (Feature-Based Module Structure) and IV (DDD Code Rules) are **internally consistent** in their normative rules. They address complementary concerns — III defines *where code lives*, IV defines *what code is*. However, cross-referencing reveals gaps and a redundancy that could cause confusion during implementation.

---

## Compatible Rules (no issues)

| Section III Rule | Section IV Rule | Relationship |
|-----------------|----------------|-------------|
| Domain feature packages have `entity/`, `valueobject/`, `service/` | Defines what Entities, Value Objects, and Domain Services are | III provides the folders, IV provides the contents |
| Domain: "No infrastructure concerns" | Anti-pattern: "Infrastructure leakage" | Mutually reinforcing |
| App: "MUST delegate business rules to domain services" | Application Services: "MUST delegate business rules to domain services" | Same rule stated twice (see Redundancy below) |
| App has `repository/` package | Repository implementations in app module | Compatible — implementations go in `app/repository/` |
| Cross-feature imports forbidden in domain | External code references only aggregate roots | Complementary — aggregate roots are part of the public contract |
| App exempt from cross-feature restriction | Application Services orchestrate domain objects | Compatible — app needs cross-feature access to compose services |

---

## Gap 1: Repository interfaces have no home in domain feature packages

**Section III** (line 113-114) defines domain feature sub-packages as:
> `entity/`, `valueobject/`, `service/`

**Section IV** (line 173) requires:
> "Repositories MUST be defined as interfaces in the domain module"

**Problem**: Section III's domain sub-package list does not include `repository/`. There is no specified location for repository interfaces within a domain feature package. Options are:

- Add `repository/` to III's domain sub-package list (recommended)
- Place repository interfaces in `service/` (semantically wrong — repositories are not services)
- Place them at the feature package root (unconventional, no precedent in III)

**Impact**: Without resolution, developers will be unsure where to put repository interfaces, and different features may place them inconsistently.

**Suggested fix**: Update Section III domain rules to:
> Feature packages under `com.techs.domain.<feature>/` with DDD-style sub-packages: `entity/`, `valueobject/`, `service/`, `repository/`

---

## Gap 2: Aggregate root access rule and cross-feature contract rule use different terminology

**Section III** (line 88-91) defines cross-feature access in terms of:
> "public service interfaces and API contracts (outer contracts)"

**Section IV** (line 160-162) defines external access in terms of:
> "External code MUST only reference aggregate roots, never internal entities directly"

**Problem**: These two rules govern the same concern (what can be referenced across boundaries) but use different vocabularies without connecting them. A developer reading III alone would think cross-feature access is about service interfaces and DTOs. A developer reading IV alone would think it's about aggregate roots. Neither section clarifies that aggregate roots, domain service interfaces, and value objects collectively form the "public contract" of a feature.

**Impact**: Ambiguity about whether referencing another feature's entity directly (without going through its service interface) is allowed. III could be read as permitting it (entities are not "internal implementations" if they're aggregate roots), while IV forbids it.

**Suggested fix**: Add a clarifying sentence to Section III's cross-feature import rule:
> A feature's public contract consists of its aggregate roots, domain service interfaces, and value objects. Internal entities that are not aggregate roots MUST NOT be referenced externally.

---

## Redundancy: Business logic delegation rule stated in both sections

**Section III** (line 132-133):
> "App code MUST delegate business rules to domain services, never implement business logic directly"

**Section IV** (line 169-170):
> Application Services "MUST delegate business rules to domain services, never implement business logic directly"

This is the same rule stated twice with identical wording. Not a conflict, but a maintenance risk — if one is updated without the other, they could diverge. Since III is NON-NEGOTIABLE and IV is RECOMMENDED, the authoritative statement should be in III, and IV should reference it rather than restate it.

**Suggested fix**: In Section IV, replace the restated rule with a reference:
> Application Services (in app module): Orchestrate domain objects and infrastructure. Per Principle III, MUST delegate business rules to domain services.

---

## Minor Observation: Section IV scope claim overreaches

Section IV (line 147) states:
> "All Java modules SHOULD follow Domain-Driven Design (DDD) conventions"

But the API module is a contract/DTO layer, not a domain concern. DDD tactical patterns (entities, value objects, aggregate roots, domain services, repositories) don't apply to DTOs. Section III correctly treats the API module separately with its own rules (immutability, `@JsonProperty`). Section IV's scope should be clarified as applying to the **domain and app modules**, not "all Java modules."

---

## Summary

| Finding | Type | Severity | Action |
|---------|------|----------|--------|
| No `repository/` in domain sub-package list | Gap | Medium | Add `repository/` to III's domain sub-packages |
| Aggregate root vs cross-feature contract terminology mismatch | Gap | Low | Clarify that aggregate roots are part of the public contract in III |
| Business logic delegation rule duplicated | Redundancy | Low | IV should reference III instead of restating |
| IV scope says "all Java modules" but DDD doesn't apply to API module | Overreach | Low | Narrow IV scope to domain and app modules |
