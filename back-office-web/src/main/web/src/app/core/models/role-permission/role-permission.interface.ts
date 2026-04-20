
export interface RolePermissionRequest {
  roleId: number;
  permissionId: number;
}

export interface RolePermissionResponse {
  id: number;
  roleId?: number;
  roleName?: string;
  permissionId?: number;
  permissionName?: string;
}

