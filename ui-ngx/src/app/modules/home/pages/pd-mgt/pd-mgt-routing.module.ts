import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Authority } from '@shared/models/authority.enum';
import { PdMgtComponent } from './pd-mgt.component';

const routes: Routes = [
  {
    path: 'pd-mgt',
    component: PdMgtComponent,
    data: {
      auth: [Authority.TENANT_ADMIN],
      title: '盘点记录',
      breadcrumb: {
        label: '盘点记录',
        icon: 'devices_other'
      }
    },
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PdMgtRoutingModule { }
