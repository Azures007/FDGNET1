package org.thingsboard.server.controller;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gson.JsonParseException;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.kv.*;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.TSysDevice.TSysDeviceService;
import org.thingsboard.server.dao.constant.GlobalConstant;
import org.thingsboard.server.dao.dto.*;
import org.thingsboard.server.dao.orderProcess.OrderProcessRecordService;
import org.thingsboard.server.dao.tSysAbrasiveSpecification.TSysAbrasiveSpecificationService;
import org.thingsboard.server.dao.timeseries.TimeseriesService;
import org.thingsboard.server.dao.vo.*;
import org.thingsboard.server.service.security.AccessValidator;
import org.thingsboard.server.service.security.model.SecurityUser;
import org.thingsboard.server.service.security.permission.Operation;
import org.thingsboard.server.service.telemetry.TsData;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.thingsboard.server.controller.ControllerConstants.*;

/**
 * @Auther: l
 * @Date: 2022/5/6 10:58
 * @Description:
 */
@Api(value = "订单报工接口", tags = "订单报工接口")
@RequestMapping("/api/orderRecord")
@RestController
@Slf4j
public class OrderProcessRecordController extends BaseController {
    @Autowired
    TSysDeviceService deviceService;

    private Object lock = new Object();

    @Autowired
    OrderProcessRecordService orderProcessRecordService;
    @Autowired
    private TimeseriesService tsService;
    @Autowired
    private AccessValidator accessValidator;

    @PostMapping("/getPotAllRecordDetails")
    @ApiOperation("获取当前积累锅数报工详情")
    public ResponseResult<ListJoinRecordVo> getPotAllRecordDetails(@RequestBody GetPotAllRecordDetailsDto getPotAllRecordDetailsDto) {
        ListJoinRecordVo getPotAllRecordDetailsVos = orderProcessRecordService.getPotAllRecordDetails(getPotAllRecordDetailsDto);
        return ResultUtil.success(getPotAllRecordDetailsVos);
    }

    @GetMapping("/getFingerprintAuthentication")
    @ApiOperation("获取工序指纹认证")
    public ResponseResult<String> getFingerprintAuthentication(@RequestParam("processId") Integer processId) {
        String fingerprintAuthentication = orderProcessRecordService.getFingerprintAuthentication(processId);
        return ResultUtil.success(fingerprintAuthentication);
    }

    @ApiOperation("组合报工")
    @PostMapping("/joinRecord")
    public ResponseResult<List<JoinRecordVo>> joinRecord(@RequestBody JoinRecordDto joinRecordDto) {
        List<JoinRecordVo> recordVos = orderProcessRecordService.joinRecord(joinRecordDto.getOrderPpbomId(),
                joinRecordDto.getOrderId(), joinRecordDto.getOrderProcessId(), joinRecordDto.getIds());
        return ResultUtil.success(recordVos);
    }

    @ApiOperation("产品标识单打印列表")
    @ApiImplicitParam(name = "orderProcessId", value = "工序执行id", required = true)
    @GetMapping("/listPrintRecord")
    public ResponseResult<List<ListPrintRecordVo>> listPrintRecord(@RequestParam("orderProcessId") Integer orderProcessId) {
        List<ListPrintRecordVo> vo = orderProcessRecordService.listPrintRecord(orderProcessId);
        return ResultUtil.success(vo);
    }

    @ApiOperation("工序锅数展示")
    @PostMapping("/getPot")
    public ResponseResult<GetPotVo> getPot(@RequestBody GetPotDto getPotDto) {
        GetPotVo vo = orderProcessRecordService.getPot(getPotDto);
        return ResultUtil.success(vo);
    }

    @ApiOperation("累计产出数据/个人产出数据")
    @PostMapping("/chopAndMixTotal")
    public ResponseResult<ChopAndMixTotalVo> chopAndMixTotal(@RequestBody ChopAndMixTotalSearchDto searchDto) throws Exception {
        SecurityUser user = getCurrentUser();
        ChopAndMixTotalVo totalVo = orderProcessRecordService.chopAndMixTotal(searchDto, user.getId().getId().toString());
        return ResultUtil.success(totalVo);
    }

    @ApiOperation("拌料工序投入做为产出报工")
    @PostMapping("/verifyRecordByThree")
    public ResponseResult<BigDecimal> verifyRecordByThree(@RequestBody GetPotDto getPotDto) {
        BigDecimal val = orderProcessRecordService.verifyPot(getPotDto);
        return ResultUtil.success(val);
    }

    @ApiOperation("拌料工序锅数展示(废弃)")
    @PostMapping("/getPotByBanLiao")
    public ResponseResult<GetPotVo> getPotByBanLiao(@RequestBody GetPotDto getPotDto) {
        GetPotVo vo = orderProcessRecordService.getPotBYBL(getPotDto.getOrderId(), getPotDto.getDevicePersonIds(), getPotDto.getOrderProcessId(), LichengConstants.PROCESS_NUMBER_BANLIAO);
        return ResultUtil.success(vo);
    }


    @ApiOperation("删除报工记录")
    @GetMapping("/deleteRecord")
    public ResponseResult deleteRecord(@RequestParam("orderProcessHistoryId") Integer orderProcessHistoryId) {
        synchronized (lock) {
            appOrderProcessRecordDeleteService.deleteRecord(orderProcessHistoryId, true);
        }
        return ResultUtil.success();
    }

    @ApiOperation("订单列表（移交）")
    @PostMapping("/handOverOrderList")
    public ResponseResult<List<TaskListFinishVo>> handOverOrderList(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                                    @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                    @RequestParam(value = "sort", defaultValue = "desc") String sort,
                                                                    @RequestBody(required = false) OrderTaskSelectDto selectDto) throws Exception {
        if ("desc".equals(sort) || "asc".equals(sort)) {
            SecurityUser currentUser = getCurrentUser();
            List<String> processStatusList = new ArrayList<>();
            processStatusList.add("1");
            processStatusList.add("2");
            PageVo<TaskListFinishVo> taskListVoPageVo = appOrderTaskService.getTaskListByPersonIdAndProcessStatus(current, size, processStatusList, currentUser.getId().getId().toString(), sort, selectDto);
            return ResultUtil.success(taskListVoPageVo);
        } else {
            return ResultUtil.error("排序参数值错误！");
        }
    }

    @ApiOperation("订单列表（关联订单）")
    @PostMapping("/relationOrderList")
    public ResponseResult<PageVo<TaskListRelationVo>> relationOrderList(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                                        @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                        @RequestParam(value = "sort", defaultValue = "desc") String sort,
                                                                        @RequestParam(value = "orderId") Integer orderId,
                                                                        @RequestParam(value = "processId") Integer processId) throws Exception {
        if ("desc".equals(sort) || "asc".equals(sort)) {
            TBusOrderHead orderHead = orderHeadService.getTBusOrderHead(orderId);
            if (null == orderHead || StringUtils.isEmpty(orderHead.getMidMoSaleOrderNo())) {
                return ResultUtil.error("获取订单的需求单据失败");
            }
            PageVo<TaskListRelationVo> taskListVoPageVo = orderHeadService.getRelationRecords(current, size, orderHead.getMidMoSaleOrderNo(), processId, sort);
            return ResultUtil.success(taskListVoPageVo);
        } else {
            return ResultUtil.error("排序参数值错误！");
        }
    }

    @ApiOperation("机台号选择")
    @ApiImplicitParam(name = "belongProcessId", value = "所属工序id", required = true)
    @PostMapping("/deviceList")
    public ResponseResult<PageVo<TsysDevice>> getDevice(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                        @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                        @RequestParam("belongProcessId") Integer belongProcessId) throws Exception {
        TSysDeviceDto deviceDto = new TSysDeviceDto();
        deviceDto.setEnabled("0");
        Page<TsysDevice> devices = deviceService.tSysDeviceList(current, size, deviceDto, belongProcessId);
        PageVo<TsysDevice> pageVo = new PageVo<>(devices);
        return ResultUtil.success(pageVo);
    }

    @ApiOperation("机台号选择（多选机台号过滤）")
    @PostMapping("/deviceList2")
    public ResponseResult<List<TsysDevice>> getDevice2(@RequestBody TSysDeviceSearchDto deviceDto) throws Exception {
        deviceDto.setEnabled("1");
        var devices = deviceService.tSysDeviceList(deviceDto);
        return ResultUtil.success(devices);
    }

    @ApiOperation("机排手选择")
    @PostMapping("/personList")
    public ResponseResult<PageVo<TsysDevice>> personList(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                         @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                         @RequestParam(value = "name", required = false) String name,
                                                         @RequestParam(value = "station", required = false) String station) throws Exception {
        TSysPersonnelInfoDto tSysPersonnelInfoDto = new TSysPersonnelInfoDto();
        if (!StringUtils.isEmpty(name)) {
            tSysPersonnelInfoDto.setName(name);
        }
        if (!StringUtils.isEmpty(station)) {
            tSysPersonnelInfoDto.setStation(station);
        }
        SecurityUser currentUser = getCurrentUser();
        Page<TSysPersonnelInfo> personnelList = tSysPersonnelInfoService.tSysPersonnelInfoListByUserId(current, size, tSysPersonnelInfoDto, currentUser.getId().getId().toString());
        PageVo<TSysPersonnelInfo> pageVo = new PageVo<>();
        PageVo<TSysPersonnelInfo> pageVo1 = pageVo.get(personnelList);
        return ResultUtil.success(pageVo1);
    }

    @ApiOperation("选择工序")
    @PostMapping("/processList")
    public ResponseResult<List<TSysProcessInfo>> getProcess(@RequestParam Integer orderId) throws Exception {
        List<TSysProcessInfo> results = orderProcessRecordService.getProcessInfos(orderId);
        return ResultUtil.success(results);
    }

//    @ApiOperation("报工原辅料二级类目")
//    @PostMapping("/getOrderPpbom")
//    public ResponseResult<List<OrderPPbomResult>> getOrderPpbom(@RequestParam Integer orderId, @RequestParam Integer executeProcessId, @RequestParam String recordType) throws Exception {
//        List<OrderPPbomResult> results = orderProcessRecordService.getOrderPpbom(orderId, executeProcessId, recordType);
//        return ResultUtil.success(results);
//    }
//
//    @ApiOperation("获取累计报工数量")
//    @PostMapping("/getOrderPpbomByPpbomMaterialId")
//    public ResponseResult<OrderPPbomResult> getOrderPpbomByPpbomMaterialId(@RequestBody OrderPPbomSearchDto searchDto) throws Exception {
//        OrderPPbomResult orderPPbomResult = orderProcessRecordService.getOrderPpbomByPpbomMaterialId(searchDto);
//        return ResultUtil.success(orderPPbomResult);
//    }

    @ApiOperation("报工原辅料二级类目（获取累计报工数量）")
    @PostMapping("/getOrderPpbom2")
    public ResponseResult<List<PpbomGroupVo>> getOrderPpbom(@RequestBody OrderPPbomSearchDto searchDto) throws Exception {
        List<PpbomGroupVo> results = orderProcessRecordService.getOrderPpbom(searchDto);
        return ResultUtil.success(results);
    }

    @ApiOperation("获取上下膜的用料清单")
    @PostMapping("/getOrderPpbomLsm")
    public ResponseResult<List<OrderPPbomResult>> getOrderPpbomLsm(@RequestBody OrderPPbomSearchDto searchDto) {
        List<OrderPPbomResult> results = orderProcessRecordService.getOrderPpbomLsm(searchDto);
        return ResultUtil.success(results);
    }

    @ApiOperation("报工提交校验")
    @PostMapping("/submitCheck")
    public ResponseResult<OrderRecordCheckVo> submitCheck(@RequestBody OrderRecordCheckDto saveDto) {
        OrderRecordCheckVo result = orderProcessRecordService.submitCheck(saveDto);
        return ResultUtil.success(result);
    }

    @ApiOperation("报工提交")
    @PostMapping("/submit")
    public ResponseResult submit(@RequestBody OrderRecordSaveDto saveDto) throws Exception {
        SecurityUser user = getCurrentUser();
        synchronized (lock) {
            appOrderProcessRecordSubmitService.submit(saveDto, user.getId().getId().toString());
        }
        return ResultUtil.success();
    }

    @Transactional(rollbackFor = Exception.class)
    @ApiOperation("报工提交(批量)")
    @PostMapping("/mulSubmit")
    public ResponseResult submit(@RequestBody OrderRecordSaveListDto saveListDto) throws Exception {
        SecurityUser user = getCurrentUser();
        synchronized (lock) {
            appOrderProcessRecordSubmitService.mulSubmit(saveListDto, user.getId().getId().toString());
        }
        return ResultUtil.success();
    }

    @GetMapping("/getRecordQtyUpdate")
    @ApiOperation("拌料工序数量换算")
    public ResponseResult<GetRecordQtyUpdateVo> getRecordQtyUpdate(@RequestParam("orderProcessId") int orderProcessId,
                                                                   @RequestParam("orderId") Integer orderId,
                                                                   @RequestParam("devicePersonIds") List<Integer> devicePersonIds) {
//        orderProcessRecordService.verifyPot(orderId,devicePersonId,orderProcessId);
        GetRecordQtyUpdateVo vo = orderProcessRecordService.getRecordQtyUpdate(orderProcessId);
        return ResultUtil.success(vo);
    }

    @ApiOperation("报工提交（扫码投入）")
    @PostMapping("/submitAndBindCode")
    public ResponseResult submitAndBindCode(@RequestBody List<OrderBindCodeDto> tBusOrderBindCodeList) throws Exception {
        SecurityUser user = getCurrentUser();
        return orderProcessRecordService.submitAndBindCode(user.getId().getId().toString(), tBusOrderBindCodeList);
    }

    @ApiOperation("报工提交（产后投入）")
    @PostMapping("/submitAndBindCheckMes")
    public ResponseResult submitAndBindCheckMes(@RequestBody SubmitAndBindCheckMesDto submitAndBindCheckMesDto) throws Exception {
        SecurityUser user = getCurrentUser();
        orderProcessRecordService.submitAndBindCheckMes(submitAndBindCheckMesDto, user.getId().getId().toString());
        return ResultUtil.success();
    }

    @ApiOperation("报工提交（产后投入）")
    @PostMapping("/submitAndModify")
    public ResponseResult submitAndModify(@RequestBody SubmitAndModifySaveDto submitAndModifySaveDto) throws Exception {
        SecurityUser user = getCurrentUser();
        orderProcessRecordService.submitAndModify(submitAndModifySaveDto, user.getId().getId().toString());
        return ResultUtil.success();
    }

    @ApiOperation("斩拌信息展示")
    @PostMapping("/chopAndMixMsg")
    public ResponseResult<ChopAndMixVo> chopAndMixMsg(@RequestBody OrderProcessRecordSearchDto searchDto) throws Exception {
        SecurityUser user = getCurrentUser();
        ChopAndMixVo mixVo = orderProcessRecordService.chopAndMixMsg(searchDto, user.getId().getId().toString());
        return ResultUtil.success(mixVo);
    }

    @ApiOperation("报工记录模块")
    @PostMapping("/getRecords")
    public ResponseResult<OrderRecordHeadVo> getRecords(@RequestBody OrderProcessRecordSearchDto searchDto) throws Exception {
        SecurityUser user = getCurrentUser();
        if (searchDto.getCustomWorkerCategoryFlag() == null) {
            return ResultUtil.error("报工标识不能为空，请联系管理员");
        }
        OrderRecordHeadVo orderRecordHeadVo = orderProcessRecordService.getRecords(searchDto, user.getId().getId().toString());
        return ResultUtil.success(orderRecordHeadVo);
    }

    @ApiOperation("斩拌报工记录")
    @PostMapping("/getHistories")
    public ResponseResult<List<OrderRecordVo>> getHistories(@RequestBody OrderProcessRecordSearchDto searchDto) throws Exception {
        SecurityUser user = getCurrentUser();
        List<OrderRecordVo> recordVoList = orderProcessRecordService.getHistories(searchDto, user.getId().getId().toString());
        return ResultUtil.success(recordVoList);
    }

    @ApiOperation("累计报工重量")
    @PostMapping("/getTotalWeight")
    public ResponseResult<WeightVo> getTotalWeight(@RequestBody OrderProcessRecordSearchDto searchDto) throws Exception {
        SecurityUser user = getCurrentUser();
        return orderProcessRecordService.getTotalWeight(searchDto, user.getId().getId().toString());
    }

    @ApiOperation("累计报工数量（AB料投入）")
    @PostMapping("/getTotalAbImport")
    public ResponseResult<WeightVo> getTotalAbImport(@RequestBody OrderProcessRecordSearchDto searchDto) throws Exception {
        SecurityUser user = getCurrentUser();
        return orderProcessRecordService.getTotalAbImport(searchDto);
    }

    @ApiOperation("累计报工数量（AB料产出）")
    @PostMapping("/getTotalAbExport")
    public ResponseResult<WeightTotalAbExportVo> getTotalAbExport(@RequestBody OrderProcessRecordSearchDto searchDto) throws Exception {
        SecurityUser user = getCurrentUser();
        return orderProcessRecordService.getTotalAbExport(searchDto);
    }

    @ApiOperation("合格品产出")
    @PostMapping("/getQualifiedExport")
    public ResponseResult<QualifiedExportVo> getQualifiedExport(@RequestBody QualifiedExportDao searchDto) throws Exception {
        return orderProcessRecordService.getQualifiedExport(searchDto);
    }

    @ApiOperation("自定义报工提交")
    @PostMapping("/customWorkerCategorySubmit")
    public ResponseResult customWorkerCategorySubmit(@RequestBody OrderRecordSaveDto saveDto) throws Exception {
        SecurityUser user = getCurrentUser();
        appOrderProcessRecordSubmitService.submit(saveDto, user.getId().getId().toString());
        return ResultUtil.success();
    }

    @ApiOperation("自定义报工，获取订单的工序列表")
    @PostMapping("/getCustomWCOrderProcess")
    public ResponseResult<PageVo<CraftProcessListVo>> getCustomWCOrderProcess(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                                              @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                              @RequestParam("orderId") Integer orderId) throws Exception {
        PageVo<CraftProcessListVo> processList = orderProcessRecordService.getCustomWCOrderProcess(current, size, orderId);
        return ResultUtil.success(processList);
    }

    //stretchMachine 拉伸膜机取数
    @ApiOperation("iot拉伸膜机取数")
    @PostMapping("/getStretchMachineRunNum")
    public ResponseResult<IotDiffValueVo> getStretchMachineRunNum(@ApiParam(value = "格式：yyyy-MM-dd HH:mm:ss:SSS")
                                                                  @RequestParam(value = "startTime") String startTime,
                                                                  @ApiParam(value = "格式：yyyy-MM-dd HH:mm:ss:SSS")
                                                                  @RequestParam(value = "endTime", required = false) String endTime,
                                                                  @RequestParam("deviceCode") String deviceCode) throws Exception {
//        IotDiffValueVo vo = this.getDiffValueNum(startTime,endTime,deviceCode,"正常模式跑动次数");
        return ResultUtil.success(orderProcessRecordService.getStretchMachineRunNum(startTime, endTime, deviceCode));
    }

    //stretchMachine 拉伸膜机取数
    @ApiOperation("iot拉伸膜机取数(多选)")
    @PostMapping("/getStretchMachineRunNums")
    public ResponseResult<IotDiffValueVo> getStretchMachineRunNums(@RequestBody OrderProcessIotSearchDto searchDto) throws Exception {
//        IotDiffValueVo vo = this.getDiffValueNum(startTime,endTime,deviceCode,"正常模式跑动次数");
        return ResultUtil.success(orderProcessRecordService.getStretchMachineRunNum(searchDto));
    }

    //cuttingMachine 剥皮机取数
    @ApiOperation("iot剥皮机取数")
    @PostMapping("/getCuttingMachineRunNum")
    public IotDiffValueVo getCuttingMachineRunNum(@ApiParam(value = "格式：yyyy-MM-dd HH:mm:ss:SSS")
                                                  @RequestParam(value = "startTime") String startTime,
                                                  @ApiParam(value = "格式：yyyy-MM-dd HH:mm:ss:SSS")
                                                  @RequestParam(value = "endTime", required = false) String endTime,
                                                  @RequestParam("deviceCode") String deviceCode) throws Exception {
//        IotDiffValueVo vo = this.getDiffValueNum(startTime,endTime,deviceCode,"正常模式生产支数");
        return orderProcessRecordService.getCuttingMachineRunNum(startTime, endTime, deviceCode);
    }

    //crabLine 蟹柳机取数
    @ApiOperation("iot蟹柳机取数")
    @PostMapping("/getCrabLineRunNum")
    public IotDiffValueVo getCrabLineRunNum(@ApiParam(value = "格式：yyyy-MM-dd HH:mm:ss:SSS")
                                            @RequestParam(value = "startTime") String startTime,
                                            @ApiParam(value = "格式：yyyy-MM-dd HH:mm:ss:SSS")
                                            @RequestParam(value = "endTime", required = false) String endTime,
                                            @RequestParam("deviceCode") String deviceCode) throws Exception {
//        IotDiffValueVo vo = this.getDiffValueNum(startTime,endTime,deviceCode,"正常模式生产支数");
        return orderProcessRecordService.getCrabLineRunNum(startTime, endTime, deviceCode);
    }

    //packLine 包装线取数
    @ApiOperation("iot包装线取数")
    @PostMapping("/getPackLineRunNum")
    public ResponseResult<IotDiffValueVo> getPackLineRunNum(@ApiParam(value = "格式：yyyy-MM-dd HH:mm:ss:SSS")
                                                            @RequestParam(value = "startTime") String startTime,
                                                            @ApiParam(value = "格式：yyyy-MM-dd HH:mm:ss:SSS")
                                                            @RequestParam(value = "endTime", required = false) String endTime,
                                                            @RequestParam("deviceCode") String deviceCode) throws Exception {
        return ResultUtil.success(orderProcessRecordService.getPackLineRunNum(startTime, endTime, deviceCode));
    }

    //packLine 包装线取数
    @ApiOperation("iot包装线取数(多选)")
    @PostMapping("/getPackLineRunNums")
    public ResponseResult<IotDiffValueVo> getPackLineRunNums(@RequestBody OrderProcessIotSearchDto searchDto) throws Exception {
        return ResultUtil.success(orderProcessRecordService.getPackLineRunNum(searchDto));
    }

    //stretchMachine 拉伸膜机取数
    @ApiOperation("iot机器取数（一段时间的差值）")
    @PostMapping("/getDiffValueNum")
    public ResponseResult<IotDiffValueVo> getDiffValueNum(@ApiParam(value = "格式：yyyy-MM-dd HH:mm:ss:SSS")
                                                          @RequestParam(value = "startTime") String startTime,
                                                          @ApiParam(value = "格式：yyyy-MM-dd HH:mm:ss:SSS")
                                                          @RequestParam(value = "endTime") String endTime,
                                                          @RequestParam("deviceCode") String deviceCode,
                                                          @RequestParam("key") String key) throws Exception {
        return ResultUtil.success(orderProcessRecordService.getDiffValueNum(startTime, endTime, deviceCode, key));
    }

    @ApiOperation("获取设备上报数据")
    @GetMapping("/getTimeseries")
    public DeferredResult<ResponseEntity> getTimeseries(
            @ApiParam(value = "表示实体类型的字符串值,例如'DEVICE'", required = true, defaultValue = "DEVICE") @RequestParam("entityType") String entityType,
            @ApiParam(value = "代表实体的id字符串值。例如,“784f394c-42-b6-435A-983c-b7beff2784f9”", required = true) @RequestParam("entityId") String entityIdStr,
            @ApiParam(value = "以逗号分隔的字符串值代表遥测key。", required = true) @RequestParam(name = "keys") String keys,
            @ApiParam(value = "Long类型参数，代表的开始时间戳以毫秒为单位的时间范围,UTC。")
            @RequestParam(name = "startTs") Long startTs,
            @ApiParam(value = "Long类型参数，代表的结束时间戳在毫秒时间范围,UTC。")
            @RequestParam(name = "endTs") Long endTs,
            @ApiParam(value = "Long类型参数，代表汇总时间间隔范围,以毫秒为单位,如果使用agg的聚合参数，需传值，可用endTs-startTs")
            @RequestParam(name = "interval", defaultValue = "0") Long interval,
            @ApiParam(value = "integer类型参数, 代表查询时序数据的最大数量." +
                    " 这个参数只有在agg使用'NONE'时生效.", defaultValue = "100")
            @RequestParam(name = "limit", defaultValue = "100") Integer limit,
            @ApiParam(value = "String类型，代表聚合函数. " +
                    "如果没有指定interval参数的值，agg默认使用NONE",
                    allowableValues = "MIN, MAX, AVG, SUM, COUNT, NONE")
            @RequestParam(name = "agg", defaultValue = "NONE") String aggStr,
            @ApiParam(value = SORT_ORDER_DESCRIPTION, allowableValues = SORT_ORDER_ALLOWABLE_VALUES)
            @RequestParam(name = "orderBy", defaultValue = "DESC") String orderBy,
            @ApiParam(value = STRICT_DATA_TYPES_DESCRIPTION)
            @RequestParam(name = "useStrictDataTypes", required = false, defaultValue = "false") Boolean useStrictDataTypes) throws ThingsboardException {
        try {
            DeferredResult<ResponseEntity> a = accessValidator.validateEntityAndCallback(getCurrentUser(), Operation.READ_TELEMETRY, entityType, entityIdStr,
                    (result, tenantId, entityId) -> {
                        // If interval is 0, convert this to a NONE aggregation, which is probably what the user really wanted
                        Aggregation agg = interval == 0L ? Aggregation.valueOf(Aggregation.NONE.name()) : Aggregation.valueOf(aggStr);
                        List<ReadTsKvQuery> queries = toKeysList(keys).stream().map(key -> new BaseReadTsKvQuery(key, startTs, endTs, interval, limit, agg, orderBy))
                                .collect(Collectors.toList());

                        Futures.addCallback(tsService.findAll(tenantId, entityId, queries), getTsKvListCallback(result, useStrictDataTypes), MoreExecutors.directExecutor());
                    });
            return a;
        } catch (Exception e) {
            throw handleException(e);
        }
    }


    private List<String> toKeysList(String keys) {
        List<String> keyList = null;
        if (!org.springframework.util.StringUtils.isEmpty(keys)) {
            keyList = Arrays.asList(keys.split(","));
        }
        return keyList;
    }

    private FutureCallback<List<TsKvEntry>> getTsKvListCallback(final DeferredResult<ResponseEntity> response, Boolean useStrictDataTypes) {
        return new FutureCallback<>() {
            @Override
            public void onSuccess(List<TsKvEntry> data) {
                Map<String, List<TsData>> result = new LinkedHashMap<>();
                for (TsKvEntry entry : data) {
                    Object value = useStrictDataTypes ? getKvValue(entry) : entry.getValueAsString();
                    result.computeIfAbsent(entry.getKey(), k -> new ArrayList<>()).add(new TsData(entry.getTs(), value));
                }
                response.setResult(new ResponseEntity<>(result, HttpStatus.OK));
            }

            @Override
            public void onFailure(Throwable e) {
                log.error("Failed to fetch historical data", e);
                AccessValidator.handleError(e, response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        };
    }

    private JsonNode toJsonNode(String value) {
        try {
            return JacksonUtil.toJsonNode(value);
        } catch (IllegalArgumentException e) {
            throw new JsonParseException("Can't parse jsonValue: " + value, e);
        }
    }

    private Object getKvValue(KvEntry entry) {
        if (entry.getDataType() == DataType.JSON) {
            return toJsonNode(entry.getJsonValue().get());
        }
        return entry.getValue();
    }

    @ApiOperation("剩余膜换算")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "materialId", value = "物料ID", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "getValue", value = "称重值", required = true, dataTypeClass = Float.class)
    })
    @GetMapping("/getTheResidualFilm")
    public ResponseResult<ResModalVo> getTheResidualFilm(@RequestParam("materialId") Integer materialId,
                                                         @RequestParam("getValue") Float getValue) {
        ResModalVo f = orderProcessRecordService.getTheResidualFilm(materialId, getValue);
        return ResultUtil.success(f);
    }

    @ApiOperation("剩余膜换算(多选)")
    @PostMapping("/getTheResidualFilms")
    public ResponseResult<List<ResModalVo>> getTheResidualFilm(@RequestBody TheResidualFilmSearchDto searchDtos) {
        List<ResModalVo> f = orderProcessRecordService.getTheResidualFilms(searchDtos);
        return ResultUtil.success(f);
    }

    @ApiOperation("工序开始结束时间")
    @PostMapping("/getProcessTime")
    public ResponseResult<OrderProcessTimeVo> getProcessTime(@RequestBody OrderProcessTimeDto searchDto) {
        OrderProcessTimeVo f = orderProcessRecordService.getProcessTime(searchDto);
        return ResultUtil.success(f);
    }

    @ApiOperation("设备开始结束时间")
    @PostMapping("/getIotDeviceTime")
    public ResponseResult<OrderProcessTimeVo> getIotDeviceTime(@RequestBody OrderProcessIotDeviceTimeDto searchDto) {
        OrderProcessTimeVo f = orderProcessRecordService.getIotDeviceTime(searchDto);
        return ResultUtil.success(f);
    }

    @ApiOperation("单位净重")
    @GetMapping("/getMaterialNeWeight")
    public ResponseResult getProcessTime(@RequestParam("materialNumber") String materialNumber) {
        return orderProcessRecordService.getMaterialNeWeight(materialNumber);
    }

    @ApiOperation("磨具列表")
    @GetMapping("/listGrindingApparatus")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "materialId", value = "物料ID", required = false, dataTypeClass = Integer.class)
    })
    public ResponseResult<List<TSysAbrasiveSpecification>> listGrindingApparatus(@RequestParam("materialId") Integer materialId) {
        List<TSysAbrasiveSpecification> tSysAbrasiveSpecifications = tSysAbrasiveSpecificationService.findAll();
        var midMaterial = orderProcessRecordService.getMidMaterial(materialId);
        if (null != midMaterial) {
            tSysAbrasiveSpecifications.stream().forEach(s -> {
                s.setKdMaterialStretchWeight(midMaterial.getKdMaterialStretchWeight());
            });
        }
        return ResultUtil.success(tSysAbrasiveSpecifications);
    }

    @ApiOperation("获取指定时间段设备采集信息")
    @PostMapping("/getIotByDevices")
    public ResponseResult<GetIotByDevicesVo> getIotByDevices(@RequestBody GetIotByDevicesDto getIotByDevicesDto) throws Exception {
        GetIotByDevicesVo iotDiffValueVo=orderProcessRecordService.getIotByDevices(getIotByDevicesDto);
        return ResultUtil.success(iotDiffValueVo);
    }

}
