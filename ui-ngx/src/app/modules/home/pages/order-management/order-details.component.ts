

import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, FormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { Utils } from './w-utils';
import { OrderService } from "@core/http/order.service";
import { DictionaryService } from '@app/core/http/dictionary.service';
import { CraftDetailComponent } from '../../components/order/craft-detail.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'tb-order-details',
  templateUrl: './order-details.component.html',
  styleUrls: ['./order-details.component.scss', './order-management.component.scss']
})
export class OrderDetailsComponent implements OnInit {

  maxDate: Date;

  dataParams = {
    billNo: '',
    orderNo: '',
    billType: '',
    vwkname: '',
    orderStatus: '',
    billDate: '',
    creatorName: '',
    remark: '',
    craftName: ''
  };

  materialList = [];

  // 用料清单
  userMaterialList = [];

  // 工序
  processList = [];

  craftDetail = [];
  //单位列表
  allUnits = [];

  get totalPrdQty() {

    let res = 0;

    this.materialList.forEach(item => {
      res += Number(item.planQty);
    })

    return res
  }

  bodyUnit = "";

  detailData: any;


  constructor(
    private utils: Utils,
    public fb: FormBuilder,
    private DictionaryService: DictionaryService,
    private activeRoute: ActivatedRoute,
    private apiOrder: OrderService,
    public _dialog: MatDialog,
    private router: Router,
  ) {

  }

  ngOnInit() {
    let par = {
      current: 0,
      size: 999,
      codeClId: 'UNIT0000',
      enabledSt: 1
    }
    this.DictionaryService.fetchGetTypeTableList(par).subscribe(res => {
      this.allUnits = res.data.list;
      this.activeRoute.params.subscribe(res => {
        this.init(res);
      });
    })

  }
  openCraftDetail(){
    let dialogRef = this._dialog.open(CraftDetailComponent, {
      width: "800px",
      height: "auto",
      panelClass: 'custom-modalbox',
      data: {
        title: this.dataParams.craftName,
        dataSource: this.craftDetail
      }
    })
    dialogRef.afterClosed().subscribe(result => {

    });
  }
  // 数据查询
  init(data) {
    this.apiOrder.fetchGetOrderDetails({
      orderId: data.orderId
    }).subscribe(res => {
      if (res.errcode === 200) {
        const allData = res.data;
        this.dataParams.remark = allData.remark;
        this.dataParams.billNo = allData.billNo;
        this.dataParams.orderNo = allData.orderNo;
        this.dataParams.billType = allData.billType;
        this.dataParams.vwkname = allData.vwkname;
        this.dataParams.orderStatus = allData.orderStatus;
        this.dataParams.billDate = allData.billDate;
        this.dataParams.creatorName = allData.creatorName;
        this.dataParams.craftName = allData.craftName;
        this.craftDetail = allData.craftProcesses || [];

        let statusStr = '';
        switch (allData.orderStatus) {
          case '1':
            statusStr = '已开工';
            break;
          case '2':
            statusStr = '暂停';
            break;
          case '3':
            statusStr = '已完工';
            break;
          case '0':
            statusStr = '未开工';
            break;
        }
        this.dataParams.orderStatus = statusStr;


        // 默认只显示一行
        this.materialList = allData.product ? [allData.product] : [];

        this.userMaterialList = allData.materials || [];

        this.processList = allData.processExecutes || [];

      } else {
        this.utils.showMessage(res.errmsg, 'error');
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

  handleEvent(data, type) {

  }
  back(){
    this.router.navigate(['/order/list']);
  }


}
