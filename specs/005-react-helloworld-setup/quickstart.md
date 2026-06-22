# Quickstart: React Hello World Project

**Feature**: 005-react-helloworld-setup | **Date**: 2026-06-23

## Prerequisites

- Node.js v22.x or higher (verify: `node --version`)
- npm (bundled with Node.js)

## Setup

```bash
# 1. Navigate to the web project
cd web

# 2. Install dependencies
npm install

# 3. Install Playwright browsers (first time only)
npx playwright install
```

## Development

```bash
# Start the dev server
npm run dev
```

Open `http://localhost:5173` in your browser. You should see the Hello World page with MUI theming applied.

## Building

```bash
# Create a production build
npm run build

# Preview the production build locally
npm run preview
```

## Testing

```bash
# Run Playwright e2e tests
npm test
```

Tests run against the Vite dev server automatically. Results are reported for Chromium, Firefox, and WebKit.

## Project Structure

```text
web/
├── src/            # Application source code
│   ├── App.tsx     # Root component
│   └── main.tsx    # Entry point
├── e2e/            # Playwright end-to-end tests
├── public/         # Static assets
└── docs/           # Project documentation (at repo root)
```

## Documentation

- Architecture overview: `docs/Architecture.md`
- Architecture template: `docs/template/architecture-template.md`
- Feature specification: `specs/005-react-helloworld-setup/spec.md`
