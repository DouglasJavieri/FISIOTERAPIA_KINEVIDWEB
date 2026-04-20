export interface UserResponse {
  id: number;
  username: string;
  email: string;
  status: string;
}

export interface UserPageResponse {
  id: number;
  username: string;
  email: string;
  status: string;
  roleName?: string;
  statusLabel?: string;
}

export interface UserRequest {
  username: string;
  email: string;
  password: string;
}

export interface UserUpdateRequest {
  username: string;
  email: string;
  password?: string;
}

export interface ChangeUserStatusRequest {
  status: string;
}

