# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/),
and this project adheres to [Semantic Versioning](https://semver.org/).

## [Unreleased]

### Added

- 4-module monorepo project structure: `domain`, `api`, `app`, `web`
- Maven parent/aggregator POM for Java modules (domain, api, app)
- Root npm workspace configuration for web module
- `api` module with typescript-generator Maven plugin for Java-to-TypeScript type generation
- `app` module with Spring Boot application entry point
- `web` module with React + Vite + TypeScript setup
- Sample domain entity (`ProjectInfo`), API DTO (`UserDto`), and app service (`ProjectService`)
- Cross-module dependency tests verifying `web → api → domain ← app` graph
- Structure validation script (`scripts/validate-structure.mjs`)
- Root `build.ps1` orchestration script
- README with build commands and module documentation

### Changed

### Fixed

### Removed
