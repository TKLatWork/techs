export interface Right {
  id: string;
  name: string;
  type: "BASE" | "FEATURE";
  urlPattern?: string;
  action?: string;
  description?: string;
  source?: string;
  featureId?: string;
}

export interface User {
  id: string;
  username: string;
  displayName: string;
  roleName: string;
  createdAt: string;
  effectiveRights: Right[];
  individualRights: Right[];
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  expiresAt: string;
  user: User;
}

export interface RegisterRequest {
  username: string;
  password: string;
  displayName: string;
}

export interface RegisterResponse {
  user: User;
}

export interface ErrorResponse {
  message: string;
  code: string;
  timestamp: string;
}
