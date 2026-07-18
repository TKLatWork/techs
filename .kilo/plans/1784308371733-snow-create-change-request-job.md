# Plan: SNOW createChangeRequest Job with SnowClient

## Context
Create a Jenkins job that calls ServiceNow's `createChangeRequest` API, backed by a new `SnowClient` shared-lib class in JK-Lib. Follows the existing G3Client/ICEClient patterns.

## Decisions
- **Class name:** `SnowClient` (matches `G3Client`/`ICEClient` convention)
- **Auth:** Basic Auth via `usernamePassword` Jenkins credential (id: `snow-api-key`)
- **Job location:** `jobs/dev/createChangeRequest/Jenkinsfile`
- **SNOW envs:** dev / qa / prod (base URLs: `https://<env>-instance.service-now.com`)

## Tasks

### 1. Create `SnowClient.groovy` in shared lib
**File:** `libs/JK-Lib/src/com/jklib/SnowClient.groovy`

Follow `ICEClient.groovy` pattern:
- Constructor: `SnowClient(script, String env, String credentialId = 'snow-api-key')`
- `baseUrl`: `https://${env}-instance.service-now.com`
- `withCredentials` using `usernamePassword` type, variables `SNOW_USER` / `SNOW_PASS`
- Auth header: `Basic base64(SNOW_USER:SNOW_PASS)`
- Private `request(method, path, body)` method
- Public method:
  ```
  Map createChangeRequest(
      String shortDescription,
      String description = null,
      String category = null,
      String riskLevel = null,
      String plannedStart = null,
      String plannedEnd = null,
      String assignmentGroup = null
  )
  ```
- POST to `/api/now/change_request`
- Body uses SNOW field names: `short_description`, `description`, `category`, `risk_level`, `planned_start`, `planned_end`, `assignment_group`
- Only include non-null optional fields in body

### 2. Update JK-Lib README
**File:** `libs/JK-Lib/README.md`

Add `SnowClient` section with usage example and method table (same format as G3Client/ICEClient sections).

### 3. Create Jenkins job
**File:** `jobs/dev/createChangeRequest/Jenkinsfile`

Follow `createAuditEntry/Jenkinsfile` structure:

**Parameters:**
| Name | Type | Choices/Default | Required |
|---|---|---|---|
| `short_description` | string | — | yes |
| `description` | text | `''` | no |
| `category` | choice | Standard/Normal/Emergency | no |
| `risk_level` | choice | Low/Medium/High/Critical | no |
| `planned_start` | string | `''` | no |
| `planned_end` | string | `''` | no |
| `assignment_group` | string | `''` | no |
| `snow_env` | choice | dev/qa/prod | — |

**Stages:**
1. **Validate** — check `short_description` is non-empty
2. **Create Change Request** — instantiate `SnowClient`, call `createChangeRequest`, store `sys_id` / `number` / `state` in env vars
3. **Output** — echo `CR Number`, `sys_id`, `State`

**Post:** failure/always blocks (same pattern as existing jobs)

## Validation
- Confirm `SnowClient.groovy` compiles (Groovy syntax check)
- Confirm Jenkinsfile matches existing job structure
- Verify README section matches existing format
