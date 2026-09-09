import { MemoryRouter } from 'react-router-dom'
import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import NavBar from './NavBar'
import { AuthProvider } from '../auth/AuthProvider'
import { navItems } from '../navItems'
import { mockAuthService } from '../../test/mockAuthService'

describe('NavBar', () => {
  it('UT-01 renders every nav item and highlights the active route', () => {
    render(
      <MemoryRouter initialEntries={['/timezone']}>
        <AuthProvider service={mockAuthService()}>
          <NavBar />
        </AuthProvider>
      </MemoryRouter>,
    )

    for (const item of navItems) {
      expect(screen.getByRole('link', { name: item.label })).toBeInTheDocument()
    }
    const active = screen.getByRole('link', { name: 'Timezone' })
    expect(active.className).toContain('MuiButton-outlined')
  })
})
