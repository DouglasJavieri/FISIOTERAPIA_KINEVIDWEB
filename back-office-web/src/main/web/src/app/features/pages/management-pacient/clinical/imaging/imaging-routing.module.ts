import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FootAnalysisComponent } from './foot-analysis/foot-analysis.component';

/**
 * Routing del módulo de Análisis de Pisada (Imaging).
 * Pantalla separada del stepper principal de sesión clínica.
 * La ruta base viene desde clinical-routing:
 *   /management-pacient/episodes/:episodeId/sessions/:sessionId/imaging
 */
const routes: Routes = [
  { path: '', component: FootAnalysisComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ImagingRoutingModule {}
