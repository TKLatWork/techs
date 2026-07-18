# Tooling-ICE Jenkins Job

## Summary
- Description: ICE Audit Service job-as-function. Routes to ICE API methods via JOB_FUNCTION parameter.

## Terminology
- **JOB_FUNCTION:** The ICE API method to call
- **INPUT_JSON:** JSON string containing all input parameters for the function

## Input

| Parameter | Type | Description |
|---|---|---|
| `INPUT_JSON` | text | JSON string with function-specific input parameters |
| `JOB_FUNCTION` | choice | Function to call: createAuditEntry, getAuditRecord, listAuditRecords, getAuditSummary, exportAuditLog |
| `ice_env` | choice | ICE API environment: dev, qa, prod |

### INPUT_JSON by JOB_FUNCTION

| JOB_FUNCTION | Required Fields | Optional Fields |
|---|---|---|
| createAuditEntry | resource_id, resource_type, action, actor, change_source | details, compliance_tags, change_request_id |
| getAuditRecord | audit_id | - |
| listAuditRecords | - | resource_id, resource_type, actor, action, change_source, from, to, compliance_tag, limit, cursor |
| getAuditSummary | from, to | group_by, compliance_tag |
| exportAuditLog | from, to | format, resource_type, compliance_tag, async |

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

### createAuditEntry Test

Create an audit entry and verify the response.

#### Test steps

1. Set JOB_FUNCTION=createAuditEntry, ice_env=dev
2. Set INPUT_JSON=`{"resource_id":"server-01","resource_type":"server","action":"modified","actor":"admin","change_source":"Jenkins"}`
3. Run the job

#### Verification

- Return.json code=200
- data contains id, resource_id, action, actor, timestamp, immutable fields

### listAuditRecords Test

List audit records with filters.

#### Test steps

1. Set JOB_FUNCTION=listAuditRecords, ice_env=dev
2. Set INPUT_JSON=`{"resource_type":"server","limit":10}`
3. Run the job

#### Verification

- Return.json code=200
- data contains total, records, next_cursor fields

### Criteria

- All 5 JOB_FUNCTIONs return code=200 on success
- Invalid JOB_FUNCTION triggers failure with code=500
- Invalid INPUT_JSON triggers parse error

## Dependencies

- JK-Lib shared library
- ICE API credential (ice-api-key)
- HTTP Request Jenkins plugin

## Key References

- [ICE API Resource](../../../resources/ICE-api/README.md)
- [Job-as-Function Resource](../../../resources/job-as-function/README.md)

# Notes

# Change log
- 2026-07-18: Initial spec created
