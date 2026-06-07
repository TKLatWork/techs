import { describe, it, expect, vi } from "vitest";
import { render, screen, fireEvent } from "@testing-library/react";
import { BrowserRouter } from "react-router-dom";
import { ThemeProvider } from "@mui/material/styles";
import { theme } from "../../../app/theme";
import { LogoutButton } from "../components/LogoutButton";
import * as AuthContext from "../hooks/AuthContext";

vi.mock("../hooks/AuthContext", () => ({
  useAuthContext: vi.fn(),
}));

function renderWithProviders(ui: React.ReactElement) {
  return render(
    <ThemeProvider theme={theme}>
      <BrowserRouter>{ui}</BrowserRouter>
    </ThemeProvider>
  );
}

describe("LogoutButton", () => {
  it("renders logout button", () => {
    vi.mocked(AuthContext.useAuthContext).mockReturnValue({
      user: null,
      token: null,
      isAuthenticated: true,
      effectiveRights: [],
      login: vi.fn(),
      logout: vi.fn().mockResolvedValue(undefined),
    });

    renderWithProviders(<LogoutButton />);
    expect(screen.getByText("Logout")).toBeDefined();
  });

  it("calls logout when clicked", async () => {
    const mockLogout = vi.fn().mockResolvedValue(undefined);
    vi.mocked(AuthContext.useAuthContext).mockReturnValue({
      user: null,
      token: null,
      isAuthenticated: true,
      effectiveRights: [],
      login: vi.fn(),
      logout: mockLogout,
    });

    renderWithProviders(<LogoutButton />);
    fireEvent.click(screen.getByText("Logout"));
    expect(mockLogout).toHaveBeenCalled();
  });
});
