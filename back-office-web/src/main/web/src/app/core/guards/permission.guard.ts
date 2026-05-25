import { Injectable } from '@angular/core';
import {
  CanActivate,
  ActivatedRouteSnapshot,
  Router,
  UrlTree
} from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * Guard de control por permisos.
 * Recomendado para permitir roles personalizados/dinámicos.
 *
 * Configuración en rutas:
 * ─────────────────────────────────────────────────────────────────
 * {
 *   path: 'usuarios',
 *   canActivate: [AuthGuard, PermissionGuard],
 *   data: { permissions: ['LIST_USER'] }
 * }
 * ─────────────────────────────────────────────────────────────────
 */
@Injectable({ providedIn: 'root' })
export class PermissionGuard implements CanActivate {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(route: ActivatedRouteSnapshot): boolean | UrlTree {
    const requiredPermissions: string[] = route.data['permissions'] ?? [];

    // Sin permisos declarados en la ruta → acceso libre
    if (requiredPermissions.length === 0) {
      return true;
    }

    const user = this.authService.getCurrentUser();
    if (!user) {
      return this.router.createUrlTree(['/login']);
    }

    // Verificar si el usuario tiene AL MENOS UNO de los permisos requeridos
    if (this.authService.hasAnyPermission(requiredPermissions)) {
      return true;
    }

    // Permiso insuficiente → redirigir al home
    return this.router.createUrlTree(['/home']);
  }
}
