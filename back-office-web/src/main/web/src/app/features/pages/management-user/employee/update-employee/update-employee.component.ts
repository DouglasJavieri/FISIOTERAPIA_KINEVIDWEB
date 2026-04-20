import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import * as Notiflix from 'notiflix';

import { EmployeeService } from '../../../../../core/services/employee/employee.service';
import {
  bolivianDepartmentOptions,
  EmployeeResponse,
  EmployeeUpdateRequest,
} from '../../../../../core/models/employee/employee.interface';
import {
  noWhitespaceValidator,
  noOnlyWhitespaceValidator,
} from '../../../../../shared/utils/validators.util';

@Component({
  selector: 'knv-update-employee',
  templateUrl: './update-employee.component.html',
  styleUrls: ['./update-employee.component.scss'],
})
export class UpdateEmployeeComponent implements OnInit {

  form!: FormGroup;
  departmentOptions = bolivianDepartmentOptions;
  maxDate = new Date();
  employeeId!: number;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private employeeService: EmployeeService,
  ) {}

  ngOnInit(): void {
    this.buildForm();
    this.employeeId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadEmployee();
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

  private loadEmployee(): void {
    Notiflix.Loading.pulse('Cargando datos...');
    this.employeeService.getById(this.employeeId).subscribe({
      next: (emp: EmployeeResponse) => {
        this.form.patchValue({
          firstName: emp.firstName,
          paternalSurname: emp.paternalSurname,
          maternalSurname: emp.maternalSurname ?? '',
          ci: emp.ci,
          expedition: emp.expedition,
          specialty: emp.specialty,
          phone: emp.phone ?? '',
          admissionDate: this.parseDate(emp.admissionDate),
          address: emp.address ?? '',
          department: emp.department,
          professionalEmail: emp.professionalEmail ?? '',
        });
        Notiflix.Loading.remove(300);
      },
      error: err => {
        Notiflix.Loading.remove(300);
        Notiflix.Report.failure(
          'Error',
          err?.error?.message ?? 'No se pudo cargar los datos del empleado.',
          'OK',
          () => this.goBack(),
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
    const body: EmployeeUpdateRequest = {
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

    Notiflix.Loading.pulse('Actualizando...');
    this.employeeService.update(this.employeeId, body).subscribe({
      next: () => {
        Notiflix.Loading.remove(300);
        Notiflix.Report.success(
          'Operación Exitosa',
          'El empleado fue actualizado exitosamente.',
          'OK',
          () => this.goBack(),
        );
      },
      error: err => {
        Notiflix.Loading.remove(300);
        Notiflix.Report.failure(
          'Error',
          err?.error?.message ?? 'Ocurrió un error al actualizar el empleado.',
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

