# Data Model: React Hello World Project Setup

**Feature**: 005-react-helloworld-setup | **Date**: 2026-06-23

## Overview

This feature is a project scaffold with no domain entities or data persistence. The "data model" consists of configuration artifacts that define the project structure and build pipeline.

## Configuration Entities

### ProjectConfig (`web/package.json`)

| Field | Type | Description |
|-------|------|-------------|
| name | string | Project identifier (`"web"`) |
| version | string | Semantic version (`"0.0.0"`) |
| type | string | Module type (`"module"`) |
| scripts | object | Command aliases: `dev`, `build`, `preview`, `test` |
| dependencies | object | Runtime deps: `react`, `react-dom`, `@mui/material`, `@emotion/react`, `@emotion/styled` |
| devDependencies | object | Build/test deps: `vite`, `typescript`, `@vitejs/plugin-react`, `@playwright/test`, `@types/react`, `@types/react-dom` |

### TypeScriptConfig (`web/tsconfig.json`)

| Field | Type | Description |
|-------|------|-------------|
| compilerOptions.target | string | JS target (`"ES2020"`) |
| compilerOptions.module | string | Module system (`"ESNext"`) |
| compilerOptions.jsx | string | JSX transform (`"react-jsx"`) |
| compilerOptions.strict | boolean | Strict mode (`true`) |
| references | array | Project references to `tsconfig.app.json` and `tsconfig.node.json` |

### ViteConfig (`web/vite.config.ts`)

| Field | Type | Description |
|-------|------|-------------|
| plugins | array | `[@vitejs/plugin-react]` |
| server.port | number | Dev server port (`5173` default) |

### PlaywrightConfig (`web/playwright.config.ts`)

| Field | Type | Description |
|-------|------|-------------|
| testDir | string | Test directory (`"./e2e"`) |
| webServer.command | string | Dev server command (`"npm run dev"`) |
| webServer.url | string | Server URL (`"http://localhost:5173"`) |
| use.projects | array | Browser targets: Chromium, Firefox, WebKit |

### ArchitectureDocTemplate (`docs/template/architecture-template.md`)

| Section | Sub-sections | Description |
|---------|--------------|-------------|
| Project Summary | — | High-level project overview |
| Modules | Module Summary | Brief description of module purpose |
| Modules | Tech stack | Technologies used by this module |
| Modules | Notes | Additional context or constraints |

## Relationships

```
ProjectConfig
├── references → TypeScriptConfig
├── references → ViteConfig
└── references → PlaywrightConfig

ArchitectureDocTemplate
└── instantiated-by → ArchitectureDoc (docs/Architecture.md)
```

## State Transitions

Not applicable — configuration files are static artifacts, not runtime entities.
