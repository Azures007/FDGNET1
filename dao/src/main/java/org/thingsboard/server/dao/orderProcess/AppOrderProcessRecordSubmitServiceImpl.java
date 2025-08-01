package org.thingsboard.server.dao.orderProcess;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.common.util.BigDecimalUtil;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.nc_inventory.NcInventory;
import org.thingsboard.server.common.data.nc_inventory.NcInventoryInOut;
import org.thingsboard.server.common.data.nc_warehouse.NcWarehouse;
import org.thingsboard.server.dao.TSysCraftinfo.TSysCraftInfoService;
import org.thingsboard.server.dao.constant.GlobalConstant;
import org.thingsboard.server.dao.dto.OrderRecordSaveDto;
import org.thingsboard.server.dao.dto.OrderRecordSaveListDto;
import org.thingsboard.server.dao.nc_inventory.NcInventoryService;
import org.thingsboard.server.dao.order.OrderHeadService;
import org.thingsboard.server.dao.order.OrderProcessDeviceRelService;
import org.thingsboard.server.dao.order.OrderProcessHistoryService;
import org.thingsboard.server.dao.order.OrderProcessPersonRelService;
import org.thingsboard.server.dao.sql.TSysCraftInfo.TSysCraftInfoRepository;
import org.thingsboard.server.dao.sql.TSysCraftInfo.TSysCraftMaterialRelRepository;
import org.thingsboard.server.dao.sql.TSysCraftInfo.TSysCraftProcessRelRepository;
import org.thingsboard.server.dao.sql.TSysProcessInfo.TSysProcessClassRelRepository;
import org.thingsboard.server.dao.sql.TSysProcessInfo.TSysProcessInfoRepository;
import org.thingsboard.server.dao.sql.device.TSysDeviceIotHistoryRepository;
import org.thingsboard.server.dao.sql.device.TSysDeviceIotRepository;
import org.thingsboard.server.dao.sql.licheng.MidMaterialRepository;
import org.thingsboard.server.dao.sql.nc_inventory.NcInventoryInoutRepository;
import org.thingsboard.server.dao.sql.nc_inventory.NcInventoryRepository;
import org.thingsboard.server.dao.sql.order.*;
import org.thingsboard.server.dao.sql.tSysClass.ClassGroupLeaderRepository;
import org.thingsboard.server.dao.sql.tSysClass.TSysClassRepository;
import org.thingsboard.server.dao.sql.tSysCodeDsc.TSysCodeDscRepository;
import org.thingsboard.server.dao.sql.tSysDevice.TSysDeviceRepository;
import org.thingsboard.server.dao.sql.tSysPersonnelInfo.ClassPersonnelRepository;
import org.thingsboard.server.dao.sql.tSysPersonnelInfo.TSysPersonnelInfoRepository;
import org.thingsboard.server.dao.tSysClass.TSysClassService;
import org.thingsboard.server.dao.user.UserService;
import org.thingsboard.server.dao.vo.OrderProcessRecordVo;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 报工提交服务，本代码只处理报工提交的接口服务
 * @Auther: hhh
 * @Date: 2024/12/10 10:12
 * @Description:
 */
@Service
public class AppOrderProcessRecordSubmitServiceImpl implements AppOrderProcessRecordSubmitService {

    protected final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    RedisTemplate redisTemplate;

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
    TSysCraftInfoService craftInfoService;

    @Autowired
    OrderProcessDeviceRelService orderProcessDeviceRelService;

    @Autowired
    OrderProcessPersonRelService orderProcessPersonRelService;

    @Autowired
    protected UserService userService;

    @Autowired
    NcInventoryRepository ncInventoryRepository;

    @Autowired
    NcInventoryInoutRepository ncInventoryInoutRepository;

    @Value("${submit.enabled:0}")
    String submitEnabled;

    /**
     * 报工提交
     *
     * @param saveDto
     * @param userId
     * @return
     * @throws ParseException
     */
    @Transactional(rollbackFor = Exception.class)
//    @Override
    public Integer submit(OrderRecordSaveDto saveDto, String userId) throws ParseException {
        // 公共校验逻辑
        validateSaveDto(saveDto);

        // 类目类型（类目编码）:1=原辅料投入；2=二级品产出（AB料产出报工）；3=产后数量（合格品产出报工）；4=自定义报工；5=投入前道数量（自动报工前道）；6=AB料投入
        // 根据RecordType处理不同业务逻辑
        switch (saveDto.getRecordType()) {
            case "1":
                // RecordType 1: 原辅料投入报工
                return handleRawMaterialInput(saveDto, userId);
            case "2":
                // RecordType 2: AB料产出报工
                return handleABMaterialOutput(saveDto, userId);
            case "3":
                // RecordType 3: 合格品产出报工
                return handleQualifiedProductOutput(saveDto, userId);
            case "4":
                // RecordType 4: 自定义报工
                return handleCustomReport(saveDto, userId);
            case "5":
                // RecordType 5: 前道自动投入报工
                return handlePreProcessAutomaticInput(saveDto, userId);
            case "6":
                // RecordType 6: AB料投入
                return handleABMaterialInput(saveDto, userId);
            default:
                // 未知RecordType，直接返回或抛出异常
                throw new RuntimeException("Unsupported RecordType: " + saveDto.getRecordType());
        }
    }

    // 公共校验逻辑
    private void validateSaveDto(OrderRecordSaveDto saveDto) {
        if (StringUtils.isEmpty(saveDto.getRecordType())) {
            throw new RuntimeException("报工提交时，报工类型不能为空");
        }
        if (saveDto.getOrderProcessId() == null || saveDto.getOrderProcessId() == 0) {
            throw new RuntimeException("工序执行表ID不能为空");
        }
        if (StringUtils.isEmpty(saveDto.getRecordUnit())) {
            throw new RuntimeException("单位不能为空");
        }
    }

    private Integer handleRawMaterialInput(OrderRecordSaveDto saveDto, String userId) throws ParseException {
        if (StringUtils.isEmpty(saveDto.getMaterialNumber())) {
            throw new RuntimeException("原辅料报工提交时，需要选择二级类目");
        }
        // 原辅料投入的具体处理逻辑
        return submitOrderType1(saveDto, userId);
    }

    private Integer handleABMaterialOutput(OrderRecordSaveDto saveDto, String userId) throws ParseException {
        if (StringUtils.isEmpty(saveDto.getMaterialNumber())) {
            throw new RuntimeException("AB料报工提交时，需要选择二级类目");
        }
        // AB料产出的具体处理逻辑
        return submitOrderType2(saveDto, userId);
    }

    private Integer handleQualifiedProductOutput(OrderRecordSaveDto saveDto, String userId) throws ParseException {
        // 合格品产出的具体处理逻辑
        return submitOrderType3(saveDto, userId);
    }

    private Integer handleCustomReport(OrderRecordSaveDto saveDto, String userId) throws ParseException {
        // 自定义报工的具体处理逻辑
        return submitOrderType4(saveDto, userId);
    }

    private Integer handlePreProcessAutomaticInput(OrderRecordSaveDto saveDto, String userId) throws ParseException {
        // 前道自动投入报工（投入）
        return submitOrderType5(saveDto, userId);
    }

    private Integer handleABMaterialInput(OrderRecordSaveDto saveDto, String userId) throws ParseException {
        // AB料投入的具体处理逻辑
        return submitOrderType6(saveDto, userId);
    }

    // 报工提交：原辅料投入
    public Integer submitOrderType1(OrderRecordSaveDto saveDto, String userId) throws ParseException {
        if ("1".equals(saveDto.getRecordType())) {
            if (StringUtils.isEmpty(saveDto.getMaterialNumber())) {
                throw new RuntimeException("原辅料报工提交时，需要选择二级类目");
            }
        }
        TBusOrderProcess tBusOrderProcess = orderProcessRepository.findById(saveDto.getOrderProcessId()).orElse(null);
        TSysProcessInfo processInfo = tBusOrderProcess.getProcessId();
        TSysClass tSysClass = tBusOrderProcess.getClassId();
        TSysPersonnelInfo personnelInfo = tSysPersonnelInfoRepository.findByUserId(userId);
        List<TBusOrderHead> heads = orderHeadRepository.findByOrderNo(saveDto.getOrderNo());
        TBusOrderHead tBusOrderHead = heads.get(0);
        if (LichengConstants.ORDERSTATUS_4.equals(tBusOrderHead.getOrderStatus())) {
            throw new RuntimeException("订单已挂起，禁止操作。");
        }
        //获取当前锅数
        int currentPot1 = 0;
        //根据工序执行表ID，多选机台号id，多选操作员ID，获取报工结果表的记录
        List<Map> maps = null;
        String deviceGroupId = "";//多选机台号分组标识ID
        String devicePersonGroupId = "";//多选操作员分组标识ID
        //校验机台号是否已经被报工过，校验维度：工序执行表ID+机台号分组标识+操作员分组标识
        if (saveDto.getDeviceIds() != null && saveDto.getDeviceIds().size() > 0) {
            deviceGroupId = orderProcessDeviceRelService.createdDeviceGroupId(saveDto.getOrderProcessId(), saveDto.getDeviceIds());
        }
        if (saveDto.getDevicePersonIds() != null && saveDto.getDevicePersonIds().size() > 0) {
            devicePersonGroupId = orderProcessPersonRelService.createdPersonGroupId(saveDto.getOrderProcessId(), saveDto.getDevicePersonIds());
        }
        maps = orderProcessRecordRepository.getBGRecord(saveDto.getOrderProcessId(), deviceGroupId, devicePersonGroupId, saveDto.getRecordTypeBg(), saveDto.getRecordUnit());
//        maps = orderProcessRecordRepository.getBGRecord(saveDto.getOrderProcessId(), deviceGroupId, devicePersonGroupId);

        String mapStr = JSON.toJSONString(maps);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        Date dateStartTime = null;
        if (!StringUtils.isEmpty(saveDto.getIotCollectionLastTime())) {
            date = sdf.parse(saveDto.getIotCollectionLastTime());
        }
        if (!StringUtils.isEmpty(saveDto.getIotCollectionStartTime())) {
            dateStartTime = sdf.parse(saveDto.getIotCollectionStartTime());
        }
        List<OrderProcessRecordVo> recordVos = JSON.parseArray(mapStr, OrderProcessRecordVo.class);
        TBusOrderProcessRecord record = new TBusOrderProcessRecord();
        BeanUtils.copyProperties(saveDto, record);
        record.setReportTime(new Date());
        record.setClassId(tSysClass);
        record.setPersonId(personnelInfo);
        record.setProcessId(processInfo);
        record.setProcessName(processInfo.getProcessName());
        record.setProcessNumber(processInfo.getProcessNumber());
        //机排手，机台号(多选)
        record.setDeviceGroupId(deviceGroupId == null ? "" : deviceGroupId);
        record.setDevicePersonGroupId(devicePersonGroupId == null ? "" : devicePersonGroupId);
        for (OrderProcessRecordVo recordVo : recordVos) {
            if (recordVo.getRecordType() != null && !recordVo.getRecordType().equals(saveDto.getRecordType())) {
                // 报工类型不同
                continue;
            } else
            if (recordVo.getMaterialId() != null && recordVo.getMaterialId().equals(saveDto.getMaterialId())
                    || recordVo.getMaterialNumber() != null && recordVo.getMaterialNumber().equals(saveDto.getMaterialNumber())
                    || recordVo.getMaterialName() != null && recordVo.getMaterialName().equals(saveDto.getMaterialName())
                    || recordVo.getLsmMaterialId() != null && recordVo.getLsmMaterialId().equals(saveDto.getLsmMaterialId())) {
                if (!saveDto.getRecordUnit().equals(recordVo.getRecordUnit())) {
                    throw new RuntimeException("提交的单位与历史报工的单位不一致！");
                }
                record.setImportPot(recordVo.getImportPot());
                record.setExportPot(recordVo.getExportPot());
                record.setExportPotMin(recordVo.getExportPotMin());
                record.setRecordQty(BigDecimalUtil.add(recordVo.getRecordQty() == null ? 0 : recordVo.getRecordQty(), saveDto.getRecordQty()).floatValue());
                record.setRecordManualQty(BigDecimalUtil.add(recordVo.getRecordManualQty() == null ? 0 : recordVo.getRecordManualQty(), saveDto.getRecordManualQty()).floatValue());
                record.setOrderProcessRecordId(recordVo.getOrderProcessRecordId());
                break;
            } else {
                record.setRecordQty(saveDto.getRecordQty());
                record.setRecordManualQty(saveDto.getRecordManualQty());
            }
        }
        if ("1".equals(saveDto.getRecordType()) && "1".equals(submitEnabled)) {
            String processNumber = saveDto.getProcessNumber() == null ? "" : saveDto.getProcessNumber();
            currentPot1= getAllPot(saveDto.getOrderProcessId(), saveDto.getOrderPPBomId());
            /*// 斩拌
            if (LichengConstants.PROCESS_NUMBER_ZHANBAN.equals(processNumber)) {
                //斩拌工序验证锅数
                Integer orderId = orderHeadRepository.findByOrderNo(saveDto.getOrderNo()).get(0).getOrderId();
                TBusOrderHead tBusOrderHead2 = orderHeadRepository.findById(orderId).get();
                int pot = tBusOrderHead2.getBodyPotQty() == null ? 0 : tBusOrderHead2.getBodyPotQty();
                float exportPot = orderPPBomRepository.findExportPotByImportAll(orderId, LichengConstants.PROCESS_NUMBER_ZHANBAN, "-1", "-1");
                if (pot <= exportPot) {
                    throw new RuntimeException("积累投入锅数超出计划锅数，请变更订单！\n(积累投入锅数:" + exportPot + "锅  计划锅数：" + pot + "锅)");
                }
            }*/
            //判断是否投入字段
            if (saveDto.getOrderPPBomId() != null) {
                Optional<TBusOrderPPBom> byId = orderPPBomRepository.findById(saveDto.getOrderPPBomId());
                if (!byId.isEmpty()) {
                    // 去除判断是否投入字段的判断,不需要判断 2022-11-04
//                    if (GlobalConstant.enableTrue.equals(byId.get().getMidPpbomEntryIsInto() + "")) {
//                        if (record.getRecordQty() != null && record.getRecordQty() > 0) {
                    if (saveDto.getIsReplaceGroup() != null && saveDto.getIsReplaceGroup().intValue() == 1) {
                        //组合投料根据前端返回当前锅数

                        record.setImportPotGroup(saveDto.getImportPotGroup());
                        record.setImportPot(saveDto.getImportPot().floatValue());
                        if (saveDto.getImportPotGroup().intValue() == 1) {
                            int orderPpbomId = record.getOrderPPBomId();
                            int orderProcessId = record.getOrderProcessId();
                            //获取用料id列表
                            List<Integer> ppboms = orderPPBomRepository.joinPpbomId(orderPpbomId, orderProcessId);
                            ppboms.stream().forEach(ppbom -> {
                                var recordList = orderProcessRecordRepository.getOrderProcessRecordBgGroup(record.getOrderProcessId(), ppbom, record.getRecordTypeBg(), record.getDevicePersonGroupId()
                                        , LichengConstants.ORDER_PROCESS_IMPORT_POT_GROUP_0, saveDto.getImportPot().floatValue());
                                for (int i = 0; i < recordList.size(); i++) {
                                    recordList.get(i).setImportPotGroup(LichengConstants.ORDER_PROCESS_IMPORT_POT_GROUP_1);
                                }
                                orderProcessRecordRepository.saveAll(recordList);
                            });
                        }
                    } else if (LichengConstants.REPORTYPE0002.equals(record.getRecordTypeBg())) {
                        //报工类型为尾料或者其他，则默认为1
                        record.setImportPot(1f);
                    } else {
                        //投入锅数+1
                        record.setImportPot(record.getImportPot() == null ? 1 : record.getImportPot() + 1);
                    }
//                        }
//                    }
                }
            }
        }
        record.setRecordUnit(saveDto.getRecordUnit());
        record.setBusType(LichengConstants.ORDER_BUS_TYPE_BG);
        record.setIotCollectionStartTime(dateStartTime);
        record.setIotCollectionLastTime(date);
        orderProcessRecordRepository.saveAndFlush(record);

        TBusOrderProcessHistory history = new TBusOrderProcessHistory();
        BeanUtils.copyProperties(saveDto, history);
        history.setOrderProcessRecordId(record.getOrderProcessRecordId());//记录报工结果表id
        history.setClassId(tSysClass);
        history.setAllImportPot(currentPot1);
        history.setPersonId(personnelInfo);
        history.setProcessId(processInfo);
        history.setProcessName(processInfo.getProcessName());
        history.setProcessNumber(processInfo.getProcessNumber());
        //机排手，机台号(多选)
        history.setDeviceGroupId(deviceGroupId == null ? "" : deviceGroupId);
        history.setDevicePersonGroupId(devicePersonGroupId == null ? "" : devicePersonGroupId);
        history.setReportTime(new Date());
        history.setBusType(LichengConstants.ORDER_BUS_TYPE_BG);
        history.setReportStatus(LichengConstants.ORDER_PROCESS_HISTORY_STATUS_0);//正常状态
        history.setIotCollectionStartTime(dateStartTime);
        history.setIotCollectionLastTime(date);
        history.setIotMath(saveDto.getIotMath());
        history.setIotQty(saveDto.getIotQty());
//        history.setRemark(saveDto.getRemark());
        //获取投入锅数、产出锅数
        if ("1".equals(saveDto.getRecordType()) && "1".equals(submitEnabled)) {
            if (saveDto.getIsReplaceGroup() != null && saveDto.getIsReplaceGroup().intValue() == 1) {
                //组合投料根据前端返回当前锅数
                history.setImportPotGroup(saveDto.getImportPotGroup());
                history.setImportPot(saveDto.getImportPot().floatValue());
                if (saveDto.getImportPotGroup().intValue() == 1) {
                    int orderPpbomId = history.getOrderPPBomId();
                    int orderProcessId = history.getOrderProcessId();
                    //获取用料id列表
                    List<Integer> ppboms = orderPPBomRepository.joinPpbomId(orderPpbomId, orderProcessId);
                    orderProcessHistoryRepository.updateHistoryByJoinPPbomId(ppboms, orderProcessId, saveDto.getImportPot().floatValue(), 1);
                }
            } else if (LichengConstants.REPORTYPE0002.equals(record.getRecordTypeBg())) {
                //报工类型为尾料或者其他，则默认为1
                history.setImportPot(1f);
            } else {
                var historyList = orderProcessHistoryRepository.getOrderProcessHistoryBg(history.getOrderProcessId(), history.getOrderPPBomId(), history.getRecordTypeBg(), history.getDevicePersonGroupId(), LichengConstants.ORDER_PROCESS_HISTORY_STATUS_0);
                var historyDeleteList = orderProcessHistoryRepository.getOrderProcessHistoryBg(history.getOrderProcessId(), history.getOrderPPBomId(), history.getRecordTypeBg(), history.getDevicePersonGroupId(), LichengConstants.ORDER_PROCESS_HISTORY_STATUS_1);
                for (int i = 0; i < historyDeleteList.stream().count(); i++) {
                    var historyDelete = historyDeleteList.get(i);
                    if (LichengConstants.ORDER_PROCESS_HISTORY_STATUS_1.equals(historyDelete.getReportStatus())) {
                        int historyId = historyDelete.getOrderProcessHistoryId();
                        float importPot = historyDelete.getImportPot();
                        long historyCount = historyList.stream().filter(r -> r.getImportPot().floatValue() == importPot && r.getOrderProcessHistoryId().intValue() != historyId).count();
                        if (historyCount > 0) {
                            continue;
                        } else {
                            //使用积累的
                            int sum = orderProcessRepository.getImportByPPbomAndOrderProcessId(history.getOrderProcessId(), history.getOrderPPBomId(), LichengConstants.ORDER_PROCESS_HISTORY_STATUS_0);
                            history.setImportPot(sum + 1F);
                            break;
                        }
                    }
                }
                if (null == history.getImportPot()) {
                    history.setImportPot(record.getImportPot());
                }
            }
        }
        history.setExportPot(record.getExportPot());
        history.setExportPotMin(record.getExportPotMin());
        history=orderProcessHistoryRepository.saveAndFlush(history);
        //扣减线边仓库存
        String cwkid =userService.getUserCurrentCwkid(userId);
        String pkOrg = userService.getUserCurrentPkOrg(userId);
        List<NcWarehouse> ncWarehouses = userService.findNcWarehouseByUserIdAndPkOrgAndWorkline(userId,pkOrg,cwkid);
        if(ncWarehouses!=null&& !ncWarehouses.isEmpty()){
            String wid=ncWarehouses.get(0).getPkStordoc();
            List<NcInventory> inventories =ncInventoryRepository.findByWarehouseIdAndMaterialCodeAndStatusOrderByLotAsc(wid,saveDto.getMaterialNumber(),"生效");
            //出库记录
            List<NcInventoryInOut> inventoriesInOuts = new ArrayList<>();
            if(inventories!=null&& !inventories.isEmpty()){
                Float qty=saveDto.getRecordQty();
                for(NcInventory inventory:inventories){
                    if(inventory.getQty()>=qty){
                        Float f=(float)(Math.round((inventory.getQty()-qty)*10000)/10000.0);
                        inventory.setQty(f);
                        qty=0f;
                        NcInventoryInOut inventoriesInOut=new NcInventoryInOut();
                        inventoriesInOut.setBillId(inventory.getBillId());
                        inventoriesInOut.setOrderProcessHistoryId(history.getOrderProcessHistoryId());
                        inventoriesInOut.setQty(-1*qty);
                        inventoriesInOuts.add(inventoriesInOut);
                        break;
                    }else{
                        qty=(float)(Math.round((qty-inventory.getQty())*10000)/10000.0);
                        inventory.setQty(0f);
                        NcInventoryInOut inventoriesInOut=new NcInventoryInOut();
                        inventoriesInOut.setBillId(inventory.getBillId());
                        inventoriesInOut.setOrderProcessHistoryId(history.getOrderProcessHistoryId());
                        inventoriesInOut.setQty(-1*inventory.getQty());
                        inventoriesInOuts.add(inventoriesInOut);
                    }
                }
                if(qty>0){
                    //插入负库存行
                    NcInventory newinventory=new NcInventory();
                    BeanUtils.copyProperties(inventories.get(0), newinventory);
                    newinventory.setBillId(UUID.randomUUID().toString());
                    newinventory.setQty(-1*qty);
                    inventories.add(newinventory);

                    NcInventoryInOut inventoriesInOut=new NcInventoryInOut();
                    inventoriesInOut.setBillId(newinventory.getBillId());
                    inventoriesInOut.setOrderProcessHistoryId(history.getOrderProcessHistoryId());
                    inventoriesInOut.setQty(-1*qty);
                    inventoriesInOuts.add(inventoriesInOut);
                }
                if(!inventoriesInOuts.isEmpty()){
                    ncInventoryInoutRepository.saveAll(inventoriesInOuts);
                }
                ncInventoryRepository.saveAll(inventories);
            }
        }
        return history.getOrderProcessHistoryId();
    }

    // 报工提交：AB料产出报工
    public Integer submitOrderType2(OrderRecordSaveDto saveDto, String userId) throws ParseException {

        //AB投入和产出，赋值二级类目
        if (StringUtils.isEmpty(saveDto.getRecordTypeL2()) && ("2".equals(saveDto.getRecordType()) || "6".equals(saveDto.getRecordType()))) {
            saveDto.setRecordTypeL2(saveDto.getMaterialNumber());
        }
        TBusOrderProcess tBusOrderProcess = orderProcessRepository.findById(saveDto.getOrderProcessId()).orElse(null);
        TSysProcessInfo processInfo = tBusOrderProcess.getProcessId();
        TSysClass tSysClass = tBusOrderProcess.getClassId();
        TSysPersonnelInfo personnelInfo = tSysPersonnelInfoRepository.findByUserId(userId);
        List<TBusOrderHead> heads = orderHeadRepository.findByOrderNo(saveDto.getOrderNo());
        TBusOrderHead tBusOrderHead = heads.get(0);
        if (LichengConstants.ORDERSTATUS_4.equals(tBusOrderHead.getOrderStatus())) {
            throw new RuntimeException("订单已挂起，禁止操作。");
        }
        //获取当前锅数
        int currentPot1 = 0;
        //根据工序执行表ID，多选机台号id，多选操作员ID，获取报工结果表的记录
        List<Map> maps = null;
        String deviceGroupId = "";//多选机台号分组标识ID
        String devicePersonGroupId = "";//多选操作员分组标识ID
        //校验机台号是否已经被报工过，校验维度：工序执行表ID+机台号分组标识+操作员分组标识
        if (saveDto.getDeviceIds() != null && saveDto.getDeviceIds().size() > 0) {
            deviceGroupId = orderProcessDeviceRelService.createdDeviceGroupId(saveDto.getOrderProcessId(), saveDto.getDeviceIds());
        }
        if (saveDto.getDevicePersonIds() != null && saveDto.getDevicePersonIds().size() > 0) {
            devicePersonGroupId = orderProcessPersonRelService.createdPersonGroupId(saveDto.getOrderProcessId(), saveDto.getDevicePersonIds());
        }
        maps = orderProcessRecordRepository.getBGRecord(saveDto.getOrderProcessId(), deviceGroupId, devicePersonGroupId, saveDto.getRecordTypeBg(), saveDto.getRecordUnit());
//        maps = orderProcessRecordRepository.getBGRecord(saveDto.getOrderProcessId(), deviceGroupId, devicePersonGroupId);

        String mapStr = JSON.toJSONString(maps);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        Date dateStartTime = null;
        if (!StringUtils.isEmpty(saveDto.getIotCollectionLastTime())) {
            date = sdf.parse(saveDto.getIotCollectionLastTime());
        }
        if (!StringUtils.isEmpty(saveDto.getIotCollectionStartTime())) {
            dateStartTime = sdf.parse(saveDto.getIotCollectionStartTime());
        }
        List<OrderProcessRecordVo> recordVos = JSON.parseArray(mapStr, OrderProcessRecordVo.class);
        TBusOrderProcessRecord record = new TBusOrderProcessRecord();
        BeanUtils.copyProperties(saveDto, record);
        record.setReportTime(new Date());
        record.setClassId(tSysClass);
        record.setPersonId(personnelInfo);
        record.setProcessId(processInfo);
        record.setProcessName(processInfo.getProcessName());
        record.setProcessNumber(processInfo.getProcessNumber());
        //机排手，机台号(多选)
        record.setDeviceGroupId(deviceGroupId == null ? "" : deviceGroupId);
        record.setDevicePersonGroupId(devicePersonGroupId == null ? "" : devicePersonGroupId);
        for (OrderProcessRecordVo recordVo : recordVos) {
            if (recordVo.getRecordType() != null && !recordVo.getRecordType().equals(saveDto.getRecordType())) {
                // 报工类型不同
                continue;
            } else
            if (recordVo.getMaterialId() != null && recordVo.getMaterialId().equals(saveDto.getMaterialId())
                    || recordVo.getMaterialNumber() != null && recordVo.getMaterialNumber().equals(saveDto.getMaterialNumber())
                    || recordVo.getMaterialName() != null && recordVo.getMaterialName().equals(saveDto.getMaterialName())
                    || recordVo.getLsmMaterialId() != null && recordVo.getLsmMaterialId().equals(saveDto.getLsmMaterialId())) {
                if (!saveDto.getRecordUnit().equals(recordVo.getRecordUnit())) {
                    throw new RuntimeException("提交的单位与历史报工的单位不一致！");
                }
                record.setImportPot(recordVo.getImportPot());
                record.setExportPot(recordVo.getExportPot());
                record.setExportPotMin(recordVo.getExportPotMin());
                record.setRecordQty(BigDecimalUtil.add(recordVo.getRecordQty() == null ? 0 : recordVo.getRecordQty(), saveDto.getRecordQty()).floatValue());
                record.setRecordManualQty(BigDecimalUtil.add(recordVo.getRecordManualQty() == null ? 0 : recordVo.getRecordManualQty(), saveDto.getRecordManualQty()).floatValue());
                record.setOrderProcessRecordId(recordVo.getOrderProcessRecordId());
                break;
            } else {
                record.setRecordQty(saveDto.getRecordQty());
                record.setRecordManualQty(saveDto.getRecordManualQty());
            }
        }
        record.setRecordUnit(saveDto.getRecordUnit());
        record.setBusType(LichengConstants.ORDER_BUS_TYPE_BG);
        record.setIotCollectionStartTime(dateStartTime);
        record.setIotCollectionLastTime(date);
        orderProcessRecordRepository.saveAndFlush(record);

        TBusOrderProcessHistory history = new TBusOrderProcessHistory();
        BeanUtils.copyProperties(saveDto, history);
        history.setOrderProcessRecordId(record.getOrderProcessRecordId());//记录报工结果表id
        history.setClassId(tSysClass);
        history.setAllImportPot(currentPot1);
        history.setPersonId(personnelInfo);
        history.setProcessId(processInfo);
        history.setProcessName(processInfo.getProcessName());
        history.setProcessNumber(processInfo.getProcessNumber());
        //机排手，机台号(多选)
        history.setDeviceGroupId(deviceGroupId == null ? "" : deviceGroupId);
        history.setDevicePersonGroupId(devicePersonGroupId == null ? "" : devicePersonGroupId);
        history.setReportTime(new Date());
        history.setBusType(LichengConstants.ORDER_BUS_TYPE_BG);
        history.setReportStatus(LichengConstants.ORDER_PROCESS_HISTORY_STATUS_0);//正常状态
        history.setIotCollectionStartTime(dateStartTime);
        history.setIotCollectionLastTime(date);
        history.setIotMath(saveDto.getIotMath());
        history.setIotQty(saveDto.getIotQty());
        history.setExportPot(record.getExportPot());
        history.setExportPotMin(record.getExportPotMin());
//        history.setRemark(saveDto.getRemark());
        // 获取设备采集时间：拉伸膜工序并且产后报工,合格品产出及AB料产出
        // recordType(报工类型)为二级品产出，并且recordTypeL2(二级类目类型)：5=使用膜
        if (LichengConstants.PROCESS_NUMBER_LASHENMO.equals(saveDto.getProcessNumber())
                && (saveDto.getRecordType().equals("2") && "5".equals(saveDto.getRecordTypeL2()))
                && saveDto.getRecordQty() > 0) {
            //拉伸膜工序并且产后报工,合格品产出及AB料产出
            List<Integer> deviceIds = saveDto.getDeviceIds();
            TSysDeviceIot tSysDeviceIot;
            for (Integer deviceId : deviceIds) {
                tSysDeviceIot = tSysDeviceIotRepository.findByDeviceIdAndRecordType(deviceId, saveDto.getRecordType());
                if (tSysDeviceIot == null) {
                    throw new RuntimeException("设备id：" + deviceId + "  还没初始化，请初始化后尝试");
                }
                history.setIotCollectionStartTime(tSysDeviceIot.getIotTime());
                tSysDeviceIot.setIotTime(date);
                tSysDeviceIotRepository.saveAndFlush(tSysDeviceIot);

            }
        }
        orderProcessHistoryRepository.saveAndFlush(history);

        return history.getOrderProcessHistoryId();
    }

    // 报工提交：合格品产出报工
    public Integer submitOrderType3(OrderRecordSaveDto saveDto, String userId) throws ParseException {
        TBusOrderProcess tBusOrderProcess = orderProcessRepository.findById(saveDto.getOrderProcessId()).orElse(null);
        TSysProcessInfo processInfo = tBusOrderProcess.getProcessId();
        TSysClass tSysClass = tBusOrderProcess.getClassId();
        TSysPersonnelInfo personnelInfo = tSysPersonnelInfoRepository.findByUserId(userId);
        List<TBusOrderHead> heads = orderHeadRepository.findByOrderNo(saveDto.getOrderNo());
        TBusOrderHead tBusOrderHead = heads.get(0);
        if (LichengConstants.ORDERSTATUS_4.equals(tBusOrderHead.getOrderStatus())) {
            throw new RuntimeException("订单已挂起，禁止操作。");
        }
        //获取当前锅数
        int currentPot1 = 0;
        //根据工序执行表ID，多选机台号id，多选操作员ID，获取报工结果表的记录
        List<Map> maps = null;
        String deviceGroupId = "";//多选机台号分组标识ID
        String devicePersonGroupId = "";//多选操作员分组标识ID
        //校验机台号是否已经被报工过，校验维度：工序执行表ID+机台号分组标识+操作员分组标识
        if (saveDto.getDeviceIds() != null && saveDto.getDeviceIds().size() > 0) {
            deviceGroupId = orderProcessDeviceRelService.createdDeviceGroupId(saveDto.getOrderProcessId(), saveDto.getDeviceIds());
        }
        if (saveDto.getDevicePersonIds() != null && saveDto.getDevicePersonIds().size() > 0) {
            devicePersonGroupId = orderProcessPersonRelService.createdPersonGroupId(saveDto.getOrderProcessId(), saveDto.getDevicePersonIds());
        }
        maps = orderProcessRecordRepository.getBGRecord(saveDto.getOrderProcessId(), deviceGroupId, devicePersonGroupId, saveDto.getRecordTypeBg(), saveDto.getRecordUnit());
//        maps = orderProcessRecordRepository.getBGRecord(saveDto.getOrderProcessId(), deviceGroupId, devicePersonGroupId);

        String mapStr = JSON.toJSONString(maps);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        Date dateStartTime = null;
        if (!StringUtils.isEmpty(saveDto.getIotCollectionLastTime())) {
            date = sdf.parse(saveDto.getIotCollectionLastTime());
        }
        if (!StringUtils.isEmpty(saveDto.getIotCollectionStartTime())) {
            dateStartTime = sdf.parse(saveDto.getIotCollectionStartTime());
        }
        List<OrderProcessRecordVo> recordVos = JSON.parseArray(mapStr, OrderProcessRecordVo.class);
        TBusOrderProcessRecord record = new TBusOrderProcessRecord();
        BeanUtils.copyProperties(saveDto, record);
        record.setReportTime(new Date());
        record.setClassId(tSysClass);
        record.setPersonId(personnelInfo);
        record.setProcessId(processInfo);
        record.setProcessName(processInfo.getProcessName());
        record.setProcessNumber(processInfo.getProcessNumber());
        //机排手，机台号(多选)
        record.setDeviceGroupId(deviceGroupId == null ? "" : deviceGroupId);
        record.setDevicePersonGroupId(devicePersonGroupId == null ? "" : devicePersonGroupId);
        for (OrderProcessRecordVo recordVo : recordVos) {
            if (recordVo.getRecordType() != null && !recordVo.getRecordType().equals(saveDto.getRecordType())) {
                // 报工类型不同
                continue;
            } else
            if ((recordVo.getMaterialId() != null && recordVo.getMaterialId().equals(saveDto.getMaterialId())
                    || recordVo.getMaterialNumber() != null && recordVo.getMaterialNumber().equals(saveDto.getMaterialNumber())
                    || recordVo.getMaterialName() != null && recordVo.getMaterialName().equals(saveDto.getMaterialName())
                    || recordVo.getLsmMaterialId() != null && recordVo.getLsmMaterialId().equals(saveDto.getLsmMaterialId()))
            ) {
                if (!saveDto.getRecordUnit().equals(recordVo.getRecordUnit())) {
                    throw new RuntimeException("提交的单位与历史报工的单位不一致！");
                }
                record.setImportPot(recordVo.getImportPot());
                record.setExportPot(recordVo.getExportPot());
                record.setExportPotMin(recordVo.getExportPotMin());
                record.setRecordQty(BigDecimalUtil.add(recordVo.getRecordQty() == null ? 0 : recordVo.getRecordQty(), saveDto.getRecordQty()).floatValue());
                record.setRecordManualQty(BigDecimalUtil.add(recordVo.getRecordManualQty() == null ? 0 : recordVo.getRecordManualQty(), saveDto.getRecordManualQty()).floatValue());
                record.setOrderProcessRecordId(recordVo.getOrderProcessRecordId());
                break;
            } else {
                record.setRecordQty(saveDto.getRecordQty());
                record.setRecordManualQty(saveDto.getRecordManualQty());
            }
        }
        if ("3".equals(saveDto.getRecordType()) && "1".equals(submitEnabled)) {
            float exportPotByPerson = orderPPBomRepository.findExportPotOne(record.getOrderProcessId(), "-1", "-1");
            /*if (LichengConstants.PROCESS_NUMBER_ZHANBAN.equals(saveDto.getProcessNumber())) {
                // 斩拌工序 验证
                //积累投入锅数
                int importPot = orderPPBomRepository.findExportPotByImportAll(tBusOrderHead.getOrderId(), LichengConstants.PROCESS_NUMBER_ZHANBAN, "-1", "-1");
                if (exportPotByPerson >= importPot) {
                    throw new RuntimeException("产出锅数大于等于投入锅数（产出锅数" + exportPotByPerson + "锅，投入锅数" + importPot + "锅）");
                }
            }*/
            float exportPot = BigDecimalUtil.add(record.getExportPot() == null ? 0 : record.getExportPot(), 0.5F).floatValue();
            record.setExportPot(exportPot);
            record.setExportPotMin(BigDecimalUtil.add(record.getExportPotMin() == null ? 0 : record.getExportPotMin(), 1F).floatValue());
        }
        record.setRecordUnit(saveDto.getRecordUnit());
        record.setBusType(LichengConstants.ORDER_BUS_TYPE_BG);
        record.setIotCollectionStartTime(dateStartTime);
        record.setIotCollectionLastTime(date);
        orderProcessRecordRepository.saveAndFlush(record);

        TBusOrderProcessHistory history = new TBusOrderProcessHistory();
        BeanUtils.copyProperties(saveDto, history);
        history.setOrderProcessRecordId(record.getOrderProcessRecordId());//记录报工结果表id
        history.setClassId(tSysClass);
        history.setAllImportPot(currentPot1);
        history.setPersonId(personnelInfo);
        history.setProcessId(processInfo);
        history.setProcessName(processInfo.getProcessName());
        history.setProcessNumber(processInfo.getProcessNumber());
        //机排手，机台号(多选)
        history.setDeviceGroupId(deviceGroupId == null ? "" : deviceGroupId);
        history.setDevicePersonGroupId(devicePersonGroupId == null ? "" : devicePersonGroupId);
        history.setReportTime(new Date());
        history.setBusType(LichengConstants.ORDER_BUS_TYPE_BG);
        history.setReportStatus(LichengConstants.ORDER_PROCESS_HISTORY_STATUS_0);//正常状态
        history.setIotCollectionStartTime(dateStartTime);
        history.setIotCollectionLastTime(date);
        history.setIotMath(saveDto.getIotMath());
        history.setIotQty(saveDto.getIotQty());
        history.setExportPot(record.getExportPot());
        history.setExportPotMin(record.getExportPotMin());
//        history.setRemark(saveDto.getRemark());
        //产后报工生成下道工序的记录
//        // 原设计：合格品产出报工后，会自动判断生成工艺路线的下道工序，订单工序执行表记录，用于接单开发。26644 增加判断：工序加按钮：开工控制（勾选后本工序合格品报工后，下道工序才允许接单）
//        if (!StringUtils.isEmpty(saveDto.getRecordType()) && saveDto.getRecordType().equals("3")) {
//            if (processInfo.getBySetStartTask().equals("1")) {
//                orderProcessRecordService.createNextOrderProcess(saveDto.getOrderProcessId(), tBusOrderHead);
//            }
//        }
        // 获取设备采集时间：拉伸膜工序并且产后报工,合格品产出及AB料产出
        if (LichengConstants.PROCESS_NUMBER_LASHENMO.equals(saveDto.getProcessNumber())
                && (saveDto.getRecordType().equals("3"))
                && saveDto.getRecordQty() > 0) {
            List<Integer> deviceIds = saveDto.getDeviceIds();
            TSysDeviceIot tSysDeviceIot;
            for (Integer deviceId : deviceIds) {
                tSysDeviceIot = tSysDeviceIotRepository.findByDeviceIdAndRecordType(deviceId, saveDto.getRecordType());
                if (tSysDeviceIot == null) {
                    throw new RuntimeException("设备id：" + deviceId + "  还没初始化，请初始化后尝试");
                }
                history.setIotCollectionStartTime(tSysDeviceIot.getIotTime());
                tSysDeviceIot.setIotTime(date);
                tSysDeviceIotRepository.saveAndFlush(tSysDeviceIot);

            }
        }
        orderProcessHistoryRepository.saveAndFlush(history);

        //12954 【app端】报工模块-拉伸膜工序，产出合格品按比例，自动投入下道优化
        //本道产后报工后，自动生成下道工序的报工一级类目为“投入前道数量”的记录
//        if (LichengConstants.ORDER_RECORD_TYPE_3.equals(saveDto.getRecordType()) && LichengConstants.PROCESS_NUMBER_LASHENMO.equals(saveDto.getProcessNumber())) {
//            if (null != record.getCapacityQty() && record.getCapacityQty() > 0) {
//                //拉伸膜工序，当产能数量大于0时，下道包装用产能数量自动报工
//                saveDto.setRecordQty(record.getCapacityQty());
//                saveDto.setRecordUnit(record.getCapacityUnit());
//            }
//            //12954 【app端】报工模块-拉伸膜工序，产出合格品按比例，自动投入下道优化
//            autoSubmitNextProcess(saveDto, userId, tBusOrderHead);
//        }
        return history.getOrderProcessHistoryId();
    }

    // 报工提交：自定义报工
    public Integer submitOrderType4(OrderRecordSaveDto saveDto, String userId) throws ParseException {
        TBusOrderProcess tBusOrderProcess = orderProcessRepository.findById(saveDto.getOrderProcessId()).orElse(null);
        TSysProcessInfo processInfo = tBusOrderProcess.getProcessId();
        TSysClass tSysClass = tBusOrderProcess.getClassId();
        TSysPersonnelInfo personnelInfo = tSysPersonnelInfoRepository.findByUserId(userId);
        List<TBusOrderHead> heads = orderHeadRepository.findByOrderNo(saveDto.getOrderNo());
        TBusOrderHead tBusOrderHead = heads.get(0);
        if (LichengConstants.ORDERSTATUS_4.equals(tBusOrderHead.getOrderStatus())) {
            throw new RuntimeException("订单已挂起，禁止操作。");
        }
        //获取当前锅数
        int currentPot1 = 0;
        //根据工序执行表ID，多选机台号id，多选操作员ID，获取报工结果表的记录
        List<Map> maps = null;
        String deviceGroupId = "";//多选机台号分组标识ID
        String devicePersonGroupId = "";//多选操作员分组标识ID
        //校验机台号是否已经被报工过，校验维度：工序执行表ID+机台号分组标识+操作员分组标识
        if (saveDto.getDeviceIds() != null && saveDto.getDeviceIds().size() > 0) {
            deviceGroupId = orderProcessDeviceRelService.createdDeviceGroupId(saveDto.getOrderProcessId(), saveDto.getDeviceIds());
        }
        if (saveDto.getDevicePersonIds() != null && saveDto.getDevicePersonIds().size() > 0) {
            devicePersonGroupId = orderProcessPersonRelService.createdPersonGroupId(saveDto.getOrderProcessId(), saveDto.getDevicePersonIds());
        }
        maps = orderProcessRecordRepository.getBGRecord(saveDto.getOrderProcessId(), deviceGroupId, devicePersonGroupId, saveDto.getRecordTypeBg(), saveDto.getRecordUnit());
//        maps = orderProcessRecordRepository.getBGRecord(saveDto.getOrderProcessId(), deviceGroupId, devicePersonGroupId);

        String mapStr = JSON.toJSONString(maps);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        Date dateStartTime = null;
        if (!StringUtils.isEmpty(saveDto.getIotCollectionLastTime())) {
            date = sdf.parse(saveDto.getIotCollectionLastTime());
        }
        if (!StringUtils.isEmpty(saveDto.getIotCollectionStartTime())) {
            dateStartTime = sdf.parse(saveDto.getIotCollectionStartTime());
        }
        List<OrderProcessRecordVo> recordVos = JSON.parseArray(mapStr, OrderProcessRecordVo.class);
        TBusOrderProcessRecord record = new TBusOrderProcessRecord();
        BeanUtils.copyProperties(saveDto, record);
        record.setReportTime(new Date());
        record.setClassId(tSysClass);
        record.setPersonId(personnelInfo);
        record.setProcessId(processInfo);
        record.setProcessName(processInfo.getProcessName());
        record.setProcessNumber(processInfo.getProcessNumber());
        //机排手，机台号(多选)
        record.setDeviceGroupId(deviceGroupId == null ? "" : deviceGroupId);
        record.setDevicePersonGroupId(devicePersonGroupId == null ? "" : devicePersonGroupId);
        for (OrderProcessRecordVo recordVo : recordVos) {
            if (recordVo.getRecordType() != null && !recordVo.getRecordType().equals(saveDto.getRecordType())) {
                // 报工类型不同
                continue;
            } else
            if (recordVo.getMaterialId() != null && recordVo.getMaterialId().equals(saveDto.getMaterialId())
                    || recordVo.getMaterialNumber() != null && recordVo.getMaterialNumber().equals(saveDto.getMaterialNumber())
                    || recordVo.getMaterialName() != null && recordVo.getMaterialName().equals(saveDto.getMaterialName())
                    || recordVo.getLsmMaterialId() != null && recordVo.getLsmMaterialId().equals(saveDto.getLsmMaterialId())) {
                //TODO 自定义报工的单位判断是否也需要判断
                if ("3".equals(saveDto.getRecordType()) && !saveDto.getRecordUnit().equals(recordVo.getRecordUnit())) {
                    throw new RuntimeException("提交的单位与历史报工的单位不一致！");
                }
                record.setImportPot(recordVo.getImportPot());
                record.setExportPot(recordVo.getExportPot());
                record.setExportPotMin(recordVo.getExportPotMin());
                record.setRecordQty(BigDecimalUtil.add(recordVo.getRecordQty() == null ? 0 : recordVo.getRecordQty(), saveDto.getRecordQty()).floatValue());
                record.setRecordManualQty(BigDecimalUtil.add(recordVo.getRecordManualQty() == null ? 0 : recordVo.getRecordManualQty(), saveDto.getRecordManualQty()).floatValue());
                record.setOrderProcessRecordId(recordVo.getOrderProcessRecordId());
                break;
            } else {
                record.setRecordQty(saveDto.getRecordQty());
                record.setRecordManualQty(saveDto.getRecordManualQty());
            }
        }
        record.setRecordUnit(saveDto.getRecordUnit());
        record.setBusType(LichengConstants.ORDER_BUS_TYPE_BG);
        record.setIotCollectionStartTime(dateStartTime);
        record.setIotCollectionLastTime(date);
        orderProcessRecordRepository.saveAndFlush(record);

        TBusOrderProcessHistory history = new TBusOrderProcessHistory();
        BeanUtils.copyProperties(saveDto, history);
        history.setOrderProcessRecordId(record.getOrderProcessRecordId());//记录报工结果表id
        history.setClassId(tSysClass);
        history.setAllImportPot(currentPot1);
        history.setPersonId(personnelInfo);
        history.setProcessId(processInfo);
        history.setProcessName(processInfo.getProcessName());
        history.setProcessNumber(processInfo.getProcessNumber());
        //机排手，机台号(多选)
        history.setDeviceGroupId(deviceGroupId == null ? "" : deviceGroupId);
        history.setDevicePersonGroupId(devicePersonGroupId == null ? "" : devicePersonGroupId);
        history.setReportTime(new Date());
        history.setBusType(LichengConstants.ORDER_BUS_TYPE_BG);
        history.setReportStatus(LichengConstants.ORDER_PROCESS_HISTORY_STATUS_0);//正常状态
        history.setIotCollectionStartTime(dateStartTime);
        history.setIotCollectionLastTime(date);
        history.setIotMath(saveDto.getIotMath());
        history.setIotQty(saveDto.getIotQty());
        history.setExportPot(record.getExportPot());
        history.setExportPotMin(record.getExportPotMin());
        orderProcessHistoryRepository.saveAndFlush(history);

        return history.getOrderProcessHistoryId();
    }

    //前道自动投入报工
    public Integer submitOrderType5(OrderRecordSaveDto saveDto, String userId) throws ParseException {
        TBusOrderProcess tBusOrderProcess = orderProcessRepository.findById(saveDto.getOrderProcessId()).orElse(null);
        TSysProcessInfo processInfo = tBusOrderProcess.getProcessId();
        TSysClass tSysClass = tBusOrderProcess.getClassId();
        TSysPersonnelInfo personnelInfo = tSysPersonnelInfoRepository.findByUserId(userId);
        List<TBusOrderHead> heads = orderHeadRepository.findByOrderNo(saveDto.getOrderNo());
        TBusOrderHead tBusOrderHead = heads.get(0);
        if (LichengConstants.ORDERSTATUS_4.equals(tBusOrderHead.getOrderStatus())) {
            throw new RuntimeException("订单已挂起，禁止操作。");
        }
        //获取当前锅数
        int currentPot1 = 0;
        //根据工序执行表ID，多选机台号id，多选操作员ID，获取报工结果表的记录
        List<Map> maps = null;
        String deviceGroupId = "";//多选机台号分组标识ID
        String devicePersonGroupId = "";//多选操作员分组标识ID
        //校验机台号是否已经被报工过，校验维度：工序执行表ID+机台号分组标识+操作员分组标识
        if (saveDto.getDeviceIds() != null && saveDto.getDeviceIds().size() > 0) {
            deviceGroupId = orderProcessDeviceRelService.createdDeviceGroupId(saveDto.getOrderProcessId(), saveDto.getDeviceIds());
        }
        if (saveDto.getDevicePersonIds() != null && saveDto.getDevicePersonIds().size() > 0) {
            devicePersonGroupId = orderProcessPersonRelService.createdPersonGroupId(saveDto.getOrderProcessId(), saveDto.getDevicePersonIds());
        }
        maps = orderProcessRecordRepository.getBGRecord(saveDto.getOrderProcessId(), deviceGroupId, devicePersonGroupId, saveDto.getRecordTypeBg(), saveDto.getRecordUnit());
//        maps = orderProcessRecordRepository.getBGRecord(saveDto.getOrderProcessId(), deviceGroupId, devicePersonGroupId);

        String mapStr = JSON.toJSONString(maps);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        Date dateStartTime = null;
        if (!StringUtils.isEmpty(saveDto.getIotCollectionLastTime())) {
            date = sdf.parse(saveDto.getIotCollectionLastTime());
        }
        if (!StringUtils.isEmpty(saveDto.getIotCollectionStartTime())) {
            dateStartTime = sdf.parse(saveDto.getIotCollectionStartTime());
        }
        List<OrderProcessRecordVo> recordVos = JSON.parseArray(mapStr, OrderProcessRecordVo.class);
        TBusOrderProcessRecord record = new TBusOrderProcessRecord();
        BeanUtils.copyProperties(saveDto, record);
        record.setReportTime(new Date());
        record.setClassId(tSysClass);
        record.setPersonId(personnelInfo);
        record.setProcessId(processInfo);
        record.setProcessName(processInfo.getProcessName());
        record.setProcessNumber(processInfo.getProcessNumber());
        //机排手，机台号(多选)
        record.setDeviceGroupId(deviceGroupId == null ? "" : deviceGroupId);
        record.setDevicePersonGroupId(devicePersonGroupId == null ? "" : devicePersonGroupId);
        for (OrderProcessRecordVo recordVo : recordVos) {
            if (recordVo.getRecordType() != null && !recordVo.getRecordType().equals(saveDto.getRecordType())) {
                // 报工类型不同
                continue;
            } else
            if (recordVo.getMaterialId() != null && recordVo.getMaterialId().equals(saveDto.getMaterialId())
                    || recordVo.getMaterialNumber() != null && recordVo.getMaterialNumber().equals(saveDto.getMaterialNumber())
                    || recordVo.getMaterialName() != null && recordVo.getMaterialName().equals(saveDto.getMaterialName())
                    || recordVo.getLsmMaterialId() != null && recordVo.getLsmMaterialId().equals(saveDto.getLsmMaterialId())) {
                if ("3".equals(saveDto.getRecordType()) && !saveDto.getRecordUnit().equals(recordVo.getRecordUnit())) {
                    throw new RuntimeException("提交的单位与历史报工的单位不一致！");
                }
                record.setImportPot(recordVo.getImportPot());
                record.setExportPot(recordVo.getExportPot());
                record.setExportPotMin(recordVo.getExportPotMin());
                record.setRecordQty(BigDecimalUtil.add(recordVo.getRecordQty() == null ? 0 : recordVo.getRecordQty(), saveDto.getRecordQty()).floatValue());
                record.setRecordManualQty(BigDecimalUtil.add(recordVo.getRecordManualQty() == null ? 0 : recordVo.getRecordManualQty(), saveDto.getRecordManualQty()).floatValue());
                record.setOrderProcessRecordId(recordVo.getOrderProcessRecordId());
                break;
            } else {
                record.setRecordQty(saveDto.getRecordQty());
                record.setRecordManualQty(saveDto.getRecordManualQty());
            }
        }
        record.setRecordUnit(saveDto.getRecordUnit());
        record.setBusType(LichengConstants.ORDER_BUS_TYPE_BG);
        record.setIotCollectionStartTime(dateStartTime);
        record.setIotCollectionLastTime(date);
        orderProcessRecordRepository.saveAndFlush(record);

        TBusOrderProcessHistory history = new TBusOrderProcessHistory();
        BeanUtils.copyProperties(saveDto, history);
        history.setOrderProcessRecordId(record.getOrderProcessRecordId());//记录报工结果表id
        history.setClassId(tSysClass);
        history.setAllImportPot(currentPot1);
        history.setPersonId(personnelInfo);
        history.setProcessId(processInfo);
        history.setProcessName(processInfo.getProcessName());
        history.setProcessNumber(processInfo.getProcessNumber());
        //机排手，机台号(多选)
        history.setDeviceGroupId(deviceGroupId == null ? "" : deviceGroupId);
        history.setDevicePersonGroupId(devicePersonGroupId == null ? "" : devicePersonGroupId);
        history.setReportTime(new Date());
        history.setBusType(LichengConstants.ORDER_BUS_TYPE_BG);
        history.setReportStatus(LichengConstants.ORDER_PROCESS_HISTORY_STATUS_0);//正常状态
        history.setIotCollectionStartTime(dateStartTime);
        history.setIotCollectionLastTime(date);
        history.setIotMath(saveDto.getIotMath());
        history.setIotQty(saveDto.getIotQty());
        history.setExportPot(record.getExportPot());
        history.setExportPotMin(record.getExportPotMin());
//        //产后报工生成下道工序的记录
//        // 原设计：合格品产出报工后，会自动判断生成工艺路线的下道工序，订单工序执行表记录，用于接单开发。26644 增加判断：工序加按钮：开工控制（勾选后本工序合格品报工后，下道工序才允许接单）
//        if (!StringUtils.isEmpty(saveDto.getRecordType()) && saveDto.getRecordType().equals("3")) {
//            if (processInfo.getBySetStartTask().equals("1")) {
//                orderProcessRecordService.createNextOrderProcess(saveDto.getOrderProcessId(), tBusOrderHead);
//            }
//        }
        orderProcessHistoryRepository.saveAndFlush(history);

        return history.getOrderProcessHistoryId();
    }
    // 报工提交：AB料投入报工
    public Integer submitOrderType6(OrderRecordSaveDto saveDto, String userId) throws ParseException {
        TBusOrderProcess tBusOrderProcess = orderProcessRepository.findById(saveDto.getOrderProcessId()).orElse(null);
        TSysProcessInfo processInfo = tBusOrderProcess.getProcessId();
        TSysClass tSysClass = tBusOrderProcess.getClassId();
        TSysPersonnelInfo personnelInfo = tSysPersonnelInfoRepository.findByUserId(userId);
        List<TBusOrderHead> heads = orderHeadRepository.findByOrderNo(saveDto.getOrderNo());
        TBusOrderHead tBusOrderHead = heads.get(0);
        if (LichengConstants.ORDERSTATUS_4.equals(tBusOrderHead.getOrderStatus())) {
            throw new RuntimeException("订单已挂起，禁止操作。");
        }
        //获取当前锅数
        int currentPot1 = 0;
        //根据工序执行表ID，多选机台号id，多选操作员ID，获取报工结果表的记录
        List<Map> maps = null;
        String deviceGroupId = "";//多选机台号分组标识ID
        String devicePersonGroupId = "";//多选操作员分组标识ID
        //校验机台号是否已经被报工过，校验维度：工序执行表ID+机台号分组标识+操作员分组标识
        if (saveDto.getDeviceIds() != null && saveDto.getDeviceIds().size() > 0) {
            deviceGroupId = orderProcessDeviceRelService.createdDeviceGroupId(saveDto.getOrderProcessId(), saveDto.getDeviceIds());
        }
        if (saveDto.getDevicePersonIds() != null && saveDto.getDevicePersonIds().size() > 0) {
            devicePersonGroupId = orderProcessPersonRelService.createdPersonGroupId(saveDto.getOrderProcessId(), saveDto.getDevicePersonIds());
        }
        maps = orderProcessRecordRepository.getBGRecord(saveDto.getOrderProcessId(), deviceGroupId, devicePersonGroupId, saveDto.getRecordTypeBg(), saveDto.getRecordUnit());
//        maps = orderProcessRecordRepository.getBGRecord(saveDto.getOrderProcessId(), deviceGroupId, devicePersonGroupId);

        String mapStr = JSON.toJSONString(maps);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        Date dateStartTime = null;
        if (!StringUtils.isEmpty(saveDto.getIotCollectionLastTime())) {
            date = sdf.parse(saveDto.getIotCollectionLastTime());
        }
        if (!StringUtils.isEmpty(saveDto.getIotCollectionStartTime())) {
            dateStartTime = sdf.parse(saveDto.getIotCollectionStartTime());
        }
        List<OrderProcessRecordVo> recordVos = JSON.parseArray(mapStr, OrderProcessRecordVo.class);
        TBusOrderProcessRecord record = new TBusOrderProcessRecord();
        BeanUtils.copyProperties(saveDto, record);
        record.setReportTime(new Date());
        record.setClassId(tSysClass);
        record.setPersonId(personnelInfo);
        record.setProcessId(processInfo);
        record.setProcessName(processInfo.getProcessName());
        record.setProcessNumber(processInfo.getProcessNumber());
        //机排手，机台号(多选)
        record.setDeviceGroupId(deviceGroupId == null ? "" : deviceGroupId);
        record.setDevicePersonGroupId(devicePersonGroupId == null ? "" : devicePersonGroupId);
        for (OrderProcessRecordVo recordVo : recordVos) {
            if (recordVo.getRecordType() != null && !recordVo.getRecordType().equals(saveDto.getRecordType())) {
                // 报工类型不同
                continue;
            } else
            if (recordVo.getMaterialId() != null && recordVo.getMaterialId().equals(saveDto.getMaterialId())
                    || recordVo.getMaterialNumber() != null && recordVo.getMaterialNumber().equals(saveDto.getMaterialNumber())
                    || recordVo.getMaterialName() != null && recordVo.getMaterialName().equals(saveDto.getMaterialName())
                    || recordVo.getLsmMaterialId() != null && recordVo.getLsmMaterialId().equals(saveDto.getLsmMaterialId())) {
                if (!saveDto.getRecordUnit().equals(recordVo.getRecordUnit())) {
                    throw new RuntimeException("提交的单位与历史报工的单位不一致！");
                }
                record.setImportPot(recordVo.getImportPot());
                record.setExportPot(recordVo.getExportPot());
                record.setExportPotMin(recordVo.getExportPotMin());
                record.setRecordQty(BigDecimalUtil.add(recordVo.getRecordQty() == null ? 0 : recordVo.getRecordQty(), saveDto.getRecordQty()).floatValue());
                record.setRecordManualQty(BigDecimalUtil.add(recordVo.getRecordManualQty() == null ? 0 : recordVo.getRecordManualQty(), saveDto.getRecordManualQty()).floatValue());
                record.setOrderProcessRecordId(recordVo.getOrderProcessRecordId());
                break;
            } else {
                record.setRecordQty(saveDto.getRecordQty());
                record.setRecordManualQty(saveDto.getRecordManualQty());
            }
        }
        record.setRecordUnit(saveDto.getRecordUnit());
        record.setBusType(LichengConstants.ORDER_BUS_TYPE_BG);
        record.setIotCollectionStartTime(dateStartTime);
        record.setIotCollectionLastTime(date);
        orderProcessRecordRepository.saveAndFlush(record);

        TBusOrderProcessHistory history = new TBusOrderProcessHistory();
        BeanUtils.copyProperties(saveDto, history);
        history.setOrderProcessRecordId(record.getOrderProcessRecordId());//记录报工结果表id
        history.setClassId(tSysClass);
        history.setAllImportPot(currentPot1);
        history.setPersonId(personnelInfo);
        history.setProcessId(processInfo);
        history.setProcessName(processInfo.getProcessName());
        history.setProcessNumber(processInfo.getProcessNumber());
        //机排手，机台号(多选)
        history.setDeviceGroupId(deviceGroupId == null ? "" : deviceGroupId);
        history.setDevicePersonGroupId(devicePersonGroupId == null ? "" : devicePersonGroupId);
        history.setReportTime(new Date());
        history.setBusType(LichengConstants.ORDER_BUS_TYPE_BG);
        history.setReportStatus(LichengConstants.ORDER_PROCESS_HISTORY_STATUS_0);//正常状态
        history.setIotCollectionStartTime(dateStartTime);
        history.setIotCollectionLastTime(date);
        history.setIotMath(saveDto.getIotMath());
        history.setIotQty(saveDto.getIotQty());
        history.setExportPot(record.getExportPot());
        history.setExportPotMin(record.getExportPotMin());
        orderProcessHistoryRepository.saveAndFlush(history);

        return history.getOrderProcessHistoryId();
    }

    /**
     * 批量报工提交
     *
     * @param saveListDto
     * @param userId
     * @throws ParseException
     */
    @Transactional
    @Override
    public void mulSubmit(OrderRecordSaveListDto saveListDto, String userId) throws ParseException {
        for (int i = 0; i < saveListDto.getSaveDtoList().size(); i++) {
            OrderRecordSaveDto saveDto = saveListDto.getSaveDtoList().get(i);
            this.submit(saveDto, userId);
        }
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

    /**
     * 12954 【app端】报工模块-拉伸膜工序，产出合格品按比例，自动投入下道优化
     * 本道产后报工后，自动生成下道工序的报工一级类目为“投入前道数量”的记录
     *
     * @param saveDto
     * @param userId
     * @param tBusOrderHead
     * @throws ParseException
     */
    private void autoSubmitNextProcess(OrderRecordSaveDto saveDto, String userId, TBusOrderHead tBusOrderHead) throws ParseException {
        if (!LichengConstants.ORDER_RECORD_TYPE_3.equals(saveDto.getRecordType())) {
            return;
        }
        if (!LichengConstants.PROCESS_NUMBER_LASHENMO.equals(saveDto.getProcessNumber())) {
            return;
        }
        //拉伸膜工序，进行合格品报工，提交报工后，根据报工数量按照一定比例视为合格品，并且自动投入包装工序
        List<TSysCodeDsc> tSysCodeDscAParams = tSysCodeDscRepository.findByCodeClIdAndCodeDsc("OTHER0000", "A参数");
        List<TSysCodeDsc> tSysCodeDscAParamEnableds = tSysCodeDscRepository.findByCodeClIdAndCodeDsc("OTHER0000", "A参数是否启用");
        var aParamEnabled = tSysCodeDscAParamEnableds.stream().findFirst().orElse(null);
        if (tSysCodeDscAParams.size() > 0 && aParamEnabled.getCodeValue().equals(GlobalConstant.enableTrue)
                && LichengConstants.PROCESS_NUMBER_LASHENMO.equals(saveDto.getProcessNumber())) {
            var tSysCodeDsc = tSysCodeDscAParams.stream().findFirst().orElse(null);
            float aParam = 0f;
            try {
                aParam = Float.parseFloat(tSysCodeDsc.getCodeValue());
            } catch (NumberFormatException e) {
                throw new RuntimeException("获取转换A参数发生异常");
            }
            float recordQtyBz = BigDecimalUtil.format(saveDto.getRecordQty() * aParam, 0);//取整 2022-07-23
            //判断下道工序为包装
            TSysProcessInfo processInfoBz2 = craftInfoService.getCraftNextProcessInfo(tBusOrderHead.getCraftId().getCraftId(), saveDto.getProcessId());
            if (processInfoBz2 == null || processInfoBz2.getProcessNumber().equals(LichengConstants.PROCESS_NUMBER_BAOZHUANG)) {
                throw new RuntimeException("不允许报工，工艺路线的下道工序不存在或者不为包装");
            }
            Set<TBusOrderProcess> orderProcessSet = tBusOrderHead.getTBusOrderProcessSet();
            //检查下道工序包装的工序执行表记录是否存在
            TBusOrderProcess tBusOrderProcessBZ = orderProcessSet.stream().filter(s -> s.getProcessId().getProcessNumber().equals(LichengConstants.PROCESS_NUMBER_BAOZHUANG)).findFirst().orElse(null);
            if (tBusOrderProcessBZ == null) {
                throw new RuntimeException("不允许报工，请在本工序报工后，再进行下道包装工序先接单开工");
            }
            for (var orderProcess : orderProcessSet) {
                var processInfoBz = orderProcess.getProcessId();
                if (processInfoBz != null && !processInfoBz.getProcessNumber().equals(LichengConstants.PROCESS_NUMBER_BAOZHUANG)) {
                    continue;
                }
                if (processInfoBz.getProcessId().intValue() == processInfoBz.getProcessId().intValue()) {
                    //判断是否为移交订单，并且移交的订单工序ID为空，则为移交前的记录，直接跳过
                    if (orderProcess.getType().equals("2") && (orderProcess.getOldOrderProcessId() == null || orderProcess.getOldOrderProcessId() == 0)) {
                        continue;
                    }
                    orderProcessRepository.saveAndFlush(orderProcess);
                    //1.下道未接单（下道工序状态未开工、处理人为空）,不允许报工，请下道包装工序先接单开工。---取消，继续执行报工，然后让包装的班别去接单
                    //2.下道工序暂停（下道工序状态暂停）,不允许报工，下道包装工序已暂停。
                    //3.下道已完工（下道工序状态已完工）,不允许报工，下道包装工序已完工。
//                    if (orderProcess.getProcessStatus().equals(LichengConstants.PROCESSSTATUS_0)) {
//                        throw new RuntimeException("不允许报工，请下道包装工序先接单开工");
//                    } else
                    if (orderProcess.getProcessStatus().equals(LichengConstants.PROCESSSTATUS_2)) {
                        throw new RuntimeException("不允许报工，下道包装工序已暂停。");
                    } else if (orderProcess.getProcessStatus().equals(LichengConstants.PROCESSSTATUS_3)) {
                        throw new RuntimeException("不允许报工，下道包装工序已完工。");
                    }
                    OrderRecordSaveDto saveDtoBg = new OrderRecordSaveDto();
                    BeanUtils.copyProperties(saveDto, saveDtoBg);//后续取消copy
                    saveDtoBg.setOrderNo(saveDto.getOrderNo());
                    saveDtoBg.setBodyLot(saveDto.getBodyLot());
                    saveDtoBg.setOrderProcessId(orderProcess.getOrderProcessId());
                    saveDtoBg.setOrderPPBomId(null);
                    saveDtoBg.setRecordQty(recordQtyBz);//计算公式：包装数量=拉伸膜*A参数
                    saveDtoBg.setRecordUnit(saveDto.getRecordUnit());
                    saveDtoBg.setProcessId(processInfoBz.getProcessId());
                    saveDtoBg.setProcessNumber(processInfoBz.getProcessNumber());
                    saveDtoBg.setProcessName(processInfoBz.getProcessName());
                    saveDtoBg.setMaterialName(LichengConstants.ORDER_REPORT_MATERIAL_NAME_5);
                    //机台号、机排手放空
                    saveDtoBg.setDeviceId(null);
                    saveDtoBg.setDevicePersonId(null);
                    saveDtoBg.setProcessName(processInfoBz.getProcessName());
                    saveDtoBg.setRecordType(LichengConstants.ORDER_RECORD_TYPE_5);//投入前道数量
                    saveDtoBg.setRecordTypeBg(saveDto.getRecordTypeBg());
                    this.submit(saveDtoBg, userId);
                    break;
                }
            }
        }
    }




}
