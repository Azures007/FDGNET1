import { Component, OnInit, Inject, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Utils } from '../../order-management/w-utils';

import { TechnologicalService } from '@app/core/http/technological.service';
import { ClassService } from '@app/core/http/class.service';
import { tr } from 'date-fns/locale';
import { TileStyler } from '@angular/material/grid-list/tile-styler';
@Component({
  selector: 'tb-transfer',
  templateUrl: './transfer.html',
  styleUrls: ['./dialog.scss', './transfer.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TransferComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<TransferComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private utils: Utils,
    private api: TechnologicalService,
    private classService: ClassService,
    private cd: ChangeDetectorRef,
  ) { }

  completeColumns = [];
  searchName: null;
  listLeft = [];
  listRight = [];

  transferLeft() {
    return this.completeColumns.filter(item => item.direction === 'left');
  }

  transferRight() {
    return this.completeColumns.filter(item => item.direction === 'right');
  }

  get someLeft() {
    return !this.listLeft.some(item => item.checked);
  }

  get someRight() {
    return !this.listRight.some(item => item.checked);
  }


  ngOnInit(): void {
    this.completeColumns = this.data;
    this.listLeft = this.transferLeft();
    this.listRight = this.transferRight();
    console.log(this.listRight,'right')
  }

  // 关闭新增角色弹窗
  addDialogClose(): void {
    this.dialogRef.close();
  }

  submit() {
    let data={
      save:true,
      list:this.listRight
    }
    this.dialogRef.close(data);
  }

  move(type) {
    if (type === 'right') {
      this.listLeft.forEach(item => {
        if (item.checked) {
          item.checked = false;
          item.direction = 'right';
        }
      });
      this.listLeft = this.transferLeft();
      this.listRight = this.transferRight();
    } else {
      this.listRight.forEach(item => {
        if (item.checked) {
          item.checked = false;
          item.direction = 'left';
        }
      });
      this.listLeft = this.transferLeft();
      this.listRight = this.transferRight();
    }
  }

  searchClass() {
    let par = {
      current: 0,
      size: 9999,
      body: {
        name: this.searchName == "" ? null : this.searchName,
        enabledSt: null
      }
    }
    this.classService.fetchGetTableList(par).subscribe(res => {
      if (res.errcode === 200) {
        const allData = res.data.list;
        this.listLeft = allData.map(item => {
          return {
            label: item.name,
            classId: item.classId,
            leader: item.groupLeader,
            teamNum: item.teamNum,
            direction: 'left',
            checked: false
          }
        })
        this.cd.detectChanges();
      }
    })
  }

}
