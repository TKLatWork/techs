# Test Specification

<!-- docguard:version 0.2.0 -->
<!-- docguard:status active -->
<!-- docguard:last-reviewed 2026-05-31 -->
<!-- docguard:owner @project-maintainer -->

> **Canonical document** — Design intent. This file declares what tests MUST exist.
> Last updated: 2026-05-31

| Metadata | Value |
|----------|-------|
| **Status** | ![Status](https://img.shields.io/badge/status-active-green) |
| **Version** | `0.2.0` |
| **Last Updated** | 2026-05-31 |
| **Owner** | @project-maintainer |

---

## Test Categories

| Category | Required | Applies To | Suggested Tools |
|----------|----------|-----------|-----------------|
| Unit | Yes | Services, utilities, helpers, domain models | Vitest (web), JUnit 5 (app/domain) |
| Integration | Yes | API routes, DB operations, module interactions | Spring Boot Test (app), Vitest + MSW (web) |
| E2E | No | Critical user journeys | Playwright (web) |
| Contract | Optional | Public-facing APIs between web and app | Spring Cloud Contract |
| Load | Optional | High-traffic endpoints | k6 |
| Security | Optional | Auth flows, input validation | OWASP ZAP |

## Coverage Rules

| Source Pattern | Required Test Pattern | Category |
|---------------|----------------------|----------|
| `web/src/**/*.{ts,tsx}` | `web/src/**/*.test.{ts,tsx}` | Unit |
| `app/src/main/java/**/*.java` | `app/src/test/java/**/*Test.java` | Unit + Integration |
| `domain/src/main/java/**/*.java` | `domain/src/test/java/**/*Test.java` | Unit |

## Service-to-Test Map

No source files exist yet. This map will be populated as implementation begins.

| Source File | Unit Test | Integration Test | Status |
|------------|-----------|-----------------|--------|
| `domain/src/main/java/com/techs/domain/model/ProjectInfo.java` | `domain/src/test/java/com/techs/domain/ProjectInfoTest.java` | N/A | Active |
| `api/src/main/java/com/techs/api/model/UserDto.java` | `api/src/test/java/com/techs/api/UserDtoTest.java` | N/A | Active |
| `app/src/main/java/com/techs/app/service/ProjectService.java` | `app/src/test/java/com/techs/app/ProjectServiceTest.java` | N/A | Active |
| `web/src/components/StatusBadge.tsx` | `web/src/__tests__/StatusBadge.test.tsx` | N/A | Active |
| `web/src/services/userService.ts` | N/A | N/A | Active |

## Critical User Journeys (E2E Required)

| # | Journey Description | Test File | Status |
|---|-------------------|-----------|--------|
| 1 | Project structure creation — verify web, app, domain modules exist | TBD | Pending implementation |
| 2 | Domain module shared types — verify web and app consume domain definitions | TBD | Pending implementation |
| 3 | Independent module build — verify each module builds and tests in isolation | TBD | Pending implementation |

## Test Commands

| Module | Command | Description |
|--------|---------|-------------|
| domain | `mvn -pl domain test` | Run domain library unit tests via Maven |
| api | `mvn -pl api test` | Run API contract unit tests via Maven |
| app | `mvn -pl app test` | Run backend unit + integration tests via Maven |
| web | `npm --workspace web run test` | Run frontend unit tests via Vitest |
| Root (Java) | `mvn test` | Run all Java module tests from project root |

## Recommended Test Patterns

| Pattern | Description | Priority |
|---------|-------------|----------|
| Test-first (TDD) | Write failing tests before implementation per constitution Principle I | High |
| Config-awareness | Test behavior changes per `.docguard.json` / env config | High |
| Individual functions | Test each module/function directly, not just via CLI | High |
| Edge cases | Empty inputs, missing files, invalid config | Medium |
| Error paths | Verify graceful failure, not just happy path | Medium |
| Regression guards | Pin specific bug fixes with dedicated tests | Medium |
| Module isolation | Each module tests independently without requiring other modules to be built | High |

---

## Revision History

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 0.1.0 | 2026-05-31 | DocGuard Init | Initial template |
| 0.2.0 | 2026-05-31 | Constitution update | Filled placeholders with project test strategy |
