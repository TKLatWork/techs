# Final Report: Tooling-G3 Refactor

## Summary

Refactored the Tooling-G3 job-as-function across 3 files to eliminate DRY violations and improve code ownership. The main change was replacing a 6-case switch statement in the Jenkinsfile with a dispatch map + `execute()` pattern on model classes, and having the G3Client accept model objects instead of positional parameters.

## Line Count Before/After

| File | Before | After | Delta |
|---|---|---|---|
| `jobs/dev/Tooling-G3/Jenkinsfile` | 86 | 42 | **-44 (-51%)** |
| `libs/JK-Lib/src/com/jklib/G3Models.groovy` | 204 | 248 | +44 (+22%) |
| `libs/JK-Lib/src/com/jklib/G3Client.groovy` | 97 | 92 | -5 (-5%) |
| **Total** | **387** | **382** | **-5 (-1%)** |

Total line count is nearly unchanged, but the distribution shifted correctly — complexity moved from the pipeline (Jenkinsfile) to where it belongs (models and client).

## Changes by Loop

### Loop 1 — Jenkinsfile dispatch pattern (Accepted)
- Replaced 6-case switch with `FUNCTION_MAP` dispatch + `execute()` on each Input model
- Moved filter-building logic from Jenkinsfile to `ListReleasePackagesInput.toFilters()`
- Moved defaults (`artifacts`, `config_overrides`, `dry_run`, `rollback_on_failure`, `notify`, `force`) into `fromMap()` methods
- Added `FUNCTION_MAP` static field to `G3Models`
- Added `execute(G3Client)` method to all 6 Input classes

### Loop 2 — G3Models data conversion (No actionable advice)
- Evaluated generic `fromMap`/`fromApiResult`/`toMap` abstraction — rejected (readability cost too high)

### Loop 3 — G3Client model-accepting methods (Accepted)
- Changed all 6 client methods to accept their corresponding Input model instead of positional parameters
- Simplified all 6 `execute()` methods to `client.method(this)` pattern

### Loop 4 — Cross-file coherence (No actionable advice)
- Verified all 6 JOB_FUNCTION paths end-to-end
- Confirmed clean layered architecture: Jenkinsfile → G3Models → G3Client

## Advice Accepted vs Rejected

| # | Advice | Loop | Decision | Reason |
|---|---|---|---|---|
| 1 | Replace switch-case with dispatch map | 1 | **Accepted** | Major DRY violation — 6 identical cases |
| 2 | Move filter-building to model `toFilters()` | 1 | **Accepted** | Model should hold data convert logic per AGENTS.md |
| 3 | Move defaults into `fromMap()` | 1 | **Accepted** | Removes defensive coding from Jenkinsfile |
| 4 | Generic `bindFields()` for fromMap/toMap | 2 | **Rejected** | Hurts readability, complicates default handling |
| 5 | Client methods accept model objects | 3 | **Accepted** | Eliminates positional params, simplifies execute() |
| 6 | Cross-file coherence issues | 4 | **None found** | Architecture is clean after Loops 1-3 |

## Remaining Known Issues / Future Ideas

1. **Cross-job pattern duplication**: The Tooling-ICE and Tooling-SNOW jobs have identical switch-case structures. A shared `JobAsFunction` base pattern in JK-Lib could abstract the dispatch for all 3 jobs. Out of scope for this refactor but a natural next step.
2. **`fromMap`/`toMap` boilerplate**: The 12 model classes have repetitive conversion methods. A Groovy AST transform or base class could reduce this, but the readability trade-off wasn't worth it in this pass.
3. **No tests**: No automated tests exist for these files. Behavioral equivalence was verified by manual walkthrough of all 6 JOB_FUNCTION paths.
