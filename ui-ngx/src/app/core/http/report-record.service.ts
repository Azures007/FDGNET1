import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})

export class ReportRecordService {
  constructor(
    private http: HttpClient
  ) { }
  // 获取列表
  public fetchGetTableList(data): Observable<any> {
    return this.http.post(`/api/reportRecord/list?current=${data.current}&size=${data.size}`, data.body);
  }

  // 导出盘点列表
  public exportReportRecordList(data): Observable<any> {
    let url = `/api/reportRecord/export?current=${data.current}&size=${data.size}`
    return this.http.post(url, data.body, {
      responseType: "blob",//指定响应中包含的数据类型,指定response 是一个包含二进制数据的 Blob 对象
      observe: 'response',//要获取到完全的response,在 发起请求时 在option中添加 observe: ‘response’;
    })
  }

}
