import { ActivatedRouteSnapshot, DetachedRouteHandle, RouteReuseStrategy } from '@angular/router';

export class CustomRouteReuseStrategy implements RouteReuseStrategy {
  public static handlers: { [key: string]: DetachedRouteHandle } = {};

  private maxCacheCount = 100;
  public static storedRoutes: string[] = [];
  public static waitDelete: string | null;

  shouldDetach(route: ActivatedRouteSnapshot): boolean {
    // return route.data.reuse !== false;
    return false;

  }

  store(route: ActivatedRouteSnapshot, handle: DetachedRouteHandle): void {
    const url = this.getRouteUrl(route);

    // 如果待删除的是当前路由则不存储快照
    if (url.includes(CustomRouteReuseStrategy.waitDelete)) {
      CustomRouteReuseStrategy.waitDelete = null;
      return;
    }

    if (handle) {
      if (Object.keys(CustomRouteReuseStrategy.handlers).length >= this.maxCacheCount) {
        const oldestUrl = CustomRouteReuseStrategy.storedRoutes.shift();
        if (oldestUrl) {
          const oldestHandle = CustomRouteReuseStrategy.handlers[oldestUrl];
          // console.log("🚀 ~ oldestHandle:", oldestHandle)
          delete CustomRouteReuseStrategy.handlers[oldestUrl];
          //
        }
      }
      CustomRouteReuseStrategy.handlers[url] = handle;
      if (!CustomRouteReuseStrategy.storedRoutes.includes(url)) {
        CustomRouteReuseStrategy.storedRoutes.push(url);
      }
    }
  }

  shouldAttach(route: ActivatedRouteSnapshot): boolean {
    const path = route.routeConfig?.path;
    return !!path && !!CustomRouteReuseStrategy.handlers[this.getRouteUrl(route)];
  }

  retrieve(route: ActivatedRouteSnapshot): DetachedRouteHandle {
    const path = route.routeConfig?.path;
    if(path && CustomRouteReuseStrategy.handlers[this.getRouteUrl(route)]) {
      return CustomRouteReuseStrategy.handlers[this.getRouteUrl(route)];
    }
    return null;
  }

  shouldReuseRoute(future: ActivatedRouteSnapshot, curr: ActivatedRouteSnapshot): boolean {
    return future.routeConfig === curr.routeConfig &&
    JSON.stringify(future.params) === JSON.stringify(curr.params);
  }

  private getRouteUrl(route: ActivatedRouteSnapshot) {
    // console.log(CustomRouteReuseStrategy.storedRoutes)
    console.log(route);
    return route.pathFromRoot.map(v => v.url.map(segment => segment.toString()).join('/')).join('/').slice(1);
  }

  public static clearCache(dashboardKey: string) {
    const ids = CustomRouteReuseStrategy.storedRoutes.findIndex(key => key.includes(dashboardKey));
    if (ids > -1) {
      delete CustomRouteReuseStrategy.handlers[CustomRouteReuseStrategy.storedRoutes[ids]];
      CustomRouteReuseStrategy.storedRoutes.splice(ids, 1);
    }
  }
}
