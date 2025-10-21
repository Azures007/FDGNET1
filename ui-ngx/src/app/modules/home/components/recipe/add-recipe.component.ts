import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';
import { FormBuilder, FormGroup, FormControl, Validators } from '@angular/forms';
import { Utils } from '../../pages/order-management/w-utils';
import { DictionaryService } from '@app/core/http/dictionary.service';
import { ChooseMaterialComponent } from '../material/choose-material/choose-material.component';
import { AuthService } from '@app/core/auth/auth.service';
import { AccountService } from '@app/core/http/account.service';
import { RecipeService } from '@app/core/http/recipe.service';

@Component({
  selector: 'tb-add-recipe',
  templateUrl: './add-recipe.component.html',
  styleUrls: ['./dialog.scss', './add-recipe.component.scss']
})
export class AddRecipeComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<AddRecipeComponent>,
    @Inject(MAT_DIALOG_DATA) public injectData: any,
    private fb: FormBuilder,
    private utils: Utils,
    private recipeService: RecipeService,
    private DictionaryService: DictionaryService,
    public _dialog: MatDialog,
    private authService: AuthService,
    private accountService: AccountService
  ) { }
  // 还原材料列表
  materialBoms = [];
  // 新增材料参数
  addParams = {
    recipeId: "",
    recipeName: "",
    recipeCode: "",
    pkOrg: '',
    status: '',
    recipeDescription: '',
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
  pkOrgList = [];
  unitList = [];
  get modalTitle() {
    return this.formType ? (this.formType === 'details' ? '查看配方' : '编辑配方') : '新增配方';
  }

  ngOnInit(): void {
    let par = {
      current: 0,
      size: 999,
      codeClId: 'UNIT0000',
      // enabledSt: 1
    }
    this.DictionaryService.fetchGetTypeTableList({
      ...par,
    }).subscribe(res => {
      this.unitList = res.data.list;
    })
    this.authService.getCurrentLine().subscribe((res: any) => {
      if (res.data && res.data.pkOrg) {
        // 只显示用户当前选择的基地
        const currentOrgId = res.data.pkOrg;
        this.accountService.getOrgList().subscribe(orgRes => {
          // 过滤出用户当前选择的基地
          const currentOrg = orgRes.data.find(item => item.pk_org === currentOrgId);
          if (currentOrg) {
            this.pkOrgList = [{
              name: currentOrg.org_name,
              id: currentOrg.pk_org,
            }];
          }
        });
      } else {
        // 如果没有当前选择的基地，则获取所有基地（回退方案）
        this.accountService.getOrgList().subscribe(res => {
          this.pkOrgList = res.data.map(item => {
            return {
              name: item.org_name,
              id: item.pk_org,
            }
          });
        });
      }
    });
    if (this.utils.isEmpty(this.injectData.data)) {
      this.dataForm = this.fb.group({
        recipeName: ['', [Validators.required]],
        recipeCode: ['', [Validators.required]],
        pkOrg: ['', [Validators.required]],
        status: ['1', [Validators.required]],
        recipeDescription: [''],
        creator: [{ value: '提交自动生成', disabled: true }],
        createTime: [{ value: '提交自动生成', disabled: true }],
      });
    } else {
      this.formType = this.injectData.data.type;
      const obj = {
        recipeId: "",
        recipeName: "",
        recipeCode: "",
        pkOrg: '',
        status: '',
        recipeDescription: '',
        creator: '',
        createTime: '',
      }
      Object.keys(this.injectData.data.data.recipe).forEach(key => {
        obj[key] = this.injectData.data.data.recipe[key];
      });
      if (this.injectData.data.data.recipeInputs) {
        this.configs = this.injectData.data.data.recipeInputs;
      }
      if (this.formType === 'details') {
        this.dataForm = this.fb.group({
          recipeName: [{ value: obj.recipeName, disabled: true }],
          recipeCode: [{ value: obj.recipeCode, disabled: true }],
          pkOrg: [{ value: obj.pkOrg, disabled: true }],
          status: [{ value: obj.status, disabled: true }],
          recipeDescription: [{ value: obj.recipeDescription, disabled: true }],
          creator: [{ value: obj.creator, disabled: true }],
          createTime: [{ value: obj.createTime, disabled: true }],
        });
      } else {
        this.addParams.recipeId = this.injectData.data.data.recipeId;
        this.dataForm = this.fb.group({
          recipeName: [{ value: obj.recipeName, disabled: false }, [Validators.required]],
          recipeCode: [{ value: obj.recipeCode, disabled: false }, [Validators.required]],
          pkOrg: [{ value: obj.pkOrg, disabled: false }, [Validators.required]],
          status: [{ value: obj.status, disabled: false }, [Validators.required]],
          recipeDescription: [{ value: obj.recipeDescription, disabled: false }],
          creator: [{ value: obj.creator, disabled: true }],
          createTime: [{ value: obj.createTime, disabled: true }],
        });
      }
    }

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
      if (this.injectData.data?.data?.recipe?.recipeId) {
        this.addParams['recipeId'] = this.injectData.data.data.recipe.recipeId;
      } else {
        delete this.addParams.recipeId;
      }
      if(!this.configs.length) {
        this.utils.showMessage('请添加投入设置', 'error');
        return;
      } else {
        // 校验物料名称不能为空
        if (this.configs.some(item => !item.materialName)) {
          this.utils.showMessage('请选择物料名称', 'error');
          return;
        }
        // 校验每锅投入标准不能为空，且为数字，可以为小数
        if (this.configs.some(item => !item.standardInput || isNaN(Number(item.standardInput)))) {
          this.utils.showMessage('请填写每锅投入标准，且为数字', 'error');
          return;
        }
        // 校验单位为必填
        if (this.configs.some(item => !item.unit)) {
          this.utils.showMessage('请选择单位', 'error');
          return;
        }
        // 校验投入下限比例为必填，且为数字，可以为小数
        if (this.configs.some(item => !item.lowerLimitRatio || isNaN(Number(item.lowerLimitRatio)))) {
          this.utils.showMessage('请填写投入下限比例，且为数字', 'error');
          return;
        }
        // 校验投入上限比例为必填,且为数字，可以为小数
        if (this.configs.some(item => !item.upperLimitRatio || isNaN(Number(item.upperLimitRatio)))) {
          this.utils.showMessage('请填写投入上限比例，且为数字', 'error');
          return;
        }
        // 校验投入下限比例不能大于投入上限比例
        if (this.configs.some(item => Number(item.lowerLimitRatio) > Number(item.upperLimitRatio))) {
          this.utils.showMessage('投入下限比例不能大于投入上限比例', 'error');
          return;
        }
        // 校验工序必填
        if (this.configs.some(item => !item.processNumber)) {
          this.utils.showMessage('请选择工序', 'error');
          return;
        }
      }
      const orgName = this.pkOrgList.find(item => item.id == this.dataForm.value.pkOrg)?.name;
      const params = {
        recipe: {
          ...this.addParams,
          orgName
        },
        recipeInputs: this.configs,
      }
      this.recipeService.fetchSave(params).subscribe(res => {
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
  get isAllChecked() {
    if(this.configs.length === 0) {
      return false;
    }
    return this.configs.every(item => item.isChecked);
  }
  set isAllChecked(value) {
    this.configs.forEach(item => {
      item.isChecked = value;
    })
  }
  addConfig() {
    this.configs.push({
      isChecked: false,
      materialName: '',
      materialCode: '',
      standardInput: '',
      unit: '',
      lowerLimitRatio: '',
      upperLimitRatio: '',
      processNumber: '',
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

  selectMaterial(item, index) {
    let dialogRef = this._dialog.open(ChooseMaterialComponent, {
      width: "1400px",
      height: "800px",
      panelClass: 'custom-modalbox',
      data: {
        title: '选择物料'
      }
    })
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.configs[index].materialName = result.materialName;
        this.configs[index].materialCode = result.materialCode;
      }
    });
  }
}
