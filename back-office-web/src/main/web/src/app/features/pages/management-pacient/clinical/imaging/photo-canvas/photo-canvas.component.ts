import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
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
  @Input() tempPhotos: { file: File, photoUrl: string }[] = [];
  @Output() prev = new EventEmitter<void>();
  @Output() next = new EventEmitter<void>();

  photos: any[] = [];
  selectedPhoto: any | null = null;

  constructor(
    private photoService: AnalysisPhotoService,
    private sanitizer: DomSanitizer
  ) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['tempPhotos']) {
      this.loadFromTemp();
    } else if (changes['footAnalysis'] && this.footAnalysis) {
      this.loadPhotos();
    }
  }

  private loadFromTemp(): void {
    this.photos = this.tempPhotos.map((tp, index) => ({
      id: index, // ID temporal
      photoUrl: this.sanitizer.bypassSecurityTrustUrl(tp.photoUrl),
      photoOrder: index + 1,
      isSelected: true,
      hasAnnotations: false,
      annotationsJson: null
    }));
    if (this.photos.length > 0) {
      this.selectedPhoto = this.photos[0];
    } else {
      this.selectedPhoto = null;
    }
  }

  private loadPhotos(): void {
    this.photoService.getByAnalysisId(this.footAnalysis!.id).subscribe({
      next: photos => {
        this.photos = photos.map(p => ({
          ...p,
          photoUrl: this.sanitizer.bypassSecurityTrustUrl(p.photoUrl)
        }));
        if (this.photos.length > 0) this.selectedPhoto = this.photos[0];
      },
      error: () => {},
    });
  }

  selectPhoto(photo: any): void {
    this.selectedPhoto = photo;
  }

  saveAnnotations(json: string): void {
    if (!this.selectedPhoto) return;
    this.selectedPhoto.hasAnnotations = true;
    this.selectedPhoto.annotationsJson = json;
    Notiflix.Notify.success('Trazos guardados temporalmente.');
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

  goPrev(): void { this.prev.emit(); }
  goNext(): void { this.next.emit(); }
}
