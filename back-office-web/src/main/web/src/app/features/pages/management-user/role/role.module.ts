import { NgModule }            from '@angular/core';
import { CommonModule }        from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { FlexLayoutModule }    from '@angular/flex-layout';

// Angular Material
import { MatButtonModule }    from '@angular/material/button';
import { MatIconModule }      from '@angular/material/icon';
import { MatMenuModule }      from '@angular/material/menu';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule }    from '@angular/material/select';
import { MatDialogModule }    from '@angular/material/dialog';
import { MatInputModule }     from '@angular/material/input';
import { MatDividerModule }   from '@angular/material/divider';
import { MatTooltipModule }   from '@angular/material/tooltip';

// Shared & Layout
import { SharedModule }      from '../../../../shared/shared.module';
import { PageLayoutModule }  from '../../../../shared/components/page-layout/page-layout.module';
import { BreadcrumbsModule } from '../../../../shared/components/breadcrumbs/breadcrumbs.module';

// Feature
import { RoleComponent }       from './role.component';
import { RoleRoutingModule }   from './role-routing.module';
import { AddRoleComponent }    from './add-role/add-role.component';
import { UpdateRoleComponent } from './update-role/update-role.component';

@NgModule({
  declarations: [
    RoleComponent,
    AddRoleComponent,
    UpdateRoleComponent,
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
    SharedModule,
    PageLayoutModule,
    BreadcrumbsModule,
    RoleRoutingModule,
  ],
})
export class RoleModule {}

