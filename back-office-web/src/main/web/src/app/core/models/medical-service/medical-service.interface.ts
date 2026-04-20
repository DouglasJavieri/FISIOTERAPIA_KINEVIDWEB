// ── Tipos/Enums ───────────────────────────────────────────────────────────────

export type ServiceCategory =
  | 'REHABILITATION'
  | 'SPORTS_KINESIOLOGY'
  | 'MASSOTHERAPY'
  | 'POSTURAL_ANALYSIS'
  | 'PEDIATRIC_KINESIOLOGY'
  | 'NEUROLOGICAL_REHABILITATION'
  | 'OTHER';

export type ServiceStatus = 'ACTIVE' | 'INACTIVE' | 'ELIMINATION';

export const serviceCategoryOptions: { value: ServiceCategory; label: string }[] = [
  { value: 'REHABILITATION',             label: 'Rehabilitación' },
  { value: 'SPORTS_KINESIOLOGY',         label: 'Kinesiología Deportiva' },
  { value: 'MASSOTHERAPY',               label: 'Masoterapia' },
  { value: 'POSTURAL_ANALYSIS',          label: 'Análisis Postural' },
  { value: 'PEDIATRIC_KINESIOLOGY',      label: 'Kinesiología Pediátrica' },
  { value: 'NEUROLOGICAL_REHABILITATION',label: 'Rehabilitación Neurológica' },
  { value: 'OTHER',                      label: 'Otro' },
];

export const serviceStatusOptions: { value: ServiceStatus; label: string; color: string }[] = [
  { value: 'ACTIVE',     label: 'Activo',   color: 'success' },
  { value: 'INACTIVE',   label: 'Inactivo', color: 'warning' },
  { value: 'ELIMINATION',label: 'Eliminado',color: 'danger' },
];

// ── Respuesta del backend ─────────────────────────────────────────────────────

export interface MedicalServiceResponse {
  id: number;
  name: string;
  description: string | null;
  category: ServiceCategory;
  categoryDescription: string;
  durationMinutes: number | null;
  price: number | null;
  status: ServiceStatus;
}

/** Fila enriquecida para tabla */
export interface MedicalServicePageResponse extends MedicalServiceResponse {
  statusLabel?: string;
}

// ── Requests ──────────────────────────────────────────────────────────────────

export interface MedicalServiceRequest {
  name: string;
  description?: string | null;
  category: ServiceCategory;
  durationMinutes?: number | null;
  price?: number | null;
}

export interface MedicalServiceUpdateRequest extends MedicalServiceRequest {}

export interface ChangeMedicalServiceStatusRequest {
  status: ServiceStatus;
}

