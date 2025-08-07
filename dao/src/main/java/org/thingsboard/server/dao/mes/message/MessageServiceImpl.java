package org.thingsboard.server.dao.mes.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.common.util.BigDecimalUtil;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.mes.*;
import org.thingsboard.server.common.data.mes.bus.TBusOrderHead;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcess;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcessRecord;
import org.thingsboard.server.common.data.mes.sys.TSysCodeDsc;
import org.thingsboard.server.common.data.mes.sys.TSysMessageOrder;
import org.thingsboard.server.dao.constant.GlobalConstant;
import org.thingsboard.server.dao.mes.message.MessageService;
import org.thingsboard.server.dao.mes.vo.MessageSizeVo;
import org.thingsboard.server.dao.mes.vo.PageVo;
import org.thingsboard.server.dao.mes.vo.ProcessAlarmVo;
import org.thingsboard.server.dao.mes.vo.ProcessEndVo;
import org.thingsboard.server.dao.sql.mes.message.MessageOrderRepository;
import org.thingsboard.server.dao.sql.mes.order.OrderHeadRepository;
import org.thingsboard.server.dao.sql.mes.order.OrderProcessRecordRepository;
import org.thingsboard.server.dao.sql.mes.order.OrderProcessRepository;
import org.thingsboard.server.dao.sql.mes.tSysCodeDsc.TSysCodeDscRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    MessageOrderRepository messageOrderRepository;

    @Autowired
    OrderHeadRepository orderHeadRepository;

    @Autowired
    TSysCodeDscRepository tSysCodeDscRepository;

    @Autowired
    OrderProcessRecordRepository orderProcessRecordRepository;

    @Autowired
    OrderProcessRepository orderProcessRepository;

    @Transactional
    @Override
    public PageVo<TSysMessageOrder> listMessage(Integer current, Integer size, String userId) throws Exception {
        Sort sort = Sort.by(Sort.Direction.DESC, "mesTime");
        PageRequest page = PageRequest.of(current, size, sort);
        TSysMessageOrder tSysMessageOrder = new TSysMessageOrder();
        tSysMessageOrder.setUserId(userId);
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withMatcher("userId", ExampleMatcher.GenericPropertyMatchers.exact());//匹配
        Example<TSysMessageOrder> tSysMessageOrderExample = Example.of(tSysMessageOrder, matcher);
        Page<TSysMessageOrder> messageOrders = messageOrderRepository.findAll(tSysMessageOrderExample, page);
        PageVo<TSysMessageOrder> tSysMessageOrderPageVo = new PageVo<>(messageOrders);

        List<TSysMessageOrder> messageOrderList = messageOrders.getContent();
        for (TSysMessageOrder messageOrder : messageOrderList) {
            messageOrder.setBotMessageVos(StringUtils.isBlank(messageOrder.getBotMessage()) ? null :
                    JSON.parseArray(messageOrder.getBotMessage(), BotMessageVo.class));
            messageOrder.setIsRead("1");
        }
        messageOrderRepository.saveAll(messageOrderList);
        return tSysMessageOrderPageVo;
    }


    @Override
    public MessageSizeVo getMessageSizeVo(String userId) {
        MessageSizeVo messageSizeVo = new MessageSizeVo();
        Sort sort = Sort.by(Sort.Direction.DESC, "createdTime");
        TSysMessageOrder tSysMessageOrder = new TSysMessageOrder();
        tSysMessageOrder.setUserId(userId);
        tSysMessageOrder.setIsRead("0");
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withMatcher("userId", ExampleMatcher.GenericPropertyMatchers.exact())//匹配
                .withMatcher("isRead", ExampleMatcher.GenericPropertyMatchers.exact());//匹配
        Example<TSysMessageOrder> tSysMessageOrderExample = Example.of(tSysMessageOrder, matcher);
        List<TSysMessageOrder> tSysMessageOrders = messageOrderRepository.findAll(tSysMessageOrderExample, sort);
        tSysMessageOrders.stream().forEach(tSysMessageOrder1 -> {
            tSysMessageOrder1.setBotMessageVos(StringUtils.isBlank(tSysMessageOrder1.getBotMessage()) ? null :
                    JSON.parseArray(tSysMessageOrder1.getBotMessage(), BotMessageVo.class));
        });
        messageSizeVo.setMegSize(tSysMessageOrders.size());
        messageSizeVo.setTSysMessageOrders(tSysMessageOrders);
        return messageSizeVo;
    }

    @Override
    public void chatMessage(String userId, TSysMessageOrder tSysMessageOrder) {
        tSysMessageOrder.setIsRead("0");
        tSysMessageOrder.setMesTime(new Date());
        messageOrderRepository.save(tSysMessageOrder);
    }

    @Override
    public List<ProcessEndVo> getProcessEndMessage(String userId) {
        List<String> processStatusList = new ArrayList<>();
        //工序状态 已开工
        processStatusList.add(LichengConstants.PROCESSSTATUS_1);
        //工序状态 暂停
        processStatusList.add(LichengConstants.PROCESSSTATUS_2);
        List<TSysCodeDsc> codeDscList = tSysCodeDscRepository.findByCodeClIdAndCodeDscAndEnabledSt(LichengConstants.CODE_DSC_PROCESSENDMESSAGE, LichengConstants.CODE_DSC_PROCESSENDMESSAGE_1, LichengConstants.CODE_DSC_ENABLEDST_1);
        if (codeDscList.size() <= 0) {
            return null;
        }
        for (TSysCodeDsc tSysCodeDsc : codeDscList) {
            List<Map> processEndVoList = orderHeadRepository.getProcessEndByPersonIdAndProcessStatusAndOrderProcessTypeAndreceiveTime(userId, processStatusList, LichengConstants.ORDER_PROCESS_TYPE_1, Integer.valueOf(tSysCodeDsc.getCodeValue()));
            List<ProcessEndVo> processEndVos = JSON.parseArray(JSON.toJSONString(processEndVoList), ProcessEndVo.class);
            CopyOnWriteArrayList<ProcessEndVo> cowList = new CopyOnWriteArrayList<ProcessEndVo>(processEndVos);
            //取出所有redis中用户跳过校验的所有数据
            List<String> range = redisTemplate.opsForList().range(userId + "setProcessEndMessage", 0, -1);
            for (String s : range) {
                for (ProcessEndVo processEndVo : processEndVos) {
                    if (s.equals(processEndVo.getOrderNo())) {
                        cowList.remove(processEndVo);
                    }
                }
            }
            return cowList;
        }
        return null;
    }

    @Override
    public void setProcessEndMessage(String userId, String orderNo) {
        redisTemplate.opsForList().leftPush(userId + "setProcessEndMessage",orderNo.toString());
        //设置缓存数据到期失效时间
        redisTemplate.expire(userId + "setProcessEndMessage",getRemainSecondsOneDay(new Date()),TimeUnit.SECONDS);
    }

    private static Long getRemainSecondsOneDay(Date currentDate) {
        //使用plusDays加传入的时间加10天，将时分秒设置成0
        LocalDateTime midnight = LocalDateTime.ofInstant(currentDate.toInstant(),
                        ZoneId.systemDefault()).plusDays(10).withHour(0).withMinute(0)
                .withSecond(0).withNano(0);
        LocalDateTime currentDateTime = LocalDateTime.ofInstant(currentDate.toInstant(),
                ZoneId.systemDefault());
        //使用ChronoUnit.SECONDS.between方法，传入两个LocalDateTime对象即可得到相差的秒数
        long seconds = ChronoUnit.SECONDS.between(currentDateTime, midnight);
        return  seconds;
    }

    @Override
    public List<ProcessAlarmVo> alarmProcessStartAndEnd(String orderNo, Integer orderProcessId, String userId) {
        Integer interval=getInterval();
        if(StringUtils.isNotEmpty(orderNo)&&orderProcessId!=null){
            //工序结束警告
            List<TBusOrderProcessRecord> orderProcessRecordList = orderProcessRecordRepository.findAllByOrderProcessIdAndBusType(orderProcessId, "BG");
            TBusOrderHead orderHead=orderHeadRepository.getByOrderNo(orderNo);
            ProcessAlarmVo alarmVo=new ProcessAlarmVo();
            List<ProcessAlarmVo> alarmVos=new ArrayList<>();
            //废膜
            Float wasteFilm=0f;
            String reportFmUnit="";
            //使用膜
            Float useFilm=0f;
            String reportUseUnit="";
            for (TBusOrderProcessRecord record : orderProcessRecordList) {
                if (record.getRecordTypeL2() == null) {
                } else if (record.getRecordTypeL2().equals("1")) {
                    wasteFilm += record.getRecordQty();
                    reportFmUnit=record.getRecordUnit();
                }
                if(LichengConstants.RECORDTYPEL20000_5.equals(record.getMaterialName())){
                    useFilm+=record.getRecordQty();
                    reportUseUnit=record.getRecordUnit();
                }
            };
            String reportFmUnitStr=GlobalConstant.getCodeDscName("UNIT0000", reportFmUnit);
            String reportUseUnitStr=GlobalConstant.getCodeDscName("UNIT0000", reportUseUnit);
            Float filmPerF=null;
            if(useFilm==0f){
                filmPerF=100f;
            }else {
                filmPerF=100f-BigDecimalUtil.format(wasteFilm / useFilm * 100, 2);
            }
            alarmVo.setBodyLot(orderHead.getBodyLot());
            alarmVo.setMaterialName(orderHead.getBodyMaterialName());
            alarmVo.setReportFmQty(wasteFilm);
            alarmVo.setReportFmQtyStr(reportFmUnitStr);
            alarmVo.setFilmUse(useFilm);
            alarmVo.setFilmUseStr(reportUseUnitStr);
            alarmVo.setFilmPer(filmPerF+"%");
            TBusOrderProcess process=orderProcessRepository.findById(orderProcessId).orElse(null);
            alarmVo.setType(process.getType());
            alarmVo.setOrderNo(orderNo);
            alarmVo.setOrderId(orderHead.getOrderId());
            alarmVo.setOrderProcessId(orderProcessId);
            alarmVo.setInterval(interval);
//            alarmVo.setToOrderProcessId(process.);
            int a=getIsController(filmPerF);
            alarmVo.setIsController(a);
            if(filmPerF==100f){
                alarmVo.setAlarmStr("以下订单膜利用率异常。");
                alarmVos.add(alarmVo);
            }else if(filmPerF<90f){
                alarmVo.setAlarmStr("以下订单膜利用率超出理论范围," +"\n"+"理论范围：90%-99%");
                alarmVos.add(alarmVo);
            }

            return alarmVos;
        }else {
            //开工警告
            //获取所有已开工，工序处于拉伸膜的工序
            List<ProcessAlarmVo> alarmVos=new ArrayList<>();
            List<Integer> orderProcessIds=orderProcessRecordRepository.getAllLsmOrderProcessId(userId);
            List<String> ids= redisTemplate.opsForList().range( "setProcessAlarmMessage", 0, -1);
            for (Integer orderprocessid : orderProcessIds) {
                //不再提醒
                if(ids.size()>0&&ids.contains(orderprocessid)){
                    continue;
                }
                ProcessAlarmVo alarmVo=new ProcessAlarmVo();
                List<TBusOrderProcessRecord> orderProcessRecordList = orderProcessRecordRepository.findAllByOrderProcessIdAndBusType(orderprocessid, "BG");
                if(orderProcessRecordList.size()==0){
                    continue;
                }
                TBusOrderHead orderHead=orderHeadRepository.getByOrderNo(orderProcessRecordList.get(0).getOrderNo());
                //废膜
                Float wasteFilm=0f;
                String reportFmUnit="";
                //使用膜
                Float useFilm=0f;
                String reportUseUnit="";
                for (TBusOrderProcessRecord record : orderProcessRecordList) {
                    if (record.getRecordTypeL2() == null) {
                    } else if (record.getRecordTypeL2().equals("1")) {
                        wasteFilm += record.getRecordQty();
                        reportFmUnit=record.getRecordUnit();
                    }
                    if(LichengConstants.RECORDTYPEL20000_5.equals(record.getMaterialName())){
                        useFilm+=record.getRecordQty();
                        reportUseUnit=record.getRecordUnit();
                    }
                };
                String reportFmUnitStr=GlobalConstant.getCodeDscName("UNIT0000", reportFmUnit);
                String reportUseUnitStr=GlobalConstant.getCodeDscName("UNIT0000", reportUseUnit);
                Float filmPerF=null;
                if(useFilm==0f){
                    filmPerF=100f;
                }else {
                    filmPerF=100f-BigDecimalUtil.format(wasteFilm / useFilm * 100, 2);
                }
                alarmVo.setBodyLot(orderHead.getBodyLot());
                alarmVo.setMaterialName(orderHead.getBodyMaterialName());
                alarmVo.setReportFmQty(wasteFilm);
                alarmVo.setReportFmQtyStr(reportFmUnitStr);
                alarmVo.setFilmUse(useFilm);
                alarmVo.setFilmUseStr(reportUseUnitStr);
                alarmVo.setFilmPer(filmPerF+"%");
                TBusOrderProcess process=orderProcessRepository.findById(orderprocessid).orElse(null);
                alarmVo.setType(process.getType());
                alarmVo.setOrderNo(orderHead.getOrderNo());
                alarmVo.setOrderId(orderHead.getOrderId());
                alarmVo.setOrderProcessId(orderprocessid);
                alarmVo.setInterval(interval);
//            alarmVo.setToOrderProcessId(process.);
                if(filmPerF==100f){
                    alarmVo.setAlarmStr("以下订单膜利用率异常。");
                    alarmVos.add(alarmVo);
                }else if(filmPerF<90f){
                    alarmVo.setAlarmStr("以下订单膜利用率超出理论范围,理论范围：90%-99%");
                    alarmVos.add(alarmVo);
                }
                if(alarmVos.size()>5){
                    return alarmVos;
                }
            }
            return alarmVos;
        }
    }

    private int getIsController(float per) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("codeClId", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("enabledSt", ExampleMatcher.GenericPropertyMatchers.exact());
        TSysCodeDsc tSysCodeDsc = new TSysCodeDsc();
        tSysCodeDsc.setCodeClId("PROCESSAlARM");
        tSysCodeDsc.setEnabledSt("1");
        Example<TSysCodeDsc> example = Example.of(tSysCodeDsc, matcher);
        List<TSysCodeDsc> codeDscs = tSysCodeDscRepository.findAll(example);
        for (TSysCodeDsc codeDsc : codeDscs) {
            if("工序结束告警".equals(codeDsc.getCodeDsc())){
                String codeValue=codeDsc.getCodeValue();
                JSONArray jsonArray = JSON.parseArray(codeValue);
                BigDecimal max100=null;
                BigDecimal max=null;
                BigDecimal min=null;
                Integer maxController=0;
                Integer minController=0;
                for (Object obj : jsonArray) {
                    JSONObject jsonObject= (JSONObject) obj;
                    if(jsonObject.containsKey("max100")){
                        max100=new BigDecimal(jsonObject.getString("max100"));
                        maxController=jsonObject.getInteger("isControl");
                    }else if(jsonObject.containsKey("max")){
                        max=new BigDecimal(jsonObject.getString("max"));
                        min=new BigDecimal(jsonObject.getString("min"));
                        minController=jsonObject.getInteger("isControl");
                    }

                }
                BigDecimal pers=new BigDecimal(per).divide(new BigDecimal(100));
                if(pers.compareTo(max100)==0){
                    return maxController;
                }
                if(!(pers.compareTo(min)==1&&pers.compareTo(max)==-1)){
                    return minController;
                }
            }
        }
        return 1;
    }

    private int getInterval(){
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("codeClId", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("enabledSt", ExampleMatcher.GenericPropertyMatchers.exact());
        TSysCodeDsc tSysCodeDsc = new TSysCodeDsc();
        tSysCodeDsc.setCodeClId("PROCESSAlARM");
        tSysCodeDsc.setEnabledSt("1");
        Example<TSysCodeDsc> example = Example.of(tSysCodeDsc, matcher);
        List<TSysCodeDsc> codeDscs = tSysCodeDscRepository.findAll(example);
        int interVal=0;
        for (TSysCodeDsc codeDsc : codeDscs) {
            String codeValue=codeDsc.getCodeValue();
            JSONArray jsonArray = JSON.parseArray(codeValue);
            for (Object obj : jsonArray) {
                JSONObject jsonObject= (JSONObject) obj;
                if(jsonObject.containsKey("Interval")){
                    interVal=Integer.valueOf(jsonObject.getString("Interval"));
                }

            }

        }
        return interVal*60;
    }

    @Override
    public void setProcessAlarmMessage(Integer orderProcessId) {
        redisTemplate.opsForList().leftPush("setProcessAlarmMessage",orderProcessId);
    }
}
