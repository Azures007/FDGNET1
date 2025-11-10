import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';



@Injectable({
  providedIn: 'root'
})
export class RecipeService {
  constructor(
    private http: HttpClient
  ) { }

  // 获取配方列表
  public fetchGetTableList(data): Observable<any> {
    return this.http.post(`/api/recipe/list?current=${data.current}&size=${data.size}`, data.body);
  }

  // 保存质检
  public fetchSave(data): Observable<any> {
    return this.http.post(`/api/recipe/save`, data);
  }

  // 禁用启用质检
  public fetchDisable(data): Observable<any> {
    return this.http.get(`/api/recipe/updateStatus?recipeId=${data.id}&status=${data.isEnabled}`, {});
  }

  // 获取质检详情
  public fetchGetDetails(data): Observable<any> {
    return this.http.get(`/api/recipe/detail?recipeId=${data}`);
  }

  // 删除质检
  public fetchDel(data): Observable<any> {
    return this.http.get(`/api/recipe/delete?recipeId=${data}`);
  }

  // 搜索列表
  public fetchGetFiterTableList(data): Observable<any> {
    return this.http.get(`/api/recipe/availableProducts?current=${data.current}&size=${data.size}&recipeId=${data.recipeId}&productName=${data.productName}`);
  }

  // 获取绑定列表
  public fetchGetBindingTableList(data): Observable<any> {
    return this.http.get(`/api/recipe/productBindings?recipeId=${data}`);
  }

  // 保存绑定
  public fetchSaveMaterial(data): Observable<any> {
    return this.http.post(`/api/recipe/saveProductBindings?recipeId=${data.recipeId}`, data.body);
  }

  // 复制配方
  public fetchCopy(data): Observable<any> {
    return this.http.post(`/api/recipe/copy`, {recipeId: data});
  }

}
