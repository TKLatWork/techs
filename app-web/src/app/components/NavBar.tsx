import AppBar from '@mui/material/AppBar'
import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import Toolbar from '@mui/material/Toolbar'
import Typography from '@mui/material/Typography'
import { Link, useLocation } from 'react-router-dom'
import { navItems } from '../navItems'
import { useAuth } from '../auth/AuthProvider'

export default function NavBar() {
  const { pathname } = useLocation()
  const { user, logout } = useAuth()
  return (
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6" sx={{ mr: 4 }}>
          WebApp
        </Typography>
        {navItems.map((item) => (
          <Button
            key={item.path}
            component={Link}
            to={item.path}
            color="inherit"
            variant={pathname.startsWith(item.path) ? 'outlined' : 'text'}
            sx={{ color: 'inherit' }}
          >
            {item.label}
          </Button>
        ))}
        <Box sx={{ flexGrow: 1 }} />
        {user && (
          <>
            <Typography sx={{ mr: 2 }}>{user.username}</Typography>
            <Button color="inherit" onClick={logout}>
              Logout
            </Button>
          </>
        )}
      </Toolbar>
    </AppBar>
  )
}
