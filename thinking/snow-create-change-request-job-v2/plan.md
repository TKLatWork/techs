# Plan: SNOW createChangeRequest Job with SnowClient (v2)

## Context
Redo of previous attempt. Create a Jenkins job that calls ServiceNow's `createChangeRequest` API, backed by a new `SnowClient` shared-lib class in JK-Lib. This version follows JenkinsJob skill rules strictly.

## Decisions
- **Class name:** `SnowClient` (matches `G3Client`/`ICEClient` convention)
- **Auth:** Basic Auth via `usernamePassword` Jenkins credential (id: `snow-api-key`)
- **Job location:** `jobs/dev/createChangeRequest/Jenkinsfile`
- **SNOW envs:** dev / qa / prod (base URLs: `https://<env>-instance.service-now.com`)
- **No Validate stage:** JenkinsJob skill requires validation only in parameter definition, not pipeline code
- **Output stage as single line:** Per skill rule "If a stage only 1 line of script, make the whole stage a single line"

## Files
1. `libs/JK-Lib/src/com/jklib/SnowClient.groovy` (created)
2. `libs/JK-Lib/README.md` (updated)
3. `jobs/dev/createChangeRequest/Jenkinsfile` (created)

## Key Differences from v1
- Removed Validate stage (validation belongs in parameter definitions only)
- Applied stage length rules (≤10 lines, single-line for 1-line stages)
- SnowClient uses `usernamePassword` credential binding (not `string`)
- SnowClient uses Basic Auth header (not Bearer)
- `createChangeRequest` returns inner `result` map from nested SNOW response
