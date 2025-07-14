import { Component, OnInit, Inject } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';
import { MaterialService } from '@app/core/http/material.service';

@Component({
  selector: 'tb-choose-material',
  templateUrl: './choose-material.component.html',
  styleUrls: ['./../dialog.scss', './choose-material.component.scss']
})
export class ChooseMaterialComponent implements OnInit {

  constructor(
    @Inject(MAT_DIALOG_DATA) public injectData: any,
    public dialogRef: MatDialogRef<ChooseMaterialComponent>,
    public _dialog: MatDialog,
    public fb: FormBuilder,
    private MaterialService: MaterialService,
  ) { }
  dataSource = [];
  displayedColumns: string[] = ['ncMaterialCategory', 'materialName', 'materialCode', 'customColumn1'];
  total = 0;
  searchFormGroup = this.fb.group({
    current: 0,
    size: 10,
    ncMaterialCategory: '',
    materialCode: '',
    materialName: '',
    materialStatus: '1',
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
        ncMaterialCategory: this.searchFormGroup.value.ncMaterialCategory,
        materialCode: this.searchFormGroup.value.materialCode,
        materialName: this.searchFormGroup.value.materialName,
        materialStatus: this.searchFormGroup.value.materialStatus,
      }
    }
    this.MaterialService.fetchGetTableList(par).subscribe(res => {
      this.dataSource = res.data.list;
      this.total = res.data.total;
    })
  }
  handleChoose(item: any) {
    this.dialogRef.close(item);
  }

  //关闭弹窗
  addDialogClose() {
    this.dialogRef.close();
  }
  reset() {
    this.searchFormGroup.controls['ncMaterialCategory'].setValue('');
    this.searchFormGroup.controls['materialCode'].setValue('');
    this.searchFormGroup.controls['materialName'].setValue('');
    this.searchFormGroup.controls['materialStatus'].setValue('1');
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
