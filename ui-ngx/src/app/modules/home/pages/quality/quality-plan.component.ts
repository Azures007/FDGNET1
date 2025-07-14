

import { Component, OnInit, ViewChild } from '@angular/core';
import { FormGroup, FormControl, FormBuilder } from '@angular/forms';
import { TechnologicalService } from '@app/core/http/technological.service';
import { QualityService } from '@app/core/http/quality.service';
import { DictionaryService } from '@app/core/http/dictionary.service';
import { Router } from '@angular/router';

import { Utils } from '../order-management/w-utils';
import { MatDialog } from '@angular/material/dialog';


import { MatTableDataSource } from '@angular/material/table';
import { ClassService } from '@app/core/http/class.service';
import { MatSort } from '@angular/material/sort';
import { AddPlanComponent } from '../../components/quality/add-plan.component';


@Component({
  selector: 'tb-quality-plan',
  templateUrl: './quality-plan.html',
  styleUrls: ['./quality-plan.scss', './quality.component.scss']
})
export class QualityPlanComponent implements OnInit {

  @ViewChild(MatSort) sort: MatSort;
  searchData = {
    planName: '',
    productionLineName: '',
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

  displayedColumns: string[] = ['no', 'planName', 'productionLineName', 'remarks', 'isEnabled', 'customColumn1'];

  btns = JSON.parse(localStorage.getItem('btns'));
  set = new Set(this.btns);
  process = [];
  methods = [];
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
  getProcess(value) {
    let label = '';
    this.process.forEach(item => {
      if (item.codeValue === value) {
        label = item.codeDsc;
      }
    })
    return label;
  }
  getMethod(value) {
    const arr = value.split(',');
    let label = '';
    this.methods.forEach(item => {
      if (arr.indexOf(item.codeValue) !== -1) {
        label += item.codeDsc + ',';
      }
    })
    return label;
  }
  constructor(private utils: Utils,
    private DictionaryService: DictionaryService,
    public fb: FormBuilder,
    private apiTechnolog: TechnologicalService,
    private qualityService: QualityService,
    private ClassService: ClassService,
    public router: Router,
    public dialog: MatDialog) {

  }

  ngOnInit() {
    let par = {
      current: 0,
      size: 999,
      codeClId: 'GJGX0000',
      enabledSt: 1
    }
    this.DictionaryService.fetchGetTypeTableList(par).subscribe(res => {
      this.process = res.data.list;
    })
    this.DictionaryService.fetchGetTypeTableList({
      ...par,
      codeClId: 'JKFF0000',
    }).subscribe(res => {
      this.methods = res.data.list;
    })
    this.searchList();
  }
  resetQuery() {
    this.searchData = {
      planName: '',
      productionLineName: '',
      current: 0,
      size: 50,
    }
    this.searchList();

  }
  resetPaging() {
    this.searchList();
  }

  searchList() {
    let params = null;
    params = {
      current: this.searchData.current,
      size: this.searchData.size,
      sortField: this.sort?.active,
      sortOrder: this.sort?.direction,
      body: {
        planName: this.searchData.planName,
        productionLineName: this.searchData.productionLineName,
      }
    }


    this.qualityService.fetchGetPlanList(params).subscribe(res => {

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
          id: data.id,
          isEnabled: '0',
        }
        this.qualityService.fetchDisable(params).subscribe(res => {
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
          id: data.id,
          isEnabled: '1',
        }
        this.qualityService.fetchDisable(params).subscribe(res => {
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
          this.qualityService.fetchDel(data.id).subscribe(res => {
            if (res.errcode === 200) {
              this.utils.showMessage('删除成功', 'success');
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
      this.qualityService.fetchGetDetails(data.data.id).subscribe(res => {
        data.data = res.data;
        let dialogRef = this.dialog.open(AddPlanComponent, {
          width: "1400px",
          height: "auto",
          panelClass: 'custom-modalbox',
          data: {
            data: JSON.parse(JSON.stringify(data)),
          }
        })
        dialogRef.afterClosed().subscribe(result => {
          if (result && result === 'refresh') {
            this.searchList();
            const msg = data.type ? (data.type === 'details' ? '查看方案' : '编辑方案') : '新增方案';
            this.utils.showMessage(`${msg}成功`, 'success');
          }
        });
      })
      return;
    }
    let dialogRef = this.dialog.open(AddPlanComponent, {
      width: "1400px",
      height: "auto",
      panelClass: 'custom-modalbox',
      data: {
        data: JSON.parse(JSON.stringify(data)),
      }
    })
    dialogRef.afterClosed().subscribe(result => {
      if (result && result === 'refresh') {
        this.searchList();
        const msg = data.type ? (data.type === 'details' ? '查看方案' : '编辑方案') : '新增方案';
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
