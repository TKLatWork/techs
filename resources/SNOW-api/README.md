# SNOW API Resource

# Summary
- Type: API
- Description: ServiceNow Change Request Management API for creating, tracking, approving, and closing change requests

# Terminology
- **Change Request (CR):** A formal proposal to modify infrastructure, application, or configuration
- **CAB:** Change Advisory Board — group that reviews and approves change requests
- **Risk Level:** Classification (Low/Medium/High/Critical) assigned to a change request
- **State:** Current lifecycle stage of a CR (Draft → Review → Approved → Scheduled → In Progress → Closed)

# API Env
- **Base URL:** `https://<instance>.service-now.com`
- **Environments:**
  - DEV: `https://dev-instance.service-now.com` — development/testing
  - QA: `https://qa-instance.service-now.com` — staging/validation
  - PROD: `https://prod-instance.service-now.com` — production

# Auth
- **Method:** Basic Authentication (username + password) or OAuth 2.0
- **Header:** `Authorization: Basic <base64(user:pass)>` or `Authorization: Bearer <token>`
- **Content-Type:** `application/json`

# Methods

## createChangeRequest Method
Description: Create a new change request
Url: POST `/api/now/change_request`
Params:
- `short_description` (string, required) — brief summary of the change
- `description` (string) — detailed description
- `category` (string) — change category (Standard/Normal/Emergency)
- `risk_level` (string) — Low/Medium/High/Critical
- `planned_start` (datetime) — scheduled start time
- `planned_end` (datetime) — scheduled end time
- `assignment_group` (string) — sys_id of the assignment group
Returns: `{ "result": { "sys_id": "string", "number": "CHG0012345", "state": "Draft" } }`
Notes: Returns the created CR with auto-generated number. State defaults to "Draft".

## getChangeRequest Method
Description: Get a change request by ID
Url: GET `/api/now/change_request/{sys_id}`
Params:
- `sys_id` (string, required) — the CR sys_id (path param)
Returns: `{ "result": { "sys_id": "string", "number": "string", "state": "string", "short_description": "string", ... } }`
Notes: Returns full CR object. Returns 404 if not found.

## updateChangeRequest Method
Description: Update an existing change request
Url: PATCH `/api/now/change_request/{sys_id}`
Params:
- `sys_id` (string, required) — the CR sys_id (path param)
- `short_description` (string) — updated summary
- `description` (string) — updated description
- `risk_level` (string) — updated risk level
- `planned_start` (datetime) — updated start time
- `planned_end` (datetime) — updated end time
Returns: `{ "result": { "sys_id": "string", "number": "string", "state": "string", ... } }`
Notes: Only provided fields are updated. Returns updated CR.

## listChangeRequests Method
Description: List and filter change requests
Url: GET `/api/now/change_request`
Params:
- `state` (string, optional) — filter by state
- `category` (string, optional) — filter by category
- `assignment_group` (string, optional) — filter by group sys_id
- `sysparm_limit` (integer, optional) — max results (default 100, max 1000)
- `sysparm_offset` (integer, optional) — pagination offset
Returns: `{ "result": [ { "sys_id": "string", "number": "string", "state": "string", ... }, ... ] }`
Notes: Supports pagination. Default sort by `sys_created_on` descending.

## approveChangeRequest Method
Description: Approve a change request (transitions state to Approved)
Url: POST `/api/now/change_request/{sys_id}/approve`
Params:
- `sys_id` (string, required) — the CR sys_id (path param)
- `approval_comments` (string) — optional comments from approver
Returns: `{ "result": { "sys_id": "string", "number": "string", "state": "Approved", "approval": "approved" } }`
Notes: CR must be in "Review" state. Caller must have approval permissions. Returns 400 if state is invalid.

## closeChangeRequest Method
Description: Close/complete a change request
Url: POST `/api/now/change_request/{sys_id}/close`
Params:
- `sys_id` (string, required) — the CR sys_id (path param)
- `close_code` (string, required) — Successful/Unsuccessful/Cancelled
- `close_notes` (string, required) — summary of outcome
Returns: `{ "result": { "sys_id": "string", "number": "string", "state": "Closed", "close_code": "string" } }`
Notes: CR must be in "In Progress" state. Transitions state to "Closed".

# References
- ServiceNow REST API documentation
- Change Management best practices (ITIL)

# Notes
- All datetime params use ISO 8601 format: `2026-07-18T10:00:00Z`
- Rate limit: 100 requests per minute per user
- sys_id values are 32-character hex strings

# Change log
- 2026-07-18: Initial spec created with 6 methods
