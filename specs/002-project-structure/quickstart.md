# Quickstart: Project Structure Setup

**Feature**: 002-project-structure

## Prerequisites

- Java 21 (JDK 21) installed and `JAVA_HOME` set
- Maven 3.9+ installed
- Node.js 18+ and npm 9+ installed
- Git installed

## Project Layout

```text
techs/
├── domain/                 # Java shared library (business logic, models)
│   ├── src/main/java/
│   ├── src/test/java/
│   └── pom.xml
├── api/                    # API contracts (Java DTOs → TypeScript types)
│   ├── src/main/java/
│   ├── src/test/java/
│   ├── target/generated-sources/typescript/   # Generated TypeScript output
│   └── pom.xml
├── app/                    # Spring Boot backend
│   ├── src/main/java/
│   ├── src/test/java/
│   └── pom.xml
├── web/                    # React + Vite frontend
│   ├── src/
│   ├── public/
│   ├── package.json
│   ├── tsconfig.json
│   └── vite.config.ts
├── pom.xml                 # Maven parent/aggregator (domain, api, app)
├── package.json            # Root npm workspace config (web)
└── build.ps1              # Root build orchestration script
```

## Dependency Graph

```
web ──> api ──> domain
                ^
app ────────────┘
app ──> api
```

## Build Commands

### Full project build

```powershell
# Build Java modules (domain → api → app)
mvn clean install

# Generate TypeScript types from api module (runs as part of api build)
# Output: api/target/generated-sources/typescript/api-models.ts

# Build web module
cd web
npm install
npm run build
```

### Per-module commands

```powershell
# Domain module
cd domain
mvn clean test

# API module (includes TypeScript generation)
cd api
mvn clean install

# App module
cd app
mvn clean test
mvn spring-boot:run

# Web module
cd web
npm install
npm run dev      # Development server
npm run build    # Production build
npm test         # Run tests (Vitest)
```

## TypeScript Type Generation

The `api` module generates TypeScript types from Java DTOs during the Maven `process-classes` phase using the typescript-generator Maven plugin.

Generated output: `api/target/generated-sources/typescript/api-models.ts`

The `web` module references this file via `tsconfig.json` path mapping:

```json
{
  "compilerOptions": {
    "paths": {
      "@api/*": ["../api/target/generated-sources/typescript/*"]
    }
  }
}
```

## Adding a New API Contract

1. Add a Java class/DTO in `api/src/main/java/`
2. Run `mvn clean install` in the `api` module
3. Verify generated TypeScript in `api/target/generated-sources/typescript/`
4. Import the generated type in `web/src/` using `@api/` path alias
5. Implement the corresponding endpoint in `app`
