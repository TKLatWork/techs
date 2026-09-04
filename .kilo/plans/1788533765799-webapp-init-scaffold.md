# Plan: Init WebApp project scaffold

## Goal
Scaffold the "WebApp" multi-module Maven project defined in `doc/project-basic/AGENTS.md`: JDK 21 + Spring Boot backend, React/Vite/TS frontend, demonstrating the full module pattern (`app-server` + `service-*` + `web-*`).

## Decisions (confirmed with user)
- Modules: `app-server`, `service-hello`, `web-hello` (full pattern demo)
- GroupId / base package: `me.webapp`
- Hosting model: `app-server` is the single runnable Spring Boot app; `service-*` are plain library jars (controllers/services, no main class); `web-*` jars carry built static assets
- Web build: `frontend-maven-plugin` runs npm install/build inside `web-*`; Node 22 pinned in plugin; npm as package manager
- Maven wrapper (`mvnw`) committed
- Spring Boot 3.5.x (latest stable), spring-boot-starter-web
- Known doc typo to fix: `doc/project-basic/AGENTS.md:16` describes `web-*` as "backend service module" — should be frontend module

## Tasks

### 1. Root Maven setup
- Create root `pom.xml`: groupId `me.webapp`, artifactId `webapp`, packaging `pom`, Java 21 (`maven.compiler.release=21`), modules `app-server`, `service-hello`, `web-hello`
- Manage Spring Boot via `spring-boot-starter-parent` 3.5.x or dependencyManagement BOM in root pom
- Add Maven wrapper (`mvnw`, `mvnw.cmd`, `.mvn/wrapper`) via `mvn wrapper:wrapper`
- Add root `.gitignore`: `target/`, `node_modules/`, `dist/`, IDE files (`.idea/`, `*.iml`, `.vscode/`)
- Add root `README.md`: project name, module map, build/run commands

### 2. service-hello module
- `service-hello/pom.xml`: jar packaging, depends on `spring-boot-starter-web` (no boot plugin)
- Package `me.webapp.service.hello`:
  - `HelloController`: `GET /api/hello` returning JSON `{ "message": "Hello from WebApp" }` (use a small record/model, not raw Map)

### 3. web-hello module
- `web-hello/pom.xml`: jar packaging with `frontend-maven-plugin`:
  - `install-node-and-npm` goal pinning Node v22
  - `npm install` and `npm run build` goals
  - Include Vite `dist/` output into the jar under `static/` (resources configuration or maven-resources-plugin copy to `target/classes/static`)
- Vite + React + TypeScript app (`package.json`, `vite.config.ts`, `tsconfig.json`, `index.html`, `src/`):
  - Dev proxy `/api` -> `http://localhost:8080` in `vite.config.ts`
  - `src/App.tsx`: fetches `GET /api/hello` and renders the message
- `web-hello/.gitignore`: `node_modules/`, `dist/`

### 4. app-server module
- `app-server/pom.xml`: depends on `me.webapp:service-hello` and `me.webapp:web-hello`; applies `spring-boot-maven-plugin` (repackage) — only runnable module
- Package `me.webapp.server`:
  - `Application.java`: `@SpringBootApplication(scanBasePackages = "me.webapp")` so controllers in service jars are picked up
  - `application.yml`: port 8080
- Static assets from `web-hello` jar served at `/` via Spring Boot default static resource handling

### 5. Doc fixes
- Fix `doc/project-basic/AGENTS.md:16`: `web-<Service-Name>` description -> frontend web module
- Optionally extend `doc/AGENTS.md` index with a link to `project-basic`

### 6. Git
- Stage all new files plus the already-deleted old files (old repo content removed from worktree) and commit as the init commit, e.g. `init: scaffold WebApp multi-module maven project`

## Validation
1. `mvnw.cmd clean package` succeeds (builds all modules incl. npm build of web-hello)
2. `mvnw.cmd spring-boot:run -pl app-server` starts on 8080
3. `curl http://localhost:8080/api/hello` returns the JSON message
4. `http://localhost:8080/` serves the React page showing the fetched message
5. Dev mode: `npm run dev` in `web-hello` proxies `/api` to app-server

## Risks / notes
- `frontend-maven-plugin` downloads Node; needs network on first build
- `scanBasePackages = "me.webapp"` in app-server is required since controllers live outside its own package
- Vite `base` may need setting if assets are served from jar root; verify `/assets/*` paths resolve when served by Spring

## Out of scope
- CI/CD, Docker, tests beyond smoke validation, additional service/web modules
