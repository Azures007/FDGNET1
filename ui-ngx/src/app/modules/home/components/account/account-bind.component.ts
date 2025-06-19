import { Component, OnInit, Inject, AfterViewInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { AccountService } from '@app/core/http/account.service';
import { DialogService } from '@app/core/public-api';

@Component({
  selector: 'tb-account-bind',
  templateUrl: './account-bind.component.html',
  styleUrls: ['../../../common/scss/dialog.common.scss', './account-bind.component.scss']
})
export class AccountBindComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<AccountBindComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private AccountService: AccountService,
    private DialogService: DialogService,
  ) { }

  //表格列参数
  displayedColumns: string[] = ["first_name", "username", "role_name", "customColumn1"]

  //当前选中账号
  currentAccount = "";
  accountData = {};

  //搜索参数
  name = ""

  ngOnInit(): void {
    console.log(this.data, '账号列表');
  }



  //关闭弹窗
  DialogClose(): void {
    this.dialogRef.close();
  }

  //获取列表
  getList() {
    let par = {
      value: this.name
    }
    this.AccountService.fetchGetDialogList(par).subscribe(res => {
      this.data.accountList = res.data;
    })
  }

  //提交
  submit() {
    if (this.currentAccount == "") {
      let data = {
        title: "消息提示",
        message: "请选择绑定账号",
        ok: '确定',
      }
      this.DialogService.message(data)
    } else {
      this.dialogRef.close(this.accountData)
    }
  }

  //改变当前选中
  currentChange(e) {
    this.currentAccount = e.username;
    this.accountData = e;
  }


}
