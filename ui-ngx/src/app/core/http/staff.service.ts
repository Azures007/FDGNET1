import { Injectable } from '@angular/core';
import { defaultHttpOptionsFromConfig, RequestConfig } from './http-utils';
import { Observable, ReplaySubject } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
    providedIn: 'root'
})

export class StaffService {

    constructor(
        private http: HttpClient
    ) { }

    //获取列表
    public fetchGetTableList(data): Observable<any> {
        let url = `/api/tSysPersonnelInfo/personnelInfoList?current=${data.current}&size=${data.size}`
        return this.http.post(url, data.body)
    }

    //新建或保存人员
    public fetchSaveAdd(data): Observable<any> {
        let url = `/api/tSysPersonnelInfo/savePersonnelInfo`
        return this.http.post(url, data)
    }

    //状态控制
    public fetchIsEnabled(params, config?: RequestConfig): Observable<any> {
        let url = "/api/tSysPersonnelInfo/isEnabled"
        return this.http.get(url + `?enabledSt=${params.enabledSt}&personnelId=${params.personnelId}`, defaultHttpOptionsFromConfig(config))
    }

    //删除
    public fetchDelete(params, config?: RequestConfig): Observable<any> {
        let url = "/api/tSysPersonnelInfo/deletePersonnelInfo"
        return this.http.get(url + `?personnelId=${params.personnelId}`, defaultHttpOptionsFromConfig(config))
    }

    //下载模板
    fetchDownloadModel(config?: RequestConfig) {
        let url = "/api/tSysPersonnelInfo/downloadImportTemplate"
        return this.http.get(url, {
            responseType: "blob",//指定响应中包含的数据类型,指定response 是一个包含二进制数据的 Blob 对象
            observe: 'response',//要获取到完全的response,在 发起请求时 在option中添加 observe: ‘response’;
        })
    }

    //导入
    fetchImport(data): Observable<any> {
        let url = "/api/tSysPersonnelInfo/importDevices"
        return this.http.post(url, data)
    }

    //采集
    fetchGetFinger(data):Observable<any> {
        let url = "/api/tSysPersonnelInfo/addDevices"
        return this.http.post(url, data)
    }
}