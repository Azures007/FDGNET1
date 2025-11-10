import { Injectable } from '@angular/core';
import { defaultHttpOptionsFromConfig, RequestConfig } from './http-utils';
import { Observable, ReplaySubject } from 'rxjs';
import { HttpClient } from '@angular/common/http';

import { myPost} from '../api/httpUtils.js'

@Injectable({
    providedIn: 'root'
})

export class ClassService {
    constructor(
        private http: HttpClient,
    ) { }

    //获取列表
    public fetchGetTableList(data): Observable<any> {
        let url = `/api/class/classList?current=${data.current}&size=${data.size}`
        return this.http.post(url, data.body)
    }

    //导出
    public fetchExportTable(data): Observable<any> {
        let url = `/api/class/export?current=${data.current}&size=${data.size}`
        return this.http.post(url, data.body, {
            responseType: "blob",//指定响应中包含的数据类型,指定response 是一个包含二进制数据的 Blob 对象
            observe: 'response',//要获取到完全的response,在 发起请求时 在option中添加 observe: ‘response’;
        })
    }



    //新建或保存
    public fetchSaveAdd(data): Observable<any> {
        let url = `/api/class/saveClass`
        return this.http.post(url, data)
    }

    //状态控制
    public fetchIsEnabled(params, config?: RequestConfig): Observable<any> {
        let url = "/api/class/isEnabled"
        return this.http.get(url + `?enabledSt=${params.enabledSt}&classId=${params.classId}`, defaultHttpOptionsFromConfig(config))
    }

    //删除
    public fetchDelete(params, config?: RequestConfig): Observable<any> {
        let url = "/api/class/deleteClass"
        return this.http.get(url + `?classId=${params.classId}`, defaultHttpOptionsFromConfig(config))
    }

    //获取组员列表
    public fetchGetTeamList(params, config?: RequestConfig): Observable<any> {
        let url = `/api/class/getRelByClassId?classId=${params.classId}`
        return this.http.get(url, defaultHttpOptionsFromConfig(config))
    }

    //保存组员列表
    public fetchSaveTeam(data): Observable<any> {
        let url = `/api/class/saveTSysClassPersonnelRel?classId=${data.classId}`
        return this.http.post(url, data.personnelList)
    }

    //获取组长列表
    public fetchGetTeamLeaderList(params, config?: RequestConfig): Observable<any> {
        let url = `/api/class/getGroupLeaderRelByClassId?classId=${params.classId}`
        return this.http.get(url, defaultHttpOptionsFromConfig(config))
    }
    public fetchBaseList(): Observable<any> {
        return this.http.get('/api/manage/workline/list')
    }
}
