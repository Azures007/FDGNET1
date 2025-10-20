import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';
import { Utils } from '../../pages/order-management/w-utils';

@Component({
  selector: 'tb-look-role-dialog',
  templateUrl: './look-role-dialog.component.html',
  styleUrls: ['./look-role-dialog.component.scss']
})
export class LookRoleDialogComponent implements OnInit {

  constructor(
    private utils: Utils,
    public dialogRef: MatDialogRef<LookRoleDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,) { }

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
  DialogClose(): void {
    this.dialogRef.close();
  }
  DialogSaveClose(){
    if(!this.data.roleDetail.roleName) {
      this.utils.showMessage('请输入角色名称', 'error');
      return;
    }
    this.dialogRef.close(this.data);
  }

}
