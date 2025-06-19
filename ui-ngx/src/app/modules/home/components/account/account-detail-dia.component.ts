import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'tb-account-detail-dia',
  templateUrl: './account-detail-dia.component.html',
  styleUrls: ['./account-detail-dia.component.scss']
})
export class AccountDetailDiaComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<AccountDetailDiaComponent>,
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


  ngOnInit(): void {
  }

  //关闭新增角色弹窗
  DialogClose(): void {
    this.dialogRef.close();
  }
  DialogSaveClose() {
    this.dialogRef.close(this.data);
  }

}
