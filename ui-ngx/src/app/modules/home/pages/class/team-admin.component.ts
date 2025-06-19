import { Component, OnInit } from '@angular/core';
import { ClassService } from '@app/core/http/class.service';
import { ActivatedRoute, Router } from '@angular/router';
import { StaffService } from '@app/core/http/staff.service';
import { DialogService } from '@app/core/public-api';
import { DictionaryService } from '@app/core/http/dictionary.service';


@Component({
  selector: 'tb-team-admin',
  templateUrl: './team-admin.component.html',
  styleUrls: ['./team-admin.component.scss']
})
export class TeamAdminComponent implements OnInit {

  constructor(
    private ClassService: ClassService,
    private StaffService: StaffService,
    private dialogService: DialogService,
    public route: ActivatedRoute,
    public router: Router,
    private DictionaryService: DictionaryService,
  ) { }

  //搜索参数
  searchPar = {
    current: 0,
    size: 999,
    body: {
      className: null,
      name: null,
      sex: null,
      enabledSt: 1,
    }
  }
  //班组信息
  classInfo = {
    name: "",
    classId: "",
  };

  //当前选中
  checkData = {
    personnelId: ""
  };

  //人员列表
  dataSource = [];

  //选中人员列表
  dataCheck = [];

  stationList = [];
  stationListMap = new Map();

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams) => {
      this.classInfo.name = queryParams.name;
      this.classInfo.classId = queryParams.classId;
    })
    this.getStationList();
  }

  //获取岗位列表
  getStationList() {
    let par = {
      current: 0,
      size: 999,
      codeClId: 'JOB0000',
      enabledSt: 1
    }
    this.DictionaryService.fetchGetTypeTableList(par).subscribe(res => {
      this.stationList = res.data.list;
      for (let i = 0; i < this.stationList.length; i++) {
        this.stationListMap.set(`${this.stationList[i].codeValue}`, this.stationList[i])
      }
      this.getTableData();
      this.getTeamList();
    })
  }

  handleStation(value) {
    if (this.stationListMap.get(value)) {
      return this.stationListMap.get(value).codeDsc;
    } else {
      return '---'
    }
  }

  //获取表格数据
  getTableData(): void {
    this.StaffService.fetchGetTableList(this.searchPar).subscribe(res => {
      this.dataSource = res.data.list
    })
  }

  //获取组员列表
  getTeamList() {
    this.ClassService.fetchGetTeamList(this.classInfo).subscribe(res => {
      console.log(res);
      this.dataCheck = res.data;
    })
  }

  //改变当前选中
  chooseCurrent(e) {
    this.checkData = e;
  }

  //去选中列表
  goRight() {
    for (let i = 0; i < this.dataCheck.length; i++) {
      if (this.dataCheck[i].personnelId == this.checkData.personnelId) {
        return
      }
    }
    if (this.checkData.personnelId == "") {
      return
    }
    this.dataCheck.push(this.checkData);
  }

  //移除
  remove(index) {
    this.dataCheck.splice(index, 1)
  }

  //保存组员
  saveList() {
    let par = {
      classId: this.classInfo.classId,
      personnelList: this.dataCheck
    }
    this.ClassService.fetchSaveTeam(par).subscribe(res => {
      let data = {
        title: "消息提示",
        message: "保存成功",
        ok: '确定',
      }
      this.dialogService.message(data).subscribe(res => {
        this.getTeamList();
      })
    })
  }

  //返回
  back() {
    this.router.navigate(['/class'], {
      skipLocationChange: false, queryParams: {

      }
    }).then()
  }

}
