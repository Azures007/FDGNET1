import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { StatisticsService } from '@app/core/http/statistics.service';
import { Utils } from '../order-management/w-utils';


@Component({
  selector: 'tb-statistics-material',
  templateUrl: './material.html',
  styleUrls: ['./material.scss']
})
export class MaterialComponent implements OnInit {
  // 默认前一天到当天
  pdRange = new FormGroup({
    start: new FormControl(this.utils.dateFormat(new Date(new Date().getTime() - 24 * 60 * 60 * 1000), 'yyyy-MM-dd')),
    end: new FormControl(this.utils.dateFormat(new Date(), 'yyyy-MM-dd')),
  });
  //搜索参数
  searchFormGroup = this.fb.group({
    current: 0,
    size: 50,
    productName: "友臣原味电商版周转肉松饼",
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
    private statisticsService: StatisticsService,
    private utils: Utils,
  ) { }

  ngOnInit(): void {
    this.getTableData();
  }
  //获取表格数据
  getTableData(): void {
    let par = {
      current: this.searchFormGroup.value.current,
      size: this.searchFormGroup.value.size,
      body: {
        productName: this.searchFormGroup.value.productName,
        orderDateStart: this.pdRange.value.start ? new Date(this.utils.dateFormat(new Date(this.pdRange.value.start), 'yyyy-MM-dd 00:00:00')) : null,
        orderDateEnd: this.pdRange.value.end ? new Date(this.utils.dateFormat(new Date(this.pdRange.value.end), 'yyyy-MM-dd 23:59:59')) : null,
      }
    }
    this.statisticsService.fetchGetMaterialTableList(par).subscribe(res => {
      const listTemp = res.data.list;
      listTemp.forEach(item => {
        let firstColsNum = 1;
        item.processGroupInfoList.forEach(processGroupInfo => {
          let secondColsNum = 1;
          processGroupInfo.materialInfoList.forEach(materialInfo => {
            firstColsNum++;
            secondColsNum++;
          })
          if(secondColsNum > 1) {
            secondColsNum--;
          }
          processGroupInfo.secondColsNum = secondColsNum;
        })
        if(firstColsNum > 1) {
          firstColsNum--;
        }
        item.firstColsNum = firstColsNum;
      })
      console.log(listTemp);
      this.dataSource = listTemp;
      this.total = res.data.total;
    })
  }

  export() {
    let par = {
      current: this.searchFormGroup.value.current,
      size: this.searchFormGroup.value.size,
      body: {
        productName: this.searchFormGroup.value.productName,
        orderDateStart: this.pdRange.value.start ? new Date(this.utils.dateFormat(new Date(this.pdRange.value.start), 'yyyy-MM-dd 00:00:00')) : null,
        orderDateEnd: this.pdRange.value.end ? new Date(this.utils.dateFormat(new Date(this.pdRange.value.end), 'yyyy-MM-dd 23:59:59')) : null,
      }
    }
    this.statisticsService.exportMaterialTableList(par).subscribe(res => {
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
    this.searchFormGroup.value.productName = '';
    this.pdRange = new FormGroup({
      start: new FormControl(this.utils.dateFormat(new Date(new Date().getTime() - 24 * 60 * 60 * 1000), 'yyyy-MM-dd')),
      end: new FormControl(this.utils.dateFormat(new Date(), 'yyyy-MM-dd')),
    });

    this.getTableData();
  }
}
