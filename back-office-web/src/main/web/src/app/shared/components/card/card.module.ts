import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import {
  KnvCard,
  KnvCardActions,
  KnvCardContent,
  KnvCardHeader,
  KnvCardHeaderActions,
  KnvCardHeaderSubTitle,
  KnvCardHeaderTitle
} from './card.component';

const cardComponents = [
  KnvCard,
  KnvCardHeader,
  KnvCardHeaderTitle,
  KnvCardHeaderSubTitle,
  KnvCardHeaderActions,
  KnvCardContent,
  KnvCardActions
];

@NgModule({
  imports: [
    CommonModule
  ],
  declarations: [
    ...cardComponents
  ],
  exports: [
    ...cardComponents
  ]
})
export class KnvCardModule {
}
