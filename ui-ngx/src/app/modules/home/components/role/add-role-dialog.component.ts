import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'tb-add-role-dialog',
  templateUrl: './add-role-dialog.component.html',
  styleUrls: ['./add-role-dialog.component.scss']
})
export class AddRoleDialogComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<AddRoleDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    ) { }

  //状态下拉
  status = [{
    label: "启用",
    value: "0",
  }, {
    label: "禁用",
    value: "1",
  }]

  ngOnInit(): void {
  }

  //关闭新增角色弹窗
  addDialogClose(): void {
    this.dialogRef.close();
  }

}
