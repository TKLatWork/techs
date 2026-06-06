# Authentication API Contracts

**Feature**: 004-user-auth-access-control
**Base URL**: `/api/auth`
**Date**: 2026-06-07

## POST /api/auth/login

Authenticate a user and receive a session token.

### Request

```json
{
  "username": "admin",
  "password": "123"
}
```

| Field | Type | Required | Constraints |
|-------|------|----------|-------------|
| username | String | Yes | Non-blank |
| password | String | Yes | Non-blank |

### Response (200 OK)

```json
{
  "token": "550e8400-e29b-41d4-a716-446655440000",
  "expiresAt": "2026-06-14T00:57:02Z",
  "user": {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "username": "admin",
    "displayName": "Administrator",
    "roleName": "Admin",
    "createdAt": "2026-06-07T00:00:00Z",
    "effectiveRights": [
      { "id": "right-uuid-1", "name": "read:/admin", "type": "BASE" },
      { "id": "right-uuid-2", "name": "write:/admin", "type": "BASE" },
      { "id": "right-uuid-3", "name": "read:/profile", "type": "BASE" },
      { "id": "right-uuid-4", "name": "read:/permissions", "type": "BASE" },
      { "id": "right-uuid-5", "name": "manage_users", "type": "FEATURE" },
      { "id": "right-uuid-6", "name": "manage_permissions", "type": "FEATURE" }
    ],
    "individualRights": []
  }
}
```

### Error Responses

| Status | Code | Condition |
|--------|------|-----------|
| 400 | BLANK_CREDENTIALS | Username or password is blank |
| 401 | INVALID_CREDENTIALS | Username/password combination is incorrect |

```json
{
  "message": "Invalid username or password",
  "code": "INVALID_CREDENTIALS",
  "timestamp": "2026-06-07T00:57:02Z"
}
```

---

## POST /api/auth/register

Register a new user account. Auto-assigned Visitor role.

### Request

```json
{
  "username": "newuser",
  "password": "secret",
  "displayName": "New User"
}
```

| Field | Type | Required | Constraints |
|-------|------|----------|-------------|
| username | String | Yes | Non-blank, unique |
| password | String | Yes | Non-blank |
| displayName | String | Yes | Non-blank |

### Response (201 Created)

```json
{
  "user": {
    "id": "550e8400-e29b-41d4-a716-446655440002",
    "username": "newuser",
    "displayName": "New User",
    "roleName": "Visitor",
    "createdAt": "2026-06-07T01:00:00Z",
    "effectiveRights": [
      { "id": "right-uuid-3", "name": "read:/profile", "type": "BASE" }
    ],
    "individualRights": []
  }
}
```

### Error Responses

| Status | Code | Condition |
|--------|------|-----------|
| 400 | BLANK_FIELD | Required field is blank |
| 409 | USERNAME_TAKEN | Username already exists |

```json
{
  "message": "Username 'newuser' is already taken",
  "code": "USERNAME_TAKEN",
  "timestamp": "2026-06-07T01:00:00Z"
}
```

---

## POST /api/auth/logout

Invalidate the current session token.

### Request

**Headers**: `Authorization: Bearer <token>`

No request body.

### Response (204 No Content)

No response body.

### Error Responses

| Status | Code | Condition |
|--------|------|-----------|
| 401 | UNAUTHORIZED | Missing or invalid session token |

---

## GET /api/auth/me

Get the currently authenticated user's information including effective rights.
The frontend uses this endpoint to obtain user data and permission state for
route guards and feature-right checks.

### Request

**Headers**: `Authorization: Bearer <token>`

### Response (200 OK)

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440001",
  "username": "admin",
  "displayName": "Administrator",
  "roleName": "Admin",
  "createdAt": "2026-06-07T00:00:00Z",
  "effectiveRights": [
    { "id": "right-uuid-1", "name": "read:/admin", "type": "BASE" },
    { "id": "right-uuid-2", "name": "write:/admin", "type": "BASE" },
    { "id": "right-uuid-3", "name": "read:/profile", "type": "BASE" },
    { "id": "right-uuid-4", "name": "read:/permissions", "type": "BASE" },
    { "id": "right-uuid-5", "name": "manage_users", "type": "FEATURE" },
    { "id": "right-uuid-6", "name": "manage_permissions", "type": "FEATURE" }
  ],
  "individualRights": []
}
```

### Error Responses

| Status | Code | Condition |
|--------|------|-----------|
| 401 | UNAUTHORIZED | Missing, invalid, or expired session token |

---

## Authentication Header

All protected endpoints require the `Authorization` header:

```
Authorization: Bearer <session-token>
```

The `SessionAuthFilter` extracts the token, validates it against the `SessionRepository`, and sets the `SecurityContext`. If the token is missing, invalid, or expired, a 401 response is returned.

## Standard Error Response Format

All error responses follow this format:

```json
{
  "message": "Human-readable error description",
  "code": "ERROR_CODE_CONSTANT",
  "timestamp": "2026-06-07T00:57:02Z"
}
```
