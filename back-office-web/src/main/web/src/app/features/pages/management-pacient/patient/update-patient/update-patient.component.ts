import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import * as Notiflix from 'notiflix';

import { PatientService } from '../../../../../core/services/patient/patient.service';
import {
  genderOptions,
  bloodTypeOptions,
  PatientResponse,
  PatientUpdateRequest,
} from '../../../../../core/models/patient/patient.interface';
import {
  noWhitespaceValidator,
  noOnlyWhitespaceValidator,
} from '../../../../../shared/utils/validators.util';

@Component({
  selector: 'knv-update-patient',
  templateUrl: './update-patient.component.html',
  styleUrls: ['./update-patient.component.scss'],
})
export class UpdatePatientComponent implements OnInit {
  form!: FormGroup;
  maxDate = new Date();
  patientId!: number;
  genderOptions = genderOptions;
  bloodTypeOptions = bloodTypeOptions;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private patientService: PatientService
  ) {}

  ngOnInit(): void {
    this.buildForm();
    this.patientId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadPatient();
  }

  private buildForm(): void {
    this.form = new FormGroup({
      firstName: new FormControl('', [Validators.required, Validators.minLength(2), Validators.maxLength(80), noWhitespaceValidator(),]),
      paternalSurname: new FormControl('', [Validators.required, Validators.minLength(2), Validators.maxLength(80), noWhitespaceValidator(),]),
      maternalSurname: new FormControl('', [Validators.maxLength(80), noOnlyWhitespaceValidator(),]),
      ci: new FormControl('', [Validators.required, Validators.minLength(3), Validators.maxLength(20), noWhitespaceValidator(),]),
      gender: new FormControl(null, [Validators.required]),
      birthDate: new FormControl(null, [Validators.required]),
      phone: new FormControl('', [Validators.maxLength(15), Validators.pattern('^[0-9+\\-\\s()]*$'), noOnlyWhitespaceValidator(),]),
      email: new FormControl('', [Validators.email, Validators.maxLength(80),]),
      address: new FormControl('', [Validators.maxLength(200), noOnlyWhitespaceValidator(),]),
      bloodType: new FormControl(null),
      occupation: new FormControl('', [Validators.maxLength(100), noOnlyWhitespaceValidator(),]),
      emergencyContactName: new FormControl('', [Validators.maxLength(100), noOnlyWhitespaceValidator(),]),
      emergencyContactPhone: new FormControl('', [Validators.maxLength(15), Validators.pattern('^[0-9+\\-\\s()]*$'), noOnlyWhitespaceValidator(),]),
      notes: new FormControl('', [Validators.maxLength(500), noOnlyWhitespaceValidator(),]),
    });
  }

  private loadPatient(): void {
    Notiflix.Loading.pulse('Cargando datos...');
    this.patientService.getById(this.patientId).subscribe({
      next: (p: PatientResponse) => {
        this.form.patchValue({
          firstName: p.firstName,
          paternalSurname: p.paternalSurname,
          maternalSurname: p.maternalSurname ?? '',
          ci: p.ci,
          gender: p.gender,
          birthDate: this.parseDate(p.birthDate),
          phone: p.phone ?? '',
          email: p.email ?? '',
          address: p.address ?? '',
          bloodType: p.bloodType ?? null,
          occupation: p.occupation ?? '',
          emergencyContactName: p.emergencyContactName ?? '',
          emergencyContactPhone: p.emergencyContactPhone ?? '',
          notes: p.notes ?? '',
        });
        Notiflix.Loading.remove(300);
      },
      error: (err) => {
        Notiflix.Loading.remove(300);
        Notiflix.Report.failure(
          'Error',
          err?.error?.message ?? 'No se pudo cargar los datos del paciente.',
          'OK',
          () => this.goBack()
        );
      },
    });
  }

  f(name: string) {
    return this.form.get(name);
  }

  private parseDate(dateStr: string): Date {
    const [year, month, day] = dateStr.split('-').map(Number);
    return new Date(year, month - 1, day);
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
    const body: PatientUpdateRequest = {
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

    Notiflix.Loading.pulse('Actualizando...');
    this.patientService.update(this.patientId, body).subscribe({
      next: () => {
        Notiflix.Loading.remove(300);
        Notiflix.Report.success(
          'Operación Exitosa',
          'El paciente fue actualizado exitosamente.',
          'OK',
          () => this.goBack()
        );
      },
      error: (err) => {
        Notiflix.Loading.remove(300);
        Notiflix.Report.failure(
          'Error',
          err?.error?.message ?? 'Ocurrió un error al actualizar el paciente.',
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
