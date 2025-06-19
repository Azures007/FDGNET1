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
import { AddRoleDialogComponent } from '@home/components/role/add-role-dialog.component';
import { LookRoleDialogComponent } from '@home/components/role/look-role-dialog.component';
import { MenuPackageDialogComponent } from '@home/components/role/menu-package-dialog.component';
import { DataPackageDialogComponent } from '@home/components/role/data-package-dialog.component';
import { RoleAdminComponent } from '@home/components/role/role-admin.component';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { result } from 'lodash';
import { RoleService } from '@app/core/http/role.service';
import { AlertDialogComponent } from '@shared/components/dialog/alert-dialog.component';
import { DialogService } from '@app/core/public-api';
import { TranslateService } from '@ngx-translate/core';

export interface PeriodicElement {
  name: string;
  position: number;
  weight: number;
  symbol: string;
  createTime: string;
  createPerson: string;
  status: number;
}


@Component({
  selector: 'tb-role',
  templateUrl: './role.component.html',
  styleUrls: ['./role.component.scss']
})
export class RoleComponent implements OnInit {

  //搜索参数
  searchFormGroup = this.fb.group({
    current: 0,
    size: 50,
    roleName: "",
    roleCode: "",
    enabled: "",
  });

  //新增角色参数
  roleData = {
    byFactory: "",
    byGroup: "",
    createdName: "",
    createdTime: "",
    enabled: "0",
    roleCode: "",
    roleExplain: "",
    roleId: "",
    roleName: "",
    updatedName: "",
    updatedTime: ""
  }
  //角色弹窗
  dialogRef: any

  //翻页参数
  length: number;
  pageSize = 50;
  pageIndex = 0;
  pageSizeOptions: number[] = [50, 100, 300,200,500];
  total = 0;
  // pageEvent: PageEvent;

  //table
  displayedColumns: string[] = ['roleId', 'roleName', 'roleCode', 'roleExplain','createTime', 'createdName',  'enabled', 'customColumn1'];

  // @ViewChild(MatPaginator) paginator: MatPaginator;

  dataSource = [];

  btns = JSON.parse(localStorage.getItem('btns'));
  set = new Set(this.btns);

  oauth2Clients: Array<OAuth2ClientInfo> = null;
  constructor(protected store: Store<AppState>,
    private authService: AuthService,
    private RoleService: RoleService,
    private dialogService: DialogService,
    public fb: FormBuilder,
    private translate: TranslateService,
    public _dialog: MatDialog,
    private router: Router) { }

  ngOnInit(): void {
    this.getTableData();
  }

  //获取表格数据
  getTableData(): void {
    let par = {
      current: this.searchFormGroup.value.current,
      size: this.searchFormGroup.value.size,
      body:{
        enabled:this.searchFormGroup.value.enabled,
        roleCode:this.searchFormGroup.value.roleCode,
        roleName:this.searchFormGroup.value.roleName,
      }
    }
    this.RoleService.fetchGetRoleTableList(par).subscribe(res => {
      this.dataSource = res.data.list;
      this.total = res.data.total;
    })
  }

  //显示新增角色弹窗
  showAddVisibilly(): void {
    let dialogRef = this._dialog.open(AddRoleDialogComponent, {
      width: "695px",
      height: "auto",
      panelClass: 'custom-modalbox',
      data: this.roleData,
    })
    dialogRef.afterClosed().subscribe(result => {
      if (result != undefined) {
        let parData = result;
        this.RoleService.fetchSaveRole(parData).subscribe(res => {
          let data = {
            title: "消息提示",
            message: '新增角色成功',
            ok: '确定',
          }
          this.messageDia(data);
        })
      }
    })
  }

  //显示编辑角色弹窗
  showEditVisibilly(value): void {
    console.log(value)
  }

  //查看角色弹窗
  lookRole(value, isEdit): void {
    let flag = false;
    isEdit == 0 ? flag = true : flag = false;
    let dialogRef = this._dialog.open(LookRoleDialogComponent, {
      width: "695px",
      height: "auto",
      panelClass: 'custom-modalbox',
      data: {
        isEdit: flag,
        roleDetail: JSON.parse(JSON.stringify(value)) 
      },
    })
    dialogRef.afterClosed().subscribe(result => {
      if (result != undefined) {
        let parData = result.roleDetail;
        this.RoleService.fetchSaveRole(parData).subscribe(res => {
          let data = {
            title: "消息提示",
            message: '角色保存成功',
            ok: '确定',
          }
          this.messageDia(data);
        })
      }
    })
  }

  //数据权限配置
  showDataPageDia(value) {
    let diaref = this._dialog.open(RoleAdminComponent, {
      width: "695px",
      height: "auto",
      panelClass: 'custom-modalbox',
      data: value
    })
    console.log(value)
    diaref.afterClosed().subscribe(res=>{
      if(res){
        res.byFactory == true ? res.byFactory = 0 : res.byFactory = 1;
        res.byGroup == true ? res.byGroup = 0 : res.byGroup = 1;
        let parData = res;
        this.RoleService.fetchSaveRole(parData).subscribe(res => {
          let data = {
            title: "消息提示",
            message: '保存权限成功,重新登录后生效',
            ok: '确定',
          }
          this.messageDia(data);
        })
      }
    })
  }

  //菜单配置弹窗
  showMenuDia(value): void {
    let menuData = null;
    let dialogRef = null;
    this.RoleService.fetchMenuPackRole(value.roleId).subscribe(res => {

      dialogRef = this._dialog.open(MenuPackageDialogComponent, {
        width: "695px",
        height: "auto",
        panelClass: 'custom-modalbox',
        data: res.data,
      })
      dialogRef.afterClosed().subscribe(result => {
        if (result != undefined) {
          let data = {
            roleId: value.roleId,
            menuListVo: result
          }
          this.RoleService.fetchSaveRoleMenu(data).subscribe(res => {
            let data = {
              title: "消息提示",
              message: '保存角色菜单配置成功',
              ok: '确定',
            }
            this.messageDia(data);
          })
        }
      })
    })
  }

  //询问启用 弹窗
  enterDia(value, enbled) {
    if (enbled == 0) {
      this.dialogService.confirm(
        this.translate.instant('role.enter-role'),
        '确定后,该角色将启用',
        this.translate.instant('action.no'),
        this.translate.instant('action.yes'),
        true
      ).subscribe((res) => {
        if (res) {
          let par = {
            roleId: value.roleId,
            enabled: enbled
          }
          this.RoleService.fetchIsEnabledRole(par).subscribe(res => {
            let data = {
              title: "消息提示",
              message: '角色已启用',
              ok: '确定',
            }
            this.messageDia(data);
          })
        }
      }
      );
    } else {
      this.dialogService.confirm(
        this.translate.instant('role.disable-role'),
        '确定后,该角色将禁用',
        this.translate.instant('action.no'),
        this.translate.instant('action.yes'),
        true
      ).subscribe((res) => {
        if (res) {
          let par = {
            roleId: value.roleId,
            enabled: enbled
          }
          this.RoleService.fetchIsEnabledRole(par).subscribe(res => {
            let data = {
              title: "消息提示",
              message: '角色已禁用',
              ok: '确定',
            }
            this.messageDia(data);
          })
        }
      }
      );
    }

  }

  //删除角色弹窗
  delDia(value) {
    this.dialogService.confirm(
      this.translate.instant('role.delete-role'),
      '删除该角色后不可恢复',
      this.translate.instant('action.no'),
      this.translate.instant('action.yes'),
      true
    ).subscribe((res) => {
      if (res) {
        let par = {
          roleId: value.roleId,
        }
        this.RoleService.fetchDeleteRole(par).subscribe(res => {
          let data = {
            title: "消息提示",
            message: '角色已删除',
            ok: '确定',
          }
          this.messageDia(data);
        })
      }
    }
    );
  }


  //吐司弹窗
  messageDia(data) {
    const dialogConfig: MatDialogConfig = {
      disableClose: true,
      data: data
    };
    let mydia = this._dialog.open(AlertDialogComponent, dialogConfig);
    mydia.afterClosed().subscribe(res => {
      this.getTableData();
    })
  }


  //翻页事件
  getNotices($event): any {
    // 点击paginator事件，获取pageIndex，重新加载页面
    this.searchFormGroup.value.current= $event.pageIndex;
    this.searchFormGroup.value.size = $event.pageSize;
    this.getTableData();
  }
}
