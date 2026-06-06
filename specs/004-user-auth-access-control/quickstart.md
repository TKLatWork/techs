# Quickstart: User Authentication & Access Control

**Feature**: 004-user-auth-access-control
**Date**: 2026-06-07

## Prerequisites

- JDK 21 installed and on PATH
- Node.js 18+ installed and on PATH
- Maven 3.9+ installed and on PATH

## Build & Run

### 1. Build Java modules (domain → api → app)

```bash
# From repository root
mvn clean install
```

This compiles all Java modules, runs tests, and generates TypeScript types from the api module's DTOs into `api/target/generated-sources/typescript/api-models.ts`.

### 2. Install web dependencies

```bash
# From repository root
npm install
```

This installs all npm dependencies for the web module, including `react-router-dom`, `@mui/material`, and `@emotion/react`.

### 3. Start the backend

```bash
# From repository root
mvn -pl app spring-boot:run
```

The Spring Boot application starts on `http://localhost:8080`. The `DataSeeder` runs on startup and seeds:
- Built-in admin account: `admin` / `123`
- Built-in roles: Admin, User, Visitor
- Built-in rights: read:/profile, read:/admin, write:/admin, read:/permissions, manage_users, manage_permissions

### 4. Start the frontend

```bash
# From repository root
npm run dev
```

The Vite dev server starts on `http://localhost:5173`.

## Test the Feature

### Login as Admin

1. Navigate to `http://localhost:5173`
2. You are redirected to `/login` (unauthenticated)
3. Enter username: `admin`, password: `123`
4. You are redirected to `/` (home page) with a personalized greeting

### View Profile

1. Navigate to `/profile`
2. Your profile displays: username, display name, role (Admin), creation date

### Access Permission Management

1. Navigate to `/permissions`
2. The permission management page loads (Admin has `read:/permissions` right)
3. You can view roles, rights, and users

### Register a New User

1. Logout (click logout button)
2. Click "Register" link on the login page
3. Enter a new username, password, and display name
4. You are redirected to `/login`
5. Login with your new credentials
6. You are assigned the Visitor role — you can only view your profile (read-only)

### Test Access Control

1. Login as the new Visitor user
2. Navigate to `/permissions` — you see "Access Denied"
3. Login as admin
4. Go to `/permissions` → promote the Visitor to User role
5. Login as the promoted user
6. Navigate to `/permissions` — still "Access Denied" (User role doesn't have `read:/permissions`)

## Run Tests

Testing follows the three-layer testing pyramid (constitution Principle I, v2.1.0):

### Layer 1: Domain Unit Tests (no Spring context)

```bash
mvn -pl domain test
```

Pure business logic tests — entities, value objects, domain services.
No Spring context, no I/O, no network. Fast execution.

### Layer 2: API E2E / Integration Tests (full Spring context)

```bash
mvn -pl app test
```

HTTP endpoint tests against running Spring Boot context with in-memory
repositories. Validates request/response contracts, auth flows, and
authorization enforcement.

### Layer 3: Web E2E / Component Tests

```bash
# From repository root
npm test

# Watch mode
npm run test:watch
```

User-facing interaction tests via Vitest + @testing-library/react.
Validates user journeys, route guards, form validation, and error display.

### All modules

```bash
mvn test    # Java modules (domain + api + app)
npm test    # Web module
```

## Module Entry Points

| Module | Entry Point | Test Location |
|--------|-------------|---------------|
| domain | `domain/src/main/java/com/techs/domain/` | `domain/src/test/java/` |
| api | `api/src/main/java/com/techs/api/model/` | `api/src/test/java/` |
| app | `app/src/main/java/com/techs/app/Application.java` | `app/src/test/java/` |
| web | `web/src/main.tsx` | `web/src/**/__tests__/` |

## Key Files

| File | Purpose |
|------|---------|
| `web/src/app/App.tsx` | Root component with BrowserRouter, MUI ThemeProvider, route rendering |
| `web/src/app/featureRegistry.ts` | Auto-discovers feature routes via import.meta.glob |
| `web/src/features/auth/components/ProtectedRoute.tsx` | Route guard checking auth + rights |
| `web/src/features/auth/hooks/useAuth.ts` | Auth state management (login, logout, current user) |
| `web/src/features/auth/hooks/usePermission.ts` | Permission checking (hasRight) |
| `web/src/shared/services/apiClient.ts` | HTTP client with auth header injection |
| `app/src/main/java/.../config/DataSeeder.java` | Seeds built-in admin, roles, rights on startup |
| `app/src/main/java/.../security/SessionAuthFilter.java` | Session token validation filter |
| `app/src/main/java/.../service/BCryptPasswordEncoder.java` | Implements domain PasswordEncoder interface |
| `domain/src/main/java/.../userauth/service/AuthService.java` | Authentication orchestrator (via injected interfaces) |
| `domain/src/main/java/.../userauth/service/AuthorizationService.java` | Effective rights computation |
| `domain/src/main/java/.../userauth/service/PasswordEncoder.java` | Password hashing interface (impl in app) |
