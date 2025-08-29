import { Component, OnInit, Inject } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';
import { QualityService } from '@app/core/http/quality.service';
import { Utils } from '../../pages/order-management/w-utils';

@Component({
  selector: 'tb-choose-check-cate',
  templateUrl: './choose-check-cate.component.html',
  styleUrls: ['./dialog.scss', './choose-check-cate.component.scss']
})
export class ChooseCheckCateComponent implements OnInit {

  constructor(
    @Inject(MAT_DIALOG_DATA) public injectData: any,
    public dialogRef: MatDialogRef<ChooseCheckCateComponent>,
    public _dialog: MatDialog,
    public fb: FormBuilder,
    private qualityService: QualityService,
    private utils: Utils,
  ) { }
  dataSource = [];
  displayedColumns: string[] = ['no', 'frequency', 'importantItem', 'remark', 'customColumn1'];
  total = 0;
  searchFormGroup = this.fb.group({
    current: 0,
    size: 10,
    frequency: '',
    importantItem: '',
  });
  pageSizeOptions: number[] = [10, 50, 100, 200, 500];
  ngOnInit(): void {
    this.getTableData();
  }
  get isAllChecked() {
    if (this.dataSource.filter(item => !item.disabled).length === 0) {
      return false;
    }
    return this.dataSource.filter(item => !item.disabled).every(item => item.isChecked);
  }
  set isAllChecked(value) {
    this.dataSource.forEach(item => {
      !item.disabled && (item.isChecked = value);
    })
  }
  get allCheckedDisabled() {
    return this.dataSource.filter(item => !item.disabled).length === 0;
  }
  getTableData(): void {
    let par = {
      current: this.searchFormGroup.value.current,
      size: this.searchFormGroup.value.size,
      body: {
        frequency: this.searchFormGroup.value.frequency,
        importantItem: this.searchFormGroup.value.importantItem,
        enabled: 1,
      }
    }
    this.qualityService.fetchGetDailyList(par).subscribe(res => {
      this.dataSource = res.data.list?.map((item, index) => {
        const disabled = this.injectData.configs.findIndex(i => i.id === item.id) !== -1;
        return {
          ...item,
          no: index + 1,
          isChecked: false,
          disabled,
        }
      });
      this.total = res.data.total;
    })
  }
  getFrequency(value) {
    let label = '';
    this.injectData.frequencyList?.forEach(item => {
      if (item.codeValue === value) {
        label = item.codeDsc;
      }
    })
    return label;
  }
  handleChoose() {
    const list = this.dataSource.filter(item => item.isChecked);
    if (!list.length) {
      this.utils.showMessage('请选择配置项', 'error');
      return;
    }
    this.dialogRef.close(list);
  }

  //关闭弹窗
  addDialogClose() {
    this.dialogRef.close();
  }
  reset() {
    this.searchFormGroup.controls['frequency'].setValue('');
    this.searchFormGroup.controls['importantItem'].setValue('');
    this.getTableData();
  }
  submit() {

  }
  getNotices($event): any {
    // 点击paginator事件，获取pageIndex，重新加载页面
    this.searchFormGroup.value.current = $event.pageIndex;
    this.searchFormGroup.value.size = $event.pageSize;
    this.getTableData();
  }
}
