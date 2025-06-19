

import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, FormBuilder } from '@angular/forms';
import { TechnologicalService } from '@app/core/http/technological.service';
import { OrderService } from '@app/core/http/order.service';
import { DictionaryService } from '@app/core/http/dictionary.service';
import { Router } from '@angular/router';

import { Utils } from './w-utils';
import { MatDialog } from '@angular/material/dialog';

import { StartOrderComponent } from './dialog/start-order.component';
import { ChangeClassComponent } from './dialog/change-class.component';
import { element } from 'protractor';
import { SelectionModel } from '@angular/cdk/collections';
import { MatTableDataSource } from '@angular/material/table';
import { ClassService } from '@app/core/http/class.service';
import { retry } from 'rxjs/operators';
import { GlobalConstants } from '@app/common/global-constants';

export interface PeriodicElement {
  no: Number,
  billNo: string,
  billDate: string,
  bodyPrdDept: string,
  bodyPlanPrdQty: string,
  bodyPlanFinishDate: string,
  bodyMaterialSpecification: string,
  statusStr: string,
  processStr: string,
  classId: string,
  bodyMaterialNumber: string,
  orderId: Number,
}


@Component({
  selector: 'tb-order-list',
  templateUrl: './order-list.html',
  styleUrls: ['./order-list.scss', './order-management.component.scss']
})
export class OrderListComponent implements OnInit {

  minDate: Date;
  orderBillRange = new FormGroup({
    start: new FormControl(new Date(new Date().getTime() - 2 * 24 * 3600 * 1000)),
    end: new FormControl(new Date(new Date().getTime() + 4 * 24 * 3600 * 1000)),
  });

  orderPlanRange = new FormGroup({
    start: new FormControl(new Date(new Date().getTime() - 2 * 24 * 3600 * 1000)),
    end: new FormControl(new Date(new Date().getTime() + 4 * 24 * 3600 * 1000)),
  });

  orderSelect = [];

  orderValue


  shiftValue = '';
  orderValue2 = '';
  orderNo = '';


  // 翻页参数
  length: number;
  pageSize = 50;
  pageIndex = 0;
  pageSizeOptions: number[] = [50, 100, 300, 200, 500];
  total = 0;
  tableData: PeriodicElement[] = [];
  dataSource = new MatTableDataSource<PeriodicElement>(this.tableData);

  selection = new SelectionModel<PeriodicElement>(true, []);

  //单位列表
  allUnits = [];

  displayedColumns: string[] = ['select', 'no', 'billNo', 'bodyLot', 'billDate', 'bodyPrdDept', 'midMoEntryTeamName', 'bodyPlanPrdQty', 'bodyPlanFinishDate',
    'bodyMaterialSpecification', 'statusStr', 'processStr', 'classId', 'orderMatching', 'customColumn1',];


  processOptions = [];
  shiftSelect = [];


  // 搜索参数
  searchFormGroup = this.fb.group({
    current: 0,
    size: 50,
    currentProcess: 0,
    billNo: '',
    orderStatus: '',
    classId: 0,
    orderMatching: '0',
  });

  clickedRows = new Set();
  bodyUnit = "";

  orderMatchingStasus = [
    {
      label: "全部",
      value: ""
    },
    {
      label: "匹配",
      value: "1"
    },
    {
      label: "不匹配",
      value: "-1"
    },
  ]

  btns = JSON.parse(localStorage.getItem('btns'));
  set = new Set(this.btns);

  //table

  constructor(private utils: Utils,
    private DictionaryService: DictionaryService,
    public fb: FormBuilder,
    private apiTechnolog: TechnologicalService,
    private apiOrder: OrderService,
    private ClassService: ClassService,
    public router: Router,
    public dialog: MatDialog) {

  }

  ngOnInit() {
    let testStr = GlobalConstants.apiURL;
    console.log(testStr,'global')
    this.minDate = new Date('2020-01-01 00:00:00');
    let par = {
      current: 0,
      size: 999,
      codeClId: 'UNIT0000',
      enabledSt: 1
    }
    this.DictionaryService.fetchGetTypeTableList(par).subscribe(res => {
      this.allUnits = res.data.list;
      this.getAllClass();
      this.getStatus();
    })

  }

  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  toggleAllRows() {
    if (this.isAllSelected()) {
      this.selection.clear();
      return;
    }
    this.selection.select(...this.dataSource.data);
  }
  /** The label for the checkbox on the passed row */
  checkboxLabel(row?: PeriodicElement): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${Number(row.no) + 1}`;
  }

  listStart() {
    let data = [];
    for (let i = 0; i < this.selection.selected.length; i++) {
      let o = {
        bodyMaterialNumber: this.selection.selected[i].bodyMaterialNumber,
        orderId: this.selection.selected[i].orderId,
        craftDesc: "",
        craftId: "",
      }
      data.push(o);
    }
    this.apiOrder.fetchListStartOrder(data).subscribe(res => {
      if (res.errcode == 0) {
        this.utils.showMessage(res.errmsg, 'error')
      } else {
        this.utils.showMessage('批量接单开工成功', 'success')
        this.searchList();
      }
    })
  }


  // 列表查询
  // this.searchFormGroup.value.currentProcess !== 0 ? this.searchFormGroup.value.currentProcess : '',

  searchList() {
    let storagePar = localStorage.getItem('storagePar');
    let params = null;
    if (storagePar && JSON.parse(storagePar).body) {
      let storageParObj = JSON.parse(storagePar);
      this.searchFormGroup.value.current = storageParObj.current;
      this.searchFormGroup.value.size = storageParObj.size;
      this.searchFormGroup.value.currentProcess = storageParObj.body.currentProcess;
      this.searchFormGroup.value.classId = storageParObj.body.classId;
      this.searchFormGroup.value.billNo = storageParObj.body.billNo;
      this.searchFormGroup.value.orderStatus = storageParObj.body.orderStatus;
      this.searchFormGroup.value.orderMatching = storageParObj.body.orderMatching;
      this.orderBillRange.value.end = storageParObj.body.billDateEnd;
      this.orderBillRange.value.start = storageParObj.body.billDateStart;
      this.orderPlanRange.value.end = storageParObj.body.planStartDateEnd;
      this.orderPlanRange.value.start = storageParObj.body.planStartDateStart;
    } else {

    }
    params = {
      current: this.searchFormGroup.value.current,
      size: this.searchFormGroup.value.size,
      body: {
        currentProcess: this.searchFormGroup.value.currentProcess,
        classId: this.searchFormGroup.value.classId,
        billDateEnd: this.utils.dateFormat(new Date(this.orderBillRange.value.end), 'yyyy-MM-dd 23:59:59'),
        billDateStart: this.utils.dateFormat(new Date(this.orderBillRange.value.start), 'yyyy-MM-dd 00:00:00'),
        billNo: this.searchFormGroup.value.billNo,
        orderStatus: this.searchFormGroup.value.orderStatus !== 0 ? this.searchFormGroup.value.orderStatus : '',
        orderMatching: this.searchFormGroup.value.orderMatching,
        planStartDateEnd: this.utils.dateFormat(new Date(this.orderPlanRange.value.end), 'yyyy-MM-dd 23:59:59'),
        planStartDateStart: this.utils.dateFormat(new Date(this.orderPlanRange.value.start), 'yyyy-MM-dd 00:00:00'),
      }
    }


    this.apiOrder.fetchGetTableList(params).subscribe(res => {

      if (res.errcode === 200) {

        const allData = res.data || [];
        allData.list.forEach((item, index) => {
          item.no = ++index;
          item.processStr = '';
          item.statusStr = '';
          this.processOptions.forEach(data => {
            if (item?.currentProcess && Number(item.currentProcess.processId) === data.value) {
              item.processStr = data.label;
            }
          })

          this.orderSelect.forEach(data => {
            if (Number(item.orderStatus) === Number(data.value)) {
              item.statusStr = data.label;
            }
          })
        })
        const MyData: PeriodicElement[] = allData.list;
        this.tableData = allData.list;
        this.selection = new SelectionModel<PeriodicElement>(true, []);
        this.dataSource = new MatTableDataSource<PeriodicElement>(MyData);
        this.total = res.data.total;
        localStorage.removeItem('storagePar')
      } else {
        this.utils.showMessage(res.errmsg, 'error');
        localStorage.removeItem('storagePar')
      }


    });
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



  handleMatch(value) {
    let str = '';
    value = Number(value);
    switch (value) {
      case -1:
        str = "不匹配";
        return str;
      case 0:
        str = "";
        return str;
      case 1:
        str = "匹配";
        return str;
    }
  }




  // 导出

  export() {

    const params = {
      current: this.searchFormGroup.value.current,
      size: this.searchFormGroup.value.size,
      body: {
        currentProcess: this.searchFormGroup.value.currentProcess,
        classId: this.searchFormGroup.value.classId,
        billDateEnd: this.utils.dateFormat(new Date(this.orderBillRange.value.end), 'yyyy-MM-dd 23:59:59'),
        billDateStart: this.utils.dateFormat(new Date(this.orderBillRange.value.start), 'yyyy-MM-dd 00:00:00'),
        billNo: this.searchFormGroup.value.billNo,
        orderStatus: this.searchFormGroup.value.orderStatus !== 0 ? this.searchFormGroup.value.orderStatus : '',
        orderMatching: this.searchFormGroup.value.orderMatching,
        planStartDateEnd: this.utils.dateFormat(new Date(this.orderPlanRange.value.end), 'yyyy-MM-dd 23:59:59'),
        planStartDateStart: this.utils.dateFormat(new Date(this.orderPlanRange.value.start), 'yyyy-MM-dd 00:00:00'),
      }
    }



    // const url = `/api/orderhead/export?current=${this.searchFormGroup.value.current}&size=${this.searchFormGroup.value.size}`;


    this.apiOrder.fetchExport(params).subscribe(res => {

      let name = res.headers.get('content-disposition');
      name = decodeURIComponent(name).substring(name.indexOf('=') + 1);

      const blob = new Blob([res.body], { type: 'application/x-zip-compressed' });
      const node = document.createElement('a');
      node.href = window.URL.createObjectURL(blob);
      node.download = name;
      node.setAttribute('target', '_blank'); // 弹屏下载

      document.body.appendChild(node);
      node.click();
      document.body.removeChild(node);
    });


  }

  // 获取订单状态
  getStatus() {

    this.apiOrder.fetchGetOrdierStatus({
      codeClId: 'ORDERSTATUS0000',
    }).subscribe(res => {

      if (res.errcode === 200) {

        this.orderSelect = [{ value: '', label: '全部' }]

        const allData = res.data.list;
        allData.forEach((item, index) => {
          this.orderSelect.push({
            label: item.codeDsc,
            value: item.codeValue
          });
        });

        this.getProcessList();

      } else {
        this.utils.showMessage(res.errmsg, 'error');
      }





    });
  }

  // 获取工序
  getProcessList() {

    if (this.processOptions.length) return

    this.apiTechnolog.fetchGetTableList({
      current: 0,
      size: 9999
    }).subscribe(res => {

      this.processOptions = [{
        label: '全部',
        value: 0
      }];

      const allData = res.data.content;
      allData.forEach((item, index) => {


        this.processOptions.push({
          label: item.processName,
          value: item.processId
        });
      });

      this.searchList();

    });
  }

  getAllClass() {
    this.apiTechnolog.fetchGetClassList({
      current: 0,
      size: 9999
    }).subscribe(res => {


      if (res.errcode === 200) {

        this.shiftSelect = [{
          label: '全部',
          value: 0
        }];

        const allData = res.data.list;
        allData.forEach((item, index) => {

          this.shiftSelect.push({
            label: item.name,
            value: item.classId
          });
        });



      } else {
        this.utils.showMessage(res.errmsg, 'error');
      }

    });
  }

  handleEvent(data, type) {
    switch (type) {
      case 'details':
        let params = {
          current: this.searchFormGroup.value.current,
          size: this.searchFormGroup.value.size,
          body: {
            currentProcess: this.searchFormGroup.value.currentProcess,
            classId: this.searchFormGroup.value.classId,
            billDateEnd: this.orderBillRange.value.end,
            billDateStart: this.orderBillRange.value.start,
            billNo: this.searchFormGroup.value.billNo,
            orderStatus: this.searchFormGroup.value.orderStatus,
            planStartDateEnd: this.orderPlanRange.value.end,
            planStartDateStart: this.orderPlanRange.value.start,
          }
        }
        localStorage.setItem('storagePar', JSON.stringify(params));
        this.router.navigateByUrl(`order/details/${data.orderId}`);
        break;
      case 'start':
        this.showAddVisibilly(data.orderId, data)
        break;
      case 'change':
        this.showChangeVisibilly(data.orderId, data)
        break;

    }
  }


  // 接单开工弹窗
  showAddVisibilly(data, element): void {
    let par = {
      materialNumber: element.bodyMaterialNumber,
      orderId:data,
    }
    this.apiOrder.fetchGetMaterial(par).subscribe(res => {
      const dialogRef = this.dialog.open(StartOrderComponent, {
        width: '500px',
        height: 'auto',
        panelClass: 'custom-modalbox',
        data: {
          orderId: data,
          data: {
            // classId:element.
          },
          routeData: res
        }
      })

      dialogRef.afterClosed().subscribe(result => {
        if (result && result === 'refresh') {
          this.utils.showMessage('接单成功', 'success');
          this.searchList();
        } else if (result && result === 'error') {
          this.utils.showMessage('请去工艺路线处理物料绑定', 'error');
        }
      });
    })
  }


  // 改变班别弹窗
  showChangeVisibilly(id, element): void {

    this.apiOrder.fetchGetOrderClassInfo({ orderId: id }).subscribe(res => {
      const dialogRef = this.dialog.open(ChangeClassComponent, {
        width: '500px',
        height: 'auto',
        panelClass: 'custom-modalbox',
        data: {
          orderId: id,
          classId: element.classId.classId,
          classList: res.data
        }
      })

      dialogRef.afterClosed().subscribe(result => {
        if (result && result === 'refresh') {
          this.utils.showMessage('更改成功成功', 'success');
          this.searchList();
        } else {
          this.utils.showMessage(result, 'error');
        }
      });
    })


  }



  getNotices($event): any {
    // 点击paginator事件，获取pageIndex，重新加载页面
    this.searchFormGroup.value.current = $event.pageIndex;
    this.searchFormGroup.value.size = $event.pageSize;
    this.searchList();
  }

}
