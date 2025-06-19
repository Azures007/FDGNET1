import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, } from '@angular/material/dialog';
import { StaffService } from '@app/core/http/staff.service';

@Component({
  selector: 'tb-staff-upload',
  templateUrl: './staff-upload.component.html',
  styleUrls: ['../../../common/scss/dialog.common.scss','./staff-upload.component.scss']
})
export class StaffUploadComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<StaffUploadComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private StaffService: StaffService,
  ) { }

  uploadData: any;

  ngOnInit(): void {
  }

  //关闭弹窗
  DialogClose(): void {
    this.dialogRef.close();
  }

  //下载模板
  downloadModel() {
    this.StaffService.fetchDownloadModel().subscribe(res => {
      var name = res.headers.get('content-disposition')//获取文件名，（后台返回的文件名在响应头当中）
      name = decodeURIComponent(name)//由于中文通常都是乱码，所以需要解码
      name = name.substring(name.indexOf("=") + 1)//数据处理获得名字
      this.downloadFile(res.body, name)//数据流都存在body中
    })
  }

  //文件数据流有多种类型，需自己明确好
  downloadFile(data, name) {
    const contentType = "application/x-zip-compressed";
    const blob = new Blob([data], { type: contentType });
    const url = window.URL.createObjectURL(blob);
    // 打开新窗口方式进行下载
    // window.open(url); 

    // 以动态创建a标签进行下载
    const a = document.createElement("a");
    a.href = url;
    a.download = name;
    a.click();
    window.URL.revokeObjectURL(url);
  }


  handleFile() {
    let files = $('#csv_file').prop('files');
    let data = new FormData();
    data.append('file', files[0]);
    console.log(data);
    this.uploadData = data;
  }

}
