import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { TechnologicalService } from '@app/core/http/technological.service';
import { DialogService } from '@app/core/public-api';
import { RecipeService } from '@app/core/http/recipe.service';

@Component({
  selector: 'tb-recipe-bind-mater',
  templateUrl: './recipe-bind-mater.component.html',
  styleUrls: ['./recipe-bind-mater.component.scss']
})
export class RecipeBindMaterComponent implements OnInit {

  constructor(
    private TechnologicalService: TechnologicalService,
    private recipeService: RecipeService,
    private dialogService: DialogService,
    public route: ActivatedRoute,
    public router: Router
  ) { }

  //搜索参数
  searchFormGroup = {
    current: 0,
    size: 50,
    productName: "",
  };

  //路线信息
  recipeId = ''

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
      this.recipeId = queryParams.recipeId;
      this.searchList();
      this.searchFiterList();
    })
  }


  //获取列表数据
  searchList(): void {
    let par = {
      current: this.searchFormGroup.current,
      size: this.searchFormGroup.size,
      recipeId: this.recipeId,
      productName: this.searchFormGroup.productName,
    }
    this.recipeService.fetchGetFiterTableList(par).subscribe(res => {
      this.dataSource = res.data.list;
      this.total = res.data.total;
    })
  }
  searchFiterList() {
    this.recipeService.fetchGetBindingTableList(this.recipeId).subscribe(res => {
      this.dataCheck = res.data.map(item => {
        return {
          id: item.bindingId,
          materialName: item.productName,
          materialCode: item.productCode,
          recipeId: item.recipeId,
        }
      });
      this.dataCheckCopy = JSON.parse(JSON.stringify(this.dataCheck));
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
    this.dataCheck = this.dataCheck.filter(item => item.materialCode != data)
    this.dataCheckCopy = this.dataCheckCopy.filter(item => item.materialCode != data)
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
        if (name.indexOf(this.materialNameCheck) != -1 || datache.materialCode.indexOf(this.materialNameCheck) != -1) {
          arr.push(datache);
        }
      })
      this.dataCheck = arr;
    } else {
      this.dataCheckCopy.map(datache => {
        let name = datache.materialName;
        if (name.indexOf(this.materialNameCheck) != -1 || datache.materialCode.indexOf(this.materialNameCheck) != -1) {
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
        bindingId: this.dataCheckCopy[i].id,
        productCode: this.dataCheckCopy[i].materialCode,
        productName: this.dataCheckCopy[i].materialName,
        recipeId: this.recipeId,
      };
      tem.push(data)
    }
    let data = {
      recipeId: this.recipeId,
      body: tem
    }
    this.recipeService.fetchSaveMaterial(data).subscribe(res => {
      let data = {
        title: "消息提示",
        message: "保存成功",
        ok: '确定',
      }
      this.dialogService.message(data).subscribe(res => {
        this.searchList();
      })
    })
  }

  //返回
  back() {
    this.router.navigate(['/recipe'], {
      skipLocationChange: false, queryParams: {
      }
    }).then()
  }
}
