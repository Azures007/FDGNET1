package org.thingsboard.server.dao.mes.ncInventory;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.mes.ncInventory.NcInventory;
import org.thingsboard.server.common.data.mes.ncWarehouse.NcWarehouse;
import org.thingsboard.server.common.data.mes.sys.NcSyncLog;
import org.thingsboard.server.dao.mes.dto.NcInventoryProductDto;
import org.thingsboard.server.dao.mes.dto.NcInventorySyncRequest;
import org.thingsboard.server.dao.sql.mes.ncInventory.NcInventoryRepository;
import org.thingsboard.server.dao.sql.mes.order.NcSyncLogRepository;
import org.thingsboard.server.dao.user.UserService;
import org.thingsboard.server.dao.mes.vo.PageVo;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NcInventoryServiceImpl implements NcInventoryService {
    @Autowired
    private NcInventoryRepository repository;

    @Autowired
    protected UserService userService;

    @Autowired
    private NcSyncLogRepository ncSyncLogRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private static final int BATCH_SIZE = 1000; // 每批处理数量

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateBatchByBillId(NcInventorySyncRequest request) {
        long startTime = System.currentTimeMillis();
        String syncType = "库存同步";
        String syncContent = "";
        String syncStatus = "0"; // 0：成功 1：失败
        String requestJson = "";
        String errorMessage = null;
        Integer dataCount = 0;
        List<NcInventory> inventoryList = new ArrayList<>();
        String warehouseId = "";
        String warehouseCode = "";

        try {
            if (request == null) {
                throw new IllegalArgumentException("请求参数不能为空");
            }

            // 保存原始JSON入参
            try {
                requestJson = JacksonUtil.toString(request);
            } catch (Exception e) {
                log.warn("转换原始JSON入参失败", e);
                requestJson = "转换JSON失败：" + e.getMessage();
            }

            warehouseId = StringUtils.defaultString(request.getWarehouseId(), "");
            warehouseCode = StringUtils.defaultString(request.getWarehouseCode(), "");
            String warehouseName = StringUtils.defaultString(request.getWarehouseName(), "");

            if (StringUtils.isBlank(warehouseId) && StringUtils.isBlank(warehouseCode)) {
                throw new IllegalArgumentException("仓库编码和仓库ID不能同时为空");
            }

            List<NcInventoryProductDto> products = request.getProduct();
            if (products != null && !products.isEmpty()) {
                for (NcInventoryProductDto product : products) {
                    NcInventory inventory = new NcInventory();
                    inventory.setWarehouseId(warehouseId);
                    inventory.setWarehouseCode(warehouseCode);
                    inventory.setWarehouseName(warehouseName);
                    inventory.setMaterialId(StringUtils.defaultString(product.getMaterialId(), ""));
                    inventory.setMaterialCode(StringUtils.defaultString(product.getMaterialCode(), ""));
                    inventory.setMaterialName(StringUtils.defaultString(product.getMaterialName(), ""));
                    inventory.setMaterialType(StringUtils.defaultString(product.getMaterialType(), ""));
                    inventory.setLot(StringUtils.defaultString(product.getLot(), ""));
                    inventory.setSpec(StringUtils.defaultString(product.getSpec(), ""));
                    inventory.setUnit(StringUtils.defaultString(product.getUnit(), ""));
                    inventory.setQty(product.getQty() == null ? 0F : product.getQty());
                    inventory.setStatus("生效");

                    String billId = inventory.getWarehouseId() + "_" + inventory.getMaterialId() + "_" + inventory.getLot();
                    inventory.setBillId(billId);
                    inventoryList.add(inventory);
                }
            }

            dataCount = inventoryList.size();
            log.info("开始NC库存同步，仓库：{}({})，总数量：{}", warehouseName, StringUtils.defaultIfBlank(warehouseId, warehouseCode), dataCount);

            // 1. 删除当前仓库历史记录
            log.info("开始删除仓库[{}]的历史库存记录...", StringUtils.defaultIfBlank(warehouseId, warehouseCode));
            repository.deleteByWarehouse(warehouseId, warehouseCode);
            log.info("删除仓库[{}]的历史库存记录完成", StringUtils.defaultIfBlank(warehouseId, warehouseCode));

            if (inventoryList.isEmpty()) {
                syncContent = "同步数据为空，仅执行了清空操作";
                log.info(syncContent);
                saveSyncLog(syncType, syncStatus, syncContent, requestJson, dataCount, System.currentTimeMillis() - startTime, null);
                return;
            }

            // 2. 分页批量插入（使用原生SQL提高性能）
            int batchCount = 0;
            int totalInserted = 0;
            for (int i = 0; i < inventoryList.size(); i += BATCH_SIZE) {
                int endIndex = Math.min(i + BATCH_SIZE, inventoryList.size());
                List<NcInventory> batch = inventoryList.subList(i, endIndex);
                batchInsert(batch);
                batchCount++;
                totalInserted += batch.size();
                log.info("第{}批插入完成，本批数量：{}，累计插入：{}/{}", batchCount, batch.size(), totalInserted, dataCount);
            }

            long duration = System.currentTimeMillis() - startTime;
            syncContent = String.format("同步成功，仓库：%s，总数量：%d，分%d批插入，耗时：%d ms",
                    StringUtils.defaultIfBlank(warehouseName, warehouseCode), dataCount, batchCount, duration);
            log.info("NC库存同步完成：{}", syncContent);
            saveSyncLog(syncType, syncStatus, syncContent, requestJson, dataCount, duration, null);

        } catch (Exception e) {
            syncStatus = "1";
            errorMessage = e.getMessage();
            syncContent = "同步失败：" + errorMessage;
            long duration = System.currentTimeMillis() - startTime;
            log.error("NC库存同步失败", e);
            saveSyncLog(syncType, syncStatus, syncContent, requestJson, dataCount, duration, errorMessage);
            throw new RuntimeException("NC库存同步失败：" + errorMessage, e);
        }
    }

    /**
     * 批量插入库存数据（使用原生SQL提高性能）
     */
    private void batchInsert(List<NcInventory> batch) {
        if (batch == null || batch.isEmpty()) {
            return;
        }

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("INSERT INTO t_bus_inventory(");
        sqlBuilder.append("bill_id, warehouse_id, warehouse_name, warehouse_code, ");
        sqlBuilder.append("material_id, material_code, material_name, material_type, ");
        sqlBuilder.append("lot, spec, unit, qty, status");
        sqlBuilder.append(") VALUES ");

        for (int i = 0; i < batch.size(); i++) {
            NcInventory inventory = batch.get(i);
            if (i > 0) {
                sqlBuilder.append(",");
            }
            sqlBuilder.append("(");
            sqlBuilder.append("'").append(escapeSql(inventory.getBillId())).append("',");
            sqlBuilder.append("'").append(escapeSql(inventory.getWarehouseId())).append("',");
            sqlBuilder.append("'").append(escapeSql(inventory.getWarehouseName())).append("',");
            sqlBuilder.append("'").append(escapeSql(inventory.getWarehouseCode())).append("',");
            sqlBuilder.append("'").append(escapeSql(inventory.getMaterialId())).append("',");
            sqlBuilder.append("'").append(escapeSql(inventory.getMaterialCode())).append("',");
            sqlBuilder.append("'").append(escapeSql(inventory.getMaterialName())).append("',");
            sqlBuilder.append("'").append(escapeSql(inventory.getMaterialType())).append("',");
            sqlBuilder.append("'").append(escapeSql(inventory.getLot())).append("',");
            sqlBuilder.append("'").append(escapeSql(inventory.getSpec())).append("',");
            sqlBuilder.append("'").append(escapeSql(inventory.getUnit())).append("',");
            sqlBuilder.append(inventory.getQty() != null ? inventory.getQty() : "0").append(",");
            sqlBuilder.append("'").append(escapeSql(inventory.getStatus())).append("'");
            sqlBuilder.append(")");
        }

        String sql = sqlBuilder.toString();
        Query query = entityManager.createNativeQuery(sql);
        int result = query.executeUpdate();
        log.debug("批量插入SQL执行结果：{}", result);
    }

    /**
     * SQL字符串转义，防止SQL注入
     */
    private String escapeSql(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("'", "''");
    }

    /**
     * 保存同步日志
     */
    private void saveSyncLog(String syncType, String syncStatus, String syncContent, 
                             String requestJson, Integer dataCount, Long durationMs, String errorMessage) {
        try {
            NcSyncLog ncSyncLog = new NcSyncLog();
            ncSyncLog.setSyncType(syncType);
            ncSyncLog.setSyncTime(new Date());
            ncSyncLog.setSyncStatus(syncStatus);
            ncSyncLog.setSyncContent(syncContent);
            ncSyncLog.setRequestJson(requestJson);
            ncSyncLog.setDataCount(dataCount);
            ncSyncLog.setDurationMs(durationMs);
            ncSyncLog.setErrorMessage(errorMessage);
            ncSyncLogRepository.save(ncSyncLog);
        } catch (Exception e) {
            log.error("保存NC同步日志失败", e);
        }
    }
    @Override
    public PageVo<NcInventory> queryInventory(String userId,String warehouseName, String materialName, String spec,Integer current, Integer size) {
        //List<String> cwkids =userService.getUserCurrentCwkid(userId);
        String pkOrg = userService.getUserCurrentPkOrg(userId);
        List<NcWarehouse> ncWarehouses = userService.findNcWarehouseByUserIdAndPkOrg(userId,pkOrg);
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.DESC, "billId"));
        Sort sort1 = Sort.by(orders);
        Pageable pageable = PageRequest.of(current, size, sort1);
        Page<NcInventory> inventoryPage = repository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<Predicate>();

            //仓库名称
            if (!StringUtils.isEmpty(warehouseName)) {
                predicates.add(criteriaBuilder.like(root.get("warehouseName"), "%" + warehouseName + "%"));
            }
            //物料名称
            if (!StringUtils.isEmpty(materialName)) {
                predicates.add(criteriaBuilder.like(root.get("materialName"), "%" + materialName + "%"));
            }
            //规格
            if (!StringUtils.isEmpty(spec)) {
                predicates.add(criteriaBuilder.like(root.get("spec"), "%" + spec + "%"));
            }
            if(ncWarehouses!=null && !ncWarehouses.isEmpty()){
                List<String> warehouseIds = ncWarehouses.stream().map(NcWarehouse::getPkStordoc).distinct().collect(Collectors.toList());
                predicates.add(root.get("warehouseId").in(warehouseIds));
            }
            return criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
        }, pageable);
        PageVo<NcInventory> pageVo = new PageVo<>(inventoryPage);
        return pageVo;
    }
}
