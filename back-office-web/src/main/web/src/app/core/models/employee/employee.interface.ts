/** Departamentos de Bolivia — coinciden exactamente con el enum BolivianDepartment del backend */
export type BolivianDepartment =
  | 'BENI'
  | 'CHUQUISACA'
  | 'COCHABAMBA'
  | 'LA_PAZ'
  | 'ORURO'
  | 'PANDO'
  | 'POTOSI'
  | 'SANTA_CRUZ'
  | 'TARIJA';

export const bolivianDepartmentOptions: { value: BolivianDepartment; label: string }[] = [
  { value: 'BENI',       label: 'Beni' },
  { value: 'CHUQUISACA', label: 'Chuquisaca' },
  { value: 'COCHABAMBA', label: 'Cochabamba' },
  { value: 'LA_PAZ',     label: 'La Paz' },
  { value: 'ORURO',      label: 'Oruro' },
  { value: 'PANDO',      label: 'Pando' },
  { value: 'POTOSI',     label: 'Potosí' },
  { value: 'SANTA_CRUZ', label: 'Santa Cruz' },
  { value: 'TARIJA',     label: 'Tarija' },
];

/** Respuesta del backend para un empleado */
export interface EmployeeResponse {
  id: number;
  firstName: string;
  paternalSurname: string;
  maternalSurname: string | null;
  ci: string;
  expedition: BolivianDepartment;
  specialty: string;
  phone: string | null;
  address: string | null;
  department: BolivianDepartment;
  professionalEmail: string | null;
  admissionDate: string;   // LocalDate → string ISO 'YYYY-MM-DD'
  status: string;
  // Usuario asignado (nullable)
  userId: number | null;
  username: string | null;
  email: string | null;
}

/** Fila enriquecida para la tabla paginada */
export interface EmployeePageResponse extends EmployeeResponse {
  fullName?: string;
  statusLabel?: string;
  expeditionLabel?: string;
  departmentLabel?: string;
  userAssigned?: string;
}

/** Body para crear un empleado */
export interface EmployeeRequest {
  firstName: string;
  paternalSurname: string;
  maternalSurname?: string | null;
  ci: string;
  expedition: BolivianDepartment;
  specialty: string;
  phone?: string | null;
  address?: string | null;
  department: BolivianDepartment;
  professionalEmail?: string | null;
  admissionDate: string;   // 'YYYY-MM-DD'
}

/** Body para actualizar un empleado */
export interface EmployeeUpdateRequest {
  firstName: string;
  paternalSurname: string;
  maternalSurname?: string | null;
  ci: string;
  expedition: BolivianDepartment;
  specialty: string;
  phone?: string | null;
  address?: string | null;
  department: BolivianDepartment;
  professionalEmail?: string | null;
  admissionDate: string;
}

/** Body para cambiar estado */
export interface ChangeEmployeeStatusRequest {
  status: string;
}

/** Body para asignar usuario */
export interface AssignUserToEmployeeRequest {
  userId: number;
}

