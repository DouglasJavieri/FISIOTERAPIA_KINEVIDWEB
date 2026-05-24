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
  SessionStatus,
  sessionStatusOptions,
} from '../../../../../core/models/clinical/clinical.interface';
import {
  ITableColumn,
  ITableEvents,
  ITableRowAction,
  PaginatedFn,
  noopTableEvent,
} from '../../../../../shared/components/table/table.model';
import { sessionActionsCode, sessionTableColumns } from './session-list.util';

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

  columns: ITableColumn[] = [...sessionTableColumns];

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
      listAction:         this.authService.hasPermission(AppPermission.LIST_CLINICAL_SESSION),
      createAction:       this.authService.hasPermission(AppPermission.CREATE_CLINICAL_SESSION),
      updateAction:       this.authService.hasPermission(AppPermission.UPDATE_CLINICAL_SESSION),
      deleteAction:       this.authService.hasPermission(AppPermission.DELETE_CLINICAL_SESSION),
      changeStatusAction: this.authService.hasPermission(AppPermission.UPDATE_CLINICAL_SESSION),
    };
    this.rowActions = this.buildRowActions();
  }

  private buildRowActions(): ITableRowAction[] {
    const actions: ITableRowAction[] = [];
    if (this.actions['updateAction']) {
      actions.push({ action: 'Ver / Editar', actionCode: sessionActionsCode.editAction, icon: 'edit' });
    }
    if (this.actions['changeStatusAction']) {
      actions.push({
        action: 'Cambiar estado',
        actionCode: sessionActionsCode.changeStatusAction,
        icon: 'swap_horiz',
        tooltip: 'Solo disponible para sesiones ABIERTAS',
        isDisabledFn: (row: ClinicalSessionResponse) => row.sessionStatus !== 'OPEN',
      });
    }
    if (this.actions['deleteAction']) {
      actions.push({ action: 'Eliminar', actionCode: sessionActionsCode.deleteAction, icon: 'delete' });
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
      if (actionCode === sessionActionsCode.editAction) {
        this.router.navigate(['/management-pacient/episodes', this.episodeId, 'sessions', item.id]);
      }
      if (actionCode === sessionActionsCode.changeStatusAction) {
        this.changeSessionStatus(item);
      }
      if (actionCode === sessionActionsCode.deleteAction) {
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

  changeSessionStatus(item: ClinicalSessionResponse): void {
    // Solo sesiones OPEN pueden cambiarse
    if (item.sessionStatus !== 'OPEN') {
      Notiflix.Report.warning(
        'No permitido',
        `La sesión #${item.sessionNumber} ya está en estado "${sessionStatusOptions.find(s => s.value === item.sessionStatus)?.label ?? item.sessionStatus}". Solo se pueden cambiar sesiones ABIERTAS.`,
        'OK',
      );
      return;
    }

    // Diálogo para elegir CLOSED o CANCELLED
    Notiflix.Confirm.show(
      `Cambiar estado — Sesión #${item.sessionNumber}`,
      '¿Cómo desea cambiar el estado de esta sesión?',
      'Cerrar sesión',
      'Cancelar sesión',
      () => this.applyStatusChange(item, 'CLOSED'),
      () => this.applyStatusChange(item, 'CANCELLED'),
    );
  }

  private applyStatusChange(item: ClinicalSessionResponse, status: SessionStatus): void {
    const label = sessionStatusOptions.find(s => s.value === status)?.label ?? status;
    Notiflix.Loading.pulse('Cambiando estado...');
    this.sessionService.changeStatus(item.id, { status }).subscribe({
      next: () => {
        Notiflix.Loading.remove(300);
        Notiflix.Report.success(
          'Estado actualizado',
          `La sesión #${item.sessionNumber} fue marcada como "${label}".`,
          'OK',
        );
        this.tableEvents.next({ event: 'RELOAD_PAGE' });
      },
      error: err => {
        Notiflix.Loading.remove(300);
        Notiflix.Report.failure('Error', err?.error?.message ?? 'No se pudo cambiar el estado.', 'OK');
      },
    });
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

