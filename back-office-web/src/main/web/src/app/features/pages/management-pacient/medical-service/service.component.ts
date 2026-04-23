import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatTableDataSource } from '@angular/material/table';
import { BehaviorSubject } from 'rxjs';
import { Router } from '@angular/router';
import * as Notiflix from 'notiflix';

import { AuthService }           from '../../../../core/services/auth.service';
import { MedicalServiceService } from '../../../../core/services/medical-service/medical-service.service';
import { AppPermission }         from '../../../../core/models/auth.model';
import {
  ITableColumn,
  ITableEvents,
  ITableRowAction,
  PaginatedFn,
  noopTableEvent,
} from '../../../../shared/components/table/table.model';
import {
  serviceActionsCode,
  serviceCategoryFilterOptions,
  serviceStatusOptions,
  serviceTableColumns,
} from './service.util';
import {
  MedicalServicePageResponse,
  serviceCategoryOptions,
} from '../../../../core/models/medical-service/medical-service.interface';

@Component({
  selector: 'knv-service',
  templateUrl: './service.component.html',
  styleUrls: ['./service.component.scss'],
})
export class ServiceComponent implements OnInit {

  tableEvents     = new BehaviorSubject<ITableEvents>(noopTableEvent());
  dataSource      = new MatTableDataSource<MedicalServicePageResponse>([]);
  columns: ITableColumn[]       = [...serviceTableColumns];
  rowActions: ITableRowAction[] = [];
  pageSizeOptions: number[]     = [5, 10, 25, 50];
  actions: { [key: string]: boolean } = {};
  form!: FormGroup;

  statusOptions   = serviceStatusOptions;
  categoryOptions = serviceCategoryFilterOptions;

  constructor(
    private authService:           AuthService,
    private medicalServiceService: MedicalServiceService,
    private router:                Router,
  ) {}

  ngOnInit(): void {
    this.buildForm();
    this.loadActions();
    this.tableEvents.subscribe(this.tableActionManager);
  }

  private buildForm(): void {
    this.form = new FormGroup({
      status:   new FormControl(null),
      category: new FormControl(null),
    });
  }

  private loadActions(): void {
    this.actions = {
      listAction:         this.authService.hasPermission(AppPermission.LIST_SERVICE),
      createAction:       this.authService.hasPermission(AppPermission.CREATE_SERVICE),
      updateAction:       this.authService.hasPermission(AppPermission.UPDATE_SERVICE),
      changeStatusAction: this.authService.hasPermission(AppPermission.CHANGE_SERVICE_STATUS),
      deleteAction:       this.authService.hasPermission(AppPermission.DELETE_SERVICE),
    };
    this.rowActions = this.buildRowActions();
  }

  protected buildRowActions = (): ITableRowAction[] => {
    const actions: ITableRowAction[] = [];
    if (this.actions['updateAction']) {
      actions.push({ action: 'Actualizar',    actionCode: serviceActionsCode.updateAction,       icon: 'edit' });
    }
    if (this.actions['changeStatusAction']) {
      actions.push({ action: 'Cambiar estado', actionCode: serviceActionsCode.changeStatusAction, icon: 'toggle_on' });
    }
    if (this.actions['deleteAction']) {
      actions.push({ action: 'Eliminar',       actionCode: serviceActionsCode.deleteAction,       icon: 'delete' });
    }
    return actions;
  };

  requestServiceListFn: PaginatedFn = (queryParams: any) => {
    const extra = this.getFilterParams();
    return this.medicalServiceService.getAll({ ...queryParams, ...extra });
  };

  itemServiceFormatterFn = (content: MedicalServicePageResponse[]): MedicalServicePageResponse[] =>
    content.map(item => ({
      ...item,
      categoryDescription: serviceCategoryOptions.find(c => c.value === item.category)?.label ?? item.categoryDescription,
      statusLabel:         item.status === 'ACTIVE' ? 'ACTIVO' : 'INACTIVO',
      price:               item.price != null ? item.price : null,
    }));

  protected tableActionManager = (event: ITableEvents): void => {
    if (event.event === 'ROW_CLICK') {
      this.rowActionEvent(event.data);
    }
  };

  protected rowActionEvent = (event: { item: MedicalServicePageResponse; actionCode: string }): void => {
    const { item, actionCode } = event;
    if (actionCode === serviceActionsCode.updateAction)       this.updateService(item);
    if (actionCode === serviceActionsCode.changeStatusAction) this.changeServiceStatus(item);
    if (actionCode === serviceActionsCode.deleteAction)       this.deleteService(item);
  };

  createService(): void {
    this.router.navigate(['/management-pacient/services/add']);
  }

  updateService(item: MedicalServicePageResponse): void {
    this.router.navigate(['/management-pacient/services/update', item.id]);
  }

  changeServiceStatus(item: MedicalServicePageResponse): void {
    const newStatus   = item.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
    const actionLabel = newStatus === 'ACTIVE' ? 'activar'  : 'desactivar';
    const successMsg  = newStatus === 'ACTIVE' ? 'activado' : 'desactivado';

    Notiflix.Confirm.show(
      'Cambiar estado',
      `¿Deseas ${actionLabel} el servicio "${item.name}"?`,
      'Sí', 'No',
      () => {
        this.medicalServiceService.changeStatus(item.id, { status: newStatus }).subscribe({
          next: () => {
            Notiflix.Report.success('Operación Exitosa', `El servicio fue ${successMsg} con éxito.`, 'OK');
            this.tableEvents.next({ event: 'RELOAD_PAGE' });
          },
          error: err => this.handleError(err),
        });
      },
    );
  }

  deleteService(item: MedicalServicePageResponse): void {
    Notiflix.Confirm.show(
      'Eliminar servicio',
      `¿Está seguro de eliminar el servicio "${item.name}"?`,
      'Sí', 'No',
      () => {
        this.medicalServiceService.delete(item.id).subscribe({
          next: () => {
            Notiflix.Report.success('Operación Exitosa', 'El servicio fue eliminado con éxito.', 'OK');
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
    this.form.patchValue({ status: null, category: null });
    this.tableEvents.next({ event: 'RESET', data: { aditionalParams: {} } });
  }

  private getFilterParams(): Record<string, any> {
    const { status, category } = this.form.value;
    const params: Record<string, any> = {};
    if (status)   params['status']   = status;
    if (category) params['category'] = category;
    return params;
  }

  protected handleError = (error: any): void => {
    console.error('Error en ServiceComponent:', error);
    Notiflix.Report.failure('Error', error?.error?.message ?? 'Ocurrió un error inesperado.', 'OK');
  };
}

