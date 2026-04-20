import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { BreadcrumbsComponent } from './breadcrumbs.component';

@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    MatIconModule
  ],
  declarations: [BreadcrumbsComponent],
  exports: [BreadcrumbsComponent]
})
export class BreadcrumbsModule {
}
