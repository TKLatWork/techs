# Research: User Authentication & Access Control

**Feature**: 004-user-auth-access-control
**Date**: 2026-06-07

## R-001: Storage Strategy (No Database)

**Decision**: In-memory storage using `ConcurrentHashMap` in Spring `@Repository` beans.

**Rationale**: The canonical ARCHITECTURE.md marks database as TBD. This feature proves the modular architecture without introducing infrastructure dependencies. In-memory storage is sufficient for a proof-of-concept and keeps each module independently testable (SC-006). Data is seeded at application startup via a `DataSeeder` component.

**Alternatives considered**:
- **H2 in-memory database**: Adds JDBC dependency and SQL complexity for a proof-of-concept. Rejected — overkill when no persistence is needed.
- **File-based storage (JSON files)**: Adds I/O complexity and file locking concerns. Rejected — unnecessary for in-process testing.
- **PostgreSQL/MySQL**: Full database infrastructure. Rejected — canonical says TBD, and this feature doesn't require persistence.

**Implications**: All data resets on application restart. Built-in admin, roles, and rights are re-seeded on every startup. This is acceptable for the proof-of-concept phase.

## R-002: Password Hashing

**Decision**: BCrypt via Spring Security's `BCryptPasswordEncoder`.

**Rationale**: BCrypt is the industry standard for password hashing in Java/Spring applications. It includes built-in salting, is resistant to rainbow table attacks, and has a configurable work factor. Spring Security provides `BCryptPasswordEncoder` out of the box.

**Alternatives considered**:
- **Argon2**: More modern, winner of the Password Hashing Competition. Rejected — not natively supported by Spring Security; would require additional dependency (`bouncy-castle` or `argon2-jvm`).
- **PBKDF2**: NIST recommended. Rejected — slower than BCrypt in Java without native acceleration, and BCrypt is more widely used in the Spring ecosystem.
- **SHA-256 with salt**: Simple but not adaptive. Rejected — not resistant to GPU-based attacks.

**Implications**: The domain module defines a `PasswordEncoder` interface (`com.techs.domain.userauth.service.PasswordEncoder`) with `encode(rawPassword)` and `matches(rawPassword, encodedPassword)` methods. The app module implements it via `BCryptPasswordEncoder` (`com.techs.app.service.BCryptPasswordEncoder`). The domain `AuthService` receives the `PasswordEncoder` via constructor injection and uses it for password comparison during authentication. This preserves DDD purity — domain owns all business rules through abstractions, app only provides infrastructure implementations.

## R-003: Session Token Strategy

**Decision**: UUID-based session tokens stored in an in-memory `SessionRepository`. Tokens are returned to the client upon login and sent as `Authorization: Bearer <token>` headers on subsequent requests.

**Rationale**: UUID tokens are simple, secure (128-bit randomness), and sufficient for in-memory storage. The `SessionRepository` maps tokens to user IDs and expiration times. Spring Security's filter chain validates tokens on each request.

**Alternatives considered**:
- **JWT (JSON Web Tokens)**: Self-contained, no server-side storage needed. Rejected — JWTs cannot be easily invalidated (logout requires a blacklist), and the spec requires session invalidation on logout (FR-009). In-memory storage is simpler for this phase.
- **Spring Session with Redis**: Distributed session storage. Rejected — requires Redis infrastructure, which is out of scope.
- **Cookie-based sessions**: Server sets a session cookie. Rejected — the spec assumes token-based auth stored in browser storage; cookie-based auth adds CSRF complexity.

**Implications**: Sessions expire after 7 days (FR-019). Logout invalidates the token server-side (FR-009). The web module stores the token in `localStorage` and includes it in API request headers via an `apiClient` interceptor.

## R-004: Spring Security Configuration

**Decision**: Spring Security 6.x with a custom `SessionAuthFilter` (extends `OncePerRequestFilter`) for token validation and URL-based authorization via `SecurityFilterChain` configuration.

**Rationale**: Spring Security provides the filter chain infrastructure needed for authentication and authorization. A custom filter extracts the session token from the `Authorization` header, validates it against the `SessionRepository`, and sets the `SecurityContext`. URL authorization rules are configured in `SecurityConfig` to map URL patterns to required rights.

**Alternatives considered**:
- **Spring Security with OAuth2/JWT**: Full OAuth2 resource server. Rejected — overkill for session-based auth; adds unnecessary complexity.
- **Custom servlet filter without Spring Security**: Manual filter chain. Rejected — loses Spring Security's built-in CSRF protection, exception handling, and method-level security.
- **Spring Security with form login**: Traditional form-based authentication. Rejected — the frontend is a React SPA that uses token-based auth, not form submissions.

**Implications**: The `SecurityConfig` defines public endpoints (login, register) and protected endpoints. The `SessionAuthFilter` runs before Spring Security's `AuthorizationFilter`. URL authorization rules reference the two-tier right model (base rights for URL access, feature rights checked within controllers).

## R-005: Frontend Route Guards

**Decision**: A `ProtectedRoute` component in `features/auth/components/` that wraps `react-router-dom`'s `<Route>` and checks the user's authentication state and effective rights before rendering the child component.

**Rationale**: React Router v6 supports wrapper components for route protection. The `ProtectedRoute` component uses the `useAuth` hook to check authentication and the `usePermission` hook to check rights. If the user lacks access, it renders an "Access Denied" page or redirects to login.

**Alternatives considered**:
- **React Router `loader` functions**: Data-loading functions that can redirect. Rejected — loaders run before rendering but don't have easy access to React context (auth state). Wrapper components are more idiomatic for auth checks.
- **Higher-Order Component (HOC) pattern**: `withAuth(Component)`. Rejected — HOCs are less composable than wrapper components in React Router v6's declarative routing model.
- **Middleware pattern (TanStack Router style)**: Route-level middleware. Rejected — react-router-dom v6 doesn't have built-in middleware; wrapper components are the standard approach.

**Implications**: Each feature's `routes.tsx` wraps protected routes with `<ProtectedRoute requiredRight="read:/path">`. The `ProtectedRoute` component checks both authentication (redirect to `/login` if unauthenticated) and authorization (show "Access Denied" if lacking the required right).

## R-006: MUI Integration

**Decision**: MUI v5 (`@mui/material`) with `@emotion/react` and `@emotion/styled` as the styling engine. A custom theme is defined in `app/theme.ts` and provided via `<ThemeProvider>` in the app shell.

**Rationale**: MUI v5 is the latest stable version and uses Emotion as its default styling engine. It provides a comprehensive set of accessible, customizable components (TextField, Button, Card, Table, Dialog, etc.) that cover all UI needs for this feature. The theme provider enables consistent styling across all features.

**Alternatives considered**:
- **MUI v4**: Older version with JSS styling. Rejected — v5 is the current standard with better performance and Emotion integration.
- **Ant Design**: Alternative component library. Rejected — user explicitly specified MUI.
- **Chakra UI**: Lighter alternative. Rejected — user explicitly specified MUI.
- **Tailwind CSS**: Utility-first CSS. Rejected — user explicitly specified MUI; Tailwind would be a complementary tool, not a replacement.

**Implications**: All UI components use MUI components (`<TextField>`, `<Button>`, `<Card>`, `<Table>`, `<Alert>`, etc.). The `ThemeProvider` in `App.tsx` wraps the entire application. Features import MUI components directly — no custom wrapper layer needed for this phase.

## R-007: API Client for Web Module

**Decision**: A lightweight `apiClient` in `shared/services/` using the native `fetch` API with a request interceptor that attaches the session token from `localStorage`.

**Rationale**: The native `fetch` API is built into all modern browsers and requires no additional dependencies. A thin wrapper adds the `Authorization` header and handles JSON parsing and error responses. This keeps the web module's dependency footprint small.

**Alternatives considered**:
- **Axios**: Popular HTTP client with built-in interceptors. Rejected — adds a dependency when `fetch` is sufficient. Axios interceptors are more ergonomic but not worth the dependency for this scope.
- **React Query / TanStack Query**: Data fetching and caching library. Rejected — adds complexity for a proof-of-concept. Can be introduced later when data fetching patterns become more complex.
- **SWR**: Lightweight data fetching. Rejected — same reasoning as React Query.

**Implications**: The `apiClient` exposes `get()`, `post()`, `put()`, `delete()` methods. It reads the session token from `localStorage` and attaches it as `Authorization: Bearer <token>`. On 401 responses, it clears the token and redirects to `/login`. Feature services (`authService`, `profileService`, `permissionService`) use `apiClient` for all HTTP calls.

## R-008: Feature Route Auto-Discovery

**Decision**: Vite's `import.meta.glob` to auto-discover `features/*/routes.tsx` files at build time. Each feature exports a `routes` array of route objects. The `featureRegistry.ts` in `app/` collects all routes and the `App.tsx` renders them inside `<Routes>`.

**Rationale**: `import.meta.glob` is a Vite-specific feature that resolves glob patterns at build time, producing static imports. This enables true zero-config feature addition — creating a new `features/<name>/routes.tsx` file is sufficient for the routes to be registered. Combined with `React.lazy()` for code splitting, this provides both developer convenience and performance.

**Alternatives considered**:
- **Manual route aggregation**: App shell imports each feature's routes explicitly. Rejected — requires modifying the app shell for every new feature, violating the zero-config goal.
- **React Router `createBrowserRouter` with dynamic imports**: Programmatic route creation. Rejected — more complex setup; the declarative `<Routes>` approach with auto-discovery is simpler.
- **File-based routing (Next.js style)**: Routes derived from file system structure. Rejected — react-router-dom doesn't support this natively; would require additional tooling.

**Implications**: The `featureRegistry.ts` uses `import.meta.glob("../features/*/routes.tsx", { eager: true })` to collect all feature route modules. Each feature's `routes.tsx` uses `React.lazy()` for page components to enable code splitting. The `App.tsx` wraps all routes in `<Suspense>` for loading states.

## R-009: Two-Tier Permission Model Implementation

**Decision**: Base rights are stored as strings in the format `{action}:{urlPattern}` (e.g., `read:/admin`, `write:/profile`). Feature rights are stored as named identifiers (e.g., `manage_users`, `export_data`). Both are stored in the `Right` entity with a `type` field distinguishing them.

**Rationale**: The two-tier model separates URL-level access control (checked by Spring Security filters and React route guards) from feature-level access control (checked within page/endpoint logic). Base rights use a simple `action:urlPattern` format that can be matched against request URLs. Feature rights are opaque identifiers checked by application logic.

**Alternatives considered**:
- **Single-tier with URL patterns only**: All rights are URL-based. Rejected — cannot express fine-grained in-page permissions (e.g., "can view admin page but cannot delete users").
- **Single-tier with named permissions only**: All rights are named identifiers. Rejected — requires a separate mapping from permissions to URLs, adding indirection.
- **RBAC with resource-action pairs**: Rights are `{resource}:{action}` tuples. Rejected — more complex than needed; the URL-based model is simpler for web applications.

**Implications**: The backend's `UrlAuthorizationFilter` matches request URLs against base rights. The frontend's `ProtectedRoute` checks base rights for page access and feature rights for in-page elements. The `AuthorizationService` in the domain module computes effective rights (role rights + individual rights) and provides `hasRight(userId, rightName)` checks.

## R-010: Built-in Data Seeding

**Decision**: A `DataSeeder` Spring component (`@Component` implementing `CommandLineRunner`) that runs on application startup and seeds: (1) built-in roles (Admin, User, Visitor), (2) built-in base rights (read:/profile, read:/admin, write:/admin, read:/permissions), (3) built-in feature rights (manage_users, manage_permissions), (4) role-right associations, and (5) the built-in admin account (`admin` / `123`).

**Rationale**: `CommandLineRunner` executes after the Spring context is fully initialized, ensuring all repositories are available. The seeder is idempotent — it checks for existing data before inserting, so it's safe to run on every startup. This approach keeps seeding logic in the app module (infrastructure concern) while the domain module defines the entities and validation rules.

**Alternatives considered**:
- **Database migration scripts (Flyway/Liquibase)**: SQL-based seeding. Rejected — no database in this phase.
- **`@PostConstruct` on repositories**: Seed data in each repository's initialization. Rejected — scatters seeding logic across multiple classes; harder to understand the full initial state.
- **External configuration file (JSON/YAML)**: Define initial data in a config file. Rejected — adds parsing complexity for a small, well-defined set of initial data.

**Implications**: The `DataSeeder` creates the Admin role with all rights, the User role with `read:/profile`, and the Visitor role with `read:/profile` (read-only). The built-in admin account is created with the Admin role. All seeding is idempotent and logged.

## R-011: Domain AuthService Dependency Inversion

**Decision**: The domain `AuthService` is the full authentication orchestrator. It receives all dependencies via constructor injection: `UserRepository`, `SessionRepository`, `RoleRepository`, and `PasswordEncoder` (an interface defined in domain, implemented in app). It performs credential validation, password hash comparison, session creation, and user lookup through these abstractions.

**Rationale**: The constitution (Principle IV) requires domain services to own business logic and be stateless. The spec clarification (session 2026-06-07f) resolved that domain should be the full orchestrator rather than delegating password comparison to the app layer. By defining `PasswordEncoder` as a domain interface and receiving it via constructor injection, the domain module remains infrastructure-free while owning all authentication business rules. The app module wires concrete implementations via Spring's dependency injection.

**Alternatives considered**:
- **App-layer orchestration**: App service handles password comparison and session creation, domain only validates format. Rejected — splits authentication business logic across modules, violating DDD principle that domain owns all business rules.
- **Domain receives only password hashes**: Domain compares pre-hashed passwords. Rejected — domain would need to know about hashing algorithms, leaking infrastructure concerns.

**Implications**: The `AuthService` constructor signature is `AuthService(UserRepository, RoleRepository, SessionRepository, PasswordEncoder)`. Domain unit tests mock all four dependencies. The app module's Spring configuration provides the concrete `BCryptPasswordEncoder` and `InMemory*Repository` beans. Controllers delegate to `AuthService` for all authentication operations.

## R-012: Concurrent Sessions Strategy

**Decision**: Allow multiple concurrent sessions per user. Each login creates a new independent session with its own token and expiration. Logout invalidates only the current session, not other active sessions for the same user.

**Rationale**: The spec clarification (session 2026-06-07f) resolved this as the desired behavior. Multiple concurrent sessions are the simplest approach for a proof-of-concept, match the data model design (`SessionRepository` has a `userId → Set<token>` index), and avoid surprising users by invalidating their other sessions. Session limits can be added later if needed.

**Alternatives considered**:
- **Single session per user (kick-out)**: New login invalidates all existing sessions. Rejected — surprising UX; user logged out on one device when logging in on another without warning.
- **Configurable session limit**: Admin sets max sessions per user. Rejected — adds unnecessary complexity for proof-of-concept phase.

**Implications**: The `SessionRepository` stores sessions keyed by token with a secondary index `userId → Set<token>`. The `SessionService.validateSession(token)` checks only the specific token's validity and expiration. `SessionService.invalidateSession(token)` deletes only the specified token. The `DataSeeder` creates only one session for the built-in admin at startup.
