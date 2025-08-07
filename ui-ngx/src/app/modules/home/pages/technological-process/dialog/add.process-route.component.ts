import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { FormBuilder, Validators } from '@angular/forms';
import { Utils } from '../../order-management/w-utils';

import { TechnologicalService } from '@app/core/http/technological.service';
import { TransferComponent } from '@home/pages/technological-process/dialog/transfer.component';
import { RoleService } from '@app/core/http/role.service';

@Component({
  selector: 'tb-add.process-route',
  templateUrl: './add.process-route.html',
  styleUrls: ['./dialog.scss', './add.process-route.scss']
})
export class AddProcessRouteComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<AddProcessRouteComponent>,
    @Inject(MAT_DIALOG_DATA) public injectData: any,
    private fb: FormBuilder,
    private utils: Utils,
    private api: TechnologicalService,
    public dialog: MatDialog,
    private roleService: RoleService,
  ) { }


  // 保存参数
  addParams = {
    craftDetail: '',  // 说明
    craftId: '',
    craftName: '',
    craftNumber: '',
    createdTime: '',
    createdUser: '',
    effectiveTime: '',
    failureTime: '',
    enabled: '',
    processInfos: [],
    updatedTime: '',
    updatedUser: '',
    pkOrg: '',
  };

  settingSource = [{
    className: '',
    processId: '',
    processName: '',
    sort: '',
    joins: [],
    isReceivingBindCode: false,
    isReceivingUnBindCode: false,
    isReportingBindCode: false,
  }]

  //生产车间列表
  midDeptList = JSON.parse(localStorage.getItem('depts'));

  //生产组织列表
  midOrgList = JSON.parse(localStorage.getItem('orgs'));

  processOptions = [];
  classOptions = [];

  completeColumns = [];

  dataForm;

  processId;

  transferCurrent;
  dataRight = [];
  currentJoinsIndex = 0;
  allList = [];

  formType: string;

  // 状态下拉
  status = [{
    label: '启用',
    value: 1,
  }, {
    label: '禁用',
    value: 0,
  }];

  get modalTitle() {
    return this.formType ? (this.formType === 'details' ? '查看工艺路线' : '编辑工艺路线') : '新增工艺路线';
  }

  ngOnInit(): void {
    const params = {
      current: 0,
      size: 999,
      body: {
        craftName: "",
        craftNumber: "",
        enabled: "",
      }
    }

    if (!this.injectData.data?.craftId) {
      this.dataForm = this.fb.group({
        craftName: ['', [Validators.required]],
        // craftNumber: ['', [Validators.required]],
        enabled: ['', [Validators.required]],
        craftDetail: '',
        effectiveTime: ['', [Validators.required]],
        failureTime: ['', [Validators.required]],
        pkOrg: ['', [Validators.required]],
        prevCraftId: ['',],
      });

    } else {
      this.formType = this.injectData.type;
      const obj = {
        craftName: '',
        // craftNumber: 0,
        enabled: '',
        craftDetail: '',
        effectiveTime: '',
        failureTime: '',
        pkOrg: '',
        prevCraftId: "",
      };

      Object.keys(this.injectData.data).forEach(key => {
        obj[key] = this.injectData.data[key];
      });

      if (this.formType === 'details') {

        this.dataForm = this.fb.group({
          craftName: [{ value: obj.craftName, disabled: true }, [Validators.required]],
          // craftNumber: [{ value: obj.craftNumber, disabled: true }, [Validators.required]],
          enabled: [{ value: obj.enabled, disabled: true }, [Validators.required]],
          craftDetail: [{ value: obj.craftDetail, disabled: true }],
          effectiveTime: [{ value: new Date(obj.effectiveTime), disabled: true }, [Validators.required]],
          failureTime: [{ value: new Date(obj.failureTime), disabled: true }, [Validators.required]],
          pkOrg: [{ value: obj.pkOrg, disabled: true }, [Validators.required]],
          prevCraftId: [{ value: obj.prevCraftId, disabled: true }],
        });
      } else {

        this.addParams.craftId = this.injectData.data.craftId;

        this.dataForm = this.fb.group({
          craftName: [{ value: obj.craftName, disabled: false }, [Validators.required]],
          // craftNumber: [{ value: obj.craftNumber, disabled: false }, [Validators.required]],
          enabled: [{ value: obj.enabled, disabled: false }, [Validators.required]],
          craftDetail: [{ value: obj.craftDetail, disabled: false }],
          effectiveTime: [{ value: new Date(obj.effectiveTime), disabled: false }, [Validators.required]],
          failureTime: [{ value: new Date(obj.failureTime), disabled: false }, [Validators.required]],
          pkOrg: [{ value: obj.pkOrg, disabled: false }, [Validators.required]],
          prevCraftId: [{ value: obj.prevCraftId, disabled: false }],
        });
      }

    }

    this.getProcessList();
    this.getAllClass();
    this.api.fetchGetCraftTableList(params).subscribe(res => {
      this.allList = res.data.list || [];
      this.allList.push({
        craftId:0,
        craftName:"无"
      })
    });


  }

  // 关闭新增角色弹窗
  addDialogClose(): void {
    this.dialogRef.close();
  }

  // 打开选择班别框
  showTransfer(data, index): void {

    this.processId = data.processId;
    this.transferCurrent = data;

    this.dataRight = JSON.parse(JSON.stringify(data.joins));
    this.currentJoinsIndex = index;

    this.completeColumns = []

    this.api.fetchGetFiltterClassList({
      current: 0,
      size: 9999,
      body: {
        enabledSt: 1,
        classNumber: "",
        name: ""
      }
    }).subscribe(res => {

      if (res.errcode === 200) {
        const allData = res.data.list;
        allData.forEach((item, index) => {
          this.completeColumns.push({
            label: item.name,
            classId: item.classId,
            leader: item.groupLeader,
            teamNum: item.teamNum,
            direction: 'left',
            checked: false
          });

        });
        this.transferCurrent.joins.forEach(item => {
          this.completeColumns.push({
            label: item.name,
            classId: item.classId,
            leader: item.groupLeader,
            teamNum: item.teamNum,
            direction: 'right',
            checked: false
          });
        });
        const dialogRef = this.dialog.open(TransferComponent, {
          width: '700px',
          height: 'auto',
          panelClass: 'custom-modalbox',
          data: this.completeColumns,
        })
        dialogRef.afterClosed().subscribe(result => {
          // 判断是否有改动
          let same = false;

          if (this.completeColumns.length !== result.length) {
            same = false;
          } else {
            this.completeColumns.forEach(item => {
              item.exist = false;
              result.forEach(rData => {
                if (item.classId === rData.classId) {
                  item.exist = true;
                }
              })
            })
            same = !this.completeColumns.some(item => !item.exist);
          }

          if (!same) {
            this.utils.confirm('温馨提示', `修改工序的班别可能会影响其他工艺，是否确认修改?`, () => {
              this.transferCurrent.joins = result.list;

              const processIds = [];

              this.transferCurrent.joins.forEach(item => {
                processIds.push(item.classId);
              });

              this.saveClass(processIds);
            })
          }
        });

      } else {
        this.utils.showMessage(res.errmsg, 'error');

      }

    });







  }

  // 保存工序中的班别
  saveClass(data) {
    this.api.fetchSaveClass({
      processId: this.processId,
      body: data
    }).subscribe(res => {
      if (res.errcode === 200) {
        this.utils.showMessage('班别已保存', 'error');
        // this.dialogRef.close('refresh');
      } else {
        this.utils.showMessage(res.errmsg, 'error');
        this.transferCurrent.joins = this.dataRight;
        this.settingSource[this.currentJoinsIndex].joins = this.dataRight;
        this.dataRight = [];
        this.currentJoinsIndex = 0;
      }
    });
  }

  handleEvent(data, type) {

    switch (type) {
      case 'add':
        this.settingSource.push({
          className: '',
          processId: '',
          processName: '',
          sort: '',
          joins: [],
          isReceivingBindCode: false,
          isReceivingUnBindCode: false,
          isReportingBindCode: false,
        });

        break;
      case 'del':
        this.utils.confirm(`是否确认删除该行数据?`, '请注意，确认后删除数据将不可恢复', () => {
          this.settingSource.splice(data, 1);
        })
        break;
    }
  }


  getProcessList() {

    if (this.processOptions.length) return

    this.api.fetchGetTableList({
      current: 0,
      size: 9999,
      body: {
        processName: "",
        processNumber: "",
        enabled: 1
      }
    }).subscribe(res => {

      this.processOptions = [];

      const allData = res.data.content;
      allData.forEach((item, index) => {
        this.processOptions.push({
          label: item.processName,
          value: item.processId
        });
      });

    });
  }

  // 获取所有的组别，用于数据填充
  getAllClass() {
    this.api.fetchGetClassList({
      current: 0,
      size: 9999
    }).subscribe(res => {


      if (res.errcode === 200) {
        const allData = res.data.list;
        this.classOptions = allData;

        // setting table 处理

        if (this.injectData.data?.processInfos && this.injectData.data.processInfos.length) {
          this.settingSource = [];
          this.injectData.data.processInfos.forEach(item => {
            const classObj = {
              className: item.className,
              processId: item.processId,
              processName: item.processName,
              sort: item.sort,
              joins: [],
              isReceivingBindCode: item.isReceivingBindCode,
              isReceivingUnBindCode: item.isReceivingUnBindCode,
              isReportingBindCode: item.isReportingBindCode,
            }

            if (item.className && item.className.length) {

              let classNames = null;
              if (item.className.indexOf(',') > -1) {
                classNames = item.className.split(',');
              } else {
                classNames = [item.className];
              }


              this.classOptions.forEach(data => {
                if (classNames.includes(data.name)) {
                  classObj.joins.push(data);
                }
              })
            }

            this.settingSource.push(classObj);

          })

        }


      } else {
        this.utils.showMessage(res.errmsg, 'error');
      }

    });
  }

  // 删除标签
  delTag(parentIndex, index) {
    this.settingSource[parentIndex].joins.splice(index, 1);
  }

  // 获取工序下的组别
  getClassForProcess(data, item) {

    this.processId = data;

    this.api.fetchGetClassForProcess({
      processId: data,
    }).subscribe(res => {

      if (res.errcode === 200) {
        const allData = res.data || [];
        item.joins = allData;

      } else {
        this.utils.showMessage(res.errmsg, 'error');
      }

    });
  }

  submit() {

    const userInfo = JSON.parse(localStorage.getItem('userInfo'));

    if (this.dataForm.valid) {
      // 判断this.settingSource是否存在sort相同的
      const sortList = this.settingSource.map(item => item.sort);
      const sortSet = new Set(sortList);
      if (sortList.length !== sortSet.size) {
        this.utils.showMessage('工序设置执行序号不能重复', 'warn');
        return;
      }

      let exist = true;

      this.addParams.processInfos = [];

      this.settingSource.forEach(item => {
        const obj = {
          className: '',
          processId: Number(item.processId),
          processName: '',
          sort: item.sort,
          isReceivingBindCode: item.isReceivingBindCode,
          isReceivingUnBindCode: item.isReceivingUnBindCode,
          isReportingBindCode: item.isReportingBindCode,
        }
        Object.keys(item.joins).forEach(key => {
          if (obj.className.length) {
            obj.className += ',';
          };
          obj.className += item.joins[key].name || item.joins[key].label;
        });

        const process = this.processOptions.filter(pItem => pItem.value === Number(obj.processId))[0];

        obj.processName = process ? process.label : '';

        for (const key in obj) {
          if (key === 'processId' || key === 'prosortcessId') {
            if (!obj[key]) {
              exist = false;
            }
          }

        }

        if (exist) {
          this.addParams.processInfos.push(obj);
        }

      });

      if (this.addParams.processInfos.length === 0) {
        this.utils.showMessage('工艺至少需要一条完整的工序', 'warn');
        return;
      }
      if (new Date(this.dataForm.value.effectiveTime).getTime() > new Date(this.dataForm.value.failureTime).getTime()) {
        this.utils.showMessage('失效时间不能早于生效时间', 'warn');
        return;
      }


      Object.keys(this.dataForm.value).forEach(key => {

        if (key === 'effectiveTime' || key === 'failureTime') {
          this.addParams[key] = this.utils.dateFormat(new Date(this.dataForm.value[key]), 'yyyy-MM-ddThh:mm:ss.000Z');

        } else {
          this.addParams[key] = this.dataForm.value[key];
        }

      });

      this.addParams.createdTime = this.utils.dateFormat(new Date(), 'yyyy-MM-ddThh:mm:ss.000Z');
      this.addParams.createdUser = userInfo.firstName;





      this.api.fetchSaveCraft(this.addParams).subscribe(res => {
        if (res.errcode === 200) {
          this.dialogRef.close('refresh');
        } else {
          this.utils.showMessage(res.errmsg, 'error');
        }

      });

    } else {

      for (const i in this.dataForm.controls) {
        this.dataForm.controls[i].markAsTouched();
        this.dataForm.controls[i].updateValueAndValidity();
      }
    }
  }

  //复选框change事件
  handleCodeRule(n, index) {
    if (n == 0) {
      this.settingSource[index].isReceivingUnBindCode = !this.settingSource[index].isReceivingBindCode;
    } else {
      // console.log(this.settingSource[index].isReceivingBindCode, '接单扫码')
      // console.log(this.settingSource[index].isReceivingUnBindCode, '扫码解绑')
      this.settingSource[index].isReceivingBindCode = !this.settingSource[index].isReceivingUnBindCode;
    }
  }
}
