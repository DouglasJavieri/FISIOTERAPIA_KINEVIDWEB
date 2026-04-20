import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { BehaviorSubject } from 'rxjs';
import * as Notiflix from 'notiflix';

import { AuthService }    from '../../../../core/services/auth.service';
import { RoleService }    from '../../../../core/services/roles/role.service';
import { AppPermission }  from '../../../../core/models/auth.model';
import {
  ITableColumn,
  ITableEvents,
  ITableRowAction,
  PaginatedFn,
  noopTableEvent,
} from '../../../../shared/components/table/table.model';
import {
  roleActionsCode,
  roleStatusOptions,
  roleTableColumns,
} from './role.util';
import { RolePageResponse }          from '../../../../core/models/roles/role.interface';
import { AddRoleComponent }          from './add-role/add-role.component';
import { UpdateRoleComponent }       from './update-role/update-role.component';
import { buildRightDialogConfig }    from '../../../../shared/utils/dialog.util';

@Component({
  selector: 'knv-role',
  templateUrl: './role.component.html',
  styleUrls: ['./role.component.scss'],
})
export class RoleComponent implements OnInit {

  tableEvents  = new BehaviorSubject<ITableEvents>(noopTableEvent());
  dataSource   = new MatTableDataSource<RolePageResponse>([]);
  columns: ITableColumn[]       = [...roleTableColumns];
  rowActions: ITableRowAction[] = [];
  pageSizeOptions: number[]     = [5, 10, 25, 50];
  actions: { [key: string]: boolean } = {};
  form!: FormGroup;
  statusOptions = roleStatusOptions;

  constructor(
    private authService: AuthService,
    private roleService: RoleService,
    private matDialog: MatDialog,
  ) {}

  ngOnInit(): void {
    this.buildForm();
    this.loadActions();
    this.tableEvents.subscribe(this.tableActionManager);
  }

  private buildForm(): void {
    this.form = new FormGroup({
      status: new FormControl(null),
    });
  }

  private loadActions(): void {
    this.actions = {
      listAction: this.authService.hasPermission(AppPermission.LIST_ROLE),
      createAction: this.authService.hasPermission(AppPermission.CREATE_ROLE),
      updateAction: this.authService.hasPermission(AppPermission.UPDATE_ROLE),
      changeStatusAction: this.authService.hasPermission(AppPermission.CHANGE_ROLE_STATUS),
      deleteAction: this.authService.hasPermission(AppPermission.DELETE_ROLE),
    };
    this.rowActions = this.buildRowActions();
    if (this.rowActions.length === 0) {
      this.tableEvents.next({ event: 'RELOAD_ACTIONS' });
    }
  }

  protected buildRowActions = (): ITableRowAction[] => {
    const actions: ITableRowAction[] = [];
    if (this.actions['updateAction']) {
      actions.push({
        action: 'Actualizar',
        actionCode: roleActionsCode.updateAction,
        icon: 'edit',
      });
    }
    if (this.actions['changeStatusAction']) {
      actions.push({
        action: 'Cambiar estado',
        actionCode: roleActionsCode.changeStatusAction,
        icon: 'toggle_on',
      });
    }
    if (this.actions['deleteAction']) {
      actions.push({
        action: 'Eliminar',
        actionCode: roleActionsCode.deleteAction,
        icon: 'delete',
      });
    }
    return actions;
  };

  requestRoleListFn: PaginatedFn = (queryParams: any) => {
    const extra = this.getFilterParams();
    return this.roleService.getAll({ ...queryParams, ...extra });
  };

  itemRoleFormatterFn = (content: RolePageResponse[]): RolePageResponse[] =>
    content.map(item => ({
      ...item,
      statusLabel: item.status === 'ACTIVO' ? 'ACTIVO' : 'INACTIVO',
    }));

  protected tableActionManager = (event: ITableEvents): void => {
    if (event.event === 'ROW_CLICK') {
      this.rowActionEvent(event.data);
    }
  };

  protected rowActionEvent = (event: { item: RolePageResponse; actionCode: string }): void => {
    const { item, actionCode } = event;
    if (actionCode === roleActionsCode.updateAction) this.updateRole(item);
    if (actionCode === roleActionsCode.changeStatusAction) this.changeRoleStatus(item);
    if (actionCode === roleActionsCode.deleteAction) this.deleteRole(item);
  };

  createRole(): void {
    const ref = this.matDialog.open(AddRoleComponent, buildRightDialogConfig(null));
    ref.afterClosed().subscribe((ok: boolean | undefined) => {
      if (ok) this.tableEvents.next({ event: 'RELOAD_PAGE' });
    });
  }

  updateRole(item: RolePageResponse): void {
    const ref = this.matDialog.open(UpdateRoleComponent, buildRightDialogConfig({ role: item }));
    ref.afterClosed().subscribe((ok: boolean | undefined) => {
      if (ok) this.tableEvents.next({ event: 'RELOAD_PAGE' });
    });
  }

  changeRoleStatus(item: RolePageResponse): void {
    const newStatus    = item.status === 'ACTIVO' ? 'INACTIVE' : 'ACTIVE';
    const actionLabel  = newStatus === 'ACTIVE' ? 'activar' : 'desactivar';
    const successLabel = newStatus === 'ACTIVE' ? 'activado' : 'desactivado';

    Notiflix.Confirm.show(
      'Cambiar estado',
      `¿Deseas ${actionLabel} el rol "${item.name}"?`,
      'Sí',
      'No',
      () => {
        this.roleService.changeStatus(item.id, { status: newStatus }).subscribe({
          next: () => {
            Notiflix.Report.success(
              'Operación Exitosa',
              `El rol fue ${successLabel} con éxito.`,
              'OK',
            );
            this.tableEvents.next({ event: 'RELOAD_PAGE' });
          },
          error: err => this.handleError(err),
        });
      },
    );
  }

  deleteRole(item: RolePageResponse): void {
    Notiflix.Confirm.show(
      'Eliminar rol',
      `¿Está seguro de eliminar el rol "${item.name}"?`,
      'Sí',
      'No',
      () => {
        this.roleService.delete(item.id).subscribe({
          next: () => {
            Notiflix.Report.success(
              'Operación Exitosa',
              'El rol ha sido eliminado con éxito.',
              'OK',
            );
            this.tableEvents.next({ event: 'RELOAD_PAGE' });
          },
          error: err => this.handleError(err),
        });
      },
    );
  }

  applyFilter(): void {
    const aditionalParams = this.getFilterParams();
    this.tableEvents.next({ event: 'RESET', data: { aditionalParams } });
  }

  clearFilter(): void {
    this.form.get('status')?.patchValue(null);
    this.tableEvents.next({ event: 'RESET', data: { aditionalParams: {} } });
  }

  private getFilterParams(): Record<string, any> {
    const status = this.form.get('status')?.value;
    return status ? { status } : {};
  }

  protected handleError = (error: any): void => {
    console.error('Error en RoleComponent:', error);
    Notiflix.Report.failure(
      'Error',
      error?.message ?? 'Ocurrió un error inesperado.',
      'OK',
    );
  };
}

