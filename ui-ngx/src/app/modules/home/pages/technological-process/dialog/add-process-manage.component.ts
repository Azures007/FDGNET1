import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormBuilder, Validators } from '@angular/forms';
import { Utils } from '../../order-management/w-utils';

import { TechnologicalService } from '@app/core/http/technological.service';
import { DictionaryService } from '@app/core/http/dictionary.service';

@Component({
  selector: 'tb-add-process-manage',
  templateUrl: './add-process-manage.html',
  styleUrls: ['./dialog.scss']
})
export class AddProcessManageComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<AddProcessManageComponent>,
    @Inject(MAT_DIALOG_DATA) public injectData: any,
    private fb: FormBuilder,
    private utils: Utils,
    private api: TechnologicalService,
    private DictionaryService: DictionaryService,
  ) { }

  // 保存参数
  addParams = {
    createdUser: '',
    createdTime: '',
    enabled: '',
    processDetail: '',
    processId: '',
    processName: '',
    processNumber: '',
    // erpProcessNumber: '',
    updatedTime: '',
    updatedUser: '',
    // fingerprintAuthentication:""
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

  erpProcessList = [];


  get modalTitle() {
    return this.formType ? (this.formType === 'details' ? '查看工序' : '编辑工序') : '新增工序';
  }

  ngOnInit(): void {
    this.getErpProList();
    if (this.utils.isEmpty(this.injectData)) {
      this.dataForm = this.fb.group({
        processName: ['', [Validators.required]],
        processNumber: ['', [Validators.required]],
        // erpProcessNumber: ['', [Validators.required]],
        enabled: ['', [Validators.required]],
        // bySetImport: [0, [Validators.required]],
        // bySetExport: [0, [Validators.required]],
        // fingerprintAuthentication: [0, [Validators.required]],
        processDetail: '',
      });
    } else {
      this.formType = this.injectData.type;

      const obj = {
        processName: '',
        processNumber: "",
        // erpProcessNumber: "",
        enabled: 0,
        processDetail: '',
        // bySetImport: 0,
        // bySetExport:0,
        // fingerprintAuthentication:0,
      };

      Object.keys(this.injectData.data).forEach(key => {
        obj[key] = this.injectData.data[key];
      });


      if (this.formType === 'details') {
        console.log(obj, '数据')

        this.dataForm = this.fb.group({
          processName: [{ value: obj.processName, disabled: true }, [Validators.required]],
          processNumber: [{ value: obj.processNumber, disabled: true }, [Validators.required]],
          // erpProcessNumber: [{ value: obj.erpProcessNumber, disabled: true }, [Validators.required]],
          enabled: [{ value: obj.enabled, disabled: true }, [Validators.required]],
          // bySetImport: [{ value: obj.bySetImport ? Number(obj.bySetImport) : 0, disabled: true }, [Validators.required]],
          // bySetExport: [{ value: obj.bySetExport ? Number(obj.bySetExport) : 0, disabled: true }, [Validators.required]],
          // fingerprintAuthentication: [{ value: obj.fingerprintAuthentication ? Number(obj.fingerprintAuthentication) : 0, disabled: true }, [Validators.required]],
          processDetail: [{ value: obj.processDetail, disabled: true }],
        });
      } else {
        this.addParams.processId = this.injectData.data.processId;

        this.dataForm = this.fb.group({
          processName: [{ value: obj.processName, disabled: false }, [Validators.required]],
          processNumber: [{ value: obj.processNumber, disabled: false }, [Validators.required]],
          // erpProcessNumber: [{ value: obj.erpProcessNumber, disabled: false }, [Validators.required]],
          enabled: [{ value: obj.enabled, disabled: false }, [Validators.required]],
          // bySetImport: [{ value: obj.bySetImport ? Number(obj.bySetImport) : 0, disabled: false }, [Validators.required]],
          // bySetExport: [{ value: obj.bySetExport ? Number(obj.bySetExport) : 0, disabled: false }, [Validators.required]],
          processDetail: [{ value: obj.processDetail, disabled: false }],
          // fingerprintAuthentication: [{ value: obj.fingerprintAuthentication ? Number(obj.fingerprintAuthentication) : 0, disabled: false }, [Validators.required]],
        });
      }

    }
  }

  //获取erp工序

  getErpProList() {
    let par = {
      current: 0,
      size: 999,
      codeClId: 'ERPPROCESS0000',
      enabledSt: 1
    }
    this.DictionaryService.fetchGetTypeTableList(par).subscribe(res => {
      console.log(res, 'erp工序');
      this.erpProcessList = res.data.list;
    })
  }


  // 关闭新增角色弹窗
  addDialogClose(): void {
    this.dialogRef.close();
  }

  submit() {

    const userInfo = JSON.parse(localStorage.getItem('userInfo'));

    if (this.dataForm.valid) {

      Object.keys(this.dataForm.value).forEach(key => {
        this.addParams[key] = this.dataForm.value[key];
      });

      this.addParams.createdTime = this.utils.dateFormat(new Date(), 'yyyy-MM-ddThh:mm:ssZ');
      this.addParams.createdUser = userInfo.firstName;

      this.api.fetchSaveProcess(this.addParams).subscribe(res => {
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
