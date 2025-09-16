package org.thingsboard.server.controller.app;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.mes.sys.TSyncMaterial;
import org.thingsboard.server.common.data.mes.sys.TSysPdRecord;
import org.thingsboard.server.common.data.mes.ncInventory.NcInventory;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.controller.BaseController;
import org.thingsboard.server.dao.mes.dto.PdMaterialsDto;
import org.thingsboard.server.dao.mes.vo.PageVo;

import java.util.List;

@RestController
@RequestMapping("/api/app/pd")
@Api(value = "app盘点模块",tags = "app盘点模块")
public class YcPdAppController extends BaseController {

    @PostMapping("/pdMaterials")
    @ApiOperation("盘点物料列表")
    public ResponseResult<List<NcInventory>> pdMaterials(@RequestBody PdMaterialsDto pdMaterialsDto){
        List<NcInventory> ncInventories=ycPdService.pdMaterials(pdMaterialsDto);
        return ResultUtil.success(ncInventories);
    }

    @ApiOperation("自定义盘点物料")
    @GetMapping("/listMaterial")
    public ResponseResult<List<TSyncMaterial>> listMaterial(@RequestParam(value = "selectBy",defaultValue = "") String selectBy){
        List<TSyncMaterial> tSyncMaterials=ycPdService.listMaterial(selectBy);
        return ResultUtil.success(tSyncMaterials);
    }


    @ApiOperation("保存盘点记录")
    @PostMapping("/savePd")
    public ResponseResult savePd(@RequestBody TSysPdRecord tSysPdRecord){
        TSysPdRecord tSysPdRecord1=ycPdService.savePd(tSysPdRecord);
        return ResultUtil.success(tSysPdRecord1);
    }

    @ApiOperation("复盘记录选择")
    @GetMapping("/fpWorkshopRecord")
    public ResponseResult<List<TSysPdRecord>> fpWorkshopRecord(@RequestParam("startDate") String startDate,
                                           @RequestParam("endDate") String endDate){
        List<TSysPdRecord> tSysPdRecords=ycPdService.fpWorkshopRecord(startDate,endDate);
        return ResultUtil.success(tSysPdRecords);
    }

    @ApiOperation("复盘记录详情列表")
    @GetMapping("/showWorkshopRecord")
    public ResponseResult<List<TSysPdRecord>> showWorkshopRecord(@RequestParam("pdTimeStr") String pdTimeStr,
                                                                 @RequestParam("pdWorkshopNumber") String pdWorkshopNumber){
        List<TSysPdRecord> tSysPdRecords=ycPdService.showWorkshopRecord(pdTimeStr,pdWorkshopNumber);
        return ResultUtil.success(tSysPdRecords);
    }
}
