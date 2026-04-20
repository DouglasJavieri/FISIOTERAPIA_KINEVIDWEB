import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { MainLayoutComponent } from '../../layout/main-layout/main-layout.component';
import { HomeComponent }       from './home/home.component';
import { RoleGuard }           from '../../core/guards/role.guard';
import { AppRole }             from '../../core/models/auth.model';

const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    children: [

      // ── Home (all authenticated users) ───────────────────────────────
      { path: 'home', component: HomeComponent },

      // ── Management Users (ADMIN / ROOT) ─────────────────────────────────
      {
        path: 'management-users',
        canActivate: [RoleGuard],
        data: { roles: [AppRole.ADMIN, AppRole.ROOT] },
        loadChildren: () =>
          import('./management-user/management-user.module').then(m => m.ManagementUserModule),
      },

      // ── Management Pacient (ADMIN / ROOT / FISIOTERAPEUTA) ────────────
      {
        path: 'management-pacient',
        canActivate: [RoleGuard],
        data: { roles: [AppRole.ADMIN, AppRole.ROOT, AppRole.FISIOTERAPEUTA] },
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
