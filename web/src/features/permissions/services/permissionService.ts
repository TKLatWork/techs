import { apiClient } from "../../../shared/services/apiClient";
import type { RoleDto, RightDto, UserPermissionsDto } from "../types/permissions";

export const permissionService = {
  listRoles: () => apiClient.get<RoleDto[]>("/api/permissions/roles"),
  createRole: (role: Partial<RoleDto>) => apiClient.post<RoleDto>("/api/permissions/roles", role),
  updateRole: (roleId: string, role: Partial<RoleDto>) => apiClient.put<RoleDto>(`/api/permissions/roles/${roleId}`, role),
  deleteRole: (roleId: string) => apiClient.delete<void>(`/api/permissions/roles/${roleId}`),
  listRights: () => apiClient.get<RightDto[]>("/api/permissions/rights"),
  deleteRight: (rightId: string) => apiClient.delete<void>(`/api/permissions/rights/${rightId}`),
  listUsers: () => apiClient.get<UserPermissionsDto[]>("/api/permissions/users"),
  assignRole: (userId: string, roleId: string) =>
    apiClient.put<UserPermissionsDto>(`/api/permissions/users/${userId}/role`, { roleId }),
  addRight: (userId: string, rightId: string) =>
    apiClient.post<UserPermissionsDto>(`/api/permissions/users/${userId}/rights`, { rightId }),
  removeRight: (userId: string, rightId: string) =>
    apiClient.delete<UserPermissionsDto>(`/api/permissions/users/${userId}/rights/${rightId}`),
  listFeatures: () => apiClient.get<FeatureInfo[]>("/api/permissions/features"),
  disableFeature: (featureId: string) => apiClient.post<void>(`/api/permissions/features/${featureId}/disable`),
};

export interface FeatureInfo {
  featureId: string;
  rightCount: number;
  rights: RightDto[];
}
