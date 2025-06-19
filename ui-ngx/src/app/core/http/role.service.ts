import { Injectable } from '@angular/core';
import { defaultHttpOptionsFromConfig, RequestConfig } from './http-utils';
import { Observable, ReplaySubject } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { PageLink } from '@shared/models/page/page-link';
import { PageData } from '@shared/models/page/page-data';
import { EntitySubtype } from '@app/shared/models/entity-type.models';
import { AuthService } from '@core/auth/auth.service';
import { BulkImportRequest, BulkImportResult } from '@home/components/import-export/import-export.models';
import { PersistentRpc } from '@shared/models/rpc.models';
import { param } from 'jquery';


@Injectable({
    providedIn: 'root'
})
export class RoleService {
    constructor(
        private http: HttpClient
    ) { }

    //获取列表
    public fetchGetRoleTableList(data): Observable<any> {
        let url = `/api/role/listRole?current=${data.current}&size=${data.size}`
        return this.http.post(url, data.body)
    }

    //新增角色
    public fetchSaveRole(data): Observable<any> {
        let url = "/api/role/saveRole"
        return this.http.post(url, data)
    }

    //获取菜单配置数据
    public fetchMenuPackRole(params, config?: RequestConfig): Observable<any> {
        let url = "/api/menu/listMenu"
        return this.http.get(url + `?roleId=${params}`, defaultHttpOptionsFromConfig(config))
    }

    //提交角色菜单配置
    public fetchSaveRoleMenu(data): Observable<any> {
        let url = "/api/menu/setMenu"
        return this.http.post(url, data)
    }

    //角色状态控制
    public fetchIsEnabledRole(params, config?: RequestConfig): Observable<any> {
        let url = "/api/role/isEnabled"
        return this.http.get(url + `?enabled=${params.enabled}&roleId=${params.roleId}`, defaultHttpOptionsFromConfig(config))
    }

    //删除角色
    public fetchDeleteRole(params, config?: RequestConfig): Observable<any> {
        let url = "/api/role/deleteRole"
        return this.http.get(url + `?roleId=${params.roleId}`, defaultHttpOptionsFromConfig(config))
    }

    //保存角色数据权限配置


    //获取当前用户菜单
    public fetchGetCurrentMenu(config?: RequestConfig): Observable<any> {
        let url = "/api/menu/getMyMenu"
        return this.http.get(url, defaultHttpOptionsFromConfig(config))
    }

    //获取当前用户按钮权限
    public fetchGetCurrentButton(config?: RequestConfig): Observable<any> {
        let url = "/api/menu/listPath"
        return this.http.get(url, defaultHttpOptionsFromConfig(config))
    }

    //获取当前组织列表
    public fetchGetMid(data, config?: RequestConfig): Observable<any> {
        let url = `/api/midOrg/listMidOrg?current=${data.params.current}&size=${data.params.size}`
        return this.http.post(url, data.body)
    }

    //获取当前车间列表
    public fetchGetMidDept(data, config?: RequestConfig): Observable<any> {
        let url = `/api/midDept/listMidDept?current=${data.params.current}&size=${data.params.size}`
        return this.http.post(url, data.body)

    }

}  