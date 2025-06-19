import { Injectable } from '@angular/core';
import { defaultHttpOptionsFromConfig, RequestConfig } from './http-utils';
import { Observable, ReplaySubject } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
    providedIn: 'root'
})

export class MaterialService {
    constructor(
        private http: HttpClient
    ) { }


    //获取列表
    public fetchGetTableList(data): Observable<any> {
        let url = `/api/material/listMaterial?current=${data.current}&size=${data.size}`
        return this.http.post(url, data.body)
    }

    //获取过滤后列表
    public fetchGetFiterTableList(params, config?: RequestConfig): Observable<any> {
        let url = "/api/material/listMaterialFiter"
        return this.http.get(url + `?craftId=${params.craftId}&current=${params.current}&materialCode=${params.materialCode}&size=${params.size}&kdDeptId=${params.kdDeptId}&kdOrgId=${params.kdOrgId   }`, defaultHttpOptionsFromConfig(config))
    }

    //同步
    public fetchSync(params, config?: RequestConfig): Observable<any> {
        let url = "/api/material/sync"
        return this.http.get(url, defaultHttpOptionsFromConfig(config))
    }

    //新增
    public fetchSave(data): Observable<any> {
        let url = "/api/material/update"
        return this.http.post(url, data)
    }

    //删除
    public fetchDelete(params, config?: RequestConfig): Observable<any> {
        let url = "/api/material/delete"
        return this.http.get(url + `?id=${params.id}`, defaultHttpOptionsFromConfig(config))
    }

    //状态控制
    public fetchIsEnabled(params, config?: RequestConfig): Observable<any> {
        let url = "/api/material/isEnabled"
        return this.http.get(url + `?enabled=${params.enbled}&id=${params.id}`, defaultHttpOptionsFromConfig(config))
    }
    
    //工艺路线绑定物料
    public fetchSaveMaterial(data): Observable<any> {
        return this.http.post(`/api/craft/setMaterial?craftId=${data.craftId}`, data.body);
    }

}