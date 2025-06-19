import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';


@Component({
  selector: 'tb-account-setrole-dia',
  templateUrl: './account-setrole-dia.component.html',
  styleUrls: ['./account-setrole-dia.component.scss']
})
export class AccountSetroleDiaComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<AccountSetroleDiaComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) { }

  ngOnInit(): void {
  }

  dialogClose() {
    this.dialogRef.close()
  }

  DialogSaveClose() {
    this.dialogRef.close(this.data.roleId);
  }
}
