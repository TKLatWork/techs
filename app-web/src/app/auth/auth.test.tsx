import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { describe, expect, it, vi } from 'vitest'
import { AuthProvider } from './AuthProvider'
import RequireAuth from './RequireAuth'
import LoginPage from './LoginPage'
import NavBar from '../components/NavBar'
import { mockAuthService } from '../../test/mockAuthService'
import type { AuthUser } from './authService'
import type { ReactNode } from 'react'

function renderAtLogin(user: AuthUser | null = null) {
  const service = mockAuthService(user)
  const result = render(
    <MemoryRouter initialEntries={['/login']}>
      <AuthProvider service={service}>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/" element={<div>home-page</div>} />
        </Routes>
      </AuthProvider>
    </MemoryRouter>,
  )
  return { service, ...result }
}

function renderGuarded(user: AuthUser | null, element: ReactNode = <div>protected</div>) {
  const service = mockAuthService(user)
  const result = render(
    <MemoryRouter initialEntries={['/timezone']}>
      <AuthProvider service={service}>
        <Routes>
          <Route path="/login" element={<div>login-page</div>} />
          <Route path="/timezone" element={<RequireAuth>{element}</RequireAuth>} />
        </Routes>
      </AuthProvider>
    </MemoryRouter>,
  )
  return { service, ...result }
}

describe('Auth', () => {
  it('UT-03 login success calls service and navigates away from login', async () => {
    const { service } = renderAtLogin()
    const user = userEvent.setup()

    await user.type(screen.getByLabelText(/username/i), 'alice')
    await user.type(screen.getByLabelText(/password/i), 'secret')
    await user.click(screen.getByRole('button', { name: /sign in/i }))

    await waitFor(() => expect(service.login).toHaveBeenCalledWith('alice', 'secret'))
    expect(await screen.findByText('home-page')).toBeInTheDocument()
  })

  it('UT-04 login failure shows error and stays on login page', async () => {
    const { service } = renderAtLogin()
    vi.mocked(service.login).mockRejectedValue(new Error('HTTP 401'))
    const user = userEvent.setup()

    await user.type(screen.getByLabelText(/username/i), 'alice')
    await user.type(screen.getByLabelText(/password/i), 'wrong')
    await user.click(screen.getByRole('button', { name: /sign in/i }))

    expect(await screen.findByText(/401/)).toBeInTheDocument()
    expect(screen.queryByText('home-page')).not.toBeInTheDocument()
  })

  it('UT-05 unauthenticated redirects to login', () => {
    renderGuarded(null)
    expect(screen.getByText('login-page')).toBeInTheDocument()
    expect(screen.queryByText('protected')).not.toBeInTheDocument()
  })

  it('UT-05 authenticated renders guarded content', () => {
    renderGuarded({ username: 'alice' })
    expect(screen.getByText('protected')).toBeInTheDocument()
  })

  it('UT-06 logout clears state and returns to login', async () => {
    const { service } = renderGuarded({ username: 'alice' }, <NavBar />)
    const user = userEvent.setup()

    expect(screen.getByText('alice')).toBeInTheDocument()
    await user.click(screen.getByRole('button', { name: /logout/i }))

    await waitFor(() => expect(service.logout).toHaveBeenCalled())
    expect(await screen.findByText('login-page')).toBeInTheDocument()
  })
})
