import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, } from '@angular/material/dialog';
import { BasketService } from '@app/core/http/basket.service';

@Component({
  selector: 'tb-show-code',
  templateUrl: './show-code.component.html',
  styleUrls: ['./show-code.component.scss']
})
export class ShowCodeComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<ShowCodeComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private BasketService: BasketService,) { }

  codeSrc = '';

  ngOnInit(): void {
    let par={
      code:this.data.data.code
    }
    this.BasketService.fetchGetCode(par).subscribe(res=>{
      console.log(res);
      this.codeSrc = 'data:image/jpeg;base64,'+res.data;
    })
  }

  //关闭弹窗
  DialogClose(): void {
    this.dialogRef.close();
  }

}
