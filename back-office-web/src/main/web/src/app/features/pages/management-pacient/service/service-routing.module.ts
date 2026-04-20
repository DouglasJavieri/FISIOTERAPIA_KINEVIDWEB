import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ServiceComponent } from './service.component';
import { AddServiceComponent } from './add-service/add-service.component';
import { UpdateServiceComponent } from './update-service/update-service.component';

const routes: Routes = [
  { path: '', component: ServiceComponent },
  { path: 'add', component: AddServiceComponent },
  { path: 'update/:id', component: UpdateServiceComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ServiceRoutingModule {}
