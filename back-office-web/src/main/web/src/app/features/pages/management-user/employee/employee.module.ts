import { NgModule }            from '@angular/core';
import { CommonModule }        from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { FlexLayoutModule }    from '@angular/flex-layout';

// Angular Material
import { MatButtonModule }       from '@angular/material/button';
import { MatIconModule }         from '@angular/material/icon';
import { MatMenuModule }         from '@angular/material/menu';
import { MatFormFieldModule }    from '@angular/material/form-field';
import { MatSelectModule }       from '@angular/material/select';
import { MatDialogModule }       from '@angular/material/dialog';
import { MatInputModule }        from '@angular/material/input';
import { MatDividerModule }      from '@angular/material/divider';
import { MatTooltipModule }      from '@angular/material/tooltip';
import { MatDatepickerModule }      from '@angular/material/datepicker';
import { MatNativeDateModule }      from '@angular/material/core';

// Shared & Layout
import { SharedModule }      from '../../../../shared/shared.module';
import { PageLayoutModule }  from '../../../../shared/components/page-layout/page-layout.module';
import { BreadcrumbsModule } from '../../../../shared/components/breadcrumbs/breadcrumbs.module';

// Feature
import { EmployeeComponent }            from './employee.component';
import { AddEmployeeComponent }         from './add-employee/add-employee.component';
import { UpdateEmployeeComponent }      from './update-employee/update-employee.component';
import { AssignUserEmployeeComponent }  from './assign-user-employee/assign-user-employee.component';
import { EmployeeRoutingModule }        from './employee-routing.module';

@NgModule({
  declarations: [
    EmployeeComponent,
    AddEmployeeComponent,
    UpdateEmployeeComponent,
    AssignUserEmployeeComponent,
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
    MatDialogModule,
    MatInputModule,
    MatDividerModule,
    MatTooltipModule,
    MatDatepickerModule,
    MatNativeDateModule,
    SharedModule,
    PageLayoutModule,
    BreadcrumbsModule,
    EmployeeRoutingModule,
  ],
})
export class EmployeeModule {}

