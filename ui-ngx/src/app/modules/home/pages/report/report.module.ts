import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ReportRoutingModule } from './report-routing.module';
import { SubmitWorkReportComponent } from './submit-work-report.component';


@NgModule({
  declarations: [
    SubmitWorkReportComponent
  ],
  imports: [
    CommonModule,
    ReportRoutingModule
  ]
})
export class ReportModule { }
