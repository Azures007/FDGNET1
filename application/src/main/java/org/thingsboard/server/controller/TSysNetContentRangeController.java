package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.mes.sys.TSysNetContentRange;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.mes.TSysNetContentRange.TSysNetContentRangeService;
import org.thingsboard.server.dao.mes.dto.TSysNetContentRangeDto;
import org.thingsboard.server.dao.mes.sync.MaterialService;
import org.thingsboard.server.dao.mes.vo.PageVo;
import org.thingsboard.server.dao.mes.vo.TSyncMaterialVo;
import org.thingsboard.server.dao.mes.vo.TSysNetContentRangeVo;
import org.thingsboard.server.service.security.model.SecurityUser;

/**
 * @author 许文言
 * @project youchen_IOTServer
 * @description 净含量范围管理
 * @date 2025/8/6 15:48:38
 */

@Api(value = "净含量范围管理接口", tags = "净含量范围管理接口")
@RequestMapping("/api/tSysNetContentRange")
@RestController
public class TSysNetContentRangeController extends BaseController {

    @Autowired
    private MaterialService materialService;

    @Autowired
    private TSysNetContentRangeService tSysNetContentRangeService;


    /**
     * 物料基础信息列表
     *
     * @param current
     * @param size
     * @param tSysNetContentRangeDto
     * @return
     */
    @ApiOperation("物料基础信息列表（新增时）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", required = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", required = false)

    })
    @PostMapping("/listMaterial")
    public ResponseResult<PageVo<TSyncMaterialVo>> listMaterial(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                                @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                @RequestBody TSysNetContentRangeDto tSysNetContentRangeDto) {
        PageVo<TSyncMaterialVo> pageVo = materialService.listNetMaterial(current, size, tSysNetContentRangeDto);
        return ResultUtil.success(pageVo);
    }

    /**
     * 查询净含量范围列表
     *
     * @param current
     * @param size
     * @param sortField
     * @param sortOrder
     * @param tSysNetContentRangeDto
     * @return
     */
    @ApiOperation("查询净含量范围列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", readOnly = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", readOnly = false),
            @ApiImplicitParam(name = "sortField", value = "排序字段", readOnly = false),
            @ApiImplicitParam(name = "sortOrder", value = "排序方式（asc/desc）", readOnly = false)

    })
    @PostMapping("/netContentRangeList")
    public ResponseResult<PageVo<TSysNetContentRangeVo>> qualityPlanList(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                                         @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                         @RequestParam(value = "sortField", defaultValue = "") String sortField,
                                                                         @RequestParam(value = "sortOrder", defaultValue = "") String sortOrder,
                                                                         @RequestBody TSysNetContentRangeDto tSysNetContentRangeDto) throws ThingsboardException {
        SecurityUser currentUser = getCurrentUser();
        Page<TSysNetContentRangeVo> netContentRangeList = tSysNetContentRangeService.tSysNetContentRangeList(currentUser.getId().toString(),current, size,sortField,sortOrder, tSysNetContentRangeDto);
        PageVo<TSysNetContentRangeVo> pageVo = new PageVo<>(netContentRangeList);
        return ResultUtil.success(pageVo);

    }

    @ApiOperation("保存净含量范围（新增或编辑）")
    @PostMapping("/saveNetContentRange")
    public ResponseResult<TSysNetContentRange> saveNetContentRange(@RequestBody TSysNetContentRange tSysNetContentRange) throws ThingsboardException {
        try {
            SecurityUser currentUser = getCurrentUser();
            // 设置创建人
            if (tSysNetContentRange.getId() == null) {
                tSysNetContentRange.setCreateUser(currentUser.getName());
            }
            TSysNetContentRange saved = tSysNetContentRangeService.saveNetContentRange(tSysNetContentRange);
            return ResultUtil.success(saved);
        } catch (RuntimeException e) {
            return ResultUtil.error(e.getMessage());
        }
    }


    /**
     * 根据ID删除净含量范围记录
     *
     * @param id
     * @return
     */
    @ApiOperation("根据ID删除净含量范围记录")
    @DeleteMapping("/deleteNetContentRange")
    public ResponseResult deleteNetContentRangeById(@RequestParam Integer id) throws ThingsboardException {
        tSysNetContentRangeService.deleteNetContentRangeById(id);
        return ResultUtil.success();
    }

    /**
     * 根据ID查询净含量范围明细
     *
     * @param id
     * @return
     */
    @ApiOperation("根据ID查询净含量范围明细")
    @GetMapping("/getNetContentRangeById")
    public ResponseResult<TSysNetContentRange> getNetContentRangeById(@RequestParam Integer id) throws ThingsboardException {
        TSysNetContentRange netContentRange = tSysNetContentRangeService.getNetContentRangeById(id);
        return ResultUtil.success(netContentRange);
    }

    /**
     * 启用/禁用净含量范围记录
     *
     * @param id
     * @param status 状态值（"启用" 或 "禁用"）
     * @return
     */
    @ApiOperation("启用/禁用净含量范围记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "净含量范围ID", required = true),
            @ApiImplicitParam(name = "status", value = "状态值（启用/禁用）", required = true)
    })
    @GetMapping("/updateStatus")
    public ResponseResult<TSysNetContentRange> updateStatus(@RequestParam("id") Integer id, @RequestParam("status") String status) throws ThingsboardException {
        TSysNetContentRange updated = tSysNetContentRangeService.updateNetContentRangeStatus(id, status);
        return ResultUtil.success(updated);
    }
}