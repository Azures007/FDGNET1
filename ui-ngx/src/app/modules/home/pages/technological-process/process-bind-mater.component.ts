import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { TechnologicalService } from '@app/core/http/technological.service';
import { MaterialService } from '@app/core/http/material.service';
import { DialogService } from '@app/core/public-api';

@Component({
  selector: 'tb-process-bind-mater',
  templateUrl: './process-bind-mater.component.html',
  styleUrls: ['./process-bind-mater.component.scss']
})
export class ProcessBindMaterComponent implements OnInit {

  constructor(
    private TechnologicalService: TechnologicalService,
    private MaterialService: MaterialService,
    private dialogService: DialogService,
    public route: ActivatedRoute,
    public router: Router
  ) { }

  //搜索参数
  searchFormGroup = {
    current: 0,
    size: 50,
    materialCode: "",
  };

  //路线信息
  craftInfo = {
    name: "",
    craftId: "",
    kdDeptId: "",
    kdOrgId: "",
  };

  //当前选中
  checkData = {
    id: ""
  };
  checkIndex: null;

  //物料列表
  dataSource = [];

  //选中物料列表
  dataCheck = [];
  searchDataCheck = [];
  searchDataCheckShow: false;
  //搜索变量
  materialNameCheck: "";
  //备份已选中列表
  dataCheckCopy = [];


  //翻页参数
  length: number;
  pageSize = 50;
  pageIndex = 0;
  pageSizeOptions: number[] = [50, 100, 300, 200, 500];
  total = 0;

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams) => {
      this.craftInfo.name = queryParams.name;
      this.craftInfo.craftId = queryParams.craftId;
      this.craftInfo.kdDeptId = queryParams.kdDeptId;
      this.craftInfo.kdOrgId = queryParams.kdOrgId;
      this.getTableData();
    })
  }


  //获取列表数据
  getTableData(): void {
    let par = {
      current: 0,
      size: 50,
      craftId: this.craftInfo.craftId,
      kdDeptId: this.craftInfo.kdDeptId,
      kdOrgId: this.craftInfo.kdOrgId,
      materialCode: '',
    }
    this.MaterialService.fetchGetFiterTableList(par).subscribe(res => {
      this.dataSource = res.data.noMaterial.list;
      this.dataCheck = res.data.offMaterial;
	  this.dataCheckCopy = res.data.offMaterial;
      this.total = res.data.noMaterial.total;
      this.pageIndex = res.data.noMaterial.current;
      console.log(res.data)
    })
  }

  //搜索列表
  searchList() {
    let par = {
      current: this.searchFormGroup.current,
      size: this.searchFormGroup.size,
      craftId: this.craftInfo.craftId,
      materialCode: this.searchFormGroup.materialCode,
      kdDeptId: this.craftInfo.kdDeptId,
      kdOrgId: this.craftInfo.kdOrgId,
    }
    this.MaterialService.fetchGetFiterTableList(par).subscribe(res => {
      this.dataSource = res.data.noMaterial.list;
      this.total = res.data.noMaterial.total;
      this.pageIndex = res.data.noMaterial.current;
    })
  }

  getNotices($event): any {
    // 点击paginator事件，获取pageIndex，重新加载页面
    this.searchFormGroup.current = $event.pageIndex;
    this.searchFormGroup.size = $event.pageSize;
    this.searchList();
  }

  //改变当前选中
  chooseCurrent(e, index) {
    this.checkData = e;
    this.checkIndex = index;
  }

  //去选中列表
  goRight() {
    for (let i = 0; i < this.dataCheck.length; i++) {
      if (this.dataCheck[i].id == this.checkData.id) {
        return
      }
    }
    if (this.checkData.id == "") {
      return
    }
    this.dataCheck.push(this.checkData);
    this.dataSource = this.dataSource.filter(item => item != this.checkData)
    this.dataCheckCopy = JSON.parse(JSON.stringify(this.dataCheck));
  }


  //移除
  remove(data) {
    this.dataCheck = this.dataCheck.filter(item => item != data)
    this.dataCheckCopy = this.dataCheckCopy.filter(item => item != data)
	console.log(this.dataCheck);
    console.log(this.dataCheckCopy);
    this.dataSource.push(data);
  }

  //搜索已选中列表
  searchCheckList() {
    let arr = new Array();
    if (this.dataCheckCopy.length == 0) {
      this.dataCheckCopy = JSON.parse(JSON.stringify(this.dataCheck));
      this.dataCheck.map(datache => {
        let name = datache.materialName;
        if (name.indexOf(this.materialNameCheck) != -1) {
          arr.push(datache);
        }
      })
      this.dataCheck = arr;
    } else {
      this.dataCheckCopy.map(datache => {
        let name = datache.materialName;
        if (name.indexOf(this.materialNameCheck) != -1) {
          arr.push(datache);
        }
      })
      this.dataCheck = arr;
    }
    console.log(this.dataCheckCopy);

  }

  //重置已选中列表搜索
  searchCheckListReset() {
    this.materialNameCheck = "";
    this.dataCheck = JSON.parse(JSON.stringify(this.dataCheckCopy));
  }

  //保存
  saveList() {

    let tem = [];
    for (let i = 0; i < this.dataCheckCopy.length; i++) {
      let data = {
        materialCode: this.dataCheckCopy[i].materialCode,
        materialId: this.dataCheckCopy[i].kdMaterialId
      };
      tem.push(data)
    }
    let data = {
      craftId: this.craftInfo.craftId,
      body: tem
    }
    this.MaterialService.fetchSaveMaterial(data).subscribe(res => {
      let data = {
        title: "消息提示",
        message: "保存成功",
        ok: '确定',
      }
      this.dialogService.message(data).subscribe(res => {
        this.getTableData();
      })
    })
  }

  //返回
  back() {
    this.router.navigate(['/technological'], {
      skipLocationChange: false, queryParams: {
      }
    }).then()
  }
}
