import { Component, OnInit, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';
import { AuthService } from '@app/core/auth/auth.service';
import { Utils } from '../../pages/order-management/w-utils';


@Component({
  selector: 'tb-change-org',
  templateUrl: './change-org.component.html',
  styleUrls: ['../material/dialog.scss', './change-org.component.scss']
})
export class ChangeOrgComponent implements OnInit {
  dataForm: FormGroup = this.fb.group({
    pkOrg: ['', [Validators.required]],
    cwkid: ['', [Validators.required]]
  });
  pkOrgList = [];
  cwkList = [];
  constructor(
    @Inject(MAT_DIALOG_DATA) public injectData: any,
    public dialogRef: MatDialogRef<ChangeOrgComponent>,
    public _dialog: MatDialog,
    public fb: FormBuilder,
    private authService: AuthService,
    private utils: Utils,
  ) { }
  ngOnInit(): void {
    this.authService.getCurrentLine().subscribe(
        (res: any) => {
          if (res.data) {
            this.dataForm = this.fb.group({
              pkOrg: [res.data.pkOrg, [Validators.required]],
              cwkid: [res.data.cwkid, [Validators.required]]
            })
            this.authService.getOrgList(this.injectData.email).subscribe(
              (res: any) => {
                if (res.data) {
                  this.pkOrgList = res.data;
                }
              }
            )
            this.authService.getLineList(this.injectData.email, this.dataForm.value.pkOrg).subscribe(
              (res: any) => {
                if (res.data) {
                  this.cwkList = res.data;
                }
              }
            )
          }
        }
      )
  }
  getCwkList(e) {
    this.authService.getLineList(this.injectData.email, this.dataForm.value.pkOrg).subscribe(
      (res: any) => {
        if (res.data) {
          this.dataForm.value.cwkid = '';
          this.cwkList = res.data;
        }
      }
    )
  }

  //关闭弹窗
  addDialogClose(isSubmit: boolean) {
    if(this.dataForm.valid && isSubmit) {
      this.authService.changeOrg(this.dataForm.value).subscribe(
        (res: any) => {
          if(res.errcode == 200) {
            this.utils.showMessage('切换账套成功', 'success');
            this.dialogRef.close();
            window.location.reload();
          }

        }
      )
    } else {
      this.dialogRef.close();
    }
  }

}
