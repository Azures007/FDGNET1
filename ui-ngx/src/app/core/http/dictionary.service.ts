import { Injectable } from '@angular/core';
import { defaultHttpOptionsFromConfig, RequestConfig } from './http-utils';
import { Observable, ReplaySubject } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
    providedIn: 'root'
})

export class DictionaryService {
    constructor(
        private http: HttpClient
    ) { }


    //获取类型列表
    public fetchGetTypeList(): Observable<any> {
        let url = `/api/tSysCodeDsc/getGroupCode`
        return this.http.get(url)
    }

    //获取字典列表
    public fetchGetinitTableList(data): Observable<any> {
        let url = `/api/tSysCodeDsc/codeList?current=${data.current}&size=${data.size}`
        return this.http.post(url, data.body)
    }

    //根据字典分类获取字典列表
    public fetchGetTypeTableList(params, config?: RequestConfig): Observable<any> {
        let status = '';
        params.enabledSt == undefined ? status = "" : status = params.enabledSt;
        let url = `/api/tSysCodeDsc/getCodeByCodeCl?current=${params.current}&size=${params.size}&codeClId=${params.codeClId}&enabledSt=${status}`
        return this.http.get(url, defaultHttpOptionsFromConfig(config))
    }

    //新建或编辑保存字典类型
    public fetchSaveDicType(data): Observable<any> {
        let url = "/api/tSysCodeDsc/saveCodeCl"
        return this.http.post(url, data)
    }

    //新建或编辑字典类型
    public fetchSaveDic(data): Observable<any> {
        let url = "/api/tSysCodeDsc/saveCode"
        return this.http.post(url, data)
    }

    //改变字典状态
    public fetchIsEnabled(params, config?: RequestConfig): Observable<any> {
        let url = "/api/tSysCodeDsc/isEnabled"
        return this.http.get(url + `?enabledSt=${params.enabledSt}&codeId=${params.codeId}`, defaultHttpOptionsFromConfig(config))
    }

    //删除字典
    public fetchDeleteCode(params, config?: RequestConfig): Observable<any> {
        let url = "/api/tSysCodeDsc/deleteTSysCodeDsc"
        return this.http.get(url + `?codeId=${params.codeId}`, defaultHttpOptionsFromConfig(config))
    }

}