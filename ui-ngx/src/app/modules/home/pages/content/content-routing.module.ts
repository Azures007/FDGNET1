import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Authority } from '@shared/models/authority.enum';
import { ContentComponent } from './content.component';

const routes: Routes = [
  {
    path: 'content',
    component: ContentComponent,
    data: {
      auth: [Authority.TENANT_ADMIN, Authority.CUSTOMER_USER],
      title: '净含量范围管理',
      devicesType: 'tenant',
      breadcrumb: {
        label: '净含量范围管理',
        icon: 'devices_other'
      }
    },
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ContentRoutingModule { }
