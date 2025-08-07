import { Injectable } from '@angular/core';
import { defaultHttpOptionsFromConfig, RequestConfig } from './http-utils';
import { Observable, ReplaySubject } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
    providedIn: 'root'
})

export class AccountService {
    constructor(
        private http: HttpClient
    ) { }


    //获取列表
    public fetchGetTableList(data): Observable<any> {
        let url = `/api/user/pageUser?current=${data.current}&size=${data.size}`
        return this.http.post(url, data.body)
    }

    //获取模糊列表
    public fetchGetDialogList(params, config?: RequestConfig): Observable<any> {
        let url = `/api/user/pageUserByUserNameAndName?nameAndUsername=${params.value}`
        return this.http.get(url, defaultHttpOptionsFromConfig(config))
    }

    //新增账号
    public fetchSaveUser(data): Observable<any> {
        let url = "/api/user/updateAndSave"
        return this.http.post(url, data)
    }

    //删除账号
    public fetchDelete(params, config?: RequestConfig): Observable<any> {
        let url = "/api/user/delete"
        return this.http.get(url + `?userId=${params.userId}`, defaultHttpOptionsFromConfig(config))
    }

    //账号状态控制
    public fetchIsEnabled(params, config?: RequestConfig): Observable<any> {
        let url = "/api/user/isEnabled"
        return this.http.get(url + `?enabled=${params.enabled}&userId=${params.userId}`, defaultHttpOptionsFromConfig(config))
    }

    //重置密码
    public fetchReset(params, config?: RequestConfig): Observable<any> {
        let url = "/api/user/resetPassword"
        return this.http.get(url + `?userId=${params.userId}&password=${params.password}`, defaultHttpOptionsFromConfig(config))
    }

    //设置角色
    public fetchSetRole(params, config?: RequestConfig): Observable<any> {
        let url = "/api/user/setRole"
        return this.http.get(url + `?roleId=${params.roleId}&userId=${params.userId}`, defaultHttpOptionsFromConfig(config))
    }

    public fetchBaseList(params): Observable<any> {
        let url = ""
        if(params.type == 'base'){
            url = "/api/manage/org/list"
        } else if(params.type == 'line'){
            url = "/api/manage/workline/byOrg?pkOrg=" + params.id
        } else {
            url = "/api/manage/warehouse/byOrg?pkOrg=" + params.id
        }
        return this.http.get(url)
    }

    // 根据用户名获取基地列表
    getOrgList(): Observable<any> {
      const userName = localStorage.getItem('userInfo') ? JSON.parse(localStorage.getItem('userInfo')).email : '';
      return this.http.get('/api/noauth/user/orgList?username=' + userName);

    }

    // 获取用户详情
    public fetchUserDetail(id): Observable<any> {
        let url = "/api/user/listUserDetail"
        return this.http.get(url + `?userId=${id}`)
    }
}
