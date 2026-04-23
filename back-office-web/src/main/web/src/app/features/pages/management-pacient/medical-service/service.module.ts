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
import { MatTooltipModule } from '@angular/material/tooltip';

// Shared
import { SharedModule } from '../../../../shared/shared.module';
import { PageLayoutModule } from '../../../../shared/components/page-layout/page-layout.module';
import { BreadcrumbsModule } from '../../../../shared/components/breadcrumbs/breadcrumbs.module';

// Feature
import { ServiceComponent } from './service.component';
import { AddServiceComponent } from './add-service/add-service.component';
import { UpdateServiceComponent } from './update-service/update-service.component';
import { ServiceRoutingModule } from './service-routing.module';

@NgModule({
  declarations: [
    ServiceComponent,
    AddServiceComponent,
    UpdateServiceComponent,
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
    SharedModule,
    PageLayoutModule,
    BreadcrumbsModule,
    ServiceRoutingModule,
  ],
})
export class ServiceModule {}
