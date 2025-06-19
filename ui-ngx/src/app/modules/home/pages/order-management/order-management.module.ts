

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { HomeComponentsModule } from '@modules/home/components/home-components.module';

import { OrderManagementComponent } from './order-management.component';
import { OrderManagementRoutingModule } from './order-management-routing.module';

import { OrderListComponent } from './order-list.component';
import { OrderDetailsComponent } from './order-details.component';


import { StartOrderComponent } from './dialog/start-order.component';



import {DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE} from '@angular/material/core';

import { Utils } from './w-utils';
import { ChangeClassComponent } from './dialog/change-class.component';

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
  declarations: [OrderManagementComponent, OrderDetailsComponent, OrderListComponent, StartOrderComponent, ChangeClassComponent],
  imports: [
    CommonModule,
    SharedModule,
    HomeComponentsModule,
    OrderManagementRoutingModule
  ],
  providers: [
    Utils,
    { provide: MAT_DATE_FORMATS , useValue: COMMISSION_DATE_FORMATS },  { provide: MAT_DATE_LOCALE, useValue: 'zh-CN' }

  ]
})
export class OrderManagementModule { }
