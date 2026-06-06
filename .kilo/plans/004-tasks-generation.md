# Plan: Generate tasks.md for 004-user-auth-access-control

## Context

The feature has 8 user stories (US0–US7), 36 functional requirements, and full design artifacts (spec, plan, data-model, contracts, research, quickstart). The constitution mandates Test-First / Red-Green-Refactor, so test tasks will be included.

## Pre-Execution Hooks

- `before_tasks` hook: Optional git commit (`speckit.git.commit`) — prompt user before generation

## Task Generation Strategy

### Phase Mapping

| Phase | Story | Priority | Goal |
|-------|-------|----------|------|
| 1 | US0 | P0 | Restructure all 4 modules to feature-based layouts; install react-router-dom + MUI |
| 2 | — | — | Foundational: domain entities/services/repos, API DTOs, shared infrastructure (security, seeder, apiClient) |
| 3 | US1 | P1 | User Login — auth endpoint, LoginForm, useAuth, ProtectedRoute |
| 4 | US5 | P1 | Self-Registration — register endpoint, RegisterForm, RegisterPage |
| 5 | US2 | P1 | View User Profile — profile endpoint, ProfileCard, ProfilePage |
| 6 | US4 | P2 | Logout — logout endpoint, session invalidation, UI integration |
| 7 | US3 | P2 | Permission-Based Access Control — URL authorization filter, usePermission, route guards |
| 8 | US6 | P2 | Permission Management Page — CRUD endpoints for roles/rights/users, management UI |
| 9 | US7 | P2 | Feature Permission Registration — registration API, feature disable, management UI integration |
| 10 | — | — | Polish & Cross-Cutting — quickstart validation, cleanup |

### Story Dependencies

```
US0 (P0) → Foundational → US1 (P1) → US5 (P1) → US2 (P1)
                                    → US4 (P2) → US3 (P2) → US6 (P2) → US7 (P2)
```

- US1 must complete before US5 (need login to verify registration)
- US1 must complete before US2 (need auth to view profile)
- US1 must complete before US4 (need login to logout)
- US3 must complete before US6 (need access control enforcement before management UI)
- US6 must complete before US7 (need management page before feature registration)

### Within Each Story Phase

Tests (write first, must fail) → Backend (controller + service integration) → Frontend (components, hooks, services) → Integration verification

### Parallel Opportunities

- **Phase 1**: Domain restructuring ∥ API restructuring ∥ App restructuring ∥ Web restructuring (different modules, no file conflicts)
- **Phase 2**: Domain entities ∥ API DTOs (different modules); InMemory repos ∥ SecurityConfig (different packages)
- **Per Story**: Backend tasks ∥ Frontend tasks (different modules); multiple frontend components within a story

### Test Inclusion

Tests ARE included because:
1. Constitution check: "all features will follow Red-Green-Refactor" (plan.md:39)
2. Spec has mandatory "User Scenarios & Testing" section with acceptance scenarios
3. Test frameworks and locations are specified (JUnit 5, Vitest)

### Estimated Task Count

| Phase | Estimated Tasks |
|-------|----------------|
| Phase 1 (Setup/US0) | ~15 |
| Phase 2 (Foundational) | ~20 |
| Phase 3 (US1 Login) | ~12 |
| Phase 4 (US5 Registration) | ~8 |
| Phase 5 (US2 Profile) | ~8 |
| Phase 6 (US4 Logout) | ~6 |
| Phase 7 (US3 Access Control) | ~10 |
| Phase 8 (US6 Permission Mgmt) | ~14 |
| Phase 9 (US7 Feature Registration) | ~8 |
| Phase 10 (Polish) | ~4 |
| **Total** | **~105** |

### File Path Conventions

All paths relative to repo root `E:\code\project\techs\`:
- Domain: `domain/src/main/java/com/techs/domain/userauth/`
- API: `api/src/main/java/com/techs/api/userauth/model/`
- App: `app/src/main/java/com/techs/app/userauth/`
- Web: `web/src/features/<name>/`, `web/src/shared/`, `web/src/pages/`
- Tests: co-located in `__tests__/` (web) or `src/test/java/` (Java)

## Post-Execution Hooks

- `after_tasks` hook: Optional git commit (`speckit.git.commit`) — prompt user after generation

## Output

- File: `E:\code\project\techs\specs\004-user-auth-access-control\tasks.md`
- Template: `.specify/templates/tasks-template.md`
- Format: Strict checklist format `- [ ] [TaskID] [P?] [Story?] Description with file path`
