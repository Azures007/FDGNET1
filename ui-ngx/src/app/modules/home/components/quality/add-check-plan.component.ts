import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';
import { FormBuilder, FormGroup, FormControl, Validators } from '@angular/forms';
import { Utils } from '../../pages/order-management/w-utils';
import { QualityService } from '@app/core/http/quality.service';
import { DictionaryService } from '@app/core/http/dictionary.service';
import { ChooseMaterialComponent } from '../material/choose-material/choose-material.component';
import { ChooseCheckCateComponent } from './choose-check-cate.component';

@Component({
  selector: 'tb-add-check-plan',
  templateUrl: './add-check-plan.component.html',
  styleUrls: ['./dialog.scss', './add-check-plan.component.scss']

})
export class AddCheckPlanComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<AddCheckPlanComponent>,
    @Inject(MAT_DIALOG_DATA) public injectData: any,
    private fb: FormBuilder,
    private utils: Utils,
    private qualityService: QualityService,
    private DictionaryService: DictionaryService,
    public _dialog: MatDialog,
  ) { }
  // 基础列表
  baseList = [];
  // 还原材料列表
  materialBoms = [];
  // 新增材料参数
  addParams = {
    id: "",
    productName: '',
    productId: '',
    prodLineId: '',
    prodLineName: '',
    enabled: '',
    remark: ''
  }
  configs = [];
  productItem = {
    productName: '',
    productId: ''
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
  frequencyList = [];
  get modalTitle() {
    return this.formType ? (this.formType === 'details' ? '查看方案' : '编辑方案') : '新增方案';
  }

  ngOnInit(): void {
    this.qualityService.fetchBaseList().subscribe(res => {
      this.baseList = res.data;
    })
    let par = {
      current: 0,
      size: 999,
      codeClId: 'FREQUENCY000',
      enabledSt: 1
    }
    if (this.utils.isEmpty(this.injectData.data)) {
      this.dataForm = this.fb.group({
        productName: ['', [Validators.required]],
        prodLineId: ['', [Validators.required]],
        enabled: ['', [Validators.required]],
        remark: [''],
      });
    } else {
      this.formType = this.injectData.data.type;
      const obj = {
        id: "",
        productName: '',
        productId: '',
        prodLineId: '',
        enabled: '',
        remark: ''
      }

      Object.keys(this.injectData.data.data).forEach(key => {
        obj[key] = this.injectData.data.data[key];
      });
      this.productItem.productName = this.injectData.data.data.productName;
      this.productItem.productId = this.injectData.data.data.productId;
      if(this.injectData.data.data.itemList) {
        this.configs = this.injectData.data.data.itemList
      }
      if (this.formType === 'details') {
        this.dataForm = this.fb.group({
          productName: [{ value: obj.productName, disabled: true }],
          prodLineId: [{ value: obj.prodLineId, disabled: true }],
          enabled: [{ value: '' + obj.enabled, disabled: true }],
          remark: [{ value: obj.remark, disabled: true }],
        });
      } else {
        this.addParams.id = this.injectData.data.data.id;
        this.addParams.prodLineName = this.injectData.data.data.prodLineName;
        this.dataForm = this.fb.group({
          productName: [{ value: obj.productName, disabled: false }, [Validators.required]],
          prodLineId: [{ value: obj.prodLineId, disabled: false }, [Validators.required]],
          enabled: [{ value: '' + obj.enabled, disabled: false }, [Validators.required]],
          remark: [{ value: obj.remark, disabled: false }],
        });
      }
    }
    this.DictionaryService.fetchGetTypeTableList(par).subscribe(res => {
      this.frequencyList = res.data.list;
    })
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
      if (this.injectData.data?.data?.id) {
        this.addParams['id'] = this.injectData.data.data.id;
      }
      this.addParams['prodLineName'] = this.baseList.find(item => item.cwkid === this.addParams.prodLineId)?.vwkname;
      this.addParams['productId'] = this.productItem.productId;
      if (!this.configs.length) {
        this.utils.showMessage('请新增配置信息', 'error');
        return;
      }
      const params = {
        ...this.addParams,
        itemList: this.configs.map(item => {
          return {
            categoryId: item.id,
          }
        })
      }
      this.qualityService.fetchSaveDailyPlan(params).subscribe(res => {
        if (res.errcode === 200) {
          this.dialogRef.close('refresh');
        } else {
          this.utils.showMessage(res.errmsg, 'error');
        }
      })

    } else {
      for (const i in this.dataForm.controls) {
        this.dataForm.controls[i].markAsTouched();
        this.dataForm.controls[i].updateValueAndValidity();
      }
    }
  }
    selectProduct() {
    let dialogRef = this._dialog.open(ChooseMaterialComponent, {
      width: "1400px",
      height: "800px",
      panelClass: 'custom-modalbox',
      data: {}
    })
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.dataForm.setControl('productName', new FormControl(result.materialName, [Validators.required]));
        this.productItem = {
          productName: result.materialName,
          productId: result.id
        }
      }
    });
  }
  addConfig() {
    let dialogRef = this._dialog.open(ChooseCheckCateComponent, {
      width: "1400px",
      height: "800px",
      panelClass: 'custom-modalbox',
      data: {
        frequencyList: this.frequencyList,
      }
    })
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.configs.push({
          ...result,
          categoryId: result.id,
          isChecked: false,
        });

      }
    });
  }
  delConfig() {
    this.configs = this.configs.filter(item => !item.isChecked);
  }
  checkChange(event, item) {

  }
  getFrequency(value) {
    let label = '';
    this.frequencyList?.forEach(item => {
      if (item.codeValue === value) {
        label = item.codeDsc;
      }
    })
    return label;
  }
}
