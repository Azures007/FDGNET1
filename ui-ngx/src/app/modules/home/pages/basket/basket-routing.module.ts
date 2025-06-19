import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Authority } from '@shared/models/authority.enum';
import { BasketManageComponent } from './basket-manage.component';

const routes: Routes = [
  {
    path: 'basketManage',
    component: BasketManageComponent,
    data: {
      auth: [Authority.TENANT_ADMIN, Authority.CUSTOMER_USER],
      title: 'basket.basket-admin',
      devicesType: 'tenant',
      breadcrumb: {
        label: 'basket.basket',
        icon: 'devices_other'
      }
    },
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class BasketRoutingModule { }
