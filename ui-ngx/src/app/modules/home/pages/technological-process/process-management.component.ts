

import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, FormBuilder } from '@angular/forms';

import { Utils } from '../order-management/w-utils';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { AlertDialogComponent } from '@shared/components/dialog/alert-dialog.component';

import { TechnologicalService } from '@app/core/http/technological.service';
import { AddProcessManageComponent } from './dialog/add-process-manage.component';
import { ProcessSettingComponent } from './dialog/process-setting.component';

@Component({
  selector: 'tb-process-management',
  templateUrl: './process-management.html',
  styleUrls: ['./process-management.scss', './technological-process.scss']
})
export class ProcessManagementComponent implements OnInit {

  // 翻页参数
  length: number;
  pageSize = 50;
  pageIndex = 0;
  pageSizeOptions: number[] = [50, 100, 300, 200, 500];
  total = 0;
  dataSource = [];

  displayedColumns: string[] = ['no', 'processName', 'processNumber', 'erpProcessNumber', 'processDetail','bySetImport', 'bySetExport','enabled', 'customColumn1',];

  // 搜索参数
  searchFormGroup = this.fb.group({
    current: 0,
    size: 50,
    name: '',
    username: '',
    userStatus: '',
  });

  btns = JSON.parse(localStorage.getItem('btns'));
  set = new Set(this.btns);

  erpProcess = JSON.parse(localStorage.getItem('erpProcess'));
  erpProcessMap : any;





  clickedRows = new Set();

  constructor(private utils: Utils, public fb: FormBuilder, public dialog: MatDialog, private api: TechnologicalService) {

  }

  ngOnInit() {
    this.setMyMap();
  }
  

  //创建哈希表
  setMyMap() {
    this.erpProcessMap = this.putHash(new Map(), this.erpProcess, 'erpProcess', 'codeValue');
    console.log(this.erpProcessMap, 'map')
    this.searchList();
  }

  //哈希表存储
  putHash(map, arr, str, key) {
    for (let i = 0; i < arr.length; i++) {
      map.set(`${str}-${arr[i][key]}`, JSON.stringify(arr[i]));
    }
    return map;
  }

  //从哈希表获取数据
  handleHash(value) {
    let str = '';
    if (value) {
      this.erpProcessMap.get(`erpProcess-${value}`) ? str = JSON.parse(this.erpProcessMap.get(`erpProcess-${value}`)).codeDsc : ""
      return str;
    } else {
      return str = "未绑定";
    }

  }


  // 列表查询
  searchList() {
    const params = {
      current: this.searchFormGroup.value.current,
      size: this.searchFormGroup.value.size,
    }
    this.api.fetchGetTableList(params).subscribe(res => {
      const allData = res.data.content;
      allData.forEach((item, index) => {
        item.no = ++index;
      })
      this.dataSource = allData;
      this.total = res.data.totalElements;
    });
  }

  // 表格操作
  handleEvent(data, type) {

    switch (type) {
      case 'enable':

        this.utils.confirm('温馨提示', `是否确认启用${data.processName}?`, () => {
          this.api.fetchHandleData({
            enable: 1,
            processId: data.processId
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
        this.utils.confirm('温馨提示', `是否确认禁用${data.processName}?`, () => {
          this.api.fetchHandleData({
            enable: 0,
            processId: data.processId
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
        this.utils.confirm(`是否确认删除${data.processName}?`, '请注意，确认后删除数据将不可恢复', () => {
          this.api.fetchDeleteData({
            processId: data.processId
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
        this.api.fetchGetProcessDetails({
          processId: data.processId
        }).subscribe(res => {
          if (res.errcode === 200) {
            const obj = {
              type: 'details',
              data: res.data
            };
            this.showAddVisibilly(obj);
          } else {
            this.utils.showMessage(res.errmsg, 'error');
          }


        });
        break;
      case 'edit':
        this.api.fetchGetProcessDetails({
          processId: data.processId
        }).subscribe(res => {
          if (res.errcode === 200) {
            const obj = {
              type: 'edit',
              data: res.data
            };
            this.showAddVisibilly(obj);
          } else {
            this.utils.showMessage(res.errmsg, 'error');
          }

        });
        break;
    }
  }

  // 弹窗
  showAddVisibilly(data): void {
    const dialogRef = this.dialog.open(AddProcessManageComponent, {
      width: '450px',
      height: 'auto',
      panelClass: 'custom-modalbox',
      data,
    })

    dialogRef.afterClosed().subscribe(result => {
      if (result && result === 'refresh') {
        this.searchList();
        const msg = data.type ? (data.type === 'details' ? '查看工序' : '编辑工序') : '新增工序';
        this.utils.showMessage(`${msg}成功`, 'success');
      }
    });
  }

  // 工艺设置弹窗
  showProcessSetting(data): void {

    const dialogRef = this.dialog.open(ProcessSettingComponent, {
      width: '610px',
      height: 'auto',
      panelClass: 'custom-modalbox',
      data: {
        processId: data.processId,
      },
    });

    dialogRef.afterClosed().subscribe(result => {

      if (result && result === 'refresh') {
        // this.searchList();
        this.utils.showMessage('工序设置成功', 'success');
      }
    });

  }

  getNotices($event): any {
    // 点击paginator事件，获取pageIndex，重新加载页面
    this.searchFormGroup.value.current = $event.pageIndex;
    this.searchFormGroup.value.size = $event.pageSize;
    this.searchList();
  }

  // 吐司弹窗
  messageDia(data) {
    const dialogConfig: MatDialogConfig = {
      disableClose: true,
      data
    };
    const mydia = this.dialog.open(AlertDialogComponent, dialogConfig);
    mydia.afterClosed().subscribe(res => {
      // this.getTableData();
    });
  }

}
