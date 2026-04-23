import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import * as Notiflix from 'notiflix';

import { MedicalServiceService } from '../../../../../core/services/medical-service/medical-service.service';
import {
  MedicalServiceResponse,
  MedicalServiceUpdateRequest,
  serviceCategoryOptions,
} from '../../../../../core/models/medical-service/medical-service.interface';
import { noWhitespaceValidator, noOnlyWhitespaceValidator } from '../../../../../shared/utils/validators.util';

@Component({
  selector: 'knv-update-service',
  templateUrl: './update-service.component.html',
  styleUrls: ['./update-service.component.scss'],
})
export class UpdateServiceComponent implements OnInit {

  form!: FormGroup;
  categoryOptions = serviceCategoryOptions;
  serviceId!: number;

  constructor(
    private route:                 ActivatedRoute,
    private router:                Router,
    private medicalServiceService: MedicalServiceService,
  ) {}

  ngOnInit(): void {
    this.buildForm();
    this.serviceId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadService();
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

  private loadService(): void {
    Notiflix.Loading.pulse('Cargando datos...');
    this.medicalServiceService.getById(this.serviceId).subscribe({
      next: (svc: MedicalServiceResponse) => {
        this.form.patchValue({
          name:            svc.name,
          description:     svc.description ?? '',
          category:        svc.category,
          durationMinutes: svc.durationMinutes ?? null,
          price:           svc.price ?? null,
        });
        Notiflix.Loading.remove(300);
      },
      error: err => {
        Notiflix.Loading.remove(300);
        Notiflix.Report.failure(
          'Error',
          err?.error?.message ?? 'No se pudo cargar los datos del servicio.',
          'OK',
          () => this.goBack(),
        );
      },
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
    const body: MedicalServiceUpdateRequest = {
      name:            v.name.trim(),
      description:     v.description?.trim() || null,
      category:        v.category,
      durationMinutes: v.durationMinutes ?? null,
      price:           v.price ?? null,
    };

    Notiflix.Loading.pulse('Actualizando...');
    this.medicalServiceService.update(this.serviceId, body).subscribe({
      next: () => {
        Notiflix.Loading.remove(300);
        Notiflix.Report.success(
          'Operación Exitosa',
          'El servicio fue actualizado exitosamente.',
          'OK',
          () => this.goBack(),
        );
      },
      error: err => {
        Notiflix.Loading.remove(300);
        Notiflix.Report.failure(
          'Error',
          err?.error?.message ?? 'Ocurrió un error al actualizar el servicio.',
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

