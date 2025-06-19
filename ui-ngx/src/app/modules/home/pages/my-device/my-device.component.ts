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
import { map, result } from 'lodash';

import { AlertDialogComponent } from '@shared/components/dialog/alert-dialog.component';
import { DialogService } from '@app/core/public-api';
import { TranslateService } from '@ngx-translate/core';

import { DictionaryService } from '@app/core/http/dictionary.service';
import { MyDeviceService } from '@app/core/http/my-device.service';
import { MyDeviveAddComponent } from '../../components/my-device/my-devive-add.component';
import { DeviveUploadComponent } from '../../components/my-device/devive-upload.component';



@Component({
  selector: 'tb-my-device',
  templateUrl: './my-device.component.html',
  styleUrls: ['./my-device.component.scss']
})
export class MyDeviceComponent implements OnInit {

  constructor(
    private authService: AuthService,
    private dialogService: DialogService,
    private MyDeviceService: MyDeviceService,
    private DictionaryService: DictionaryService,
    public fb: FormBuilder,
    private translate: TranslateService,
    public _dialog: MatDialog,
    private router: Router
  ) { }

  //搜索参数
  searchFormGroup = this.fb.group({
    current: 0,
    size: 50,
    deviceName: null,
    deviceNumber: null,
    enabled: "",
  });

  //翻页参数
  length: number;
  pageSize = 50;
  pageIndex = 0;
  pageSizeOptions: number[] = [50, 100, 300, 200, 500];
  total = 0;
  dataSource = [];

  //表格列参数
  displayedColumns: string[] = ['deviceId', 'deviceName', 'deviceNumber', 'belongProcessId', 'kdDeptId', 'kdOrgId', 'note', 'enabled', 'customColumn1'];


  btns = JSON.parse(localStorage.getItem('btns'));
  set = new Set(this.btns);

  //新增设备参数
  devParams = {
    deviceId: null,
    deviceName: "",
    deviceNumber: "",
    enabled: null,
    note: "",
    // status: null,
    // workshop: "",
    // workshopNo: "",
    belongProcessId: "",//工序
    kdDeptId: "",//生产车间
    kdOrgId: "",//生产组织
  }

  //生产车间列表
  midDeptList = JSON.parse(localStorage.getItem('depts'));

  //生产组织列表
  midOrgList = JSON.parse(localStorage.getItem('orgs'));

  //工序列表
  processList = JSON.parse(localStorage.getItem('process'));

  deptMap = new Map();
  orgMap = new Map();
  processMap = new Map();

  //车间列表
  workShop = [];

  ngOnInit(): void {
    this.setMyMap();
  }

  //创建哈希表
  setMyMap() {
    this.processMap = this.putHash(new Map(), this.processList, 'process', 'processId');
    this.deptMap = this.putHash(new Map(), this.midDeptList, 'dept', 'kdDeptId');
    this.orgMap = this.putHash(new Map(), this.midOrgList, 'org', 'kdOrgId');
    this.getWorkShopList();
  }

  //哈希表存储
  putHash(map, arr, str, key) {
    for (let i = 0; i < arr.length; i++) {
      map.set(`${str}-${arr[i][key]}`, JSON.stringify(arr[i]));
    }
    return map;
  }

  //从哈希表获取数据
  handleHash(n, value) {
    let str = '';
    if (value) {
      switch (n) {
        case 0:
          this.processMap.get(`process-${value}`) ? str = JSON.parse(this.processMap.get(`process-${value}`)).processName : ""
          return str;
        case 1:
          this.deptMap.get(`dept-${value}`) ? str = JSON.parse(this.deptMap.get(`dept-${value}`)).kdDeptName : ""
          return str;
        case 2:
          this.orgMap.get(`org-${value}`) ? str = JSON.parse(this.orgMap.get(`org-${value}`)).kdOrgName : ""
          return str;
      }
    } else {
      return str = "未绑定";
    }

  }



  //获取表格数据
  getTableData(): void {
    let par = {
      current: this.searchFormGroup.value.current,
      size: this.searchFormGroup.value.size,
      body: {
        deviceName: this.searchFormGroup.value.deviceName == "" ? null : this.searchFormGroup.value.deviceName,
        deviceNumber: this.searchFormGroup.value.deviceNumber == "" ? null : this.searchFormGroup.value.deviceNumber,
        enabled: this.searchFormGroup.value.enabled == "" ? null : this.searchFormGroup.value.enabled,
      }
    }
    this.MyDeviceService.fetchGetTableList(par).subscribe(res => {
      this.dataSource = res.data.list;
      this.total = res.data.total;
    })
  }

  //获取车间列表
  getWorkShopList() {
    let par = {
      current: 0,
      size: 999,
      codeClId: 'CJ0000',
      enabledSt: 1
    }
    this.DictionaryService.fetchGetTypeTableList(par).subscribe(res => {
      this.workShop = res.data.list;
      this.getTableData();
    })
  }

  //新增设备
  showAddVisibilly() {
    let data = {
      params: this.devParams,
      edit: true,
      wsList: this.workShop,
      midDeptList: this.midDeptList,
      midOrgList: this.midOrgList,
      processList: this.processList
    }
    let diaref = this._dialog.open(MyDeviveAddComponent, {
      width: "695px",
      height: "auto",
      panelClass: 'custom-modalbox',
      data: JSON.parse(JSON.stringify(data))
    })
    diaref.afterClosed().subscribe(res => {
      if (res != undefined) {
        let parData = res;
        this.MyDeviceService.fetchSaveAdd(parData.params).subscribe(res => {
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
              message: '新增设备成功',
              ok: '确定',
            }
            this.dialogService.message(data).subscribe(res => {
              this.getTableData();
            })
          }
        })
      }
    })
  }

  //查看和编辑
  lookDia(value, n) {
    let params = JSON.parse(JSON.stringify(value));
    let data = {
      params: params,
      edit: n == 0 ? true : false,
      wsList: this.workShop,
      midDeptList: this.midDeptList,
      midOrgList: this.midOrgList,
      processList: this.processList
    }
    console.log(data,'data');

    let dialogRef = this._dialog.open(MyDeviveAddComponent, {
      width: "695px",
      height: "auto",
      panelClass: 'custom-modalbox',
      data: JSON.parse(JSON.stringify(data)),
    })
    dialogRef.afterClosed().subscribe(res => {
      if (res != undefined) {
        let parData = res;
        this.MyDeviceService.fetchSaveAdd(parData.params).subscribe(res => {
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
              message: '编辑设备成功',
              ok: '确定',
            }
            this.dialogService.message(data).subscribe(res => {
              this.getTableData();
            })
          }
        })
      }
    })
  }

  //询问启用
  enterDia(value, enbled) {
    let par = {
      deviceId: value.deviceId,
      enable: enbled,
    }
    let changeToen = "";
    if (enbled == 0) {
      changeToen = "启用"
    } else {
      changeToen = "禁用"
    }
    this.dialogService.confirm(
      `是否要${changeToen}该设备?`,
      `确定后,该设备将${changeToen}`,
      this.translate.instant('action.no'),
      this.translate.instant('action.yes'),
      true
    ).subscribe((res) => {
      if (res) {
        this.MyDeviceService.fetchIsEnabled(par).subscribe(res => {
          let data = {
            title: "消息提示",
            message: `设备已${changeToen}`,
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
      `确定要删除设备${value.deviceName}吗？`,
      '删除该设备后不可恢复',
      this.translate.instant('action.no'),
      this.translate.instant('action.yes'),
      true
    ).subscribe((res) => {
      if (res) {
        let params = {
          deviceId: value.deviceId
        }
        this.MyDeviceService.fetchDelete(params).subscribe(res => {
          let data = {
            title: "消息提示",
            message: `设备${value.deviceName}已删除`,
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

  //导出
  hadnleExportTable() {
    let par = {
      current: this.searchFormGroup.value.current,
      size: 9999,
      body: {
        deviceName: this.searchFormGroup.value.deviceName == "" ? null : this.searchFormGroup.value.deviceName,
        deviceNumber: this.searchFormGroup.value.deviceNumber == "" ? null : this.searchFormGroup.value.deviceNumber,
        enabled: this.searchFormGroup.value.enabled == "" ? null : this.searchFormGroup.value.enabled,
      }
    }
    this.MyDeviceService.fetchExportTable(par).subscribe(res => {
      var name = res.headers.get('content-disposition')//获取文件名，（后台返回的文件名在响应头当中）
      name = decodeURIComponent(name)//由于中文通常都是乱码，所以需要解码
      name = name.substring(name.indexOf("=") + 1)//数据处理获得名字
      this.downloadFile(res.body, name)//数据流都存在body中
    })
  }

  //文件数据流有多种类型，需自己明确好
  downloadFile(data, name) {
    const contentType = "application/x-zip-compressed";
    const blob = new Blob([data], { type: contentType });
    const url = window.URL.createObjectURL(blob);
    // 打开新窗口方式进行下载
    // window.open(url); 
    // 以动态创建a标签进行下载
    const a = document.createElement("a");
    a.href = url;
    a.download = name;
    a.click();
    window.URL.revokeObjectURL(url);
  }


  //导入
  handleUploadData() {
    let data = {

    }
    let dialogRef = this._dialog.open(DeviveUploadComponent, {
      width: "695px",
      height: "auto",
      panelClass: 'custom-modalbox',
      data: JSON.parse(JSON.stringify(data)),
    })
    dialogRef.afterClosed().subscribe(res => {

      if (res != undefined) {
        this.MyDeviceService.fetchImport(res).subscribe(res => {
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
              message: '导入设备成功',
              ok: '确定',
            }
            this.dialogService.message(data).subscribe(res => {
              this.getTableData();
            })
          }
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
}
