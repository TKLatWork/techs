import { Typography, Box, Button } from "@mui/material";
import { Link } from "react-router-dom";

export default function NotFoundPage() {
  return (
    <Box sx={{ p: 3, textAlign: "center" }}>
      <Typography variant="h1">404</Typography>
      <Typography variant="body1" sx={{ mt: 2 }}>
        Page not found.
      </Typography>
      <Button component={Link} to="/" sx={{ mt: 2 }}>
        Go Home
      </Button>
    </Box>
  );
}
