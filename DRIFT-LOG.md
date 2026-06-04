# Drift Log

> Documents conscious deviations from canonical specifications.  
> Every `// DRIFT:` comment in code MUST have a matching entry here.

---

## Active Drift

| ID | File | Line | Canonical Doc | Deviation | Reason | Date |
|----|------|------|--------------|-----------|--------|------|
| D-001 | specs/002-project-structure/spec.md | N/A | ARCHITECTURE.md | Added 4th module (api); changed dependency graph from `web→domain, app→domain` to `web→api→domain←app` | Web (npm/React) cannot directly depend on domain (Java/Maven) due to ecosystem differences; api module bridges the gap | 2026-05-31 |
| D-002 | specs/002-project-structure/data-model.md | N/A | DATA-MODEL.md | Module type enum extended with API value; new api module entity with typescript-generator config | API module needed as contract layer between Java backend and TypeScript frontend | 2026-05-31 |

## Resolved Drift

| ID | Resolution | Date |
|----|-----------|------|
| <!-- D-000 --> | <!-- How it was resolved --> | <!-- 2026-05-31 --> |
