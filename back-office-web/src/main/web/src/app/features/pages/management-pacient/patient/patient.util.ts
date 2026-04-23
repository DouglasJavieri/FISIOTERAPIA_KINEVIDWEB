import { ITableColumn } from '../../../../shared/components/table/table.model';
import { PatientPageResponse } from '../../../../core/models/patient/patient.interface';

export const patientActionsCode = {
  updateAction:       'updateAction',
  changeStatusAction: 'changeStatusAction',
  deleteAction:       'deleteAction',
};

export const patientStatusFilterOptions = [
  { value: 'ACTIVE',    label: 'Activo'      },
  { value: 'INACTIVE',  label: 'Inactivo'    },
  { value: 'DISCHARGE', label: 'Alta médica' },
];

export const patientTableColumns: ITableColumn[] = [
  {
    name: 'Nombre completo',
    property: 'fullName',
    visible: true,
    isModelProperty: true,
    isSort: false,
    width: '230px',
  },
  {
    name: 'C.I.',
    property: 'ci',
    visible: true,
    isModelProperty: true,
    isSort: false,
    width: '90px',
  },
  {
    name: 'Género',
    property: 'genderLabel',
    visible: true,
    isModelProperty: true,
    isSort: false,
    width: '100px',
  },
  {
    name: 'Teléfono',
    property: 'phone',
    visible: true,
    isModelProperty: true,
    isSort: false,
    width: '110px',
  },
  {
    name: 'Edad',
    property: 'age',
    visible: true,
    isModelProperty: true,
    isSort: false,
    width: '70px',
  },
  {
    name: 'Estado',
    property: 'statusLabel',
    visible: true,
    isModelProperty: true,
    isSort: false,
    width: '100px',
    textContainerCellStyle: (row: PatientPageResponse): { [key: string]: string } => {
      switch (row.status) {
        case 'ACTIVE':     return { color: '#2e7d32', fontWeight: 'bold' };
        case 'INACTIVE':   return { color: '#c62828', fontWeight: 'bold' };
        case 'DISCHARGE':  return { color: '#1565c0', fontWeight: 'bold' };
        case 'ELIMINATION': return { color: '#757575', fontWeight: 'bold' };
        default:           return {};
      }
    },
  },
];

