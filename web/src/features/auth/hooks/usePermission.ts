import { useAuthContext } from "./AuthContext";

export function usePermission() {
  const { effectiveRights } = useAuthContext();

  const hasRight = (rightName: string): boolean => {
    return effectiveRights.includes(rightName);
  };

  return { hasRight };
}
