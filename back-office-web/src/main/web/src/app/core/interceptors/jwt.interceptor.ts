import { Injectable } from '@angular/core';
import {
  HttpRequest, HttpHandler, HttpEvent,
  HttpInterceptor, HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { catchError, filter, switchMap, take } from 'rxjs/operators';
import { TokenService } from '../services/token.service';
import { AuthService } from '../services/auth.service';
import { ApiResponse, JwtResponse } from '../models/auth.model';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {

  private isRefreshing = false;
  private refreshTokenSubject = new BehaviorSubject<string | null>(null);

  private readonly PUBLIC_URLS = ['/auth/login', '/auth/refresh'];

  constructor(
    private tokenService: TokenService,
    private authService: AuthService
  ) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (this.isPublicUrl(request.url)) {
      return next.handle(request);
    }

    const token = this.tokenService.getAccessToken();
    if (token) {
      request = this.addToken(request, token);
    }

    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          return this.handle401Error(request, next);
        }
        return throwError(() => error);
      })
    );
  }

  private addToken(req: HttpRequest<any>, token: string): HttpRequest<any> {
    return req.clone({ setHeaders: { Authorization: 'Bearer ' + token } });
  }

  private isPublicUrl(url: string): boolean {
    return this.PUBLIC_URLS.some(pub => url.includes(pub));
  }

  private handle401Error(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Requests concurrentes: esperar a que el refresh en curso termine
    if (this.isRefreshing) {
      return this.refreshTokenSubject.pipe(
        filter(token => token !== null),
        take(1),
        switchMap(token => next.handle(this.addToken(request, token!)))
      );
    }

    this.isRefreshing = true;
    this.refreshTokenSubject.next(null);

    return this.authService.refreshToken().pipe(
      switchMap((res: ApiResponse<JwtResponse>) => {
        const newToken = res.data.accessToken;
        this.isRefreshing = false;
        this.refreshTokenSubject.next(newToken);
        return next.handle(this.addToken(request, newToken));
      }),
      catchError(err => {
        this.isRefreshing = false;
        // El refresh también falló (refreshToken expirado/revocado).
        // Usar handleSessionExpired() en lugar de logout() para que el
        // LoginComponent pueda mostrar el mensaje "Tu sesión ha expirado".
        this.authService.handleSessionExpired();
        return throwError(() => err);
      })
    );
  }
}


