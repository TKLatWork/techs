# Security

<!-- docguard:version 0.2.3 -->
<!-- docguard:status active -->
<!-- docguard:last-reviewed 2026-05-31 -->
<!-- docguard:owner @project-maintainer -->
<!-- docguard:quality negation-load off — security rules inherently use prohibitions -->

> **Canonical document** — Design intent. This file defines the security model.
> Last updated: 2026-05-31

| Metadata | Value |
|----------|-------|
| **Status** | ![Status](https://img.shields.io/badge/status-active-green) |
| **Version** | `0.2.3` |
| **Last Updated** | 2026-05-31 |
| **Owner** | @project-maintainer |

---

## Authentication

The project currently operates without authentication. The team MUST define an authentication mechanism as part of the first API feature requiring user identity.

| Method | Implementation | Details |
|--------|---------------|---------|
| TBD | TBD | Define alongside first user-facing API feature |

## Authorization

The project currently operates without role-based access control. The team MUST define roles and permissions as part of the first feature requiring access control.

| Role | Permissions | Scope |
|------|------------|-------|
| TBD | TBD | Define alongside first access control feature |

## Secrets Management

The project currently requires no secrets. The following rules apply to all future secrets:

| Secret | Storage | Access Pattern |
|--------|---------|---------------|
| Database credentials | Environment variables (.env) | Load at application startup via Spring Boot config |
| API keys | Environment variables (.env) | Load at application startup |

## Security Rules

- All API routes MUST require authentication (health check endpoints remain public)
- Keep secrets out of code, logs, and error messages at all times
- Validate all user input before processing
- Mask PII (email, phone, name) in logs
- List `.env` files in `.gitignore` and keep them out of version control
- Scan all dependencies for known vulnerabilities before inclusion

## .gitignore Audit

Exclude the following sensitive patterns from version control:

- `.env`, `.env.*` (environment variable files)
- `**/target/` (Java build output)
- `**/node_modules/` (Node.js dependencies)
- `**/*.log` (log files that may contain sensitive data)

---

## Revision History

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 0.1.0 | 2026-05-31 | DocGuard Init | Initial template |
| 0.2.0 | 2026-05-31 | Constitution update | Filled placeholders with project security posture |
| 0.2.1 | 2026-05-31 | Doc quality fix | Rewrote to active voice, added negation-load override |
| 0.2.2 | 2026-05-31 | Doc quality fix | Added conditional-load override |
| 0.2.3 | 2026-05-31 | Doc quality fix | Rewrote conditional sentences to declarative form |
