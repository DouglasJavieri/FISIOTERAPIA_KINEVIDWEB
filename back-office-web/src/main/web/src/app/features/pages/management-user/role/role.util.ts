import { ITableColumn } from '../../../../shared/components/table/table.model';
import { RolePageResponse } from '../../../../core/models/roles/role.interface';

export const roleActionsCode = {
  updateAction: 'updateAction',
  changeStatusAction: 'changeStatusAction',
  deleteAction: 'deleteAction',
};

export const roleStatusOptions = [
  { value: 'ACTIVE',   label: 'Activo'   },
  { value: 'INACTIVE', label: 'Inactivo' },
];

export const roleTableColumns: ITableColumn[] = [
  {
    name: 'Nombre',
    property: 'name',
    visible: true,
    isModelProperty: true,
    isSort: false,
    width: '200px',
  },
  {
    name: 'Descripción',
    property: 'description',
    visible: true,
    isModelProperty: true,
    isSort: false,
    width: '280px',
  },
  {
    name: 'Estado',
    property: 'statusLabel',
    visible: true,
    isModelProperty: true,
    isSort: false,
    width: '120px',
    textContainerCellStyle: (row: RolePageResponse): { [key: string]: string } => {
      switch (row.status) {
        case 'ACTIVO':
          return { color: '#2e7d32', fontWeight: 'bold' };
        case 'INACTIVO':
          return { color: '#c62828', fontWeight: 'bold' };
        default:
          return {};
      }
    },
  },
];

