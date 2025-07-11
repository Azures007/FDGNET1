import { Injectable } from '@angular/core';
import { defaultHttpOptionsFromConfig, RequestConfig } from './http-utils';
import { Observable, ReplaySubject } from 'rxjs';
import { HttpClient } from '@angular/common/http';



@Injectable({
  providedIn: 'root'
})
export class OrderService {
  constructor(
    private http: HttpClient
  ) { }

  // 获取订单列表
  public fetchGetTableList(data): Observable<any> {
    return this.http.post(`/api/manage/orderhead/query?current=${data.current}&size=${data.size}`, data.body);
  }

  // 导出
  public fetchExport(data): Observable<any> {
    return this.http.post(`/api/manage/orderhead/exportorder?current=${data.current}&size=${data.size}`, data.body, {
      responseType: 'blob',
      observe: 'response'
    });
  }

  // 获取订单详情
  public fetchGetOrderDetails(data): Observable<any> {
    return this.http.get(`/api/manage/orderhead/get?orderId=${data.orderId}`);
  }

  // 获取订单中的工序详情
  public fetchGetProcessInfo(data): Observable<any> {
    return this.http.get(`/api/orderhead/getProcessInfo?orderId=${data.orderId}`);
  }


  // 获取订单状态
  public fetchGetOrdierStatus(data): Observable<any> {
    return this.http.get(`/api/tSysCodeDsc/getCodeByCodeCl?current=0&size=9999&codeClId=${data.codeClId}`);
  }


  // 接单开工
  public fetchStartOrder(data): Observable<any> {
    return this.http.post(`/api/orderhead/startOrder?orderId=${data.orderId}&craftId=${data.craftId}&craftDesc=${data.craftDesc}`, {});
  }

  //通过物料获取工艺路线
  public fetchGetMaterial(data): Observable<any> {
    return this.http.post(`/api/orderhead/getCraftInfo?materialNumber=${data.materialNumber}&orderId=${data.orderId}`, {});
  }

  //批量接单开工
  public fetchListStartOrder(data): Observable<any> {
    return this.http.post(`/api/orderhead/startOrderBatch`, data);
  }

  //改变班别
  public fetchChangeOrderClass(data): Observable<any> {
    return this.http.post(`/api/orderhead/changeOrderClass`, data);
  }

  //根据订单ID获取班别列表
  public fetchGetOrderClassInfo(data): Observable<any> {
    return this.http.get(`/api/orderhead/getOrderClassInfo?orderId=${data.orderId}`);
  }
  public fetchBaseList(): Observable<any> {
        return this.http.get('/api/manage/workline/list')
    }
}
