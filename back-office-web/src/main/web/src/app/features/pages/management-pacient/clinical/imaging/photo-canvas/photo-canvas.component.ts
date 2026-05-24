import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import * as Notiflix from 'notiflix';

import { AnalysisPhotoService } from '../../../../../../core/services/imaging/analysis-photo.service';
import { AnalysisPhotoResponse, FootAnalysisResponse } from '../../../../../../core/models/imaging/imaging.interface';

/**
 * Sub-paso 2: Canvas de trazos y anotaciones.
 * Permite seleccionar una foto, dibujar trazos y guardar las anotaciones JSON.
 * La lógica del canvas HTML5 (líneas, ángulos, coordenadas) se implementa en Fase 6B.
 */
@Component({
  selector: 'knv-photo-canvas',
  templateUrl: './photo-canvas.component.html',
  styleUrls: ['./photo-canvas.component.scss'],
})
export class PhotoCanvasComponent implements OnChanges {

  @Input() footAnalysis: FootAnalysisResponse | null = null;
  @Output() prev = new EventEmitter<void>();
  @Output() next = new EventEmitter<void>();

  photos: AnalysisPhotoResponse[] = [];
  selectedPhoto: AnalysisPhotoResponse | null = null;

  constructor(private photoService: AnalysisPhotoService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['footAnalysis'] && this.footAnalysis) {
      this.loadPhotos();
    }
  }

  private loadPhotos(): void {
    this.photoService.getByAnalysisId(this.footAnalysis!.id).subscribe({
      next: photos => {
        this.photos = photos;
        if (photos.length > 0) this.selectedPhoto = photos[0];
      },
      error: () => {},
    });
  }

  selectPhoto(photo: AnalysisPhotoResponse): void {
    this.selectedPhoto = photo;
  }

  toggleSelection(photo: AnalysisPhotoResponse): void {
    this.photoService.toggleSelection(photo.id).subscribe({
      next: updated => {
        const idx = this.photos.findIndex(p => p.id === photo.id);
        if (idx !== -1) this.photos[idx] = updated;
        if (this.selectedPhoto?.id === photo.id) this.selectedPhoto = updated;
        Notiflix.Notify.success(updated.isSelected ? 'Foto marcada para reporte.' : 'Foto desmarcada del reporte.');
      },
      error: err => Notiflix.Report.failure('Error', err?.error?.message ?? 'Error al actualizar selección.', 'OK'),
    });
  }

  saveAnnotations(annotationsJson: string): void {
    if (!this.selectedPhoto) return;
    this.photoService.updateAnnotations(this.selectedPhoto.id, { annotationsJson }).subscribe({
      next: updated => {
        const idx = this.photos.findIndex(p => p.id === this.selectedPhoto!.id);
        if (idx !== -1) this.photos[idx] = updated;
        this.selectedPhoto = updated;
        Notiflix.Notify.success('Anotaciones guardadas.');
      },
      error: err => Notiflix.Report.failure('Error', err?.error?.message ?? 'Error al guardar anotaciones.', 'OK'),
    });
  }

  goPrev(): void { this.prev.emit(); }
  goNext(): void { this.next.emit(); }
}
