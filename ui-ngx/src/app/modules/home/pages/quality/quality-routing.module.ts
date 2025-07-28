
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { Authority } from '@shared/models/authority.enum';
import { QualityCateComponent } from './quality-cate.component';
import { QualityPlanComponent } from './quality-plan.component';
import { QualityCheckCateComponent } from './quality-check-cate.component';
import { QualityCheckPlanComponent } from './quality-check-plan.component';


const routes: Routes = [
  {
    path: 'quality',
    data: {
      breadcrumb: {
        label: '质量管理',
        icon: 'bookmarks'
      }
    },
    children: [
      {
        path: '',
        data: {
          auth: [Authority.TENANT_ADMIN],
          redirectTo: '/quality/cate'
        }
      },
      {
        path: 'cate',
        component: QualityCateComponent,
        data: {
          title: '品质类目管理',
          breadcrumb: {
            label: '品质类目管理',
            icon: 'bookmarks'
          }
        }
      },
      {
        path: 'plan',
        component: QualityPlanComponent,
        data: {
          title: '品质方案管理',
          breadcrumb: {
            label: '品质方案管理',
            icon: 'bookmarks'
          }
        }
      },
      {
        path: 'check-cate',
        component: QualityCheckCateComponent,
        data: {
          title: '日报检查项维护',
          breadcrumb: {
            label: '日报检查项维护',
            icon: 'bookmarks'
          }
        }
      },
      {
        path: 'check-plan',
        component: QualityCheckPlanComponent,
        data: {
          title: '日报方案管理',
          breadcrumb: {
            label: '日报方案管理',
            icon: 'bookmarks'
          }
        }
      },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class QualityRoutingModule { }
