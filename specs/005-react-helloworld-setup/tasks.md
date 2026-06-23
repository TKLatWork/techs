# Tasks: React Hello World Project Setup

**Input**: Design documents from `/specs/005-react-helloworld-setup/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, quickstart.md

**Tests**: Playwright e2e testing is explicitly requested in the feature specification (FR-004, US2). Test tasks are included.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3, US4)
- Include exact file paths in descriptions

## Path Conventions

- **Web project**: `web/` at repository root
- **Documentation**: `docs/` at repository root
- **Feature specs**: `specs/005-react-helloworld-setup/`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Verify prerequisites and create directory structure

- [X] T001 Verify Node.js version is v22.x or higher by running `node --version` and confirming output shows v22.17.1 or compatible v22.x version
- [X] T002 Create `docs/template/` directory structure at repository root for architecture documentation templates

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Scaffold the Vite+React+TypeScript project and install all dependencies

**CRITICAL**: No user story work can begin until this phase is complete

- [X] T003 Scaffold Vite+React+TypeScript project under `web/` by running `npm create vite@latest web -- --template react-ts` from repository root
- [X] T004 Install MUI and Emotion dependencies in `web/` by running `npm install @mui/material @emotion/react @emotion/styled` from `web/` directory
- [X] T005 Install Playwright as dev dependency in `web/` by running `npm install -D @playwright/test` from `web/` directory, then run `npx playwright install` to download browser binaries
- [X] T006 Add `"test": "npx playwright test"` script to `web/package.json` scripts section alongside existing `dev`, `build`, and `preview` scripts

**Checkpoint**: Foundation ready — `web/` project exists with all dependencies installed, user story implementation can now begin

---

## Phase 3: User Story 1 - Project Scaffolding (Priority: P1) MVP

**Goal**: A working React application that renders a "Hello World" page using MUI components, with a functional dev server and clean production build.

**Independent Test**: Run `npm run dev` from `web/` and verify the browser at `http://localhost:5173` displays a Hello World page with MUI styling. Run `npm run build` and confirm zero errors.

### Implementation for User Story 1

- [X] T007 [P] [US1] Replace default Vite template `web/src/App.tsx` with a Hello World component that imports and renders MUI components (e.g., `Typography`, `Box`, `Button` from `@mui/material`)
- [X] T008 [P] [US1] Update `web/src/main.tsx` to wrap the App component with MUI `ThemeProvider` and `CssBaseline` for default theming
- [X] T009 [US1] Remove default Vite template boilerplate files: delete `web/src/App.css` and `web/src/index.css`, clean up any unused assets in `web/public/`
- [X] T010 [US1] Verify dev server starts successfully by running `npm run dev` from `web/` and confirming `http://localhost:5173` renders the Hello World page with MUI components visible
- [X] T011 [US1] Verify production build completes with zero errors and zero TypeScript type errors by running `npm run build` from `web/`

**Checkpoint**: At this point, User Story 1 is fully functional — a working React+TypeScript+MUI app under `web/` with dev server and clean build

---

## Phase 4: User Story 2 - Automated Testing Setup (Priority: P2)

**Goal**: Playwright end-to-end testing fully configured with a sample test that validates the Hello World page renders correctly across Chromium, Firefox, and WebKit.

**Independent Test**: Run `npm test` from `web/` and confirm Playwright executes the sample test and reports passing results.

### Implementation for User Story 2

- [X] T012 [P] [US2] Create `web/playwright.config.ts` with `testDir` set to `./e2e`, `webServer` configured with `command: "npm run dev"` and `url: "http://localhost:5173"`, and projects for Chromium, Firefox, and WebKit browsers
- [X] T013 [P] [US2] Create `web/e2e/` directory for Playwright test files
- [X] T014 [US2] Create sample e2e test at `web/e2e/hello.spec.ts` that navigates to the root URL and asserts the page contains "Hello World" text and that an MUI component is rendered (e.g., check for a Material UI button or typography element)
- [ ] T015 [US2] Run `npm test` from `web/` and verify Playwright executes successfully with the sample test passing on all configured browsers

**Checkpoint**: At this point, User Stories 1 AND 2 are both functional — the app renders correctly and e2e tests validate it

---

## Phase 5: User Story 3 - Node.js Version Verification (Priority: P2)

**Goal**: The project enforces Node.js version compatibility so developers get clear feedback when using an unsupported version.

**Independent Test**: Inspect `web/package.json` for the `engines` field and verify it specifies Node.js >= 22.0.0.

### Implementation for User Story 3

- [X] T016 [US3] Add `"engines": { "node": ">=22.0.0" }` field to `web/package.json` to enforce minimum Node.js version requirement
- [X] T017 [US3] Add `"check:node": "node -e \"const v=process.versions.node.split('.');if(parseInt(v[0])<22){console.error('Node.js v22+ required, found '+process.versions.node);process.exit(1)}\""` script to `web/package.json` scripts section for explicit version checking

**Checkpoint**: Node.js version compatibility is enforced at both the package manager level and via an explicit check script

---

## Phase 6: User Story 4 - Architecture Documentation (Priority: P2)

**Goal**: A reusable Architecture doc template and a filled Architecture document that describes the project's high-level module structure and technology choices without feature details or implementation code.

**Independent Test**: Verify `docs/template/architecture-template.md` exists with required sections (Project Summary, Modules with Module Summary/Tech stack/Notes sub-sections) and `docs/Architecture.md` exists with filled content for this project.

### Implementation for User Story 4

- [X] T018 [P] [US4] Create Architecture doc template at `docs/template/architecture-template.md` with the following structure: `# Project Summary` section (placeholder for high-level overview), `# Modules` section containing a `## <name> module` subsection pattern, each module subsection having `### Module Summary`, `### Tech stack`, and `### Notes` sub-sections
- [X] T019 [US4] Create filled Architecture document at `docs/Architecture.md` using the template structure, documenting: Project Summary describing this as a React SPA, and Modules section covering the `web` frontend module (Module Summary: React SPA with Hello World page, Tech stack: React 19, TypeScript 6, Vite 8, MUI 9, Playwright, Notes: frontend-only, no backend integration) — ensure no feature-level details or implementation code is included

**Checkpoint**: Architecture documentation is complete and reviewable — template is reusable for future projects, filled doc describes this project's structure

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Final validation and cleanup across all user stories

- [ ] T020 Run quickstart.md validation end-to-end: `cd web && npm install && npx playwright install && npm run dev && npm run build && npm test` — confirm all commands succeed
- [X] T021 Verify `web/tsconfig.json` strict mode is enabled and no TypeScript type errors exist across the project by running `npx tsc --noEmit` from `web/`
- [X] T022 Final review: confirm `docs/Architecture.md` contains no feature details or implementation code, only high-level module and tech stack information

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies — can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion — BLOCKS all user stories
- **User Stories (Phase 3-6)**: All depend on Foundational phase completion
  - US1 (Phase 3): Must complete first — other stories depend on the scaffolded project
  - US2 (Phase 4): Depends on US1 (needs the running app to test against)
  - US3 (Phase 5): Can start after Foundational — independent of US1/US2
  - US4 (Phase 6): Can start after Foundational — independent of US1/US2/US3
- **Polish (Phase 7)**: Depends on all user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) — no dependencies on other stories
- **User Story 2 (P2)**: Depends on US1 — needs the Hello World app running to test against
- **User Story 3 (P2)**: Can start after Foundational (Phase 2) — independent of other stories
- **User Story 4 (P2)**: Can start after Foundational (Phase 2) — independent of other stories

### Within Each User Story

- Core implementation before verification
- Story complete before moving to next priority (except where independence allows parallel)

### Parallel Opportunities

- T007 and T008 (US1) can run in parallel — different files (`App.tsx` vs `main.tsx`)
- T012 and T013 (US2) can run in parallel — different files (`playwright.config.ts` vs `e2e/` directory)
- US3 (Phase 5) and US4 (Phase 6) can run in parallel with each other after US1 completes
- T018 (US4 template) can run in parallel with US3 tasks

---

## Parallel Example: User Story 1

```text
# Launch implementation tasks together (different files):
Task: "Replace default Vite template web/src/App.tsx with Hello World MUI component"
Task: "Update web/src/main.tsx to wrap App with MUI ThemeProvider"
```

## Parallel Example: US3 + US4 (after US1)

```text
# US3 and US4 can proceed simultaneously:
Task: "Add engines field to web/package.json"
Task: "Create Architecture doc template at docs/template/architecture-template.md"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL — blocks all stories)
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: Run `npm run dev` and `npm run build` from `web/`
5. Deploy/demo if ready — a working React+TS+MUI Hello World app

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Working app (MVP!)
3. Add User Story 2 → Test independently → App with e2e tests
4. Add User Story 3 → Test independently → Version enforcement
5. Add User Story 4 → Test independently → Architecture docs
6. Each story adds value without breaking previous stories

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Developer A: User Story 1 (scaffolding + Hello World page)
3. Once US1 done:
   - Developer A: User Story 2 (Playwright setup)
   - Developer B: User Story 3 + User Story 4 (version check + docs, in parallel)
4. Stories complete and integrate independently

---

## Notes

- [P] tasks = different files, no dependencies on incomplete tasks in same phase
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- The `web/` directory is the working directory for all npm commands
- All documentation paths are relative to repository root
