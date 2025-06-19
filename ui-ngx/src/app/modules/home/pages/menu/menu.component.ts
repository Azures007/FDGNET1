import { Component, OnInit } from '@angular/core';
import { AuthService } from '@core/auth/auth.service';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { FormBuilder } from '@angular/forms';

import { NestedTreeControl } from '@angular/cdk/tree';
import { MatTreeNestedDataSource } from '@angular/material/tree';

import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { DialogService } from '@app/core/public-api';
import { TranslateService } from '@ngx-translate/core';
import { Utils } from '../order-management/w-utils';

import { MenuAddComponent } from '../../components/menu-admin/menu-add.component';
import { MenuAdminService } from '@app/core/http/menuAdmin.service';


interface FoodNode {
  menuName: string;
  tsysMenus?: FoodNode[];
  menuType: string
}

@Component({
  selector: 'tb-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.scss']
})
export class MenuComponent implements OnInit {

  constructor(
    protected store: Store<AppState>,
    private authService: AuthService,
    private dialogService: DialogService,
    private MenuAdminService: MenuAdminService,
    public fb: FormBuilder,
    private utils: Utils,
    private translate: TranslateService,
    public _dialog: MatDialog,
  ) { }

  //搜索参数
  searchFormGroup = this.fb.group({
    current: 0,
    size: 50,
  });

  //翻页参数
  length: number;
  pageSize = 50;
  pageIndex = 1;
  pageSizeOptions: number[] = [50, 100, 300, 200, 500];
  total = 0;

  treeControl = new NestedTreeControl<FoodNode>(node => node.tsysMenus);
  dataSource = new MatTreeNestedDataSource<FoodNode>();

  //左侧树形数据
  treeDataSource = [];

  //类型类表
  typeList = [
    '菜单', '功能按钮', '外链URL'
  ];

  //右侧表格数据
  tableDataSource = [];
  displayedColumns: string[] = ['menuId', 'menuName', 'parentId', 'flag', 'path', 'createdTime', 'sort', 'br', 'enabled', 'customColumn1']

  ngOnInit(): void {
    this.getTreeData();
  }

  hasChild = (_: number, node: FoodNode) => !!node.tsysMenus && (node.tsysMenus.length > 0 && node.tsysMenus[0].menuType != null);

  //获取左侧树形数据
  getTreeData() {
    this.MenuAdminService.fetchGetTree().subscribe(res => {
      let arr = [];
      arr.push(res.data);
      // const TREE_DATA:FoodNode[] = arr;
      this.dataSource.data = arr;
      console.log(this.dataSource);
      this.getTableData();
    })
  }

  //获取右侧表格数据
  getTableData() {
    let par = {
      current: this.searchFormGroup.value.current,
      size: this.searchFormGroup.value.size,
      body: {

      }
    }
    this.MenuAdminService.fetchGetTable(par).subscribe(res => {
      this.tableDataSource = res.data.list;
      this.total = res.data.total;
    })
  }

  //显示类型
  handleUrlType() { }

  //新增菜单
  handSaveAdd() {

  }


  showAddVisibilly(data) {
    let dialogRef = this._dialog.open(MenuAddComponent, {
      width: "695px",
      height: "auto",
      panelClass: 'custom-modalbox',
      data: { data: JSON.parse(JSON.stringify(data)), }
    })
    dialogRef.afterClosed().subscribe(result => {
      if (result && result === 'refresh') {
        this.getTreeData();
        const msg = data.type ? (data.type === 'details' ? '查看菜单' : '编辑菜单') : '新增菜单';
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



  //状态
  enterDia(value, enbled) {
    let par = {
      id: value.menuId,
      enbled: enbled,
    }
    let changeToen = "";
    if (enbled == 0) {
      changeToen = "禁用"
    } else {
      changeToen = "启用"
    }

    this.dialogService.confirm(
      `是否要${changeToen}该菜单?`,
      `确定后,该菜单将${changeToen}`,
      this.translate.instant('action.no'),
      this.translate.instant('action.yes'),
      true
    ).subscribe((res) => {
      if (res) {
        this.MenuAdminService.fetchIsEnabled(par).subscribe(res => {
          let data = {
            title: "消息提示",
            message: `菜单已${changeToen}`,
            ok: '确定',
          }
          this.dialogService.message(data).subscribe(res => {
            this.getTreeData();
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
      '删除该菜单后不可恢复',
      this.translate.instant('action.no'),
      this.translate.instant('action.yes'),
      true
    ).subscribe((res) => {
      if (res) {
        let params = {
          id: value.menuId
        }
        this.MenuAdminService.fetchDelete(params).subscribe(res => {
          let data = {};
          if (res.errcode == 200) {
            data = {
              title: "消息提示",
              message: `菜单已删除`,
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
            this.getTreeData();
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
