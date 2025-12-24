package org.thingsboard.server.dao.mes.order;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.mes.LichengConstants;
import org.thingsboard.server.dao.constant.GlobalConstant;
import org.thingsboard.server.dao.mes.dto.OrderTaskSelectDto;
import org.thingsboard.server.dao.mes.vo.*;
import org.thingsboard.server.dao.sql.mes.order.AppOrderTaskRepository;
import org.thingsboard.server.dao.sql.mes.order.OrderHeadRepository;
import org.thingsboard.server.dao.user.UserService;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AppOrderTaskServiceImpl implements AppOrderTaskService {

    protected final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    private OrderHeadRepository orderHeadRepository;


    private static String ORDER_HEAD_HEADER_SIZE = "order:size:";

    @Autowired
    private AppOrderTaskRepository appOrderTaskRepository;

    @Autowired
    protected UserService userService;
    /**
     * 获取当前用户订单信息
     * @param userId
     * @return
     */
    @Override
    public GetOrderSizeVo getOrderSize(String userId) {
        GetOrderSizeVo getOrderSizeVo;
        String Cwkid =userService.getOneUserCurrentCwkid(userId);
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Object val = valueOperations.get(ORDER_HEAD_HEADER_SIZE + userId+":"+(Cwkid != null ? Cwkid : ""));
        if (val != null) {
            Map map = JSON.parseObject(JSON.toJSONString(val), Map.class);
            getOrderSizeVo = JSON.parseObject(JSON.toJSONString(map), GetOrderSizeVo.class);
        } else {
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            String currentDateStr = sdf1.format(new Date());
            //判断当前用户角色是否有主任/厂长权限
//        TSysRole role = roleRepository.getByUserId(userId);
//        userId = role.getByFactory().equals("0") ? "" : userId;
            //获取今日任务
            int currentTask = getTaskByDate(userId, currentDateStr);
            List<String> processStatusList1 = new ArrayList<>();
            processStatusList1.add("0");
            // 待生产任务
//        int waitTask = orderHeadRepository.getWaitTaskCountByUserId(userId,getMyDate());
            Integer waitTask = appOrderTaskRepository.getWaitTaskCountByUserId2(userId, "", "", "");
            //生产中任务
            List<String> processStatusList = new ArrayList<>();
            processStatusList.add("1");
            processStatusList.add("2");
            Integer startTask = appOrderTaskRepository.getTaskListCountByPersonIdAndProcessStatusAndOrderProcessType(userId, processStatusList, LichengConstants.ORDER_PROCESS_TYPE_1, "", "", "");
            //未生产任务
            Integer offTask = appOrderTaskRepository.getOffTask2(userId, "", "", "");
            //已完工任务
            List<String> processStatusList2 = new ArrayList<>();
            processStatusList2.add("3");
            Integer endTask = appOrderTaskRepository.getTaskListCountByPersonIdAndProcessStatus(userId, processStatusList2, "", "", "");
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 1);
            Date time = cal.getTime();
            //明日任务
            Integer tomorrowTask = appOrderTaskRepository.getCountNextDayTask(userId,sdf1.format(time), "");
            // 场景废弃，默认返回0：移交待生产任务、移交生产中任务、转移列表 2025-12-16
            //移交待生产任务
            Integer waithandOverVerify = 0;//appOrderTaskRepository.getWaitTaskCountByUserIdHandOver(userId, "", "", "");
            //移交生产中任务
            Integer handOverTask = 0;//appOrderTaskRepository.getTaskListCountByPersonIdAndProcessStatusAndOrderProcessType(userId, processStatusList, LichengConstants.ORDER_PROCESS_TYPE_2, "", "", "");
            //转移列表
            Integer shiftTask = 0;//appOrderTaskRepository.countShiftNoAcceptTaskList(userId, "", "");
            getOrderSizeVo = new GetOrderSizeVo(currentTask, waitTask, startTask, offTask, endTask, tomorrowTask, handOverTask, waithandOverVerify, shiftTask);
            if (getOrderSizeVo != null) {
                valueOperations.set(ORDER_HEAD_HEADER_SIZE + userId+":"+Cwkid,getOrderSizeVo, 10, TimeUnit.SECONDS);
            }
        }
        return getOrderSizeVo;
    }

    // 根据日期获取用户订单数
    public Integer getTaskByDate(String userId, String currentDateStr) {
//        return orderHeadRepository.getCountCurrentTask(userId, currentDateStr);
        Integer count = appOrderTaskRepository.getCountCurrentTask2(userId, currentDateStr, "", "");
        return count != null ? count : 0;  // 处理 null 值，返回默认值 0
    }

    /**
     * 获取账号今日任务列表
     * @return
     */
    @Override
    public PageVo<TaskListVo> getTodayTaskList(Integer current, Integer size, String userId, String sort, OrderTaskSelectDto selectDto) {
        if (null == selectDto) {
            selectDto = new OrderTaskSelectDto();
        }
//        Sort sort1 = Sort.by("desc".equals(sort) ? Sort.Direction.DESC : Sort.Direction.ASC, "bill_date");
        Sort.Order order1 = new Sort.Order("desc".equals(sort) ? Sort.Direction.DESC : Sort.Direction.ASC, "bill_date");
        Sort.Order order2 = new Sort.Order(Sort.Direction.DESC, "order_no");
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(order1);
        orders.add(order2);
        Sort sort1 = Sort.by(orders);
        PageRequest of = PageRequest.of(current, size, sort1);
//        current = current * size;
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateStr = sdf1.format(new Date());
//        TSysRole role = roleRepository.getByUserId(userId);
//        userId = role.getByFactory().equals("0") ? "" : userId;
        Page<Map> select = appOrderTaskRepository.getTodayTaskList2(userId, currentDateStr, selectDto.getProcessNumber(), selectDto.getBodyLot(), of);
        try {
            PageVo<TaskListVo> pageVo = new PageVo(size, current);
            List<TaskListVo> castEntity = JSON.parseArray(JSON.toJSONString(select.getContent()), TaskListVo.class);
            /*castEntity.stream().forEach(order -> {
                order.setBodyUnitStr(GlobalConstant.getCodeDscName("UNIT0000", order.getBodyUnit()));
            });*/
            int total = (int) select.getTotalElements();//appOrderTaskRepository.getCountCurrentTask2(userId, currentDateStr, selectDto.getProcessNumber(), selectDto.getBodyLot());
            pageVo.setTotal(total);
            pageVo.setList(castEntity);
            return pageVo;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生产中任务列表
     * @param current
     * @param size
     * @param processStatusList
     * @param orderProcessType
     * @param userId
     * @param sort
     * @param selectDto
     * @return
     */
    @Override
    public PageVo<TaskListVo> getTaskListByPersonIdAndProcessStatus(Integer current, Integer size, List<String> processStatusList, String orderProcessType, String userId, String sort, OrderTaskSelectDto selectDto) {
        if (null == selectDto) {
            selectDto = new OrderTaskSelectDto();
        }
        Sort.Order order1 = new Sort.Order("desc".equals(sort) ? Sort.Direction.DESC : Sort.Direction.ASC, "bill_date");
        Sort.Order order2 = new Sort.Order(Sort.Direction.DESC, "orderNo");
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(order1);
        orders.add(order2);
        Sort sort1 = Sort.by(orders);
        PageRequest of = PageRequest.of(current, size, sort1);
        taskVerify(selectDto);
//        current = current * size;
        //判断当前用户角色是否有主任/厂长权限
//        TSysRole role = roleRepository.getByUserId(userId);
//        userId = role.getByFactory().equals("0") ? "" : userId;
//        List<Object[]> select = orderHeadRepository.getTaskListByPersonIdAndProcessStatusAndOrderProcessType(userId, processStatusList, orderProcessType, of);
        Page<Map> select = appOrderTaskRepository.getTaskListByPersonIdAndProcessStatusAndOrderProcessType(userId, processStatusList, orderProcessType, selectDto.getProcessNumber(), selectDto.getBodyLot(), selectDto.getSelectOrField(), of);
        try {
            PageVo<TaskListVo> pageVo = new PageVo(size, current);
//            List<TaskListVo> castEntity = EntityUtils.castEntity(select, TaskListVo.class, new TaskListVo());
            List<TaskListVo> castEntity = JSON.parseArray(JSON.toJSONString(select.getContent()), TaskListVo.class);
            /*castEntity.stream().forEach(order -> {
                order.setBodyUnitStr(GlobalConstant.getCodeDscName("UNIT0000", order.getBodyUnit()));
            });*/
            int total = (int) select.getTotalElements();//appOrderTaskRepository.getTaskListCountByPersonIdAndProcessStatusAndOrderProcessType(userId, processStatusList, orderProcessType, selectDto.getProcessNumber(), selectDto.getBodyLot(), selectDto.getSelectOrField());
            pageVo.setTotal(total);
            pageVo.setList(castEntity);
            return pageVo;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

//    @Override
//    public PageVo<TaskListVo> getWaithandOverVerify(Integer current, Integer size, String userId, String sort, OrderTaskSelectDto selectDto) {
//        if (null == selectDto) {
//            selectDto = new OrderTaskSelectDto();
//        }
//        Sort.Order order1 = new Sort.Order("desc".equals(sort) ? Sort.Direction.DESC : Sort.Direction.ASC, "bill_date");
//        Sort.Order order2 = new Sort.Order(Sort.Direction.DESC, "orderNo");
//        List<Sort.Order> orders = new ArrayList<>();
//        orders.add(order1);
//        orders.add(order2);
//        Sort sort1 = Sort.by(orders);
//        PageRequest of = PageRequest.of(current, size, sort1);
////        current = current * size;
////        List<Object[]> select = orderHeadRepository.getWaitTaskUserIdHandOver(userId, of);
////        TSysRole role = roleRepository.getByUserId(userId);
////        userId = role.getByFactory().equals("0") ? "" : userId;
//        taskVerify(selectDto);
//        List<Map> select = appOrderTaskRepository.getWaitTaskUserIdHandOver(userId, selectDto.getProcessNumber(), selectDto.getBodyLot(), selectDto.getSelectOrField(), of);
//        try {
//            PageVo<TaskListVo> pageVo = new PageVo(size, current);
////            List<TaskListVo> castEntity = EntityUtils.castEntity(select, TaskListVo.class, new TaskListVo());
//            List<TaskListVo> castEntity = JSON.parseArray(JSON.toJSONString(select), TaskListVo.class);
//            castEntity.stream().forEach(order -> {
//                order.setBodyUnitStr(GlobalConstant.getCodeDscName("UNIT0000", order.getBodyUnit()));
//            });
//            int total = appOrderTaskRepository.getWaitTaskCountByUserIdHandOver(userId, selectDto.getProcessNumber(), selectDto.getBodyLot(), selectDto.getSelectOrField());
//            pageVo.setTotal(total);
//            pageVo.setList(castEntity);
//            return pageVo;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    @Override
    public PageVo<TaskListVo> getUnStartTaskList(Integer current, Integer size, String userId, String sort, OrderTaskSelectDto selectDto) {
        if (null == selectDto) {
            selectDto = new OrderTaskSelectDto();
        }
        Sort.Order order1 = new Sort.Order("desc".equals(sort) ? Sort.Direction.DESC : Sort.Direction.ASC, "bill_date");
        Sort.Order order2 = new Sort.Order(Sort.Direction.DESC, "orderNo");
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(order1);
        orders.add(order2);
        Sort sort1 = Sort.by(orders);
        PageRequest of = PageRequest.of(current, size, sort1);
        taskVerify(selectDto);
//        TSysRole role = roleRepository.getByUserId(userId);
//        userId = role.getByFactory().equals("0") ? "" : userId;
//        current = current * size;
//        List<Object[]> select = orderHeadRepository.getOffTaskList2(userId, of);
        List<Map> select = appOrderTaskRepository.getOffTaskList2(userId, selectDto.getProcessNumber(), selectDto.getBodyLot(), selectDto.getSelectOrField(), of);
        try {
            PageVo<TaskListVo> pageVo = new PageVo(size, current);
//            List<TaskListVo> castEntity = EntityUtils.castEntity(select, TaskListVo.class, new TaskListVo());
            List<TaskListVo> castEntity = JSON.parseArray(JSON.toJSONString(select), TaskListVo.class);
            castEntity.stream().forEach(order -> {
                order.setBodyUnitStr(GlobalConstant.getCodeDscName("UNIT0000", order.getBodyUnit()));
            });
//            int total = orderHeadRepository.getOffTask(userId);
            int total = appOrderTaskRepository.getOffTask2(userId, selectDto.getProcessNumber(), selectDto.getBodyLot(), selectDto.getSelectOrField());
            pageVo.setTotal(total);
            pageVo.setList(castEntity);
            return pageVo;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public PageVo<TaskListVo> getNextDayTaskList(Integer current, Integer size, String userId, String sort, OrderTaskSelectDto selectDto) {
        if (null == selectDto) {
            selectDto = new OrderTaskSelectDto();
        }
        Sort.Order order1 = new Sort.Order("desc".equals(sort) ? Sort.Direction.DESC : Sort.Direction.ASC, "bill_date");
        Sort.Order order2 = new Sort.Order(Sort.Direction.DESC, "orderNo");
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(order1);
        orders.add(order2);
        Sort sort1 = Sort.by(orders);
        PageRequest of = PageRequest.of(current, size, sort1);
//        current = current * size;
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DAY_OF_MONTH, 1);
        String nextDayDateStr = sdf1.format(c.getTime());

//        List<Object[]> select = orderHeadRepository.getNextDayTaskList(nextDayDateStr, of);
        Page<Map> select = appOrderTaskRepository.getNextDayTaskList(userId,nextDayDateStr, selectDto.getBodyLot(), of);
        try {
            PageVo<TaskListVo> pageVo = new PageVo(size, current);
//            List<TaskListVo> castEntity = EntityUtils.castEntity(select, TaskListVo.class, new TaskListVo());
            List<TaskListVo> castEntity = JSON.parseArray(JSON.toJSONString(select.getContent()), TaskListVo.class);
            /*castEntity.stream().forEach(order -> {
                order.setBodyUnitStr(GlobalConstant.getCodeDscName("UNIT0000", order.getBodyUnit()));
            });*/
            int total = (int) select.getTotalElements();//appOrderTaskRepository.getCountNextDayTask(userId,nextDayDateStr, selectDto.getBodyLot());
            pageVo.setTotal(total);
            pageVo.setList(castEntity);
            return pageVo;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取账号待移交任务列表
     * @param current
     * @param size
     * @param processStatusList
     * @param userId
     * @param sort
     * @return
     */
    @Override
    public PageVo<TaskListHandOverVo> getTaskHandOverListByPersonIdAndProcessStatus(Integer current, Integer size, List<String> processStatusList, String userId, String sort) {
        Sort.Order order1 = new Sort.Order("desc".equals(sort) ? Sort.Direction.DESC : Sort.Direction.ASC, "billDate");
        Sort.Order order2 = new Sort.Order(Sort.Direction.DESC, "orderNo");
        List<Sort.Order> orders = new ArrayList<>();
//        orders.add(order1);
        orders.add(order2);
        Sort sort1 = Sort.by(orders);
        PageRequest of = PageRequest.of(current, size, sort1);
//        current = current * size;
//        List<Object[]> select = orderHeadRepository.getTaskListHandOverByPersonIdAndProcessStatus(userId, processStatusList, of);
        List<Map> select = appOrderTaskRepository.getTaskListHandOverByPersonIdAndProcessStatus(userId, processStatusList, of);
        try {
            PageVo<TaskListHandOverVo> pageVo = new PageVo(size, current);
//            List<TaskListHandOverVo> castEntity = EntityUtils.castEntity(select, TaskListHandOverVo.class, new TaskListHandOverVo());
            List<TaskListHandOverVo> castEntity = JSON.parseArray(JSON.toJSONString(select), TaskListHandOverVo.class);
            castEntity.stream().forEach(order -> {
                order.setBodyUnitStr(GlobalConstant.getCodeDscName("UNIT0000", order.getBodyUnit()));
            });
            int total = appOrderTaskRepository.getTaskListHandOverCountByPersonIdAndProcessStatus(userId, processStatusList);
            pageVo.setTotal(total);
            pageVo.setList(castEntity);
            return pageVo;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取账号待生产任务列表
     * @return
     */
    @Override
    public PageVo<TaskListVo> getWaitTaskUserId2(Integer current, Integer size, List<String> processStatusList, String userId, String sort, OrderTaskSelectDto selectDto) {
        if (null == selectDto) {
            selectDto = new OrderTaskSelectDto();
        }
        Sort.Order order1 = new Sort.Order("desc".equals(sort) ? Sort.Direction.DESC : Sort.Direction.ASC, "bill_date");
        Sort.Order order2 = new Sort.Order(Sort.Direction.DESC, "order_no");
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(order1);
        orders.add(order2);
        Sort sort1 = Sort.by(orders);
        PageRequest of = PageRequest.of(current, size, sort1);
        Page<Map> select = appOrderTaskRepository.getWaitTaskUserId2(userId, selectDto.getProcessNumber(), selectDto.getBodyLot(), selectDto.getSelectOrField(), of);
//        System.out.println("-----");
        try {
            PageVo<TaskListVo> pageVo = new PageVo(size, current);
//            List<TaskListVo> castEntity = EntityUtils.castEntity(select, TaskListVo.class, new TaskListVo());
            List<TaskListVo> castEntity = JSON.parseArray(JSON.toJSONString(select.getContent()), TaskListVo.class);
            /*castEntity.stream().forEach(order -> {
                order.setBodyUnitStr(GlobalConstant.getCodeDscName("UNIT0000", order.getBodyUnit()));
            });*/
            int total = (int) select.getTotalElements();//appOrderTaskRepository.getWaitTaskCountByUserId2(userId, selectDto.getProcessNumber(), selectDto.getBodyLot(), selectDto.getSelectOrField());
            pageVo.setTotal(total);
            pageVo.setList(castEntity);
            return pageVo;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取订单任务列表（或移交）
     * @param current
     * @param size
     * @param processStatusList
     * @param userId
     * @param sort
     * @param selectDto
     * @return
     */
    @Override
    public PageVo<TaskListFinishVo> getTaskListByPersonIdAndProcessStatus(Integer current, Integer size, List<String> processStatusList, String userId, String sort, OrderTaskSelectDto selectDto) {
        if (null == selectDto) {
            selectDto = new OrderTaskSelectDto();
        }
        Sort.Order order1 = new Sort.Order("desc".equals(sort) ? Sort.Direction.DESC : Sort.Direction.ASC, "bill_date");
        Sort.Order order2 = new Sort.Order(Sort.Direction.DESC, "orderNo");
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(order1);
        orders.add(order2);
        Sort sort1 = Sort.by(orders);
//        current = current * size;
        PageRequest of = PageRequest.of(current, size, sort1);
        taskVerify(selectDto);
//        TSysRole role = roleRepository.getByUserId(userId);
//        userId = role.getByFactory().equals("0") ? "" : userId;
//        List<Object[]> select = orderHeadRepository.getTaskListByPersonIdAndProcessStatus(userId, processStatusList, of);
        Page<Map> select = appOrderTaskRepository.getTaskListByPersonIdAndProcessStatus(userId, processStatusList, selectDto.getProcessNumber(), selectDto.getBodyLot(), selectDto.getSelectOrField(), of);
        try {
            PageVo<TaskListFinishVo> pageVo = new PageVo(size, current);
//            List<TaskListVo> castEntity = EntityUtils.castEntity(select, TaskListVo.class, new TaskListVo());
            List<TaskListFinishVo> castEntity = JSON.parseArray(JSON.toJSONString(select.getContent()), TaskListFinishVo.class);
            /*castEntity.stream().forEach(order -> {
                order.setBodyUnitStr(GlobalConstant.getCodeDscName("UNIT0000", order.getBodyUnit()));

//                TBusOrderProcess tBusOrderProcess = orderProcessRepository.findById(order.getOrderProcessId()).orElse(null);
                //order.setPersonName(order.getProcessName());//接受人==处理人
//                TBusOrderProcess tBusOrderProcessOld = orderProcessRepository.findById(tBusOrderProcess == null ? -1 : tBusOrderProcess.getOldOrderProcessId()).orElse(null);
//                if (tBusOrderProcessOld != null) {
//                    TSysPersonnelInfo tSysPersonnelInfo = tBusOrderProcessOld.getHandOverPerSonId();
//                    order.setHandOverPersonName(tSysPersonnelInfo == null ? "" : tSysPersonnelInfo.getName());//移交人==移交前记录的移交人
//                    order.setTransferTime(Utils.formatDateTimeToString(tBusOrderProcessOld.getHandOverTime()));//转移时间
//                }
                // 任务14219 2022-09-04 已完工列表，拉伸膜、包装工序，实际完成产量，修改为获取“合格品产出(手输)”数量
                if (PROCESS_NUMBER_LASHENMO.equals(order.getExecuteProcessNumber()) || PROCESS_NUMBER_BAOZHUANG.equals(order.getExecuteProcessNumber())) {
                    order.setRecordT3UnitStr(GlobalConstant.getCodeDscName("UNIT0000", order.getRecordT3Unit()));
                } else {
                    order.setRecordT3ManualQty(-1f);
                }
            });*/
            int total = (int) select.getTotalElements();//appOrderTaskRepository.getTaskListCountByPersonIdAndProcessStatus(userId, processStatusList, selectDto.getProcessNumber(), selectDto.getBodyLot(), selectDto.getSelectOrField());
            pageVo.setTotal(total);
            pageVo.setList(castEntity);
            return pageVo;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private void taskVerify(OrderTaskSelectDto selectDto) {
        selectDto.setSelectOrField(StringUtils.isBlank(selectDto.getSelectOrField()) ? "" : selectDto.getSelectOrField());
        selectDto.setBodyLot(StringUtils.isBlank(selectDto.getBodyLot()) ? "" : selectDto.getBodyLot());
        selectDto.setProcessNumber(StringUtils.isBlank(selectDto.getProcessNumber()) ? "" : selectDto.getProcessNumber());
    }

    /**
     * 获取已完工任务(拌料工序)
     * @param current
     * @param size
     * @param sort
     * @param orderProcessId
     * @return
     */
//    @Override
//    public PageVo<TaskListVo> listFinishProcessTaskList(Integer current, Integer size, String sort, Integer orderProcessId) {
//        Sort.Order order1 = new Sort.Order("desc".equals(sort) ? Sort.Direction.DESC : Sort.Direction.ASC, "bill_date");
//        Sort.Order order2 = new Sort.Order(Sort.Direction.DESC, "orderNo");
//        List<Sort.Order> orders = new ArrayList<>();
//        orders.add(order1);
//        orders.add(order2);
//        Sort sort1 = Sort.by(orders);
//        PageRequest of = PageRequest.of(current, size, sort1);
//        current = current * size;
//        List<Map> maps = appOrderTaskRepository.listFinishProcessTaskList(orderProcessId, of);
//        int count = appOrderTaskRepository.countFinishProcessTaskList(orderProcessId);
//        PageVo<TaskListVo> pageVo = new PageVo(size, current);
//        pageVo.setTotal(count);
//        pageVo.setList(JSON.parseArray(JSON.toJSONString(maps), TaskListVo.class));
//        return pageVo;
//    }

    /**
     * 获取任务列表详情
     * @param orderProcessId
     * @return
     */
    @Override
    public TaskListVo getTaskListVo(Integer orderProcessId) {
        Map taskListVo = appOrderTaskRepository.getTaskListVo(orderProcessId);
        TaskListVo taskListVo1 = JSON.parseObject(JSON.toJSONString(taskListVo), TaskListVo.class);
        return taskListVo1;
    }

    /**
     * 任务列表的批次集合
     * @param userId
     * @param type
     * @return
     */
    @Override
    public List<String> listBodyIot(String userId, String type) {
        List<String> iots = new ArrayList<>();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        switch (type) {
            case LichengConstants.TASK_TYPE_1: {
                //今日任务
                String currentDateStr = sdf1.format(new Date());
                iots = appOrderTaskRepository.listTaskType1(userId, currentDateStr);
            }
            break;
            case LichengConstants.TASK_TYPE_2: {
                //待生产
                iots = appOrderTaskRepository.listTaskType2(userId);
            }
            break;
            case LichengConstants.TASK_TYPE_3: {
                //生产中
                List<String> processStatusList = new ArrayList<>();
                processStatusList.add("1");
                processStatusList.add("2");
                String orderProcessType = LichengConstants.ORDER_PROCESS_TYPE_1;
                iots = appOrderTaskRepository.listTaskType3(userId, processStatusList, orderProcessType);
            }
            break;
            case LichengConstants.TASK_TYPE_4: {
                //未生产
                iots = appOrderTaskRepository.listTaskType4(userId);
            }
            break;
            case LichengConstants.TASK_TYPE_5: {
                //已完工
                List<String> processStatusList2 = new ArrayList<>();
                processStatusList2.add("3");
                iots = appOrderTaskRepository.listTaskType5(userId, processStatusList2);
            }
            break;
            case LichengConstants.TASK_TYPE_6: {
                //移交待生产
                iots = appOrderTaskRepository.listTaskType6(userId);
            }
            break;
            case LichengConstants.TASK_TYPE_7: {
                //移交生产中
                List<String> processStatusList = new ArrayList<>();
                processStatusList.add("1");
                processStatusList.add("2");
                iots = appOrderTaskRepository.listTaskType7(userId, processStatusList, LichengConstants.ORDER_PROCESS_TYPE_2);
            }
            break;
            case LichengConstants.TASK_TYPE_8: {
                //明日任务
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 1);
                Date time = cal.getTime();
                iots = appOrderTaskRepository.listTaskType8(sdf1.format(time));
            }
            break;
        }
        return iots;
    }




}
