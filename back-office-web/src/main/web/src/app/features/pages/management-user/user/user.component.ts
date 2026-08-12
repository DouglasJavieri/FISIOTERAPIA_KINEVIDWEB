import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { BehaviorSubject } from 'rxjs';
import * as Notiflix from 'notiflix';
import {saveAs} from "file-saver";
import { AuthService }   from '../../../../core/services/auth.service';
import { UserService }   from '../../../../core/services/user/user.service';
import { AppPermission } from '../../../../core/models/auth.model';
import {
  ITableColumn,
  ITableEvents,
  ITableRowAction,
  PaginatedFn,
  noopTableEvent,
} from '../../../../shared/components/table/table.model';
import {
  userActionsCode,
  userStatusOptions,
  userTableColumns,
} from './user.util';
import { UserPageResponse }      from '../../../../core/models/user/user.interface';
import { AddUserComponent }      from './add-user/add-user.component';
import { UpdateUserComponent }   from './update-user/update-user.component';
import { buildRightDialogConfig } from '../../../../shared/utils/dialog.util';
import {ReportService} from "../../../../core/services/report/report.service";
import {fileUtil} from "../../../../core/services/util/file.util";

@Component({
  selector: 'knv-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss'],
})
export class UserComponent implements OnInit {

  tableEvents  = new BehaviorSubject<ITableEvents>(noopTableEvent());
  dataSource   = new MatTableDataSource<UserPageResponse>([]);
  columns: ITableColumn[]       = [...userTableColumns];
  rowActions: ITableRowAction[] = [];
  pageSizeOptions: number[]     = [5, 10, 25, 50];
  actions: { [key: string]: boolean } = {};
  form!: FormGroup;
  statusOptions = userStatusOptions;
  document: any;


  constructor(
    private authService: AuthService,
    private userService: UserService,
    private matDialog: MatDialog,
    private reportService: ReportService,
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
      listAction:         this.authService.hasPermission(AppPermission.LIST_USER),
      createAction:       this.authService.hasPermission(AppPermission.CREATE_USER),
      updateAction:       this.authService.hasPermission(AppPermission.UPDATE_USER),
      changeStatusAction: this.authService.hasPermission(AppPermission.CHANGE_USER_STATUS),
      deleteAction:       this.authService.hasPermission(AppPermission.DELETE_USER),
    };
    this.rowActions = this.buildRowActions();
    if (this.rowActions.length === 0) {
      this.tableEvents.next({ event: 'RELOAD_ACTIONS' });
    }
  }

  protected buildRowActions = (): ITableRowAction[] => {
    const actions: ITableRowAction[] = [];
    if (this.actions['updateAction']) {
      actions.push({ action: 'Actualizar', actionCode: userActionsCode.updateAction, icon: 'edit' });
    }
    if (this.actions['changeStatusAction']) {
      actions.push({ action: 'Cambiar estado', actionCode: userActionsCode.changeStatusAction, icon: 'toggle_on' });
    }
    if (this.actions['deleteAction']) {
      actions.push({ action: 'Eliminar', actionCode: userActionsCode.deleteAction, icon: 'delete' });
    }
    return actions;
  };

  requestUserListFn: PaginatedFn = (queryParams: any) => {
    const extra = this.getFilterParams();
    return this.userService.getAll({ ...queryParams, ...extra });
  };

  itemUserFormatterFn = (content: UserPageResponse[]): UserPageResponse[] =>
    content.map(item => ({
      ...item,
      roleName:    item.roleName ?? '—',
      statusLabel: item.status === 'ACTIVE' ? 'ACTIVO' : 'INACTIVO',
    }));

  protected tableActionManager = (event: ITableEvents): void => {
    if (event.event === 'ROW_CLICK') {
      this.rowActionEvent(event.data);
    }
  };

  protected rowActionEvent = (event: { item: UserPageResponse; actionCode: string }): void => {
    const { item, actionCode } = event;
    if (actionCode === userActionsCode.updateAction)       this.updateUser(item);
    if (actionCode === userActionsCode.changeStatusAction) this.changeUserStatus(item);
    if (actionCode === userActionsCode.deleteAction)       this.deleteUser(item);
  };

  report() {
    let titleReport: string = `Reporte usuario`;


    this.reportService.reportSheetPayrollPdf().subscribe({
      next: (response) => {
        Notiflix.Loading.remove();
        if (response) {
          this.document = response;
          const arrayBytes: Uint8Array = fileUtil.codificarArrayBytes(this.document.body);
          const blob = new Blob([arrayBytes.buffer], { type: this.document.body.tipoMime });
          saveAs(blob, `${titleReport}.pdf`);

        }
      },
      error: (err) => {
        Notiflix.Loading.remove();
        console.error('Error al generar el PDF:', err);
      }
    });


  }

  createUser(): void {
    const ref = this.matDialog.open(AddUserComponent, buildRightDialogConfig(null));
    ref.afterClosed().subscribe((ok: boolean | undefined) => {
      if (ok) this.tableEvents.next({ event: 'RELOAD_PAGE' });
    });
  }

  updateUser(item: UserPageResponse): void {
    const ref = this.matDialog.open(UpdateUserComponent, buildRightDialogConfig({ user: item }));
    ref.afterClosed().subscribe((ok: boolean | undefined) => {
      if (ok) this.tableEvents.next({ event: 'RELOAD_PAGE' });
    });
  }

  changeUserStatus(item: UserPageResponse): void {
    const newStatus   = item.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
    const actionLabel  = newStatus === 'ACTIVE' ? 'activar'   : 'desactivar';
    const successLabel = newStatus === 'ACTIVE' ? 'activado'  : 'desactivado';

    Notiflix.Confirm.show(
      'Cambiar estado',
      `¿Deseas ${actionLabel} al usuario "${item.username}"?`,
      'Sí', 'No',
      () => {
        this.userService.changeStatus(item.id, { status: newStatus }).subscribe({
          next: () => {
            Notiflix.Report.success('Operación Exitosa', `El usuario fue ${successLabel} con éxito.`, 'OK');
            this.tableEvents.next({ event: 'RELOAD_PAGE' });
          },
          error: err => this.handleError(err),
        });
      },
    );
  }

  deleteUser(item: UserPageResponse): void {
    Notiflix.Confirm.show(
      'Eliminar usuario',
      `¿Está seguro de eliminar al usuario "${item.username}"?`,
      'Sí', 'No',
      () => {
        this.userService.delete(item.id).subscribe({
          next: () => {
            Notiflix.Report.success('Operación Exitosa', 'El usuario fue eliminado con éxito.', 'OK');
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
    console.error('Error en UserComponent:', error);
    Notiflix.Report.failure('Error', error?.message ?? 'Ocurrió un error inesperado.', 'OK');
  };
}

