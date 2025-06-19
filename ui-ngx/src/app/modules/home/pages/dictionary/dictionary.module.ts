import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@app/shared/shared.module';

import { DictionaryRoutingModule } from './dictionary-routing.module';
import { DictionaryComponent } from './dictionary.component';


@NgModule({
  declarations: [
    DictionaryComponent
  ],
  imports: [
    CommonModule,
    DictionaryRoutingModule,
    SharedModule
  ]
})
export class DictionaryModule { }
