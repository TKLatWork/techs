import { Typography, Box } from "@mui/material";
import { useAuthContext } from "../features/auth/hooks/AuthContext";

export default function HomePage() {
  const { isAuthenticated, user } = useAuthContext();

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h1">Techs</Typography>
      {isAuthenticated && user ? (
        <Typography variant="body1" sx={{ mt: 2 }}>
          Welcome, {user.displayName}!
        </Typography>
      ) : (
        <Typography variant="body1" sx={{ mt: 2 }}>
          Welcome to the Techs platform. Please log in.
        </Typography>
      )}
    </Box>
  );
}
