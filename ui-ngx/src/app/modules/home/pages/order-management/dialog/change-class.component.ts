import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormBuilder, Validators } from '@angular/forms';
import { Utils } from '../../order-management/w-utils';

import { TechnologicalService } from '@app/core/http/technological.service';
import { OrderService } from '@app/core/http/order.service';

@Component({
  selector: 'tb-change-class',
  templateUrl: './change-class.component.html',
  styleUrls: ['./dialog.scss', '../../../../common/scss/dialog.common.scss', './change-class.component.scss']
})
export class ChangeClassComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<ChangeClassComponent>,
    @Inject(MAT_DIALOG_DATA) public injectData: any,
    private fb: FormBuilder,
    private utils: Utils,
    private apiTechnolog: TechnologicalService,
    private apiOrder: OrderService,) { }

  ngOnInit(): void {
    console.log('data', this.injectData)
  }

  submit() {
    let data = {
      classId: this.injectData.classId,
      orderId: this.injectData.orderId,
    }
    this.apiOrder.fetchChangeOrderClass(data).subscribe(res => {
      console.log(res, 'res');
      if(res.errcode == 200){
        this.dialogRef.close('refresh')
      }else{
        this.dialogRef.close(res.errmsg)
      }
    })
  }

  addDialogClose() {
    this.dialogRef.close();
  }

}
