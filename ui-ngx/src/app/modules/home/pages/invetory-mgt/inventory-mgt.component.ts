import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { FormBuilder } from '@angular/forms';
import { InventoryMgtService } from '@app/core/http/invetory-mgt.service';

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
  selector: 'tb-inventory-mgt',
  templateUrl: './inventory-mgt.component.html',
  styleUrls: ['./inventory-mgt.component.scss']
})
export class InventoryMgtComponent implements OnInit {

  //搜索参数
  searchFormGroup = this.fb.group({
    current: 0,
    size: 50,
    warehouseName: "",
    materialName: "",
    spec: "",
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
  displayedColumns: string[] = ['no', 'warehouseName', 'warehouseId', 'warehouseCode','materialId', 'materialName',  'materialCode', 'spec', 'unit', 'qty'];

  // @ViewChild(MatPaginator) paginator: MatPaginator;

  dataSource = [];

  btns = JSON.parse(localStorage.getItem('btns'));
  set = new Set(this.btns);

  constructor(protected store: Store<AppState>,
    public fb: FormBuilder,
    private inventoryMgtService: InventoryMgtService,
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
        warehouseName:this.searchFormGroup.value.warehouseName,
        materialName:this.searchFormGroup.value.materialName,
        spec:this.searchFormGroup.value.spec,
      }
    }
    this.inventoryMgtService.fetchGetTableList(par).subscribe(res => {
      this.dataSource = res.data.list;
      this.total = res.data.total;
    })
  }



  //翻页事件
  getNotices($event): any {
    // 点击paginator事件，获取pageIndex，重新加载页面
    this.searchFormGroup.value.current= $event.pageIndex;
    this.searchFormGroup.value.size = $event.pageSize;
    this.getTableData();
  }
  reset() {
    this.searchFormGroup.value.warehouseName = '';
    this.searchFormGroup.value.materialName = '';
    this.searchFormGroup.value.spec = '';
    this.getTableData();
  }
}
