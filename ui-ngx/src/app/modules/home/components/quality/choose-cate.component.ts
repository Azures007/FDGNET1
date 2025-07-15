import { Component, OnInit, Inject } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';
import { QualityService } from '@app/core/http/quality.service';

@Component({
  selector: 'tb-choose-cate',
  templateUrl: './choose-cate.component.html',
  styleUrls: ['./dialog.scss', './choose-cate.component.scss']
})
export class ChooseCateComponent implements OnInit {

  constructor(
    @Inject(MAT_DIALOG_DATA) public injectData: any,
    public dialogRef: MatDialogRef<ChooseCateComponent>,
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
    inspectionItem: '',
    keyProcess: '',
    productName: ''
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
        inspectionItem: this.searchFormGroup.value.inspectionItem,
        keyProcess: this.searchFormGroup.value.keyProcess,
        productName: this.searchFormGroup.value.productName,
        isEnabled: '1',
      }
    }
    this.qualityService.fetchGetQualityCategoryList(par).subscribe(res => {
      const temp = res.data.list.map(item => {
        return {
          ...item,
          configData: JSON.parse(item.configData),
        }
      })
      console.log(temp);
      // this.dataSource = res.data.list?.map((item, index) => {
      //   return {
      //     ...item,
      //     no: index + 1,
      //   }
      // });
      // this.total = res.data.total;
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
    this.searchFormGroup.controls['inspectionItem'].setValue('');
    this.searchFormGroup.controls['keyProcess'].setValue('');
    this.searchFormGroup.controls['productName'].setValue('');
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
