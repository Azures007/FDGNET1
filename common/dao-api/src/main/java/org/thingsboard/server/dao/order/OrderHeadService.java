package org.thingsboard.server.dao.order;

import org.thingsboard.server.common.data.TBusOrderHead;
import org.thingsboard.server.common.data.TBusOrderUpdate;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.dao.dto.OrderTaskSelectDto;
import org.thingsboard.server.dao.dto.ShiftRecordDto;
import org.thingsboard.server.dao.dto.TBusOrderDto;
import org.thingsboard.server.dao.dto.TBusOrderHeadDto;
import org.thingsboard.server.dao.vo.*;

import java.text.ParseException;
import java.util.List;

public interface OrderHeadService {
    /**
     * 返回订单
     * @return
     */
    PageVo<TBusOrderHead> tBusOrderHeadList(Integer current, Integer size, TBusOrderHeadDto tBusOrderHeadDto);

    /**
     * 查询列表
     * @return
     */
    PageVo<TBusOrderHead> tBusOrderHeadList(Integer current, Integer size, TBusOrderDto orderDto);

    /**
     * 根据日期范围查询列表
     * @return
     */
    PageVo<OrderListVo> getOrderHeadList(Integer current, Integer size, TBusOrderDto tBusOrderDto) throws Exception;

//    /**
//     * 获取账号今日任务列表
//     * @return
//     */
//    PageVo<TaskListVo> getTodayTaskList(Integer current, Integer size, String userId, String sort,OrderTaskSelectDto selectDto);

    /**
     * 根据工序状态和用户id，获取生产中任务列表和完工任务列表
     * 生产中  "订单表"关联"工序执行表"，"工序执行表"的"处理人"是当前登录用户，且"工序状态"为"已开工"或"暂停"。
     * 已完工  "订单表"关联"工序执行表"，"工序执行表"的"处理人"是当前登录用户，且"工序状态"为"已完工"。
     * @return
     */
//    PageVo<TaskListFinishVo> getTaskListByPersonIdAndProcessStatus(Integer current, Integer size, List<String> processStatusList, String userId, String sort, OrderTaskSelectDto selectDto);

//    PageVo<TaskListVo> getTaskListByPersonIdAndProcessStatus(Integer current, Integer size, List<String> processStatusList, String orderProcessType, String userId, String sort,OrderTaskSelectDto selectDto);

//    PageVo<TaskListHandOverVo> getTaskHandOverListByPersonIdAndProcessStatus(Integer current, Integer size, List<String> processStatusList, String userId, String sort);

    /**
     * 根据工序状态获取账号待生产任务列表
     * @return
     */
//    PageVo<TaskListVo> getWaitTaskUserId2(Integer current, Integer size, List<String> processStatusList, String userId, String sort,OrderTaskSelectDto selectDto);

    /**
     * 获取账号未开工任务列表
     * @return
     */
//    PageVo<TaskListVo> getUnStartTaskList(Integer current, Integer size, String userId, String sort,OrderTaskSelectDto selectDto);
//    /**
//     * 获取账号完工任务列表
//     * @return
//     */
//    PageVo<TaskListVo> getFinishTaskList(Integer current, Integer size, String userId, String sort);
    /**
     * 获取账号明日任务列表
     * @return
     */
//    PageVo<TaskListVo> getNextDayTaskList(Integer current, Integer size, String userId, String sort,OrderTaskSelectDto selectDto);


    /**
     * 保存订单
     * @param tBusOrderHead
     */
    void saveTBusOrderHead(TBusOrderHead tBusOrderHead);

    /**
     * 根据ID获取订单详情
     * @param orderId
     */
    TBusOrderHead getTBusOrderHead(Integer orderId);

    /**
     * 根据ID获取订单详情
     * @param orderId
     */
    TBusOrderHead getOrderById(Integer orderId);

    /**
     * 订单详情
     * @param orderId
     */
    OrderDetailVo getTBusOrderDetail(Integer orderId,Integer orderProcessId);

    /**
     * 订单工序详情
     * @param orderId
     */
    List<OrderProcessVo> getTBusOrderProcess(Integer orderId);

    /**
     * App工序订单移交详情
     * @param orderId
     */
    List<OrderTransferVo> getAppProcessTBusOrderTransfer(Integer orderId,Integer OrderProcessId);

    /**
     * 订单移交详情
     * @param orderId
     */
    List<OrderTransferVo> getTBusOrderTransfer(Integer orderId);

    /**
     * 更新订单的单据状态
     * @param tBusOrderHead
     */
    void updateBillStatus(TBusOrderHead tBusOrderHead);

    /**
     * 删除订单
     * @param orderId
     */
    void deleteTBusOrderHead(Integer orderId);

//    /**
//     * 获取当前用户订单信息
//     * @param userId
//     * @return
//     */
//    GetOrderSizeVo getOrderSize(String userId);
//
//    Integer getCurrentTask(String toString);

    TBusOrderUpdate listOrderUpdate(Integer id);

    void verifyPot(Integer orderId, Integer orderProcessId);

    void sendMes(TBusOrderHead orderHead, String s);

    /**
     * 移交待确认任务列表
     */
//    PageVo<TaskListVo> getWaithandOverVerify(Integer current, Integer size,String toString, String sort,OrderTaskSelectDto selectDto);

    /**
     * 移交生产中任务列表
     * @param current
     * @param size
     * @param userId
     * @param sort
     * @return
     */
//    PageVo<TaskListVo> gethandOverTask(Integer current, Integer size, String userId, String sort);

    /**
     * 工序暂停
     */
    ResponseResult suspendProcess(Integer orderId, Integer orderProcessId, String suspendReason);

    /**
     * 工序恢复
     */
    ResponseResult recoverProcess(Integer orderId, Integer orderProcessId);

    /**
     * 工序结束
     */
    ResponseResult stopProcess(Integer orderId, Integer orderProcessId) throws Exception;

    /**
     * 工序撤回
     */
    ResponseResult resumedProcess(Integer orderId, Integer orderProcessId);

    /**
     * 接单开工接口
     */
    ResponseResult orderProcessStart(Integer orderId, Integer orderProcessId, String userId);

    /**
     * 接收移交接口
     */
    ResponseResult receiveHandover(Integer orderProcessId,String userId) throws ParseException;

    PageVo<TaskListHandOverVo> getHandOverRecords(Integer current, Integer size, String toString, String sort);

    /**
     * 获取关联订单：单据编号、工序id
     */
    PageVo<TaskListRelationVo> getRelationRecords(Integer current, Integer size, String billNo, Integer processId, String sort);

    /**
     *  转移
     * @param orderProcessId
     * @param userId
     */
    void shiftRecord(Integer orderProcessId,String userId,Integer toOrderProcessId);

//    PageVo<TaskListVo> listFinishProcessTaskList(Integer current, Integer size, String sort,Integer orderProcessId);

    ShiftRecordDetailVo shiftRecordDetail(Integer orderProcessId, Integer toOrderProcessId);

    PageVo<TaskListVo> listShiftTaskList(Integer current, Integer size, String sort, String userId,OrderTaskSelectDto selectDto);

    PageVo<TaskListVo> listShiftNoAcceptTaskList(Integer current, Integer size, String sort, String userId,OrderTaskSelectDto selectDto);

    /**
     * 接受转移
     * @param orderProcessId
     * @param toOrderProcessId
     */
    void acceptShiftRecord(Integer orderProcessId, Integer toOrderProcessId,String userId) throws ParseException;

    /**
     * 移交驳回
     * @param shiftRecordDto
     * @param userId
     */
    void rejectedShiftRecord(ShiftRecordDto shiftRecordDto, String userId) throws ParseException;

    /**
     * 任务列表批次
     * @param userId
     * @param type
     * @return
     */
//    List<String> listBodyIot(String userId, String type);

    TBusOrderHead getOrderProcess(Integer orderProcessId);

    /**
     * 查询订单简要列表
     */
    PageVo<OrderSimpleListVo> getSimpleOrderList(Integer current, Integer size, TBusOrderDto orderDto);

    /**
     * 获取订单详情（简要VO）
     */
    OrderDetailSimpleVo getOrderDetailSimple(Integer orderId);
}
