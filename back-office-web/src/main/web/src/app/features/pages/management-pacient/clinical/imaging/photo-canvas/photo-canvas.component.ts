import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges, ViewChild, ElementRef, AfterViewInit, HostListener } from '@angular/core';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import * as Notiflix from 'notiflix';

import { AnalysisPhotoService } from '../../../../../../core/services/imaging/analysis-photo.service';
import { AnalysisPhotoResponse, FootAnalysisResponse } from '../../../../../../core/models/imaging/imaging.interface';

interface Point { x: number; y: number; }
interface LegMeasurement {
  vertical: { start: Point, end: Point } | null;
  internal: Point | null;
  external: Point | null;
  angles: { internal: number, external: number };
}

type DrawState = 'IDLE' | 'DRAWING_VERTICAL' | 'CAPTURING_INTERNAL' | 'CAPTURING_EXTERNAL';
type TargetLeg = 'LEFT' | 'RIGHT' | null;

@Component({
  selector: 'knv-photo-canvas',
  templateUrl: './photo-canvas.component.html',
  styleUrls: ['./photo-canvas.component.scss'],
})
export class PhotoCanvasComponent implements OnChanges, AfterViewInit {

  @Input() footAnalysis: FootAnalysisResponse | null = null;
  @Input() tempPhotos: { file: File, photoUrl: string }[] = [];
  @Output() prev = new EventEmitter<void>();
  @Output() next = new EventEmitter<any>();

  @ViewChild('drawingCanvas') canvasRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('baseImage') imageRef!: ElementRef<HTMLImageElement>;

  photos: any[] = [];
  selectedPhoto: any | null = null;

  // Estados de dibujo
  drawState: DrawState = 'IDLE';
  drawMode: TargetLeg = null;

  // Mediciones acumuladas por pierna
  leftAngles = { internal: 0, external: 0 };
  rightAngles = { internal: 0, external: 0 };

  private measurements: Record<string, LegMeasurement> = {
    'LEFT':  { vertical: null, internal: null, external: null, angles: { internal: 0, external: 0 } },
    'RIGHT': { vertical: null, internal: null, external: null, angles: { internal: 0, external: 0 } }
  };

  private ctx!: CanvasRenderingContext2D;
  private mousePos: Point = { x: 0, y: 0 };

  constructor(
    private photoService: AnalysisPhotoService,
    private sanitizer: DomSanitizer
  ) {}

  ngAfterViewInit() {
    this.initCanvas();
  }

  @HostListener('window:resize')
  onResize() {
    this.initCanvas();
    this.redraw();
  }

  private initCanvas() {
    if (!this.canvasRef) return;
    const canvas = this.canvasRef.nativeElement;
    this.ctx = canvas.getContext('2d')!;

    // Ajustar tamaño del canvas al contenedor
    const container = canvas.parentElement;
    if (container) {
      canvas.width = container.clientWidth;
      canvas.height = container.clientHeight;
    }
  }

  onImageLoad(event: any) {
    setTimeout(() => {
      this.initCanvas();
      this.redraw();
    }, 100);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['tempPhotos']) {
      this.loadFromTemp();
    } else if (changes['footAnalysis'] && this.footAnalysis) {
      this.loadPhotos();
    }
  }

  private loadFromTemp(): void {
    this.photos = this.tempPhotos.map((tp, index) => ({
      id: index,
      photoUrl: this.sanitizer.bypassSecurityTrustUrl(tp.photoUrl),
      photoOrder: index + 1,
      isSelected: true,
      hasAnnotations: false,
      annotationsJson: null
    }));
    if (this.photos.length > 0) {
      this.selectPhoto(this.photos[0]);
    }
  }

  private loadPhotos(): void {
    this.photoService.getByAnalysisId(this.footAnalysis!.id).subscribe({
      next: photos => {
        this.photos = photos.map(p => ({
          ...p,
          photoUrl: this.sanitizer.bypassSecurityTrustUrl(p.photoUrl)
        }));
        if (this.photos.length > 0) this.selectPhoto(this.photos[0]);
      },
      error: () => {},
    });
  }

  selectPhoto(photo: any): void {
    this.selectedPhoto = photo;
    this.clearCanvasData();
    setTimeout(() => this.redraw(), 50);
  }

  // ─── Lógica de Dibujo e Instrucciones ──────────────────────────────────────

  get isDrawing(): boolean { return this.drawState !== 'IDLE'; }

  get instructions(): string {
    if (this.drawMode === 'LEFT') {
      if (this.drawState === 'DRAWING_VERTICAL') return 'Haz clic para situar el eje VERTICAL del pie IZQUIERDO';
      if (this.drawState === 'CAPTURING_INTERNAL') return 'Mueve el mouse y haz clic para fijar el ÁNGULO INTERNO (Izq)';
      if (this.drawState === 'CAPTURING_EXTERNAL') return 'Mueve el mouse y haz clic para fijar el ÁNGULO EXTERNO (Izq)';
    }
    if (this.drawMode === 'RIGHT') {
      if (this.drawState === 'DRAWING_VERTICAL') return 'Haz clic para situar el eje VERTICAL del pie DERECHO';
      if (this.drawState === 'CAPTURING_INTERNAL') return 'Mueve el mouse y haz clic para fijar el ÁNGULO INTERNO (Der)';
      if (this.drawState === 'CAPTURING_EXTERNAL') return 'Mueve el mouse y haz clic para fijar el ÁNGULO EXTERNO (Der)';
    }
    return '';
  }

  startDrawing(leg: TargetLeg) {
    this.drawMode = leg;
    this.drawState = 'DRAWING_VERTICAL';
    Notiflix.Notify.info(`Iniciando trazado: Pierna ${leg === 'LEFT' ? 'Izquierda' : 'Derecha'}`);
  }

  onMouseDown(event: MouseEvent) {
    if (this.drawState === 'IDLE') return;

    const rect = this.canvasRef.nativeElement.getBoundingClientRect();
    const x = event.clientX - rect.left;
    const y = event.clientY - rect.top;
    const pos = { x, y };

    const leg = this.drawMode!;
    const m = this.measurements[leg];

    if (this.drawState === 'DRAWING_VERTICAL') {
      // Trazo vertical de tope a tope en el eje X del clic
      m.vertical = {
        start: { x, y: 0 },
        end:   { x, y: this.canvasRef.nativeElement.height }
      };
      this.drawState = 'CAPTURING_INTERNAL';
    }
    else if (this.drawState === 'CAPTURING_INTERNAL') {
      m.internal = pos;
      m.angles.internal = this.calculateAngle(m.vertical!, pos);
      this.syncAngles();
      this.drawState = 'CAPTURING_EXTERNAL';
    }
    else if (this.drawState === 'CAPTURING_EXTERNAL') {
      m.external = pos;
      m.angles.external = this.calculateAngle(m.vertical!, pos);
      this.syncAngles();
      this.drawState = 'IDLE';
      this.drawMode = null;
      Notiflix.Notify.success('Medición de pierna completada.');
    }

    this.redraw();
  }

  onMouseMove(event: MouseEvent) {
    const rect = this.canvasRef.nativeElement.getBoundingClientRect();
    this.mousePos = {
      x: event.clientX - rect.left,
      y: event.clientY - rect.top
    };
    // Siempre redibujamos para mostrar el puntero fantasma o feedback elástico
    this.redraw();
  }

  private redraw() {
    if (!this.ctx) return;
    const canvas = this.canvasRef.nativeElement;
    this.ctx.clearRect(0, 0, canvas.width, canvas.height);

    // 1. Dibujar mediciones existentes (Fijas)
    this.drawLeg(this.measurements['LEFT'], '#4dabf7');
    this.drawLeg(this.measurements['RIGHT'], '#fab005');

    // 2. Dibujar Feedback Visual Activo (Feedback elástico o Puntero Fantasma)
    if (this.isDrawing && this.drawMode) {
      const color = this.drawMode === 'LEFT' ? '#4dabf7' : '#fab005';

      if (this.drawState === 'DRAWING_VERTICAL') {
        // Puntero Fantasma: Línea vertical que sigue al mouse antes del clic
        this.ctx.setLineDash([5, 5]);
        this.ctx.strokeStyle = color;
        this.ctx.lineWidth = 2;
        this.ctx.beginPath();
        this.ctx.moveTo(this.mousePos.x, 0);
        this.ctx.lineTo(this.mousePos.x, canvas.height);
        this.ctx.stroke();
      } else {
        // Feedback elástico para ángulos
        this.drawElasticFeedback(this.measurements[this.drawMode], color);
      }
    }
  }

  private drawLeg(m: LegMeasurement, color: string) {
    if (!m.vertical) return;

    this.ctx.lineWidth = 3;
    this.ctx.setLineDash([5, 5]); // Vertical siempre punteada
    this.ctx.strokeStyle = color;

    // Línea Vertical (de tope a tope)
    this.ctx.beginPath();
    this.ctx.moveTo(m.vertical.start.x, 0);
    this.ctx.lineTo(m.vertical.end.x, this.canvasRef.nativeElement.height);
    this.ctx.stroke();

    // Ángulos (Líneas rectas sólidas)
    this.ctx.setLineDash([]);
    if (m.internal) {
      this.drawTriangle(m.vertical, m.internal, color, 0.2);
      this.drawAngleText(m.vertical, m.internal, m.angles.internal, color);
    }
    if (m.external) {
      this.drawTriangle(m.vertical, m.external, color, 0.2);
      this.drawAngleText(m.vertical, m.external, m.angles.external, color);
    }
  }

  private drawElasticFeedback(m: LegMeasurement, color: string) {
    if (this.drawState === 'CAPTURING_INTERNAL' || this.drawState === 'CAPTURING_EXTERNAL') {
      this.ctx.setLineDash([]); // Líneas del ángulo son sólidas según requisito
      this.ctx.strokeStyle = color;
      this.drawTriangle(m.vertical!, this.mousePos, color, 0.1);
      const liveAngle = this.calculateAngle(m.vertical!, this.mousePos);
      this.drawAngleText(m.vertical!, this.mousePos, liveAngle, color);
    }
  }

  private drawTriangle(v: {start: Point, end: Point}, p: Point, color: string, opacity: number) {
    this.ctx.beginPath();
    this.ctx.moveTo(v.start.x, v.start.y);
    this.ctx.lineTo(p.x, p.y);
    this.ctx.lineTo(v.end.x, v.end.y);
    this.ctx.closePath();
    this.ctx.stroke();
    this.ctx.fillStyle = this.hexToRgba(color, opacity);
    this.ctx.fill();
  }

  private drawAngleText(v: {start: Point, end: Point}, p: Point, angle: number, color: string) {
    this.ctx.fillStyle = '#fff';
    this.ctx.font = 'bold 14px Roboto';
    this.ctx.setLineDash([]);
    this.ctx.fillText(`${angle.toFixed(2)}°`, p.x + 10, p.y);
  }

  private calculateAngle(v: {start: Point, end: Point}, p: Point): number {
    // Usamos ley de cosenos o vectores para hallar el ángulo en el vértice P
    const d1 = Math.sqrt(Math.pow(p.x - v.start.x, 2) + Math.pow(p.y - v.start.y, 2));
    const d2 = Math.sqrt(Math.pow(p.x - v.end.x, 2) + Math.pow(p.y - v.end.y, 2));
    const d3 = Math.sqrt(Math.pow(v.start.x - v.end.x, 2) + Math.pow(v.start.y - v.end.y, 2));

    const cos = (Math.pow(d1, 2) + Math.pow(d2, 2) - Math.pow(d3, 2)) / (2 * d1 * d2);
    const rad = Math.acos(Math.max(-1, Math.min(1, cos)));
    return rad * (180 / Math.PI);
  }

  private syncAngles() {
    this.leftAngles = { ...this.measurements['LEFT'].angles };
    this.rightAngles = { ...this.measurements['RIGHT'].angles };
  }

  confirmMeasurements() {
    if (!this.selectedPhoto) return;

    // Preparar el objeto con todos los ángulos capturados para esta imagen
    const result = {
      angleLeftInternal: this.leftAngles.internal,
      angleLeftExternal: this.leftAngles.external,
      angleRightInternal: this.rightAngles.internal,
      angleRightExternal: this.rightAngles.external
    };

    this.selectedPhoto.hasAnnotations = true;
    this.selectedPhoto.annotationsJson = JSON.stringify(this.measurements);

    Notiflix.Notify.success('Ángulos capturados correctamente.');

    // Emitimos los ángulos para que el componente padre los pase al Stepper 3
    this.next.emit(result);
  }

  clearCanvas() {
    this.clearCanvasData();
    this.redraw();
    Notiflix.Notify.warning('Lienzo limpio.');
  }

  private clearCanvasData() {
    this.measurements = {
      'LEFT':  { vertical: null, internal: null, external: null, angles: { internal: 0, external: 0 } },
      'RIGHT': { vertical: null, internal: null, external: null, angles: { internal: 0, external: 0 } }
    };
    this.syncAngles();
    this.drawState = 'IDLE';
    this.drawMode = null;
  }

  private hexToRgba(hex: string, opacity: number) {
    const r = parseInt(hex.slice(1, 3), 16);
    const g = parseInt(hex.slice(3, 5), 16);
    const b = parseInt(hex.slice(5, 7), 16);
    return `rgba(${r}, ${g}, ${b}, ${opacity})`;
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
