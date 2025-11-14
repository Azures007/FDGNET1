import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';
import { Utils } from '../../pages/order-management/w-utils';

@Component({
  selector: 'tb-dic-type-add',
  templateUrl: './dic-type-add.component.html',
  styleUrls: ['./dic-type-add.component.scss']
})
export class DicTypeAddComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<DicTypeAddComponent>,
    private utils: Utils,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) { }

  ngOnInit(): void {
  }

  //关闭新增弹窗
  addDialogClose(): void {
    this.dialogRef.close();
  }
  commit(){
    if(!this.data.codeValue){
      this.utils.showMessage('请输入类型编码', 'error');
      return;
    }
    if(!this.data.codeDsc){
      this.utils.showMessage('请输入类型名称', 'error');
      return;
    }
    this.dialogRef.close(this.data);
  }

}
