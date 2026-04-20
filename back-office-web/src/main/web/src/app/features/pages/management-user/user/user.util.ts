import { ITableColumn } from '../../../../shared/components/table/table.model';
import { UserPageResponse } from '../../../../core/models/user/user.interface';

export const userActionsCode = {
  updateAction:       'updateAction',
  changeStatusAction: 'changeStatusAction',
  deleteAction:       'deleteAction',
};

export const userStatusOptions = [
  { value: 'ACTIVE',   label: 'Activo'   },
  { value: 'INACTIVE', label: 'Inactivo' },
];

export const userTableColumns: ITableColumn[] = [
  {
    name: 'Usuario',
    property: 'username',
    visible: true,
    isModelProperty: true,
    isSort: false,
    width: '180px',
  },
  {
    name: 'Correo electrónico',
    property: 'email',
    visible: true,
    isModelProperty: true,
    isSort: false,
    width: '260px',
  },
  {
    name: 'Estado',
    property: 'statusLabel',
    visible: true,
    isModelProperty: true,
    isSort: false,
    width: '120px',
    textContainerCellStyle: (row: UserPageResponse): { [key: string]: string } => {
      switch (row.status) {
        case 'ACTIVE':
          return { color: '#2e7d32', fontWeight: 'bold' };
        case 'INACTIVE':
          return { color: '#c62828', fontWeight: 'bold' };
        default:
          return {};
      }
    },
  },
];


