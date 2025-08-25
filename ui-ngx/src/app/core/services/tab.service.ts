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
                this.addTab(event.urlAfterRedirects);
            }
        });
    }
    menu = JSON.parse(localStorage.getItem('menu') || '[]');
    get flatMenu() {
      const menu = [];
      this.menu.forEach(item => {
        !item.pages?.length && menu.push({
          path: item.path,
          name: item.name,
        });
        if(item.pages) {
          item.pages.forEach(page => {
            menu.push({
              path: page.path,
              name: page.name,
            })
          })
        }
      })
      return menu;
    }
    addTab(url: string) {
        const currentTabs = this.tabsSubject.value;
        if (!currentTabs.find(t => t.link === url)) {
            if (!this.flatMenu.find((item: any) => item.path === url)?.name) {
              return;
            }
            const newTab = {
                link: url,
                label: this.flatMenu.find((item: any) => item.path === url)?.name || this.getLabelFromUrl(url),
                icon: 'tab'
            };
            this.tabsSubject.next([...currentTabs, newTab]);
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
        return '详情';
    }
}
