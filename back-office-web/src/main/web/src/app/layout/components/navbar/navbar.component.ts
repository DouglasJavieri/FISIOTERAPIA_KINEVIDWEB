import { Component, EventEmitter, Output, Input, ElementRef, HostListener } from '@angular/core';
import { UserAuthInfo } from '../../../core/models/auth.model';

@Component({
  selector: 'knv-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent {
  @Input() user: UserAuthInfo | null = null;
  @Input() sidebarCollapsed = false;

  @Output() toggleSidebar = new EventEmitter<void>();
  @Output() logout = new EventEmitter<void>();

  showUserMenu = false;

  constructor(private host: ElementRef<HTMLElement>) {}

  get isAuthenticated(): boolean {
    return this.user !== null;
  }

  get displayNombre(): string {
    return this.user?.username ?? '';
  }

  get displayEmail(): string {
    return this.user?.email ?? '';
  }

  onToggleSidebar(): void {
    this.toggleSidebar.emit();
  }

  toggleUserMenu(): void {
    if (!this.isAuthenticated) return;
    this.showUserMenu = !this.showUserMenu;
  }

  onLogout(): void {
    this.showUserMenu = false;
    this.logout.emit();
  }

  closeUserMenu(): void {
    this.showUserMenu = false;
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    if (!this.showUserMenu) return;
    const clickedInside = this.host.nativeElement.contains(event.target as Node);
    if (!clickedInside) {
      this.showUserMenu = false;
    }
  }
}


