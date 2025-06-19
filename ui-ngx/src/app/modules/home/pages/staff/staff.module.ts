import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@app/shared/shared.module';

import { StaffRoutingModule } from './staff-routing.module';
import { StaffComponent } from './staff.component';


@NgModule({
  declarations: [
    StaffComponent,
  ],
  imports: [
    CommonModule,
    StaffRoutingModule,
    SharedModule
  ]
})
export class StaffModule { }
