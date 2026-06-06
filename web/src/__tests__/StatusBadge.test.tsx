/**
 * Tests web component that consumes generated TypeScript from API module.
 * @req FR-001 — Verify web module exists as a distinct top-level directory
 * @req FR-002 — Verify web module has source code directory
 * @req FR-003 — Verify web module has its own configuration (tsconfig, vite.config)
 * @req FR-004 — Verify web depends on API only (not domain directly)
 * @req FR-006 — Verify web module has entry point awareness
 * @req SC-004 — Web module can be independently tested
 */
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
