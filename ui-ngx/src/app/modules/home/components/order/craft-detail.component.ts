import { Component, OnInit, Inject } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'tb-craft-detail',
  templateUrl: './craft-detail.component.html',
  styleUrls: ['../material/dialog.scss', './craft-detail.component.scss']
})
export class CraftDetailComponent implements OnInit {

  constructor(
    @Inject(MAT_DIALOG_DATA) public injectData: any,
    public dialogRef: MatDialogRef<CraftDetailComponent>,
    public _dialog: MatDialog,
  ) { }
  displayedColumns: string[] = ['sort', 'processName', 'className', 'groupLeader'];
  ngOnInit(): void {
  }

  //关闭弹窗
  addDialogClose() {
    this.dialogRef.close();
  }

}
