import {NgModule} from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import {SidebarComponent} from "./components/sidebar/sidebar.component";
import {NavbarComponent} from "./components/navbar/navbar.component";
import {MainLayoutComponent} from "./main-layout/main-layout.component";
import {MatIconModule} from "@angular/material/icon";
import {MatTooltipModule} from "@angular/material/tooltip";

@NgModule({
  declarations: [
    SidebarComponent,
    NavbarComponent,
    MainLayoutComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    MatIconModule,
    MatTooltipModule,
  ],
  exports: [
    SidebarComponent,
    NavbarComponent,
    MainLayoutComponent
  ],
  providers: [],
})
export class LayoutModule {
}
