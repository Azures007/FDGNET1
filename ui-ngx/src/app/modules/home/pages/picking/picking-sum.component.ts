import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';

import { Utils } from '../order-management/w-utils';
import { MatDialog } from '@angular/material/dialog';

import { SelectionModel } from '@angular/cdk/collections';
import { MatTableDataSource } from '@angular/material/table';

import { PickSumService } from '@app/core/http/picking-sum.service';

@Component({
  selector: 'tb-picking-sum',
  templateUrl: './picking-sum.component.html',
  styleUrls: ['./picking-sum.component.scss']
})
export class PickingSumComponent implements OnInit {


  constructor(
    public fb: FormBuilder,
    private utils: Utils,
    private PickSumService: PickSumService,
    public router: Router,
    public dialog: MatDialog
  ) { }

  minDate: Date;

  orderBillRange = new FormGroup({
    start: new FormControl(new Date(new Date().getTime() - 2 * 24 * 3600 * 1000)),
    end: new FormControl(new Date(new Date().getTime() + 4 * 24 * 3600 * 1000)),
  });

  // 翻页参数
  length: number;
  pageSize = 50;
  pageIndex = 0;
  pageSizeOptions: number[] = [50, 100, 300, 200, 500];
  total = 0;

  sumData:null;


  // 搜索参数
  searchFormGroup = this.fb.group({
    current: 0,
    size: 50,
    createdName: '',
    pickingNo: '',
    pickingStatus: '',
  });

  statusSelect = [
    { label: "进行中", value: "0" },
    { label: "待提交", value: "1" },
    { label: "已完工", value: "2" },
  ]



  ngOnInit(): void {
    this.minDate = new Date('2020-01-01 00:00:00');
    console.log(this.searchFormGroup)
    this.searchList();
  }



  //搜索
  searchList() {
    let storagePar = localStorage.getItem('storagePar');
    let params = null;
    if (storagePar && JSON.parse(storagePar).body) {
      let storageParObj = JSON.parse(storagePar);
      this.searchFormGroup.value.current = storageParObj.current;
      this.searchFormGroup.value.size = storageParObj.size;
      this.searchFormGroup.value.createdName = storageParObj.body.createdName;
      this.searchFormGroup.value.pickingNo = storageParObj.body.pickingNo;
      this.searchFormGroup.value.pickingStatus = storageParObj.body.pickingStatus;
    } else {

    }
    params = {
      current: this.searchFormGroup.value.current,
      size: this.searchFormGroup.value.size,
      body: {
        createdTimeLater: this.utils.dateFormat(new Date(this.orderBillRange.value.end), 'yyyy-MM-dd 23:59:59'),
        createdTimeFront: this.utils.dateFormat(new Date(this.orderBillRange.value.start), 'yyyy-MM-dd 00:00:00'),
        createdName: this.searchFormGroup.value.createdName,
        pickingNo: this.searchFormGroup.value.pickingNo,
        pickingStatus: this.searchFormGroup.value.pickingStatus,
      }
    }

    this.PickSumService.fetchGetTableList(params).subscribe(res => {
      console.log(res,'listres');
      this.sumData = res.data
    })
  }

  //导出
  export() { }

}
