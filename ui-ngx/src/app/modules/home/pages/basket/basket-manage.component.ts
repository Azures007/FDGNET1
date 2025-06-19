import { Component, OnInit } from '@angular/core';
import { AuthService } from '@core/auth/auth.service';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { FormGroup, FormControl, FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { DialogService } from '@app/core/public-api';
import { TranslateService } from '@ngx-translate/core';
import { DictionaryService } from '@app/core/http/dictionary.service';


import { MatDialog } from '@angular/material/dialog';
import { Utils } from '../order-management/w-utils';
import { BasketService } from '@app/core/http/basket.service';
import { AddBasketComponent } from '../../components/basket/add-basket.component';
import { EnterDiaComponent } from '../../components/basket/enter-dia.component';
import { ShowCodeComponent } from '../../components/basket/show-code.component';
import { Directionality } from '@angular/cdk/bidi';


@Component({
  selector: 'tb-basket-manage',
  templateUrl: './basket-manage.component.html',
  styleUrls: ['./basket-manage.component.scss']
})
export class BasketManageComponent implements OnInit {

  constructor(
    protected store: Store<AppState>,
    public fb: FormBuilder,
    public _dialog: MatDialog,
    private utils: Utils,
    private BasketService: BasketService,
    private dialogService: DialogService,
    private translate: TranslateService,
    private DictionaryService: DictionaryService,
  ) { }

  minDate: Date;
  // createRange = new FormGroup({
  //   start: new FormControl(new Date(new Date().getTime() - 2 * 24 * 3600 * 1000)),
  //   end: new FormControl(new Date(new Date().getTime() + 4 * 24 * 3600 * 1000)),
  // });


  //搜索参数
  searchFormGroup = this.fb.group({
    current: 0,
    size: 50,
    chargingBasketStatus: -1,
    code: "",
    // createdTimeFront: '',
    // createdTimeLater: '',
    orderKey: '',
    orderVal: '',
  });



  //翻页参数
  length: number;
  pageSize = 50;
  pageIndex = 0;
  pageSizeOptions: number[] = [50, 100, 300, 200, 500];
  total = 0;
  dataSource = [];
  allUnits = [];

  unitList = [];

  displayedColumns: string[] = ['code', 'createdTime', 'createdName', 'chargingBasketSize', 'maxBearing',
    'weight', 'unit', 'br', 'chargingBasketStatus', 'customColumn1'];

  basketStatus: [
    { label: '全部', value: '-1' },
    { label: '禁用', value: '0' },
    { label: '启用', value: '1' },
  ]

  btns = JSON.parse(localStorage.getItem('btns'));
  set = new Set(this.btns);

  ngOnInit(): void {
    this.getUnitList();
  }

  getList() {
    const params = {
      current: this.searchFormGroup.value.current,
      size: this.searchFormGroup.value.size,
      body: {
        chargingBasketStatus: this.searchFormGroup.value.chargingBasketStatus == -1 ? '' : this.searchFormGroup.value.chargingBasketStatus,
        code: this.searchFormGroup.value.code,
        // createdTimeLater: this.utils.dateFormat(new Date(this.createRange.value.end), 'yyyy-MM-dd'),
        // createdTimeFront: this.utils.dateFormat(new Date(this.createRange.value.start), 'yyyy-MM-dd'),
        orderKey: this.searchFormGroup.value.orderKey,
        orderVal: this.searchFormGroup.value.orderVal,
      }
    }
    this.BasketService.fetchGetTableList(params).subscribe(res => {
      this.dataSource = res.data.list;
      this.total = res.data.total;
    })
  }

  //获取单位列表
  getUnitList() {
    let par = {
      current: 0,
      size: 999,
      codeClId: 'UNIT0000',
      enabledSt: 1
    }
    this.DictionaryService.fetchGetTypeTableList(par).subscribe(res => {
      this.unitList = res.data.list;
      this.getList();
    })
  }

    //处理单位
    handleUnit(code) {
      let str = '';
      for (let i = 0; i < this.unitList.length; i++) {
        if (code == this.unitList[i].codeValue) {
          str = this.unitList[i].codeDsc
          return str
        }
      }
    }

  //重置搜索条件
  resetQuery() {
    // this.createRange = new FormGroup({
    //   start: new FormControl(new Date(new Date().getTime() - 2 * 24 * 3600 * 1000)),
    //   end: new FormControl(new Date(new Date().getTime() + 4 * 24 * 3600 * 1000)),
    // });
    this.searchFormGroup.value.code = '';
    this.searchFormGroup.value.chargingBasketStatus = -1;
  }

  //新增料筐
  showAddVisibilly(data) {
    let dialogRef = this._dialog.open(AddBasketComponent, {
      width: "695px",
      height: "auto",
      panelClass: 'custom-modalbox',
      data: { data: JSON.parse(JSON.stringify(data)), unitList: this.unitList }
    })
    dialogRef.afterClosed().subscribe(result => {
      if (result && result === 'refresh') {
        this.getList();
        const msg = data.type ? (data.type === 'details' ? '查看料筐' : '编辑料筐') : '新增料筐';
        this.utils.showMessage(`${msg}成功`, 'success');
      }
    });
  }

  //表格操作
  handleEvent(data, type) {
    switch (type) {
      case 'details':
        let obj = {
          type: 'details',
          data: data
        };
        this.showAddVisibilly(obj);
        break;
      case 'edit':
        obj = {
          type: 'edit',
          data: data
        };
        this.showAddVisibilly(obj);
        break;
      case 'code':
        obj = {
          type: 'code',
          data: data
        };
        this.showCodeVisibilly(obj);
        break;
    }
  }


  //询问启用 弹窗
  enterDia(value, enbled) {
    let par = {
      id: value.chargingBasketId,
      enbled: enbled,
    }
    let changeToen = "";
    if (enbled == 0) {
      changeToen = "禁用"
    } else {
      changeToen = "启用"
    }

    this.dialogService.confirm(
      `是否要${changeToen}该料筐?`,
      `确定后,该料筐将${changeToen}`,
      this.translate.instant('action.no'),
      this.translate.instant('action.yes'),
      true
    ).subscribe((res) => {
      if (res) {
        this.BasketService.fetchIsEnabled(par).subscribe(res => {
          let data = {
            title: "消息提示",
            message: `料筐已${changeToen}`,
            ok: '确定',
          }
          this.dialogService.message(data).subscribe(res => {
            this.getList();
          })
        })
      }
    }
    );
  }

  //删除
  delDia(value) {
    this.dialogService.confirm(
      `确定要删除吗？`,
      '删除该料筐后不可恢复',
      this.translate.instant('action.no'),
      this.translate.instant('action.yes'),
      true
    ).subscribe((res) => {
      if (res) {
        let params = {
          id: value.chargingBasketId
        }
        this.BasketService.fetchDelete(params).subscribe(res => {
          let data = {};
          if (res.errcode == 200) {
            data = {
              title: "消息提示",
              message: `料筐已删除`,
              ok: '确定',
            }
          } else {
            data = {
              title: "消息提示",
              message: `${res.errmsg}`,
              ok: '确定',
            }
          }

          this.dialogService.message(data).subscribe(res => {
            this.getList();
          })
        })
      }
    }
    );
  }

  //删除
  breakDia(value) {
    this.dialogService.confirm(
      `解绑二维码`,
      '确定要解绑二维码吗？',
      this.translate.instant('action.no'),
      this.translate.instant('action.yes'),
      true
    ).subscribe((res) => {
      if (res) {
        let params = {
          bindCodeNumber: value.code
        }
        this.BasketService.fetchBreakCode(params).subscribe(res => {
          let data = {};
          if (res.errcode == 200) {
            data = {
              title: "消息提示",
              message: `二维码已解绑`,
              ok: '确定',
            }
          } else {
            data = {
              title: "消息提示",
              message: `${res.errmsg}`,
              ok: '确定',
            }
          }

          this.dialogService.message(data).subscribe(res => {
            this.getList();
          })
        })
      }
    }
    );
  }

  //导出
  hadnleExportTable() {
    const params = {
      current: this.searchFormGroup.value.current,
      size: this.searchFormGroup.value.size,
      body: {
        chargingBasketStatus: this.searchFormGroup.value.chargingBasketStatus == -1 ? '' : this.searchFormGroup.value.chargingBasketStatus,
        code: this.searchFormGroup.value.code,
        // createdTimeLater: this.utils.dateFormat(new Date(this.createRange.value.end), 'yyyy-MM-dd'),
        // createdTimeFront: this.utils.dateFormat(new Date(this.createRange.value.start), 'yyyy-MM-dd'),
        orderKey: this.searchFormGroup.value.orderKey,
        orderVal: this.searchFormGroup.value.orderVal,
      }
    }
    this.BasketService.fetchExportTable(params).subscribe(res => {
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
    let dialogRef = this._dialog.open(EnterDiaComponent, {
      width: "695px",
      height: "auto",
      panelClass: 'custom-modalbox',
      data: JSON.parse(JSON.stringify(data)),
    })
    dialogRef.afterClosed().subscribe(res => {

      if (res != undefined) {
        this.BasketService.fetchImport(res).subscribe(res => {
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
              message: '导入料筐成功',
              ok: '确定',
            }
            this.dialogService.message(data).subscribe(res => {
              this.getList();
            })
          }
        })
      }
    })
  }
  //显示二维码
  showCodeVisibilly(obj) {
    let dialogRef = this._dialog.open(ShowCodeComponent, {
      width: "695px",
      height: "auto",
      panelClass: 'custom-modalbox',
      data: JSON.parse(JSON.stringify(obj))
    })

  }


  getNotices($event): any {
    // 点击paginator事件，获取pageIndex，重新加载页面
    this.searchFormGroup.value.current = $event.pageIndex;
    this.searchFormGroup.value.size = $event.pageSize;
    this.getList();
  }

}
