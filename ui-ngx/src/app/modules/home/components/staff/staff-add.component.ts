import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';
import { AccountBindComponent } from '../account/account-bind.component';
import { DialogService } from '@app/core/public-api';


@Component({
  selector: 'tb-staff-add',
  templateUrl: './staff-add.component.html',
  styleUrls: ['../../../common/scss/dialog.common.scss', './staff-add.component.scss']
})
export class StaffAddComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<StaffAddComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private dialogService: DialogService,
    public _dialog: MatDialog,
  ) { }

  ngOnInit(): void {

  }

  //关闭弹窗
  DialogClose(): void {
    this.dialogRef.close();
  }

  test(e){}

  submit() {
    console.log(this.data)
    if(this.data.params.name){
      this.dialogRef.close(this.data);
    }else{
      let data = {
        title: "消息提示",
        message: "请输入姓名",
        ok: '确定',
      }
      this.dialogService.message(data).subscribe(res => {

      })
    }
  }

  //字符分隔
  onPhoneup(event) {
    let input = event.target;
     let value = input.value.replace(/[^0-9]/ig, '');
    //  var arr = value.split('');
    //  if (arr.length >= 4) {
    //    arr.splice(3, 0, ' ');
    //  }
    //  if (arr.length >= 9) {
    //    arr.splice(8, 0, ' ');
    //  }
    //  input.value = arr.join('');
     // 输完11位之后
     let phone = event.target.value.replace(/\s+/g, '');
     this.data.params.phone = value;
   }

   onPhonepress(event) {
  	// 判断是否为数字
  	let inputStr = String.fromCharCode(event.keyCode);
  	let re = /^[0-9]+.?[0-9]*$/; //判断字符串是否为数字
  	if(!re.test(inputStr)) {
  		return false;
  	}
  }

  //选择账号弹框
  handleBindAccount() {
    let data = {
      accountList: this.data.accountList
    };
    let diaref = this._dialog.open(AccountBindComponent, {
      width: "695px",
      height: "auto",
      panelClass: 'custom-modalbox',
      data: JSON.parse(JSON.stringify(data))
    })
    diaref.afterClosed().subscribe(res => {
      if (res) {
        this.data.params.userId = res.user_id;
        this.data.params.userEmail = res.username;
      }
    })
  }
  handleDeleteAccount(){
    this.data.params.userId = null;
    this.data.params.userEmail = null;
  }
}
