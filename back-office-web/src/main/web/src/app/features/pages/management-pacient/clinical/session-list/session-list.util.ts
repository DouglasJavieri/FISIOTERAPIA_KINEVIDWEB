import { ITableColumn } from '../../../../../shared/components/table/table.model';
import { ClinicalSessionResponse } from '../../../../../core/models/clinical/clinical.interface';

export const sessionActionsCode = {
  editAction:         'EDIT_SESSION',
  changeStatusAction: 'CHANGE_STATUS_SESSION',
  deleteAction:       'DELETE_SESSION',
};

export const sessionTableColumns: ITableColumn[] = [
  {
    name: '',
    property: 's',
    visible: true,
    isModelProperty: true,
    isSort: false,
    width: '10px',
  },
  {
    name: 'Sesión #',
    property: 'sessionNumber',
    visible: true,
    isModelProperty: true,
    isSort: false,
    width: '100px',
  },
  {
    name: 'Fecha',
    property: 'sessionDate',
    visible: true,
    isModelProperty: true,
    isSort: false,
    width: '120px',
  },
  {
    name: 'Terapeuta',
    property: 'employeeFullName',
    visible: true,
    isModelProperty: true,
    isSort: false,
    width: '180px',
  },
  {
    name: 'Motivo',
    property: 'reasonForConsultation',
    visible: true,
    isModelProperty: true,
    isSort: false,
  },
  {
    name: 'Estado',
    property: 'sessionStatusLabel',
    visible: true,
    isModelProperty: true,
    isSort: false,
    width: '120px',
    textContainerCellStyle: (row: ClinicalSessionResponse): { [key: string]: string } => {
      switch (row.sessionStatus) {
        case 'OPEN':      return { color: '#2e7d32', fontWeight: 'bold' };
        case 'CLOSED':    return { color: '#1565c0', fontWeight: 'bold' };
        case 'CANCELLED': return { color: '#c62828', fontWeight: 'bold' };
        default:          return {};
      }
    },
  },
  {
    name: 'Tiene imagen',
    property: 'hasImageAnalysis',
    visible: true,
    isModelProperty: true,
    isSort: false,
    width: '110px',
  },
];

