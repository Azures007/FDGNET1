package org.thingsboard.server.dao.mes.orderProcess;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.common.util.BigDecimalUtil;
import org.thingsboard.server.common.data.mes.*;
import org.thingsboard.server.common.data.mes.bus.TBusOrderHead;
import org.thingsboard.server.common.data.mes.bus.TBusOrderPPBom;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcessHistory;
import org.thingsboard.server.common.data.mes.bus.TBusOrderProcessRecord;
import org.thingsboard.server.common.data.mes.ncInventory.NcInventory;
import org.thingsboard.server.common.data.mes.ncInventory.NcInventoryInOut;
import org.thingsboard.server.dao.constant.GlobalConstant;
import org.thingsboard.server.dao.mes.orderProcess.AppOrderProcessRecordDeleteService;
import org.thingsboard.server.dao.mes.orderProcess.OrderProcessRecordService;
import org.thingsboard.server.dao.sql.mes.ncInventory.NcInventoryInoutRepository;
import org.thingsboard.server.dao.sql.mes.ncInventory.NcInventoryRepository;
import org.thingsboard.server.dao.sql.mes.order.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 报工记录删除服务，本代码只处理报工记录删除的接口服务
 * 报工记录在删除操作时，在结果表扣减报工数量后，在更新历史表的状态为已删除
 * @Auther: hhh
 * @Date: 2024/12/10 10:12
 * @Description:
 */
@Service
public class AppOrderProcessRecordDeleteServiceImpl implements AppOrderProcessRecordDeleteService {

    protected final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    OrderHeadRepository orderHeadRepository;

    @Autowired
    OrderPPBomRepository orderPPBomRepository;

    @Autowired
    OrderProcessHistoryRepository orderProcessHistoryRepository;

    @Autowired
    OrderProcessRecordRepository orderProcessRecordRepository;

    @Autowired
    OrderProcessRecordService orderProcessRecordService;

    @Autowired
    NcInventoryInoutRepository ncInventoryInoutRepository;

    @Autowired
    NcInventoryRepository ncInventoryRepository;

    @Autowired
    TBusOrderAccumulationRepository accumulationRepository;
    @Autowired
    org.thingsboard.server.dao.sql.mes.order.OrderPotCountRepository orderPotCountRepository;

    @Value("${submit.enabled:0}")
    String submitEnabled;

    /**
     * 删除报工记录
     * @param orderProcessHistoryId
     * @param checkRecordTypeBg
     */
    @Transactional
    @Override
    public void deleteRecord(Integer orderProcessHistoryId, Boolean checkRecordTypeBg, String isConfirm) {
        this.deleteRecord(orderProcessHistoryId, checkRecordTypeBg, false,isConfirm);
    }

    /**
     * 删除报工记录
     * @param orderProcessHistoryId
     * @param checkRecordTypeBg
     * @param deleteAutoRecord 是否可删除自动投入产出的记录
     */
    @Transactional
    @Override
    public void deleteRecord(Integer orderProcessHistoryId, Boolean checkRecordTypeBg, Boolean deleteAutoRecord, String isConfirm) {
        // 校验输入参数
        TBusOrderProcessHistory tBusOrderProcessHistory = validateAndGetHistory(orderProcessHistoryId);
        TBusOrderProcessRecord tBusOrderProcessRecord = validateAndGetRecord(tBusOrderProcessHistory.getOrderProcessRecordId());

        // 公共校验逻辑
        validateHistory(tBusOrderProcessHistory, tBusOrderProcessRecord, deleteAutoRecord);
        // 根据RecordType处理不同的删除逻辑
        switch (tBusOrderProcessHistory.getRecordType()) {
            case "1":
                // RecordType 1: 原辅料投入报工删除
                handleRawMaterialInputDeletion(tBusOrderProcessHistory, tBusOrderProcessRecord, checkRecordTypeBg,isConfirm);
                break;
            case "2":
                // RecordType 2: AB料产出报工删除
                handleABMaterialOutputDeletion(tBusOrderProcessHistory, tBusOrderProcessRecord, checkRecordTypeBg);
                break;
            case "3":
                // RecordType 3: 合格品产出报工删除
                handleQualifiedProductOutputDeletion(tBusOrderProcessHistory, tBusOrderProcessRecord, checkRecordTypeBg);
                break;
            case "4":
                // RecordType 4: 自定义报工删除
                handleCustomReportDeletion(tBusOrderProcessHistory, tBusOrderProcessRecord, checkRecordTypeBg);
                break;
            case "5":
                // RecordType 5: 前道自动投入报工删除
                handlePreProcessAutomaticInputDeletion(tBusOrderProcessHistory, tBusOrderProcessRecord, checkRecordTypeBg);
                break;
            case "6":
                // RecordType 6: AB料投入报工删除
                handleABMaterialInputDeletion(tBusOrderProcessHistory, tBusOrderProcessRecord, checkRecordTypeBg);
                break;
            default:
                throw new RuntimeException("Unsupported RecordType: " + tBusOrderProcessHistory.getRecordType());
        }
    }
    /**
     * 更新累计数量
     */
    private void updateAccumulatedQty(String orderNo, Integer orderProcessId, String devicePersonGroupId, Integer orderPPBomId, Integer materialId, String materialNumber, Float qty) {
        if (orderNo == null || orderProcessId == null || orderPPBomId == null || materialNumber == null || qty == null) {
            return;
        }
        var opt = accumulationRepository.findByOrderNoAndOrderProcessIdAndOrderPpbomIdAndDevicePersonGroupIdAndMaterialNumber(
                orderNo, orderProcessId, orderPPBomId, devicePersonGroupId == null ? "" : devicePersonGroupId, materialNumber);
        if (opt.isPresent()) {
            var acc = opt.get();
            acc.setAccumulatedQty(new java.math.BigDecimal(qty.toString()));
            acc.setLastUpdateTime(new Date());
            accumulationRepository.save(acc);
        } else {
            var acc = new org.thingsboard.server.common.data.mes.bus.TBusOrderAccumulation();
            acc.setOrderNo(orderNo);
            acc.setOrderProcessId(orderProcessId);
            acc.setOrderPpbomId(orderPPBomId);
            acc.setDevicePersonGroupId(devicePersonGroupId == null ? "" : devicePersonGroupId);
            acc.setMaterialId(materialId);
            acc.setMaterialNumber(materialNumber);
            acc.setAccumulatedQty(new java.math.BigDecimal(qty.toString()));
            acc.setCreatedTime(new Date());
            acc.setLastUpdateTime(new Date());
            accumulationRepository.save(acc);
        }
    }


    private void handleRawMaterialInputDeletion(TBusOrderProcessHistory tBusOrderProcessHistory, TBusOrderProcessRecord tBusOrderProcessRecord, Boolean checkRecordTypeBg, String isConfirm) {
        List<TBusOrderHead> heads = orderHeadRepository.findByOrderNo(tBusOrderProcessHistory.getOrderNo());
        TBusOrderHead tBusOrderHead = heads.get(0);
        if (tBusOrderProcessHistory.getReportStatus().equals("1")) {
            throw new RuntimeException("当前记录已被删除");
        }

        // 12953 原辅料类目和投入上道数量类目两种类型的报工记录，进行删除操作，判断当前执行工序是否存在合格品报工记录（判断报工历史记录表），如果存在则不能删除记录，弹层提示：“不允许删除，已生成产出合格品报工记录”，点击关闭按钮，关闭弹层。
        //原辅料类目和投入上道数量类目两种类型的报工记录，进行删除操作，
        // 判断当前执行工序是否存在合格品报工记录（判断报工历史记录表），
        // 如果存在则不能删除记录，弹层提示：“不允许删除，已生成产出合格品报工记录”，点击关闭按钮，关闭弹层。
        if (!orderProcessRecordRepository.findAllByOrderProcessIdAndBusType(tBusOrderProcessHistory.getOrderProcessId(), "3").isEmpty()) {
            throw new RuntimeException("不允许删除，已生成产出合格品报工记录");
        }
        // 回退锅数记录表（正常类型才回退）
        if (!LichengConstants.REPORTYPE0002.equals(tBusOrderProcessHistory.getRecordTypeBg())) {
            var opt = orderPotCountRepository.findByOrderProcessIdAndOrderPPBomIdAndDevicePersonGroupIdAndMaterialNumber(
                    tBusOrderProcessHistory.getOrderProcessId(), tBusOrderProcessHistory.getOrderPPBomId(),
                    tBusOrderProcessHistory.getDevicePersonGroupId() == null ? "" : tBusOrderProcessHistory.getDevicePersonGroupId(), tBusOrderProcessHistory.getMaterialNumber());
            opt.ifPresent(p -> {
                if(p.getPotNumber()<=p.getInputCount()){
                    //已满足1次的记录
                    if(isConfirm==null||isConfirm.isEmpty()){
                        //抛出异常，提示当前物料已满足一次投入重量或数量，删除后是否重新提交报工？
                        throw new IllegalArgumentException("当前物料已满足一次投入重量或数量，删除后是否重新提交报工？");
                    }

                    List<TBusOrderProcessHistory> hasOther= orderProcessHistoryRepository.findAllByOrderProcessIdAndPotNumberAndReportStatusAndOrderProcessHistoryIdIsNot(
                            tBusOrderProcessHistory.getOrderProcessId(),
                            tBusOrderProcessHistory.getPotNumber(),
                            LichengConstants.ORDER_PROCESS_HISTORY_STATUS_0,
                            tBusOrderProcessHistory.getOrderProcessHistoryId());
                    if(hasOther.isEmpty()){
                        //没有其他报工记录，在删除时扣投入次数
                        orderPotCountRepository.incrementInputCount(p.getId(), -1);
                        //没有其他订单报工记录，则更新锅数-1
                        orderPotCountRepository.updatePotNumberByOrderProcessAndMaterialNumber(tBusOrderProcessHistory.getOrderProcessId(), tBusOrderProcessHistory.getMaterialNumber(), p.getPotNumber()-1);

                    }else{
                        //有其他订单报工记录
                        if(isConfirm.equals("1")){
                            //已满足1次的记录且有其他报工记录，且确认重新报工，次数先-1，因为删除后不满足1次
                            orderPotCountRepository.incrementInputCount(p.getId(), -1);
                            float sum = (float) hasOther.stream()
                                    .mapToDouble(TBusOrderProcessHistory::getRecordQty) // 先用double精度计算
                                    .sum();
                            updateAccumulatedQty(tBusOrderProcessHistory.getOrderNo(),
                                    tBusOrderProcessHistory.getOrderProcessId(),
                                    tBusOrderProcessHistory.getDevicePersonGroupId(),
                                    tBusOrderProcessHistory.getOrderPPBomId(),
                                    tBusOrderProcessHistory.getMaterialId(),
                                    tBusOrderProcessHistory.getMaterialNumber(),
                                    sum);
                            //设置必须补料
                            tBusOrderProcessHistory.setIsSupplement("1");
                        }else{
                            //已满足1次的记录且没有其他报工记录，且确认不重新报工，次数不变
                        }
                    }
                }else{
                    //未满足1次的记录，则不更新
                }
            });
        }
        // 报工数量
        Float qty = (tBusOrderProcessRecord.getRecordQty() == null ? 0F : tBusOrderProcessRecord.getRecordQty().floatValue()) - (tBusOrderProcessHistory.getRecordQty() == null ? 0F : tBusOrderProcessHistory.getRecordQty().floatValue());
        tBusOrderProcessRecord.setRecordQty(qty);
        // 报工数量（手工输入）
        if (tBusOrderProcessHistory.getRecordManualQty() != null && tBusOrderProcessHistory.getRecordManualQty() > 0) {
            Float manualQty = (tBusOrderProcessRecord.getRecordManualQty() == null ? 0F : tBusOrderProcessRecord.getRecordManualQty().floatValue()) - (tBusOrderProcessHistory.getRecordManualQty() == null ? 0F : tBusOrderProcessHistory.getRecordManualQty().floatValue());
            tBusOrderProcessRecord.setRecordManualQty(manualQty);
        }

        //删除记录重新计算投入锅数和产后锅数
        if ("1".equals(tBusOrderProcessRecord.getRecordType()) && "1".equals(submitEnabled)) {
            //判断是否投入字段
            if (tBusOrderProcessRecord.getOrderPPBomId() != null) {
                Optional<TBusOrderPPBom> byId = orderPPBomRepository.findById(tBusOrderProcessRecord.getOrderPPBomId());
                if (!byId.isEmpty()) {
                    if (GlobalConstant.enableTrue.equals(byId.get().getMidPpbomEntryIsInto() + "")) {
                        //判断当前记录是否是组合报工记录
                        List<Integer> ppbomIds = orderPPBomRepository.joinPpbomId(tBusOrderProcessHistory.getOrderPPBomId(), tBusOrderProcessHistory.getOrderProcessId());
                        if (ppbomIds != null && ppbomIds.size() > 1) {
                            //组合报工记录
                            if (tBusOrderProcessHistory.getImportPotGroup() == 1) {
                                //当前记录删除并且当前记录所属记录修改为未投满
                                tBusOrderProcessHistory.setImportPotGroup(0);
                                orderProcessHistoryRepository.updateHistoryByJoinPPbomId(ppbomIds, tBusOrderProcessHistory.getOrderProcessId(), tBusOrderProcessHistory.getImportPot(), 0);
                                tBusOrderProcessRecord.setImportPot(tBusOrderProcessRecord.getImportPot() == null || tBusOrderProcessRecord.getImportPot().intValue() <= 0 ? 0 : tBusOrderProcessRecord.getImportPot() - 1);
                            }
                        } else {
                            //非产后报工,非组合报工
                            tBusOrderProcessRecord.setImportPot(tBusOrderProcessRecord.getImportPot() == null || tBusOrderProcessRecord.getImportPot().intValue() <= 0 ? 0 : tBusOrderProcessRecord.getImportPot() - 1);
                        }

                    }
                }
            }
        }
        orderProcessRecordRepository.saveAndFlush(tBusOrderProcessRecord);

        tBusOrderProcessHistory.setReportStatus(LichengConstants.ORDER_PROCESS_HISTORY_STATUS_1);//删除状态
        //还原库存数量，删除出库记录
        List<NcInventoryInOut> inout=ncInventoryInoutRepository.getAllByOrderProcessHistoryId(tBusOrderProcessHistory.getOrderProcessHistoryId());
        for(NcInventoryInOut ncInventoryInOut:inout){
            NcInventory inv=ncInventoryRepository.getOne(ncInventoryInOut.getBillId());
            float qty1 = inv.getQty() - ncInventoryInOut.getQty();
            inv.setQty(qty1<0?0:qty1);
            ncInventoryRepository.saveAndFlush(inv);
        }
        ncInventoryInoutRepository.deleteAll(inout);
        orderProcessHistoryRepository.saveAndFlush(tBusOrderProcessHistory);

    }

    private void handleABMaterialOutputDeletion(TBusOrderProcessHistory tBusOrderProcessHistory, TBusOrderProcessRecord tBusOrderProcessRecord, Boolean checkRecordTypeBg) {
        List<TBusOrderHead> heads = orderHeadRepository.findByOrderNo(tBusOrderProcessHistory.getOrderNo());
        TBusOrderHead tBusOrderHead = heads.get(0);

        // 报工数量
        Float qty = (tBusOrderProcessRecord.getRecordQty() == null ? 0F : tBusOrderProcessRecord.getRecordQty().floatValue()) - (tBusOrderProcessHistory.getRecordQty() == null ? 0F : tBusOrderProcessHistory.getRecordQty().floatValue());
        tBusOrderProcessRecord.setRecordQty(qty);
        // 报工数量（手工输入）
        if (tBusOrderProcessHistory.getRecordManualQty() != null && tBusOrderProcessHistory.getRecordManualQty() > 0) {
            Float manualQty = (tBusOrderProcessRecord.getRecordManualQty() == null ? 0F : tBusOrderProcessRecord.getRecordManualQty().floatValue()) - (tBusOrderProcessHistory.getRecordManualQty() == null ? 0F : tBusOrderProcessHistory.getRecordManualQty().floatValue());
            tBusOrderProcessRecord.setRecordManualQty(manualQty);
        }
        orderProcessRecordRepository.saveAndFlush(tBusOrderProcessRecord);
        tBusOrderProcessHistory.setReportStatus(LichengConstants.ORDER_PROCESS_HISTORY_STATUS_1);//删除状态
        orderProcessHistoryRepository.saveAndFlush(tBusOrderProcessHistory);

        // 烤线和扒皮工序，自动删除关联生成的投入产出记录
        autoDelelteHistroyByParentId(tBusOrderProcessHistory, checkRecordTypeBg);
    }

    private void handleQualifiedProductOutputDeletion(TBusOrderProcessHistory tBusOrderProcessHistory, TBusOrderProcessRecord tBusOrderProcessRecord, Boolean checkRecordTypeBg) {
        List<TBusOrderHead> heads = orderHeadRepository.findByOrderNo(tBusOrderProcessHistory.getOrderNo());
        TBusOrderHead tBusOrderHead = heads.get(0);

        // 报工数量
        Float qty = (tBusOrderProcessRecord.getRecordQty() == null ? 0F : tBusOrderProcessRecord.getRecordQty().floatValue()) - (tBusOrderProcessHistory.getRecordQty() == null ? 0F : tBusOrderProcessHistory.getRecordQty().floatValue());
        tBusOrderProcessRecord.setRecordQty(qty);
        // 报工数量（手工输入）
        if (tBusOrderProcessHistory.getRecordManualQty() != null && tBusOrderProcessHistory.getRecordManualQty() > 0) {
            Float manualQty = (tBusOrderProcessRecord.getRecordManualQty() == null ? 0F : tBusOrderProcessRecord.getRecordManualQty().floatValue()) - (tBusOrderProcessHistory.getRecordManualQty() == null ? 0F : tBusOrderProcessHistory.getRecordManualQty().floatValue());
            tBusOrderProcessRecord.setRecordManualQty(manualQty);
        }

        if ("3".equals(tBusOrderProcessRecord.getRecordType()) && "1".equals(submitEnabled)) {
            float exportPot = BigDecimalUtil.sub(tBusOrderProcessRecord.getExportPot() == null ? 0 : tBusOrderProcessRecord.getExportPot(), 0.5F).floatValue();
            tBusOrderProcessRecord.setExportPot(exportPot);
            tBusOrderProcessRecord.setExportPotMin(BigDecimalUtil.sub(tBusOrderProcessRecord.getExportPotMin() == null ? 0 : tBusOrderProcessRecord.getExportPotMin(), 1F).floatValue());
        }
        orderProcessRecordRepository.saveAndFlush(tBusOrderProcessRecord);
        tBusOrderProcessHistory.setReportStatus(LichengConstants.ORDER_PROCESS_HISTORY_STATUS_1);//删除状态
        orderProcessHistoryRepository.saveAndFlush(tBusOrderProcessHistory);

        // 斩拌工序，自动删除关联生成的烤线投入记录
        // 烤线和扒皮工序，自动删除关联生成的投入产出记录
        autoDelelteHistroyByParentId(tBusOrderProcessHistory, checkRecordTypeBg);
    }

    private void handleCustomReportDeletion(TBusOrderProcessHistory tBusOrderProcessHistory, TBusOrderProcessRecord tBusOrderProcessRecord, Boolean checkRecordTypeBg) {
        List<TBusOrderHead> heads = orderHeadRepository.findByOrderNo(tBusOrderProcessHistory.getOrderNo());
        TBusOrderHead tBusOrderHead = heads.get(0);

        // 报工数量
        Float qty = (tBusOrderProcessRecord.getRecordQty() == null ? 0F : tBusOrderProcessRecord.getRecordQty().floatValue()) - (tBusOrderProcessHistory.getRecordQty() == null ? 0F : tBusOrderProcessHistory.getRecordQty().floatValue());
        tBusOrderProcessRecord.setRecordQty(qty);
        // 报工数量（手工输入）
        if (tBusOrderProcessHistory.getRecordManualQty() != null && tBusOrderProcessHistory.getRecordManualQty() > 0) {
            Float manualQty = (tBusOrderProcessRecord.getRecordManualQty() == null ? 0F : tBusOrderProcessRecord.getRecordManualQty().floatValue()) - (tBusOrderProcessHistory.getRecordManualQty() == null ? 0F : tBusOrderProcessHistory.getRecordManualQty().floatValue());
            tBusOrderProcessRecord.setRecordManualQty(manualQty);
        }

        orderProcessRecordRepository.saveAndFlush(tBusOrderProcessRecord);
        tBusOrderProcessHistory.setReportStatus(LichengConstants.ORDER_PROCESS_HISTORY_STATUS_1);//删除状态
        orderProcessHistoryRepository.saveAndFlush(tBusOrderProcessHistory);

    }

    private void handlePreProcessAutomaticInputDeletion(TBusOrderProcessHistory tBusOrderProcessHistory, TBusOrderProcessRecord tBusOrderProcessRecord, Boolean checkRecordTypeBg) {
        List<TBusOrderHead> heads = orderHeadRepository.findByOrderNo(tBusOrderProcessHistory.getOrderNo());
        TBusOrderHead tBusOrderHead = heads.get(0);

        // 12953 原辅料类目和投入上道数量类目两种类型的报工记录，进行删除操作，判断当前执行工序是否存在合格品报工记录（判断报工历史记录表），如果存在则不能删除记录，弹层提示：“不允许删除，已生成产出合格品报工记录”，点击关闭按钮，关闭弹层。
        //原辅料类目和投入上道数量类目两种类型的报工记录，进行删除操作，
        // 判断当前执行工序是否存在合格品报工记录（判断报工历史记录表），
        // 如果存在则不能删除记录，弹层提示：“不允许删除，已生成产出合格品报工记录”，点击关闭按钮，关闭弹层。
        if (!orderProcessRecordRepository.findAllByOrderProcessIdAndBusType(tBusOrderProcessHistory.getOrderProcessId(), "3").isEmpty()) {
            throw new RuntimeException("不允许删除，已生成产出合格品报工记录");
        }

        // 报工数量
        Float qty = (tBusOrderProcessRecord.getRecordQty() == null ? 0F : tBusOrderProcessRecord.getRecordQty().floatValue()) - (tBusOrderProcessHistory.getRecordQty() == null ? 0F : tBusOrderProcessHistory.getRecordQty().floatValue());
        tBusOrderProcessRecord.setRecordQty(qty);
        // 报工数量（手工输入）
        if (tBusOrderProcessHistory.getRecordManualQty() != null && tBusOrderProcessHistory.getRecordManualQty() > 0) {
            Float manualQty = (tBusOrderProcessRecord.getRecordManualQty() == null ? 0F : tBusOrderProcessRecord.getRecordManualQty().floatValue()) - (tBusOrderProcessHistory.getRecordManualQty() == null ? 0F : tBusOrderProcessHistory.getRecordManualQty().floatValue());
            tBusOrderProcessRecord.setRecordManualQty(manualQty);
        }

        orderProcessRecordRepository.saveAndFlush(tBusOrderProcessRecord);
        tBusOrderProcessHistory.setReportStatus(LichengConstants.ORDER_PROCESS_HISTORY_STATUS_1);//删除状态
        orderProcessHistoryRepository.saveAndFlush(tBusOrderProcessHistory);

    }

    private void handleABMaterialInputDeletion(TBusOrderProcessHistory tBusOrderProcessHistory, TBusOrderProcessRecord tBusOrderProcessRecord, Boolean checkRecordTypeBg) {
        List<TBusOrderHead> heads = orderHeadRepository.findByOrderNo(tBusOrderProcessHistory.getOrderNo());
        TBusOrderHead tBusOrderHead = heads.get(0);

        // 报工数量
        Float qty = (tBusOrderProcessRecord.getRecordQty() == null ? 0F : tBusOrderProcessRecord.getRecordQty().floatValue()) - (tBusOrderProcessHistory.getRecordQty() == null ? 0F : tBusOrderProcessHistory.getRecordQty().floatValue());
        tBusOrderProcessRecord.setRecordQty(qty);
        // 报工数量（手工输入）
        if (tBusOrderProcessHistory.getRecordManualQty() != null && tBusOrderProcessHistory.getRecordManualQty() > 0) {
            Float manualQty = (tBusOrderProcessRecord.getRecordManualQty() == null ? 0F : tBusOrderProcessRecord.getRecordManualQty().floatValue()) - (tBusOrderProcessHistory.getRecordManualQty() == null ? 0F : tBusOrderProcessHistory.getRecordManualQty().floatValue());
            tBusOrderProcessRecord.setRecordManualQty(manualQty);
        }

        orderProcessRecordRepository.saveAndFlush(tBusOrderProcessRecord);
        tBusOrderProcessHistory.setReportStatus(LichengConstants.ORDER_PROCESS_HISTORY_STATUS_1);//删除状态
        orderProcessHistoryRepository.saveAndFlush(tBusOrderProcessHistory);
    }

    //1. 参数校验和获取历史记录
    private TBusOrderProcessHistory validateAndGetHistory(Integer orderProcessHistoryId) {
        if (orderProcessHistoryId == null) {
            throw new RuntimeException("报工历史记录ID不能为空");
        }

        Optional<TBusOrderProcessHistory> historyOpt = orderProcessHistoryRepository.findById(orderProcessHistoryId);
        if (historyOpt.isEmpty()) {
            throw new RuntimeException("删除的报工历史记录不存在，请重试");
        }

        return historyOpt.get();
    }

    private TBusOrderProcessRecord validateAndGetRecord(Integer orderProcessRecordId) {
        if (orderProcessRecordId == null) {
            throw new RuntimeException("报工结果表ID不能为空");
        }

        Optional<TBusOrderProcessRecord> recordOpt = orderProcessRecordRepository.findById(orderProcessRecordId);
        if (recordOpt.isEmpty()) {
            throw new RuntimeException("删除的报工结果表不存在，请重试");
        }

        return recordOpt.get();
    }

    // 公共校验逻辑
    private void validateHistory(TBusOrderProcessHistory tBusOrderProcessHistory, TBusOrderProcessRecord tBusOrderProcessRecord, Boolean deleteAutoRecord) {
        if (tBusOrderProcessHistory.getReportStatus().equals("1")) {
            throw new RuntimeException("当前报工记录已被删除");
        }

        if (tBusOrderProcessRecord == null) {
            throw new RuntimeException("不允许删除，删除的对象获取报工结果表不存在，请重试");
        }

//        if (LichengConstants.ORDER_RECORD_TYPE_5.equals(tBusOrderProcessHistory.getRecordType())) {
//            throw new RuntimeException("不允许删除，属于自动投入数量！");
//        }
        // 报工类型（RecordType）为投入前道数量
        // 工序报工记录列表，投入上道数量类目的记录，删除操作弹层提示：“不允许删除，属于自动投入数量。”。
        if (LichengConstants.ORDER_RECORD_TYPE_5.equals(tBusOrderProcessHistory.getRecordType()) && !deleteAutoRecord) {
            // 包装工序报工记录，校验投入前道数量
            if (LichengConstants.PROCESS_NUMBER_BAOZHUANG.equals(tBusOrderProcessHistory.getProcessNumber())) {
                throw new RuntimeException("不允许删除，属于自动投入数量！");
            }
        }
    }

    /**
     * 根据父级报工记录ID，自动删除关联生成的投入产出记录
     * 1、烤线和扒皮工序，自动删除关联生成的投入产出记录
     * @param tBusOrderProcessHistory
     * @param checkRecordTypeBg
     */
    private void autoDelelteHistroyByParentId(TBusOrderProcessHistory tBusOrderProcessHistory, Boolean checkRecordTypeBg) {
        // 烤线和扒皮工序，自动删除关联生成的投入产出记录
        // 斩拌接单生成蟹柳线的工序执行记录；合格品产出时，自动投入蟹柳线的投入; 所以优化斩拌自动删除烤线的投入
        if (tBusOrderProcessHistory.getProcessNumber().equals(LichengConstants.PROCESS_NUMBER_ZHANBAN)
        ) {
            Integer orderProcessHistoryId = tBusOrderProcessHistory.getOrderProcessHistoryId();
            List<TBusOrderProcessHistory> autoHistorys =
                    orderProcessHistoryRepository.findAllByOrderProcessHistoryParentIdAndBusTypeAndReportStatus(
                            orderProcessHistoryId,
                            LichengConstants.ORDER_BUS_TYPE_BG,
                            LichengConstants.ORDER_PROCESS_HISTORY_STATUS_0);
            // 遍历遍历删除关联生成的投入产出记录
            autoHistorys.forEach(autoHistory -> {
                // 烤线和扒皮工序
                this.deleteRecord(autoHistory.getOrderProcessHistoryId(), checkRecordTypeBg, true,null);
                logger.info(String.format("遍历自动删除关联生成的投入产出记录，工序编码: %s, OrderProcessHistoryId: %d", autoHistory.getProcessNumber(), autoHistory.getOrderProcessHistoryId()));
            });
        }
    }

}
