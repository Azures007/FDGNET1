import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormBuilder, Validators } from '@angular/forms';
import { Utils } from '../../order-management/w-utils';

import { TechnologicalService } from '@app/core/http/technological.service';
import { OrderService } from '@app/core/http/order.service';

@Component({
  selector: 'tb-start-order',
  templateUrl: './start-order.html',
  styleUrls: ['./dialog.scss']
})
export class StartOrderComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<StartOrderComponent>,
    @Inject(MAT_DIALOG_DATA) public injectData: any,
    private fb: FormBuilder,
    private utils: Utils,
    private apiTechnolog: TechnologicalService,
    private apiOrder: OrderService,
  ) { }

  // 保存参数
  addParams = {
    orderId: '',
    craftId: '',
    craftDesc: '',
  };

  dataForm;

  formType = '';

  errorMsg = "";
  materialError = false;

  // 状态下拉
  craftOptions = [];

  get modalTitle() {
    return this.formType ? (this.formType === 'details' ? '查看接单' : '编辑接单') : '接单开工';
  }

  ngOnInit(): void {
    console.log('data', this.injectData)
    this.getCraftList();

    if (this.utils.isEmpty(this.injectData.data)) {
      if (this.injectData.routeData.errcode == 0) {
        this.dataForm = this.fb.group({
          craftId: [{ value: "", disabled: true }, [Validators.required]],
          craftDesc: ['', []],
        });
        this.materialError = true;
        this.errorMsg = this.injectData.routeData.errmsg;

      } else {
        this.dataForm = this.fb.group({
          craftId: [{ value: this.injectData.routeData.data.craftId, disabled: true }, [Validators.required]],
          craftDesc: ['', []],
        });
      }

    } else {
      this.formType = this.injectData.type;
      const obj = {
        craftId: '',
        craftDesc: '',
      };

      Object.keys(this.injectData.data).forEach(key => {
        obj[key] = this.injectData.data[key];
      });

    }
  }

  // 关闭弹窗
  addDialogClose(): void {
    this.dialogRef.close();
  }

  // 获取工艺路线值

  getCraftList() {
    const params = {
      current: 0,
      size: 9999,
      body: {
        craftName: '',
        craftNumber: '',
        enabled: '',
      }
    }
    this.apiTechnolog.fetchGetCraftTableList(params).subscribe(res => {

      this.craftOptions = [];

      if (res.errcode === 200) {
        const allData = res.data.list || [];
        allData.forEach((item) => {
          this.craftOptions.push({
            label: item.craftName,
            value: item.craftId
          })
        })
      } else {
        this.utils.showMessage(res.errmsg, 'error');
      }


    });
  }


  submit() {
    if (this.materialError) {
      this.dialogRef.close('error');
    }
    const userInfo = JSON.parse(localStorage.getItem('userInfo'));

    if (this.dataForm.valid) {

      // Object.keys(this.dataForm.value).forEach(key => {
      //   this.addParams[key] = this.dataForm.value[key];
      // });
      this.addParams.craftId = this.injectData.routeData.data.craftId;

      this.addParams.orderId = this.injectData.orderId;

      this.apiOrder.fetchStartOrder(this.addParams).subscribe(res => {
        if (res.errcode === 200) {
          this.dialogRef.close('refresh');
        } else {
          this.utils.showMessage(res.errmsg, 'error')
          // this.dialogRef.close('refresh');
        }
      })

    } else {

      for (const i in this.dataForm.controls) {
        this.dataForm.controls[i].markAsTouched();
        this.dataForm.controls[i].updateValueAndValidity();
      }
    }
  }

}
