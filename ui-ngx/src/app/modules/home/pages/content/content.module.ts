import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@app/shared/shared.module';

import { ContentRoutingModule } from './content-routing.module';
import { ContentComponent } from './content.component';
import { AddContentComponent } from './dialog/add-content.component';



@NgModule({
  declarations: [
    ContentComponent,
    AddContentComponent
  ],
  imports: [
    CommonModule,
    ContentRoutingModule,
    SharedModule
  ]
})
export class ContentModule { }
