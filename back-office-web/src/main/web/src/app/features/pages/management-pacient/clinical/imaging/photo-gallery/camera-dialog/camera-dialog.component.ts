import { Component, ElementRef, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import * as Notiflix from 'notiflix';

/**
 * Diálogo para la captura de fotos mediante la cámara web.
 * Estructura ordenada: Componente separado en su propia carpeta.
 */
@Component({
  selector: 'knv-camera-dialog',
  templateUrl: './camera-dialog.component.html',
  styleUrls: ['./camera-dialog.component.scss']
})
export class CameraDialogComponent implements OnInit, OnDestroy {
  @ViewChild('video') videoElement!: ElementRef<HTMLVideoElement>;
  @ViewChild('canvas') canvasElement!: ElementRef<HTMLCanvasElement>;

  private stream: MediaStream | null = null;
  capturedCount = 0;
  isCountingDown = false;
  counter = 0;

  constructor(private dialogRef: MatDialogRef<CameraDialogComponent>) {}

  async ngOnInit() {
    try {
      this.stream = await navigator.mediaDevices.getUserMedia({
        video: {
          facingMode: 'environment',
          width: { ideal: 1920 },
          height: { ideal: 1080 }
        }
      });
      if (this.videoElement) {
        this.videoElement.nativeElement.srcObject = this.stream;
      }
    } catch (err) {
      console.error('Error cámara:', err);
      Notiflix.Notify.failure('Error al acceder a la cámara. Verifique los permisos del navegador.');
      this.dialogRef.close();
    }
  }

  ngOnDestroy() {
    this.stopCamera();
  }

  stopCamera() {
    if (this.stream) {
      this.stream.getTracks().forEach(t => t.stop());
    }
  }

  takeSnapshot() {
    const video = this.videoElement.nativeElement;
    const canvas = this.canvasElement.nativeElement;
    const context = canvas.getContext('2d');

    if (!context) return;

    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;
    context.drawImage(video, 0, 0);

    canvas.toBlob((blob) => {
      if (blob) {
        const file = new File([blob], `capture_${Date.now()}.jpg`, { type: 'image/jpeg' });
        const photoUrl = URL.createObjectURL(blob);
        this.capturedCount++;
        // Llamamos al método que el padre sobreescribirá o escuchará
        this.onCapture(file, photoUrl);
        Notiflix.Notify.success('Foto capturada con éxito.');
      }
    }, 'image/jpeg', 0.95);
  }

  /** Hook para comunicación con el componente padre */
  onCapture(file: File, url: string) {}

  close() {
    this.dialogRef.close();
  }
}
