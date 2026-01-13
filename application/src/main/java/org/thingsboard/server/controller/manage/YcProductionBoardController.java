package org.thingsboard.server.controller.manage;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.mes.ncWorkline.NcWorkline;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.controller.BaseController;
import org.thingsboard.server.dao.mes.ncWorkline.NcWorklineService;
import org.thingsboard.server.dao.mes.productionBoard.ProductionBoardService;
import org.thingsboard.server.dao.mes.vo.*;

import java.util.List;

/**
 * 生产看板管理接口
 */
@RestController
@RequestMapping("/api/noauth/productionBoard")
@Api(tags = "生产看板管理接口")
public class YcProductionBoardController extends BaseController {

    @Autowired
    private ProductionBoardService productionBoardService;
    
    @Autowired
    private NcWorklineService ncWorklineService;
    
    @Autowired
    private org.thingsboard.server.dao.sql.mes.tSysCodeDsc.TSysCodeDscRepository tSysCodeDscRepository;

    @ApiOperation("获取生产统计数据")
    @GetMapping("/statistics")
    public ResponseResult<ProductionStatistics> getProductionStatistics(
            @ApiParam("生产线cwkid") @RequestParam(required = false) String productionLine,
            @ApiParam("时间维度(今日/昨日/本周/本月等)") @RequestParam(required = false) String timeDimension,
            @ApiParam("开始日期(yyyy-MM-dd)") @RequestParam(required = false) String startDate,
            @ApiParam("结束日期(yyyy-MM-dd)") @RequestParam(required = false) String endDate) throws ThingsboardException {
        
        // 验证：时间维度和日期范围只能传其一
        boolean hasTimeDimension = timeDimension != null && !timeDimension.trim().isEmpty();
        boolean hasDateRange = (startDate != null && !startDate.trim().isEmpty());
        
        /*if (hasTimeDimension && hasDateRange) {
            return ResultUtil.error("时间维度和日期范围只能传其一，不能同时传递");
        }
        
        if (!hasTimeDimension && !hasDateRange) {
            return ResultUtil.error("时间维度或日期范围必须传递其中一个");
        }*/
        
        // 如果传了日期范围，验证必须同时传开始和结束日期
        if (hasDateRange) {
            if (endDate == null || endDate.trim().isEmpty()) {
                endDate = startDate;
            }
        }
        
        return ResultUtil.success(productionBoardService.getProductionStatistics(
            productionLine, timeDimension, startDate, endDate));
    }

    @ApiOperation("获取订单计划达成率分析")
    @GetMapping("/orderPlanAnalysis")
    public ResponseResult<List<OrderPlanAnalysis>> getOrderPlanAnalysis(
            @ApiParam("生产线cwkid") @RequestParam(required = false) String productionLine,
            @ApiParam("日期类型(日/月)") @RequestParam(required = true) String timeDimension,
            @ApiParam("开始日期(yyyy-MM-dd)") @RequestParam(required = false) String startDate,
            @ApiParam("结束日期(yyyy-MM-dd)") @RequestParam(required = false) String endDate) throws ThingsboardException {
        if (timeDimension == null || timeDimension.trim().isEmpty()) {
            return ResultUtil.error("日期类型不能为空");
        }
        return ResultUtil.success(productionBoardService.getOrderPlanAnalysis(productionLine, timeDimension, startDate, endDate));
    }

    @ApiOperation("获取订单废料产出分析")
    @GetMapping("/wasteOutputAnalysis")
    public ResponseResult<List<WasteOutputAnalysis>> getWasteOutputAnalysis(
            @ApiParam("生产线cwkid") @RequestParam(required = false) String productionLine,
            @ApiParam("日期类型(日/周/月)") @RequestParam(required = true) String timeDimension,
            @ApiParam("开始日期(yyyy-MM-dd)") @RequestParam(required = false) String startDate,
            @ApiParam("结束日期(yyyy-MM-dd)") @RequestParam(required = false) String endDate) throws ThingsboardException {
        return ResultUtil.success(productionBoardService.getWasteOutputAnalysis(productionLine, timeDimension, startDate, endDate));
    }

    @ApiOperation("获取订单进度完成情况")
    @GetMapping("/orderProgress")
    public ResponseResult<OrderProgressPageVo> getOrderProgress(
            @ApiParam("生产线cwkid") @RequestParam(required = false) String productionLine,
            @ApiParam("当前页码") @RequestParam(value = "current", defaultValue = "0") Integer current,
            @ApiParam("每页数量") @RequestParam(value = "size", defaultValue = "10") Integer size,
            @ApiParam("开始日期(yyyy-MM-dd)") @RequestParam(required = false) String startDate,
            @ApiParam("结束日期(yyyy-MM-dd)") @RequestParam(required = false) String endDate) throws ThingsboardException {
        return ResultUtil.success(productionBoardService.getOrderProgress(productionLine, current, size, startDate, endDate));
    }

    @ApiOperation("获取生产态势监控")
    @GetMapping("/productionBG")
    public ResponseResult<PageVo<ProductionBgVo>> getProductionBG(
            @ApiParam("生产线cwkid") @RequestParam(required = false) String productionLine,
            @ApiParam("当前页码") @RequestParam(value = "current", defaultValue = "0") Integer current,
            @ApiParam("每页数量") @RequestParam(value = "size", defaultValue = "10") Integer size,
            @ApiParam("开始日期(yyyy-MM-dd)") @RequestParam(required = false) String startDate,
            @ApiParam("结束日期(yyyy-MM-dd)") @RequestParam(required = false) String endDate) throws ThingsboardException {
        return ResultUtil.success(productionBoardService.getProductionBG(productionLine, current, size, startDate, endDate));
    }

    @ApiOperation("获取外包净含量实况")
    @GetMapping("/outsourcingNetContent")
    public ResponseResult<PageVo<OutsourcingNetContent>> getOutsourcingNetContent(
            @ApiParam("生产线cwkid") @RequestParam(required = false) String productionLine,
            @ApiParam("当前页码") @RequestParam(value = "current", defaultValue = "0") Integer current,
            @ApiParam("每页数量") @RequestParam(value = "size", defaultValue = "10") Integer size,
            @ApiParam("开始日期(yyyy-MM-dd)") @RequestParam(required = false) String startDate,
            @ApiParam("结束日期(yyyy-MM-dd)") @RequestParam(required = false) String endDate) throws ThingsboardException {
        return ResultUtil.success(productionBoardService.getOutsourcingNetContent(productionLine, current, size, startDate, endDate));
    }

    @ApiOperation("查询所有生效的生产线")
    @GetMapping("/worklines")
    public ResponseResult<List<NcWorkline>> getWorklines(
            @ApiParam("基地ID（可选）") @RequestParam(required = false) String pkOrg) throws ThingsboardException {
        // 从字典获取产线ID列表
        List<org.thingsboard.server.common.data.mes.sys.TSysCodeDsc> tSysCodeDscList = 
            tSysCodeDscRepository.findByCodeClIdAndEnabledSt("production_board_line", org.thingsboard.server.dao.constant.GlobalConstant.enableTrue);
        
        List<String> allowedWorklineIds = tSysCodeDscList.stream()
            .map(org.thingsboard.server.common.data.mes.sys.TSysCodeDsc::getCodeValue)
            .collect(java.util.stream.Collectors.toList());
        
        // 如果字典中没有配置，返回空列表
        if (allowedWorklineIds.isEmpty()) {
            return ResultUtil.success(new java.util.ArrayList<>());
        }
        
        List<NcWorkline> allWorklines;
        if (pkOrg != null && !pkOrg.trim().isEmpty()) {
            // 如果传了基地ID，查询该基地下的生产线
            allWorklines = ncWorklineService.findByPkOrg(pkOrg);
        } else {
            // 否则查询所有生效的生产线
            allWorklines = ncWorklineService.findByStatus("生效");
        }
        
        // 过滤只返回指定的产线
        List<NcWorkline> filteredWorklines = allWorklines.stream()
            .filter(workline -> allowedWorklineIds.contains(workline.getCwkid()))
            .collect(java.util.stream.Collectors.toList());
        
        return ResultUtil.success(filteredWorklines);
    }

    /*@ApiOperation("获取完整的生产看板数据（一次性获取所有数据）")
    @GetMapping("/all")
    public ResponseResult getAllProductionBoardData(
            @ApiParam("生产线") @RequestParam(required = false) String productionLine,
            @ApiParam("日期") @RequestParam(required = false) String date,
            @ApiParam("日期类型(日/月/年)") @RequestParam(required = false, defaultValue = "月") String dateType) throws ThingsboardException {
        return ResultUtil.success(productionBoardService.getAllProductionBoardData(productionLine, date, dateType));
    }*/
}
