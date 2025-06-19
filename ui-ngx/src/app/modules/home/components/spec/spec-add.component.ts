import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormBuilder, Validators } from '@angular/forms';
import { Utils } from '../../pages/order-management/w-utils';

import { RoleService } from '@app/core/http/role.service';
import { SpecService } from '@app/core/http/spec.servive';


@Component({
  selector: 'tb-spec-add',
  templateUrl: './spec-add.component.html',
  styleUrls: ['../material/dialog.scss', './spec-add.component.scss']
})
export class SpecAddComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<SpecAddComponent>,
    @Inject(MAT_DIALOG_DATA) public injectData: any,
    private fb: FormBuilder,
    private utils: Utils,
    private roleService: RoleService,
    private SpecService: SpecService,
  ) { }

  // 保存参数
  addParams = {
    abrasiveSpecificationId: '',
    abrasiveSpecificationNo: '',
    abrasiveSpecificationRemark: '',
    abrasiveSpecificationSeq: '',
    abrasiveSpecificationStatus: '',
    createdName: '',
    createdTime: '',
    isDeleted: '',
    updatedTime: '',
    updatedUser: '',
    kdOrgId: "",
    kdWorkshopId: "",
    mathByQty: "",
    mathByWeight: "",
    singleGramQty: "",
    version: "",
  };

  dataForm;

  formType = '';

  // 状态下拉
  status = [{
    label: '启用',
    value: 1,
  }, {
    label: '禁用',
    value: 0,
  }];

  addNew = false;


  get modalTitle() {
    return this.formType ? (this.formType === 'details' ? '查看磨具' : '编辑磨具') : '新增磨具';
  }


  ngOnInit(): void {
    if (this.utils.isEmpty(this.injectData.data)) {
      this.addNew = true;
      this.dataForm = this.fb.group({
        abrasiveSpecificationNo: ['', [Validators.required]],
        abrasiveSpecificationRemark: ['', [Validators.required]],
        abrasiveSpecificationSeq: ['', [Validators.required]],
        abrasiveSpecificationStatus: ['', [Validators.required]],
        kdOrgId: ['', [Validators.required]],
        kdWorkshopId: ['', [Validators.required]],
        mathByQty: ['', [Validators.required]],
        mathByWeight: ['', [Validators.required]],
        singleGramQty: ['', [Validators.required]],
      })
      this.injectData.midDeptList = [];
    } else {
      console.log(this.injectData.data, '111')

      const obj = {
        abrasiveSpecificationId: '',
        abrasiveSpecificationNo: '',
        abrasiveSpecificationRemark: '',
        abrasiveSpecificationSeq: '',
        abrasiveSpecificationStatus: '',
        createdName: '',
        createdTime: '',
        isDeleted: '',
        updatedTime: '',
        updatedUser: '',
        kdOrgId: "",
        kdWorkshopId: "",
        mathByQty: "",
        mathByWeight: "",
        singleGramQty: "",
        version: "",
      };
      Object.keys(this.injectData.data.data).forEach(key => {
        obj[key] = this.injectData.data.data[key];
      });

      this.dataForm = this.fb.group({
        abrasiveSpecificationNo: [{ value: obj.abrasiveSpecificationNo, disabled: true }, [Validators.required]],
        abrasiveSpecificationRemark: [{ value: obj.abrasiveSpecificationRemark, disabled: true }, [Validators.required]],
        abrasiveSpecificationSeq: [{ value: obj.abrasiveSpecificationSeq, disabled: true }, [Validators.required]],
        abrasiveSpecificationStatus: [{ value: Number(obj.abrasiveSpecificationStatus), disabled: true }, [Validators.required]],
        kdOrgId: [{ value: obj.kdOrgId, disabled: true }, [Validators.required]],
        kdWorkshopId: [{ value: obj.kdWorkshopId, disabled: true }, [Validators.required]],
        mathByQty: [{ value: obj.mathByQty, disabled: true }, [Validators.required]],
        mathByWeight: [{ value: obj.mathByWeight, disabled: true }, [Validators.required]],
        singleGramQty: [{ value: obj.singleGramQty, disabled: true }, [Validators.required]],
      })
    }
  }

  addDialogClose() {
    this.dialogRef.close();
  }

  //获取组织下车间列表
  handleGetDept(orgId) {
    this.roleService.fetchGetMidDept({ params: { current: 0, size: 999 }, body: { kdOrgId: orgId } }).subscribe(res => {
      this.injectData.midDeptList = res.data.list;
      // this.injectData.params.kdDeptId = res.data.list[0].midDeptId;
      console.log(this.dataForm)
    })
  }

  handleGetDeptChange(orgId) {
    if (orgId == null) {
      this.injectData.midDeptList = []
    } else {
      this.roleService.fetchGetMidDept({ params: { current: 0, size: 999 }, body: { kdOrgId: orgId } }).subscribe(res => {
        this.injectData.midDeptList = res.data.list;
      })
    }
  }

  //提交
  submit() {
    if (this.dataForm.valid) {
      Object.keys(this.dataForm.value).forEach(key => {
        this.addParams[key] = this.dataForm.value[key];
      });
      if (!this.utils.isEmpty(this.injectData.data)) {
        this.addParams.createdName = this.injectData.data.data.createdName;
        this.addParams.createdTime = this.injectData.data.data.createdTime;
      }
      console.log(this.addParams)
      this.SpecService.fetchSave(this.addParams).subscribe(res => {
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
