import { createContext, useContext, useState } from 'react'
import type { ReactNode } from 'react'
import { createHttpAuthService } from './authService'
import type { AuthService, AuthUser } from './authService'

export interface AuthState {
  user: AuthUser | null
  service: AuthService
  login(username: string, password: string): Promise<void>
  logout(): Promise<void>
}

const AuthContext = createContext<AuthState | null>(null)

export function AuthProvider({ service, children }: { service?: AuthService; children: ReactNode }) {
  const [authService] = useState(() => service ?? createHttpAuthService())
  const [user, setUser] = useState<AuthUser | null>(() => authService.restore())

  const login = async (username: string, password: string) => {
    setUser(await authService.login(username, password))
  }

  const logout = async () => {
    await authService.logout()
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, service: authService, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth(): AuthState {
  const state = useContext(AuthContext)
  if (!state) throw new Error('AuthProvider is missing')
  return state
}
