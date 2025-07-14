import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';
import { FormBuilder, FormGroup, FormControl, Validators } from '@angular/forms';
import { Utils } from '../../pages/order-management/w-utils';
import { QualityService } from '@app/core/http/quality.service';
import { DictionaryService } from '@app/core/http/dictionary.service';
import { ChooseMaterialComponent } from '../material/choose-material/choose-material.component';

@Component({
  selector: 'tb-add-check-cate',
  templateUrl: './add-check-cate.component.html',
  styleUrls: ['./dialog.scss', './add-check-cate.component.scss']
})
export class AddCheckCateComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<AddCheckCateComponent>,
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
    frequency: "",
    importantItem: "",
    enabled: '',
    remark: '',
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
  fieldTypes = [];
  configs = [];

  get modalTitle() {
    return this.formType ? (this.formType === 'details' ? '查看类目' : '编辑类目') : '新增类目';
  }

  ngOnInit(): void {
    let par = {
      current: 0,
      size: 999,
      codeClId: 'FREQUENCY000',
      enabledSt: 1
    }
    if (this.utils.isEmpty(this.injectData.data)) {
      this.dataForm = this.fb.group({
        frequency: ['', [Validators.required]],
        importantItem: ['', [Validators.required]],
        enabled: ['', [Validators.required]],
        remark: [''],
      });
    } else {
      this.formType = this.injectData.data.type;
      const obj = {
        id: "",
        frequency: "",
        importantItem: "",
        enabled: '',
        remark: '',
      }

      Object.keys(this.injectData.data.data).forEach(key => {
        obj[key] = this.injectData.data.data[key];
      });
      if (this.injectData.data.data.itemList) {
        this.configs = this.injectData.data.data.itemList;
      }
      if (this.formType === 'details') {
        this.dataForm = this.fb.group({
          frequency: [{ value: obj.frequency, disabled: true }],
          importantItem: [{ value: obj.importantItem, disabled: true }],
          enabled: [{ value: '' + obj.enabled, disabled: true }],
          remark: [{ value: obj.remark, disabled: true }],
        });
      } else {
        this.addParams.id = this.injectData.data.data.id;
        this.dataForm = this.fb.group({
          frequency: [{ value: obj.frequency, disabled: false }, [Validators.required]],
          importantItem: [{ value: obj.importantItem, disabled: false }, [Validators.required]],
          enabled: [{ value: '' + obj.enabled, disabled: false }, [Validators.required]],
          remark: [{ value: obj.remark, disabled: false }],
        });
      }
    }



    this.DictionaryService.fetchGetTypeTableList(par).subscribe(res => {
      this.frequencyList = res.data.list;
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
      if(!this.configs.length) {
        this.utils.showMessage('请添加配置项', 'error');
        return;
      } else {
        const fieldNameFlag = this.configs.filter(item => {
          return !item.fieldName;
        })
        if(fieldNameFlag?.length) {
          this.utils.showMessage('请填写达成（异常）情况描述', 'error');
          return;
        }
        const fieldType1Flag = this.configs.filter(item => {
          return !item.fieldType;
        })
        if(fieldType1Flag?.length) {
          this.utils.showMessage('请选择达成情况类型', 'error');
          return;
        }
        const fieldTypeFlag = this.configs.filter(item => {
          return item.fieldType == 'ZDLX0004' && !item.dropdownFields;
        })
        if(fieldTypeFlag?.length) {
          this.utils.showMessage('请选择下拉项', 'error');
          return;
        }
        const requiredFlag = this.configs.filter(item => {
          return !item.required;
        })
        if(requiredFlag?.length) {
          this.utils.showMessage('请选择是否必填', 'error');
          return;
        }
      }
      const params = {
        ...this.addParams,
        itemList: this.configs,
      }
      this.qualityService.fetchSaveDaily(params).subscribe(res => {
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
    this.configs.push({
      isChecked: false,
      fieldName: '',
      fieldType: '',
      dropdownFields: '',
      required: ''
    });

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

}
