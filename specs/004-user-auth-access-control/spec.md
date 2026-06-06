# Feature Specification: User Authentication & Access Control

**Feature Branch**: `004-user-auth-access-control`

**Created**: 2026-06-06

**Status**: Draft

**Input**: User description: "Review current project structure and make it modularized. Build a userInfo/login/access right control feature to prove this structure"

## Clarifications

### Session 2026-06-06

- Q: Use email or username for login? → A: Username (not email). Login accepts username + password.
- Q: How is the initial admin account provisioned? → A: Built-in admin with preset credentials (`admin` / `123`), seeded at system initialization. Cannot be deleted or demoted.
- Q: How are new user accounts created? → A: Self-registration via public registration page. New accounts are auto-assigned the "Visitor" role.
- Q: What access difference exists between Visitor and User roles? → A: Visitor has read-only profile view only. User has full standard access (profile + other features). Admin can promote Visitor → User.
- Q: What are the minimum password requirements? → A: None — password must only be non-empty (non-blank). No length or complexity rules.
- Q: How long should a session remain valid? → A: 7 days. Sessions expire after 7 days unless the user logs out explicitly.

### Session 2026-06-07

- Q: How should the authorization system work? → A: Permission-based model with URL enforcement on both frontend and backend. Individual rights can be specified per feature. A role functions as a group of rights. Rights can also be assigned directly to individual users. An admin management page supports configuring all of this.
- Q: What granularity should rights have? → A: Two-tier model — base rights are simple read/write on page/URL (e.g., "read:/admin", "write:/profile"). Feature-level permissions overlay on top of the base URL rights for finer control (e.g., "manage_users" overlays on the admin page).
- Q: Can a user have multiple roles? → A: No — single role per user. Additional access is granted via individually assigned rights on top of the role.
- Q: Can admins create custom rights? → A: Rights are not manually created by admins. Instead, features register their own permissions via an API. The admin account manages and approves registered permissions through the management page. (Superseded by session 2026-06-07b)
- Q: What happens when a custom role is deleted while users are assigned to it? → A: Users are automatically reassigned to the built-in Visitor role as a safe fallback.
- Q: Can admins modify built-in roles? → A: Yes — Admins can add/remove rights from the built-in User and Visitor roles. The Admin role is immutable and always has all rights.

### Session 2026-06-07b

- Q: How should new permissions be introduced into the system? → A: Features register their own permissions via an API. The admin account manages registered permissions through the management page. This replaces the previous "built-in rights only" constraint.
- Q: Do feature-registered permissions require admin approval? → A: No — always auto-approved. Feature-registered permissions are immediately available for assignment to roles and users upon registration.
- Q: What happens on duplicate permission registration? → A: Idempotent upsert — re-registration updates the existing permission silently; latest definition wins.
- Q: What happens to feature-registered permissions when the registering feature is removed? → A: Admin manual trigger — admins mark features as "disabled" in the management page, which triggers auto-deletion of that feature's permissions and unassignment from all roles/users.
- Q: Can admins delete individual feature-registered permissions without disabling the entire feature? → A: Yes — admins can delete individual feature-registered permissions; they are unassigned from all roles and users upon deletion.

### Session 2026-06-07c

- Q: What is the prerequisite for implementing this feature? → A: The web module MUST be restructured to a feature-based directory structure before any feature implementation begins. A router library must also be added.
- Q: Which router library should be used? → A: `react-router-dom` (v6/v7) — industry standard for React, supports route guards, nested routes, and lazy loading.
- Q: What internal structure should each feature folder follow? → A: Standard — `components/`, `hooks/`, `services/`, `types/`, `__tests__/` directories. Consistent and predictable per feature.
- Q: How should route definitions be organized? → A: Per-feature — each feature defines its own routes and registers them with the router.
- Q: Should the `pages/` directory still exist? → A: Yes — `pages/` remains as a top-level directory holding route-level wrapper components. Features hold their own feature-specific components, hooks, services, and tests.
- Q: How should features register their routes with the app shell? → A: Dynamic import with auto-discovery — features are auto-discovered via `import.meta.glob` (Vite), lazy-loaded with `React.lazy()`, and zero-config for new features. Adding a new feature only requires creating `features/<name>/routes.tsx`.
- Q: What UI component library should be used? → A: MUI (Material UI) — installed as part of the web module restructuring (P0 prerequisite). All UI components across features and shared code should use MUI components.

### Session 2026-06-07e

- Q: Should domain and api modules also be feature-based? → A: Yes — domain and api modules MUST follow a feature-based package structure. The app module uses a flat structure per constitution Principle III (see session 2026-06-07f). User-auth is one feature. The P0 restructuring applies to all modules, not just web.
- Q: What internal package structure should each Java feature package follow? → A: DDD-style — `entity/`, `valueobject/`, `service/`, `repository/` sub-packages within each feature package.
- Q: Where should existing ProjectInfo and UserDto be placed? → A: Remove them — they are placeholder code from project structure setup and will be deleted during restructuring.
- Q: What happens to existing Java tests during restructuring? → A: Delete all — `ProjectInfoTest`, `UserDtoTest`, `ProjectServiceTest`, `DomainDependencyTest`, `ApiContractDependencyTest` are removed along with the placeholder code they test.
- Q: What belongs in shared packages? → A: Cross-cutting only — base classes, utilities, exceptions, and infrastructure used by 2+ features. Feature-specific code stays in feature packages.

### Session 2026-06-07f

- Q: Should the app module use feature-based sub-packages or a flat structure? → A: Flat structure — the app module uses `com.techs.app.controller/`, `com.techs.app.service/`, `com.techs.app.repository/`, `com.techs.app.config/`, `com.techs.app.security/` per constitution Principle III. The app module is the orchestration layer and does NOT use feature-based sub-packages. This supersedes the earlier clarification (session 2026-06-07e) that stated app follows feature-based packages.
- Q: Where should authentication business logic live given that domain never sees plaintext passwords? → A: Domain AuthService is the full orchestrator — it receives repository interfaces (UserRepository, SessionRepository) and a PasswordEncoder interface (defined in domain, implemented in app) via constructor injection. It performs all authentication logic through these abstractions: credential validation, password hash comparison, session creation, and user lookup. The app module provides concrete implementations. This preserves DDD purity — domain owns all business rules, app only provides infrastructure.
- Q: How should the frontend obtain the current user's effective rights for route guards and feature-right checks? → A: Extend the existing `GET /api/auth/me` endpoint to include `effectiveRights` (List of RightDto) and `individualRights` (List of RightDto) in the response. No separate endpoint needed. The frontend calls this on login and caches the rights for permission checks.
- Q: How does the system handle concurrent login attempts from the same account? → A: Allow multiple concurrent sessions per user. Each login creates a new independent session. Logout invalidates only the current session, not other active sessions for the same user.
- Q: Can an Admin remove the "manage_permissions" right from their own role, potentially locking out all admin access? → A: No — the Admin role is fully immutable. The management page MUST block all modifications to the Admin role's rights, preventing any self-lockout scenario.
- Q: Should the RegisterResponse include effectiveRights and individualRights in the UserDto? → A: Yes — RegisterResponse MUST include the full UserDto with `effectiveRights` and `individualRights` fields, consistent with the data-model definition. A newly registered Visitor user will have effectiveRights (e.g., read:/profile from Visitor role) and empty individualRights. This avoids requiring a separate `/api/auth/me` call after registration.

## User Scenarios & Testing *(mandatory)*

### User Story 0 - All Modules Feature-Based Restructuring (Priority: P0)

All four modules (web, domain, api, app) are restructured from their current flat layouts into feature-based architectures before any feature work begins. User-auth is the first feature, and its code is organized into feature-specific packages/directories within each module. The web module additionally gets a client-side router (`react-router-dom`) and UI component library (MUI) installed and wired up. All existing code is migrated into the appropriate feature packages and all existing tests continue to pass.

**Why this priority**: This is the foundational prerequisite for the entire feature. Without a feature-based structure across all modules, the auth/user/permission features would be built on a flat architecture that cannot scale. Every module must organize code by feature (not by layer) so that adding a new feature in the future means adding a new feature package in each module, not scattering code across unrelated directories.

**Independent Test**: Can be tested by verifying all modules build, all existing tests pass, the web router renders a home page at `/`, and the directory/package structure follows the feature-based layout in all four modules.

**Acceptance Scenarios**:

1. **Given** the current project, **When** the restructuring is complete, **Then** the domain module organizes code under feature packages with DDD-style sub-packages (e.g., `com.techs.domain.userauth.entity`, `com.techs.domain.userauth.valueobject`, `com.techs.domain.userauth.service`) with a `shared` package for cross-feature code
2. **Given** the current project, **When** the restructuring is complete, **Then** the api module organizes DTOs under feature packages (e.g., `com.techs.api.userauth.model`) with a `shared` package for cross-feature DTOs
3. **Given** the current project, **When** the restructuring is complete, **Then** the app module organizes code under a flat structure by technical concern (e.g., `com.techs.app.controller`, `com.techs.app.service`, `com.techs.app.repository`, `com.techs.app.config`, `com.techs.app.security`) with cross-feature imports permitted
4. **Given** the current project, **When** the restructuring is complete, **Then** the web module contains `app/`, `features/`, `shared/`, `pages/`, and `styles/` directories under `src/`, and each feature folder contains `components/`, `hooks/`, `services/`, `types/`, and `__tests__/` subdirectories
5. **Given** the restructured project, **When** all modules are built, **Then** every module builds successfully with no errors and placeholder classes (`ProjectInfo`, `UserDto`, `ProjectService`) are deleted
6. **Given** the restructured project, **When** all tests are run, **Then** all new tests pass across all modules (all placeholder tests have been deleted)
7. **Given** the restructured web module with router, **When** a user navigates to `/`, **Then** the home page is rendered
8. **Given** the restructured web module, **When** a user navigates to a non-existent route, **Then** a fallback/not-found page or redirect is displayed
9. **Given** any restructured module, **When** code in one feature package attempts to import from another feature package, **Then** the import boundary rule is enforced (features only import from `shared`, not from other features)
10. **Given** the restructured web module with auto-discovery, **When** a new feature is added with a `routes.tsx` file, **Then** its routes are automatically available without modifying the app shell
11. **Given** the restructured web module with MUI, **When** a component renders a MUI element (e.g., `<Button>`), **Then** it renders with MUI styling and the MUI theme is applied globally via the app shell's theme provider

---

### User Story 1 - User Login (Priority: P1)

A user navigates to the application and is presented with a login page. The user enters their credentials (username and password), submits the form, and upon successful authentication, is redirected to the home page where they see a personalized greeting and their user information.

**Why this priority**: Authentication is the gateway to the entire application. Without login, no other feature (user info, access control) can function. This is the foundational slice that proves the modular architecture end-to-end: the web frontend sends credentials to the backend API, which validates against domain rules, and returns a session token.

**Independent Test**: Can be fully tested by navigating to the login page, entering valid credentials, and verifying redirection to the home page with a personalized greeting. This single slice exercises the web → api → domain dependency chain.

**Acceptance Scenarios**:

1. **Given** the user is not authenticated, **When** they navigate to any page, **Then** they are redirected to the login page
2. **Given** the user is on the login page, **When** they enter valid credentials and submit, **Then** they are authenticated and redirected to the home page
3. **Given** the user is on the login page, **When** they enter invalid credentials and submit, **Then** an error message is displayed without revealing which field was incorrect
4. **Given** the user is authenticated, **When** they revisit the login page, **Then** they are redirected to the home page automatically

---

### User Story 2 - View User Profile (Priority: P1)

An authenticated user can view their profile page, which displays their personal information including username, display name, role, and account creation date. This validates that the web module can fetch and display data from the backend through the API contract layer.

**Why this priority**: User profile display is the primary proof that the modular data flow works correctly — the web module consumes generated TypeScript types from the api module, fetches data from the app backend, and renders it. This validates the entire cross-module contract.

**Independent Test**: Can be tested by logging in, navigating to the profile page, and verifying that all user fields (username, display name, role, creation date) are displayed correctly.

**Acceptance Scenarios**:

1. **Given** the user is authenticated, **When** they navigate to the profile page, **Then** their username, display name, role, and account creation date are displayed
2. **Given** the user is not authenticated, **When** they attempt to access the profile page, **Then** they are redirected to the login page

---

### User Story 3 - Permission-Based Access Control (Priority: P2)

The system enforces permission-based access control using a two-layer model: **rights** (individual capabilities) and **roles** (named groups of rights). Access is enforced based on URL paths on both the frontend (route guards) and backend (endpoint protection). A user's effective rights are the union of rights from their assigned role plus any individually assigned rights.

**Why this priority**: Access control is critical for security but builds on top of authentication (P1). The permission model proves that the domain module can encapsulate authorization logic independently of the web and app modules, validating the shared domain library's role in the architecture.

**Independent Test**: Can be tested by logging in as a user without a specific right and verifying that the corresponding URL/page returns access denied on both frontend and backend, then assigning that right and verifying access is granted.

**Acceptance Scenarios**:

1. **Given** a user without a base "read" right for a URL path, **When** they navigate to that URL on the frontend, **Then** they see an "Access Denied" message (frontend base-right enforcement)
2. **Given** a user without a base "read" right for a backend endpoint, **When** they send a request to that endpoint, **Then** the backend returns an authorization error (backend base-right enforcement)
3. **Given** a user with a base "read" right but without a specific feature right on a page, **When** they access the page, **Then** the page loads but the feature-gated functionality is hidden or disabled
4. **Given** a user with a base "write" right for a URL path, **When** they submit a write operation to that endpoint, **Then** the operation succeeds; without the "write" right, the backend rejects it
5. **Given** a user with an individually assigned right (not from their role), **When** they access the corresponding URL or feature, **Then** access is granted based on the individual right
6. **Given** a user's effective rights change (role reassigned or individual right added/removed), **When** they next load a page or make a request, **Then** the new rights take effect immediately

---

### User Story 4 - Logout (Priority: P2)

An authenticated user can log out of the system. After logging out, their session is invalidated, and they are redirected to the login page. Subsequent attempts to access protected pages require re-authentication.

**Why this priority**: Logout completes the authentication lifecycle. It ensures session cleanup works correctly across the web and backend modules.

**Independent Test**: Can be tested by logging in, clicking logout, and verifying that protected pages are no longer accessible without re-login.

**Acceptance Scenarios**:

1. **Given** the user is authenticated, **When** they click logout, **Then** their session is invalidated and they are redirected to the login page
2. **Given** the user has logged out, **When** they attempt to access a protected page, **Then** they are redirected to the login page

---

### User Story 5 - Self-Registration (Priority: P1)

A visitor navigates to the application and sees a registration link on the login page. They click through to a registration page, enter a unique username and a password, and submit the form. Upon successful registration, they are automatically assigned the "Visitor" role and redirected to the login page where they can sign in with their new credentials.

**Why this priority**: Self-registration is the entry point for all non-admin users. Without it, only the built-in admin can use the system. This is essential for proving the modular architecture with multiple user types.

**Independent Test**: Can be tested by navigating to the registration page, creating a new account, and verifying login works with the new credentials and the Visitor role is assigned.

**Acceptance Scenarios**:

1. **Given** a visitor is on the registration page, **When** they enter a unique username and valid password and submit, **Then** an account is created with the "Visitor" role and they are redirected to the login page
2. **Given** a visitor is on the registration page, **When** they enter a username that already exists and submit, **Then** an error message indicates the username is taken
3. **Given** a visitor is on the login page, **When** they look for a way to create an account, **Then** a link to the registration page is visible

---

### User Story 6 - Permission Management Page (Priority: P2)

An Admin user accesses a dedicated management page where they can view and configure the authorization system. The page allows them to: manage roles (create, edit, delete roles and assign rights to them), view all available rights in the system (both built-in and feature-registered, with source identifiers), manage registered features (view all features that have registered permissions and disable features to remove their permissions), and manage user permissions (assign roles to users and add/remove individual rights per user).

**Why this priority**: The management page is the operational interface for the permission system. Without it, admins cannot configure authorization without direct database access. It also exercises the full modular stack: web UI → API contracts → backend logic → domain rules.

**Independent Test**: Can be tested by logging in as admin, navigating to the management page, creating a new role with specific rights, assigning it to a user, and verifying the user gains the expected access.

**Acceptance Scenarios**:

1. **Given** an Admin is on the management page, **When** they create a new role and assign a set of rights (built-in or approved feature-registered) to it, **Then** the role is saved and available for assignment to users
2. **Given** an Admin is on the management page, **When** they assign a role to a user, **Then** the user's effective rights update to include all rights from that role
3. **Given** an Admin is on the management page, **When** they add an individual right directly to a user, **Then** the user gains that right in addition to their role's rights
4. **Given** an Admin is on the management page, **When** they remove an individual right from a user, **Then** the user loses that right (unless it is also provided by their role)
5. **Given** an Admin is on the management page, **When** they view a user's permissions, **Then** they see the user's role, the rights from that role, and any individually assigned rights displayed clearly
6. **Given** an Admin is on the management page, **When** they view the rights catalog, **Then** they see all built-in rights and all feature-registered rights with their source feature identifiers
7. **Given** an Admin is on the management page, **When** they mark a feature as "disabled", **Then** all permissions registered by that feature are deleted and unassigned from all roles and users
8. **Given** an Admin is on the management page, **When** they delete an individual feature-registered permission, **Then** that permission is removed and unassigned from all roles and users
9. **Given** a non-Admin user, **When** they attempt to access the management page, **Then** they see an "Access Denied" message

---

### User Story 7 - Feature Permission Registration (Priority: P2)

A developer building a new feature needs to integrate with the authorization system. They call the permission registration API (authenticated as admin) to register the permissions their feature requires — specifying URL paths, right types, and descriptions. The registered permissions are immediately available for assignment to roles and users, and appear in the permission management page for visibility.

**Why this priority**: Permission registration enables the system to scale as new features are added without requiring code changes to the auth module itself. It proves that the auth system is extensible and that the modular architecture supports cross-feature integration through a well-defined API contract.

**Independent Test**: Can be tested by calling the registration API with admin credentials to register a new permission, verifying it appears on the management page, assigning it to a role/user, and verifying access control works.

**Acceptance Scenarios**:

1. **Given** an authenticated admin, **When** they call the permission registration API with a valid permission definition, **Then** the permission is created and immediately available for assignment to roles and users
2. **Given** a feature-registered permission, **When** an Admin views the management page, **Then** the permission appears in the rights catalog with its source feature identifier
3. **Given** a non-admin user, **When** they attempt to call the permission registration API, **Then** the request is rejected with an authorization error
4. **Given** a feature-registered permission that has been assigned to a role, **When** a user with that role accesses the corresponding URL, **Then** access is granted normally

---

### Edge Cases

- What happens when a user's session expires while they are on a protected page?
- How does the system handle concurrent login attempts from the same account? → Resolved: Multiple concurrent sessions allowed per user. Each login creates an independent session; logout invalidates only the current session.
- What happens when the backend is unreachable during login or registration?
- How does the system handle malformed or tampered session tokens?
- What happens when a user submits a registration form with a blank (empty or whitespace-only) password?
- How does the system handle rapid repeated registration attempts (rate limiting)?
- What happens when an Admin deletes a role that is currently assigned to users? → Resolved: Users are reassigned to Visitor role (FR-026).
- What happens when a right is removed from a role while users with that role are actively using the system?
- Can an Admin remove the "manage_permissions" right from their own role, potentially locking out all admin access? → Resolved: Admin role is fully immutable — management page blocks all modifications to Admin role's rights, making self-lockout impossible (FR-021).
- What happens when frontend and backend disagree on a user's rights (e.g., rights change mid-session)?
- What happens when an Admin tries to delete a built-in role (Admin, User, Visitor) or a built-in right? → Resolved: Built-in roles and rights cannot be deleted; only User and Visitor roles can have their rights modified.
- What happens when an Admin removes all rights from the Visitor role, making it effectively useless?
- What happens when a feature registers a permission with the same name as an existing built-in or previously registered right? → Resolved: Idempotent upsert — re-registration updates the existing permission silently (FR-031).
- What happens when a feature registers a permission for a URL path that overlaps with an existing right's URL pattern?
- What happens to approved and assigned feature-registered permissions if the registering feature is later removed or disabled? → Resolved: Admin marks feature as disabled on management page, triggering auto-deletion and unassignment of all that feature's permissions (FR-032).
- Can an Admin delete a feature-registered permission that is currently assigned to roles/users? → Resolved: Yes — admins can delete individual feature-registered permissions; they are unassigned from all roles/users (FR-033).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-000**: All four modules (web, domain, api, app) MUST be restructured to feature-based layouts before any feature implementation begins; existing code must be migrated without breaking functionality. Each module MUST organize code by feature (not by layer), with user-auth as the first feature. Cross-feature imports are forbidden within any module; features may only import from `shared`.
  - **Web**: Feature folders under `src/features/` with standard structure: `components/`, `hooks/`, `services/`, `types/`, `__tests__/`. Each feature defines routes in `routes.tsx`. App shell auto-discovers routes via `import.meta.glob`.
  - **Domain**: Feature packages under `com.techs.domain.<feature>.` with DDD-style sub-packages: `entity/` (aggregate roots and entities), `valueobject/` (immutable value types), `service/` (domain services / business rules). Shared code in `com.techs.domain.shared`.
  - **API**: Feature packages under `com.techs.api.<feature>.model` (DTOs only). Shared DTOs in `com.techs.api.shared.model`.
  - **App**: Flat structure under `com.techs.app/` organized by technical concern: `controller/` (REST endpoints), `service/` (application services), `repository/` (data access), `config/` (Spring configuration), `security/` (filters and security infrastructure). Cross-feature imports ARE permitted — the app module is the orchestration layer.
- **FR-000a**: The `react-router-dom` library (v6/v7) MUST be installed and integrated into the web module, enabling URL-based navigation between pages with support for route guards
- **FR-000b**: MUI (Material UI) MUST be installed and configured as the UI component library for the web module; all UI components across features and shared code MUST use MUI components for consistent design and accessibility
- **FR-001**: System MUST provide a login page that accepts username and password
- **FR-002**: System MUST authenticate users against stored credentials using secure password hashing
- **FR-003**: System MUST issue a session token upon successful authentication
- **FR-004**: System MUST reject login attempts with invalid credentials and display a generic error message
- **FR-005**: System MUST protect all authenticated routes from unauthenticated access
- **FR-006**: System MUST provide a user profile page displaying username, display name, role, and account creation date. The `GET /api/auth/me` endpoint MUST return the current user's profile data along with their `effectiveRights` (all rights from role + individual rights) and `individualRights` (rights assigned directly to the user) to support frontend permission enforcement.
- **FR-007**: System MUST assign the "Visitor" role to all self-registered users at account creation
- **FR-008**: System MUST enforce permission-based access control on both frontend (URL route guards) and backend (endpoint protection); a user's effective rights are the union of their role's rights and individually assigned rights
- **FR-009**: System MUST provide a logout mechanism that invalidates the user's session
- **FR-010**: System MUST redirect unauthenticated users to the login page when they access protected routes
- **FR-011**: System MUST redirect authenticated users away from the login page to the home page
- **FR-012**: System MUST display an "Access Denied" message when a user lacks the required right for a page or action, on both frontend and backend
- **FR-013**: System MUST create a built-in admin account with preset credentials at initialization; this account has the Admin role and cannot be deleted or demoted
- **FR-014**: System MUST provide a public registration page where new users can create an account by providing a username and password
- **FR-015**: System MUST enforce username uniqueness during registration and reject duplicate usernames with a clear error message
- **FR-016**: System MUST restrict users to only the URLs/features covered by their effective rights; users without a specific right cannot access the corresponding URL or action
- **FR-017**: System MUST allow Admins to change a user's role, immediately updating the user's effective rights
- **FR-018**: System MUST reject registration and login attempts where the password is blank (empty or whitespace-only); no other password complexity rules apply
- **FR-019**: System MUST expire sessions after 7 days; expired sessions are treated as unauthenticated
- **FR-020**: System MUST provide a permission management page accessible only to users with the "manage_permissions" right (Admin role by default)
- **FR-021**: The permission management page MUST allow Admins to create, edit, and delete custom roles, and to edit the built-in User and Visitor roles (add/remove rights from both built-in and feature-registered rights); the built-in Admin role is immutable and always retains all rights
- **FR-022**: The permission management page MUST allow Admins to assign a role to any user and to add or remove individual rights (built-in or approved feature-registered) directly on a user
- **FR-023**: The permission management page MUST display each user's current role, rights inherited from that role, and individually assigned rights
- **FR-024**: System MUST support a two-tier right model: base rights (read/write on URL paths) and feature rights (overlay permissions on specific capabilities within a URL); both frontend route guards and backend endpoint protection MUST check base URL rights, and feature rights MUST be checked within the page/endpoint logic
- **FR-025**: System MUST provide built-in base rights for core URL paths (read:/profile, read:/admin, write:/admin, read:/permissions) and built-in feature rights (manage_users, manage_permissions) at initialization, associated with the built-in roles
- **FR-026**: When an Admin deletes a custom role that has users assigned, the system MUST automatically reassign those users to the built-in Visitor role
- **FR-027**: System MUST provide a permission registration API that allows other features to register their own permissions (base rights and/or feature rights) with the auth system
- **FR-028**: The permission registration API MUST require authentication with an admin account; only users with the "manage_permissions" right can register new permissions
- **FR-029**: Each feature-registered permission MUST include: a unique name, type (base or feature), associated URL path pattern, description, and the registering feature's identifier
- **FR-030**: Feature-registered permissions MUST be auto-approved upon registration and immediately available for assignment to roles and users; they MUST appear in the permission management page for visibility and management
- **FR-031**: The permission registration API MUST support idempotent upsert: re-registering a permission with the same name updates the existing permission silently with the latest definition
- **FR-032**: The permission management page MUST allow admins to mark a feature as "disabled"; when a feature is disabled, the system MUST automatically delete all permissions registered by that feature and unassign them from all roles and users
- **FR-033**: The permission management page MUST allow admins to delete individual feature-registered permissions; deleted permissions are unassigned from all roles and users

### Key Entities

- **User**: Represents a person with an account. Attributes: unique identifier, username (unique, used for login), display name, hashed password, role (exactly one), individual rights (zero or more), account creation date. A user's effective rights = rights from their role + individually assigned rights.
- **Right** (Permission): An individual capability or access grant. Two tiers: (1) **Base rights** — simple read or write access on a URL path (e.g., "read:/admin", "write:/profile"); (2) **Feature rights** — overlay permissions that grant specific capabilities within a page/URL (e.g., "manage_users", "export_data"). Attributes: unique identifier, name (unique), type (base or feature), associated URL path pattern, description, source (built-in or feature-registered), registering feature identifier (if feature-registered). Rights can be assigned to roles or directly to users.
- **Role**: A named group of rights. Attributes: unique identifier, name (unique), description, set of associated rights, built-in flag. Built-in roles: Admin (all rights, immutable), User (standard rights, editable by admins), Visitor (read-only profile, editable by admins). Custom roles can be created by admins using built-in rights. Each user has exactly one role.
- **Session**: Represents an authenticated user's active login state. Attributes: session token, user reference, expiration time (7 days from creation).
- **Built-in Admin**: A pre-seeded administrator account created at system initialization with a preset username (`admin`) and password (`123`), ensuring the system always has at least one admin capable of managing other users. This account has the Admin role and cannot be deleted or demoted.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can complete the login flow (enter credentials → see home page) in under 10 seconds
- **SC-002**: 100% of protected pages are inaccessible without valid authentication
- **SC-003**: 100% of protected URLs are inaccessible to users lacking the corresponding right, enforced on both frontend and backend
- **SC-004**: The feature exercises all four modules (web, api, app, domain) with correct dependency direction, validating the modular architecture
- **SC-005**: User profile data displayed in the web module matches the data returned by the backend with zero type mismatches
- **SC-006**: Each module (web, api, app, domain) can be independently tested for its portion of this feature without requiring the full system to be running
- **SC-007**: Admins can create a custom role, assign rights, assign it to a user, and verify the user gains the expected access — all through the management page without direct database access
- **SC-008**: Admins can modify the built-in User or Visitor role's rights and verify the changes take effect for all users with that role
- **SC-009**: A new feature can register its permissions via the API and verify that users with the assigned rights can access the feature's protected URLs — all without modifying the auth module's code
- **SC-010**: All four modules (web, domain, api, app) follow feature-based structures with user-auth as the first feature package, and the web module has both `react-router-dom` and MUI installed and functional before any feature code is written

## Assumptions

- All four modules (web, domain, api, app) will be restructured to feature-based layouts with user-auth as the first feature package, and the web module will have `react-router-dom` (v6/v7) and MUI (Material UI) installed as the very first task (P0), before any auth, user, or permission feature work begins
- Users have stable internet connectivity for web application access
- A built-in admin account with preset credentials is seeded at system initialization; this account cannot be deleted or demoted
- Authentication uses session-based tokens (not OAuth or third-party SSO) for this initial implementation
- The domain module owns all business rules for authentication and authorization through dependency inversion. Domain services receive repository interfaces and a `PasswordEncoder` interface (defined in domain, implemented in app) via constructor injection. The app module provides concrete implementations and delegates all business decisions to domain services.
- The api module defines DTOs for login request/response, user profile, and error responses; TypeScript types are generated from these DTOs. The UserDto consistently includes `effectiveRights` and `individualRights` in all API responses that return user data (LoginResponse, RegisterResponse, GET /api/auth/me)
- Password hashing uses industry-standard algorithms (e.g., bcrypt). The domain module defines a `PasswordEncoder` interface; the app module implements it. The domain module receives plaintext passwords from controllers and delegates hash comparison to the injected `PasswordEncoder` — it never stores or exposes plaintext passwords.
- The web module stores session tokens in browser storage and includes them in subsequent API requests
- Mobile-responsive design is out of scope for this feature; desktop browser is the target
- No email-based features (verification, password reset, notifications) are included; the system uses username-based authentication only
- The authorization system uses a permission-based model: individual rights are the atomic unit of access control, roles are named groups of rights, and users can have both a role and individual rights
- URL-based enforcement is the primary authorization mechanism; each right can be mapped to URL path patterns checked by both frontend route guards and backend endpoint protection
- Built-in roles (Admin, User, Visitor) and built-in rights are seeded at system initialization and cannot be deleted; the Admin role is immutable (always all rights); the User and Visitor roles are editable by admins (rights can be added/removed); custom roles can be created by admins using built-in and feature-registered rights; new rights are introduced by features registering them via the permission registration API (auto-approved, no manual approval required)
- The built-in Admin role cannot have its rights modified; it always has all rights to prevent accidental lockout
