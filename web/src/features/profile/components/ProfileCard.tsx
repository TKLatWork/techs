import { Card, CardContent, Typography, CircularProgress, Box } from "@mui/material";
import { useProfile } from "../hooks/useProfile";

export function ProfileCard() {
  const { user, loading, error } = useProfile();

  if (loading) {
    return (
      <Box sx={{ display: "flex", justifyContent: "center", p: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error || !user) {
    return <Typography color="error">{error || "Failed to load profile"}</Typography>;
  }

  return (
    <Card sx={{ maxWidth: 500, mx: "auto", mt: 4 }}>
      <CardContent>
        <Typography variant="h2" gutterBottom>Profile</Typography>
        <Typography><strong>Username:</strong> {user.username}</Typography>
        <Typography><strong>Display Name:</strong> {user.displayName}</Typography>
        <Typography><strong>Role:</strong> {user.roleName}</Typography>
        <Typography><strong>Created:</strong> {new Date(user.createdAt).toLocaleDateString()}</Typography>
      </CardContent>
    </Card>
  );
}
