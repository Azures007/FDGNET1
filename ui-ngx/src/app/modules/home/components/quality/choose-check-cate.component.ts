import { Component, OnInit, Inject } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';
import { QualityService } from '@app/core/http/quality.service';

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
  getTableData(): void {
    let par = {
      current: this.searchFormGroup.value.current,
      size: this.searchFormGroup.value.size,
      body: {
        frequency: this.searchFormGroup.value.frequency,
        importantItem: this.searchFormGroup.value.importantItem,
        enable: 1,
      }
    }
    this.qualityService.fetchGetDailyList(par).subscribe(res => {
      this.dataSource = res.data.list?.map((item, index) => {
        return {
          ...item,
          no: index + 1,
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
  handleChoose(item: any) {
    this.dialogRef.close(item);
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
