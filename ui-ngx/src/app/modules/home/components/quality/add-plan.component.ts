import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';
import { FormBuilder, FormGroup, FormControl, Validators } from '@angular/forms';
import { Utils } from '../../pages/order-management/w-utils';
import { QualityService } from '@app/core/http/quality.service';
import { DictionaryService } from '@app/core/http/dictionary.service';
import { ChooseMaterialComponent } from '../material/choose-material/choose-material.component';
import { ChooseCateComponent } from './choose-cate.component';

@Component({
  selector: 'tb-add-plan',
  templateUrl: './add-plan.component.html',
  styleUrls: ['./dialog.scss', './add-plan.component.scss']
})
export class AddPlanComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<AddPlanComponent>,
    @Inject(MAT_DIALOG_DATA) public injectData: any,
    private fb: FormBuilder,
    private utils: Utils,
    private qualityService: QualityService,
    private DictionaryService: DictionaryService,
    public _dialog: MatDialog,
  ) { }
  // 基础列表
  baseList = [];

  configs = [];
  // 新增材料参数
  addParams = {
    id: "",
    planName: '',
    productionLineId: '',
    productionLineName: '',
    isEnabled: '',
    remarks: ''
  }
  dataForm: FormGroup;
  fieldNames = [];
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
          if (item.codeValue == 'QPJ0001') {
            return {
              ...obj,
              fieldType: 'ZDLX0002',
            }
          }
          if (item.codeValue == 'QPJ0002') {
            return {
              ...obj,
              fieldType: 'ZDLX0001',
            }
          }
          if (item.codeValue == 'QPJ0003') {
            return {
              ...obj,
              fieldType: 'ZDLX0004',
              dropdownFields: '合格，不合格'
            }
          }
          if (item.codeValue == 'QPJ0004') {
            return {
              ...obj,
              fieldType: 'ZDLX0002',
            }
          }
          if (item.codeValue == 'QPJ0005') {
            return {
              ...obj,
              fieldType: 'ZDLX0001',
            }
          }
          if (item.codeValue == 'QPJ0006') {
            return {
              ...obj,
              fieldType: 'ZDLX0004',
              dropdownFields: '合格，不合格'
            }
          }
        });
      })
      this.dataForm = this.fb.group({
        planName: ['', [Validators.required]],
        productionLineId: ['', [Validators.required]],
        isEnabled: ['', [Validators.required]],
        remarks: [''],
      });
    } else {
      this.formType = this.injectData.data.type;
      const obj = {
        id: "",
        planName: '',
        productionLineId: '',
        isEnabled: '',
        remarks: ''
      }

      Object.keys(this.injectData.data.data).forEach(key => {
        obj[key] = this.injectData.data.data[key];
      });
      if (this.injectData.data.data.tSysQualityPlanJudgmentList) {
        this.judgeList = this.injectData.data.data.tSysQualityPlanJudgmentList;
      }
      if (this.injectData.data.data.tSysQualityPlanConfigList) {
        this.configs = this.injectData.data.data.tSysQualityPlanConfigList.map(item => {
          return {
            ...item,
            configData: JSON.parse(item.configData)
          }
        });

      }
      if (this.formType === 'details') {
        this.dataForm = this.fb.group({
          planName: [{ value: obj.planName, disabled: true }],
          productionLineId: [{ value: obj.productionLineId, disabled: true }],
          isEnabled: [{ value: obj.isEnabled, disabled: true }],
          remarks: [{ value: obj.remarks, disabled: true }],
        });
      } else {
        this.addParams.id = this.injectData.data.data.id;
        this.dataForm = this.fb.group({
          planName: [{ value: obj.planName, disabled: false }, [Validators.required]],
          productionLineId: [{ value: obj.productionLineId, disabled: false }, [Validators.required]],
          isEnabled: [{ value: obj.isEnabled, disabled: false }, [Validators.required]],
          remarks: [{ value: obj.remarks, disabled: false }],
        });
      }
    }

    this.DictionaryService.fetchGetTypeTableList({
      ...par,
      codeClId: 'ZDLX0000',
    }).subscribe(res => {
      this.fieldTypes = res.data.list;
    })
    this.DictionaryService.fetchGetTypeTableList({
      ...par,
      codeClId: 'QCCF0000',
      enabledSt: undefined
    }).subscribe(res => {
      this.fieldNames = res.data.list;
    })
  }
  //关闭弹窗
  addDialogClose() {
    this.dialogRef.close();
  }
  getArray() {
    return new Array(this.fieldNames.length + 1);
  }
  getFieldValue(item) {
    const arr = [];
    item.configData.forEach(subItem => {
      const arr1 = [];
      arr1.push(subItem.productName);
      this.fieldNames.forEach(fieldItem => {
        const itemTemp = subItem.fieldList.find(item1 => item1.fieldName == fieldItem.codeValue);
        if (fieldItem.codeValue == itemTemp.fieldName) {
          if(itemTemp.fieldName=='QCCF0001') {
            if(itemTemp.isEnabled != '1') {
              arr1.push('/');
            } else {
              arr1.push(itemTemp.paramRange)
            }
          } else {
            arr1.push(itemTemp.isEnabled == '1' ? '' : '/');
          }
        }

      })
      arr.push(arr1);
    })
    return arr;
  }
  submit() {
    if (this.dataForm.valid) {
      const judgeListFlag = this.judgeList.find(item => item.fieldType == 'ZDLX0004' && !item.dropdownFields);
      if (judgeListFlag) {
        this.utils.showMessage('请填写下拉框字段', 'error');
        return;
      }
      if (!this.configs.length) {
        this.utils.showMessage('请新增配置项', 'error');
        return;
      }
      Object.keys(this.dataForm.value).forEach(key => {
        this.addParams[key] = this.dataForm.value[key];
      });
      if (this.injectData.data?.data?.id) {
        this.addParams['id'] = this.injectData.data.data.id;
      }
      this.addParams['productionLineName'] = this.baseList.find(item => item.cwkid === this.addParams['productionLineId'])?.vwkname;
      const configs = this.configs.map(item => {
        return {
          ...item,
          configData: JSON.stringify(item.configData),
        }
      })
      const params = {
        tSysQualityPlan: this.addParams,
        tSysQualityPlanJudgmentList: this.judgeList,
        tSysQualityPlanConfigList: configs,
      }
      this.qualityService.fetchSavePlan(params).subscribe(res => {
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
  addConfig() {
    let dialogRef = this._dialog.open(ChooseCateComponent, {
      width: "1400px",
      height: "800px",
      panelClass: 'custom-modalbox',
      data: {
        configs: this.configs,
      }
    })
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        const tmp = JSON.parse(JSON.stringify(result)).map(item => {
          return {
            ...item,
            isChecked: false,
          }
        })
        this.configs.push(...tmp);
      }
    });
  }
  delConfig() {
    this.configs = this.configs.filter(item => !item.isChecked);
  }
}
