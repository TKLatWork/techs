import { vi } from 'vitest'
import type { AuthService, AuthUser } from '../app/auth/authService'

export function mockAuthService(user: AuthUser | null = null): AuthService {
  return {
    login: vi.fn(async (username: string) => ({ username })),
    logout: vi.fn(async () => {}),
    restore: vi.fn(() => user),
  }
}
