import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { AlertDialogComponent } from '@app/shared/components/dialog/alert-dialog.component';

@Component({
  selector: 'tb-account-reset-dia',
  templateUrl: './account-reset-dia.component.html',
  styleUrls: ['./account-reset-dia.component.scss']
})
export class AccountResetDiaComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<AccountResetDiaComponent>,
    public _dialog: MatDialog,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) { }

  ngOnInit(): void {
  }

  addDialogClose(): void {
    this.dialogRef.close();
  }


  addSubmitDialogClose() {
    let message = "";

    if (this.data.password == "") {
      message = "请输入密码"
      this.showError(message);
      return
    }
    if (this.data.password != this.data.checkWord) {
      message = "请确认两次密码输入一致"
      this.showError(message);
      return
    }
    this.dialogRef.close(this.data);
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
