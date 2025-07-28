import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, } from '@angular/material/dialog';
import { Utils } from '../../pages/order-management/w-utils';
import { RoleService } from '@app/core/http/role.service';

@Component({
  selector: 'tb-my-devive-add',
  templateUrl: './my-devive-add.component.html',
  styleUrls: ['./my-devive-add.component.scss']
})
export class MyDeviveAddComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<MyDeviveAddComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private utils: Utils,
    private roleService: RoleService,
  ) { }

  ngOnInit(): void {
    if (this.data.params.deviceId) {
      this.handleGetDeptChange(this.data.params.kdOrgId)
    } else {

    }
  }

  //获取组织下车间列表
  handleGetDept(orgId) {
    this.roleService.fetchGetMidDept({ params: { current: 0, size: 999 }, body: { kdOrgId: orgId } }).subscribe(res => {
      this.data.midDeptList = res.data.list;
      this.data.params.kdDeptId = res.data.list[0].midDeptId;
    })
  }

  handleGetDeptChange(orgId) {
    if (orgId == null) {
      this.data.midDeptList = []
    } else {
      this.roleService.fetchGetMidDept({ params: { current: 0, size: 999 }, body: { kdOrgId: orgId } }).subscribe(res => {
        this.data.midDeptList = res.data.list;
      })
    }
  }

  //关闭弹窗
  DialogClose(): void {
    this.dialogRef.close();
  }

  submit() {
    console.log(this.data.params.midDeptId);
    if (this.data.params.deviceName == "" || this.data.params.deviceName == null) {
      this.utils.showMessage("设备名称不能为空", 'error');
      return
    }
    if (this.data.params.deviceNumber == "" || this.data.params.deviceNumber == null) {
      this.utils.showMessage("设备编码不能为空", 'error');
      return
    }
    if (this.data.params.belongProcessId == "" || this.data.params.belongProcessId == null) {
      this.utils.showMessage("工序不能为空", 'error');
      return
    }
    if (this.data.params.pkOrg == "" || this.data.params.pkOrg == null) {
      this.utils.showMessage("基地不能为空", 'error');
      return
    }
    if (this.data.params.enabled == "" || this.data.params.enabled == null) {
      this.utils.showMessage("状态不能为空", 'error');
      return
    }
    this.dialogRef.close(this.data);
  }

  test(e) {
    console.log(e)
  }
}
