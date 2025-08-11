import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';

import { MatDialog } from '@angular/material/dialog';

import { DialogService } from '@app/core/public-api';
import { TranslateService } from '@ngx-translate/core';

import { ContentService } from '@app/core/http/content.service';


import { AddContentComponent } from './dialog/add-content.component';
import { Utils } from '../order-management/w-utils';



@Component({
  selector: 'tb-content',
  templateUrl: './content.component.html',
  styleUrls: ['./content.component.scss']
})
export class ContentComponent implements OnInit {

  constructor(
    private dialogService: DialogService,
    private contentService: ContentService,
    public fb: FormBuilder,
    private translate: TranslateService,
    private utils: Utils,
    public _dialog: MatDialog) { }

  //搜索参数
  searchFormGroup = this.fb.group({
    current: 0,
    size: 50,
    materialName: '',
    materialCode: '',
    materialModel: '',
    status: '',
  });

  //翻页参数
  length: number;
  pageSize = 50;
  pageIndex = 0;
  pageSizeOptions: number[] = [50, 100, 300, 200, 500];
  total = 0;
  dataSource = [];

  //表格列参数
  displayedColumns: string[] = ['no', 'materialName', 'materialCode', 'materialModel', 'lowerLimit', 'upperLimit', 'status', 'customColumn1']


  //新增人员参数
  addParams = {
    materialName: "",
    materialCode: "",
    materialModel: "",
    lowerLimit: "",
    upperLimit: "",
    status: "",
  }


  btns = JSON.parse(localStorage.getItem('btns'));
  set = new Set(this.btns);


  ngOnInit(): void {
    this.getTableData();

  }


  //获取表格数据
  getTableData(): void {
    let par = {
      current: this.searchFormGroup.value.current,
      size: this.searchFormGroup.value.size,
      body: {
        materialName: this.searchFormGroup.value.materialName,
        materialCode: this.searchFormGroup.value.materialCode,
        materialModel: this.searchFormGroup.value.materialModel,
        status: this.searchFormGroup.value.status,
      }
    }
    this.contentService.fetchGetTableList(par).subscribe(res => {
      this.dataSource = res.data.list;
      this.total = res.data.total;
    })
  }

  //新增
  showAddVisibilly() {
    let data = {
      params: this.addParams,
      edit: true,
    }
    let diaref = this._dialog.open(AddContentComponent, {
      width: "450px",
      height: "auto",
      panelClass: 'custom-modalbox',
      data: JSON.parse(JSON.stringify(data))
    })
    diaref.afterClosed().subscribe(res => {
      if (res) {
        this.getTableData();
      }
    })
  }


  //查看和编辑
  lookDia(value, n) {
    this.contentService.fetchGetDetail(value.id).subscribe(res => {
      if (res.errcode === 200) {
        let data = {
          params: JSON.parse(JSON.stringify(res.data)),
          edit: n == 0 ? true : false,
        }
        let diaref = this._dialog.open(AddContentComponent, {
          width: "450px",
          height: "auto",
          panelClass: 'custom-modalbox',
          data: JSON.parse(JSON.stringify(data))
        })
        diaref.afterClosed().subscribe(res => {
          if (res) {
            this.getTableData();
          }
        })
      } else {
        this.utils.showMessage(res.errmsg, 'error');
      }
    })
  }

  //询问启用
  enterDia(value, enabledSt) {
    let changeToen = "";
    if (enabledSt == 0) {
      changeToen = "禁用"
    } else {
      changeToen = "启用"
    }
    this.dialogService.confirm(
      `是否要${changeToen}该人员?`,
      `确定后,该人员将${changeToen}`,
      this.translate.instant('action.no'),
      this.translate.instant('action.yes'),
      true
    ).subscribe((res) => {
      if (res) {
        let par = {
          id: value.id,
          status: enabledSt,
        }
        this.contentService.fetchEnableOrDisable(par).subscribe(res => {
          if (res.errcode === 200) {
            this.utils.showMessage(changeToen + '成功', 'success');
            this.getTableData();
          } else {
            this.utils.showMessage(res.errmsg, 'error');
          }
        })
      }
    }
    );
  }

  //删除
  delDia(value) {
    this.dialogService.confirm(
      `确定要删除该内容吗？`,
      '删除该内容后不可恢复',
      this.translate.instant('action.no'),
      this.translate.instant('action.yes'),
      true
    ).subscribe((res) => {
      if (res) {
        this.contentService.fetchDelete(value.id).subscribe(res => {
          if (res.errcode === 200) {
            this.utils.showMessage('删除成功', 'success');
            this.getTableData();
          } else {
            this.utils.showMessage(res.errmsg, 'error');
          }
        })
      }
    }
    );
  }


  //翻页
  getNotices($event): any {
    // 点击paginator事件，获取pageIndex，重新加载页面
    this.searchFormGroup.value.current = $event.pageIndex;
    this.searchFormGroup.value.size = $event.pageSize;
    this.getTableData();
  }
  reset() {
    this.searchFormGroup.value.materialName = '';
    this.searchFormGroup.value.materialCode = '';
    this.searchFormGroup.value.materialModel = '';
    this.searchFormGroup.value.status = '';
    this.getTableData();
  }
}
