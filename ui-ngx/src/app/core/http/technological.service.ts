import { Injectable } from '@angular/core';
import { defaultHttpOptionsFromConfig, RequestConfig } from './http-utils';
import { Observable, ReplaySubject } from 'rxjs';
import { HttpClient } from '@angular/common/http';



@Injectable({
  providedIn: 'root'
})
export class TechnologicalService {
  constructor(
    private http: HttpClient
  ) { }

  // 获取工序管理列表
  public fetchGetTableList(data): Observable<any> {
    return this.http.post(`/api/process/getProcessInfoList?current=${data.current}&size=${data.size}`, data.body || {});
  }


  // 新增工序
  public fetchSaveProcess(data): Observable<any> {
    return this.http.post('/api/process/saveProcess', data);
  }

  // 禁用or启用

  public fetchHandleData(data): Observable<any> {
    return this.http.get(`/api/process/enableProcess?enable=${data.enable}&processId=${data.processId}`);
  }

  // 删除工序

  public fetchDeleteData(data): Observable<any> {
    return this.http.get(`/api/process/delete?processId=${data.processId}`);
  }

  // 获取工序详情
  public fetchGetProcessDetails(data): Observable<any> {
    return this.http.get(`/api/process/processDetail?processId=${data.processId}`);
  }

  // 新增工艺

  public fetchSaveCraft(data): Observable<any> {
    return this.http.post('/api/craft/save', data);
  }

  // 获取工艺路线列表

  public fetchGetCraftTableList(data): Observable<any> {
    const url = `/api/craft/list?current=${data.current}&size=${data.size}`
    return this.http.post(url, data.body);
  }
  // 工艺路线禁用or启用
  public fetchHandleCraftData(data): Observable<any> {
    return this.http.get(`/api/craft/enable?enable=${data.enable}&craftId=${data.craftId}`);
  }

  // 删除工艺

  public fetchDeleteCraftData(data): Observable<any> {
    return this.http.post(`/api/craft/delete?craftId=${data.craftId}`, {});
  }

  // 获取工艺详情
  public fetchGetCraftDetails(data): Observable<any> {
    return this.http.post(`/api/craft/detail?craftId=${data.craftId}`, {});
  }


  // 获取工序下的班别

  public fetchGetClassForProcess(data): Observable<any> {
    return this.http.post(`/api/process/classList?processId=${data.processId}`, {});
  }

  // 获取所有班别
  public fetchGetClassList(data): Observable<any> {
    return this.http.post(`/api/class/classList?current=${data.current}&size=${data.size}`, {});
  }

  // 获取筛选班别
  public fetchGetFiltterClassList(data): Observable<any> {
    return this.http.post(`/api/class/classList?current=${data.current}&size=${data.size}`, data.body);
  }



  // 保存班别
  public fetchSaveClass(data): Observable<any> {
    return this.http.post(`/api/process/processSetting?processId=${data.processId}`, data.body);
  }



}
