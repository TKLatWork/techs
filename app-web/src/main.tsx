import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import CssBaseline from '@mui/material/CssBaseline'
import { ThemeProvider } from '@mui/material/styles'
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { theme } from './app/theme'
import Layout from './app/Layout'
import { TimezoneServiceProvider } from './features/timezone/TimezoneServiceProvider'
import TimezonePage from './features/timezone/TimezonePage'

// Composition root: shared infra (theme, router, layout) wires feature pages and their service providers
createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <BrowserRouter basename="/web">
        <Routes>
          <Route element={<Layout />}>
            <Route index element={<Navigate to="/timezone" replace />} />
            <Route
              path="/timezone"
              element={
                <TimezoneServiceProvider>
                  <TimezonePage />
                </TimezoneServiceProvider>
              }
            />
            <Route path="*" element={<Navigate to="/timezone" replace />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </ThemeProvider>
  </StrictMode>,
)
