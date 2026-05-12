# Issue: Create Frontend React Module with Vite and Login UI

**Status:** ready-for-agent

## Description

Create the `frontend` Maven module with React, Vite, and authentication UI components.

## Acceptance Criteria

- `frontend/pom.xml` with `frontend-maven-plugin`
- React application with Vite build tool
- Login form component with username/password
- Authentication context for state management
- Protected routes that redirect to login
- API client with JWT token interceptor

## Technical Notes

### Dependencies
- `react`, `react-dom`
- `react-router-dom`
- `axios`

### Components
- `Login.jsx` - Login form with validation
- `App.jsx` - Main app with routing
- `AuthContext.jsx` - Authentication state provider

### API Integration
- `api/auth.js` - Login/register API calls
- Axios interceptor adds `Authorization: Bearer <token>` header
- JWT token stored in `localStorage`

### Vite Configuration
- Dev server on port 3000
- Proxy `/api` to `http://localhost:8080`
