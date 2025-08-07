package org.thingsboard.server.controller.app.orderTask;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.mes.LichengConstants;
import org.thingsboard.server.common.data.mes.sys.TSysProcessInfo;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.controller.BaseController;
import org.thingsboard.server.dao.mes.TSysProcessInfo.TSysProcessInfoService;
import org.thingsboard.server.dao.mes.dto.OrderTaskSelectDto;
import org.thingsboard.server.dao.mes.vo.PageVo;
import org.thingsboard.server.dao.mes.vo.TaskListFinishVo;
import org.thingsboard.server.dao.mes.vo.TaskListVo;
import org.thingsboard.server.service.security.model.SecurityUser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author hhh
 * @version V1.0
 * @Package org.thingsboard.server.controller
 * @date 2022/4/29 11:01
 * @Description:
 */
@RestController
@Api(value = "订单生产任务", tags = "订单生产任务")
@RequestMapping("/api/orderTask")
public class OrderTaskController extends BaseController {

    @Autowired
    TSysProcessInfoService tSysProcessInfoService;

    @ApiOperation("任务列表批次")
    @GetMapping("/listBodyIot")
    public ResponseResult<List<String>> listBodyIot(@RequestParam("type") String type) throws ThingsboardException {
        SecurityUser currentUser = getCurrentUser();
        return ResultUtil.success(appOrderTaskService.listBodyIot(currentUser.getId().getId().toString(),type));
    }

    @ApiOperation("任务工序列表")
    @GetMapping("/listProcess")
    public ResponseResult<Map> listProcess(){
        return ResultUtil.success(orderProcessService.listProcess());
    }

    @ApiOperation("获取任务列表详情")
    @GetMapping("/getOrderTaskVo")
    public ResponseResult<TaskListVo> getTaskListVo(@RequestParam("orderProcessId") Integer orderProcessId){
        return ResultUtil.success(appOrderTaskService.getTaskListVo(orderProcessId));
    }

    @ApiOperation("获取今日任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current",value = "页码(默认第0页,页码从0开始)",readOnly = false),
            @ApiImplicitParam(name = "size",value = "数量(默认10条)",readOnly = false),
            @ApiImplicitParam(name = "sort",value = "下单时间排序，desc倒叙，asc正序",readOnly = false)
    })
    @PostMapping("/getTodayTask")
    public ResponseResult<PageVo<TaskListVo>> list(@RequestParam(value = "current",defaultValue = "0") Integer current,
                                                   @RequestParam(value = "size",defaultValue = "10") Integer size,
                                                   @RequestParam(value = "sort",defaultValue = "desc") String sort,
                                                   @RequestBody(required = false) OrderTaskSelectDto selectDto) throws Exception{
        if ("desc".equals(sort) || "asc".equals(sort)){
            SecurityUser currentUser = getCurrentUser();
            PageVo<TaskListVo> classList = appOrderTaskService.getTodayTaskList(current,size,currentUser.getId().getId().toString(),sort,selectDto);
            return ResultUtil.success(classList);
        }else {
            return ResultUtil.error("排序参数值错误！");
        }

    }

    @ApiOperation("获取待生产任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", readOnly = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", readOnly = false),
            @ApiImplicitParam(name = "sort", value = "下单时间排序，desc倒叙，asc正序", readOnly = false)
    })
    @PostMapping("/getWaitTaskList")
    public ResponseResult<PageVo<TaskListVo>> getWaitTaskList(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                              @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                              @RequestParam(value = "sort", defaultValue = "desc") String sort,
                                                              @RequestBody(required = false) OrderTaskSelectDto selectDto) throws Exception {
        if ("desc".equals(sort) || "asc".equals(sort)) {
            SecurityUser currentUser = getCurrentUser();
            List<String> processStatusList = new ArrayList<>();
            processStatusList.add("0");
            PageVo<TaskListVo> classList = appOrderTaskService.getWaitTaskUserId2(current, size, processStatusList, currentUser.getId().getId().toString(), sort,selectDto);
            return ResultUtil.success(classList);
        } else {
            return ResultUtil.error("排序参数值错误！");
        }
    }

    @ApiOperation("接受转移")
    @GetMapping("/acceptShiftRecord")
    public ResponseResult acceptShiftRecord(@ApiParam(name = "orderProcessId", value = "当前工序执行表id", required = true) @RequestParam("orderProcessId") Integer orderProcessId,
                                            @ApiParam(name = "toOrderProcessId", value = "目标工序执行id", required = true) @RequestParam("toOrderProcessId") Integer toOrderProcessId) throws ThingsboardException, ParseException {
        SecurityUser currentUser = getCurrentUser();
        orderHeadService.acceptShiftRecord(orderProcessId,toOrderProcessId,currentUser.getId().getId().toString());
        return ResultUtil.success();
    }

    @ApiOperation("获取生产中任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current",value = "页码(默认第0页,页码从0开始)",readOnly = false),
            @ApiImplicitParam(name = "size",value = "数量(默认10条)",readOnly = false),
            @ApiImplicitParam(name = "sort",value = "下单时间排序，desc倒叙，asc正序",readOnly = false)
    })
    @PostMapping("/getStartTaskList")
    public ResponseResult<PageVo<TaskListVo>> getStartTaskList(@RequestParam(value = "current",defaultValue = "0") Integer current,
                                                               @RequestParam(value = "size",defaultValue = "10") Integer size,
                                                               @RequestParam(value = "sort",defaultValue = "desc") String sort,
                                                               @RequestBody(required = false) OrderTaskSelectDto selectDto) throws Exception{
        if ("desc".equals(sort) || "asc".equals(sort)){
            SecurityUser currentUser = getCurrentUser();
            List<String> processStatusList = new ArrayList<>();
            processStatusList.add("1");
            processStatusList.add("2");
            String orderProcessType = LichengConstants.ORDER_PROCESS_TYPE_1;
            PageVo<TaskListVo> classList = appOrderTaskService.getTaskListByPersonIdAndProcessStatus(current,size,processStatusList,orderProcessType,currentUser.getId().getId().toString(),sort,selectDto);
            return ResultUtil.success(classList);
        }else {
            return ResultUtil.error("排序参数值错误！");
        }
    }

    /**
     * 今日任务	1.根据订单表“工艺路线”关联各工序处理班组班长信息，当前登录用户存在于以上班长信息中。
     * 2.且订单表“计划开工时间”是今日日期（取系统日期）。
     * 待生产	“订单表”关联“工序执行表”查询“工序执行号”为最新的（即为订单当前工序），“工序执行表”的“处理人”是当前登录用户，且“工序状态”为“未开工”。
     * 生产中	“订单表”关联“工序执行表”，“工序执行表”的“处理人”是当前登录用户，且“工序状态”为“已开工”或“暂停”。
     * 未生产	1.根据订单表“工艺路线”关联各工序处理班组班长信息，当前登录用户存在于以上班长信息中。
     * 2.且“订单表”关联“工序执行表”，后者表“处理人”字段匹配当前登录用户，在后者表中无记录。
     * 已完工	“订单表”关联“工序执行表”，“工序执行表”的“处理人”是当前登录用户，且“工序状态”为“已完工”。
     * 明日任务	查询订单表“计划开工时间”是明日日期（取系统日期）的订单。(不需要过滤用户)
     */
    /*
       【未生产】
        1.通过当前登录账号关联查询出所在班组、班组关联的工序、工序关联的工艺路线，工艺路线关联的订单等信息。
        2.再根据“工序执行表”的班组编码，关联查询以上班组绑定的组长账号信息，使用当前登录账号进行关联查询。
        3.根据以上查询结果的“工序编码”，关联查询“工序执行表”，如果查询无记录，则是未生产订单。
        （注意：每个工序编码单独进行该条件过滤）
     */
    @ApiOperation("获取未生产任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", readOnly = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", readOnly = false),
            @ApiImplicitParam(name = "sort", value = "下单时间排序，desc倒叙，asc正序", readOnly = false)
    })
    @PostMapping("/getUnStartTaskList")
    public ResponseResult<PageVo<TaskListVo>> getUnStartTaskList(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                                 @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                 @RequestParam(value = "sort", defaultValue = "desc") String sort,
                                                                 @RequestBody(required = false) OrderTaskSelectDto selectDto) throws Exception {
        if ("desc".equals(sort) || "asc".equals(sort)) {
            SecurityUser currentUser = getCurrentUser();
            PageVo<TaskListVo> taskListVoPageVo = appOrderTaskService.getUnStartTaskList(current, size, currentUser.getId().getId().toString(), sort,selectDto);
            return ResultUtil.success(taskListVoPageVo);
        } else {
            return ResultUtil.error("排序参数值错误！");
        }
    }

    @ApiOperation("转移未接受列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", readOnly = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", readOnly = false),
            @ApiImplicitParam(name = "sort", value = "下单时间排序，desc倒叙，asc正序", readOnly = false)
    })
    @PostMapping("/listShiftNoAcceptTaskList")
    public ResponseResult<PageVo<TaskListVo>> listShiftNoAcceptTaskList(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                                        @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                        @RequestParam(value = "sort", defaultValue = "desc") String sort,
                                                                        @RequestBody(required = false) OrderTaskSelectDto selectDto) throws ThingsboardException {
        if ("desc".equals(sort) || "asc".equals(sort)) {
            SecurityUser currentUser = getCurrentUser();
            PageVo<TaskListVo> taskListVoPageVo = orderHeadService.listShiftNoAcceptTaskList(current, size ,sort,currentUser.getId().getId().toString(),selectDto);
            return ResultUtil.success(taskListVoPageVo);
        } else {
            return ResultUtil.error("排序参数值错误！");
        }
    }

    @ApiOperation("转移记录列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", readOnly = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", readOnly = false),
            @ApiImplicitParam(name = "sort", value = "下单时间排序，desc倒叙，asc正序", readOnly = false)
    })
    @PostMapping("/listShiftTaskList")
    public ResponseResult<PageVo<TaskListVo>> listShiftTaskList(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                                @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                @RequestParam(value = "sort", defaultValue = "desc") String sort,
                                                                @RequestBody(required = false) OrderTaskSelectDto selectDto) throws ThingsboardException {
        if ("desc".equals(sort) || "asc".equals(sort)) {
            SecurityUser currentUser = getCurrentUser();
            PageVo<TaskListVo> taskListVoPageVo = orderHeadService.listShiftTaskList(current, size ,sort,currentUser.getId().getId().toString(),selectDto);
            return ResultUtil.success(taskListVoPageVo);
        } else {
            return ResultUtil.error("排序参数值错误！");
        }
    }

    @ApiOperation("获取已完工任务(拌料工序)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", readOnly = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", readOnly = false),
            @ApiImplicitParam(name = "sort", value = "下单时间排序，desc倒叙，asc正序", readOnly = false)
    })
    @PostMapping("/listFinishProcessTaskList")
    public ResponseResult<PageVo<TaskListVo>> listFinishProcessTaskList(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                                        @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                        @RequestParam(value = "sort", defaultValue = "desc") String sort,
                                                                        @RequestParam("orderProcessId") Integer orderProcessId) {
        if ("desc".equals(sort) || "asc".equals(sort)) {
            PageVo<TaskListVo> taskListVoPageVo = appOrderTaskService.listFinishProcessTaskList(current, size ,sort,orderProcessId);
            return ResultUtil.success(taskListVoPageVo);
        } else {
            return ResultUtil.error("排序参数值错误！");
        }
    }


    @ApiOperation("获取已完工任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", readOnly = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", readOnly = false),
            @ApiImplicitParam(name = "sort", value = "下单时间排序，desc倒叙，asc正序", readOnly = false)
    })
    @PostMapping("/getFinishTaskList")
    public ResponseResult<PageVo<TaskListFinishVo>> getFinishTaskList(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                                @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                @RequestParam(value = "sort", defaultValue = "desc") String sort,
                                                                @RequestBody(required = false) OrderTaskSelectDto selectDto) throws Exception {
        if ("desc".equals(sort) || "asc".equals(sort)) {
            SecurityUser currentUser = getCurrentUser();
            List<String> processStatusList = new ArrayList<>();
            processStatusList.add("3");
            PageVo<TaskListFinishVo> taskListVoPageVo = appOrderTaskService.getTaskListByPersonIdAndProcessStatus(current, size, processStatusList, currentUser.getId().getId().toString(), sort,selectDto);
            return ResultUtil.success(taskListVoPageVo);
        } else {
            return ResultUtil.error("排序参数值错误！");
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", readOnly = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", readOnly = false),
            @ApiImplicitParam(name = "sort", value = "下单时间排序，desc倒叙，asc正序", readOnly = false)
    })
    @ApiOperation("移交待生产任务列表")
    @PostMapping("/getWaithandOverVerify")
    public ResponseResult<PageVo<TaskListVo>> getWaithandOverVerify(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                                    @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                    @RequestParam(value = "sort", defaultValue = "desc") String sort,
                                                                    @RequestBody(required = false) OrderTaskSelectDto selectDto) throws ThingsboardException {
        if ("desc".equals(sort) || "asc".equals(sort)) {
            SecurityUser currentUser = getCurrentUser();
            PageVo<TaskListVo> taskListVoPageVo = appOrderTaskService.getWaithandOverVerify(current, size, currentUser.getId().getId().toString(), sort,selectDto);
            return ResultUtil.success(taskListVoPageVo);
        } else {
            return ResultUtil.error("排序参数值错误！");
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", readOnly = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", readOnly = false),
            @ApiImplicitParam(name = "sort", value = "下单时间排序，desc倒叙，asc正序", readOnly = false)
    })
    @ApiOperation("移交生产中任务列表")
    @PostMapping("/gethandOverTask")
    public ResponseResult<PageVo<TaskListVo>> gethandOverTask(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                              @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                              @RequestParam(value = "sort", defaultValue = "desc") String sort,
                                                              @RequestBody(required = false) OrderTaskSelectDto selectDto) throws ThingsboardException {
        if ("desc".equals(sort) || "asc".equals(sort)) {
            SecurityUser currentUser = getCurrentUser();
            List<String> processStatusList = new ArrayList<>();
            processStatusList.add("1");
            processStatusList.add("2");
            String orderProcessType = LichengConstants.ORDER_PROCESS_TYPE_2;
            PageVo<TaskListVo> classList = appOrderTaskService.getTaskListByPersonIdAndProcessStatus(current, size, processStatusList, orderProcessType, currentUser.getId().getId().toString(), sort,selectDto);
            return ResultUtil.success(classList);
        } else {
            return ResultUtil.error("排序参数值错误！");
        }
    }

    @ApiOperation("获取明日订单任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "页码(默认第0页,页码从0开始)", readOnly = false),
            @ApiImplicitParam(name = "size", value = "数量(默认10条)", readOnly = false),
            @ApiImplicitParam(name = "sort", value = "下单时间排序，desc倒叙，asc正序", readOnly = false)
    })
    @PostMapping("/getNextDayTaskList")
    public ResponseResult<PageVo<TaskListVo>> getNextDayTaskList(@RequestParam(value = "current", defaultValue = "0") Integer current,
                                                                 @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                                 @RequestParam(value = "sort", defaultValue = "desc") String sort,
                                                                 @RequestBody(required = false) OrderTaskSelectDto selectDto) throws Exception {
        if ("desc".equals(sort) || "asc".equals(sort)) {
            SecurityUser currentUser = getCurrentUser();
            PageVo<TaskListVo> taskListVoPageVo = appOrderTaskService.getNextDayTaskList(current, size, currentUser.getId().getId().toString(), sort,selectDto);
            return ResultUtil.success(taskListVoPageVo);
        } else {
            return ResultUtil.error("排序参数值错误！");
        }
    }

    @ApiOperation("工序撤回接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "订单id", required = true),
            @ApiImplicitParam(name = "orderProcessId", value = "订单工序执行ID", required = true)
    })
    @GetMapping("/resumedProcess")
    public ResponseResult resumedProcess(@RequestParam("orderId") Integer orderId, @RequestParam("orderProcessId") Integer orderProcessId) throws Exception {
        return orderHeadService.resumedProcess(orderId, orderProcessId);
    }

    @ApiOperation("通过工序id获取工序详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processId", value = "工序ID", required = true)
    })
    @GetMapping("/processDetailByProcessId")
    public ResponseResult<TSysProcessInfo> processDetail(@RequestParam Integer processId) {
        TSysProcessInfo processInfo = tSysProcessInfoService.processDetail(processId);
        return ResultUtil.success(processInfo);
    }

    @ApiOperation("通过订单工序执行id获取工序详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderProcessId", value = "订单工序执行ID", required = true)
    })
    @GetMapping("/processDetailByOrderProcessId")
    public ResponseResult<TSysProcessInfo> processDetailByOrderProcessId(@RequestParam Integer orderProcessId) {
        TSysProcessInfo processInfo=tSysProcessInfoService.processDetailByOrderProcessId(orderProcessId);
        return ResultUtil.success(processInfo);
    }



}
