# G3 API Resource

# Summary
- Type: API
- Description: Release package management and execution API for building, deploying, and rolling back release packages

# Terminology
- **Release Package:** A bundle of artifacts (binaries, configs, scripts) prepared for deployment
- **Execution:** A single run of a release package against a target environment
- **Artifact:** An individual deployable unit within a package (JAR, WAR, config file, etc.)
- **Rollback:** Reverting a release execution to the previous stable state
- **Pipeline:** The sequence of stages a release goes through (Build → Package → Deploy → Verify)

# API Env
- **Base URL:** `https://g3-api.<env>.internal`
- **Environments:**
  - DEV: `https://g3-api.dev.internal` — development/testing
  - QA: `https://g3-api.qa.internal` — staging/validation
  - PROD: `https://g3-api.prod.internal` — production

# Auth
- **Method:** API Key or JWT Bearer Token
- **Header:** `X-API-Key: <key>` or `Authorization: Bearer <jwt>`
- **Content-Type:** `application/json`

# Methods

## createReleasePackage Method
Description: Create a new release package
Url: POST `/api/v1/packages`
Params:
- `name` (string, required) — package name
- `version` (string, required) — semantic version (e.g., "1.2.3")
- `description` (string) — package description
- `artifacts` (array) — list of artifact references
- `target_env` (string) — target environment (dev/qa/prod)
- `config_overrides` (object) — environment-specific config overrides
Returns: `{ "id": "pkg_abc123", "name": "string", "version": "string", "status": "created", "created_at": "datetime" }`
Notes: Package starts in "created" status. Artifacts must already exist in the artifact registry.

## getReleasePackage Method
Description: Get release package details by ID
Url: GET `/api/v1/packages/{package_id}`
Params:
- `package_id` (string, required) — the package ID (path param)
Returns: `{ "id": "string", "name": "string", "version": "string", "status": "string", "artifacts": [...], "executions": [...], "created_at": "datetime" }`
Notes: Includes full artifact list and execution history. Returns 404 if not found.

## listReleasePackages Method
Description: List and filter release packages
Url: GET `/api/v1/packages`
Params:
- `name` (string, optional) — filter by package name (partial match)
- `version` (string, optional) — filter by exact version
- `status` (string, optional) — filter by status (created/executing/completed/failed)
- `target_env` (string, optional) — filter by target environment
- `limit` (integer, optional) — max results (default 50, max 200)
- `offset` (integer, optional) — pagination offset
Returns: `{ "total": 120, "limit": 50, "offset": 0, "packages": [ { "id": "string", "name": "string", "version": "string", "status": "string" }, ... ] }`
Notes: Default sort by `created_at` descending. Supports cursor-based pagination.

## executeRelease Method
Description: Trigger execution of a release package against its target environment
Url: POST `/api/v1/packages/{package_id}/execute`
Params:
- `package_id` (string, required) — the package ID (path param)
- `dry_run` (boolean, optional) — if true, validate without deploying (default false)
- `rollback_on_failure` (boolean, optional) — auto-rollback if execution fails (default true)
- `notify` (array, optional) — list of email addresses to notify on completion
Returns: `{ "execution_id": "exec_xyz789", "package_id": "string", "status": "queued", "started_at": "datetime" }`
Notes: Package must be in "created" status. Returns execution ID for tracking. Queues the execution asynchronously.

## getExecutionStatus Method
Description: Get the status of a release execution
Url: GET `/api/v1/executions/{execution_id}`
Params:
- `execution_id` (string, required) — the execution ID (path param)
Returns: `{ "execution_id": "string", "package_id": "string", "status": "string", "stages": [ { "name": "string", "status": "string", "started_at": "datetime", "completed_at": "datetime" } ], "started_at": "datetime", "completed_at": "datetime|null" }`
Notes: Status values: queued/running/completed/failed/rolled_back. Includes per-stage breakdown.

## rollbackRelease Method
Description: Rollback a completed or failed release execution to the previous stable state
Url: POST `/api/v1/executions/{execution_id}/rollback`
Params:
- `execution_id` (string, required) — the execution ID (path param)
- `reason` (string, required) — reason for rollback
- `force` (boolean, optional) — force rollback even if current state is partially deployed (default false)
Returns: `{ "rollback_id": "rb_def456", "execution_id": "string", "status": "queued", "started_at": "datetime" }`
Notes: Only available for executions in "completed" or "failed" status. Creates a new rollback execution record.

# References
- G3 API Swagger documentation
- Release management runbook

# Notes
- Execution is asynchronous — use `getExecutionStatus` to poll or configure webhooks
- All timestamps are UTC in ISO 8601 format
- Rate limit: 50 requests per minute per API key

# Change log
- 2026-07-18: Initial spec created with 6 methods
