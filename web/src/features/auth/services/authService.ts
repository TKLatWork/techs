import { apiClient } from "../../../shared/services/apiClient";
import type { LoginRequest, LoginResponse, RegisterRequest, RegisterResponse } from "../types/auth";

export const authService = {
  login: (request: LoginRequest) => apiClient.post<LoginResponse>("/api/auth/login", request),
  register: (request: RegisterRequest) => apiClient.post<RegisterResponse>("/api/auth/register", request),
  logout: () => apiClient.post<void>("/api/auth/logout"),
};
