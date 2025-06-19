import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';
import { StaffService } from '@app/core/http/staff.service';

@Component({
  selector: 'tb-class-leader',
  templateUrl: './class-leader.component.html',
  styleUrls: ['./class-leader.component.scss']
})
export class ClassLeaderComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<ClassLeaderComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public _dialog: MatDialog,
    private StaffService:StaffService,
  ) { }

  //搜索参数
  searchPar = {
    current: 0,
    size: 999,
    body: {
      className: null,
      name: null ,
      sex: null,
      enabledSt:1,
    }
  }

  //当前选中
  checkData={
    personnelId:""
  };

  ngOnInit(): void {
    this.getTableData()
    this.dataCheck = this.data.leaderIdGroup;
  }

  //人员列表
  dataSource = [];

  //选中人员列表
  dataCheck = [];


  //关闭弹窗
  DialogClose(): void {
    this.dialogRef.close();
  }

  
  //获取表格数据
  getTableData(): void {
    this.StaffService.fetchGetTableList(this.searchPar).subscribe(res => {
      this.dataSource = res.data.list;
    })
  }

  //改变当前选中
  chooseCurrent(e){
    this.checkData = e;
  }

  //去选中列表
  goRight(){
    for(let i = 0;i<this.dataCheck.length;i++){
      if(this.dataCheck[i].personnelId == this.checkData.personnelId){
        return
      }
    }
    if(this.checkData.personnelId == ""){
      return
    }
    this.dataCheck.push(this.checkData);
  }

  //移除
  remove(index){
    this.dataCheck.splice(index,1)
  }


}
