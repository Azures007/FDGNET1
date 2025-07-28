import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Authority } from '@shared/models/authority.enum';
import { InventoryMgtComponent } from './inventory-mgt.component';

const routes: Routes = [
  {
    path: 'invetory-mgt',
    component: InventoryMgtComponent,
    data: {
      auth: [Authority.TENANT_ADMIN],
      title: '线边仓库管理',
      breadcrumb: {
        label: '线边仓库管理',
        icon: 'devices_other'
      }
    },
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class InventoryMgtRoutingModule { }
