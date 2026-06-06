# Web Route Contracts

**Feature**: 004-user-auth-access-control
**Date**: 2026-06-07

## Route Map

| Path | Page Component | Feature | Auth Required | Required Right | Description |
|------|---------------|---------|---------------|----------------|-------------|
| `/` | HomePage | — | No | — | Landing page; redirects to `/profile` if authenticated |
| `/login` | LoginPage | auth | No | — | Login form; redirects to `/` if already authenticated |
| `/register` | RegisterPage | auth | No | — | Registration form; redirects to `/` if already authenticated |
| `/profile` | ProfilePage | profile | Yes | `read:/profile` | User profile display |
| `/permissions` | PermissionsPage | permissions | Yes | `read:/permissions` | Permission management (admin only) |
| `*` | NotFoundPage | — | No | — | 404 fallback page |

## Route Registration by Feature

Each feature defines its routes in `features/<name>/routes.tsx`:

### features/auth/routes.tsx

```typescript
export const routes = [
  { path: "/login", element: lazy(() => import("../../pages/LoginPage")) },
  { path: "/register", element: lazy(() => import("../../pages/RegisterPage")) },
];
```

### features/profile/routes.tsx

```typescript
export const routes = [
  {
    path: "/profile",
    element: lazy(() => import("../../pages/ProfilePage")),
    requiredRight: "read:/profile",
  },
];
```

### features/permissions/routes.tsx

```typescript
export const routes = [
  {
    path: "/permissions",
    element: lazy(() => import("../../pages/PermissionsPage")),
    requiredRight: "read:/permissions",
  },
];
```

## Route Guard Behavior

The `ProtectedRoute` component (in `features/auth/components/`) wraps protected routes:

1. **Unauthenticated user** → Redirect to `/login`
2. **Authenticated user lacking required right** → Render "Access Denied" page
3. **Authenticated user with required right** → Render the page component

## Auto-Discovery Mechanism

The `app/featureRegistry.ts` uses Vite's `import.meta.glob`:

```typescript
const featureModules = import.meta.glob("../features/*/routes.tsx", { eager: true });
```

This collects all `routes` exports from every feature. The `App.tsx` renders them inside `<Routes>` wrapped with `<ProtectedRoute>` for routes that specify `requiredRight`.

## Frontend Authorization Model

### Base Right Enforcement (Route-Level)

Route guards check base rights (`read:/path`) before rendering a page. This is the first layer of defense.

### Feature Right Enforcement (Component-Level)

Within a page, feature rights are checked to show/hide specific functionality:

```typescript
const { hasRight } = usePermission();

{hasRight("manage_users") && <Button>Manage Users</Button>}
{hasRight("export_data") && <Button>Export</Button>}
```

The `usePermission` hook reads the user's effective rights from the auth context and provides a `hasRight(rightName: string): boolean` function.

## Navigation Flow

### Unauthenticated User

```
Any protected URL → Redirect to /login
/login → Login form
/register → Registration form
```

### Authenticated User (Visitor)

```
/login → Redirect to /
/register → Redirect to /
/ → HomePage
/profile → ProfilePage (read-only)
/permissions → Access Denied
```

### Authenticated User (User)

```
/login → Redirect to /
/register → Redirect to /
/ → HomePage
/profile → ProfilePage (full access)
/permissions → Access Denied
```

### Authenticated User (Admin)

```
/login → Redirect to /
/register → Redirect to /
/ → HomePage
/profile → ProfilePage (full access)
/permissions → PermissionsPage (full access)
```
