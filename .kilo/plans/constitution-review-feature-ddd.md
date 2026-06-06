# Plan: Constitution Review — Feature-Based Module Structure & DDD Code Rules

**Date**: 2026-06-07
**Scope**: Update `.specify/memory/constitution.md` Principles III and IV
**Version**: 1.2.1 → 2.0.0 (MAJOR — backward-incompatible redefinition of app module structure rules)

---

## Summary

Update constitution Principles III (Feature-Based Module Structure) and IV (Domain-Driven Design Code Rules) based on user review feedback:

1. **Cross-feature imports**: Relax from "FORBIDDEN" to "FORBIDDEN via internal implementations; MUST use public service interfaces/contracts"
2. **App module structure**: Remove feature-based sub-packages — app is flat/shared, organized by technical concern
3. **Test dependencies**: Acknowledge tests inherit feature dependencies
4. **Remove misplaced rules**: DTO/@JsonProperty rules belong in API module, not app

---

## Changes to Principle III (Feature-Based Module Structure)

### 3a. Update cross-feature import rule (lines 84-85)

**Current**:
```
- Cross-feature imports within the domain, api, and web modules are FORBIDDEN;
  features may only import from `shared`
```

**New**:
```
- Cross-feature imports of internal implementations within the domain, api,
  and web modules are FORBIDDEN; features MAY reference other features' public
  service interfaces and API contracts (outer contracts), but MUST NOT import
  internal classes directly
```

### 3b. Update independent testability rule (line 89)

**Current**:
```
- Each feature MUST be independently buildable and testable within its module
```

**New**:
```
- Each feature MUST be independently buildable within its module; tests
  inherently carry their feature's dependencies and MAY reference the same
  cross-feature contracts permitted in production code
```

### 3c. Rewrite App module section (lines 116-128)

**Current**: Feature-based with DDD sub-packages (`com.techs.app.<feature>/controller/`, etc.)

**New**:
```
**App** (Java/Spring Boot) — *shared orchestration layer*:
- The app module is the orchestration layer that wires domain features into
  working endpoints. It is inherently cross-feature and does NOT use
  feature-based sub-packages.
- All code lives under `com.techs.app/` organized by technical concern:
  `controller/`, `service/`, `repository/`, `config/`, `security/`, `seeder/`
- Cross-feature imports ARE permitted — app code composes domain services
  from multiple features
- App code MUST delegate business rules to domain services, never implement
  business logic directly
```

### 3d. Update rationale (lines 130-136)

Update to reflect that app module is flat/shared rather than feature-based with cross-feature exemption.

---

## Changes to Principle IV (DDD Code Rules)

### 4a. No structural changes needed

Principle IV (RECOMMENDED) remains as-is. The DDD conventions for entity classification, service classification, repository pattern, and anti-patterns are unchanged.

The app module structure change in Principle III already removes the misplaced DDD sub-package prescription. Principle IV's guidance on domain services vs application services remains valid.

---

## Consistency Propagation

### Templates to check

| Template | Action |
|----------|--------|
| `.specify/templates/plan-template.md` | Check Constitution Check table (lines 47-62) — update "Cross-feature imports only in app module?" note to reflect contract-based cross-feature access |
| `.specify/templates/spec-template.md` | No changes needed (no constitution-specific constraints) |
| `.specify/templates/tasks-template.md` | No changes needed (no principle-specific task types affected) |
| `.specify/templates/commands/*.md` | No command templates exist |

### Plan template update (line 61)

**Current**: `Cross-feature imports only in app module?`
**New**: `Cross-feature refs use public contracts? App module flat/shared?`

---

## Sync Impact Report (to be prepended to constitution)

```
Version change: 1.2.1 → 2.0.0
Bump rationale: MAJOR — Backward-incompatible redefinition of app module
  structure (feature-based → flat/shared) and relaxation of cross-feature
  import rules (FORBIDDEN → contracts-only)

Modified principles:
- III. Feature-Based Module Structure:
  - Cross-feature rule: FORBIDDEN → forbidden for internals, allowed via
    public contracts
  - Testability rule: clarified test dependency inheritance
  - App module: feature-based sub-packages → flat by technical concern

Added sections: None
Removed sections: None

Templates requiring updates:
- .specify/templates/plan-template.md: ✅ updated (Constitution Check table)
- .specify/templates/spec-template.md: ✅ No changes needed
- .specify/templates/tasks-template.md: ✅ No changes needed
- .specify/templates/commands/*.md: ✅ No command templates exist

Follow-up TODOs: None
```

---

## Execution Steps

1. Update `.specify/memory/constitution.md` with all changes above
2. Update `.specify/templates/plan-template.md` Constitution Check table
3. Prepend Sync Impact Report to constitution
4. Update version line: `**Version**: 2.0.0 | **Ratified**: 2026-05-31 | **Last Amended**: 2026-06-07`
5. Validate: no unexplained bracket tokens, dates in ISO format, principles declarative

---

## Suggested Commit Message

```
docs: amend constitution to v2.0.0 (app module flat/shared, contract-based cross-feature imports)
```
