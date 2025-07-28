import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '@app/shared/shared.module';
import { InventoryMgtRoutingModule } from './invetory-mgt-routing.module';
import { InventoryMgtComponent } from './inventory-mgt.component';


@NgModule({
  declarations: [
    InventoryMgtComponent
  ],
  imports: [
    CommonModule,
    InventoryMgtRoutingModule,
    SharedModule
  ]
})
export class InventoryMgtModule { }
