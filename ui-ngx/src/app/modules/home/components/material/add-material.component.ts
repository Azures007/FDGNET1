import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';
import { FormBuilder, FormGroup, FormControl, Validators } from '@angular/forms';
import { Utils } from '../../pages/order-management/w-utils';
import { MaterialService } from '@app/core/http/material.service';
import { DictionaryService } from '@app/core/http/dictionary.service';
import { RoleService } from '@app/core/http/role.service';

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

  // 新增物料参数
  addParams = {
    id: "",
    br: "",
    groupCode: "",
    materialCode: "",
    materialModel: "",
    materialName: "",
    materialStatus: "",
    materialUnit: "",
    createdName: "",
    createdTime: "",
    kdMaterialStretchWeight: "", // 拉伸膜后重量、单支克重
    kdMaterialEachPieceNum: "", // 每件支数
    // 膜厚度、膜宽度、膜密度
    kdMaterialMembraneThickness: "",
    kdMaterialMembraneWidth: "",
    kdMaterialMembraneDensity: "",
    kdMaterialUseOrgId: "",
    kdMaterialWorkshopId: "",
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
        groupCode: ['', [Validators.required]],
        materialCode: ['', [Validators.required]],
        materialName: ['', [Validators.required]],
        materialModel: "",
        materialStatus: ['', [Validators.required]],
        materialUnit: ['', [Validators.required]],
        kdMaterialStretchWeight: "",
        kdMaterialEachPieceNum: "",
        kdMaterialMembraneThickness: "",
        kdMaterialMembraneWidth: "",
        kdMaterialMembraneDensity: "",
        kdMaterialUseOrgId: "",
        kdMaterialWorkshopId: "",
        br: "",
        createdName: "",
        createdTime: "",
      });
    } else {
      this.formType = this.injectData.data.type;
      const obj = {
        br: "",
        groupCode: "",
        materialCode: "",
        materialModel: "",
        materialName: "",
        materialStatus: "",
        materialUnit: "",
        createdName: "",
        createdTime: "",
        kdMaterialStretchWeight: "",
        kdMaterialEachPieceNum: "",
        kdMaterialMembraneThickness: "",
        kdMaterialMembraneWidth: "",
        kdMaterialMembraneDensity: "",
        kdMaterialUseOrgId: "",
        kdMaterialWorkshopId: ""
      }

      Object.keys(this.injectData.data.data).forEach(key => {
        obj[key] = this.injectData.data.data[key];
      });
      if(this.injectData.data.data.kdMaterialUseOrgId != ""){
        this.handleGetDept(this.injectData.data.data.kdMaterialUseOrgId);
      }

      if (this.formType === 'details') {
        this.dataForm = this.fb.group({
          groupCode: [{ value: obj.groupCode, disabled: true }, [Validators.required]],
          materialCode: [{ value: obj.materialCode, disabled: true }, [Validators.required]],
          materialName: [{ value: obj.materialName, disabled: true }, [Validators.required]],
          materialModel: [{ value: obj.materialModel, disabled: true },],
          materialStatus: [{ value: obj.materialStatus, disabled: true }, [Validators.required]],
          materialUnit: [{ value: obj.materialUnit, disabled: true }, [Validators.required]],
          kdMaterialStretchWeight: [{ value: obj.kdMaterialStretchWeight, disabled: true }],
          kdMaterialEachPieceNum: [{ value: obj.kdMaterialEachPieceNum, disabled: true }],
          kdMaterialMembraneThickness: [{ value: obj.kdMaterialMembraneThickness, disabled: true }],
          kdMaterialMembraneWidth: [{ value: obj.kdMaterialMembraneWidth, disabled: true }],
          kdMaterialMembraneDensity: [{ value: obj.kdMaterialMembraneDensity, disabled: true }],
          kdMaterialUseOrgId: [{ value: obj.kdMaterialUseOrgId, disabled: true }],
          kdMaterialWorkshopId: [{ value: obj.kdMaterialWorkshopId, disabled: true }],
          br: [{ value: obj.br, disabled: true },],
        });
      } else {
        this.addParams.id = this.injectData.data.data.id;
        this.dataForm = this.fb.group({
          groupCode: [{ value: obj.groupCode, disabled: false }, [Validators.required]],
          materialCode: [{ value: obj.materialCode, disabled: false }, [Validators.required]],
          materialName: [{ value: obj.materialName, disabled: false }, [Validators.required]],
          materialModel: [{ value: obj.materialModel, disabled: false }],
          materialStatus: [{ value: obj.materialStatus, disabled: false }, [Validators.required]],
          materialUnit: [{ value: obj.materialUnit, disabled: false }, [Validators.required]],
          kdMaterialStretchWeight: [{ value: obj.kdMaterialStretchWeight, disabled: true }],
          kdMaterialEachPieceNum: [{ value: obj.kdMaterialEachPieceNum, disabled: true }],
          kdMaterialMembraneThickness: [{ value: obj.kdMaterialMembraneThickness, disabled: true }],
          kdMaterialMembraneWidth: [{ value: obj.kdMaterialMembraneWidth, disabled: true }],
          kdMaterialMembraneDensity: [{ value: obj.kdMaterialMembraneDensity, disabled: true }],
          kdMaterialUseOrgId: [{ value: obj.kdMaterialUseOrgId, disabled: false }],
          kdMaterialWorkshopId: [{ value: obj.kdMaterialWorkshopId, disabled: false }],
          br: [{ value: obj.br, disabled: false }],
        });
      }
    }
    this.DictionaryService.fetchGetTypeTableList(par).subscribe(res => {
      this.units = res.data.list;
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
      if (!this.utils.isEmpty(this.injectData)) {
        this.addParams.createdName = this.injectData.data.createdName;
        this.addParams.createdTime = this.injectData.data.createdTime;
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
}
