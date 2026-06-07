import { useEffect, useState } from "react";
import { apiClient } from "../../../shared/services/apiClient";
import type { User } from "../../auth/types/auth";

export function useProfile() {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    apiClient
      .get<User>("/api/auth/me")
      .then(({ data }) => {
        setUser(data);
        setLoading(false);
      })
      .catch((err) => {
        setError(err instanceof Error ? err.message : "Failed to load profile");
        setLoading(false);
      });
  }, []);

  return { user, loading, error };
}
