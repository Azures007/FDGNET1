import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@app/shared/shared.module';

import { SpecificationRoutingModule } from './specification-routing.module';
import { SpecificationComponent } from './specification.component';



@NgModule({
  declarations: [
    SpecificationComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    SpecificationRoutingModule
  ]
})
export class SpecificationModule { }
