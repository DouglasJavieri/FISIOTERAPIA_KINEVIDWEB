import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PageLayoutDirective } from './page-layout.directive';
import { PageLayoutHeaderDirective } from './page-layout-header.directive';
import { PageLayoutContentDirective } from './page-layout-content.directive';
import { PageLayoutComponent } from './page-layout.component';

@NgModule({
  imports: [
    CommonModule
  ],
  declarations: [
    PageLayoutDirective,
    PageLayoutHeaderDirective,
    PageLayoutContentDirective,
    PageLayoutComponent
  ],
  exports: [
    PageLayoutDirective,
    PageLayoutHeaderDirective,
    PageLayoutContentDirective,
    PageLayoutComponent
  ]
})
export class PageLayoutModule {
}
