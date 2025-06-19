import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';

import { Utils } from '../order-management/w-utils';
import { MatDialog } from '@angular/material/dialog';
import { SelectionModel } from '@angular/cdk/collections';
import { MatTableDataSource } from '@angular/material/table';

import { DictionaryService } from '@app/core/http/dictionary.service';
import { ReportAdminService } from '@app/core/http/report-admin.service';


@Component({
  selector: 'tb-work-report',
  templateUrl: './work-report.component.html',
  styleUrls: ['./report.scss', '../order-management/order-management.component.scss',]
})
export class WorkReportComponent implements OnInit {

  constructor(
    private utils: Utils,
    public fb: FormBuilder,
    public router: Router,
    public dialog: MatDialog,
    private ReportAdminService: ReportAdminService,
    private DictionaryService: DictionaryService,
  ) { }

  minDate: Date;
  orderBillRange = new FormGroup({
    start: new FormControl(new Date(new Date().getTime() - 2 * 24 * 3600 * 1000)),
    end: new FormControl(new Date(new Date().getTime() + 4 * 24 * 3600 * 1000)),
  });

  ngOnInit(): void {
  }

}
