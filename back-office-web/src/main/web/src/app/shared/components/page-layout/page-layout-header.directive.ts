import { Directive } from '@angular/core';

@Directive({
  selector: '[knvPageLayoutHeader],knv-page-layout-header',
  host: {
    class: 'knv-page-layout-header'
  }
})
export class PageLayoutHeaderDirective {

  constructor() { }

}
