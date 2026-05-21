import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import * as Notiflix from 'notiflix';

import { BiomechanicalAnalysisService } from '../../../../../../core/services/imaging/biomechanical-analysis.service';
import {
  BiomechanicalAnalysisRequest,
  BiomechanicalAnalysisResponse,
  FootAnalysisResponse,
  FootSide,
  footSideOptions,
  gaitOptions,
  tibialMalleolarRuleOptions,
} from '../../../../../../core/models/imaging/imaging.interface';

/**
 * Sub-paso 3: Análisis biomecánico por pie (LEFT / RIGHT).
 * Upsert — si ya existe un registro para el pie, se actualiza.
 */
@Component({
  selector: 'knv-biomechanical-form',
  templateUrl: './biomechanical-form.component.html',
  styleUrls: ['./biomechanical-form.component.scss'],
})
export class BiomechanicalFormComponent implements OnChanges {

  @Input() footAnalysis: FootAnalysisResponse | null = null;
  @Output() prev = new EventEmitter<void>();
  @Output() next = new EventEmitter<void>();

  footSideOptions  = footSideOptions;
  tibialOptions    = tibialMalleolarRuleOptions;
  gaitOptions      = gaitOptions;

  leftForm!: FormGroup;
  rightForm!: FormGroup;

  leftData:  BiomechanicalAnalysisResponse | null = null;
  rightData: BiomechanicalAnalysisResponse | null = null;

  isSavingLeft  = false;
  isSavingRight = false;

  constructor(private biomechanicalService: BiomechanicalAnalysisService) {
    this.buildForms();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['footAnalysis'] && this.footAnalysis) {
      this.loadBiomechanical();
    }
  }

  private buildForms(): void {
    this.leftForm  = this.buildFootForm();
    this.rightForm = this.buildFootForm();
  }

  private buildFootForm(): FormGroup {
    return new FormGroup({
      tibialMalleolarRule: new FormControl(null),
      shoeWear:            new FormControl(''),
      tibiaPalpation:      new FormControl(''),
      gait:                new FormControl(null),
    });
  }

  private loadBiomechanical(): void {
    this.biomechanicalService.getByFootAnalysisId(this.footAnalysis!.id).subscribe({
      next: list => {
        list.forEach(item => {
          if (item.footSide === 'LEFT') {
            this.leftData = item;
            this.leftForm.patchValue({
              tibialMalleolarRule: item.tibialMalleolarRule,
              shoeWear:            item.shoeWear ?? '',
              tibiaPalpation:      item.tibiaPalpation ?? '',
              gait:                item.gait,
            });
          } else {
            this.rightData = item;
            this.rightForm.patchValue({
              tibialMalleolarRule: item.tibialMalleolarRule,
              shoeWear:            item.shoeWear ?? '',
              tibiaPalpation:      item.tibiaPalpation ?? '',
              gait:                item.gait,
            });
          }
        });
      },
      error: () => {},
    });
  }

  saveFoot(side: FootSide): void {
    const form   = side === 'LEFT' ? this.leftForm : this.rightForm;
    const v      = form.value;
    const body: BiomechanicalAnalysisRequest = {
      footSide:            side,
      tibialMalleolarRule: v.tibialMalleolarRule || null,
      shoeWear:            v.shoeWear?.trim()    || null,
      tibiaPalpation:      v.tibiaPalpation?.trim() || null,
      gait:                v.gait || null,
    };

    if (side === 'LEFT') this.isSavingLeft = true;
    else                 this.isSavingRight = true;

    this.biomechanicalService.saveOrUpdate(this.footAnalysis!.id, body).subscribe({
      next: saved => {
        if (side === 'LEFT') { this.leftData = saved;  this.isSavingLeft  = false; }
        else                 { this.rightData = saved; this.isSavingRight = false; }
        Notiflix.Notify.success(`Pie ${side === 'LEFT' ? 'Izquierdo' : 'Derecho'} guardado.`);
      },
      error: err => {
        if (side === 'LEFT') this.isSavingLeft  = false;
        else                 this.isSavingRight = false;
        Notiflix.Report.failure('Error', err?.error?.message ?? 'No se pudo guardar.', 'OK');
      },
    });
  }

  goPrev(): void { this.prev.emit(); }
  goNext(): void { this.next.emit(); }
}
