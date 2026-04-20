import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import * as Notiflix from 'notiflix';

import { EmployeeService } from '../../../../../core/services/employee/employee.service';
import { EmployeePageResponse } from '../../../../../core/models/employee/employee.interface';
import { UserResponse } from '../../../../../core/models/user/user.interface';

export interface AssignUserDialogData {
  employee: EmployeePageResponse;
}

@Component({
  selector: 'knv-assign-user-employee',
  templateUrl: './assign-user-employee.component.html',
  styleUrls: ['./assign-user-employee.component.scss'],
})
export class AssignUserEmployeeComponent implements OnInit {

  form!: FormGroup;
  userList: UserResponse[] = [];
  private originalUserId: number | null = null;

  constructor(
    private dialogRef: MatDialogRef<AssignUserEmployeeComponent>,
    @Inject(MAT_DIALOG_DATA) public data: AssignUserDialogData,
    private employeeService: EmployeeService,
  ) {}

  ngOnInit(): void {
    this.originalUserId = this.data.employee.userId ?? null;
    this.buildForm();
    this.loadUsers();
  }

  private buildForm(): void {
    this.form = new FormGroup({
      userId: new FormControl(this.originalUserId),
    });
  }

  private loadUsers(): void {
    Notiflix.Loading.pulse('Cargando usuarios...');
    this.employeeService.getUsersAvailable().subscribe({
      next: (users) => {
        const emp = this.data.employee;
        if (emp.userId && emp.username) {
          const currentUser: UserResponse = {
            id: emp.userId,
            username: emp.username,
            email: emp.email ?? '',
            status: 'ACTIVE',
          };
          this.userList = [currentUser, ...users.filter(u => u.id !== currentUser.id)];
        } else {
          this.userList = users;
        }
        Notiflix.Loading.remove(300);
      },
      error: () => {
        Notiflix.Loading.remove(300);
        Notiflix.Report.warning('Advertencia', 'No se pudo cargar la lista de usuarios disponibles.', 'OK');
      },
    });
  }

  submit(): void {
    const selectedUserId: number | null = this.form.value.userId ?? null;

    // Sin cambios → cerrar sin llamar al backend
    if (selectedUserId === this.originalUserId) {
      this.dialogRef.close(false);
      return;
    }

    const employeeId = this.data.employee.id;
    Notiflix.Loading.pulse('Guardando...');

    if (selectedUserId !== null) {
      this.employeeService.assignUser(employeeId, { userId: selectedUserId }).subscribe({
        next: () => {
          Notiflix.Loading.remove(300);
          Notiflix.Report.success('Operación Exitosa',
            'El usuario fue asignado al empleado.',
            'OK');
          this.dialogRef.close(true);
        },
        error: err => {
          Notiflix.Loading.remove(300);
          Notiflix.Report.failure('Error', err?.error?.message ?? 'Ocurrió un error al asignar el usuario.', 'OK');
        },
      });
    } else {
      this.employeeService.removeUser(employeeId).subscribe({
        next: () => {
          Notiflix.Loading.remove(300);
          Notiflix.Report.success('Operación Exitosa',
            'El usuario fue removido del empleado.',
            'OK');
          this.dialogRef.close(true);
        },
        error: err => {
          Notiflix.Loading.remove(300);
          Notiflix.Report.failure('Error', err?.error?.message ?? 'Ocurrió un error al remover el usuario.', 'OK');
        },
      });
    }
  }

  close(): void {
    this.dialogRef.close(false);
  }
}

