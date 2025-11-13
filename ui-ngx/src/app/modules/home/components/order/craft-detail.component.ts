import { Component, OnInit, Inject } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';
import { OrderService } from '@app/core/http/order.service';
import { Utils } from '../../pages/order-management/w-utils';

@Component({
  selector: 'tb-craft-detail',
  templateUrl: './craft-detail.component.html',
  styleUrls: ['../material/dialog.scss', './craft-detail.component.scss']
})
export class CraftDetailComponent implements OnInit {
  dataForm;
  constructor(
    @Inject(MAT_DIALOG_DATA) public injectData: any,
    public dialogRef: MatDialogRef<CraftDetailComponent>,
    public _dialog: MatDialog,
    private fb: FormBuilder,
    private apiOrder: OrderService,
    private utils: Utils,
  ) { }
  displayedColumns: string[] = ['sort', 'processName', 'className', 'groupLeader'];
  ngOnInit(): void {
    this.dataForm = this.fb.group({
      craftId: ['', [Validators.required]],
    })
  }

  //关闭弹窗
  addDialogClose() {
    this.dialogRef.close();
  }
  submit() {
    if (this.dataForm.valid) {
      this.apiOrder.startOrderChange({
        orderId: this.injectData.row.orderId,
        craftId: this.dataForm.value.craftId,
        craftDesc: '',
      }).subscribe((res) => {
        if (res.errcode === 200) {
          this.dialogRef.close(true);
        } else {
          this.utils.showMessage(res.errmsg, 'error')
        }
      })
    } else {
      for (const i in this.dataForm.controls) {
        this.dataForm.controls[i].markAsTouched();
        this.dataForm.controls[i].updateValueAndValidity();
      }
    }
  }
}
