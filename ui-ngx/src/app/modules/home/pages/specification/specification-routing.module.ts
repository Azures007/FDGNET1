import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Authority } from '@shared/models/authority.enum';
import { SpecificationComponent } from './specification.component';

const routes: Routes = [
  {
    path: 'mySpec',
    component: SpecificationComponent,
    data: {
      auth: [Authority.TENANT_ADMIN, Authority.CUSTOMER_USER],
      title: 'my-spec.my-spec',
      devicesType: 'tenant',
      breadcrumb: {
        label: 'my-spec.my-spec',
        icon: 'devices_other'
      }
    },

  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SpecificationRoutingModule { }
