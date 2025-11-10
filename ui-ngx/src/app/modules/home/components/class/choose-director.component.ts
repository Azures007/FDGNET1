import { Component, OnInit, Inject } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';
import { StaffService } from '@app/core/http/staff.service';

@Component({
  selector: 'tb-choose-director',
  templateUrl: './choose-director.component.html',
  styleUrls: ['./../material/dialog.scss', './choose-director.component.scss']
})
export class ChooseDirectorComponent implements OnInit {

  constructor(
    @Inject(MAT_DIALOG_DATA) public injectData: any,
    public dialogRef: MatDialogRef<ChooseDirectorComponent>,
    public _dialog: MatDialog,
    public fb: FormBuilder,
    private staffService: StaffService,
  ) { }
  dataSource = [];
  displayedColumns: string[] = ['name', 'sex', 'phone', 'customColumn1'];
  total = 0;
  searchFormGroup = this.fb.group({
    current: 0,
    size: 10,
    name: null,
    sex: '',
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
        name: this.searchFormGroup.value.name,
        sex: this.searchFormGroup.value.sex || null,
        className: null,
        enabledSt:1,
      }
    }
    this.staffService.fetchGetTableList(par).subscribe(res => {
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
    this.searchFormGroup.controls['name'].setValue(null);
    this.searchFormGroup.controls['sex'].setValue('');
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
