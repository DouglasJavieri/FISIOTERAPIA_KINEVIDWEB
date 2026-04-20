import { Observable } from "rxjs";
import { ThemePalette } from "@angular/material/core";
import { Paginator } from "../../models/paginator.model";

export type TableEventType =
  | "ROW_CLICK"
  | "RELOAD_PAGE"
  | "RESET"
  | "NOOP"
  | "RELOAD_ACTIONS"
  | "DATA_IS_LOADED"
  | "START_COUNTER_TO_RELOAD"
  | "STOP_COUNTER_TO_RELOAD";

export type RequestGroupedDataFn = <T>() => Observable<T[]>;

export type IsGroupItemVerifyFn = <T>(index: number, item: T) => boolean;

export type RowCheckVerifyFn = (row: any) => boolean;

export type RowCheckDisableVerifyFn = (row: any) => boolean;

export type CellCheckLoadingCellFn = (row: any) => boolean;

export type TextContainerCellStyle = (row: any) => { [key: string]: string };

export type ItemFormatterFn = (content: any[]) => any[];

export type RowDisabledFn = (row: any) => boolean;

export type PaginatedFn = (queryParams: {
  [param: string]:
    | string
    | number
    | boolean
    | ReadonlyArray<string | number | boolean>;
}) => Observable<Paginator<any>>;

export const defaultItemFormatter: ItemFormatterFn = (content: any[]) =>
  content;

export const actionTableColumItem: ITableColumn = {
  name: "Actions",
  property: "actions",
  visible: true,
  isModelProperty: false,
};

export const checkRowTableColumItem: ITableColumn = {
  name: "Check",
  property: "checkbox",
  visible: true,
  isModelProperty: false,
};

export const noopTableEvent = (): ITableEvents => ({ event: "NOOP" });

export type CellTypeFn = "IMAGE" | "TEXT" | "LARGE_TEXT" | "ICON";

export interface ITableEvents {
  event: TableEventType;
  data?: any;
}

export interface ITableColumn {
  name: string;
  property: string;
  isSort?: boolean;
  sortProperty?: string;
  visible: boolean;
  isModelProperty: boolean;
  width?: string; // util para tablas sobre Dialog o BottomSheet
  largeText?: ILargeTextCell;
  showLoadinCell?: CellCheckLoadingCellFn;
  textContainerCellStyle?: TextContainerCellStyle;
  cellType?: CellTypeFn;
  imageConfig?: ITableImageConfigCell;
}

export interface ILargeTextCell {
  text: string;
  actionCode: string;
  icon?: string;
}

export interface ITableRowAction {
  action: string;
  icon: string;
  actionCode: string;
  tooltip?: string;
  isDisabledFn?: RowDisabledFn;
}

export interface ITableRowCheckboxable {
  rowCheckedVerifyFn: RowCheckVerifyFn;
  rowCheckDisableVerifyFn: RowCheckDisableVerifyFn;
  checkActionCode: any;
  color: ThemePalette;
}

export interface ITableImageConfigCell {
  altMessage: string;
  width: string;
  height: string;
}

export class TableRequiredPropertyException extends DOMException {
  constructor(component: string, property: string, type: string) {
    super(`${component} -> ${property}: ${type} is required`);
  }
}
