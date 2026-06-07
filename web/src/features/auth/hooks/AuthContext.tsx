import { createContext, useContext, useState, useCallback, useEffect, type ReactNode } from "react";
import type { User, LoginRequest, LoginResponse } from "../types/auth";
import { apiClient } from "../../../shared/services/apiClient";

interface AuthContextType {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  effectiveRights: string[];
  login: (request: LoginRequest) => Promise<void>;
  logout: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(localStorage.getItem("auth_token"));

  const effectiveRights = user?.effectiveRights.map((r) => r.name) ?? [];

  const login = useCallback(async (request: LoginRequest) => {
    const { data } = await apiClient.post<LoginResponse>("/api/auth/login", request);
    localStorage.setItem("auth_token", data.token);
    setToken(data.token);
    setUser(data.user);
  }, []);

  const logout = useCallback(async () => {
    try {
      await apiClient.post("/api/auth/logout");
    } finally {
      localStorage.removeItem("auth_token");
      setToken(null);
      setUser(null);
    }
  }, []);

  useEffect(() => {
    if (token && !user) {
      apiClient
        .get<User>("/api/auth/me")
        .then(({ data }) => setUser(data))
        .catch(() => {
          localStorage.removeItem("auth_token");
          setToken(null);
        });
    }
  }, [token, user]);

  return (
    <AuthContext.Provider
      value={{ user, token, isAuthenticated: !!token && !!user, effectiveRights, login, logout }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuthContext() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuthContext must be used within AuthProvider");
  }
  return context;
}
