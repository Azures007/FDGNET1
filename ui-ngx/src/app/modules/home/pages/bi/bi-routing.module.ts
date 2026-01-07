
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { Authority } from '@shared/models/authority.enum';
import { ManageComponent } from './manage.component';


const routes: Routes = [
  {
    path: 'bi',
    data: {
      breadcrumb: {
        label: '电视看板',
        icon: 'bookmarks'
      }
    },
    children: [
      {
        path: '',
        data: {
          auth: [Authority.TENANT_ADMIN, Authority.CUSTOMER_USER],
          redirectTo: '/bi/manage'
        }
      },
      {
        path: 'manage',
        component: ManageComponent,
        data: {
          title: '管理看板',
          breadcrumb: {
            label: '管理看板',
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
export class BiRoutingModule { }
