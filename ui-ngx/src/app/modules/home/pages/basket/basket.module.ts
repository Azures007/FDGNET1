import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@app/shared/shared.module';

import { BasketRoutingModule } from './basket-routing.module';
import { BasketManageComponent } from './basket-manage.component';


@NgModule({
  declarations: [
    BasketManageComponent
  ],
  imports: [
    CommonModule,
    BasketRoutingModule,
    SharedModule
  ]
})
export class BasketModule { }
