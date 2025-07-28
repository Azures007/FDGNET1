import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';
import { FormBuilder, FormGroup, FormControl, Validators } from '@angular/forms';
import { Utils } from '../../pages/order-management/w-utils';
import { QualityService } from '@app/core/http/quality.service';
import { DictionaryService } from '@app/core/http/dictionary.service';
import { ChooseMaterialComponent } from '../material/choose-material/choose-material.component';

@Component({
  selector: 'tb-add-cate',
  templateUrl: './add-cate.component.html',
  styleUrls: ['./dialog.scss', './add-cate.component.scss']
})
export class AddCateComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<AddCateComponent>,
    @Inject(MAT_DIALOG_DATA) public injectData: any,
    private fb: FormBuilder,
    private utils: Utils,
    private qualityService: QualityService,
    private DictionaryService: DictionaryService,
    public _dialog: MatDialog,
  ) { }
  // 还原材料列表
  materialBoms = [];
  // 新增材料参数
  addParams = {
    id: "",
    inspectionItem: "",
    keyProcess: "",
    keyProcessName: '',
    monitoringMethod: '',
    monitoringMethodName: '',
    materialId: '',
    productName: '',
    isEnabled: '',
    standard: '',
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
  process = [];
  methods = [];
  fieldNames = []
  fieldTypes = [];
  configs = [];

  get modalTitle() {
    return this.formType ? (this.formType === 'details' ? '查看类目' : '编辑类目') : '新增类目';
  }

  ngOnInit(): void {
    let par = {
      current: 0,
      size: 999,
      codeClId: 'GJGX0000',
      // enabledSt: 1
    }
    this.DictionaryService.fetchGetTypeTableList({
      ...par,
      codeClId: 'QCCF0000',
    }).subscribe(res => {
      this.fieldNames = res.data.list;
    })
    if (this.utils.isEmpty(this.injectData.data)) {
      this.dataForm = this.fb.group({
        inspectionItem: ['', [Validators.required]],
        keyProcess: ['', [Validators.required]],
        monitoringMethod: ['', [Validators.required]],
        productName: ['', [Validators.required]],
        isEnabled: ['', [Validators.required]],
        standard: [''],
        remarks: [''],
      });
    } else {
      this.formType = this.injectData.data.type;
      const obj = {
        id: "",
        inspectionItem: "",
        keyProcess: "",
        monitoringMethod: '',
        materialId: '',
        productName: '',
        isEnabled: '',
        standard: '',
        remarks: ''
      }

      Object.keys(this.injectData.data.data).forEach(key => {
        obj[key] = this.injectData.data.data[key];
      });
      if (this.injectData.data.data.tSysQualityCategoryConfigList) {
        this.configs = this.injectData.data.data.tSysQualityCategoryConfigList;
      }
      this.productItem.productName = this.injectData.data.data.productName;
      this.productItem.materialId = this.injectData.data.data.materialId;
      if (this.formType === 'details') {
        this.dataForm = this.fb.group({
          inspectionItem: [{ value: obj.inspectionItem, disabled: true }],
          keyProcess: [{ value: obj.keyProcess, disabled: true }],
          monitoringMethod: [{ value: obj.monitoringMethod.split(','), disabled: true }],
          productName: [{ value: obj.productName, disabled: true }],
          isEnabled: [{ value: obj.isEnabled, disabled: true }],
          standard: [{ value: obj.standard, disabled: true }],
          remarks: [{ value: obj.remarks, disabled: true }],
        });
      } else {
        this.addParams.id = this.injectData.data.data.id;
        this.dataForm = this.fb.group({
          inspectionItem: [{ value: obj.inspectionItem, disabled: false }, [Validators.required]],
          keyProcess: [{ value: obj.keyProcess, disabled: false }, [Validators.required]],
          monitoringMethod: [{ value: obj.monitoringMethod.split(','), disabled: false }, [Validators.required]],
          productName: [{ value: obj.productName, disabled: false }, [Validators.required]],
          isEnabled: [{ value: obj.isEnabled, disabled: false }, [Validators.required]],
          standard: [{ value: obj.standard, disabled: false }],
          remarks: [{ value: obj.remarks, disabled: false }],
        });
      }
    }



    this.DictionaryService.fetchGetTypeTableList(par).subscribe(res => {
      this.process = res.data.list;
    })
    this.DictionaryService.fetchGetTypeTableList({
      ...par,
      codeClId: 'JKFF0000',
    }).subscribe(res => {
      this.methods = res.data.list;
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
      if(!this.configs.length) {
        // this.utils.showMessage('请添加配置项', 'error');
        // return;
      } else {
        const materialNameFlag = this.configs.filter(item => {
          return !item.materialName;
        })
        if(materialNameFlag?.length) {
          this.utils.showMessage('请选择品名', 'error');
          return;
        }
        const fieldTypeFlag = this.configs.filter(item => {
          return item.fieldType == 'ZDLX0004' && !item.dropdownFields;
        })
        if(fieldTypeFlag?.length) {
          this.utils.showMessage('请选择下拉项', 'error');
          return;
        }
      }
      this.addParams['keyProcessName'] = this.process.find(item => item.codeValue == this.addParams['keyProcess'])?.codeDsc;
      // this.addParams['monitoringMethodName'] = this.methods.find(item => item.codeValue == this.addParams['monitoringMethod'][0])?.codeDsc;
      let monitoringMethodName = [];
      (this.addParams['monitoringMethod'] as unknown as Array<string>).forEach(item => {
        monitoringMethodName.push(this.methods.find(subItem => subItem.codeValue == item)?.codeDsc);
      })
      this.addParams['monitoringMethodName'] = monitoringMethodName.join(',');
      const params = {
        tSysQualityCategory: {
          ...this.addParams,
          monitoringMethod: (this.addParams.monitoringMethod as unknown as string[]).join(','),

        },
        tSysQualityCategoryConfigList: this.configs,
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
  addConfig() {
    this.fieldNames.forEach(item => {
      let obj = {
        id: '',
        isChecked: false,
        materialName: '',
        materialId: '',
        fieldName: item.codeValue,
        isEnabled: '1',
        fieldType: 'ZDLX0001',
        parameterRange: '',
        dropdownFields: '',
        unit: '',
        isRequired: '1',
      }
      if (item.codeValue == 'QCCF0001') {
        obj.isEnabled = '1';
        obj.fieldType = 'ZDLX0001';
        obj.isRequired = '1';
      }
      if (item.codeValue == 'QCCF0002') {
        obj.isEnabled = '1';
        obj.fieldType = 'ZDLX0001';
        obj.isRequired = '1';
      }
      if (item.codeValue == 'QCCF0003') {
        obj.isEnabled = '1';
        obj.fieldType = 'ZDLX0001';
        obj.isRequired = '1';
      }
      if (item.codeValue == 'QCCF0004') {
        obj.isEnabled = '1';
        obj.fieldType = 'ZDLX0004';
        obj.isRequired = '1';
        obj.dropdownFields = '是，否'
      }
      this.configs.push(obj);
    })

  }

  checkChange(event, index) {
    for (let i = 0; i < this.fieldNames.length; i++) {
      this.configs[i + index].isChecked = event.checked;
    }
  }
  delConfig() {
    if(!this.configs.filter(item => item.isChecked).length) {
      return;
    }
    this.utils.confirm('温馨提示', `是否确认删除?`, () => {
      this.configs = this.configs.filter(item => {
        return !item.isChecked;
      })
    })
  }


  selectProduct() {
    let dialogRef = this._dialog.open(ChooseMaterialComponent, {
      width: "1400px",
      height: "800px",
      panelClass: 'custom-modalbox',
      data: {
        title: '选择品名'
      }
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
  selectMaterial(item, index) {
    let dialogRef = this._dialog.open(ChooseMaterialComponent, {
      width: "1400px",
      height: "800px",
      panelClass: 'custom-modalbox',
      data: {
        title: '选择品名'
      }
    })
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        for (let i = 0; i < this.fieldNames.length; i++) {
          this.configs[index + i].materialName = result.materialName;
          this.configs[index + i].materialId = result.id;
        }
      }
    });
  }
}
