import { MatDialogConfig } from '@angular/material/dialog';
import { MatBottomSheetConfig } from '@angular/material/bottom-sheet';

export const NAVBAR_HEIGHT = '60px';

export const leftDialogWidth = '400px';

/**
 * Configuración estándar para diálogos en panel lateral derecho.
 * Usar para formularios de creación y edición.
 * El panel comienza justo debajo del navbar fijo (60px).
 *
 * this.dialog.open(MyFormComponent, buildRightDialogConfig({ id: 1 }));
 */
export const buildRightDialogConfig = <T>(data: T): MatDialogConfig<T> => ({
  width: leftDialogWidth,
  height: `calc(100vh - ${NAVBAR_HEIGHT})`,
  autoFocus: false,
  disableClose: true,
  position: { right: '0', top: NAVBAR_HEIGHT },
  data,
});

/**
 * Configuración estándar para BottomSheet (80% altura).
 * Usar para selecciones o acciones secundarias en mobile/tablet.
 */
export const buildBottomSheetConfig = <T>(data: T): MatBottomSheetConfig<T> => ({
  autoFocus: 'dialog',
  disableClose: true,
  closeOnNavigation: true,
  panelClass: 'bottom-sheet-container-80',
  data,
});

/**
 * Configuración personalizada para BottomSheet.
 * Permite sobreescribir cualquier propiedad del config estándar.
 */
export const buildBottomSheetConfigCustom = <T>(data: T, config: Partial<MatBottomSheetConfig<T>>): MatBottomSheetConfig<T> => ({
  ...config,
  data,
});
