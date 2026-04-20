
export interface PermissionResponse {
  id: number;
  name: string;
  description: string;
  status: string;
}

export interface PermissionPageResponse {
  id: number;
  name: string;
  description: string;
  status: string;
  statusLabel?: string;
}

export interface PermissionRequest {
  name: string;
  description: string;
}

export interface ChangePermissionStatusRequest {
  status: string;
}

