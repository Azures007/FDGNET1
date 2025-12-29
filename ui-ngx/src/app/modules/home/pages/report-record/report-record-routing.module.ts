import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Authority } from '@shared/models/authority.enum';
import { ReportRecordComponent } from './report-record.component';

const routes: Routes = [
  {
    path: 'report-record',
    component: ReportRecordComponent,
    data: {
      auth: [Authority.TENANT_ADMIN, Authority.CUSTOMER_USER],
      title: '报工记录',
      breadcrumb: {
        label: '报工记录',
        icon: 'devices_other'
      }
    },
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ReportRecordRoutingModule { }
