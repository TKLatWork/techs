# Plan: Job-as-Function Pattern for All API Jobs

## Context

All existing jobs (createChangeRequest, createAuditEntry, createReleasePackage) use individual parameters and per-method structure. They need to be replaced with consolidated jobs following the job-as-function pattern: one job per API, using `INPUT_JSON` + `JOB_FUNCTION` params, with `Return.json` output.

Additionally, the lib clients (ICEClient, SnowClient) are missing most API methods and need to be completed.

## Decisions

| Decision | Choice |
|---|---|
| Job granularity | One job per API |
| Job names | `Tooling-SNOW`, `Tooling-ICE`, `Tooling-G3` |
| Job location | `jobs/dev/<job-name>/Jenkinsfile` |
| Existing jobs | Delete and replace |
| Model classes | Full input/output models, one file per API in `com.jklib` |
| Return.json helper | Shared `vars/writeReturnJson.groovy` step in JK-Lib |

## Scope

### Jobs (3 new, 3 deleted)

| New Job | Replaces | API Methods |
|---|---|---|
| `Tooling-SNOW` | `createChangeRequest` | 6: createChangeRequest, getChangeRequest, updateChangeRequest, listChangeRequests, approveChangeRequest, closeChangeRequest |
| `Tooling-ICE` | `createAuditEntry` | 5: createAuditEntry, getAuditRecord, listAuditRecords, getAuditSummary, exportAuditLog |
| `Tooling-G3` | `createReleasePackage` | 6: createReleasePackage, getReleasePackage, listReleasePackages, executeRelease, getExecutionStatus, rollbackRelease |

### Lib Client Methods to Add

| Client | Missing Methods |
|---|---|
| `SnowClient` | getChangeRequest, updateChangeRequest, listChangeRequests, approveChangeRequest, closeChangeRequest |
| `ICEClient` | getAuditRecord, listAuditRecords, getAuditSummary, exportAuditLog |
| `G3Client` | (complete - all 6 methods exist) |

## Job-as-Function Pattern (per job)

### Parameters
| Name | Type | Description |
|---|---|---|
| `INPUT_JSON` | text | JSON string with all input params for the function |
| `JOB_FUNCTION` | choice | Method name to call (choices = API method names) |
| `<api>_env` | choice | Target environment (dev/qa/prod) |

### Pipeline Flow
1. Parse `INPUT_JSON` into Map
2. Instantiate model input class from Map
3. Create API client with env
4. Route by `JOB_FUNCTION` → call client method with model data
5. Wrap result in output model
6. Call `writeReturnJson(200, 'success', outputData)`
7. On error: call `writeReturnJson(500, errorMessage)`

### Return.json Output
```json
{
    "code": 200,
    "message": "success",
    "data": { ... },
    "files": { "file1": "path" }
}
```

## File Manifest

### New Files (10)

| # | Path | Description |
|---|---|---|
| 1 | `libs/JK-Lib/vars/writeReturnJson.groovy` | Shared step: builds Return.json, writes + archives it |
| 2 | `libs/JK-Lib/src/com/jklib/SnowModels.groovy` | Input/output model classes for all 6 SNOW methods |
| 3 | `libs/JK-Lib/src/com/jklib/ICEModels.groovy` | Input/output model classes for all 5 ICE methods |
| 4 | `libs/JK-Lib/src/com/jklib/G3Models.groovy` | Input/output model classes for all 6 G3 methods |
| 5 | `jobs/dev/Tooling-SNOW/Jenkinsfile` | SNOW job-as-function (6 methods) |
| 6 | `jobs/dev/Tooling-ICE/Jenkinsfile` | ICE job-as-function (5 methods) |
| 7 | `jobs/dev/Tooling-G3/Jenkinsfile` | G3 job-as-function (6 methods) |
| 8 | `jobs/dev/Tooling-SNOW/README.md` | Job spec (Jenkins-Job template) |
| 9 | `jobs/dev/Tooling-ICE/README.md` | Job spec (Jenkins-Job template) |
| 10 | `jobs/dev/Tooling-G3/README.md` | Job spec (Jenkins-Job template) |

### Modified Files (3)

| # | Path | Changes |
|---|---|---|
| 11 | `libs/JK-Lib/src/com/jklib/SnowClient.groovy` | Add 5 methods: getChangeRequest, updateChangeRequest, listChangeRequests, approveChangeRequest, closeChangeRequest |
| 12 | `libs/JK-Lib/src/com/jklib/ICEClient.groovy` | Add 4 methods: getAuditRecord, listAuditRecords, getAuditSummary, exportAuditLog |
| 13 | `libs/JK-Lib/README.md` | Document new methods + model classes + writeReturnJson |

### Deleted Files (3)

| # | Path |
|---|---|
| 14 | `jobs/dev/createChangeRequest/Jenkinsfile` |
| 15 | `jobs/dev/createAuditEntry/Jenkinsfile` |
| 16 | `jobs/dev/createReleasePackage/Jenkinsfile` |

## Implementation Order

1. **writeReturnJson.groovy** — shared helper, no dependencies
2. **Model classes** (SnowModels, ICEModels, G3Models) — depend on API specs only
3. **Lib client methods** (SnowClient +5, ICEClient +4) — depend on model classes
4. **Job Jenkinsfiles** (Tooling-SNOW, Tooling-ICE, Tooling-G3) — depend on models + clients + helper
5. **Job README specs** — document each job
6. **Update JK-Lib README** — document all new methods/models
7. **Delete old jobs** — remove createChangeRequest, createAuditEntry, createReleasePackage

## Validation

- Groovy syntax check on all new .groovy files
- Cross-check all client method signatures against API specs in `resources/<api>/README.md`
- Cross-check model fields against API method params
- Verify Return.json structure matches `resources/job-as-function/README.md`
- Verify job Jenkinsfile follows code style rules from `docs/JenkinsJob/AGENTS.md`
