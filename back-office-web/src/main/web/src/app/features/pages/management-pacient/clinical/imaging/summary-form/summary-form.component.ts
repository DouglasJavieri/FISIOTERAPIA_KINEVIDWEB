import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import * as Notiflix from 'notiflix';
import { from, of } from 'rxjs';
import { concatMap, last, catchError } from 'rxjs/operators';

import { FootAnalysisService } from '../../../../../../core/services/imaging/foot-analysis.service';
import { AnalysisPhotoService } from '../../../../../../core/services/imaging/analysis-photo.service';
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
export class SummaryFormComponent implements OnChanges, OnInit {

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

  constructor(
    private route: ActivatedRoute,
    private footAnalysisService: FootAnalysisService,
    private photoService: AnalysisPhotoService
  ) {
    this.buildForm();
    this.checkScreenSize();
  }

  private checkScreenSize(): void {
    this.screenSmall = window.innerWidth < 600;
    window.addEventListener('resize', () => {
      this.screenSmall = window.innerWidth < 600;
    });
  }

  ngOnInit(): void {
    // Si no llega por Input, lo tomamos de la ruta como respaldo
    if (!this.sessionId) {
      this.sessionId = Number(this.route.snapshot.paramMap.get('sessionId'));
      console.log('SessionId recuperado de la ruta en SummaryForm:', this.sessionId);
    }
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

      // Cargar datos biomecánicos si existen
      if (this.footAnalysis.biomechanicalAnalysis && this.footAnalysis.biomechanicalAnalysis.length > 0) {
        const left = this.footAnalysis.biomechanicalAnalysis.find(b => b.footSide === 'LEFT');
        const right = this.footAnalysis.biomechanicalAnalysis.find(b => b.footSide === 'RIGHT');

        if (left) {
          this.form.patchValue({
            leftTibialMalleolarRule: left.tibialMalleolarRule,
            leftShoeWear: left.shoeWear,
            leftTibiaPalpation: left.tibiaPalpation,
            leftGait: left.gait
          });
        }
        if (right) {
          this.form.patchValue({
            rightTibialMalleolarRule: right.tibialMalleolarRule,
            rightShoeWear: right.shoeWear,
            rightTibiaPalpation: right.tibiaPalpation,
            rightGait: right.gait
          });
        }
      }

      // Cargar huella plantar si existe
      if (this.footAnalysis.footprintAnalysis && this.footAnalysis.footprintAnalysis.length > 0) {
        const fp = this.footAnalysis.footprintAnalysis[0]; // Es genérica, tomamos la primera
        this.form.patchValue({
          footprintType: fp.footprintType,
          footprintNotes: fp.notes
        });
      }
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

      // Biomecánico
      leftTibialMalleolarRule:   new FormControl('NORMAL'),
      leftShoeWear:              new FormControl(''),
      leftTibiaPalpation:        new FormControl(''),
      leftGait:                  new FormControl('NORMAL'),

      rightTibialMalleolarRule:  new FormControl('NORMAL'),
      rightShoeWear:             new FormControl(''),
      rightTibiaPalpation:       new FormControl(''),
      rightGait:                 new FormControl('NORMAL'),

      // Huella Plantar
      footprintType:            new FormControl(null, [Validators.required]),
      footprintNotes:           new FormControl('')
    });
  }

  saveAll(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      console.error('Formulario inválido:', this.form.errors, this.form.value);
      Notiflix.Notify.failure('Por favor, complete los campos obligatorios.');
      return;
    }

    if (!this.sessionId) {
      console.error('SessionId no disponible en SummaryForm');
      Notiflix.Notify.failure('Error interno: No se identificó la sesión clínica.');
      return;
    }

    this.isSaving = true;
    Notiflix.Loading.pulse('Procesando análisis integral...');

    const formVal = this.form.value;
    console.log('Guardando análisis integral para sesión:', this.sessionId, formVal);

    // Mapeo integral al DTO masivo
    const request = {
      clinicalSessionId: Number(this.sessionId),
      diagnosis: formVal.diagnosis,
      relevantBackground: formVal.relevantBackground,
      kinesiologicalEvaluation: formVal.kinesiologicalEvaluation,
      observations: formVal.observations,
      distanceIntermaleolar: formVal.distanceIntermaleolar,
      distanceIntercondylar: formVal.distanceIntercondylar,
      angleLeftInternal: formVal.angleLeftInternal,
      angleLeftExternal: formVal.angleLeftExternal,
      angleRightInternal: formVal.angleRightInternal,
      angleRightExternal: formVal.angleRightExternal,

      // Datos Biomecánicos
      leftTibialMalleolarRule:  formVal.leftTibialMalleolarRule,
      leftShoeWear:             formVal.leftShoeWear,
      leftTibiaPalpation:       formVal.leftTibiaPalpation,
      leftGait:                 formVal.leftGait,

      rightTibialMalleolarRule: formVal.rightTibialMalleolarRule,
      rightShoeWear:            formVal.rightShoeWear,
      rightTibiaPalpation:      formVal.rightTibiaPalpation,
      rightGait:                formVal.rightGait,

      // Huella Plantar
      footprintType: formVal.footprintType,
      footprintNotes: formVal.footprintNotes
    };

    this.footAnalysisService.saveFull(request).subscribe({
      next: (resp) => {
        console.log('Análisis base guardado con éxito. ID:', resp.id);
        this.footAnalysis = resp; // Actualizar el estado local para obtener el ID

        // Subir fotos temporales si existen
        if (this.tempPhotos && this.tempPhotos.length > 0) {
          console.log('Detectadas fotos temporales para subir:', this.tempPhotos.length);
          Notiflix.Loading.pulse('Subiendo imágenes...');

          from(this.tempPhotos).pipe(
            concatMap((p, index) => {
              const photoOrder = index + 1;
              console.log(`Subiendo foto ${photoOrder}...`);
              return this.photoService.upload(resp.id, p.file, photoOrder).pipe(
                catchError(err => {
                  console.error(`Error subiendo foto ${photoOrder}:`, err);
                  return of(null); // Continuar con las demás
                })
              );
            }),
            last() // Esperar a que termine la última subida
          ).subscribe({
            next: (finalResp) => {
              console.log('Proceso de subida de fotos finalizado.');
              Notiflix.Loading.remove();
              this.isSaving = false;
              Notiflix.Notify.success('Análisis y fotos guardados exitosamente.');
              this.saved.emit();
            },
            error: (err) => {
              console.error('Error fatal en el flujo de subida:', err);
              Notiflix.Loading.remove();
              this.isSaving = false;
              Notiflix.Notify.warning('Análisis guardado, pero hubo un error inesperado al procesar las fotos.');
              this.saved.emit();
            }
          });
        } else {
          console.warn('No se encontraron fotos temporales para subir.');
          Notiflix.Loading.remove();
          this.isSaving = false;
          Notiflix.Notify.success('Análisis integral guardado exitosamente.');
          this.saved.emit();
        }
      },
      error: (err) => {
        Notiflix.Loading.remove();
        this.isSaving = false;
        Notiflix.Notify.failure('Error al guardar el análisis: ' + (err.error?.message || err.message));
      }
    });
  }

  downloadReport(): void {
    if (!this.footAnalysis?.id) {
      Notiflix.Notify.warning('Debe guardar el análisis antes de descargar el reporte.');
      return;
    }

    Notiflix.Loading.pulse('Generando reporte PDF...');
    this.footAnalysisService.downloadReport(this.footAnalysis.id).subscribe({
      next: (blob) => {
        Notiflix.Loading.remove();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `Reporte_Analisis_Pisada_${this.footAnalysis?.id}.pdf`;
        a.click();
        window.URL.revokeObjectURL(url);
        Notiflix.Notify.success('Reporte descargado con éxito.');
      },
      error: (err) => {
        Notiflix.Loading.remove();
        Notiflix.Notify.failure('Error al generar el reporte.');
      }
    });
  }

  goPrev(): void { this.prev.emit(); }
}
