import { Component, OnInit } from '@angular/core';
import { AuthService } from '@core/auth/auth.service';
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';

import { MatDialog, MatDialogConfig } from '@angular/material/dialog';

import { DialogService } from '@app/core/public-api';
import { TranslateService } from '@ngx-translate/core';

import { DictionaryService } from '@app/core/http/dictionary.service';
import { RoleService } from '@app/core/http/role.service';
import { SpecService } from '@app/core/http/spec.servive';

import { SpecAddComponent } from '../../components/spec/spec-add.component';
import { Utils } from '../order-management/w-utils';

@Component({
  selector: 'tb-specification',
  templateUrl: './specification.component.html',
  styleUrls: ['./specification.component.scss']
})
export class SpecificationComponent implements OnInit {

  constructor(
    private authService: AuthService,
    private dialogService: DialogService,
    private DictionaryService: DictionaryService,
    private roleService: RoleService,
    private SpecService: SpecService,
    public fb: FormBuilder,
    private translate: TranslateService,
    public _dialog: MatDialog,
    private router: Router,
    private utils: Utils,
  ) { }

  //搜索参数
  searchFormGroup = this.fb.group({
    current: 0,
    size: 50,
    abrasiveSpecificationNo: "",
    abrasiveSpecificationStatus:"",
    kdDeptId: "",
    kdOrgId: "",
  });

  //翻页参数
  length: number;
  pageSize = 50;
  pageIndex = 0;
  pageSizeOptions: number[] = [50, 100, 300, 200, 500];
  total = 0;
  dataSource = [];

  //表格列参数
  displayedColumns: string[] = ["abrasiveSpecificationId",
    "abrasiveSpecificationNo", "singleGramQty", "mathByWeight",
    "mathByQty", "abrasiveSpecificationSeq",
    "kdOrgId", "kdWorkshopId",
    "abrasiveSpecificationStatus",
    "customColumn1"];

  //生产车间列表
  midDeptList = JSON.parse(localStorage.getItem('depts'));

  //生产组织列表
  midOrgList = JSON.parse(localStorage.getItem('orgs'));


  deptMap = new Map();
  orgMap = new Map();

  //车间列表
  workShop = [];


  ngOnInit(): void {
    this.setMyMap();
  }

  //创建哈希表
  setMyMap() {
    this.deptMap = this.putHash(new Map(), this.midDeptList, 'dept', 'kdDeptId');
    this.orgMap = this.putHash(new Map(), this.midOrgList, 'org', 'kdOrgId');
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
      this.getTableData();
    })
  }

  //获取组织下车间列表
  handleGetDept(orgId) {
    this.roleService.fetchGetMidDept({ params: { current: 0, size: 999 }, body: { kdOrgId: orgId } }).subscribe(res => {
      this.midDeptList = res.data.list;
      this.searchFormGroup.value.kdDeptId = res.data.list[0].midDeptId;
    })
  }

  //获取表格
  getTableData() {
    let par = {
      current: this.searchFormGroup.value.current,
      size: this.searchFormGroup.value.size,
      body: {
        abrasiveSpecificationNo: this.searchFormGroup.value.abrasiveSpecificationNo == "" ? null : this.searchFormGroup.value.abrasiveSpecificationNo,
        abrasiveSpecificationStatus: this.searchFormGroup.value.abrasiveSpecificationStatus == "" ? null : this.searchFormGroup.value.abrasiveSpecificationStatus,
        kdOrgId: this.searchFormGroup.value.kdOrgId == "" ? null : this.searchFormGroup.value.kdOrgId,
        kdDeptId: this.searchFormGroup.value.kdDeptId == "" ? null : this.searchFormGroup.value.kdDeptId,
      }
    }
    this.SpecService.fetchGetTableList(par).subscribe(res => {
      this.dataSource = res.data.list;
      this.total = res.data.total;
    })
  }

  //重置参数
  searchReset(){
    this.searchFormGroup = this.fb.group({
      current: 0,
      size: 50,
      abrasiveSpecificationNo: "",
      abrasiveSpecificationStatus:"",
      kdDeptId: "",
      kdOrgId: "",
    });
  }

  //新增磨具
  showAddVisibilly(data) {
    let _dialogRef = this._dialog.open(SpecAddComponent, {
      width: "695px",
      height: "auto",
      panelClass: 'custom-modalbox',
      data: {
        data: JSON.parse(JSON.stringify(data)),
        midDeptList: this.midDeptList,
        midOrgList: this.midOrgList,
      }
    })

    _dialogRef.afterClosed().subscribe(result => {
      if (result && result === 'refresh') {
        this.getTableData();
        const msg = data.type ? (data.type === 'details' ? '查看磨具' : '编辑磨具') : '新增磨具';
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
  enterDia(value, status) {
    let par = {
      id: value.abrasiveSpecificationId,
      status: status,
    }
    let changeToen = "";
    if (status == 0) {
      changeToen = "禁用"
    } else {
      changeToen = "启用"
    }

    this.dialogService.confirm(
      `是否要${changeToen}该磨具?`,
      `确定后,该磨具将${changeToen}`,
      this.translate.instant('action.no'),
      this.translate.instant('action.yes'),
      true
    ).subscribe((res) => {
      if (res) {
        this.SpecService.fetchIsEnabled(par).subscribe(res => {
          let data = {
            title: "消息提示",
            message: `磨具已${changeToen}`,
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
        '删除该磨具后不可恢复',
        this.translate.instant('action.no'),
        this.translate.instant('action.yes'),
        true
      ).subscribe((res) => {
        if (res) {
          let params = {
            id: value.abrasiveSpecificationId,
            version:value.version,
          }
          this.SpecService.fetchDelete(params).subscribe(res => {
            let data = {};
            if (res.errcode == 200) {
              data = {
                title: "消息提示",
                message: `磨具已删除`,
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
  


  //翻页
  getNotices($event): any {
    // 点击paginator事件，获取pageIndex，重新加载页面
    this.searchFormGroup.value.current = $event.pageIndex;
    this.searchFormGroup.value.size = $event.pageSize;
    this.getTableData();
  }
}
