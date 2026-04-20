import { Component, Input, OnInit } from '@angular/core';
import { IBreadcrumb } from './breadcrumbs.model';

@Component({
  selector: 'knv-breadcrumbs',
  templateUrl: './breadcrumbs.component.html',
  styleUrls: ['./breadcrumbs.component.scss']
})
export class BreadcrumbsComponent implements OnInit {

  @Input() current: string = '';
  @Input() img: string = '';
  @Input() crumbs: IBreadcrumb[] = [];

  constructor() { }

  ngOnInit(): void { }

}
