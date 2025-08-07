package org.thingsboard.server.dao.mes.order;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.mes.*;
import org.thingsboard.server.common.data.mes.bus.TBusOrderHead;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcess;
import org.thingsboard.server.common.data.mes.sys.*;
import org.thingsboard.server.dao.mes.TSysCraftinfo.TSysCraftInfoService;
import org.thingsboard.server.dao.mes.TSysProcessInfo.TSysProcessInfoService;
import org.thingsboard.server.dao.mes.dto.OrderChangeClassSaveDto;
import org.thingsboard.server.dao.mes.dto.OrderStartOrderSaveDto;
import org.thingsboard.server.dao.mes.message.MessageService;
import org.thingsboard.server.dao.sql.mes.TSysCraftInfo.TSysCraftInfoRepository;
import org.thingsboard.server.dao.sql.mes.TSysCraftInfo.TSysCraftMaterialRelRepository;
import org.thingsboard.server.dao.sql.mes.TSysCraftInfo.TSysCraftProcessRelRepository;
import org.thingsboard.server.dao.sql.mes.TSysProcessInfo.TSysProcessClassRelRepository;
import org.thingsboard.server.dao.sql.mes.TSysProcessInfo.TSysProcessInfoRepository;
import org.thingsboard.server.dao.sql.mes.order.OrderHeadRepository;
import org.thingsboard.server.dao.sql.mes.order.OrderProcessRepository;
import org.thingsboard.server.dao.sql.mes.sync.SyncMaterialRepository;
import org.thingsboard.server.dao.sql.mes.tSysClass.ClassGroupLeaderRepository;
import org.thingsboard.server.dao.sql.mes.tSysClass.TSysClassRepository;
import org.thingsboard.server.dao.mes.tSysClass.TSysClassService;
import org.thingsboard.server.dao.mes.tSysCodeDsc.TSysCodeDscService;
import org.thingsboard.server.dao.mes.tSysPersonnelInfo.TSysPersonnelInfoService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderBackendServiceImpl implements OrderBackendService {
    @Autowired
    OrderHeadService orderHeadService;

    @Autowired
    OrderHeadRepository orderHeadRepository;

    @Autowired
    TSysCodeDscService sysCodeDscService;

    @Autowired
    OrderProcessRepository orderProcessRepository;

    @Autowired
    TSysClassRepository classRepository;

    @Autowired
    TSysClassService classService;

    @Autowired
    TSysCraftInfoRepository craftInfoRepository;

    @Autowired
    TSysCraftInfoService craftInfoService;

    @Autowired
    TSysCraftProcessRelRepository craftProcessRelRepository;

    @Autowired
    TSysProcessInfoRepository processInfoRepository;

    @Autowired
    TSysProcessInfoService processInfoService;

    @Autowired
    TSysProcessClassRelRepository processClassRelRepository;

    @Autowired
    ClassGroupLeaderRepository classGroupLeaderRepository;

    @Autowired
    MessageService messageService;

    @Autowired
    TSysPersonnelInfoService personnelInfoService;

    @Autowired
    SyncMaterialRepository syncMaterialRepository;

    @Autowired
    TSysCraftMaterialRelRepository craftMaterialRelRepository;

    @Async
    @Override
    @Transactional
    public void setOrderHeadOrderMatching(Integer orderId,String orderMatching){
        TBusOrderHead tBusOrderHeadRt = orderHeadRepository.findById(orderId).orElse(null);
        tBusOrderHeadRt.setOrderMatching(orderMatching);
        orderHeadRepository.save(tBusOrderHeadRt);
    }
    @Transactional
    @Override
    public void startOrder(Integer orderId, Integer craftId, String craftDesc) throws Exception {
        TBusOrderHead tBusOrderHeadRt = orderHeadRepository.findById(orderId).orElse(null);
        if (null == tBusOrderHeadRt) {
            throw new RuntimeException("订单不存在");
        }
        if (tBusOrderHeadRt.getCraftId() != null) {
            throw new RuntimeException("工艺路线已经有值，不要重复接单");
        }
        boolean isSuccess = true;
        try {
            TSysCraftInfo tSysCraftInfo = craftInfoService.getAndCheck(craftId);
            tBusOrderHeadRt.setCraftId(tSysCraftInfo);
            tBusOrderHeadRt.setCraftDesc(craftDesc);
            //通过工艺路线获取工艺工序关系表
            List<TSysCraftProcessRel> tSysCraftProcessRelList = craftProcessRelRepository.findByCraftId(tSysCraftInfo.getCraftId(), Sort.by(Sort.Direction.ASC, "sort"));
//        List<TSysClass> sysClassList = new ArrayList<>();
            TSysClass startOrderClass = null;//订单的接单班别
            if (tSysCraftProcessRelList.size() <= 0) {
                throw new RuntimeException("通过工艺路线没有获取工艺工序关系表的数据");
            } else {
                Set<TBusOrderProcess> tBusOrderProcessSet = new HashSet<>();
                for (int i = 0; i < tSysCraftProcessRelList.size(); i++) {
                    var tSysCraftProcessRel=tSysCraftProcessRelList.get(i);
                    //获取工序
                    TSysProcessInfo processInfo = processInfoService.getAndCheck(tSysCraftProcessRel.getProcessId());
                    //通过工序获取工序设置表
                    List<TSysProcessClassRel> processClassRelList = processClassRelRepository.findByProcessId(processInfo.getProcessId());
                    if (processClassRelList.size() <= 0) {
                        throw new RuntimeException("通过工序没有获取工序设置班别的数据");
                    } else {
                        for (var processClassRel : processClassRelList) {
                            TSysClass classById = classService.getClassById(processClassRel.getClassId());
                            // 匹配订单的班组：通过班别中的ERP班组来匹配；如果没有匹配到则还是用原来的方式匹配
                            if (StringUtils.isNotEmpty(tBusOrderHeadRt.getMidMoEntryTeamNumber())
                                    && StringUtils.isNotEmpty(classById.getClassTeamNumber())
                                    && tBusOrderHeadRt.getMidMoEntryTeamNumber().equals(classById.getClassTeamNumber())) {
                                // 1.通过班别中的ERP班组来匹配
                                startOrderClass = classById;
                                break;
                            }
                            if (null == startOrderClass) {
                                // 2.按当前时间获取早晚班
                                var sysClassC = classService.getAndCheckByScheduling(processClassRel.getClassId());
                                if (null != sysClassC) {
                                    startOrderClass = sysClassC;
                                    break;
                                }
                            }
                        }
                    }
                    if (null == startOrderClass) {
                        throw new RuntimeException("接单开工发生异常：第一道工序没有匹配到合适的班别，请检查下班别和排班信息");
                    } else {
                        //设置匹配工艺路线: -1: 不匹配, 1:匹配
//                    setOrderHeadOrderMatching(orderId, tSysCraftProcessRelList.size() <= 0 ? LichengConstants.ORDER_HEAD_MATCHING__1 : LichengConstants.ORDER_HEAD_MATCHING_1);
//                    tBusOrderHeadRt.setOrderMatching(tSysCraftProcessRelList.size() <= 0 ? LichengConstants.ORDER_HEAD_MATCHING__1 : LichengConstants.ORDER_HEAD_MATCHING_1);
                        if(i==0) {
                            //赋值订单表头的当前工序，处理班别
                            tBusOrderHeadRt.setClassId(startOrderClass);
                            tBusOrderHeadRt.setCurrentProcess(processInfo);
                        }
                        //选择工艺路线后，默认生成首道工序
                        TBusOrderProcess tBusOrderProcess = new TBusOrderProcess();
                        tBusOrderProcess.setOrderNo(tBusOrderHeadRt.getOrderNo());
//                    tBusOrderProcess.setPersonId(personnelInfo);
                        tBusOrderProcess.setProcessId(processInfo);
                        tBusOrderProcess.setProcessSeq(tSysCraftProcessRel.getSort());//工序执行序号，是否取值tSysCraftProcessRel的排序序号
                        tBusOrderProcess.setClassId(startOrderClass);//班别
                        tBusOrderProcessSet.add(tBusOrderProcess);

                        if (tBusOrderProcessSet.size() <= 0){
                            throw new RuntimeException("接单开工发生异常：生成第一道工序的工序执行表发生异常，请检查下班别、排版和组长信息");
                        }
                    }
                }
                var tBusOrderProcessSetRt = orderProcessRepository.saveAll(tBusOrderProcessSet);
                tBusOrderHeadRt.setTBusOrderProcessSet(tBusOrderProcessSetRt.stream().collect(Collectors.toSet()));
                //tBusOrderHeadRt.setOrderStatus("1");//已开工
                tBusOrderHeadRt.setOrderMatching(LichengConstants.ORDER_HEAD_MATCHING_1);//匹配
                orderHeadRepository.save(tBusOrderHeadRt);
            }
        } catch (RuntimeException e1) {
            isSuccess = false;
            throw e1;
        } finally {
            /*if (isSuccess) {
                String billNo = tBusOrderHeadRt.getBillNo();
                long fentryid = tBusOrderHeadRt.getErpMoEntryId();
                long start = System.currentTimeMillis();
                //post数据
                String toStartJson = "{\"billNo\":\"" + billNo + "\",\"fentryid\":\"" + fentryid + "\"}";
                log.info("请求报文力诚 post json data finish!,url:http://192.168.100.36:6801/midMoApi/toStart,数据:" + toStartJson);
                LcToStartDTO lcToStartDTO = new LcToStartDTO();
                lcToStartDTO.setBillNo(billNo);
                lcToStartDTO.setEntryId(fentryid);
                LcToStartVo st = LcERPCallBack.toStart(lcToStartDTO);
                log.info("响应报文力诚 post json data finish!,url:http://192.168.100.36:6801/midMoApi/toStart,耗时:"
                        + (System.currentTimeMillis() - start) + "ms,数据:" + st.getMessage());
                if (!st.isSuccess() && !st.getMessage().contains("该工序已开工")) {
                    throw new RuntimeException("接单开工发生异常：通知ERP的生产订单开工，执行力城api开工接口返回失败，请检查下ERP生产订单:" + st.getMessage());
                }
            } else {
                // 如果发生异常，更新为不匹配工艺路线
                setOrderHeadOrderMatching(orderId, LichengConstants.ORDER_HEAD_MATCHING__1);
            }*/
        }

    }

    //@Transactional
    @Override
    public void startOrderBatch(List<OrderStartOrderSaveDto> saveDtoList) throws Exception {
        for (OrderStartOrderSaveDto saveDto:saveDtoList) {
            TSysCraftInfo craft = this.getCraftInfoByMaterial(saveDto.getBodyMaterialNumber());
            this.startOrder(saveDto.getOrderId(),craft.getCraftId(),craft.getCraftDetail());
        }
    }

    @Override
    public List<TSysClass> getOrderClassInfo(Integer orderId) {
        List<TSysClass> sysClassList = null;
        TBusOrderHead tBusOrderHeadRt = orderHeadRepository.findById(orderId).orElse(null);
        if (null == tBusOrderHeadRt) {
            throw new RuntimeException("订单不存在");
        }
        TSysProcessInfo processInfo = tBusOrderHeadRt.getCurrentProcess();
        //通过工序获取工序设置表
        List<TSysProcessClassRel> processClassRelList = processClassRelRepository.findByProcessId(processInfo.getProcessId());
        sysClassList = new ArrayList<>();
        for (TSysProcessClassRel processClassRel : processClassRelList) {
            TSysClass sysClass = classService.getClassById(processClassRel.getClassId());
            sysClassList.add(sysClass);
        }
        return sysClassList;
    }

    /***
     *
     * @Author huanghuihuang
     * @Description 点击提交后，校验当订单状态“已开工”且首个工序“未开工”，否则提示“班别已经接单开工，不允许变更班别。”，校验通过后，工序执行表更新处理班别，弹层提示“提交成功！”
     * @Date 2022/10/20 10:02
     * @param saveDto
     * @return
     **/
    @Transactional
    @Override
    public void changeOrderClass(OrderChangeClassSaveDto saveDto) {
        log.info("订单变更提交，提交参数：orderId：" + saveDto.getOrderId() + "，classId：" + saveDto.getClassId());
        TBusOrderHead tBusOrderHeadRt = orderHeadRepository.findById(saveDto.getOrderId()).orElse(null);
        if (null == tBusOrderHeadRt) {
            throw new RuntimeException("订单不存在");
        }
        //校验当订单状态“已开工”且首个工序“未开工”，否则提示“班别已经接单开工，不允许变更班别。”
        if (!LichengConstants.ORDERSTATUS_1.equals(tBusOrderHeadRt.getOrderStatus())) {
            throw new RuntimeException("订单未开工");
        }
        var orderProcessSet = tBusOrderHeadRt.getTBusOrderProcessSet();
        if (orderProcessSet.size() > 1) {
            throw new RuntimeException("订单包含了多个工序执行表记录");
        }
        if (tBusOrderHeadRt.getClassId() != null && tBusOrderHeadRt.getClassId().getClassId().intValue() == saveDto.getClassId().intValue()) {
            throw new RuntimeException("与订单当前的班别相同，不允许变更班别。");
        }
        var orderProcess = orderProcessSet.iterator().next();
        if (!LichengConstants.PROCESSSTATUS_0.equals(orderProcess.getProcessStatus())) {
            throw new RuntimeException("班别已经接单开工，不允许变更班别。");
        }
        var classInfo = classService.getClassById(saveDto.getClassId());
        orderProcess.setClassId(classInfo);
//        var tBusOrderProcessSetRt = orderProcessRepository.saveAll(tBusOrderProcessSet);
        tBusOrderHeadRt.setTBusOrderProcessSet(orderProcessSet);
        tBusOrderHeadRt.setClassId(classInfo);
        orderHeadRepository.save(tBusOrderHeadRt);
    }
    @Override
    public TSysCraftInfo getCraftInfoByMaterial(String materialNumber) {
        //获取物料
        var materialList = syncMaterialRepository.getByMaterialCode(materialNumber);
        if (materialList.size() == 0) {
            throw new RuntimeException("不存在的物料编码，请在MES更新主产品物料信息");
        }
        var material = materialList.get(0);
        if ("0".equals(material.getMaterialStatus())) {
            throw new RuntimeException("物料已被禁用，请在MES变更物料状态");
        }
        //通过物料获取工艺路线
        List<TSysCraftMaterialRel> materialRels = craftMaterialRelRepository.findByMaterialCode(materialNumber);
        if(materialRels.size()==0){
            //设置匹配工艺路线: -1: 不匹配, 1:匹配
//            setOrderHeadOrderMatching(orderId,  LichengConstants.ORDER_HEAD_MATCHING__1 );
            throw new RuntimeException("物料未绑定工艺路线，请维护物料工艺路线");
        }
        TSysCraftInfo craft = craftInfoRepository.findById(materialRels.get(0).getCraftId()).get();
        if ("0".equals(craft.getEnabled())) {
            //设置匹配工艺路线: -1: 不匹配, 1:匹配
//            setOrderHeadOrderMatching(orderId,  LichengConstants.ORDER_HEAD_MATCHING__1 );
            throw new RuntimeException("工艺路线已被禁用，请在MES变更物料对应工艺路线");
        }
        return craft;
    }


}
