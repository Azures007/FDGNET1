import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { PickingRoutingModule } from './picking-routing.module';
import { PickingSumComponent } from './picking-sum.component';
import { PickingPlanComponent } from './picking-plan.component';

import { DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE } from '@angular/material/core';
import { Utils } from '../order-management/w-utils';
import { SharedModule } from '@app/shared/shared.module';

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
  declarations: [
    PickingSumComponent,
    PickingPlanComponent,
  ],
  imports: [
    CommonModule,
    PickingRoutingModule,
    SharedModule
  ],
  providers: [
    Utils, { provide: MAT_DATE_FORMATS, useValue: COMMISSION_DATE_FORMATS },
    { provide: MAT_DATE_LOCALE, useValue: 'zh-CN' }
  ]
})
export class PickingModule { }
