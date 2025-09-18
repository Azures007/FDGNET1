import { Component, OnInit, Inject } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';
import { AccountService } from '@app/core/http/account.service';

@Component({
  selector: 'tb-choose-base',
  templateUrl: './choose-base.component.html',
  styleUrls: ['../../material/dialog.scss', './choose-base.component.scss']
})
export class ChooseBaseComponent implements OnInit {

  constructor(
    @Inject(MAT_DIALOG_DATA) public injectData: any,
    public dialogRef: MatDialogRef<ChooseBaseComponent>,
    public _dialog: MatDialog,
    public fb: FormBuilder,
    private accountService: AccountService,
  ) { }
  name = '';
  dataSource = [];
  dataSourceTemp = [];
  displayedColumns: string[] = ['name', 'customColumn1'];
  ngOnInit(): void {
    this.getTableData();
  }
  search() {
    this.dataSource = this.dataSourceTemp.filter(item => item.name.indexOf(this.name) != -1);
  }
  getTableData(): void {
    let par = {
      type: this.injectData.type,
      id: this.injectData.id,
    }
    this.accountService.fetchBaseList(par).subscribe(res => {
      this.dataSource = res.data.map(item => {
        if(this.injectData.type == 'base') {
          return {
            name: item.org_name,
            id: item.pk_org,
          }
        } else if(this.injectData.type == 'line') {
          return {
            name: item.vwkname,
            id: item.cwkid,
          }
        } else {
          return {
            name: item.name,
            id: item.pkStordoc,
          }
        }
      });
      this.dataSourceTemp = JSON.parse(JSON.stringify(this.dataSource));
    })
  }
  handleChoose(item: any) {
    this.dialogRef.close(item);
  }

  //关闭弹窗
  addDialogClose() {
    this.dialogRef.close();
  }

  submit() {

  }
}
