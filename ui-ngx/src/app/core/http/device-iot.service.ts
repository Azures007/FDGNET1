import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})

export class DeviceIotService {
  constructor(
    private http: HttpClient
  ) { }
  // 获取烤炉设备数采列表
  public fetchGetOvenDeviceList(data): Observable<any> {
    return this.http.post(`/api/noauth/board/byiots/listIotDeviceAndOven?current=${data.current}&size=${data.size}`, data.body);
  }

  // 获取设备列表
  public fetchGetDeviceList(deviceType): Observable<any> {
    return this.http.get(`/api/noauth/board/oven/listDeviceIot?deviceType=${deviceType}`);
  }

  // 导出烤炉设备数采列表
  public exportOvenDeviceRunTableList(data): Observable<any> {
    let url = `/api/noauth/board/byiots/exportIotDeviceAndOven?current=${data.current}&size=${data.size}`
    return this.http.post(url, data.body, {
      responseType: "blob",//指定响应中包含的数据类型,指定response 是一个包含二进制数据的 Blob 对象
      observe: 'response',//要获取到完全的response,在 发起请求时 在option中添加 observe: ‘response’;
    })
  }

  // 获取温湿度传感器设备数采列表
  public fetchGetTANSensorDeviceList(data): Observable<any> {
    return this.http.post(`/api/noauth/board/byiots/listIotDeviceAndTANS?current=${data.current}&size=${data.size}`, data.body);
  }

  // 导出温湿度传感器设备数采列表
  public exportTANSensorDeviceRunTableList(data): Observable<any> {
    let url = `/api/noauth/board/byiots/exportIotDeviceAndTANS?current=${data.current}&size=${data.size}`
    return this.http.post(url, data.body, {
      responseType: "blob",//指定响应中包含的数据类型,指定response 是一个包含二进制数据的 Blob 对象
      observe: 'response',//要获取到完全的response,在 发起请求时 在option中添加 observe: ‘response’;
    })
  }

  // 获取烤炉折线图数据
  public fetchGetOvenChartData(data): Observable<any> {
    return this.http.post(`/api/noauth/board/byiots/listSellpOven`, data);
  }

  // 获取温湿度传感器折线图数据
  public fetchGetTANSensorChartData(data): Observable<any> {
    return this.http.post(`/api/noauth/board/byiots/listSellpTANSensor`, data);
  }

}


