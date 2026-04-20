import { Component, OnInit, AfterViewInit } from "@angular/core";
import { FormControl, FormGroup, Validators } from "@angular/forms";
import { MatDialogRef } from "@angular/material/dialog";
import { switchMap } from "rxjs/operators";
import * as Notiflix from "notiflix";
import { UserService } from "../../../../../core/services/user/user.service";
import { UserRoleService } from "../../../../../core/services/user-role/user-role.service";
import { RoleService } from "../../../../../core/services/roles/role.service";
import { UserRequest } from "../../../../../core/models/user/user.interface";
import { RolePageResponse } from "../../../../../core/models/roles/role.interface";
import { noWhitespaceValidator } from "../../../../../shared/utils/validators.util";
@Component({
  selector: "knv-add-user",
  templateUrl: "./add-user.component.html",
  styleUrls: ["./add-user.component.scss"],
})
export class AddUserComponent implements OnInit, AfterViewInit {
  form!: FormGroup;
  roleList: RolePageResponse[] = [];
  hidePassword = true;
  constructor(
    private dialogRef: MatDialogRef<AddUserComponent>,
    private userService: UserService,
    private userRoleService: UserRoleService,
    private roleService: RoleService,
  ) {}
  ngOnInit(): void {
    this.buildForm();
    this.loadRoles();
  }

  ngAfterViewInit(): void {
    setTimeout(() => this.form.reset(), 150);
  }
  private buildForm(): void {
    this.form = new FormGroup({
      username: new FormControl("", [Validators.required, Validators.minLength(3), Validators.maxLength(30), Validators.pattern("^[a-zA-Z0-9_]+$"), noWhitespaceValidator(),]),
      email: new FormControl("", [Validators.required, Validators.email, Validators.maxLength(50),]),
      password: new FormControl("", [Validators.required, Validators.minLength(8), Validators.maxLength(100),]),
      role: new FormControl(null, [Validators.required]),
    });
  }
  private loadRoles(): void {
    this.roleService.getAll({ page: 0, size: 200, sortBy: "name", sortDir: "ASC", status: "ACTIVE" })
      .subscribe({
        next: paginator => {
          this.roleList = paginator.content;
        },
        error: () => {
          Notiflix.Report.warning("Advertencia", "No se pudo cargar la lista de roles.", "OK");
        },
      });
  }
  submit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    const body: UserRequest = {
      username: this.form.value.username.trim(),
      email: this.form.value.email.trim(),
      password: this.form.value.password,
    };
    const selectedRoleId: number = this.form.value.role;
    Notiflix.Loading.pulse("Guardando...");
    this.userService.create(body).pipe(
      switchMap(created => this.userRoleService.assign({ userId: created.id, roleId: selectedRoleId }))
    ).subscribe({
      next: () => {
        Notiflix.Loading.remove(300);
        Notiflix.Report.success("Operacion Exitosa", "El usuario fue creado exitosamente.", "OK");
        this.dialogRef.close(true);
      },
      error: err => {
        Notiflix.Loading.remove(300);
        Notiflix.Report.failure("Error", err?.error?.message ?? "Ocurrio un error al crear el usuario.", "OK");
      },
    });
  }
  close(): void { this.dialogRef.close(false); }
}
