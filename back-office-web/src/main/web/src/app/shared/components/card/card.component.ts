import { ChangeDetectionStrategy, Component, Directive, Input, ViewEncapsulation } from '@angular/core';

// noinspection TsLint
@Component({
  selector: 'knv-card',
  templateUrl: './card.component.html',
  styleUrls: ['./card.component.scss'],
  host: { 'class': 'knv-card' },
  encapsulation: ViewEncapsulation.None,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class KnvCard {
}

// noinspection TsLint
@Component({
  selector: 'knv-card-header',
  encapsulation: ViewEncapsulation.None,
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { 'class': 'knv-card-header' },
  template: `
    <div class="knv-card-header-heading-group">
      <ng-content select="knv-card-header-heading"></ng-content>
      <ng-content select="knv-card-header-subheading"></ng-content>
    </div>
    <ng-content></ng-content>
    <ng-content select="knv-card-header-actions"></ng-content>
  `
})
export class KnvCardHeader {
}

// noinspection TsLint
@Component({
  selector: 'knv-card-content',
  encapsulation: ViewEncapsulation.None,
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { 'class': 'knv-card-content' },
  template: `
    <ng-content></ng-content>`
})
export class KnvCardContent {
}

// noinspection TsLint
@Directive({
  selector: 'knv-card-header-heading',
  host: { 'class': 'knv-card-header-heading' }
})
export class KnvCardHeaderTitle {
}

// noinspection TsLint
@Directive({
  selector: 'knv-card-header-subheading',
  host: { 'class': 'knv-card-header-subheading' }
})
export class KnvCardHeaderSubTitle {
}

// noinspection TsLint
@Directive({
  selector: 'knv-card-header-actions',
  host: { 'class': 'knv-card-header-actions' }
})
export class KnvCardHeaderActions {
}

// noinspection TsLint
@Directive({
  selector: 'knv-card-actions',
  host: {
    'class': 'knv-card-actions',
    '[class.knv-card-actions-align-end]': 'align === "end"',
  }
})
export class KnvCardActions {
  /** Posición de las acciones dentro del card. 'start' | 'end' */
  @Input() align: 'start' | 'end' = 'start';
}
