import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})

export class PdMgtService {
  constructor(
    private http: HttpClient
  ) { }
  // 获取列表
  public fetchGetTableList(data): Observable<any> {
    return this.http.post(`/api/tSysPdRecord/pdRecordList?current=${data.current}&size=${data.size}`, data.body);
  }
  // 获取列表
  public fetchGetTableListWithSplit(data): Observable<any> {
    return this.http.post(`/api/tSysPdRecord/pdRecordListWithSplit?current=${data.current}&size=${data.size}`, data.body);
  }
}
