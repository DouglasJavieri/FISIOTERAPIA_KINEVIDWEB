import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import * as Notiflix from 'notiflix';

import { MedicalServiceService } from '../../../../../core/services/medical-service/medical-service.service';
import {
  MedicalServiceRequest,
  serviceCategoryOptions,
} from '../../../../../core/models/medical-service/medical-service.interface';
import { noWhitespaceValidator, noOnlyWhitespaceValidator } from '../../../../../shared/utils/validators.util';

@Component({
  selector: 'knv-add-service',
  templateUrl: './add-service.component.html',
  styleUrls: ['./add-service.component.scss'],
})
export class AddServiceComponent implements OnInit {

  form!: FormGroup;
  categoryOptions = serviceCategoryOptions;

  constructor(
    private router:                Router,
    private medicalServiceService: MedicalServiceService,
  ) {}

  ngOnInit(): void {
    this.buildForm();
  }

  private buildForm(): void {
    this.form = new FormGroup({
      name: new FormControl('', [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(100),
        noWhitespaceValidator(),
      ]),
      description: new FormControl('', [
        Validators.maxLength(300),
        noOnlyWhitespaceValidator(),
      ]),
      category: new FormControl(null, [Validators.required]),
      durationMinutes: new FormControl(null, [
        Validators.min(1),
        Validators.max(480),
      ]),
      price: new FormControl(null, [
        Validators.min(0.01),
      ]),
    });
  }

  f(name: string) {
    return this.form.get(name);
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const v = this.form.value;
    const body: MedicalServiceRequest = {
      name:            v.name.trim(),
      description:     v.description?.trim() || null,
      category:        v.category,
      durationMinutes: v.durationMinutes ?? null,
      price:           v.price ?? null,
    };

    Notiflix.Loading.pulse('Guardando...');
    this.medicalServiceService.create(body).subscribe({
      next: () => {
        Notiflix.Loading.remove(300);
        Notiflix.Report.success(
          'Operación Exitosa',
          'El servicio fue creado exitosamente.',
          'OK',
          () => this.goBack(),
        );
      },
      error: err => {
        Notiflix.Loading.remove(300);
        Notiflix.Report.failure(
          'Error',
          err?.error?.message ?? 'Ocurrió un error al crear el servicio.',
          'OK',
        );
      },
    });
  }

  cancel(): void {
    this.goBack();
  }

  private goBack(): void {
    this.router.navigate(['/management-pacient/services']);
  }
}

