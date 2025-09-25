
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { Authority } from '@shared/models/authority.enum';
import { ProcessManagementComponent } from './process-management.component';
import { ProcessRouteComponent } from './process-route.component';
import { ProcessBindMaterComponent } from './process-bind-mater.component';

const routes: Routes = [
  {
    path: 'technological',
    data: {
      breadcrumb: {
        label: 'technological.technological',
        icon: 'bookmarks'
      }
    },
    children: [
      {
        path: '',
        data: {
          auth: [Authority.TENANT_ADMIN],
          redirectTo: '/technological/processRoute'
        }
      },

      {
        path: 'processRoute',
        component: ProcessRouteComponent,
        data: {
          title: 'technological.processRoute',
          breadcrumb: {
            label: 'technological.processRoute',
            icon: 'bookmarks'
          }
        }
      },
      {
        path: 'processManage',
        component: ProcessManagementComponent,
        data: {
          title: 'technological.processManage',
          breadcrumb: {
            label: 'technological.processManage',
            icon: 'bookmarks'
          }
        }
      },
    ]
  },
  {
    path: 'material-bind',
    component: ProcessBindMaterComponent,
    data: {
      auth: [Authority.TENANT_ADMIN, Authority.CUSTOMER_USER],
      keep:true,
      reuse: false,
      title: '物料绑定',
      devicesType: 'tenant',
      breadcrumb: {
        label: '物料绑定',
        icon: 'devices_other'
      }
    },
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TechnologicalProcessRoutingModule { }
