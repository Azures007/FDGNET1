import { ActivatedRouteSnapshot, DetachedRouteHandle, RouteReuseStrategy } from '@angular/router';

export class CustomRouteReuseStrategy implements RouteReuseStrategy {
  public static handlers: { [key: string]: DetachedRouteHandle } = {};

  private maxCacheCount = 100;
  public static storedRoutes: string[] = [];
  public static waitDelete: string | null;

  shouldDetach(route: ActivatedRouteSnapshot): boolean {
    // Enable caching for routes with a component by default.
    // Opt-out by setting data.reuse === false on the route definition.
    const hasComponent = !!route.routeConfig && !!route.routeConfig.component;
    const reuseAllowed = route.data ? route.data.reuse !== false : true;
    const result = hasComponent && reuseAllowed;
    return result;
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
          delete CustomRouteReuseStrategy.handlers[oldestUrl];
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
    const url = this.getRouteUrl(route);
    const result = !!path && !!CustomRouteReuseStrategy.handlers[url];
    return result;
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
    // 构建包含路径和参数的完整URL作为缓存键
    // 这样每个不同的参数组合都有独立的缓存
    const pathSegments = route.pathFromRoot
      .map(r => r.url.map(segment => segment.toString()).join('/'))
      .join('/');

    // 移除开头的斜杠
    return pathSegments.startsWith('/') ? pathSegments.slice(1) : pathSegments;
  }

  public static clearCache(dashboardKey: string) {
    const ids = CustomRouteReuseStrategy.storedRoutes.findIndex(key => key.includes(dashboardKey));
    if (ids > -1) {
      delete CustomRouteReuseStrategy.handlers[CustomRouteReuseStrategy.storedRoutes[ids]];
      CustomRouteReuseStrategy.storedRoutes.splice(ids, 1);
    }
  }
}
