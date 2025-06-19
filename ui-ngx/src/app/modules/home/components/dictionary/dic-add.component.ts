import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, } from '@angular/material/dialog';


@Component({
  selector: 'tb-dic-add',
  templateUrl: './dic-add.component.html',
  styleUrls: ['./dic-add.component.scss']
})
export class DicAddComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<DicAddComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) { }

  ngOnInit(): void {
  }

  //关闭新增弹窗
  addDialogClose(): void {
    this.dialogRef.close();
  }

}
