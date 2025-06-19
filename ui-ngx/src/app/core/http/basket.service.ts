import { Injectable } from '@angular/core';
import { defaultHttpOptionsFromConfig, RequestConfig } from './http-utils';
import { Observable, ReplaySubject } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
    providedIn: 'root'
})

export class BasketService {
    constructor(
        private http: HttpClient
    ) { }
    // 获取列表
    public fetchGetTableList(data): Observable<any> {
        return this.http.post(`/api/chargingbasket/pageChargingBasket?current=${data.current}&size=${data.size}`, data.body);
    }

    //新增
    public fetchSave(data): Observable<any> {
        let url = "/api/chargingbasket/update"
        return this.http.post(url, data)
    }


    //删除
    public fetchDelete(params, config?: RequestConfig): Observable<any> {
        let url = "/api/chargingbasket/delete"
        return this.http.get(url + `?id=${params.id}`, defaultHttpOptionsFromConfig(config))
    }

    //解绑
    public fetchBreakCode(params, config?: RequestConfig): Observable<any> {
        let url = "/api/chargingbasket/unBind"
        return this.http.get(url + `?bindCodeNumber=${params.bindCodeNumber}`, defaultHttpOptionsFromConfig(config))
    }



    //状态控制
    public fetchIsEnabled(params, config?: RequestConfig): Observable<any> {
        let url = "/api/chargingbasket/isEnabled"
        return this.http.get(url + `?enabled=${params.enbled}&id=${params.id}`, defaultHttpOptionsFromConfig(config))
    }

    //导出（特别传参，指定响应数据类型）
    fetchExportTable(data) {
        let url = `/api/chargingbasket/export?current=${data.current}&size=${data.size}`
        return this.http.post(url, data.body, {
            responseType: "blob",//指定响应中包含的数据类型,指定response 是一个包含二进制数据的 Blob 对象
            observe: 'response',//要获取到完全的response,在 发起请求时 在option中添加 observe: ‘response’;
        })
    }

    //下载模板
    fetchDownloadModel(config?: RequestConfig) {
        let url = "/api/chargingbasket/downTemplate"
        return this.http.get(url, {
            responseType: "blob",//指定响应中包含的数据类型,指定response 是一个包含二进制数据的 Blob 对象
            observe: 'response',//要获取到完全的response,在 发起请求时 在option中添加 observe: ‘response’;
        })
    }

    //导入
    fetchImport(data): Observable<any> {
        let url = "/api/chargingbasket/importExcel"
        return this.http.post(url, data)
    }

    //查看二维码
    public fetchGetCode(params, config?: RequestConfig): Observable<any> {
        let url = "/api/chargingbasket/getQR"
        return this.http.get(url + `?code=${params.code}`, defaultHttpOptionsFromConfig(config))
    }
}