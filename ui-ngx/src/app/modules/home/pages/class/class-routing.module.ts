import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Authority } from '@shared/models/authority.enum';
import { ClassComponent } from './class.component';
import { TeamAdminComponent } from './team-admin.component';


const routes: Routes = [
  {
    path: 'class',
    component: ClassComponent,
    data: {
      auth: [Authority.TENANT_ADMIN, Authority.CUSTOMER_USER],
      title: '班别管理',
      devicesType: 'tenant',
      breadcrumb: {
        label: '班别',
        icon: 'devices_other'
      }
    },
  },
  {
    path: 'team-admin',
    component: TeamAdminComponent,
    data: {
      auth: [Authority.TENANT_ADMIN, Authority.CUSTOMER_USER],
      keep:true,
      reuse: false,
      title: '组员管理',
      devicesType: 'tenant',
      breadcrumb: {
        label: '班别',
        icon: 'devices_other'
      }
    },
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ClassRoutingModule { }
