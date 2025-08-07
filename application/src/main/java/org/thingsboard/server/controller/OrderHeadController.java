package org.thingsboard.server.controller;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.mes.bus.TBusOrderHead;
import org.thingsboard.server.common.data.mes.bus.TBusOrderUpdate;
import org.thingsboard.server.common.data.mes.sys.TSysClass;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.dto.OrderChangeClassSaveDto;
import org.thingsboard.server.dao.dto.OrderStartOrderSaveDto;
import org.thingsboard.server.dao.dto.TBusOrderDto;
import org.thingsboard.server.dao.dto.TBusOrderHeadDto;
import org.thingsboard.server.dao.order.OrderBackendService;
import org.thingsboard.server.dao.vo.*;
import org.thingsboard.server.service.order.OrderHeadExcelService;
import org.thingsboard.server.service.security.model.SecurityUser;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;


@Api(value = "订单接口", tags = "订单接口")
@RequestMapping("/api/orderhead")
@RestController
public class OrderHeadController extends BaseController {

    @Autowired
    OrderHeadExcelService orderHeadExcelService;
    @Autowired
    OrderBackendService orderBackendService;



    @ApiOperation("获取工序执行数据")
    @GetMapping("/getOrderProcess")
    public ResponseResult<TBusOrderHead> getOrderProcess(@RequestParam("orderProcessId") Integer orderProcessId) {
        return ResultUtil.success(orderHeadService.getOrderProcess(orderProcessId));
    }

    @ApiOperation("接受转移")
    @GetMapping("/acceptShiftRecord")
    public ResponseResult acceptShiftRecord(@ApiParam(name = "orderProcessId", value = "当前工序执行表id", required = true) @RequestParam("orderProcessId") Integer orderProcessId,
                                            @ApiParam(name = "toOrderProcessId", value = "目标工序执行id", required = true) @RequestParam("toOrderProcessId") Integer toOrderProcessId) throws ThingsboardException, ParseException {
        SecurityUser currentUser = getCurrentUser();
        orderHeadService.acceptShiftRecord(orderProcessId, toOrderProcessId, currentUser.getId().getId().toString());
        return ResultUtil.success();
    }

    @ApiOperation("转移")
    @GetMapping("/shiftRecord")
    public ResponseResult shiftRecord(@ApiParam(name = "orderProcessId", value = "当前工序执行表id", required = true) @RequestParam("orderProcessId") Integer orderProcessId,
                                      @ApiParam(name = "toOrderProcessId", value = "目标工序执行id", required = true) @RequestParam("toOrderProcessId") Integer toOrderProcessId) throws ThingsboardException {
        String userId = getCurrentUser().getId().getId().toString();
        orderHeadService.shiftRecord(orderProcessId, userId, toOrderProcessId);
        return ResultUtil.success();
    }

    @ApiOperation("转移订单详情")
    @GetMapping("/shiftRecordDetail")
    public ResponseResult<ShiftRecordDetailVo> shiftRecordDetail(@ApiParam(name = "orderProcessId", value = "当前工序执行表id", required = true) @RequestParam("orderProcessId") Integer orderProcessId,
                                                                 @ApiParam(name = "toOrderProcessId", value = "目标工序执行id", required = true) @RequestParam("toOrderProcessId") Integer toOrderProcessId) {
        ShiftRecordDetailVo vo = orderHeadService.shiftRecordDetail(orderProcessId, toOrderProcessId);
        return ResultUtil.success(vo);
    }


    @ApiOperation("查询订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", readOnly = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", readOnly = false)
    })
    @PostMapping("/list")
    public ResponseResult<PageVo<TBusOrderHead>> list(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                      @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                      @RequestBody TBusOrderHeadDto tBusOrderHeadDto) {
        PageVo<TBusOrderHead> pageVo = orderHeadService.tBusOrderHeadList(current, size, tBusOrderHeadDto);
        return ResultUtil.success(pageVo);
    }

    @ApiOperation("查询订单列表(废弃)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", readOnly = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", readOnly = false)
    })
    @PostMapping("/listorder")
    public ResponseResult<PageVo<OrderListVo>> listOrder(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                         @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                         @RequestBody TBusOrderDto pageOrderDto) throws Exception {
        PageVo<OrderListVo> pageVo = orderHeadService.getOrderHeadList(current, size, pageOrderDto);
        return ResultUtil.success(pageVo);
    }



    @ApiOperation("保存/修改订单（id为空则表示新增，id不为空表示修改）")
    @PostMapping("/save")
    public ResponseResult save(@RequestBody TBusOrderHead tBusOrderHead) throws Exception {
        SecurityUser currentUser = getCurrentUser();
        tBusOrderHead.setUpdatedName(currentUser.getName());
        tBusOrderHead.setUpdatedTime(new Date());
        orderHeadService.saveTBusOrderHead(tBusOrderHead);
        return ResultUtil.success();
    }

    @ApiOperation("订单详情信息")
    @GetMapping("/getDetailInfo")
    public ResponseResult<OrderDetailVo> getDetailInfo(@RequestParam(value = "orderId") Integer orderId,
                                                       @RequestParam(value = "orderProcessId",defaultValue = "0") Integer orderProcessId) throws Exception {
        OrderDetailVo orderProcessVo = orderHeadService.getTBusOrderDetail(orderId,orderProcessId);
        return ResultUtil.success(orderProcessVo);
    }

    @ApiOperation("报工详情信息")
    @GetMapping("/getProcessHistoryInfo")
    public ResponseResult<List<OrderProcessVo>> getProcessHistoryInfo(@RequestParam(value = "orderId") Integer orderId) throws Exception {
        List<OrderProcessVo> orderProcessVoList = orderHeadService.getProcessHistoryInfo(orderId);
        return ResultUtil.success(orderProcessVoList);
    }

    @ApiOperation("工序详情")
    @GetMapping("/getProcessInfo")
    public ResponseResult<List<OrderProcessVo>> getProcessInfo(@RequestParam(value = "orderId") Integer orderId) throws Exception {
        List<OrderProcessVo> orderProcessVoList = orderHeadService.getTBusOrderProcess(orderId);
        return ResultUtil.success(orderProcessVoList);
    }

    @ApiOperation("订单APP工序详情信息")
    @GetMapping("/getAppProcessTransferInfo")
    public ResponseResult<List<OrderTransferVo>> getAppProcessTransferInfo(@RequestParam(value = "orderId") Integer orderId, @RequestParam(value = "orderProcessId", defaultValue = "0") Integer orderProcessId) throws Exception {
        List<OrderTransferVo> orderTransferVoList = orderHeadService.getAppProcessTBusOrderTransfer(orderId, orderProcessId);
        return ResultUtil.success(orderTransferVoList);
    }

    @ApiOperation("移交详情")
    @GetMapping("/getTransferInfo")
    public ResponseResult<List<OrderTransferVo>> getTransferInfo(@RequestParam(value = "orderId") Integer orderId) throws Exception {
        List<OrderTransferVo> orderTransferVoList = orderHeadService.getTBusOrderTransfer(orderId);
        return ResultUtil.success(orderTransferVoList);
    }

    @ApiOperation("更新单据状态")
    @PostMapping("/updateStatus")
    public ResponseResult updateStatus(@RequestBody TBusOrderHead tBusOrderHead) throws Exception {
        SecurityUser currentUser = getCurrentUser();
        tBusOrderHead.setUpdatedName(currentUser.getName());
        tBusOrderHead.setUpdatedTime(new Date());
        orderHeadService.updateBillStatus(tBusOrderHead);
        return ResultUtil.success();
    }

    @ApiOperation("接单开工")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "订单ID", required = true),
            @ApiImplicitParam(name = "craftId", value = "工艺路线ID", required = true),
            @ApiImplicitParam(name = "craftDesc", value = "备注说明", required = true)
    })
    @PostMapping("/startOrder")
    public ResponseResult startOrder(@RequestParam("orderId") Integer orderId, @RequestParam("craftId") Integer craftId, @RequestParam("craftDesc") String craftDesc) throws Exception {
        orderBackendService.startOrder(orderId, craftId, craftDesc);
        return ResultUtil.success();
    }

    @ApiOperation("接单开工（批量）")
    @PostMapping("/startOrderBatch")
    public ResponseResult startOrderBatch(@RequestBody List<OrderStartOrderSaveDto> saveDtoList) throws Exception {
        orderBackendService.startOrderBatch(saveDtoList);
        return ResultUtil.success();
    }

    @ApiOperation("通过物料获取工艺路线")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "materialNumber", value = "订单明细物料编码", required = true),
            @ApiImplicitParam(name = "orderId", value = "订单ID", required = true),
    })
    @PostMapping("/getCraftInfo")
    public ResponseResult startOrder(@RequestParam("materialNumber") String materialNumber, @RequestParam("orderId") Integer orderId) throws Exception {
        var craftInfo = orderBackendService.getCraftInfoByMaterial(materialNumber);
        return ResultUtil.success(craftInfo);
    }

    @ApiOperation("excel导出")
    @PostMapping("/export")
    public void download(@RequestParam(value = "current", defaultValue = "0") Integer current,
                         @RequestParam(value = "size", defaultValue = "10") Integer size, @RequestBody TBusOrderHeadDto tBusOrderHeadDto, HttpServletResponse response) throws IOException {
        orderHeadExcelService.download(current, size, tBusOrderHeadDto, response);
    }



//    @ApiOperation("excel导出(废弃)")
//    @PostMapping("/exportorder")
//    public void downloadOrder(@RequestParam(value = "current",defaultValue = "0") Integer current,
//                         @RequestParam(value = "size",defaultValue = "10") Integer size, @RequestBody TBusOrderDto tBusOrderDto, HttpServletResponse response) throws Exception {
//        orderHeadExcelService.downloadOrder(current,size,tBusOrderDto,response);
//    }

    @ApiOperation("删除订单")
    @GetMapping("/delete")
    public ResponseResult delete(@RequestParam("orderHeadId") Integer orderHeadId) {
        orderHeadService.deleteTBusOrderHead(orderHeadId);
        return ResultUtil.success();
    }

    @ApiOperation("查询变更信息")
    @ApiImplicitParam(name = "id", value = "订单id", required = true)
    @GetMapping("/listOrderUpdate")
    public ResponseResult<TBusOrderUpdate> listOrderUpdate(@RequestParam("id") Integer id) {
        TBusOrderUpdate tBusOrderUpdates = orderHeadService.listOrderUpdate(id);
        return ResultUtil.success(tBusOrderUpdates);
    }

    @ApiOperation("根据订单id获取班别")
    @ApiImplicitParam(name = "orderId", value = "订单id", required = true)
    @GetMapping("/getOrderClassInfo")
    public ResponseResult<List<TSysClass>> getOrderClassInfo(@RequestParam("orderId") Integer orderId) {
        return ResultUtil.success(orderBackendService.getOrderClassInfo(orderId));
    }

    @ApiOperation("订单班别变更提交")
    @PostMapping("/changeOrderClass")
    public ResponseResult changeOrderClass(@RequestBody OrderChangeClassSaveDto saveDto) {
        orderBackendService.changeOrderClass(saveDto);
        return ResultUtil.success();
    }
}
