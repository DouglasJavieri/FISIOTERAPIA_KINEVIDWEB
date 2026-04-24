import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import * as Notiflix from 'notiflix';

import { AuthService } from '../../../../../core/services/auth.service';
import { PatientService } from '../../../../../core/services/patient/patient.service';
import { ClinicalEpisodeService } from '../../../../../core/services/clinical/clinical-episode.service';
import { AppPermission } from '../../../../../core/models/auth.model';
import { PatientResponse } from '../../../../../core/models/patient/patient.interface';
import {
  ClinicalEpisodeResponse,
  ClinicalEpisodeRequest,
  CloseEpisodeRequest,
} from '../../../../../core/models/clinical/clinical.interface';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { PageEvent } from '@angular/material/paginator';

@Component({
  selector: 'knv-patient-profile',
  templateUrl: './patient-profile.component.html',
  styleUrls: ['./patient-profile.component.scss'],
})
export class PatientProfileComponent implements OnInit {

  patientId!: number;
  patient: PatientResponse | null = null;
  loading = true;

  // Episodios
  episodes: ClinicalEpisodeResponse[] = [];
  totalEpisodes = 0;
  pageSize = 5;
  currentPage = 0;
  loadingEpisodes = false;

  actions: { [key: string]: boolean } = {};

  // Formulario para nuevo episodio
  newEpisodeForm!: FormGroup;
  showNewEpisodeForm = false;
  savingEpisode = false;

  // Formulario para cerrar episodio
  closeEpisodeForm!: FormGroup;
  showCloseForm: { [episodeId: number]: boolean } = {};
  closingEpisode = false;

  constructor(
    public authService: AuthService,
    private route: ActivatedRoute,
    private router: Router,
    private patientService: PatientService,
    private episodeService: ClinicalEpisodeService,
  ) {}

  ngOnInit(): void {
    this.patientId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadActions();
    this.loadPatient();
    this.buildForms();
  }

  private loadActions(): void {
    this.actions = {
      viewEpisode:   this.authService.hasPermission(AppPermission.VIEW_EPISODE),
      listEpisode:   this.authService.hasPermission(AppPermission.LIST_EPISODE),
      createEpisode: this.authService.hasPermission(AppPermission.CREATE_EPISODE),
      closeEpisode:  this.authService.hasPermission(AppPermission.CLOSE_EPISODE),
      listSession:   this.authService.hasPermission(AppPermission.LIST_CLINICAL_SESSION),
    };
  }

  private buildForms(): void {
    this.newEpisodeForm = new FormGroup({
      reasonForAdmission: new FormControl('', [Validators.required, Validators.maxLength(500)]),
    });
    this.closeEpisodeForm = new FormGroup({
      dischargeReason: new FormControl('', [Validators.required, Validators.maxLength(500)]),
    });
  }

  loadPatient(): void {
    this.loading = true;
    this.patientService.getById(this.patientId).subscribe({
      next: p => {
        this.patient = p;
        this.loading = false;
        if (this.actions['listEpisode']) this.loadEpisodes();
      },
      error: () => {
        this.loading = false;
        Notiflix.Report.failure('Error', 'No se pudo cargar el paciente.', 'OK');
      },
    });
  }

  loadEpisodes(page = 0): void {
    this.loadingEpisodes = true;
    this.currentPage = page;
    this.episodeService.getByPatient(this.patientId, { page, size: this.pageSize, sortBy: 'episodeNumber', sortDir: 'DESC' }).subscribe({
      next: resp => {
        this.episodes = resp.content;
        this.totalEpisodes = resp.totalElements;
        this.loadingEpisodes = false;
      },
      error: () => {
        this.loadingEpisodes = false;
      },
    });
  }

  onPageChange(event: PageEvent): void {
    this.pageSize = event.pageSize;
    this.loadEpisodes(event.pageIndex);
  }

  viewSessions(episode: ClinicalEpisodeResponse): void {
    this.router.navigate(['/management-pacient/episodes', episode.id, 'sessions']);
  }

  // ── Nuevo episodio ────────────────────────────────────────────────────────

  toggleNewEpisodeForm(): void {
    this.showNewEpisodeForm = !this.showNewEpisodeForm;
    if (!this.showNewEpisodeForm) this.newEpisodeForm.reset();
  }

  submitNewEpisode(): void {
    if (this.newEpisodeForm.invalid) return;
    this.savingEpisode = true;
    const body: ClinicalEpisodeRequest = {
      patientId: this.patientId,
      reasonForAdmission: this.newEpisodeForm.value.reasonForAdmission,
    };
    this.episodeService.create(body).subscribe({
      next: () => {
        this.savingEpisode = false;
        this.showNewEpisodeForm = false;
        this.newEpisodeForm.reset();
        Notiflix.Report.success('Operación exitosa', 'Episodio clínico creado.', 'OK');
        this.loadEpisodes();
      },
      error: err => {
        this.savingEpisode = false;
        Notiflix.Report.failure('Error', err?.error?.message ?? 'Error al crear episodio.', 'OK');
      },
    });
  }

  // ── Reactivar paciente ────────────────────────────────────────────────────

  reactivatePatient(): void {
    Notiflix.Confirm.show(
      'Reactivar paciente',
      '¿Desea reactivar al paciente y abrir un nuevo episodio clínico?',
      'Sí', 'No',
      () => {
        // Pide motivo primero
        this.showNewEpisodeForm = true;
      },
    );
  }

  submitReactivate(): void {
    if (this.newEpisodeForm.invalid) return;
    this.savingEpisode = true;
    const body: ClinicalEpisodeRequest = {
      patientId: this.patientId,
      reasonForAdmission: this.newEpisodeForm.value.reasonForAdmission,
    };
    this.episodeService.reactivatePatient(this.patientId, body).subscribe({
      next: () => {
        this.savingEpisode = false;
        this.showNewEpisodeForm = false;
        this.newEpisodeForm.reset();
        Notiflix.Report.success('Operación exitosa', 'Paciente reactivado y nuevo episodio creado.', 'OK');
        this.loadPatient();
      },
      error: err => {
        this.savingEpisode = false;
        Notiflix.Report.failure('Error', err?.error?.message ?? 'Error al reactivar.', 'OK');
      },
    });
  }

  // ── Cerrar episodio ───────────────────────────────────────────────────────

  toggleCloseForm(episodeId: number): void {
    this.showCloseForm[episodeId] = !this.showCloseForm[episodeId];
    if (!this.showCloseForm[episodeId]) this.closeEpisodeForm.reset();
  }

  submitCloseEpisode(episodeId: number): void {
    if (this.closeEpisodeForm.invalid) return;
    this.closingEpisode = true;
    const body: CloseEpisodeRequest = { dischargeReason: this.closeEpisodeForm.value.dischargeReason };
    this.episodeService.close(episodeId, body).subscribe({
      next: () => {
        this.closingEpisode = false;
        this.showCloseForm[episodeId] = false;
        this.closeEpisodeForm.reset();
        Notiflix.Report.success('Alta médica', 'Episodio cerrado y paciente dado de alta.', 'OK');
        this.loadPatient();
      },
      error: err => {
        this.closingEpisode = false;
        Notiflix.Report.failure('Error', err?.error?.message ?? 'Error al cerrar episodio.', 'OK');
      },
    });
  }

  goBack(): void {
    this.router.navigate(['/management-pacient/patients']);
  }

  editPatient(): void {
    this.router.navigate(['/management-pacient/patients/update', this.patientId]);
  }
}

