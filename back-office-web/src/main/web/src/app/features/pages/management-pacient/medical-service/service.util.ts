import { ITableColumn } from '../../../../shared/components/table/table.model';
import { MedicalServicePageResponse } from '../../../../core/models/medical-service/medical-service.interface';

export const serviceActionsCode = {
  updateAction:       'updateAction',
  changeStatusAction: 'changeStatusAction',
  deleteAction:       'deleteAction',
};

export const serviceStatusOptions = [
  { value: 'ACTIVE',   label: 'Activo'   },
  { value: 'INACTIVE', label: 'Inactivo' },
];

export const serviceCategoryFilterOptions = [
  { value: 'REHABILITATION',        label: 'Rehabilitación' },
  { value: 'ELECTROTHERAPY',        label: 'Electroterapia' },
  { value: 'SPORTS_KINESIOLOGY',    label: 'Kinesiología Deportiva' },
  { value: 'THERAPEUTIC_GYMNASIUM', label: 'Gimnasio Terapéutico' },
  { value: 'POSTURAL_ANALYSIS',     label: 'Análisis Postural' },
  { value: 'THERMOTHERAPY',         label: 'Termoterapia' },
  { value: 'MANUAL_THERAPY',        label: 'Terapia Manual' },
  { value: 'KINESIOTHERAPY',        label: 'Kinesioterapia' },
  { value: 'OTHER',                 label: 'Otro' },
];

export const serviceTableColumns: ITableColumn[] = [
  {
    name: 'Nombre',
    property: 'name',
    visible: true,
    isModelProperty: true,
    isSort: false,
    width: '220px',
  },
  {
    name: 'Categoría',
    property: 'categoryDescription',
    visible: true,
    isModelProperty: true,
    isSort: false,
    width: '190px',
  },
  {
    name: 'Duración (min)',
    property: 'durationMinutes',
    visible: true,
    isModelProperty: true,
    isSort: false,
    width: '120px',
  },
  {
    name: 'Precio (Bs.)',
    property: 'price',
    visible: true,
    isModelProperty: true,
    isSort: false,
    width: '110px',
  },
  {
    name: 'Estado',
    property: 'statusLabel',
    visible: true,
    isModelProperty: true,
    isSort: false,
    width: '90px',
    textContainerCellStyle: (row: MedicalServicePageResponse): { [key: string]: string } => {
      switch (row.status) {
        case 'ACTIVE':   return { color: '#2e7d32', fontWeight: 'bold' };
        case 'INACTIVE': return { color: '#c62828', fontWeight: 'bold' };
        default:         return {};
      }
    },
  },
];

