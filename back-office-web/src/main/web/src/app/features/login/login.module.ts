import {NgModule} from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';

import {LoginRoutingModule} from "./login-routing.module";
import {LoginComponent} from "./login.component";
import {MatInputModule} from "@angular/material/input";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {MatCardModule} from "@angular/material/card";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";

@NgModule({
  declarations: [LoginComponent],
  imports: [
    LoginRoutingModule,
    CommonModule,
    ReactiveFormsModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    MatCardModule,
    MatProgressSpinnerModule,
  ],
  exports: [LoginComponent],
  providers: [],
})
export class LoginModule {
}
