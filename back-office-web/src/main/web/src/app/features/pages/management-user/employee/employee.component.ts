import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatTableDataSource } from '@angular/material/table';
import { MatDialog } from '@angular/material/dialog';
import { BehaviorSubject } from 'rxjs';
import { Router } from '@angular/router';
import * as Notiflix from 'notiflix';

import { AuthService }     from '../../../../core/services/auth.service';
import { EmployeeService } from '../../../../core/services/employee/employee.service';
import { AppPermission }   from '../../../../core/models/auth.model';
import {
  ITableColumn,
  ITableEvents,
  ITableRowAction,
  PaginatedFn,
  noopTableEvent,
} from '../../../../shared/components/table/table.model';
import {
  employeeActionsCode,
  employeeStatusOptions,
  employeeTableColumns,
} from './employee.util';
import {
  bolivianDepartmentOptions,
  EmployeePageResponse,
} from '../../../../core/models/employee/employee.interface';
import { AssignUserEmployeeComponent } from './assign-user-employee/assign-user-employee.component';
import { buildRightDialogConfig }      from '../../../../shared/utils/dialog.util';

@Component({
  selector: 'knv-employee',
  templateUrl: './employee.component.html',
  styleUrls: ['./employee.component.scss'],
})
export class EmployeeComponent implements OnInit {

  tableEvents  = new BehaviorSubject<ITableEvents>(noopTableEvent());
  dataSource   = new MatTableDataSource<EmployeePageResponse>([]);
  columns: ITableColumn[]       = [...employeeTableColumns];
  rowActions: ITableRowAction[] = [];
  pageSizeOptions: number[]     = [5, 10, 25, 50];
  actions: { [key: string]: boolean } = {};
  form!: FormGroup;
  statusOptions  = employeeStatusOptions;

  constructor(
    private authService:     AuthService,
    private employeeService: EmployeeService,
    private router:          Router,
    private matDialog:       MatDialog,
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
      listAction: this.authService.hasPermission(AppPermission.LIST_EMPLOYEE),
      createAction: this.authService.hasPermission(AppPermission.CREATE_EMPLOYEE),
      updateAction: this.authService.hasPermission(AppPermission.UPDATE_EMPLOYEE),
      changeStatusAction: this.authService.hasPermission(AppPermission.CHANGE_EMPLOYEE_STATUS),
      deleteAction: this.authService.hasPermission(AppPermission.DELETE_EMPLOYEE),
      assignUserAction: this.authService.hasPermission(AppPermission.ASSIGN_USER_TO_EMPLOYEE),
    };
    this.rowActions = this.buildRowActions();
    if (this.rowActions.length === 0) {
      this.tableEvents.next({ event: 'RELOAD_ACTIONS' });
    }
  }

  protected buildRowActions = (): ITableRowAction[] => {
    const actions: ITableRowAction[] = [];
    if (this.actions['updateAction']) {
      actions.push({ action: 'Actualizar',
        actionCode: employeeActionsCode.updateAction,
        icon: 'edit' });
    }
    if (this.actions['assignUserAction']) {
      actions.push({ action: 'Asignar usuario',
        actionCode: employeeActionsCode.assignUserAction,
        icon: 'manage_accounts' });
    }
    if (this.actions['changeStatusAction']) {
      actions.push({ action: 'Cambiar estado',
        actionCode: employeeActionsCode.changeStatusAction,
        icon: 'toggle_on' });
    }
    if (this.actions['deleteAction']) {
      actions.push({ action: 'Eliminar',
        actionCode: employeeActionsCode.deleteAction,
        icon: 'delete' });
    }
    return actions;
  };

  requestEmployeeListFn: PaginatedFn = (queryParams: any) => {
    const extra = this.getFilterParams();
    return this.employeeService.getAll({ ...queryParams, ...extra });
  };

  itemEmployeeFormatterFn = (content: EmployeePageResponse[]): EmployeePageResponse[] =>
    content.map(item => ({
      ...item,
      fullName:        `${item.firstName} ${item.paternalSurname}${item.maternalSurname ? ' ' + item.maternalSurname : ''}`,
      statusLabel:     item.status === 'ACTIVE' ? 'ACTIVO' : 'INACTIVO',
      departmentLabel: bolivianDepartmentOptions.find(d => d.value === item.department)?.label ?? item.department,
      expeditionLabel: bolivianDepartmentOptions.find(d => d.value === item.expedition)?.label ?? item.expedition,
      userAssigned:    item.username ?? '—',
    }));

  protected tableActionManager = (event: ITableEvents): void => {
    if (event.event === 'ROW_CLICK') {
      this.rowActionEvent(event.data);
    }
  };

  protected rowActionEvent = (event: { item: EmployeePageResponse; actionCode: string }): void => {
    const { item, actionCode } = event;
    if (actionCode === employeeActionsCode.updateAction) this.updateEmployee(item);
    if (actionCode === employeeActionsCode.assignUserAction) this.assignUser(item);
    if (actionCode === employeeActionsCode.changeStatusAction) this.changeEmployeeStatus(item);
    if (actionCode === employeeActionsCode.deleteAction) this.deleteEmployee(item);
  };

  createEmployee(): void {
    this.router.navigate(['/management-users/employees/add']);
  }

  updateEmployee(item: EmployeePageResponse): void {
    this.router.navigate(['/management-users/employees/update', item.id]);
  }

  assignUser(item: EmployeePageResponse): void {
    const ref = this.matDialog.open(
      AssignUserEmployeeComponent,
      buildRightDialogConfig({ employee: item }),
    );
    ref.afterClosed().subscribe((ok: boolean | undefined) => {
      if (ok) this.tableEvents.next({ event: 'RELOAD_PAGE' });
    });
  }

  changeEmployeeStatus(item: EmployeePageResponse): void {
    const newStatus = item.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
    const actionLabel = newStatus === 'ACTIVE' ? 'activar'   : 'desactivar';
    const successLabel = newStatus === 'ACTIVE' ? 'activado'  : 'desactivado';

    Notiflix.Confirm.show(
      'Cambiar estado',
      `¿Deseas ${actionLabel} al empleado "${item.firstName} ${item.paternalSurname}"?`,
      'Sí', 'No',
      () => {
        this.employeeService.changeStatus(item.id, { status: newStatus }).subscribe({
          next: () => {
            Notiflix.Report.success('Operación Exitosa',
              `El empleado fue ${successLabel} con éxito.`,
              'OK');
            this.tableEvents.next({ event: 'RELOAD_PAGE' });
          },
          error: err => this.handleError(err),
        });
      },
    );
  }

  deleteEmployee(item: EmployeePageResponse): void {
    Notiflix.Confirm.show(
      'Eliminar empleado',
      `¿Está seguro de eliminar al empleado "${item.firstName} ${item.paternalSurname}"?`,
      'Sí', 'No',
      () => {
        this.employeeService.delete(item.id).subscribe({
          next: () => {
            Notiflix.Report.success('Operación Exitosa',
              'El empleado fue eliminado con éxito.',
              'OK');
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
    console.error('Error en EmployeeComponent:', error);
    Notiflix.Report.failure('Error', error?.message ?? 'Ocurrió un error inesperado.', 'OK');
  };
}



