import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';
import { FormBuilder, FormGroup, FormControl, Validators } from '@angular/forms';
import { Utils } from '../../pages/order-management/w-utils';
import { BasketService } from '@app/core/http/basket.service';

@Component({
  selector: 'tb-add-basket',
  templateUrl: './add-basket.component.html',
  styleUrls: ['../material/dialog.scss', './add-basket.component.scss']
})
export class AddBasketComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<AddBasketComponent>,
    @Inject(MAT_DIALOG_DATA) public injectData: any,
    private fb: FormBuilder,
    private utils: Utils,
    public _dialog: MatDialog,
    private BasketService:BasketService
  ) { }

  //新增参数
  addParams = {
    br: "",
    chargingBasketId: '',
    chargingBasketSize: "",
    chargingBasketStatus: "",
    code: "",
    maxBearing: "",
    weight: 0,
    createdName: "",
    createdTime: "",
    unit:"",

  }

  dataForm: FormGroup;

  formType = '';

  // 状态下拉
  status = [{
    label: '启用',
    value: '1',
  }, {
    label: '禁用',
    value: '0',
  }];

  get modalTitle() {
    return this.formType ? (this.formType === 'details' ? '查看料筐' : '编辑料筐') : '新增料筐';
  }

  ngOnInit(): void {
    console.log(this.injectData,'in')
    if (this.utils.isEmpty(this.injectData.data)) {
      this.dataForm = this.fb.group({
        chargingBasketSize: '',
        code: ['', [Validators.required]],
        maxBearing: '',
        chargingBasketStatus: ['', [Validators.required]],
        weight: ['', [Validators.required]],
        unit: ['kg', [Validators.required]],
        br: "",
        createdName: "",
        createdTime: "",
        chargingBasketId: '',
      });
    } else {
      this.formType = this.injectData.data.type;
      const obj = {
        br: "",
        chargingBasketId: '',
        chargingBasketSize: "",
        chargingBasketStatus: "",
        code: "",
        maxBearing: "",
        weight: 0,
        createdName: "",
        createdTime: "",
        unit:"",
      }
      Object.keys(this.injectData.data.data).forEach(key => {
        obj[key] = this.injectData.data.data[key];
      });
      if (this.formType === 'details') {
        this.dataForm = this.fb.group({
          chargingBasketSize: [{ value: obj.chargingBasketSize, disabled: true },],
          code: [{ value: obj.code, disabled: true }, [Validators.required]],
          maxBearing: [{ value: obj.maxBearing, disabled: true },],
          chargingBasketStatus: [{value: obj.chargingBasketStatus, disabled: true}, [Validators.required]],
          weight: [{value: obj.weight, disabled: true}, [Validators.required]],
          unit: [{value: obj.unit, disabled: true}, [Validators.required]],
          br: [{ value: obj.br, disabled: true }],
        });
      }else{
        this.addParams.chargingBasketId = this.injectData.data.data.chargingBasketId;
        this.dataForm = this.fb.group({
          chargingBasketSize: [{ value: obj.chargingBasketSize, disabled: false },],
          code: [{ value: obj.code, disabled: false }, [Validators.required]],
          maxBearing: [{ value: obj.maxBearing, disabled: false },],
          chargingBasketStatus: [{value: obj.chargingBasketStatus, disabled: false}, [Validators.required]],
          weight: [{value: obj.weight, disabled: false}, [Validators.required]],
          unit: [{value: obj.unit, disabled: false}, [Validators.required]],
          br: [{ value: obj.br, disabled: false }],
        });
      }

    }
  }

    //关闭弹窗
    addDialogClose() {
      this.dialogRef.close();
    }
    submit() {
      if (this.dataForm.valid) {
        Object.keys(this.dataForm.value).forEach(key => {
          this.addParams[key] = this.dataForm.value[key];
        });
        if (!this.utils.isEmpty(this.injectData.data)) {
          this.addParams.createdName = this.injectData.data.data.createdName;
          this.addParams.createdTime = this.injectData.data.data.createdTime;
        }
  
        this.BasketService.fetchSave(this.addParams).subscribe(res => {
          if (res.errcode === 200) {
            this.dialogRef.close('refresh');
          } else {
            this.utils.showMessage(res.errmsg, 'error');
          }
        })
  
      }else {
        for (const i in this.dataForm.controls) {
          this.dataForm.controls[i].markAsTouched();
          this.dataForm.controls[i].updateValueAndValidity();
        }
      }
    }

}
