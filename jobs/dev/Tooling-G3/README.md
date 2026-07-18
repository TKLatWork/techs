# Tooling-G3 Jenkins Job

## Summary
- Description: G3 Release Package Management job-as-function. Routes to G3 API methods via JOB_FUNCTION parameter.

## Terminology
- **JOB_FUNCTION:** The G3 API method to call
- **INPUT_JSON:** JSON string containing all input parameters for the function

## Input

| Parameter | Type | Description |
|---|---|---|
| `INPUT_JSON` | text | JSON string with function-specific input parameters |
| `JOB_FUNCTION` | choice | Function to call: createReleasePackage, getReleasePackage, listReleasePackages, executeRelease, getExecutionStatus, rollbackRelease |
| `g3_env` | choice | G3 API environment: dev, qa, prod |

### INPUT_JSON by JOB_FUNCTION

| JOB_FUNCTION | Required Fields | Optional Fields |
|---|---|---|
| createReleasePackage | name, version | description, artifacts, target_env, config_overrides |
| getReleasePackage | package_id | - |
| listReleasePackages | - | name, version, status, target_env, limit, offset |
| executeRelease | package_id | dry_run, rollback_on_failure, notify |
| getExecutionStatus | execution_id | - |
| rollbackRelease | execution_id, reason | force |

## Output

Return.json archive:
```json
{
    "code": 200,
    "message": "success",
    "data": { ... }
}
```

## Acceptence Criteria

### createReleasePackage Test

Create a release package and verify the response.

#### Test steps

1. Set JOB_FUNCTION=createReleasePackage, g3_env=dev
2. Set INPUT_JSON=`{"name":"my-app","version":"1.0.0","target_env":"dev"}`
3. Run the job

#### Verification

- Return.json code=200
- data contains id, name, version, status, created_at fields

### executeRelease Test

Execute a release package and verify the response.

#### Test steps

1. Set JOB_FUNCTION=executeRelease, g3_env=dev
2. Set INPUT_JSON=`{"package_id":"<known_package_id>","dry_run":true}`
3. Run the job

#### Verification

- Return.json code=200
- data contains execution_id, package_id, status, started_at fields

### Criteria

- All 6 JOB_FUNCTIONs return code=200 on success
- Invalid JOB_FUNCTION triggers failure with code=500
- Invalid INPUT_JSON triggers parse error

## Dependencies

- JK-Lib shared library
- G3 API credential (g3-api-key)
- HTTP Request Jenkins plugin

## Key References

- [G3 API Resource](../../../resources/G3-api/README.md)
- [Job-as-Function Resource](../../../resources/job-as-function/README.md)

# Notes

# Change log
- 2026-07-18: Initial spec created
