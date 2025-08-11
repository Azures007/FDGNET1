import { Injectable } from '@angular/core';
import { defaultHttpOptionsFromConfig, RequestConfig } from './http-utils';
import { Observable, ReplaySubject } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})

export class ContentService {

  constructor(
    private http: HttpClient
  ) { }

  //获取列表
  public fetchGetTableList(data): Observable<any> {
    let url = `/api/tSysNetContentRange/netContentRangeList?current=${data.current}&size=${data.size}`
    return this.http.post(url, data.body)
  }

  // 获取详情
  public fetchGetDetail(id): Observable<any> {
    let url = `/api/tSysNetContentRange/getNetContentRangeById?id=${id}`
    return this.http.get(url)
  }


  // 启用或禁用
  public fetchEnableOrDisable(data): Observable<any> {
    let url = `/api/tSysNetContentRange/updateStatus?id=${data.id}&status=${data.status}`

    return this.http.get(url)
  }

  // 删除
  public fetchDelete(id): Observable<any> {
    let url = `/api/tSysNetContentRange/deleteNetContentRange?id=${id}`
    return this.http.delete(url)
  }

  // 新增或编辑
  public fetchSaveProcess(data): Observable<any> {
    let url = `/api/tSysNetContentRange/saveNetContentRange`
    return this.http.post(url, data)
  }

}
