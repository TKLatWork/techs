# WebApp

Multi-module Maven project: JDK 21 + Spring Boot backend, React + Vite + TypeScript frontend.

## Modules

| Module | Role |
|--------|------|
| `app-server` | Main server module. The only runnable module (Spring Boot app), hosts all `service-*` and `web-*` modules. |
| `service-hello` | Backend service module. Plain library jar with controllers/services, no main class. |
| `web-hello` | Frontend web module. React/Vite/TS app, built static assets packaged into a jar under `static/`. |

## Requirements

- JDK 21+
- Maven wrapper included (`mvnw` / `mvnw.cmd`); Node is downloaded automatically by the build (v22 pinned)

## Build

```
mvnw.cmd clean package
```

## Run

```
mvnw.cmd spring-boot:run -pl app-server
```

- App: http://localhost:8080/
- API: http://localhost:8080/api/hello

## Frontend dev mode

```
cd web-hello
npm install
npm run dev
```

Dev server runs on http://localhost:5173 and proxies `/api` to `http://localhost:8080` (requires `app-server` running).
