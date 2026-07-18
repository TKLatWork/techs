# Tooling-SNOW Jenkins Job

## Summary
- Description: ServiceNow Change Request Management job-as-function. Routes to SNOW API methods via JOB_FUNCTION parameter.

## Terminology
- **JOB_FUNCTION:** The SNOW API method to call
- **INPUT_JSON:** JSON string containing all input parameters for the function

## Input

| Parameter | Type | Description |
|---|---|---|
| `INPUT_JSON` | text | JSON string with function-specific input parameters |
| `JOB_FUNCTION` | choice | Function to call: createChangeRequest, getChangeRequest, updateChangeRequest, listChangeRequests, approveChangeRequest, closeChangeRequest |
| `snow_env` | choice | ServiceNow environment: dev, qa, prod |

### INPUT_JSON by JOB_FUNCTION

| JOB_FUNCTION | Required Fields | Optional Fields |
|---|---|---|
| createChangeRequest | short_description | description, category, risk_level, planned_start, planned_end, assignment_group |
| getChangeRequest | sys_id | - |
| updateChangeRequest | sys_id | short_description, description, risk_level, planned_start, planned_end |
| listChangeRequests | - | state, category, assignment_group, sysparm_limit, sysparm_offset |
| approveChangeRequest | sys_id | approval_comments |
| closeChangeRequest | sys_id, close_code, close_notes | - |

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

### createChangeRequest Test

Create a change request and verify the response contains sys_id, number, and state.

#### Test steps

1. Set JOB_FUNCTION=createChangeRequest, snow_env=dev
2. Set INPUT_JSON=`{"short_description":"Test CR","category":"Standard","risk_level":"Low"}`
3. Run the job

#### Verification

- Return.json code=200
- data contains sys_id, number, state fields

### getChangeRequest Test

Retrieve a change request by sys_id.

#### Test steps

1. Set JOB_FUNCTION=getChangeRequest, snow_env=dev
2. Set INPUT_JSON=`{"sys_id":"<known_sys_id>"}`
3. Run the job

#### Verification

- Return.json code=200
- data.change_request contains full CR object

### Criteria

- All 6 JOB_FUNCTIONs return code=200 on success
- Invalid JOB_FUNCTION triggers failure with code=500
- Invalid INPUT_JSON triggers parse error

## Dependencies

- JK-Lib shared library
- SNOW API credential (snow-api-key)
- HTTP Request Jenkins plugin

## Key References

- [SNOW API Resource](../../../resources/SNOW-api/README.md)
- [Job-as-Function Resource](../../../resources/job-as-function/README.md)

# Notes

# Change log
- 2026-07-18: Initial spec created
