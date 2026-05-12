# Issue: Write Frontend Component Tests

**Status:** ready-for-agent

## Description

Write tests for the frontend React components and authentication flow.

## Acceptance Criteria

- Tests for `Login` component (form submission, error display, loading state)
- Tests for `AuthContext` (login/logout state changes)
- Tests for API module (mocked responses, error handling)

## Technical Notes

### Test Framework
- Vitest (via Vite)
- React Testing Library

### Test Cases
- Login form: renders correctly, submits with valid data, shows errors for invalid credentials
- AuthContext: initializes unauthenticated, updates on login, clears on logout
- API client: attaches token to requests, handles 401 errors
