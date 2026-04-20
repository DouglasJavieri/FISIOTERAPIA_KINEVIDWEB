import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import * as Notiflix from 'notiflix';

import { EmployeeService } from '../../../../../core/services/employee/employee.service';
import { EmployeeRequest, bolivianDepartmentOptions } from '../../../../../core/models/employee/employee.interface';
import {
  noWhitespaceValidator,
  noOnlyWhitespaceValidator,
} from '../../../../../shared/utils/validators.util';

@Component({
  selector: 'knv-add-employee',
  templateUrl: './add-employee.component.html',
  styleUrls: ['./add-employee.component.scss'],
})
export class AddEmployeeComponent implements OnInit {

  form!: FormGroup;
  departmentOptions = bolivianDepartmentOptions;
  maxDate = new Date();

  constructor(
    private router: Router,
    private employeeService: EmployeeService,
  ) {}

  ngOnInit(): void {
    this.buildForm();
  }

  private buildForm(): void {
    this.form = new FormGroup({
      firstName: new FormControl('', [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(50),
        noWhitespaceValidator(),
      ]),
      paternalSurname: new FormControl('', [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(50),
        noWhitespaceValidator(),
      ]),
      maternalSurname: new FormControl('', [
        Validators.maxLength(50),
        noOnlyWhitespaceValidator(),
      ]),
      ci: new FormControl('', [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(15),
        noWhitespaceValidator(),
      ]),
      expedition: new FormControl(null, [Validators.required]),
      specialty: new FormControl('', [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(100),
        noWhitespaceValidator(),
      ]),
      phone: new FormControl('', [
        Validators.maxLength(15),
        Validators.pattern('^[0-9+\\-\\s()]*$'),
        noOnlyWhitespaceValidator(),
      ]),
      admissionDate: new FormControl(null, [Validators.required]),
      address: new FormControl('', [
        Validators.maxLength(150),
        noOnlyWhitespaceValidator(),
      ]),
      department: new FormControl(null, [Validators.required]),
      professionalEmail: new FormControl('', [
        Validators.email,
        Validators.maxLength(100),
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
    const body: EmployeeRequest = {
      firstName: v.firstName.trim(),
      paternalSurname: v.paternalSurname.trim(),
      maternalSurname: v.maternalSurname?.trim() || null,
      ci: v.ci.trim(),
      expedition: v.expedition,
      specialty: v.specialty.trim(),
      phone: v.phone?.trim() || null,
      admissionDate: this.formatDate(v.admissionDate),
      address: v.address?.trim() || null,
      department: v.department,
      professionalEmail: v.professionalEmail?.trim() || null,
    };

    Notiflix.Loading.pulse('Guardando...');
    this.employeeService.create(body).subscribe({
      next: () => {
        Notiflix.Loading.remove(300);
        Notiflix.Report.success(
          'Operación Exitosa',
          'El empleado fue creado exitosamente.',
          'OK',
          () => this.goBack(),
        );
      },
      error: err => {
        Notiflix.Loading.remove(300);
        Notiflix.Report.failure(
          'Error',
          err?.error?.message ?? 'Ocurrió un error al crear el empleado.',
          'OK',
        );
      },
    });
  }

  cancel(): void {
    this.goBack();
  }

  private goBack(): void {
    this.router.navigate(['/management-users/employees']);
  }
}

