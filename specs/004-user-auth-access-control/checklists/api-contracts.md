# API Contract Quality Checklist: User Authentication & Access Control

**Purpose**: Validate API contract completeness, clarity, and consistency before implementation
**Created**: 2026-06-07
**Feature**: [spec.md](../spec.md)
**Depth**: Lightweight pre-implementation sanity
**Focus**: API contract quality (auth-api, permission-api, web-routes)
**Coverage**: All stories P0-P2

## Requirement Completeness

- [ ] CHK001 Are error response formats specified for all failure scenarios across auth-api endpoints (login, register, logout, me)? [Completeness, Spec §FR-004, FR-015, FR-018]
- [ ] CHK002 Are all permission management API endpoints documented with request/response schemas and error codes? [Completeness, Spec §FR-020 through FR-033]
- [ ] CHK003 Are web route contracts defined for all user stories (P0 through P2) with auth requirements and required rights? [Completeness, Spec §US0-US7]
- [ ] CHK004 Is the session token format and expiration behavior documented in the auth-api contract? [Completeness, Spec §FR-003, FR-019]
- [ ] CHK005 Are rate limiting or throttling requirements documented for login and registration endpoints? [Gap, Edge Case §concurrent login, rapid registration]

## Requirement Clarity

- [ ] CHK006 Is "non-blank" constraint for username/password fields quantified (e.g., whitespace-only handling)? [Clarity, Spec §FR-018, auth-api §login/register]
- [ ] CHK007 Are conditional field requirements in permission registration API clearly specified (urlPattern required for BASE, action required for BASE)? [Clarity, permission-api §register]
- [ ] CHK008 Is the distinction between "built-in" and "feature-registered" rights clearly documented with source identifiers? [Clarity, Spec §FR-025, FR-030, permission-api §rights]
- [ ] CHK009 Are the redirect behaviors for authenticated users accessing /login and /register explicitly defined in web-routes? [Clarity, web-routes §route map]

## Requirement Consistency

- [ ] CHK010 Do auth-api error codes align with the generic error message requirement (no field-specific leakage)? [Consistency, Spec §FR-004, auth-api §error responses]
- [ ] CHK011 Are role immutability rules consistent between spec (Admin role immutable), permission-api (403 ROLE_IMMUTABLE), and web-routes (admin-only access)? [Consistency, Spec §FR-021, permission-api §PUT roles]
- [ ] CHK012 Do web-route required rights match the built-in rights defined in spec (read:/profile, read:/permissions)? [Consistency, Spec §FR-025, web-routes §route map]
- [ ] CHK013 Is the UserPermissionsDto response structure consistent across all permission management endpoints that return it? [Consistency, permission-api §users]

## Acceptance Criteria Quality

- [ ] CHK014 Can "session expires after 7 days" be objectively verified from the auth-api contract (expiresAt field format)? [Measurability, Spec §FR-019, auth-api §login response]
- [ ] CHK015 Are success criteria for permission registration (idempotent upsert) measurable from the API contract response? [Measurability, Spec §FR-031, permission-api §register response]

## Scenario Coverage

- [ ] CHK016 Are requirements defined for session token validation failure scenarios (missing, invalid, expired)? [Coverage, auth-api §logout/me error responses]
- [ ] CHK017 Are requirements specified for role deletion with assigned users (reassignment to Visitor)? [Coverage, Spec §FR-026, permission-api §DELETE roles]
- [ ] CHK018 Are requirements defined for feature disable cascading (delete all permissions, unassign from roles/users)? [Coverage, Spec §FR-032, permission-api §POST features/disable]

## Edge Case Coverage

- [ ] CHK019 Is fallback behavior specified when a referenced right ID does not exist during role creation/update? [Edge Case, permission-api §POST/PUT roles error 404 RIGHT_NOT_FOUND]
- [ ] CHK020 Are requirements defined for duplicate permission registration (same name, different definition)? [Edge Case, Spec §FR-031, permission-api §register behavior]
- [ ] CHK021 Is the behavior specified when an admin attempts to delete the built-in Admin role or built-in rights? [Edge Case, Spec §FR-021, permission-api §DELETE roles/rights errors]

## Dependencies & Assumptions

- [ ] CHK022 Are TypeScript type generation requirements from Java DTOs documented for cross-module contract enforcement? [Dependency, Spec §api module, plan §typescript-generator]
- [ ] CHK023 Is the assumption of in-memory storage (no persistence) documented and its impact on session/role lifecycle acknowledged? [Assumption, plan §storage, Spec §assumptions]

## Ambiguities & Conflicts

- [ ] CHK024 Is the term "immediately available" for feature-registered permissions quantified (synchronous vs. eventual)? [Ambiguity, Spec §FR-030, permission-api §register behavior]
- [ ] CHK025 Are "effective rights" calculation rules unambiguous (union of role rights + individual rights, no precedence)? [Ambiguity, Spec §FR-008, permission-api §GET users response]

## Notes

- Check items off as completed: `[x]`
- Add comments or findings inline
- Items are numbered sequentially for easy reference
- This checklist validates contract quality, not implementation correctness
