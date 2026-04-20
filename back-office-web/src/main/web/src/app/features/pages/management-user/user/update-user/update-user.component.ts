import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { forkJoin } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import * as Notiflix from 'notiflix';

import { UserService }      from '../../../../../core/services/user/user.service';
import { UserRoleService }  from '../../../../../core/services/user-role/user-role.service';
import { RoleService }      from '../../../../../core/services/roles/role.service';
import { UserPageResponse, UserUpdateRequest } from '../../../../../core/models/user/user.interface';
import { RolePageResponse, RoleResponse }      from '../../../../../core/models/roles/role.interface';
import { noWhitespaceValidator }               from '../../../../../shared/utils/validators.util';

export interface UpdateUserDialogData {
  user: UserPageResponse;
}

@Component({
  selector: 'knv-update-user',
  templateUrl: './update-user.component.html',
  styleUrls: ['./update-user.component.scss'],
})
export class UpdateUserComponent implements OnInit {

  form!: FormGroup;
  roleList: RolePageResponse[]  = [];
  rolesLoading                  = false;
  private currentRoleId: number | null = null;

  constructor(
    private dialogRef: MatDialogRef<UpdateUserComponent>,
    @Inject(MAT_DIALOG_DATA) public data: UpdateUserDialogData,
    private userService: UserService,
    private userRoleService: UserRoleService,
    private roleService: RoleService,
  ) {}

  ngOnInit(): void {
    this.buildForm();
    this.loadData();
  }

  private buildForm(): void {
    const { username, email } = this.data.user;
    this.form = new FormGroup({
      username: new FormControl(username, [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(30),
        Validators.pattern('^[a-zA-Z0-9_]+$'),
        noWhitespaceValidator(),
      ]),
      email: new FormControl(email, [
        Validators.required,
        Validators.email,
        Validators.maxLength(50),
      ]),
      password: new FormControl('', [
        Validators.minLength(8),
        Validators.maxLength(100),
      ]),
      role: new FormControl(null, [Validators.required]),
    });
  }

  private loadData(): void {
    const userId = this.data?.user?.id;
    if (!userId) {
      Notiflix.Report.failure('Error', 'No se pudo obtener el ID del usuario.', 'OK');
      this.dialogRef.close(false);
      return;
    }
    this.rolesLoading = true;
    forkJoin({
      allRoles:    this.roleService.getAll({ page: 0, size: 200, sortBy: 'name', sortDir: 'ASC', status: 'ACTIVE' }),
      userRoles:   this.userRoleService.getRolesByUserId(userId),
    }).subscribe({
      next: ({ allRoles, userRoles }) => {
        this.roleList     = allRoles.content;
        const currentRole: RoleResponse | undefined = userRoles[0];
        this.currentRoleId = currentRole?.id ?? null;
        this.form.get('role')?.setValue(this.currentRoleId);
        this.rolesLoading = false;
      },
      error: () => {
        this.rolesLoading = false;
        Notiflix.Report.warning('Advertencia', 'No se pudo cargar la información del usuario.', 'OK');
      },
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const userId = this.data.user.id;
    const body: UserUpdateRequest = {
      username: this.form.value.username.trim(),
      email:    this.form.value.email.trim(),
    };
    const pwd: string = this.form.value.password;
    if (pwd && pwd.trim().length > 0) {
      body.password = pwd;
    }

    const newRoleId: number | null = this.form.value.role ?? null;

    Notiflix.Loading.pulse('Actualizando...');

    this.userService.update(userId, body).pipe(
      switchMap(() =>
        this.userRoleService.syncRole(userId, newRoleId, this.currentRoleId)
      )
    ).subscribe({
      next: () => {
        Notiflix.Loading.remove(300);
        Notiflix.Report.success('Operación Exitosa', 'El usuario fue actualizado exitosamente.', 'OK');
        this.dialogRef.close(true);
      },
      error: err => {
        Notiflix.Loading.remove(300);
        Notiflix.Report.failure('Error', err?.error?.message ?? 'Ocurrió un error al actualizar el usuario.', 'OK');
      },
    });
  }

  close(): void {
    this.dialogRef.close(false);
  }
}

