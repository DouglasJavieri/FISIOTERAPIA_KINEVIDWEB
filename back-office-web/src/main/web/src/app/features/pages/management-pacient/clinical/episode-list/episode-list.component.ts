import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatTableDataSource } from '@angular/material/table';
import { BehaviorSubject } from 'rxjs';
import { Router } from '@angular/router';
import * as Notiflix from 'notiflix';

import { AuthService } from '../../../../../core/services/auth.service';
import { ClinicalEpisodeService } from '../../../../../core/services/clinical/clinical-episode.service';
import { PatientService } from '../../../../../core/services/patient/patient.service';
import { AppPermission } from '../../../../../core/models/auth.model';
import {
  ClinicalEpisodeResponse,
  EpisodeStatus,
  episodeStatusOptions,
} from '../../../../../core/models/clinical/clinical.interface';
import { PatientResponse } from '../../../../../core/models/patient/patient.interface';
import {
  ITableColumn,
  ITableEvents,
  ITableRowAction,
  PaginatedFn,
  noopTableEvent,
} from '../../../../../shared/components/table/table.model';

@Component({
  selector: 'knv-episode-list',
  templateUrl: './episode-list.component.html',
  styleUrls: ['./episode-list.component.scss'],
})
export class EpisodeListComponent implements OnInit {

  tableEvents = new BehaviorSubject<ITableEvents>(noopTableEvent());
  dataSource = new MatTableDataSource<ClinicalEpisodeResponse>([]);
  pageSizeOptions = [5, 10, 25];
  actions: { [key: string]: boolean } = {};
  rowActions: ITableRowAction[] = [];
  form!: FormGroup;

  statusOptions = [{ value: null, label: 'Todos' }, ...episodeStatusOptions];
  patientList: PatientResponse[] = [];

  columns: ITableColumn[] = [
    { name: 'Episodio #', property: 'episodeNumber', visible: true, isModelProperty: true },
    { name: 'Paciente', property: 'patientFullName', visible: true, isModelProperty: true },
    { name: 'CI', property: 'patientCi', visible: true, isModelProperty: true },
    { name: 'Fecha inicio', property: 'startDate', visible: true, isModelProperty: true },
    { name: 'Fecha cierre', property: 'endDate', visible: true, isModelProperty: true },
    { name: 'Estado', property: 'episodeStatusLabel', visible: true, isModelProperty: true },
    { name: 'Motivo', property: 'reasonForAdmission', visible: true, isModelProperty: true },
  ];

  constructor(
    public authService: AuthService,
    private episodeService: ClinicalEpisodeService,
    private patientService: PatientService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.buildForm();
    this.loadActions();
    this.loadPatients();
    this.tableEvents.subscribe(this.tableActionManager);
  }

  private buildForm(): void {
    this.form = new FormGroup({
      patientId: new FormControl(null),
      status: new FormControl(null),
    });
  }

  private loadActions(): void {
    this.actions = {
      listAction:   this.authService.hasPermission(AppPermission.LIST_EPISODE),
      viewAction:   this.authService.hasPermission(AppPermission.VIEW_EPISODE),
      createAction: this.authService.hasPermission(AppPermission.CREATE_EPISODE),
    };
    this.rowActions = this.buildRowActions();
  }

  private buildRowActions(): ITableRowAction[] {
    const actions: ITableRowAction[] = [];
    if (this.actions['viewAction']) {
      actions.push({ action: 'Ver sesiones', actionCode: 'VIEW_SESSIONS', icon: 'visibility' });
    }
    return actions;
  }

  private loadPatients(): void {
    this.patientService.getActiveList().subscribe({
      next: list => this.patientList = list,
      error: () => {},
    });
  }

  requestEpisodeListFn: PaginatedFn = (queryParams: any) => {
    const extra = this.getFilterParams();
    return this.episodeService.getAll({ ...queryParams, ...extra });
  };

  itemFormatterFn = (content: ClinicalEpisodeResponse[]): any[] =>
    content.map(item => ({
      ...item,
      episodeStatusLabel: episodeStatusOptions.find(s => s.value === item.episodeStatus)?.label ?? item.episodeStatus,
      endDate: item.endDate ?? '—',
    }));

  protected tableActionManager = (event: ITableEvents): void => {
    if (event.event === 'ROW_CLICK') {
      const { item, actionCode } = event.data;
      if (actionCode === 'VIEW_SESSIONS') {
        this.router.navigate(['/management-pacient/episodes', item.id, 'sessions']);
      }
    }
  };

  applyFilter(): void {
    const aditionalParams = this.getFilterParams();
    this.tableEvents.next({ event: 'RESET', data: { aditionalParams } });
  }

  clearFilter(): void {
    this.form.reset();
    this.tableEvents.next({ event: 'RESET', data: { aditionalParams: {} } });
  }

  private getFilterParams(): Record<string, any> {
    const { patientId, status } = this.form.value;
    const params: Record<string, any> = {};
    if (patientId) params['patientId'] = patientId;
    if (status) params['status'] = status;
    return params;
  }

  protected handleError = (error: any): void => {
    Notiflix.Report.failure('Error', error?.error?.message ?? 'Ocurrió un error inesperado.', 'OK');
  };
}

