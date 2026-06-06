# Techs

Multi-module monorepo with a Java backend and TypeScript frontend.

## Modules

| Module | Build System | Description |
|--------|-------------|-------------|
| `domain/` | Maven | Shared domain library (business logic, models) |
| `api/` | Maven | API contracts (Java DTOs → TypeScript types) |
| `app/` | Maven | Spring Boot backend application |
| `web/` | npm | React + Vite frontend |

## Dependency Graph

```
web ──> api ──> domain
                ^
app ────────────┘
app ──> api
```

## Prerequisites

- Java 21+ (JDK 21)
- Maven 3.9+
- Node.js 18+ and npm 9+

## Build

```powershell
# Java modules
mvn clean install

# Web module
npm --workspace web run build

# Full build script
.\build.ps1
```

## Per-Module Commands

```powershell
# Domain
mvn -pl domain test

# API (includes TypeScript generation)
mvn -pl api install

# App
mvn -pl app test
mvn -pl app spring-boot:run

# Web
npm --workspace web run dev
npm --workspace web run test
```

## TypeScript Type Generation

The `api` module generates TypeScript types from Java DTOs during Maven build. Output: `api/target/generated-sources/typescript/api-models.ts`. The `web` module consumes these via `@api/` path alias.

## Usage

1. Build Java modules: `mvn clean install`
2. Start the backend: `mvn -pl app spring-boot:run`
3. Start the frontend dev server: `npm --workspace web run dev`
4. Run all tests: `mvn test` (Java) + `npm --workspace web run test` (web)

See `specs/002-project-structure/quickstart.md` for detailed setup guide.

## License

UNLICENSED — All rights reserved.
