package org.thingsboard.server.controller.quality;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.controller.BaseController;

/**
 * @author 陈懋燊
 * @project youchen_IOTServer
 * @description 质检分类配置
 * @date 2025/6/27 15:06:25
 */

@Api(value = "质检类目配置接口", tags = "质检类目配置接口")
@RequestMapping("/api/tSysQualityCategoryConfig")
@RestController
public class TSysQualityCategoryConfigController extends BaseController {

//    @Autowired
//    private QualityCategoryConfigService qualityCategoryConfigService;
//
//    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
//    @RequestMapping(value = "/quality/category/config", method = RequestMethod.POST)
//    @ResponseStatus(value = HttpStatus.CREATED)
//    @ApiOperation(value = "创建或更新质检类目配置", notes = "创建或更新质检类目配置对象。需要TENANT_ADMIN或CUSTOMER_USER权限。")
//    public QualityCategoryConfig saveQualityCategoryConfig(@RequestBody QualityCategoryConfig config) throws Exception {
//        try {
//            TenantId tenantId = getCurrentUser().getTenantId();
//            return checkNotNull(qualityCategoryConfigService.saveQualityCategoryConfig(tenantId, config));
//        } catch (Exception e) {
//            log.error("Failed to save quality category config", e);
//            throw handleException(e);
//        }
//    }
//
//    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
//    @RequestMapping(value = "/quality/category/config/{configId}", method = RequestMethod.DELETE)
//    @ResponseStatus(value = HttpStatus.OK)
//    @ApiOperation(value = "删除质检类目配置", notes = "根据ID删除质检类目配置。需要TENANT_ADMIN或CUSTOMER_USER权限。")
//    public void deleteQualityCategoryConfig(@PathVariable("configId") String strConfigId) throws Exception {
//        try {
//            UUID configId = toUUID(strConfigId);
//            TenantId tenantId = getCurrentUser().getTenantId();
//            qualityCategoryConfigService.deleteQualityCategoryConfig(tenantId, configId);
//        } catch (Exception e) {
//            log.error("Failed to delete quality category config", e);
//            throw handleException(e);
//        }
//    }
//
//    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
//    @RequestMapping(value = "/quality/category/config/{configId}", method = RequestMethod.GET)
//    @ResponseStatus(value = HttpStatus.OK)
//    @ApiOperation(value = "获取质检类目配置", notes = "根据ID获取质检类目配置。需要TENANT_ADMIN或CUSTOMER_USER权限。")
//    public QualityCategoryConfig getQualityCategoryConfigById(@PathVariable("configId") String strConfigId) throws Exception {
//        try {
//            UUID configId = toUUID(strConfigId);
//            TenantId tenantId = getCurrentUser().getTenantId();
//            return checkNotNull(qualityCategoryConfigService.findQualityCategoryConfigById(tenantId, configId).orElse(null));
//        } catch (Exception e) {
//            log.error("Failed to get quality category config", e);
//            throw handleException(e);
//        }
//    }
//
//    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
//    @RequestMapping(value = "/quality/category/{categoryId}/configs", params = {"pageSize", "page"}, method = RequestMethod.GET)
//    @ResponseStatus(value = HttpStatus.OK)
//    @ApiOperation(value = "分页获取质检类目配置", notes = "根据租户ID和类目ID分页获取质检类目配置列表。需要TENANT_ADMIN或CUSTOMER_USER权限。")
//    public PageData<QualityCategoryConfig> getQualityCategoryConfigs(
//            @PathVariable("categoryId") String strCategoryId,
//            @RequestParam int pageSize,
//            @RequestParam int page,
//            @RequestParam(required = false) String textSearch,
//            @RequestParam(required = false) String sortProperty,
//            @RequestParam(required = false) String sortOrder) throws Exception {
//        try {
//            TenantId tenantId = getCurrentUser().getTenantId();
//            UUID categoryId = toUUID(strCategoryId);
//            TextPageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
//            return checkNotNull(qualityCategoryConfigService.findQualityCategoryConfigsByTenantIdAndCategoryId(tenantId, categoryId, pageLink));
//        } catch (Exception e) {
//            log.error("Failed to get quality category configs", e);
//            throw handleException(e);
//        }
//    }

}
