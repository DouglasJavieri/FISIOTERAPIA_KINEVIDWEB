export interface UserRoleRequest {
  userId: number;
  roleId: number;
}

export interface UserRoleResponse {
  id: number;
  userId?: number;
  username?: string;
  roleId?: number;
  roleName?: string;
}

