import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '@app/shared/shared.module';
import { ReportRecordRoutingModule } from './report-record-routing.module';
import { ReportRecordComponent } from './report-record.component';


@NgModule({
  declarations: [
    ReportRecordComponent
  ],
  imports: [
    CommonModule,
    ReportRecordRoutingModule,
    SharedModule
  ]
})
export class ReportRecordModule { }
