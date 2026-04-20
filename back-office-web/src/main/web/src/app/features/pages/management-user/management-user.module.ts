import { NgModule } from '@angular/core';
import { ManagementUserRoutingModule } from './management-user-routing.module';

/**
 * Módulo raíz de la sección Gestión de Usuarios.
 * No declara ni exporta componentes: solo orquesta el routing.
 * Cada sub-sección (permisos, roles, usuarios) tiene su propio módulo lazy.
 */
@NgModule({
  imports: [
    ManagementUserRoutingModule,
  ],
})
export class ManagementUserModule {}

