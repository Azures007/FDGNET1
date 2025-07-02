import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog,MatDialogConfig } from '@angular/material/dialog';
import { AlertDialogComponent } from '@app/shared/components/dialog/alert-dialog.component';
import { FormBuilder,FormGroup, FormControl, Validators } from '@angular/forms';
import { ChooseBaseComponent } from './pick/choose-base.component';
import { Utils } from '../../pages/order-management/w-utils';

@Component({
  selector: 'tb-account-add-dia',
  templateUrl: './account-add-dia.component.html',
  styleUrls: ['../material/dialog.scss','./account-add-dia.component.scss']
})
export class AccountAddDiaComponent implements OnInit {


  constructor(
    public dialogRef: MatDialogRef<AccountAddDiaComponent>,
    public _dialog: MatDialog,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private fb: FormBuilder,
    private utils: Utils,
  ) { }
  checkWord = "";

  dataForm:FormGroup;
  baseList = [];

  ngOnInit(): void {
    // console.log("13164360596".replace(/^[1][3,4,5,7,8][0-9]{9}$/),'验证')
    this.dataForm = this.fb.group({
      first_name:['', [Validators.required]],
      username:['', [Validators.maxLength(11),Validators.minLength(11),Validators.required]],
      password:['', [Validators.maxLength(6),Validators.minLength(6),Validators.required]]
    })
  }
  addBase(){
    this.baseList.push({
      ncOrgName:'',
      ncVwkname:'',
      ncWarehouseName:'',
    })
  }
  showBasePick(item,i){
    let dialogRef = this._dialog.open(ChooseBaseComponent, {
      width: "500px",
      height: "auto",
      panelClass: 'custom-modalbox',
      data: {
        title: '基地',
        type: 'base',
      }
    })
    dialogRef.afterClosed().subscribe(result => {
      if(result){
        this.baseList[i] = {
          ...this.baseList[i],
          ncOrgName:result.name,
          ncPkOrg:result.id,
        };
      }
    });
  }
  showLinePick(item,i){
    if(!item.ncPkOrg) {
      this.utils.showMessage('请先选择基地', 'error');
      return;
    }
    let dialogRef = this._dialog.open(ChooseBaseComponent, {
      width: "500px",
      height: "auto",
      panelClass: 'custom-modalbox',
      data: {
        title: '产线',
        type: 'line',
        id: item.ncPkOrg,
      }
    })
    dialogRef.afterClosed().subscribe(result => {
      if(result){
        this.baseList[i] = {
          ...this.baseList[i],
          ncVwkname:result.name,
          ncCwkid:result.id,
        };
      }
    });
  }
  showWarehousePick(item,i){
    if(!item.ncPkOrg) {
      this.utils.showMessage('请先选择基地', 'error');
      return;
    }

    let dialogRef = this._dialog.open(ChooseBaseComponent, {
      width: "500px",
      height: "auto",
      panelClass: 'custom-modalbox',
      data: {
        title: '仓库',
        type: 'warehouse',
        id: item.ncPkOrg,
      }
    })
    dialogRef.afterClosed().subscribe(result => {
      if(result){
        this.baseList[i] = {
          ...this.baseList[i],
          ncWarehouseName:result.name,
          ncWarehouseId:result.id,
        };
      }
    });
  }
  delBase(i){
    this.baseList.splice(i,1)
  }
  //关闭新增角色弹窗
  addDialogClose(): void {
    this.dialogRef.close();
  }

  addSubmitDialogClose() {
    let message = "";
    if (this.data.first_name == "") {
      message = "请输入用户名"
      this.showError(message);
      return
    }
    if (this.data.username == "") {
      message = "请输入登录账号(邮箱号)"
      this.showError(message);
      return
    }
    if (this.data.password == "") {
      message = "请输入密码"
      this.showError(message);
      return
    }
    if (this.data.password != this.checkWord) {
      message = "请确认两次密码输入一致"
      this.showError(message);
      return
    }
    if (this.baseList.length == 0) {
      message = "请添加基地"
      this.showError(message);
      return
    }
    const flag = this.baseList.find(item => !item.ncPkOrg);
    if(flag) {
      message = "请选择基地"
      this.showError(message);
      return
    }
    this.dialogRef.close({
      ...this.data,
      tsysUserDetailList: this.baseList,
    });
  }

  //提示消息
  showError(message) {
    let data = {
      title: "消息提示",
      message: message,
      ok: '确定',
    }
    const dialogConfig: MatDialogConfig = {
      disableClose: true,
      data: data
    };
    this._dialog.open(AlertDialogComponent, dialogConfig);
  }
}
