import { http } from '../http'

export interface AuthUser {
  username: string
}

export interface AuthService {
  login(username: string, password: string): Promise<AuthUser>
  logout(): Promise<void>
  restore(): AuthUser | null
}

const TOKEN_KEY = 'auth.token'
const USER_KEY = 'auth.username'

export function createHttpAuthService(): AuthService {
  const applyToken = (token: string | null) => {
    if (token) {
      http.defaults.headers.common.Authorization = `Bearer ${token}`
    } else {
      delete http.defaults.headers.common.Authorization
    }
  }

  applyToken(sessionStorage.getItem(TOKEN_KEY))

  return {
    login: async (username, password) => {
      const { data } = await http.post<{ username: string; token: string }>('/api/auth/login', {
        username,
        password,
      })
      sessionStorage.setItem(TOKEN_KEY, data.token)
      sessionStorage.setItem(USER_KEY, data.username)
      applyToken(data.token)
      return { username: data.username }
    },
    logout: async () => {
      sessionStorage.removeItem(TOKEN_KEY)
      sessionStorage.removeItem(USER_KEY)
      applyToken(null)
    },
    restore: () => {
      const username = sessionStorage.getItem(USER_KEY)
      return username ? { username } : null
    },
  }
}
