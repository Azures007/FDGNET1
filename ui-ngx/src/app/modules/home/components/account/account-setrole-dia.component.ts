import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { AlertDialogComponent } from '@app/shared/components/dialog/alert-dialog.component';


@Component({
  selector: 'tb-account-setrole-dia',
  templateUrl: './account-setrole-dia.component.html',
  styleUrls: ['./account-setrole-dia.component.scss']
})
export class AccountSetroleDiaComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<AccountSetroleDiaComponent>,
    public _dialog: MatDialog,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) { }

  ngOnInit(): void {
  }

  dialogClose() {
    this.dialogRef.close()
  }

  DialogSaveClose() {
    if (!this.data.roleId) {
      this.showError("请选择角色");
      return;
    }
    this.dialogRef.close(this.data.roleId);
  }
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
