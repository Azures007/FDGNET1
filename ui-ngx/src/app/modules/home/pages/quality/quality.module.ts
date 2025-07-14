

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { HomeComponentsModule } from '@modules/home/components/home-components.module';

import { QualityComponent } from './quality.component';
import { QualityCateComponent } from './quality-cate.component';
import { QualityPlanComponent } from './quality-plan.component';
import { QualityRoutingModule } from './quality-routing.module';
import { QualityCheckPlanComponent } from './quality-check-plan.component';




import {DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE} from '@angular/material/core';

import { Utils } from '../order-management/w-utils';
import { QualityCheckCateComponent } from './quality-check-cate.component';

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
  declarations: [QualityComponent, QualityCateComponent, QualityPlanComponent, QualityCheckCateComponent, QualityCheckPlanComponent],
  imports: [
    CommonModule,
    SharedModule,
    HomeComponentsModule,
    QualityRoutingModule
  ],
  providers: [
    Utils,
    { provide: MAT_DATE_FORMATS , useValue: COMMISSION_DATE_FORMATS },  { provide: MAT_DATE_LOCALE, useValue: 'zh-CN' }

  ]
})
export class QualityModule { }
