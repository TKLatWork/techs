import { Navigate } from "react-router-dom";
import { useAuthContext } from "../features/auth/hooks/AuthContext";
import { RegisterForm } from "../features/auth/components/RegisterForm";

export default function RegisterPage() {
  const { isAuthenticated } = useAuthContext();

  if (isAuthenticated) {
    return <Navigate to="/" replace />;
  }

  return <RegisterForm />;
}
