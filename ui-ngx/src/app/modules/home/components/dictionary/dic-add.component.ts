import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, } from '@angular/material/dialog';
import { Utils } from '../../pages/order-management/w-utils';


@Component({
  selector: 'tb-dic-add',
  templateUrl: './dic-add.component.html',
  styleUrls: ['./dic-add.component.scss']
})
export class DicAddComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<DicAddComponent>,
    private utils: Utils,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) { }

  ngOnInit(): void {
  }

  //关闭新增弹窗
  addDialogClose(): void {
    this.dialogRef.close();
  }
  submit() {
    if(!this.data.params.codeClId){
      this.utils.showMessage('请选择字典分类', 'error');
      return;
    }
    if(!this.data.params.codeDsc){
      this.utils.showMessage('请输入字典名称', 'error');
      return;
    }
    if(!this.data.params.codeValue){
      this.utils.showMessage('请输入字典编码', 'error');
      return;
    }
    if(!this.data.params.enabledSt){
      this.utils.showMessage('请选择状态', 'error');
      return;
    }
    if(this.data.params.codeClId === 'QCCF0000') {
      this.utils.confirm('温馨提示', `本次操作会修改所有【品质类目管理】中配置信息对应内容，是否确定操作?`, () => {
        this.dialogRef.close(this.data);
      })
    } else {
      this.dialogRef.close(this.data);
    }

  }

}
