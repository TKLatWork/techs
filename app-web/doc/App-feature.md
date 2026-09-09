# Frontend feature App-feature
Name: App-feature
Version: 0.2.0

# Summary
Shared base of the app-web SPA that every feature builds on: navigation, IoC (DI via Context + Provider), http client, theme, auth. Auth is scaffolded against an assumed backend Basic-login API (not live yet).

# Terminology
| Term | Meaning |
|------|---------|
| Shared infra | Code under `src/app`, one obvious home for cross-feature capabilities |
| Feature | Code under `src/features/<feature-name>`, a client route at `/web/<feature-name>` |
| Composition root | `src/main.tsx`, the single place wiring theme, router, layout, and feature providers |
| Service interface | Abstraction a feature exposes; components depend on it, never on a concrete impl |
| Provider / hook | React Context pair injecting a service instance and consuming it in components |
| Basic login | Username + password form login exchanging credentials for a token; not HTTP Basic |

## Biz requirements
- All feature pages share one consistent nav bar, layout, and theme
- Adding a feature = new feature folder + one route + one nav item; existing feature code untouched
- Features consume capabilities via interfaces, so implementations can be swapped (mock in tests) without touching component code
- Auth: users sign in at a login page before reaching feature pages; sign-in state is shared across the SPA; nav bar shows the signed-in user and offers sign-out

## Technical/Implement notes
| Capability | Status | Home | Notes |
|------------|--------|------|-------|
| Nav | Implemented | `src/app/navItems.ts`, `src/app/components/NavBar.tsx`, `src/app/Layout.tsx` | NavItem list drives NavBar; active route highlighted; Layout wraps every feature route; shows user + Logout when signed in |
| IoC | Implemented | `src/main.tsx` + per-feature `*ServiceProvider.tsx` | Pattern and code examples: [Coding-frontend](../../doc/common/Coding-frontend.md) Rule: DI/IoC |
| Http | Implemented | `src/app/http.ts` | Shared axios instance + `toErrorMessage`; services use it, components never call it directly |
| Theme | Implemented | `src/app/theme.ts` | MUI theme applied once at composition root |
| Auth | Scaffolded | `src/app/auth/` (authService, AuthProvider, RequireAuth, LoginPage) | IoC citizen: `AuthService` interface + provider + `useAuth` hook; provider accepts injected service for tests |

### Auth assumed API contract (backend not implemented yet)
| Item | Value |
|------|-------|
| Endpoint | `POST /api/auth/login` |
| Request | `{ username, password }` |
| Response 200 | `{ username, token }` |
| Response 401 | invalid credentials, body `{ message }` |
| Token usage | `Authorization: Bearer <token>` on subsequent `/api` calls via shared http instance |
| Persistence | `sessionStorage` (token + username), restored on reload, cleared on logout (client-side only) |

### Auth wiring
- `/login` route stands outside `Layout` (no nav bar on login page)
- Feature routes wrapped in `RequireAuth`: no user -> redirect `/login`, remembering the attempted path
- On login success, navigate back to the attempted path (default `/`)

# Testing
Test stack: vitest + jsdom + Testing Library (unit, `npm test` in app-web); Playwright (SIT, per project-basic Testing pattern).

## Unit test cases

### UT-01-navbar-renders-nav-items
Test target: NavBar renders every entry of `navItems` and highlights the active route
Expect result: one button per NavItem; button matching current path uses the active variant
Test setup/flow: render NavBar inside MemoryRouter + AuthProvider (mock service); assert buttons by label
Status: implemented (`src/app/components/NavBar.test.tsx`)

### UT-02-ioc-mock-injection
Test target: a component consumes its service via hook only
Expect result: component renders/acts on data from a mock service; no network, no module mocking
Test setup/flow: render inside a provider whose value is a mock service; assert behavior
Status: covered by UT-03..UT-06 (AuthProvider with mock AuthService)

### UT-03-login-success
Test target: LoginPage submits credentials through `AuthService.login`
Expect result: `login` called with entered username/password; on success navigates away from `/login`
Test setup/flow: mock AuthService resolves; render LoginPage in MemoryRouter + AuthProvider; fill form, submit, assert call + navigation
Status: implemented (`src/app/auth/auth.test.tsx`)

### UT-04-login-failure-shows-error
Test target: failed login surfaces an error message
Expect result: error alert shown, user stays on login page
Test setup/flow: mock AuthService rejects; submit form; assert error text visible
Status: implemented (`src/app/auth/auth.test.tsx`)

### UT-05-require-auth-redirects
Test target: RequireAuth guards feature routes
Expect result: unauthenticated -> redirected to `/login`; authenticated -> children rendered
Test setup/flow: mock AuthService `restore()` returns null / a user; render guarded route; assert which content shows
Status: implemented (`src/app/auth/auth.test.tsx`)

### UT-06-logout
Test target: sign-out clears auth state
Expect result: `logout` called; user redirected to `/login`
Test setup/flow: mock AuthService with restored user; render NavBar; click Logout; assert
Status: implemented (`src/app/auth/auth.test.tsx`)

## SIT

### SIT-01-spa-navigation
Test target: built SPA served by app-server, navigation works end to end
Expect result: `/web/timezone` loads; clicking a nav item routes to the feature page without full reload
Test setup/flow: Playwright against running app-server; goto, click nav, assert URL and page content
Status: pending (Playwright project not set up yet)

### SIT-02-auth-login-e2e
Test target: real login flow against backend auth API
Expect result: login page -> valid credentials -> feature page; invalid credentials -> error
Test setup/flow: Playwright against app-server with auth service implemented
Status: blocked (backend auth API not implemented)
