import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EpisodeListComponent } from './episode-list/episode-list.component';
import { SessionListComponent } from './session-list/session-list.component';
import { SessionFormComponent } from './session-form/session-form.component';

const routes: Routes = [
  { path: '', component: EpisodeListComponent },
  { path: ':episodeId/sessions', component: SessionListComponent },
  { path: ':episodeId/sessions/new', component: SessionFormComponent },
  { path: ':episodeId/sessions/:sessionId', component: SessionFormComponent },
  {
    // Pantalla separada de análisis de pisada
    // /management-pacient/episodes/:episodeId/sessions/:sessionId/imaging
    path: ':episodeId/sessions/:sessionId/imaging',
    loadChildren: () =>
      import('./imaging/imaging.module').then(m => m.ImagingModule),
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ClinicalRoutingModule {}

