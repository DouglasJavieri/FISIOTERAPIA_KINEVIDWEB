import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatTableDataSource } from '@angular/material/table';
import { BehaviorSubject } from 'rxjs';
import * as Notiflix from 'notiflix';

import { AuthService } from '../../../../../core/services/auth.service';
import { ClinicalEpisodeService } from '../../../../../core/services/clinical/clinical-episode.service';
import { ClinicalSessionService } from '../../../../../core/services/clinical/clinical-session.service';
import { AppPermission } from '../../../../../core/models/auth.model';
import {
  ClinicalEpisodeResponse,
  ClinicalSessionResponse,
  sessionStatusOptions,
} from '../../../../../core/models/clinical/clinical.interface';
import {
  ITableColumn,
  ITableEvents,
  ITableRowAction,
  PaginatedFn,
  noopTableEvent,
} from '../../../../../shared/components/table/table.model';

@Component({
  selector: 'knv-session-list',
  templateUrl: './session-list.component.html',
  styleUrls: ['./session-list.component.scss'],
})
export class SessionListComponent implements OnInit {

  episodeId!: number;
  episode: ClinicalEpisodeResponse | null = null;

  tableEvents = new BehaviorSubject<ITableEvents>(noopTableEvent());
  dataSource = new MatTableDataSource<ClinicalSessionResponse>([]);
  pageSizeOptions = [5, 10, 25];
  actions: { [key: string]: boolean } = {};
  rowActions: ITableRowAction[] = [];

  columns: ITableColumn[] = [
    { name: 'Sesión #', property: 'sessionNumber', visible: true, isModelProperty: true },
    { name: 'Fecha', property: 'sessionDate', visible: true, isModelProperty: true },
    { name: 'Terapeuta', property: 'employeeFullName', visible: true, isModelProperty: true },
    { name: 'Motivo', property: 'reasonForConsultation', visible: true, isModelProperty: true },
    { name: 'Estado', property: 'sessionStatusLabel', visible: true, isModelProperty: true },
    { name: 'Tiene imagen', property: 'hasImageAnalysis', visible: true, isModelProperty: true },
  ];

  constructor(
    public authService: AuthService,
    private route: ActivatedRoute,
    private router: Router,
    private episodeService: ClinicalEpisodeService,
    private sessionService: ClinicalSessionService,
  ) {}

  ngOnInit(): void {
    this.episodeId = Number(this.route.snapshot.paramMap.get('episodeId'));
    this.loadActions();
    this.loadEpisode();
    this.tableEvents.subscribe(this.tableActionManager);
  }

  private loadActions(): void {
    this.actions = {
      listAction: this.authService.hasPermission(AppPermission.LIST_CLINICAL_SESSION),
      createAction: this.authService.hasPermission(AppPermission.CREATE_CLINICAL_SESSION),
      updateAction: this.authService.hasPermission(AppPermission.UPDATE_CLINICAL_SESSION),
      deleteAction: this.authService.hasPermission(AppPermission.DELETE_CLINICAL_SESSION),
    };
    this.rowActions = this.buildRowActions();
  }

  private buildRowActions(): ITableRowAction[] {
    const actions: ITableRowAction[] = [];
    if (this.actions['updateAction']) {
      actions.push({ action: 'Ver / Editar', actionCode: 'EDIT_SESSION', icon: 'edit' });
    }
    if (this.actions['deleteAction']) {
      actions.push({ action: 'Eliminar', actionCode: 'DELETE_SESSION', icon: 'delete' });
    }
    return actions;
  }

  private loadEpisode(): void {
    this.episodeService.getById(this.episodeId).subscribe({
      next: ep => this.episode = ep,
      error: () => Notiflix.Report.failure('Error', 'No se pudo cargar el episodio.', 'OK'),
    });
  }

  requestSessionsFn: PaginatedFn = (queryParams: any) =>
    this.sessionService.getByEpisode(this.episodeId, queryParams);

  itemFormatterFn = (content: ClinicalSessionResponse[]): any[] =>
    content.map(item => ({
      ...item,
      sessionStatusLabel: sessionStatusOptions.find(s => s.value === item.sessionStatus)?.label ?? item.sessionStatus,
      hasImageAnalysis: item.hasImageAnalysis ? 'Sí' : 'No',
    }));

  protected tableActionManager = (event: ITableEvents): void => {
    if (event.event === 'ROW_CLICK') {
      const { item, actionCode } = event.data;
      if (actionCode === 'EDIT_SESSION') {
        this.router.navigate(['/management-pacient/episodes', this.episodeId, 'sessions', item.id]);
      }
      if (actionCode === 'DELETE_SESSION') {
        this.deleteSession(item);
      }
    }
  };

  newSession(): void {
    this.router.navigate(['/management-pacient/episodes', this.episodeId, 'sessions', 'new']);
  }

  goBack(): void {
    const patientId = this.episode?.patientId;
    if (patientId) {
      this.router.navigate(['/management-pacient/patients', patientId]);
    } else {
      this.router.navigate(['/management-pacient/episodes']);
    }
  }

  deleteSession(item: ClinicalSessionResponse): void {
    Notiflix.Confirm.show(
      'Eliminar sesión',
      `¿Eliminar la sesión #${item.sessionNumber}? Esta acción no se puede deshacer.`,
      'Sí', 'No',
      () => {
        this.sessionService.delete(item.id).subscribe({
          next: () => {
            Notiflix.Report.success('Operación exitosa', 'Sesión eliminada.', 'OK');
            this.tableEvents.next({ event: 'RELOAD_PAGE' });
          },
          error: err => Notiflix.Report.failure('Error', err?.error?.message ?? 'Error al eliminar.', 'OK'),
        });
      },
    );
  }
}

