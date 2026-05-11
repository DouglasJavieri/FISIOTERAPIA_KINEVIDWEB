// ── Tipos/Enums ───────────────────────────────────────────────────────────────

export type EpisodeStatus = 'ACTIVE' | 'CLOSED';
export type SessionStatus = 'OPEN' | 'CLOSED' | 'CANCELLED';

export const episodeStatusOptions: { value: EpisodeStatus; label: string; color: string }[] = [
  { value: 'ACTIVE', label: 'Activo',  color: 'success' },
  { value: 'CLOSED', label: 'Cerrado', color: 'default' },
];

export const sessionStatusOptions: { value: SessionStatus; label: string; color: string }[] = [
  { value: 'OPEN',      label: 'Abierta',   color: 'success' },
  { value: 'CLOSED',    label: 'Cerrada',   color: 'default' },
  { value: 'CANCELLED', label: 'Cancelada', color: 'warn'    },
];

// ── Episodio Clínico ──────────────────────────────────────────────────────────

export interface ClinicalEpisodeResponse {
  id: number;
  patientId: number;
  patientFullName: string;
  patientCi: string;
  episodeNumber: number;
  startDate: string;       // LocalDate → 'YYYY-MM-DD'
  endDate: string | null;
  reasonForAdmission: string;
  dischargeReason: string | null;
  episodeStatus: EpisodeStatus;
}

export interface ClinicalEpisodeRequest {
  patientId: number;
  reasonForAdmission: string;
  startDate?: string | null;
}

export interface CloseEpisodeRequest {
  dischargeReason: string;
}

// ── Sesión Clínica ────────────────────────────────────────────────────────────

export interface ClinicalSessionResponse {
  id: number;
  episodeId: number;
  episodeNumber: number;
  patientId: number;
  patientFullName: string;
  employeeId: number;
  employeeFullName: string;
  sessionDate: string;     // 'YYYY-MM-DD'
  sessionNumber: number;
  reasonForConsultation: string;
  relevantBackground: string | null;
  kinesiologicalEvaluation: string | null;
  actualIllnessHistory: string | null;
  gait: string | null;
  functionalTests: string | null;
  complementaryExams: string | null;
  kinesiologicalDiagnosis: string | null;
  treatmentApplied: string | null;
  observations: string | null;
  evolution: string | null;
  hasImageAnalysis: boolean;
  sessionStatus: SessionStatus;
}

export interface ClinicalSessionRequest {
  episodeId: number;
  employeeId: number;
  sessionDate?: string | null;
  reasonForConsultation: string;
  relevantBackground?: string | null;
}

export interface ClinicalSessionUpdateRequest {
  employeeId?: number | null;
  reasonForConsultation?: string | null;
  relevantBackground?: string | null;
  kinesiologicalEvaluation?: string | null;
  actualIllnessHistory?: string | null;
  gait?: string | null;
  functionalTests?: string | null;
  complementaryExams?: string | null;
  kinesiologicalDiagnosis?: string | null;
  treatmentApplied?: string | null;
  observations?: string | null;
  evolution?: string | null;
}

export interface ChangeSessionStatusRequest {
  status: SessionStatus;
}

// ── Servicios aplicados en sesión (N:M) ───────────────────────────────────────

export interface SessionServiceResponse {
  id: number;
  sessionId: number;
  medicalServiceId: number;
  medicalServiceName: string;
  medicalServiceCategory: string | null;
  quantity: number;
  unitPrice: number | null;
  totalPrice: number | null;
  notes: string | null;
}

export interface SessionServiceRequest {
  medicalServiceId: number;
  quantity: number;
  unitPrice?: number | null;
  notes?: string | null;
}

