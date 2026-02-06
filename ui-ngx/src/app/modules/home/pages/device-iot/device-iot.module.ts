

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { HomeComponentsModule } from '@modules/home/components/home-components.module';

import { DeviceIotComponent } from './device-iot.component';
import { DeviceIotRoutingModule } from './device-iot-routing.module';


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
  declarations: [DeviceIotComponent],
  imports: [
    CommonModule,
    SharedModule,
    HomeComponentsModule,
    DeviceIotRoutingModule
  ],
  providers: [
    Utils,
    { provide: MAT_DATE_FORMATS , useValue: COMMISSION_DATE_FORMATS },  { provide: MAT_DATE_LOCALE, useValue: 'zh-CN' }

  ]
})
export class DeviceIotModule { }
