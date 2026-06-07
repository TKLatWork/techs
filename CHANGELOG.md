# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/),
and this project adheres to [Semantic Versioning](https://semver.org/).

## [Unreleased]

### Added

- **User Authentication & Access Control** (Feature 004)
  - Username/password login with session token authentication
  - Self-registration with auto-assigned Visitor role
  - Two-tier permission model: base URL rights + feature rights
  - Role-based access control with single role per user plus individual rights
  - Permission management page for admin users (roles, rights, users, features)
  - Feature permission registration API for self-registering permissions
  - Spring Security integration with session filter chain
  - In-memory repositories (ConcurrentHashMap) with data seeding
  - Built-in admin account (`admin`/`123`), roles (Admin, User, Visitor), and rights
  - ProtectedRoute component for frontend route guards
  - MUI v5 integration with theme configuration
  - react-router-dom v6 with feature-based route auto-discovery
  - Domain PasswordEncoder interface with BCrypt implementation in app module
  - AuthContext, useAuth, usePermission hooks for frontend auth state
  - apiClient fetch wrapper with auth header injection and 401 handling

### Changed

- Restructured domain module to feature-based packages (`userauth/entity/`, `userauth/valueobject/`, `userauth/service/`, `userauth/repository/`)
- Restructured api module to feature-based packages (`userauth/model/`)
- Restructured app module to flat structure (`controller/`, `service/`, `repository/`, `config/`, `security/`)
- Restructured web module to feature-based architecture (`features/auth/`, `features/profile/`, `features/permissions/`, `shared/`, `pages/`, `app/`)
- Updated typescript-generator classPattern to scan feature-based packages

### Removed

- Placeholder classes: `ProjectInfo` (domain), `UserDto` (api), `ProjectService` (app)
- Placeholder tests: `ProjectInfoTest`, `UserDtoTest`, `ProjectServiceTest`, `DomainDependencyTest`, `ApiContractDependencyTest`
- Legacy web components: `StatusBadge`, `userService`, `StatusBadge.test.tsx`

### Added (Initial)

- 4-module monorepo project structure: `domain`, `api`, `app`, `web`
- Maven parent/aggregator POM for Java modules (domain, api, app)
- Root npm workspace configuration for web module
- `api` module with typescript-generator Maven plugin for Java-to-TypeScript type generation
- `app` module with Spring Boot application entry point
- `web` module with React + Vite + TypeScript setup
- Structure validation script (`scripts/validate-structure.mjs`)
- Root `build.ps1` orchestration script
- README with build commands and module documentation
