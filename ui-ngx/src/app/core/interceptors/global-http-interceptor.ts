///
/// Copyright © 2016-2021 The Thingsboard Authors
///
/// Licensed under the Apache License, Version 2.0 (the "License");
/// you may not use this file except in compliance with the License.
/// You may obtain a copy of the License at
///
///     http://www.apache.org/licenses/LICENSE-2.0
///
/// Unless required by applicable law or agreed to in writing, software
/// distributed under the License is distributed on an "AS IS" BASIS,
/// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
/// See the License for the specific language governing permissions and
/// limitations under the License.
///

import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/internal/Observable';
import { Inject, Injectable } from '@angular/core';
import { AuthService } from '@core/auth/auth.service';
import { Constants } from '@shared/models/constants';
import { InterceptorHttpParams } from './interceptor-http-params';
import { catchError, delay, finalize, mergeMap, switchMap, tap } from 'rxjs/operators';
import { of, throwError } from 'rxjs';
import { InterceptorConfig } from './interceptor-config';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { ActionLoadFinish, ActionLoadStart } from './load.actions';
import { ActionNotificationShow } from '@app/core/notification/notification.actions';
import { DialogService } from '@core/services/dialog.service';
import { TranslateService } from '@ngx-translate/core';
import { ActivatedRoute, Router, UrlTree } from '@angular/router';

let tmpHeaders = {};

const myprot = '4200';

@Injectable()
export class GlobalHttpInterceptor implements HttpInterceptor {

  private AUTH_SCHEME = 'Bearer ';
  private AUTH_HEADER_NAME = 'X-Authorization';

  private internalUrlPrefixes = [
    '/api/auth/token',
    '/api/rpc'
  ];

  private activeRequests = 0;

  constructor(@Inject(Store) private store: Store<AppState>,
    @Inject(DialogService) private dialogService: DialogService,
    @Inject(TranslateService) private translate: TranslateService,
    @Inject(AuthService) private authService: AuthService,
    private router: Router,
  ) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    if (req.url.startsWith('/api/')) {
      const config = this.getInterceptorConfig(req);
      this.updateLoadingState(config, true);
      let observable$: Observable<HttpEvent<any>>;
      if (this.isTokenBasedAuthEntryPoint(req.url)) {
        if (!AuthService.getJwtToken() && !this.authService.refreshTokenPending()) {
          observable$ = this.handleResponseError(req, next, new HttpErrorResponse({ error: { message: 'Unauthorized!' }, status: 401 }));
        } else if (!AuthService.isJwtTokenValid()) {
          observable$ = this.handleResponseError(req, next, new HttpErrorResponse({ error: { refreshTokenPending: true } }));
        } else {
          observable$ = this.jwtIntercept(req, next);
        }
      } else {
        observable$ = this.handleRequest(req, next);
      }
      return observable$.pipe(
        tap(event => {
          if (event instanceof HttpResponse) {
            // let url = event.url;
            // console.log(url)
            // 成功
            if (event.body == null) {

            } else {
              let url = event.url;
              if (url != 'http://localhost:4200/api/noauth/oauth2Clients?platform=WEB') {
                let times = parseInt(localStorage.getItem('405_times'));
                const port  = window.location.port;
                let portI = url.indexOf(port) + port.length;
                // let portI = url.indexOf(myprot)+ myprot.length;
                let urlFlag = url.substring(portI, portI + 4);
                if (urlFlag == '/api') {
                  let code = event.body.errcode;
                  if (code == 455) {
                    times += 1;
                    localStorage.setItem('405_times', JSON.stringify(times))
                    if (times == 1) {
                      let data = {
                        title: "消息提示",
                        message: `当前登陆令牌无效或已失效请重试`,
                        ok: '确定',
                      }
                      this.dialogService.message(data).subscribe(res => {
                        this.authService.logout();
                      })
                    }
                  } else if (code == 465) {
                    times += 1;
                    localStorage.setItem('405_times', JSON.stringify(times))
                    if (times == 1) {
                      let data = {
                        title: "消息提示",
                        message: `当前登陆令牌无效或已失效请重试`,
                        ok: '确定',
                      }
                      this.dialogService.message(data).subscribe(res => {
                        this.authService.logout();
                      })
                    }
                  } else {

                  }
                }
              }
            }

          }
        }, error => {
          // 失败
          console.log(error)
        }),
        finalize(() => {
          if (req.url.startsWith('/api/')) {
            this.updateLoadingState(config, false);
          }
        })
      );
    } else {
      return next.handle(req);
    }
  }


  private jwtIntercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const newReq = this.updateAuthorizationHeader(req);
    if (newReq) {
      return this.handleRequest(newReq, next);
    } else {
      return throwError(new Error('Could not get JWT token from store.'));
    }
  }

  private handleRequest(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      // mergeMap((evt) => {
      //   return this.handleResponse(evt)
      // }),
      catchError((err) => {
        const errorResponse = err as HttpErrorResponse;
        return this.handleResponseError(req, next, errorResponse);
      })
    );
  }


  handleResponse(evt: any) {
    console.log("拦截器A在数据返回后的拦截处理");
    return new Observable<HttpEvent<any>>(observer => {
      observer.next(evt);
    });
  }

  private handleResponseError(req: HttpRequest<any>, next: HttpHandler, errorResponse: HttpErrorResponse): Observable<HttpEvent<any>> {
    const config = this.getInterceptorConfig(req);
    let unhandled = false;
    const ignoreErrors = config.ignoreErrors;
    const resendRequest = config.resendRequest;
    const errorCode = errorResponse.error ? errorResponse.error.errorCode : null;

    if (errorResponse.error && errorResponse.error.refreshTokenPending || errorResponse.status === 401) {
      console.log("异常详")
      if (errorResponse.error && errorResponse.error.refreshTokenPending ||
        errorCode && errorCode === Constants.serverErrorCode.jwtTokenExpired) {
        return this.refreshTokenAndRetry(req, next);
      } else if (errorCode !== Constants.serverErrorCode.credentialsExpired) {
        unhandled = true;
      }
    } else if (errorResponse.status === 429) {
      if (resendRequest) {
        return this.retryRequest(req, next);
      }
    } else if (errorResponse.status === 403) {
      if (!ignoreErrors) {
        this.dialogService.forbidden();
      }
    } else if (errorResponse.status === 0 || errorResponse.status === -1) {
      this.showError('Unable to connect');
    } else if (!(req.url.startsWith('/api/rpc') || req.url.startsWith('/api/plugins/rpc'))) {
      if (errorResponse.status === 404) {
        if (!ignoreErrors) {
          this.showError(req.method + ': ' + req.url + '<br/>' +
            errorResponse.status + ': ' + errorResponse.statusText);
        }
      } else {
        unhandled = true;
      }
    } else if (errorResponse.status === 200) {
      console.log(errorResponse, '正常')
    }

    if (unhandled && !ignoreErrors) {
      let error = null;
      if (req.responseType === 'text') {
        try {
          error = errorResponse.error ? JSON.parse(errorResponse.error) : null;
        } catch (e) { }
      } else {
        error = errorResponse.error;
      }
      if (error && !error.message) {
        this.showError(this.prepareMessageFromData(error));
      } else if (error && error.message) {
        this.showError(error.message, error.timeout ? error.timeout : 0);
      } else {
        this.showError('Unhandled error code ' + (error ? error.status : '\'Unknown\''));
      }
    }
    return throwError(errorResponse);
  }

  private prepareMessageFromData(data) {
    if (typeof data === 'object' && data.constructor === ArrayBuffer) {
      const msg = String.fromCharCode.apply(null, new Uint8Array(data));
      try {
        const msgObj = JSON.parse(msg);
        if (msgObj.message) {
          return msgObj.message;
        } else {
          return msg;
        }
      } catch (e) {
        return msg;
      }
    } else {
      return data.message || data.error || data;
    }
  }

  private retryRequest(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const thisTimeout = 1000 + Math.random() * 3000;
    return of(null).pipe(
      delay(thisTimeout),
      mergeMap(() => {
        return this.jwtIntercept(req, next);
      }
      ));
  }

  private refreshTokenAndRetry(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return this.authService.refreshJwtToken().pipe(switchMap(() => {
      return this.jwtIntercept(req, next);
    }),
      catchError((err: Error) => {
        this.authService.logout(true);
        const message = err ? err.message : 'Unauthorized!';
        return this.handleResponseError(req, next, new HttpErrorResponse({ error: { message, timeout: 200 }, status: 401 }));
      }));
  }

  private updateAuthorizationHeader(req: HttpRequest<any>): HttpRequest<any> {
    const jwtToken = AuthService.getJwtToken();
    if (jwtToken) {
      req = req.clone({
        setHeaders: (tmpHeaders = {},
          tmpHeaders[this.AUTH_HEADER_NAME] = '' + this.AUTH_SCHEME + jwtToken,
          tmpHeaders)
      });
      req = req.clone({
        setHeaders: (tmpHeaders = {},
          tmpHeaders["app_key"] = '1',
          tmpHeaders)
      });
      // req.headers.append("app_key","1");
      // console.log(req,'req')
      return req;
    } else {
      return null;
    }
  }


  private updateAuthorizationHeader_2(req: HttpRequest<any>): HttpRequest<any> {
    const jwtToken = AuthService.getJwtToken();
    if (jwtToken) {
      req = req.clone({
        setHeaders: (tmpHeaders = {},
          tmpHeaders["app_key"] = '1',
          tmpHeaders)
      });
    } else {
      return null;
    }
  }


  private isInternalUrlPrefix(url): boolean {
    for (const index in this.internalUrlPrefixes) {
      if (url.startsWith(this.internalUrlPrefixes[index])) {
        return true;
      }
    }
    return false;
  }

  private isTokenBasedAuthEntryPoint(url): boolean {
    return url.startsWith('/api/') &&
      !url.startsWith(Constants.entryPoints.login) &&
      !url.startsWith(Constants.entryPoints.tokenRefresh) &&
      !url.startsWith(Constants.entryPoints.nonTokenBased);
  }

  private updateLoadingState(config: InterceptorConfig, isLoading: boolean) {
    if (!config.ignoreLoading) {
      if (isLoading) {
        this.activeRequests++;
      } else {
        this.activeRequests--;
      }
      if (this.activeRequests === 1 && isLoading) {
        this.store.dispatch(new ActionLoadStart());
      } else if (this.activeRequests === 0) {
        this.store.dispatch(new ActionLoadFinish());
      }
    }
  }

  private getInterceptorConfig(req: HttpRequest<any>): InterceptorConfig {
    let config: InterceptorConfig;
    if (req.params && req.params instanceof InterceptorHttpParams) {
      config = (req.params as InterceptorHttpParams).interceptorConfig;
    } else {
      config = new InterceptorConfig(false, false);
    }
    if (this.isInternalUrlPrefix(req.url)) {
      config.ignoreLoading = true;
    }
    return config;
  }

  private showError(error: string, timeout: number = 0) {
    setTimeout(() => {
      this.store.dispatch(new ActionNotificationShow({ message: error, type: 'error' }));
    }, timeout);
  }
}
