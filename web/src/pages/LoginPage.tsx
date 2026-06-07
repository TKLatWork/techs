import { Navigate } from "react-router-dom";
import { useAuthContext } from "../features/auth/hooks/AuthContext";
import { LoginForm } from "../features/auth/components/LoginForm";
import { Box, Link as MuiLink } from "@mui/material";
import { Link } from "react-router-dom";

export default function LoginPage() {
  const { isAuthenticated } = useAuthContext();

  if (isAuthenticated) {
    return <Navigate to="/" replace />;
  }

  return (
    <Box>
      <LoginForm />
      <Box sx={{ textAlign: "center", mt: 2 }}>
        <MuiLink component={Link} to="/register">
          Don't have an account? Register
        </MuiLink>
      </Box>
    </Box>
  );
}
