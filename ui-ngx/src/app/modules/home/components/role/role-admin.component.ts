import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'tb-role-admin',
  templateUrl: './role-admin.component.html',
  styleUrls: ['./role-admin.component.scss']
})
export class RoleAdminComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<RoleAdminComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,) { }

  ngOnInit(): void {

    this.data.byFactory == 0 ? this.data.byFactory = true : this.data.byFactory = false;
    this.data.byGroup == 0 ? this.data.byGroup = true : this.data.byGroup = false;

  }


  DialogClose(): void {
    this.dialogRef.close();
  }
  DialogSaveClose() {
    this.dialogRef.close(this.data);
  }
}
