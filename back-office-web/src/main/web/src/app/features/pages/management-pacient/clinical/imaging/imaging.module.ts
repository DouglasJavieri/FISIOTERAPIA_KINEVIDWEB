import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { FlexLayoutModule } from '@angular/flex-layout';

// Angular Material
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatStepperModule } from '@angular/material/stepper';
import { MatRadioModule } from '@angular/material/radio';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatDividerModule } from '@angular/material/divider';

// Shared
import { SharedModule } from '../../../../../shared/shared.module';
import { PageLayoutModule } from '../../../../../shared/components/page-layout/page-layout.module';
import { BreadcrumbsModule } from '../../../../../shared/components/breadcrumbs/breadcrumbs.module';

// Feature
import { ImagingRoutingModule } from './imaging-routing.module';
import { FootAnalysisComponent } from './foot-analysis/foot-analysis.component';
import { PhotoGalleryComponent } from './photo-gallery/photo-gallery.component';
import { PhotoCanvasComponent } from './photo-canvas/photo-canvas.component';
import { BiomechanicalFormComponent } from './biomechanical-form/biomechanical-form.component';
import { FootprintFormComponent } from './footprint-form/footprint-form.component';
import { SummaryFormComponent } from './summary-form/summary-form.component';

@NgModule({
  declarations: [
    FootAnalysisComponent,
    PhotoGalleryComponent,
    PhotoCanvasComponent,
    BiomechanicalFormComponent,
    FootprintFormComponent,
    SummaryFormComponent,
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FlexLayoutModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatChipsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatTooltipModule,
    MatStepperModule,
    MatRadioModule,
    MatProgressBarModule,
    MatDividerModule,
    SharedModule,
    PageLayoutModule,
    BreadcrumbsModule,
    ImagingRoutingModule,
  ],
})
export class ImagingModule {}
