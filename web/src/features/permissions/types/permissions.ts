export interface RightDto {
  id: string;
  name: string;
  type: "BASE" | "FEATURE";
  urlPattern?: string;
  action?: string;
  description?: string;
  source?: string;
  featureId?: string;
}

export interface RoleDto {
  id: string;
  name: string;
  description?: string;
  rightIds: string[];
  builtIn: boolean;
  immutable: boolean;
}

export interface UserPermissionsDto {
  userId: string;
  username: string;
  displayName: string;
  role: { id: string; name: string };
  effectiveRights: RightDto[];
  individualRights: RightDto[];
}
