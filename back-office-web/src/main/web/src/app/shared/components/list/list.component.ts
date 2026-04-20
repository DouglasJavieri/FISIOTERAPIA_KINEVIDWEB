import { AfterViewInit, Component, ElementRef, EventEmitter, Input, Output, ViewChild, ViewEncapsulation } from '@angular/core';
import { fromEvent } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { ListColumn } from './list-column.model';

@Component({
  selector: 'knv-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class ListComponent implements AfterViewInit {

  @Input() searchPlaceholder = 'Buscar...';
  filterValue = '';
  lastFilterValue = '';
  @Input() name!: string;
  @Input() columns!: ListColumn[];

  @ViewChild('filter') filter!: ElementRef;
  @Output() filterChange = new EventEmitter<string>();

  @Input() hideHeader!: boolean;
  @Input() withFilter = true;

  constructor() {
  }

  ngAfterViewInit() {
    if (this.withFilter) {
      fromEvent(this.filter.nativeElement, 'keyup').pipe(
        distinctUntilChanged(),
        debounceTime(500)
      ).subscribe(() => {
        if (this.lastFilterValue.trim().toLowerCase() !== this.filterValue.trim().toLowerCase()) {
          this.lastFilterValue = this.filterValue;
          this.filterChange.emit(this.filterValue);
        }
      });
    }
  }

  toggleColumnVisibility(column: ListColumn, event: MouseEvent) {
    event.stopPropagation();
    event.stopImmediatePropagation();
    column.visible = !column.visible;
  }
}
