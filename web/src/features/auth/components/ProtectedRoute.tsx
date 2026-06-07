import { Navigate } from "react-router-dom";
import { Typography, Box } from "@mui/material";
import { useAuthContext } from "../hooks/AuthContext";
import { usePermission } from "../hooks/usePermission";
import type { ReactNode } from "react";

interface ProtectedRouteProps {
  children: ReactNode;
  requiredRight?: string;
}

export function ProtectedRoute({ children, requiredRight }: ProtectedRouteProps) {
  const { isAuthenticated } = useAuthContext();
  const { hasRight } = usePermission();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (requiredRight && !hasRight(requiredRight)) {
    return (
      <Box sx={{ p: 3, textAlign: "center" }}>
        <Typography variant="h2">Access Denied</Typography>
        <Typography variant="body1" sx={{ mt: 2 }}>
          You do not have permission to access this page.
        </Typography>
      </Box>
    );
  }

  return <>{children}</>;
}
