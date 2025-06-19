import { Injectable } from '@angular/core';
import { defaultHttpOptionsFromConfig, RequestConfig } from './http-utils';
import { Observable, ReplaySubject } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
    providedIn: 'root'
})
export class MyDeviceService {

    constructor(
        private http: HttpClient
    ) { }
    //获取列表
    public fetchGetTableList(data): Observable<any> {
        let url = `/api/device/getDevice?current=${data.current}&size=${data.size}`
        return this.http.post(url, data.body)
    }

    //新增设备
    public fetchSaveAdd(data): Observable<any> {
        let url = "/api/device/saveDevice"
        return this.http.post(url, data)
    }

    //删除设备
    public fetchDelete(params, config?: RequestConfig): Observable<any> {
        let url = "/api/device/deleteDevice"
        return this.http.get(url + `?deviceId=${params.deviceId}`, defaultHttpOptionsFromConfig(config))
    }

    //设备状态控制
    public fetchIsEnabled(params, config?: RequestConfig): Observable<any> {
        let url = "/api/device/enableDevice"
        return this.http.get(url + `?enable=${params.enable}&deviceId=${params.deviceId}`, defaultHttpOptionsFromConfig(config))
    }

    //导出（特别传参，指定响应数据类型）
    fetchExportTable(data) {
        let url = `/api/device/exportDevices?current=${data.current}&size=${data.size}`
        return this.http.post(url, data.body, {
            responseType: "blob",//指定响应中包含的数据类型,指定response 是一个包含二进制数据的 Blob 对象
            observe: 'response',//要获取到完全的response,在 发起请求时 在option中添加 observe: ‘response’;
        })
    }
    
    //下载模板
    fetchDownloadModel(config?: RequestConfig) {
        let url = "/api/device/downTemplate"
        return this.http.get(url,{
            responseType: "blob",//指定响应中包含的数据类型,指定response 是一个包含二进制数据的 Blob 对象
            observe: 'response',//要获取到完全的response,在 发起请求时 在option中添加 observe: ‘response’;
        })
    }

    //导入
    fetchImport(data): Observable<any> {
        let url = "/api/device/importDevices"
        return this.http.post(url, data)
    }
}