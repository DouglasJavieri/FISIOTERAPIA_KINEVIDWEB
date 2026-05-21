// ── Tipos controlados ─────────────────────────────────────────────────────────

export type DiagnosisType = 'NORMAL' | 'PRONATION' | 'SUPINATION';

export type FootSide = 'LEFT' | 'RIGHT';

export type TibialMalleolarRule = 'NORMAL' | 'VARUS' | 'VALGUS' | 'OTHER';

export type GaitType = 'NORMAL' | 'PRONATOR' | 'SUPINATOR' | 'MIXED';

export type FootprintType =
  | 'INDEX_NORMAL'
  | 'INDEX_FLAT_FOOT'
  | 'INDEX_CAVUS_FOOT'
  | 'FLAT_FOOT'
  | 'FLAT_NORMAL'
  | 'NORMAL'
  | 'NORMAL_CAVUS'
  | 'CAVUS_FOOT'
  | 'STRONG_CAVUS'
  | 'EXTREME_CAVUS';

// ── Opciones para selects (UI) ────────────────────────────────────────────────

export const diagnosisOptions: { value: DiagnosisType; label: string }[] = [
  { value: 'NORMAL',    label: 'Normal'    },
  { value: 'PRONATION', label: 'Pronación' },
  { value: 'SUPINATION', label: 'Supinación' },
];

export const footSideOptions: { value: FootSide; label: string }[] = [
  { value: 'LEFT',  label: 'Pie Izquierdo' },
  { value: 'RIGHT', label: 'Pie Derecho'   },
];

export const tibialMalleolarRuleOptions: { value: TibialMalleolarRule; label: string }[] = [
  { value: 'NORMAL', label: 'Normal' },
  { value: 'VARUS',  label: 'Varo'   },
  { value: 'VALGUS', label: 'Valgo'  },
  { value: 'OTHER',  label: 'Otro'   },
];

export const gaitOptions: { value: GaitType; label: string }[] = [
  { value: 'NORMAL',    label: 'Normal'    },
  { value: 'PRONATOR',  label: 'Pronador'  },
  { value: 'SUPINATOR', label: 'Supinador' },
  { value: 'MIXED',     label: 'Mixto'     },
];

export const footprintTypeOptions: { value: FootprintType; label: string }[] = [
  { value: 'INDEX_NORMAL',     label: 'Índice Normal'      },
  { value: 'INDEX_FLAT_FOOT',  label: 'Índice Pie Plano'   },
  { value: 'INDEX_CAVUS_FOOT', label: 'Índice Pie Cavo'    },
  { value: 'FLAT_FOOT',        label: 'Pie Plano'          },
  { value: 'FLAT_NORMAL',      label: 'Pie Plano Normal'   },
  { value: 'NORMAL',           label: 'Pie Normal'         },
  { value: 'NORMAL_CAVUS',     label: 'Pie Normal Cavo'    },
  { value: 'CAVUS_FOOT',       label: 'Pie Cavo'           },
  { value: 'STRONG_CAVUS',     label: 'Pie Cavo Fuerte'    },
  { value: 'EXTREME_CAVUS',    label: 'Pie Cavo Extremo'   },
];

// ─────────────────────────────────────────────────────────────────────────────
//  FOOT ANALYSIS — Análisis de pisada principal
// ─────────────────────────────────────────────────────────────────────────────

export interface FootAnalysisResponse {
  id: number;
  analysisDate: string;           // LocalDateTime → ISO string

  // Contexto clínico
  clinicalSessionId: number;
  sessionNumber: number;
  sessionStatus: string;
  episodeId: number;
  episodeNumber: number;
  patientId: number;
  patientFullName: string;
  patientCi: string;
  employeeId: number;
  employeeFullName: string;

  // Datos del análisis
  relevantBackground: string | null;
  kinesiologicalEvaluation: string | null;
  observations: string | null;
  diagnosis: DiagnosisType | null;

  // Mediciones
  distanceIntermaleolar: number | null;
  distanceIntercondylar: number | null;
  angleLeftInternal: number | null;
  angleLeftExternal: number | null;
  angleRightInternal: number | null;
  angleRightExternal: number | null;
}

export interface FootAnalysisRequest {
  clinicalSessionId: number;
  relevantBackground?: string | null;
  kinesiologicalEvaluation?: string | null;
  observations?: string | null;
  diagnosis: DiagnosisType;
  distanceIntermaleolar?: number | null;
  distanceIntercondylar?: number | null;
  angleLeftInternal?: number | null;
  angleLeftExternal?: number | null;
  angleRightInternal?: number | null;
  angleRightExternal?: number | null;
}

export interface FootAnalysisUpdateRequest {
  relevantBackground?: string | null;
  kinesiologicalEvaluation?: string | null;
  observations?: string | null;
  diagnosis?: DiagnosisType | null;
  distanceIntermaleolar?: number | null;
  distanceIntercondylar?: number | null;
  angleLeftInternal?: number | null;
  angleLeftExternal?: number | null;
  angleRightInternal?: number | null;
  angleRightExternal?: number | null;
}

// ─────────────────────────────────────────────────────────────────────────────
//  ANALYSIS PHOTO — Fotos con anotaciones del canvas
// ─────────────────────────────────────────────────────────────────────────────

export interface AnalysisPhotoResponse {
  id: number;
  footAnalysisId: number;
  photoOrder: number;
  photoUrl: string;
  storageFileId: string;
  annotationsJson: string | null;
  isSelected: boolean;
  hasAnnotations: boolean;
}

export interface AnnotationsUpdateRequest {
  annotationsJson: string;
}

// ─────────────────────────────────────────────────────────────────────────────
//  BIOMECHANICAL ANALYSIS — Análisis biomecánico por pie
// ─────────────────────────────────────────────────────────────────────────────

export interface BiomechanicalAnalysisResponse {
  id: number;
  footAnalysisId: number;
  footSide: FootSide;
  footSideDescription: string;
  tibialMalleolarRule: TibialMalleolarRule | null;
  tibialMalleolarRuleDescription: string | null;
  shoeWear: string | null;
  tibiaPalpation: string | null;
  gait: GaitType | null;
  gaitDescription: string | null;
}

export interface BiomechanicalAnalysisRequest {
  footSide: FootSide;
  tibialMalleolarRule?: TibialMalleolarRule | null;
  shoeWear?: string | null;
  tibiaPalpation?: string | null;
  gait?: GaitType | null;
}

// ─────────────────────────────────────────────────────────────────────────────
//  FOOTPRINT ANALYSIS — Valoración de Hernández Corvo
// ─────────────────────────────────────────────────────────────────────────────

export interface FootprintAnalysisResponse {
  id: number;
  footAnalysisId: number;
  footprintType: FootprintType;
  footprintTypeDescription: string;
  notes: string | null;
}

export interface FootprintAnalysisRequest {
  footprintType: FootprintType;
  notes?: string | null;
}
