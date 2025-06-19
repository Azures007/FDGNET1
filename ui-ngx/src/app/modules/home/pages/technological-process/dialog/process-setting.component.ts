import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormBuilder, Validators } from '@angular/forms';
import { Utils } from '../../order-management/w-utils';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';


import { TechnologicalService } from '@app/core/http/technological.service';
import { TransferComponent } from './transfer.component';

@Component({
  selector: 'tb-process-setting',
  templateUrl: './process-setting.html',
  styleUrls: ['./dialog.scss']
})
export class ProcessSettingComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<ProcessSettingComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private fb: FormBuilder,
    private utils: Utils,
    private api: TechnologicalService,
    public dialog: MatDialog
  ) { }

  joins = [];




  ngOnInit(): void {
    this.getClassForProcess();
  }

  // 获取当前工序下的班别

  getClassForProcess() {

    // console.log('this.data.processId', this.data.processId)

    this.api.fetchGetClassForProcess({
      processId: this.data.processId,
    }).subscribe(res => {
      if (res.errcode === 200) {
        const allData = res.data || [];
        this.joins = allData;

      } else {
        this.utils.showMessage(res.errmsg, 'error');
      }

    });
  }

  // 保存工序中的班别
  saveClass(data) {
    this.api.fetchSaveClass({
      processId: this.data.processId,
      body: data
    }).subscribe(res => {
      if (res.errcode === 200) {
        this.dialogRef.close('refresh');
      } else {
        this.utils.showMessage(res.errmsg, 'error');
      }

    });
  }


  // 关闭新增角色弹窗
  addDialogClose(): void {
    this.dialogRef.close();
  }

  // 打开选择班别框
  showTransfer(): void {

    const completeColumns = []

    this.api.fetchGetFiltterClassList({
      current: 0,
      size: 9999,
      body:{
        enabledSt:1,
        classNumber:"",
        name:""
      }
    }).subscribe(res => {

      if (res.errcode === 200) {
        const allData = res.data.list;
        allData.forEach((item, index) => {
          completeColumns.push({
            label: item.name,
            classId: item.classId,
            leader: item.groupLeader,
            teamNum: item.teamNum,
            direction: 'left',
            checked: false
          });
        });

        this.joins.forEach(item => {
          completeColumns.forEach(cItem => {
            if (item.classId === cItem.classId) {
              cItem.direction = 'right';
            }
          })
        });

        const dialogRef = this.dialog.open(TransferComponent, {
          width: '700px',
          height: 'auto',
          panelClass: 'custom-modalbox',
          data: completeColumns,
        })

        dialogRef.afterClosed().subscribe(res => {
          if (res.save != undefined) {
            this.joins = res.list;
          }
        })


      } else {
        this.utils.showMessage(res.errmsg, 'error');
      }

    });



  }


  // 删除标签
  delTag(index) {
    this.joins.splice(index, 1);
  }



  submit() {
    const processIds = [];

    this.joins.forEach(item => {
      processIds.push(item.classId);
    });

    if (processIds.length === 0) {
      this.utils.showMessage('未选择对应的班别', 'warn');
      return
    }

    this.saveClass(processIds);


  }

}
