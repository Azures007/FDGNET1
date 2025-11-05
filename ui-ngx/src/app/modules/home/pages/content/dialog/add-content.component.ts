import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { Utils } from '../../order-management/w-utils';

import { DictionaryService } from '@app/core/http/dictionary.service';
import { ContentService } from '@app/core/http/content.service';
import { ChooseMaterialComponent } from '@app/modules/home/components/material/choose-material/choose-material.component';

@Component({
  selector: 'tb-add-content',
  templateUrl: './add-content.html',
  styleUrls: ['./dialog.scss']
})
export class AddContentComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<AddContentComponent>,
    @Inject(MAT_DIALOG_DATA) public injectData: any,
    private fb: FormBuilder,
    private utils: Utils,
    private contentService: ContentService,
    private DictionaryService: DictionaryService,
    public _dialog: MatDialog,
  ) { }

  // 保存参数
  addParams = {
    id: '',
    materialId: '',
    materialName: '',
    materialCode: '',
    materialModel: '',
    lowerLimit: '',
    upperLimit: '',
    status: '',
  };

  dataForm;

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
    return this.formType ? (this.formType === 'details' ? '查看净含量范围' : '编辑净含量范围') : '新增净含量范围';
  }

  ngOnInit(): void {
    console.log(this.injectData, '数据')

    if (!this.injectData.params.id) {
      this.dataForm = this.fb.group({
        materialName: [{ value: '', disabled: false }, [Validators.required]],
        materialCode: [{ value: '', disabled: true }, []],
        materialModel: [{ value: '', disabled: true }, []],
        lowerLimit: ['', [Validators.required, Validators.pattern(/^[0-9]+(\.[0-9]+)?$/)]],
        upperLimit: ['', [Validators.required, Validators.pattern(/^[0-9]+(\.[0-9]+)?$/)]],
        status: ['1', [Validators.required]],
      });
    } else {
      this.formType = this.injectData.edit ? 'edit' : 'details';


      const obj = {
        id: '',
        materialId: '',
        materialName: '',
        materialCode: "",
        materialModel: '',
        lowerLimit: '',
        upperLimit: '',
        status: '',
      };

      Object.keys(this.injectData.params).forEach(key => {
        obj[key] = this.injectData.params[key];
      });


      if (this.formType === 'details') {

        this.dataForm = this.fb.group({
          materialName: [{ value: obj.materialName, disabled: true }, [Validators.required]],
          materialCode: [{ value: obj.materialCode, disabled: true }, [Validators.required]],
          materialModel: [{ value: obj.materialModel, disabled: true }, [Validators.required]],
          lowerLimit: [{ value: obj.lowerLimit, disabled: true }, [Validators.required]],
          upperLimit: [{ value: obj.upperLimit, disabled: true }, [Validators.required]],
          status: [{ value: obj.status, disabled: true }, [Validators.required]],
        });
      } else {
        this.addParams.id = this.injectData.params.id;
        this.addParams.materialId = this.injectData.params.materialId;
        this.dataForm = this.fb.group({
          materialName: [{ value: obj.materialName, disabled: false }, [Validators.required]],
          materialCode: [{ value: obj.materialCode, disabled: false }, []],
          materialModel: [{ value: obj.materialModel, disabled: false }, []],
          lowerLimit: [{ value: obj.lowerLimit, disabled: false }, [Validators.required, Validators.pattern(/^[0-9]+(\.[0-9]+)?$/)]],
          upperLimit: [{ value: obj.upperLimit, disabled: false }, [Validators.required, Validators.pattern(/^[0-9]+(\.[0-9]+)?$/)]],
          status: [{ value: obj.status, disabled: false }, [Validators.required]],
        });
      }

    }
  }
  // 关闭新增角色弹窗
  addDialogClose(): void {
    this.dialogRef.close();
  }
  selectProduct() {
    let dialogRef = this._dialog.open(ChooseMaterialComponent, {
      width: "1400px",
      height: "800px",
      panelClass: 'custom-modalbox',
      data: {
        title: '选择产品'
      }
    })
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.dataForm.setControl('materialName', new FormControl(result.materialName, [Validators.required]));
        this.dataForm.setControl('materialCode', new FormControl(result.materialCode, []));
        this.dataForm.setControl('materialModel', new FormControl(result.materialModel, []));
        this.addParams.materialId = result.id;
      }
    });
  }
  submit() {

    if (this.dataForm.valid) {

      Object.keys(this.dataForm.value).forEach(key => {
        this.addParams[key] = this.dataForm.value[key];
      });

      this.contentService.fetchSaveProcess(this.addParams).subscribe(res => {
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
