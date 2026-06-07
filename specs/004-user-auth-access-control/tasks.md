# Tasks: User Authentication & Access Control

**Input**: Design documents from `/specs/004-user-auth-access-control/`

**Prerequisites**: plan.md (required), spec.md (required), research.md, data-model.md, contracts/

**Tests**: Test tasks follow the three-layer testing pyramid (constitution Principle I, v2.1.0): domain unit tests (JUnit 5, no Spring context), API E2E tests (JUnit 5 + Spring Boot Test, full Spring context), and web E2E/component tests (Vitest + @testing-library/react). Tests MUST fail before implementation (Red-Green-Refactor).

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US5)
- Include exact file paths in descriptions

## Path Conventions

- **domain**: `domain/src/main/java/com/techs/domain/` (entities, value objects, domain services, repository interfaces)
- **api**: `api/src/main/java/com/techs/api/` (DTOs, TypeScript generation)
- **app**: `app/src/main/java/com/techs/app/` (controllers, services, repositories, config, security â€?**flat structure**)
- **web**: `web/src/` (React components, hooks, services, pages)

---

## Phase 1: Setup â€?All Modules Feature-Based Restructuring (US0 / P0)

**Purpose**: Restructure domain, api, and web modules into feature-based architectures. Restructure app module into a flat structure by technical concern. Install `react-router-dom` and MUI. Wire up routing, theme, and auto-discovery. This is the mandatory prerequisite for all feature work.

**Independent Test**: All modules build successfully, placeholder code is deleted, web router renders a home page at `/`, and the directory/package structure follows the correct layout in all four modules.

- [x] T001 [P] Create feature-based package directories in domain module (`entity/`, `valueobject/`, `service/`, `repository/` under `domain/src/main/java/com/techs/domain/userauth/` and `shared/`)
- [x] T002 [P] Create feature-based package directories in api module (`model/` under `api/src/main/java/com/techs/api/userauth/` and `shared/model/`)
- [x] T003 [P] Create flat package directories in app module (`controller/`, `service/`, `repository/`, `config/`, `security/` under `app/src/main/java/com/techs/app/`)
- [x] T004 [P] Create feature-based directory structure in web module (`features/auth/`, `features/profile/`, `features/permissions/` each with `components/`, `hooks/`, `services/`, `types/`, `__tests__/`; `app/`, `shared/`, `pages/`, `styles/`)
- [x] T005 Install `react-router-dom` (v6/v7), `@mui/material`, `@emotion/react`, `@emotion/styled` in `web/package.json`
- [x] T006 Create app shell with BrowserRouter, MUI ThemeProvider, and Suspense in `web/src/app/App.tsx`
- [x] T007 Create MUI theme configuration in `web/src/app/theme.ts`
- [x] T008 Create feature registry with `import.meta.glob` auto-discovery in `web/src/app/featureRegistry.ts`
- [x] T009 [P] Create shared components directory with StatusBadge migration in `web/src/shared/components/`
- [x] T010 [P] Create shared hooks directory with useApi hook in `web/src/shared/hooks/useApi.ts`
- [x] T011 [P] Create shared types, constants, and utils directories in `web/src/shared/types/`, `web/src/shared/constants/`, `web/src/shared/utils/`
- [x] T012 [P] Create HomePage in `web/src/pages/HomePage.tsx`
- [x] T013 [P] Create NotFoundPage in `web/src/pages/NotFoundPage.tsx`
- [x] T014 [P] Create global styles in `web/src/styles/global.css`
- [x] T015 [P] Create test setup file with jest-dom in `web/src/__tests__/setup.ts`
- [x] T016 Update `web/src/main.tsx` entry point to import App shell and global styles
- [x] T017 Delete placeholder classes: `ProjectInfo` in domain, `UserDto` in api, `ProjectService` in app
- [x] T018 Delete placeholder tests: `ProjectInfoTest`, `UserDtoTest`, `ProjectServiceTest`, `DomainDependencyTest`, `ApiContractDependencyTest`
- [x] T019 Verify all modules build successfully (`mvn clean install` for Java; `npm install && npm run build` for web)

**Checkpoint**: All four modules follow correct structures (domain/api/web = feature-based, app = flat), web has react-router-dom and MUI installed and functional, home page renders at `/`, not-found page renders at `*`. Ready for foundational work.

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core domain entities, value objects, repository interfaces and implementations, API DTOs, PasswordEncoder interface, security infrastructure, data seeding, and shared web infrastructure. ALL of these MUST be complete before any user story can be implemented.

**CRITICAL**: No user story work can begin until this phase is complete.

### Domain Layer â€?Value Objects & Entities

- [x] T020 [P] Create RoleType value object enum in `domain/src/main/java/com/techs/domain/userauth/valueobject/RoleType.java`
- [x] T021 [P] Create RightType value object enum (BASE, FEATURE) in `domain/src/main/java/com/techs/domain/userauth/valueobject/RightType.java`
- [x] T022 [P] Create RightAction value object enum (READ, WRITE) in `domain/src/main/java/com/techs/domain/userauth/valueobject/RightAction.java`
- [x] T023 [P] Create Right entity with id, name, type, urlPattern, action, description, source, featureId, builtIn fields in `domain/src/main/java/com/techs/domain/userauth/entity/Right.java`
- [x] T024 [P] Create Role entity with id, name, description, rightIds, builtIn, immutable fields in `domain/src/main/java/com/techs/domain/userauth/entity/Role.java`
- [x] T025 Create User aggregate root entity with id, username, displayName, passwordHash, roleId, individualRightIds, createdAt fields in `domain/src/main/java/com/techs/domain/userauth/entity/User.java`
- [x] T026 [P] Create Session entity with token, userId, expiresAt, createdAt fields in `domain/src/main/java/com/techs/domain/userauth/entity/Session.java`

### Domain Layer â€?Repository Interfaces

- [x] T027 [P] Create UserRepository interface (aggregate root) in `domain/src/main/java/com/techs/domain/userauth/repository/UserRepository.java`
- [x] T028 [P] Create RoleRepository interface in `domain/src/main/java/com/techs/domain/userauth/repository/RoleRepository.java`
- [x] T029 [P] Create RightRepository interface in `domain/src/main/java/com/techs/domain/userauth/repository/RightRepository.java`
- [x] T030 [P] Create SessionRepository interface in `domain/src/main/java/com/techs/domain/userauth/repository/SessionRepository.java`

### Domain Layer â€?Interfaces & Domain Services

- [x] T031 Create PasswordEncoder interface (encode, matches methods) in `domain/src/main/java/com/techs/domain/userauth/service/PasswordEncoder.java`
- [x] T032 Create UserValidator domain service (username/displayName/password non-blank, username uniqueness check via UserRepository) in `domain/src/main/java/com/techs/domain/userauth/service/UserValidator.java`
- [x] T033 Create AuthService domain service skeleton (constructor injection: UserRepository, RoleRepository, SessionRepository, PasswordEncoder; placeholder methods for login, register, getCurrentUser) in `domain/src/main/java/com/techs/domain/userauth/service/AuthService.java`
- [x] T034 Create AuthorizationService domain service skeleton (constructor injection: UserRepository, RoleRepository, RightRepository; placeholder methods for getEffectiveRights, hasRight) in `domain/src/main/java/com/techs/domain/userauth/service/AuthorizationService.java`

### API Layer â€?DTOs

- [x] T035 [P] Create ErrorResponse DTO in `api/src/main/java/com/techs/api/userauth/model/ErrorResponse.java`
- [x] T036 [P] Create LoginRequest DTO in `api/src/main/java/com/techs/api/userauth/model/LoginRequest.java`
- [x] T037 [P] Create LoginResponse DTO (includes UserDto with effectiveRights + individualRights) in `api/src/main/java/com/techs/api/userauth/model/LoginResponse.java`
- [x] T038 [P] Create RegisterRequest DTO in `api/src/main/java/com/techs/api/userauth/model/RegisterRequest.java`
- [x] T039 [P] Create RegisterResponse DTO in `api/src/main/java/com/techs/api/userauth/model/RegisterResponse.java`
- [x] T040 [P] Create UserDto DTO (includes effectiveRights: List\<RightDto\>, individualRights: List\<RightDto\>) in `api/src/main/java/com/techs/api/userauth/model/UserDto.java`
- [x] T041 [P] Create RoleDto DTO in `api/src/main/java/com/techs/api/userauth/model/RoleDto.java`
- [x] T042 [P] Create RightDto DTO in `api/src/main/java/com/techs/api/userauth/model/RightDto.java`
- [x] T043 [P] Create PermissionRegistrationRequest DTO (featureId, permissions list) in `api/src/main/java/com/techs/api/userauth/model/PermissionRegistrationRequest.java`
- [x] T044 [P] Create UserPermissionsDto DTO (userId, username, displayName, role, effectiveRights, individualRights) in `api/src/main/java/com/techs/api/userauth/model/UserPermissionsDto.java`
- [x] T045 Configure typescript-generator plugin and verify TypeScript type generation in `api/pom.xml`

### App Layer â€?Repository Implementations

- [x] T046 [P] Implement InMemoryUserRepository (ConcurrentHashMap, username index) in `app/src/main/java/com/techs/app/repository/InMemoryUserRepository.java`
- [x] T047 [P] Implement InMemoryRoleRepository (ConcurrentHashMap, name index) in `app/src/main/java/com/techs/app/repository/InMemoryRoleRepository.java`
- [x] T048 [P] Implement InMemoryRightRepository (ConcurrentHashMap, name index, featureId index) in `app/src/main/java/com/techs/app/repository/InMemoryRightRepository.java`
- [x] T049 [P] Implement InMemorySessionRepository (ConcurrentHashMap, userId â†?Set\<token\> index) in `app/src/main/java/com/techs/app/repository/InMemorySessionRepository.java`

### App Layer â€?Services & Infrastructure

- [x] T050 Implement BCryptPasswordEncoder (implements domain PasswordEncoder interface, wraps Spring Security BCryptPasswordEncoder) in `app/src/main/java/com/techs/app/service/BCryptPasswordEncoder.java`
- [x] T051 Implement SessionService (session validation, invalidation, 7-day expiry check, concurrent sessions support) in `app/src/main/java/com/techs/app/service/SessionService.java`
- [x] T052 Implement SessionAuthFilter (OncePerRequestFilter: extract Bearer token, validate session via SessionService, set SecurityContext) in `app/src/main/java/com/techs/app/security/SessionAuthFilter.java`
- [x] T053 Implement UrlAuthorizationFilter (match request URLs against user's base rights from AuthorizationService) in `app/src/main/java/com/techs/app/security/UrlAuthorizationFilter.java`
- [x] T054 Configure SecurityConfig (SecurityFilterChain: public endpoints for login/register, protected endpoints, filter registration, CORS, wire domain AuthService with injected dependencies) in `app/src/main/java/com/techs/app/config/SecurityConfig.java`
- [x] T055 Implement DataSeeder (CommandLineRunner: seed Admin/User/Visitor roles, built-in base and feature rights, admin account `admin`/`123`) in `app/src/main/java/com/techs/app/config/DataSeeder.java`
- [x] T056 Configure application.yml (server port, Spring Security settings) in `app/src/main/resources/application.yml`

### Web Layer â€?Shared Infrastructure

- [x] T057 Implement apiClient (fetch wrapper: get/post/put/delete, auth header injection, 401 handling with redirect to /login) in `web/src/shared/services/apiClient.ts`
- [x] T058 Create AuthContext (React context: user state, token management, effectiveRights cache, login/logout actions) in `web/src/features/auth/hooks/AuthContext.tsx`
- [x] T059 Implement useAuth hook (login, logout, isAuthenticated, currentUser, effectiveRights) in `web/src/features/auth/hooks/useAuth.ts`
- [x] T060 Implement usePermission hook (hasRight function reading effectiveRights from AuthContext) in `web/src/features/auth/hooks/usePermission.ts`
- [x] T061 Implement ProtectedRoute component (check auth â†?redirect to /login; check requiredRight â†?show Access Denied) in `web/src/features/auth/components/ProtectedRoute.tsx`
- [x] T062 Create auth TypeScript types (LoginRequest, LoginResponse with effectiveRights, RegisterRequest, RegisterResponse, User, Right) in `web/src/features/auth/types/auth.ts`

**Checkpoint**: Foundation ready â€?domain entities, repositories, PasswordEncoder interface, security, data seeding, and shared web infrastructure are complete. Domain AuthService is wired via constructor injection. User story implementation can now begin.

---

## Phase 3: User Story 1 â€?User Login (Priority: P1) MVP

**Goal**: A user navigates to the application, sees a login page, enters credentials, and upon successful authentication is redirected to the home page with a personalized greeting and their effective rights cached for permission checks.

**Independent Test**: Navigate to `/login`, enter `admin`/`123`, verify redirect to `/` with personalized greeting. Enter invalid credentials, verify generic error message.

### Tests for User Story 1

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

- [x] T063 [P] [US1] Domain unit test for AuthService.login (valid credentials, invalid credentials, blank credentials â€?mock UserRepository and PasswordEncoder) in `domain/src/test/java/com/techs/domain/userauth/service/AuthServiceTest.java`
- [x] T064 [P] [US1] API E2E test for POST /api/auth/login (success returns token + user with effectiveRights, invalid credentials returns 401, blank fields returns 400) in `app/src/test/java/com/techs/app/controller/AuthControllerTest.java`
- [x] T065 [P] [US1] Web component test for LoginForm (render form, submit valid form, submit invalid form, display error Alert) in `web/src/features/auth/__tests__/LoginForm.test.tsx`

### Implementation for User Story 1

- [x] T066 [US1] Implement login method in AuthService (validate credentials via UserRepository, compare password via PasswordEncoder.matches, create Session via SessionRepository, compute effectiveRights via AuthorizationService) in `domain/src/main/java/com/techs/domain/userauth/service/AuthService.java`
- [x] T067 [US1] Implement POST /api/auth/login endpoint in AuthController (delegate to AuthService, return LoginResponse with token + UserDto including effectiveRights) in `app/src/main/java/com/techs/app/controller/AuthController.java`
- [x] T068 [P] [US1] Implement authService with login API call (store token in localStorage, cache effectiveRights) in `web/src/features/auth/services/authService.ts`
- [x] T069 [P] [US1] Implement LoginForm component (MUI TextField for username/password, submit Button, error Alert) in `web/src/features/auth/components/LoginForm.tsx`
- [x] T070 [US1] Implement LoginPage (render LoginForm, redirect to `/` if already authenticated) in `web/src/pages/LoginPage.tsx`
- [x] T071 [US1] Create auth route definitions (lazy-loaded /login, /register routes) in `web/src/features/auth/routes.tsx`
- [x] T072 [US1] Verify login flow end-to-end: navigate to `/login` â†?enter `admin`/`123` â†?redirect to `/` with greeting; verify effectiveRights cached in AuthContext

**Checkpoint**: User Login is fully functional. Admin can log in and see the home page. Invalid credentials show a generic error. Effective rights are cached for permission checks.

---

## Phase 4: User Story 2 â€?View User Profile (Priority: P1)

**Goal**: An authenticated user can view their profile page displaying username, display name, role, and account creation date.

**Independent Test**: Log in as admin, navigate to `/profile`, verify all user fields are displayed. Log out, navigate to `/profile`, verify redirect to `/login`.

### Tests for User Story 2

- [x] T073 [P] [US2] Domain unit test for AuthService.getCurrentUser (resolve user from session, compute effectiveRights â€?mock SessionRepository and AuthorizationService) in `domain/src/test/java/com/techs/domain/userauth/service/AuthServiceTest.java`
- [x] T074 [P] [US2] API E2E test for GET /api/auth/me (success returns user with effectiveRights + individualRights, unauthenticated returns 401) in `app/src/test/java/com/techs/app/controller/UserControllerTest.java`
- [x] T075 [P] [US2] Web component test for ProfileCard (render user data, display all fields including roleName) in `web/src/features/profile/__tests__/ProfileCard.test.tsx`

### Implementation for User Story 2

- [x] T076 [US2] Implement getCurrentUser method in AuthService (resolve user from Session via SessionRepository, compute effectiveRights via AuthorizationService, return User with rights) in `domain/src/main/java/com/techs/domain/userauth/service/AuthService.java`
- [x] T077 [US2] Implement GET /api/auth/me endpoint in UserController (delegate to AuthService, return UserDto with effectiveRights + individualRights) in `app/src/main/java/com/techs/app/controller/UserController.java`
- [x] T078 [P] [US2] Create profile TypeScript types in `web/src/features/profile/types/profile.ts`
- [x] T079 [P] [US2] Implement profileService with getCurrentUser API call in `web/src/features/profile/services/profileService.ts`
- [x] T080 [P] [US2] Implement useProfile hook (fetch and cache current user data) in `web/src/features/profile/hooks/useProfile.ts`
- [x] T081 [P] [US2] Implement ProfileCard component (MUI Card displaying username, displayName, roleName, createdAt) in `web/src/features/profile/components/ProfileCard.tsx`
- [x] T082 [US2] Implement ProfilePage (render ProfileCard, require `read:/profile` right) in `web/src/pages/ProfilePage.tsx`
- [x] T083 [US2] Create profile route definitions (lazy-loaded /profile with ProtectedRoute) in `web/src/features/profile/routes.tsx`
- [x] T084 [US2] Verify profile flow: login â†?navigate to `/profile` â†?verify all fields displayed; logout â†?navigate to `/profile` â†?redirect to `/login`

**Checkpoint**: User Profile is fully functional. Authenticated users can view their profile. Unauthenticated users are redirected to login.

---

## Phase 5: User Story 5 â€?Self-Registration (Priority: P1)

**Goal**: A visitor can create an account via a registration page, is auto-assigned the Visitor role, and can log in with new credentials.

**Independent Test**: Navigate to `/register`, create a new account, verify redirect to `/login`, log in with new credentials, verify Visitor role is assigned.

### Tests for User Story 5

- [x] T085 [P] [US5] Domain unit test for AuthService.register (unique username, duplicate username, blank fields, Visitor role assignment â€?mock UserRepository and RoleRepository) in `domain/src/test/java/com/techs/domain/userauth/service/AuthServiceTest.java`
- [x] T086 [P] [US5] API E2E test for POST /api/auth/register (success returns 201 with Visitor role, duplicate username returns 409, blank fields returns 400) in `app/src/test/java/com/techs/app/controller/AuthControllerTest.java`
- [x] T087 [P] [US5] Web component test for RegisterForm (render form, submit valid form, duplicate username error display) in `web/src/features/auth/__tests__/RegisterForm.test.tsx`

### Implementation for User Story 5

- [x] T088 [US5] Implement register method in AuthService (validate uniqueness via UserValidator, look up Visitor role via RoleRepository, hash password via PasswordEncoder.encode, create User, save via UserRepository) in `domain/src/main/java/com/techs/domain/userauth/service/AuthService.java`
- [x] T089 [US5] Implement POST /api/auth/register endpoint in AuthController (delegate to AuthService, return RegisterResponse with UserDto) in `app/src/main/java/com/techs/app/controller/AuthController.java`
- [x] T090 [P] [US5] Implement authService.register API call in `web/src/features/auth/services/authService.ts`
- [x] T091 [P] [US5] Implement RegisterForm component (MUI TextField for username/password/displayName, submit Button, error Alert) in `web/src/features/auth/components/RegisterForm.tsx`
- [x] T092 [US5] Implement RegisterPage (render RegisterForm, redirect to `/` if authenticated) in `web/src/pages/RegisterPage.tsx`
- [x] T093 [US5] Add registration link to LoginPage (MUI Link to `/register`) in `web/src/pages/LoginPage.tsx`
- [x] T094 [US5] Verify registration flow: navigate to `/register` â†?create account â†?redirect to `/login` â†?login with new credentials â†?verify Visitor role on profile

**Checkpoint**: Self-Registration is fully functional. New users can register, are assigned Visitor role, and can log in.

---

## Phase 6: User Story 3 â€?Permission-Based Access Control (Priority: P2)

**Goal**: The system enforces permission-based access control using base rights (URL-level) and feature rights (in-page). Access is enforced on both frontend (route guards) and backend (endpoint protection).

**Independent Test**: Log in as Visitor (no `read:/permissions` right), navigate to `/permissions`, verify "Access Denied". Log in as Admin (has `read:/permissions`), verify access granted.

### Tests for User Story 3

- [x] T095 [P] [US3] Domain unit test for AuthorizationService (effective rights computation: role rights union individual rights, Admin always has all rights, hasRight checks) in `domain/src/test/java/com/techs/domain/userauth/service/AuthorizationServiceTest.java`
- [x] T096 [P] [US3] API E2E test for UrlAuthorizationFilter (access granted with right, access denied without right â€?full Spring context) in `app/src/test/java/com/techs/app/security/UrlAuthorizationFilterTest.java`
- [x] T097 [P] [US3] Web component test for ProtectedRoute (redirect unauthenticated, access denied without right, render with right) in `web/src/features/auth/__tests__/ProtectedRoute.test.tsx`

### Implementation for User Story 3

- [x] T098 [US3] Implement getEffectiveRights and hasRight methods in AuthorizationService (union of role.rights and user.individualRights, Admin role always returns all rights) in `domain/src/main/java/com/techs/domain/userauth/service/AuthorizationService.java`
- [x] T099 [US3] Refine UrlAuthorizationFilter (match request URL + HTTP method against user's base rights: READ for GET, WRITE for POST/PUT/DELETE) in `app/src/main/java/com/techs/app/security/UrlAuthorizationFilter.java`
- [x] T100 [P] [US3] Create permission TypeScript types (Right, Role, UserPermissions) in `web/src/features/permissions/types/permissions.ts`
- [x] T101 [P] [US3] Refine usePermission hook (read effectiveRights from AuthContext, provide hasRight function for both base and feature rights) in `web/src/features/auth/hooks/usePermission.ts`
- [x] T102 [US3] Refine ProtectedRoute component (check base right `read:/path` for route-level enforcement using usePermission.hasRight) in `web/src/features/auth/components/ProtectedRoute.tsx`
- [x] T103 [US3] Demonstrate feature-right enforcement pattern (usePermission.hasRight check to show/hide in-page elements) in `web/src/pages/PermissionsPage.tsx`
- [x] T104 [US3] Verify access control: Visitor cannot access `/permissions` (Access Denied); Admin can access `/permissions`; backend rejects requests without required right

**Checkpoint**: Permission-based access control is enforced on both frontend and backend. Users without required rights see "Access Denied".

---

## Phase 7: User Story 4 â€?Logout (Priority: P2)

**Goal**: An authenticated user can log out. Their session is invalidated (only the current session, not other concurrent sessions) and they are redirected to the login page.

**Independent Test**: Log in, click logout, verify redirect to `/login`. Attempt to access `/profile`, verify redirect to `/login`. Log in from another browser â€?that session remains valid.

### Tests for User Story 4

- [x] T105 [P] [US4] API E2E test for SessionService (logout deletes only current session, other sessions for same user remain valid, expired session treated as unauthenticated) in `app/src/test/java/com/techs/app/service/SessionServiceTest.java`
- [x] T106 [P] [US4] API E2E test for POST /api/auth/logout (success returns 204, unauthenticated returns 401) in `app/src/test/java/com/techs/app/controller/AuthControllerTest.java`
- [x] T107 [P] [US4] Web component test for LogoutButton (render button, click triggers logout API call, clears auth state and effectiveRights cache) in `web/src/features/auth/__tests__/LogoutButton.test.tsx`

### Implementation for User Story 4

- [x] T108 [US4] Implement logout method in SessionService (delete only the specified session token from SessionRepository, preserve other sessions for same user) in `app/src/main/java/com/techs/app/service/SessionService.java`
- [x] T109 [US4] Implement POST /api/auth/logout endpoint in AuthController (extract token from header, delegate to SessionService.logout) in `app/src/main/java/com/techs/app/controller/AuthController.java`
- [x] T110 [P] [US4] Implement authService.logout API call in `web/src/features/auth/services/authService.ts`
- [x] T111 [P] [US4] Implement LogoutButton component (MUI Button, calls logout API, clears auth state and effectiveRights cache) in `web/src/features/auth/components/LogoutButton.tsx`
- [x] T112 [US4] Integrate logout into App shell (LogoutButton in header/nav, clear token from localStorage, clear AuthContext, redirect to `/login`) in `web/src/app/App.tsx`
- [x] T113 [US4] Verify logout flow: login â†?click logout â†?redirect to `/login` â†?access `/profile` â†?redirect to `/login`

**Checkpoint**: Logout is fully functional. Only the current session is invalidated. Other concurrent sessions remain valid. Protected pages require re-authentication.

---

## Phase 8: User Story 6 â€?Permission Management Page (Priority: P2)

**Goal**: Admin users access a management page to configure roles, rights, user permissions, and feature management. Non-admin users see "Access Denied". The Admin role is fully immutable â€?all modification attempts are blocked.

**Independent Test**: Log in as admin, navigate to `/permissions`, create a role, assign rights, assign role to a user, verify user gains expected access. Log in as non-admin, verify "Access Denied". Attempt to modify Admin role â€?verify blocked.

### Tests for User Story 6

- [x] T114 [P] [US6] Domain unit test for AuthorizationService role/user management (create role, update rights, delete role with Visitor reassignment, Admin immutability enforcement, assign role, add/remove individual rights) in `domain/src/test/java/com/techs/domain/userauth/service/AuthorizationServiceTest.java`
- [x] T115 [P] [US6] API E2E test for PermissionController (CRUD roles, assign user role, add/remove individual rights, list users/rights/features, disable feature, delete right, Admin role immutability enforcement) in `app/src/test/java/com/techs/app/controller/PermissionControllerTest.java`
- [x] T116 [P] [US6] Web component test for RoleManager (list roles, create role, edit role rights, delete custom role, Admin role shown as read-only) in `web/src/features/permissions/__tests__/RoleManager.test.tsx`
- [x] T117 [P] [US6] Web component test for UserManager (list users, assign role, add/remove individual rights) in `web/src/features/permissions/__tests__/UserManager.test.tsx`

### Implementation for User Story 6 â€?Backend (Domain)

- [x] T118 [US6] Implement role CRUD methods in AuthorizationService (create custom role, update role rights, delete custom role with Visitor reassignment, prevent Admin role modification â€?return error for any Admin role mutation attempt) in `domain/src/main/java/com/techs/domain/userauth/service/AuthorizationService.java`
- [x] T119 [US6] Implement user permission management methods in AuthorizationService (assign role to user, add/remove individual rights, recompute effective rights) in `domain/src/main/java/com/techs/domain/userauth/service/AuthorizationService.java`
- [x] T120 [US6] Implement feature disable method in AuthorizationService (delete all rights by featureId, unassign from all roles and users) in `domain/src/main/java/com/techs/domain/userauth/service/AuthorizationService.java`

### Implementation for User Story 6 â€?Backend (App Controllers)

- [x] T121 [US6] Implement role CRUD endpoints (GET/POST/PUT/DELETE /api/permissions/roles) in PermissionController in `app/src/main/java/com/techs/app/controller/PermissionController.java`
- [x] T122 [US6] Implement rights listing and deletion endpoints (GET /api/permissions/rights, DELETE /api/permissions/rights/{rightId}) in PermissionController in `app/src/main/java/com/techs/app/controller/PermissionController.java`
- [x] T123 [US6] Implement user permission endpoints (GET /api/permissions/users, PUT /api/permissions/users/{userId}/role, POST/DELETE /api/permissions/users/{userId}/rights) in PermissionController in `app/src/main/java/com/techs/app/controller/PermissionController.java`
- [x] T124 [US6] Implement feature management endpoints (GET /api/permissions/features, POST /api/permissions/features/{featureId}/disable) in PermissionController in `app/src/main/java/com/techs/app/controller/PermissionController.java`

### Implementation for User Story 6 â€?Frontend

- [x] T125 [P] [US6] Implement permissionService (API calls for roles, rights, users, features CRUD) in `web/src/features/permissions/services/permissionService.ts`
- [x] T126 [P] [US6] Implement useRoles hook (fetch, create, update, delete roles) in `web/src/features/permissions/hooks/useRoles.ts`
- [x] T127 [P] [US6] Implement useRights hook (fetch rights catalog, delete feature-registered rights) in `web/src/features/permissions/hooks/useRights.ts`
- [x] T128 [P] [US6] Implement useUserPermissions hook (fetch users, assign roles, add/remove individual rights) in `web/src/features/permissions/hooks/useUserPermissions.ts`
- [x] T129 [P] [US6] Implement RoleManager component (MUI Table: list roles, create/edit/delete custom roles, assign rights via checkboxes, Admin role displayed as read-only with all rights â€?no edit/delete controls) in `web/src/features/permissions/components/RoleManager.tsx`
- [x] T130 [P] [US6] Implement RightManager component (MUI Table: list all rights with source identifiers, delete feature-registered rights, prevent built-in right deletion) in `web/src/features/permissions/components/RightManager.tsx`
- [x] T131 [P] [US6] Implement UserManager component (MUI Table: list users, assign role via dropdown, add/remove individual rights) in `web/src/features/permissions/components/UserManager.tsx`
- [x] T132 [P] [US6] Implement FeatureManager component (MUI Table: list features with registered permissions, disable feature button) in `web/src/features/permissions/components/FeatureManager.tsx`
- [x] T133 [US6] Implement PermissionsPage (MUI Tabs: Roles, Rights, Users, Features; require `read:/permissions` right) in `web/src/pages/PermissionsPage.tsx`
- [x] T134 [US6] Create permissions route definitions (lazy-loaded /permissions with ProtectedRoute) in `web/src/features/permissions/routes.tsx`
- [x] T135 [US6] Verify management flow: admin creates role â†?assigns rights â†?assigns role to user â†?user gains access; non-admin sees "Access Denied"; Admin role cannot be modified

**Checkpoint**: Permission management page is fully functional. Admins can configure the entire authorization system through the UI. Admin role is immutable.

---

## Phase 9: User Story 7 â€?Feature Permission Registration (Priority: P2)

**Goal**: Features can register their own permissions via an API (authenticated as admin). Registered permissions are immediately available for assignment and appear in the management page. Idempotent upsert on duplicate names.

**Independent Test**: Call POST /api/permissions/register with admin credentials to register a new permission. Verify it appears on the management page. Assign it to a role/user. Verify access control works for the new permission.

### Tests for User Story 7

- [x] T136 [P] [US7] API E2E test for PermissionRegistrationController (register new permissions, idempotent upsert on duplicate name, non-admin rejection returns 403) in `app/src/test/java/com/techs/app/controller/PermissionRegistrationControllerTest.java`
- [x] T137 [P] [US7] Domain unit test for AuthorizationService.registerPermissions (create right, upsert on duplicate name, auto-approve, FEATURE_REGISTERED source) in `domain/src/test/java/com/techs/domain/userauth/service/AuthorizationServiceTest.java`

### Implementation for User Story 7

- [x] T138 [US7] Implement registerPermissions method in AuthorizationService (create/update rights with FEATURE_REGISTERED source, idempotent upsert by name, auto-approve â€?immediately available for assignment) in `domain/src/main/java/com/techs/domain/userauth/service/AuthorizationService.java`
- [x] T139 [US7] Implement POST /api/permissions/register endpoint in PermissionRegistrationController (require manage_permissions right, delegate to AuthorizationService, return registered/updated counts) in `app/src/main/java/com/techs/app/controller/PermissionRegistrationController.java`
- [x] T140 [US7] Verify registration flow: register permission via API â†?appears in rights catalog on management page â†?assign to role/user â†?access control works

**Checkpoint**: Feature permission registration is fully functional. New features can self-register permissions without modifying auth module code.

---

## Phase 10: Polish & Cross-Cutting Concerns

**Purpose**: Documentation updates, code cleanup, and final validation across all user stories.

- [x] T141 [P] Update ARCHITECTURE.md in `docs-implementation/ARCHITECTURE.md` with auth components, security filters, permission model, and PasswordEncoder interface pattern
- [x] T142 [P] Update DATA-MODEL.md in `docs-canonical/DATA-MODEL.md` with User, Role, Right, Session entities
- [x] T143 [P] Update CHANGELOG.md in `CHANGELOG.md` with feature summary
- [x] T144 Code cleanup: remove unused imports, verify consistent code style across all modules
- [x] T145 Run full test suite across all three layers (`mvn -pl domain test` for domain unit, `mvn -pl app test` for API E2E, `npm test` for web E2E) and fix any failures
- [x] T146 Run quickstart.md validation (build all modules, start backend, start frontend, walk through all test scenarios)
- [x] T147 Verify all 10 success criteria (SC-001 through SC-010) are met, including SC-001 login latency measurement

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies â€?start immediately
- **Foundational (Phase 2)**: Depends on Phase 1 completion â€?BLOCKS all user stories
- **US1 Login (Phase 3)**: Depends on Phase 2 â€?foundational auth infrastructure
- **US2 Profile (Phase 4)**: Depends on Phase 3 (US1 Login) â€?requires authentication to be working
- **US5 Registration (Phase 5)**: Depends on Phase 2 â€?can start in parallel with US1/US2 after Phase 2
- **US3 Access Control (Phase 6)**: Depends on Phase 2 â€?can start after foundational authorization infrastructure
- **US4 Logout (Phase 7)**: Depends on Phase 3 (US1 Login) â€?requires session management from login
- **US6 Permission Mgmt (Phase 8)**: Depends on Phase 6 (US3 Access Control) â€?requires authorization infrastructure
- **US7 Registration API (Phase 9)**: Depends on Phase 8 (US6 Permission Mgmt) â€?extends permission management
- **Polish (Phase 10)**: Depends on all user stories being complete

### User Story Dependencies

```
Phase 1 (Setup) â†?Phase 2 (Foundational) â†?â”¬â”€ US1 (Login) â”€â”€â†?US2 (Profile)
                                            â”?              â””â”€â”€â†?US4 (Logout)
                                            â”œâ”€ US5 (Registration) [parallel with US1]
                                            â””â”€ US3 (Access Control) â”€â”€â†?US6 (Perm Mgmt) â”€â”€â†?US7 (Reg API)
```

- **US1, US5, US3** can start in parallel after Phase 2 (different controllers, different frontend features)
- **US2** depends on US1 (needs authentication working first)
- **US4** depends on US1 (needs session management from login)
- **US6** depends on US3 (needs authorization infrastructure)
- **US7** depends on US6 (extends permission management)

### Within Each User Story

- Tests MUST be written and FAIL before implementation (three layers: domain unit â†?API E2E â†?web E2E)
- Domain services before app controllers
- Backend before frontend integration
- Core implementation before end-to-end verification

### Parallel Opportunities

- Phase 1: T001-T004 (module restructuring), T009-T015 (shared web files) can all run in parallel
- Phase 2: T020-T022 (value objects), T023-T026 (entities), T027-T030 (repo interfaces), T035-T044 (DTOs), T046-T049 (repo impls) can run in parallel within their groups
- Phase 3+: Tests within each story can run in parallel across all three layers; frontend services/hooks/types can run in parallel

---

## Parallel Example: Phase 2 Foundational

```
# Launch all value objects together:
Task T020: RoleType in domain/.../valueobject/RoleType.java
Task T021: RightType in domain/.../valueobject/RightType.java
Task T022: RightAction in domain/.../valueobject/RightAction.java

# Launch all entities together (after value objects):
Task T023: Right entity in domain/.../entity/Right.java
Task T024: Role entity in domain/.../entity/Role.java
Task T026: Session entity in domain/.../entity/Session.java

# Launch all repository interfaces together:
Task T027: UserRepository in domain/.../repository/UserRepository.java
Task T028: RoleRepository in domain/.../repository/RoleRepository.java
Task T029: RightRepository in domain/.../repository/RightRepository.java
Task T030: SessionRepository in domain/.../repository/SessionRepository.java

# Launch all DTOs together:
Task T035-T044: All DTOs in api/.../userauth/model/

# Launch all in-memory repositories together:
Task T046: InMemoryUserRepository in app/.../repository/
Task T047: InMemoryRoleRepository in app/.../repository/
Task T048: InMemoryRightRepository in app/.../repository/
Task T049: InMemorySessionRepository in app/.../repository/
```

## Parallel Example: User Story 1 (Login) â€?Three-Layer Tests

```
# Launch all three test layers together (they target different files/modules):
Task T063: Domain unit test â€?AuthServiceTest in domain/src/test/
Task T064: API E2E test â€?AuthControllerTest in app/src/test/
Task T065: Web component test â€?LoginForm.test.tsx in web/src/features/auth/__tests__/

# Launch frontend service and component together:
Task T068: authService in web/src/features/auth/services/
Task T069: LoginForm in web/src/features/auth/components/
```

## Parallel Example: User Story 2 (Profile) â€?Three-Layer Tests

```
# Launch all three test layers together:
Task T073: Domain unit test â€?AuthServiceTest.getCurrentUser in domain/src/test/
Task T074: API E2E test â€?UserControllerTest in app/src/test/
Task T075: Web component test â€?ProfileCard.test.tsx in web/src/features/profile/__tests__/
```

---

## Implementation Strategy

### MVP First (US0 + US1 Only)

1. Complete Phase 1: Setup (module restructuring + router + MUI)
2. Complete Phase 2: Foundational (entities, repos, PasswordEncoder, security, seeding)
3. Complete Phase 3: User Story 1 (Login)
4. **STOP and VALIDATE**: Login as admin (`admin`/`123`), see home page with greeting
5. Deploy/demo if ready â€?proves the full web â†?api â†?domain dependency chain

### Incremental Delivery

1. Complete Setup + Foundational â†?Foundation ready
2. Add US1 (Login) â†?Test independently â†?**MVP!**
3. Add US2 (Profile) + US5 (Registration) â†?Test independently â†?Multiple user types work
4. Add US3 (Access Control) + US4 (Logout) â†?Test independently â†?Security layer complete
5. Add US6 (Permission Mgmt) â†?Test independently â†?Admin can configure permissions
6. Add US7 (Registration API) â†?Test independently â†?System is extensible
7. Polish â†?Documentation and final validation

### Parallel Team Strategy

With multiple developers (after Phase 2):

- Developer A: US1 (Login) â†?US2 (Profile) â†?US4 (Logout)
- Developer B: US5 (Registration) â†?US3 (Access Control)
- Developer C: US6 (Permission Mgmt) â†?US7 (Registration API)

Stories complete and integrate independently.

---

## Notes

- [P] tasks = different files, no dependencies on incomplete tasks
- [Story] label maps task to specific user story for traceability
- Each user story is independently completable and testable
- Tests MUST fail before implementing (Red-Green-Refactor) across all three layers
- Three-layer testing: domain unit (JUnit 5, no Spring) â†?API E2E (Spring Boot Test) â†?web E2E (Vitest + @testing-library/react)
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- All domain entities use Lombok `@Data`, `@Builder`, `@AllArgsConstructor` per project conventions
- All DTOs use Lombok `@Data` per project conventions
- In-memory repositories use `ConcurrentHashMap` for thread safety
- Session tokens are UUIDs sent as `Authorization: Bearer <token>` headers
- Password hashing uses BCrypt via domain PasswordEncoder interface, implemented by app BCryptPasswordEncoder
- Domain AuthService is the full orchestrator â€?receives all dependencies via constructor injection
- Multiple concurrent sessions per user are allowed; logout invalidates only the current session
- Admin role is fully immutable â€?management page blocks all modification attempts
