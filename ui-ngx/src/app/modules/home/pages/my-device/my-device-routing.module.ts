import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Authority } from '@shared/models/authority.enum';
import { MyDeviceComponent } from './my-device.component';

const routes: Routes = [
  {
    path: 'mydevice',
    component: MyDeviceComponent,
    data: {
      auth: [Authority.TENANT_ADMIN, Authority.CUSTOMER_USER],
      title: 'my-device.my-device-admin',
      devicesType: 'tenant',
      breadcrumb: {
        label: 'my-device.my-device',
        icon: 'devices_other'
      }
    },
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MyDeviceRoutingModule { }
