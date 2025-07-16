import { Injectable } from '@angular/core';
import { defaultHttpOptionsFromConfig, RequestConfig } from './http-utils';
import { Observable, ReplaySubject } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
    providedIn: 'root'
})

export class MenuAdminService {
    constructor(
        private http: HttpClient
    ) { }

    //获取树形数据列表
    public fetchGetTree(params?, config?: RequestConfig): Observable<any> {
        let url = `/api/menu/getMenuListVoByTree`
        return this.http.get(url, defaultHttpOptionsFromConfig(config))
    }

    //获取表格数据
    public fetchGetTable(data): Observable<any> {
        let url = `/api/menu/pageMenu?current=${data.current}&size=${data.size}`
        return this.http.post(url, data.body)
    }

    //新增
    public fetchSave(data): Observable<any> {
        let url = "/api/menu/update"
        return this.http.post(url, data)
    }

    //删除
    public fetchDelete(params, config?: RequestConfig): Observable<any> {
        let url = "/api/menu/delete"
        return this.http.get(url + `?menuId=${params.id}`, defaultHttpOptionsFromConfig(config))
    }

    //状态控制
    public fetchIsEnabled(params, config?: RequestConfig): Observable<any> {
        let url = "/api/menu/isEnabled"
        return this.http.get(url + `?enabled=${params.enbled}&menuId=${params.id}`, defaultHttpOptionsFromConfig(config))
    }

}
