import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { AuthService } from '@core/auth/auth.service';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { PageComponent } from '@shared/components/page.component';
import { FormBuilder } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Constants } from '@shared/models/constants';
import { Router, NavigationExtras } from '@angular/router';
import { OAuth2ClientInfo } from '@shared/models/oauth2.models';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';

import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { result } from 'lodash';

import { AlertDialogComponent } from '@shared/components/dialog/alert-dialog.component';
import { DialogService } from '@app/core/public-api';
import { TranslateService } from '@ngx-translate/core';
import { RoleService } from '@app/core/http/role.service';

import { ClassService } from '@app/core/http/class.service';
import { DictionaryService } from '@app/core/http/dictionary.service';

import { ClassAddComponent } from '../../components/class/class-add.component';
import { TechnologicalService } from '@app/core/http/technological.service';
import { AccountService } from '@app/core/http/account.service';


@Component({
  selector: 'tb-class',
  templateUrl: './class.component.html',
  styleUrls: ['./class.component.scss']
})
export class ClassComponent implements OnInit {

  constructor(
    private accountService: AccountService,
    private authService: AuthService,
    private dialogService: DialogService,
    private ClassService: ClassService,
    private DictionaryService: DictionaryService,
    public fb: FormBuilder,
    private translate: TranslateService,
    public _dialog: MatDialog,
    private router: Router,
    private roleService: RoleService,
    private technologicalService: TechnologicalService,
  ) { }

  //搜索参数
  searchFormGroup = this.fb.group({
    current: 0,
    size: 50,
    name: null,
    enabledSt: '',
    classNumber: "",
  });

  //翻页参数
  length: number;
  pageSize = 50;
  pageIndex = 0;
  pageSizeOptions: number[] = [50, 100, 300, 200, 500];
  total = 0;
  dataSource = [];

  //表格列参数
  displayedColumns: string[] = ['name', 'classNumber', 'groupLeader', 'teamNum', 'scheduling', 'belongProcessId', 'pkOrg', 'enabledSt', 'customColumn1']


  //新增班组参数
  addParams = {
    tSysClass: {
      classId: "",
      enabledSt: "1",
      groupLeader: "",
      groupLeaderID: "",
      name: "",
      process: "",
      scheduling: "",
      teamNum: "",
      belongProcessId: "",//工序
      kdDeptId: "",//生产车间
      kdOrgId: "",//生产组织
      workshopDirector: "",//车间主任
      workshopDirectorId: "",//车间主任id
    },
    tsysClassGroupLeaderRelLits: []
  }

  btns = JSON.parse(localStorage.getItem('btns'));
  set = new Set(this.btns);

  //排班列表
  schedulingList = [];

  //erp班别列表
  erpClassList = [];


  midDeptList = [];
  midOrgList = [];
  processList = [];
  pkOrgList = [];
  //生产车间列表
  // midDeptList = localStorage.getItem('depts') ? JSON.parse(localStorage.getItem('depts')) : [];

  //生产组织列表
  // midOrgList = localStorage.getItem('orgs') ? JSON.parse(localStorage.getItem('orgs')) : [];

  //工序列表
  // processList = localStorage.getItem('process') ? JSON.parse(localStorage.getItem('process')) : [];

  deptMap = new Map();
  orgMap = new Map();
  processMap = new Map();
  erpClassMap = new Map();

  ngOnInit(): void {
    this.getStationList()
    // 获取用户当前选择的基地
    this.authService.getCurrentLine().subscribe((res: any) => {
      if (res.data && res.data.pkOrg) {
        // 只显示用户当前选择的基地
        const currentOrgId = res.data.pkOrg;
        this.accountService.getOrgList().subscribe(orgRes => {
          // 过滤出用户当前选择的基地
          const currentOrg = orgRes.data.find(item => item.pk_org === currentOrgId);
          if (currentOrg) {
            this.pkOrgList = [{
              name: currentOrg.org_name,
              id: currentOrg.pk_org,
            }];
          }
        });
      } else {
        // 如果没有当前选择的基地，则获取所有基地（回退方案）
        this.accountService.getOrgList().subscribe(res => {
          this.pkOrgList = res.data.map(item => {
            return {
              name: item.org_name,
              id: item.pk_org,
            }
          });
        });
      }
    });
  }
  getOrgName(id) {
    let name = '';
    this.pkOrgList.forEach(item => {
      if(item.id == id) {
        name = item.name;
      }
    })
    return name;
  }
  //创建哈希表
  setMyMap() {
    this.processMap = this.putHash(new Map(), this.processList, 'process', 'processId');
    this.deptMap = this.putHash(new Map(), this.midDeptList, 'dept', 'kdDeptId');
    this.orgMap = this.putHash(new Map(), this.midOrgList, 'org', 'kdOrgId');
    this.erpClassMap = this.putHash(new Map(), this.erpClassList, 'erpclass', 'codeId');
    this.getTableData();
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
        case 3:
          this.erpClassMap.get(`erpclass-${value}`) ? str = JSON.parse(this.erpClassMap.get(`erpclass-${value}`)).codeDsc : ""
          return str;
      }
    } else {
      return str = "未绑定";
    }

  }

  //获取表格数据
  getTableData(): void {
    let storagePar = localStorage.getItem('storagePar');
    let par = null;
    if (storagePar) {
      let storageParObj = JSON.parse(storagePar);
      for (let key in this.searchFormGroup.value) {
        this.searchFormGroup.value[key] = storageParObj[key];
      }

    } else {

    }
    par = {
      current: this.searchFormGroup.value.current,
      size: this.searchFormGroup.value.size,
      body: {
        name: this.searchFormGroup.value.name == "" ? null : this.searchFormGroup.value.name,
        enabledSt: this.searchFormGroup.value.enabledSt == "" ? null : this.searchFormGroup.value.enabledSt,
        classNumber: this.searchFormGroup.value.classNumber == "" ? null : this.searchFormGroup.value.classNumber,
      }
    }
    this.ClassService.fetchGetTableList(par).subscribe(res => {
      this.dataSource = res.data.list;
      this.total = res.data.total;
      localStorage.removeItem('storagePar')
    })
  }

  //导出
  hadnleExportTable() {
    let par = {
      current: this.searchFormGroup.value.current,
      size: 999,
      body: {
        name: this.searchFormGroup.value.name == "" ? null : this.searchFormGroup.value.name,
        enabledSt: this.searchFormGroup.value.enabledSt == "" ? null : this.searchFormGroup.value.enabledSt,
        classNumber: this.searchFormGroup.value.classNumber == "" ? null : this.searchFormGroup.value.classNumber,
      }
    }
    this.ClassService.fetchExportTable(par).subscribe(res => {
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


  //获取岗位列表
  getStationList() {
    let par_1 = {
      current: 0,
      size: 999,
      codeClId: 'ERPCLASS0000',
      enabledSt: 1
    }
    this.DictionaryService.fetchGetTypeTableList(par_1).subscribe(res => {
      this.erpClassList = res.data.list;
      this.roleService.fetchGetMid({ params: { current: 0, size: 999 }, body: {} }).subscribe(res1 => {
        this.midOrgList = res1.data.list;
        this.roleService.fetchGetMidDept({ params: { current: 0, size: 999 }, body: {} }).subscribe(res2 => {
          this.midDeptList = res2.data.list;
          this.technologicalService.fetchGetTableList({ current: 0, size: 999 }).subscribe(res3 => {
            this.processList = res3.data.content;
            let par_2 = {
              current: 0,
              size: 999,
              codeClId: 'SCHEDULING0000',
              enabledSt: 1
            }
            this.DictionaryService.fetchGetTypeTableList(par_2).subscribe(res => {
              this.schedulingList = res.data.list;
              this.setMyMap();
            })
          });
        })
      })
    })
  }

  handleScheduing(code) {
    let str = ""
    for (let i = 0; i < this.schedulingList.length; i++) {
      if (code == this.schedulingList[i].codeValue) {
        str = this.schedulingList[i].codeDsc;
        return str;
      }
    }
  }

  //新增
  showAddVisibilly() {
    let data = {
      params: JSON.parse(JSON.stringify(this.addParams)),
      edit: true,
      schedulingList: this.schedulingList,
      midDeptList: this.midDeptList,
      midOrgList: this.midOrgList,
      processList: this.processList,
      erpClassList: this.erpClassList,
      pkOrgList: this.pkOrgList,
    }
    let diaref = this._dialog.open(ClassAddComponent, {
      width: "695px",
      height: "auto",
      panelClass: 'custom-modalbox',
      data: JSON.parse(JSON.stringify(data))
    })

    diaref.afterClosed().subscribe(res => {
      if (res) {
        let data = {
          title: "消息提示",
          message: '新增班组成功',
          ok: '确定',
        }
        this.dialogService.message(data).subscribe(res => {
          this.getTableData();
        })
      }
    })
  }

  //查看和编辑
  lookDia(value, n) {
    let editParams = JSON.parse(JSON.stringify(this.addParams));
    editParams.tSysClass = JSON.parse(JSON.stringify(value));
    let par = {
      classId: value.classId
    }
    this.ClassService.fetchGetTeamLeaderList(par).subscribe(res => {
      editParams.tsysClassGroupLeaderRelLits = res.data;
      let data = {
        params: editParams,
        edit: n == 0 ? true : false,
        schedulingList: this.schedulingList,
        midDeptList: this.midDeptList,
        midOrgList: this.midOrgList,
        processList: this.processList,
        erpClassList: this.erpClassList,
        pkOrgList: this.pkOrgList,
      }

      this.roleService.fetchGetMidDept({ params: { current: 0, size: 999 }, body: { kdOrgId: data.params.tSysClass.kdOrgId } }).subscribe(res => {
        data.midDeptList = res.data.list;
        console.log(data);

        let diaref = this._dialog.open(ClassAddComponent, {
          width: "695px",
          height: "auto",
          panelClass: 'custom-modalbox',
          data: JSON.parse(JSON.stringify(data))
        })

        diaref.afterClosed().subscribe(res => {
          if (res) {
            if (res == 'add') {
              let data = {
                title: "消息提示",
                message: "新增班组成功",
                ok: '确定',
              }
              this.dialogService.message(data).subscribe(res => {
                this.getTableData();
              })
            } else {
              let data = {
                title: "消息提示",
                message: '编辑班组成功',
                ok: '确定',
              }
              this.dialogService.message(data).subscribe(res => {
                this.getTableData();
              })
            }
          }
        })
      })


    })


  }


  //询问启用
  enterDia(value, enabledSt) {
    let par = {
      classId: value.classId,
      enabledSt: enabledSt,
    }
    let changeToen = "";
    if (enabledSt == 1) {
      changeToen = "启用"
    } else {
      changeToen = "禁用"
    }

    this.dialogService.confirm(
      `是否要${changeToen}该班组?`,
      `确定后,该班组将${changeToen}`,
      this.translate.instant('action.no'),
      this.translate.instant('action.yes'),
      true
    ).subscribe((res) => {
      if (res) {
        this.ClassService.fetchIsEnabled(par).subscribe(res => {
          let data = {
            title: "消息提示",
            message: `班组已${changeToen}`,
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
      `确定要删除班组吗？`,
      '删除该班组后不可恢复',
      this.translate.instant('action.no'),
      this.translate.instant('action.yes'),
      true
    ).subscribe((res) => {
      if (res) {
        let params = {
          classId: value.classId
        }
        this.ClassService.fetchDelete(params).subscribe(res => {
          let data = {};
          if (res.errcode == 200) {
            data = {
              title: "消息提示",
              message: `班别已删除`,
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


  //组员管理
  goTeamAdmin(e) {
    let queryParams: NavigationExtras = {
      queryParams: {
        name: e.name,
        classId: e.classId
      }
    }
    localStorage.setItem('storagePar', JSON.stringify(this.searchFormGroup.value));
    this.router.navigate(['/team-admin'], queryParams);
  }

  //翻页
  getNotices($event): any {
    // 点击paginator事件，获取pageIndex，重新加载页面
    this.searchFormGroup.value.current = $event.pageIndex;
    this.searchFormGroup.value.size = $event.pageSize;
    this.getTableData();
  }

  getNum(str) {
    let n = parseInt(str);
    return n;
  }

  getTeamTotal(element) {
    let leaderNum = 0;
    let total = 0;
    if (element.groupLeader != "") {
      leaderNum = element.groupLeader.split(',').length;
      element.teamNum == "" ? total = 0 : total = this.getNum(element.teamNum);
    } else {
      element.teamNum == "" ? total = 0 : total = this.getNum(element.teamNum);
    }
    return total;
  }
  reset() {
    this.searchFormGroup.value.name = null;
    this.searchFormGroup.value.enabledSt = '';
    this.searchFormGroup.value.classNumber = '';
    this.getTableData();
  }
}
