package org.thingsboard.server.dao.orderProcess;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.thingsboard.common.util.BigDecimalUtil;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.mes.*;
import org.thingsboard.server.common.data.mes.bus.*;
import org.thingsboard.server.common.data.mes.mid.MidMaterial;
import org.thingsboard.server.common.data.mes.sys.*;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.TSysCraftinfo.TSysCraftInfoService;
import org.thingsboard.server.dao.chargingbasket.ChargingBasketService;
import org.thingsboard.server.dao.constant.GlobalConstant;
import org.thingsboard.server.dao.dto.*;
import org.thingsboard.server.dao.order.*;
import org.thingsboard.server.dao.sql.mes.TSysCraftInfo.TSysCraftInfoRepository;
import org.thingsboard.server.dao.sql.mes.TSysCraftInfo.TSysCraftMaterialRelRepository;
import org.thingsboard.server.dao.sql.mes.TSysCraftInfo.TSysCraftProcessRelRepository;
import org.thingsboard.server.dao.sql.mes.TSysProcessInfo.TSysProcessClassRelRepository;
import org.thingsboard.server.dao.sql.mes.TSysProcessInfo.TSysProcessInfoRepository;
import org.thingsboard.server.dao.sql.mes.device.TSysDeviceIotHistoryRepository;
import org.thingsboard.server.dao.sql.mes.device.TSysDeviceIotRepository;
import org.thingsboard.server.dao.sql.mes.licheng.MidMaterialRepository;
import org.thingsboard.server.dao.sql.mes.order.*;
import org.thingsboard.server.dao.sql.mes.tSysClass.ClassGroupLeaderRepository;
import org.thingsboard.server.dao.sql.mes.tSysClass.TSysClassRepository;
import org.thingsboard.server.dao.sql.mes.tSysCodeDsc.TSysCodeDscRepository;
import org.thingsboard.server.dao.sql.mes.tSysDevice.TSysDeviceRepository;
import org.thingsboard.server.dao.sql.mes.tSysPersonnelInfo.ClassPersonnelRepository;
import org.thingsboard.server.dao.sql.mes.tSysPersonnelInfo.TSysPersonnelInfoRepository;
import org.thingsboard.server.dao.tSysClass.TSysClassService;
import org.thingsboard.server.dao.vo.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Auther: l
 * @Date: 2022/5/10 10:12
 * @Description:
 */
@Service
public class OrderProcessRecordServiceImpl implements OrderProcessRecordService {
    protected final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    TSysDeviceIotRepository tSysDeviceIotRepository;

    @Autowired
    TSysDeviceIotHistoryRepository tSysDeviceIotHistoryRepository;

    @Autowired
    OrderProcessPotRepository orderProcessPotRepository;

    @Autowired
    TSysCraftProcessRelRepository tSysCraftProcessRelRepository;

    @Autowired
    OrderBindCodeRepository orderBindCodeRepository;

    @Autowired
    TSysPersonnelInfoRepository tSysPersonnelInfoRepository;

    @Autowired
    ClassPersonnelRepository classPersonnelRepository;

    @Autowired
    TSysClassRepository tSysClassRepository;

    @Autowired
    OrderHeadRepository orderHeadRepository;

    @Autowired
    OrderProcessHistoryRepository orderProcessHistoryRepository;

    @Autowired
    OrderProcessHistoryService orderProcessHistoryService;

    @Autowired
    OrderProcessRecordRepository orderProcessRecordRepository;

    @Autowired
    OrderProcessRecordService orderProcessRecordService;

    @Autowired
    TSysProcessInfoRepository tSysProcessInfoRepository;

    @Autowired
    ClassGroupLeaderRepository classGroupLeaderRepository;

    @Autowired
    MidMaterialRepository midMaterialRepository;

    @Autowired
    TSysCraftMaterialRelRepository tSysCraftMaterialRelRepository;

    @Autowired
    OrderHeadService orderHeadService;

    @Autowired
    TSysCraftInfoRepository tSysCraftInfoRepository;

    @Autowired
    OrderProcessRepository orderProcessRepository;

    @Autowired
    TSysProcessClassRelRepository processClassRelRepository;

    @Autowired
    TSysClassService classService;

    @Autowired
    TSysDeviceRepository deviceRepository;

    @Autowired
    TSysCodeDscRepository tSysCodeDscRepository;

    @Autowired
    OrderPPBomRepository orderPPBomRepository;

    @Autowired
    OrderPPBomService orderPPBomService;

    @Autowired
    ChargingBasketService chargingBasketService;

    @Autowired
    TSysCraftInfoService craftInfoService;

    @Autowired
    OrderProcessDeviceRelService orderProcessDeviceRelService;

    @Autowired
    OrderProcessPersonRelService orderProcessPersonRelService;

    @Autowired
    OrderProcessPersonRelRepository orderProcessPersonRelRepository;

    @Autowired
    OrderProcessDeviceRelRepository orderProcessDeviceRelRepository;

    @Value("${submit.enabled:0}")
    String submitEnabled;

    @Autowired
    AppOrderProcessRecordSubmitService appOrderProcessRecordSubmitService;

    @Autowired
    AppOrderProcessRecordDeleteService appOrderProcessRecordDeleteService;

//    @Override
//    public List<OrderPPbomResult> getOrderPpbom(Integer orderId, Integer processId, String recordType) {
//        var orderHead = orderHeadRepository.findById(orderId).orElse(null);
//        var tSysProcessInfo = tSysProcessInfoRepository.findById(processId).orElse(null);
//        if ("1".equals(recordType)) {//原辅料投入
//            List<OrderPPbomResult> orderPPbomResults = new ArrayList<>();
//            switch (tSysProcessInfo.getProcessNumber()) {
//                case LichengConstants.PROCESS_NUMBER_ZHANBAN: {
//                    //斩拌工序
//                    orderPPbomResults = getZB(tSysProcessInfo, orderId);
//                }
//                break;
//                case LichengConstants.PROCESS_NUMBER_BANLIAO: {
//                    //拌料工序
//                    orderPPbomResults = getBL(tSysProcessInfo, orderId);
//                }
//                break;
//                default: {
//                    String midPpbomEntryInputProcess = LichengConstants.ERP_PROCESS_NUMBER_MAP.get(tSysProcessInfo.getProcessNumber());
//                    if (StringUtils.isEmpty(midPpbomEntryInputProcess)) {
//                        logger.warn("报工获取用料清单异常，用工序匹配投入工序为空");
//                        return null;
//                    }
//                    List<Map> maps = orderHeadRepository.getOrderPPbomByOrderIdAndMidPpbomEntryInputProcess(orderId, midPpbomEntryInputProcess);
//                    String mapsStr = JSON.toJSONString(maps);
//                    orderPPbomResults = JSON.parseArray(mapsStr, OrderPPbomResult.class);
//                }
//            }
//            return orderPPbomResults;
//        } else if ("2".equals(recordType)) {//AB料产出
//            List<OrderPPbomResult> orderPPbomResults = new ArrayList<>();
//            List<TSysCodeDsc> tSysCodeDscList = tSysCodeDscRepository.findByCodeClIdAndEnabledSt("RECORDTYPEL20000", GlobalConstant.enableTrue);
//            tSysCodeDscList.sort(Comparator.comparing(TSysCodeDsc::getCodeId));
//            for (TSysCodeDsc tSysCodeDsc : tSysCodeDscList) {
//                //过滤A料、B料、废料
//                if (LichengConstants.RECORDTYPEL20001.equals(tSysCodeDsc.getCodeDsc()) || LichengConstants.RECORDTYPEL20002.equals(tSysCodeDsc.getCodeDsc()) || LichengConstants.RECORDTYPEL20003.equals(tSysCodeDsc.getCodeDsc())) {
//                    OrderPPbomResult result = new OrderPPbomResult();
//                    result.setMaterialNumber(tSysCodeDsc.getCodeValue());
//                    result.setMaterialName(tSysCodeDsc.getCodeDsc());
//                    result.setUnit(LichengConstants.UNIT_KG);
//                    result.setUnitStr(LichengConstants.UNIT_KG_NAME);
//                    orderPPbomResults.add(result);
//                }
//            }
//            for (TSysCodeDsc tSysCodeDsc : tSysCodeDscList) {
//                //拉伸膜的AB料产出，过滤废膜、剩余膜
//                if (LichengConstants.PROCESS_NUMBER_LASHENMO.equals(tSysProcessInfo.getProcessNumber())) {
//                    if (LichengConstants.RECORDTYPEL20000_1.equals(tSysCodeDsc.getCodeDsc()) || LichengConstants.RECORDTYPEL20000_5.equals(tSysCodeDsc.getCodeDsc())) {
//                        OrderPPbomResult result = new OrderPPbomResult();
//                        result.setMaterialNumber(tSysCodeDsc.getCodeValue());
//                        result.setMaterialName(tSysCodeDsc.getCodeDsc());
//                        result.setUnit(LichengConstants.UNIT_KG);
//                        result.setUnitStr(LichengConstants.UNIT_KG_NAME);
//                        orderPPbomResults.add(result);
//                    }
//                }
//            }
//            return orderPPbomResults;
//        } else if ("6".equals(recordType)) {//AB料投入
//            List<OrderPPbomResult> orderPPbomResults = new ArrayList<>();
//            //斩拌工序报工模块，原辅料投入报工优化 2022-07-04
//            if (LichengConstants.PROCESS_NUMBER_ZHANBAN.equals(tSysProcessInfo.getProcessNumber())) {
//                //增加返回AB料，取值自字典
//                List<TSysCodeDsc> tSysCodeDscList = tSysCodeDscRepository.findByCodeClIdAndEnabledSt("RECORDL20000", GlobalConstant.enableTrue);
//                tSysCodeDscList.sort(Comparator.comparing(TSysCodeDsc::getCodeId));
//                for (TSysCodeDsc tSysCodeDsc : tSysCodeDscList) {
//                    OrderPPbomResult result = new OrderPPbomResult();
//                    result.setMaterialNumber(tSysCodeDsc.getCodeValue());
//                    result.setMaterialName(tSysCodeDsc.getCodeDsc());
//                    if (tSysCodeDsc.getCodeDsc().contains("A")) {
//                        result.setMustQty(orderHead.getMidMoEntryFirstMaterialMaxValue() == null ? 0.0 : orderHead.getMidMoEntryFirstMaterialMaxValue());
//                        result.setUnit(LichengConstants.UNIT_KG);
//                        result.setUnitStr(LichengConstants.UNIT_KG_NAME);
//                    } else if (tSysCodeDsc.getCodeDsc().contains("B")) {
//                        result.setMustQty(orderHead.getMidMoEntrySecondMaterialMaxValue() == null ? 0.0 : orderHead.getMidMoEntrySecondMaterialMaxValue());
//                        result.setUnit(LichengConstants.UNIT_KG);
//                        result.setUnitStr(LichengConstants.UNIT_KG_NAME);
//                    }
//                    orderPPbomResults.add(result);
//                }
//            }
//            return orderPPbomResults;
//        }
//        return null;
//    }

//    /**
//     * 按物料id获取累计投入和个人投入
//     * @param searchDto
//     * @return
//     */
//    @Override
//    public OrderPPbomResult getOrderPpbomByPpbomMaterialId(OrderPPbomSearchDto searchDto) {
//        Integer orderId = searchDto.getOrderId();
//        Integer processId = searchDto.getExecuteProcessId();
//        Integer orderProcessId = searchDto.getOrderProcessId();
//        String recordType = searchDto.getRecordType();
//        List<Integer> devicePersonIds = searchDto.getDevicePersonIds();
//        Integer ppbomMaterialId = searchDto.getPpbomMaterialId();
//        var orderHead = orderHeadRepository.findById(orderId).orElse(null);
//        if ("1".equals(recordType)) {//原辅料投入
//            OrderPPbomResult orderPPbomResult = new OrderPPbomResult();
//            List<Map> ppbomRecordTotals = orderProcessRecordRepository.getPpbomRecordQtyTotal(orderHead.getOrderNo(), processId, ppbomMaterialId);
//            List<Map> ppbomRecordPersons = new ArrayList<>();
//            if (devicePersonIds != null && devicePersonIds.size() > 0) {
//                String groupId = orderProcessPersonRelRepository.getPersonGroupId(orderProcessId, devicePersonIds, devicePersonIds.size());
//                if (StringUtils.isNotEmpty(groupId)) {
//                    ppbomRecordPersons = orderProcessRecordRepository.getPpbomRecordQtyPersonal(orderHead.getOrderNo(), processId, groupId, ppbomMaterialId);
//                }
//            }
//            List<Map> finalPpbomRecordPersons = ppbomRecordPersons;
//            var totalMap = ppbomRecordTotals.stream().filter(r -> String.valueOf(r.get("order_ppbom_id")).equals(orderPPbomResult.getOrderPPBomId() + "")).findFirst().orElse(null);
//            var personMap = finalPpbomRecordPersons.stream().filter(r -> String.valueOf(r.get("order_ppbom_id")).equals(orderPPbomResult.getOrderPPBomId() + "")).findFirst().orElse(null);
//            if (null != totalMap) {
//                orderPPbomResult.setRecordQtyTotal(Float.parseFloat(String.valueOf(totalMap.get("record_qty"))));
//                orderPPbomResult.setRecordUnit(String.valueOf(totalMap.get("record_unit")));
//                orderPPbomResult.setRecordUnitStr(GlobalConstant.getCodeDscName("UNIT0000", orderPPbomResult.getRecordUnit()));
//            }
//            if (null != personMap) {
//                orderPPbomResult.setRecordQtyPersonal(Float.parseFloat(String.valueOf(personMap.get("record_qty"))));
//            }
//            return orderPPbomResult;
//        }
//        return null;
//    }

    /**
     * 获取用料清单，并查询报工：累计投入和个人投入
     *
     * @param searchDto
     * @return
     */
    @Override
    public List<PpbomGroupVo> getOrderPpbom(OrderPPbomSearchDto searchDto) {
        List<PpbomGroupVo> ppbomGroupVos = new ArrayList<>();
        PpbomGroupVo ppbomGroupVo;
        Integer orderId = searchDto.getOrderId();
        Integer processId = searchDto.getExecuteProcessId();
        Integer orderProcessId = searchDto.getOrderProcessId();
        String recordType = searchDto.getRecordType();
        List<Integer> devicePersonIds = searchDto.getDevicePersonIds();
        Integer ppbomMaterialId = searchDto.getPpbomMaterialId() == null ? -1 : searchDto.getPpbomMaterialId();
        var orderHead = orderHeadRepository.findById(orderId).orElse(null);
        var tSysProcessInfo = tSysProcessInfoRepository.findById(processId).orElse(null);
        if ("1".equals(recordType)) {//原辅料投入
            List<OrderPPbomResult> orderPPbomResults = new ArrayList<>();
            if (ppbomMaterialId > 0) {
                ppbomGroupVo = new PpbomGroupVo();
                OrderPPbomResult orderPPbomResult = new OrderPPbomResult();
                orderPPbomResult.setMaterialId(ppbomMaterialId);
                orderPPbomResults.add(orderPPbomResult);
                getOrderPPbomResults(processId, orderProcessId, devicePersonIds, orderHead, orderPPbomResults, ppbomMaterialId);
                ppbomGroupVo.setOrderPPbomResultList(orderPPbomResults);
                ppbomGroupVos.add(ppbomGroupVo);
                return ppbomGroupVos;
            }
            switch (tSysProcessInfo.getProcessNumber()) {

                default: {
                    /*String midPpbomEntryInputProcess = tSysProcessInfo.getErpProcessNumber();
                    if (StringUtils.isEmpty(midPpbomEntryInputProcess)) {
                        throw new RuntimeException("工序没有绑定ERP工序，请检查下后台的工序管理配置");
                    }*/
                    List<Map> maps = orderHeadRepository.getOrderPPbomByOrderIdAndMidPpbomEntryInputProcess(orderId);
                    String mapsStr = JSON.toJSONString(maps);
                    ppbomGroupVo = new PpbomGroupVo();
                    orderPPbomResults = JSON.parseArray(mapsStr, OrderPPbomResult.class);
                    for (OrderPPbomResult orderPPbomResult : orderPPbomResults) {
                        orderPPbomResult.setMidPpbomEntryWeighDeveptQty(orderPPbomResult.getMustQty().floatValue());
                        orderPPbomResult.setMidPpbomEntryWeighMesQty(orderPPbomResult.getMustQty().floatValue());
                        orderPPbomResult.setMidPpbomEntryWeighMesUnit(orderPPbomResult.getUnit());
                        orderPPbomResult.setMidPpbomEntryWeighDeveptUnit(orderPPbomResult.getUnit());
                        orderPPbomResult.setUnitStr(orderPPbomResult.getUnit());
                        orderPPbomResult.setRecordUnit(orderPPbomResult.getUnit());
                        orderPPbomResult.setRecordUnitStr(orderPPbomResult.getUnit());
                    }
                    ppbomGroupVo.setOrderPPbomResultList(orderPPbomResults);
                    ppbomGroupVos.add(ppbomGroupVo);
                }
            }
            if (ppbomMaterialId == -1) {
                for (PpbomGroupVo groupVo : ppbomGroupVos) {
                    getOrderPPbomResults(processId, orderProcessId, devicePersonIds, orderHead, groupVo.getOrderPPbomResultList(), ppbomMaterialId);
                }
            }
            return ppbomGroupVos;
        } else if ("2".equals(recordType)) {//AB料产出
            List<OrderPPbomResult> orderPPbomResults = new ArrayList<>();
            List<TSysCodeDsc> tSysCodeDscList = tSysCodeDscRepository.findByCodeClIdAndEnabledSt("RECORDTYPEL20000", GlobalConstant.enableTrue);
            tSysCodeDscList.sort(Comparator.comparing(TSysCodeDsc::getCodeId));
            for (TSysCodeDsc tSysCodeDsc : tSysCodeDscList) {
                //过滤A料、B料、废料
                if (LichengConstants.RECORDTYPEL20001_NUMBER.equals(tSysCodeDsc.getCodeValue()) || LichengConstants.RECORDTYPEL20002_NUMBER.equals(tSysCodeDsc.getCodeValue()) || LichengConstants.RECORDTYPEL20003_NUMBER.equals(tSysCodeDsc.getCodeValue()) || LichengConstants.RECORDTYPEL20004_NUMBER.equals(tSysCodeDsc.getCodeValue())) {
                    OrderPPbomResult result = new OrderPPbomResult();
                    result.setMaterialNumber(tSysCodeDsc.getCodeValue());
                    result.setMaterialName(tSysCodeDsc.getCodeDsc());
                    result.setUnit(LichengConstants.UNIT_KG);
                    result.setUnitStr(LichengConstants.UNIT_KG_NAME);
                    orderPPbomResults.add(result);
                }
            }
            for (TSysCodeDsc tSysCodeDsc : tSysCodeDscList) {
                //拉伸膜的AB料产出，过滤废膜、剩余膜
                if (LichengConstants.PROCESS_NUMBER_LASHENMO.equals(tSysProcessInfo.getProcessNumber())) {
                    if (LichengConstants.RECORDTYPEL20000_1.equals(tSysCodeDsc.getCodeDsc()) || LichengConstants.RECORDTYPEL20000_5.equals(tSysCodeDsc.getCodeDsc())) {
                        OrderPPbomResult result = new OrderPPbomResult();
                        result.setMaterialNumber(tSysCodeDsc.getCodeValue());
                        result.setMaterialName(tSysCodeDsc.getCodeDsc());
                        result.setUnit(LichengConstants.UNIT_KG);
                        result.setUnitStr(LichengConstants.UNIT_KG_NAME);
                        orderPPbomResults.add(result);
                    }
                }
            }
            ppbomGroupVo = new PpbomGroupVo();
            ppbomGroupVo.setOrderPPbomResultList(orderPPbomResults);
            ppbomGroupVos.add(ppbomGroupVo);
            return ppbomGroupVos;
        } else if ("6".equals(recordType)) {//AB料投入
            List<OrderPPbomResult> orderPPbomResults1 = new ArrayList<>();
            List<OrderPPbomResult> orderPPbomResults2 = new ArrayList<>();
            List<OrderPPbomResult> orderPPbomResults3 = new ArrayList<>();
            //斩拌工序报工模块，原辅料投入报工优化 2022-07-04
            if (LichengConstants.PROCESS_NUMBER_ZHANBAN.equals(tSysProcessInfo.getProcessNumber())) {
                //增加返回AB料，取值自字典
                List<TSysCodeDsc> tSysCodeDscList = tSysCodeDscRepository.findByCodeClIdAndEnabledSt("RECORDL20000", GlobalConstant.enableTrue);
                tSysCodeDscList.sort(Comparator.comparing(TSysCodeDsc::getCodeId));
                for (TSysCodeDsc tSysCodeDsc : tSysCodeDscList) {
                    OrderPPbomResult result = new OrderPPbomResult();
                    result.setMaterialNumber(tSysCodeDsc.getCodeValue());
                    result.setMaterialName(tSysCodeDsc.getCodeDsc());
                    if (tSysCodeDsc.getCodeValue().contains("B_RECORDL2")) {
                        if (tSysCodeDsc.getCodeDsc().contains("A")) {
                            result.setMustQty(orderHead.getMidMoEntryFirstMaterialMaxValue() == null ? 0.0 : orderHead.getMidMoEntryFirstMaterialMaxValue());
                            result.setUnit(LichengConstants.UNIT_KG);
                            result.setUnitStr(LichengConstants.UNIT_KG_NAME);
                        } else if (tSysCodeDsc.getCodeDsc().contains("B")) {
                            result.setMustQty(orderHead.getMidMoEntrySecondMaterialMaxValue() == null ? 0.0 : orderHead.getMidMoEntrySecondMaterialMaxValue());
                            result.setUnit(LichengConstants.UNIT_KG);
                            result.setUnitStr(LichengConstants.UNIT_KG_NAME);
                        }
                        if (tSysCodeDsc.getCodeValue().equals("B_RECORDL20006")) {
                            result.setMustQty(orderHead.getMidMoEntryIceWater() == null ? 0.0 : orderHead.getMidMoEntryIceWater());
                            result.setUnit(LichengConstants.UNIT_KG);
                            result.setUnitStr(LichengConstants.UNIT_KG_NAME);
                        }
                        orderPPbomResults2.add(result);
                    } else if (tSysCodeDsc.getCodeValue().contains("C_RECORDL2")) {
                        if (tSysCodeDsc.getCodeValue().equals("C_RECORDL20005")) {
                            result.setMustQty(orderHead.getMidMoEntryEmulsion() == null ? 0.0 : orderHead.getMidMoEntryEmulsion());
                            result.setUnit(LichengConstants.UNIT_KG);
                            result.setUnitStr(LichengConstants.UNIT_KG_NAME);
                        }
                        orderPPbomResults3.add(result);
                    } else {
                        if (tSysCodeDsc.getCodeDsc().contains("A")) {
                            result.setMustQty(orderHead.getMidMoEntryFirstMaterialMaxValue() == null ? 0.0 : orderHead.getMidMoEntryFirstMaterialMaxValue());
                            result.setUnit(LichengConstants.UNIT_KG);
                            result.setUnitStr(LichengConstants.UNIT_KG_NAME);
                        } else if (tSysCodeDsc.getCodeDsc().contains("B")) {
                            result.setMustQty(orderHead.getMidMoEntrySecondMaterialMaxValue() == null ? 0.0 : orderHead.getMidMoEntrySecondMaterialMaxValue());
                            result.setUnit(LichengConstants.UNIT_KG);
                            result.setUnitStr(LichengConstants.UNIT_KG_NAME);
                        }
                        orderPPbomResults1.add(result);
                    }
                }
                PpbomGroupVo ppbomGroupVo1 = new PpbomGroupVo();
                ppbomGroupVo1.setMidPpbomEntryHandleGroup(1);
                ppbomGroupVo1.setMidPpbomEntryHandleGroupName("直接投入");
                ppbomGroupVo1.setOrderPPbomResultList(orderPPbomResults1);

                PpbomGroupVo ppbomGroupVo2 = new PpbomGroupVo();
                ppbomGroupVo2.setMidPpbomEntryHandleGroup(2);
                ppbomGroupVo2.setMidPpbomEntryHandleGroupName("生成次品浆投入");
                ppbomGroupVo2.setOrderPPbomResultList(orderPPbomResults2);

                PpbomGroupVo ppbomGroupVo3 = new PpbomGroupVo();
                ppbomGroupVo3.setMidPpbomEntryHandleGroup(3);
                ppbomGroupVo3.setMidPpbomEntryHandleGroupName("次品浆投入");
                ppbomGroupVo3.setOrderPPbomResultList(orderPPbomResults3);
                ppbomGroupVos.add(ppbomGroupVo1);
                ppbomGroupVos.add(ppbomGroupVo2);
                ppbomGroupVos.add(ppbomGroupVo3);
            }
            //ppbomGroupVo = new PpbomGroupVo();
            //ppbomGroupVo.setMidPpbomEntryHandleGroup(1);
            //ppbomGroupVo.setMidPpbomEntryHandleGroupName("直接投入");
            //ppbomGroupVo.setMidPpbomEntryHandleGroup(2);
            //ppbomGroupVo.setMidPpbomEntryHandleGroupName("生成次品浆投入");
            //ppbomGroupVo.setMidPpbomEntryHandleGroup(3);
            //ppbomGroupVo.setMidPpbomEntryHandleGroupName("次品浆投入");
            //ppbomGroupVo.setOrderPPbomResultList(orderPPbomResults);
            //ppbomGroupVos.add(ppbomGroupVo);
            return ppbomGroupVos;
        }
        return null;
    }

    /**
     * 乳化浆工序原辅料列表
     *
     * @param tSysProcessInfo
     * @param orderId
     * @return
     */
    private List<OrderPPbomResult> getRhj(TSysProcessInfo tSysProcessInfo, Integer orderId) {
        String midPpbomEntryInputProcess = tSysProcessInfo.getErpProcessNumber();
        if (StringUtils.isEmpty(midPpbomEntryInputProcess)) {
            throw new RuntimeException("工序没有绑定ERP工序，请检查下后台的工序管理配置");
        }
        List<Map> mapRhj = orderHeadRepository.getOrderPPbomByOrderIdAndMidPpbomEntryInputProcess(orderId);
        String mapsStr = JSON.toJSONString(mapRhj);
        List<OrderPPbomResult> orderPPbomResults = JSON.parseArray(mapsStr, OrderPPbomResult.class);
        return orderPPbomResults;

    }

    private void getOrderPPbomResults(Integer processId, Integer orderProcessId, List<Integer> devicePersonIds, TBusOrderHead orderHead, List<OrderPPbomResult> orderPPbomResults, Integer ppbomMaterialId) {
        List<Map> ppbomRecordTotals = orderProcessRecordRepository.getPpbomRecordQtyTotal(orderHead.getOrderNo(), processId, ppbomMaterialId);
        List<Map> ppbomRecordPersons = new ArrayList<>();
        ppbomRecordPersons = orderProcessRecordRepository.getPpbomRecordQtyPersonal(orderHead.getOrderNo(), processId, ppbomMaterialId);
        System.out.println(ppbomRecordPersons);
        List<Map> finalPpbomRecordPersons = ppbomRecordPersons;
        orderPPbomResults.stream().forEach(orderPPbomResult -> {
            if (ppbomMaterialId > 0) {
                var totalMap = ppbomRecordTotals.stream().findFirst().orElse(null);
                var personMap = finalPpbomRecordPersons.stream().findFirst().orElse(null);
                if (null != totalMap) {
                    orderPPbomResult.setRecordQtyTotal(Float.parseFloat(String.valueOf(totalMap.get("record_qty"))));
                    orderPPbomResult.setRecordUnit(String.valueOf(totalMap.get("record_unit")));
                    orderPPbomResult.setRecordUnitStr(String.valueOf(totalMap.get("record_unit")));
                }
                if (null != personMap) {
                    orderPPbomResult.setRecordQtyPersonal(Float.parseFloat(String.valueOf(personMap.get("record_qty"))));
                    orderPPbomResult.setPersonalCount(null == personMap.get("personanl_count") ? "0" : String.valueOf(personMap.get("personanl_count")));
                }
            } else {
                var totalMap = ppbomRecordTotals.stream().filter(r -> String.valueOf(r.get("order_ppbom_id")).equals(orderPPbomResult.getOrderPPBomId() + "")).findFirst().orElse(null);
                var personMap = finalPpbomRecordPersons.stream().filter(r -> String.valueOf(r.get("order_ppbom_id")).equals(orderPPbomResult.getOrderPPBomId() + "")).findFirst().orElse(null);
                if (null != totalMap) {
                    orderPPbomResult.setRecordQtyTotal(Float.parseFloat(String.valueOf(totalMap.get("record_qty"))));
                    orderPPbomResult.setRecordUnit(String.valueOf(totalMap.get("record_unit")));
                    orderPPbomResult.setRecordUnitStr(String.valueOf(totalMap.get("record_unit")));
                }
                if (null != personMap) {
                    orderPPbomResult.setRecordQtyPersonal(Float.parseFloat(String.valueOf(personMap.get("record_qty"))));
                    orderPPbomResult.setPersonalCount(String.valueOf(personMap.get("personanl_count")));
                }
            }//"personanl_count" -> "9.0"
            orderPPbomResult.setPersonalCount(StringUtils.isNotBlank(orderPPbomResult.getPersonalCountBak()) ? orderPPbomResult.getPersonalCountBak() : orderPPbomResult.getPersonalCount());
        });
    }

    /**
     * 获取用料清单，过滤拉伸膜
     */
    @Override
    public List<OrderPPbomResult> getOrderPpbomLsm(OrderPPbomSearchDto searchDto) {
        Integer orderId = searchDto.getOrderId();
        Integer processId = searchDto.getExecuteProcessId();
        Integer orderProcessId = searchDto.getOrderProcessId();
        String recordType = searchDto.getRecordType();
        List<Integer> devicePersonIds = searchDto.getDevicePersonIds();
        Integer ppbomMaterialId = searchDto.getPpbomMaterialId() == null ? -1 : searchDto.getPpbomMaterialId();
        var orderHead = orderHeadRepository.findById(orderId).orElse(null);
        var tSysProcessInfo = tSysProcessInfoRepository.findById(processId).orElse(null);

        List<OrderPPbomResult> orderPPbomResults = new ArrayList<>();
        switch (tSysProcessInfo.getProcessNumber()) {
            case LichengConstants.PROCESS_NUMBER_LASHENMO: {
                //拉伸膜工序
                String midPpbomEntryInputProcess = tSysProcessInfo.getErpProcessNumber();
                if (StringUtils.isEmpty(midPpbomEntryInputProcess)) {
                    throw new RuntimeException("工序没有绑定ERP工序，请检查下后台的工序管理配置");
                }
                List<Map> maps = orderHeadRepository.getOrderPPbomLsmByOrderIdAndMidPpbomEntryInputProcess(orderId, midPpbomEntryInputProcess);
                String mapsStr = JSON.toJSONString(maps);
                orderPPbomResults = JSON.parseArray(mapsStr, OrderPPbomResult.class);
            }
            break;
            default: {
                break;
            }
        }
        return orderPPbomResults;
    }

//    @NotNull
//    private List<OrderPPbomResult> getOrderPPbomResults(Integer processId, Integer orderProcessId, List<Integer> devicePersonIds, TBusOrderHead orderHead, List<OrderPPbomResult> orderPPbomResults, Integer ppbomMaterialId) {
//
//        List<Map> ppbomRecordTotals = orderProcessRecordRepository.getPpbomRecordQtyTotal(orderHead.getOrderNo(), processId, ppbomMaterialId);
//        List<Map> ppbomRecordPersons = new ArrayList<>();
//        if (devicePersonIds != null && devicePersonIds.size() > 0) {
//            String groupId = orderProcessPersonRelRepository.getPersonGroupId(orderProcessId, devicePersonIds, devicePersonIds.size());
//            if (StringUtils.isNotEmpty(groupId)) {
//                ppbomRecordPersons = orderProcessRecordRepository.getPpbomRecordQtyPersonal(orderHead.getOrderNo(), processId, groupId, ppbomMaterialId);
//            }
//        }
//        List<Map> finalPpbomRecordPersons = ppbomRecordPersons;
//        var totalMap = ppbomRecordTotals.stream().filter(r -> String.valueOf(r.get("order_ppbom_id")).equals(orderPPbomResult.getOrderPPBomId() + "")).findFirst().orElse(null);
//        var personMap = finalPpbomRecordPersons.stream().filter(r -> String.valueOf(r.get("order_ppbom_id")).equals(orderPPbomResult.getOrderPPBomId() + "")).findFirst().orElse(null);
//        if (null != totalMap) {
//            orderPPbomResult.setRecordQtyTotal(Float.parseFloat(String.valueOf(totalMap.get("record_qty"))));
//            orderPPbomResult.setRecordUnit(String.valueOf(totalMap.get("record_unit")));
//            orderPPbomResult.setRecordUnitStr(GlobalConstant.getCodeDscName("UNIT0000", orderPPbomResult.getRecordUnit()));
//        }
//        if (null != personMap) {
//            orderPPbomResult.setRecordQtyPersonal(Float.parseFloat(String.valueOf(personMap.get("record_qty"))));
//        }
//        orderPPbomResults.add(orderPPbomResult);
//        return orderPPbomResults;
//    }

    /**
     * 拌料工序获取用料列表
     *
     * @param tSysProcessInfo
     * @param orderId
     * @return
     */
    private List<OrderPPbomResult> getBL(TSysProcessInfo tSysProcessInfo, Integer orderId, Integer orderProcessId) {
        List<OrderPPbomResult> orderPPbomResults = new ArrayList<>();
        String midPpbomEntryInputProcess = tSysProcessInfo.getErpProcessNumber();
        if (StringUtils.isEmpty(midPpbomEntryInputProcess)) {
            throw new RuntimeException("工序没有绑定ERP工序，请检查下后台的工序管理配置");
        }
        //1.获取蟹肉棒——此项通过用料清单“子项物料属性”为自制且“子项BOM版本”为非空（有版本号）
//        Map map = orderHeadRepository.getBLXRB(orderId, midPpbomEntryInputProcess);
//        List<Map> maps = new ArrayList<>();
//        if (map != null && map.size() > 0) {
//            maps.add(map);
//        }
//        String mapsStr1 = JSON.toJSONString(maps);
//        List<OrderPPbomResult> orderPPbomResults1 = JSON.parseArray(mapsStr1, OrderPPbomResult.class);

        //2.获取拌料信息——此项通过用料清单“子项物料属性”不为自制且“子项BOM版本”为空
//        List<Map> mapBl = orderHeadRepository.listBLPPboms(orderId, midPpbomEntryInputProcess);
//        maps.addAll(mapBl);
//        String mapsStr = JSON.toJSONString(maps);
//        orderPPbomResults = JSON.parseArray(mapsStr, OrderPPbomResult.class);

        var orderPPbomResults2 = listMap(orderId, midPpbomEntryInputProcess, -1, orderProcessId);
//        orderPPbomResults1.addAll(orderPPbomResults2);
        return orderPPbomResults2;

    }

    /**
     * 斩拌工序获取用料列表
     *
     * @param tSysProcessInfo
     * @return
     */
    private List<PpbomGroupVo> getZB(TSysProcessInfo tSysProcessInfo, Integer orderId, Integer orderProcessId) {
        //斩拌工序
        String midPpbomEntryInputProcess = tSysProcessInfo.getErpProcessNumber();
        if (StringUtils.isEmpty(midPpbomEntryInputProcess)) {
            throw new RuntimeException("工序没有绑定ERP工序，请检查下后台的工序管理配置");
        }
        List<PpbomGroupVo> ppbomGroupVos = new ArrayList<>();
        List<OrderPPbomResult> orderPPbomResults;
        PpbomGroupVo vo;
        if (StringUtils.isNotEmpty(midPpbomEntryInputProcess)) {
            PpbomGroupVo vo1 = new PpbomGroupVo();
            vo1.setMidPpbomEntryHandleGroup(-1);
            vo1.setMidPpbomEntryHandleGroupName("全部");
            List<OrderPPbomResult> orderPPbomResults1 = listMap(orderId, midPpbomEntryInputProcess, -1, orderProcessId);
            vo1.setOrderPPbomResultList(orderPPbomResults1);
            ppbomGroupVos.add(vo1);
            for (Map.Entry<Integer, String> entry : LichengConstants.PPBOM_GROUP_NUMBER_MAP.entrySet()) {
                vo = new PpbomGroupVo();
                vo.setMidPpbomEntryHandleGroup(entry.getKey());
                vo.setMidPpbomEntryHandleGroupName(entry.getValue());
                orderPPbomResults = listMap(orderId, midPpbomEntryInputProcess, entry.getKey(), orderProcessId);
                vo.setOrderPPbomResultList(orderPPbomResults);
                ppbomGroupVos.add(vo);
            }
        }
        return ppbomGroupVos;
    }

    private List<OrderPPbomResult> listMap(Integer orderId, String midPpbomEntryInputProcess, Integer key, Integer orderProcessId) {
        List<OrderPPbomResult> orderPPbomResults;
        List<Map> maps = orderHeadRepository.getOrderPPbomByOrderIdAndMidPpbomEntryInputProcess(orderId);
        if (maps != null && maps.size() > 0) {
            String mapsStr = JSON.toJSONString(maps);
            orderPPbomResults = JSON.parseArray(mapsStr, OrderPPbomResult.class);
            //组合配置
            //标记
            int i = 0;
            int trueI = -1;
            boolean flag;
            String count = null;
            for (int j = 0; j < orderPPbomResults.size(); j++) {
                orderPPbomResults.get(j).setUnitStr(GlobalConstant.getCodeDscName("UNIT0000", orderPPbomResults.get(j).getUnit()));
                if (orderPPbomResults.get(j) == null || orderPPbomResults.get(j).getMidPpbomEntryReplaceGroup() == null || trueI == orderPPbomResults.get(j).getMidPpbomEntryReplaceGroup().intValue()) {
                    continue;
                }
                i++;
                flag = false;
                for (int k = j + 1; k < orderPPbomResults.size(); k++) {
                    if (orderPPbomResults.get(j).getMidPpbomEntryReplaceGroup() != null && orderPPbomResults.get(k).getMidPpbomEntryReplaceGroup() != null) {
                        if (orderPPbomResults.get(j).getMidPpbomEntryReplaceGroup().intValue() == orderPPbomResults.get(k).getMidPpbomEntryReplaceGroup().intValue()) {
                            flag = true;
                            trueI = orderPPbomResults.get(j).getMidPpbomEntryReplaceGroup().intValue();
                            orderPPbomResults.get(k).setGroupFlag("组合" + (i));
                            List<JoinRecordVo> joinRecordVos = this.joinRecord(orderPPbomResults.get(j).getOrderPPBomId(), orderId, orderProcessId, null);
                            count = String.valueOf(joinRecordVos.get(0).getImportPot() - 1);
                            orderPPbomResults.get(k).setPersonalCountBak(count);
                        }
                    }
                }
                if (flag) {
                    orderPPbomResults.get(j).setGroupFlag("组合" + (i));
                    orderPPbomResults.get(j).setPersonalCountBak(count);
                } else {
                    i--;
                }
            }
        } else {
            orderPPbomResults = new ArrayList<>();
        }
        return orderPPbomResults;
    }

    @Override
    public List<TBusOrderProcessRecord> getOrderProcessRecord(Integer orderProcessId, String busType) {
        return orderProcessRecordRepository.findAllByOrderProcessIdAndBusType(orderProcessId, busType);
    }

    @Override
    public List<TBusOrderProcessRecord> getBgOrderProcessRecords(Integer orderProcessId, String recordType) {
        return orderProcessRecordRepository.findAllByOrderProcessIdAndBusTypeAndRecordType(orderProcessId, LichengConstants.ORDER_BUS_TYPE_BG, recordType);
    }

    @Override
    public List<TBusOrderProcessRecord> getPdOrderProcessRecords(Integer orderProcessId, String recordType) {
        return orderProcessRecordRepository.findAllByOrderProcessIdAndBusTypeAndRecordType(orderProcessId, LichengConstants.ORDER_BUS_TYPE_PD, recordType);
    }

    @Override
    public OrderRecordCheckVo submitCheck(OrderRecordCheckDto saveDto) {
        OrderRecordCheckVo checkVo;
        if (saveDto.getOrderProcessId() == null || saveDto.getOrderProcessId() == 0) {
            throw new RuntimeException("工序执行表ID不能为空");
        }
        if (StringUtils.isEmpty(saveDto.getRecordUnit())) {
            throw new RuntimeException("单位不为空");
        }
        TBusOrderProcess tBusOrderProcess = orderProcessRepository.findById(saveDto.getOrderProcessId()).orElse(null);
        TSysProcessInfo processInfo = tBusOrderProcess.getProcessId();
        List<TBusOrderHead> heads = orderHeadRepository.findByOrderNo(saveDto.getOrderNo());
        TBusOrderHead tBusOrderHead = heads.get(0);
        if (LichengConstants.ORDERSTATUS_4.equals(tBusOrderHead.getOrderStatus())) {
            throw new RuntimeException("订单已挂起，禁止操作。");
        }
        checkVo = new OrderRecordCheckVo();
        if ("1".equals(saveDto.getRecordType()) && "1".equals(submitEnabled)) {
            // 15947 【APP端生产】自制品原辅料投入控制优化
            // 报工模块，原辅料投入模块，如果是物料属于自制品，则需要判断该自制品在前道工序是否已经进行合格品产出报工，如果没有报工记录，则弹层提示“不允许报工，该自制品前道工单未作产出报工！”，点击关闭按钮，关闭弹层。
            boolean mainPpbomFlag = orderPPBomService.getMainPpbomFlag(saveDto.getOrderPPBomId());
            if (mainPpbomFlag) {
                // 通过自制品物料编码+需求单号，关联出前道订单
                float sumExportRecordQtyPrevOrder = this.getBGSumRecordQtyPrevOrder(saveDto.getOrderNo(), LichengConstants.ORDER_RECORD_TYPE_3, saveDto.getRecordTypeBg());
                if (sumExportRecordQtyPrevOrder == 0) {
                    throw new RuntimeException("不允许报工，该自制品前道工单未作产出报工！");
                }
            }
            // 任务15078 【APP端生产】报工模块投入和产出规则控制优化
            String processNumber = saveDto.getProcessNumber() == null ? "" : saveDto.getProcessNumber();
            // 拌料
            if (LichengConstants.PROCESS_NUMBER_BANLIAO.equals(processNumber)) {
                // 判断需求单号是否为空
                if (StringUtils.isNotBlank(tBusOrderHead.getMidMoSaleOrderNo())) {
                    List<TSysCodeDsc> tSysCodeDscExcessMaxParams = tSysCodeDscRepository.findByCodeClIdAndCodeDsc("OTHER0000", "原料投入超额最大比例");
                    if (tSysCodeDscExcessMaxParams.size() > 0) {
                        var tSysCodeDscExcessMax = tSysCodeDscExcessMaxParams.stream().findFirst().orElse(null);
                        Integer excessParamMax = 0;
                        try {
                            excessParamMax = Integer.parseInt(tSysCodeDscExcessMax.getCodeValue());
                        } catch (NumberFormatException e) {
                            throw new RuntimeException("投入超额校验处理异常，获取转换参数发生异常");
                        }
                        if (processInfo != null && StringUtils.isNotEmpty(processInfo.getErpProcessNumber())) {
                            int orderPpbomId = saveDto.getOrderPPBomId();
                            TBusOrderPPBom orderPpbom = orderPPBomRepository.findById(orderPpbomId).orElse(null);
                            // 计划锅数
                            int pot = tBusOrderHead.getBodyPotQty() == null ? 0 : tBusOrderHead.getBodyPotQty();
                            // 获取工序全部用料对应的累计投入数量
                            float sumImportRecordQty = orderProcessRecordRepository.getBGSumRecordQty(saveDto.getProcessId(), tBusOrderHead.getMidMoSaleOrderNo(), saveDto.getRecordTypeBg());
                            sumImportRecordQty += saveDto.getRecordQty();//累加本次提交的数量
                            // 前道产出数量：获取拌料的前道订单全部用料累计投入数量
                            float sumPlanImportPrevOrder = this.getBGSumRecordQtyPrevOrder(saveDto.getOrderNo(), LichengConstants.ORDER_RECORD_TYPE_1, saveDto.getRecordTypeBg());
                            if (sumPlanImportPrevOrder == 0) {
                                throw new RuntimeException("投入超额校验处理异常，获取前道产出数量为0，请检查下订单前道工序是否包含斩拌");
                            }
                            // 计算当前投入比例 = 本道原辅料累计投入数量/前道产出数量*100
                            float importProportion = BigDecimalUtil.format(sumImportRecordQty / sumPlanImportPrevOrder * 100, 2);
                            checkVo.setImportProportion(importProportion);
                            checkVo.setImportExcessProportionMax(excessParamMax);
                            checkVo.setSumImportRecordQty(sumImportRecordQty);
                            checkVo.setSumImportRecordUnit(orderPpbom.getMidPpbomEntryWeighDeveptUnit());
                            checkVo.setSumPlanImportQty(sumPlanImportPrevOrder);
                            checkVo.setSumPlanImportUnit(orderPpbom.getMidPpbomEntryWeighDeveptUnit());
                            checkVo.setIsProcessBL(1);//是否拌料
                            return checkVo;
                        }
                    } else {
                        throw new RuntimeException("报工校验失败：拌料原料投入报工时，不允许工序或者ERP工序为空！");
                    }
                } else {
                    throw new RuntimeException("报工校验失败：拌料原料投入报工时，不允许订单需求单号为空！");
                }
            } else {
                // (1)100%<=(累计投入数量/计划投入总数量)<=110%，提交报工，弹层提示“当前物料累计投入数量达到预警区间（达到计划投入100%~110%之间），是否继续提交？”,点击确定继续提交。
                // (2)(累计投入数量/计划投入总数量)>110%，提交报工，弹层提示“当前物料累计投入数量超出计划投入数量比例（超出计划投入110%）”，不允许提交。
                List<TSysCodeDsc> tSysCodeDscWarnMinParams = tSysCodeDscRepository.findByCodeClIdAndCodeDsc("OTHER0000", "原料投入预警最小比例");
                List<TSysCodeDsc> tSysCodeDscWarnMaxParams = tSysCodeDscRepository.findByCodeClIdAndCodeDsc("OTHER0000", "原料投入预警最大比例");
                List<TSysCodeDsc> tSysCodeDscExcessMaxParams = tSysCodeDscRepository.findByCodeClIdAndCodeDsc("OTHER0000", "原料投入超额最大比例");
                if (tSysCodeDscWarnMinParams.size() > 0 && tSysCodeDscWarnMaxParams.size() > 0 && tSysCodeDscExcessMaxParams.size() > 0) {
                    var tSysCodeDscWarnMin = tSysCodeDscWarnMinParams.stream().findFirst().orElse(null);
                    var tSysCodeDscWarnMax = tSysCodeDscWarnMaxParams.stream().findFirst().orElse(null);
                    var tSysCodeDscExcessMax = tSysCodeDscExcessMaxParams.stream().findFirst().orElse(null);
                    Integer warnParamMin = 0;
                    Integer warnParamMax = 0;
                    Integer excessParamMax = 0;
                    try {
                        warnParamMin = Integer.parseInt(tSysCodeDscWarnMin.getCodeValue());
                        warnParamMax = Integer.parseInt(tSysCodeDscWarnMax.getCodeValue());
                        excessParamMax = Integer.parseInt(tSysCodeDscExcessMax.getCodeValue());
                    } catch (NumberFormatException e) {
                        throw new RuntimeException("投入超额校验处理异常，获取转换参数发生异常");
                    }
                    if (processInfo != null && StringUtils.isNotEmpty(processInfo.getErpProcessNumber())) {
                        int orderPpbomId = saveDto.getOrderPPBomId();
                        TBusOrderPPBom orderPpbom = orderPPBomRepository.findById(orderPpbomId).orElse(null);
                        // 计划锅数
                        int pot = tBusOrderHead.getBodyPotQty() == null ? 0 : tBusOrderHead.getBodyPotQty();
                        // 获取工序和用料id对应的累计投入数量
                        float sumImportRecordQty = orderProcessRecordRepository.getBGSumRecordQty(saveDto.getProcessId(), orderPpbomId, saveDto.getRecordTypeBg());
                        sumImportRecordQty += saveDto.getRecordQty();//累加本次提交的数量
                        // 计划投入总数量
                        float sumPlanImportQty = 0;
                        if (orderPpbom != null) {
                            sumPlanImportQty = orderPpbom.getMustQty().floatValue();//orderPpbom.getMidPpbomEntryWeighDeveptQty() * pot;
                        }
                        /*if (sumPlanImportQty <= 0) {
                            throw new RuntimeException("投入超额校验处理异常，计划投入总数量为0，请检查下计划锅数和开发分子");
                        }*/
                        // 计算当前投入比例 = 累计投入数量/计划投入总数量*100
                        float importProportion = BigDecimalUtil.format(sumImportRecordQty / sumPlanImportQty * 100, 2);
                        checkVo.setImportProportion(importProportion);
                        checkVo.setImportWarnProportionMin(warnParamMin);
                        checkVo.setImportWarnProportionMax(warnParamMax);
                        checkVo.setImportExcessProportionMax(excessParamMax);
                        checkVo.setSumImportRecordQty(sumImportRecordQty);
                        checkVo.setSumImportRecordUnit(orderPpbom.getMidPpbomEntryWeighDeveptUnit());
                        checkVo.setSumPlanImportQty(sumPlanImportQty);
                        checkVo.setSumPlanImportUnit(orderPpbom.getMidPpbomEntryWeighDeveptUnit());
                        checkVo.setIsProcessBL(0);//是否拌料
                        return checkVo;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 根据前道订单获取投入或者产出。返回-1代表不需要校验
     *
     * @param orderNo
     * @param recordType
     * @return
     */
    public Float getBGSumRecordQtyPrevOrder(String orderNo, String recordType, String recordTypeBg) {
        // 如果报工类型不为正常，获取前道则不过滤报工类型
        if (!recordTypeBg.equals("REPORTYPE0001")) {
            recordTypeBg = "";
        }
        TBusOrderHead tBusOrderHead = orderHeadRepository.getByOrderNo(orderNo);
        TSysCraftInfo tSysCraftInfo = tBusOrderHead.getCraftId();
        if (null != tSysCraftInfo.getPrevCraftId() && tSysCraftInfo.getPrevCraftId() > 0) {
            // 过滤需求单号不为空
            if (StringUtils.isNotBlank(tBusOrderHead.getMidMoSaleOrderNo())) {
                // 前道工艺路线的最后一个工序信息
                TSysProcessInfo prevProcessInfo = craftInfoService.findLastProcessIdByCraftId(tSysCraftInfo.getPrevCraftId());
                // 前道订单信息
                List<TBusOrderHead> orderHeadPrevList = orderHeadRepository.getByMidMoSaleOrderNoAndCraftId(tBusOrderHead.getMidMoSaleOrderNo(), tSysCraftInfo.getPrevCraftId());
                if (orderHeadPrevList.size() > 0) {
                    TBusOrderHead orderHeadPrev = orderHeadPrevList.get(0);
                    float sumRecordQtyPrevOrder = orderProcessRecordRepository.getBGSumRecordQtyPrevOrder(prevProcessInfo.getProcessId(), tBusOrderHead.getMidMoSaleOrderNo(), orderHeadPrev.getOrderNo(), recordType, recordTypeBg);
                    return sumRecordQtyPrevOrder;
                }
            }
        }
        return -1f;
    }

    /**
     * 报工提交获取当前锅数
     *
     * @param orderProcessId
     * @param orderPPBomId
     * @return
     */
    private int getAllPot(Integer orderProcessId, Integer orderPPBomId) {

        Integer all = orderProcessHistoryRepository.getOneHistory(orderProcessId, orderPPBomId);
        if (all == null || all != 1) {
            return 1;
        }
        Integer pot=orderProcessHistoryRepository.breakHistory(orderProcessId,orderPPBomId);
        return pot;
    }

    @Transactional
    @Override
    public ResponseResult submitAndBindCode(String userId, List<OrderBindCodeDto> orderBindCodeDtoList) throws ParseException {
        for (var orderBindCodeDto : orderBindCodeDtoList) {
            var saveDto = orderBindCodeDto.getOrderRecordSaveDto();
            Integer orderProcessHistoryId = appOrderProcessRecordSubmitService.submit(saveDto, userId);
            TBusOrderProcessHistory tBusOrderProcessHistory = orderProcessHistoryRepository.findById(orderProcessHistoryId).get();
            orderBindCodeDto.setOrderProcessRecordId(tBusOrderProcessHistory.getOrderProcessRecordId());
            orderBindCodeDto.setOrderProcessHistoryId(tBusOrderProcessHistory.getOrderProcessHistoryId());
        }
        //处理报工ID和历史记录ID
        chargingBasketService.bindCheckMesStartTask(orderBindCodeDtoList);
        return ResultUtil.success();
    }

//    /**
//     * 报工完增加报工锅数流水
//     *
//     * @param saveDto
//     * @param userId
//     */
//    private void insertPot(OrderRecordSaveDto saveDto, String userId) {
//        //获取统一锅数标识
//        Integer deviceId = saveDto.getDeviceId();
//        Integer devicePersonId = saveDto.getDevicePersonId();
//        Integer orderProcessId = saveDto.getOrderProcessId();
//        Integer orderPPBomId = saveDto.getOrderPPBomId();
//        int sameFlag = orderProcessPotRepository.getSameFlag(deviceId, devicePersonId, orderProcessId, orderPPBomId);
//        TBusOrderProcessPot tBusOrderProcessPot = new TBusOrderProcessPot();
//        tBusOrderProcessPot.setSameFlag(sameFlag);
//        tBusOrderProcessPot.setCreatedName(userId);
//        tBusOrderProcessPot.setCreatedTime(new Date());
//        tBusOrderProcessPot.setDeviceId(deviceId);
//        tBusOrderProcessPot.setDevicePersonId(devicePersonId);
//        tBusOrderProcessPot.setOrderPpbomId(orderPPBomId);
//        tBusOrderProcessPot.setOrderProcessId(orderProcessId);
//        orderProcessPotRepository.saveAndFlush(tBusOrderProcessPot);
//    }

//    @Override
//    public void customSubmit(OrderRecordSaveDto saveDto, String userId) {
//        TSysPersonnelInfo personnelInfo = tSysPersonnelInfoRepository.findByUserId(userId);
//        List<TSysClassGroupLeaderRel> rels = classGroupLeaderRepository.findByPersonnelId(personnelInfo.getPersonnelId());
//        TSysClassGroupLeaderRel rel = rels.get(0);
//        TSysProcessInfo processInfo = tSysProcessInfoRepository.findById(saveDto.getProcessId()).get();
//        TSysClass tSysClass = tSysClassRepository.findById(rel.getClassId()).get();
////        List<TBusOrderHead> heads=orderHeadRepository.findByOrderNo(saveDto.getOrderNo());
////        TSysClass tSysClass=heads.get(0).getClassId();
//        List<Map> maps = orderProcessRecordRepository.getBGRecord(saveDto.getOrderNo(), saveDto.getProcessId(), tSysClass.getClassId());
//        String mapStr = JSON.toJSONString(maps);
//        List<OrderProcessRecordVo> recordVos = JSON.parseArray(mapStr, OrderProcessRecordVo.class);
//        if (recordVos.size() > 0) {
//            TBusOrderProcessRecord record = new TBusOrderProcessRecord();
//            BeanUtils.copyProperties(saveDto, record);
//            record.setReportTime(new Date());
//            record.setClassId(tSysClass);
//            record.setPersonId(personnelInfo);
//            record.setProcessId(processInfo);
//            if (saveDto.getMaterialId() != null) {
//                for (OrderProcessRecordVo recordVo : recordVos) {
//                    if (recordVo.getMaterialId() != null && record.getMaterialId() == saveDto.getMaterialId()) {
//                        record.setRecordQty(recordVo.getRecordQty() + saveDto.getRecordQty());
//                        record.setOrderProcessRecordId(recordVo.getOrderProcessRecordId());
//                    } else if (saveDto.getMaterialName().contains("返工订单")) {
//                        if (saveDto.getMaterialName().equals(recordVo.getMaterialName())) {
//                            record.setRecordQty(recordVo.getRecordQty() + saveDto.getRecordQty());
//                            record.setOrderProcessRecordId(recordVo.getOrderProcessRecordId());
//                        }
//                    } else {
//                        record.setRecordQty(saveDto.getRecordQty());
//                    }
//                }
//            }
//            record.setRecordUnit(saveDto.getRecordUnit());
//            record.setBusType("BG");
//            orderProcessRecordRepository.saveAndFlush(record);
//        }
//        TBusOrderProcessHistory history = new TBusOrderProcessHistory();
//        BeanUtils.copyProperties(saveDto, history);
//        history.setClassId(tSysClass);
//        history.setPersonId(personnelInfo);
//        history.setProcessId(processInfo);
//        history.setReportTime(new Date());
//        history.setBusType("BG");
//        history.setReportStatus("1");
//        orderProcessHistoryRepository.saveAndFlush(history);
//    }

    @Override
    public ChopAndMixVo chopAndMixMsg(OrderProcessRecordSearchDto searchDto, String userId) {
        if (searchDto.getOrderProcessId() == null || searchDto.getOrderProcessId() == 0) {
            throw new RuntimeException("工序执行表ID不能为空");
        }
        TBusOrderProcess tBusOrderProcess = orderProcessRepository.findById(searchDto.getOrderProcessId()).orElse(null);
        //TSysProcessInfo processInfo = tBusOrderProcess.getProcessId();
        TSysClass tSysClass = tBusOrderProcess.getClassId();
//        TSysPersonnelInfo personnelInfo = tSysPersonnelInfoRepository.findByUserId(userId);
        TBusOrderHead orderHead = orderHeadRepository.findById(searchDto.getOrderId()).get();
        ChopAndMixVo mixVo = new ChopAndMixVo();
        Float recordQty = null;
        if (searchDto.getMaterialId() != null) {
            recordQty = orderProcessRecordRepository.getBGRecordWeight(searchDto.getOrderNo(), searchDto.getProcessId(), tSysClass.getClassId());
//            recordQty = orderProcessRecordRepository.getBGRecordWeight(searchDto.getOrderNo(), searchDto.getProcessId(), 22 );
        } else {
            recordQty = 0f;
        }
        if (recordQty == null) {
            recordQty = 0f;
        }
        //累计重量
        mixVo.setRecordQty(recordQty);
        //累计锅数:累计锅数=累计重量/每锅数量
        if (recordQty != null)
            mixVo.setBodyPotQty((int) (recordQty / orderHead.getBodyOnePotQty() + 0.5f));
        //未完成重量:未完成重量=(每锅数量*锅数)-累计重量；
        Float unDoneWeight = (orderHead.getBodyOnePotQty() * orderHead.getBodyPotQty()) - recordQty;
        unDoneWeight = Math.round(unDoneWeight * 100) / 100f;
        if (unDoneWeight < 0) {
            unDoneWeight = 0f;
        }
        mixVo.setUndoneWeight(unDoneWeight);
        //累计完成=累计重量/(累计重量+未完成重量)；单位%，保留一位小数
        Float finishNum = recordQty / (recordQty + mixVo.getUndoneWeight());
        finishNum = Math.round(finishNum * 100) / 1f;
        String finishQty = finishNum + "%";
        mixVo.setFinishQty(finishQty);
        //未完成=100%-累计完成；单位%，保留一位小数
        mixVo.setUndoneQty((100f - finishNum) + "%");
        return mixVo;
    }


    @Override
    public OrderRecordHeadVo getRecords(OrderProcessRecordSearchDto searchDto, String userId) {
        //  数据有效性验证
        getRecordsVerify(searchDto, userId);
        if (orderHeadRepository.findById(searchDto.getOrderId()).isEmpty()) {
            return null;
        }
        if (searchDto.getOrderProcessId() == null || searchDto.getOrderProcessId() == 0) {
            throw new RuntimeException("工序执行表ID不能为空");
        }
        TBusOrderProcess tBusOrderProcess = orderProcessRepository.findById(searchDto.getOrderProcessId()).orElse(null);
        //TSysPersonnelInfo personnelInfo = tBusOrderProcess.getPersonId();
//        TSysProcessInfo processInfo = tBusOrderProcess.getProcessId();
        TSysClass tSysClass = tBusOrderProcess.getClassId();
        TBusOrderHead head = orderHeadRepository.findById(searchDto.getOrderId()).get();
//        TSysPersonnelInfo personnelInfo = tSysPersonnelInfoRepository.findByUserId(userId);
        //判断customWorkerCategoryFlag(0-普通报工，1-自定义报工)自定义报工记录标识
        String flag = searchDto.getCustomWorkerCategoryFlag();
        List<String> recordTypeList = new ArrayList<>();
        if ("0".equals(flag)) {
            recordTypeList = Arrays.asList("1", "2", "3", "5", "6");
        } else if ("1".equals(flag)) {
            recordTypeList = Arrays.asList("4");
        }
        List<Map> maps = orderProcessRecordRepository.getBGRecordByRecordtype(searchDto.getOrderNo(),
                searchDto.getProcessId(),
                tSysClass.getClassId(),
                recordTypeList,
                searchDto.getSelectMaterialName(),
                searchDto.getSelectDevicePersonnelId(),
                searchDto.getSelectDeviceId(),
                searchDto.getSelectStation());
        String mapStr = JSON.toJSONString(maps);
        List<OrderRecordVo> recordVos = JSON.parseArray(mapStr, OrderRecordVo.class);
        long start = System.currentTimeMillis();
        //物料下拉列表
        HashSet<String> materials = new HashSet<>();
        //岗位下拉列表
        HashSet<StationVo> stations = new HashSet<>();
        //操作员下拉列表
        HashSet<DevicePersonVo> devicePersonVos = new HashSet<>();
        //机台下拉列表
        HashSet<DeviceVo> deviceVos = new HashSet<>();
        //存放人员分组id
        HashSet<String> devicePersons = new HashSet<>();
        //存放机台分组id
        HashSet<String> deviceGroupIds = new HashSet<>();
        OrderRecordHeadVo headVo = new OrderRecordHeadVo();
        HashMap<String, String> personGroupMap = new HashMap<>();
        HashMap<String, String> deviceGroupMap = new HashMap<>();
        HashMap<String, String> personStationGroupMap = new HashMap<>();
        recordVos.stream().forEach(recordVo -> {
            recordVo.setRecordUnitStr(GlobalConstant.getCodeDscName("UNIT0000", recordVo.getRecordUnit()));
            TBusOrderBindCode tBusOrderBindCode = orderBindCodeRepository.findByOrderProcessHistoryId(recordVo.getOrderProcessHistoryId());
            recordVo.setBindCodeNumber(tBusOrderBindCode == null ? "" : tBusOrderBindCode.getBindCodeNumber());
            if (recordVo.getRecordType().equals("5")) {
                recordVo.setMaterialName(LichengConstants.ORDER_REPORT_MATERIAL_NAME_5);//订单串联投入扫码时，报工提交，会保存上道订单的物料名称，这里特殊处理
            }
            //机台和机台手名称多选
            if (StringUtils.isNotEmpty(recordVo.getDeviceGroupId())) {
                if (deviceGroupMap.containsKey(recordVo.getDeviceGroupId())) {
                    recordVo.setDeviceGroups(deviceGroupMap.get(recordVo.getDeviceGroupId()));
                } else {
                    String deviceGroups = orderProcessDeviceRelService.getDeviceGroupNames(recordVo.getDeviceGroupId());
                    recordVo.setDeviceGroups(deviceGroups);
                    deviceGroupMap.put(recordVo.getDeviceGroupId(), deviceGroups);
                }
            }
            if (StringUtils.isNotEmpty(recordVo.getDevicePersonGroupId())) {
                if (personGroupMap.containsKey(recordVo.getDevicePersonGroupId())) {
                    recordVo.setDevicePersonGroups(personGroupMap.get(recordVo.getDevicePersonGroupId()));
                } else {
                    String personGroups = orderProcessPersonRelService.getPersonGroupNames(recordVo.getDevicePersonGroupId());
                    recordVo.setDevicePersonGroups(personGroups);
                    personGroupMap.put(recordVo.getDevicePersonGroupId(), personGroups);
                }
            }
            //添加岗位
            if (StringUtils.isNotEmpty(recordVo.getDevicePersonGroupId())) {
                if (personStationGroupMap.containsKey(recordVo.getDevicePersonGroupId())) {
                    recordVo.setDevicePersonStation(personStationGroupMap.get(recordVo.getDevicePersonGroupId()));
                } else {
                    devicePersons.add(recordVo.getDevicePersonGroupId());
                    Map devicePersonStation = orderProcessPersonRelRepository.getDevicePersonStation(recordVo.getDevicePersonGroupId());
                    if (devicePersonStation != null && devicePersonStation.get("station_name") != null) {
                        String station_name = devicePersonStation.get("station_name").toString();
                        recordVo.setDevicePersonStation(station_name);
                        personStationGroupMap.put(recordVo.getDevicePersonGroupId(), station_name);
                    }
                }
            }
            if (StringUtils.isNotEmpty(recordVo.getDeviceGroupId())) {
                deviceGroupIds.add(recordVo.getDeviceGroupId());
            }
//            // 操作员列表添加数据
//            if (recordVo.getDevicePersonId() != null && StringUtils.isNotEmpty(recordVo.getDevicePersonName())) {
//                DevicePersonVo devicePersonVo = new DevicePersonVo();
//                devicePersonVo.setPersonId(recordVo.getDevicePersonId());
//                devicePersonVo.setPersonName(recordVo.getDevicePersonName());
//                devicePersonVos.add(devicePersonVo);
//            }
//            //机台列表添加数据
//            if (recordVo.getDeviceId() != null && StringUtils.isNotEmpty(recordVo.getDeviceName())) {
//                DeviceVo deviceVo = new DeviceVo();
//                deviceVo.setDeviceId(recordVo.getDeviceId());
//                deviceVo.setDeviceName(recordVo.getDeviceName());
//                deviceVos.add(deviceVo);
//            }

            //物料下拉列表添加数据
            if (StringUtils.isNotEmpty(recordVo.getMaterialName())) {
                materials.add(recordVo.getMaterialName());
            }
        });
        //岗位列表添加数据
        List<Map> stationList = orderProcessPersonRelRepository.getStationListByGroup(devicePersons);
        List<StationVo> stationVos = JSON.parseArray(JSON.toJSONString(stationList), StationVo.class);
        stations.addAll(stationVos);

        //操作员列表添加数据
        List<Map> devicePersonList = orderProcessPersonRelRepository.getDevicePersonListByGroup(devicePersons);
        List<DevicePersonVo> devicePersonVoList = JSON.parseArray(JSON.toJSONString(devicePersonList), DevicePersonVo.class);
        devicePersonVos.addAll(devicePersonVoList);

        //操作员列表添加数据
        List<Map> deviceList = orderProcessPersonRelRepository.getDeviceListByGroup(deviceGroupIds);
        List<DeviceVo> deviceVoList = JSON.parseArray(JSON.toJSONString(deviceList), DeviceVo.class);
        deviceVos.addAll(deviceVoList);


        long end = System.currentTimeMillis();
        System.out.println("耗费时间" + ((end - start) / 1000));
        BeanUtils.copyProperties(head, headVo);
        headVo.setRecordVoList(recordVos);
        headVo.setBodyMaterialName(head.getBodyMaterialName());
        TSysProcessInfo tSysProcessInfo = tSysProcessInfoRepository.findById(searchDto.getProcessId()).get();
        headVo.setProcessName(tSysProcessInfo.getProcessName());
        headVo.setMaterials(materials);
        headVo.setDevicePersonVos(devicePersonVos);
        headVo.setDeviceVos(deviceVos);
        headVo.setStations(stations);
        return headVo;
    }

    /**
     * 查询报工记录数据有效性验证
     *
     * @param searchDto
     * @param userId
     */
    private void getRecordsVerify(OrderProcessRecordSearchDto searchDto, String userId) {
        if (StringUtils.isEmpty(userId)) {
            throw new RuntimeException("当前操作用户无效");
        }
        searchDto.setSelectMaterialName(StringUtils.isEmpty(searchDto.getSelectMaterialName()) ? "" : searchDto.getSelectMaterialName());
        searchDto.setSelectStation(StringUtils.isEmpty(searchDto.getSelectStation()) ? "" : searchDto.getSelectStation());
        searchDto.setSelectDeviceId(searchDto.getSelectDeviceId() == null ? -1 : searchDto.getSelectDeviceId());
        searchDto.setSelectDevicePersonnelId(searchDto.getSelectDevicePersonnelId() == null ? -1 : searchDto.getSelectDevicePersonnelId());
    }

//    private String getDeviceGroupNames(String deviceGroupId){
//        if(StringUtils.isNotEmpty(deviceGroupId)){
//            List<TBusOrderProcessDeviceRel> deviceRels=orderProcessDeviceRelService.findByDeviceGroupId(deviceGroupId);
//            StringBuilder deviceBuf=new StringBuilder();
//            for (TBusOrderProcessDeviceRel deviceRel : deviceRels) {
//                TsysDevice device=deviceRepository.findById(deviceRel.getDeviceId()).orElse(null);
//                deviceBuf.append(device.getDeviceName()+",");
//            }
//            if(deviceBuf.length()>0){
//                return deviceBuf.substring(0,deviceBuf.lastIndexOf(","));
//            }
//        }
//        return null;
//    }

//    private String getPersonGroupNames(String personGroupId){
//        if(StringUtils.isNotEmpty(personGroupId)){
//            List<TBusOrderProcessPersonRel> personRels=orderProcessPersonRelRepository.findAllByDevicePersonGroupId(personGroupId);
//            StringBuilder personBuf=new StringBuilder();
//            for (TBusOrderProcessPersonRel personRel : personRels) {
//                TSysPersonnelInfo personnelInfo=tSysPersonnelInfoRepository.findById(personRel.getDevicePersonId()).orElse(null);
//                personBuf.append(personnelInfo.getName()+",");
//            }
//            if(personBuf.length()>0){
//                return personBuf.substring(0,personBuf.lastIndexOf(","));
//            }
//        }
//        return null;
//    }

    @Override
    public List<OrderRecordVo> getHistories(OrderProcessRecordSearchDto searchDto, String userId) {
        if (searchDto.getOrderProcessId() == null || searchDto.getOrderProcessId() == 0) {
            throw new RuntimeException("工序执行表ID不能为空");
        }
        TBusOrderProcess tBusOrderProcess = orderProcessRepository.findById(searchDto.getOrderProcessId()).orElse(null);
        TSysProcessInfo processInfo = tBusOrderProcess.getProcessId();
        TSysClass tSysClass = tBusOrderProcess.getClassId();
        List<Map> maps = orderProcessHistoryRepository.getBGHistoryRecord(searchDto.getOrderNo(), searchDto.getProcessId(),
                tSysClass.getClassId());
        String mapStr = JSON.toJSONString(maps);
        List<OrderRecordVo> recordVos = JSON.parseArray(mapStr, OrderRecordVo.class);
        List<OrderRecordVo> recordVoList = new ArrayList<>();
        for (OrderRecordVo recordVo : recordVos) {
            if (searchDto.getMaterialId() != null && searchDto.getMaterialId().equals(recordVo.getMaterialId())) {
                //机台和机台手名称多选
                if (StringUtils.isNotEmpty(recordVo.getDeviceGroupId())) {
                    String deviceGroups = orderProcessDeviceRelService.getDeviceGroupNames(recordVo.getDeviceGroupId());
                    recordVo.setDeviceGroups(deviceGroups);
                }
                if (StringUtils.isNotEmpty(recordVo.getDevicePersonGroupId())) {
                    String personGroups = orderProcessPersonRelService.getPersonGroupNames(recordVo.getDevicePersonGroupId());
                    recordVo.setDevicePersonGroups(personGroups);
                }
                recordVoList.add(recordVo);
                recordVo.setRecordUnitStr(GlobalConstant.getCodeDscName("UNIT0000", recordVo.getRecordUnit()));
            }
        }
        return recordVoList;
    }


    @Override
    public PageVo<CraftProcessListVo> getCustomWCOrderProcess(Integer current, Integer size, Integer orderId) {
        Sort sort1 = Sort.by(Sort.Direction.ASC, "t1.sort");
        PageRequest of = PageRequest.of(current, size, sort1);


        //todo 通过订单ID获取工艺路线ID
//        List<Object[]> select = orderHeadRepository.getProcessInfoList(orderId, of);
        List<Map> select = orderHeadRepository.getProcessInfoList(orderId, of);
        try {
            PageVo<CraftProcessListVo> pageVo = new PageVo(size, current);
//            List<CraftProcessListVo> castEntity = EntityUtils.castEntity(select, CraftProcessListVo.class, new CraftProcessListVo());
            List<CraftProcessListVo> castEntity = JSON.parseArray(JSON.toJSONString(select), CraftProcessListVo.class);

            int total = orderHeadRepository.getCountProcessInfoList(orderId);
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
    public List<ResModalVo> getTheResidualFilms(TheResidualFilmSearchDto searchDto) {
        List<ResModalVo> resModalVos = new ArrayList<>();
        var materialIds = searchDto.getMaterialIds();
        materialIds.stream().forEach(materialId -> {
            var resModalVo = this.getTheResidualFilm(materialId, searchDto.getGetValue(), searchDto.getBeatTimes());
            resModalVos.add(resModalVo);
        });
        return resModalVos;
    }

    /**
     * 计算两种膜的使用使用量
     * 使用膜长=跑动次数*跑动一次使用膜长（跑动一次使用膜长固定“500mm”，做成数据字典。）
     * 使用膜重量=使用膜长*膜密度*膜宽度*膜厚度
     * 膜密度、膜宽度、膜厚度：通过前端传入膜的编码关联物料表获取。
     * 膜密度换算为kg/m³、膜厚度换算为m、膜厚度换算为m、膜长换算为m、重量换算为kg
     *
     * @param materialId 用料物料id
     * @param getValue   使用膜长
     * @return
     */

    @Override
    public ResModalVo getTheResidualFilm(Integer materialId, Float getValue) {
        ResModalVo resModalVo = new ResModalVo();
        MidMaterial midMaterial = midMaterialRepository.getBykdMaterialId(materialId);
        if (midMaterial == null) {
            return resModalVo;
        }
        resModalVo.setMaterialId(materialId);
        resModalVo.setMaterialNumber(midMaterial.getKdMaterialNumber());
        resModalVo.setMaterialName(midMaterial.getKdMaterialName());
        Float kdMaterialNetWeight = midMaterial.getKdMaterialNetWeight() == null ? 0 : midMaterial.getKdMaterialNetWeight();
        Float kdMaterialMembraneWidth = midMaterial.getKdMaterialMembraneWidth() == null ? 0 : midMaterial.getKdMaterialMembraneWidth();//膜宽度
        Float kdMaterialMembraneDensity = midMaterial.getKdMaterialMembraneDensity() == null ? 0 : midMaterial.getKdMaterialMembraneDensity();//膜密度
        Float kdMaterialMembraneThickness = midMaterial.getKdMaterialMembraneThickness() == null ? 0 : midMaterial.getKdMaterialMembraneThickness();//膜厚度
        resModalVo.setKdMaterialMembraneDensity(kdMaterialMembraneDensity);
        resModalVo.setKdMaterialMembraneWidth(kdMaterialMembraneWidth);
        resModalVo.setKdMaterialMembraneThickness(kdMaterialMembraneThickness);
        resModalVo.setKdMaterialNetWeight(kdMaterialNetWeight);
        // 使用膜重量=使用膜长*膜密度*膜宽度*膜厚度，单位为千克
        Float value = getValue * (kdMaterialMembraneDensity * 1000) * (kdMaterialMembraneWidth / 100) * (kdMaterialMembraneThickness / 100);
        value = BigDecimalUtil.format(value, 3);
        resModalVo.setValue(value);
        return resModalVo;
    }

    /**
     * 计算两种膜的使用使用量
     * 使用膜长=跑动次数*跑动一次使用膜长（跑动一次使用膜长固定“500mm”，做成数据字典。）
     * 使用膜重量=使用膜长*膜密度*膜宽度*膜厚度
     * 膜密度、膜宽度、膜厚度：通过前端传入膜的编码关联物料表获取。
     * 膜密度换算为kg/m³、膜厚度换算为m、膜厚度换算为m、膜长换算为m、重量换算为kg
     *
     * @param materialId 用料物料id
     * @param getValue   使用膜长
     * @param beatTimes  跳动次数
     * @return
     */
    @Override
    public ResModalVo getTheResidualFilm(Integer materialId, Float getValue, Float beatTimes) {
        ResModalVo resModalVo = new ResModalVo();
        MidMaterial midMaterial = midMaterialRepository.getBykdMaterialId(materialId);
        if (midMaterial == null) {
            return resModalVo;
        }
        resModalVo.setMaterialId(materialId);
        resModalVo.setMaterialNumber(midMaterial.getKdMaterialNumber());
        resModalVo.setMaterialName(midMaterial.getKdMaterialName());
        Float kdMaterialNetWeight = midMaterial.getKdMaterialNetWeight() == null ? 0 : midMaterial.getKdMaterialNetWeight();
        Float kdMaterialMembraneWidth = midMaterial.getKdMaterialMembraneWidth() == null ? 0 : midMaterial.getKdMaterialMembraneWidth();//膜宽度
        Float kdMaterialMembraneDensity = midMaterial.getKdMaterialMembraneDensity() == null ? 0 : midMaterial.getKdMaterialMembraneDensity();//膜密度
        Float kdMaterialMembraneThickness = midMaterial.getKdMaterialMembraneThickness() == null ? 0 : midMaterial.getKdMaterialMembraneThickness();//膜厚度
        resModalVo.setKdMaterialMembraneDensity(kdMaterialMembraneDensity);
        resModalVo.setKdMaterialMembraneWidth(kdMaterialMembraneWidth);
        resModalVo.setKdMaterialMembraneThickness(kdMaterialMembraneThickness);
        resModalVo.setKdMaterialNetWeight(kdMaterialNetWeight);
        // 使用膜长=跑动次数*跑动一次使用膜长（跑动一次使用膜长固定“500mm”，做成数据字典。）
        if (beatTimes != null && beatTimes > 0) {
            List<TSysCodeDsc> tSysCodeDscAParams = tSysCodeDscRepository.findByCodeClIdAndCodeDsc("MEMBRANE0000", "跑动一次使用膜长");
            var tSysCodeDsc = tSysCodeDscAParams.stream().findFirst().orElse(null);
            float aParam = 0f;
            try {
                aParam = Float.parseFloat(tSysCodeDsc.getCodeValue());
            } catch (NumberFormatException e) {
                throw new RuntimeException("获取拉伸膜参数【跑动一次使用膜长】发生异常");
            }
            Float allInMembrane = BigDecimalUtil.mul(beatTimes, aParam).floatValue();
            allInMembrane = BigDecimalUtil.format(allInMembrane, 3);
            resModalVo.setAllInMembrane(allInMembrane);
            // 使用膜重量=使用膜长*膜密度*膜宽度*膜厚度，单位为千克
            // 使用膜重量=跑动次数*0.5*(膜密度*1000)*(膜宽度/100)*(膜厚度/100) 2022-09-20 任务14515
            Float value = allInMembrane * (kdMaterialMembraneDensity * 1000) * (kdMaterialMembraneWidth / 100) * (kdMaterialMembraneThickness / 100);
            value = BigDecimalUtil.format(value, 3);
            resModalVo.setValue(value);
        } else {
            // 使用膜重量=使用膜长*膜密度*膜宽度*膜厚度，单位为千克
            Float value = getValue * (kdMaterialMembraneDensity * 1000) * (kdMaterialMembraneWidth / 100) * (kdMaterialMembraneThickness / 100);
            value = BigDecimalUtil.format(value, 3);
            resModalVo.setValue(value);
        }
        return resModalVo;
    }

    @Override
    public ResponseResult<WeightVo> getTotalWeight(OrderProcessRecordSearchDto searchDto, String userId) {
        if (searchDto.getMaterialId() == null) {
            ResultUtil.error("物料不存在");
        }
        if (searchDto.getOrderProcessId() == null || searchDto.getOrderProcessId() == 0) {
            throw new RuntimeException("工序执行表ID不能为空");
        }
        TBusOrderProcess tBusOrderProcess = orderProcessRepository.findById(searchDto.getOrderProcessId()).orElse(null);
        //TSysPersonnelInfo personnelInfo = tBusOrderProcess.getPersonId();
        TSysProcessInfo processInfo = tBusOrderProcess.getProcessId();
        TSysClass tSysClass = tBusOrderProcess.getClassId();
        TSysPersonnelInfo personnelInfo = tSysPersonnelInfoRepository.findByUserId(userId);
        Float weight = null;
        String unit = null;
        WeightVo vo = new WeightVo();
        if (searchDto.getMaterialId() != null) {
            List<Map> maps = orderProcessRecordRepository.getBGRecordWeight(searchDto.getOrderNo(), searchDto.getProcessId(), tSysClass.getClassId(), searchDto.getMaterialId());
            String mapStr = JSON.toJSONString(maps);
            List<WeightVo> recordVos = JSON.parseArray(mapStr, WeightVo.class);
            if (recordVos.size() > 0) {
                weight = recordVos.get(0).getRecordQty();
                vo.setUnit(recordVos.get(0).getRecordUnit());
            }
        }
        if (weight == null) {
            weight = 0f;
            vo.setUnit("kg");
        }

        vo.setValue(weight);
        vo.setUnitStr(GlobalConstant.getCodeDscName("UNIT0000", vo.getUnit()));
        return ResultUtil.success(vo);
    }

    @Override
    public ResponseResult<WeightVo> getTotalAbImport(OrderProcessRecordSearchDto searchDto) {
        WeightVo vo = new WeightVo();
        // 类目类型为AB料投入
//        List<TBusOrderProcessHistory> histories = orderProcessHistoryRepository.findAllByOrderProcessIdAndBusTypeAndRecordType(searchDto.getOrderProcessId(), "BG", "6");
        List<TBusOrderProcessHistory> histories = orderProcessHistoryRepository.getBgOrderProcessRecordsAndMaterialNumberLike(searchDto.getOrderProcessId(), "6", "%");
        vo.setValue(Float.valueOf(histories.size() + ""));
        vo.setUnit(LichengConstants.UNIT_GUO);
        vo.setUnitStr(LichengConstants.UNIT_GUO_NAME);
        return ResultUtil.success(vo);
    }

    @Override
    public ResponseResult<WeightTotalAbExportVo> getTotalAbExport(OrderProcessRecordSearchDto searchDto) {
        WeightTotalAbExportVo vo = new WeightTotalAbExportVo();
        // 类目类型为二级品数量
        List<TBusOrderProcessRecord> records = orderProcessRecordRepository.findAllByOrderProcessIdAndBusTypeAndRecordType(searchDto.getOrderProcessId(), "BG", "2");
        for (var record : records) {
            if (LichengConstants.RECORDTYPEL20001.equals(record.getMaterialName())) {
                vo.setValueA(BigDecimalUtil.add(vo.getValueA(), record.getRecordQty()).floatValue());
                vo.setUnitA(record.getRecordUnit());
                vo.setUnitStrA(GlobalConstant.getCodeDscName("UNIT0000", vo.getUnitA()));
            } else if (LichengConstants.RECORDTYPEL20002.equals(record.getMaterialName())) {
                vo.setValueB(BigDecimalUtil.add(vo.getValueB(), record.getRecordQty()).floatValue());
                vo.setUnitB(record.getRecordUnit());
                vo.setUnitStrB(GlobalConstant.getCodeDscName("UNIT0000", vo.getUnitB()));
            } else if (LichengConstants.RECORDTYPEL20003.equals(record.getMaterialName())) {
                vo.setValueFl(BigDecimalUtil.add(vo.getValueFl(), record.getRecordQty()).floatValue());
                vo.setUnitFl(record.getRecordUnit());
                vo.setUnitStrFl(GlobalConstant.getCodeDscName("UNIT0000", vo.getUnitFl()));
            } else if (LichengConstants.RECORDTYPEL20000_1.equals(record.getMaterialName())) {
                vo.setValueFm(BigDecimalUtil.add(vo.getValueFm(), record.getRecordQty()).floatValue());
                vo.setUnitFm(record.getRecordUnit());
                vo.setUnitStrFm(GlobalConstant.getCodeDscName("UNIT0000", vo.getUnitFm()));
            } else if (LichengConstants.RECORDTYPEL20000_5.equals(record.getMaterialName())) {
                vo.setValueSym(BigDecimalUtil.add(vo.getValueSym(), record.getRecordQty()).floatValue());
                vo.setUnitSym(record.getRecordUnit());
                vo.setUnitStrSym(GlobalConstant.getCodeDscName("UNIT0000", vo.getUnitSym()));
            }
        }
        return ResultUtil.success(vo);
    }

    @Override
    public ResponseResult<QualifiedExportVo> getQualifiedExport(QualifiedExportDao searchDto) {
        QualifiedExportVo vo = new QualifiedExportVo();
        //个人产出：根据工序执行ID+操作员维度统计合格品产出数量合计（手动输入数量），后缀加上单位
        Float singleValue = 0f; //个人产出
        Float totalValue = 0f; //累计产出
        for (Integer devicePersonId : searchDto.getDevicePersonIds()) {
            String groupId = orderProcessPersonRelRepository.getPersonGroupId(searchDto.getOrderProcessId(), new ArrayList() {
                {
                    add(devicePersonId);
                }
            }, 1);
            List<TBusOrderProcessRecord> records = orderProcessRecordRepository.findAllByOrderProcessIdAndDevicePersonGroupIdAndRecordType(searchDto.getOrderProcessId(), groupId, "3");
            for (var record : records) {
                if (LichengConstants.PROCESS_NUMBER_LASHENMO.equals(record.getProcessNumber())
                        || LichengConstants.PROCESS_NUMBER_BAOZHUANG.equals(record.getProcessNumber())) {//拉伸膜、包装
                    if (LichengConstants.UNIT_JIAN.equals(record.getRecordUnit())) {
                        //报工数量（手工输入）
                        singleValue += record.getRecordManualQty();
                    } else {
                        //报工数量
                        singleValue += record.getRecordQty();
                    }
                    vo.setSingleUnit(record.getRecordUnit());
                    vo.setSingleUnitStr(GlobalConstant.getCodeDscName("UNIT0000", record.getRecordUnit()));
                } else {
                    singleValue += record.getRecordQty();
                    vo.setSingleUnit(record.getRecordUnit());
                    vo.setSingleUnitStr(GlobalConstant.getCodeDscName("UNIT0000", record.getRecordUnit()));
                }
            }
            //累计产出：根据订单号+工序编码维度统计合格品产出数量合计（手动输入数量），后缀加上单位
            records = orderProcessRecordRepository.findALLByOrderNoAndProcessNumberAndRecordType(searchDto.getOrderNo(), searchDto.getProcessNumber(), "3");
            for (var record : records) {
                if (LichengConstants.PROCESS_NUMBER_LASHENMO.equals(record.getProcessNumber())
                        || LichengConstants.PROCESS_NUMBER_BAOZHUANG.equals(record.getProcessNumber())) {//拉伸膜、包装
                    if (LichengConstants.UNIT_JIAN.equals(record.getRecordUnit())) {
                        //报工数量（手工输入）
                        totalValue += record.getRecordManualQty();
                    } else {
                        //报工数量
                        totalValue += record.getRecordQty();
                    }
                    vo.setTotalUnit(record.getRecordUnit());
                    vo.setTotalUnitStr(GlobalConstant.getCodeDscName("UNIT0000", record.getRecordUnit()));
                } else {
                    totalValue += record.getRecordQty();
                    vo.setTotalUnit(record.getRecordUnit());
                    vo.setTotalUnitStr(GlobalConstant.getCodeDscName("UNIT0000", record.getRecordUnit()));
                }
            }
        }
        vo.setSingleValue(singleValue);
        vo.setTotalValue(totalValue);
        return ResultUtil.success(vo);
    }

    @Override
    public BigInteger getIotValueByKeyAndDeviceId(long startTs, long endTs, String deviceId, Integer key, String agg) throws Exception {
        if ("MAX".equals(agg)) {
            BigInteger maxValue = orderProcessRecordRepository.getIotMaxValueByKeyAndDeviceId(startTs, endTs, deviceId, key);
            return maxValue;
        } else if ("MIN".equals(agg)) {
            BigInteger minValue = orderProcessRecordRepository.getIotMinValueByKeyAndDeviceId(startTs, endTs, deviceId, key);
            return minValue;
        }
        return null;
    }

    @Override
    public BigInteger getIotDiffValueByKeyAndDeviceId(long startTs, long endTs, UUID deviceId, Integer key) throws Exception {
        BigInteger diffValue = orderProcessRecordRepository.getIotDiffValueByKeyAndDeviceId(startTs, endTs, deviceId, key);
        return diffValue;
    }

//    @Override
//    public BigInteger getIotDiffValueByKeyAndDeviceName(long startTs, long endTs, String deviceId, Integer key) throws Exception {
//        BigInteger diffValue = orderProcessRecordRepository.getIotDiffValueByKeyAndDeviceName(startTs, endTs, deviceId, key);
//        return diffValue;
//    }

    @Override
    public UUID getDeviceIdByName(String deviceCode) throws Exception {
        return orderProcessRecordRepository.getDeviceIdByName(deviceCode);
    }

    @Override
    public Integer getDeviceKeyIdByKey(String keyName) throws Exception {
        return orderProcessRecordRepository.getDeviceKeyIdByKey(keyName);
    }

    @Override
    public OrderProcessTimeVo getProcessTime(OrderProcessTimeDto timeDto) {
        OrderProcessTimeVo vo = new OrderProcessTimeVo();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        List<String> times = null;
        // AB料产出和合格品产出传条件：订单号+工序id+一级类目+二级类目
        times = orderProcessRecordRepository.getBGRecordTime(timeDto);
        if (times.size() > 0 && times.get(0) != null) {
            vo.setStartTime(times.get(0));
            vo.setEndTime(sdf.format(new Date()));
            return vo;
        } else {
            List<Map> maps = orderHeadRepository.getProcessTime(timeDto.getOrderId(), timeDto.getProcessId());
            String mapStr = JSON.toJSONString(maps);
            List<OrderProcessTimeVo> timeVos = JSON.parseArray(mapStr, OrderProcessTimeVo.class);
            if (timeVos.size() > 0) {
                vo = timeVos.get(0);
                vo.setEndTime(sdf.format(new Date()));
                return vo;
            }
            return null;
        }
    }

    @Override
    public OrderProcessTimeVo getIotDeviceTime(OrderProcessIotDeviceTimeDto timeDto) {
        OrderProcessTimeVo vo = new OrderProcessTimeVo();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        List<String> times = orderProcessRecordRepository.getBGRecordTime(timeDto.getDeviceIds());
        if (times.size() > 0) {
            vo.setStartTime(times.get(0));
            vo.setEndTime(sdf.format(new Date()));
        }
        return vo;
    }

    @Override
    public IotDiffValueVo getStretchMachineRunNum(String startTime, String endTime, String deviceCode) throws Exception {
        return this.getDiffValueNum(startTime, endTime, deviceCode, "正常模式跑动次数");
    }

    @Override
    public IotDiffValueVo getStretchMachineRunNum(OrderProcessIotSearchDto searchDto) throws Exception {
        IotDiffValueVo vo = new IotDiffValueVo();
        BigInteger diffValue = new BigInteger("0");
        var startTime = searchDto.getStartTime();
        var endTime = searchDto.getEndTime();
        for (String deviceCode : searchDto.getDeviceCodes()) {
            try {
                IotDiffValueVo vo2 = this.getStretchMachineRunNum(startTime, endTime, deviceCode);
                diffValue = vo2.getDiffValue().add(diffValue);
            } catch (Exception e) {
                logger.error("获取设备发生异常", e);
            }
        }
        vo.setDiffValue(diffValue);
        vo.setEndTime(endTime);
        return vo;
    }

    @Override    public IotDiffValueVo getCuttingMachineRunNum(String startTime, String endTime, String deviceCode) throws Exception {
        return this.getDiffValueNum(startTime, endTime, deviceCode, "正常模式生产支数");
    }

    @Override
    public IotDiffValueVo getCrabLineRunNum(String startTime, String endTime, String deviceCode) throws Exception {
        return this.getDiffValueNum(startTime, endTime, deviceCode, "正常模式生产支数");
    }

    @Override
    public IotDiffValueVo getPackLineRunNum(String startTime, String endTime, String deviceCode) throws Exception {
        return this.getDiffValueNum(startTime, endTime, deviceCode, "生产件数");
    }

    @Override
    public IotDiffValueVo getPackLineRunNum(OrderProcessIotSearchDto searchDto) throws Exception {
        IotDiffValueVo vo = new IotDiffValueVo();
        BigInteger diffValue = new BigInteger("0");
        var startTime = searchDto.getStartTime();
        var endTime = searchDto.getEndTime();
        for (String deviceCode : searchDto.getDeviceCodes()) {
            try {
                IotDiffValueVo vo2 = this.getPackLineRunNum(startTime, endTime, deviceCode);
                diffValue = vo2.getDiffValue().add(diffValue);
            } catch (Exception e) {
                logger.error("获取设备发生异常", e);
            }
        }
        vo.setDiffValue(diffValue);
        vo.setEndTime(endTime);
        return vo;
    }

    @Override
    public IotDiffValueVo getDiffValueNum(@ApiParam(value = "格式：yyyy-MM-dd HH:mm:ss")
                                          @RequestParam(value = "startTime") String startTime,
                                          @ApiParam(value = "格式：yyyy-MM-dd HH:mm:ss")
                                          @RequestParam(value = "endTime") String endTime,
                                          @RequestParam("deviceCode") String deviceCode,
                                          @RequestParam("key") String key) throws Exception {
        IotDiffValueVo vo = new IotDiffValueVo();
//        SimpleTimeZone
        //工序：开始时间，结束时间，设备ID
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate = sdf.parse(startTime);
        Date endDate = new Date();
        if (endTime != null) {
            endDate = sdf.parse(endTime);
        }
        SimpleDateFormat UTCformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        String time1 = UTCformat.format(startDate);
        String time2 = UTCformat.format(endDate);

        long startTs = UTCformat.parse(time1).getTime();
        long endTs = UTCformat.parse(time2).getTime();

        //根据设备编码获取设备ID
//        EntityIdFactory.getByTypeAndId("DEVICE", entityIdStr)
        UUID deviceId = this.getDeviceIdByName(deviceCode);
        if (deviceId == null) {
            throw new RuntimeException("未找到匹配的实体设备ID，请确认设备编码是否正确！");
        }
        //获取key_id
        Integer key_id = this.getDeviceKeyIdByKey(key);
        if (key_id == null) {
            throw new RuntimeException("未匹配到设备数据Key值，请确认key是否正确！");
        }
        //查询差值
        BigInteger diffValue = this.getIotDiffValueByKeyAndDeviceId(startTs, endTs, deviceId, key_id);
//        BigInteger diffValue = orderProcessRecordService.getIotDiffValueByKeyAndDeviceName(startTs,  endTs,  deviceCode,  key_id);
        vo.setDiffValue(diffValue == null ? new BigInteger("0") : diffValue);
        vo.setEndTime(endTime);
        return vo;
    }

    @Override
    public ResponseResult getMaterialNeWeight(String materialNumber) {
//        MidMaterial midMaterial = midMaterialRepository.getByKdMaterialNumber(materialNumber);
        List<MidMaterial> materialNumberList = midMaterialRepository.findAllByKdMaterialNumber(materialNumber);
        MidMaterial midMaterial = materialNumberList.get(0);
        Float weight = 0f;
        if (midMaterial != null) {
            weight = midMaterial.getKdMaterialNetWeight();
        }
        return ResultUtil.success(weight);
    }

    @Override
    public MidMaterial getMidMaterial(Integer materialId) {
        return midMaterialRepository.getBykdMaterialId(materialId);
    }

    @Override
    public List<TBusOrderProcessRecord> findAllByOrderProcessIdAndBusType(Integer orderProcessId, String busType) {
        return orderProcessRecordRepository.findAllByOrderProcessIdAndBusType(orderProcessId, busType);
    }

    @Override
    public List<TBusOrderProcessRecord> findAllByOrderProcessIdAndBusTypeAndRecordTypePd(Integer orderProcessId, String busType, String recordTypePd) {
        return orderProcessRecordRepository.findAllByOrderProcessIdAndBusTypeAndRecordTypePd(orderProcessId, busType, recordTypePd);
    }

    @Override
    public ChopAndMixTotalVo chopAndMixTotal(ChopAndMixTotalSearchDto searchDto, String userId) {
        String groupId = orderProcessPersonRelRepository.getPersonGroupId(searchDto.getOrderProcessId(), searchDto.getDevicePersonIds(), searchDto.getDevicePersonIds().size());
        ChopAndMixTotalVo chopAndMixTotalVo = null;
        switch (searchDto.getProcessType()) {

            default: {
                //默认通用
                chopAndMixTotalVo = getExport(groupId, searchDto, searchDto.getProcessType());
                //产出锅数需要*2
                ChopAndMixTotalData totalData = chopAndMixTotalVo.getTotalData();
                totalData.setQualifiedBodyPotQty(totalData.getQualifiedBodyPotQty() * 2);
            }
        }
        return chopAndMixTotalVo;
    }

    /**
     * 产后数据详情
     */
    private ChopAndMixTotalVo getExport(String groupId, ChopAndMixTotalSearchDto searchDto, String processNumber) {
        ChopAndMixTotalVo vo = new ChopAndMixTotalVo();
        TBusOrderHead tBusOrderHead = orderHeadRepository.findById(searchDto.getOrderId()).orElse(null);
        /*
         * 积累产出数据
         */
        ChopAndMixTotalData allData = new ChopAndMixTotalData();
        //计划数量=计划生产数量（订单表获取）
        Float bodyPlanPrdQty = 0F;
        if (tBusOrderHead.getBodyUnit().equals("kg")) {
            bodyPlanPrdQty = tBusOrderHead.getBodyPlanPrdQty();
        } else {
            BigDecimal bigDecimal = new BigDecimal(tBusOrderHead.getBodyPlanPrdQty().toString());
            BigDecimal bigDecimal2 = new BigDecimal("0.001");
            bodyPlanPrdQty = bigDecimal.multiply(bigDecimal2).floatValue();
        }
        allData.setBodyPlanPrdQty(bodyPlanPrdQty);
        //计划锅数=订单表“锅数”（数量后缀加单位锅）
        Integer bodyPotQty = tBusOrderHead.getBodyPotQty()==null?0:tBusOrderHead.getBodyPotQty();
        allData.setBodyPotQty(bodyPotQty);
        //总合格累计数量=产后合格品报工累计数量（订单编号+工序编码进行统计）
        Float sumExportPotByImportAll = orderPPBomRepository.sumExportPotQtyAllByBL(searchDto.getOrderId(), processNumber);
        allData.setQualifiedQty(sumExportPotByImportAll);
        // 总累计锅数=通过订单编号+工序编码进行统计累计合格品产后报工锅数（提交报工前端传入锅数）（数量后缀加单位锅）
        Float exportPotByImportAll = orderPPBomRepository.sumExportPotAllByBL(searchDto.getOrderId(), processNumber);
        allData.setQualifiedBodyPotQty(exportPotByImportAll);
        //总合格完成数量比例=总合格累计数量/计划数量，单位%，保留两位小数
        float exportRatio = 0;
        if (sumExportPotByImportAll > 0 && bodyPlanPrdQty > 0) {
            BigDecimal bigDecimal = new BigDecimal(sumExportPotByImportAll.toString());
            BigDecimal bigDecimal1 = new BigDecimal(bodyPlanPrdQty.toString());
            exportRatio = bigDecimal.divide(bigDecimal1, 2, RoundingMode.DOWN).floatValue();
        }
        allData.setQualifiedQtyPercent(String.valueOf(exportRatio * 100 > 100 ? 100 : exportRatio * 100) + "%");
        //总完成锅数比例=总累计锅数/计划锅数，单位%，保留两位小数
        float exportPotRatio = 0;
        if (exportPotByImportAll > 0 && bodyPotQty > 0) {
            BigDecimal bigDecimal = new BigDecimal(exportPotByImportAll.toString());
            BigDecimal bigDecimal1 = new BigDecimal(bodyPotQty.toString());
            exportPotRatio = bigDecimal.divide(bigDecimal1, 2, RoundingMode.DOWN).floatValue();
        }
        allData.setQualifiedBodyPotQtyPercent(String.valueOf(exportPotRatio * 100 > 100 ? 100 : exportPotRatio * 100) + "%");
        //积累斗数
        float qualifiedBodyFightQty = exportPotByImportAll * 2;
        allData.setQualifiedBodyFightQty(qualifiedBodyFightQty);
        vo.setTotalData(allData);


        /*
         * 个人产出数据
         */
        ChopAndMixPersonData currentData = new ChopAndMixPersonData();
        //本机排手合格累计数量
        Float personQualifiedQty = 0F;

        //本机排手累计锅数
        Float personQualifiedBodyPotQty = 0F;

        //本机排手合格数量比例
        Float personQualifiedQtyPercent = 0F;

        //本机排手锅数比例
        Float personQualifiedBodyPotQtyPercent = 0F;

        //本机台手斗数
        Float personQualifiedBodyFightQty = 0F;

        if (StringUtils.isNotEmpty(groupId)) {
            //本机排手合格累计数量
            personQualifiedQty = orderPPBomRepository.findExportPotByOne(searchDto.getOrderProcessId(), "-1", groupId);

            //本机排手累计锅数
            personQualifiedBodyPotQty = orderPPBomRepository.findExportPotOne(searchDto.getOrderProcessId(), "-1", groupId);
            //本机台手斗数
            personQualifiedBodyFightQty = personQualifiedBodyPotQty * 2;
            //本机排手合格数量比例。
            if (personQualifiedQty > 0 && sumExportPotByImportAll > 0) {
                BigDecimal bigDecimal = new BigDecimal(sumExportPotByImportAll.toString());
                BigDecimal bigDecimal1 = new BigDecimal(personQualifiedQty.toString());
                personQualifiedQtyPercent = bigDecimal.divide(bigDecimal1, 2, RoundingMode.DOWN).floatValue();
            }
            //本机排手锅数比例。
            if (personQualifiedBodyPotQty > 0 && exportPotByImportAll > 0) {
                BigDecimal bigDecimal = new BigDecimal(exportPotByImportAll.toString());
                BigDecimal bigDecimal1 = new BigDecimal(personQualifiedBodyPotQty.toString());
                personQualifiedBodyPotQtyPercent = bigDecimal.divide(bigDecimal1, 2, RoundingMode.DOWN).floatValue();
            }
        }
        currentData.setPersonQualifiedBodyPotQty(personQualifiedBodyPotQty);
        currentData.setPersonQualifiedBodyPotQtyPercent(String.valueOf(personQualifiedBodyPotQtyPercent * 100 > 100 ? 100 : personQualifiedBodyPotQtyPercent * 100) + "%");
        currentData.setPersonQualifiedQty(personQualifiedQty);
        currentData.setPersonQualifiedQtyPercent(String.valueOf(personQualifiedQtyPercent * 100 > 100 ? 100 : personQualifiedQtyPercent * 100) + "%");
        currentData.setPersonQualifiedBodyFightQty(personQualifiedBodyFightQty);
        vo.setPersonData(currentData);
        return vo;
    }


    @Override
    public GetRecordQtyUpdateVo getRecordQtyUpdate(int orderProcessId) {
        Float sumFloat = orderProcessPotRepository.sumRecordQtyByOrderProcessIdAndRecordTypes(orderProcessId);
        sumFloat = sumFloat.floatValue();
        GetRecordQtyUpdateVo getRecordQtyUpdateVo = new GetRecordQtyUpdateVo();
        getRecordQtyUpdateVo.setPutIntoSize(sumFloat);
        //本道工序历史合格品报工数量，通过工序执行ID关联“报工/盘点历史记录表”过滤一级类目为“产后数量”的数量合计。
        Float sumFloat2 = orderProcessPotRepository.sumRecordQtyByOrderProcessIdAndRecordType(orderProcessId, LichengConstants.ORDER_RECORD_TYPE_3);
        sumFloat2 = sumFloat2.floatValue();
        getRecordQtyUpdateVo.setPutIntoHistorySize(sumFloat2);
        return getRecordQtyUpdateVo;
    }

    @Override
    public GetPotVo getPot(GetPotDto getPotDto) {
        //分组id
        String groupId = orderProcessPersonRelRepository.getPersonGroupId(getPotDto.getOrderProcessId(), getPotDto.getDevicePersonIds(), getPotDto.getDevicePersonIds().size());
        TBusOrderProcess tBusOrderProcess = orderProcessRepository.findById(getPotDto.getOrderProcessId()).orElse(null);
        TSysProcessInfo tSysProcessInfo = tBusOrderProcess.getProcessId();
        String midPpbomEntryInputProcess = tSysProcessInfo.getErpProcessNumber();
        if (StringUtils.isEmpty(midPpbomEntryInputProcess)) {
            throw new RuntimeException("工序没有绑定ERP工序，请检查下后台的工序管理配置");
        }
        GetPotVo getPotVo = null;
        switch (getPotDto.getProcessType()) {
            case LichengConstants.PROCESS_NUMBER_ZHANBAN: {
                //斩拌
                getPotVo = getGetPotVoByZb(groupId, getPotDto);
            }
            break;
            case LichengConstants.PROCESS_NUMBER_BANLIAO: {
                //拌料
                getPotVo = getGetPotVoByBl(groupId, getPotDto);
            }
            break;
            case LichengConstants.PROCESS_NUMBER_RUHUAJIANG: {
                //乳化浆
                getPotVo = getGetPotVoByUnify(groupId, getPotDto, getPotDto.getProcessType(), midPpbomEntryInputProcess);
            }
            break;
            case LichengConstants.PROCESS_NUMBER_XIELIUSESU: {
                //蟹柳色素
                getPotVo = getGetPotVoByUnify(groupId, getPotDto, getPotDto.getProcessType(), midPpbomEntryInputProcess);
            }
            break;
            default: {
                //默认通用
                getPotVo = getGetPotVoByUnify(groupId, getPotDto, getPotDto.getProcessType(), midPpbomEntryInputProcess);
            }
        }
        return getPotVo;
    }

    /**
     * 通用工序统计
     *
     * @param groupId
     * @param getPotDto
     * @return
     */
    private GetPotVo getGetPotVoByUnify(String groupId, GetPotDto getPotDto, String processNumber, String midPpbomEntryInputProcess) {
        GetPotVo getPotVo = new GetPotVo();
        //积累锅数
        int exportPotByImportAll = orderPPBomRepository.findExportPotByImportAllByRhj(getPotDto.getOrderId(), processNumber, midPpbomEntryInputProcess, "-1", "-1");
        getPotVo.setAllImportVo(exportPotByImportAll);
        //积累投入数量
        float sumImportPotQty = orderPPBomRepository.sumImportPotQty(getPotDto.getOrderId(), processNumber);
        getPotVo.setAllImportQty(sumImportPotQty);
        if (StringUtils.isEmpty(groupId)) {
            getPotVo.setCurrentImportVo(0);
        } else {
            //个人投入锅数
            int exportPotByImport = orderPPBomRepository.findExportPotByImport(getPotDto.getOrderId(), groupId, "-1", getPotDto.getOrderProcessId(), midPpbomEntryInputProcess);
            getPotVo.setCurrentImportVo(exportPotByImport);
        }
        return getPotVo;
    }

    /**
     * 拌料工序锅数详情
     */
    private GetPotVo getGetPotVoByBl(String groupId, GetPotDto getPotDto) {
        GetPotVo getPotVo = new GetPotVo();
        //个人锅数
        int exportPotByImport = 0;
        //个人投入框数
        int currentImportFrame = 0;
        //本机台手投入斗数
        int currentImportLowa = 0;
        if (StringUtils.isNotEmpty(groupId)) {
            exportPotByImport = orderPPBomRepository.findExportPotByImportByBL(getPotDto.getOrderId(), groupId, "-1", getPotDto.getOrderProcessId(), LichengConstants.REPORTYPE0001);
            currentImportFrame = orderPPBomRepository.findExportPotFrameByImportByBL(getPotDto.getOrderId(), groupId, getPotDto.getOrderProcessId(), LichengConstants.REPORTYPE0001);
            currentImportLowa = orderPPBomRepository.findExportPotLowaByImportByBL(getPotDto.getOrderId(), groupId, "-1", getPotDto.getOrderProcessId(), LichengConstants.REPORTYPE0001);
        }
        getPotVo.setCurrentImportVo(exportPotByImport);
        getPotVo.setCurrentImportFrame(currentImportFrame);
        getPotVo.setCurrentImportLowa(currentImportLowa);
        //积累锅数
        int exportPotByImportAll = orderPPBomRepository.findExportPotByImportAllByBL(getPotDto.getOrderId(), LichengConstants.PROCESS_NUMBER_BANLIAO, "-1", "-1");
        //积累投入框数
        int allImportFrame = orderPPBomRepository.findExportPotByImportFrameAllByBL(getPotDto.getOrderId(), LichengConstants.PROCESS_NUMBER_BANLIAO);
        ;
        //积累投入斗数
        int allImportLowa = orderPPBomRepository.findExportPotByImportLowaAllByBL(getPotDto.getOrderId(), LichengConstants.PROCESS_NUMBER_BANLIAO, "-1", "-1");
        getPotVo.setAllImportVo(exportPotByImportAll);
        getPotVo.setAllImportFrame(allImportFrame);
        getPotVo.setAllImportLowa(allImportLowa);
        return getPotVo;
    }

    /**
     * 获取斩拌工序锅数明细
     *
     * @param groupId
     * @param getPotDto
     * @return
     */
    private GetPotVo getGetPotVoByZb(String groupId, GetPotDto getPotDto) {
        GetPotVo getPotVo = new GetPotVo();
        //积累锅数
        int exportPotByImportAll = orderPPBomRepository.findExportPotByImportAll(getPotDto.getOrderId(), LichengConstants.PROCESS_NUMBER_ZHANBAN, "-1", "-1");
        getPotVo.setAllImportVo(exportPotByImportAll);

        if (StringUtils.isEmpty(groupId)) {
            getPotVo.setCurrentImportVo(0);
        } else {
            //个人投入锅数
            int exportPotByImport = orderPPBomRepository.findExportPotByImport(getPotDto.getOrderId(), groupId, "-1", getPotDto.getOrderProcessId());
            getPotVo.setCurrentImportVo(exportPotByImport);
        }
        return getPotVo;
    }

    @Override
    public List<ListPrintRecordVo> listPrintRecord(Integer orderProcessId) {
        List<Map> maps = orderProcessRecordRepository.listPrintRecord(orderProcessId);
        List<ListPrintRecordVo> listPrintRecordVos = JSON.parseArray(JSON.toJSONString(maps), ListPrintRecordVo.class);
        return listPrintRecordVos;
    }

    @Override
    public String getOrderRecordTypePd(Integer orderProcessId) {
        return orderProcessRecordRepository.getOrderRecordTypePd(orderProcessId);
    }

    @Transactional
    @Override
    public void submitAndBindCheckMes(SubmitAndBindCheckMesDto submitAndBindCheckMesDto, String userId) throws ParseException {
        Integer orderProcessHistoryId = appOrderProcessRecordSubmitService.submit(submitAndBindCheckMesDto.getSaveDto(), userId);
        TBusOrderProcessHistory tBusOrderProcessHistory = orderProcessHistoryRepository.findById(orderProcessHistoryId).get();
        TBusOrderBindCode tBusOrderBindCode = submitAndBindCheckMesDto.getTBusOrderBindCode();
        tBusOrderBindCode.setOrderProcessRecordId(tBusOrderProcessHistory.getOrderProcessRecordId());
        tBusOrderBindCode.setOrderProcessHistoryId(orderProcessHistoryId);
        chargingBasketService.bindCheckMes(tBusOrderBindCode);
    }

    @Transactional
    @Override
    public void submitAndModify(SubmitAndModifySaveDto modifySaveDto, String userId) throws ParseException {
        Integer orderProcessHistoryId = modifySaveDto.getOrderProcessHistoryId();
        Float modifyQty = modifySaveDto.getModifyQty();
        if (modifyQty <= 0) {
            throw new RuntimeException("修改报工记录的数量必须大于0！");
        }
        var history = orderProcessHistoryRepository.findById(orderProcessHistoryId).orElse(null);
        if (history.getOrderPPBomId() != null && history.getOrderPPBomId() > 0) {
            List<Integer> ppboms = orderPPBomRepository.joinPpbomId(history.getOrderPPBomId(), history.getOrderProcessId());
            if (ppboms.size() > 1) {
                throw new RuntimeException("组合投料不允许修改！");
            }
        }
        if (history.getReportStatus().equals(LichengConstants.ORDER_PROCESS_HISTORY_STATUS_1)) {
            throw new RuntimeException("报工记录的状态为已删除，无法修改数量！");
        }
        // 删除报工记录
        try {
            appOrderProcessRecordDeleteService.deleteRecord(orderProcessHistoryId, true);
        } catch (RuntimeException e2) {
            throw new RuntimeException("修改报工数量执行失败，处理删除原报工记录异常：" + e2.getMessage());
        }
        // 提交修改的报工
        OrderRecordSaveDto saveDto = new OrderRecordSaveDto();
        saveDto.setOrderProcessId(history.getOrderProcessId());
        saveDto.setOrderPPBomId(history.getOrderPPBomId());
        saveDto.setOrderNo(history.getOrderNo());
        saveDto.setProcessId(history.getProcessId().getProcessId());
        saveDto.setProcessName(history.getProcessId().getProcessName());
        saveDto.setProcessNumber(history.getProcessId().getProcessNumber());
        saveDto.setRecordType(history.getRecordType());
        saveDto.setRecordTypeBg(history.getRecordTypeBg());
        saveDto.setRecordQty(modifyQty);
        saveDto.setRecordManualQty(history.getRecordManualQty());
        saveDto.setRecordUnit(history.getRecordUnit());
        saveDto.setCapacityQty(history.getCapacityQty());
        saveDto.setCapacityUnit(history.getCapacityUnit());
        saveDto.setMaterialId(history.getMaterialId());
        saveDto.setMaterialNumber(history.getMaterialNumber());
        saveDto.setMaterialName(history.getMaterialName());
        saveDto.setBodyLot(history.getBodyLot());
        List<TBusOrderProcessDeviceRel> deviceRelList = orderProcessDeviceRelService.findByDeviceGroupId(history.getDeviceGroupId());
        List<Integer> deviceIds = deviceRelList.stream().map(TBusOrderProcessDeviceRel::getDeviceId).collect(Collectors.toList());
        saveDto.setDeviceIds(deviceIds);
        List<TBusOrderProcessPersonRel> personRelList = orderProcessPersonRelService.findByPersonGroupId(history.getDevicePersonGroupId());
        List<Integer> devicePersonIds = personRelList.stream().map(TBusOrderProcessPersonRel::getDevicePersonId).collect(Collectors.toList());
        saveDto.setDevicePersonIds(devicePersonIds);
        saveDto.setImportPot(history.getImportPot() == null ? null : history.getImportPot().intValue());
        // 组合投料暂时不允许删除
//        saveDto.setIsReplaceGroup(history.getImportPot());
        saveDto.setImportPotGroup(history.getImportPotGroup());
        saveDto.setOrderProcessHistoryParentId(history.getOrderProcessHistoryParentId());
        saveDto.setLsmMaterialId(history.getLsmMaterialId());
        appOrderProcessRecordSubmitService.submit(saveDto, userId);
    }

    @Override
    public BigDecimal verifyPot(GetPotDto getPotDto) {
        BigDecimal val = new BigDecimal("0");
        //1.获取积累产出数据
        Float exportVal = orderPPBomRepository.sumExportPotQtyAllByBL(getPotDto.getOrderId(), LichengConstants.PROCESS_NUMBER_BANLIAO);
        //2.获取积累投入数据
        Float importVal = orderPPBomRepository.sumImportPotQtyAll(getPotDto.getOrderId(), LichengConstants.PROCESS_NUMBER_BANLIAO);
        if (importVal.floatValue() == exportVal.floatValue()) {
            throw new RuntimeException("必须投满一锅，才能产出");
        }
        val = new BigDecimal(importVal.toString()).subtract(new BigDecimal(exportVal.toString()));
        return val;
    }

    /**
     * 获取尾料产后数据
     *
     * @return
     */
    private Double getREPORTYPE0002(Integer orderProcessId, String groupId, String bglx) {
        //1.获取尾料报工总数
        Double sumVal = orderProcessRecordRepository.sumBG2(orderProcessId, groupId, bglx);
        if (sumVal == null || sumVal == 0) {
            throw new RuntimeException("请先投入尾料在报工！");
        }
        TBusOrderProcessRecord tBusOrderProcessRecordCH = new TBusOrderProcessRecord();
        tBusOrderProcessRecordCH.setOrderProcessId(orderProcessId);
        tBusOrderProcessRecordCH.setBusType(LichengConstants.ORDER_BUS_TYPE_BG);
        tBusOrderProcessRecordCH.setDevicePersonGroupId(groupId);
        tBusOrderProcessRecordCH.setRecordType(LichengConstants.ORDER_RECORD_TYPE_3);
        tBusOrderProcessRecordCH.setRecordTypeBg(bglx);
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withMatcher("busType", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("recordType", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("recordTypeBg", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("devicePersonGroupId", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("orderProcessId", ExampleMatcher.GenericPropertyMatchers.exact());
        Example<TBusOrderProcessRecord> example = Example.of(tBusOrderProcessRecordCH, matcher);
        List<TBusOrderProcessRecord> recordRepositoryAll = orderProcessRecordRepository.findAll(example);
        if (recordRepositoryAll.isEmpty()) {
            return sumVal;
        }
        TBusOrderProcessRecord tBusOrderProcessRecord = recordRepositoryAll.get(0);
        BigDecimal bigDecimal;
        if (tBusOrderProcessRecord.getRecordUnit().equals("kg")) {
            bigDecimal = new BigDecimal(tBusOrderProcessRecord.getRecordQty().toString());
        } else {
            bigDecimal = new BigDecimal(tBusOrderProcessRecord.getRecordQty()).multiply(new BigDecimal(Double.valueOf(0.001)));
        }
        BigDecimal subtract = new BigDecimal(sumVal.toString()).subtract(bigDecimal);
        return subtract.doubleValue();
    }

    @Override
    public GetPotVo getPotBYBL(Integer orderId, List<Integer> devicePersonIds, Integer orderProcessId, String processNumberBanliao) {
        //分组id
        String groupId = orderProcessPersonRelRepository.getPersonGroupId(orderProcessId, devicePersonIds, devicePersonIds.size());
        GetPotVo getPotVo = new GetPotVo();
        int exportPotByImport = 0;
        if (StringUtils.isNotEmpty(groupId)) {
            exportPotByImport = orderPPBomRepository.findExportPotByImportByBL(orderId, groupId, "-1", orderProcessId, LichengConstants.REPORTYPE0001);
        }
        getPotVo.setCurrentImportVo(exportPotByImport);
        int exportPotByImportAll = orderPPBomRepository.findExportPotByImportAllByBL(orderId, processNumberBanliao, "-1", "-1");
        getPotVo.setAllImportVo(exportPotByImportAll);
        return getPotVo;
    }

    @Override
    public List<JoinRecordVo> joinRecord(Integer orderPpbomId, Integer orderId, Integer orderProcessId, List<Integer> ids) {
        //判断第几锅
        String groupId;
        //分组id
        if (ids == null || ids.size() == 0) {
            groupId = "";
        } else {
            groupId = orderProcessPersonRelRepository.getPersonGroupId(orderProcessId, ids, ids.size());
        }
        //获取用料id列表
        List<Integer> ppboms = orderPPBomRepository.joinPpbomId(orderPpbomId, orderProcessId);
//        TBusOrderProcessRecord record = new TBusOrderProcessRecord();
//        record.setOrderProcessId(orderProcessId);
//        record.setOrderPPBomId(orderPpbomId);
//        record.setImportPotGroup(1);
//        record.setDevicePersonGroupId(groupId);
//        ExampleMatcher matcher = ExampleMatcher.matching()
//                .withMatcher("orderProcessId", ExampleMatcher.GenericPropertyMatchers.exact())
//                .withMatcher("orderPpbomId", ExampleMatcher.GenericPropertyMatchers.exact())
//                .withMatcher("devicePersonGroupId", ExampleMatcher.GenericPropertyMatchers.exact())
//                .withMatcher("importPotGroup", ExampleMatcher.GenericPropertyMatchers.exact());
//        Example<TBusOrderProcessRecord> example = Example.of(record, matcher);
//        List<TBusOrderProcessRecord> one = orderProcessRecordRepository.findAll(example);
        //第几锅
        int importPot;
        List<TBusOrderProcessHistory> one2 = orderProcessHistoryRepository.findAnd(orderProcessId, ppboms, groupId, 0);
        if (one2.isEmpty()) {
            List<TBusOrderProcessHistory> one = orderProcessHistoryRepository.findAnd(orderProcessId, ppboms, groupId, 1);
            if (one.isEmpty()) {
                importPot = 1;
            } else {
                importPot = one.get(one.size() - 1).getImportPot().intValue() + 1;
            }
        } else {
            Float importPot1 = one2.get(0).getImportPot();
            importPot = importPot1.intValue();
        }
        List<JoinRecordVo> joinRecordVos = new ArrayList<>();

        // 获取标准件开发数量
        Float mesVal = orderPPBomRepository.getMesVal(ppboms);
        //获取其他组合报工记录
        ppboms.stream().forEach(ppbom -> {
            Map map = null;
            JoinRecordVo joinRecordVo = null;
            if (StringUtils.isNotEmpty(groupId)) {
                map = orderProcessHistoryRepository.getByPpbomLimitTwo(orderProcessId, ppbom, importPot, groupId);
                joinRecordVo = JSON.parseObject(JSON.toJSONString(map), JoinRecordVo.class);
            }
//            if (map != null && map.size() > 0) {
            if (map != null && map.size() > 0 && joinRecordVo.getRecordQty() > 0) {
                joinRecordVo.setImportPot(importPot);
                joinRecordVo.setLimittationQty(mesVal);
                joinRecordVos.add(joinRecordVo);
            } else {
                JoinRecordVo joinRecordVo1 = new JoinRecordVo();
                TBusOrderPPBom tBusOrderPPBom = orderPPBomRepository.findById(ppbom).orElse(null);
                joinRecordVo1.setImportPot(importPot);
                joinRecordVo1.setMaterialId(tBusOrderPPBom.getMaterialId());
                joinRecordVo1.setMaterialName(tBusOrderPPBom.getMaterialName());
                joinRecordVo1.setMaterialNumber(tBusOrderPPBom.getMaterialNumber());
                joinRecordVo1.setRecordQty(0F);
                joinRecordVo1.setLimittationQty(mesVal);
                joinRecordVo1.setOrderPpbomId(tBusOrderPPBom.getOrderPPBomId());
                joinRecordVos.add(joinRecordVo1);
            }
//            }
        });
        return joinRecordVos;
    }

    public List<TSysProcessInfo> getProcessInfos(Integer orderId) {
        TBusOrderHead orderHead = orderHeadRepository.findById(orderId).orElse(null);
        if (null == orderHead || StringUtils.isEmpty(orderHead.getMidMoSaleOrderNo())) {
            throw new RuntimeException("获取订单的需求单据失败");
        }
        return tSysProcessInfoRepository.getProcessInfos(orderHead.getMidMoSaleOrderNo());
    }

    @Override
    public ListJoinRecordVo getPotAllRecordDetails(GetPotAllRecordDetailsDto getPotAllRecordDetailsDto) {
        ListJoinRecordVo list = new ListJoinRecordVo();
        List<GetPotAllRecordDetailsVo> getPotAllRecordDetailsVos = new ArrayList<>();
        TBusOrderHead tBusOrderHead = orderHeadRepository.findById(getPotAllRecordDetailsDto.getOrderId()).orElse(null);
        int currentPot1 = orderPPBomRepository.findExportPotByImportAll(getPotAllRecordDetailsDto.getOrderId(), LichengConstants.PROCESS_NUMBER_ZHANBAN, "-1", "-1");
        //获取当前锅数
        currentPot1++;
        //已投锅数
        int endPot = (currentPot1 - 1) < 0 ? 0 : (currentPot1 - 1);
        int restPot = tBusOrderHead.getBodyPotQty() - endPot;
        list.setRestPot(restPot);
        list.setEndPot(endPot);
        GetPotAllRecordDetailsVo getPotAllRecordDetailsVo;
        for (int i = 1; i <= tBusOrderHead.getBodyPotQty(); i++) {
            getPotAllRecordDetailsVo = new GetPotAllRecordDetailsVo();
            int currentPot = i;
            if (currentPot == 1 && currentPot1 == 1) {
                getPotAllRecordDetailsVo.setFlag(0);
            } else {
                getPotAllRecordDetailsVo.setFlag(currentPot < currentPot1 ? 1 : 0);
            }
            getPotAllRecordDetailsVo.setCurrentPot(currentPot);
            List<GetPotAllRecordDetailsGroup> getPotAllRecordDetailsGroups = new ArrayList<>();
            GetPotAllRecordDetailsGroup getPotAllRecordDetailsGroup;
            //分组数据
            List<GetPotAllRecordDetailsPpbom> getPotAllRecordDetailsPpboms;
            List<Integer> keys = new ArrayList<>();
            List<Map> currentMaps;
            //获取用料详情
            List<Map> maps = orderPPBomRepository.listPpbomRecordDetails(getPotAllRecordDetailsDto.getOrderId(), LichengConstants.PROCESS_NUMBER_ZHANBAN, currentPot);
            for (Map.Entry<Integer, String> entry : LichengConstants.PPBOM_GROUP_NUMBER_MAP.entrySet()) {
                currentMaps = new ArrayList<>();
                getPotAllRecordDetailsGroup = new GetPotAllRecordDetailsGroup();
                for (Map map : maps) {
                    if (map.get("mid_ppbom_entry_handle_group") != null &&
                            Integer.parseInt(String.valueOf(map.get("mid_ppbom_entry_handle_group"))) == entry.getKey()) {
                        getPotAllRecordDetailsGroup = new GetPotAllRecordDetailsGroup();
                        getPotAllRecordDetailsGroup.setMidPpbomEntryHandleGroup(entry.getKey());
                        getPotAllRecordDetailsGroup.setMidPpbomEntryHandleGroupName(entry.getValue());
                        currentMaps.add(map);
                    }
                }
                getPotAllRecordDetailsPpboms = JSON.parseArray(JSON.toJSONString(currentMaps), GetPotAllRecordDetailsPpbom.class);
                getPotAllRecordDetailsGroup.setGetPotAllRecordDetailsPpboms(getPotAllRecordDetailsPpboms);
                getPotAllRecordDetailsGroups.add(getPotAllRecordDetailsGroup);
            }
            //其他
            List<Map> myMap = new ArrayList<>();
            for (Map map : maps) {
                if (map.get("mid_ppbom_entry_handle_group") == null) {
                    myMap.add(map);
                }
            }
            getPotAllRecordDetailsGroup = new GetPotAllRecordDetailsGroup();
            getPotAllRecordDetailsGroup.setMidPpbomEntryHandleGroup(-1);
            getPotAllRecordDetailsGroup.setMidPpbomEntryHandleGroupName("其他");
            getPotAllRecordDetailsPpboms = JSON.parseArray(JSON.toJSONString(myMap), GetPotAllRecordDetailsPpbom.class);
            getPotAllRecordDetailsGroup.setGetPotAllRecordDetailsPpboms(getPotAllRecordDetailsPpboms);
            getPotAllRecordDetailsGroups.add(getPotAllRecordDetailsGroup);
//            for (Map.Entry<Integer, String> entry : LichengConstants.PPBOM_GROUP_NUMBER_MAP.entrySet()) {
//                keys.add(entry.getKey());
//                getPotAllRecordDetailsGroup = new GetPotAllRecordDetailsGroup();
//                getPotAllRecordDetailsGroup.setMidPpbomEntryHandleGroup(entry.getKey());
//                getPotAllRecordDetailsGroup.setMidPpbomEntryHandleGroupName(entry.getValue());
//                //获取用料详情
//                maps = orderPPBomRepository.listPpbomRecordDetails(getPotAllRecordDetailsDto.getOrderId(), LichengConstants.PROCESS_NUMBER_ZHANBAN, currentPot, entry.getKey());
//                getPotAllRecordDetailsPpboms = JSON.parseArray(JSON.toJSONString(maps), GetPotAllRecordDetailsPpbom.class);
//                getPotAllRecordDetailsGroup.setGetPotAllRecordDetailsPpboms(getPotAllRecordDetailsPpboms);
//                getPotAllRecordDetailsGroups.add(getPotAllRecordDetailsGroup);
//            }
//            //其他
//            getPotAllRecordDetailsGroup = new GetPotAllRecordDetailsGroup();
//            getPotAllRecordDetailsGroup.setMidPpbomEntryHandleGroup(-1);
//            getPotAllRecordDetailsGroup.setMidPpbomEntryHandleGroupName("其他");
//            maps = orderPPBomRepository.listPpbomRecordDetailsNotKeys(getPotAllRecordDetailsDto.getOrderId(), LichengConstants.PROCESS_NUMBER_ZHANBAN, currentPot, keys);
//            getPotAllRecordDetailsPpboms = JSON.parseArray(JSON.toJSONString(maps), GetPotAllRecordDetailsPpbom.class);
//            getPotAllRecordDetailsGroup.setGetPotAllRecordDetailsPpboms(getPotAllRecordDetailsPpboms);
//            getPotAllRecordDetailsGroups.add(getPotAllRecordDetailsGroup);
            getPotAllRecordDetailsVo.setGetPotAllRecordDetailsGroups(getPotAllRecordDetailsGroups);
            getPotAllRecordDetailsVos.add(getPotAllRecordDetailsVo);
        }
        list.setGetPotAllRecordDetailsVos(getPotAllRecordDetailsVos);
        return list;
    }

    @Override
    public String getFingerprintAuthentication(Integer processId) {
        TSysProcessInfo tSysProcessInfo = tSysProcessInfoRepository.findById(processId).get();
        String str = "0";
        if (tSysProcessInfo != null) {
            str = "0".equals(tSysProcessInfo.getFingerprintAuthentication()) ? "0" : "1";
        }
        return str;
    }

    @Transactional
    @Override
    public GetIotByDevicesVo getIotByDevices(GetIotByDevicesDto getIotByDevicesDto) throws Exception {
        GetIotByDevicesVo getIotByDevicesVo = new GetIotByDevicesVo();
        BigInteger value = new BigInteger("0");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentDate = new Date();
        String currentDateStr = simpleDateFormat.format(currentDate);
        List<String> devicesCodes = getIotByDevicesDto.getDevicesCodes();
        TSysDeviceIotHistory tSysDeviceIotHistory;
        List<DevicesTimeVo> devicesTimeVos = new ArrayList<>();
        DevicesTimeVo devicesTimeVo;
        for (String devicesCode : devicesCodes) {
            devicesTimeVo = new DevicesTimeVo();
            TSysDeviceIot tSysDeviceIot = tSysDeviceIotRepository.findByDeviceCodeAndRecordType(devicesCode, getIotByDevicesDto.getRecordType());
            Date iotTime = tSysDeviceIot.getIotTime();
            if (null == tSysDeviceIot || null == iotTime) {
                throw new RuntimeException("设备编号：" + devicesCode + " 未进行初始化，请联系管理人员!");
            }
            tSysDeviceIotHistory = new TSysDeviceIotHistory();
            switch (getIotByDevicesDto.getProcessCode()) {
                case LichengConstants.PROCESS_NUMBER_LASHENMO: {
                    //拉伸膜
                    Date date = new Date(iotTime.getTime());
                    String format = simpleDateFormat.format(date);
                    IotDiffValueVo vo2 = this.getStretchMachineRunNum(format, currentDateStr, devicesCode);
                    BigInteger diffValue = vo2.getDiffValue();
                    value = value.add(diffValue);
                    tSysDeviceIotHistory.setDeviceId(diffValue.intValue());
                    devicesTimeVo.setCreatedTime(format);
                    devicesTimeVo.setValue(vo2.getDiffValue());
                }

                break;
                case LichengConstants.PROCESS_NUMBER_BAOZHUANG: {
                    //包装
                    IotDiffValueVo vo2 = this.getDiffValueNum(simpleDateFormat.format(new Date(iotTime.getTime())), currentDateStr, devicesCode, "生产件数");
                    BigInteger diffValue = vo2.getDiffValue();
                    value = value.add(diffValue);
                    tSysDeviceIotHistory.setDeviceId(diffValue.intValue());
                    devicesTimeVo.setCreatedTime(simpleDateFormat.format(new Date(iotTime.getTime())));
                    devicesTimeVo.setValue(vo2.getDiffValue());
                }
                break;
            }

            tSysDeviceIotHistory.setCreatedTime(currentDate);
            tSysDeviceIotHistory.setDeviceCode(devicesCode);
            tSysDeviceIotHistory.setOrderNo(getIotByDevicesDto.getOrderNo());
            tSysDeviceIotHistory.setOrderProcessId(getIotByDevicesDto.getOrderProcessId());
            tSysDeviceIotHistory.setProcessCode(getIotByDevicesDto.getProcessCode());
            tSysDeviceIotHistory.setRecordType(org.apache.commons.lang3.StringUtils.isBlank(getIotByDevicesDto.getRecordType())?"3":getIotByDevicesDto.getRecordType());
            devicesTimeVo.setDeviceCode(devicesCode);
            devicesTimeVo.setEndTime(currentDateStr);
            tSysDeviceIotHistoryRepository.save(tSysDeviceIotHistory);
            devicesTimeVos.add(devicesTimeVo);
        }
        getIotByDevicesVo.setDiffValue(value);
        getIotByDevicesVo.setDevicesTimeVoList(devicesTimeVos);

        return getIotByDevicesVo;
    }
}
