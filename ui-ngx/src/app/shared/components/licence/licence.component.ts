import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'tb-licence',
  templateUrl: './licence.html',
  styleUrls: ['./dialog.scss', './licence.scss']
})
export class LicenceComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<LicenceComponent>,
    @Inject(MAT_DIALOG_DATA) public injectData: any,
    public _dialog: MatDialog,
  ) { }
  ngOnInit(): void {

  }
  // 关闭新增角色弹窗
  addDialogClose(): void {
    this.dialogRef.close();
  }
}
