import { describe, it, expect, vi } from "vitest";
import { render, screen } from "@testing-library/react";
import { MemoryRouter, Routes, Route } from "react-router-dom";
import { ThemeProvider } from "@mui/material/styles";
import { theme } from "../../../app/theme";
import { ProtectedRoute } from "../components/ProtectedRoute";
import * as AuthContext from "../hooks/AuthContext";

vi.mock("../hooks/AuthContext", () => ({
  useAuthContext: vi.fn(),
}));

vi.mock("../hooks/usePermission", () => ({
  usePermission: () => ({
    hasRight: (right: string) => right === "read:/allowed",
  }),
}));

function TestPage() {
  return <div>Protected Content</div>;
}

function renderProtected(initialPath: string, requiredRight?: string) {
  return render(
    <ThemeProvider theme={theme}>
      <MemoryRouter initialEntries={[initialPath]}>
        <Routes>
          <Route
            path="/test"
            element={
              <ProtectedRoute requiredRight={requiredRight}>
                <TestPage />
              </ProtectedRoute>
            }
          />
          <Route path="/login" element={<div>Login Page</div>} />
        </Routes>
      </MemoryRouter>
    </ThemeProvider>
  );
}

describe("ProtectedRoute", () => {
  it("redirects to login when not authenticated", () => {
    vi.mocked(AuthContext.useAuthContext).mockReturnValue({
      user: null,
      token: null,
      isAuthenticated: false,
      effectiveRights: [],
      login: vi.fn(),
      logout: vi.fn(),
    });

    renderProtected("/test");
    expect(screen.getByText("Login Page")).toBeDefined();
  });

  it("shows access denied when lacking required right", () => {
    vi.mocked(AuthContext.useAuthContext).mockReturnValue({
      user: { id: "1", username: "user", displayName: "User", roleName: "User", createdAt: "", effectiveRights: [], individualRights: [] },
      token: "token",
      isAuthenticated: true,
      effectiveRights: [],
      login: vi.fn(),
      logout: vi.fn(),
    });

    renderProtected("/test", "read:/denied");
    expect(screen.getByText("Access Denied")).toBeDefined();
  });

  it("renders content when authenticated with required right", () => {
    vi.mocked(AuthContext.useAuthContext).mockReturnValue({
      user: { id: "1", username: "admin", displayName: "Admin", roleName: "Admin", createdAt: "", effectiveRights: [], individualRights: [] },
      token: "token",
      isAuthenticated: true,
      effectiveRights: ["read:/allowed"],
      login: vi.fn(),
      logout: vi.fn(),
    });

    renderProtected("/test", "read:/allowed");
    expect(screen.getByText("Protected Content")).toBeDefined();
  });
});
