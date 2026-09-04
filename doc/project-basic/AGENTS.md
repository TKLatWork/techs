# Project basic
> Project Name: WebApp
> project use maven as build tool, starts with a pom.xml on root

# Pattern
This project is multi-module maven. It have following module pattern:

## module: app-server
> Main server module
> Infar module, support and host service-* and web-* modules

## module: service-<Service-Name>
> backend service module

## module: web-<Service-Name>
> frontend web module

## URL convention
> service-* api under `/api/<service-name>/...`
> web-* pages under `/web/<web-module-name>`
> root `/` links to a web module, e.g. `/web/timezone`

# Tech Spec

## Maven:
> Require maven 3.9, don't use mvnw file

## Java and spring boot
>JDK 21 + spring boot web

## Web nodejs and react
>node v22+
>react with Vite and typescript

# Build and Run

## Build
> `mvn clean package` on root
> Node is downloaded automatically by the build (v22 pinned)

## Run server
> `mvn spring-boot:run -pl app-server`
> Root `/` links to `/web/<web-module-name>`, e.g. http://localhost:8080/ -> /web/timezone
> API under `/api/<service-name>/...`, e.g. http://localhost:8080/api/timezone/zones

## Frontend dev mode
> in web-* module: `npm install` then `npm run dev`
> dev server on http://localhost:5173, proxies `/api` to http://localhost:8080 (requires app-server running)


