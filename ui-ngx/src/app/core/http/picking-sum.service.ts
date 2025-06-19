import { Injectable } from '@angular/core';
import { defaultHttpOptionsFromConfig, RequestConfig } from './http-utils';
import { Observable, ReplaySubject } from 'rxjs';
import { HttpClient } from '@angular/common/http';



@Injectable({
  providedIn: 'root'
})
export class PickSumService {
  constructor(
    private http: HttpClient
  ) { }


  //获取数据列表
  public fetchGetTableList(data): Observable<any> {
    return this.http.post(`/api/picking/listSumPickingReport?current=${data.current}&size=${data.size}`, data.body);
  }


}