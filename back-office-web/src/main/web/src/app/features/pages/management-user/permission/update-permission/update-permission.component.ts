import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import * as Notiflix from 'notiflix';

import { PermissionService }      from '../../../../../core/services/permission/permission.service';
import { PermissionPageResponse, PermissionRequest } from '../../../../../core/models/permission/permission.interface';
import { noWhitespaceValidator }  from '../../../../../shared/utils/validators.util';

export interface UpdatePermissionDialogData {
  permission: PermissionPageResponse;
}

@Component({
  selector: 'knv-update-permission',
  templateUrl: './update-permission.component.html',
  styleUrls: ['./update-permission.component.scss'],
})
export class UpdatePermissionComponent implements OnInit {

  form!: FormGroup;

  constructor(
    private dialogRef: MatDialogRef<UpdatePermissionComponent>,
    @Inject(MAT_DIALOG_DATA) public data: UpdatePermissionDialogData,
    private permissionService: PermissionService,
  ) {}

  ngOnInit(): void {
    this.buildForm();
  }

  private buildForm(): void {
    const { name, description } = this.data.permission;
    this.form = new FormGroup({
      name: new FormControl(name, [Validators.required, Validators.maxLength(60), noWhitespaceValidator(),]),
      description: new FormControl(description, [Validators.required, Validators.maxLength(255), noWhitespaceValidator(),]),
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const body: PermissionRequest = {
      name: this.form.value.name.trim(),
      description: this.form.value.description.trim(),
    };
    Notiflix.Loading.pulse('Actualizando...');
    this.permissionService.update(this.data.permission.id, body).subscribe({
      next: () => {
        Notiflix.Loading.remove(300);
        Notiflix.Report.success(
          'Operación Exitosa',
          'El permiso fue actualizado exitosamente.',
          'OK',
        );
        this.dialogRef.close(true);
      },
      error: (err) => {
        Notiflix.Loading.remove(300);
        Notiflix.Report.failure(
          'Error',
          err?.error?.message ?? 'Ocurrió un error al actualizar el permiso.',
          'OK',
        );
      },
    });
  }

  close(): void {
    this.dialogRef.close(false);
  }
}

