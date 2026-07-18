# Plan: G3 createRelease Job & G3Client

**Source:** `.kilo/plans/1784306240089-g3-createrelease-job-and-lib.md`
**Date:** 2026-07-18

## Objective

Create a Jenkins job for `createReleasePackage` and a G3Client shared library class that wraps all 6 G3 API methods.

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

### 1. Create G3Client class
**Path:** `libs/JK-Lib/src/com/jklib/G3Client.groovy`

- Package: `com.jklib`, implements `Serializable`
- Constructor: `G3Client(script, String env, String credentialId = 'g3-api-key')`
- Private `request()` method for all HTTP calls
- 6 public methods: `createReleasePackage`, `getReleasePackage`, `listReleasePackages`, `executeRelease`, `getExecutionStatus`, `rollbackRelease`

### 2. Create Jenkins job
**Path:** `jobs/dev/createReleasePackage/Jenkinsfile`

- Scripted pipeline with `@Library('JK-Lib') _`
- Parameters: name, version, description, artifacts (JSON), target_env, config_overrides (JSON), g3_env
- Stages: Validate → Create Release Package → Output
- Error handling with try/catch/finally

### 3. Update README
**Path:** `libs/JK-Lib/README.md`

- Append `## G3Client` section with usage example and method table

## File Manifest

| Action | Path |
|---|---|
| Create | `libs/JK-Lib/src/com/jklib/G3Client.groovy` |
| Create | `jobs/dev/createReleasePackage/Jenkinsfile` |
| Edit | `libs/JK-Lib/README.md` |

## Prerequisites

- Jenkins HTTP Request plugin installed
- Jenkins credential `g3-api-key` configured

## Validation

- G3Client: valid Groovy syntax, correct package, all 6 methods match G3 API spec
- Jenkinsfile: valid scripted pipeline, `@Library` resolves `com.jklib.G3Client`
- Cross-check all API URLs, methods, and params against `resources/G3-api/README.md`
