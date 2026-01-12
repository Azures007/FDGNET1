package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.common.data.mes.vo.ProductionData;
import org.thingsboard.server.dao.mes.dto.ProductionDataQueryDto;
import org.thingsboard.server.dao.mes.production.ProductionDataService;
import org.thingsboard.server.dao.mes.vo.PageVo;

import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.common.data.web.ResponseResult;

/**
 * 投入产出比报表控制器
 */
@Api(tags = "投入产出比报表")
@RestController
@RequestMapping("/api/production-data")
public class ProductionDataController {

    @Autowired
    private ProductionDataService productionDataService;



    @ApiOperation("查询投入产出比报表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", readOnly = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", readOnly = false)
    })
    @PostMapping("/query")
    public ResponseResult<PageVo<ProductionData>> query(
            @RequestParam(value = "current", defaultValue = "0") Integer current,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestBody ProductionDataQueryDto queryDto) {
        return ResultUtil.success(productionDataService.queryProductionData(current, size, queryDto));
    }
}