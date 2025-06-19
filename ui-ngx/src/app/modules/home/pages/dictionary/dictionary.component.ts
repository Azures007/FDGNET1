import { Component, OnInit } from '@angular/core';
import { AuthService } from '@core/auth/auth.service';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { Router } from '@angular/router';
import { FormBuilder } from '@angular/forms';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { DialogService } from '@app/core/public-api';
import { TranslateService } from '@ngx-translate/core';
import { PageComponent } from '@shared/components/page.component';
import { Constants } from '@shared/models/constants';
import { OAuth2ClientInfo } from '@shared/models/oauth2.models';

import { DictionaryService } from '@app/core/http/dictionary.service';


import { DicTypeAddComponent } from '@home/components/dictionary/dic-type-add.component';
import { DicAddComponent } from '../../components/dictionary/dic-add.component';


@Component({
  selector: 'tb-dictionary',
  templateUrl: './dictionary.component.html',
  styleUrls: ['./dictionary.component.scss']
})
export class DictionaryComponent implements OnInit {

  constructor(
    protected store: Store<AppState>,
    private authService: AuthService,
    private dialogService: DialogService,
    private DictionaryService: DictionaryService,
    public fb: FormBuilder,
    private translate: TranslateService,
    public _dialog: MatDialog,
  ) { }

  //类型类表
  typeList = [];

  //字典表格数据
  dataSource = [];

  displayedColumns: string[] = ['codeClDsc', 'codeClId', 'codeDsc', 'codeValue', 'enabledSt', 'customColumn1'];

  //搜索参数
  searchFormGroup = this.fb.group({
    current: 0,
    size: 50,
    codeDsc: null,
    codeClId: null,
    enabledSt: '',
  });

  //翻页参数
  length: number;
  pageSize = 50;
  pageIndex = 1;
  pageSizeOptions: number[] = [50, 100, 300, 200, 500];
  total = 0;

  //当前字典类型
  currentType = null;

  btns = JSON.parse(localStorage.getItem('btns'));
  set = new Set(this.btns);


  ngOnInit(): void {
    this.getDicTypeList();
    this.getInitTableList();
  }


  //获取字典类型列表
  getDicTypeList() {
    this.DictionaryService.fetchGetTypeList().subscribe(res => {
      this.typeList = res.data
    })
  }

  //获取初始字典列表
  getInitTableList() {
    let par = {
      current: this.searchFormGroup.value.current,
      size: this.searchFormGroup.value.size,
      body: {
        codeDsc: this.searchFormGroup.value.codeDsc == "" ? null : this.searchFormGroup.value.codeDsc,
        codeClId: this.searchFormGroup.value.codeClId == "" ? null : this.searchFormGroup.value.codeClId,
        enabledSt: this.searchFormGroup.value.enabledSt == "" ? null : this.searchFormGroup.value.enabledSt,
      }
    }

    this.DictionaryService.fetchGetinitTableList(par).subscribe(res => {
      this.dataSource = res.data.list;
      this.total = res.data.total;
    })
  }

  //搜索查询
  searchTable() {
    this.searchFormGroup.value.current = 1
  }

  //新增字典类型
  handSaveAddType() {
    let data = {
      codeClDsc: "",
      codeClId: "",
      codeDsc: "",
      codeId: "",
      codeValue: "",
      crtTime: "",
      crtUser: "",
      enabledSt: "",
      updateTime: "",
      updateUser: ""
    }
    let dialogRef = this._dialog.open(DicTypeAddComponent, {
      width: "695px",
      height: "auto",
      panelClass: 'custom-modalbox',
      data: JSON.parse(JSON.stringify(data)),
    })
    dialogRef.afterClosed().subscribe(result => {
      if (result != undefined) {
        let parData = result;
        this.DictionaryService.fetchSaveDicType(parData).subscribe(res => {
          if (res.errcode == 0) {
            let data = {
              title: "消息提示",
              message: res.errmsg,
              ok: '确定',
            }
            this.dialogService.message(data).subscribe(res => {
              this.getDicTypeList();
            })
          } else {
            let data = {
              title: "消息提示",
              message: '新增字典类型成功',
              ok: '确定',
            }
            this.dialogService.message(data).subscribe(res => {
              this.getDicTypeList();
            })
          }
        })
      }
    })
  }

  //编辑字典类型
  handSaveEditType(value) {
    let dialogRef = this._dialog.open(DicTypeAddComponent, {
      width: "695px",
      height: "auto",
      panelClass: 'custom-modalbox',
      data: JSON.parse(JSON.stringify(value)),
    })
    dialogRef.afterClosed().subscribe(result => {
      if (result != undefined) {
        let parData = result;
        this.DictionaryService.fetchSaveDicType(parData).subscribe(res => {
          if (res.errcode == 0) {
            let data = {
              title: "消息提示",
              message: res.errmsg,
              ok: '确定',
            }
            this.dialogService.message(data).subscribe(res => {
              this.getDicTypeList();
            })
          } else {
            let data = {
              title: "消息提示",
              message: '保存字典类型成功',
              ok: '确定',
            }
            this.dialogService.message(data).subscribe(res => {
              this.getDicTypeList();
            })
          }

        })

      }
    })
  }

  //改变类型
  handleTypeChange(type) {
    this.currentType = type;
    let par;
    this.pageSize = 50;
    this.pageIndex = 0;
    this.searchFormGroup.value.codeDsc = null;
    if (type != null) {
      this.searchFormGroup.value.codeClId = this.currentType.codeValue;
      par = {
        codeClId: this.currentType.codeValue,
        current: this.pageIndex,
        size: this.pageSize,
      }

      this.DictionaryService.fetchGetTypeTableList(par).subscribe(res => {
        this.dataSource = res.data.list;
        this.total = res.data.total;
      })
    } else {
      this.searchFormGroup.value.codeClId = null;
      par = {
        current: this.pageIndex,
        size: this.pageSize,
        body: {
          codeDsc: null,
          codeClId: null,
          enabledSt: null,
        }
      }
      this.DictionaryService.fetchGetinitTableList(par).subscribe(res => {
        this.dataSource = res.data.list;
        this.total = res.data.total;
      })
    }



  }

  //删除字典类型
  handleDelType(value) {
    console.log(value);
    this.dialogService.confirm(
      '是否确定删除该字典分类',
      '删除该类型后不可恢复',
      this.translate.instant('action.no'),
      this.translate.instant('action.yes'),
      true
    ).subscribe(res => {
      if (res) {
        let params = {
          codeId: value.codeId
        }
        this.DictionaryService.fetchDeleteCode(params).subscribe(res => {
          if (res.errcode == 0) {
            let data = {
              title: "消息提示",
              message: res.errmsg,
              ok: '确定',
            }
            this.dialogService.message(data).subscribe(res => {
              this.getDicTypeList();
            })
          } else {
            let data = {
              title: "消息提示",
              message: '字典分类已删除',
              ok: '确定',
            }
            this.dialogService.message(data).subscribe(res => {
              this.getDicTypeList();
            })
          }

        })
      }
    })
  }

  //新增字典
  handSaveAdd() {
    let params = {
      codeClDsc: "",
      codeClId: "",
      codeDsc: "",
      codeId: "",
      codeValue: "",
      crtTime: "",
      crtUser: "",
      enabledSt: "",
      updateTime: "",
      updateUser: ""
    }
    let data = {
      params: params,
      typelsit: this.typeList
    }
    let dialogRef = this._dialog.open(DicAddComponent, {
      width: "695px",
      height: "auto",
      panelClass: 'custom-modalbox',
      data: JSON.parse(JSON.stringify(data)),
    })
    dialogRef.afterClosed().subscribe(result => {
      if (result != undefined) {
        let parData = result;
        let code = result.params.codeClId
        for (let i = 0; i < this.typeList.length; i++) {
          if (this.typeList[i].codeValue == code) {
            parData.params.codeClDsc = this.typeList[i].codeDsc
          }
        }
        this.DictionaryService.fetchSaveDic(parData.params).subscribe(res => {

          if (res.errcode == 0) {
            let data = {
              title: "消息提示",
              message: res.errmsg,
              ok: '确定',
            }
            this.dialogService.message(data).subscribe(res => {
              this.getInitTableList();
            })
          } else {
            let data = {
              title: "消息提示",
              message: '新增字典成功',
              ok: '确定',
            }
            this.dialogService.message(data).subscribe(res => {
              this.getInitTableList();
            })
          }


        })
      }
    })
  }

  //查看或编辑字典
  lookEle(value, n) {
    console.log(value)
    let params = JSON.parse(JSON.stringify(value));
    let data = {
      params: params,
      typelsit: this.typeList,
      edit: n == 0 ? true : false,
    }
    let dialogRef = this._dialog.open(DicAddComponent, {
      width: "695px",
      height: "auto",
      panelClass: 'custom-modalbox',
      data: JSON.parse(JSON.stringify(data)),
    })
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        let parData = result;
        this.DictionaryService.fetchSaveDic(parData.params).subscribe(res => {

          if (res.errcode == 0) {
            let data = {
              title: "消息提示",
              message: res.errmsg,
              ok: '确定',
            }
            this.dialogService.message(data).subscribe(res => {
              this.getInitTableList();
            })
          } else {
            let data = {
              title: "消息提示",
              message: '保存字典成功',
              ok: '确定',
            }
            this.dialogService.message(data).subscribe(res => {
              this.getInitTableList();
            })
          }


        })
      }
    })
  }

  //启用禁用字典
  enterDia(value, enbled) {
    let par = {
      codeId: value.codeId,
      enabledSt: enbled,
    }
    if (enbled == 1) {
      this.dialogService.confirm(
        '是否要启用该字典?',
        '确定后,该字典将启用',
        this.translate.instant('action.no'),
        this.translate.instant('action.yes'),
        true
      ).subscribe((res) => {
        if (res) {
          this.DictionaryService.fetchIsEnabled(par).subscribe(res => {

            let data = {
              title: "消息提示",
              message: '字典已启用',
              ok: '确定',
            }
            this.dialogService.message(data).subscribe(res => {
              this.getInitTableList();
            })


          })
        }
      }
      );
    } else {
      this.dialogService.confirm(
        "是否要禁用该字典?",
        '确定后,该字典将禁用',
        this.translate.instant('action.no'),
        this.translate.instant('action.yes'),
        true
      ).subscribe((res) => {
        if (res) {

          this.DictionaryService.fetchIsEnabled(par).subscribe(res => {

            let data = {
              title: "消息提示",
              message: '字典已禁用',
              ok: '确定',
            }
            this.dialogService.message(data).subscribe(res => {
              this.getInitTableList();
            })

          })
        }
      }
      );
    }
  }

  //删除字典
  delDia(value) {
    this.dialogService.confirm(
      '确定要删除字典吗？',
      '删除该字典后不可恢复',
      this.translate.instant('action.no'),
      this.translate.instant('action.yes'),
      true
    ).subscribe((res) => {
      if (res) {
        let params = {
          codeId: value.codeId
        }
        this.DictionaryService.fetchDeleteCode(params).subscribe(res => {
          let data = {
            title: "消息提示",
            message: '字典已删除',
            ok: '确定',
          }
          this.dialogService.message(data).subscribe(res => {
            this.getInitTableList();
          })
        })
      }
    }
    );

  }



  //翻页事件
  getNotices($event): any {
    console.log($event, '翻页器参数');
    // 点击paginator事件，获取pageIndex，重新加载页面
    this.searchFormGroup.value.current = $event.pageIndex;
    this.searchFormGroup.value.size = $event.pageSize;
    this.getInitTableList();
  }
}
