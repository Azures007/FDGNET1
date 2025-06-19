import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Authority } from '@shared/models/authority.enum';
import { StaffComponent } from './staff.component';

const routes: Routes = [
  {
    path: 'staff',
    component: StaffComponent,
    data: {
      auth: [Authority.TENANT_ADMIN, Authority.CUSTOMER_USER],
      title: 'staff.staff-admin',
      devicesType: 'tenant',
      breadcrumb: {
        label: 'staff.staff-admin',
        icon: 'devices_other'
      }
    },
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class StaffRoutingModule { }
