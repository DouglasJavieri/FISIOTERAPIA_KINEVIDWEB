import { NgModule } from '@angular/core';
import { ManagementPacientRoutingModule } from './management-pacient-routing.module';

/**
 * Módulo raíz de la sección Gestión de Pacientes.
 * No declara ni exporta componentes: solo orquesta el routing.
 * Cada sub-sección (pacientes, servicios) tiene su propio módulo lazy.
 */
@NgModule({
  imports: [
    ManagementPacientRoutingModule,
  ],
})
export class ManagementPacientModule {}

