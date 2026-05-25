import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import * as Notiflix from 'notiflix';

import { FootAnalysisService } from '../../../../../../core/services/imaging/foot-analysis.service';
import {
  diagnosisOptions,
  FootAnalysisRequest,
  FootAnalysisResponse,
  FootAnalysisUpdateRequest,
} from '../../../../../../core/models/imaging/imaging.interface';

/**
 * Sub-paso 5: Resumen y datos generales del análisis.
 * Crea el FootAnalysis si aún no existe o actualiza sus datos.
 * Botón "Volver a Sesión" finaliza el flujo de imaging.
 */
@Component({
  selector: 'knv-summary-form',
  templateUrl: './summary-form.component.html',
  styleUrls: ['./summary-form.component.scss'],
})
export class SummaryFormComponent implements OnChanges {

  @Input() sessionId!: number;
  @Input() footAnalysis: FootAnalysisResponse | null = null;
  @Input() tempPhotos: { file: File, photoUrl: string }[] = [];
  @Input() capturedAngles: any = null; // Recibe ángulos del Paso 2

  @Output() saved = new EventEmitter<void>();
  @Output() prev = new EventEmitter<void>();

  diagnosisOptions = diagnosisOptions;
  form!: FormGroup;
  isSaving = false;
  screenSmall = false;

  constructor(private footAnalysisService: FootAnalysisService) {
    this.buildForm();
    this.checkScreenSize();
  }

  private checkScreenSize(): void {
    this.screenSmall = window.innerWidth < 600;
    window.addEventListener('resize', () => {
      this.screenSmall = window.innerWidth < 600;
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['footAnalysis'] && this.footAnalysis) {
      this.form.patchValue({
        relevantBackground:      this.footAnalysis.relevantBackground      ?? '',
        kinesiologicalEvaluation: this.footAnalysis.kinesiologicalEvaluation ?? '',
        observations:            this.footAnalysis.observations            ?? '',
        diagnosis:               this.footAnalysis.diagnosis               ?? null,
        distanceIntermaleolar:   this.footAnalysis.distanceIntermaleolar   ?? null,
        distanceIntercondylar:   this.footAnalysis.distanceIntercondylar   ?? null,
        angleLeftInternal:       this.footAnalysis.angleLeftInternal        ?? null,
        angleLeftExternal:       this.footAnalysis.angleLeftExternal        ?? null,
        angleRightInternal:      this.footAnalysis.angleRightInternal       ?? null,
        angleRightExternal:      this.footAnalysis.angleRightExternal       ?? null,
      });
    }

    if (changes['capturedAngles'] && this.capturedAngles) {
      // Redondear ángulos a máximo 3 decimales antes de parchear el formulario
      const roundedAngles = { ...this.capturedAngles };
      Object.keys(roundedAngles).forEach(key => {
        if (typeof roundedAngles[key] === 'number') {
          roundedAngles[key] = Number(roundedAngles[key].toFixed(3));
        }
      });
      this.form.patchValue(roundedAngles);
    }
  }

  private buildForm(): void {
    this.form = new FormGroup({
      // Datos Generales
      relevantBackground:       new FormControl(''),
      kinesiologicalEvaluation: new FormControl(''),
      observations:             new FormControl(''),
      diagnosis:                new FormControl(null, [Validators.required]),
      distanceIntermaleolar:    new FormControl(null, [Validators.min(0)]),
      distanceIntercondylar:    new FormControl(null, [Validators.min(0)]),

      // Ángulos (Auto-llenado)
      angleLeftInternal:        new FormControl(null, [Validators.min(0), Validators.max(180)]),
      angleLeftExternal:        new FormControl(null, [Validators.min(0), Validators.max(180)]),
      angleRightInternal:       new FormControl(null, [Validators.min(0), Validators.max(180)]),
      angleRightExternal:       new FormControl(null, [Validators.min(0), Validators.max(180)]),

      // Biomecánico Simplificado
      shoeWear:                 new FormControl(''),
      tibiaPalpation:           new FormControl(''),
      gait:                     new FormControl('NORMAL'),

      // Huella Plantar
      footprintType:            new FormControl(null, [Validators.required]),
      footprintNotes:           new FormControl('')
    });
  }

  saveAll(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      Notiflix.Notify.failure('Por favor, complete los campos obligatorios.');
      return;
    }

    this.isSaving = true;
    Notiflix.Loading.pulse('Procesando análisis integral...');

    // Lógica de guardado masivo simulada para esta fase
    setTimeout(() => {
      Notiflix.Loading.remove();
      this.isSaving = false;
      this.saved.emit();
      Notiflix.Notify.success('Análisis guardado con éxito.');
    }, 2000);
  }

  goPrev(): void { this.prev.emit(); }
}
