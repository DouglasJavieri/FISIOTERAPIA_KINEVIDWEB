import { Component, Input } from '@angular/core';

@Component({
  selector: 'knv-page-layout',
  template: `
    <div knvPageLayout [mode]="mode" [class]="customClass">
      <ng-content></ng-content>
    </div>
  `,
  styleUrls: ['./page-layout.directive.scss', './page-layout.directive.theme.scss']
})
export class PageLayoutComponent {

  @Input() mode: 'card' | 'simple' = 'simple';
  @Input() customClass: string = '';

  constructor() { }

}
