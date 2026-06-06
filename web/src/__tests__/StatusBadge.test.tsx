import { describe, it, expect } from "vitest";
import { render, screen } from "@testing-library/react";
import { StatusBadge } from "../components/StatusBadge";
import type { UserDto } from "@api/api-models";

describe("StatusBadge", () => {
  it("renders user name", () => {
    const user: UserDto = { id: "1", name: "Alice", email: "alice@example.com" };

    render(<StatusBadge user={user} />);

    expect(screen.getByText("Alice")).toBeDefined();
  });

  it("renders user email", () => {
    const user: UserDto = { id: "2", name: "Bob", email: "bob@example.com" };

    render(<StatusBadge user={user} />);

    expect(screen.getByText("bob@example.com")).toBeDefined();
  });
});
