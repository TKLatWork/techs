# Issue: Write Backend Unit and Integration Tests

**Status:** ready-for-agent

## Description

Write comprehensive tests for the backend authentication flow and security configuration.

## Acceptance Criteria

- Unit tests for `JwtService` (token generation, parsing, validation, expiration)
- Unit tests for `UserService` (registration, login, duplicate user handling)
- Integration tests for `AuthController` endpoints
- Security tests verifying public vs protected endpoints

## Technical Notes

### Test Framework
- JUnit 5
- Mockito for mocking
- `@SpringBootTest` for integration tests
- `MockMvc` for controller tests

### Test Cases
- Register: success, duplicate username, invalid input
- Login: success, invalid credentials, missing fields
- JWT: valid token, expired token, malformed token
- Security: public endpoints accessible, protected endpoints require auth
