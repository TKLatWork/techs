# Plan: Split Coding.md into frontend/backend docs + DI/IoC pattern section

## Goal
Replace `doc/common/Coding.md` with two docs — `Coding-frontend.md` and `Coding-backend.md` — and document the Context + Provider pattern as the standard DI/IoC approach for `web-*` React modules.

## Discoveries
- `doc/common/Coding.md` is **empty** (0 lines) — "split" is really delete + create two fresh docs; no content to migrate.
- No file anywhere references `Coding.md` (grep clean); `doc/AGENTS.md` links only to folders, so no index updates are strictly required.
- Existing naming convention in `doc/common/`: capitalized kebab-case (`General-behave.md`, `Coding.md`).

## Decisions
- Filenames: `doc/common/Coding-frontend.md` and `doc/common/Coding-backend.md` (capitalized, matching `General-behave.md` convention rather than the lowercase form in the request).
- Frontend doc: header/scope note + the DI/IoC section with a short TypeScript sketch (this is the requested content).
- Backend doc: minimal skeleton — header/scope note + the few conventions already established in code (constructor injection via Spring IoC, records for API DTOs, URL convention pointer to `doc/project-basic/AGENTS.md`). No invented rules.
- DI/IoC pattern documented as a **rule** for `web-*` modules: components never import concrete service modules directly; services are created at the composition root and injected via React Context, consumed through custom hooks.
- Docs-only change: no refactor of `web-timezone` code in this task.

## Tasks

### 1. Remove old file
- Delete `doc/common/Coding.md`

### 2. Create `doc/common/Coding-frontend.md`
- Title + scope line: frontend coding rules for `web-*` modules (React + Vite + TypeScript), following `doc/common` quote style
- Section `## DI/IoC: Context + Provider pattern` containing:
  - Rule: components depend on service abstractions injected via Context, never direct module-singleton imports
  - Composition root: service instances created once at app root (`main.tsx`/root `App.tsx`) and provided via `<Provider>`
  - Services defined as interface/type + implementation; consumed via custom hooks (e.g. `useTimezoneService()`)
  - Multiple services: one context per service (or a single services object) — prefer one context per service
  - Testability: swap provider value with a mock in tests
  - Short TS sketch (~20 lines): service type, context, provider at root, `useXxxService` hook, component consuming it

### 3. Create `doc/common/Coding-backend.md`
- Title + scope line: backend coding rules for `service-*` and `app-server` modules (JDK 21 + Spring Boot)
- Seed sections (brief, reflecting existing code):
  - IoC: use Spring constructor injection; no field `@Autowired`
  - API DTOs: Java `record` types
  - URL convention: pointer to `doc/project-basic/AGENTS.md` (`/api/<service-name>/...`)
- Note that further rules will be added as the project grows

### 4. Index touch-up (optional, low risk)
- `doc/AGENTS.md`: no change needed (folder-level links only) — skip unless the two new docs should be listed; keep index as-is

## Validation
1. `doc/common/` contains exactly `Coding-frontend.md`, `Coding-backend.md`, `General-behave.md`
2. Grep for `Coding.md` returns no dangling references
3. Frontend doc contains the DI/IoC section with a compilable-looking TS sketch consistent with `web-timezone` stack (React 19, TS strict)

## Out of scope
- Refactoring `web-timezone` to actually use the Context + Provider pattern (follow-up task if desired)
- Adding further frontend/backend coding rules beyond the seeded ones
- Git commit (unless requested)
