import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { FlexLayoutModule } from '@angular/flex-layout';

// Angular Material
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatTooltipModule }      from '@angular/material/tooltip';
import { MatDatepickerModule }  from '@angular/material/datepicker';
import { MatNativeDateModule }  from '@angular/material/core';

// Shared & Layout
import { SharedModule } from '../../../../shared/shared.module';
import { PageLayoutModule } from '../../../../shared/components/page-layout/page-layout.module';
import { BreadcrumbsModule } from '../../../../shared/components/breadcrumbs/breadcrumbs.module';

// Feature
import { PatientComponent } from './patient.component';
import { AddPatientComponent } from './add-patient/add-patient.component';
import { UpdatePatientComponent } from './update-patient/update-patient.component';
import { PatientRoutingModule } from './patient-routing.module';

@NgModule({
  declarations: [
    PatientComponent,
    AddPatientComponent,
    UpdatePatientComponent,
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FlexLayoutModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    MatTooltipModule,
    MatDatepickerModule,
    MatNativeDateModule,
    SharedModule,
    PageLayoutModule,
    BreadcrumbsModule,
    PatientRoutingModule,
  ],
})
export class PatientModule {}
