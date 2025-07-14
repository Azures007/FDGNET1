/**
 * Created by Wangsl on 2022/04/20.
 */
import { Injectable } from '@angular/core';
import {ActionNotificationShow} from '@core/notification/notification.actions';
import { Store } from '@ngrx/store';
import {AppState} from '@core/core.state';
import {DialogService} from '@core/services/dialog.service';
/**
 * Utils类存放和业务无关的公共方法
 * @description
 */
@Injectable({
  providedIn: 'root',
})
export class Utils {


  constructor(protected store: Store<AppState>, private dialogService: DialogService) {

  }



  /**
   * 日期对象转为日期字符串
   * @param date 需要格式化的日期对象
   * @param sFormat 输出格式,默认为yyyy-MM-dd                         年：y，月：M，日：d，时：h，分：m，秒：s
   * @example  dateFormat(new Date())                                "2017-02-28"
   * @example  dateFormat(new Date(),'yyyy-MM-dd')                   "2017-02-28"
   * @example  dateFormat(new Date(),'yyyy-MM-dd hh:mm:ss')         "2017-02-28 09:24:00"
   * @example  dateFormat(new Date(),'hh:mm')                       "09:24"
   * @example  dateFormat(new Date(),'yyyy-MM-ddThh:mm:ss+08:00')   "2017-02-28T09:24:00+08:00"
   *
   */

  public dateFormat(date: Date, sFormat: string = 'yyyy-MM-dd HH:mm:ss'): string {

    if (date instanceof String){
      date = new Date(date);
    }

    const time = {
      Year: 0,
      TYear: '0',
      Month: 0,
      TMonth: '0',
      Day: 0,
      TDay: '0',
      Hour: 0,
      THour: '0',
      hour: 0,
      Thour: '0',
      Minute: 0,
      TMinute: '0',
      Second: 0,
      TSecond: '0',
      Millisecond: 0
    };
    time.Year = date.getFullYear();
    time.TYear = String(time.Year).substr(2);
    time.Month = date.getMonth() + 1;
    time.TMonth = time.Month < 10 ? '0' + time.Month : String(time.Month);
    time.Day = date.getDate();
    time.TDay = time.Day < 10 ? '0' + time.Day : String(time.Day);
    time.Hour = date.getHours();
    time.THour = time.Hour < 10 ? '0' + time.Hour : String(time.Hour);
    time.hour = time.Hour < 13 ? time.Hour : time.Hour - 12;
    time.Thour = time.hour < 10 ? '0' + time.hour : String(time.hour);
    time.Minute = date.getMinutes();
    time.TMinute = time.Minute < 10 ? '0' + time.Minute : String(time.Minute);
    time.Second = date.getSeconds();
    time.TSecond = time.Second < 10 ? '0' + time.Second : String(time.Second);
    time.Millisecond = date.getMilliseconds();

    return sFormat.replace(/yyyy/ig, String(time.Year))
      .replace(/yyy/ig, String(time.Year))
      .replace(/yy/ig, time.TYear)
      .replace(/y/ig, time.TYear)
      .replace('年', String(time.Year) + '年')
      .replace(/MM/g, time.TMonth)
      .replace(/M/g, String(time.Month))
      .replace('月', String(time.Month) + '月')
      .replace(/dd/ig, time.TDay)
      .replace(/d/ig, String(time.Day))
      .replace('日', String(time.TDay) + '日')
      .replace(/HH/g, time.THour)
      .replace(/H/g, String(time.Hour))
      .replace('时', String(time.THour) + '时')
      .replace(/hh/g, time.Thour)
      .replace(/h/g, String(time.hour))
      .replace(/mm/g, time.TMinute)
      .replace(/m/g, String(time.Minute))
      .replace('分', String(time.TMinute) + '分')
      .replace(/ss/ig, time.TSecond)
      .replace(/s/ig, String(time.Second))
      .replace(/fff/ig, String(time.Millisecond))
  }

  /**
   * 手机号码验证
   */
  public isPhoneNum(strDate) {
    const reg = /^1[356789]\d{9}$/;
    if (!reg.exec(strDate)){
      return false;
    }
    return true;
  }

  /**
   * 提示框
   * @param title 标题
   * @param content 提示内容
   * @param fn  执行方法
   */

  confirm(title: string, content: string, fn) {
    this.dialogService.confirm(
      title,
      content,
      '否',
      '是',
      true
    ).subscribe((result) => {
      if (result) {
        fn();
      }
    });
  }


  /**
   * cookie
   * @param name
   * @param value
   * @param time
   */
  addCookie(name, value, time = '') {
    let Days: any;
    if (null == time || undefined === time || time === '') {
      Days = 1;
    } else {
      Days = time;
    }
    const exp = new Date();
    exp.setTime(exp.getTime() + Days * 24 * 60 * 60 * 1000);
    document.cookie = name + '=' + encodeURI(JSON.stringify(value)) + ';expires=' + exp.toUTCString();
  }

  // 是否为空对象
  isEmpty(obj: any): boolean {
    for (const key of Object.keys(obj)) {
      if (Object.prototype.hasOwnProperty.call(obj, key)) {
        return false;
      }
    }
    return true;
  }

  // 判断是否Obj
  isObject(obj: any): boolean {
    return Object.prototype.toString.call(obj) === '[object Object]';
  }

  isRegExp(v: any): boolean {
    return Object.prototype.toString.call(v) === '[object RegExp]';
  }

  toNumber(val: string): number | string {
    const n = parseFloat(val);
    return isNaN(n) ? val : n;
  }

  /**
   * toast
   * @param message
   * @param type 'info' | 'warn' | 'success' | 'error'
   */
  showMessage(message: string, type: any){
    this.store.dispatch(new ActionNotificationShow({
      message,
      type,
      verticalPosition: 'top',
      horizontalPosition: 'center',
      duration: 2000,
    }));
  }

}
