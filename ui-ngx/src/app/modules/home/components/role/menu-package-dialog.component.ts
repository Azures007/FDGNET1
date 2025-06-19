import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';
import { FlatTreeControl } from '@angular/cdk/tree';
import { MatTreeFlatDataSource, MatTreeFlattener } from '@angular/material/tree';
import { Flag } from '@material-ui/icons';

@Component({
  selector: 'tb-menu-package-dialog',
  templateUrl: './menu-package-dialog.component.html',
  styleUrls: ['./menu-package-dialog.component.scss']
})
export class MenuPackageDialogComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<MenuPackageDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,) { }

  ngOnInit(): void {
  }

  //关闭新增角色弹窗
  DialogClose(): void {
    this.dialogRef.close();
  }




  NodeClickEventEmit(event: any) {
    console.log(event)
    this.parentHandle(event.parentId, event.isFlag);
  }
  //判断子元素是否选择，并且影响父元素
  parentHandle(parentId, isFlag) {
    if (parentId == 0) {
      return
    } else {
      console.log(this.data)
      this.checkTreeData(this.data, parentId, isFlag)

    }
  }

  //递归遍历菜单数据
  checkTreeData(treeData, id, isFlag) {
    if (treeData.tsysMenus && treeData.tsysMenus.length > 0) {
      //选中
      if (isFlag == '0') {
        for (let i = 0; i < treeData.tsysMenus.length; i++) {
          if (treeData.tsysMenus[i].menuId == id) {
            treeData.tsysMenus[i].select = true;
            treeData.tsysMenus[i].isFlag = '0';
            //选中时 还要判断上一级
            this.parentHandle(treeData.tsysMenus[i].parentId, isFlag);
          } else {
            this.checkTreeData(treeData.tsysMenus[i], id, isFlag);
          }
        }

      } else {
        // 未选中
        for (let i = 0; i < treeData.tsysMenus.length; i++) {
          if (treeData.tsysMenus[i].menuId == id) {
            let a = 0;
            for (let j = 0; j < treeData.tsysMenus[i].tsysMenus.length; j++) {
              if (treeData.tsysMenus[i].tsysMenus[j].select == true) {
                a++
              }
            }
            if (a == 0) {
              treeData.tsysMenus[i].select = false;
              treeData.tsysMenus[i].isFlag = '1';
              this.parentHandle(treeData.tsysMenus[i].parentId, isFlag);
            }
            //选中时 还要判断上一级
          } else {
            this.checkTreeData(treeData.tsysMenus[i], id, isFlag);
          }
        }
      }
    }
  }

  ChildrenAll(event, flag) {
    if (flag) {
      return flag;
    }
    if (event.tsysMenus && event.tsysMenus.length > 0) {
      for (let i = 0; i < event.tsysMenus.length; i++) {
        if (event.tsysMenus[i].isFlag == '0') {
          flag = true
          return flag;
        }
        this.ChildrenAll(event.tsysMenus[i], flag);
      }
    }
  };

}
