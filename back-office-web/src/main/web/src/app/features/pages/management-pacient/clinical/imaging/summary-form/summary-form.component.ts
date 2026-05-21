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
  @Output() analysisUpdated = new EventEmitter<FootAnalysisResponse>();
  @Output() prev   = new EventEmitter<void>();
  @Output() finish = new EventEmitter<void>();

  diagnosisOptions = diagnosisOptions;
  form!: FormGroup;
  isSaving = false;

  constructor(private footAnalysisService: FootAnalysisService) {
    this.buildForm();
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
  }

  private buildForm(): void {
    this.form = new FormGroup({
      relevantBackground:       new FormControl(''),
      kinesiologicalEvaluation: new FormControl(''),
      observations:             new FormControl(''),
      diagnosis:                new FormControl(null, [Validators.required]),
      distanceIntermaleolar:    new FormControl(null, [Validators.min(0)]),
      distanceIntercondylar:    new FormControl(null, [Validators.min(0)]),
      angleLeftInternal:        new FormControl(null, [Validators.min(0), Validators.max(180)]),
      angleLeftExternal:        new FormControl(null, [Validators.min(0), Validators.max(180)]),
      angleRightInternal:       new FormControl(null, [Validators.min(0), Validators.max(180)]),
      angleRightExternal:       new FormControl(null, [Validators.min(0), Validators.max(180)]),
    });
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const v = this.form.value;
    this.isSaving = true;

    if (this.footAnalysis) {
      // Actualizar análisis existente
      const body: FootAnalysisUpdateRequest = {
        relevantBackground:       v.relevantBackground?.trim()       || null,
        kinesiologicalEvaluation: v.kinesiologicalEvaluation?.trim() || null,
        observations:             v.observations?.trim()             || null,
        diagnosis:                v.diagnosis,
        distanceIntermaleolar:    v.distanceIntermaleolar,
        distanceIntercondylar:    v.distanceIntercondylar,
        angleLeftInternal:        v.angleLeftInternal,
        angleLeftExternal:        v.angleLeftExternal,
        angleRightInternal:       v.angleRightInternal,
        angleRightExternal:       v.angleRightExternal,
      };
      this.footAnalysisService.update(this.footAnalysis.id, body).subscribe({
        next: updated => {
          this.isSaving = false;
          this.analysisUpdated.emit(updated);
          Notiflix.Notify.success('Resumen guardado correctamente.');
        },
        error: err => {
          this.isSaving = false;
          Notiflix.Report.failure('Error', err?.error?.message ?? 'No se pudo guardar.', 'OK');
        },
      });
    } else {
      // Crear nuevo análisis
      const body: FootAnalysisRequest = {
        clinicalSessionId:        this.sessionId,
        relevantBackground:       v.relevantBackground?.trim()       || null,
        kinesiologicalEvaluation: v.kinesiologicalEvaluation?.trim() || null,
        observations:             v.observations?.trim()             || null,
        diagnosis:                v.diagnosis,
        distanceIntermaleolar:    v.distanceIntermaleolar,
        distanceIntercondylar:    v.distanceIntercondylar,
        angleLeftInternal:        v.angleLeftInternal,
        angleLeftExternal:        v.angleLeftExternal,
        angleRightInternal:       v.angleRightInternal,
        angleRightExternal:       v.angleRightExternal,
      };
      this.footAnalysisService.create(body).subscribe({
        next: created => {
          this.isSaving = false;
          this.analysisUpdated.emit(created);
          Notiflix.Notify.success('Análisis de pisada creado correctamente.');
        },
        error: err => {
          this.isSaving = false;
          Notiflix.Report.failure('Error', err?.error?.message ?? 'No se pudo crear el análisis.', 'OK');
        },
      });
    }
  }

  goFinish(): void { this.finish.emit(); }
  goPrev():   void { this.prev.emit(); }

  f(name: string) { return this.form.get(name); }
}
