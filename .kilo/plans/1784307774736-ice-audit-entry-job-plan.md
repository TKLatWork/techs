# Plan: ICE createAuditEntry Job with ICE Lib

## Goal
Create a Jenkins job that creates audit entries via the ICE Audit API, using an `ICEClient` class added to the existing `JK-Lib` shared library.

## Scope
- Add `ICEClient.groovy` to `libs/JK-Lib/src/com/jklib/`
- Create job at `jobs/dev/createAuditEntry/Jenkinsfile`
- Update `libs/JK-Lib/README.md` with ICEClient documentation

## Design Decisions (Resolved)
1. **Library structure**: Add `ICEClient` to existing `JK-Lib` (not a separate library)
2. **Credential**: Use `ice-api-key` (pattern matches existing `g3-api-key`)
3. **Auth method**: OAuth 2.0 Bearer token via `Authorization` header
4. **Job location**: `jobs/dev/createAuditEntry/` (matches existing `jobs/dev/createReleasePackage/` pattern)

## ICEClient.groovy

### Location
`libs/JK-Lib/src/com/jklib/ICEClient.groovy`

### Constructor
```groovy
ICEClient(script, String env, String credentialId = 'ice-api-key')
```
- Base URL: `https://ice-audit.${env}.internal`

### Method: createAuditEntry
```groovy
Map createAuditEntry(
    String resourceId,
    String resourceType,
    String action,
    String actor,
    String changeSource,
    Map details = [:],
    List complianceTags = [],
    String changeRequestId = null
)
```
- POST `/api/v1/audit`
- Required params: `resource_id`, `resource_type`, `action`, `actor`, `change_source`
- Optional: `details`, `compliance_tags`, `change_request_id`
- Returns: `{ "id": "...", "resource_id": "...", "action": "...", "actor": "...", "timestamp": "...", "immutable": true }`

### Request method
- Same pattern as `G3Client.request()` but uses `Authorization: Bearer <token>` header instead of `X-API-Key`
- Uses `httpRequest` step with `validResponseCodes: '100:599'`

## Jenkinsfile

### Location
`jobs/dev/createAuditEntry/Jenkinsfile`

### Parameters
| Name | Type | Required | Default | Description |
|---|---|---|---|---|
| `resource_id` | string | Yes | - | Identifier of the changed resource |
| `resource_type` | choice | Yes | - | server/database/app/config |
| `action` | choice | Yes | - | created/modified/deleted/deployed |
| `actor` | string | Yes | - | User or service that performed the action |
| `change_source` | string | Yes | Jenkins | Originating system |
| `details` | text | No | `{}` | JSON object of change details |
| `compliance_tags` | text | No | `[]` | JSON array of compliance frameworks |
| `change_request_id` | string | No | - | SNOW change request ID |
| `ice_env` | choice | Yes | dev | dev/qa/prod |

### Stages
1. **Validate** — Check required params
2. **Create Audit Entry** — Call `ICEClient.createAuditEntry()`, store result in `env.AUDIT_ID`
3. **Output** — Echo `AUDIT_ID` and timestamp

### Post
- `failure`: Echo error message
- `always`: Echo completion timestamp

## Implementation Steps
1. Create `libs/JK-Lib/src/com/jklib/ICEClient.groovy`
2. Update `libs/JK-Lib/README.md` with ICEClient usage table
3. Create `jobs/dev/createAuditEntry/Jenkinsfile`

## Validation
- Verify Jenkinsfile syntax with `jenkinsfile-lint` or dry-run
- Verify `ICEClient.groovy` compiles (Groovy syntax check)
- Test job in DEV environment with sample parameters

## Open Questions
- None
