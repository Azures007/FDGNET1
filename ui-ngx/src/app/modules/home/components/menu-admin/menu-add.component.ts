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
    menuType: 'link',
    br: "",
    sort: "",
    enabled: "",
    path: "",
    flag: "0",
    skipUrl: '',
    menuExplain: 'domain'
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
    label: '折叠菜单',
    value: 'toggle',
  }, {
    label: '菜单',
    value: 'link',
  }];
  menuFlagS = [{
    label: '菜单',
    value: '0',
  }, {
    label: '功能按钮',
    value: '1',
  }, {
    label: '外链URL',
    value: '2',
  }]



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
        parentId: [{ value: '-1', disabled: false }, [Validators.required]],
        menuType: [{ value: 'link', disabled: false }, [Validators.required]],
        br: ['', [Validators.required]],
        sort: ['', [Validators.required]],
        enabled: ['', [Validators.required]],
        path: ['', [Validators.required]],
        flag: [{ value: '0', disabled: false }, [Validators.required]],
        skipUrl: ['', []],
        menuExplain: ['domain', []]
      });
    } else {
      this.formType = this.injectData.data.type;
      const isDetail = this.formType === 'details';
      const obj = {
        menuId: "",
        menuName: '',
        parentId: -1,
        menuType: 'link',
        br: "",
        sort: "",
        enabled: "",
        path: "",
        flag: '0',
        skipUrl: '',
        menuExplain: 'domain'
      }
      Object.keys(this.injectData.data.data).forEach(key => {
        obj[key] = this.injectData.data.data[key];
      });
      this.dataForm = this.fb.group({
        menuId: obj.menuId,
        menuName: [{ value: obj.menuName, disabled: isDetail }, [Validators.required]],
        parentId: [{ value: obj.parentId, disabled: isDetail }, [Validators.required]],
        menuType: [{ value: obj.menuType, disabled: isDetail }, [Validators.required]],
        br: [{ value: obj.br, disabled: isDetail }, [Validators.required]],
        sort: [{ value: obj.sort, disabled: isDetail }, [Validators.required]],
        enabled: [{ value: obj.enabled, disabled: isDetail }, [Validators.required]],
        path: [{ value: obj.path, disabled: isDetail }, [Validators.required]],
        flag: [{ value: obj.flag, disabled: isDetail }, [Validators.required]],
        skipUrl: [{ value: obj.skipUrl, disabled: isDetail }, []],
        menuExplain: [{ value: obj.menuExplain, disabled: isDetail }, []]
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

    } else {
      for (const i in this.dataForm.controls) {
        this.dataForm.controls[i].markAsTouched();
        this.dataForm.controls[i].updateValueAndValidity();
      }
    }
  }

}
