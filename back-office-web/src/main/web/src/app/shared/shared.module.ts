import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HasRoleDirective } from './directives/has-role.directive';
import { ServerPagingTableModule } from './components/table/server-page-table/server-paging-table.module';
import { ListModule } from './components/list/list.module';
import { SelectsModule } from './components/selects/selects.module';
import { KnvCardModule } from './components/card/card.module';

/**
 * Módulo compartido de la aplicación.
 * Importarlo en cualquier módulo de feature que necesite:
 *   - La directiva *knvHasRole para control de visibilidad por rol.
 *   - <knv-server-paging-table> para tablas con paginado en servidor.
 *   - <knv-list> como contenedor de lista con filtro y encabezado.
 *   - <knv-select-multiple-search> para selects múltiples con búsqueda.
 *   - CommonModule (NgIf, NgFor, etc.) de forma centralizada.
 *
 * USO:
 *   imports: [SharedModule]
 */
@NgModule({
  declarations: [
    HasRoleDirective
  ],
  imports: [
    CommonModule,
    ServerPagingTableModule,
    ListModule,
    SelectsModule,
    KnvCardModule,
  ],
  exports: [
    CommonModule,
    HasRoleDirective,
    ServerPagingTableModule,
    ListModule,
    SelectsModule,
    KnvCardModule,
  ]
})
export class SharedModule {}

