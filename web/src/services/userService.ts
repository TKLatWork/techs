import type { UserDto } from "@api/api-models";

export function formatUser(user: UserDto): string {
  return `${user.name} <${user.email}>`;
}
