import {
  Directive,
  Input,
  OnDestroy,
  OnInit,
  TemplateRef,
  ViewContainerRef
} from '@angular/core';
import { Subscription } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';

/**
 * Directiva estructural para mostrar/ocultar elementos según el rol del usuario.
 * Funciona igual que *ngIf pero evaluando el rol del usuario autenticado.
 *
 * Uso con un rol:
 * ─────────────────────────────────────────────────────────────
 * <button *knvHasRole="AppRole.ADMIN">Gestionar usuarios</button>
 * ─────────────────────────────────────────────────────────────
 *
 * Uso con múltiples roles (OR — cualquiera de ellos):
 * ─────────────────────────────────────────────────────────────
 * <div *knvHasRole="[AppRole.ADMIN, AppRole.FISIOTERAPEUTA]">
 *   Contenido visible para admin o fisioterapeuta
 * </div>
 * ─────────────────────────────────────────────────────────────
 *
 * IMPORTANTE: Esta directiva es solo UX (oculta en el DOM).
 * La validación real de acceso siempre debe estar en el backend.
 */
@Directive({ selector: '[knvHasRole]' })
export class HasRoleDirective implements OnInit, OnDestroy {

  private _roles: string[] = [];
  private subscription = Subscription.EMPTY;

  @Input()
  set knvHasRole(roles: string | string[]) {
    this._roles = Array.isArray(roles) ? roles : [roles];
    this.updateView();
  }

  constructor(
    private viewContainer: ViewContainerRef,
    private templateRef:   TemplateRef<any>,
    private authService:   AuthService
  ) {}

  ngOnInit(): void {
    // Suscribirse a cambios del usuario (login, logout, refresh)
    // para que la directiva reaccione automáticamente
    this.subscription = this.authService.currentUser$.subscribe(() => {
      this.updateView();
    });
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  private updateView(): void {
    this.viewContainer.clear();
    if (this.authService.hasAnyRole(this._roles)) {
      this.viewContainer.createEmbeddedView(this.templateRef);
    }
  }
}

