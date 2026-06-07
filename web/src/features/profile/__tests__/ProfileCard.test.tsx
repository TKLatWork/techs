import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen } from "@testing-library/react";
import { BrowserRouter } from "react-router-dom";
import { ThemeProvider } from "@mui/material/styles";
import { theme } from "../../../app/theme";
import { ProfileCard } from "../components/ProfileCard";
import * as apiClientModule from "../../../shared/services/apiClient";

vi.mock("../../../shared/services/apiClient", () => ({
  apiClient: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
  ApiError: class ApiError extends Error {
    code: string;
    status: number;
    constructor(message: string, code: string, status: number) {
      super(message);
      this.code = code;
      this.status = status;
    }
  },
}));

const mockUser = {
  id: "u1",
  username: "admin",
  displayName: "Administrator",
  roleName: "Admin",
  createdAt: "2026-06-07T00:00:00Z",
  effectiveRights: [],
  individualRights: [],
};

function renderWithProviders(ui: React.ReactElement) {
  return render(
    <ThemeProvider theme={theme}>
      <BrowserRouter>{ui}</BrowserRouter>
    </ThemeProvider>
  );
}

describe("ProfileCard", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("renders user data when loaded", async () => {
    vi.mocked(apiClientModule.apiClient.get).mockResolvedValue({
      data: mockUser,
      status: 200,
    });

    renderWithProviders(<ProfileCard />);

    expect(await screen.findByText("admin")).toBeDefined();
    expect(screen.getByText("Administrator")).toBeDefined();
    expect(screen.getByText("Admin")).toBeDefined();
  });

  it("displays all profile fields", async () => {
    vi.mocked(apiClientModule.apiClient.get).mockResolvedValue({
      data: mockUser,
      status: 200,
    });

    renderWithProviders(<ProfileCard />);

    await screen.findByText("admin");
    expect(screen.getByText(/Username/)).toBeDefined();
    expect(screen.getByText(/Display Name/)).toBeDefined();
    expect(screen.getByText(/Role/)).toBeDefined();
    expect(screen.getByText(/Created/)).toBeDefined();
  });
});
