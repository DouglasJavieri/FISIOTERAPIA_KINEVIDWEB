import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import * as Notiflix from 'notiflix';

import { PatientService } from '../../../../../core/services/patient/patient.service';
import { PatientRequest } from '../../../../../core/models/patient/patient.interface';
import {
  genderOptions,
  bloodTypeOptions,
} from '../../../../../core/models/patient/patient.interface';
import {
  noWhitespaceValidator,
  noOnlyWhitespaceValidator,
} from '../../../../../shared/utils/validators.util';

@Component({
  selector: 'knv-add-patient',
  templateUrl: './add-patient.component.html',
  styleUrls: ['./add-patient.component.scss'],
})
export class AddPatientComponent implements OnInit {
  form!: FormGroup;
  maxDate = new Date();
  genderOptions = genderOptions;
  bloodTypeOptions = bloodTypeOptions;

  constructor(
    private router: Router,
    private patientService: PatientService
  ) {}

  ngOnInit(): void {
    this.buildForm();
  }

  private buildForm(): void {
    this.form = new FormGroup({
      firstName: new FormControl('', [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(80),
        noWhitespaceValidator(),
      ]),
      paternalSurname: new FormControl('', [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(80),
        noWhitespaceValidator(),
      ]),
      maternalSurname: new FormControl('', [
        Validators.maxLength(80),
        noOnlyWhitespaceValidator(),
      ]),
      ci: new FormControl('', [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(20),
        noWhitespaceValidator(),
      ]),
      gender: new FormControl(null, [Validators.required]),
      birthDate: new FormControl(null, [Validators.required]),
      phone: new FormControl('', [
        Validators.maxLength(15),
        Validators.pattern('^[0-9+\\-\\s()]*$'),
        noOnlyWhitespaceValidator(),
      ]),
      email: new FormControl('', [Validators.email, Validators.maxLength(80)]),
      address: new FormControl('', [
        Validators.maxLength(200),
        noOnlyWhitespaceValidator(),
      ]),
      bloodType: new FormControl(null),
      occupation: new FormControl('', [
        Validators.maxLength(100),
        noOnlyWhitespaceValidator(),
      ]),
      emergencyContactName: new FormControl('', [
        Validators.maxLength(100),
        noOnlyWhitespaceValidator(),
      ]),
      emergencyContactPhone: new FormControl('', [
        Validators.maxLength(15),
        Validators.pattern('^[0-9+\\-\\s()]*$'),
        noOnlyWhitespaceValidator(),
      ]),
      notes: new FormControl('', [
        Validators.maxLength(500),
        noOnlyWhitespaceValidator(),
      ]),
    });
  }

  f(name: string) {
    return this.form.get(name);
  }

  private formatDate(date: Date): string {
    const y = date.getFullYear();
    const m = String(date.getMonth() + 1).padStart(2, '0');
    const d = String(date.getDate()).padStart(2, '0');
    return `${y}-${m}-${d}`;
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const v = this.form.value;
    const body: PatientRequest = {
      firstName: v.firstName.trim(),
      paternalSurname: v.paternalSurname.trim(),
      maternalSurname: v.maternalSurname?.trim() || null,
      ci: v.ci.trim(),
      gender: v.gender,
      birthDate: this.formatDate(v.birthDate),
      phone: v.phone?.trim() || null,
      email: v.email?.trim().toLowerCase() || null,
      address: v.address?.trim() || null,
      bloodType: v.bloodType || null,
      occupation: v.occupation?.trim() || null,
      emergencyContactName: v.emergencyContactName?.trim() || null,
      emergencyContactPhone: v.emergencyContactPhone?.trim() || null,
      notes: v.notes?.trim() || null,
    };

    Notiflix.Loading.pulse('Guardando...');
    this.patientService.create(body).subscribe({
      next: () => {
        Notiflix.Loading.remove(300);
        Notiflix.Report.success(
          'Operación Exitosa',
          'El paciente fue registrado exitosamente.',
          'OK',
          () => this.goBack()
        );
      },
      error: (err) => {
        Notiflix.Loading.remove(300);
        Notiflix.Report.failure(
          'Error',
          err?.error?.message ?? 'Ocurrió un error al registrar el paciente.',
          'OK'
        );
      },
    });
  }

  cancel(): void {
    this.goBack();
  }

  private goBack(): void {
    this.router.navigate(['/management-pacient/patients']);
  }
}
