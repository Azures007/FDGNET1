import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Authority } from '@shared/models/authority.enum';
import { DictionaryComponent } from './dictionary.component';

const routes: Routes = [
  {
    path: 'dictionary',
    component: DictionaryComponent,
    data: {
      auth: [Authority.TENANT_ADMIN, Authority.CUSTOMER_USER],
      title: 'dictionary.dictionary-admin',
      devicesType: 'tenant',
      breadcrumb: {
        label: 'dictionary.dictionary',
        icon: 'devices_other'
      }
    },
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DictionaryRoutingModule { }
