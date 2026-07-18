# Plan: Fill API Resource Specs with Test Data

## Goal
Populate the 3 API resource spec files under `resources/` with realistic placeholder content following the API-Resource template, including 4-6 methods each. Also fix the template filename.

## Tasks

### 1. Rename template file
- Rename `docs/spec/API-Resource copy.md` → `docs/spec/API-Resource.md`
- This fixes the broken reference in `docs/spec/AGENTS.md`

### 2. Fill `resources/SNOW-api/README.md`
SNOW = Change Request Management API (ServiceNow-style)

**Sections to fill:** Summary, Terminology, API Env, Auth, Methods (6), References, Notes, Change log

**Methods (6):**
1. `createChangeRequest` — POST `/api/now/change_request` — Create a new change request
2. `getChangeRequest` — GET `/api/now/change_request/{id}` — Get change request by ID
3. `updateChangeRequest` — PATCH `/api/now/change_request/{id}` — Update an existing change request
4. `listChangeRequests` — GET `/api/now/change_request` — List/filter change requests
5. `approveChangeRequest` — POST `/api/now/change_request/{id}/approve` — Approve a change request
6. `closeChangeRequest` — POST `/api/now/change_request/{id}/close` — Close/complete a change request

### 3. Fill `resources/G3-api/README.md`
G3 = Release Package and Execution API

**Sections to fill:** Summary, Terminology, API Env, Auth, Methods (6), References, Notes, Change log

**Methods (6):**
1. `createReleasePackage` — POST `/api/v1/packages` — Create a new release package
2. `getReleasePackage` — GET `/api/v1/packages/{id}` — Get package details
3. `listReleasePackages` — GET `/api/v1/packages` — List/filter release packages
4. `executeRelease` — POST `/api/v1/packages/{id}/execute` — Trigger a release execution
5. `getExecutionStatus` — GET `/api/v1/executions/{id}` — Get execution status
6. `rollbackRelease` — POST `/api/v1/executions/{id}/rollback` — Rollback a release

### 4. Fill `resources/ICE-api/README.md`
ICE = Change Record Auditing Service

**Sections to fill:** Summary, Terminology, API Env, Auth, Methods (5), References, Notes, Change log

**Methods (5):**
1. `createAuditEntry` — POST `/api/v1/audit` — Create an audit entry for a change
2. `getAuditRecord` — GET `/api/v1/audit/{id}` — Get a specific audit record
3. `listAuditRecords` — GET `/api/v1/audit` — List/filter audit records
4. `getAuditSummary` — GET `/api/v1/audit/summary` — Get audit summary/report
5. `exportAuditLog` — GET `/api/v1/audit/export` — Export audit log (JSON/CSV)

## File changes
- `docs/spec/API-Resource copy.md` → `docs/spec/API-Resource.md` (rename)
- `resources/SNOW-api/README.md` (edit — fill all sections)
- `resources/G3-api/README.md` (edit — fill all sections)
- `resources/ICE-api/README.md` (edit — fill all sections)

## Validation
- Each README follows the API-Resource template structure exactly
- Each has 4-6 methods with Description, Url, Params, Returns, Notes
- Template filename matches the AGENTS.md reference
