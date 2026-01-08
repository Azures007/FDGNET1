

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { HomeComponentsModule } from '@modules/home/components/home-components.module';

import { BiComponent } from './bi.component';
import { ManageComponent } from './manage.component';
import { BiRoutingModule } from './bi-routing.module';




import {DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE} from '@angular/material/core';

import { Utils } from '../order-management/w-utils';

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
  declarations: [BiComponent, ManageComponent],
  imports: [
    CommonModule,
    SharedModule,
    HomeComponentsModule,
    BiRoutingModule
  ],
  providers: [
    Utils,
    { provide: MAT_DATE_FORMATS , useValue: COMMISSION_DATE_FORMATS },  { provide: MAT_DATE_LOCALE, useValue: 'zh-CN' }

  ]
})
export class BiModule { }
