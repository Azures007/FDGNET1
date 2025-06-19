import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'tb-dic-type-add',
  templateUrl: './dic-type-add.component.html',
  styleUrls: ['./dic-type-add.component.scss']
})
export class DicTypeAddComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<DicTypeAddComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) { }

  ngOnInit(): void {
  }

  //关闭新增弹窗
  addDialogClose(): void {
    this.dialogRef.close();
  }

}
