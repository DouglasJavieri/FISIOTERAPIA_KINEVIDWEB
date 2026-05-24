import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import * as Notiflix from 'notiflix';

import { AnalysisPhotoService } from '../../../../../../core/services/imaging/analysis-photo.service';
import { AnalysisPhotoResponse, FootAnalysisResponse } from '../../../../../../core/models/imaging/imaging.interface';

/**
 * Sub-paso 1: Galería de fotos.
 * Permite subir hasta 6 fotos al análisis y eliminarlas.
 */
@Component({
  selector: 'knv-photo-gallery',
  templateUrl: './photo-gallery.component.html',
  styleUrls: ['./photo-gallery.component.scss'],
})
export class PhotoGalleryComponent implements OnInit {

  @Input() footAnalysis: FootAnalysisResponse | null = null;
  @Output() next = new EventEmitter<void>();

  photos: AnalysisPhotoResponse[] = [];
  readonly MAX_PHOTOS = 6;

  constructor(private photoService: AnalysisPhotoService) {}

  ngOnInit(): void {
    if (this.footAnalysis) {
      this.loadPhotos();
    }
  }

  private loadPhotos(): void {
    this.photoService.getByAnalysisId(this.footAnalysis!.id).subscribe({
      next: photos => this.photos = photos,
      error: () => {},
    });
  }

  get canUpload(): boolean {
    return this.photos.length < this.MAX_PHOTOS;
  }

  get nextOrderAvailable(): number {
    const usedOrders = this.photos.map(p => p.photoOrder);
    for (let i = 1; i <= this.MAX_PHOTOS; i++) {
      if (!usedOrders.includes(i)) return i;
    }
    return -1;
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length || !this.footAnalysis) return;

    const file = input.files[0];
    const order = this.nextOrderAvailable;
    if (order === -1) return;

    Notiflix.Loading.pulse('Subiendo foto...');
    this.photoService.upload(this.footAnalysis.id, file, order).subscribe({
      next: photo => {
        Notiflix.Loading.remove(300);
        this.photos = [...this.photos, photo].sort((a, b) => a.photoOrder - b.photoOrder);
        Notiflix.Notify.success('Foto subida correctamente.');
        // Limpiar input para permitir volver a seleccionar el mismo archivo
        input.value = '';
      },
      error: err => {
        Notiflix.Loading.remove(300);
        Notiflix.Report.failure('Error', err?.error?.message ?? 'No se pudo subir la foto.', 'OK');
      },
    });
  }

  deletePhoto(photo: AnalysisPhotoResponse): void {
    Notiflix.Confirm.show(
      'Eliminar foto',
      `¿Eliminar la foto en posición ${photo.photoOrder}?`,
      'Sí', 'No',
      () => {
        this.photoService.delete(photo.id).subscribe({
          next: () => {
            this.photos = this.photos.filter(p => p.id !== photo.id);
            Notiflix.Notify.success('Foto eliminada.');
          },
          error: err => Notiflix.Report.failure('Error', err?.error?.message ?? 'Error al eliminar.', 'OK'),
        });
      },
    );
  }

  goNext(): void {
    this.next.emit();
  }
}
