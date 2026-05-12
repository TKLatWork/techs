# Issue: Create Backend Spring Boot Module with JWT Authentication

**Status:** ready-for-agent

## Description

Create the `backend` Maven module with Spring Boot, Spring Security, JWT authentication, and H2 database.

## Acceptance Criteria

- `backend/pom.xml` with required dependencies
- Spring Security configured with JWT filter chain
- JWT token generation and validation service
- User registration and login endpoints
- H2 in-memory database configured
- Password encoding with BCrypt

## Technical Notes

### Dependencies
- `spring-boot-starter-web`
- `spring-boot-starter-security`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-validation`
- `jjwt-api`, `jjwt-impl`, `jjwt-jackson`
- `h2`
- `spring-boot-starter-test`

### API Endpoints
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Authenticate user
- `GET /api/user/profile` - Get authenticated user profile
- `GET /api/health` - Health check

### Package Structure
- `com.myapp.backend.config` - Security and JWT configuration
- `com.myapp.backend.controller` - REST controllers
- `com.myapp.backend.model` - JPA entities
- `com.myapp.backend.repository` - Spring Data repositories
- `com.myapp.backend.service` - Business logic
- `com.myapp.backend.security` - JWT filter and entry point
