import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { AuthService } from '@core/auth/auth.service';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { PageComponent } from '@shared/components/page.component';
import { FormBuilder } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Constants } from '@shared/models/constants';
import { Router } from '@angular/router';
import { OAuth2ClientInfo } from '@shared/models/oauth2.models';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';

import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { result } from 'lodash';

import { AlertDialogComponent } from '@shared/components/dialog/alert-dialog.component';
import { DialogService } from '@app/core/public-api';
import { TranslateService } from '@ngx-translate/core';

import { DictionaryService } from '@app/core/http/dictionary.service';
import { StaffService } from '@app/core/http/staff.service';
import { AccountService } from '@app/core/http/account.service';


import { StaffAddComponent } from '../../components/staff/staff-add.component';
import { StaffUploadComponent } from '../../components/staff/staff-upload.component';
import { AddFingerComponent } from '../../components/staff/add-finger.component';



@Component({
  selector: 'tb-staff',
  templateUrl: './staff.component.html',
  styleUrls: ['./staff.component.scss']
})
export class StaffComponent implements OnInit {

  constructor(
    private authService: AuthService,
    private dialogService: DialogService,
    private StaffService: StaffService,
    private DictionaryService: DictionaryService,
    private AccountService: AccountService,
    public fb: FormBuilder,
    private translate: TranslateService,
    public _dialog: MatDialog,
    private router: Router
  ) { }

  //搜索参数
  searchFormGroup = this.fb.group({
    current: 0,
    size: 50,
    className: null,
    name: null,
    sex: '',
  });

  //翻页参数
  length: number;
  pageSize = 50;
  pageIndex = 0;
  pageSizeOptions: number[] = [50, 100, 300, 200, 500];
  total = 0;
  dataSource = [];

  //表格列参数
  displayedColumns: string[] = ['name', 'sex', 'phone', 'class','enabledSt', 'customColumn1']

  //岗位列表
  stationList = [];
  //账号列表
  accountList = [];

  //新增人员参数
  addParams = {
    className: "",
    enabledSt: null,
    name: "",
    phone: "",
    sex: "",
    // station: "",
    userId: "",
    userEmail: "",
    personnelId: "",
  }

  //岗位类别map
  stationListMap = new Map();

  btns = JSON.parse(localStorage.getItem('btns'));
  set = new Set(this.btns);


  ngOnInit(): void {
    this.getAccountList();
  }


  //获取表格数据
  getTableData(): void {
    let par = {
      current: this.searchFormGroup.value.current,
      size: this.searchFormGroup.value.size,
      body: {
        className: this.searchFormGroup.value.className == "" ? null : this.searchFormGroup.value.className,
        name: this.searchFormGroup.value.name == "" ? null : this.searchFormGroup.value.name,
        sex: this.searchFormGroup.value.sex == "" ? null : this.searchFormGroup.value.sex,
      }
    }
    this.StaffService.fetchGetTableList(par).subscribe(res => {
      this.dataSource = res.data.list;
      this.total = res.data.total;
    })
  }

  //获取岗位列表
  getStationList() {
    let par = {
      current: 0,
      size: 999,
      codeClId: 'JOB0000',
      enabledSt: 1
    }
    this.DictionaryService.fetchGetTypeTableList(par).subscribe(res => {
      this.stationList = res.data.list;
      for (let i = 0; i < this.stationList.length; i++) {
        this.stationListMap.set(`${this.stationList[i].codeValue}`, this.stationList[i])
      }
      this.getTableData();
    })
  }

  handleStation(value) {
    if (this.stationListMap.get(value)) {
      return this.stationListMap.get(value).codeDsc;
    } else {
      return '---'
    }
  }

  //获取账号列表
  getAccountList(cb?) {
    let par = {
      current: 0,
      size: 999,
      body: {
        userStatus: 1,
        username: "",
        name: "",
      }
    }
    this.AccountService.fetchGetTableList(par).subscribe(res => {
      this.accountList = res.data.list;
      this.getStationList();
      cb && cb();
    })
  }

  //新增
  showAddVisibilly() {
    this.getAccountList(() => {
      let data = {
        params: this.addParams,
        edit: true,
        stationList: this.stationList,
        accountList: this.accountList,
      }
      let diaref = this._dialog.open(StaffAddComponent, {
        width: "695px",
        height: "auto",
        panelClass: 'custom-modalbox',
        data: JSON.parse(JSON.stringify(data))
      })
      diaref.afterClosed().subscribe(res => {
        if (res) {
          let parData = res.params;
          this.StaffService.fetchSaveAdd(parData).subscribe(res => {
            if (res.errcode == 0) {
              let data = {
                title: "消息提示",
                message: res.errmsg,
                ok: '确定',
              }
              this.dialogService.message(data)
            } else {
              let data = {
                title: "消息提示",
                message: '新增人员成功',
                ok: '确定',
              }
              this.dialogService.message(data).subscribe(res => {
                this.getTableData();
              })
            }
          })
        }
      })
    })
  }

  //采集指纹
  fingerDia(value){
    let diaref = this._dialog.open(AddFingerComponent, {
      width: "695px",
      height: "auto",
      panelClass: 'custom-modalbox',
      data: JSON.parse(JSON.stringify(value))
    })
    diaref.afterClosed().subscribe(res=>{
      console.log(res);
      if(res==1){
        this.getAccountList();
      }
    })
  }

  //查看和编辑
  lookDia(value, n) {
    this.getAccountList(() => {
      let data = {
        params: JSON.parse(JSON.stringify(value)),
        edit: n == 0 ? true : false,
        stationList: this.stationList,
        accountList: this.accountList,
      }
      let diaref = this._dialog.open(StaffAddComponent, {
        width: "695px",
        height: "auto",
        panelClass: 'custom-modalbox',
        data: JSON.parse(JSON.stringify(data))
      })
      diaref.afterClosed().subscribe(res => {
        if (res) {
          let parData = res.params;
          this.StaffService.fetchSaveAdd(parData).subscribe(res => {
            if (res.errcode == 0) {
              let data = {
                title: "消息提示",
                message: res.errmsg,
                ok: '确定',
              }
              this.dialogService.message(data)
            } else {
              let data = {
                title: "消息提示",
                message: '编辑人员成功',
                ok: '确定',
              }
              this.dialogService.message(data).subscribe(res => {
                this.getTableData();
              })
            }
          })
        }
      })
    })
  }

  //询问启用
  enterDia(value, enabledSt) {
    let par = {
      personnelId: value.personnelId,
      enabledSt: enabledSt,
    }
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
        this.StaffService.fetchIsEnabled(par).subscribe(res => {
          let data = {
            title: "消息提示",
            message: `人员已${changeToen}`,
            ok: '确定',
          }
          this.dialogService.message(data).subscribe(res => {
            this.getTableData();
          })
        })
      }
    }
    );
  }

  //删除
  delDia(value) {
    this.dialogService.confirm(
      `确定要删除人员吗？`,
      '删除该人员后不可恢复',
      this.translate.instant('action.no'),
      this.translate.instant('action.yes'),
      true
    ).subscribe((res) => {
      if (res) {
        let params = {
          personnelId: value.personnelId
        }
        this.StaffService.fetchDelete(params).subscribe(res => {
          let data = {
            title: "消息提示",
            message: `人员已删除`,
            ok: '确定',
          }
          this.dialogService.message(data).subscribe(res => {

            this.getTableData();

          })
        })
      }
    }
    );
  }


  //导入
  handleUploadData() {
    let data = {

    }
    let dialogRef = this._dialog.open(StaffUploadComponent, {
      width: "695px",
      height: "auto",
      panelClass: 'custom-modalbox',
      data: JSON.parse(JSON.stringify(data)),
    })
    dialogRef.afterClosed().subscribe(res => {
      if (res != undefined) {
        this.StaffService.fetchImport(res).subscribe(res => {
          let data = {
            title: "消息提示",
            message: `人员导入成功`,
            ok: '确定',
          }
          this.dialogService.message(data).subscribe(res => {

            this.getTableData();

          })
        })
      }
    })
  }


  //翻页
  getNotices($event): any {
    // 点击paginator事件，获取pageIndex，重新加载页面
    this.searchFormGroup.value.current = $event.pageIndex;
    this.searchFormGroup.value.size = $event.pageSize;
    this.getTableData();
  }
  reset() {
    this.searchFormGroup.value.className = null;
    this.searchFormGroup.value.name = null;
    this.searchFormGroup.value.sex = '';
    this.getTableData();
  }
}
