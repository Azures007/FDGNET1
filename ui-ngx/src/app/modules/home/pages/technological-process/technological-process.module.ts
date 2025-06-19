

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { HomeComponentsModule } from '@modules/home/components/home-components.module';

import { ProcessManagementComponent } from './process-management.component';
import { TechnologicalProcessRoutingModule } from './technological-process-routing.module';

import { TechnologicalProcessComponent } from './technological-process.component';
import { ProcessRouteComponent } from './process-route.component';

// modal
import { AddProcessManageComponent } from './dialog/add-process-manage.component';
import { AddProcessRouteComponent } from './dialog/add.process-route.component';
import { ProcessSettingComponent } from './dialog/process-setting.component';
import { TransferComponent } from './dialog/transfer.component';


import {DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE} from '@angular/material/core';

import { Utils } from '../order-management/w-utils';
import { ProcessBindMaterComponent } from './process-bind-mater.component';

const COMMISSION_DATE_FORMATS = {
  parse: {
    dateInput: 'MM/YYYY',
  },
  display: {
    dateInput: 'MM/YYYY',
    monthYearLabel: 'MMM YYYY',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'MMMM YYYY',
  },
};

@NgModule({
  declarations: [ProcessManagementComponent, TechnologicalProcessComponent,
    ProcessRouteComponent, AddProcessManageComponent, ProcessSettingComponent,
    TransferComponent, AddProcessRouteComponent, ProcessBindMaterComponent],
  imports: [
    CommonModule,
    SharedModule,
    HomeComponentsModule,
    TechnologicalProcessRoutingModule
  ],
  providers: [
    Utils,
    { provide: MAT_DATE_FORMATS , useValue: COMMISSION_DATE_FORMATS },  { provide: MAT_DATE_LOCALE, useValue: 'zh-CN' }

  ]
})
export class TechnologicalProcessModule { }
