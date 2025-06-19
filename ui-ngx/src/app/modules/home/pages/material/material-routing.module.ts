import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Authority } from '@shared/models/authority.enum';
import { MaterialComponent } from './material.component';

const routes: Routes = [
  {
    path: 'material',
    component: MaterialComponent,
    data: {
      auth: [Authority.TENANT_ADMIN, Authority.CUSTOMER_USER],
      title: 'material.material-admin',
      devicesType: 'tenant',
      breadcrumb: {
        label: 'material.material-admin',
        icon: 'devices_other'
      }
    },
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MaterialRoutingModule { }
