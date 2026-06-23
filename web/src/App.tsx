import { Box, Typography, Button } from '@mui/material'

function App() {
  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        minHeight: '100vh',
        gap: 2,
      }}
    >
      <Typography variant="h2" component="h1">
        Hello World
      </Typography>
      <Typography variant="body1" color="text.secondary">
        Welcome to the React + TypeScript + MUI project
      </Typography>
      <Button variant="contained" color="primary">
        Get Started
      </Button>
    </Box>
  )
}

export default App
