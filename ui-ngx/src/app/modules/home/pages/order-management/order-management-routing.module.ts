
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { Authority } from '@shared/models/authority.enum';
import { OrderDetailsComponent } from './order-details.component';
import { OrderListComponent } from './order-list.component';


const routes: Routes = [
  {
    path: 'order',
    data: {
      breadcrumb: {
        label: 'order.management',
        icon: 'bookmarks'
      }
    },
    children: [
      {
        path: '',
        data: {
          auth: [Authority.TENANT_ADMIN],
          redirectTo: '/order/list'
        }
      },
      {
      path: 'list',
      component: OrderListComponent,
      data: {
        title: 'order.list',
        breadcrumb: {
          label: 'order.list',
          icon: 'bookmarks'
        }
      }
    },
      {
        path: 'details/:orderId',
        component: OrderDetailsComponent,
        data: {
          title: 'order.details',
          breadcrumb: {
            label: 'order.details',
            icon: 'bookmarks'
          }
        }
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class OrderManagementRoutingModule { }
