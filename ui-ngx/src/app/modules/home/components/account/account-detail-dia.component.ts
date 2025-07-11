import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { ChooseBaseComponent } from './pick/choose-base.component';
import { Utils } from '../../pages/order-management/w-utils';
import { AlertDialogComponent } from '@app/shared/components/dialog/alert-dialog.component';
import { AccountService } from '@app/core/http/account.service';
@Component({
  selector: 'tb-account-detail-dia',
  templateUrl: './account-detail-dia.component.html',
  styleUrls: ['./account-detail-dia.component.scss']
})
export class AccountDetailDiaComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<AccountDetailDiaComponent>,
    private utils: Utils,
    public _dialog: MatDialog,
    private accountService: AccountService,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) { }

  //状态下拉
  status = [{
    label: "启用",
    value: "1",
  }, {
    label: "禁用",
    value: "0",
  }]
  baseList = [];

  ngOnInit(): void {
    this.accountService.fetchUserDetail(this.data.userDetail.user_id).subscribe(res => {
      this.baseList = res.data || [];
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
  DialogClose(): void {
    this.dialogRef.close();
  }
  DialogSaveClose() {
    let message = "";
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
