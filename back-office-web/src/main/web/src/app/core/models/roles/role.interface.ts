
export interface RoleResponse {
  id: number;
  name: string;
  description: string;
  status: string;
}

export interface RolePageResponse {
  id: number;
  name: string;
  description: string;
  status: string;
  statusLabel?: string;
}

export interface RoleRequest {
  name: string;
  description: string;
}

export interface ChangeRoleStatusRequest {
  status: string;
}

