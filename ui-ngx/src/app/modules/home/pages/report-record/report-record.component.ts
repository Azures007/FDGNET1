import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ReportRecordService } from '@app/core/http/report-record.service';
import { Utils } from '../order-management/w-utils';
import { OrderService } from '@app/core/http/order.service';


@Component({
  selector: 'tb-report-record',
  templateUrl: './report-record.component.html',
  styleUrls: ['./report-record.component.scss']
})
export class ReportRecordComponent implements OnInit {
  // 默认当月月初值当天
  pdRange = new FormGroup({
    start: new FormControl(this.utils.dateFormat(new Date(), 'yyyy-MM-01')),
    end: new FormControl(this.utils.dateFormat(new Date(), 'yyyy-MM-dd')),
  });
  //搜索参数
  searchFormGroup = this.fb.group({
    current: 0,
    size: 50,
    orderNo: "",
    materialName: "",
    materialNumber: "",
    productName: "",
    productNumber: "",
    processName: "",
    cwkLine: "",
  });
  cwkList = [];
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
  curTable = 1;
  //翻页参数
  length: number;
  pageSize = 50;
  pageIndex = 0;
  pageSizeOptions: number[] = [50, 100, 300, 200, 500];
  total = 0;
  // pageEvent: PageEvent;

  //table
  displayedColumns: string[] = ['cwkLine', 'orderNo', 'productName', 'productNumber', 'processName', 'reportTime', 'personName', 'recordTypeBgName', 'materialName', 'materialNumber', 'potNumber', 'recordQty', 'recordUnit'];

  // @ViewChild(MatPaginator) paginator: MatPaginator;

  dataSource = [];

  btns = JSON.parse(localStorage.getItem('btns'));
  set = new Set(this.btns);

  constructor(protected store: Store<AppState>,
    public fb: FormBuilder,
    private reportRecordService: ReportRecordService,
    private utils: Utils,
    private apiOrder: OrderService,
  ) { }

  ngOnInit(): void {
    this.getTableData();
    this.apiOrder.fetchBaseList().subscribe(res => {
      this.cwkList = res.data;
    })
  }
  //获取表格数据
  getTableData(): void {
    let par = {
      current: this.searchFormGroup.value.current,
      size: this.searchFormGroup.value.size,
      body: {
        orderNo: this.searchFormGroup.value.orderNo,
        materialName: this.searchFormGroup.value.materialName,
        materialNumber: this.searchFormGroup.value.materialNumber,
        productName: this.searchFormGroup.value.productName,
        productNumber: this.searchFormGroup.value.productNumber,
        processName: this.searchFormGroup.value.processName,
        cwkLine: this.searchFormGroup.value.cwkLine,
        reportTimeStart: this.pdRange.value.start ? new Date(this.utils.dateFormat(new Date(this.pdRange.value.start), 'yyyy-MM-dd 00:00:00')) : null,
        reportTimeEnd: this.pdRange.value.end ? new Date(this.utils.dateFormat(new Date(this.pdRange.value.end), 'yyyy-MM-dd 23:59:59')) : null,
      }
    }
    this.reportRecordService.fetchGetTableList(par).subscribe(res => {
      this.dataSource = res.data.content;
      this.total = res.data.totalElements;
    })
  }

  export() {
    let par = {
      current: this.searchFormGroup.value.current,
      size: this.searchFormGroup.value.size,
      body: {
        orderNo: this.searchFormGroup.value.orderNo,
        materialName: this.searchFormGroup.value.materialName,
        materialNumber: this.searchFormGroup.value.materialNumber,
        productName: this.searchFormGroup.value.productName,
        productNumber: this.searchFormGroup.value.productNumber,
        processName: this.searchFormGroup.value.processName,
        cwkLine: this.searchFormGroup.value.cwkLine,
        reportTimeStart: this.pdRange.value.start ? new Date(this.utils.dateFormat(new Date(this.pdRange.value.start), 'yyyy-MM-dd 00:00:00')) : null,
        reportTimeEnd: this.pdRange.value.end ? new Date(this.utils.dateFormat(new Date(this.pdRange.value.end), 'yyyy-MM-dd 23:59:59')) : null,
      }
    }
    this.reportRecordService.exportReportRecordList(par).subscribe(res => {
      var name = res.headers.get('content-disposition')//获取文件名，（后台返回的文件名在响应头当中）
      name = decodeURIComponent(name)//由于中文通常都是乱码，所以需要解码
      name = name.substring(name.indexOf("=") + 1)//数据处理获得名字
      this.downloadFile(res.body, name)//数据流都存在body中
    })

  }
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
  //翻页事件
  getNotices($event): any {
    // 点击paginator事件，获取pageIndex，重新加载页面
    this.searchFormGroup.value.current = $event.pageIndex;
    this.searchFormGroup.value.size = $event.pageSize;
    this.getTableData();
  }
  reset() {
    this.searchFormGroup.value.orderNo = '';
    this.searchFormGroup.value.materialName = '';
    this.searchFormGroup.value.materialNumber = '';
    this.searchFormGroup.value.productName = '';
    this.searchFormGroup.value.productNumber = '';
    this.searchFormGroup.value.processName = '';
    this.searchFormGroup.value.cwkLine = '';
    this.pdRange = new FormGroup({
      start: new FormControl(this.utils.dateFormat(new Date(), 'yyyy-MM-01')),
      end: new FormControl(this.utils.dateFormat(new Date(), 'yyyy-MM-dd')),
    });

    this.getTableData();
  }
}
