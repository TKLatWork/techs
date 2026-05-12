# PRD: Multi-Module Maven Project with React Web + Spring Boot Backend and User Login

## Problem Statement

As a developer, I need a foundational multi-module Maven project that combines a React web frontend with a Spring Boot backend, including user authentication functionality. Currently, there is no project structure in place, and I need a ready-to-use scaffold that supports user registration, login, and protected API access from the web interface.

## Solution

Create a multi-module Maven project named `myapp` with two modules:
- **backend**: A Spring Boot 3.2.x REST API with JWT-based authentication, H2 in-memory database, and Spring Security
- **frontend**: A React application built with Vite that provides a login UI, authentication state management, and protected routes

The frontend will communicate with the backend API for user registration, login, and authenticated requests using JWT tokens.

## User Stories

1. As a new user, I want to register with a username and password, so that I can create an account
2. As a registered user, I want to login with my credentials, so that I can access the application
3. As a logged-in user, I want to see my profile information, so that I can verify my account details
4. As a user, I want to be redirected to the login page when accessing protected routes without authentication, so that I know I need to sign in
5. As a user, I want to stay logged in across page refreshes, so that I don't have to re-enter credentials repeatedly
6. As a user, I want to see validation errors when I enter invalid credentials, so that I can correct my input
7. As a user, I want to see a loading state during login, so that I know the request is being processed
8. As a developer, I want to run the backend and frontend independently during development, so that I can test changes quickly
9. As a developer, I want to build the entire project with a single Maven command, so that I can streamline CI/CD
10. As a developer, I want to access the H2 console for debugging, so that I can inspect the database state

## Implementation Decisions

### Modules

1. **Parent POM (`myapp`)**
   - Packaging: `pom`
   - Modules: `backend`, `frontend`
   - Inherits from `spring-boot-starter-parent` 3.2.x
   - Manages Java 21 toolchain

2. **Backend Module**
   - Spring Boot 3.2.x with Spring Security, Spring Data JPA, and Spring Web
   - JWT authentication using `jjwt` library (io.jsonwebtoken)
   - H2 in-memory database for development and testing
   - Password encoding with BCrypt
   - Package structure: `com.myapp.backend`
   - Deep modules:
     - `JwtService` - Encapsulates JWT token generation, parsing, and validation
     - `UserService` - Encapsulates user registration, authentication, and retrieval

3. **Frontend Module**
   - React 18 with Vite for fast development and builds
   - `frontend-maven-plugin` to manage Node.js and npm through Maven
   - `axios` for HTTP requests with JWT interceptor
   - `react-router-dom` for routing with protected routes
   - Authentication state managed via React Context (`AuthContext`)
   - JWT token stored in `localStorage`

### API Contracts

```
POST /api/auth/register
Request: { "username": "string", "password": "string" }
Response 200: { "message": "User registered successfully", "token": "string" }
Response 400: { "error": "Username already exists" }

POST /api/auth/login
Request: { "username": "string", "password": "string" }
Response 200: { "token": "string", "username": "string" }
Response 401: { "error": "Invalid credentials" }

GET /api/user/profile
Header: Authorization: Bearer <token>
Response 200: { "username": "string", "roles": ["string"] }
Response 401: { "error": "Unauthorized" }

GET /api/health
Response 200: { "status": "UP" }
```

### Security Configuration

- Spring Security configured with stateless session management
- JWT filter added before `UsernamePasswordAuthenticationFilter`
- Public endpoints: `/api/auth/**`, `/h2-console/**`, `/api/health`
- All other endpoints require authentication
- CORS enabled for `http://localhost:3000` (frontend dev server)

### Database Schema

```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    roles VARCHAR(255) DEFAULT 'ROLE_USER'
);
```

### Frontend Architecture

- `AuthContext` provides: `isAuthenticated`, `login()`, `logout()`, `user`
- Protected route component wraps routes requiring authentication
- Axios interceptor automatically adds `Authorization` header with JWT token
- Login form with username/password fields and submit handler

## Testing Decisions

### What Makes a Good Test

- Tests should verify external behavior, not implementation details
- Tests should be independent and not rely on external state
- Tests should cover happy paths and error cases

### Modules to Test

1. **Backend**
   - `JwtService` - Test token generation, parsing, expiration, and validation
   - `UserService` - Test registration (duplicate user), login (valid/invalid credentials)
   - `AuthController` - Integration tests with `@SpringBootTest` for register/login endpoints
   - Security configuration - Test that public endpoints are accessible and protected endpoints require authentication

2. **Frontend**
   - `Login` component - Test form submission, error display, and loading state
   - `AuthContext` - Test login/logout state changes
   - API module - Mock axios responses to test error handling

### Prior Art

This is a new project, so tests will follow Spring Boot and React testing best practices:
- Backend: JUnit 5, Mockito, `@SpringBootTest`, `MockMvc`
- Frontend: Vitest (via Vite), React Testing Library

## Out of Scope

- Password reset functionality
- Email verification
- OAuth2 / social login (Google, GitHub, etc.)
- Role-based access control beyond basic `ROLE_USER`
- Production-grade database (PostgreSQL, MySQL)
- Docker containerization
- CI/CD pipeline configuration
- User profile editing
- Session management / logout invalidation on server

## Further Notes

- The JWT secret should be externalized to environment variables for production use
- The H2 console is enabled only for development; it should be disabled in production
- The `frontend-maven-plugin` ensures consistent Node.js versions across environments
- The project can be extended with additional modules (e.g., `common`, `api-client`) as needed
- Consider adding Swagger/OpenAPI documentation for the backend API in future iterations
