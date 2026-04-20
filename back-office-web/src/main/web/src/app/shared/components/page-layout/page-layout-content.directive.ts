import { Directive } from '@angular/core';

@Directive({
  selector: '[knvPageLayoutContent],knv-page-layout-content',
  host: {
    class: 'knv-page-layout-content'
  }
})
export class PageLayoutContentDirective {

  constructor() { }

}
