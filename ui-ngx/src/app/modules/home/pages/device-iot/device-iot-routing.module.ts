
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { Authority } from '@shared/models/authority.enum';
import { DeviceIotComponent } from './device-iot.component';



const routes: Routes = [
  {
    path: 'device-iot',
    component: DeviceIotComponent,
    data: {
      auth: [Authority.TENANT_ADMIN, Authority.CUSTOMER_USER],
      title: '设备数采记录',
      breadcrumb: {
        label: '设备数采记录',
        icon: 'devices_other'
      }
    },
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class DeviceIotRoutingModule { }
