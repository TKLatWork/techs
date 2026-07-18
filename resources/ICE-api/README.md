# ICE API Resource

# Summary
- Type: API
- Description: Change record auditing service for tracking, querying, and exporting audit logs of infrastructure and application changes

# Terminology
- **Audit Entry:** A single immutable record of a change event (who, what, when, where)
- **Audit Trail:** The chronological sequence of audit entries for a given resource
- **Actor:** The user or service account that performed the change
- **Change Source:** The system that originated the change (e.g., Jenkins, Terraform, manual)
- **Compliance Tag:** A label indicating which compliance framework the change relates to (SOX, PCI, HIPAA)

# API Env
- **Base URL:** `https://ice-audit.<env>.internal`
- **Environments:**
  - DEV: `https://ice-audit.dev.internal` — development/testing
  - QA: `https://ice-audit.qa.internal` — staging/validation
  - PROD: `https://ice-audit.prod.internal` — production

# Auth
- **Method:** OAuth 2.0 Client Credentials or mTLS
- **Header:** `Authorization: Bearer <token>`
- **Scopes required:** `audit:read`, `audit:write`, `audit:export`
- **Content-Type:** `application/json`

# Methods

## createAuditEntry Method
Description: Create an immutable audit entry for a change event
Url: POST `/api/v1/audit`
Params:
- `resource_id` (string, required) — identifier of the changed resource
- `resource_type` (string, required) — type of resource (server/database/app/config)
- `action` (string, required) — action performed (created/modified/deleted/deployed)
- `actor` (string, required) — user or service that performed the action
- `change_source` (string, required) — originating system
- `details` (object) — structured diff or change payload
- `compliance_tags` (array) — applicable compliance frameworks
- `change_request_id` (string) — linked SNOW change request ID (if applicable)
Returns: `{ "id": "aud_abc123", "resource_id": "string", "action": "string", "actor": "string", "timestamp": "datetime", "immutable": true }`
Notes: Entries are immutable once created — cannot be updated or deleted. Timestamp is auto-generated server-side.

## getAuditRecord Method
Description: Get a specific audit record by ID
Url: GET `/api/v1/audit/{audit_id}`
Params:
- `audit_id` (string, required) — the audit record ID (path param)
Returns: `{ "id": "string", "resource_id": "string", "resource_type": "string", "action": "string", "actor": "string", "change_source": "string", "details": {}, "compliance_tags": [], "change_request_id": "string|null", "timestamp": "datetime" }`
Notes: Returns full audit record including details object. Returns 404 if not found.

## listAuditRecords Method
Description: List and filter audit records
Url: GET `/api/v1/audit`
Params:
- `resource_id` (string, optional) — filter by resource
- `resource_type` (string, optional) — filter by resource type
- `actor` (string, optional) — filter by actor
- `action` (string, optional) — filter by action type
- `change_source` (string, optional) — filter by originating system
- `from` (datetime, optional) — start of time range
- `to` (datetime, optional) — end of time range
- `compliance_tag` (string, optional) — filter by compliance framework
- `limit` (integer, optional) — max results (default 100, max 1000)
- `cursor` (string, optional) — pagination cursor
Returns: `{ "total": 5420, "records": [ { "id": "string", "resource_id": "string", "action": "string", "actor": "string", "timestamp": "datetime" }, ... ], "next_cursor": "string|null" }`
Notes: Default sort by `timestamp` descending. Cursor-based pagination for large result sets.

## getAuditSummary Method
Description: Get aggregated audit summary/report for a time period
Url: GET `/api/v1/audit/summary`
Params:
- `from` (datetime, required) — start of reporting period
- `to` (datetime, required) — end of reporting period
- `group_by` (string, optional) — group results by: actor/resource_type/action/change_source (default: action)
- `compliance_tag` (string, optional) — filter by compliance framework
Returns: `{ "period": { "from": "datetime", "to": "datetime" }, "total_records": 1250, "groups": [ { "key": "deployed", "count": 340 }, { "key": "modified", "count": 780 }, { "key": "deleted", "count": 130 } ] }`
Notes: Useful for dashboards and compliance reports. Max time range is 90 days.

## exportAuditLog Method
Description: Export audit log data in JSON or CSV format
Url: GET `/api/v1/audit/export`
Params:
- `from` (datetime, required) — start of export range
- `to` (datetime, required) — end of export range
- `format` (string, optional) — output format: json/csv (default: json)
- `resource_type` (string, optional) — filter by resource type
- `compliance_tag` (string, optional) — filter by compliance framework
- `async` (boolean, optional) — if true, returns a download URL instead of inline data (default false for small sets)
Returns: Inline: `{ "format": "csv", "record_count": 5000, "data": "..." }` or Async: `{ "export_id": "exp_xyz789", "status": "processing", "download_url": "string|null" }`
Notes: Exports over 10,000 records automatically switch to async mode. Async exports available for 24 hours.

# References
- ICE Audit Service architecture doc
- Compliance requirements matrix (SOX/PCI)

# Notes
- All audit entries are immutable — no PUT/PATCH/DELETE on `/api/v1/audit/{id}`
- Timestamps are UTC in ISO 8601 format
- Rate limit: 200 read requests/min, 50 write requests/min per client
- Export downloads are signed with the service's private key for integrity verification

# Change log
- 2026-07-18: Initial spec created with 5 methods
