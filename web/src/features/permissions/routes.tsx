import { lazy } from "react";
import type { RouteObject } from "react-router-dom";
import { ProtectedRoute } from "../auth/components/ProtectedRoute";

const PermissionsPage = lazy(() => import("../../pages/PermissionsPage"));

export const routes: RouteObject[] = [
  {
    path: "/permissions",
    element: (
      <ProtectedRoute requiredRight="read:/permissions">
        <PermissionsPage />
      </ProtectedRoute>
    ),
  },
];
