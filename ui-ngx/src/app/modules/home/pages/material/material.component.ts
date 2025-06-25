import { Component, OnInit } from '@angular/core';
import { AuthService } from '@core/auth/auth.service';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';

import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { DialogService } from '@app/core/public-api';
import { TranslateService } from '@ngx-translate/core';

import { MaterialService } from '@app/core/http/material.service';
import { DictionaryService } from '@app/core/http/dictionary.service';

import { AddMaterialComponent } from '../../components/material/add-material.component';
import { Utils } from '../order-management/w-utils';

@Component({
  selector: 'tb-material',
  templateUrl: './material.component.html',
  styleUrls: ['./material.component.scss']
})
export class MaterialComponent implements OnInit {

  constructor(
    protected store: Store<AppState>,
    private dialogService: DialogService,
    private MaterialService: MaterialService,
    private DictionaryService: DictionaryService,
    public fb: FormBuilder,
    private translate: TranslateService,
    public _dialog: MatDialog,
    private utils: Utils,
  ) { }

  //搜索参数
  searchFormGroup = this.fb.group({
    current: 0,
    size: 50,
    materialStatus: "",
    materialCode: "",
    materialName: '',
    ncMaterialCategory: ''
  });

  //翻页参数
  length: number;
  pageSize = 50;
  pageIndex = 0;
  pageSizeOptions: number[] = [50, 100, 300, 200, 500];
  total = 0;
  dataSource = [];
  allUnits = [];

  //新增物料参数
  addParams = {
    br: "",
    groupCode: "",
    materialCode: "",
    materialModel: "",
    materialName: "",
    materialStatus: "",
    materialUnit: "",
  }

  btns = JSON.parse(localStorage.getItem('btns'));
  set = new Set(this.btns);

  //生产车间列表
  midDeptList = JSON.parse(localStorage.getItem('depts'));

  //生产组织列表
  midOrgList = JSON.parse(localStorage.getItem('orgs'));


  deptMap = new Map();
  orgMap = new Map();

  //车间列表
  workShop = [];


  displayedColumns: string[] = ['ncMaterialCategory', 'ncMaterialMainCategory', 'ncMaterialClassification', 'materialCode', 'materialName', 'materialModel', 'materialUnit', 'ncMaterialQualityNum', 'materialStatus', 'customColumn1'];

  ngOnInit(): void {
    this.setMyMap();
  }

  //创建哈希表
  setMyMap() {
    this.deptMap = this.putHash(new Map(), this.midDeptList, 'dept', 'kdDeptId');
    this.orgMap = this.putHash(new Map(), this.midOrgList, 'org', 'kdOrgId');
    console.log(this.orgMap, 'orgmap')
    this.getWorkShopList()
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
      this.getUnits();
    })
  }

  //获取表格数据
  getTableData(): void {
    let par = {
      current: this.searchFormGroup.value.current,
      size: this.searchFormGroup.value.size,
      body: {
        materialName: this.searchFormGroup.value.materialName,
        ncMaterialCategory: this.searchFormGroup.value.ncMaterialCategory,
        materialStatus: this.searchFormGroup.value.materialStatus,
        materialCode: this.searchFormGroup.value.materialCode,
      }
    }
    this.MaterialService.fetchGetTableList(par).subscribe(res => {
      this.dataSource = res.data.list;
      this.total = res.data.total;
    })
  }

  //获取单位列表
  getUnits() {
    let par = {
      current: 0,
      size: 999,
      codeClId: 'UNIT0000',
      enabledSt: 1
    }
    this.DictionaryService.fetchGetTypeTableList(par).subscribe(res => {
      this.allUnits = res.data.list;
      this.getTableData();
    })
  }

  //处理单位
  handleUnit(code) {
    let str = '';
    for (let i = 0; i < this.allUnits.length; i++) {
      if (code == this.allUnits[i].codeValue) {
        str = this.allUnits[i].codeDsc
        return str
      }
    }
  }
  handleQualityUnit(unit) {
    let str = '';
    if (unit == '2') {
      str = '日'
    }
    if (unit == '1') {
      str = '月'
    }
    if (unit == '0') {
      str = '年'
    }
    return str
  }
  //新增物料
  showAddVisibilly(data) {
    if (data.type) {
      this.MaterialService.fetchGetDetails(data.data.id).subscribe(res => {
        data.data = res.data;
        let dialogRef = this._dialog.open(AddMaterialComponent, {
          width: "950px",
          height: "auto",
          panelClass: 'custom-modalbox',
          data: {
            data: JSON.parse(JSON.stringify(data)),
            midDeptList: this.midDeptList,
            midOrgList: this.midOrgList,
          }
        })
        dialogRef.afterClosed().subscribe(result => {
          if (result && result === 'refresh') {
            this.getTableData();
            const msg = data.type ? (data.type === 'details' ? '查看物料' : '编辑物料') : '新增物料';
            this.utils.showMessage(`${msg}成功`, 'success');
          }
        });
      })
      return;
    }
    let dialogRef = this._dialog.open(AddMaterialComponent, {
      width: "950px",
      height: "auto",
      panelClass: 'custom-modalbox',
      data: {
        data: JSON.parse(JSON.stringify(data)),
        midDeptList: this.midDeptList,
        midOrgList: this.midOrgList,
      }
    })
    dialogRef.afterClosed().subscribe(result => {
      if (result && result === 'refresh') {
        this.getTableData();
        const msg = data.type ? (data.type === 'details' ? '查看物料' : '编辑物料') : '新增物料';
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

    }
  }



  //询问启用 弹窗
  enterDia(value, enbled) {
    let par = {
      id: value.id,
      enbled: enbled,
    }
    let changeToen = "";
    if (enbled == 0) {
      changeToen = "禁用"
    } else {
      changeToen = "启用"
    }

    this.dialogService.confirm(
      `是否要${changeToen}该物料?`,
      `确定后,该物料将${changeToen}`,
      this.translate.instant('action.no'),
      this.translate.instant('action.yes'),
      true
    ).subscribe((res) => {
      if (res) {
        this.MaterialService.fetchIsEnabled(par).subscribe(res => {
          let data = {
            title: "消息提示",
            message: `物料已${changeToen}`,
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
      `确定要删除吗？`,
      '删除该物料后不可恢复',
      this.translate.instant('action.no'),
      this.translate.instant('action.yes'),
      true
    ).subscribe((res) => {
      if (res) {
        let params = {
          id: value.id
        }
        this.MaterialService.fetchDelete(params).subscribe(res => {
          let data = {};
          if (res.errcode == 200) {
            data = {
              title: "消息提示",
              message: `物料已删除`,
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
            this.getTableData();
          })
        })
      }
    }
    );
  }

  //同步
  showSync() {
    this.dialogService.confirm(
      `确认操作`,
      '确定要同步物料数据吗?',
      this.translate.instant('action.no'),
      this.translate.instant('action.yes'),
      true
    ).subscribe((res) => {
      if (res) {
        let params = {
        }
        this.MaterialService.fetchSync(params).subscribe(res => {
          let data = {};
          if (res.errcode == 200) {
            data = {
              title: "消息提示",
              message: `物料数据已同步`,
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
            this.getTableData();
          })
        })
      }
    }
    );
  }

  getNotices($event): any {
    // 点击paginator事件，获取pageIndex，重新加载页面
    this.searchFormGroup.value.current = $event.pageIndex;
    this.searchFormGroup.value.size = $event.pageSize;
    this.getTableData();
  }

}
