import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PatientRoutingModule } from './patient-routing.module';
import { PatientListComponent } from './patient-list/patient-list.component';
import { SharedModule } from '../../../../shared/shared.module';

@NgModule({
  declarations: [
    PatientListComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    PatientRoutingModule,
  ],
})
export class PatientModule {}

