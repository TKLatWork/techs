import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import CssBaseline from '@mui/material/CssBaseline'
import { ThemeProvider } from '@mui/material/styles'
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { theme } from './app/theme'
import Layout from './app/Layout'
import { AuthProvider } from './app/auth/AuthProvider'
import RequireAuth from './app/auth/RequireAuth'
import LoginPage from './app/auth/LoginPage'
import { TimezoneServiceProvider } from './features/timezone/TimezoneServiceProvider'
import TimezonePage from './features/timezone/TimezonePage'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <BrowserRouter basename="/web">
        <AuthProvider>
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route element={<Layout />}>
              <Route index element={<Navigate to="/timezone" replace />} />
              <Route
                path="/timezone"
                element={
                  <RequireAuth>
                    <TimezoneServiceProvider>
                      <TimezonePage />
                    </TimezoneServiceProvider>
                  </RequireAuth>
                }
              />
              <Route path="*" element={<Navigate to="/timezone" replace />} />
            </Route>
          </Routes>
        </AuthProvider>
      </BrowserRouter>
    </ThemeProvider>
  </StrictMode>,
)
