import {NgModule} from '@angular/core';

import {HomeComponent} from "./home.component";
import {MatCardModule} from "@angular/material/card";
import {CommonModule} from "@angular/common";
import {MatIconModule} from "@angular/material/icon";

@NgModule({
  declarations: [HomeComponent],
    imports: [
        CommonModule,
        MatCardModule,
        MatIconModule,
    ],
  exports: [HomeComponent],
  providers: [],
})
export class HomeModule {
}
