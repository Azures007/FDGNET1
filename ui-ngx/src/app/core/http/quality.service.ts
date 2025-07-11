import { Injectable } from '@angular/core';
import { defaultHttpOptionsFromConfig, RequestConfig } from './http-utils';
import { Observable, ReplaySubject } from 'rxjs';
import { HttpClient } from '@angular/common/http';



@Injectable({
  providedIn: 'root'
})
export class QualityService {
  constructor(
    private http: HttpClient
  ) { }

  // 获取质检列表
  public fetchGetTableList(data): Observable<any> {
    return this.http.post(`/api/tSysQualityCategory/qualityCategoryList?current=${data.current}&size=${data.size}&sortField=${data.sortField || ''}&sortOrder=${data.sortOrder || ''}`, data.body);
  }

  // 保存质检
  public fetchSave(data): Observable<any> {
    return this.http.post(`/api/tSysQualityCategory/saveQualityCategory`, data);
  }

  // 禁用启用质检
  public fetchDisable(data): Observable<any> {
    return this.http.post(`/api/tSysQualityCategory/isEnabled?categoryId=${data.id}&enabledSt=${data.isEnabled}`, {});
  }

  // 获取质检详情
  public fetchGetDetails(data): Observable<any> {
    return this.http.get(`/api/tSysQualityCategory/getQualityCategoryById?categoryId=${data}`);
  }

  // 删除质检
  public fetchDel(data): Observable<any> {
    return this.http.get(`/api/tSysQualityCategory/deleteQualityCategory?categoryId=${data}`);
  }

  // 获取方案列表
  public fetchGetPlanList(data): Observable<any> {
    return this.http.post(`/api/tSysQualityPlan/qualityPlanList?current=${data.current}&size=${data.size}&sortField=${data.sortField || ''}&sortOrder=${data.sortOrder || ''}`, data.body);
  }
  public fetchBaseList(): Observable<any> {
    return this.http.get('/api/manage/workline/list')
  }
}
