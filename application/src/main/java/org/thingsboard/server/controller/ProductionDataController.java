package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.mes.vo.ProductionData;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.mes.dto.ProductionDataQueryDto;
import org.thingsboard.server.dao.mes.production.ProductionDataService;
import org.thingsboard.server.dao.mes.vo.PageVo;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 投入产出比报表控制器
 */
@Slf4j
@Api(tags = "投入产出比报表")
@RestController
@RequestMapping("/api/inputoutputratio")
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

    @ApiOperation("导出投入产出比报表")
    @PostMapping("/export")
    public void export(@RequestBody ProductionDataQueryDto queryDto, HttpServletResponse response) {
        try {
            String fileName = "投入产出比报表_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            productionDataService.exportProductionData(queryDto, response);
        } catch (Exception e) {
            try {
                response.reset();
                response.setContentType("application/json;charset=utf-8");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
                response.getWriter().flush();
            } catch (Exception ex) {
                log.error("导出报表时发生异常", ex);
            }
        }
    }
}