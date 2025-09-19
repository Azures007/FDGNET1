
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { Authority } from '@shared/models/authority.enum';
import { RecipeComponent } from './recipe.component';
import { RecipeBindMaterComponent } from './recipe-bind-mater.component';


const routes: Routes = [
  {
    path: 'recipe',
    component: RecipeComponent,
    data: {
      auth: [Authority.TENANT_ADMIN, Authority.CUSTOMER_USER],
      title: '配方管理',
      breadcrumb: {
        label: '配方管理',
        icon: 'devices_other'
      }
    },
  },
  {
      path: 'recipe-material-bind',
      component: RecipeBindMaterComponent,
      data: {
        auth: [Authority.TENANT_ADMIN, Authority.CUSTOMER_USER],
        reuse: false,
        title: '产品绑定',
        devicesType: 'tenant',
        breadcrumb: {
          label: '产品绑定',
          icon: 'devices_other'
        }
      },
    }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class RecipeRoutingModule { }
