import { Injectable } from '@angular/core';
import { defaultHttpOptionsFromConfig, RequestConfig } from './http-utils';
import { Observable, ReplaySubject } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
    providedIn: 'root'
})

export class SpecService {
    constructor(
        private http: HttpClient
    ) { }

    //获取列表
    public fetchGetTableList(data): Observable<any> {
        let url = `/api/TSysAbrasiveSpecification/getList?current=${data.current}&size=${data.size}`
        return this.http.post(url, data.body)
    }

    //新增磨具
    public fetchSave(data): Observable<any> {
        let url = "/api/TSysAbrasiveSpecification/save"
        return this.http.post(url, data)
    }

    //状态控制
    public fetchIsEnabled(params, config?: RequestConfig): Observable<any> {
        let url = "/api/TSysAbrasiveSpecification/setStatus"
        return this.http.get(url + `?status=${params.status}&id=${params.id}`, defaultHttpOptionsFromConfig(config))
    }

    //删除
    public fetchDelete(params, config?: RequestConfig): Observable<any> {
        let url = "/api/TSysAbrasiveSpecification/delete"
        return this.http.get(url + `?id=${params.id}&version=${params.version}`, defaultHttpOptionsFromConfig(config))
    }
}