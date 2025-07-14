import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';
import { FormBuilder, FormGroup, FormControl, Validators } from '@angular/forms';
import { Utils } from '../../pages/order-management/w-utils';
import { QualityService } from '@app/core/http/quality.service';
import { DictionaryService } from '@app/core/http/dictionary.service';
import { ChooseMaterialComponent } from '../material/choose-material/choose-material.component';

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
    materialId: '',
    productionLineId: '',
    isEnabled: '',
    remarks: ''
  }
  productItem = {
    productName: '',
    materialId: ''
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
  fieldTypes = [];
  judgeList = [];

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
      codeClId: 'QPJ0000',
      enabledSt: 1
    }
    if (this.utils.isEmpty(this.injectData.data)) {
      this.dataForm = this.fb.group({
        productName: ['', [Validators.required]],
        productionLineId: ['', [Validators.required]],
        isEnabled: ['', [Validators.required]],
        remarks: [''],
      });
    } else {
      this.formType = this.injectData.data.type;
      const obj = {
        id: "",
        productName: '',
        materialId: '',
        productionLineId: '',
        isEnabled: '',
        remarks: ''
      }

      Object.keys(this.injectData.data.data).forEach(key => {
        obj[key] = this.injectData.data.data[key];
      });
      this.productItem.productName = this.injectData.data.data.productName;
      this.productItem.materialId = this.injectData.data.data.materialId;
      if (this.formType === 'details') {
        this.dataForm = this.fb.group({
          productName: [{ value: obj.productName, disabled: true }],
          productionLineId: [{ value: obj.productionLineId, disabled: true }],
          isEnabled: [{ value: obj.isEnabled, disabled: true }],
          remarks: [{ value: obj.remarks, disabled: true }],
        });
      } else {
        this.addParams.id = this.injectData.data.data.id;
        this.dataForm = this.fb.group({
          productName: [{ value: obj.productName, disabled: false }, [Validators.required]],
          productionLineId: [{ value: obj.productionLineId, disabled: false }, [Validators.required]],
          isEnabled: [{ value: obj.isEnabled, disabled: false }, [Validators.required]],
          remarks: [{ value: obj.remarks, disabled: false }],
        });
      }
    }
    this.DictionaryService.fetchGetTypeTableList(par).subscribe(res => {
      this.judgeList = res.data.list?.map(item => {
        let obj = {
            fieldName: item.codeDsc,
            isEnabled: '1',
            fieldType: 'ZDLX0001',
            dropdownFields: '',
            unit: '',
            isRequired: '1'
          }
        if(item.codeValue == 'QPJ0001') {
          return {
            ...obj,
            fieldType: 'ZDLX0002',
          }
        }
        if(item.codeValue == 'QPJ0002') {
          return {
            ...obj,
            fieldType: 'ZDLX0001',
          }
        }
        if(item.codeValue == 'QPJ0003') {
          return {
            ...obj,
            fieldType: 'ZDLX0004',
            dropdownFields: '合格，不合格'
          }
        }
        if(item.codeValue == 'QPJ0004') {
          return {
            ...obj,
            fieldType: 'ZDLX0002',
          }
        }
        if(item.codeValue == 'QPJ0005') {
          return {
            ...obj,
            fieldType: 'ZDLX0001',
          }
        }
        if(item.codeValue == 'QPJ0006') {
          return {
            ...obj,
            fieldType: 'ZDLX0004',
            dropdownFields: '合格，不合格'
          }
        }
      });
    })
    this.DictionaryService.fetchGetTypeTableList({
      ...par,
      codeClId: 'ZDLX0000',
    }).subscribe(res => {
      this.fieldTypes = res.data.list;
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
      this.addParams['materialId'] = this.productItem.materialId;
      const params = {
        tSysQualityCategory: {
          ...this.addParams,

        },
      }
      this.qualityService.fetchSave(params).subscribe(res => {
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
          materialId: result.id
        }
      }
    });
  }

}
