import { Injectable } from '@angular/core';
import { ActivatedRoute, Router, NavigationEnd } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { CustomRouteReuseStrategy } from './custom-route-reuse-strategy';

@Injectable({ providedIn: 'root' })
export class TabService {
    private tabsSubject = new BehaviorSubject<any[]>([]);
    tabs$ = this.tabsSubject.asObservable();

    constructor(private router: Router, private route: ActivatedRoute) {
        this.router.events.subscribe(event => {
            if (event instanceof NavigationEnd) {
              if (!this.countTemp) {
                setTimeout(() => {
                  this.countTemp++;
                  this.addTab(event.urlAfterRedirects);
                }, 1100);
              } else {
                this.addTab(event.urlAfterRedirects);
              }
            }
        });
    }
    countTemp = 0;
    menu = JSON.parse(localStorage.getItem('menu') || '[]');
    get flatMenu() {
      const menu = [];
      JSON.parse(localStorage.getItem('menu') || '[]')?.forEach(item => {
        !item.pages?.length && menu.push({
          path: item.path,
          name: item.name,
        });
        if (item.pages?.length && !item.pages.find(page => page.type == 'link')) {
          menu.push({
            path: item.path,
            name: item.name,
          })
        }
        if(item.pages) {
          item.pages.forEach(page => {
            page.type == 'link' && menu.push({
              path: page.path,
              name: page.name,
            })
          })
        }
      })
      return menu;
    }
    addTab(url: string) {
        const currentTabs = this.tabsSubject.value.slice();

        // 规则：订单模块只保留一个页签，在模块内导航（列表⇄详情）时更新同一个页签
        if (url.startsWith('/order/')) {
            const idx = currentTabs.findIndex(t => (t.link as string).startsWith('/order/'));
            const label = this.getLabelFromUrl(url);
            if (idx > -1) {
                // 更新已有“订单”页签
                currentTabs[idx] = { ...currentTabs[idx], link: url, label };
                this.tabsSubject.next(currentTabs);
                return;
            }
            // 如果不存在，则按正常逻辑新增一个“订单”页签
        }

        if (!currentTabs.find(t => t.link === url)) {
            // 检查是否是已知的菜单项或子路由
            const menuItem = this.flatMenu.find((item: any) =>
                item.path === url || url.startsWith(item.path + '/')
            );

            if (!menuItem ) {
                console.log('Tab not added - unknown route:', url);
                return;
            }

            const newTab = {
                link: url,
                label: menuItem?.name || this.getLabelFromUrl(url),
                icon: 'tab'
            };
            this.tabsSubject.next(currentTabs.concat([newTab]));
        } else {
            console.log('Tab already exists:', url);
        }
    }

    removeTab(url: string) {
      const filteredTabs = this.tabsSubject.value.filter(t => t.link !== url);

      // 直接使用URL作为缓存键
      CustomRouteReuseStrategy.waitDelete = url;
      CustomRouteReuseStrategy.clearCache(url);

      // 跳转到上一个页签
      if (this.router.url === url && filteredTabs.length > 0) {
          const lastTab = filteredTabs[filteredTabs.length - 1];
          this.router.navigateByUrl(lastTab.link);
      }

      this.tabsSubject.next(filteredTabs);
    }

    private getLabelFromUrl(url: string): string {
        if(url.includes('/order/details')) {
          return '订单详情'
        }
        if(url.includes('/order/list')) {
          return '订单列表'
        }
        return '详情';
    }
}
