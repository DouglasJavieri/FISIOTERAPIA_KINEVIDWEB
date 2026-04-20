import { Injectable } from '@angular/core';
import {
  CanActivate,
  ActivatedRouteSnapshot,
  Router,
  UrlTree
} from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * Guard de control por roles.
 * Se usa SIEMPRE combinado con AuthGuard (AuthGuard verifica sesión,
 * RoleGuard verifica el rol dentro de esa sesión).
 *
 * Configuración en rutas:
 * ─────────────────────────────────────────────────────────────────
 * {
 *   path: 'usuarios',
 *   canActivate: [AuthGuard, RoleGuard],
 *   data: { roles: [AppRole.ADMIN] },
 *   loadChildren: () => import('./user/user.module').then(m => m.UserModule)
 * }
 * ─────────────────────────────────────────────────────────────────
 *
 * IMPORTANTE: Este guard solo lee el campo `role` del UserAuthInfo.
 * El backend es la autoridad real — este guard es solo UX.
 */
@Injectable({ providedIn: 'root' })
export class RoleGuard implements CanActivate {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(route: ActivatedRouteSnapshot): boolean | UrlTree {
    const requiredRoles: string[] = route.data['roles'] ?? [];

    // Sin roles declarados en la ruta → acceso libre (no restringido por rol)
    if (requiredRoles.length === 0) {
      return true;
    }

    // Sin usuario en sesión → redirigir a login (no debería ocurrir si AuthGuard va primero)
    const user = this.authService.getCurrentUser();
    if (!user) {
      return this.router.createUrlTree(['/login']);
    }

    // Verificar si el usuario tiene alguno de los roles requeridos
    if (this.authService.hasAnyRole(requiredRoles)) {
      return true;
    }

    // Rol insuficiente → redirigir al home (acceso denegado silencioso)
    // En el futuro puedes cambiar esto a una ruta /acceso-denegado dedicada
    return this.router.createUrlTree(['/home']);
  }
}

