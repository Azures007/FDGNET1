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

import { Component, OnInit } from '@angular/core';
import { AuthService } from '@core/auth/auth.service';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { PageComponent } from '@shared/components/page.component';
import { FormBuilder, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Constants } from '@shared/models/constants';
import { Router } from '@angular/router';
import { OAuth2ClientInfo } from '@shared/models/oauth2.models';
import { Utils } from '@app/modules/home/pages/order-management/w-utils';

@Component({
  selector: 'tb-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent extends PageComponent implements OnInit {

  loginFormGroup = this.fb.group({
    username: ['', [Validators.required]],
    password: ['', [Validators.required]],
    pkOrg: ['', [Validators.required]],
    cwkid: ['', [Validators.required]],
  });
  oauth2Clients: Array<OAuth2ClientInfo> = null;
  orgList = [];
  lineList = [];
  userNameComplete = false;
  constructor(protected store: Store<AppState>,
              private authService: AuthService,
              public fb: FormBuilder,
              private utils: Utils,
              private router: Router) {
    super(store);
  }

  ngOnInit() {
    this.oauth2Clients = this.authService.oauth2Clients;
  }
  changeOrg(e) {
    this.authService.getLineList(this.loginFormGroup.value.username, this.loginFormGroup.value.pkOrg).subscribe(
      (res: any) => {
        this.lineList = res.data || [];
      }
    )
  }
  login(): void {
    if (!this.userNameComplete) {
      if(!this.loginFormGroup.value.username) {
        this.utils.showMessage('请输入账号', 'error');
        return;
      }
      this.authService.getOrgList(this.loginFormGroup.value.username).subscribe(
        (res: any) => {
          this.orgList = res.data || [];
          if(this.orgList.length) {
            this.userNameComplete = true;
          }
        }
      );
      return;
    }
    if (this.loginFormGroup.valid) {

      this.authService.login(this.loginFormGroup.value).subscribe(
        () => {},
        (error: HttpErrorResponse) => {
          if (error && error.error && error.error.errorCode) {
            if (error.error.errorCode === Constants.serverErrorCode.credentialsExpired) {
              this.router.navigateByUrl(`login/resetExpiredPassword?resetToken=${error.error.resetToken}`);
            }
          }
        }
      );
    } else {
      Object.keys(this.loginFormGroup.controls).forEach(field => {
        const control = this.loginFormGroup.get(field);
        control.markAsTouched({onlySelf: true});
      });
    }
  }

}
