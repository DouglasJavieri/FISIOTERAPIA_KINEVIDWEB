import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatTableDataSource } from '@angular/material/table';
import { BehaviorSubject } from 'rxjs';
import { Router } from '@angular/router';
import * as Notiflix from 'notiflix';

import {  AuthService }    from '../../../../core/services/auth.service';
import { PatientService } from '../../../../core/services/patient/patient.service';
import { AppPermission }  from '../../../../core/models/auth.model';
import {
  ITableColumn,
  ITableEvents,
  ITableRowAction,
  PaginatedFn,
  noopTableEvent,
} from '../../../../shared/components/table/table.model';
import {
  patientActionsCode,
  patientStatusFilterOptions,
  patientTableColumns,
} from './patient.util';
import {
  genderOptions,
  PatientPageResponse,
  patientStatusOptions,
} from '../../../../core/models/patient/patient.interface';

@Component({
  selector: 'knv-patient',
  templateUrl: './patient.component.html',
  styleUrls: ['./patient.component.scss'],
})
export class PatientComponent implements OnInit {

  tableEvents = new BehaviorSubject<ITableEvents>(noopTableEvent());
  dataSource = new MatTableDataSource<PatientPageResponse>([]);
  columns: ITableColumn[] = [...patientTableColumns];
  rowActions: ITableRowAction[] = [];
  pageSizeOptions: number[] = [5, 10, 25, 50];
  actions: { [key: string]: boolean } = {};
  form!: FormGroup;

  statusOptions = patientStatusFilterOptions;

  constructor(
    private authService: AuthService,
    private patientService: PatientService,
    private router: Router,
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
      listAction: this.authService.hasPermission(AppPermission.LIST_PATIENT),
      createAction: this.authService.hasPermission(AppPermission.CREATE_PATIENT),
      updateAction: this.authService.hasPermission(AppPermission.UPDATE_PATIENT),
      changeStatusAction:this.authService.hasPermission(AppPermission.CHANGE_PATIENT_STATUS),
      deleteAction: this.authService.hasPermission(AppPermission.DELETE_PATIENT),
      viewProfileAction: this.authService.hasPermission(AppPermission.VIEW_PATIENT),
    };
    this.rowActions = this.buildRowActions();
  }

  protected buildRowActions = (): ITableRowAction[] => {
    const actions: ITableRowAction[] = [];
    if (this.actions['updateAction']) {
      actions.push({
        action: 'Actualizar',
        actionCode: patientActionsCode.updateAction,
        icon: 'edit',
      });
    }
    if (this.actions['viewProfileAction']) {
      actions.push({
        action: 'Ver perfil',
        actionCode: patientActionsCode.viewProfileAction,
        icon: 'folder_open',
      });
    }
    if (this.actions['changeStatusAction']) {
      actions.push({
        action: 'Cambiar estado',
        actionCode: patientActionsCode.changeStatusAction,
        icon: 'toggle_on',
      });
    }
    if (this.actions['deleteAction']) {
      actions.push({
        action: 'Eliminar',
        actionCode: patientActionsCode.deleteAction,
        icon: 'delete',
      });
    }
    return actions;
  };

  requestPatientListFn: PaginatedFn = (queryParams: any) => {
    const extra = this.getFilterParams();
    return this.patientService.getAll({ ...queryParams, ...extra });
  };

  itemPatientFormatterFn = (content: PatientPageResponse[]): PatientPageResponse[] =>
    content.map(item => ({
      ...item,
      fullName: `${item.firstName} ${item.paternalSurname}${
        item.maternalSurname ? ' ' + item.maternalSurname : ''
      }`,
      genderLabel: genderOptions.find(g => g.value === item.gender)?.label ?? item.gender,
      phone: item.phone ?? '—',
    }));

  protected tableActionManager = (event: ITableEvents): void => {
    if (event.event === 'ROW_CLICK') {
      this.rowActionEvent(event.data);
    }
  };

  protected rowActionEvent = (event: { item: PatientPageResponse; actionCode: string }): void => {
    const { item, actionCode } = event;
    if (actionCode === patientActionsCode.viewProfileAction) this.viewProfile(item);
    if (actionCode === patientActionsCode.updateAction) this.updatePatient(item);
    if (actionCode === patientActionsCode.changeStatusAction) this.changePatientStatus(item);
    if (actionCode === patientActionsCode.deleteAction) this.deletePatient(item);
  };

  viewProfile(item: PatientPageResponse): void {
    this.router.navigate(['/management-pacient/patients', item.id]);
  }

  createPatient(): void {
    this.router.navigate(['/management-pacient/patients/add']);
  }

  updatePatient(item: PatientPageResponse): void {
    this.router.navigate(['/management-pacient/patients/update', item.id]);
  }

  changePatientStatus(item: PatientPageResponse): void {
    // Ciclo de estados: ACTIVE → INACTIVE → DISCHARGE → ACTIVE
    const statusCycle: { [key: string]: string } = {
      ACTIVE:    'INACTIVE',
      INACTIVE:  'DISCHARGE',
      DISCHARGE: 'ACTIVE',
    };
    const newStatus = statusCycle[item.status] ?? 'INACTIVE';
    const statusLabel = patientStatusOptions.find(s => s.value === newStatus)?.label ?? newStatus;
    const currentLabel = patientStatusOptions.find(s => s.value === item.status)?.label ?? item.status;

    Notiflix.Confirm.show(
      'Cambiar estado',
      `Cambiar al paciente "${item.firstName} ${item.paternalSurname}" de "${currentLabel}" a "${statusLabel}"?`,
      'Sí', 'No',
      () => {
        this.patientService.changeStatus(item.id, { status: newStatus as any }).subscribe({
          next: () => {
            Notiflix.Report.success('Operación Exitosa',
              `El paciente fue actualizado a "${statusLabel}" con éxito.`, 'OK');
            this.tableEvents.next({ event: 'RELOAD_PAGE' });
          },
          error: err => this.handleError(err),
        });
      },
    );
  }

  deletePatient(item: PatientPageResponse): void {
    Notiflix.Confirm.show(
      'Eliminar paciente',
      `¿Está seguro de eliminar al paciente "${item.firstName} ${item.paternalSurname}"? Esta acción no se puede deshacer.`,
      'Sí', 'No',
      () => {
        this.patientService.delete(item.id).subscribe({
          next: () => {
            Notiflix.Report.success('Operación Exitosa',
              'El paciente fue eliminado con éxito.', 'OK');
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
    console.error('Error en PatientComponent:', error);
    Notiflix.Report.failure('Error', error?.error?.message ?? 'Ocurrió un error inesperado.', 'OK');
  };
}





