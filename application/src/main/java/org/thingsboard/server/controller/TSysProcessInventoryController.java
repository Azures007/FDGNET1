package org.thingsboard.server.controller;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.constant.GlobalConstant;
import org.thingsboard.server.dao.dto.OrderProcessRecordSaveDto;
import org.thingsboard.server.dao.dto.OrderProcessRecordSearchDto;
import org.thingsboard.server.dao.order.OrderInventoryService;
import org.thingsboard.server.dao.order.OrderProcessService;
import org.thingsboard.server.dao.sql.tSysClass.TSysClassRepository;
import org.thingsboard.server.dao.vo.*;
import org.thingsboard.server.service.security.model.SecurityUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Auther: l
 * @Date: 2022/4/25 17:10
 * @Description:
 */
@Api(value = "工序盘点管理", tags = "工序盘点管理")
@RequestMapping("/api/process/inventory")
@RestController
public class TSysProcessInventoryController extends BaseController {
    @Autowired
    OrderInventoryService orderInventoryService;
    @Autowired
    OrderProcessService orderProcessService;
    @Autowired
    TSysClassRepository classRepository;

    private Object lock = new Object();

    @GetMapping("/deletePDHistory")
    @ApiOperation("删除盘点记录")
    public ResponseResult deletePDHistory(@RequestParam("orderProcessHistoryId") Integer orderProcessHistoryId) {
        synchronized (lock) {
            orderInventoryService.deletePDHistory(orderProcessHistoryId);
        }
        return ResultUtil.success();
    }

    @ApiOperation("批次选择")
    @GetMapping("/getBatchList")
    public ResponseResult<List<BatchVo>> getBatchList(@RequestParam(value = "orderNo") String orderNo) {
        List<BatchVo> results = orderInventoryService.getBatchList(orderNo);
        return ResultUtil.success(results);
    }

    @ApiOperation("订单用料明细")
    @GetMapping("/getOrderPpbom")
    public ResponseResult<OrderProcessResult> getOrderPpbom2(@ApiParam("订单id") @RequestParam(value = "orderId") Integer orderId,
                                                             @ApiParam("工序id") @RequestParam(value = "processId") Integer processId) throws Exception {
        OrderProcessResult result = orderInventoryService.getOrderPpbom(orderId, processId);
        return ResultUtil.success(result);
    }

    @ApiOperation("盘点类型字典")
    @GetMapping("/getOrderRecordTypePdList")
    public ResponseResult<List<TSysCodeDsc>> getOrderRecordTypePdList(@ApiParam("订单工序执行表id") @RequestParam(value = "orderProcessId") Integer orderProcessId) {
        List<TSysCodeDsc> result = orderInventoryService.getOrderRecordTypePdList(orderProcessId);
        return ResultUtil.success(result);
    }

    @ApiOperation("盘点记录提交")
    @PostMapping("/submit")
    public ResponseResult submit(@ApiParam("盘点状态：0->首次盘点，-1->重新盘点") @RequestParam(value = "isFist", defaultValue = "0") Integer isFirst,
                                 @ApiParam("盘点次数") @RequestParam(value = "size", defaultValue = "0") Integer size,
                                 @ApiParam("是否允许重复盘点：0->否，-1->是") @RequestParam(value = "isRepeat", defaultValue = "0", required = false) Integer isRepeat,
                                 @RequestBody OrderProcessRecordSaveDto saveDto) throws Exception {
        SecurityUser securityUser = getCurrentUser();
        return orderInventoryService.submit(isFirst, size, saveDto, securityUser.getId().getId().toString(), isRepeat);
    }

    @ApiOperation("盘点次数")
    @PostMapping("/getSize")
    public ResponseResult<Integer> getSize(@RequestBody OrderProcessRecordSearchDto searchDto) throws Exception {
        SecurityUser securityUser = getCurrentUser();
        Integer result = orderInventoryService.getSize(searchDto, securityUser.getId().getId().toString());
        return ResultUtil.success(result);
    }

    @ApiOperation("盘点记录")
    @PostMapping("/getOrderProcessHistory")
    public ResponseResult<OrderProcessHistory> getOrderProcessHistory(@RequestBody OrderProcessRecordSearchDto searchDto) throws Exception {
        SecurityUser securityUser = getCurrentUser();
        OrderProcessHistory result = orderInventoryService.getOrderProcessHistory(searchDto, securityUser.getId().getId().toString());
        return ResultUtil.success(result);
    }

    @ApiOperation("移交详情")
    @PostMapping("/handOverDetail")
    public ResponseResult<OrderHandoverVo> handOverDetail(@RequestBody OrderProcessRecordSearchDto searchDto) throws Exception {
        SecurityUser securityUser = getCurrentUser();
        ResponseResult<OrderHandoverVo> result = orderInventoryService.handOver(searchDto, securityUser.getId().getId().toString());
        if (result == null) {
            return ResultUtil.error("订单移交详情不存在");
        }
        return result;
    }

    @ApiOperation("订单列表")
    @PostMapping("/handOverOrderList")
    public ResponseResult<List<TaskListHandOverVo>> handOverOrderList(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                                      @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                      @RequestParam(value = "sort", defaultValue = "desc") String sort) throws Exception {
        if ("desc".equals(sort) || "asc".equals(sort)) {
            SecurityUser currentUser = getCurrentUser();
            List<String> processStatusList = new ArrayList<>();
            processStatusList.add("1");
            processStatusList.add("2");
            PageVo<TaskListHandOverVo> taskListVoPageVo = orderHeadService.getTaskHandOverListByPersonIdAndProcessStatus(current, size, processStatusList, currentUser.getId().getId().toString(), sort);
            return ResultUtil.success(taskListVoPageVo);
        } else {
            return ResultUtil.error("排序参数值错误！");
        }
    }

    @ApiOperation("移交")
    @GetMapping("/handOver")
    public ResponseResult handOver(@RequestParam(value = "orderId") Integer orderId,
                                   @RequestParam(value = "orderProcessId") Integer orderProcessId,
                                   @RequestParam(value = "classId") Integer classId,
                                   @RequestParam(value = "userId") String userId) throws Exception {
        SecurityUser currentUser = getCurrentUser();
        TBusOrderHead tBusOrderHead = orderHeadService.getTBusOrderHead(orderId);
        if (LichengConstants.ORDERSTATUS_4.equals(tBusOrderHead.getOrderStatus())) {
            throw new RuntimeException("订单已挂起，禁止操作。");
        }
        //根据用户ID获取当前用户绑定的的人员信息
        TSysPersonnelInfo tSysPersonnelInfo = tSysPersonnelInfoService.getPersonnelInfoByUserId(userId);
        if (tSysPersonnelInfo == null) {
            return ResultUtil.error("获取当前用户绑定的人员信息失败，请确认是否已成功绑定");
        }
        //获取移交的工序
        TBusOrderProcess handOverOrderProcess = orderProcessService.findById(orderProcessId);
        if (handOverOrderProcess == null) {
            return ResultUtil.error("未查询到移交工序信息，请联系管理员");
        }
        // 获取移交目标的班组信息（根据）
        TSysClass tSysClass = tSysClassService.getClassById(classId);
        if (tSysClass == null) {
            return ResultUtil.error("未查询到移交的班组信息，请联系管理员");
        }
        orderProcessService.handOver(tSysPersonnelInfo, tSysClass, handOverOrderProcess, orderId, String.valueOf(currentUser.getId().getId()));
        return ResultUtil.success();
    }

    @ApiOperation("获取移交的班组列表")
    @GetMapping("/getHandOverClassList")
    public ResponseResult<List<HandOverClassVo>> getHandOverClassList(@RequestParam(value = "orderProcessId") Integer orderProcessId,
                                                                      @RequestParam(value = "processId") Integer processId) throws Exception {
        SecurityUser currentUser = getCurrentUser();
        if (processId == null) {
            return ResultUtil.success(null);
        } else {
            //根据用户ID获取当前用户绑定的的人员信息
            List<TSysClass> tSysClassList = tSysClassService.getClassByProcessId(processId);
            var orderProcess = orderProcessService.findById(orderProcessId);
            if (orderProcess != null && orderProcess.getClassId() != null) {
                tSysClassList.removeIf(e -> e.getClassId() == orderProcess.getClassId().getClassId());
            }
            List<HandOverClassVo> list = new ArrayList<>();
            for (TSysClass tSysClass : tSysClassList) {
                List<Map> maps = classRepository.findClassLeaderByClassId(tSysClass.getClassId());
                String mapStr = JSON.toJSONString(maps);
                List<HandOverClassVo> stationVos = JSON.parseArray(mapStr, HandOverClassVo.class);
                List<Map> maps1 = classRepository.findPersonByClassId(tSysClass.getClassId());
                String mapStrs = JSON.toJSONString(maps1);
                List<HandOverClassVo> stationVos1 = JSON.parseArray(mapStrs, HandOverClassVo.class);
                stationVos.addAll(stationVos1);
                list.addAll(stationVos);

            }
            for (HandOverClassVo classVo : list) {
                if (classVo.getPersonnelStation() != null && classVo.getPersonnelStation().contains("JOB")) {
                    classVo.setPersonnelStation(GlobalConstant.getCodeDscName("JOB0000", classVo.getPersonnelStation()));
                }
            }

            return ResultUtil.success(list);
        }
    }

    @ApiOperation("获取移交记录")
    @GetMapping("/handOverRecords")
    public ResponseResult<List<TaskListHandOverVo>> handOverRecords(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                                    @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                    @RequestParam(value = "sort", defaultValue = "desc") String sort) throws ThingsboardException {
        if ("desc".equals(sort) || "asc".equals(sort)) {
            SecurityUser currentUser = getCurrentUser();
            PageVo<TaskListHandOverVo> taskListVoPageVo = orderHeadService.getHandOverRecords(current, size, currentUser.getId().getId().toString(), sort);
            return ResultUtil.success(taskListVoPageVo);
        } else {
            return ResultUtil.error("排序参数值错误！");
        }
    }

    @ApiOperation("获取工序执行表id的盘点类型")
    @GetMapping("/getOrderRecordTypePd")
    public ResponseResult<String> getOrderRecordTypePd(@RequestParam("orderProcessId") Integer orderProcessId) {
        String recordTypePd = orderProcessRecordService.getOrderRecordTypePd(orderProcessId);
        return ResultUtil.success(recordTypePd);
    }

    @ApiOperation("获取同单据编号信息")
    @GetMapping("/getBillNoInfo")
    public ResponseResult<List<OrderProcessResult>> getBillNoInfo(@RequestParam(value = "orderNo") String orderNo) {
        List<OrderProcessResult> result = orderInventoryService.getBillNoInfo(orderNo);
        return ResultUtil.success(result);
    }


}