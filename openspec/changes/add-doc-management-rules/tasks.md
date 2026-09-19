# Tasks: add-doc-management-rules

## 1. Templates

- [x] 1.1 Create `doc/template/Doc-template.md` — base layout: header block (`Version: a.b.c`, `Updated: YYYY-MM-DD`), Purpose section, content section placeholders, Changelog table (`Version | Date | Change`). Verify: file exists and sections appear in that order.
- [x] 1.2 Create `doc/template/Frontend-feature-spec-template.md` — restore content from `git show HEAD~1:doc/spec-driven/Frontend-feature-spec-template.md`, adapted onto the base layout (header block + Changelog added; keeps Summary, Terminology, Biz requirements, Technical/Implement notes, Testing with UT-xx/SIT-xx case blocks). Verify: diff against the git original shows only layout adaptation, no lost sections.

## 2. Rules

- [x] 2.1 Create `doc/rules/Doc-manage.md` following the base template, containing: versioning rule (every doc has `a.b.c` version + Updated date + changelog table; bump semantics: major = restructure/semantic change, minor = added rules/sections, patch = wording/fixes); template rule (every doc based on a `doc/template/` template and follows its layout; new doc types require a new template via reviewed change); wording rules (lean/clean lines for human review; lists/tables and grouped content; serve the doc's purpose only — no unrelated or over-detailed info); index rule (any doc create/update touches its `doc/AGENTS.md` row in the same change); scoping rule (folder AGENTS.md applies to folder + sub-folders, `doc/` applies project-wide). Verify: the file itself carries version header `1.0.0` and a changelog row (dogfooding).

## 3. Entry points and index

- [x] 3.1 Fill root `AGENTS.md` (currently empty) — thin entry only: project doc scoping rule + pointer to `doc/AGENTS.md`. Verify: file is under ~10 lines and contains no doc content itself.
- [x] 3.2 Create `doc/AGENTS.md` — index listing rules and templates with their current versions (living-docs section added as docs get created later). Verify: every file under `doc/rules/` and `doc/template/` appears with correct version.

## 4. OpenSpec integration

- [x] 4.1 Add a `context:` entry to `openspec/config.yaml` — one pointer line: docs are governed by `doc/rules/Doc-manage.md`, templates in `doc/template/`. Verify: `openspec instructions proposal --change <any> --json` (or a dry read of config) shows the context is picked up; existing config comments stay intact.

## 5. Final verification

- [x] 5.1 Dogfooding pass — check every created doc (rules, both templates, index, root entry) against the base template: header block present where required, changelog present where required, index rows match actual files and versions. Verify: checklist pass with zero violations; report result in the change before archive.
