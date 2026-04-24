
export interface LoginRequest {
  username: string;
  password: string;
}

/** POST /api/auth/refresh */
export interface RefreshTokenRequest {
  refreshToken: string;
}

/** POST /api/auth/logout */
export interface LogoutRequest {
  refreshToken: string;
}

// ─── RESPONSE ─────────────────────────────────────────────────────────────────

/** Información del usuario autenticado (dentro de JwtResponse) */
export interface UserAuthInfo {
  id: number;
  username: string;
  email: string;
  fullName: string | null;
  role: string | null;
  /** Permisos efectivos del usuario (todos sus roles combinados). */
  permissions: string[];
}

/** Respuesta de login y refresh — contiene ambos tokens */
export interface JwtResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  userInfo: UserAuthInfo;
}

/** Envoltura genérica que usa el backend en todas las respuestas */
export interface ApiResponse<T> {
  code: string;
  message: string;
  data: T;
}


/**
 * Enum de roles del sistema.
 * Los valores coinciden exactamente con los roles definidos en el backend.
 * Usar en Guards de ruta. Para control de UI usar AppPermission.
 */
export enum AppRole {
  ADMIN          = 'ROLE_ADMIN',
  ROOT           = 'ROLE_ROOT',
  FISIOTERAPEUTA = 'ROLE_FISIOTERAPEUTA',
  RECEPCIONISTA  = 'ROLE_RECEPCIONISTA',
}

/**
 * Enum de permisos del sistema.
 * Los valores coinciden exactamente con los permisos del backend (DataLoader).
 *
 * ═══════════════════════════════════════════════════════════════════════
 * REGLA DE ORO: Para controlar UI (mostrar/ocultar botones/pantallas)
 * el permiso SIEMPRE debe estar aquí Y en el DataLoader.
 *
 * WORKFLOW para nuevo módulo (ej: Citas):
 *   1. Agregar aquí:       LIST_APPOINTMENT = 'LIST_APPOINTMENT'
 *   2. Agregar DataLoader: {"LIST_APPOINTMENT", "Listar citas"}
 *   3. Usar en pantalla:   hasPermission(AppPermission.LIST_APPOINTMENT)
 *   4. Al reiniciar back:  syncPermissionsToExistingFullAccessRoles() lo
 *                          asigna automáticamente a ROLE_ADMIN y ROLE_ROOT
 *   5. Admin asigna otros roles desde la UI de Roles → Actualizar
 *
 * PERMISOS CREADOS SOLO POR UI (sin enum):
 *   → Solo controlan el backend (@PreAuthorize)
 *   → NO controlan visibilidad de UI
 * ═══════════════════════════════════════════════════════════════════════
 *
 * Uso: authService.hasPermission(AppPermission.LIST_ROLE)
 */
export enum AppPermission {
  // ── Usuarios ──────────────────────────────────────────────────────────────
  CREATE_USER        = 'CREATE_USER',
  VIEW_USER          = 'VIEW_USER',
  UPDATE_USER        = 'UPDATE_USER',
  DELETE_USER        = 'DELETE_USER',
  LIST_USER          = 'LIST_USER',
  CHANGE_USER_STATUS = 'CHANGE_USER_STATUS',

  // ── Roles ─────────────────────────────────────────────────────────────────
  CREATE_ROLE        = 'CREATE_ROLE',
  READ_ROLE          = 'READ_ROLE',
  UPDATE_ROLE        = 'UPDATE_ROLE',
  DELETE_ROLE        = 'DELETE_ROLE',
  LIST_ROLE          = 'LIST_ROLE',
  CHANGE_ROLE_STATUS = 'CHANGE_ROLE_STATUS',

  // ── Permisos ──────────────────────────────────────────────────────────────
  CREATE_PERMISSION             = 'CREATE_PERMISSION',
  READ_PERMISSION               = 'READ_PERMISSION',
  UPDATE_PERMISSION             = 'UPDATE_PERMISSION',
  DELETE_PERMISSION             = 'DELETE_PERMISSION',
  LIST_PERMISSION               = 'LIST_PERMISSION',
  CHANGE_PERMISSION_STATUS      = 'CHANGE_PERMISSION_STATUS',

  // ── Asignación rol-permiso ─────────────────────────────────────────────────
  ASSIGN_PERMISSION_TO_ROLE   = 'ASSIGN_PERMISSION_TO_ROLE',
  REMOVE_PERMISSION_FROM_ROLE = 'REMOVE_PERMISSION_FROM_ROLE',

  // ── Empleados ─────────────────────────────────────────────────────────────
  CREATE_EMPLOYEE             = 'CREATE_EMPLOYEE',
  VIEW_EMPLOYEE               = 'VIEW_EMPLOYEE',
  UPDATE_EMPLOYEE             = 'UPDATE_EMPLOYEE',
  DELETE_EMPLOYEE             = 'DELETE_EMPLOYEE',
  LIST_EMPLOYEE               = 'LIST_EMPLOYEE',
  CHANGE_EMPLOYEE_STATUS      = 'CHANGE_EMPLOYEE_STATUS',
  ASSIGN_USER_TO_EMPLOYEE     = 'ASSIGN_USER_TO_EMPLOYEE',
  REMOVE_USER_FROM_EMPLOYEE   = 'REMOVE_USER_FROM_EMPLOYEE',

  // ── Pacientes ─────────────────────────────────────────────────────────────
  CREATE_PATIENT              = 'CREATE_PATIENT',
  VIEW_PATIENT                = 'VIEW_PATIENT',
  UPDATE_PATIENT              = 'UPDATE_PATIENT',
  DELETE_PATIENT              = 'DELETE_PATIENT',
  LIST_PATIENT                = 'LIST_PATIENT',
  CHANGE_PATIENT_STATUS       = 'CHANGE_PATIENT_STATUS',

  // ── Servicios del consultorio ─────────────────────────────────────────────
  CREATE_SERVICE              = 'CREATE_SERVICE',
  VIEW_SERVICE                = 'VIEW_SERVICE',
  UPDATE_SERVICE              = 'UPDATE_SERVICE',
  DELETE_SERVICE              = 'DELETE_SERVICE',
  LIST_SERVICE                = 'LIST_SERVICE',
  CHANGE_SERVICE_STATUS       = 'CHANGE_SERVICE_STATUS',

  // ── Episodios Clínicos ────────────────────────────────────────────────────
  CREATE_EPISODE              = 'CREATE_EPISODE',
  VIEW_EPISODE                = 'VIEW_EPISODE',
  CLOSE_EPISODE               = 'CLOSE_EPISODE',
  LIST_EPISODE                = 'LIST_EPISODE',

  // ── Sesiones Clínicas ─────────────────────────────────────────────────────
  CREATE_CLINICAL_SESSION     = 'CREATE_CLINICAL_SESSION',
  VIEW_CLINICAL_SESSION       = 'VIEW_CLINICAL_SESSION',
  UPDATE_CLINICAL_SESSION     = 'UPDATE_CLINICAL_SESSION',
  DELETE_CLINICAL_SESSION     = 'DELETE_CLINICAL_SESSION',
  LIST_CLINICAL_SESSION       = 'LIST_CLINICAL_SESSION',
  MANAGE_SESSION_SERVICES     = 'MANAGE_SESSION_SERVICES',
}
