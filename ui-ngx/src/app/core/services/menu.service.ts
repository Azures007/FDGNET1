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

import { Injectable } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { select, Store } from '@ngrx/store';
import { AppState } from '../core.state';
import { selectAuth, selectIsAuthenticated } from '../auth/auth.selectors';
import { take } from 'rxjs/operators';
import { HomeSection, MenuSection } from '@core/services/menu.models';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { Authority } from '@shared/models/authority.enum';
import { guid } from '@core/utils';
import { AuthState } from '@core/auth/auth.models';
import { RoleService } from '@app/core/http/role.service';
import { TechnologicalService } from '@app/core/http/technological.service';
import { DictionaryService } from '@app/core/http/dictionary.service';

@Injectable({
  providedIn: 'root'
})
export class MenuService {

  menuSections$: Subject<Array<MenuSection>> = new BehaviorSubject<Array<MenuSection>>([]);
  homeSections$: Subject<Array<HomeSection>> = new BehaviorSubject<Array<HomeSection>>([]);

  constructor(
    private store: Store<AppState>,
    private authService: AuthService,
    private roleService: RoleService,
    private technologicalService: TechnologicalService,
    private DictionaryService: DictionaryService,
  ) {

    this.store.pipe(select(selectIsAuthenticated)).subscribe(
      (authenticated: boolean) => {
        if (authenticated) {
          let tem = [];
          this.roleService.fetchGetCurrentMenu().subscribe(res => {
            let obj3 = {
              createdName: null,
              createdTime: null,
              enabled: "0",
              flag: "0",
              isFlag: "1",
              menuExplain: "domain",
              menuId: 111,
              menuName: "菜单管理",
              menuType: "link",
              parentId: 0,
              path: "/menuAdmin",
              select: false,
              tsysMenus: [

              ],
              updatedName: null,
              updatedTime: null,
            }
            // res.data.push(obj2);
            res.data.push(obj3);
            for (let i = 0; i < res.data.length; i++) {
              if (res.data[i].menuId == 10000 || res.data[i].menuId == 40000) {

              } else {
                if (res.data[i].tsysMenus != null) {
                  let par = {
                    id: guid(),
                    name: res.data[i].menuName,
                    type: res.data[i].menuType,
                    path: res.data[i].path,
                    icon: res.data[i].menuExplain,
                    pages: []
                  }
                  for (let j = 0; j < res.data[i].tsysMenus.length; j++) {
                    let par_s = {
                      id: guid(),
                      name: res.data[i].tsysMenus[j].menuName,
                      type: res.data[i].tsysMenus[j].menuType,
                      path: res.data[i].tsysMenus[j].path,
                      icon: res.data[i].tsysMenus[j].menuExplain,
                    }
                    par.pages.push(par_s)
                  }
                  tem.push(par)
                } else {
                  let par = {
                    id: guid(),
                    name: res.data[i].menuName,
                    type: res.data[i].menuType,
                    path: res.data[i].path,
                    icon: res.data[i].menuExplain,
                  }
                  tem.push(par)
                }
              }
            }
            this.buildMenu(tem);
            this.roleService.fetchGetCurrentButton().subscribe(res => {
              localStorage.setItem('btns', JSON.stringify(res.data));
            })
          })
          this.roleService.fetchGetMid({ params: { current: 0, size: 999 }, body: {} }).subscribe(res => {
            localStorage.setItem('orgs', JSON.stringify(res.data.list));
          })
          this.roleService.fetchGetMidDept({ params: { current: 0, size: 999 }, body: {} }).subscribe(res => {
            localStorage.setItem('depts', JSON.stringify(res.data.list));
          })
          this.technologicalService.fetchGetTableList({ current: 0, size: 999 }).subscribe(res => {
            localStorage.setItem('process', JSON.stringify(res.data.content));
          });
          this.DictionaryService.fetchGetTypeTableList({
            current: 0,
            size: 999,
            codeClId: 'ERPPROCESS0000',
            enabledSt: 1
          }).subscribe(res => {
            localStorage.setItem('erpProcess', JSON.stringify(res.data.list));
          });
        }
      }
    );
  }

  private buildMenu(tem) {
    this.store.pipe(select(selectAuth), take(1)).subscribe(
      (authState: AuthState) => {
        if (authState.authUser) {
          let menuSections: Array<MenuSection>;
          let homeSections: Array<HomeSection>;
          switch (authState.authUser.authority) {
            case Authority.SYS_ADMIN:
              menuSections = this.buildSysAdminMenu(authState);
              homeSections = this.buildSysAdminHome(authState);
              break;
            case Authority.TENANT_ADMIN:
              menuSections = this.buildTenantAdminMenu(authState, tem);
              homeSections = this.buildTenantAdminHome(authState);
              break;
            case Authority.CUSTOMER_USER:
              menuSections = this.buildCustomerUserMenu(authState, tem);
              homeSections = this.buildCustomerUserHome(authState);
              break;
          }
          localStorage.setItem('menu', JSON.stringify(menuSections));
          this.menuSections$.next(menuSections);
          this.homeSections$.next(homeSections);
        }
      }
    );
  }

  private buildSysAdminMenu(authState: AuthState): Array<MenuSection> {
    const sections: Array<MenuSection> = [];
    sections.push(
      {
        id: guid(),
        name: 'home.home',
        type: 'link',
        path: '/home',
        icon: 'home'
      },
      {
        id: guid(),
        name: 'tenant.tenants',
        type: 'link',
        path: '/tenants',
        icon: 'supervisor_account'
      },
      {
        id: guid(),
        name: 'tenant-profile.tenant-profiles',
        type: 'link',
        path: '/tenantProfiles',
        icon: 'mdi:alpha-t-box',
        isMdiIcon: true
      },
      {
        id: guid(),
        name: 'widget.widget-library',
        type: 'link',
        path: '/widgets-bundles',
        icon: 'now_widgets'
      },
      {
        id: guid(),
        name: 'admin.system-settings',
        type: 'toggle',
        path: '/settings',
        height: '240px',
        icon: 'settings',
        pages: [
          {
            id: guid(),
            name: 'admin.general',
            type: 'link',
            path: '/settings/general',
            icon: 'settings_applications'
          },
          {
            id: guid(),
            name: 'admin.outgoing-mail',
            type: 'link',
            path: '/settings/outgoing-mail',
            icon: 'mail'
          },
          {
            id: guid(),
            name: 'admin.sms-provider',
            type: 'link',
            path: '/settings/sms-provider',
            icon: 'sms'
          },
          {
            id: guid(),
            name: 'admin.security-settings',
            type: 'link',
            path: '/settings/security-settings',
            icon: 'security'
          },
          {
            id: guid(),
            name: 'admin.oauth2.oauth2',
            type: 'link',
            path: '/settings/oauth2',
            icon: 'security'
          },
          {
            id: guid(),
            name: 'resource.resources-library',
            type: 'link',
            path: '/settings/resources-library',
            icon: 'folder'
          }
        ]
      }
    );
    return sections;
  }

  private buildSysAdminHome(authState: AuthState): Array<HomeSection> {
    const homeSections: Array<HomeSection> = [];
    homeSections.push(
      {
        name: 'tenant.management',
        places: [
          {
            name: 'tenant.tenants',
            icon: 'supervisor_account',
            path: '/tenants'
          },
          {
            name: 'tenant-profile.tenant-profiles',
            icon: 'mdi:alpha-t-box',
            isMdiIcon: true,
            path: '/tenantProfiles'
          },
        ]
      },
      {
        name: 'widget.management',
        places: [
          {
            name: 'widget.widget-library',
            icon: 'now_widgets',
            path: '/widgets-bundles'
          }
        ]
      },
      {
        name: 'admin.system-settings',
        places: [
          {
            name: 'admin.general',
            icon: 'settings_applications',
            path: '/settings/general'
          },
          {
            name: 'admin.outgoing-mail',
            icon: 'mail',
            path: '/settings/outgoing-mail'
          },
          {
            name: 'admin.sms-provider',
            icon: 'sms',
            path: '/settings/sms-provider'
          },
          {
            name: 'admin.security-settings',
            icon: 'security',
            path: '/settings/security-settings'
          },
          {
            name: 'admin.oauth2.oauth2',
            icon: 'security',
            path: '/settings/oauth2'
          },
          {
            name: 'resource.resources-library',
            icon: 'folder',
            path: '/settings/resources-library'
          }
        ]
      }
    );
    return homeSections;
  }

  private buildTenantAdminMenu(authState: AuthState, tem): Array<MenuSection> {
    const sections: Array<MenuSection> = [];
    for (let i = 0; i < tem.length; i++) {
      sections.push(tem[i]);
    }
    // const sections: Array<MenuSection> = [];
    // sections.push(
    //   {
    //     id: guid(),
    //     name: 'home.home',
    //     type: 'link',
    //     path: '/home',
    //     notExact: true,
    //     icon: 'home'
    //   },
    //   {
    //     id: guid(),
    //     name: 'role.role-admin',
    //     type: 'link',
    //     path: '/role',
    //     icon: 'domain'
    //   },
    //   {
    //     id: guid(),
    //     name: 'account.account-admin',
    //     type: 'link',
    //     path: '/account',
    //     icon: 'domain'
    //   },
    //   {
    //     id: guid(),
    //     name: 'dictionary.dictionary-admin',
    //     type: 'link',
    //     path: '/dictionary',
    //     icon: 'domain'
    //   },
    //   {
    //     id: guid(),
    //     name: 'my-device.my-device-admin',
    //     type: 'link',
    //     path: '/mydevice',
    //     icon: 'domain'
    //   },
    //   {
    //     id: guid(),
    //     name: 'staff.staff-admin',
    //     type: 'link',
    //     path: '/staff',
    //     icon: 'domain'
    //   },
    //   {
    //     id: guid(),
    //     name: '班别管理',
    //     type: 'link',
    //     path: '/class',
    //     icon: 'domain'
    //   },
    //   {
    //     id: guid(),
    //     name: 'rulechain.rulechains',
    //     type: 'link',
    //     path: '/ruleChains',
    //     icon: 'settings_ethernet'
    //   },
    //   {
    //     id: guid(),
    //     name: 'order.management',
    //     type: 'toggle',
    //     path: '/order',
    //     icon: 'bookmarks',
    //     pages: [
    //       {
    //         id: guid(),
    //         name: 'order.list',
    //         type: 'link',
    //         path: '/order/list',
    //         icon: 'bookmarks'
    //       }
    //     ]
    //   },
    //   {
    //     id: guid(),
    //     name: 'technological.technological',
    //     type: 'toggle',
    //     path: '/technological',
    //     icon: 'bookmarks',
    //     pages: [
    //       {
    //         id: guid(),
    //         name: 'technological.processRoute',
    //         type: 'link',
    //         path: '/technological/processRoute',
    //         icon: 'bookmarks'
    //       },
    //       {
    //         id: guid(),
    //         name: 'technological.processManage',
    //         type: 'link',
    //         path: '/technological/processManage',
    //         icon: 'bookmarks'
    //       },
    //     ]
    //   },
    //   {
    //     id: guid(),
    //     name: 'customer.customers',
    //     type: 'link',
    //     path: '/customers',
    //     icon: 'supervisor_account'
    //   },
    //   {
    //     id: guid(),
    //     name: 'asset.assets',
    //     type: 'link',
    //     path: '/assets',
    //     icon: 'domain'
    //   },
    //   {
    //     id: guid(),
    //     name: 'device.devices',
    //     type: 'link',
    //     path: '/devices',
    //     icon: 'devices_other'
    //   },
    //   {
    //     id: guid(),
    //     name: 'device-profile.device-profiles',
    //     type: 'link',
    //     path: '/deviceProfiles',
    //     icon: 'mdi:alpha-d-box',
    //     isMdiIcon: true
    //   },
    //   {
    //     id: guid(),
    //     name: 'ota-update.ota-updates',
    //     type: 'link',
    //     path: '/otaUpdates',
    //     icon: 'memory'
    //   },
    //   {
    //     id: guid(),
    //     name: 'entity-view.entity-views',
    //     type: 'link',
    //     path: '/entityViews',
    //     icon: 'view_quilt'
    //   }
    // );
    // if (authState.edgesSupportEnabled) {
    //   sections.push(
    //     {
    //       id: guid(),
    //       name: 'edge.edge-instances',
    //       type: 'link',
    //       path: '/edgeInstances',
    //       icon: 'router'
    //     },
    //     {
    //       id: guid(),
    //       name: 'edge.management',
    //       type: 'toggle',
    //       path: '/edgeManagement',
    //       height: '40px',
    //       icon: 'settings_input_antenna',
    //       pages: [
    //         {
    //           id: guid(),
    //           name: 'edge.rulechain-templates',
    //           type: 'link',
    //           path: '/edgeManagement/ruleChains',
    //           icon: 'settings_ethernet'
    //         }
    //       ]
    //     }
    //   );
    // }
    // sections.push(
    //   {
    //     id: guid(),
    //     name: 'widget.widget-library',
    //     type: 'link',
    //     path: '/widgets-bundles',
    //     icon: 'now_widgets'
    //   },
    //   {
    //     id: guid(),
    //     name: 'dashboard.dashboards',
    //     type: 'link',
    //     path: '/dashboards',
    //     icon: 'dashboards'
    //   },
    //   {
    //     id: guid(),
    //     name: 'audit-log.audit-logs',
    //     type: 'link',
    //     path: '/auditLogs',
    //     icon: 'track_changes'
    //   },
    //   {
    //     id: guid(),
    //     name: 'api-usage.api-usage',
    //     type: 'link',
    //     path: '/usage',
    //     icon: 'insert_chart',
    //     notExact: true
    //   },
    //   {
    //     id: guid(),
    //     name: 'admin.system-settings',
    //     type: 'toggle',
    //     path: '/settings',
    //     height: '80px',
    //     icon: 'settings',
    //     pages: [
    //       {
    //         id: guid(),
    //         name: 'admin.home-settings',
    //         type: 'link',
    //         path: '/settings/home',
    //         icon: 'settings_applications'
    //       },
    //       {
    //         id: guid(),
    //         name: 'resource.resources-library',
    //         type: 'link',
    //         path: '/settings/resources-library',
    //         icon: 'folder'
    //       }
    //     ]
    //   }
    // );
    return sections;
  }

  private buildTenantAdminHome(authState: AuthState): Array<HomeSection> {
    const homeSections: Array<HomeSection> = [];
    // homeSections.push(
    //   {
    //     name: 'rulechain.management',
    //     places: [
    //       {
    //         name: 'rulechain.rulechains',
    //         icon: 'settings_ethernet',
    //         path: '/ruleChains'
    //       }
    //     ]
    //   },
    //   {
    //     name: 'customer.management',
    //     places: [
    //       {
    //         name: 'customer.customers',
    //         icon: 'supervisor_account',
    //         path: '/customers'
    //       }
    //     ]
    //   },
    //   {
    //     name: 'asset.management',
    //     places: [
    //       {
    //         name: 'asset.assets',
    //         icon: 'domain',
    //         path: '/assets'
    //       }
    //     ]
    //   },
    //   {
    //     name: 'device.management',
    //     places: [
    //       {
    //         name: 'device.devices',
    //         icon: 'devices_other',
    //         path: '/devices'
    //       },
    //       {
    //         name: 'device-profile.device-profiles',
    //         icon: 'mdi:alpha-d-box',
    //         isMdiIcon: true,
    //         path: '/deviceProfiles'
    //       },
    //       {
    //         name: 'ota-update.ota-updates',
    //         icon: 'memory',
    //         path: '/otaUpdates'
    //       }
    //     ]
    //   },
    //   {
    //     name: 'entity-view.management',
    //     places: [
    //       {
    //         name: 'entity-view.entity-views',
    //         icon: 'view_quilt',
    //         path: '/entityViews'
    //       }
    //     ]
    //   }
    // );
    // if (authState.edgesSupportEnabled) {
    //   homeSections.push(
    //     {
    //       name: 'edge.management',
    //       places: [
    //         {
    //           name: 'edge.edge-instances',
    //           icon: 'router',
    //           path: '/edgeInstances'
    //         },
    //         {
    //           name: 'edge.rulechain-templates',
    //           icon: 'settings_ethernet',
    //           path: '/edgeManagement/ruleChains'
    //         }
    //       ]
    //     }
    //   );
    // }
    // homeSections.push(
    //   {
    //     name: 'dashboard.management',
    //     places: [
    //       {
    //         name: 'widget.widget-library',
    //         icon: 'now_widgets',
    //         path: '/widgets-bundles'
    //       },
    //       {
    //         name: 'dashboard.dashboards',
    //         icon: 'dashboard',
    //         path: '/dashboards'
    //       }
    //     ]
    //   },
    //   {
    //     name: 'audit-log.audit',
    //     places: [
    //       {
    //         name: 'audit-log.audit-logs',
    //         icon: 'track_changes',
    //         path: '/auditLogs'
    //       },
    //       {
    //         name: 'api-usage.api-usage',
    //         icon: 'insert_chart',
    //         path: '/usage'
    //       }
    //     ]
    //   },
    //   {
    //     name: 'admin.system-settings',
    //     places: [
    //       {
    //         name: 'admin.home-settings',
    //         icon: 'settings_applications',
    //         path: '/settings/home'
    //       },
    //       {
    //         name: 'resource.resources-library',
    //         icon: 'folder',
    //         path: '/settings/resources-library'
    //       }
    //     ]
    //   }
    // );
    return homeSections;
  }

  private buildCustomerUserMenu(authState: AuthState, tem): Array<MenuSection> {

    const sections: Array<MenuSection> = [];



    for (let i = 0; i < tem.length; i++) {
      sections.push(tem[i]);
    }
    return sections;
  }

  private buildCustomerUserHome(authState: AuthState): Array<HomeSection> {
    const homeSections: Array<HomeSection> = [];
    // homeSections.push(
    //   {
    //     name: 'asset.view-assets',
    //     places: [
    //       {
    //         name: 'asset.assets',
    //         icon: 'domain',
    //         path: '/assets'
    //       }
    //     ]
    //   },
    //   {
    //     name: 'device.view-devices',
    //     places: [
    //       {
    //         name: 'device.devices',
    //         icon: 'devices_other',
    //         path: '/devices'
    //       }
    //     ]
    //   },
    //   {
    //     name: 'entity-view.management',
    //     places: [
    //       {
    //         name: 'entity-view.entity-views',
    //         icon: 'view_quilt',
    //         path: '/entityViews'
    //       }
    //     ]
    //   }
    // );
    // if (authState.edgesSupportEnabled) {
    //   homeSections.push(
    //     {
    //       name: 'edge.management',
    //       places: [
    //         {
    //           name: 'edge.edge-instances',
    //           icon: 'settings_input_antenna',
    //           path: '/edgeInstances'
    //         }
    //       ]
    //     }
    //   );
    // }
    // homeSections.push(
    //   {
    //     name: 'dashboard.view-dashboards',
    //     places: [
    //       {
    //         name: 'dashboard.dashboards',
    //         icon: 'dashboard',
    //         path: '/dashboards'
    //       }
    //     ]
    //   }
    // );
    return homeSections;
  }

  public menuSections(): Observable<Array<MenuSection>> {
    return this.menuSections$;
  }

  public homeSections(): Observable<Array<HomeSection>> {
    return this.homeSections$;
  }

}

