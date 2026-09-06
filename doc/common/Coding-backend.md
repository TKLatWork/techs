# Coding backend
> Backend coding rules for service-* and app-server modules (JDK 21 + Spring Boot)

## Rule: Records for API DTOs
> Request/response payloads are Java `record` types, not `Map` or mutable beans.

Why: immutable, terse, Jackson-ready, no getter/setter boilerplate; the shape of the API is visible in one line.

```java
public record ConvertResponse(String fromZone, String toZone, String converted) {
}
```

## Rule: URL convention
> APIs under `/api/<service-name>/...`, see [project-basic](../project-basic/AGENTS.md).

Why: service name in the path keeps modules isolated behind one host; no route collisions between service-* modules.
