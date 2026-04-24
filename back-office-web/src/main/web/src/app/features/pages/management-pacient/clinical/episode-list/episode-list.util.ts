import { ITableColumn } from '../../../../../shared/components/table/table.model';

export const episodeActionsCode = {
  viewSessionsAction: 'VIEW_SESSIONS',
};

export const episodeTableColumns: ITableColumn[] = [
  {
    name: 'Episodio #',
    property: 'episodeNumber',
    visible: true,
    isModelProperty: true,
    isSort: false,
    width: '110px',
  },
  {
    name: 'Paciente',
    property: 'patientFullName',
    visible: true,
    isModelProperty: true,
    isSort: false,
    width: '200px',
  },
  {
    name: 'C.I.',
    property: 'patientCi',
    visible: true,
    isModelProperty: true,
    isSort: false,
    width: '90px',
  },
  {
    name: 'Fecha inicio',
    property: 'startDate',
    visible: true,
    isModelProperty: true,
    isSort: false,
    width: '120px',
  },
  {
    name: 'Fecha cierre',
    property: 'endDate',
    visible: true,
    isModelProperty: true,
    isSort: false,
    width: '120px',
  },
  {
    name: 'Estado',
    property: 'episodeStatusLabel',
    visible: true,
    isModelProperty: true,
    isSort: false,
    width: '110px',
  },
  {
    name: 'Motivo',
    property: 'reasonForAdmission',
    visible: true,
    isModelProperty: true,
    isSort: false,
  },
];

