# Research: React Hello World Project Setup

**Feature**: 005-react-helloworld-setup | **Date**: 2026-06-23

## R-001: Vite + React + TypeScript Scaffolding Approach

**Decision**: Use `npm create vite@latest web -- --template react-ts` to scaffold the project.

**Rationale**: The official `create-vite` tool (v9.0.7) provides a battle-tested template with pre-configured TypeScript, React 19, and Vite 8. It generates correct `tsconfig.json`, `vite.config.ts`, and `package.json` out of the box, eliminating manual configuration errors.

**Alternatives considered**:
- Manual setup from scratch: More control but error-prone and time-consuming for a Hello World project.
- Next.js or Remix: Adds SSR/routing complexity not needed for a simple SPA.

## R-002: Package Versions (verified 2026-06-23)

**Decision**: Use latest stable versions for all dependencies.

| Package | Version | Notes |
|---------|---------|-------|
| Node.js | v22.17.1 | Current environment, compatible with all packages below |
| Vite | 8.0.16 | Latest stable |
| create-vite | 9.0.7 | Latest stable |
| React | 19.2.7 | Latest stable |
| react-dom | 19.2.7 | Must match React version |
| TypeScript | 6.0.3 | Latest stable |
| @vitejs/plugin-react | 6.0.2 | Required Vite plugin for React |
| @mui/material | 9.1.1 | Latest stable MUI |
| @emotion/react | ^11.5.0 | MUI peer dependency |
| @emotion/styled | ^11.3.0 | MUI peer dependency |
| @playwright/test | 1.61.0 | Latest stable Playwright |

**Rationale**: Latest stable versions ensure access to newest features, security patches, and best community support. All versions verified compatible with Node.js v22.x.

**Alternatives considered**:
- Pinning to older LTS versions: Unnecessary for a new project; latest stable is well-tested.

## R-003: MUI Setup Requirements

**Decision**: Install `@mui/material`, `@emotion/react`, and `@emotion/styled` as the minimum MUI dependency set. Optionally add `@mui/icons-material` for icon support.

**Rationale**: MUI 9.x requires Emotion as the styling engine. The peer dependencies are `react ^17-19`, `react-dom ^17-19`, `@emotion/react ^11.5.0`, and `@emotion/styled ^11.3.0`. All satisfied by our chosen stack.

**Alternatives considered**:
- Using MUI's Pigment CSS (`@mui/material-pigment-css`): Newer zero-runtime CSS approach, but less mature ecosystem and fewer community resources.
- Using Tailwind CSS instead of MUI: User explicitly requested MUI.

## R-004: Playwright Configuration with Vite

**Decision**: Use `@playwright/test` with `webServer` config pointing to the Vite dev server (`npm run dev`).

**Rationale**: Playwright's `webServer` option automatically starts and stops the Vite dev server during test runs, ensuring tests always run against a fresh server instance. This is the recommended pattern for Vite + Playwright integration.

**Configuration approach**:
- `playwright.config.ts` at `web/` root
- `webServer.command`: `npm run dev`
- `webServer.url`: `http://localhost:5173` (Vite default)
- Test directory: `web/e2e/`
- Browsers: Chromium, Firefox, WebKit

**Alternatives considered**:
- Running against production build (`npm run preview`): Slower feedback loop; dev server is better for development.
- Using Cypress: User explicitly requested Playwright.

## R-005: Node.js Version Verification

**Decision**: Check Node.js version at the start of the scaffolding process using `node --version` and validate against minimum v22.x.

**Rationale**: Node.js v22 is the current LTS line and is required by Vite 8.x. A simple version check prevents confusing downstream errors from incompatible Node.js versions.

**Alternatives considered**:
- Using `.nvmrc` or `engines` field in `package.json`: Good supplementary measures, but an upfront check provides immediate feedback before any commands run.

## R-006: Architecture Document Structure

**Decision**: Create a reusable template at `docs/template/architecture-template.md` with sections: Project Summary, Modules (each with Module Summary, Tech stack, Notes). The filled Architecture doc at `docs/Architecture.md` documents this project's modules without feature details or implementation code.

**Rationale**: The constitution (Principle I: Spec-Driven) requires all documents to follow a template in `docs/template/`. The Architecture doc serves as a high-level structural reference, not a feature spec — it should describe what modules exist and their tech stack, not how features work internally.

**Alternatives considered**:
- Including feature details in the Architecture doc: Violates user's explicit requirement that the doc should not contain feature info or detailed code.
- Using ADR (Architecture Decision Records) format: More granular but doesn't match the user's requested structure.
