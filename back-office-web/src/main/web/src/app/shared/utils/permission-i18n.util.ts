/**
 * Diccionario de traducción para los permisos del sistema.
 * Mapea los códigos en inglés definidos en el Backend (DataLoader)
 * a etiquetas descriptivas en español para la interfaz de usuario.
 */
export const PERMISSION_TRANSLATIONS: Record<string, string> = {
  // Usuarios
  'CREATE_USER': 'Crear Usuario',
  'VIEW_USER': 'Ver Usuario',
  'UPDATE_USER': 'Actualizar Usuario',
  'DELETE_USER': 'Eliminar Usuario',
  'LIST_USER': 'Listar Usuarios',
  'CHANGE_USER_STATUS': 'Cambiar Estado de Usuario',

  // Roles
  'CREATE_ROLE': 'Crear Rol',
  'READ_ROLE': 'Ver Rol',
  'UPDATE_ROLE': 'Actualizar Rol',
  'DELETE_ROLE': 'Eliminar Rol',
  'LIST_ROLE': 'Listar Roles',
  'CHANGE_ROLE_STATUS': 'Cambiar Estado de Rol',
  'ASSIGN_PERMISSION_TO_ROLE': 'Asignar Permisos a Rol',
  'REMOVE_PERMISSION_FROM_ROLE': 'Remover Permisos de Rol',

  // Permisos
  'CREATE_PERMISSION': 'Crear Permiso',
  'READ_PERMISSION': 'Ver Permiso',
  'UPDATE_PERMISSION': 'Actualizar Permiso',
  'DELETE_PERMISSION': 'Eliminar Permiso',
  'LIST_PERMISSION': 'Listar Permisos',
  'CHANGE_PERMISSION_STATUS': 'Cambiar Estado de Permiso',

  // Empleados
  'CREATE_EMPLOYEE': 'Crear Empleado',
  'VIEW_EMPLOYEE': 'Ver Empleado',
  'UPDATE_EMPLOYEE': 'Actualizar Empleado',
  'DELETE_EMPLOYEE': 'Eliminar Empleado',
  'LIST_EMPLOYEE': 'Listar Empleados',
  'CHANGE_EMPLOYEE_STATUS': 'Cambiar Estado de Empleado',
  'ASSIGN_USER_TO_EMPLOYEE': 'Asignar Usuario a Empleado',
  'REMOVE_USER_FROM_EMPLOYEE': 'Desvincular Usuario de Empleado',

  // Pacientes
  'CREATE_PATIENT': 'Crear Paciente',
  'VIEW_PATIENT': 'Ver Paciente',
  'UPDATE_PATIENT': 'Actualizar Paciente',
  'DELETE_PATIENT': 'Eliminar Paciente',
  'LIST_PATIENT': 'Listar Pacientes',
  'CHANGE_PATIENT_STATUS': 'Cambiar Estado de Paciente',

  // Servicios
  'CREATE_SERVICE': 'Crear Servicio',
  'VIEW_SERVICE': 'Ver Servicio',
  'UPDATE_SERVICE': 'Actualizar Servicio',
  'DELETE_SERVICE': 'Eliminar Servicio',
  'LIST_SERVICE': 'Listar Servicios',
  'CHANGE_SERVICE_STATUS': 'Cambiar Estado de Servicio',

  // Episodios
  'CREATE_EPISODE': 'Abrir Episodio Clínico',
  'VIEW_EPISODE': 'Ver Episodio Clínico',
  'CLOSE_EPISODE': 'Cerrar Episodio Clínico',
  'LIST_EPISODE': 'Listar Episodios Clínicos',

  // Sesiones
  'CREATE_CLINICAL_SESSION': 'Crear Sesión Clínica',
  'VIEW_CLINICAL_SESSION': 'Ver Sesión Clínica',
  'UPDATE_CLINICAL_SESSION': 'Actualizar Sesión Clínica',
  'DELETE_CLINICAL_SESSION': 'Eliminar Sesión Clínica',
  'LIST_CLINICAL_SESSION': 'Listar Sesiones Clínicas',
  'MANAGE_SESSION_SERVICES': 'Gestionar Servicios de Sesión',

  // Análisis de Pisada
  'CREATE_FOOT_ANALYSIS': 'Crear Análisis de Pisada',
  'VIEW_FOOT_ANALYSIS': 'Ver Análisis de Pisada',
  'UPDATE_FOOT_ANALYSIS': 'Actualizar Análisis de Pisada',
  'DELETE_FOOT_ANALYSIS': 'Eliminar Análisis de Pisada',
  'MANAGE_ANALYSIS_PHOTOS': 'Gestionar Fotos de Análisis',
  'ANNOTATE_PHOTO': 'Guardar Trazos y Ángulos'
};

/**
 * Función de utilidad para traducir un código de permiso.
 * Si no encuentra traducción, devuelve el código original.
 */
export function translatePermission(code: string): string {
  return PERMISSION_TRANSLATIONS[code] || code;
}
