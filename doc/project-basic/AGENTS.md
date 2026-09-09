# Project basic
> Project Name: WebApp
> project use maven as build tool, starts with a pom.xml on root

# Spec/Doc support
> for all modules, create a doc folder under it and follow: [Spec](../spec-driven/AGENTS.md)

# Pattern
This project is multi-module maven. It have following module pattern:

## module: app-server
> Main server module
> Infar module, support and host service-* and app-web modules

## module: service-<Service-Name>
> backend service module

## module: app-web
> frontend web module, single SPA hosting multiple features
> shared infra under `src/app`, feature code under `src/features/<feature-name>`
> each feature is a client route at `/web/<feature-name>`

## URL convention
> service-* api under `/api/<service-name>/...`
> app-web feature pages under `/web/<feature-name>`
> root `/` links to a feature page, e.g. `/web/timezone`

## Hosting contract
> web module = assets-only jar: built SPA under `static/web/` at jar root, `index.html` as entry, no Java code (config/resource files at most)
> app-server hosts web modules: serves `/web/**` from `classpath:/static/web/` (classloader lookup, resolves assets from any web-module jar on the classpath)
> SPA fallback: empty or extension-less path -> `static/web/index.html`; path with extension -> real file or 404
> app-server owns the root `/` redirect to the default feature route

# Tech Spec

## Maven:
> Require maven 3.9, don't use mvnw file

## Java and spring boot
>JDK 21 + spring boot web

## Web nodejs and react
>node v22+
>react with Vite and typescript
>MUI for UI components
>axios for http calls
>react-router for SPA routing

# Build and Run

## Build
> `mvn clean package` on root
> Node is downloaded automatically by the build (v22 pinned)

## Run server
> `mvn spring-boot:run -pl app-server`
> Root `/` links to a feature page, e.g. http://localhost:8080/ -> /web/timezone
> API under `/api/<service-name>/...`, e.g. http://localhost:8080/api/timezone/zones

## Frontend dev mode
> in app-web module: `npm install` then `npm run dev`
> dev server on http://localhost:5173, feature page at /web/<feature-name>, e.g. http://localhost:5173/web/timezone
> proxies `/api` to http://localhost:8080 (requires app-server running)

# Testing pattern

## Terms
>Unit test: test that need no external dependency to run, on the module it's in, can use mocking.
>SIT: test that have external dependency to run. For maven, should only run when maven profile sit are enabled.

## Backend Server/Service test
> service-<service-name> should have their test code in service module, and should be Unit test cases.
> service's SIT cases should be in service's module, but should be run within app-server, with maven -P sit.
> Spring boot test/wiremock/Restassured libs are go to choice.

## Frontend web test
> Playwright libs are go to choice.
