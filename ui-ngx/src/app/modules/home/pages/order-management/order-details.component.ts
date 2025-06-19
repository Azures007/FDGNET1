

import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { Utils } from './w-utils';
import { OrderService } from "@core/http/order.service";
import { DictionaryService } from '@app/core/http/dictionary.service';

@Component({
  selector: 'tb-order-details',
  templateUrl: './order-details.component.html',
  styleUrls: ['./order-details.component.scss', './order-management.component.scss']
})
export class OrderDetailsComponent implements OnInit {

  maxDate: Date;

  dataParams = {
    orderNo: '',
    billNo: '',
    bodyPrdDept: '',
    planner: '',
    orderStatus: '',
    billDate: '',
    craftName: '',
    currentProcessName: '',
    personName:'', 
    billType:"",
    prdOrg:"",
    bodyPotQty:"",
    bodyClassGroup:"",
    bodyOnePotQty:"",
    midMoEntryTeamName:"",
  };

  materialList = [];

  // 用料清单
  userMaterialList = [];

  // 工序
  processList = [];

  //单位列表
  allUnits = [];

  get totalPrdQty() {

    let res = 0;

    this.materialList.forEach(item => {
      res += Number(item.bodyPlanPrdQty);
    })

    return res
  }

  bodyUnit = "";

  detailData:any;


  constructor(
    private utils: Utils,
    public fb: FormBuilder,
    private DictionaryService: DictionaryService,
    private activeRoute: ActivatedRoute,
    private apiOrder: OrderService
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

  // 数据查询
  init(data) {
    this.apiOrder.fetchGetOrderDetails({
      orderId: data.orderId
    }).subscribe(res => {
      if (res.errcode === 200) {
        const allData = res.data;
        console.log(allData,'alldada')
        this.bodyUnit = res.data.bodyUnit;
        this.detailData = allData;
        this.dataParams.billType = allData.billType;
        this.dataParams.prdOrg = allData.prdOrg;
        this.dataParams.bodyPotQty = allData.bodyPotQty;
        this.dataParams.bodyOnePotQty = allData.bodyOnePotQty;
        this.dataParams.bodyClassGroup = allData.bodyClassGroup;
        this.dataParams.midMoEntryTeamName = allData.midMoEntryTeamName;
        

        this.dataParams.billNo = allData.billNo;
        this.dataParams.orderNo = allData.orderNo;
        this.dataParams.currentProcessName = allData.currentProcess?.processName;
        let statusStr = '';
        switch (allData.orderStatus) {
          case '1':
            statusStr = '已开工';
            break;
          case '2':
            statusStr = '暂停';
            break;
          case '4':
            statusStr = '已完工';
            break;
          case '0':
            statusStr = '未开工';
            break;
        }
        this.dataParams.orderStatus = statusStr;
        this.dataParams.bodyPrdDept = allData.bodyPrdDept;
        this.dataParams.planner = allData.planner;
        this.dataParams.billDate = allData.billDate;
        this.dataParams.craftName = allData.craftId?.craftName;
        // this.dataParams.currentProcessName = allData?.currentProcessName;


        // 默认只显示一行
        this.materialList = [{
          no: '1',
          bodyLot: allData.bodyLot,
          bodyMaterialNumber: allData.bodyMaterialNumber,
          bodyMaterialName: allData.bodyMaterialName,
          bodyPlanPrdQty: allData.bodyPlanPrdQty,
          bodyPlanStartDate: allData.bodyPlanStartDate,
          bodyPlanFinishDate: allData.bodyPlanFinishDate
        }];

        this.userMaterialList = allData.tbusOrderPPBomSet || [];

        this.apiOrder.fetchGetProcessInfo({
          orderId: data.orderId
        }).subscribe(result => {
          if (result.errcode === 200) {
            this.processList = result.data || [];

            this.processList.forEach(item => {
              item.statusStr = '';
              item.classArray = [];
              item.classArray.push(item.classId)
              if (item.processStatus) {
                switch (item.processStatus) {
                  case '1':
                    item.statusStr = '已开工';
                    break;
                  case '2':
                    item.statusStr = '暂停';
                    break;
                  case '4':
                    item.statusStr = '已完工';
                    break;
                  case '0':
                    item.statusStr = '未开工';
                    break;
                }
              }
            });

            console.log(this.processList)
          } else {
            this.utils.showMessage(result.errmsg, 'error');
          }
        });



      } else {
        this.utils.showMessage(res.errmsg, 'error');
      }
    });
  }

  
  //处理单位
  handleUnit(code) {
    let str = '';
    for (let i = 0; i < this.allUnits.length; i++) {
      if(code == this.allUnits[i].codeValue){
        str = this.allUnits[i].codeDsc
        return str
      }
    }
  }

  handleEvent(data, type) {

  }



}
