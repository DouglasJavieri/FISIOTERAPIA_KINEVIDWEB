import { NgModule } from '@angular/core';

import { PagesRoutingModule } from './pages-routing.module';
import { HomeModule }         from './home/home.module';
import { SharedModule }       from '../../shared/shared.module';
import { LayoutModule }       from '../../layout/layout.module';

@NgModule({
  declarations: [],
  imports: [
    SharedModule,
    LayoutModule,
    PagesRoutingModule,
    HomeModule,
  ],
  exports: [],
  providers: [],
})
export class PagesModule {}
