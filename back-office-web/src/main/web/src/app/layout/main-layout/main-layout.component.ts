import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';
import { UserAuthInfo } from '../../core/models/auth.model';

@Component({
  selector: 'knv-main-layout',
  templateUrl: './main-layout.component.html',
  styleUrls: ['./main-layout.component.scss']
})
export class MainLayoutComponent implements OnInit {

  sidebarCollapsed = false;
  currentUser$!: Observable<UserAuthInfo | null>;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.currentUser$ = this.authService.currentUser$;
  }

  onToggleSidebar(): void {
    this.sidebarCollapsed = !this.sidebarCollapsed;
  }

  onLogout(): void {
    this.authService.logout();
  }

}
