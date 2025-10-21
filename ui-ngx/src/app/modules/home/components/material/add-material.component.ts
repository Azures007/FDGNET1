import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';
import { FormBuilder, FormGroup, FormControl, Validators } from '@angular/forms';
import { Utils } from '../../pages/order-management/w-utils';
import { MaterialService } from '@app/core/http/material.service';
import { DictionaryService } from '@app/core/http/dictionary.service';
import { RoleService } from '@app/core/http/role.service';
import { ChooseMaterialComponent } from './choose-material/choose-material.component';

@Component({
  selector: 'tb-add-material',
  templateUrl: './add-material.component.html',
  styleUrls: ['./dialog.scss', './add-material.component.scss']
})
export class AddMaterialComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<AddMaterialComponent>,
    @Inject(MAT_DIALOG_DATA) public injectData: any,
    private fb: FormBuilder,
    private utils: Utils,
    private MaterialService: MaterialService,
    private DictionaryService: DictionaryService,
    public _dialog: MatDialog,
    private roleService: RoleService,
  ) { }
  // 还原材料列表
  materialBoms = [];
  // 新增材料参数
  addParams = {
    id: "",
    materialName: "",
    materialCode: "",
    materialModel: "",
    ncMaterialCategory: "",
    ncMaterialMainCategory: "",
    ncMaterialClassification: "",
    materialStatus: "",
    materialUnit: "",
    ncMaterialQualityUnit: "",
    ncMaterialQualityNum: "",
    br: "",
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
  //单位列表
  units = [];
  qualityUnit = [];

  get modalTitle() {
    return this.formType ? (this.formType === 'details' ? '查看物料' : '编辑物料') : '新增物料';
  }

  ngOnInit(): void {
    let par = {
      current: 0,
      size: 999,
      codeClId: 'UNIT0000',
      enabledSt: 1
    }
    if (this.utils.isEmpty(this.injectData.data)) {
      this.dataForm = this.fb.group({
        materialName: ['', [Validators.required]],
        materialCode: ['', [Validators.required]],
        materialModel: ['', [Validators.required]],
        ncMaterialCategory: ['', [Validators.required]],
        ncMaterialMainCategory: ['', [Validators.required]],
        ncMaterialClassification: ['', [Validators.required]],
        materialStatus: ['1', [Validators.required]],
        materialUnit: ['', [Validators.required]],
        ncMaterialQualityUnit: ['', [Validators.required]],
        // 校验正整数
        ncMaterialQualityNum: ['', [Validators.required, Validators.pattern(/^[1-9]\d*$/)]],
        br: "",
      });
    } else {
      this.formType = this.injectData.data.type;
      const obj = {
        id: "",
        materialName: "",
        materialCode: "",
        materialModel: "",
        ncMaterialCategory: "",
        ncMaterialMainCategory: "",
        ncMaterialClassification: "",
        materialStatus: "",
        materialUnit: "",
        ncMaterialQualityUnit: "",
        ncMaterialQualityNum: "",
        br: "",
      }

      Object.keys(this.injectData.data.data).forEach(key => {
        obj[key] = this.injectData.data.data[key];
      });
      if (this.injectData.data.data.materialBoms) {
        this.materialBoms = this.injectData.data.data.materialBoms;
      }
      if (this.formType === 'details') {
        this.dataForm = this.fb.group({
          materialName: [{ value: obj.materialName, disabled: true }],
          materialCode: [{ value: obj.materialCode, disabled: true }],
          materialModel: [{ value: obj.materialModel, disabled: true }],
          ncMaterialCategory: [{ value: obj.ncMaterialCategory, disabled: true }],
          ncMaterialMainCategory: [{ value: obj.ncMaterialMainCategory, disabled: true }],
          ncMaterialClassification: [{ value: obj.ncMaterialClassification, disabled: true }],
          materialStatus: [{ value: obj.materialStatus, disabled: true }],
          materialUnit: [{ value: obj.materialUnit, disabled: true }],
          ncMaterialQualityUnit: [{ value: obj.ncMaterialQualityUnit, disabled: true }],
          ncMaterialQualityNum: [{ value: obj.ncMaterialQualityNum, disabled: true }],
          br: [{ value: obj.br, disabled: true }],
        });
      } else {
        this.addParams.id = this.injectData.data.data.id;
        this.dataForm = this.fb.group({
          materialName: [{ value: obj.materialName, disabled: false }, [Validators.required]],
          materialCode: [{ value: obj.materialCode, disabled: false }, [Validators.required]],
          materialModel: [{ value: obj.materialModel, disabled: false }, [Validators.required]],
          ncMaterialCategory: [{ value: obj.ncMaterialCategory, disabled: false }, [Validators.required]],
          ncMaterialMainCategory: [{ value: obj.ncMaterialMainCategory, disabled: false }, [Validators.required]],
          ncMaterialClassification: [{ value: obj.ncMaterialClassification, disabled: false }, [Validators.required]],
          materialStatus: [{ value: obj.materialStatus, disabled: false }, [Validators.required]],
          materialUnit: [{ value: obj.materialUnit, disabled: false }, [Validators.required]],
          ncMaterialQualityUnit: [{ value: obj.ncMaterialQualityUnit, disabled: false }, [Validators.required]],
          ncMaterialQualityNum: [{ value: obj.ncMaterialQualityNum, disabled: false }, [Validators.required]],
          br: [{ value: obj.br, disabled: false }],
        });
      }
    }
    this.DictionaryService.fetchGetTypeTableList(par).subscribe(res => {
      this.units = res.data.list;
    })
    this.DictionaryService.fetchGetTypeTableList({
      ...par,
      codeClId: 'QUALITY_UNIT_0000',
    }).subscribe(res => {
      this.qualityUnit = res.data.list;
    })
  }


  //获取组织下车间列表
  handleGetDept(orgId) {
    this.roleService.fetchGetMidDept({ params: { current: 0, size: 999 }, body: { kdOrgId: orgId } }).subscribe(res => {
      this.injectData.midDeptList = res.data.list;
      // this.injectData.params.kdDeptId = res.data.list[0].midDeptId;
      console.log(this.dataForm)
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
      if(this.materialBoms.length) {
        this.addParams['materialBoms'] = this.materialBoms;
        const items = this.materialBoms.find(item => {
          return !item.ratio;
        })
        if(items) {
          this.utils.showMessage('请输入比例', 'error');
          return;
        }
        const checkRatio = this.materialBoms.filter(item => {
          // 判断ratio是否为为小数位小于等于10位的数字
          return !/^\d+(\.\d{0,10})?$/.test(item.ratio);
        })
        if(checkRatio?.length) {
          this.utils.showMessage(checkRatio.map(item => item.materialName).join('、') + '还原比例输入格式为最大10位小数', 'error');
          return;
        }
      }
      this.MaterialService.fetchSave(this.addParams).subscribe(res => {
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
  getLabelByQualityUnit(value) {
    let label = '';
    this.qualityUnit.forEach(item => {
      if (item.codeValue === value) {
        label = item.codeDsc;
      }
    })
    return label;
  }
  addMaterial() {
    let dialogRef = this._dialog.open(ChooseMaterialComponent, {
      width: "1400px",
      height: "800px",
      panelClass: 'custom-modalbox',
      data: {}
    })
    dialogRef.afterClosed().subscribe(result => {
      if(result){
        this.materialBoms.push({
          materialId: result.id,
          materialName: result.materialName,
          ratio: '',
        })
      }
    });
  }
  selectMaterial(index) {

  }
  delMaterial(index) {
    this.materialBoms.splice(index, 1);
  }
}
