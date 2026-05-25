import { Component, EventEmitter, Input, OnInit, Output, OnDestroy } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import * as Notiflix from 'notiflix';

import { AnalysisPhotoResponse, FootAnalysisResponse } from '../../../../../../core/models/imaging/imaging.interface';
import { CameraDialogComponent } from './camera-dialog/camera-dialog.component';

/**
 * Sub-paso 1: Galería de fotos.
 * Permite capturar con cámara (vía Dialog) o subir archivos.
 * Las fotos se mantienen temporalmente en memoria hasta el paso final.
 */
@Component({
  selector: 'knv-photo-gallery',
  templateUrl: './photo-gallery.component.html',
  styleUrls: ['./photo-gallery.component.scss'],
})
export class PhotoGalleryComponent implements OnInit, OnDestroy {

  @Input() footAnalysis: FootAnalysisResponse | null = null;
  @Output() next = new EventEmitter<any>(); // Emitirá la lista de fotos temporales

  tempPhotos: { file: File, photoUrl: string, safeUrl: SafeUrl }[] = [];
  readonly MAX_PHOTOS = 6;

  constructor(
    private dialog: MatDialog,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {}

  /** Genera un array vacío para los slots de la UI */
  get emptySlots(): number[] {
    const count = this.MAX_PHOTOS - this.tempPhotos.length - (this.canUpload ? 1 : 0);
    return count > 0 ? Array(count).fill(0) : [];
  }

  ngOnDestroy(): void {
    // Limpiar URLs de objeto para evitar fugas de memoria
    this.tempPhotos.forEach(p => URL.revokeObjectURL(p.photoUrl));
  }

  get canUpload(): boolean {
    return this.tempPhotos.length < this.MAX_PHOTOS;
  }

  // ─── Lógica de Cámara (Dialog) ─────────────────────────────────────────────

  openCamera(): void {
    const dialogRef = this.dialog.open(CameraDialogComponent, {
      width: '90vw',
      maxWidth: '1200px',
      height: '90vh',
      panelClass: 'full-screen-dialog',
      disableClose: true
    });

    // Inyectar callback para recibir fotos capturadas
    dialogRef.componentInstance.onCapture = (file: File, url: string) => {
      if (this.tempPhotos.length < this.MAX_PHOTOS) {
        const safeUrl = this.sanitizer.bypassSecurityTrustUrl(url);
        this.tempPhotos.push({ file, photoUrl: url, safeUrl });
        if (this.tempPhotos.length >= this.MAX_PHOTOS) {
          dialogRef.close();
          Notiflix.Notify.info('Límite de fotos alcanzado.');
        }
      }
    };
  }

  // ─── Lógica de Archivos ────────────────────────────────────────────────────

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) return;

    const files = Array.from(input.files);

    files.forEach(file => {
      if (this.tempPhotos.length < this.MAX_PHOTOS) {
        const url = URL.createObjectURL(file);
        const safeUrl = this.sanitizer.bypassSecurityTrustUrl(url);
        this.tempPhotos.push({ file, photoUrl: url, safeUrl });
      }
    });

    input.value = '';
    Notiflix.Notify.success('Foto añadida.');
  }

  removeTempPhoto(index: number): void {
    // Revocar URL para liberar memoria
    URL.revokeObjectURL(this.tempPhotos[index].photoUrl);
    this.tempPhotos.splice(index, 1);
  }

  goNext(): void {
    // Al avanzar, pasamos las fotos acumuladas al componente padre
    this.next.emit(this.tempPhotos);
  }
}
