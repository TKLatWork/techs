# Data Model: User Authentication & Access Control

**Feature**: 004-user-auth-access-control
**Date**: 2026-06-07

## Entities

### User

Represents a person with an account in the system.

| Field | Type | Required | Constraints | Description |
|-------|------|----------|-------------|-------------|
| id | String (UUID) | Yes | Unique, auto-generated | Primary identifier |
| username | String | Yes | Unique, non-blank | Used for login; case-sensitive |
| displayName | String | Yes | Non-blank | Human-readable name shown in UI |
| passwordHash | String | Yes | Non-blank | BCrypt-hashed password; never exposed outside app module |
| roleId | String (UUID) | Yes | Must reference existing Role.id | Exactly one role per user |
| individualRightIds | Set\<String\> | No | Each must reference existing Right.id | Rights assigned directly to this user (in addition to role rights) |
| createdAt | Instant | Yes | Auto-set on creation | Account creation timestamp |

**Validation rules** (domain module):
- `username` must not be null or blank
- `displayName` must not be null or blank
- `passwordHash` must not be null or blank
- `roleId` must reference a valid role
- `username` must be unique across all users

**Effective rights computation**: `effectiveRights = role.rights ∪ individualRightIds`

### Role

A named group of rights that can be assigned to users.

| Field | Type | Required | Constraints | Description |
|-------|------|----------|-------------|-------------|
| id | String (UUID) | Yes | Unique, auto-generated | Primary identifier |
| name | String | Yes | Unique, non-blank | Role name (e.g., "Admin", "User", "Visitor") |
| description | String | No | — | Human-readable description |
| rightIds | Set\<String\> | No | Each must reference existing Right.id | Rights included in this role |
| builtIn | boolean | Yes | Immutable after creation | True for Admin, User, Visitor; false for custom roles |
| immutable | boolean | Yes | Immutable after creation | True only for Admin role; prevents any modification |

**Built-in roles**:

| Role | builtIn | immutable | Default Rights |
|------|---------|-----------|----------------|
| Admin | true | true | All rights (computed dynamically) |
| User | true | false | read:/profile |
| Visitor | true | false | read:/profile |

**Deletion behavior**: When a custom role is deleted, all users assigned to it are reassigned to the Visitor role (FR-026). Built-in roles cannot be deleted.

### Right (Permission)

An individual capability or access grant. Two tiers: base rights and feature rights.

| Field | Type | Required | Constraints | Description |
|-------|------|----------|-------------|-------------|
| id | String (UUID) | Yes | Unique, auto-generated | Primary identifier |
| name | String | Yes | Unique, non-blank | Right identifier (e.g., "read:/admin", "manage_users") |
| type | Enum | Yes | BASE or FEATURE | Tier classification |
| urlPattern | String | Conditional | Required for BASE type; optional for FEATURE | URL path pattern for base rights (e.g., "/admin", "/profile") |
| action | Enum | Conditional | Required for BASE type: READ or WRITE | Action type for base rights |
| description | String | No | — | Human-readable description |
| source | Enum | Yes | BUILT_IN or FEATURE_REGISTERED | Origin of this right |
| featureId | String | Conditional | Required when source = FEATURE_REGISTERED | Identifier of the registering feature |
| builtIn | boolean | Yes | Immutable after creation | True for system-defined rights |

**Built-in base rights**:

| Name | Type | Action | URL Pattern | Assigned To |
|------|------|--------|-------------|-------------|
| read:/profile | BASE | READ | /profile | User, Visitor |
| read:/admin | BASE | READ | /admin | Admin |
| write:/admin | BASE | WRITE | /admin | Admin |
| read:/permissions | BASE | READ | /permissions | Admin |

**Built-in feature rights**:

| Name | Type | Description | Assigned To |
|------|------|-------------|-------------|
| manage_users | FEATURE | Can manage user accounts and roles | Admin |
| manage_permissions | FEATURE | Can access permission management page | Admin |

**Feature-registered rights**: Created via the permission registration API (FR-027). Auto-approved (FR-030). Idempotent upsert on duplicate names (FR-031). Deleted when the registering feature is disabled (FR-032) or individually by admin (FR-033).

### Session

Represents an authenticated user's active login state.

| Field | Type | Required | Constraints | Description |
|-------|------|----------|-------------|-------------|
| token | String (UUID) | Yes | Unique, auto-generated | Session token sent to client |
| userId | String (UUID) | Yes | Must reference existing User.id | The authenticated user |
| expiresAt | Instant | Yes | Must be in the future | Expiration time (createdAt + 7 days) |
| createdAt | Instant | Yes | Auto-set on creation | Session creation timestamp |

**Lifecycle**:
- Created on successful login (FR-003) — multiple concurrent sessions per user allowed
- Validated on each API request via `SessionAuthFilter`
- Deleted on logout (FR-009) — only the current session is invalidated; other sessions for the same user remain active
- Expired sessions are treated as unauthenticated (FR-019)

### PasswordEncoder (Domain Interface)

Not an entity — a domain service interface for password hashing abstraction.

| Method | Parameters | Returns | Description |
|--------|-----------|---------|-------------|
| encode | String rawPassword | String | Hash a plaintext password |
| matches | String rawPassword, String encodedPassword | boolean | Compare raw password against hash |

**Location**: `com.techs.domain.userauth.service.PasswordEncoder`
**Implementation**: `com.techs.app.service.BCryptPasswordEncoder` (wraps Spring Security's `BCryptPasswordEncoder`)

**Purpose**: Enables domain `AuthService` to perform password comparison without infrastructure leakage. The domain module defines the interface; the app module provides the concrete implementation via Spring dependency injection.

## Relationships

```
User ──[N:1]──> Role        (each user has exactly one role)
Role ──[N:M]──> Right       (each role has zero or more rights)
User ──[N:M]──> Right       (each user can have zero or more individual rights)
Session ──[N:1]──> User     (each session belongs to one user)
Right ──[0..1]──> Feature   (feature-registered rights reference a feature identifier)
```

**Effective rights** for a user = `role.rights ∪ user.individualRights`

## State Transitions

### User Lifecycle

```
[Registration] → Active (Visitor role)
[Admin promotes] → Active (User role)
[Admin changes role] → Active (new role)
[Admin deletes] → Deleted (removed from system)
```

### Session Lifecycle

```
[Login] → Active (token issued, expiresAt = now + 7 days)
[Logout] → Invalidated (token deleted)
[Expiration] → Expired (expiresAt < now, treated as unauthenticated)
```

### Right Lifecycle (Feature-Registered)

```
[Feature registers] → Active (auto-approved, available for assignment)
[Feature re-registers] → Active (idempotent upsert, definition updated)
[Admin deletes] → Deleted (unassigned from all roles/users)
[Feature disabled] → Deleted (all rights from that feature deleted and unassigned)
```

### Role Lifecycle

```
[Created by admin] → Active (custom role with assigned rights)
[Rights modified] → Active (rights added/removed)
[Deleted by admin] → Deleted (users reassigned to Visitor)
```

## In-Memory Storage Design

All entities are stored in `ConcurrentHashMap` instances within Spring `@Repository` beans:

| Repository | Key | Value | Indexes |
|------------|-----|-------|---------|
| UserRepository | userId (UUID) | User | username → userId (unique) |
| RoleRepository | roleId (UUID) | Role | name → roleId (unique) |
| RightRepository | rightId (UUID) | Right | name → rightId (unique), featureId → Set\<rightId\> |
| SessionRepository | token (UUID) | Session | userId → Set\<token\> |

**Thread safety**: `ConcurrentHashMap` provides thread-safe read/write operations. Compound operations (check-then-act) use `synchronized` blocks or `ConcurrentHashMap.compute()` for atomicity.

## DTO Mapping (api module)

| Domain Entity | DTO | Notes |
|---------------|-----|-------|
| User | UserDto | Excludes passwordHash; includes username, displayName, roleName, effectiveRights (List\<RightDto\>), individualRights (List\<RightDto\>) |
| Role | RoleDto | Includes name, description, rightIds, builtIn, immutable |
| Right | RightDto | Includes name, type, urlPattern, action, description, source, featureId |
| Session | (not exposed) | Only the token string is returned in LoginResponse |
| — | LoginRequest | username, password |
| — | LoginResponse | token, expiresAt, user (UserDto with effectiveRights + individualRights) |
| — | RegisterRequest | username, password, displayName |
| — | RegisterResponse | user (UserDto with effectiveRights + individualRights) |
| — | PermissionRegistrationRequest | featureId, permissions (List of name, type, urlPattern, action, description) |
| — | UserPermissionsDto | userId, username, displayName, role (RoleDto), effectiveRights (List\<RightDto\>), individualRights (List\<RightDto\>) |
| — | ErrorResponse | message, code, timestamp |
