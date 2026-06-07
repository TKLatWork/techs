import { useAuthContext } from "./AuthContext";
import type { LoginRequest } from "../types/auth";

export function useAuth() {
  const { user, isAuthenticated, login, logout } = useAuthContext();

  return {
    user,
    isAuthenticated,
    login: (request: LoginRequest) => login(request),
    logout,
  };
}
