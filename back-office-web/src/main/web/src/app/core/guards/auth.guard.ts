import { Injectable } from '@angular/core';
import {
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  Router,
  UrlTree
} from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * Guard que protege todas las rutas privadas.
 * Delega la lógica de autenticación a AuthService (única fuente de verdad).
 * Redirige a /login?returnUrl=<ruta-intentada> si no hay sesión válida,
 * para que el LoginComponent pueda devolver al usuario a su destino original.
 */
@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(
    _route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean | UrlTree {

    if (this.authService.hasValidSession()) {
      return true;
    }

    // Guardar la URL intentada para redirigir tras el login
    return this.router.createUrlTree(['/login'], {
      queryParams: { returnUrl: state.url }
    });
  }
}

