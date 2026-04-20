import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { TokenService } from './token.service';
import {
  ApiResponse,
  JwtResponse,
  LoginRequest,
  LogoutRequest,
  RefreshTokenRequest,
  UserAuthInfo
} from '../models/auth.model';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly API = environment.apiUrl;

  /** Emite el usuario actual. null = no autenticado. */
  private currentUserSubject = new BehaviorSubject<UserAuthInfo | null>(this.loadUserFromStorage());
  currentUser$ = this.currentUserSubject.asObservable();

  constructor(
    private http:    HttpClient,
    private tokens:  TokenService,
    private router:  Router
  ) {}

  // ─── AUTH ──────────────────────────────────────────────────────────────────

  login(credentials: LoginRequest): Observable<ApiResponse<JwtResponse>> {
    return this.http.post<ApiResponse<JwtResponse>>(`${this.API}/auth/login`, credentials)
      .pipe(
        tap((response: ApiResponse<JwtResponse>) => {
          const jwt: JwtResponse = response.data;
          this.tokens.saveTokens(jwt.accessToken, jwt.refreshToken);
          // Persiste los datos del usuario: el payload JWT NO los incluye
          this.tokens.saveUserInfo(jwt.userInfo);
          this.currentUserSubject.next(jwt.userInfo);
        })
      );
  }

  refreshToken(): Observable<ApiResponse<JwtResponse>> {
    const refreshToken = this.tokens.getRefreshToken();
    const body: RefreshTokenRequest = { refreshToken: refreshToken! };
    return this.http.post<ApiResponse<JwtResponse>>(`${this.API}/auth/refresh`, body)
      .pipe(
        tap((response: ApiResponse<JwtResponse>) => {
          const jwt: JwtResponse = response.data;
          if (jwt.refreshToken) {
            // Backend con rotación de refresh token: guarda ambos tokens nuevos
            this.tokens.saveTokens(jwt.accessToken, jwt.refreshToken);
          } else {
            // Backend sin rotación: solo actualizar el access token
            // NUNCA sobreescribir el refresh token con undefined
            this.tokens.saveAccessToken(jwt.accessToken);
          }
          this.tokens.saveUserInfo(jwt.userInfo);
          this.currentUserSubject.next(jwt.userInfo);
        })
      );
  }

  /**
   * Cierra sesión: revoca el refreshToken en el servidor (best-effort)
   * y luego limpia la sesión local.
   *
   * IMPORTANTE: clearSession() se llama en finalize() para que el Bearer token
   * siga disponible cuando el interceptor adjunte el header de Authorization.
   * Si lo limpiamos antes, el backend rechaza el logout con 401.
   */
  logout(): void {
    const refreshToken = this.tokens.getRefreshToken();
    const accessToken  = this.tokens.getAccessToken();

    if (refreshToken && accessToken) {
      const body: LogoutRequest = { refreshToken };
      this.http.post(`${this.API}/auth/logout`, body)
        .pipe(finalize(() => this.clearSession()))
        .subscribe({ error: () => {} });
    } else {
      // Sin tokens válidos: limpiar localmente sin llamar al servidor
      this.clearSession();
    }
  }

  // ─── ESTADO ───────────────────────────────────────────────────────────────

  /**
   * Uso: Guards de rutas.
   * Retorna true si el accessToken es válido O si el refreshToken está
   * disponible (el interceptor lo renovará en el siguiente request).
   */
  hasValidSession(): boolean {
    return !this.tokens.isAccessTokenExpired() || !this.tokens.isRefreshTokenExpired();
  }

  /**
   * Uso: Lógica de UI (mostrar/ocultar elementos).
   * Retorna true solo si el accessToken actual es válido.
   * Más estricto que hasValidSession().
   */
  isFullyAuthenticated(): boolean {
    return !this.tokens.isAccessTokenExpired();
  }

  /** @deprecated Usar hasValidSession() para guards o isFullyAuthenticated() para UI. */
  isAuthenticated(): boolean {
    return this.hasValidSession();
  }

  getCurrentUser(): UserAuthInfo | null {
    return this.currentUserSubject.getValue();
  }

  /**
   * Verifica si el usuario actual tiene exactamente el rol indicado.
   * Uso: condiciones simples → authService.hasRole(AppRole.ADMIN)
   */
  hasRole(role: string): boolean {
    return this.getCurrentUser()?.role === role;
  }

  /**
   * Verifica si el usuario actual tiene AL MENOS UNO de los roles indicados.
   * Uso: RoleGuard y directiva *knvHasRole cuando hay múltiples roles permitidos.
   */
  hasAnyRole(roles: string[]): boolean {
    const userRole = this.getCurrentUser()?.role;
    return !!userRole && roles.includes(userRole);
  }

  /**
   * Verifica si el usuario actual tiene el permiso indicado.
   * USO PRINCIPAL para mostrar/ocultar botones y secciones de UI.
   *
   * Ejemplo: authService.hasPermission(AppPermission.CREATE_ROLE)
   */
  hasPermission(permission: string): boolean {
    const perms = this.getCurrentUser()?.permissions ?? [];
    return perms.includes(permission);
  }

  /**
   * Verifica si el usuario tiene AL MENOS UNO de los permisos indicados.
   * Útil para mostrar secciones que requieren cualquiera de varios permisos.
   */
  hasAnyPermission(permissions: string[]): boolean {
    const userPerms = this.getCurrentUser()?.permissions ?? [];
    return permissions.some(p => userPerms.includes(p));
  }

  /**
   * Verifica si el usuario tiene TODOS los permisos indicados.
   */
  hasAllPermissions(permissions: string[]): boolean {
    const userPerms = this.getCurrentUser()?.permissions ?? [];
    return permissions.every(p => userPerms.includes(p));
  }

  /**
   * Indicador de sesión expirada para el LoginComponent.
   * Se activa cuando el interceptor fuerza el logout por refresh fallido.
   * Se auto-resetea en el primer acceso (consumo único).
   */
  private _sessionJustExpired = false;

  get sessionJustExpired(): boolean {
    const val = this._sessionJustExpired;
    this._sessionJustExpired = false; // consumo único — se resetea al leer
    return val;
  }

  /**
   * Llamado EXCLUSIVAMENTE por JwtInterceptor cuando el refresh token falla.
   * Diferencia el cierre de sesión forzado del logout voluntario del usuario.
   */
  handleSessionExpired(): void {
    this._sessionJustExpired = true;
    this.clearSession();
  }

  // ─── PRIVADO ──────────────────────────────────────────────────────────────

  private clearSession(): void {
    this.tokens.clearTokens();
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }

  /**
   * Restaura los datos del usuario al recargar la página (F5).
   *
   * CORRECCIÓN: El payload del JWT solo contiene { sub, type, iat, exp }.
   * Los datos reales del usuario (id, email, role, etc.) se recuperan
   * del storage donde fueron guardados explícitamente en el login/refresh.
   *
   * PROTECCIÓN: Si ambos tokens están expirados al arrancar, se limpian
   * de forma proactiva para no emitir datos de usuario huérfanos.
   */
  private loadUserFromStorage(): UserAuthInfo | null {
    if (typeof localStorage === 'undefined') return null;
    const token = localStorage.getItem('knv_access_token');
    if (!token) return null;

    // Ambos tokens expirados → limpiar storage y arrancar sin sesión
    if (this.tokens.isAccessTokenExpired() && this.tokens.isRefreshTokenExpired()) {
      this.tokens.clearTokens();
      return null;
    }

    return this.tokens.getUserInfo();
  }
}

