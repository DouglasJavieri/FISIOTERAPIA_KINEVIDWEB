import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import * as Notiflix from 'notiflix';

import { FootprintAnalysisService } from '../../../../../../core/services/imaging/footprint-analysis.service';
import {
  FootAnalysisResponse,
  FootprintAnalysisResponse,
  footprintTypeOptions,
} from '../../../../../../core/models/imaging/imaging.interface';

/**
 * Sub-paso 4: Valoración de Hernández Corvo.
 * El fisioterapeuta selecciona UNA clasificación del tipo de huella plantar.
 */
@Component({
  selector: 'knv-footprint-form',
  templateUrl: './footprint-form.component.html',
  styleUrls: ['./footprint-form.component.scss'],
})
export class FootprintFormComponent implements OnChanges {

  @Input() footAnalysis: FootAnalysisResponse | null = null;
  @Output() prev = new EventEmitter<void>();
  @Output() next = new EventEmitter<void>();

  footprintTypeOptions = footprintTypeOptions;
  form!: FormGroup;
  existing: FootprintAnalysisResponse | null = null;
  isSaving = false;

  constructor(private footprintService: FootprintAnalysisService) {
    this.buildForm();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['footAnalysis'] && this.footAnalysis) {
      this.loadExisting();
    }
  }

  private buildForm(): void {
    this.form = new FormGroup({
      footprintType: new FormControl(null, [Validators.required]),
      notes:         new FormControl(''),
    });
  }

  private loadExisting(): void {
    this.footprintService.getByFootAnalysisId(this.footAnalysis!.id).subscribe({
      next: fp => {
        this.existing = fp;
        this.form.patchValue({
          footprintType: fp.footprintType,
          notes:         fp.notes ?? '',
        });
      },
      error: () => { /* No existe aún — es válido */ },
    });
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const v = this.form.value;
    this.isSaving = true;
    this.footprintService.saveOrUpdate(this.footAnalysis!.id, {
      footprintType: v.footprintType,
      notes:         v.notes?.trim() || null,
    }).subscribe({
      next: saved => {
        this.existing = saved;
        this.isSaving = false;
        Notiflix.Notify.success('Valoración de huella plantar guardada.');
      },
      error: err => {
        this.isSaving = false;
        Notiflix.Report.failure('Error', err?.error?.message ?? 'No se pudo guardar.', 'OK');
      },
    });
  }

  goPrev(): void { this.prev.emit(); }
  goNext(): void { this.next.emit(); }

  get selectedLabel(): string {
    return footprintTypeOptions.find(o => o.value === this.form.value.footprintType)?.label ?? '';
  }
}
