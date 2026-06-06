# Implementation Plan: User Authentication & Access Control

**Branch**: `004-user-auth-access-control` | **Date**: 2026-06-07 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/004-user-auth-access-control/spec.md`

## Summary

Implement a complete user authentication and permission-based access control system across all four modules (web, api, app, domain). The feature includes: web module restructuring to a feature-based architecture with react-router-dom and MUI (P0 prerequisite), username/password login with session tokens, self-registration with Visitor role, two-tier permission model (base URL rights + feature rights), role-based access control with single role per user plus individual rights, a permission management page for admins, and a permission registration API for features to self-register their permissions. This feature proves the modular architecture by exercising the full web → api → domain dependency chain.

## Technical Context

**Language/Version**: Java 21 (JDK 21) for domain/api/app; TypeScript 5.x for web

**Primary Dependencies**: Spring Boot 3.3.3 (app), React 18.x + Vite 5.x + react-router-dom v6/v7 + MUI v5 (web), Lombok 1.18.38 (domain, api), typescript-generator 4.1.1 (api), Jackson 2.17.2 (api, app), Spring Security 6.x (app)

**Storage**: In-memory (ConcurrentHashMap-based repositories). No database — canonical ARCHITECTURE.md marks database as TBD. Data resets on application restart. Built-in admin and roles/rights are seeded at startup.

**Testing**: JUnit 5 (domain unit tests), JUnit 5 + Spring Boot Test (API E2E tests), Vitest + @testing-library/react (web E2E/component tests). Three-layer testing pyramid per constitution Principle I (v2.1.0).

**Target Platform**: Cross-platform development (Windows primary, Linux/macOS compatible); desktop browser for web

**Project Type**: Web application (monorepo with frontend + backend + shared libraries)

**Performance Goals**: Login flow completes in under 10 seconds (SC-001); no specific throughput targets for initial implementation

**Constraints**: Session tokens expire after 7 days; password must be non-blank (no complexity rules); single role per user; Admin role is immutable; multiple concurrent sessions per user allowed

**Scale/Scope**: Initial implementation — small user base, in-memory storage, no horizontal scaling required. 8 user stories, 36 functional requirements across 4 modules.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### Pre-Design Check

| Gate | Status | Notes |
|------|--------|-------|
| TDD (I) | PASS | Three-layer testing specified: JUnit 5 (domain unit), Spring Boot Test (API E2E), Vitest + @testing-library/react (web E2E). Tests precede implementation per Red-Green-Refactor. |
| CDD Compliance (II) | DRIFT | New domain entities (User, Role, Right, Session) and API DTOs deviate from canonical DATA-MODEL.md which only defines Project/Module. Drift logged in DRIFT-LOG.md |
| Technology Justified | PASS | All technologies documented in research.md with rationale and alternatives |
| Specification Gate | PASS | Spec exists with 8 user stories, 36 functional requirements, and 10 measurable success criteria |
| Feature-Based Structure (III) | PASS | Domain, api, and web use feature-based packages. App uses flat structure per constitution. Shared packages defined in all modules. |
| DDD Code Rules (IV) | PASS | Entities/value objects classified correctly. Repositories as interfaces in domain, implementations in app. Domain services stateless. |

### Post-Design Re-Check

| Gate | Status | Notes |
|------|--------|-------|
| TDD (I) | PASS | Test locations and frameworks defined per module for all three layers. Domain tests have no Spring context. API tests use full Spring context. Web tests use component + E2E approach. |
| CDD Compliance (II) | DRIFT | DATA-MODEL.md needs updating with User, Role, Right, Session entities. ARCHITECTURE.md needs updating with auth-related components. Drift logged |
| Plan Gate | PASS | Technical plan exists with constitution compliance check |
| Feature-Based Structure (III) | PASS | Cross-feature refs use public contracts (aggregate roots, domain service interfaces, value objects). App module uses flat structure (`controller/`, `service/`, `repository/`, `config/`, `security/`). |
| DDD Code Rules (IV) | PASS | Domain services stateless. No infrastructure in domain module. PasswordEncoder defined as interface in domain, implemented in app. Domain AuthService orchestrates via injected interfaces. |

### Drift Summary

The canonical DATA-MODEL.md defines only Project and Module entities. This feature introduces User, Role, Right, and Session entities. The canonical ARCHITECTURE.md does not describe authentication or authorization components. Both drifts are logged in DRIFT-LOG.md. The canonical docs should be updated after this feature is implemented to reflect the new domain model and architecture components.

## Project Structure

### Documentation (this feature)

```text
specs/004-user-auth-access-control/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output
│   ├── auth-api.md      # Authentication API contracts
│   ├── permission-api.md # Permission management API contracts
│   └── web-routes.md    # Frontend route contracts
└── tasks.md             # Phase 2 output (/speckit.tasks command)
```

### Source Code (repository root)

```text
techs/
├── domain/                              # Java shared library
│   ├── src/
│   │   ├── main/java/com/techs/domain/
│   │   │   ├── userauth/                # Feature: user-auth
│   │   │   │   ├── entity/
│   │   │   │   │   ├── User.java        # Aggregate root: user account
│   │   │   │   │   ├── Role.java        # Entity: named group of rights
│   │   │   │   │   ├── Right.java       # Entity: permission (base or feature)
│   │   │   │   │   └── Session.java     # Entity: active login state
│   │   │   │   ├── valueobject/
│   │   │   │   │   ├── RoleType.java    # Value object: built-in role enum
│   │   │   │   │   ├── RightType.java   # Value object: BASE or FEATURE
│   │   │   │   │   └── RightAction.java # Value object: READ or WRITE
│   │   │   │   ├── service/
│   │   │   │   │   ├── AuthService.java       # Domain service: authentication orchestrator
│   │   │   │   │   ├── AuthorizationService.java # Domain service: authorization rules
│   │   │   │   │   ├── UserValidator.java     # Domain service: validation rules
│   │   │   │   │   └── PasswordEncoder.java   # Interface: password hashing (impl in app)
│   │   │   │   └── repository/
│   │   │   │       ├── UserRepository.java     # Repository interface (aggregate root)
│   │   │   │       ├── RoleRepository.java     # Repository interface
│   │   │   │       ├── RightRepository.java    # Repository interface
│   │   │   │       └── SessionRepository.java  # Repository interface
│   │   │   └── shared/                  # Cross-feature shared code
│   │   └── test/java/com/techs/domain/
│   │       ├── userauth/                # Domain unit tests (no Spring context)
│   │       └── shared/                  # Tests for shared code
│   └── pom.xml
│
├── api/                                 # API contracts (Java → TypeScript)
│   ├── src/
│   │   ├── main/java/com/techs/api/
│   │   │   ├── userauth/                # Feature: user-auth DTOs
│   │   │   │   └── model/
│   │   │   │       ├── LoginRequest.java
│   │   │   │       ├── LoginResponse.java
│   │   │   │       ├── RegisterRequest.java
│   │   │   │       ├── RegisterResponse.java
│   │   │   │       ├── UserDto.java
│   │   │   │       ├── RoleDto.java
│   │   │   │       ├── RightDto.java
│   │   │   │       ├── PermissionRegistrationRequest.java
│   │   │   │       ├── UserPermissionsDto.java
│   │   │   │       └── ErrorResponse.java
│   │   │   └── shared/                  # Cross-feature shared DTOs
│   │   │       └── model/
│   │   └── test/java/com/techs/api/
│   │       └── userauth/model/          # Tests for userauth DTOs
│   ├── target/
│   │   └── generated-sources/
│   │       └── typescript/
│   │           └── api-models.ts        # Auto-generated TypeScript types
│   └── pom.xml
│
├── app/                                 # Spring Boot backend (FLAT structure)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/techs/app/
│   │   │   │   ├── Application.java     # Spring Boot entry point (existing)
│   │   │   │   ├── controller/          # REST endpoints (flat, cross-feature)
│   │   │   │   │   ├── AuthController.java
│   │   │   │   │   ├── UserController.java
│   │   │   │   │   ├── PermissionController.java
│   │   │   │   │   └── PermissionRegistrationController.java
│   │   │   │   ├── service/             # Application services (flat, cross-feature)
│   │   │   │   │   ├── SessionService.java
│   │   │   │   │   └── BCryptPasswordEncoder.java  # Implements domain PasswordEncoder
│   │   │   │   ├── repository/          # Repository implementations (flat, cross-feature)
│   │   │   │   │   ├── InMemoryUserRepository.java
│   │   │   │   │   ├── InMemoryRoleRepository.java
│   │   │   │   │   ├── InMemoryRightRepository.java
│   │   │   │   │   └── InMemorySessionRepository.java
│   │   │   │   ├── config/              # Spring configuration
│   │   │   │   │   ├── SecurityConfig.java
│   │   │   │   │   └── DataSeeder.java
│   │   │   │   └── security/            # Security filters
│   │   │   │       ├── SessionAuthFilter.java
│   │   │   │       └── UrlAuthorizationFilter.java
│   │   │   └── resources/
│   │   │       └── application.yml
│   │   └── test/java/com/techs/app/
│   │       ├── controller/              # API E2E tests (full Spring context)
│   │       ├── service/                 # Service tests
│   │       ├── repository/              # Repository tests
│   │       └── security/                # Security filter tests
│   └── pom.xml
│
├── web/                                 # React + Vite frontend (restructured)
│   ├── src/
│   │   ├── main.tsx                     # Entry point (updated imports)
│   │   ├── app/                         # App shell
│   │   │   ├── App.tsx                  # Root: BrowserRouter + MUI ThemeProvider + Suspense
│   │   │   ├── featureRegistry.ts       # Auto-discovers features/*/routes.tsx
│   │   │   └── theme.ts                 # MUI theme configuration
│   │   ├── features/                    # Feature modules
│   │   │   ├── auth/                    # Authentication feature
│   │   │   │   ├── components/          # LoginForm, RegisterForm, ProtectedRoute, LogoutButton
│   │   │   │   ├── hooks/               # useAuth, usePermission, AuthContext
│   │   │   │   ├── services/            # authService (API calls)
│   │   │   │   ├── types/               # Auth-specific TypeScript types
│   │   │   │   ├── routes.tsx           # Auth route definitions
│   │   │   │   └── __tests__/           # Web E2E/component tests
│   │   │   ├── profile/                 # User profile feature
│   │   │   │   ├── components/          # ProfileCard
│   │   │   │   ├── hooks/               # useProfile
│   │   │   │   ├── services/            # profileService
│   │   │   │   ├── types/               # Profile types
│   │   │   │   ├── routes.tsx           # Profile route definitions
│   │   │   │   └── __tests__/
│   │   │   └── permissions/             # Permission management feature
│   │   │       ├── components/          # RoleManager, RightManager, UserManager, FeatureManager
│   │   │       ├── hooks/               # useRoles, useRights, useUserPermissions
│   │   │       ├── services/            # permissionService
│   │   │       ├── types/               # Permission types
│   │   │       ├── routes.tsx           # Permission route definitions
│   │   │       └── __tests__/
│   │   ├── shared/                      # Cross-cutting shared code
│   │   │   ├── components/              # Reusable UI (StatusBadge migrated here)
│   │   │   ├── hooks/                   # Shared hooks (useApi)
│   │   │   ├── services/                # apiClient (fetch wrapper)
│   │   │   ├── utils/                   # Pure utilities (format.ts)
│   │   │   ├── types/                   # Shared TypeScript types
│   │   │   └── constants/               # App-wide constants
│   │   ├── pages/                       # Route-level page components
│   │   │   ├── HomePage.tsx             # Renders at /
│   │   │   ├── LoginPage.tsx            # Renders at /login
│   │   │   ├── RegisterPage.tsx         # Renders at /register
│   │   │   ├── ProfilePage.tsx          # Renders at /profile
│   │   │   ├── PermissionsPage.tsx      # Renders at /permissions
│   │   │   └── NotFoundPage.tsx         # Renders at *
│   │   ├── styles/                      # Global styles
│   │   │   └── global.css
│   │   └── __tests__/
│   │       └── setup.ts                 # Test setup (jest-dom)
│   ├── public/
│   ├── index.html
│   ├── package.json                     # Updated: +react-router-dom, +@mui/material, +@emotion/*
│   ├── tsconfig.json
│   └── vite.config.ts
│
├── docs-canonical/                      # CDD canonical documents (READ-ONLY)
├── docs-implementation/                 # CDD implementation docs
├── specs/                               # Feature specifications
├── pom.xml                              # Maven parent/aggregator
├── package.json                         # Root npm config
└── DRIFT-LOG.md                         # Canonical deviation tracking
```

**Structure Decision**: 4-module monorepo with Maven aggregator for Java modules (domain, api, app) and npm for the web module. Domain, api, and web modules follow feature-based structures with `userauth` as the first feature. The app module uses a **flat structure** organized by technical concern (`controller/`, `service/`, `repository/`, `config/`, `security/`) per constitution Principle III — it is the orchestration layer and cross-feature imports are permitted. Domain feature packages use DDD-style sub-packages (`entity/`, `valueobject/`, `service/`, `repository/`) — repository interfaces are defined in `domain/<feature>/repository/` and implementations (`InMemory*`) live in `app/repository/`. The domain module defines a `PasswordEncoder` interface that the app module implements (`BCryptPasswordEncoder`), enabling domain services to orchestrate authentication without infrastructure leakage. The web module uses `app/` (shell), `features/` (self-contained feature modules), `shared/` (cross-cutting code), and `pages/` (route-level wrappers). Features auto-discover routes via `import.meta.glob`. Placeholder classes (`ProjectInfo`, `UserDto`, `ProjectService`) are deleted during restructuring. Aligned with constitution v2.1.0.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| Two-tier permission model (base + feature rights) | URL-based enforcement requires base rights; feature rights enable fine-grained in-page control | Single-tier model forces either too coarse (URL-only) or too fine (feature-only) access control |
| Feature permission registration API | Enables new features to self-register permissions without modifying auth module code | Hardcoded permissions require auth module changes for every new feature, violating modularity |
| In-memory storage with data seeding | No database specified in canonical architecture (TBD); proves the architecture without infrastructure | File-based storage adds unnecessary I/O complexity for a proof-of-concept |
| Spring Security integration | Provides session filter chain, URL authorization, and CSRF protection out of the box | Custom security implementation would duplicate Spring Security's battle-tested functionality |
| Dynamic route auto-discovery via import.meta.glob | Zero-config feature addition; new features only need a routes.tsx file | Manual route registration requires app shell changes for every new feature |
| PasswordEncoder interface in domain module | Enables domain AuthService to orchestrate authentication (password comparison) without infrastructure leakage | Domain receiving only password hashes would require app-layer orchestration, splitting business logic across modules |
