package org.thingsboard.server.controller.quality;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.TSysClass;
import org.thingsboard.server.common.data.TSysCodeDsc;
import org.thingsboard.server.common.data.TSysQualityCategory;
import org.thingsboard.server.common.data.TSysQualityCategoryConfig;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.controller.BaseController;
import org.thingsboard.server.dao.ImportParam.TSysClassImportParam;
import org.thingsboard.server.dao.ImportParam.TSysQualityCategoryImportParam;
import org.thingsboard.server.dao.constant.GlobalConstant;
import org.thingsboard.server.dao.dto.TSysClassDto;
import org.thingsboard.server.dao.dto.TSysCodeDscDto;
import org.thingsboard.server.dao.dto.TSysQualityCategoryDto;
import org.thingsboard.server.dao.tSysQualityCategory.TSysQualityCategoryService;
import org.thingsboard.server.dao.vo.PageVo;
import org.thingsboard.server.dao.vo.TSysQualityCategoryVo;
import org.thingsboard.server.service.security.model.SecurityUser;

import java.util.Date;
import java.util.List;
/**
 * @author 陈懋燊
 * @project youchen_IOTServer
 * @description 质检分类
 * @date 2025/6/27 15:12:57
 */
@Api(value = "质检类目接口", tags = "质检类目接口")
@RequestMapping("/api/tSysQualityCategory")
@RestController
public class TSysQualityCategoryController extends BaseController {

    @Autowired
    private TSysQualityCategoryService tSysQualityCategoryService;

    @ApiOperation("查询质检类目列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", readOnly = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", readOnly = false)

    })
    @PostMapping("/qualityCategoryList")
    public ResponseResult<PageVo<TSysQualityCategory>> qualityCategoryList(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                                 @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                 @RequestBody TSysQualityCategoryDto tSysQualityCategoryDto) {
        Page<TSysQualityCategory> qualityCategoryList = tSysQualityCategoryService.tSysQualityCategoryList(current, size, tSysQualityCategoryDto);
        PageVo<TSysQualityCategory> pageVo = new PageVo<>(qualityCategoryList);
        return ResultUtil.success(pageVo);
    }




    @ApiOperation("保存/修改质检类目信息（id为空则表示新增，id不为空表示修改）")
    @PostMapping("/saveQualityCategory")
    public ResponseResult saveQualityCategory(@RequestBody TSysQualityCategoryImportParam tSysQualityCategoryImportParam) throws Exception {
        SecurityUser currentUser = getCurrentUser();
        TSysQualityCategory tSysQualityCategory = tSysQualityCategoryImportParam.gettSysQualityCategory();
        tSysQualityCategory.setUpdateUser(currentUser.getName());
        tSysQualityCategory.setUpdateTime(new Date());
        tSysQualityCategoryService.saveTSysQualityCategoryAndConfig(tSysQualityCategory, tSysQualityCategoryImportParam.gettSysQualityCategoryConfigList());
        return ResultUtil.success();
    }

    @ApiOperation("删除质检类目")
    @GetMapping("/deleteQualityCategory")
    public ResponseResult deleteQualityCategory(@RequestParam("categoryId") Integer categoryId) {
        tSysQualityCategoryService.deleteTSysQualityCategory(categoryId);
        return ResultUtil.success();
    }

    @ApiOperation("根据ID查询明细")
    @GetMapping("/getQualityCategoryById")
    public ResponseResult<TSysQualityCategoryVo> getQualityCategoryById(@RequestParam("categoryId") Integer categoryId) {

        TSysQualityCategoryVo vo = tSysQualityCategoryService.getQualityCategoryById(categoryId);

        if (vo == null) {
            return ResultUtil.error("未找到该数据");
        } else {
            return ResultUtil.success(vo);
        }

    }

    @ApiOperation("禁用控制接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryId", value = "班别ID", required = true),
            @ApiImplicitParam(name = "enabledSt", value = "禁用标识 0：禁用 1：启用", required = true)
    })
    @GetMapping("/isEnabled")
    public ResponseResult isEnabled(@RequestParam("categoryId") Integer categoryId, @RequestParam("enabledSt") Integer isEnabled) throws Exception {
        TSysQualityCategory tSysQualityCategory = tSysQualityCategoryService.getQualityCategoryById(categoryId);
        tSysQualityCategory.setIsEnabled(isEnabled == 1 ? GlobalConstant.enableTrue : GlobalConstant.enableFalse);
//        this.saveClass(tSysClass);
        SecurityUser currentUser = getCurrentUser();
        tSysQualityCategory.setUpdateUser(currentUser.getName());
        tSysQualityCategory.setUpdateTime(new Date());
        tSysQualityCategoryService.saveTSysQualityCategory(tSysQualityCategory);
        return ResultUtil.success();
    }







////    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
//    @RequestMapping(value = "/quality/category", method = RequestMethod.POST)
//    @ResponseStatus(value = HttpStatus.CREATED)
//    @ApiOperation(value = "创建或更新质检类目", notes = "创建或更新质检类目对象。需要TENANT_ADMIN或CUSTOMER_USER权限。")
//    public QualityCategory saveQualityCategory(@RequestBody QualityCategory category) throws Exception {
//        try {
//            TenantId tenantId = getCurrentUser().getTenantId();
//            return checkNotNull(qualityCategoryService.saveQualityCategory(tenantId, category));
//        } catch (Exception e) {
//            log.error("Failed to save quality category", e);
//            throw handleException(e);
//        }
//    }
//
////    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
//    @RequestMapping(value = "/quality/category/{categoryId}", method = RequestMethod.DELETE)
//    @ResponseStatus(value = HttpStatus.OK)
//    @ApiOperation(value = "删除质检类目", notes = "根据ID删除质检类目。需要TENANT_ADMIN或CUSTOMER_USER权限。")
//    public void deleteQualityCategory(@PathVariable("categoryId") String strCategoryId) throws Exception {
//        try {
//            UUID categoryId = toUUID(strCategoryId);
//            TenantId tenantId = getCurrentUser().getTenantId();
//            qualityCategoryService.deleteQualityCategory(tenantId, categoryId);
//        } catch (Exception e) {
//            log.error("Failed to delete quality category", e);
//            throw handleException(e);
//        }
//    }
//
////    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
//    @RequestMapping(value = "/quality/category/{categoryId}", method = RequestMethod.GET)
//    @ResponseStatus(value = HttpStatus.OK)
//    @ApiOperation(value = "获取质检类目", notes = "根据ID获取质检类目。需要TENANT_ADMIN或CUSTOMER_USER权限。")
//    public QualityCategory getQualityCategoryById(@PathVariable("categoryId") String strCategoryId) throws Exception {
//        try {
//            UUID categoryId = toUUID(strCategoryId);
//            TenantId tenantId = getCurrentUser().getTenantId();
//            return checkNotNull(qualityCategoryService.findQualityCategoryById(tenantId, categoryId).orElse(null));
//        } catch (Exception e) {
//            log.error("Failed to get quality category", e);
//            throw handleException(e);
//        }
//    }
//
////    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
//    @RequestMapping(value = "/quality/categories", params = {"pageSize", "page"}, method = RequestMethod.GET)
//    @ResponseStatus(value = HttpStatus.OK)
//    @ApiOperation(value = "分页获取质检类目", notes = "根据租户ID分页获取质检类目列表。需要TENANT_ADMIN或CUSTOMER_USER权限。")
//    public PageData<QualityCategory> getQualityCategories(
//            @RequestParam int pageSize,
//            @RequestParam int page,
//            @RequestParam(required = false) String textSearch,
//            @RequestParam(required = false) String sortProperty,
//            @RequestParam(required = false) String sortOrder) throws Exception {
//        try {
//            TenantId tenantId = getCurrentUser().getTenantId();
//            TextPageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
//            return checkNotNull(qualityCategoryService.findQualityCategoriesByTenantId(tenantId, pageLink));
//        } catch (Exception e) {
//            log.error("Failed to get quality categories", e);
//            throw handleException(e);
//        }
//    }
//
////    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
//    @RequestMapping(value = "/quality/category/types", method = RequestMethod.GET)
//    @ResponseStatus(value = HttpStatus.OK)
//    @ApiOperation(value = "获取质检类目类型", notes = "获取指定租户下的质检类目类型列表。需要TENANT_ADMIN或CUSTOMER_USER权限。")
//    public List<EntitySubtype> getQualityCategoryTypes() throws Exception {
//        try {
//            TenantId tenantId = getCurrentUser().getTenantId();
//            return checkNotNull(qualityCategoryService.findQualityCategoryTypesByTenantId(tenantId));
//        } catch (Exception e) {
//            log.error("Failed to get quality category types", e);
//            throw handleException(e);
//        }
//    }
}
