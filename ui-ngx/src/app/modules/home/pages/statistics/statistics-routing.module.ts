
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { Authority } from '@shared/models/authority.enum';
import { MaterialComponent } from './material.component';
import { DeviceComponent } from './device.component';
import { RatioComponent } from './ratio.component';



const routes: Routes = [
  {
    path: 'statistics',
    data: {
      breadcrumb: {
        label: '数据统计',
        icon: 'bookmarks'
      }
    },
    children: [
      {
        path: '',
        data: {
          auth: [Authority.TENANT_ADMIN, Authority.CUSTOMER_USER],
          redirectTo: '/statistics/material'
        }
      },
      {
        path: 'material',
        component: MaterialComponent,
        data: {
          title: '原料投入报表',
          breadcrumb: {
            label: '原料投入报表',
            icon: 'bookmarks'
          }
        }
      },
      {
        path: 'device',
        component: DeviceComponent,
        data: {
          title: '设备运行报表',
          breadcrumb: {
            label: '设备运行报表',
            icon: 'bookmarks'
          }
        }
      },
      {
        path: 'ratio',
        component: RatioComponent,
        data: {
          title: '投入产出比报表',
          breadcrumb: {
            label: '投入产出比报表',
            icon: 'bookmarks'
          }
        }
      },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class StatisticsRoutingModule { }
