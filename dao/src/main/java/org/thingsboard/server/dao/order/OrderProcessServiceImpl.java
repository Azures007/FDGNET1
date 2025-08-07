package org.thingsboard.server.dao.order;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.mes.*;
import org.thingsboard.server.common.data.mes.bus.TBusOrderHead;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcess;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcessHistory;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcessRecord;
import org.thingsboard.server.common.data.mes.sys.TSysClass;
import org.thingsboard.server.common.data.mes.sys.TSysMessageOrder;
import org.thingsboard.server.common.data.mes.sys.TSysPersonnelInfo;
import org.thingsboard.server.common.data.mes.sys.TSysProcessInfo;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.message.MessageService;
import org.thingsboard.server.dao.orderProcess.OrderProcessRecordService;
import org.thingsboard.server.dao.sql.mes.TSysProcessInfo.TSysProcessInfoRepository;
import org.thingsboard.server.dao.sql.mes.order.OrderHeadRepository;
import org.thingsboard.server.dao.sql.mes.order.OrderProcessRepository;
import org.thingsboard.server.dao.tSysClass.TSysClassService;
import org.thingsboard.server.dao.tSysPersonnelInfo.TSysPersonnelInfoService;

import java.sql.Timestamp;
import java.util.*;

@Service
@Slf4j
public class OrderProcessServiceImpl implements OrderProcessService {
    @Autowired
    OrderProcessRepository orderProcessRepository;

    @Autowired
    OrderHeadRepository orderHeadRepository;

    @Autowired
    TSysProcessInfoRepository processInfoRepository;

    @Autowired
    MessageService messageService;

    @Autowired
    OrderHeadService orderHeadService;

    @Autowired
    TSysPersonnelInfoService tSysPersonnelInfoService;

    @Autowired
    OrderProcessService orderProcessService;

    @Autowired
    OrderProcessRecordService orderProcessRecordService;

    @Autowired
    OrderProcessHistoryService orderProcessHistoryService;

    @Autowired
    TSysClassService tSysClassService;

    @Override
    public TBusOrderProcess getProcessByOrderId(Integer orderId, Integer processid) {
        try {
//            List<Object[]> select = orderProcessRepository.getProcessByOrderId(orderId, processid);
            List<Map> select = orderProcessRepository.getProcessByOrderId(orderId, processid);
//            List<TBusOrderProcess> tBusOrderProcessList = EntityUtils.castEntity(select, TBusOrderProcess.class, new TBusOrderProcess());
            List<TBusOrderProcess> tBusOrderProcessList = JSON.parseArray(JSON.toJSONString(select),TBusOrderProcess.class);
            TBusOrderProcess tBusOrderProcess = tBusOrderProcessList.get(0);
            return tBusOrderProcess;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public TBusOrderProcess getProcessPrevByOrderId(Integer orderId, Integer orderProcessId) {
        try {
            TBusOrderProcess tBusOrderProcess = orderProcessRepository.findById(orderProcessId).orElse(null);
            TBusOrderHead orderHead = orderHeadRepository.findById(orderId).orElse(null);
            var tBusOrderProcessset = orderHead.getTBusOrderProcessSet();
            tBusOrderProcessset.stream().sorted(Comparator.comparing(TBusOrderProcess::getProcessSeq).reversed());
            TBusOrderProcess tBusOrderProcessPrev = null;
            for (var orderProcess : tBusOrderProcessset) {
                if (orderProcess.getProcessSeq() >= tBusOrderProcess.getProcessSeq()) {
                    continue;
                } else {
                    tBusOrderProcessPrev = orderProcess;
                    break;
                }
            }
            return tBusOrderProcessPrev;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void saveTBusOrderProcess(TBusOrderProcess tBusOrderProcess) {
        if (tBusOrderProcess.getClassId() != null) {
            orderProcessRepository.saveAndFlush(tBusOrderProcess);
        }

    }

    @Override
    public TBusOrderProcess findById(Integer orderProcessId) {
        Optional<TBusOrderProcess> byId = orderProcessRepository.findById(orderProcessId);
        return byId.get();
    }

    @Transactional
    @Override
    public void handOver(TSysPersonnelInfo tSysPersonnelInfo, TSysClass tSysClass, TBusOrderProcess handOverOrderProcess, Integer orderId, String userId) {
        //复制一条数据
        TBusOrderProcess newOrderProcess = new TBusOrderProcess();
        //类型字段更新为移交订单 2022-09-07 取消移交前的记录为移交
//        handOverOrderProcess.setType(LichengConstants.ORDER_PROCESS_TYPE_2);
        BeanUtils.copyProperties(handOverOrderProcess, newOrderProcess);
        // 判断盘点记录是否为中途完工盘点，是则不更新 2022-09-14 任务14384
        List<TBusOrderProcessRecord> orderProcessRecordList = orderProcessRecordService.getOrderProcessRecord(handOverOrderProcess.getOrderProcessId(), LichengConstants.ORDER_BUS_TYPE_PD);
        if (orderProcessRecordList.size() > 0 && orderProcessRecordList.get(0).getRecordTypePd().equals(LichengConstants.STOCKTAKING0003)) {
            // 不需要更新工序状态
        } else {
            //保存原工序状态
            handOverOrderProcess.setOldProcessStatus(handOverOrderProcess.getProcessStatus());
            //将旧值的工序状态变更为移交中
            handOverOrderProcess.setProcessStatus(LichengConstants.PROCESSSTATUS_4);
        }
        List<TBusOrderProcessHistory> orderProcessHistoryList = orderProcessHistoryService.getPdOrderProcessRecords(handOverOrderProcess.getOrderProcessId());
        if (orderProcessHistoryList.size() > 0) {
            orderProcessHistoryList.sort(Comparator.comparing(TBusOrderProcessHistory::getReportTime).reversed());
            // 2022-09-22 获取移交前的盘点类型和盘点时间
            newOrderProcess.setOldRecordTypePd(orderProcessHistoryList.get(0).getRecordTypePd());
            newOrderProcess.setOldRecordTypePdTime(orderProcessHistoryList.get(0).getReportTime());
        }
        //增加移交人与移交时间
        TSysPersonnelInfo handOvertSysPersonnelInfo = tSysPersonnelInfoService.getPersonnelInfoByUserId(userId);
        handOverOrderProcess.setHandOverPerSonId(handOvertSysPersonnelInfo);
        handOverOrderProcess.setHandOverTime(new Timestamp(new Date().getTime()));
        //开始处理移交后的记录
        //类型字段更新为移交订单
        //增加移交前的移交人与移交时间
        newOrderProcess.setOldHandOverPerSonId(handOvertSysPersonnelInfo);
        newOrderProcess.setOldHandOverTime(handOverOrderProcess.getHandOverTime());
        newOrderProcess.setType(LichengConstants.ORDER_PROCESS_TYPE_2);
        //新工序数据状态置为未开工
        newOrderProcess.setProcessStatus(LichengConstants.PROCESSSTATUS_0);
        //在新值中去除ID，工序编码不变，班别编码和处理人编码变更为接受班别编码和处理人（班长）,工序状态为未开工，类型更新为移交订单
        newOrderProcess.setOrderProcessId(null);
        newOrderProcess.setClassId(tSysClass);
        newOrderProcess.setPersonId(tSysPersonnelInfo);
        newOrderProcess.setOldOrderProcessId(handOverOrderProcess.getOrderProcessId());
        orderProcessRepository.saveAndFlush(handOverOrderProcess);
        orderProcessRepository.saveAndFlush(newOrderProcess);
        //保存订单的工序执行表关联
        var orderHead = orderHeadService.getTBusOrderHead(orderId);
        var orderProcessSet = orderHead.getTBusOrderProcessSet();
        orderProcessSet.add(newOrderProcess);
        orderHeadService.saveTBusOrderHead(orderHead);
        chatMess(handOverOrderProcess, newOrderProcess);
    }

    @Override
    public List<Map> listProcess() {
        List<Map>  maps=orderProcessRepository.listProcess();
        return maps;
    }

    @Transactional
    @Override
    public ResponseResult handOver(Integer orderId, Integer orderProcessId, Integer classId, String handOverUserId, String userId) {
        TBusOrderHead tBusOrderHead = orderHeadService.getTBusOrderHead(orderId);
        if (LichengConstants.ORDERSTATUS_4.equals(tBusOrderHead.getOrderStatus())) {
            throw new RuntimeException("订单已挂起，禁止操作。");
        }
        //根据用户ID获取当前用户绑定的的人员信息
        TSysPersonnelInfo tSysPersonnelInfo = tSysPersonnelInfoService.getPersonnelInfoByUserId(userId);
        if(tSysPersonnelInfo==null){
            return ResultUtil.error("获取当前用户绑定的人员信息失败，请确认是否已成功绑定");
        }
        //获取移交的工序
        TBusOrderProcess handOverOrderProcess = orderProcessService.findById(orderProcessId);
        if(handOverOrderProcess==null){
            return ResultUtil.error("未查询到移交工序信息，请联系管理员");
        }
        // 获取移交目标的班组信息（根据）
        TSysClass tSysClass = tSysClassService.getClassById(classId);
        if(tSysClass==null){
            return ResultUtil.error("未查询到移交的班组信息，请联系管理员");
        }

        //复制一条数据
        TBusOrderProcess newOrderProcess = new TBusOrderProcess();
        //类型字段更新为移交订单 2022-09-07 取消移交前的记录为移交
//        handOverOrderProcess.setType(LichengConstants.ORDER_PROCESS_TYPE_2);
        BeanUtils.copyProperties(handOverOrderProcess, newOrderProcess);
        //增加移交人与移交时间
        TSysPersonnelInfo handOvertSysPersonnelInfo = tSysPersonnelInfoService.getPersonnelInfoByUserId(userId);
        handOverOrderProcess.setHandOverPerSonId(handOvertSysPersonnelInfo);
        handOverOrderProcess.setHandOverTime(new Timestamp(new Date().getTime()));
        //开始处理移交后的记录
        //类型字段更新为移交订单
        newOrderProcess.setType(LichengConstants.ORDER_PROCESS_TYPE_2);
        //新工序数据状态置为未开工
        newOrderProcess.setProcessStatus("0");
        //在新值中去除ID，工序编码不变，班别编码和处理人编码变更为接受班别编码和处理人（班长）,工序状态为未开工，类型更新为移交订单
        newOrderProcess.setOrderProcessId(null);
        newOrderProcess.setClassId(tSysClass);
        newOrderProcess.setPersonId(tSysPersonnelInfo);
        newOrderProcess.setOldOrderProcessId(handOverOrderProcess.getOrderProcessId());
        orderProcessRepository.saveAndFlush(handOverOrderProcess);
        orderProcessRepository.saveAndFlush(newOrderProcess);
        //保存订单的工序执行表关联
        var orderHead = orderHeadService.getTBusOrderHead(orderId);
        var orderProcessSet = orderHead.getTBusOrderProcessSet();
        orderProcessSet.add(newOrderProcess);
//        orderHeadService.saveTBusOrderHead(orderHead);
        orderHeadRepository.save(orderHead);
        chatMess(handOverOrderProcess, newOrderProcess);
        if(orderHead!=null){
            throw new RuntimeException("");
        }
        return ResultUtil.success();
    }

    /**
     * 发送消息
     *
     * @param handOverOrderProcess
     * @param newOrderProcess
     */
    private void chatMess(TBusOrderProcess handOverOrderProcess, TBusOrderProcess newOrderProcess) {
        String orderNo = handOverOrderProcess.getOrderNo();
        TBusOrderHead tBusOrderHead = orderHeadRepository.getByOrderNo(orderNo);
        if (tBusOrderHead == null) {
            throw new RuntimeException("获取的订单不存在");
        }
        TSysMessageOrder tSysMessageOrder = new TSysMessageOrder();
        String userId = newOrderProcess.getPersonId().getUserId();
        tSysMessageOrder.setUserId(userId);
        tSysMessageOrder.setMesTime(new Date());
        tSysMessageOrder.setOrderId(tBusOrderHead.getOrderId());
        tSysMessageOrder.setOrderNo(orderNo);
        tSysMessageOrder.setOrderType(0);
        tSysMessageOrder.setCreatedTime(tBusOrderHead.getBillDate());
        tSysMessageOrder.setProductStandard(tBusOrderHead.getBodyMaterialName());
        tSysMessageOrder.setBillPlanQty(String.valueOf(tBusOrderHead.getBodyPlanPrdQty()));
        tSysMessageOrder.setMesType("0");
        tSysMessageOrder.setStatusType("0");
        tSysMessageOrder.setExecuteProcessStatus(newOrderProcess.getProcessStatus());
        tSysMessageOrder.setOrderProcessId(newOrderProcess.getOrderProcessId());
        //移交信息
        List<BotMessageVo> botMessageVos = new ArrayList<>();
        botMessageVos.add(getBotMessageVo(handOverOrderProcess));
        botMessageVos.add(getBotMessageVo(newOrderProcess));
        tSysMessageOrder.setOrderProcessId(handOverOrderProcess.getOrderProcessId().intValue());
        tSysMessageOrder.setRemark("");
        tSysMessageOrder.setBotMessage(JSON.toJSONString(botMessageVos));
        log.info("--------------------发送消息：");
        log.info(tSysMessageOrder.toString());
        messageService.chatMessage(userId, tSysMessageOrder);
    }

    //获取移交信息
    private BotMessageVo getBotMessageVo(TBusOrderProcess tBusOrderProcess) {
        BotMessageVo botMessageVo = new BotMessageVo();
        TSysProcessInfo processId = tBusOrderProcess.getProcessId();
        botMessageVo.setProcessName(processId.getProcessName());
        botMessageVo.setName(tBusOrderProcess.getPersonId() == null ? "" : tBusOrderProcess.getPersonId().getName());
        return botMessageVo;
    }
}
