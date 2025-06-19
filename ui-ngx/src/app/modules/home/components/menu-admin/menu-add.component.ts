import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';
import { FormBuilder, FormGroup, FormControl, Validators } from '@angular/forms';
import { Utils } from '../../pages/order-management/w-utils';
import { MenuAdminService } from '@app/core/http/menuAdmin.service';

@Component({
  selector: 'tb-menu-add',
  templateUrl: './menu-add.component.html',
  styleUrls: ['../material/dialog.scss', './menu-add.component.scss']
})
export class MenuAddComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<MenuAddComponent>,
    @Inject(MAT_DIALOG_DATA) public injectData: any,
    private fb: FormBuilder,
    private utils: Utils,
    public _dialog: MatDialog,
    private MenuAdminService: MenuAdminService
  ) { }


  //新增参数
  addParams = {
    menuId: "",
    menuName: '',
    parentId: -1,
    menuType: 2,
    br: "",
    sort: "",
    enabled: "",
    path:"",
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

  // 状态下拉
  menuTypeS = [{
    label: '菜单',
    value: '0',
  }, {
    label: '功能按钮',
    value: '1',
  }, {
    label: '外链URL',
    value: '2',
  }
  ];




  get modalTitle() {
    return this.formType ? (this.formType === 'details' ? '查看菜单' : '编辑菜单') : '新增菜单';
  }

  //初始化
  ngOnInit(): void {
    console.log(this.injectData, 'in')
    if (this.utils.isEmpty(this.injectData.data)) {
      this.dataForm = this.fb.group({
        menuId: "",
        menuName: ['', [Validators.required]],
        parentId: [{ value: '-1', disabled: true }, [Validators.required]],
        menuType: [{ value: '2', disabled: true }, [Validators.required]],
        br: ['', [Validators.required]],
        sort: ['', [Validators.required]],
        enabled: ['', [Validators.required]],
        path: ['', [Validators.required]],
      });
    } else {
      this.formType = this.injectData.data.type;
      const obj = {
        menuId: "",
        menuName: '',
        parentId: -1,
        menuType: 2,
        br: "",
        sort: "",
        enabled: "",
        path:"",
      }
      Object.keys(this.injectData.data.data).forEach(key => {
        obj[key] = this.injectData.data.data[key];
      });
    }
  }

  //关闭弹窗
  addDialogClose() {
    this.dialogRef.close();
  }

  //提交
  submit() {
    if (this.dataForm.valid) {
      Object.keys(this.dataForm.value).forEach(key => {
        this.addParams[key] = this.dataForm.value[key];
      });
      // if (!this.utils.isEmpty(this.injectData.data)) {
      //   this.addParams.createdName = this.injectData.data.data.createdName;
      //   this.addParams.createdTime = this.injectData.data.data.createdTime;
      // }

      this.MenuAdminService.fetchSave(this.addParams).subscribe(res => {
        if (res.errcode === 200) {
          this.dialogRef.close('refresh');
        } else {
          this.utils.showMessage(res.errmsg, 'error');
        }
      })

    }else {
      for (const i in this.dataForm.controls) {
        this.dataForm.controls[i].markAsTouched();
        this.dataForm.controls[i].updateValueAndValidity();
      }
    }
  }

}
