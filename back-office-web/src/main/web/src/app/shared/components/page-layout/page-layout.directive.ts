import { Directive, HostBinding, Input } from '@angular/core';

@Directive({
  selector: '[knvPageLayout],knv-page-layout',
  host: {
    class: 'knv-page-layout'
  }
})
export class PageLayoutDirective {

  @Input() mode: 'card' | 'simple' = 'simple';

  constructor() { }

  @HostBinding('class.knv-page-layout-card')
  get isCard() {
    return this.mode === 'card';
  }

  @HostBinding('class.knv-page-layout-simple')
  get isSimple() {
    return this.mode === 'simple';
  }

}
