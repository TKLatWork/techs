# SNOW Job: All Methods Implementation + Loop Refactor Plan

## Context

The Tooling-SNOW Jenkins job (`jobs/dev/Tooling-SNOW/Jenkinsfile`) implements a job-as-function pattern that routes to SNOW API methods via `JOB_FUNCTION` parameter. The SNOW API (`resources/SNOW-api/README.md`) defines 6 methods. All 6 are currently implemented.

**Current state:**
- SnowClient (`libs/JK-Lib/src/com/jklib/snow/SnowClient.groovy`): 6 methods implemented
- SnowModels (`libs/JK-Lib/src/com/jklib/snow/SnowModels.groovy`): 6 Input/Output pairs implemented
- Jenkinsfile: switch-case routing for all 6 methods (~50 lines in Execute stage)

**Problem:** Same as ICE — Execute stage exceeds 15-line guideline, switch-case repeats the same pattern, filter-building logic is in the Jenkinsfile instead of the model.

## Part 1: Verify All Methods Implemented

All 6 SNOW API methods are implemented. Verification checklist:

| Method | Client | Input Model | Output Model | Jenkinsfile Case |
|---|---|---|---|---|
| createChangeRequest | SnowClient:51 | CreateChangeRequestInput:5 | CreateChangeRequestOutput:27 | Line 25 |
| getChangeRequest | SnowClient:74 | GetChangeRequestInput:45 | GetChangeRequestOutput:55 | Line 31 |
| updateChangeRequest | SnowClient:79 | UpdateChangeRequestInput:69 | UpdateChangeRequestOutput:89 | Line 37 |
| listChangeRequests | SnowClient:98 | ListChangeRequestsInput:103 | ListChangeRequestsOutput:121 | Line 43 |
| approveChangeRequest | SnowClient:110 | ApproveChangeRequestInput:135 | ApproveChangeRequestOutput:147 | Line 55 |
| closeChangeRequest | SnowClient:118 | CloseChangeRequestInput:161 | CloseChangeRequestOutput:175 | Line 61 |

**No new methods to implement.** Part 1 is a verification pass only.

## Part 2: Loop Refactor (RefactorLoop Skill)

### Scope
- Primary: `jobs/dev/Tooling-SNOW/Jenkinsfile`
- Secondary (if needed): `libs/JK-Lib/src/com/jklib/snow/SnowModels.groovy`
- Record folder: `./refact/` (already exists from ICE refactor)

### Loop limit
- Default: 4 loops
- Early break: if no sensible advice at loop 2 (half limit)

### Loop 1: Extract dispatch from switch-case

**Review focus:** Functional pattern review (ImplementReview checklist #2)

**Expected advice:** Replace the 6-case switch with a functional function `executeSnowFunction()`. Same pattern as ICE Loop 1.

**Target:** Pipeline Execute stage should shrink from ~50 lines to ~3 lines of script.

### Loop 2: Simplify listChangeRequests filter building

**Review focus:** Technical pattern review (ImplementReview checklist #3)

**Expected advice:** The manual filter-building block (lines 45-50, 5 `if` statements) can be replaced with a `toFilters()` method on `ListChangeRequestsInput`, same pattern as ICE Loop 2.

**Target:** Replace 5 if-blocks with a model method call.

### Loop 3: Pipeline code style compliance

**Review focus:** Rule check (ImplementReview checklist #1) against `docs/JenkinsJob/AGENTS.md`

**Expected advice:** Verify Execute stage ≤15 lines, structure order correct, etc. Likely no changes needed (same outcome as ICE Loop 3).

### Loop 4: Final review and polish

**Review focus:** Full ImplementReview checklist (all 3 items)

**Target:** Catch any remaining issues. If no sensible advice found, break early.

### Refactor Records

Each loop produces a record in `./refact/`:
```
refact/
  snow-loop-1.md
  snow-loop-2.md
  snow-loop-3.md
  snow-loop-4.md (if needed)
  snow-final-report.md
```

Each record follows the template from `docs/agents/RefactorLoop.md`.

### Rollback rule
If a loop's result is worse, rollback and try a different layer/focus.

## Execution Order

1. Run Loop 1: Extract dispatch → implement → review → record
2. Run Loop 2: Simplify filter building → implement → review → record
3. Run Loop 3: Style compliance → implement → review → record (or break early)
4. Run Loop 4: Final polish → implement → review → record (or break early)
5. Write final report to `./refact/snow-final-report.md`

## Validation

- All 6 JOB_FUNCTIONs still return code=200 on success
- Invalid JOB_FUNCTION still triggers code=500
- Execute stage ≤15 lines after refactor
- No behavior change — input/output contracts unchanged
- README.md (`jobs/dev/Tooling-SNOW/README.md`) stays accurate
