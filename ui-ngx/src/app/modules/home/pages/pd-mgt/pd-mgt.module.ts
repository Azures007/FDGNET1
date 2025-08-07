import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '@app/shared/shared.module';
import { PdMgtRoutingModule } from './pd-mgt-routing.module';
import { PdMgtComponent } from './pd-mgt.component';


@NgModule({
  declarations: [
    PdMgtComponent
  ],
  imports: [
    CommonModule,
    PdMgtRoutingModule,
    SharedModule
  ]
})
export class PdMgtModule { }
