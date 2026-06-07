import { lazy } from "react";
import type { RouteObject } from "react-router-dom";
import { ProtectedRoute } from "../auth/components/ProtectedRoute";

const ProfilePage = lazy(() => import("../../pages/ProfilePage"));

export const routes: RouteObject[] = [
  {
    path: "/profile",
    element: (
      <ProtectedRoute requiredRight="read:/profile">
        <ProfilePage />
      </ProtectedRoute>
    ),
  },
];
