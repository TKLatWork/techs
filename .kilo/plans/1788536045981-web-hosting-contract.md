# Plan: Web hosting contract — assets-only web module, hosting code in app-server

## Goal
Define an explicit contract between web module(s) and `app-server` for URL handling/hosting, move the implementing Java code out of `app-web` into `app-server`, and document the contract alongside the existing rules in `doc/project-basic/AGENTS.md`.

## Current state
- `app-web/src/main/java/me/webapp/web/SpaWebConfig.java` is the only Java code in the web module; it registers `/web/**` → `classpath:/static/web/` with SPA fallback (extension-less path → `index.html`)
- `app-web/pom.xml` carries `spring-boot-starter-web` solely to compile that class
- `app-web` build already copies Vite `dist/` into the jar at `static/web/` (maven-resources-plugin, `prepare-package`)
- `app-server` already has `RootWebConfig` (`/` → redirect `/web/timezone`) and scans `me.webapp` (`scanBasePackages`)
- Docs already define URL convention (`/api/<service-name>/...`, `/web/<feature-name>`) in `doc/project-basic/AGENTS.md`

## Contract (decisions)
- **Mechanism: documented convention, no descriptor file.** With a single `app-web` module in the pattern, a machine-readable descriptor (e.g. `web-module.yml` per jar) adds machinery with no consumer. The jar layout IS the contract. (Descriptor-based discovery is out of scope until multiple web modules exist.)
- **Web module side** (`app-web` and any future web module):
  - Assets-only jar: built SPA packaged under `static/web/` at jar root, `index.html` as SPA entry
  - No Java code; at most resource/config files (pom, vite config, package.json, static assets)
- **app-server side** (host implements serving):
  - Serves `/web/**` from `classpath:/static/web/` (classloader lookup resolves assets from any web-module jar on the classpath; `classpath*:` is NOT supported by `ResourceHttpRequestHandler` — location values resolve via `ResourceLoader.getResource`, yielding a non-existent `ServletContextResource` and 404ing all extension paths)
  - SPA fallback: empty or extension-less path → `static/web/index.html`; paths with extension → real file or 404 (prevents assets 404ing as HTML 200)
  - Root `/` redirect to the default feature route stays in app-server (`RootWebConfig`, unchanged)

## Tasks

### 1. app-server: implement the contract
- Create `app-server/src/main/java/me/webapp/server/SpaWebConfig.java` — move of the current class, package `me.webapp.server`, logic identical except:
  - `addResourceLocations("classpath:/static/web/")`
  - keep `resourceChain(true)` + `PathResourceResolver` override (empty/extension-less → `ClassPathResource("static/web/index.html")` if exists, else null; otherwise `location.createRelative(path)` if exists+readable, else null)
- `RootWebConfig` untouched

### 2. app-web: strip Java
- Delete `app-web/src/main/java/` entirely (removes `me.webapp.web.SpaWebConfig`)
- `app-web/pom.xml`: remove the `spring-boot-starter-web` dependency (whole `<dependencies>` block — it has no other dependency)
- No frontend changes; resources copy to `static/web` stays as-is

### 3. Docs: record the contract
- `doc/project-basic/AGENTS.md`: add a `## Hosting contract` section under `# Pattern` (after URL convention), in the existing `>` quote style, stating:
  - web module = assets-only jar: built SPA under `static/web/`, `index.html` entry, no Java code (config/resource files at most)
  - app-server hosts: serves `/web/**` from `classpath*:/static/web/` with SPA fallback (extension-less → index.html), and owns the root `/` redirect
- `doc/common/Coding-frontend.md`: no change (pure frontend rules, unaffected)

## Validation
1. `mvn -B clean install` — BUILD SUCCESS; `app-web` jar contains `static/web/index.html` + `static/web/assets/*` and **no** `.class` files; app-web compiles without spring dependency
2. Run `java -jar app-server\target\app-server-0.1.0-SNAPSHOT.jar` (background), then:
   - `GET /` → 302 to `/web/timezone`
   - `GET /web/timezone` → 200, SPA `index.html`
   - `GET /web/assets/index-*.js` → 200
   - `GET /web/some/unknown/route` → 200 (SPA fallback), `GET /web/assets/missing.js` → 404
   - `GET /api/timezone/convert?from=Asia/Shanghai&to=Europe/Paris&dateTime=2026-09-04T16:00` → correct JSON
3. Stop the server after validation (avoid jar lock on next build)

## Risks / notes
- `classpath*:` verified broken for resource-handler locations (all extension assets 404); single-colon `classpath:` + `PathResourceResolver` fallback works and already resolves across nested jars
- m2e lifecycle-mapping (frontend plugin ignore) unaffected; VS Code Java may need a project reload after app-web loses its Java nature
- Git: deletions/additions left uncommitted unless commit is requested

## Out of scope
- Descriptor-file-based web module discovery (revisit when >1 web module exists)
- Multiple web modules, per-feature backends for web modules
- Frontend code changes, router changes, CI
