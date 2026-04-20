// ── Tipos/Enums ───────────────────────────────────────────────────────────────

export type Gender = 'MALE' | 'FEMALE' | 'OTHER';
export type BloodType =
  | 'A_POSITIVE' | 'A_NEGATIVE'
  | 'B_POSITIVE' | 'B_NEGATIVE'
  | 'AB_POSITIVE' | 'AB_NEGATIVE'
  | 'O_POSITIVE' | 'O_NEGATIVE'
  | 'UNKNOWN';

export type PatientStatus = 'ACTIVE' | 'INACTIVE' | 'DISCHARGE' | 'ELIMINATION';

export const genderOptions: { value: Gender; label: string }[] = [
  { value: 'MALE',   label: 'Masculino' },
  { value: 'FEMALE', label: 'Femenino' },
  { value: 'OTHER',  label: 'Otro' },
];

export const bloodTypeOptions: { value: BloodType; label: string }[] = [
  { value: 'A_POSITIVE',  label: 'A+' },
  { value: 'A_NEGATIVE',  label: 'A-' },
  { value: 'B_POSITIVE',  label: 'B+' },
  { value: 'B_NEGATIVE',  label: 'B-' },
  { value: 'AB_POSITIVE', label: 'AB+' },
  { value: 'AB_NEGATIVE', label: 'AB-' },
  { value: 'O_POSITIVE',  label: 'O+' },
  { value: 'O_NEGATIVE',  label: 'O-' },
  { value: 'UNKNOWN',     label: 'Desconocido' },
];

export const patientStatusOptions: { value: PatientStatus; label: string; color: string }[] = [
  { value: 'ACTIVE',     label: 'Activo',      color: 'success' },
  { value: 'INACTIVE',   label: 'Inactivo',    color: 'warning' },
  { value: 'DISCHARGE',  label: 'Alta médica', color: 'info' },
  { value: 'ELIMINATION',label: 'Eliminado',   color: 'danger' },
];

// ── Respuesta del backend ─────────────────────────────────────────────────────

export interface PatientResponse {
  id: number;
  firstName: string;
  paternalSurname: string;
  maternalSurname: string | null;
  fullName: string;
  ci: string;
  gender: Gender;
  birthDate: string;       // LocalDate → 'YYYY-MM-DD'
  age: number | null;
  phone: string | null;
  email: string | null;
  address: string | null;
  bloodType: BloodType | null;
  occupation: string | null;
  emergencyContactName: string | null;
  emergencyContactPhone: string | null;
  notes: string | null;
  status: PatientStatus;
}

/** Fila enriquecida para tabla */
export interface PatientPageResponse extends PatientResponse {
  genderLabel?: string;
  statusLabel?: string;
  bloodTypeLabel?: string;
}

// ── Requests ──────────────────────────────────────────────────────────────────

export interface PatientRequest {
  firstName: string;
  paternalSurname: string;
  maternalSurname?: string | null;
  ci: string;
  gender: Gender;
  birthDate: string;       // 'YYYY-MM-DD'
  phone?: string | null;
  email?: string | null;
  address?: string | null;
  bloodType?: BloodType | null;
  occupation?: string | null;
  emergencyContactName?: string | null;
  emergencyContactPhone?: string | null;
  notes?: string | null;
}

export interface PatientUpdateRequest extends PatientRequest {}

export interface ChangePatientStatusRequest {
  status: PatientStatus;
}

