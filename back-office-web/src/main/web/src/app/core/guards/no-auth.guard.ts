import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * Guard exclusivo para la ruta /login.
 * Si el usuario YA tiene una sesión válida, lo redirige a /home
 * para evitar que vea el formulario de login estando autenticado.
 *
 * Ejemplo de flujo bloqueado:
 *   Usuario autenticado escribe manualmente /login en la barra
 *   → NoAuthGuard → redirige a /home automáticamente.
 */
@Injectable({ providedIn: 'root' })
export class NoAuthGuard implements CanActivate {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(): boolean | UrlTree {
    if (this.authService.hasValidSession()) {
      // Ya autenticado → redirigir al home, no mostrar login
      return this.router.createUrlTree(['/home']);
    }
    // Sin sesión → permitir acceso a /login
    return true;
  }
}

