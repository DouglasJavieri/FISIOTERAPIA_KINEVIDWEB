import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import * as Notiflix from 'notiflix';

import { PermissionService } from '../../../../../core/services/permission/permission.service';
import { PermissionRequest }  from '../../../../../core/models/permission/permission.interface';
import { noWhitespaceValidator } from '../../../../../shared/utils/validators.util';

@Component({
  selector: 'knv-add-permission',
  templateUrl: './add-permission.component.html',
  styleUrls: ['./add-permission.component.scss'],
})
export class AddPermissionComponent implements OnInit {

  form!: FormGroup;

  constructor(
    private dialogRef: MatDialogRef<AddPermissionComponent>,
    private permissionService: PermissionService,
  ) {}

  ngOnInit(): void {
    this.buildForm();
  }

  private buildForm(): void {
    this.form = new FormGroup({
      name: new FormControl('', [Validators.required, Validators.maxLength(60), noWhitespaceValidator(),]),
      description: new FormControl('', [Validators.required, Validators.maxLength(255), noWhitespaceValidator(),]),
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
    Notiflix.Loading.pulse('Guardando...');

    this.permissionService.create(body).subscribe({
      next: () => {
        Notiflix.Loading.remove(300);
        Notiflix.Report.success(
          'Operación Exitosa',
          'El permiso fue creado exitosamente.',
          'OK',
        );
        this.dialogRef.close(true);
      },
      error: (err) => {
        Notiflix.Loading.remove(300);
        Notiflix.Report.failure(
          'Error',
          err?.error?.message ?? 'Ocurrió un error al crear el permiso.',
          'OK',
        );
      },
    });
  }

  close(): void {
    this.dialogRef.close(false);
  }
}

