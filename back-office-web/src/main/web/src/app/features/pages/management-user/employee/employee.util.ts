import { ITableColumn } from '../../../../shared/components/table/table.model';
import { EmployeePageResponse } from '../../../../core/models/employee/employee.interface';

export const employeeActionsCode = {
  updateAction:       'updateAction',
  changeStatusAction: 'changeStatusAction',
  deleteAction:       'deleteAction',
  assignUserAction:   'assignUserAction',
};

export const employeeStatusOptions = [
  { value: 'ACTIVE',   label: 'Activo'   },
  { value: 'INACTIVE', label: 'Inactivo' },
];

export const employeeTableColumns: ITableColumn[] = [
  {
    name: 'Nombre completo',
    property: 'fullName',
    visible: true,
    isModelProperty: true,
    isSort: false,
    width: '220px',
  },
  {
    name: 'C.I.',
    property: 'ci',
    visible: true,
    isModelProperty: true,
    isSort: false,
    width: '80px',
  },
  {
    name: 'Especialidad',
    property: 'specialty',
    visible: true,
    isModelProperty: true,
    isSort: false,
    width: '190px',
  },
  {
    name: 'Departamento',
    property: 'departmentLabel',
    visible: true,
    isModelProperty: true,
    isSort: false,
    width: '100px',
  },
  {
    name: 'Usuario asignado',
    property: 'userAssigned',
    visible: true,
    isModelProperty: true,
    isSort: false,
    width: '160px',
  },
  {
    name: 'Estado',
    property: 'statusLabel',
    visible: true,
    isModelProperty: true,
    isSort: false,
    width: '90px',
    textContainerCellStyle: (row: EmployeePageResponse): { [key: string]: string } => {
      switch (row.status) {
        case 'ACTIVE':   return { color: '#2e7d32', fontWeight: 'bold' };
        case 'INACTIVE': return { color: '#c62828', fontWeight: 'bold' };
        default:         return {};
      }
    },
  },
];

