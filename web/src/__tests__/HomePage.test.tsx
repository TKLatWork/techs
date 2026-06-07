import { describe, it, expect } from "vitest";
import { render, screen } from "@testing-library/react";
import { BrowserRouter } from "react-router-dom";
import { ThemeProvider } from "@mui/material/styles";
import { theme } from "../app/theme";
import { AuthProvider } from "../features/auth/hooks/AuthContext";
import HomePage from "../pages/HomePage";

function renderWithProviders(ui: React.ReactElement) {
  return render(
    <ThemeProvider theme={theme}>
      <AuthProvider>
        <BrowserRouter>{ui}</BrowserRouter>
      </AuthProvider>
    </ThemeProvider>
  );
}

describe("HomePage", () => {
  it("renders welcome message", () => {
    renderWithProviders(<HomePage />);
    expect(screen.getByText("Techs")).toBeDefined();
  });

  it("shows login prompt when not authenticated", () => {
    renderWithProviders(<HomePage />);
    expect(screen.getByText(/Please log in/)).toBeDefined();
  });
});
