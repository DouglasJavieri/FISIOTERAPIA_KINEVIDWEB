import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { MainLayoutComponent } from '../../layout/main-layout/main-layout.component';
import { HomeComponent }       from './home/home.component';
import { RoleGuard }           from '../../core/guards/role.guard';
import { PermissionGuard }     from '../../core/guards/permission.guard';
import { AppPermission, AppRole } from '../../core/models/auth.model';

const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    children: [

      // ── Home (all authenticated users) ───────────────────────────────
      { path: 'home', component: HomeComponent },

      // ── Management Users ─────────────────────────────────
      {
        path: 'management-users',
        canActivate: [PermissionGuard],
        data: { permissions: [AppPermission.LIST_USER] },
        loadChildren: () =>
          import('./management-user/management-user.module').then(m => m.ManagementUserModule),
      },

      // ── Management Pacient ─────────────────────────────────
      {
        path: 'management-pacient',
        canActivate: [PermissionGuard],
        data: { permissions: [AppPermission.LIST_PATIENT] },
        loadChildren: () =>
          import('./management-pacient/management-pacient.module').then(m => m.ManagementPacientModule),
      },

      { path: '', redirectTo: 'home', pathMatch: 'full' },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PagesRoutingModule {}
