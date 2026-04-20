import { Injectable } from '@angular/core';
import { UserAuthInfo } from '../models/auth.model';

const ACCESS_TOKEN_KEY  = 'knv_access_token';
const REFRESH_TOKEN_KEY = 'knv_refresh_token';
const USER_INFO_KEY      = 'knv_user_info';

/**
 * Servicio de gestión de tokens JWT en localStorage.
 * Es la ÚNICA capa que toca directamente el almacenamiento.
 * Todos los demás servicios/interceptores pasan por aquí.
 *
 * NOTA: El payload del JWT sólo contiene { type, sub, iat, exp }.
 * Los datos del usuario (id, email, role, etc.) vienen del body de la
 * respuesta del backend y se persisten por separado en USER_INFO_KEY.
 */
@Injectable({ providedIn: 'root' })
export class TokenService {

  // ─── GUARDAR ──────────────────────────────────────────────────────────────

  saveTokens(accessToken: string, refreshToken: string): void {
    localStorage.setItem(ACCESS_TOKEN_KEY,  accessToken);
    localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
  }

  /**
   * Actualiza solo el accessToken sin tocar el refreshToken.
   * Usado cuando el backend no rota el refreshToken en la respuesta /auth/refresh.
   * Evita sobreescribir el refreshToken con `undefined`.
   */
  saveAccessToken(accessToken: string): void {
    localStorage.setItem(ACCESS_TOKEN_KEY, accessToken);
  }

  /** Persiste la información del usuario autenticado. */
  saveUserInfo(userInfo: UserAuthInfo): void {
    localStorage.setItem(USER_INFO_KEY, JSON.stringify(userInfo));
  }

  // ─── OBTENER ──────────────────────────────────────────────────────────────

  getAccessToken(): string | null {
    return localStorage.getItem(ACCESS_TOKEN_KEY);
  }

  getRefreshToken(): string | null {
    return localStorage.getItem(REFRESH_TOKEN_KEY);
  }

  /**
   * Recupera la información del usuario guardada tras el último login/refresh.
   * Esta es la fuente correcta de datos del usuario, NO el payload del JWT.
   */
  getUserInfo(): UserAuthInfo | null {
    const raw = localStorage.getItem(USER_INFO_KEY);
    if (!raw) return null;
    try {
      return JSON.parse(raw) as UserAuthInfo;
    } catch {
      return null;
    }
  }

  // ─── LIMPIAR ──────────────────────────────────────────────────────────────

  /** Elimina tokens Y datos de usuario del storage. */
  clearTokens(): void {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
    localStorage.removeItem(USER_INFO_KEY);
  }

  // ─── VALIDAR ──────────────────────────────────────────────────────────────

  isAccessTokenExpired(): boolean {
    const token = this.getAccessToken();
    return token ? this.isExpired(token) : true;
  }

  isRefreshTokenExpired(): boolean {
    const token = this.getRefreshToken();
    return token ? this.isExpired(token) : true;
  }

  hasValidTokens(): boolean {
    return !!this.getAccessToken() && !this.isAccessTokenExpired();
  }

  // ─── DECODIFICAR ──────────────────────────────────────────────────────────

  /**
   * Decodifica el payload del JWT sin librería externa.
   * Solo lee la parte pública del payload (no verifica la firma — eso es del backend).
   */
  decodePayload<T = any>(token: string): T | null {
    try {
      const parts = token.split('.');
      if (parts.length !== 3) return null;
      // Decodificar Base64url → Base64 estándar → JSON
      const payload = parts[1].replace(/-/g, '+').replace(/_/g, '/');
      const decoded = atob(payload);
      return JSON.parse(decoded) as T;
    } catch {
      return null;
    }
  }

  /**
   * Devuelve los claims técnicos del accessToken: { sub, type, iat, exp }.
   * Para datos del usuario autenticado (id, email, role, etc.)
   * usar getUserInfo() en su lugar.
   */
  getTokenClaims(): { sub: string; type: string; iat: number; exp: number } | null {
    const token = this.getAccessToken();
    if (!token) return null;
    return this.decodePayload(token);
  }

  // ─── PRIVADO ──────────────────────────────────────────────────────────────

  private isExpired(token: string): boolean {
    const payload = this.decodePayload<{ exp: number }>(token);
    if (!payload?.exp) return true;
    // exp está en segundos, Date.now() en milisegundos
    return Date.now() >= payload.exp * 1000;
  }
}

