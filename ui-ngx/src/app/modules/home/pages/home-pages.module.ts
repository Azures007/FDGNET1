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

import { NgModule } from '@angular/core';

import { AdminModule } from './admin/admin.module';
import { HomeLinksModule } from './home-links/home-links.module';
import { ProfileModule } from './profile/profile.module';
import { TenantModule } from '@modules/home/pages/tenant/tenant.module';
import { CustomerModule } from '@modules/home/pages/customer/customer.module';
import { AuditLogModule } from '@modules/home/pages/audit-log/audit-log.module';
import { UserModule } from '@modules/home/pages/user/user.module';
import { DeviceModule } from '@modules/home/pages/device/device.module';

import { RoleModule } from '@modules/home/pages/role/role.module';
import { AccountModule } from '@modules/home/pages/account/account.module';
import { DictionaryModule } from '@modules/home/pages/dictionary/dictionary.module';
import { MyDeviceModule } from '@modules/home/pages/my-device/my-device.module';
import { StaffModule } from '@modules/home/pages/staff/staff.module';
import { MaterialModule } from '@modules/home/pages/material/material.module';
import { PickingModule } from '@modules/home/pages/picking/picking.module';

import { OrderManagementModule } from '@home/pages/order-management/order-management.module';
import { TechnologicalProcessModule } from '@home/pages/technological-process/technological-process.module';

import { AssetModule } from '@modules/home/pages/asset/asset.module';
import { EntityViewModule } from '@modules/home/pages/entity-view/entity-view.module';
import { RuleChainModule } from '@modules/home/pages/rulechain/rulechain.module';
import { WidgetLibraryModule } from '@modules/home/pages/widget/widget-library.module';
import { DashboardModule } from '@modules/home/pages/dashboard/dashboard.module';
import { TenantProfileModule } from './tenant-profile/tenant-profile.module';
import { MODULES_MAP } from '@shared/public-api';
import { modulesMap } from '../../common/modules-map';
import { DeviceProfileModule } from './device-profile/device-profile.module';
import { ApiUsageModule } from '@home/pages/api-usage/api-usage.module';
import { EdgeModule } from '@home/pages/edge/edge.module';
import { OtaUpdateModule } from '@home/pages/ota-update/ota-update.module';
import { ClassModule } from '@home/pages/class/class.module';
import { BasketModule } from '@home/pages/basket/basket.module';
import { SpecificationModule } from '@home/pages/specification/specification.module';
import { MenuModule } from '@home/pages/menu/menu.module';
import { QualityModule } from '@home/pages/quality/quality.module';
import { InventoryMgtModule } from '@home/pages/invetory-mgt/invetory-mgt.module';
import { PdMgtModule } from '@home/pages/pd-mgt/pd-mgt.module';
import { ContentModule } from '@home/pages/content/content.module';
import { RecipeModule } from '@home/pages/recipe/recipe.module';
import { ReportRecordModule } from '@home/pages/report-record/report-record.module';
import { BiModule } from '@home/pages/bi/bi.module';
import { StatisticsModule } from '@home/pages/statistics/statistics.module';
import { DeviceIotModule } from '@home/pages/device-iot/device-iot.module';



@NgModule({
  exports: [
    AdminModule,
    HomeLinksModule,
    ProfileModule,
    TenantProfileModule,
    TenantModule,
    DeviceProfileModule,
    DeviceModule,
    AssetModule,
    EdgeModule,
    EntityViewModule,
    CustomerModule,
    RuleChainModule,
    WidgetLibraryModule,
    DashboardModule,
    AuditLogModule,
    ApiUsageModule,
    OtaUpdateModule,
    UserModule,
    RoleModule,
    AccountModule,
    DictionaryModule,
    MyDeviceModule,
    StaffModule,
    ClassModule,
    OrderManagementModule,
    TechnologicalProcessModule,
    MaterialModule,
    BasketModule,
    PickingModule,
    SpecificationModule,
    MenuModule,
    QualityModule,
    InventoryMgtModule,
    PdMgtModule,
    ContentModule,
    RecipeModule,
    ReportRecordModule,
    BiModule,
    StatisticsModule,
    DeviceIotModule
  ],
  providers: [
    {
      provide: MODULES_MAP,
      useValue: modulesMap
    }
  ]
})
export class HomePagesModule { }
