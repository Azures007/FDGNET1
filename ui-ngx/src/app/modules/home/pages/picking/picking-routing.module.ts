import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Authority } from '@shared/models/authority.enum';
import { PickingPlanComponent } from './picking-plan.component';
import { PickingSumComponent } from './picking-sum.component';

const routes: Routes = [
  {
    path: "picking",
    data: {
      breadcrumb: {
        label: 'picking.picking-admin',
        icon: 'bookmarks'
      }
    },
    children: [
      {
        path: '',
        data: {
          auth: [Authority.TENANT_ADMIN, Authority.CUSTOMER_USER],
          redirectTo: '/picking/picking-sum'
        }
      },
      {
        path: 'picking-plan',
        component: PickingPlanComponent,
        data: {
          title: 'picking.picking-plan',
          breadcrumb: {
            label: 'picking.picking-plan',
            icon: 'bookmarks'
          }
        }
      },
      {
        path: 'picking-sum',
        component: PickingSumComponent,
        data: {
          title: 'picking.picking-sum',
          breadcrumb: {
            label: 'picking.picking-sum',
            icon: 'bookmarks'
          }
        }
      },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PickingRoutingModule { }
