import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

export interface ToastDialogData {
  message: string;
  ok: string;
}


@Component({
  selector: 'tb-toast-dialog',
  templateUrl: './toast-dialog.component.html',
  styleUrls: ['./toast-dialog.component.scss']
})
export class ToastDialogComponent {

  constructor(public dialogRef: MatDialogRef<ToastDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ToastDialogData){}

}
