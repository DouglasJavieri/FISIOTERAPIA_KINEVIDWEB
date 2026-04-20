import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'knv-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  loginForm!: FormGroup;
  isLoading        = false;
  errorMsg         = '';
  hidePass         = true;
  /** Activo cuando el interceptor forzó el logout por refresh fallido. */
  sessionExpiredMsg = false;

  /** URL a la que redirigir después del login exitoso. */
  private returnUrl = '/home';

  constructor(
    private fb:          FormBuilder,
    private authService: AuthService,
    private router:      Router,
    private route:       ActivatedRoute
  ) {}

  ngOnInit(): void {
    // Verificar si la sesión expiró (flag dejado por el interceptor)
    this.sessionExpiredMsg = this.authService.sessionJustExpired;

    // Leer el returnUrl guardado por AuthGuard (si existe) y sanitizarlo
    const raw = this.route.snapshot.queryParams['returnUrl'] ?? '/home';
    this.returnUrl = this.sanitizeReturnUrl(raw);

    this.loginForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  get username() { return this.loginForm.get('username')!; }
  get password() { return this.loginForm.get('password')!; }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }
    this.isLoading = true;
    this.errorMsg         = '';
    this.sessionExpiredMsg = false; // limpiar ambos mensajes al reintentar

    this.authService.login(this.loginForm.value).subscribe({
      next: () => {
        this.isLoading = false;
        // Redirigir al destino original o al home si no había returnUrl
        this.router.navigateByUrl(this.returnUrl);
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMsg = err?.error?.message
          ?? err?.message
          ?? 'Credenciales incorrectas. Intente de nuevo.';
      }
    });
  }

  /**
   * Valida que el returnUrl sea una ruta interna relativa.
   * Previene ataques de Open Redirect (ej: returnUrl=https://evil.com).
   */
  private sanitizeReturnUrl(url: string): string {
    if (!url || !url.startsWith('/') || url.startsWith('//')) {
      return '/home';
    }
    return url;
  }
}
