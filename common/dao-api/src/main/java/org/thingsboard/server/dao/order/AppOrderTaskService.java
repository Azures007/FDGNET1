package org.thingsboard.server.dao.order;

import org.thingsboard.server.dao.dto.OrderTaskSelectDto;
import org.thingsboard.server.dao.vo.*;

import java.util.List;

public interface AppOrderTaskService {


    /**
     * 获取当前用户订单信息
     * @param userId
     * @return
     */
    GetOrderSizeVo getOrderSize(String userId);

    /**
     * 获取账号今日任务列表
     * @return
     */
    PageVo<TaskListVo> getTodayTaskList(Integer current, Integer size, String userId, String sort, OrderTaskSelectDto selectDto);

    /**
     * 根据工序状态和用户id，获取生产中任务列表和完工任务列表
     */
    PageVo<TaskListVo> getTaskListByPersonIdAndProcessStatus(Integer current, Integer size, List<String> processStatusList, String orderProcessType, String userId, String sort,OrderTaskSelectDto selectDto);

//    PageVo<TaskListHandOverVo> getTaskHandOverListByPersonIdAndProcessStatus(Integer current, Integer size, List<String> processStatusList, String userId, String sort);

    /**
     * 移交待确认任务列表
     */
    PageVo<TaskListVo> getWaithandOverVerify(Integer current, Integer size,String toString, String sort,OrderTaskSelectDto selectDto);

    /**
     * 获取账号未开工任务列表
     * @return
     */
    PageVo<TaskListVo> getUnStartTaskList(Integer current, Integer size, String userId, String sort,OrderTaskSelectDto selectDto);
//    /**
//     * 获取账号完工任务列表
//     * @return
//     */
//    PageVo<TaskListVo> getFinishTaskList(Integer current, Integer size, String userId, String sort);
    /**
     * 获取账号明日任务列表
     * @return
     */
    PageVo<TaskListVo> getNextDayTaskList(Integer current, Integer size, String userId, String sort,OrderTaskSelectDto selectDto);

    /**
     * 获取账号待移交任务列表
     * @param current
     * @param size
     * @param processStatusList
     * @param userId
     * @param sort
     * @return
     */
    PageVo<TaskListHandOverVo> getTaskHandOverListByPersonIdAndProcessStatus(Integer current, Integer size, List<String> processStatusList, String userId, String sort);

    /**
     * 获取账号待生产任务列表
     * @return
     */
    PageVo<TaskListVo> getWaitTaskUserId2(Integer current, Integer size, List<String> processStatusList, String userId, String sort,OrderTaskSelectDto selectDto);

    /**
     * 根据工序状态和用户id，获取生产中任务列表和完工任务列表
     * 生产中  “订单表”关联“工序执行表”，“工序执行表”的“处理人”是当前登录用户，且“工序状态”为“已开工”或“暂停”。
     * 已完工  “订单表”关联“工序执行表”，“工序执行表”的“处理人”是当前登录用户，且“工序状态”为“已完工”。
     * @return
     */
    PageVo<TaskListFinishVo> getTaskListByPersonIdAndProcessStatus(Integer current, Integer size, List<String> processStatusList, String userId, String sort, OrderTaskSelectDto selectDto);

    PageVo<TaskListVo> listFinishProcessTaskList(Integer current, Integer size, String sort,Integer orderProcessId);

    /**
     * 获取任务列表详情
     * @param orderProcessId
     * @return
     */
    TaskListVo getTaskListVo(Integer orderProcessId);

    /**
     * 任务列表的批次集合
     * @param userId
     * @param type
     * @return
     */
    List<String> listBodyIot(String userId, String type);

    /**
     * 获取订单详情
     * @param orderId
     * @return
     */
    AppOrderDetailSimpleVo getAppOrderDetailSimple(Integer orderId);
}
