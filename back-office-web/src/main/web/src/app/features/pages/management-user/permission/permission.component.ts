import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { BehaviorSubject } from 'rxjs';
import * as Notiflix from 'notiflix';

import { AuthService }        from '../../../../core/services/auth.service';
import { PermissionService }  from '../../../../core/services/permission/permission.service';
import { AppPermission }      from '../../../../core/models/auth.model';
import {
  ITableColumn,
  ITableEvents,
  ITableRowAction,
  PaginatedFn,
  noopTableEvent,
} from '../../../../shared/components/table/table.model';
import {
  permissionActionsCode,
  permissionStatusOptions,
  permissionTableColumns,
} from './permission.util';
import { PermissionPageResponse }        from '../../../../core/models/permission/permission.interface';
import { AddPermissionComponent }        from './add-permission/add-permission.component';
import { UpdatePermissionComponent }     from './update-permission/update-permission.component';
import { buildRightDialogConfig }        from '../../../../shared/utils/dialog.util';

@Component({
  selector: 'knv-permission',
  templateUrl: './permission.component.html',
  styleUrls: ['./permission.component.scss'],
})
export class PermissionComponent implements OnInit {

  tableEvents = new BehaviorSubject<ITableEvents>(noopTableEvent());
  dataSource  = new MatTableDataSource<PermissionPageResponse>([]);
  columns: ITableColumn[]  = [...permissionTableColumns];
  rowActions: ITableRowAction[] = [];
  pageSizeOptions: number[] = [5, 10, 25, 50];
  actions: { [key: string]: boolean } = {};
  form!: FormGroup;
  statusOptions = permissionStatusOptions;

  constructor(
    private authService: AuthService,
    private permissionService: PermissionService,
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
      listAction: this.authService.hasPermission(AppPermission.LIST_PERMISSION),
      createAction: this.authService.hasPermission(AppPermission.CREATE_PERMISSION),
      updateAction: this.authService.hasPermission(AppPermission.UPDATE_PERMISSION),
      changeStatusAction: this.authService.hasPermission(AppPermission.CHANGE_PERMISSION_STATUS),
      deleteAction: this.authService.hasPermission(AppPermission.DELETE_PERMISSION),
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
        actionCode: permissionActionsCode.updateAction,
        icon: 'edit',
      });
    }
    if (this.actions['changeStatusAction']) {
      actions.push({
        action: 'Cambiar estado',
        actionCode: permissionActionsCode.changeStatusAction,
        icon: 'toggle_on',
      });
    }
    if (this.actions['deleteAction']) {
      actions.push({
        action: 'Eliminar',
        actionCode: permissionActionsCode.deleteAction,
        icon: 'delete',
      });
    }
    return actions;
  };

  requestPermissionListFn: PaginatedFn = (queryParams: any) => {
    const extra = this.getFilterParams();
    return this.permissionService.getAll({ ...queryParams, ...extra });
  };

  itemPermissionFormatterFn = (content: PermissionPageResponse[]): PermissionPageResponse[] =>
    content.map(item => ({
      ...item,
      statusLabel: item.status === 'ACTIVO' ? 'ACTIVO' : 'INACTIVO',
    }));

  protected tableActionManager = (event: ITableEvents): void => {
    if (event.event === 'ROW_CLICK') {
      this.rowActionEvent(event.data);
    }
  };

  protected rowActionEvent = (event: { item: PermissionPageResponse; actionCode: string }): void => {
    const { item, actionCode } = event;
    if (actionCode === permissionActionsCode.updateAction) this.updatePermission(item);
    if (actionCode === permissionActionsCode.changeStatusAction) this.changePermissionStatus(item);
    if (actionCode === permissionActionsCode.deleteAction) this.deletePermission(item);
  };

  createPermission(): void {
    const ref = this.matDialog.open(AddPermissionComponent, buildRightDialogConfig(null));
    ref.afterClosed().subscribe((ok: boolean | undefined) => {
      if (ok) this.tableEvents.next({ event: 'RELOAD_PAGE' });
    });
  }

  updatePermission(item: PermissionPageResponse): void {
    const ref = this.matDialog.open(UpdatePermissionComponent, buildRightDialogConfig({ permission: item }),);
    ref.afterClosed().subscribe((ok: boolean | undefined) => {
      if (ok) this.tableEvents.next({ event: 'RELOAD_PAGE' });
    });
  }

  changePermissionStatus(item: PermissionPageResponse): void {
    const newStatus = item.status === 'ACTIVO' ? 'INACTIVE' : 'ACTIVE';
    const actionLabel = newStatus === 'ACTIVE' ? 'activar' : 'desactivar';
    const successLabel = newStatus === 'ACTIVE' ? 'activado'  : 'desactivado';

    Notiflix.Confirm.show(
      'Cambiar estado',
      `¿Deseas ${actionLabel} el permiso "${item.name}"?`,
      'Sí',
      'No',
      () => {
        this.permissionService.changeStatus(item.id, { status: newStatus }).subscribe({
          next: () => {
            Notiflix.Report.success(
              'Operación Exitosa',
              `El permiso fue ${successLabel} con éxito.`,
              'OK',
            );
            this.tableEvents.next({ event: 'RELOAD_PAGE' });
          },
          error: err => this.handleError(err),
        });
      },
    );
  }

  deletePermission(item: PermissionPageResponse): void {
    Notiflix.Confirm.show(
      'Eliminar permiso',
      `¿Está seguro de eliminar el permiso "${item.name}"?`,
      'Sí',
      'No',
      () => {
        this.permissionService.delete(item.id).subscribe({
          next: () => {
            Notiflix.Report.success(
              'Operación Exitosa',
              'El permiso ha sido eliminado con éxito.',
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
    console.error('Error en PermissionComponent:', error);
    Notiflix.Report.failure(
      'Error',
      error?.message ?? 'Ocurrió un error inesperado.',
      'OK',
    );
  };
}



