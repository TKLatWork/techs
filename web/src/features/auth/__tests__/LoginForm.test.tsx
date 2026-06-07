import { describe, it, expect } from "vitest";
import { render, screen } from "@testing-library/react";
import { BrowserRouter } from "react-router-dom";
import { ThemeProvider } from "@mui/material/styles";
import { theme } from "../../../app/theme";
import { LoginForm } from "../components/LoginForm";
import { AuthProvider } from "../hooks/AuthContext";

function renderWithProviders(ui: React.ReactElement) {
  return render(
    <ThemeProvider theme={theme}>
      <AuthProvider>
        <BrowserRouter>{ui}</BrowserRouter>
      </AuthProvider>
    </ThemeProvider>
  );
}

describe("LoginForm", () => {
  it("renders username and password fields", () => {
    renderWithProviders(<LoginForm />);
    expect(screen.getByLabelText(/Username/i)).toBeDefined();
    expect(screen.getByLabelText(/Password/i)).toBeDefined();
  });

  it("renders login button", () => {
    renderWithProviders(<LoginForm />);
    expect(screen.getByRole("button", { name: /Login/i })).toBeDefined();
  });
});
