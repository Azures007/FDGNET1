package org.thingsboard.server.dao.mes.order;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.common.util.BigDecimalUtil;
import org.thingsboard.common.util.Utils;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.mes.*;
import org.thingsboard.server.common.data.mes.bus.TBusOrderHead;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcess;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcessHistory;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcessRecord;
import org.thingsboard.server.common.data.mes.sys.TSysClass;
import org.thingsboard.server.common.data.mes.sys.TSysCodeDsc;
import org.thingsboard.server.common.data.mes.sys.TSysPersonnelInfo;
import org.thingsboard.server.common.data.mes.sys.TSysProcessInfo;
import org.thingsboard.server.common.data.web.ResponseResult;
import org.thingsboard.server.common.data.web.ResultUtil;
import org.thingsboard.server.dao.constant.GlobalConstant;
import org.thingsboard.server.dao.mes.dto.OrderProcessRecordSaveDto;
import org.thingsboard.server.dao.mes.dto.OrderProcessRecordSearchDto;
import org.thingsboard.server.dao.mes.vo.*;
import org.thingsboard.server.dao.sql.mes.TSysProcessInfo.TSysProcessInfoRepository;
import org.thingsboard.server.dao.sql.mes.order.*;
import org.thingsboard.server.dao.sql.mes.tSysClass.ClassGroupLeaderRepository;
import org.thingsboard.server.dao.sql.mes.tSysClass.TSysClassRepository;
import org.thingsboard.server.dao.sql.mes.tSysPersonnelInfo.ClassPersonnelRepository;
import org.thingsboard.server.dao.sql.mes.tSysPersonnelInfo.TSysPersonnelInfoRepository;
import org.thingsboard.server.dao.mes.tSysCodeDsc.TSysCodeDscService;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.thingsboard.server.common.data.mes.LichengConstants.*;

/**
 * @Auther: l
 * @Date: 2022/4/25 20:00
 * @Description:
 */
@Service
@Slf4j
public class OrderInventoryServiceImpl implements OrderInventoryService {
    @Autowired
    OrderHeadRepository orderHeadRepository;

    @Autowired
    OrderPPBomRepository orderPPBomRepository;

    @Autowired
    OrderProcessRepository orderProcessRepository;

    @Autowired
    OrderProcessHistoryRepository orderProcessHistoryRepository;

    @Autowired
    OrderProcessRecordRepository orderProcessRecordRepository;

    @Autowired
    TSysClassRepository tSysClassRepository;

    @Autowired
    TSysProcessInfoRepository tSysProcessInfoRepository;

    @Autowired
    TSysPersonnelInfoRepository tSysPersonnelInfoRepository;

    @Autowired
    ClassPersonnelRepository classPersonnelRepository;

    @Autowired
    ClassGroupLeaderRepository classGroupLeaderRepository;

    @Autowired
    TSysCodeDscService tSysCodeDscService;

    @Override
    public OrderProcessResult getOrderPpbom(Integer orderId, Integer processId) throws JsonProcessingException {
        var tSysProcessInfo = tSysProcessInfoRepository.findById(processId).orElse(null);
        if (tSysProcessInfo == null) {
            throw new RuntimeException("查不到工序信息");
        }
        String midPpbomEntryInputProcess = tSysProcessInfo.getErpProcessNumber();
        if (StringUtils.isEmpty(midPpbomEntryInputProcess)) {
            throw new RuntimeException("工序没有绑定ERP工序，请检查下后台的工序管理配置");
        }
        List<Map> maps = orderHeadRepository.getOrderPPbom(orderId, midPpbomEntryInputProcess);
        OrderProcessResult processResult = new OrderProcessResult();
        String mapsStr = JSON.toJSONString(maps);
        List<OrderProcessResult> orderProcessResults = JSON.parseArray(mapsStr, OrderProcessResult.class);
        List<OrderPPbomResult> orderPPbomResults = JSON.parseArray(mapsStr, OrderPPbomResult.class);
        for (OrderPPbomResult orderPPbomResult : orderPPbomResults) {
            orderPPbomResult.setUnitStr(GlobalConstant.getCodeDscName("UNIT0000", orderPPbomResult.getUnit()));
        }
        if (orderProcessResults.size() > 0) {
            processResult = orderProcessResults.get(0);
            processResult.setPPbomResultList(orderPPbomResults);
        }
        return processResult;
    }

    @Override
    public List<TSysCodeDsc> getOrderRecordTypePdList(Integer orderProcessId) {
        List<TSysCodeDsc> tSysCodeDscList = tSysCodeDscService.getCodeByCodeClId("STOCKTAKING0000");
        if (orderProcessId <= 0) {
            return tSysCodeDscList;
        }
        TBusOrderProcess orderProcess = orderProcessRepository.findById(orderProcessId).orElse(null);
        if (orderProcess != null && LichengConstants.PROCESSSTATUS_3.equals(orderProcess.getProcessStatus())) {
            // 执行工序为“已完工”状态，展示2种盘点类型：订单完工盘点、交接班盘点。
            tSysCodeDscList.removeIf(e -> LichengConstants.STOCKTAKING0003.equals(e.getCodeValue()));
            return tSysCodeDscList;
        } else {
            return tSysCodeDscList;
        }
    }

    @Override
    public List<BatchVo> getBatchList(String orderNo) {
        List<Map> maps = orderHeadRepository.getBatchList(orderNo);
        String mapStr = JSON.toJSONString(maps);
        List<BatchVo> batchVos = JSON.parseArray(mapStr, BatchVo.class);
        for (BatchVo batchVo : batchVos) {
            batchVo.setBodyUnitStr(GlobalConstant.getCodeDscName("UNIT0000", batchVo.getBodyUnit()));
        }
        return batchVos;
    }

    @Transactional
    @Override
    public ResponseResult submit(Integer isFirst, Integer size, OrderProcessRecordSaveDto saveDto, String userId, Integer isRepeat) {
        if (saveDto.getOrderProcessId() == null || saveDto.getOrderProcessId() == 0) {
            throw new RuntimeException("订单工序执行表id不能为空");
        }
        List<TBusOrderHead> heads = orderHeadRepository.findByOrderNo(saveDto.getOrderNo());
        TBusOrderHead tBusOrderHead = heads.get(0);
        if (LichengConstants.ORDERSTATUS_4.equals(tBusOrderHead.getOrderStatus())) {
            throw new RuntimeException("订单已挂起，禁止操作。");
        }
        //标记中途完工盘点
        int flag = 0;
        List<TBusOrderProcessRecord> list = orderProcessRecordRepository.findByOrderProcessAndRecordTypePdWithOut3(saveDto.getOrderProcessId(), saveDto.getRecordTypePd());
        for (TBusOrderProcessRecord tBusOrderProcessRecord : list) {
            if (tBusOrderProcessRecord.getRecordTypePd().equals(LichengConstants.STOCKTAKING0001)) {
                flag++;
            }
        }
        if (list != null && list.size() > 0) {
            if (saveDto.getRecordTypePd().equals(LichengConstants.STOCKTAKING0001)) {
                //当前是中途完工盘点，判断前几次是不是中途完工盘点
                if (flag < list.size()) {
                    throw new RuntimeException("当前盘点无效，原因：已有其他盘点记录");
                }
            } else {
                //当前非中途完工盘点
                if (!(flag == list.size())) {
                    // 前几次非中途完工盘点
                    throw new RuntimeException("当前盘点无效，原因：已有其他盘点记录");
                }
            }
        }
        TBusOrderProcess tBusOrderProcess = orderProcessRepository.findById(saveDto.getOrderProcessId()).orElse(null);
//        TSysPersonnelInfo personnelInfo = tBusOrderProcess.getPersonId();
        TSysProcessInfo processInfo = tBusOrderProcess.getProcessId();
        TSysClass tSysClass = tBusOrderProcess.getClassId();
        TSysPersonnelInfo personnelInfo = tSysPersonnelInfoRepository.findByUserId(userId);
        saveDto.setPersonId(personnelInfo.getPersonnelId());
        saveDto.setClassId(tSysClass.getClassId());
//        List<TBusOrderProcessRecord> records = orderProcessRecordRepository.getRecord(saveDto);
        // 由于每个工序执行表记录，只会有盘点类型一种类型，所以不需要按盘点类型查询。该调整用于中途完工类型的处理。 2022-09-14 任务14384
//        List<TBusOrderProcessRecord> records = orderProcessRecordRepository.findAllByOrderProcessIdAndBusTypeAndRecordTypePd(saveDto.getOrderProcessId(), "PD", saveDto.getRecordTypePd());
        List<TBusOrderProcessRecord> records = orderProcessRecordRepository.findAllByOrderProcessIdAndBusType(saveDto.getOrderProcessId(), "PD");
        // 中途完工盘点时，结果表和记录表都存储中途类型，当做了订单完工或交接班盘点，把结果表盘点类型更为对应类型（订单完工盘点或交接班盘点）
        records.stream().forEach(record -> {
            if (record.getRecordTypePd().equals(STOCKTAKING0003) && !saveDto.getRecordTypePd().equals(STOCKTAKING0003)) {
                record.setRecordTypePd(saveDto.getRecordTypePd());
            }
        });
        orderProcessRecordRepository.saveAll(records);
        TBusOrderProcessHistory history = new TBusOrderProcessHistory();
        BeanUtils.copyProperties(saveDto, history);
        history.setBusType("PD");
        history.setReportTime(new Date());
        history.setStockCount(size);//按盘点次数来标记盘点批次
        TBusOrderProcessRecord processRecord = null;
//        TSysProcessInfo processInfo = tSysProcessInfoRepository.findById(saveDto.getProcessId()).get();
        for (TBusOrderProcessRecord record : records) {
            if (record.getProcessId() != null && record.getProcessId().getProcessId().equals(saveDto.getProcessId())) {
                if (record.getClassId() != null && record.getClassId().getClassId().equals(saveDto.getClassId())) {
                    if (saveDto.getMaterialName() != null && record.getMaterialName().equals(saveDto.getMaterialName())) {
                        processRecord = record;
                    }
                }
            }
        }
        OrderProcessRecordSearchDto searchDto = new OrderProcessRecordSearchDto();
        BeanUtils.copyProperties(saveDto, searchDto);
//        Integer size = orderProcessHistoryRepository.getSize(searchDto.getOrderProcessId(), searchDto.getProcessId(),searchDto.getPersonId());
//        List<TBusOrderProcessHistory> histories = orderProcessHistoryRepository.findAllByOrderProcessIdAndProcessIdAndBusTypeAndReportStatusOrderByOrderProcessHistoryIdDesc(saveDto.getOrderProcessId(), processInfo, "PD", "1");
        if (processRecord != null) {
            List<TBusOrderProcessHistory> pdHistories = orderProcessHistoryRepository.findAllByOrderProcessIdAndBusTypeOrderByOrderProcessHistoryIdDesc(saveDto.getOrderProcessId(), "PD");
            if (isRepeat != null && isRepeat == 0) {

                if (pdHistories.get(0).getMaterialName().equals(saveDto.getMaterialName()) && Math.abs(pdHistories.get(0).getRecordQty() - saveDto.getRecordQty()) <= 0) {
                    return ResultUtil.error(10002, "与上次盘点数量一致，确定重复盘点？");
                }
            }
            //盘点状态：0->首次盘点，-1->重新盘点，2->退出盘点界面后再次盘点"
            if (isFirst == -1) {
                //是重新盘点，替换结果
                //进入重新盘点页面，某个类目首次盘点提交数据后，需要将该类目的历史盘点记录表记录删除
                TBusOrderProcessRecord record = processRecord;
                List<TBusOrderProcessHistory> deleteList = new ArrayList<>();
                if (pdHistories.size() > 0) {
                    for (TBusOrderProcessHistory tBusOrderProcessHistory : pdHistories) {
                        if (tBusOrderProcessHistory.getMaterialName().equals(saveDto.getMaterialName())) {
                            if (tBusOrderProcessHistory.getStockCount() == null || tBusOrderProcessHistory.getStockCount() < size) {
                                deleteList.add(tBusOrderProcessHistory);
                                record.setRecordQty(saveDto.getRecordQty());
                            } else {
                                record.setRecordQty(saveDto.getRecordQty() + record.getRecordQty());
                                history.setStockCount(size);
                            }
                        }
                    }
                }
                record.setReportTime(new Date());
                orderProcessRecordRepository.save(record);
                history.setOrderProcessRecordId(record.getOrderProcessRecordId());
                history.setStockCount(size);
                //记录报工结果表id
                if (deleteList.size() > 0) {
                    orderProcessHistoryRepository.deleteAll(deleteList);
                }
            } else if (isFirst > 0) {
                //退出盘点界面后再次盘点
                history.setStockCount(isFirst);
                TBusOrderProcessRecord record = processRecord;
                record.setRecordQty(saveDto.getRecordQty() + record.getRecordQty());
                record.setBusType("PD");
                record.setReportTime(new Date());
                orderProcessRecordRepository.save(record);
                history.setOrderProcessRecordId(record.getOrderProcessRecordId());//记录报工结果表id
            } else {
                TBusOrderProcessRecord record = processRecord;
                record.setRecordQty(saveDto.getRecordQty() + record.getRecordQty());
                record.setBusType("PD");
                record.setReportTime(new Date());
                orderProcessRecordRepository.save(record);
                history.setOrderProcessRecordId(record.getOrderProcessRecordId());//记录报工结果表id
            }
        } else {
            TBusOrderProcessRecord record = new TBusOrderProcessRecord();
            BeanUtils.copyProperties(saveDto, record);
            record.setBusType("PD");
            record.setClassId(tSysClass);
            record.setPersonId(personnelInfo);
            record.setProcessId(processInfo);
            record.setProcessName(processInfo.getProcessName());
            record.setProcessNumber(processInfo.getProcessNumber());
            record.setReportTime(new Date());
            orderProcessRecordRepository.save(record);
            history.setOrderProcessRecordId(record.getOrderProcessRecordId());//记录报工结果表id
        }
        history.setClassId(tSysClass);
        history.setPersonId(personnelInfo);
        history.setProcessId(processInfo);
        history.setProcessName(processInfo.getProcessName());
        history.setProcessNumber(processInfo.getProcessNumber());
        history.setReportTime(new Date());
        history.setReportStatus("0");
        orderProcessHistoryRepository.save(history);
        return ResultUtil.success();
    }

    @Override
    public Integer getSize(OrderProcessRecordSearchDto searchDto, String userId) {
        if (searchDto.getOrderProcessId() == null || searchDto.getOrderProcessId() == 0) {
            throw new RuntimeException("工序执行表ID不能为空");
        }
        TBusOrderProcess tBusOrderProcess = orderProcessRepository.findById(searchDto.getOrderProcessId()).orElse(null);
//        TSysPersonnelInfo personnelInfo = tBusOrderProcess.getPersonId();
//        TSysProcessInfo processInfo = tBusOrderProcess.getProcessId();
        TSysClass tSysClass = tBusOrderProcess.getClassId();
        TSysPersonnelInfo personnelInfo = tSysPersonnelInfoRepository.findByUserId(userId);
        searchDto.setPersonId(personnelInfo.getPersonnelId());
        searchDto.setClassId(tSysClass.getClassId());
        //查找有几个分组
        Integer size = orderProcessHistoryRepository.getSize(searchDto.getOrderProcessId());
        if (size == null) {
            return 0;
        } else {
            return size + 1;
        }
    }

    @Override
    public OrderProcessHistory getOrderProcessHistory(OrderProcessRecordSearchDto searchDto, String userId) {
        if (searchDto.getOrderProcessId() == null || searchDto.getOrderProcessId() == 0) {
            throw new RuntimeException("工序执行表ID不能为空");
        }
        TBusOrderProcess tBusOrderProcess = orderProcessRepository.findById(searchDto.getOrderProcessId()).orElse(null);
//        TSysPersonnelInfo personnelInfo = tBusOrderProcess.getPersonId();
//        TSysProcessInfo processInfo = tBusOrderProcess.getProcessId();
        TSysClass tSysClass = tBusOrderProcess.getClassId();
        TSysPersonnelInfo personnelInfo = tSysPersonnelInfoRepository.findByUserId(userId);
        searchDto.setPersonId(personnelInfo.getPersonnelId());
        searchDto.setClassId(tSysClass.getClassId());
        //查找有几个分组
        Integer size = orderProcessHistoryRepository.getSize(searchDto.getOrderProcessId());
        List<Map> maps = orderProcessHistoryRepository.getRecord(searchDto.getOrderProcessId());
        String mapStr = JSON.toJSONString(maps);
        OrderProcessHistory orderProcessHistory = new OrderProcessHistory();
        List<OrderProcessHistoryVo> histories = JSON.parseArray(mapStr, OrderProcessHistoryVo.class);
        List<OrderRecordHistoryListVo> orderRecordHistoryListVos = new ArrayList<>();
        //对历史记录进行分组汇总
        if (size == null) {
            return null;
        }
        List<TBusOrderHead> heads = orderHeadRepository.findByOrderNo(searchDto.getOrderNo());
        OrderCheckVo orderCheckVo = new OrderCheckVo();
        for (int i = 0; i < size + 1; i++) {
            List<OrderProcessHistoryVo> historyVoList = new ArrayList<>();
            OrderRecordHistoryListVo historyListVo = new OrderRecordHistoryListVo();
            for (OrderProcessHistoryVo history : histories) {
                history.setRecordUnitStr(GlobalConstant.getCodeDscName("UNIT0000", history.getRecordUnit()));
                if (history.getStockCount() == (i)) {
                    orderCheckVo.setRecordTypePd(GlobalConstant.getCodeDscName("STOCKTAKING0000", history.getRecordTypePd()));
                    historyVoList.add(history);
                }
            }
            if (historyVoList.size() == 0) {
                continue;
            }
//            if (orderCheckVo.getHistoryVos() != null) {
//                orderCheckVo.getHistoryVos().addAll(historyVoList);
//            } else {
//                orderCheckVo.setHistoryVos(historyVoList);
//            }

            historyVoList.sort(new Comparator<OrderProcessHistoryVo>() {
                @Override
                public int compare(OrderProcessHistoryVo o1, OrderProcessHistoryVo o2) {
                    return o1.getOrderProcessHistoryId() - o2.getOrderProcessHistoryId();
                }
            });
            historyListVo.setHistoryVos(historyVoList);
            historyListVo.setPersonName(historyVoList.get(0).getPersonName());
            historyListVo.setReportTime(historyVoList.get(0).getReportTime());
            historyListVo.setClassName(historyVoList.get(0).getClassName());
            orderRecordHistoryListVos.add(historyListVo);
            if (historyVoList.size() > 0) {
                orderCheckVo.setReportTime(historyVoList.get(historyVoList.size() - 1).getReportTime());
                orderCheckVo.setClassId(tSysClass.getClassId());
                orderCheckVo.setPersonId(personnelInfo.getPersonnelId());

            }
        }
        Map<String, OrderProcessRecordVo> map = new HashMap<>();
        List<OrderProcessRecordVo> recordVoList = new ArrayList<>();
        for (OrderProcessHistoryVo history : histories) {
            if (map.containsKey(history.getMaterialName())) {
                OrderProcessRecordVo recordVo = map.get(history.getMaterialName());
                recordVo.setRecordQty(recordVo.getRecordQty() + history.getRecordQty());
                recordVo.setRecordQty(BigDecimalUtil.format(recordVo.getRecordQty(), 2));
            } else {
                OrderProcessRecordVo recordVo = new OrderProcessRecordVo();
                BeanUtils.copyProperties(history, recordVo);
                map.put(recordVo.getMaterialName(), recordVo);
            }
            BeanUtils.copyProperties(history, orderCheckVo);
        }
        recordVoList.addAll(map.values());
        if (orderCheckVo.getRecordVos() != null) {
            orderCheckVo.getRecordVos().addAll(recordVoList);
        } else {
            orderCheckVo.setRecordVos(recordVoList);
        }
        //分组历史记录列表

        orderCheckVo.getRecordVos().sort(new Comparator<OrderProcessRecordVo>() {
            @Override
            public int compare(OrderProcessRecordVo o1, OrderProcessRecordVo o2) {
                return o1.getOrderProcessHistoryId() - o2.getOrderProcessHistoryId();
            }
        });
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        orderRecordHistoryListVos.sort(new Comparator<OrderRecordHistoryListVo>() {
            @SneakyThrows
            @Override
            public int compare(OrderRecordHistoryListVo o1, OrderRecordHistoryListVo o2) {
                Date date1 = format.parse(o1.getReportTime());
                Date date2 = format.parse(o2.getReportTime());
                return (int) (date2.getTime() / 1000 - date1.getTime() / 1000);
            }
        });
        if (heads.size() > 0) {
            orderCheckVo.setMaterialName(heads.get(0).getBodyMaterialName());
        }
        orderProcessHistory.setOrderCheckVo(orderCheckVo);
        orderProcessHistory.setHistoryListVos(orderRecordHistoryListVos);
        //
        if (orderCheckVo == null) {
            return null;
        }
        return orderProcessHistory;
    }

    @Override
    public ResponseResult<OrderHandoverVo> handOver(OrderProcessRecordSearchDto searchDto, String userId) {
        if (searchDto.getOrderProcessId() == null || searchDto.getOrderProcessId() == 0) {
            throw new RuntimeException("工序执行表ID不能为空");
        }
        TBusOrderProcess tBusOrderProcess = orderProcessRepository.findById(searchDto.getOrderProcessId()).orElse(null);
//        TSysPersonnelInfo personnelInfo = tBusOrderProcess.getPersonId();
//        TSysProcessInfo processInfo = tBusOrderProcess.getProcessId();
        TSysClass tSysClass = tBusOrderProcess.getClassId();
        TSysPersonnelInfo personnelInfo = tSysPersonnelInfoRepository.findByUserId(userId);
        searchDto.setPersonId(personnelInfo.getPersonnelId());
        searchDto.setClassId(tSysClass.getClassId());
        List<TBusOrderHead> orderHeads = orderHeadRepository.findAllByOrderNoAndBodyLot(searchDto.getOrderNo(), searchDto.getBodyLot());
        if (orderHeads.size() > 0) {
            TBusOrderHead orderHead = orderHeads.get(0);
            OrderHandoverVo handoverVo = new OrderHandoverVo();
//            List<Map> maps=orderProcessRecordRepository.getDetails(searchDto.getOrderNo(),searchDto.getProcessId(),searchDto.getClassId());
//            String mapStr=JSON.toJSONString(maps);
//            List<OrderProcessRecordVo> recordVos=JSON.parseArray(mapStr,OrderProcessRecordVo.class);
            //不过滤盘点类型
            List<TBusOrderProcessHistory> orderProcessRecordList = orderProcessHistoryRepository.findAllByOrderProcessIdAndBusType(searchDto.getOrderProcessId(), "PD");
//            List<TBusOrderProcessRecord> orderProcessRecordList = orderProcessRecordRepository.findAllByOrderProcessIdAndBusType(searchDto.getOrderProcessId(), "PD");
            //报工数据
            var orderBGProcessRecordList = orderProcessRecordRepository.findAllByOrderProcessIdAndBusType(searchDto.getOrderProcessId(), "BG");
//            var orderBGProcessRecordList = orderProcessHistoryRepository.findAllByOrderProcessIdAndBusType(searchDto.getOrderProcessId(), "BG");
            if (orderProcessRecordList == null || orderProcessRecordList.size() == 0) {
                return ResultUtil.error(10001, "请先进行盘点,在进行移交");
            }
            List<OrderProcessRecordVo> recordVos = new ArrayList<>();
            Float importPot = 0f;
            Float totalQty = 0f;
            Float realPrdQty2 = 0f;
            Float ungradedWasteQty = 0f;
            String recordUnitName = null;
            String recordName = null;
            for (var orderProcessRecord : orderProcessRecordList) {
//                //过滤移交类型为：交接班盘点
//                var recordTypePd = orderProcessRecord.getRecordTypePd() == null ? "" : orderProcessRecord.getRecordTypePd();
//                if (LichengConstants.STOCKTAKING0001.equals(recordTypePd)) {
//                    var recordVo = new OrderProcessRecordVo();
//                    BeanUtils.copyProperties(orderProcessRecord, recordVo);
//                    recordVo.setClassId(orderProcessRecord.getClassId() == null ? null : orderProcessRecord.getClassId().getClassId());
//                    recordVo.setClassName(orderProcessRecord.getClassId() == null ? "" : orderProcessRecord.getClassId().getName());
//                    recordVo.setPersonName(orderProcessRecord.getPersonId() == null ? "" : orderProcessRecord.getPersonId().getName());
//                    recordVo.setReportTime(orderProcessRecord.getReportTime() == null ? "" : Utils.formatDateTimeToString(orderProcessRecord.getReportTime()));
//                    recordVos.add(recordVo);
//                }
                recordUnitName = GlobalConstant.getCodeDscName("UNIT0000", orderProcessRecord.getRecordUnit());
                var recordVo = new OrderProcessRecordVo();
                BeanUtils.copyProperties(orderProcessRecord, recordVo);
//                if (recordVo.getRecordType() != null && recordVo.getRecordType().equals("5")) {
//                    //上道半成品数量
//                    recordVo.setMaterialName("上道半成品数量");
//                }
                recordVo.setClassId(orderProcessRecord.getClassId() == null ? null : orderProcessRecord.getClassId().getClassId());
                recordVo.setClassName(orderProcessRecord.getClassId() == null ? "" : orderProcessRecord.getClassId().getName());
                recordVo.setPersonName(orderProcessRecord.getPersonId() == null ? "" : orderProcessRecord.getPersonId().getName());
                recordVo.setReportTime(orderProcessRecord.getReportTime() == null ? "" : Utils.formatDateTimeToString(orderProcessRecord.getReportTime()));
                recordVos.add(recordVo);
                //处理人
                recordName = orderProcessRecord.getPersonId().getName();
            }
            //倒序
            recordVos.sort((o1, o2) -> o2.getReportTime().compareTo(o1.getReportTime()));
            for (TBusOrderProcessRecord record : orderBGProcessRecordList) {
                //投入锅数
                if (record.getImportPot() != null) {
                    importPot = importPot + record.getImportPot();
                }
                //合格品产后报工数量
                if ("3".equals(record.getRecordType())) {
                    totalQty = totalQty + record.getRecordQty();
                }
                if (StringUtils.isNotEmpty(record.getMaterialName())) {
                    if (RECORDTYPEL20001.equals(record.getMaterialName()) || RECORDTYPEL20002.equals(record.getMaterialName())) {
                        realPrdQty2 += record.getRecordQty();
                    } else if (RECORDTYPEL20003.equals(record.getMaterialName())) {
                        ungradedWasteQty += record.getRecordQty();
                    }
                }
            }
            handoverVo.setRecordName(recordName);
            handoverVo.setImportPot(importPot + "锅");
            //合格完成率=合格品产后报工数量/计划生产数量，单位%，保留两位小数。
            if (orderHead.getBodyPlanPrdQty() != null) {
                String qualifiedRate = BigDecimalUtil.div(totalQty * 100, orderHead.getBodyPlanPrdQty()) + "%";
                handoverVo.setQualifiedRate(qualifiedRate);
            } else {
                handoverVo.setQualifiedRate(0 + "%");
            }
            //AB料累计数量,废料累计数量
            handoverVo.setUngradedAbQty(realPrdQty2 + recordUnitName);
            handoverVo.setUngradedWasteQty(ungradedWasteQty + recordUnitName);
            if (recordVos.size() == 0) {
                return null;
            } else {
                recordVos.stream().forEach(recordVo -> {
                    recordVo.setRecordUnitStr(GlobalConstant.getCodeDscName("UNIT0000", recordVo.getRecordUnit()));
                    recordVo.setCapacityUnitStr(GlobalConstant.getCodeDscName("UNIT0000", recordVo.getCapacityUnit()));
                });
            }
            BeanUtils.copyProperties(recordVos.get(0), handoverVo);
            handoverVo.setMaterialName(orderHead.getBodyMaterialName());
            handoverVo.setMaterialId(orderHead.getBodyMaterialId());
            handoverVo.setMaterialNumber(orderHead.getBodyMaterialName());
            //预期产量
            handoverVo.setBodyPlanPrdQty(orderHead.getBodyPlanPrdQty());
            handoverVo.setReportTime(recordVos.get(recordVos.size() - 1).getReportTime());
            //实际产量：从报工结果表取
            Float weight = null;
            if (searchDto.getMaterialId() != null) {
                List<Map> maps = orderProcessRecordRepository.getBGRecordWeight(searchDto.getOrderNo(), searchDto.getProcessId(), tSysClass.getClassId(), searchDto.getMaterialId());
                String mapStr = JSON.toJSONString(maps);
                List<WeightVo> weightVos = JSON.parseArray(mapStr, WeightVo.class);
                if (weightVos.size() > 0) {
                    weight = weightVos.get(0).getRecordQty();
                }
            } else {
                weight = orderProcessRecordRepository.getBGRecordWeight(searchDto.getOrderNo(), searchDto.getProcessId(), tSysClass.getClassId());
            }
            if (weight == null) {
                weight = 0f;
            }
            if (weight != null) {
                handoverVo.setActualQty(weight);
                //未生产:预期产量-实际产量
                handoverVo.setUnProduceQty(orderHead.getBodyPlanPrdQty() - weight);
            }
            handoverVo.setUnit(orderHead.getBodyUnit());
            handoverVo.setUnitStr(GlobalConstant.getCodeDscName("UNIT0000", handoverVo.getUnit()));
            handoverVo.setRecordVos(recordVos);
            //
            handoverVo.setBodyPotQty(orderHead.getBodyPotQty() + "锅");
            //移交人
            if (searchDto.getHandOverUserId() != null) {
                TSysPersonnelInfo tSysPersonnelInfo = tSysPersonnelInfoRepository.findByUserId(searchDto.getHandOverUserId());
                handoverVo.setHandOverName(tSysPersonnelInfo.getName());
            }
            //报工历史记录表记录
            List<OrderProcessHistoryVo> orderProcessHistoryVoList = new ArrayList<>();
            OrderProcessHistoryVo orderProcessHistoryVo;
            List<TBusOrderProcessHistory> tBusOrderProcessHistoryList = orderProcessHistoryRepository.findAllByOrderProcessIdAndBusTypeOrderByOrderProcessHistoryIdDesc(tBusOrderProcess.getOrderProcessId(), "PD");
            for (TBusOrderProcessHistory tBusOrderProcessHistory : tBusOrderProcessHistoryList) {
                orderProcessHistoryVo = new OrderProcessHistoryVo();
                orderProcessHistoryVo.setOrderNo(tBusOrderProcessHistory.getOrderNo());
                orderProcessHistoryVo.setBodyLot(tBusOrderProcessHistory.getBodyLot());
                orderProcessHistoryVo.setClassName(tBusOrderProcessHistory.getClassId().getName());
                orderProcessHistoryVo.setClassId(tBusOrderProcessHistory.getClassId().getClassId());
                orderProcessHistoryVo.setReportTime(Utils.formatDateTimeToString(tBusOrderProcessHistory.getReportTime()));
                orderProcessHistoryVo.setPersonName(tBusOrderProcessHistory.getPersonId().getName());
                orderProcessHistoryVo.setProcessName(tBusOrderProcessHistory.getProcessName());
                orderProcessHistoryVo.setMaterialId(tBusOrderProcessHistory.getMaterialId());
                orderProcessHistoryVo.setMaterialNumber(tBusOrderProcessHistory.getMaterialNumber());
                orderProcessHistoryVo.setMaterialName(tBusOrderProcessHistory.getMaterialName());
                orderProcessHistoryVo.setRecordUnit(tBusOrderProcessHistory.getRecordUnit());
                orderProcessHistoryVo.setRecordUnitStr(GlobalConstant.getCodeDscName("UNIT0000", tBusOrderProcessHistory.getRecordUnit()));
                orderProcessHistoryVo.setRecordQty(tBusOrderProcessHistory.getRecordQty());
                orderProcessHistoryVo.setRecordTypePd(tBusOrderProcessHistory.getRecordTypePd());
                orderProcessHistoryVo.setStockCount(tBusOrderProcessHistory.getStockCount());
                orderProcessHistoryVo.setReportStatus(tBusOrderProcessHistory.getReportStatus());
                orderProcessHistoryVoList.add(orderProcessHistoryVo);
            }
            orderProcessHistoryVoList.sort((o1, o2) -> o2.getReportTime().compareTo(o1.getReportTime()));
            handoverVo.setOrderProcessHistoryVoList(orderProcessHistoryVoList);
            return ResultUtil.success(handoverVo);
        }
        return null;
    }

    @Override
    public List<TaskListVo> handOverOrderList(String userId) {
        List<Map> maps = orderHeadRepository.getHandOverList(userId);
        String mapStr = JSON.toJSONString(maps);
        List<TaskListVo> list = JSON.parseArray(mapStr, TaskListVo.class);
        return list;
    }

    @Override
    public List<OrderProcessResult> getBillNoInfo(String orderNo) {
        // 展示扫码关联的上道订单物料名称列表：通过关联“订单表”过滤查询当前订单的单据编号所有订单物料名称，并且展示下拉框中(多行记录)。
        List<OrderProcessResult> orderProcessResults = new ArrayList<>();
        OrderProcessResult orderProcessResult = null;
        TBusOrderHead orderHead = orderHeadRepository.getByOrderNo(orderNo);
        if (null != orderHead) {
            List<TBusOrderHead> orderHeads = orderHeadRepository.findByBillNo(orderHead.getBillNo());
            for (var order : orderHeads) {
                orderProcessResult = new OrderProcessResult();
                BeanUtils.copyProperties(order, orderProcessResult);
                orderProcessResults.add(orderProcessResult);
            }
        }
        return orderProcessResults;
    }

    @Transactional
    @Override
    public void deletePDHistory(Integer orderProcessHistoryId) {
        TBusOrderProcessHistory tBusOrderProcessHistory = orderProcessHistoryRepository.findById(orderProcessHistoryId).orElse(null);
        if (tBusOrderProcessHistory.getReportStatus().equals("2")) {
            throw new RuntimeException("当前记录已被删除");
        }
        tBusOrderProcessHistory.setReportStatus("2");
        orderProcessHistoryRepository.saveAndFlush(tBusOrderProcessHistory);
        Integer orderProcessRecordId = tBusOrderProcessHistory.getOrderProcessRecordId();
        TBusOrderProcessRecord tBusOrderProcessRecord = orderProcessRecordRepository.findById(orderProcessRecordId).orElse(null);
        tBusOrderProcessRecord.setRecordQty(tBusOrderProcessRecord.getRecordQty() - tBusOrderProcessHistory.getRecordQty());
        orderProcessRecordRepository.saveAndFlush(tBusOrderProcessRecord);
    }
}
