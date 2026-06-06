# Permission Management API Contracts

**Feature**: 004-user-auth-access-control
**Base URL**: `/api/permissions`
**Date**: 2026-06-07
**Auth**: All endpoints require `Authorization: Bearer <token>` with `manage_permissions` right

---

## Roles

### GET /api/permissions/roles

List all roles.

#### Response (200 OK)

```json
[
  {
    "id": "role-uuid-1",
    "name": "Admin",
    "description": "Full system access",
    "rightIds": ["right-uuid-1", "right-uuid-2"],
    "builtIn": true,
    "immutable": true
  },
  {
    "id": "role-uuid-2",
    "name": "User",
    "description": "Standard access",
    "rightIds": ["right-uuid-3"],
    "builtIn": true,
    "immutable": false
  }
]
```

### POST /api/permissions/roles

Create a new custom role.

#### Request

```json
{
  "name": "Editor",
  "description": "Can edit content",
  "rightIds": ["right-uuid-3", "right-uuid-4"]
}
```

| Field | Type | Required | Constraints |
|-------|------|----------|-------------|
| name | String | Yes | Unique, non-blank |
| description | String | No | — |
| rightIds | Set\<String\> | No | Must reference existing rights |

#### Response (201 Created)

Returns the created RoleDto.

#### Error Responses

| Status | Code | Condition |
|--------|------|-----------|
| 400 | BLANK_FIELD | Name is blank |
| 409 | ROLE_NAME_TAKEN | Role name already exists |
| 404 | RIGHT_NOT_FOUND | Referenced right ID does not exist |

### PUT /api/permissions/roles/{roleId}

Update a role (name, description, rights). Built-in immutable roles (Admin) cannot be modified.

#### Request

```json
{
  "name": "Editor",
  "description": "Can edit and publish content",
  "rightIds": ["right-uuid-3", "right-uuid-4", "right-uuid-5"]
}
```

#### Response (200 OK)

Returns the updated RoleDto.

#### Error Responses

| Status | Code | Condition |
|--------|------|-----------|
| 403 | ROLE_IMMUTABLE | Attempting to modify the Admin role |
| 404 | ROLE_NOT_FOUND | Role ID does not exist |

### DELETE /api/permissions/roles/{roleId}

Delete a custom role. Users assigned to this role are reassigned to Visitor. Built-in roles cannot be deleted.

#### Response (204 No Content)

#### Error Responses

| Status | Code | Condition |
|--------|------|-----------|
| 403 | ROLE_BUILT_IN | Attempting to delete a built-in role |
| 404 | ROLE_NOT_FOUND | Role ID does not exist |

---

## Rights

### GET /api/permissions/rights

List all rights (built-in and feature-registered).

#### Response (200 OK)

```json
[
  {
    "id": "right-uuid-1",
    "name": "read:/admin",
    "type": "BASE",
    "urlPattern": "/admin",
    "action": "READ",
    "description": "Read access to admin pages",
    "source": "BUILT_IN",
    "featureId": null
  },
  {
    "id": "right-uuid-6",
    "name": "export_data",
    "type": "FEATURE",
    "urlPattern": null,
    "action": null,
    "description": "Can export data from reports",
    "source": "FEATURE_REGISTERED",
    "featureId": "reports-feature"
  }
]
```

### DELETE /api/permissions/rights/{rightId}

Delete an individual feature-registered right. Unassigns from all roles and users. Built-in rights cannot be deleted.

#### Response (204 No Content)

#### Error Responses

| Status | Code | Condition |
|--------|------|-----------|
| 403 | RIGHT_BUILT_IN | Attempting to delete a built-in right |
| 404 | RIGHT_NOT_FOUND | Right ID does not exist |

---

## Users

### GET /api/permissions/users

List all users with their roles and effective permissions.

#### Response (200 OK)

```json
[
  {
    "userId": "user-uuid-1",
    "username": "admin",
    "displayName": "Administrator",
    "role": { "id": "role-uuid-1", "name": "Admin" },
    "effectiveRights": [
      { "id": "right-uuid-1", "name": "read:/admin", "type": "BASE" }
    ],
    "individualRights": []
  }
]
```

### PUT /api/permissions/users/{userId}/role

Assign a role to a user.

#### Request

```json
{
  "roleId": "role-uuid-2"
}
```

#### Response (200 OK)

Returns the updated UserPermissionsDto.

#### Error Responses

| Status | Code | Condition |
|--------|------|-----------|
| 404 | USER_NOT_FOUND | User ID does not exist |
| 404 | ROLE_NOT_FOUND | Role ID does not exist |

### POST /api/permissions/users/{userId}/rights

Add an individual right to a user.

#### Request

```json
{
  "rightId": "right-uuid-4"
}
```

#### Response (200 OK)

Returns the updated UserPermissionsDto.

### DELETE /api/permissions/users/{userId}/rights/{rightId}

Remove an individual right from a user.

#### Response (200 OK)

Returns the updated UserPermissionsDto.

---

## Features

### GET /api/permissions/features

List all features that have registered permissions.

#### Response (200 OK)

```json
[
  {
    "featureId": "reports-feature",
    "rightCount": 3,
    "rights": [
      { "id": "right-uuid-6", "name": "export_data", "type": "FEATURE" }
    ]
  }
]
```

### POST /api/permissions/features/{featureId}/disable

Disable a feature and delete all its registered permissions. Unassigns from all roles and users.

#### Response (204 No Content)

#### Error Responses

| Status | Code | Condition |
|--------|------|-----------|
| 404 | FEATURE_NOT_FOUND | No rights registered by this feature |

---

## Permission Registration API

**Base URL**: `/api/permissions/register`
**Auth**: Requires `manage_permissions` right

### POST /api/permissions/register

Register permissions for a feature. Supports idempotent upsert.

#### Request

```json
{
  "featureId": "reports-feature",
  "permissions": [
    {
      "name": "read:/reports",
      "type": "BASE",
      "urlPattern": "/reports",
      "action": "READ",
      "description": "Read access to reports pages"
    },
    {
      "name": "export_data",
      "type": "FEATURE",
      "description": "Can export report data"
    }
  ]
}
```

| Field | Type | Required | Constraints |
|-------|------|----------|-------------|
| featureId | String | Yes | Non-blank |
| permissions | List | Yes | Non-empty |
| permissions[].name | String | Yes | Unique across all rights |
| permissions[].type | Enum | Yes | BASE or FEATURE |
| permissions[].urlPattern | String | Conditional | Required for BASE type |
| permissions[].action | Enum | Conditional | Required for BASE type: READ or WRITE |
| permissions[].description | String | No | — |

#### Response (200 OK)

```json
{
  "registered": 2,
  "updated": 0,
  "rights": [
    { "id": "right-uuid-6", "name": "read:/reports", "type": "BASE" },
    { "id": "right-uuid-7", "name": "export_data", "type": "FEATURE" }
  ]
}
```

#### Behavior

- New permissions are created and immediately available for assignment
- Existing permissions with the same name are updated (idempotent upsert)
- Response includes counts of newly registered vs. updated permissions
