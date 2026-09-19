# Proposal: add-doc-management-rules

## Why

The project's doc system was deliberately wiped (commit `df46548`) to be rebuilt as rule-driven, and the earlier `doc/spec-driven/` experiment (versioning, templates, wording rules) was deleted with it. Without doc-management rules in place first, every doc written next — by agents or humans — drifts: untracked versions, inconsistent layout, bloated wording. The rules and templates must exist before doc content is (re)created.

## What Changes

- Add **doc-management rules for agents**: `doc/rules/Doc-manage.md`
  - every doc carries a version `a.b.c` + `Updated` date in a header block and a changelog table; bump semantics defined (major = restructure/semantic change, minor = added rules/sections, patch = wording/fixes)
  - every doc is based on a defined template from `doc/template/` and follows its layout
  - wording rules: lean/clean lines for human review; lists/tables and grouped content; docs serve their purpose only — no unrelated or over-detailed info
- Add **templates**: `doc/template/`
  - `Doc-template.md` — base layout (header block, purpose, content sections, changelog)
  - `Frontend-feature-spec-template.md` — restored from git history (`HEAD~1:doc/spec-driven/`), refined to the base layout
- Re-seed **thin entry points**:
  - root `AGENTS.md` — doc scoping rule + pointer to `doc/AGENTS.md` (entry file is currently empty)
  - `doc/AGENTS.md` — index listing rules, templates, and living docs with their current versions
- Add **one pointer line** to `openspec/config.yaml` `context:` so OpenSpec artifacts are created under the doc rules
- **Not included** (deliberate, scope A): no project content docs (project-basic, coding rules) — those get written later, under these rules

## Capabilities

None — pure doc/process change, no system behavior changes. `skip_specs: true` is set in `.openspec.yaml`.

### New Capabilities

(none)

### Modified Capabilities

(none)

## Impact

- Files: root `AGENTS.md`, new `doc/` tree (`rules/`, `template/`, `AGENTS.md`), `openspec/config.yaml`
- No code, API, build, or dependency changes
- Governs all future doc creation and updates in this repo; the rules doc and templates themselves follow the template (dogfooding as acceptance)
