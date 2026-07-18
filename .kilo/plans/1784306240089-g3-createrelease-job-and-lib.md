# G3 createRelease Job & G3Client in JK-Lib

## Decisions

| Decision | Choice |
|---|---|
| Job scope | `createReleasePackage` only (POST `/api/v1/packages`) |
| G3Util placement | Inside existing `libs/JK-Lib/` (no separate lib) |
| G3Util method scope | All 6 G3 API methods wrapped |
| DSL wrappers | **None** — job calls `G3Client` class directly |
| Job location | `jobs/dev/createReleasePackage/Jenkinsfile` |
| Auth | Jenkins Credentials Binding (credential ID: `g3-api-key`) |
| HTTP client | Jenkins `httpRequest` pipeline step (requires HTTP Request plugin) |
| JSON handling | Groovy `JsonSlurper` / `JsonOutput` (built-in, no plugin) |
| Error handling | Throw `RuntimeException` with status code + response body on 4xx/5xx |
| Job parameters | All API params: name, version, description, artifacts, target_env, config_overrides |

## Task List

### 1. Create G3Client class — `libs/JK-Lib/src/com/jklib/G3Client.groovy`

Package: `com.jklib`, implements `Serializable`.

**Constructor**: `G3Client(script, String env, String credentialId = 'g3-api-key')`
- `script`: pipeline context (`this` from Jenkinsfile) — used to call pipeline steps (`httpRequest`, `withCredentials`, `echo`)
- `env`: G3 API environment (`dev`/`qa`/`prod`) — resolves base URL `https://g3-api.<env>.internal`
- `credentialId`: Jenkins credential ID for the API key

**Private `request(String method, String path, Map body = null)`**:
- Builds URL from `baseUrl + path`
- Uses `script.withCredentials` to bind the API key as `X-API-Key` header
- Calls `script.httpRequest` with method, URL, headers (`Content-Type: application/json`, `X-API-Key`), and JSON body (via `JsonOutput.toJson`)
- On HTTP 4xx/5xx: throws `RuntimeException` with `"G3 API error [${statusCode}]: ${responseBody}"`
- Parses response body with `JsonSlurper` and returns a `Map`

**6 public methods** (all return `Map`):

| Method | HTTP | Path |
|---|---|---|
| `createReleasePackage(String name, String version, String description = null, List artifacts = [], String targetEnv = null, Map configOverrides = [:])` | POST | `/api/v1/packages` |
| `getReleasePackage(String packageId)` | GET | `/api/v1/packages/{package_id}` |
| `listReleasePackages(Map filters = [:])` | GET | `/api/v1/packages` (query params from filters map) |
| `executeRelease(String packageId, boolean dryRun = false, boolean rollbackOnFailure = true, List notify = [])` | POST | `/api/v1/packages/{package_id}/execute` |
| `getExecutionStatus(String executionId)` | GET | `/api/v1/executions/{execution_id}` |
| `rollbackRelease(String executionId, String reason, boolean force = false)` | POST | `/api/v1/executions/{execution_id}/rollback` |

### 2. Create the Jenkins job — `jobs/dev/createReleasePackage/Jenkinsfile`

Scripted pipeline (not declarative) with:

- `@Library('JK-Lib') _` at top
- **Parameters block**:
  - `name` (string, required)
  - `version` (string, required)
  - `description` (string, optional, default `''`)
  - `artifacts` (text, optional — JSON array string, default `'[]'`)
  - `target_env` (choice: dev/qa/prod, default `dev`)
  - `config_overrides` (text, optional — JSON object string, default `'{}'`)
  - `g3_env` (choice: dev/qa/prod — G3 API environment, default `dev`)
- **Stages**:
  1. **Validate** — abort if `name` or `version` is empty
  2. **Create Release Package** — instantiate `G3Client`, parse `artifacts` and `config_overrides` from JSON strings, call `createReleasePackage`, store result
  3. **Output** — echo package ID and status from response
- **Post**: `failure` block echoes error; `always` block logs completion

### 3. Update JK-Lib README — `libs/JK-Lib/README.md`

Append a `## G3Client` section with usage example:

```groovy
@Library('JK-Lib') _

def g3 = new com.jklib.G3Client(this, 'dev', 'g3-api-key')
def result = g3.createReleasePackage('my-app', '1.0.0')
echo "Package ID: ${result.id}"
```

## File Manifest

| Action | Path |
|---|---|
| Create | `libs/JK-Lib/src/com/jklib/G3Client.groovy` |
| Create | `jobs/dev/createReleasePackage/Jenkinsfile` |
| Edit | `libs/JK-Lib/README.md` |

## Prerequisites

- Jenkins HTTP Request plugin installed (for `httpRequest` step)
- Jenkins credential `g3-api-key` configured in Jenkins credential store

## Validation

- `G3Client.groovy`: valid Groovy syntax, correct `package com.jklib` declaration, all 6 methods match G3 API spec in `resources/G3-api/README.md`
- `Jenkinsfile`: valid scripted pipeline syntax, `@Library('JK-Lib') _` resolves `com.jklib.G3Client`
- Cross-check all API URLs, HTTP methods, and parameter names against `resources/G3-api/README.md`
