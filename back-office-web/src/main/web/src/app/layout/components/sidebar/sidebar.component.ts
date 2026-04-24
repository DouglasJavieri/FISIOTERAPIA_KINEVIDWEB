import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { trigger, transition, style, animate } from '@angular/animations';
import { MenuItem } from '../../../shared/models/menu.interface';
import { AuthService } from '../../../core/services/auth.service';
import { AppPermission, AppRole } from '../../../core/models/auth.model';

export const expandCollapse = trigger('expandCollapse', [
  transition(':enter', [
    style({ height: '0px', opacity: 0, overflow: 'hidden' }),
    animate('320ms cubic-bezier(0.4, 0, 0.2, 1)',
      style({ height: '*', opacity: 1, overflow: 'hidden' })
    ),
  ]),
  transition(':leave', [
    style({ height: '*', opacity: 1, overflow: 'hidden' }),
    animate('260ms cubic-bezier(0.4, 0, 0.2, 1)',
      style({ height: '0px', opacity: 0, overflow: 'hidden' })
    ),
  ]),
]);

@Component({
  selector: 'knv-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['sidebar.component.scss'],
  animations: [expandCollapse],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SidebarComponent implements OnInit {
  @Input() isOpen = true;
  @Output() toggleSidebar = new EventEmitter<void>();

  expandedItems: Set<string> = new Set();
  logoError = false;
  menuItems: MenuItem[] = [];

  constructor(public authService: AuthService) {}

  ngOnInit(): void {
    this.menuItems = this._buildMenuItems();
  }

  private _buildMenuItems(): MenuItem[] {
    const items: MenuItem[] = [
      {
        id: 'home',
        label: 'Home',
        icon: 'dashboard',
        route: '/home',
        active: true
      },
    ];

    // ── Gestión de Usuarios (solo ADMIN / ROOT) ──────────────────────────────
    if (this.authService.hasAnyRole([AppRole.ADMIN, AppRole.ROOT])) {
      items.push({
        id: 'management-users',
        label: 'Gestión de Usuarios',
        icon: 'admin_panel_settings',
        route: null,
        children: [
          { id: 'users',label: 'Usuarios', icon: 'person', route: '/management-users/users' },
          { id: 'employees', label: 'Empleados', icon: 'badge', route: '/management-users/employees' },
          { id: 'roles', label: 'Roles', icon: 'security',  route: '/management-users/roles' },
          { id: 'permissions', label: 'Permisos', icon: 'lock', route: '/management-users/permissions' },
        ]
      });
    }

    // ── Gestión de Pacientes (ADMIN, ROOT, FISIOTERAPEUTA, RECEPCIONISTA) ───
    if (this.authService.hasPermission(AppPermission.LIST_PATIENT)) {
      const patientChildren: MenuItem[] = [
        { id: 'patients', label: 'Pacientes', icon: 'personal_injury', route: '/management-pacient/patients' },
      ];
      if (this.authService.hasPermission(AppPermission.LIST_SERVICE)) {
        patientChildren.push(
          { id: 'services', label: 'Servicios', icon: 'medical_services', route: '/management-pacient/services' }
        );
      }
      if (this.authService.hasPermission(AppPermission.LIST_EPISODE)) {
        patientChildren.push(
          { id: 'episodes', label: 'Episodios Clínicos', icon: 'folder_open', route: '/management-pacient/episodes' }
        );
      }
      items.push({
        id: 'management-pacient',
        label: 'Gestión de Pacientes',
        icon: 'health_and_safety',
        route: null,
        children: patientChildren,
      });
    }

    return items;
  }

  private _activeItems: Set<string> = new Set();

  onToggle(): void {
    this.toggleSidebar.emit();
  }

  onMenuClick(item: MenuItem): void {
    if (item.children && item.children.length > 0) {
      this.toggleExpanded(item.id!);
    } else {
      this.setActiveItem(item);
    }
  }

  onChildClick(parent: MenuItem, child: MenuItem): void {
    this._activeItems.clear();
    this._activeItems.add(parent.id!);
    this._activeItems.add(child.id!);
  }

  isActive(item: MenuItem): boolean {
    return this._activeItems.has(item.id!);
  }

  toggleExpanded(itemId: string): void {
    if (this.expandedItems.has(itemId)) {
      this.expandedItems.delete(itemId);
    } else {
      this.expandedItems.add(itemId);
    }
  }

  isExpanded(itemId: string): boolean {
    return this.expandedItems.has(itemId);
  }

  setActiveItem(item: MenuItem): void {
    this._activeItems.clear();
    this._activeItems.add(item.id!);
  }

  hasChildren(item: MenuItem): boolean {
    return item.children !== undefined && item.children.length > 0;
  }

  onLogoError(): void {
    this.logoError = true;
  }
}
