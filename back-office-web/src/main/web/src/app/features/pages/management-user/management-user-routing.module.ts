import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

/**
 * Routing del bloque Management Users.
 * Cada sub-sección se carga de forma lazy.
 * El RoleGuard ya fue aplicado en pages-routing al llegar aquí.
 */
const routes: Routes = [
  {
    path: 'permissions',
    loadChildren: () =>
      import('./permission/permission.module').then(m => m.PermissionModule),
  },

  {
    path: 'roles',
    loadChildren: () =>
      import('./role/role.module').then(m => m.RoleModule),
  },

  {
    path: 'users',
    loadChildren: () =>
      import('./user/user.module').then(m => m.UserModule),
  },

  {
    path: 'employees',
    loadChildren: () =>
      import('./employee/employee.module').then(m => m.EmployeeModule),
  },

  { path: '', redirectTo: 'permissions', pathMatch: 'full' },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ManagementUserRoutingModule {}



