import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})

export class InventoryMgtService {
  constructor(
    private http: HttpClient
  ) { }
  // 获取列表
  public fetchGetTableList(data): Observable<any> {
    return this.http.get(`/api/manage/invetory/query?current=${data.current}&size=${data.size}&warehouseName=${data.body.warehouseName}&materialName=${data.body.materialName}&spec=${data.body.spec}`);
  }
}
