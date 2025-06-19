import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@app/shared/shared.module';

import { MaterialRoutingModule } from './material-routing.module';
import { MaterialComponent } from './material.component';


@NgModule({
  declarations: [
    MaterialComponent
  ],
  imports: [
    CommonModule,
    MaterialRoutingModule,
    SharedModule
  ]
})
export class MaterialModule { }
