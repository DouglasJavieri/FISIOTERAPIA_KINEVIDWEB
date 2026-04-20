import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { switchMap } from 'rxjs/operators';
import { of } from 'rxjs';
import * as Notiflix from 'notiflix';

import { RoleService }              from '../../../../../core/services/roles/role.service';
import { RoleRequest }              from '../../../../../core/models/roles/role.interface';
import { PermissionService }        from '../../../../../core/services/permission/permission.service';
import { RolePermissionService }    from '../../../../../core/services/role-permission/role-permission.service';
import { PermissionPageResponse }   from '../../../../../core/models/permission/permission.interface';
import { noWhitespaceValidator }    from '../../../../../shared/utils/validators.util';

@Component({
  selector: 'knv-add-role',
  templateUrl: './add-role.component.html',
  styleUrls: ['./add-role.component.scss'],
})
export class AddRoleComponent implements OnInit {

  form!: FormGroup;
  permissionList: PermissionPageResponse[] = [];
  permissionsLoading = false;

  constructor(
    private dialogRef: MatDialogRef<AddRoleComponent>,
    private roleService: RoleService,
    private permissionService: PermissionService,
    private rolePermissionService: RolePermissionService,
  ) {}

  ngOnInit(): void {
    this.buildForm();
    this.loadPermissions();
  }

  private buildForm(): void {
    this.form = new FormGroup({
      name: new FormControl('', [
        Validators.required,
        Validators.maxLength(60),
        noWhitespaceValidator(),
      ]),
      description: new FormControl('', [
        Validators.required,
        Validators.maxLength(255),
        noWhitespaceValidator(),
      ]),
      permissions: new FormControl([]),
    });
  }

  private loadPermissions(): void {
    this.permissionsLoading = true;
    this.permissionService.getAllForSelect().subscribe({
      next: (list) => {
        this.permissionList = list;
        this.permissionsLoading = false;
      },
      error: () => {
        this.permissionsLoading = false;
        Notiflix.Report.warning(
          'Advertencia',
          'No se pudo cargar la lista de permisos. Podrás asignarlos después desde editar rol.',
          'OK',
        );
      },
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const body: RoleRequest = {
      name: this.form.value.name.trim(),
      description: this.form.value.description.trim(),
    };

    const selectedPermIds: number[] = this.form.value.permissions ?? [];

    Notiflix.Loading.pulse('Guardando...');

    this.roleService.create(body).pipe(
      switchMap(role => {
        if (selectedPermIds.length === 0) return of(role);
        return this.rolePermissionService.assignMany(role.id, selectedPermIds).pipe(
          switchMap(() => of(role))
        );
      })
    ).subscribe({
      next: () => {
        Notiflix.Loading.remove(300);
        Notiflix.Report.success(
          'Operación Exitosa',
          'El rol fue creado exitosamente.',
          'OK',
        );
        this.dialogRef.close(true);
      },
      error: (err) => {
        Notiflix.Loading.remove(300);
        Notiflix.Report.failure(
          'Error',
          err?.error?.message ?? 'Ocurrió un error al crear el rol.',
          'OK',
        );
      },
    });
  }

  close(): void {
    this.dialogRef.close(false);
  }
}

