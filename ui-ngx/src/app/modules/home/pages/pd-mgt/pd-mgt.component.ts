import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { PdMgtService } from '@app/core/http/pd-mgt.service';

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
  selector: 'tb-pd-mgt',
  templateUrl: './pd-mgt.component.html',
  styleUrls: ['./pd-mgt.component.scss']
})
export class PdMgtComponent implements OnInit {
  pdRange = new FormGroup({
    start: new FormControl(null),
    end: new FormControl(null),
  });
  //搜索参数
  searchFormGroup = this.fb.group({
    current: 0,
    size: 50,
    pdWorkshopName: "",
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
  pageSizeOptions: number[] = [50, 100, 300,200,500];
  total = 0;
  // pageEvent: PageEvent;

  //table
  displayedColumns: string[] = ['no', 'pdTimeStr', 'materialNumber', 'materialName','materialSpecifications', 'pdUnit',  'pdQty', 'pdCreatedName', 'isReturn', 'pdWorkshopLeaderName'];

  // @ViewChild(MatPaginator) paginator: MatPaginator;

  dataSource = [];

  btns = JSON.parse(localStorage.getItem('btns'));
  set = new Set(this.btns);

  constructor(protected store: Store<AppState>,
    public fb: FormBuilder,
    private pdMgtService: PdMgtService,
  ) { }

  ngOnInit(): void {
    this.getTableData();
  }

  //获取表格数据
  getTableData(): void {
    let par = {
      current: this.searchFormGroup.value.current,
      size: this.searchFormGroup.value.size,
      body:{
        pdWorkshopName:this.searchFormGroup.value.pdWorkshopName,
        startTime: this.pdRange.value.start,
        endTime: this.pdRange.value.end,
      }
    }
    if (this.curTable === 1) {
      this.pdMgtService.fetchGetTableList(par).subscribe(res => {
        this.dataSource = res.data.list;
        this.total = res.data.total;
      })
    } else {
      this.pdMgtService.fetchGetTableListWithSplit(par).subscribe(res => {
        this.dataSource = res.data.list;
        this.total = res.data.total;
      })
    }
  }

  export() {
    let par = {
      pdWorkshopName:this.searchFormGroup.value.pdWorkshopName,
      startTime: this.pdRange.value.start,
      endTime: this.pdRange.value.end,
    }
  }

  changeTable(index) {
    this.curTable = index;
    this.getTableData();
  }

  //翻页事件
  getNotices($event): any {
    // 点击paginator事件，获取pageIndex，重新加载页面
    this.searchFormGroup.value.current= $event.pageIndex;
    this.searchFormGroup.value.size = $event.pageSize;
    this.getTableData();
  }
  reset() {
    this.searchFormGroup.value.pdWorkshopName = '';
    this.pdRange = new FormGroup({
      start: new FormControl(null),
      end: new FormControl(null),
    });
    this.getTableData();
  }
}
