import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';
import { NoAuthGuard } from './core/guards/no-auth.guard';

const routes: Routes = [
  {
    path: 'login',
    canActivate: [NoAuthGuard],
    loadChildren: () => import('./features/login/login.module').then(m => m.LoginModule)
  },
  {
    path: '',
    canActivate: [AuthGuard],
    loadChildren: () => import('./features/pages/pages.module').then(m => m.PagesModule)
  },
  {
    path: '**',
    redirectTo: 'login'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
