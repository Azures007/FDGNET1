package org.thingsboard.server.controller.app;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.mes.sys.TSyncMaterial;
import org.thingsboard.server.common.data.mes.sys.TSysPdRecord;
import org.thingsboard.server.common.data.mes.sys.TSysUserDetail;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.controller.BaseController;
import org.thingsboard.server.dao.mes.dto.PdMaterialsDto;
import org.thingsboard.server.dao.mes.vo.PageVo;
import org.thingsboard.server.dao.mes.vo.PdMaterialsVo;
import org.thingsboard.server.service.security.model.SecurityUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/app/pd")
@Api(value = "app盘点模块",tags = "app盘点模块")
public class YcPdAppController extends BaseController {

    @PostMapping("/pdMaterials")
    @ApiOperation("盘点物料列表")
    public ResponseResult<PdMaterialsVo> pdMaterials(@RequestBody PdMaterialsDto pdMaterialsDto) throws ThingsboardException {
        SecurityUser currentUser = getCurrentUser();
        PdMaterialsVo result = ycPdService.pdMaterials(pdMaterialsDto, currentUser.getId().getId().toString());
        return ResultUtil.success(result);
    }

    @ApiOperation("自定义盘点物料")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "selectBy", value = "搜索条件", required = false),
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", required = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", required = false)
    })
    @GetMapping("/listMaterial")
    public ResponseResult<PageVo<TSyncMaterial>> listMaterial(
            @RequestParam(value = "selectBy", defaultValue = "") String selectBy,
            @RequestParam(value = "current", defaultValue = "0") Integer current,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        PageVo<TSyncMaterial> pageData = ycPdService.listMaterial(selectBy, current, size);
        return ResultUtil.success(pageData);
    }


    @ApiOperation("保存盘点记录")
    @PostMapping("/savePd")
    public ResponseResult savePd(@RequestBody TSysPdRecord tSysPdRecord)throws ThingsboardException{
        SecurityUser currentUser = getCurrentUser();
        TSysPdRecord tSysPdRecord1=ycPdService.savePd(tSysPdRecord,currentUser.getId().getId().toString());
        return ResultUtil.success(tSysPdRecord1);
    }

    @ApiOperation("复盘记录选择")
    @GetMapping("/fpWorkshopRecord")
    public ResponseResult<List<TSysPdRecord>> fpWorkshopRecord(@RequestParam("startDate") String startDate,
                                           @RequestParam("endDate") String endDate) throws ThingsboardException {
        SecurityUser currentUser = getCurrentUser();
        List<TSysPdRecord> tSysPdRecords=ycPdService.fpWorkshopRecord(startDate,endDate,currentUser.getId().getId().toString());
        return ResultUtil.success(tSysPdRecords);
    }

    @ApiOperation("复盘记录详情列表")
    @GetMapping("/showWorkshopRecord")
    public ResponseResult<List<TSysPdRecord>> showWorkshopRecord(@RequestParam("pdTimeStr") String pdTimeStr,
                                                                 @RequestParam("pdWorkshopNumber") String pdWorkshopNumber,
                                                                 @RequestParam("ncVwkname") String ncVwkname){
        List<TSysPdRecord> tSysPdRecords=ycPdService.showWorkshopRecord(pdTimeStr,pdWorkshopNumber,ncVwkname);
        return ResultUtil.success(tSysPdRecords);
    }
    
    @PostMapping("/finishPdByMaterialType")
    @ApiOperation("结束指定物料分类的盘点")
    public ResponseResult<Boolean> finishPdByMaterialType(@RequestBody PdMaterialsDto pdMaterialsDto) throws ThingsboardException {
        try {
            SecurityUser currentUser = getCurrentUser();
            
            String pdTimeStr = pdMaterialsDto.getPdTimeStr();
            if (pdTimeStr == null || pdTimeStr.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                pdTimeStr = sdf.format(new Date());
            }
            
            // 直接结束盘点，处理所有未盘点物料
            boolean result = ycPdService.finishPdByMaterialType(
                pdMaterialsDto.getMaterialTypePd(),
                currentUser.getId().getId().toString(),
                pdTimeStr,
                    pdMaterialsDto.getVwkname(),
                    pdMaterialsDto.getWarehouseCode()
            );
            
            return ResultUtil.success(result);
        } catch (Exception e) {
            // 异常信息将作为提示信息返回给前端
            return ResultUtil.error(e.getMessage());
        }
    }

    @GetMapping("/materialTypes")
    @ApiOperation("获取所有物料分类名称")
    public ResponseResult<List<String>> getMaterialTypes() throws ThingsboardException {
        SecurityUser currentUser = getCurrentUser();
        // 通过用户详细信息获取仓库名称
        String ncCwkid = currentUser.getCwkid(); // 临时使用cwkid作为仓库名
        String ncPkOrg =  currentUser.getPkOrg();
        
        // 根据用户信息查询仓库ID
        List<TSysUserDetail> userDetails = tSysUserDetailRepository.findByUserId(currentUser.getId().getId().toString());
        String warehouseId = null;
        if (userDetails != null && !userDetails.isEmpty()) {
            for (TSysUserDetail detail : userDetails) {
                if (ncPkOrg.equals(detail.getNcPkOrg()) && ncCwkid.equals(detail.getNcCwkid())) {
                    warehouseId = detail.getNcWarehouseId();
                    break;
                }
            }
            if (warehouseId == null) {
                for (TSysUserDetail detail : userDetails) {
                    if (ncPkOrg.equals(detail.getNcPkOrg())) {
                        warehouseId = detail.getNcWarehouseId();
                        break;
                    }
                }
            }
        }
        
        List<String> materialTypes = ncInventoryRepository.findDistinctMaterialTypePdByWarehouseName(warehouseId);
        return ResultUtil.success(materialTypes);
    }


}