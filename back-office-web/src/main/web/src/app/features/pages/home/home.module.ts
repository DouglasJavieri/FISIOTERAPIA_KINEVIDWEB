import {NgModule} from '@angular/core';

import {HomeComponent} from "./home.component";
import {MatCardModule} from "@angular/material/card";
import {CommonModule} from "@angular/common";

@NgModule({
  declarations: [HomeComponent],
  imports: [
    CommonModule,
    MatCardModule,
  ],
  exports: [HomeComponent],
  providers: [],
})
export class HomeModule {
}
