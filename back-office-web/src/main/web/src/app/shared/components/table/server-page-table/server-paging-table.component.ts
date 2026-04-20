import {
  AfterViewInit,
  Component,
  Input,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort, Sort } from '@angular/material/sort';
import {
  BehaviorSubject,
  interval,
  merge,
  Observable,
  ReplaySubject,
  Subject,
} from 'rxjs';
import { MatTableDataSource } from '@angular/material/table';
import { map, startWith, switchMap, takeUntil } from 'rxjs/operators';
import { buildEmptyPaginator, Paginator } from '../../../models/paginator.model';
import * as Notiflix from 'notiflix';
import {
  actionTableColumItem,
  defaultItemFormatter,
  ITableColumn,
  ITableEvents,
  ITableRowAction,
  ItemFormatterFn,
  PaginatedFn,
} from '../table.model';

@Component({
  selector: 'knv-server-paging-table',
  templateUrl: './server-paging-table.component.html',
  styleUrls: ['./server-paging-table.component.scss'],
})
export class ServerPagingTableComponent
  implements OnInit, AfterViewInit, OnDestroy
{
  @Input() dataSource!: MatTableDataSource<any>;
  @Input() columns!: ITableColumn[];
  @Input() pageSizeOptions: number[] = [5, 10, 25, 50];
  @Input() rowActions!: ITableRowAction[];
  @Input() requestPaginatedData!: PaginatedFn;
  @Input() itemFormatterFn: ItemFormatterFn = defaultItemFormatter;
  @Input() eventManager!: BehaviorSubject<ITableEvents>;
  @Input() isFilterOpened = false;
  @Input() hasFilters = false;
  @Input() actionsColumnWidth = '30px';
  @Input() actionsTriggerIcon = 'list';


  @ViewChild(MatSort, { static: true }) sort!: MatSort;
  paginator!: MatPaginator;
  @ViewChild(MatPaginator, { static: true }) set matPaginator(
    mp: MatPaginator
  ) {
    this.paginator = mp;
    if (this.paginator) {
      this.paginator._intl.itemsPerPageLabel = "Elementos por página";
      this.paginator._intl.lastPageLabel = "Última página";
      this.paginator._intl.firstPageLabel = "Primera página";
      this.paginator._intl.nextPageLabel = "Siguiente";
      this.paginator._intl.previousPageLabel = "Anterior";
      this.paginator._intl.getRangeLabel = (
        page: number,
        pageSize: number,
        length: number
      ) => {
        if (length === 0 || pageSize === 0) return `0 de ${length}`;
        length = Math.max(length, 0);

        const startIndex = page * pageSize;
        const endIndex =
          startIndex < length
            ? Math.min(startIndex + pageSize, length)
            : startIndex + pageSize;
        return `${startIndex + 1} - ${endIndex} de ${length} en total`;
      };
    }
  }
  totalElements = 0;
  filterStr = "";
  private aditionalParams: any;
  private destroyCounter$ = new Subject<void>();
  counterToReload = 9;
  get visibleColumns() {
    return this.columns
      .filter((column) => column.visible)
      .map((column) => column.property);
  }

  protected subject$: ReplaySubject<any[]> = new ReplaySubject<any[]>(1);
  protected data$: Observable<any[]> = this.subject$.asObservable();

  constructor() {
    Notiflix.Loading.init({ svgColor: '#3f51b5' });
  }

  ngOnInit() {
    if (this.eventManager) {
      this.eventManager.subscribe(this.tableActionManager);
    }
    if (this.rowActions) {
      if (!this.columns.find((item) => item.property === "actions")) {
        this.columns.unshift(actionTableColumItem);
      }
    }

    this.paginator.pageSize = this.pageSizeOptions[0];
    this.data$.pipe().subscribe((data) => {
      this.dataSource.data = data;
      if (this.eventManager) {
        this.eventManager.next({ event: "DATA_IS_LOADED" });
      }
    });
  }

  ngAfterViewInit() {
    this.sort.sortChange.subscribe(this.sortChange);
    merge(this.sort.sortChange, this.paginator.page)
      .pipe(
        startWith({}),
        switchMap(() => {
          Notiflix.Loading.standard('Cargando...');
          let queryParams = this.buildQueryParams();
          if (this.aditionalParams)
            queryParams = { ...queryParams, ...this.aditionalParams };
          return this.requestPaginatedData(queryParams);
        }),
        map((data: Paginator<any>) => {
          if (data === null)
            data = buildEmptyPaginator(
              this.paginator.pageIndex,
              this.paginator.pageSize
            );
          this.totalElements = data.totalElements;
          Notiflix.Loading.remove();
          return data;
        })
      )
      .subscribe((data: Paginator<any>) =>
        this.subject$.next(this.itemFormatterFn(data.content))
      );
  }

  ngOnDestroy() {
    this.subject$.unsubscribe();
    this.destroyCounter$.next();
    this.destroyCounter$.unsubscribe();
  }

  rowOptionClick = (item: any, actionCode: any) => {
    if (this.eventManager) {
      this.eventManager.next({
        event: "ROW_CLICK",
        data: { item, actionCode },
      });
    }
  };

  protected buildQueryParams() {
    let sortBy = "",
      sortDir = "",
      filter = "";
    if (this.sort.active && this.sort.direction.length > 0) {
      sortBy = this.sort.active;
      sortDir = this.sort.direction.toUpperCase();
    }
    return {
      page: this.paginator.pageIndex,
      size: this.paginator.pageSize,
      sortBy,
      sortDir,
      filter,
    };
  }

  protected sortChange = (_sort: Sort) => {
    this.paginator.pageIndex = 0;
  };

  protected tableActionManager = (event: ITableEvents) => {
    if (event.event === "RELOAD_ACTIONS") {
      if (this.rowActions.length === 0) {
        this.columns = this.columns.filter(
          (item) => item.property !== "actions"
        );
      }
      return;
    }
    if (event.data && event.data.filterStr) {
      this.filterStr = event.data.filterStr;
    } else {
      this.filterStr = "";
    }
    if (event.data && event.data.aditionalParams) {
      this.aditionalParams = event.data.aditionalParams;
    }
    if (event.event === "RELOAD_PAGE") this.realoadPageAction();
    if (event.event === "RESET") {
      this.paginator.firstPage();
      if (this.paginator.pageIndex === 0) this.realoadPageAction();
    }
    if (event.event === "START_COUNTER_TO_RELOAD") {
      this.startCounter(9);
    }
    if (event.event === "STOP_COUNTER_TO_RELOAD") {
      this.counterToReload = 9;
      this.destroyCounter$.next();
    }
  };

  protected realoadPageAction = () =>
    this.paginator.page.emit({
      length: this.paginator.length,
      pageIndex: this.paginator.pageIndex,
      pageSize: this.paginator.pageSize,
    });

  protected startCounter(from: number) {
    interval(1000)
      .pipe(
        map((it) => from - it),
        takeUntil(this.destroyCounter$)
      )
      .subscribe((res) => {
        this.counterToReload = res;
        if (res === 0) {
          this.destroyCounter$.next();
          this.realoadPageAction();
        }
      });
  }
}
