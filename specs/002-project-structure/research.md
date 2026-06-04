# Research: Project Structure Setup

**Feature**: 002-project-structure
**Date**: 2026-05-31

## R1: Maven Multi-Module Parent POM Structure

**Decision**: Use a parent POM at the repository root with `<modules>` declaring `domain`, `api`, and `app` (in dependency order). The `web` module is excluded from Maven (it's npm-based).

**Rationale**: Maven reactor automatically sorts modules by dependency graph. Declaring modules in dependency order (`domain` → `api` → `app`) ensures correct build sequencing. The parent POM centralizes dependency management and plugin configuration.

**Alternatives considered**:
- Separate standalone POMs per module — rejected because no shared dependency management, manual build ordering required
- Gradle multi-project — rejected because spec specifies Maven

## R2: typescript-generator Maven Plugin Configuration

**Decision**: Use `cz.habarta.typescript-generator:typescript-generator-maven-plugin` (v4.1.1) in the `api` module, bound to `process-classes` phase. Output TypeScript module files to `api/target/generated-sources/typescript/`.

**Rationale**: The plugin uses Java reflection on compiled classes, supports Jackson annotations, and generates clean TypeScript interfaces. Binding to `process-classes` ensures Java classes are compiled before generation. The `outputKind: module` setting produces ES module-compatible TypeScript.

**Alternatives considered**:
- OpenAPI Generator — rejected because requires OpenAPI spec as intermediate, adds complexity
- Manual TypeScript definitions — rejected because no single source of truth, drift risk
- Custom reflection-based script — rejected because reinventing a well-maintained tool

**Configuration details**:
- `jsonLibrary`: `jackson2` (matches Spring Boot's default JSON library)
- `outputKind`: `module` (ES module output)
- `outputFileType`: `implementationFile` (`.ts` not `.d.ts` for direct import)
- `classes` or `classPatterns`: configured to scan API DTO packages
- Output path: `api/target/generated-sources/typescript/api-models.ts`

## R3: Web Module Consuming Generated TypeScript

**Decision**: The `web` module references the `api` module's generated TypeScript output via a relative path in `tsconfig.json` path mappings. The generated file is committed to the repository (not gitignored) to avoid requiring Maven build for frontend development.

**Rationale**: Relative path references are simpler than npm workspace links for a generated artifact. Committing the generated file allows frontend developers to work without a Java/Maven environment. The file is regenerated and re-committed when API contracts change.

**Alternatives considered**:
- npm workspace link to api output — rejected because npm workspaces expect `package.json`, adding complexity for a single generated file
- Build script copies file into web/src/ — rejected because creates duplicate file, drift risk
- On-demand generation via pre-build hook — rejected because requires Maven installed for frontend-only development

## R4: Web Module Testing Framework

**Decision**: Vitest for the `web` module.

**Rationale**: Vitest is the standard testing framework for Vite-based projects. It shares Vite's transform pipeline (no duplicate config), supports React Testing Library, and provides Jest-compatible API with faster execution via native ESM.

**Alternatives considered**:
- Jest — rejected because requires separate transform config for Vite, slower with CJS
- Mocha + Chai — rejected because less React ecosystem integration

## R5: Java Module Testing Framework

**Decision**: JUnit 5 (Jupiter) with Spring Boot Test for `app`, plain JUnit 5 for `domain` and `api`.

**Rationale**: JUnit 5 is the standard for Java 21 projects. Spring Boot Test provides integration testing utilities for the `app` module. `domain` and `api` are pure libraries needing only unit tests.

**Alternatives considered**:
- TestNG — rejected because less community adoption, weaker Spring Boot integration
- JUnit 4 — rejected because legacy, JUnit 5 is current standard

## R6: Root-Level Build Orchestration

**Decision**: A root-level Maven POM acts as aggregator for Java modules (`domain`, `api`, `app`). A root-level shell script or `package.json` at root orchestrates cross-ecosystem builds (Maven + npm).

**Rationale**: Maven reactor handles Java module ordering. A root script provides a single command to build everything. This separates concerns: Maven for Java, npm for web, root script for orchestration.

**Alternatives considered**:
- Maven exec plugin calling npm — rejected because couples Maven to npm lifecycle
- Single npm script calling Maven — rejected because makes npm the orchestrator for Java builds
- Makefile — rejected because less portable on Windows (project is on Windows)

## R7: Canonical Architecture Drift

**Decision**: The spec introduces a 4th module (`api`) and changes the dependency graph from the canonical ARCHITECTURE.md. This drift MUST be logged in `DRIFT-LOG.md`.

**Rationale**: The canonical ARCHITECTURE.md describes 3 modules with `web` depending on `domain` directly. The clarified spec adds `api` as an intermediary: `web` → `api` → `domain` ← `app`. This is a material architectural change requiring drift documentation.

**Canonical state**: `web` → `domain`, `app` → `domain`
**New state**: `web` → `api` → `domain` ← `app`
