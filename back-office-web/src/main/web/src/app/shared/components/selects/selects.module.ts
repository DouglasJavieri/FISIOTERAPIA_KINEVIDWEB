
import {NgModule} from '@angular/core';
import {MatInputModule} from "@angular/material/input";
import {MatSelectModule} from "@angular/material/select";
import {MatIconModule} from "@angular/material/icon";
import {CommonModule} from "@angular/common";
import {SelectsComponent} from "./selects.component";

@NgModule({
  declarations: [SelectsComponent],
  imports: [
    MatInputModule,
    MatSelectModule,
    MatIconModule,
    CommonModule
  ],
  exports: [
    SelectsComponent
  ],
  providers: [],
})

export class SelectsModule {}
