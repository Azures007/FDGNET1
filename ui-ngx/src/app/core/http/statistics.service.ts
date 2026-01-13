import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})

export class StatisticsService {
  constructor(
    private http: HttpClient
  ) { }
  // 获取原料投入报表列表
  public fetchGetMaterialTableList(data): Observable<any> {
    return this.http.post(`/api/rawmaterialinput/query?current=${data.current}&size=${data.size}`, data.body);
  }

  // 导出原料投入报表列表
  public exportMaterialTableList(data): Observable<any> {
    let url = `/api/rawmaterialinput/export?current=${data.current}&size=${data.size}`
    return this.http.post(url, data.body, {
      responseType: "blob",//指定响应中包含的数据类型,指定response 是一个包含二进制数据的 Blob 对象
      observe: 'response',//要获取到完全的response,在 发起请求时 在option中添加 observe: ‘response’;
    })
  }

  // 获取设备运行报表列表
  public fetchGetDeviceRunTableList(data): Observable<any> {
    return this.http.post(`/api/board/deviceRunBoard?current=${data.current}&size=${data.size}`, data.body);
  }

  // 导出设备运行报表列表
  public exportDeviceRunTableList(data): Observable<any> {
    let url = `/api/board/exportDeviceRunBoard?current=${data.current}&size=${data.size}`
    return this.http.post(url, data.body, {
      responseType: "blob",//指定响应中包含的数据类型,指定response 是一个包含二进制数据的 Blob 对象
      observe: 'response',//要获取到完全的response,在 发起请求时 在option中添加 observe: ‘response’;
    })
  }

  // 获取投入产出比报表列表
  public fetchGetInputOutputRatioTableList(data): Observable<any> {
    return this.http.post(`/api/inputoutputratio/query?current=${data.current}&size=${data.size}`, data.body);
  }

  // 导出投入产出比报表列表
  public exportInputOutputRatioTableList(data): Observable<any> {
    let url = `/api/inputoutputratio/export?current=${data.current}&size=${data.size}`
    return this.http.post(url, data.body, {
      responseType: "blob",//指定响应中包含的数据类型,指定response 是一个包含二进制数据的 Blob 对象
      observe: 'response',//要获取到完全的response,在 发起请求时 在option中添加 observe: ‘response’;
    })
  }

}
