import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatStepper } from '@angular/material/stepper';
import * as Notiflix from 'notiflix';

import { AuthService } from '../../../../../core/services/auth.service';
import { ClinicalSessionService } from '../../../../../core/services/clinical/clinical-session.service';
import { ClinicalEpisodeService } from '../../../../../core/services/clinical/clinical-episode.service';
import { EmployeeService } from '../../../../../core/services/employee/employee.service';
import { MedicalServiceService } from '../../../../../core/services/medical-service/medical-service.service';
import { AppPermission } from '../../../../../core/models/auth.model';
import {
  ClinicalEpisodeResponse,
  ClinicalSessionRequest,
  ClinicalSessionResponse,
  ClinicalSessionUpdateRequest,
  SessionServiceRequest,
  SessionServiceResponse,
  sessionStatusOptions,
} from '../../../../../core/models/clinical/clinical.interface';
import { EmployeeResponse } from '../../../../../core/models/employee/employee.interface';
import { MedicalServiceResponse } from '../../../../../core/models/medical-service/medical-service.interface';
import {
  noOnlyWhitespaceValidator,
  noWhitespaceValidator,
} from '../../../../../shared/utils/validators.util';

@Component({
  selector: 'knv-session-form',
  templateUrl: './session-form.component.html',
  styleUrls: ['./session-form.component.scss'],
})
export class SessionFormComponent implements OnInit {

  @ViewChild('stepper') stepper!: MatStepper;

  episodeId!: number;
  sessionId: number | null = null;
  isNew = true;
  isLoading = false;
  isSavingService = false;

  episode: ClinicalEpisodeResponse | null = null;
  session: ClinicalSessionResponse | null = null;
  employeeList: EmployeeResponse[] = [];
  serviceList: MedicalServiceResponse[] = [];
  appliedServices: SessionServiceResponse[] = [];

  step1!: FormGroup;
  maxDate = new Date();

  step2!: FormGroup;

  canUpdate = false;
  sessionLocked = false;
  sessionStatusOptions = sessionStatusOptions;

  // Flag para detectar si se seleccionó Análisis Postural
  hasPosturalAnalysisService = false;
  canViewFootAnalysis = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    public authService: AuthService,
    private sessionService: ClinicalSessionService,
    private episodeService: ClinicalEpisodeService,
    private employeeService: EmployeeService,
    private medicalServiceService: MedicalServiceService,
  ) {}

  ngOnInit(): void {
    this.episodeId = Number(this.route.snapshot.paramMap.get('episodeId'));
    const sid = this.route.snapshot.paramMap.get('sessionId');
    this.sessionId = sid && sid !== 'new' ? Number(sid) : null;
    this.isNew = !this.sessionId;

    this.canUpdate          = this.authService.hasPermission(AppPermission.UPDATE_CLINICAL_SESSION);
    this.canViewFootAnalysis = this.authService.hasPermission(AppPermission.VIEW_FOOT_ANALYSIS);

    this.buildForms();
    this.loadCatalogues();

    if (!this.isNew) {
      this.loadSession();
    } else {
      this.loadEpisode();
    }
  }

  private buildForms(): void {
    this.step1 = new FormGroup({
      employeeId: new FormControl(null, [Validators.required]),
      sessionDate: new FormControl(null, [Validators.required]),
      reasonForConsultation: new FormControl('', [Validators.maxLength(500), noWhitespaceValidator(),]),
      relevantBackground: new FormControl('', [Validators.maxLength(1000), noOnlyWhitespaceValidator(),]),
      medicalServiceId: new FormControl(null, [Validators.required]),
      quantity: new FormControl(1, [Validators.required, Validators.min(1), Validators.max(99)]),
      unitPrice: new FormControl(null, [Validators.min(0)]),
      notes: new FormControl('', [Validators.maxLength(500), noOnlyWhitespaceValidator()]),
    });

    this.step2 = new FormGroup({
      kinesiologicalEvaluation: new FormControl('', [noOnlyWhitespaceValidator()]),
      actualIllnessHistory: new FormControl('', [noOnlyWhitespaceValidator()]),
      gait: new FormControl('', [noOnlyWhitespaceValidator()]),
      functionalTests: new FormControl('', [noOnlyWhitespaceValidator()]),
      complementaryExams: new FormControl('', [noOnlyWhitespaceValidator()]),
      kinesiologicalDiagnosis: new FormControl('', [noOnlyWhitespaceValidator()]),
      treatmentApplied: new FormControl('', [noOnlyWhitespaceValidator()]),
      observations: new FormControl('', [noOnlyWhitespaceValidator()]),
      evolution: new FormControl('', [noOnlyWhitespaceValidator()]),
    });
  }

  private loadCatalogues(): void {
    this.employeeService.getActiveList().subscribe({
      next: list => this.employeeList = list,
      error: () => {},
    });
    this.medicalServiceService.getActiveList().subscribe({
      next: list => this.serviceList = list,
      error: () => {},
    });
  }

  private loadEpisode(): void {
    this.episodeService.getById(this.episodeId).subscribe({
      next: ep => this.episode = ep,
      error: () => Notiflix.Report.failure('Error', 'No se pudo cargar el episodio.', 'OK'),
    });
  }

  private loadSession(): void {
    Notiflix.Loading.pulse('Cargando sesión...');
    this.sessionService.getById(this.sessionId!).subscribe({
      next: s => {
        this.session = s;
        this.sessionLocked = s.sessionStatus !== 'OPEN';

        this.step1.patchValue({
          employeeId: s.employeeId,
          sessionDate: s.sessionDate,
          reasonForConsultation: s.reasonForConsultation,
          relevantBackground: s.relevantBackground ?? '',
        });
        this.step2.patchValue({
          kinesiologicalEvaluation: s.kinesiologicalEvaluation ?? '',
          actualIllnessHistory: s.actualIllnessHistory ?? '',
          gait: s.gait ?? '',
          functionalTests: s.functionalTests ?? '',
          complementaryExams: s.complementaryExams ?? '',
          kinesiologicalDiagnosis: s.kinesiologicalDiagnosis ?? '',
          treatmentApplied: s.treatmentApplied ?? '',
          observations: s.observations ?? '',
          evolution: s.evolution ?? '',
        });

        if (this.sessionLocked || !this.canUpdate) {
          this.step1.disable();
          this.step2.disable();
        }

        this.loadAppliedServices();
        Notiflix.Loading.remove(300);
      },
      error: () => {
        Notiflix.Loading.remove(300);
        Notiflix.Report.failure('Error', 'No se pudo cargar la sesión.', 'OK');
      },
    });
  }

  private loadAppliedServices(): void {
    if (!this.sessionId) return;
    this.sessionService.getServices(this.sessionId).subscribe({
      next: list => {
        this.appliedServices = list;
        this.checkForPosturalAnalysisService();
      },
      error: () => {},
    });
  }

  // ─── Paso 1: guardar datos básicos y crear servicios ──────────────────────────

  saveStep1(): void {
    if (this.step1.invalid) {
      this.step1.markAllAsTouched();
      return;
    }
    if (this.isNew) {
      this.createSession();
    } else {
      this.updateSessionStep1();
    }
  }

  private createSession(): void {
    const v = this.step1.value;
    const body: ClinicalSessionRequest = {
      episodeId: this.episodeId,
      employeeId: v.employeeId,
      sessionDate: this.formatDate(v.sessionDate),
      reasonForConsultation: v.reasonForConsultation.trim(),
      relevantBackground: v.relevantBackground?.trim() || null,
    };
    Notiflix.Loading.pulse('Creando sesión...');
    this.sessionService.create(body).subscribe({
      next: (sessionData: ClinicalSessionResponse) => {
        Notiflix.Loading.remove(300);

        // Validar respuesta
        if (!sessionData || !sessionData.id) {
          console.error('Respuesta inválida del servidor:', sessionData);
          Notiflix.Report.failure(
            'Error',
            'El servidor retornó una respuesta inválida. Por favor, recargue la página.',
            'OK'
          );
          return;
        }

        this.session = sessionData;
        this.sessionId = sessionData.id;
        this.isNew = false;

        console.log('✓ Sesión creada correctamente. ID:', this.sessionId);

        // Agregar el servicio seleccionado
        this.addService(() => this.stepper.next());
      },
      error: err => {
        Notiflix.Loading.remove(300);
        console.error('Error al crear sesión:', err);
        Notiflix.Report.failure(
          'Error',
          err?.error?.message ?? 'No se pudo crear la sesión.',
          'OK'
        );
      },
    });
  }

  private updateSessionStep1(): void {
    const v1 = this.step1.value;
    const v2 = this.step2.value;
    const body: ClinicalSessionUpdateRequest = {
      employeeId: v1.employeeId,
      reasonForConsultation: v1.reasonForConsultation?.trim(),
      relevantBackground: v1.relevantBackground?.trim() || null,
      kinesiologicalEvaluation: v2.kinesiologicalEvaluation?.trim() || null,
      actualIllnessHistory: v2.actualIllnessHistory?.trim() || null,
      gait: v2.gait?.trim() || null,
      functionalTests: v2.functionalTests?.trim() || null,
      complementaryExams: v2.complementaryExams?.trim() || null,
      kinesiologicalDiagnosis: v2.kinesiologicalDiagnosis?.trim() || null,
      treatmentApplied: v2.treatmentApplied?.trim() || null,
      observations: v2.observations?.trim() || null,
      evolution: v2.evolution?.trim() || null,
    };
    Notiflix.Loading.pulse('Guardando cambios...');
    this.sessionService.update(this.sessionId!, body).subscribe({
      next: s => {
        Notiflix.Loading.remove(300);
        this.session = s;
        Notiflix.Notify.success('Sesión actualizada correctamente.');
        this.stepper.next();
      },
      error: err => {
        Notiflix.Loading.remove(300);
        Notiflix.Report.failure('Error', err?.error?.message ?? 'No se pudo actualizar la sesión.', 'OK');
      },
    });
  }

  saveStep2(): void {
    if (this.sessionLocked || !this.canUpdate) {
      this.finalize();
      return;
    }
    this.updateSessionStep1();
  }

  addService(callback?: () => void): void {
    // Si es nueva sesión, primero guardar la sesión
    if (this.isNew && !this.sessionId) {
      // Validar datos básicos requeridos
      if (this.step1.get('employeeId')?.invalid ||
          this.step1.get('sessionDate')?.invalid ||
          this.step1.get('reasonForConsultation')?.invalid) {
        this.step1.markAllAsTouched();
        Notiflix.Report.failure('Error', 'Completa los datos requeridos antes de agregar un servicio.', 'OK');
        return;
      }

      // Crear sesión primero
      this.createSessionAndAddService(callback);
      return;
    }

    // Si ya existe la sesión, proceder normalmente
    this.addServiceToExistingSession(callback);
  }

  private createSessionAndAddService(callback?: () => void): void {
    const v = this.step1.value;
    const body: ClinicalSessionRequest = {
      episodeId: this.episodeId,
      employeeId: v.employeeId,
      sessionDate: this.formatDate(v.sessionDate),
      reasonForConsultation: v.reasonForConsultation.trim(),
      relevantBackground: v.relevantBackground?.trim() || null,
    };

    console.log('Creando sesión para agregar servicio...');
    Notiflix.Loading.pulse('Creando sesión...');

    this.sessionService.create(body).subscribe({
      next: (sessionData: ClinicalSessionResponse) => {
        Notiflix.Loading.remove(300);

        if (!sessionData || !sessionData.id) {
          console.error('Respuesta inválida del servidor:', sessionData);
          Notiflix.Report.failure('Error', 'El servidor retornó una respuesta inválida.', 'OK');
          return;
        }

        this.session = sessionData;
        this.sessionId = sessionData.id;
        this.isNew = false;

        console.log('Sesión creada. ID:', this.sessionId);

        // Ahora agregar el servicio
        this.addServiceToExistingSession(callback);
      },
      error: err => {
        Notiflix.Loading.remove(300);
        console.error('Error al crear sesión:', err);
        Notiflix.Report.failure('Error', err?.error?.message ?? 'No se pudo crear la sesión.', 'OK');
      },
    });
  }

  private addServiceToExistingSession(callback?: () => void): void {
    // Validar servicio seleccionado
    if (this.step1.get('medicalServiceId')?.invalid || this.step1.get('quantity')?.invalid) {
      this.step1.markAllAsTouched();
      return;
    }

    const effectiveSessionId = this.sessionId ?? this.session?.id;

    if (!effectiveSessionId) {
      console.error('No hay sessionId disponible:', {
        sessionId: this.sessionId,
        sessionFromObject: this.session?.id,
        isNew: this.isNew,
      });
      Notiflix.Report.failure('Error', 'Error interno: No se pudo identificar la sesión.', 'OK');
      return;
    }

    console.log('Agregando servicio a sesión ID:', effectiveSessionId);

    const v = this.step1.value;
    const body: SessionServiceRequest = {
      medicalServiceId: v.medicalServiceId,
      quantity: v.quantity,
      unitPrice: v.unitPrice || null,
      notes: v.notes?.trim() || null,
    };

    this.isSavingService = true;
    this.sessionService.addService(effectiveSessionId, body).subscribe({
      next: () => {
        this.isSavingService = false;
        console.log('Servicio agregado correctamente');

        this.step1.patchValue({
          medicalServiceId: null,
          quantity: 1,
          unitPrice: null,
          notes: ''
        });

        this.loadAppliedServices();
        Notiflix.Notify.success('Servicio agregado.');

        if (callback) callback();
      },
      error: err => {
        this.isSavingService = false;
        console.error('Error al agregar servicio:', err);
        Notiflix.Report.failure('Error', err?.error?.message ?? 'No se pudo agregar el servicio.', 'OK');
      },
    });
  }

  removeService(ss: SessionServiceResponse): void {
    Notiflix.Confirm.show(
      'Eliminar servicio',
      `¿Eliminar "${ss.medicalServiceName}" de esta sesión?`,
      'Sí', 'No',
      () => {
        this.sessionService.removeService(ss.id).subscribe({
          next: () => {
            this.loadAppliedServices();
            Notiflix.Notify.success('Servicio eliminado.');
          },
          error: err => Notiflix.Report.failure('Error', err?.error?.message ?? 'Error al eliminar.', 'OK'),
        });
      },
    );
  }

  // ─── Detectar servicio de Análisis Postural ──────────────────────────────────
  // Este método se ejecuta cada vez que se carga o se agrega un servicio.
  // Verifica si algún servicio aplicado tiene la categoría 'POSTURAL_ANALYSIS'
  // y actualiza el flag para mostrar/ocultar el botón de análisis de pisada

  checkForPosturalAnalysisService(): void {
    const hasPostural = this.appliedServices.some(
      s => s.medicalServiceCategory === 'POSTURAL_ANALYSIS'
    );
    this.hasPosturalAnalysisService = hasPostural;
  }

  // Este método se ejecuta cuando el usuario selecciona un servicio en el dropdown
  // Autogestiona el precio unitario basado en el precio del servicio seleccionado
  onServiceSelected(serviceId: number): void {
    const svc = this.serviceList.find(s => s.id === serviceId);
    if (svc?.price) {
      this.step1.get('unitPrice')?.setValue(svc.price);
    }
  }

  f1(name: string) { return this.step1.get(name); }
  f2(name: string) { return this.step2.get(name); }

  private formatDate(date: any): string {
    if (!date) return '';
    if (typeof date === 'string') return date;
    const d = new Date(date);
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${y}-${m}-${day}`;
  }

  getEmployeeName(id: number): string {
    const emp = this.employeeList.find(e => e.id === id);
    return emp ? `${emp.firstName} ${emp.paternalSurname}` : String(id);
  }

  getServiceName(id: number): string {
    const svc = this.serviceList.find(s => s.id === id);
    return svc ? svc.name : String(id);
  }


  finalize(): void {
    Notiflix.Report.success(
      'Sesión guardada',
      this.isNew
        ? 'La sesión fue registrada exitosamente.'
        : 'Los cambios fueron guardados.',
      'OK',
      () => this.goBack(),
    );
  }

  goBack(): void {
    this.router.navigate(['/management-pacient/episodes', this.episodeId, 'sessions']);
  }

  goToImaging(): void {
    this.router.navigate([
      '/management-pacient/episodes', this.episodeId,
      'sessions', this.sessionId, 'imaging',
    ]);
  }

  get statusLabel(): string {
    return sessionStatusOptions.find(s => s.value === this.session?.sessionStatus)?.label ?? '';
  }
}
