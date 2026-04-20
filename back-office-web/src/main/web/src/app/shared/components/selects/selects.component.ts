import {
  EventEmitter,
  Component,
  forwardRef,
  Input,
  Output,
  ChangeDetectorRef,
  OnChanges,
  OnInit,
  SimpleChanges,
  ViewChild
} from '@angular/core';
import {NG_VALUE_ACCESSOR, ControlValueAccessor} from "@angular/forms";
import {MatSelect} from "@angular/material/select";

@Component({
  selector: 'knv-selects',
  templateUrl: './selects.component.html',
  styleUrls: ['./selects.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => SelectsComponent),
      multi: true,
    },
  ],
})
export class SelectsComponent implements OnInit, OnChanges, ControlValueAccessor {

  optionSelected: any = null;
  private onChangeCb?: (obj: any) => void;
  onTouchedCb?: () => void;
  isDisabled: boolean = false;

  isInvalid: boolean = false;
  hasBeenTouched: boolean = false;

  @Input() displayAttr: string = 'name';
  @Input() keyAttr: string = 'id';
  @Input() data: any[] = [];
  @Input() name: string = 'Input';
  @Input() placeholder: string = '';
  @Input() multiple: boolean = false;
  @Input() readonly: boolean = false;
  @Input() displayMoreInfo: boolean = false;
  @Input() currentKeyCloacksIds: any[] = [];
  @Input() isRequired: boolean = false;
  @Input() showAllOption: boolean = false;
  @Input() allOptionText: string = 'Todos';
  @Output() valueChange = new EventEmitter<any>();
  @Output() selectionChange = new EventEmitter<any>();

  filteredList: any[] = [];
  userList: any = [];
  userListFiltered: any[] = [];
  allOption: any = {};
  currentSearch: string = '';
  @ViewChild(MatSelect) matSelect!: MatSelect;

  constructor(private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.initializeAllOption();
    this.initializeLists();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['data'] && changes['data'].currentValue) {
      this.initializeAllOption();
      this.initializeLists();
      if (this.optionSelected !== null && this.optionSelected !== undefined) {
        this.validateSelectedValue();
      }
      this.cdr.detectChanges();
    }

    if (changes['showAllOption'] || changes['allOptionText']) {
      this.initializeAllOption();
      this.initializeLists();
    }
  }

  private initializeAllOption(): void {
    this.allOption = {
      [this.keyAttr]: 'all',
      [this.displayAttr]: this.allOptionText,
      isAllOption: true
    };
  }

  private initializeLists(): void {
    const baseData = this.showAllOption ? [this.allOption, ...this.data] : this.data;

    this.filteredList = baseData.map((item: any) => ({ ...item, show: true }));
    this.userList = baseData.map((item: any) => ({ ...item, show: true }));
    this.userListFiltered = this.userList.filter((item: any) =>
      this.currentKeyCloacksIds.includes(item.id)
    );
  }

  private validateSelectedValue(): void {
    if (this.multiple && Array.isArray(this.optionSelected)) {
      const validValues = this.optionSelected.filter(value =>
        this.data.some(item =>
          this.keyAttr === 'obj' ? item === value : item[this.keyAttr] === value
        )
      );

      if (validValues.length !== this.optionSelected.length) {
        this.optionSelected = validValues;
        this.onChangeCb?.(this.optionSelected);
        this.valueChange.emit(this.optionSelected);
      }
    } else {
      const valueExists = this.data.some(item =>
        this.keyAttr === 'obj' ? item === this.optionSelected : item[this.keyAttr] === this.optionSelected
      );

      if (!valueExists && this.optionSelected !== 'all') {
        this.optionSelected = null;
        this.onChangeCb?.(this.optionSelected);
        this.valueChange.emit(this.optionSelected);
      }
    }

    this.updateUserListFiltered();
  }

  filterListData(search: string): void {
    this.currentSearch = search.trim();
    const valueLower = this.currentSearch.toLowerCase();
    this.filteredList = (this.showAllOption ? [this.allOption, ...this.data] : this.data).map((item) => {
      const itemLower = String(item[this.displayAttr] ?? '').toLowerCase();
      return { ...item, show: itemLower.includes(valueLower) };
    });
    this.cdr.detectChanges();
  }
  hasNoResults(): boolean {
    return this.currentSearch !== '' &&
      this.filteredList.filter(item => item.show).length === 0 &&
      !this.isInvalid;
  }

  resetSearch(): void {
    this.currentSearch = '';
    this.filterListData('');
  }

  onTouch() {
    if (!this.hasBeenTouched) {
      this.hasBeenTouched = true;
      this.onTouchedCb?.();
      if (this.isRequired) {
        const isEmpty = this.multiple
          ? !this.optionSelected || this.optionSelected.length === 0
          : !this.optionSelected;
        this.isInvalid = isEmpty;
      }
    }
  };

  markAsTouched() {
    if (!this.hasBeenTouched) {
      this.hasBeenTouched = true;
      if (this.isRequired) {
        const isEmpty = this.multiple
          ? !this.optionSelected || this.optionSelected.length === 0
          : !this.optionSelected;
        this.isInvalid = isEmpty;
      }
    }
  };

  onValueChange(event: any): void {
    const newValue = event.value;

    if (newValue && !(Array.isArray(newValue) && newValue.length === 0)) {
      this.isInvalid = false;
    }

    if (this.showAllOption && this.multiple) {

      const allValues = this.data.map(item =>
        this.keyAttr === 'obj' ? item : item[this.keyAttr]
      );

      if (newValue.includes('all') && !this.optionSelected?.includes('all')) {
        this.optionSelected = [...allValues];
      }

      else if (!newValue.includes('all') && this.optionSelected?.length === this.data.length) {
        this.optionSelected = newValue;
      }

      else {
        this.optionSelected = newValue.filter((v: any) => v !== 'all');
      }

    } else {
      this.optionSelected = newValue;
    }

    this.updateUserListFiltered();

    let valueToEmit = this.optionSelected;
    if (Array.isArray(valueToEmit)) {
      valueToEmit = valueToEmit.filter(item => item !== 'all');
    }

    this.onChangeCb?.(valueToEmit);

    if (this.valueChange) {
      this.valueChange.emit(valueToEmit);
    }

    if (this.selectionChange) {
      this.selectionChange.emit(valueToEmit);
    }
  };

  private updateUserListFiltered(): void {
    if (this.optionSelected === 'all') {
      this.userListFiltered = this.userList.filter((item: any) =>
        !item.isAllOption && this.currentKeyCloacksIds.includes(item.id)
      );
    } else if (Array.isArray(this.optionSelected)) {
      this.userListFiltered = this.userList.filter((element: any) =>
        this.optionSelected.includes(element.id)
      );
    } else if (this.optionSelected !== null && this.optionSelected !== undefined) {
      this.userListFiltered = this.userList.filter((element: any) =>
        element.id === this.optionSelected
      );
    } else {
      this.userListFiltered = [];
    }
  }

  writeValue(obj: any): void {
    this.optionSelected = obj;
    if (obj) {
      this.isInvalid = false;
    }

    if (this.multiple && this.showAllOption && obj && Array.isArray(obj)) {
      const allSelected = this.areAllOptionsSelected();
      if (allSelected && this.data.length > 0) {
        this.optionSelected = this.showAllOption ? ['all', ...obj] : obj;
      }
    }
    this.updateUserListFiltered();
    this.cdr.detectChanges();
  };

  registerOnChange(fn: any): void {
    this.onChangeCb = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouchedCb = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.isDisabled = isDisabled;
  }

  deselectAll(): void {
    this.optionSelected = [];
    this.updateUserListFiltered();

    const valueToEmit = this.optionSelected;
    this.onChangeCb?.(valueToEmit);
    this.valueChange.emit(valueToEmit);
  }


  private areAllOptionsSelected(): boolean {
    if (!Array.isArray(this.optionSelected) || this.data.length === 0) {
      return false;
    }
    const selectedValues = new Set(this.optionSelected);
    const allValues = this.data.map(item =>
      this.keyAttr === 'obj' ? item : item[this.keyAttr]
    );
    return allValues.every(value => selectedValues.has(value));
  };

  handleKeyDown(event: KeyboardEvent): void {
    if (event.key === 'Enter') {
      event.preventDefault();
      event.stopPropagation();
      this.selectSingleMatch();
    } else {
      event.stopPropagation();
    }
  }

  selectSingleMatch(): void {
    const visibleOptions = this.filteredList.filter(item =>
      item.show && !item.isAllOption
    );

    if (visibleOptions.length === 1) {
      const selectedOption = visibleOptions[0];
      const valueToSelect = this.keyAttr === 'obj'
        ? selectedOption
        : selectedOption[this.keyAttr];

      if (this.multiple) {

        if (!this.optionSelected) {
          this.optionSelected = [];
        }

        const alreadySelected = Array.isArray(this.optionSelected)
          ? this.optionSelected.includes(valueToSelect)
          : false;

        if (!alreadySelected) {
          this.optionSelected = [...this.optionSelected, valueToSelect];

          if (this.showAllOption && this.areAllOptionsSelected()) {
            this.optionSelected = ['all', ...this.optionSelected];
          }
        }
      } else {
        this.optionSelected = valueToSelect;
      }

      this.updateUserListFiltered();

      let valueToEmit = this.optionSelected;
      if (Array.isArray(valueToEmit)) {
        valueToEmit = valueToEmit.filter(item => item !== 'all');
      }

      this.onChangeCb?.(valueToEmit);
      this.valueChange.emit(valueToEmit);
      this.selectionChange.emit(valueToEmit);

      if (this.isRequired) {
        this.isInvalid = false;
      }

      this.resetSearch();

      if (this.matSelect) {
        this.matSelect.close();
      }
      this.cdr.detectChanges();
    }
  }
}
