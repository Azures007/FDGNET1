import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Authority } from '@shared/models/authority.enum';
import { MenuComponent } from './menu.component';

const routes: Routes = [
  {
    path: 'menuAdmin',
    component: MenuComponent,
    data: {
      auth: [Authority.TENANT_ADMIN, Authority.CUSTOMER_USER],
      title: 'my-menu.my-menu',
      devicesType: 'tenant',
      breadcrumb: {
        label: 'my-menu.my-menu',
        icon: 'devices_other'
      }
    },

  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MenuRoutingModule { }
