

import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, FormBuilder } from '@angular/forms';

import { Router, NavigationExtras } from '@angular/router';
import { Utils } from '../order-management/w-utils';
import { AddProcessRouteComponent } from '@home/pages/technological-process/dialog/add.process-route.component';
import { MatDialog } from '@angular/material/dialog';
import { TechnologicalService } from '@app/core/http/technological.service';
@Component({
  selector: 'tb-process-route',
  templateUrl: './process-route.html',
  styleUrls: ['./process-route.scss', './technological-process.scss']
})
export class ProcessRouteComponent implements OnInit {



  orderSelect = [
    { value: '', label: '全部' },
    { value: 1, label: '启用' },
    { value: 0, label: '禁用' },
  ];

  // 翻页参数
  length: number;
  pageSize = 50;
  pageIndex = 0;
  pageSizeOptions: number[] = [50, 100, 300, 200, 500];
  total = 0;
  dataSource = [];
    //生产车间列表
    midDeptList = JSON.parse(localStorage.getItem('depts'));

    //生产组织列表
    midOrgList = JSON.parse(localStorage.getItem('orgs'));

  edit = false;
  displayedColumns: string[] = ['no', 'craftName', 'craftNumber', 'effectiveTime', 'failureTime','kdOrgId','kdDeptId', 'craftDetail', 'createdTime', 'enabled', 'customColumn1'];

  // 搜索参数
  searchFormGroup = this.fb.group({
    current: 0,
    size: 50,
    craftName: '',
    craftNumber: '',
    enabled: '',
  });

  clickedRows = new Set();

  btns = JSON.parse(localStorage.getItem('btns'));
  set = new Set(this.btns);

  constructor(
    private utils: Utils,
    private router: Router,
    public fb: FormBuilder,
    public dialog: MatDialog, private api: TechnologicalService) {

  }
  deptMap = new Map();
  orgMap = new Map();

  ngOnInit() {
    this.setMyMap();
    // console.log()
  }

    //创建哈希表
    setMyMap() {
      this.deptMap = this.putHash(new Map(), this.midDeptList, 'dept', 'kdDeptId');
      this.orgMap = this.putHash(new Map(), this.midOrgList, 'org', 'kdOrgId');
      this.searchList();
    }
  
    //哈希表存储
    putHash(map, arr, str, key) {
      for (let i = 0; i < arr.length; i++) {
        map.set(`${str}-${arr[i][key]}`, JSON.stringify(arr[i]));
      }
      // console.log(arr[0],str,key);
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

  // 新增弹窗
  showAddVisibilly(data): void {
    const dialogRef = this.dialog.open(AddProcessRouteComponent, {
      width: '950px',
      height: 'auto',
      panelClass: 'custom-modalbox',
      data,
    })

    dialogRef.afterClosed().subscribe(result => {
      if (result && result === 'refresh') {
        this.searchList();
        if (this.edit) {
          this.utils.showMessage('保存工艺成功', 'success');
          this.edit = false;
        } else {
          this.utils.showMessage('新增工艺成功', 'success');
        }
      }
    });
  }

  // 列表查询
  searchList() {
    const params = {
      current: this.searchFormGroup.value.current,
      size: this.searchFormGroup.value.size,
      body: {
        craftName: this.searchFormGroup.value.craftName,
        craftNumber: this.searchFormGroup.value.craftNumber,
        enabled: this.searchFormGroup.value.enabled,
      }
    }
    this.api.fetchGetCraftTableList(params).subscribe(res => {
      const allData = res.data.list || [];
      allData.forEach((item, index) => {
        item.no = ++index;
      })
      this.dataSource = allData;
      console.log('dataSource', this.dataSource)
      this.total = res.data.totalElements;
    });
  }

  // 表格操作
  handleEvent(data, type) {

    switch (type) {
      case 'enable':
        this.utils.confirm('温馨提示', `是否确认启用${data.craftName}?`, () => {
          this.api.fetchHandleCraftData({
            enable: 1,
            craftId: data.craftId
          }).subscribe(res => {
            if (res.errcode === 200) {
              this.utils.showMessage('启用成功', 'success');
              this.searchList();
            } else {
              this.utils.showMessage(res.errmsg, 'error');
            }
          });
        })

        break;
      case 'disable':
        this.utils.confirm('温馨提示', `是否确认禁用${data.craftName}?`, () => {
          this.api.fetchHandleCraftData({
            enable: 0,
            craftId: data.craftId
          }).subscribe(res => {
            if (res.errcode === 200) {
              this.utils.showMessage('禁用成功', 'success');
              this.searchList();
            } else {
              this.utils.showMessage(res.errmsg, 'error');
            }

          });
        })

        break;
      case 'del':
        this.utils.confirm(`是否确认删除${data.craftName}?`, '请注意，确认后删除数据将不可恢复', () => {
          this.api.fetchDeleteCraftData({
            craftId: data.craftId
          }).subscribe(res => {
            if (res.errcode === 200) {
              this.utils.showMessage('删除成功', 'success');
              this.searchList();
            } else {
              this.utils.showMessage(res.errmsg, 'error');
            }

          });
        })

        break;
      case 'details':
        this.api.fetchGetCraftDetails({
          craftId: data.craftId
        }).subscribe(res => {
          if (res.errcode === 200) {
            const obj = {
              type: 'details',
              data: res.data,
            };
            this.showAddVisibilly(obj);
          } else {
            this.utils.showMessage(res.errmsg, 'error');
          }


        });
        break;
      case 'edit':
        this.edit = true;
        this.api.fetchGetCraftDetails({
          craftId: data.craftId
        }).subscribe(res => {
          if (res.errcode === 200) {
            const obj = {
              type: 'edit',
              data: res.data,

            };
            this.showAddVisibilly(obj);
          } else {
            this.utils.showMessage(res.errmsg, 'error');
          }

        });
        break;
      case "bind":
        console.log(data)
        let queryParams: NavigationExtras = {
          queryParams: {
            name: data.craftName,
            craftId: data.craftId,
            kdDeptId:data.kdDeptId?data.kdDeptId:"",
            kdOrgId:data.kdOrgId?data.kdOrgId:"",
          }
        }
        this.router.navigate(['/material-bind'], queryParams);
        break;
    }
  }

  getNotices($event): any {
    // 点击paginator事件，获取pageIndex，重新加载页面
    this.searchFormGroup.value.current = $event.pageIndex;
    this.searchFormGroup.value.size = $event.pageSize;
    this.searchList();
  }

}
