

import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { RecipeService } from '@app/core/http/recipe.service';
import { NavigationExtras, Router } from '@angular/router';

import { Utils } from '../order-management/w-utils';
import { MatDialog } from '@angular/material/dialog';


import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { AddRecipeComponent } from '../../components/recipe/add-recipe.component';
import { TechnologicalService } from '@app/core/http/technological.service';


@Component({
  selector: 'tb-recipe',
  templateUrl: './recipe.html',
  styleUrls: ['./recipe.scss', './common.scss']
})
export class RecipeComponent implements OnInit {

  searchData = {
    recipeName: '',
    recipeCode: '',
    status: '',
    current: 0,
    size: 50,
  }


  // 翻页参数
  length: number;
  pageSize = 50;
  pageIndex = 0;
  pageSizeOptions: number[] = [50, 100, 300, 200, 500];
  total = 0;
  tableData: any[] = [];
  dataSource = new MatTableDataSource<any>(this.tableData);

  displayedColumns: string[] = ['no', 'recipeName', 'recipeCode', 'creator', 'createTime', 'recipeDescription', 'orgName', 'status',
    'customColumn1',];

  btns = JSON.parse(localStorage.getItem('btns'));
  set = new Set(this.btns);
  status = [{
    label: '启用',
    value: '1',
  }, {
    label: '禁用',
    value: '0',
  }];
  getStatus(value) {
    let label = '';
    this.status.forEach(item => {
      if (item.value === value) {
        label = item.label;
      }
    })
    return label;
  }
  constructor(private utils: Utils,
    public fb: FormBuilder,
    private recipeService: RecipeService,
    private api: TechnologicalService,
    public router: Router,
    public dialog: MatDialog) {

  }

  ngOnInit() {
    this.searchList();
    this.searchProcessList();
  }
  resetQuery() {
    this.searchData = {
      recipeName: '',
      recipeCode: '',
      status: '',
      current: 0,
      size: 50,
    }
    this.searchList();

  }

  searchList() {
    let params = null;
    params = {
      current: this.searchData.current,
      size: this.searchData.size,
      body: {
        recipeName: this.searchData.recipeName,
        recipeCode: this.searchData.recipeCode,
        status: this.searchData.status,
      }
    }


    this.recipeService.fetchGetTableList(params).subscribe(res => {

      if (res.errcode === 200) {

        const allData = res.data || [];
        allData.list.forEach((item, index) => {
          item.no = ++index;
        })
        const MyData: any[] = allData.list;
        this.tableData = allData.list;
        this.dataSource = new MatTableDataSource<any>(MyData);
        this.total = res.data.total;
      } else {
        this.utils.showMessage(res.errmsg, 'error');
      }


    });
  }
  processList = [];
  searchProcessList() {
    const params = {
      current: 0,
      size: 9999,
      body: {
        processName: '',
        processNumber: '',
        enabled: '1',
      }
    }
    this.api.fetchGetTableList(params).subscribe(res => {
      this.processList = res.data.content;
    });
  }
  add() {

  }

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
      case 'disable':
        let params = {
          id: data.recipeId,
          isEnabled: '0',
        }
        this.recipeService.fetchDisable(params).subscribe(res => {
          if (res.errcode === 200) {
            this.utils.showMessage('禁用成功', 'success');
            this.searchList();
          } else {
            this.utils.showMessage(res.errmsg, 'error');
          }
        })
        break;
      case 'enable':
        params = {
          id: data.recipeId,
          isEnabled: '1',
        }
        this.recipeService.fetchDisable(params).subscribe(res => {
          if (res.errcode === 200) {
            this.utils.showMessage('启用成功', 'success');
            this.searchList();
          } else {
            this.utils.showMessage(res.errmsg, 'error');
          }
        })
        break;
      case 'del':
        this.utils.confirm('温馨提示', `是否确认删除?`, () => {
          this.recipeService.fetchDel(data.recipeId).subscribe(res => {
            if (res.errcode === 200) {
              this.utils.showMessage('删除成功', 'success');
              this.searchList();
            } else {
              this.utils.showMessage(res.errmsg, 'error');
            }
          })
        })
        break;
      case 'bind':
        const queryParams: NavigationExtras = {
          queryParams: {
            recipeId: data.recipeId,
          }
        }
        this.router.navigate(['/recipe-material-bind'], queryParams);
        break;
      case 'copy':
        this.utils.confirm('温馨提示', `是否确认复制?`, () => {
          this.recipeService.fetchCopy(data.recipeId).subscribe(res => {
            if (res.errcode === 200) {
              this.utils.showMessage('复制成功', 'success');
              this.searchList();
            } else {
              this.utils.showMessage(res.errmsg, 'error');
            }
          })
        })
        break;
    }
  }


  // 接单开工弹窗
  showAddVisibilly(data) {
    if (data.type) {
      this.recipeService.fetchGetDetails(data.data.recipeId).subscribe(res => {
        data.data = res.data;
        let dialogRef = this.dialog.open(AddRecipeComponent, {
          width: "1400px",
          height: "auto",
          panelClass: 'custom-modalbox',
          data: {
            data: JSON.parse(JSON.stringify(data)),
            processList: this.processList,
          }
        })
        dialogRef.afterClosed().subscribe(result => {
          if (result && result === 'refresh') {
            this.searchList();
            const msg = data.type ? (data.type === 'details' ? '查看配方' : '编辑配方') : '新增配方';
            this.utils.showMessage(`${msg}成功`, 'success');
          }
        });
      })
      return;
    }
    let dialogRef = this.dialog.open(AddRecipeComponent, {
      width: "1400px",
      height: "auto",
      panelClass: 'custom-modalbox',
      data: {
        data: JSON.parse(JSON.stringify(data)),
        processList: this.processList,
      }
    })
    dialogRef.afterClosed().subscribe(result => {
      if (result && result === 'refresh') {
        this.searchList();
        const msg = data.type ? (data.type === 'details' ? '查看配方' : '编辑配方') : '新增配方';
        this.utils.showMessage(`${msg}成功`, 'success');
      }
    });
  }


  getNotices($event): any {
    // 点击paginator事件，获取pageIndex，重新加载页面
    this.searchData.current = $event.pageIndex;
    this.searchData.size = $event.pageSize;
    this.searchList();
  }

}
