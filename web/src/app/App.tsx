import { Suspense } from "react";
import { BrowserRouter, Routes, Route, Link } from "react-router-dom";
import { ThemeProvider } from "@mui/material/styles";
import CssBaseline from "@mui/material/CssBaseline";
import CircularProgress from "@mui/material/CircularProgress";
import Box from "@mui/material/Box";
import AppBar from "@mui/material/AppBar";
import Toolbar from "@mui/material/Toolbar";
import Typography from "@mui/material/Typography";
import Button from "@mui/material/Button";
import { theme } from "./theme";
import { collectFeatureRoutes } from "./featureRegistry";
import { AuthProvider, useAuthContext } from "../features/auth/hooks/AuthContext";
import { LogoutButton } from "../features/auth/components/LogoutButton";
import HomePage from "../pages/HomePage";
import NotFoundPage from "../pages/NotFoundPage";
import "../styles/global.css";

const featureRoutes = collectFeatureRoutes();

function NavBar() {
  const { isAuthenticated, user } = useAuthContext();

  return (
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6" component={Link} to="/" sx={{ flexGrow: 1, color: "inherit", textDecoration: "none" }}>
          Techs
        </Typography>
        {isAuthenticated && (
          <>
            <Button color="inherit" component={Link} to="/profile">
              {user?.displayName}
            </Button>
            <LogoutButton />
          </>
        )}
      </Toolbar>
    </AppBar>
  );
}

function AppContent() {
  return (
    <>
      <NavBar />
      <Suspense
        fallback={
          <Box sx={{ display: "flex", justifyContent: "center", p: 4 }}>
            <CircularProgress />
          </Box>
        }
      >
        <Routes>
          <Route path="/" element={<HomePage />} />
          {featureRoutes.map((route) => (
            <Route key={route.path} path={route.path!} element={route.element} />
          ))}
          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </Suspense>
    </>
  );
}

export function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AuthProvider>
        <BrowserRouter>
          <AppContent />
        </BrowserRouter>
      </AuthProvider>
    </ThemeProvider>
  );
}
