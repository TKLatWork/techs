import type { UserDto } from "@api/api-models";

interface StatusBadgeProps {
  user: UserDto;
}

export function StatusBadge({ user }: StatusBadgeProps) {
  return (
    <div>
      <span>{user.name}</span>
      <span>{user.email}</span>
    </div>
  );
}
