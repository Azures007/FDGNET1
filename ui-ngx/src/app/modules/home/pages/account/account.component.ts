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

import { AccountService } from '@app/core/http/account.service';
import { RoleService } from '@app/core/http/role.service';

import { AccountAddDiaComponent } from '../../components/account/account-add-dia.component';
import { AccountDetailDiaComponent } from '../../components/account/account-detail-dia.component';
import { AccountResetDiaComponent } from '../../components/account/account-reset-dia.component';
import { AccountSetroleDiaComponent } from '../../components/account/account-setrole-dia.component';

@Component({
  selector: 'tb-account',
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.scss']
})
export class AccountComponent implements OnInit {

  constructor(
    protected store: Store<AppState>,
    private authService: AuthService,
    private dialogService: DialogService,
    private AccountService: AccountService,
    private RoleService: RoleService,
    public fb: FormBuilder,
    private translate: TranslateService,
    public _dialog: MatDialog,
    private router: Router
  ) { }
  //搜索参数
  searchFormGroup = this.fb.group({
    current: 0,
    size: 50,
    name: "",
    username: "",
    userStatus: "",
  });

  //翻页参数
  length: number;
  pageSize = 50;
  pageIndex = 0;
  pageSizeOptions: number[] = [50, 100, 300, 200, 500];
  total = 0;
  dataSource = [];

  btns = JSON.parse(localStorage.getItem('btns'));
  set = new Set(this.btns);

  //新增账号参数
  userData = {
    name: "",
    password: "",
    roleId: "",
    updatedName: "",
    updatedTime: "",
    userId: "",
    userStatus: "",
    username: ""
  }

  displayedColumns: string[] = ['first_name', 'username', 'role_name', 'created_time', 'created_name', 'user_status', 'customColumn1'];

  ngOnInit(): void {
    this.getTableData();
  }
  //获取表格数据
  getTableData(): void {
    let par = {
      current: this.searchFormGroup.value.current,
      size: this.searchFormGroup.value.size,
      body: {
        userStatus: this.searchFormGroup.value.userStatus,
        username: this.searchFormGroup.value.username,
        name: this.searchFormGroup.value.name,
      }
    }
    this.AccountService.fetchGetTableList(par).subscribe(res => {
      this.dataSource = res.data.list;
      this.total = res.data.total;
    })
  }

  //新增账号
  showAddVisibilly() {
    let diaref = this._dialog.open(AccountAddDiaComponent, {
      width: "800px",
      height: "auto",
      panelClass: 'custom-modalbox',
      data: JSON.parse(JSON.stringify(this.userData))
    })
    diaref.afterClosed().subscribe(res => {
      if (res != undefined) {
        let parData = res;
        this.AccountService.fetchSaveUser(parData).subscribe(res => {
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
              message: '新增账号成功',
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
  lookUser(value, isEdit): void {
    let flag = false;
    isEdit == 0 ? flag = true : flag = false;
    let dialogRef = this._dialog.open(AccountDetailDiaComponent, {
      width: "695px",
      height: "auto",
      panelClass: 'custom-modalbox',
      data: {
        isEdit: flag,
        userDetail: JSON.parse(JSON.stringify(value))
      },
    })
    dialogRef.afterClosed().subscribe(result => {
      if (result != undefined) {
        console.log(result.userDetail, '账号')
        let parData = {
          ...result.userDetail,
          tsysUserDetailList: result.tsysUserDetailList
        };
        this.AccountService.fetchSaveUser(parData).subscribe(res => {
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
              message: '保存账号成功',
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

  //询问启用 弹窗
  enterDia(value, enbled) {
    let par = {
      userId: value.user_id,
      enabled: enbled,
    }
    if (enbled == 1) {
      this.dialogService.confirm(
        this.translate.instant('role.enter-role'),
        '确定后,该账号将启用',
        this.translate.instant('action.no'),
        this.translate.instant('action.yes'),
        true
      ).subscribe((res) => {
        if (res) {
          this.AccountService.fetchIsEnabled(par).subscribe(res => {
            let data = {
              title: "消息提示",
              message: '账号已启用',
              ok: '确定',
            }
            this.dialogService.message(data).subscribe(res => {
              this.getTableData();
            })
          })
        }
      }
      );
    } else {
      this.dialogService.confirm(
        this.translate.instant('role.disable-role'),
        '确定后,该账号将禁用',
        this.translate.instant('action.no'),
        this.translate.instant('action.yes'),
        true
      ).subscribe((res) => {
        if (res) {
          this.AccountService.fetchIsEnabled(par).subscribe(res => {
            let data = {
              title: "消息提示",
              message: '账号已禁用',
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

  }

  //删除
  delDia(value) {
    this.dialogService.confirm(
      '您确定要删除账号吗？',
      '删除该账号后不可恢复！',
      this.translate.instant('action.no'),
      this.translate.instant('action.yes'),
      true
    ).subscribe((res) => {
      if (res) {
        let par = {
          userId: value.user_id,
        }
        this.AccountService.fetchDelete(par).subscribe(res => {
          let data = {
            title: "消息提示",
            message: '账号已删除',
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

  //角色配置
  showMenuDia(value) {
    let roleList = [];
    let par = {
      current: 0,
      size: 999,
      body: {
        enabled: "",
        roleCode: "",
        roleName: "",
      }
    }
    this.RoleService.fetchGetRoleTableList(par).subscribe(res => {

      roleList = res.data.list;
      let data = {
        roleId: value.role_id,
        roleList: roleList
      }
      let diaref = this._dialog.open(AccountSetroleDiaComponent, {
        width: "695px",
        height: "auto",
        panelClass: 'custom-modalbox',
        data: data
      })
      diaref.afterClosed().subscribe(res => {
        console.log(value);
        if (res != undefined) {
          let parData = {
            roleId: res,
            userId: value.user_id,
          };
          this.AccountService.fetchSetRole(parData).subscribe(res => {
            if(res.errcode == 200){
              let data = {
                title: "消息提示",
                message: '设置角色成功',
                ok: '确定',
              }
              this.dialogService.message(data).subscribe(res => {
                this.getTableData();
              })
            }else{
              let data = {
                title: "消息提示",
                message: res.errmsg,
                ok: '确定',
              }
              this.dialogService.message(data).subscribe(res => {
                this.showMenuDia(value);
              })
            }

          })
        }
      })
    })
  }

  //重置密码
  resetPwd(value) {
    let data = {
      password: "",
      checkWord: ""
    }
    let diaref = this._dialog.open(AccountResetDiaComponent, {
      width: "695px",
      height: "auto",
      panelClass: 'custom-modalbox',
      data: data
    })
    diaref.afterClosed().subscribe(res => {
      this.searchFormGroup.value.username = "";
      console.log(this.searchFormGroup.value.username)
      if (res != undefined) {
        let parData = {
          userId: value.user_id,
          password: res.password,
        };
        this.AccountService.fetchReset(parData).subscribe(res => {
          let data = {
            title: "消息提示",
            message: '重置密码成功',
            ok: '确定',
          }
          this.dialogService.message(data).subscribe(res => {
            this.getTableData();
          })
        })
      }
    })
  }



  getNotices($event): any {
    // 点击paginator事件，获取pageIndex，重新加载页面
    this.searchFormGroup.value.current = $event.pageIndex;
    this.searchFormGroup.value.size = $event.pageSize;
    this.getTableData();
  }
}
