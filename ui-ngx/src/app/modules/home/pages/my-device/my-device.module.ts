import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@app/shared/shared.module';

import { MyDeviceRoutingModule } from './my-device-routing.module';
import { MyDeviceComponent } from './my-device.component';


@NgModule({
  declarations: [
    MyDeviceComponent
  ],
  imports: [
    CommonModule,
    MyDeviceRoutingModule,
    SharedModule
  ]
})
export class MyDeviceModule { }
