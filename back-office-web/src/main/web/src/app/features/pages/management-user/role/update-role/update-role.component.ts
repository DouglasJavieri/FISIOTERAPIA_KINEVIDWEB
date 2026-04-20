import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { forkJoin } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import * as Notiflix from 'notiflix';

import { RoleService }              from '../../../../../core/services/roles/role.service';
import { RolePageResponse, RoleRequest } from '../../../../../core/models/roles/role.interface';
import { PermissionService }        from '../../../../../core/services/permission/permission.service';
import { RolePermissionService }    from '../../../../../core/services/role-permission/role-permission.service';
import { PermissionPageResponse }   from '../../../../../core/models/permission/permission.interface';
import { noWhitespaceValidator }    from '../../../../../shared/utils/validators.util';

export interface UpdateRoleDialogData {
  role: RolePageResponse;
}

@Component({
  selector: 'knv-update-role',
  templateUrl: './update-role.component.html',
  styleUrls: ['./update-role.component.scss'],
})
export class UpdateRoleComponent implements OnInit {

  form!: FormGroup;
  permissionList: PermissionPageResponse[] = [];
  permissionsLoading = false;
  private currentPermissionIds: number[] = [];

  constructor(
    private dialogRef: MatDialogRef<UpdateRoleComponent>,
    @Inject(MAT_DIALOG_DATA) public data: UpdateRoleDialogData,
    private roleService: RoleService,
    private permissionService: PermissionService,
    private rolePermissionService: RolePermissionService,
  ) {}

  ngOnInit(): void {
    this.buildForm();
    this.loadData();
  }

  private buildForm(): void {
    const { name, description } = this.data.role;
    this.form = new FormGroup({
      name: new FormControl(name, [
        Validators.required,
        Validators.maxLength(60),
        noWhitespaceValidator(),
      ]),
      description: new FormControl(description, [
        Validators.required,
        Validators.maxLength(255),
        noWhitespaceValidator(),
      ]),
      permissions: new FormControl([]),
    });
  }

  private loadData(): void {
    const roleId = this.data?.role?.id;
    if (!roleId) {
      Notiflix.Report.failure('Operación No Exitosa',
        'No se pudo obtener el ID del rol.',
        'OK');
      this.dialogRef.close(false);
      return;
    }
    this.permissionsLoading = true;
    forkJoin({
      allPermissions:  this.permissionService.getAllForSelect(),
      rolePermissions: this.rolePermissionService.getPermissionsByRoleId(roleId),
    }).subscribe({
      next: ({ allPermissions, rolePermissions }) => {
        this.permissionList       = allPermissions;
        this.currentPermissionIds = rolePermissions.map(p => p.id);
        this.form.get('permissions')?.setValue(this.currentPermissionIds);
        this.permissionsLoading = false;
      },
      error: () => {
        this.permissionsLoading = false;
        Notiflix.Report.warning(
          'Advertencia',
          'No se pudo cargar la lista de permisos.',
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

    const newPermIds: number[] = this.form.value.permissions ?? [];
    const roleId = this.data.role.id;

    Notiflix.Loading.pulse('Actualizando...');

    this.roleService.update(roleId, body).pipe(
      switchMap(() =>
        this.rolePermissionService.syncPermissions(roleId, newPermIds, this.currentPermissionIds)
      )
    ).subscribe({
      next: () => {
        Notiflix.Loading.remove(300);
        Notiflix.Report.success(
          'Operación Exitosa',
          'El rol fue actualizado exitosamente.',
          'OK',
        );
        this.dialogRef.close(true);
      },
      error: (err) => {
        Notiflix.Loading.remove(300);
        Notiflix.Report.failure(
          'Error',
          err?.error?.message ?? 'Ocurrió un error al actualizar el rol.',
          'OK',
        );
      },
    });
  }

  close(): void {
    this.dialogRef.close(false);
  }
}

