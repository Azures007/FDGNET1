import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';
import { ClassLeaderComponent } from './class-leader.component';
import { Utils } from '../../pages/order-management/w-utils';
import { RoleService } from '@app/core/http/role.service';
import { ClassService } from '@app/core/http/class.service';
import { DialogService } from '@app/core/public-api';
import { F } from '@angular/cdk/keycodes';
import { ChooseDirectorComponent } from './choose-director.component';

@Component({
  selector: 'tb-class-add',
  templateUrl: './class-add.component.html',
  styleUrls: ['../../../common/scss/dialog.common.scss', './class-add.component.scss']
})
export class ClassAddComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<ClassAddComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public _dialog: MatDialog,
    private utils: Utils,
    private roleService: RoleService,
    private dialogService: DialogService,
    private ClassService: ClassService,
  ) { }

  ngOnInit(): void {
    if (this.data.params.tSysClass.classId) {
      this.handleGetDeptChange(this.data.params.tSysClass.kdOrgId)
    } else {
      this.data.midDeptList = [];
    }
  }

  //关闭弹窗
  DialogClose(): void {
    this.dialogRef.close();
  }

  teamList = [];

  test(e) {
    console.log(e);
    console.log(this.data.params.tSysClass.scheduling, 'data');
  }




  //获取组织下车间列表
  handleGetDept(orgId) {
    this.roleService.fetchGetMidDept({ params: { current: 0, size: 999 }, body: { kdOrgId: orgId } }).subscribe(res => {
      this.data.midDeptList = res.data.list;
      this.data.params.tSysClass.kdDeptId = res.data.list[0].kdDeptId;
    })
  }

  handleGetDeptChange(orgId) {
    if (orgId == null) {
      this.data.midDeptList = []
    } else {
      // this.roleService.fetchGetMidDept({ params: { current: 0, size: 999 }, body: { kdOrgId: orgId } }).subscribe(res => {
      //   this.data.midDeptList = res.data.list;
      // this.data.params.kdDeptId = res.data.list[0].kdDeptId;
      // })
    }
  }


  //选择组长
  handleTeamLeader() {
    let data = {
      leaderIdGroup: []
    }

    if (this.teamList.length == 0) {
      data.leaderIdGroup = this.data.params.tsysClassGroupLeaderRelLits;
    } else {
      data.leaderIdGroup = this.teamList
    }

    let diaref = this._dialog.open(ClassLeaderComponent, {
      width: "695px",
      height: "auto",
      panelClass: 'custom-modalbox',
      data: JSON.parse(JSON.stringify(data))
    })
    diaref.afterClosed().subscribe(res => {
      if (res) {
        console.log(res, 'output')
        this.teamList = res;
        let leader = "";
        let leaderId = "";
        let tem = [];
        for (let i = 0; i < res.length; i++) {
          if (i == res.length - 1) {
            leader += res[i].name;
            leaderId += res[i].personnelId;
          } else {
            leader += `${res[i].name},`;
            leaderId += `${res[i].personnelId},`;
          }
          let par = {
            classGroupLeaderId: res[i].classGroupLeaderId ? res[i].classGroupLeaderId : "",
            classId: this.data.params.tSysClass.classId,
            personnelId: res[i].personnelId
          }
          tem.push(par);
        }
        this.data.params.tsysClassGroupLeaderRelLits = tem;
        this.data.params.tSysClass.groupLeader = leader;
        this.data.params.tSysClass.groupLeaderID = leaderId;
      }
    })
  }
  handleWorkshopDirector() {
    let dialogRef = this._dialog.open(ChooseDirectorComponent, {

      width: "1400px",
      height: "800px",
      panelClass: 'custom-modalbox',
      data: {}
    })
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.data.params.tSysClass.workshopDirector = result.name;
        this.data.params.tSysClass.workshopDirectorId = result.personnelId;
      }
    });
  }
  //提交
  submit(type) {
    console.log(this.data)
    if (this.data.params.tSysClass.name == "") {
      this.utils.showMessage("班别名称不能为空", 'error');
      return
    }
    if (this.data.params.tSysClass.process == "") {
      this.utils.showMessage("班别说明不能为空", 'error');
      return
    }
    if (this.data.params.tSysClass.groupLeader == "") {
      this.utils.showMessage("组长不能为空", 'error');
      return
    }
    if (this.data.params.tSysClass.workshopDirector == "") {
      this.utils.showMessage("车间主任不能为空", 'error');
      return
    }
    if (this.data.params.tSysClass.scheduling == "") {
      this.utils.showMessage("排班不能为空", 'error');
      return
    } else {
      let arr = this.data.schedulingList;
      for (let i = 0; i < arr.length; i++) {
        if (this.data.params.tSysClass.scheduling == arr[i].codeValue) {
          this.data.params.tSysClass.schedulingCodeDsc = arr[i].codeDsc;
        }
      }
    }
    if (this.data.params.tSysClass.belongProcessId == "" || this.data.params.tSysClass.belongProcessId == null) {
      this.utils.showMessage("工序不能为空", 'error');
      return
    }
    if (this.data.params.tSysClass.kdDeptId == "" || this.data.params.tSysClass.kdDeptId == null) {
      this.utils.showMessage("生产车间不能为空", 'error');
      return
    }
    if (this.data.params.tSysClass.kdOrgId == "" || this.data.params.tSysClass.kdOrgId == null) {
      this.utils.showMessage("生产组织不能为空", 'error');
      return
    }
    if (this.data.params.tSysClass.enabledSt == "") {
      this.utils.showMessage("状态不能为空", 'error');
      return
    }
    console.log(this.data.params, '提交参数');
    this.ClassService.fetchSaveAdd(this.data.params).subscribe(res => {
      if (res.errcode == 0) {
        let data = {
          title: "消息提示",
          message: res.errmsg,
          ok: '确定',
        }
        this.dialogService.message(data)
      } else {
        this.dialogRef.close(type);
      }
    })
  }
}
