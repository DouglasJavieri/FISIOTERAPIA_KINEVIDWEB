import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

/**
 * Routing del bloque Gestión de Pacientes.
 * Cada sub-sección se carga de forma lazy.
 * El RoleGuard/PermissionGuard ya fue aplicado en pages-routing al llegar aquí.
 */
const routes: Routes = [
  {
    path: 'patients',
    loadChildren: () =>
      import('./patient/patient.module').then(m => m.PatientModule),
  },
  {
    path: 'services',
    loadChildren: () =>
      import('./medical-service/service.module').then(m => m.ServiceModule),
  },
  {
    path: 'episodes',
    loadChildren: () =>
      import('./clinical/clinical.module').then(m => m.ClinicalModule),
  },
  { path: '', redirectTo: 'patients', pathMatch: 'full' },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ManagementPacientRoutingModule {}

