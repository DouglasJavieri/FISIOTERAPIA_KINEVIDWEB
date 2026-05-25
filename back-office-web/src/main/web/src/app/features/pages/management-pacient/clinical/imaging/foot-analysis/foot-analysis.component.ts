import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatStepper } from '@angular/material/stepper';
import * as Notiflix from 'notiflix';

import { FootAnalysisService } from '../../../../../../core/services/imaging/foot-analysis.service';
import { FootAnalysisResponse } from '../../../../../../core/models/imaging/imaging.interface';

/**
 * Componente orquestador del análisis de pisada.
 * Controla el stepper de 5 sub-pasos y comparte el estado
 * del análisis actual entre todos los sub-componentes hijos.
 *
 * Ruta: /management-pacient/episodes/:episodeId/sessions/:sessionId/imaging
 */
@Component({
  selector: 'knv-foot-analysis',
  templateUrl: './foot-analysis.component.html',
  styleUrls: ['./foot-analysis.component.scss'],
})
export class FootAnalysisComponent implements OnInit {

  @ViewChild('stepper') stepper!: MatStepper;

  episodeId!: number;
  sessionId!: number;
  footAnalysis: FootAnalysisResponse | null = null;
  tempPhotos: { file: File, photoUrl: string }[] = []; // Almacén temporal de fotos
  finalAngles: any = null; // Ángulos transferidos del paso 2 al 3
  isLoading = true;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private footAnalysisService: FootAnalysisService,
  ) {}

  ngOnInit(): void {
    this.episodeId = Number(this.route.snapshot.paramMap.get('episodeId'));
    this.sessionId = Number(this.route.snapshot.paramMap.get('sessionId'));
    this.loadOrCreateAnalysis();
  }

  /** Captura las fotos enviadas por el sub-paso 1 */
  onPhotosCaptured(photos: { file: File, photoUrl: string }[]): void {
    // Clonamos el array para forzar la detección de cambios en componentes hijos
    this.tempPhotos = [...photos];
    this.nextStep();
  }

  /** Recibe los ángulos confirmados en el paso 2 para pasarlos al paso 3 */
  onAnglesConfirmed(angles: any): void {
    this.finalAngles = angles;
    this.nextStep();
  }

  onAnalysisSaved(): void {
    Notiflix.Notify.success('Análisis de pisada completado y guardado.');
    this.goBackToSession();
  }

  private loadOrCreateAnalysis(): void {
    Notiflix.Loading.pulse('Cargando análisis de pisada...');
    this.footAnalysisService.getBySessionId(this.sessionId).subscribe({
      next: fa => {
        this.footAnalysis = fa;
        this.isLoading = false;
        Notiflix.Loading.remove(300);
      },
      error: () => {
        // Si no existe, se crea en el sub-paso 5 (resumen)
        this.footAnalysis = null;
        this.isLoading = false;
        Notiflix.Loading.remove(300);
      },
    });
  }

  /** Callback que reciben los hijos para notificar que actualizaron el análisis */
  onAnalysisUpdated(updated: FootAnalysisResponse): void {
    this.footAnalysis = updated;
  }

  /** Avanza al siguiente sub-paso */
  nextStep(): void {
    this.stepper.next();
  }

  /** Retrocede al sub-paso anterior */
  prevStep(): void {
    this.stepper.previous();
  }

  /** Regresa a la sesión clínica */
  goBackToSession(): void {
    this.router.navigate([
      '/management-pacient/episodes', this.episodeId,
      'sessions', this.sessionId,
    ]);
  }
}
