package org.thingsboard.server.dao.licheng;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.dao.constant.GlobalConstant;
import org.thingsboard.server.dao.sql.licheng.MidDeptRepository;
import org.thingsboard.server.dao.sql.licheng.MidMaterialRepository;
import org.thingsboard.server.dao.sql.licheng.MidOrgRepository;
import org.thingsboard.server.dao.sql.message.MessageOrderRepository;
import org.thingsboard.server.dao.sql.order.OrderHeadRepository;
import org.thingsboard.server.dao.sql.order.OrderPPBomRepository;
import org.thingsboard.server.dao.sql.order.SyncOrderLogRepository;
import org.thingsboard.server.dao.sql.order.TBusOrderUpdateRepository;
import org.thingsboard.server.dao.sql.tSysPersonnelInfo.TSysPersonnelInfoRepository;
import org.thingsboard.server.dao.vo.OrderStatusVo;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class LiChengServiceImpl implements LiChengService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    SyncOrderLogRepository syncOrderLogRepository;

    @Autowired
    OrderHeadRepository orderHeadRepository;

    @Autowired
    OrderPPBomRepository orderPPBomRepository;

    @Autowired
    TSysPersonnelInfoRepository tSysPersonnelInfoRepository;

    @Autowired
    LichengDataSouce lichengDataSouce;

    @Autowired
    TBusOrderUpdateRepository orderUpdateRepository;

    @Autowired
    MidMaterialRepository midMaterialRepository;

    @Autowired
    MessageOrderRepository messageOrderRepository;

    @Autowired
    MidOrgRepository midOrgRepository;

    @Value("${material.sync.size:1000}")
    private Integer materialSyncHeaderSize;

    @Autowired
    MidDeptRepository midDeptRepository;


    @Transactional
    @Override
    public void sync(Integer orderId) {
        log.info("----订单同步开始：" + orderId + "----");
        Boolean flag = SyncUpdate(orderId);
        if (!flag) {
            log.info("----当前需求为订单表更------------");
            syncOrderUpdate(orderId);
            log.info("----订单同步结束-\"+orderId+\"---");
            return;
        }
        log.info("----当前需求为订单同步------------");
        List<Map> mapList = getSyncList(orderId);
        if (mapList == null || mapList.size() <= 0) {
            log.info("同步失败：查询不到同步内容");
            throw new RuntimeException("查询不到同步内容");
        }
        // 订单同步新增
        syncOrder(mapList);
        log.info("----订单同步结束-"+orderId+"---");

    }

    /**
     * 订单同步新增
     *
     * @param mapList
     */
    private void syncOrder(List<Map> mapList) {
        List<TBusOrderHead> tBusOrderHeads = new ArrayList<>();
        TBusOrderHead tBusOrderHead;
        String orderNo;
        String billNo;
        List<TBusOrderHead> tBusOrderHeadsByOrderNo;
        for (Map map : mapList) {
            tBusOrderHead = JSON.parseObject(JSON.toJSONString(map), TBusOrderHead.class);
            orderNo = String.valueOf(tBusOrderHead.getBillNo()) + "-" + String.valueOf(tBusOrderHead.getMidMoEntrySeq());
            billNo = tBusOrderHead.getBillNo();
            tBusOrderHeadsByOrderNo = orderHeadRepository.findByOrderNo(orderNo);
            if (tBusOrderHeadsByOrderNo != null && tBusOrderHeadsByOrderNo.size() > 0) {
                return;
            }
            tBusOrderHead.setOrderNo(orderNo);
            tBusOrderHead.setCreatedTime(new Date());
            tBusOrderHead.setOrderStatus("0");
            tBusOrderHead.setIsDeleted(GlobalConstant.enableTrue);//是否删除 0：删除 1：非删除
            //由于订单变更新增行，无法生成需求单号，所以订单同步时把首行的需求单号复制给变更新增行中
            if (StringUtils.isBlank(tBusOrderHead.getMidMoSaleOrderNo())) {
                String midMoSaleOrderNoByBillNo = orderHeadRepository.getMidMoSaleOrderNoByBillNo(billNo);
                tBusOrderHead.setMidMoSaleOrderNo(midMoSaleOrderNoByBillNo);
            }
            //获取订单下的用料清单列表
            Set<TBusOrderPPBom> orderPPBoms = listPpbomSync(tBusOrderHead.getErpMidMoEntryPpbomId(), tBusOrderHead.getOrderId());
            tBusOrderHead.setTBusOrderPPBomSet(orderPPBoms);
            tBusOrderHeads.add(tBusOrderHead);
        }
        log.info("同步内容获取成功，开始写入订单数据！");
        orderHeadRepository.saveAll(tBusOrderHeads);
    }

    /**
     * 判断是变更/同步 同步：true 变更：false
     *
     * @param orderId
     * @return
     */
    private Boolean SyncUpdate(Integer orderId) {
        Boolean flag;
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select * from dblink(");
        sqlBuilder.append("'hostaddr=");
        sqlBuilder.append(lichengDataSouce.getHost());
        sqlBuilder.append(" port=");
        sqlBuilder.append(lichengDataSouce.getPort());
        sqlBuilder.append(" dbname=");
        sqlBuilder.append(lichengDataSouce.getDbname());
        sqlBuilder.append(" user=");
        sqlBuilder.append(lichengDataSouce.getUser());
        sqlBuilder.append(" password=");
        sqlBuilder.append(lichengDataSouce.getPassword());
        sqlBuilder.append("',");
        sqlBuilder.append("'");
        sqlBuilder.append("select is_new from mid_mo where mid_mo_id=" + orderId + "') as lichengTable(\"is_new\" BOOL)");
        log.info("同步sql：" + sqlBuilder);
        String sql = sqlBuilder.toString();
        Query query = entityManager.createNativeQuery(sql);
        query.unwrap(NativeQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List<Map> mapList = query.getResultList();
        if(mapList==null||mapList.size()==0){
            throw new RuntimeException("没有同步数据");
        }
        flag = ((Boolean) mapList.get(0).get("is_new")) == null ? true : ((Boolean) mapList.get(0).get("is_new"));
        return flag;
    }

    /**
     * 获取中间表订单同步数据
     *
     * @param orderId
     * @return
     */
    private List<Map> getSyncList(Integer orderId) {
        Map jsonMap = JSON.parseObject(GlobalConstant.readJsonFile(GlobalConstant.currentPath() + GlobalConstant.ORDER_SYNC_JSON), Map.class);
        List<OrderFileModal> orderFields = JSON.parseArray(String.valueOf(jsonMap.get("orderFields")), OrderFileModal.class);
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select * from dblink(");
        sqlBuilder.append("'hostaddr=");
        sqlBuilder.append(lichengDataSouce.getHost());
        sqlBuilder.append(" port=");
        sqlBuilder.append(lichengDataSouce.getPort());
        sqlBuilder.append(" dbname=");
        sqlBuilder.append(lichengDataSouce.getDbname());
        sqlBuilder.append(" user=");
        sqlBuilder.append(lichengDataSouce.getUser());
        sqlBuilder.append(" password=");
        sqlBuilder.append(lichengDataSouce.getPassword());
        sqlBuilder.append("',");
        sqlBuilder.append("'");
        sqlBuilder.append("select ");
        for (OrderFileModal orderField : orderFields) {
            sqlBuilder.append(orderField.getFrom());
            sqlBuilder.append(".");
            sqlBuilder.append(orderField.getToField());
            sqlBuilder.append(" as ");
            sqlBuilder.append(orderField.getMyField());
            sqlBuilder.append(",");
        }
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        sqlBuilder.append(" from mid_mo join mid_mo_entry on mid_mo.mid_mo_id=mid_mo_entry.mid_mo_id ");
        sqlBuilder.append("where mid_mo.mid_mo_id=");
        sqlBuilder.append(orderId);
        sqlBuilder.append("')");
        sqlBuilder.append(" as lichengTable(");
        for (OrderFileModal orderField : orderFields) {
            sqlBuilder.append("\"");
            sqlBuilder.append(orderField.getMyField());
            sqlBuilder.append("\" ");
            sqlBuilder.append(orderField.getToType());
            sqlBuilder.append(",");
        }
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        sqlBuilder.append(")");
        log.info("同步sql：" + sqlBuilder);
        String sql = sqlBuilder.toString();
        Query query = entityManager.createNativeQuery(sql);
        query.unwrap(NativeQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List<Map> mapList = query.getResultList();
        return mapList;
    }

    @Transactional
    @Override
    public void syncStatus(Integer orderEntryId) {
        log.info("----订单同步开始：" + orderEntryId + "----");
        Map jsonMap = JSON.parseObject(GlobalConstant.readJsonFile(GlobalConstant.currentPath() + GlobalConstant.ORDER_STATUS_SYNC_JSON), Map.class);
        List<OrderFileModal> orderFields = JSON.parseArray(String.valueOf(jsonMap.get("orderFields")), OrderFileModal.class);
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select * from dblink(");
        sqlBuilder.append("'hostaddr=");
        sqlBuilder.append(lichengDataSouce.getHost());
        sqlBuilder.append(" port=");
        sqlBuilder.append(lichengDataSouce.getPort());
        sqlBuilder.append(" dbname=");
        sqlBuilder.append(lichengDataSouce.getDbname());
        sqlBuilder.append(" user=");
        sqlBuilder.append(lichengDataSouce.getUser());
        sqlBuilder.append(" password=");
        sqlBuilder.append(lichengDataSouce.getPassword());
        sqlBuilder.append("',");
        sqlBuilder.append("'");
        sqlBuilder.append("select ");
        for (OrderFileModal orderField : orderFields) {
            sqlBuilder.append(orderField.getFrom());
            sqlBuilder.append(".");
            sqlBuilder.append(orderField.getToField());
            sqlBuilder.append(" as ");
            sqlBuilder.append(orderField.getMyField());
            sqlBuilder.append(",");
        }
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        sqlBuilder.append(" from mid_mo_entry_status ");
        sqlBuilder.append(" JOIN mid_mo_entry ON mid_mo_entry_status.mid_mo_entry_id = mid_mo_entry.mid_mo_entry_id  ");
        sqlBuilder.append("where mid_mo_entry_status.mid_mo_entry_id=");
        sqlBuilder.append(orderEntryId);
        sqlBuilder.append("')");
        sqlBuilder.append(" as lichengTable(");
        for (OrderFileModal orderField : orderFields) {
            sqlBuilder.append("\"");
            sqlBuilder.append(orderField.getMyField());
            sqlBuilder.append("\" ");
            sqlBuilder.append(orderField.getToType());
            sqlBuilder.append(",");
        }
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        sqlBuilder.append(")");
        log.info("同步sql：" + sqlBuilder);
        String sql = sqlBuilder.toString();
        Query query = entityManager.createNativeQuery(sql);
//        query.setParameter(1, orderId);
        query.unwrap(NativeQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List<Map> mapList = query.getResultList();
        if (mapList == null || mapList.size() <= 0) {
            log.info("同步失败：查询不到同步内容");
            throw new RuntimeException("查询不到同步内容");
        }
        List<TBusOrderHead> tBusOrderHeads = new ArrayList<>();
        TBusOrderHead tBusOrderHead;
        OrderStatusVo orderStatusVo;
        String orderNo;
        List<TBusOrderHead> tBusOrderHeadsByOrderNo;
        for (Map map : mapList) {
            orderStatusVo = JSON.parseObject(JSON.toJSONString(map), OrderStatusVo.class);
            orderNo = String.valueOf(orderStatusVo.getBillNo()) + "-" + String.valueOf(orderStatusVo.getErpMoEntrySeq());
            tBusOrderHeadsByOrderNo = orderHeadRepository.findByOrderNo(orderNo);
            if (tBusOrderHeadsByOrderNo != null && tBusOrderHeadsByOrderNo.size() > 0) {
                tBusOrderHead = tBusOrderHeadsByOrderNo.get(0);
                if (orderStatusVo.getPendingValue().equals(1)) {
                    //挂起时，MES订单状态是已完成，同步取消，显示挂起无效
                    if (tBusOrderHead.getOrderStatus().equals(LichengConstants.ORDERSTATUS_3)) {
                        tBusOrderHead.setOrderPendingDesc(LichengConstants.ORDER_PENDING_DESC_1);//挂起备注
                    }
                    tBusOrderHead.setOldOrderStatus(tBusOrderHead.getOrderStatus());
                    tBusOrderHead.setOrderStatus(LichengConstants.ORDERSTATUS_4);//已挂起
                    tBusOrderHead.setOrderPendingDate(new Date());//挂起时间
                } else {
                    if (StringUtils.isNotEmpty(tBusOrderHead.getOldOrderStatus())) {
                        //反挂起时，MES订单状态原来是已完成，同步成功，显示挂起取消
                        if (tBusOrderHead.getOldOrderStatus().equals(LichengConstants.ORDERSTATUS_3)) {
                            tBusOrderHead.setOrderPendingDesc(LichengConstants.ORDER_PENDING_DESC_2);//挂起备注
                        }
                        tBusOrderHead.setOrderStatus(tBusOrderHead.getOldOrderStatus());//更新为原订单状态
                        tBusOrderHead.setOldOrderStatus(null);
                        tBusOrderHead.setOrderPendingDate(null);//挂起时间
                    }
                }
            }
        }
        log.info("订单状态同步内容获取成功，开始写入订单状态！");
        orderHeadRepository.saveAll(tBusOrderHeads);
        log.info("----订单状态同步结束-\"+orderEntryId+\"---");
    }

    /**
     * 同步用料清单，通过明细表id
     *
     * @return
     */
    private Set<TBusOrderPPBom> listPpbomSync(Integer erpMoEntryId, Integer orderId) {
        Set<TBusOrderPPBom> tBusOrderPPBoms = new HashSet<>();
        Map jsonMap = JSON.parseObject(GlobalConstant.readJsonFile(GlobalConstant.currentPath() + GlobalConstant.ORDER_SYNC_JSON), Map.class);
        List<OrderFileModal> pPBomsFields = JSON.parseArray(String.valueOf(jsonMap.get("orderPpbomFields")), OrderFileModal.class);
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select * from dblink(");
        sqlBuilder.append("'hostaddr=");
        sqlBuilder.append(lichengDataSouce.getHost());
        sqlBuilder.append(" port=");
        sqlBuilder.append(lichengDataSouce.getPort());
        sqlBuilder.append(" dbname=");
        sqlBuilder.append(lichengDataSouce.getDbname());
        sqlBuilder.append(" user=");
        sqlBuilder.append(lichengDataSouce.getUser());
        sqlBuilder.append(" password=");
        sqlBuilder.append(lichengDataSouce.getPassword());
        sqlBuilder.append("',");
        sqlBuilder.append("'");
        sqlBuilder.append("select ");
        for (OrderFileModal orderField : pPBomsFields) {
            sqlBuilder.append(orderField.getFrom());
            sqlBuilder.append(".");
            sqlBuilder.append(orderField.getToField());
            sqlBuilder.append(" as ");
            sqlBuilder.append(orderField.getMyField());
            sqlBuilder.append(",");
        }
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        sqlBuilder.append(" from mid_ppbom join mid_ppbom_entry on mid_ppbom.mid_ppbom_id=mid_ppbom_entry.mid_ppbom_id ");
        sqlBuilder.append("where mid_ppbom.mid_mo_entry_id=");
        sqlBuilder.append(erpMoEntryId);
        sqlBuilder.append("')");
        sqlBuilder.append(" as lichengTable(");
        for (OrderFileModal orderField : pPBomsFields) {
            sqlBuilder.append("\"");
            sqlBuilder.append(orderField.getMyField());
            sqlBuilder.append("\" ");
            sqlBuilder.append(orderField.getToType());
            sqlBuilder.append(",");
        }
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        sqlBuilder.append(")");
        log.info("用来清单内容同步sql:" + sqlBuilder);
        String sql = sqlBuilder.toString();
        Query query = entityManager.createNativeQuery(sql);
//        query.setParameter(1, erpMoEntryId);
        query.unwrap(NativeQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List<Map> mapList = query.getResultList();
        if (mapList == null || mapList.size() <= 0) {
            return tBusOrderPPBoms;
        }
        TBusOrderPPBom tBusOrderPPBom;
        for (Map map : mapList) {
            tBusOrderPPBom = JSON.parseObject(JSON.toJSONString(map), TBusOrderPPBom.class);
            tBusOrderPPBom.setOrderId(orderId);
//            tBusOrderPPBom.setMustQty(tBusOrderPPBom.getMustQty() == null ? 0 : tBusOrderPPBom.getMustQty());
            if (tBusOrderPPBom.getMidPpbomEntryIsIntoBool() != null) {
                tBusOrderPPBom.setMidPpbomEntryIsInto(tBusOrderPPBom.getMidPpbomEntryIsIntoBool() ? 1 : 0);
            }
            tBusOrderPPBoms.add(tBusOrderPPBom);
        }
        return tBusOrderPPBoms;
    }

    /**
     * 添加同步日志方法
     *
     * @param orderId
     * @param syncStatus
     */
    public void addLog(String orderId, String syncStatus, String syncContent, String syncType) {
        TSyncOrderLog tSyncOrderLog = new TSyncOrderLog();
        tSyncOrderLog.setBillNo(orderId);
        tSyncOrderLog.setSyncStatus(syncStatus);
        tSyncOrderLog.setSyncTime(new Date());
        tSyncOrderLog.setSyncContent(syncContent);
        tSyncOrderLog.setSyncType(syncType);
        syncOrderLogRepository.save(tSyncOrderLog);
    }

    @Transactional
    @Override
    public void midMaterialSync(Boolean flag) {
        log.info("----物料同步开始----");
        long start = System.currentTimeMillis();
        int i = 0;
        int cout = 0;
        do {
            //1.分页获取列表
            List<Map> mapList = listMap(flag, i);
            if (null == mapList || 0 == mapList.size()) {
                break;
            }
            //2.删除历史物料
            deleteKdMaterialNumber(mapList);

            //3.批量插入
            insertMaterial(mapList);
            log.info("-------------本次同步数量：" + mapList.size() + "----------------");
            cout += mapList.size();
            i++;
        } while (true);
        long end = System.currentTimeMillis();
        log.info("----------------本轮同步数量：" + cout + "-----------------------------------------");
        log.info("---------------本轮耗时：" + (end - start) + " ms-----------------------");
        log.info("----物料同步结束----");
    }

    @Transactional
    @Override
    public void syncOrg() {
        log.info("----同步组织表开始----");
        long start = System.currentTimeMillis();
        int i = 0;
        int cout = 0;
        do {
            //1.分页获取列表
            List<Map> mapList = listMapOrg(i);
            if (null == mapList || 0 == mapList.size()) {
                break;
            }
            //2.删除历史组织
            deleteOrgByKdOrgNum(mapList);

            //批量插入
            insertOrg(mapList);
            log.info("-------------本次同步数量：" + mapList.size() + "----------------");
            cout += mapList.size();
            i++;
        } while (true);
        long end = System.currentTimeMillis();
        log.info("----------------本轮同步数量：" + cout + "-----------------------------------------");
        log.info("---------------本轮耗时：" + (end - start) + " ms-----------------------");
        log.info("----同步组织表结束----");
    }

    @Transactional
    @Override
    public void syncDept() {
        log.info("----同步部门表开始----");
        long start = System.currentTimeMillis();
        int i = 0;
        int cout = 0;
        do {
            //1.分页获取列表
            List<Map> mapList = listMapDept(i);
            if (null == mapList || 0 == mapList.size()) {
                break;
            }
            //2.删除历史组织
            deleteDeptByKdOrgNum(mapList);

            //批量插入
            insertDept(mapList);
            log.info("-------------本次同步数量：" + mapList.size() + "----------------");
            cout += mapList.size();
            i++;
        } while (true);
        long end = System.currentTimeMillis();
        log.info("----------------本轮同步数量：" + cout + "-----------------------------------------");
        log.info("---------------本轮耗时：" + (end - start) + " ms-----------------------");
        log.info("----同步部门表结束----");
    }

    @Transactional
    @Override
    public void syncOrderUpdate(Integer id) {
        //获取变更的订单信息
        List<Map> syncList = getSyncList(id);
        TBusOrderHead tBusOrderHead;
        TBusOrderHead byOrderNo;
        String orderNo;
        TBusOrderUpdate tBusOrderUpdate;
        String flag;
        Boolean forFlag;
        String billNo = String.valueOf(syncList.get(0).get("bill_no"));
        List<TBusOrderHead> tBusOrderHeads = orderHeadRepository.findByBillNoAndIsDeleted(billNo, "1");
        for (Map map : syncList) {
            tBusOrderUpdate = new TBusOrderUpdate();
            //标记
            flag = "-1";
            tBusOrderHead = JSON.parseObject(JSON.toJSONString(map), TBusOrderHead.class); //变更订单
            //判断订单明细是否 新增
            flag = orderHeadUpdate(tBusOrderUpdate, tBusOrderHead);
            orderNo = String.valueOf(tBusOrderHead.getBillNo()) + "-" + String.valueOf(tBusOrderHead.getMidMoEntrySeq());
            byOrderNo = orderHeadRepository.findByOrderNo(orderNo).get(0); //原订单
            //1.数量增加场景：ERP在原有订单上，新增主产品物料，新增一行记录
            //2.采用唯一性标识本地库与中间表比对，如果中间表不存在，则为删除，将该行记录标记为删除，并将删除的数据存储在附表中。
            if (flag.equals("-1")) {
                //替换用料、数量减少、订单挂起场景，直接在原订单上进行修改。
                flag = syncOrderUpdateBySize(byOrderNo, tBusOrderHead, flag, tBusOrderUpdate);
            }
//            if (flag.equals("-1")) {
            //替换用料、数量减少、订单挂起场景，直接在原订单上进行修改。
            flag = syncOrderUpdateByUpdate(byOrderNo, tBusOrderHead, flag, tBusOrderUpdate);
//            }
//            syncOrderUpdateBBom(byOrderNo,tBusOrderHead,tBusOrderUpdate);
            tBusOrderUpdate.setCreatedTime(new Date());
            tBusOrderUpdate.setOrderId(byOrderNo.getOrderId());
            forFlag = true;
            switch (flag) {
                case "0": {
                    tBusOrderUpdate.setUpdateType(String.valueOf(flag));
                    log.info("当前订单变更为新增物料");
                    break;
                }
                case "1": {
                    tBusOrderUpdate.setUpdateType(String.valueOf(flag));
                    log.info("当前订单变更为删除物料");
                    break;

                }
                case "2": {
                    tBusOrderUpdate.setUpdateType(String.valueOf(flag));
                    log.info("当前订单变更为修改物料/订单信息");
                    break;
                }
                case "-1": {
                    log.info("订单没有任何变更");
                    forFlag = false;
                    break;
                }
                default: {
                    forFlag = false;
                    log.info("订单没有任何变更");
                }
            }
            // 移除删除数据
            for (int i = 0; i < tBusOrderHeads.size(); i++) {
                if (tBusOrderHeads.get(i).getErpMoEntryId().intValue() == tBusOrderHead.getErpMoEntryId().intValue()) {
                    //找到对象
                    tBusOrderHeads.remove(i);
                    break;
                }
            }
            if (forFlag) {
                //添加订单变更记录表
                orderUpdateRepository.save(tBusOrderUpdate);
                // 发送消息
                orderUpdateChatMessage(byOrderNo, tBusOrderUpdate);
            }
        }
        //判断是否为删除
        if (tBusOrderHeads != null && tBusOrderHeads.size() > 0) {
            log.info("----订单明细删除----");
            for (TBusOrderHead busOrderHead : tBusOrderHeads) {
                busOrderHead.setIsDeleted(GlobalConstant.enableFalse);
                orderHeadRepository.saveAndFlush(busOrderHead);
                TBusOrderUpdate tBusOrderUpdate1 = new TBusOrderUpdate();
                tBusOrderUpdate1.setOrderId(busOrderHead.getOrderId());
                tBusOrderUpdate1.setOrderJson(JSON.toJSONString(busOrderHead));
                tBusOrderUpdate1.setCreatedTime(new Date());
                tBusOrderUpdate1.setUpdateType("1");
                orderUpdateRepository.save(tBusOrderUpdate1);
            }
        }
    }

    /**
     * 订单明细新增
     *
     * @param tBusOrderUpdate
     * @param tBusOrderHead
     * @return
     */
    private String orderHeadUpdate(TBusOrderUpdate tBusOrderUpdate, TBusOrderHead tBusOrderHead) {
        //1.判断当前明细是否存在
        Integer erpMoEntryId = tBusOrderHead.getErpMoEntryId();
        if (erpMoEntryId == null) {
            return "-1";
        }
        TBusOrderHead head = orderHeadRepository.findByErpMoEntryId(erpMoEntryId);
        if (head == null) {
            ArrayList<TBusOrderHead> heads = new ArrayList<>();
            heads.add(tBusOrderHead);
            syncOrder(JSON.parseArray(JSON.toJSONString(heads), Map.class));
            return "0";
        }
        return "-1";
    }

    /**
     * 订单变更发送消息
     *
     * @param byOrderNo
     * @param tBusOrderUpdate
     */
    private void orderUpdateChatMessage(TBusOrderHead byOrderNo, TBusOrderUpdate tBusOrderUpdate) {
        Set<TBusOrderProcess> tBusOrderProcessSet = byOrderNo.getTBusOrderProcessSet();
        if (tBusOrderProcessSet == null || tBusOrderProcessSet.size() == 0) {
            return;
        }
        TSysMessageOrder tSysMessageOrder;
        List<TSysMessageOrder> tSysMessageOrders = new ArrayList<>();
        for (TBusOrderProcess tBusOrderProcess : tBusOrderProcessSet) {
            if (tBusOrderProcess == null || tBusOrderProcess.getPersonId() == null || org.apache.commons.lang3.StringUtils.isBlank(tBusOrderProcess.getPersonId().getUserId())) {
                continue;
            }
            tSysMessageOrder = new TSysMessageOrder();
            tSysMessageOrder.setOrderId(byOrderNo.getOrderId());
            tSysMessageOrder.setOrderNo(byOrderNo.getOrderNo());
            tSysMessageOrder.setCreatedTime(byOrderNo.getBillDate());
            tSysMessageOrder.setProductStandard(byOrderNo.getBodyMaterialName());
            tSysMessageOrder.setBillPlanQty(String.valueOf(byOrderNo.getBodyPlanPrdQty()));
            tSysMessageOrder.setMesType("1");
            tSysMessageOrder.setStatusType("1");
            tSysMessageOrder.setIsRead("0");
            tSysMessageOrder.setMesTime(new Date());
            //用户id
            tSysMessageOrder.setUserId(tBusOrderProcess.getPersonId().getUserId());
            String orderJson = tBusOrderUpdate.getOrderJson();
            //增加变更信息
            TBusOrderHead tBusOrderHead = JSON.parseObject(orderJson, TBusOrderHead.class);
            if (StringUtils.isNotEmpty(orderJson) && tBusOrderHead.getBodyPlanPrdQty() != null) {
                List<String> strings = new ArrayList<>();
                strings.add(String.valueOf(tBusOrderHead.getBodyPlanPrdQty()) + " " + tBusOrderHead.getBodyUnit());
                strings.add(String.valueOf(byOrderNo.getBodyPlanPrdQty()) + " " + tBusOrderHead.getBodyUnit());
                tSysMessageOrder.setUpdateStrs(JSON.toJSONString(strings));
            } else {
                List<String> strings = new ArrayList<>();
                String bodyUnit = StringUtils.isNotEmpty(tBusOrderHead.getBodyUnit()) ? tBusOrderHead.getBodyUnit() : "";
                strings.add(String.valueOf(byOrderNo.getBodyPlanPrdQty()) + "" + bodyUnit);
                strings.add(String.valueOf(byOrderNo.getBodyPlanPrdQty()) + "" + bodyUnit);
                tSysMessageOrder.setUpdateStrs(JSON.toJSONString(strings));
            }
            //订单类型
            tSysMessageOrders.add(tSysMessageOrder);
        }
        messageOrderRepository.saveAll(tSysMessageOrders);
    }

    /**
     * 替换用料、数量减少、订单挂起场景，直接在原订单上进行修改。
     *
     * @param byOrderNo
     * @param tBusOrderHead
     * @param flag
     */
    private String syncOrderUpdateByUpdate(TBusOrderHead byOrderNo, TBusOrderHead tBusOrderHead, String flag, TBusOrderUpdate tBusOrderUpdate) {
        Set<TBusOrderPPBom> orderPPBoms = listPpbomSync(tBusOrderHead.getErpMidMoEntryPpbomId(), tBusOrderHead.getOrderId());
        //存放订单变更内容
        TBusOrderHead tBusOrderHead1 = new TBusOrderHead();
        tBusOrderHead1.setOrderStatus(null);
        flag = updateByOrderHead(byOrderNo, tBusOrderHead, flag, tBusOrderHead1);
        // 存放用料清单列表的List
        Set<TBusOrderPPBom> tBusOrderPPBoms = new HashSet<>();
        flag = updateByOrderPpbom(flag, byOrderNo, orderPPBoms, tBusOrderUpdate, tBusOrderPPBoms);
        if (flag.equals("2")) {
            tBusOrderUpdate.setOrderJson(JSON.toJSONString(tBusOrderHead1));
//            tBusOrderUpdate.setPpbomJson(JSON.toJSONString(tBusOrderPPBoms));
        }
        return flag;
    }

    /**
     * 对比用料数据
     *
     * @param flag
     * @param byOrderNo:原数据订单
     * @param orderPPBoms     ：新数据
     * @return
     */
    private String updateByOrderPpbom(String flag, TBusOrderHead byOrderNo, Set<TBusOrderPPBom> orderPPBoms, TBusOrderUpdate tBusOrderUpdate, Set<TBusOrderPPBom> tBusOrderPPBoms) {
        Set<TBusOrderPPBom> set = new HashSet<>();
        Set<TBusOrderPPBom> tBusOrderPPBomSet = byOrderNo.getTBusOrderPPBomSet();
        TBusOrderPPBom currentPPBom;
        //存放变更的id值
        List<Integer> ids = new ArrayList<>();
        //数量增加场景：ERP在原有订单上，新增主产品物料，新增一行记录
        Map<String, Map> ptMap = new HashMap<>();
        //数据map
        Map<String, Object> map;

        //是否替换用料情况
        flag = deleteInsertPpbom(tBusOrderPPBomSet, orderPPBoms, ids, set);
        boolean isFlag;
        for (TBusOrderPPBom tBusOrderPPBom : tBusOrderPPBomSet) {
            currentPPBom = new TBusOrderPPBom();
            isFlag = false;
            for (TBusOrderPPBom orderPPBom : orderPPBoms) {
                if (tBusOrderPPBom.getErpPpbomEntryId().intValue() == orderPPBom.getErpPpbomEntryId().intValue()) {
                    //找到数据
                    if (orderPPBom.getMidPpbomEntryItemType().intValue() == LichengConstants.ORDER_PPBOM_ITEM_TYPE_NAME_VALUE_1) {
                        //标准件
                        map = new HashMap<>();
                        map.put("midPpbomEntryIsInto", orderPPBom.getMidPpbomEntryIsInto());
                        map.put("midPpbomEntryInputProcess", orderPPBom.getMidPpbomEntryInputProcess());
                        map.put("midPpbomEntryWeighDeveptQty", orderPPBom.getMidPpbomEntryWeighDeveptQty());
                        map.put("midPpbomEntryWeighMesQty", orderPPBom.getMidPpbomEntryWeighMesQty());
                        map.put("midPpbomEntryWeighMesUnit", orderPPBom.getMidPpbomEntryWeighMesUnit());
                        map.put("midPpbomEntryWeighDeveptUnit", orderPPBom.getMidPpbomEntryWeighDeveptUnit());
                        map.put("midPpbomEntryMaterialPositiveError", orderPPBom.getMidPpbomEntryMaterialPositiveError());
                        map.put("midPpbomEntryMaterialNegativeError", orderPPBom.getMidPpbomEntryMaterialNegativeError());
                        ptMap.put(orderPPBom.getMidPpbomEntryReplaceGroup().toString(), map);
                    }
                    if ((orderPPBom.getMustQty() == null ? 0F : orderPPBom.getMustQty().floatValue()) != (tBusOrderPPBom.getMustQty() == null ? 0F : tBusOrderPPBom.getMustQty().floatValue())) {
                        currentPPBom.setMustQty(tBusOrderPPBom.getMustQty());
                        isFlag = true;
                    }
                    if ((orderPPBom.getMidPpbomEntryMaterialNegativeError() == null ? 0F : orderPPBom.getMidPpbomEntryMaterialNegativeError().floatValue()) != (tBusOrderPPBom.getMidPpbomEntryMaterialNegativeError() == null ? 0F : tBusOrderPPBom.getMidPpbomEntryMaterialNegativeError().floatValue())) {
                        currentPPBom.setMidPpbomEntryMaterialNegativeError(tBusOrderPPBom.getMidPpbomEntryMaterialNegativeError());
                        isFlag = true;
                    }
                    if ((orderPPBom.getMidPpbomEntryMaterialPositiveError() == null ? 0F : orderPPBom.getMidPpbomEntryMaterialPositiveError().floatValue()) != (tBusOrderPPBom.getMidPpbomEntryMaterialPositiveError() == null ? 0F : tBusOrderPPBom.getMidPpbomEntryMaterialPositiveError().floatValue())) {
                        currentPPBom.setMidPpbomEntryMaterialPositiveError(tBusOrderPPBom.getMidPpbomEntryMaterialPositiveError());
                        isFlag = true;
                    }
                    if ((orderPPBom.getMidPpbomEntryMaterialStandard() == null ? 0F : orderPPBom.getMidPpbomEntryMaterialStandard().floatValue()) != (tBusOrderPPBom.getMidPpbomEntryMaterialStandard() == null ? 0F : tBusOrderPPBom.getMidPpbomEntryMaterialStandard().floatValue())) {
                        currentPPBom.setMidPpbomEntryMaterialStandard(tBusOrderPPBom.getMidPpbomEntryMaterialStandard());
                        isFlag = true;
                    }
                    if (orderPPBom.getMaterialId().intValue() != tBusOrderPPBom.getMaterialId().intValue()) {
                        currentPPBom.setMaterialId(tBusOrderPPBom.getMaterialId());
                        currentPPBom.setMaterialName(tBusOrderPPBom.getMaterialName());
                        currentPPBom.setMaterialNumber(tBusOrderPPBom.getMaterialNumber());
                        isFlag = true;
                    }
                    if (isFlag) {
                        flag = "2";
                        currentPPBom.setOrderPPBomId(tBusOrderPPBom.getOrderPPBomId());
                        ids.add(tBusOrderPPBom.getOrderPPBomId());
                        orderPPBom.setOrderPPBomId(tBusOrderPPBom.getOrderPPBomId());
                        tBusOrderPPBoms.add(orderPPBom);
                    }
                    break;
                }
            }
            if (currentPPBom.getOrderPPBomId() != null) {
                set.add(currentPPBom);
            }
        }
        if (flag.equals("2")) {
            tBusOrderUpdate.setPpbomIds(JSON.toJSONString(ids));
            tBusOrderUpdate.setPpbomJson(JSON.toJSONString(set));
        }
        orderPPBoms.stream().forEach(orderPPBom -> {
            if (orderPPBom.getMidPpbomEntryItemType().intValue() == LichengConstants.ORDER_PPBOM_ITEM_TYPE_NAME_VALUE_3) {
                Map map1 = ptMap.get(orderPPBom.getMidPpbomEntryReplaceGroup().toString());
                if (map1 != null && map1.size() > 0) {
                    orderPPBom.setMidPpbomEntryIsInto((Integer) map1.get("midPpbomEntryIsInto"));
                    orderPPBom.setMidPpbomEntryInputProcess(String.valueOf(map1.get("midPpbomEntryInputProcess")));
                    orderPPBom.setMidPpbomEntryWeighDeveptQty((Float) map1.get("midPpbomEntryWeighDeveptQty"));
                    orderPPBom.setMidPpbomEntryWeighMesQty((Float) map1.get("midPpbomEntryWeighMesQty"));
                    orderPPBom.setMidPpbomEntryWeighMesUnit(String.valueOf(map1.get("midPpbomEntryWeighMesUnit")));
                    orderPPBom.setMidPpbomEntryWeighDeveptUnit(String.valueOf(map1.get("midPpbomEntryWeighDeveptUnit")));
                    orderPPBom.setMidPpbomEntryMaterialPositiveError((Float) map1.get("midPpbomEntryMaterialPositiveError"));
                    orderPPBom.setMidPpbomEntryMaterialNegativeError((Float) map1.get("midPpbomEntryMaterialNegativeError"));
                }
            }
            tBusOrderPPBomSet.stream().forEach(myorderPPBom -> {
                if (orderPPBom.getErpPpbomEntryId().intValue() == myorderPPBom.getErpPpbomEntryId().intValue()) {
                    orderPPBom.setOrderPPBomId(myorderPPBom.getOrderPPBomId());
                }
            });
        });
        if (orderPPBoms.size() > 0 && byOrderNo.getOrderId() != null) {
            byOrderNo.setTBusOrderPPBomSet(orderPPBoms);
            orderHeadRepository.saveAndFlush(byOrderNo);
        }
        return flag;
    }

    /**
     * 替换用料情况判断
     *
     * @param tBusOrderPPBomSet 原数据
     * @param orderPPBoms       新数据
     * @param ids               修改的id集合，删除为-1
     * @param set               修改的内容集合
     * @return
     */
    private String deleteInsertPpbom(Set<TBusOrderPPBom> tBusOrderPPBomSet, Set<TBusOrderPPBom> orderPPBoms, List<Integer> ids, Set<TBusOrderPPBom> set) {
        String flag = "-1";
        // 存放需要新增的数据
        List<TBusOrderPPBom> hashSets = JSON.parseArray(JSON.toJSONString(orderPPBoms), TBusOrderPPBom.class);
        Boolean isFlag;
        //是否投入值
        Integer midPpbomEntryIsInto = -1;
        //投入工序值
        String midPpbomEntryInputProcess = null;
        for (TBusOrderPPBom tBusOrderPPBom : tBusOrderPPBomSet) {
            isFlag = false;
            for (TBusOrderPPBom orderPPBom : orderPPBoms) {
                if (tBusOrderPPBom.getErpPpbomEntryId().intValue() == orderPPBom.getErpPpbomEntryId().intValue()) {
                    isFlag = true;
                    if (orderPPBom.getMidPpbomEntryItemType().intValue() == LichengConstants.ORDER_PPBOM_ITEM_TYPE_NAME_VALUE_1) {
                        //标准件
                        if (midPpbomEntryIsInto == -1) {
                            midPpbomEntryIsInto = orderPPBom.getMidPpbomEntryIsInto();
                        }
                        if (StringUtils.isEmpty(midPpbomEntryInputProcess)) {
                            midPpbomEntryInputProcess = orderPPBom.getMidPpbomEntryInputProcess();
                        }
                    }
                    hashSets.remove(orderPPBom);
                    break;
                }
            }
            //比对完，没有找到对象的，为删除的数据
            if (!isFlag) {
                set.add(tBusOrderPPBom);
            }
        }
        if (hashSets != null && set != null && hashSets.size() > 0 && set.size() > 0) {
            for (TBusOrderPPBom hashSet : hashSets) {
                if (hashSet.getMidPpbomEntryItemType().intValue() == LichengConstants.ORDER_PPBOM_ITEM_TYPE_NAME_VALUE_3) {
                    // 替换件
                    hashSet.setMidPpbomEntryIsInto(midPpbomEntryIsInto);
                    hashSet.setMidPpbomEntryInputProcess(midPpbomEntryInputProcess);
                }
            }
            flag = "2";
            if (hashSets != null && hashSets.size() > 0) {
                orderPPBomRepository.saveAll(hashSets);
                for (TBusOrderPPBom hashSet : hashSets) {
                    ids.add(hashSet.getOrderPPBomId());
                    set.add(hashSet);
                }
            }
        }
        return flag;
    }

    /**
     * 对比订单表数据
     *
     * @param byOrderNo
     * @param tBusOrderHead
     * @param flag
     * @return
     */
    private String updateByOrderHead(TBusOrderHead byOrderNo, TBusOrderHead tBusOrderHead, String flag, TBusOrderHead newOrderHead) {
        TBusOrderHead tBusOrderHead1 = JSON.parseObject(JSON.toJSONString(byOrderNo), TBusOrderHead.class);
        if ((byOrderNo.getBodyMaterialId() == null ? 0 : byOrderNo.getBodyMaterialId().intValue()) != (tBusOrderHead.getBodyMaterialId() == null ? 0 : tBusOrderHead.getBodyMaterialId().intValue())) {
            newOrderHead.setBodyMaterialId(byOrderNo.getBodyMaterialId());
            newOrderHead.setBodyMaterialName(byOrderNo.getBodyMaterialName());
            newOrderHead.setBodyMaterialNumber(byOrderNo.getBodyMaterialNumber());
            tBusOrderHead1.setBodyMaterialId(tBusOrderHead.getBodyMaterialId());
            tBusOrderHead1.setBodyMaterialName(tBusOrderHead.getBodyMaterialName());
            tBusOrderHead1.setBodyMaterialNumber(tBusOrderHead.getBodyMaterialNumber());
            flag = "2";
        }
        if ((byOrderNo.getAddGuoQty() == null ? 0F : byOrderNo.getAddGuoQty().floatValue()) != (tBusOrderHead.getAddGuoQty() == null ? 0F : tBusOrderHead.getAddGuoQty().floatValue())) {
            //添加锅数
            newOrderHead.setAddGuoQty(byOrderNo.getAddGuoQty());
            tBusOrderHead1.setAddGuoQty(tBusOrderHead.getAddGuoQty());
            flag = "2";
        }
        if ((byOrderNo.getAddSumWeight() == null ? 0F : byOrderNo.getAddSumWeight().floatValue()) != (tBusOrderHead.getAddSumWeight() == null ? 0F : tBusOrderHead.getAddSumWeight().floatValue())) {
            //添加总重量
            newOrderHead.setAddSumWeight(byOrderNo.getAddSumWeight());
            tBusOrderHead1.setAddSumWeight(tBusOrderHead.getAddSumWeight());
            flag = "2";
        }
        if ((byOrderNo.getAddUnitWeight() == null ? 0F : byOrderNo.getAddUnitWeight().floatValue()) != (tBusOrderHead.getAddUnitWeight() == null ? 0F : tBusOrderHead.getAddUnitWeight().floatValue())) {
            //添加单位重量
            newOrderHead.setAddUnitWeight(byOrderNo.getAddUnitWeight());
            tBusOrderHead1.setAddUnitWeight(tBusOrderHead.getAddUnitWeight());
            flag = "2";
        }
        if ((byOrderNo.getBodyPlanPrdQty() == null ? 0F : byOrderNo.getBodyPlanPrdQty().floatValue()) != (tBusOrderHead.getBodyPlanPrdQty() == null ? 0F : tBusOrderHead.getBodyPlanPrdQty().floatValue())) {
            //明细-计划生产数量
            newOrderHead.setBodyPlanPrdQty(byOrderNo.getBodyPlanPrdQty());
            tBusOrderHead1.setBodyPlanPrdQty(tBusOrderHead.getBodyPlanPrdQty());
            flag = "2";
        }
        if ((byOrderNo.getBodyPotQty() == null ? 0 : byOrderNo.getBodyPotQty().intValue()) != (tBusOrderHead.getBodyPotQty() == null ? 0 : tBusOrderHead.getBodyPotQty().intValue())) {
            //明细-锅数
            newOrderHead.setBodyPotQty(byOrderNo.getBodyPotQty());
            tBusOrderHead1.setBodyPotQty(tBusOrderHead.getBodyPotQty());
            flag = "2";
        }
        if (byOrderNo.getBillNo().equals(tBusOrderHead.getBodyPotQty())) {
            //批号bill_no
            newOrderHead.setBillNo(byOrderNo.getBillNo());
            tBusOrderHead1.setBillNo(tBusOrderHead.getBillNo());
            flag = "2";
        }
        if (flag.equals("2")) {
            newOrderHead.setOrderId(byOrderNo.getOrderId());
            orderHeadRepository.saveAndFlush(tBusOrderHead1);
        }
        return flag;
    }

    /**
     * @param:byOrderNo:原订单
     * @param:tBusOrderHead变更订单
     *
     * 数量增加场景：ERP在原有订单上，新增主产品物料，新增一行记录
     * 采用唯一性标识本地库与中间表比对，如果中间表不存在，则为删除，将该行记录标记为删除，并将删除的数据存储在附表中。
     */
    private String syncOrderUpdateBySize(TBusOrderHead byOrderNo, TBusOrderHead tBusOrderHead, String flag, TBusOrderUpdate tBusOrderUpdate) {

        //判断订单是否变更
        if(!(String.valueOf(byOrderNo.getMidMoSaleOrderNo()).equals(String.valueOf(tBusOrderHead.getMidMoSaleOrderNo())))){
            //需求订单 是否变更
            byOrderNo.setMidMoSaleOrderNo(tBusOrderHead.getMidMoSaleOrderNo());
        }

        Set<TBusOrderPPBom> tBusOrderPPBomSet = byOrderNo.getTBusOrderPPBomSet();
        Set<TBusOrderPPBom> orderPPBoms = listPpbomSync(tBusOrderHead.getErpMidMoEntryPpbomId(), tBusOrderHead.getOrderId());
        Set<TBusOrderPPBom> tBusOrderPPBoms = new HashSet<>();
        Set<Integer> ppbomsIds = new HashSet<>();
//        if (tBusOrderPPBomSet != null && orderPPBoms != null && tBusOrderPPBomSet.size() > 0 && orderPPBoms.size() > 0) {
        if (orderPPBoms.size() > tBusOrderPPBomSet.size()) {
            //数量增加场景：ERP在原有订单上，新增主产品物料，新增一行记录
            Map<String, Map> ptMap = new HashMap<>();
            //数据map
            Map<String, Object> map;
            Boolean isFlag;
            for (TBusOrderPPBom orderPPBom : orderPPBoms) {
                isFlag = false;
                if (orderPPBom.getMidPpbomEntryItemType().intValue() == LichengConstants.ORDER_PPBOM_ITEM_TYPE_NAME_VALUE_1) {
                    //标准件
                    map = new HashMap<>();
                    map.put("midPpbomEntryIsInto", orderPPBom.getMidPpbomEntryIsInto());
                    map.put("midPpbomEntryInputProcess", orderPPBom.getMidPpbomEntryInputProcess());
                    map.put("midPpbomEntryWeighDeveptQty", orderPPBom.getMidPpbomEntryWeighDeveptQty());
                    map.put("midPpbomEntryWeighMesQty", orderPPBom.getMidPpbomEntryWeighMesQty());
                    map.put("midPpbomEntryWeighMesUnit", orderPPBom.getMidPpbomEntryWeighMesUnit());
                    map.put("midPpbomEntryWeighDeveptUnit", orderPPBom.getMidPpbomEntryWeighDeveptUnit());
                    ptMap.put(orderPPBom.getMidPpbomEntryReplaceGroup().toString(), map);
                }
                for (TBusOrderPPBom tBusOrderPPBom : tBusOrderPPBomSet) {
                    if (orderPPBom.getErpPpbomEntryId().intValue() == tBusOrderPPBom.getErpPpbomEntryId().intValue()) {
                        isFlag = true;
                        break;
                    }
                }
                if (!isFlag) {
                    tBusOrderPPBoms.add(orderPPBom);
                }
            }
            for (TBusOrderPPBom tBusOrderPPBom : tBusOrderPPBoms) {
                if (tBusOrderPPBom.getMidPpbomEntryItemType().intValue() == LichengConstants.ORDER_PPBOM_ITEM_TYPE_NAME_VALUE_3) {
                    //替换料
                    Map map1 = ptMap.get(tBusOrderPPBom.getMidPpbomEntryReplaceGroup().toString());
                    tBusOrderPPBom.setMidPpbomEntryIsInto((Integer) map1.get("midPpbomEntryIsInto"));
                    tBusOrderPPBom.setMidPpbomEntryInputProcess(map1.get("midPpbomEntryInputProcess")==null?null:map1.get("midPpbomEntryInputProcess").toString());
                    tBusOrderPPBom.setMidPpbomEntryWeighDeveptQty((Float) map1.get("midPpbomEntryWeighDeveptQty"));
                    tBusOrderPPBom.setMidPpbomEntryWeighMesQty((Float) map1.get("midPpbomEntryWeighMesQty"));
                    tBusOrderPPBom.setMidPpbomEntryWeighMesUnit(map1.get("midPpbomEntryWeighMesUnit")==null?null:map1.get("midPpbomEntryWeighMesUnit").toString());
                    tBusOrderPPBom.setMidPpbomEntryWeighDeveptUnit(map1.get("midPpbomEntryWeighDeveptUnit")==null?null:map1.get("midPpbomEntryWeighDeveptUnit").toString());
                }
            }
            orderPPBomRepository.saveAll(tBusOrderPPBoms);
            tBusOrderPPBoms.stream().forEach(tBusOrderPPBom -> {
                ppbomsIds.add(tBusOrderPPBom.getOrderPPBomId());
            });
            tBusOrderUpdate.setPpbomJson(JSON.toJSONString(tBusOrderPPBoms));
            tBusOrderUpdate.setPpbomIds(JSON.toJSONString(ppbomsIds));
            tBusOrderPPBomSet.addAll(tBusOrderPPBoms);
            orderHeadRepository.saveAndFlush(byOrderNo);
            flag = "1";
            return flag;
        }
        if (orderPPBoms.size() < tBusOrderPPBomSet.size()) {
            //采用唯一性标识本地库与中间表比对，如果中间表不存在，则为删除，将该行记录标记为删除，并将删除的数据存储在附表中。
            Boolean isFlag;
            Set<TBusOrderPPBom> set = new HashSet<>();
            for (TBusOrderPPBom tBusOrderPPBom : tBusOrderPPBomSet) {
                isFlag = false;
                for (TBusOrderPPBom orderPPBom : orderPPBoms) {
                    if (tBusOrderPPBom.getMaterialId().intValue() == orderPPBom.getMaterialId().intValue()) {
                        isFlag = true;
                        break;
                    }
                }
                if (!isFlag) {
                    tBusOrderPPBoms.add(tBusOrderPPBom);
                } else {
                    set.add(tBusOrderPPBom);
                }
            }
            byOrderNo.setTBusOrderPPBomSet(set);
            orderHeadRepository.saveAndFlush(byOrderNo);
            orderPPBomRepository.deleteInBatch(tBusOrderPPBoms);
            tBusOrderUpdate.setPpbomJson(JSON.toJSONString(tBusOrderPPBoms));
            flag = "1";
            return flag;
        }
//        }
        return flag;
    }

    /**
     * 批量插入
     *
     * @param mapList
     */
    private void insertMaterial(List<Map> mapList) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("insert into mid_material(kd_material_id,kd_material_number,kd_material_name,kd_material_spec,kd_material_modify_date_time,");
        sqlBuilder.append("gmt_create,gmt_modified,is_delete,kd_material_modify_create_time,kd_material_membrane_thickness,kd_material_membrane_width,");
        sqlBuilder.append("kd_material_membrane_density,kd_material_per_weight,kd_material_net_weight,kd_material_doc_status,");
        sqlBuilder.append("kd_material_disable_status,kd_material_unit_id,kd_material_unit_name,kd_material_unit_number,kd_material_group,");
        sqlBuilder.append("kd_material_use_org_id,kd_material_use_org_number,kd_material_use_org_name,");
        sqlBuilder.append("kd_material_cut_weight,kd_material_peel_weight,kd_material_mixture_weight,kd_material_stretch_weight,kd_material_is_peel,");
        sqlBuilder.append("kd_material_workshop_id,kd_material_workshop_number,kd_material_workshop_name,kd_material_props_id,kd_material_each_piece_num)");
        sqlBuilder.append("values");
        mapList.stream().forEach(map -> {
            sqlBuilder.append("(");
            sqlBuilder.append(Integer.parseInt(String.valueOf(map.get("kd_material_id"))));
            sqlBuilder.append(",");
            sqlBuilder.append("'" + String.valueOf(map.get("kd_material_number")) + "'");
            sqlBuilder.append(",");
            sqlBuilder.append("'" + String.valueOf(map.get("kd_material_name")) + "'");
            sqlBuilder.append(",");
            sqlBuilder.append("'" + String.valueOf(map.get("kd_material_spec")) + "'");
            sqlBuilder.append(",");
            sqlBuilder.append("'" + String.valueOf(map.get("kd_material_modify_date_time")) + "'");
            sqlBuilder.append(",");
            sqlBuilder.append("'" + String.valueOf(map.get("gmt_create")) + "'");
            sqlBuilder.append(",");
            sqlBuilder.append("'" + String.valueOf(map.get("gmt_modified") + "'"));
            sqlBuilder.append(",");
            sqlBuilder.append(Integer.parseInt(String.valueOf(map.get("is_delete"))));
            sqlBuilder.append(",");
            sqlBuilder.append("'" + String.valueOf(map.get("kd_material_modify_create_time")) + "'");
            sqlBuilder.append(",");
            sqlBuilder.append(Float.parseFloat(String.valueOf(map.get("kd_material_membrane_thickness"))));
            sqlBuilder.append(",");
            sqlBuilder.append(Float.parseFloat(String.valueOf(map.get("kd_material_membrane_width"))));
            sqlBuilder.append(",");
            sqlBuilder.append(Float.parseFloat(String.valueOf(map.get("kd_material_membrane_density"))));
            sqlBuilder.append(",");
            sqlBuilder.append(Float.parseFloat(String.valueOf(map.get("kd_material_per_weight"))));
            sqlBuilder.append(",");
            sqlBuilder.append(Float.parseFloat(String.valueOf(map.get("kd_material_net_weight"))));
            sqlBuilder.append(",");
            sqlBuilder.append("'" + String.valueOf(map.get("kd_material_doc_status")) + "'");
            sqlBuilder.append(",");
            sqlBuilder.append("'" + String.valueOf(map.get("kd_material_disable_status")) + "'");
            sqlBuilder.append(",");
            sqlBuilder.append(Integer.parseInt(String.valueOf(map.get("kd_material_unit_id"))));
            sqlBuilder.append(",");
            sqlBuilder.append("'" + String.valueOf(map.get("kd_material_unit_name")) + "'");
            sqlBuilder.append(",");
            sqlBuilder.append("'" + String.valueOf(map.get("kd_material_unit_number")) + "'");
            sqlBuilder.append(",");
            sqlBuilder.append("'" + String.valueOf(map.get("kd_material_group")) + "'");
            sqlBuilder.append(",");
            sqlBuilder.append(Integer.parseInt(String.valueOf(map.get("kd_material_use_org_id"))));
            sqlBuilder.append(",");
            sqlBuilder.append("'" + String.valueOf(map.get("kd_material_use_org_number")) + "'");
            sqlBuilder.append(",");
            sqlBuilder.append("'" + String.valueOf(map.get("kd_material_use_org_name")) + "'");

            sqlBuilder.append(",");
            sqlBuilder.append(Float.parseFloat(String.valueOf(map.get("kd_material_cut_weight") == null ? 0 : map.get("kd_material_cut_weight"))));
            sqlBuilder.append(",");
            sqlBuilder.append(Float.parseFloat(String.valueOf(map.get("kd_material_peel_weight") == null ? 0 : map.get("kd_material_peel_weight"))));
            sqlBuilder.append(",");
            sqlBuilder.append(Float.parseFloat(String.valueOf(map.get("kd_material_mixture_weight") == null ? 0 : map.get("kd_material_mixture_weight"))));
            sqlBuilder.append(",");
            sqlBuilder.append(Float.parseFloat(String.valueOf(map.get("kd_material_stretch_weight") == null ? 0 : map.get("kd_material_stretch_weight"))));
            sqlBuilder.append(",");
            sqlBuilder.append("true".equals(String.valueOf(map.get("kd_material_is_peel"))) ? 1 : 0);
            sqlBuilder.append(",");
            sqlBuilder.append(Integer.parseInt(String.valueOf(map.get("kd_material_workshop_id") == null ? 0 : map.get("kd_material_workshop_id"))));
            sqlBuilder.append(",");
            sqlBuilder.append("'" + String.valueOf(map.get("kd_material_workshop_number")) + "'");
            sqlBuilder.append(",");
            sqlBuilder.append("'" + String.valueOf(map.get("kd_material_workshop_name")) + "'");
            sqlBuilder.append(",");
            sqlBuilder.append("'" + String.valueOf(map.get("kd_material_props_id")) + "'");
            sqlBuilder.append(",");
            sqlBuilder.append(Integer.parseInt(String.valueOf(map.get("kd_material_each_piece_num") == null ? 0 : map.get("kd_material_each_piece_num"))));
            sqlBuilder.append(")");
            sqlBuilder.append(",");
        });
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        String sql = sqlBuilder.toString();
        log.info("sql:" + sql);
        int size = entityManager.createNativeQuery(sql).executeUpdate();
        log.info("------------本次批量插入数量：" + size);
    }

    /**
     * 批量插入部门表
     *
     * @param mapList
     */
    private void insertDept(List<Map> mapList) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("insert into mid_dept(kd_dept_id,kd_dept_name,kd_dept_num,kd_org_num,gmt_create,gmt_modified,is_delete)");
        sqlBuilder.append("values");
        mapList.stream().forEach(map -> {
            sqlBuilder.append("(");
            sqlBuilder.append(Integer.parseInt(String.valueOf(map.get("kd_dept_id"))));
            sqlBuilder.append(",");
            sqlBuilder.append("'" + String.valueOf(map.get("kd_dept_name")) + "'");
            sqlBuilder.append(",");
            sqlBuilder.append("'" + String.valueOf(map.get("kd_dept_num")) + "'");
            sqlBuilder.append(",");
            sqlBuilder.append("'" + String.valueOf(map.get("kd_org_num")) + "'");
            sqlBuilder.append(",");
            sqlBuilder.append("'" + String.valueOf(map.get("gmt_create")) + "'");
            sqlBuilder.append(",");
            sqlBuilder.append("'" + String.valueOf(map.get("gmt_modified")) + "'");
            sqlBuilder.append(",");
            sqlBuilder.append(Integer.parseInt(String.valueOf(map.get("is_delete"))));
            sqlBuilder.append(")");
            sqlBuilder.append(",");
        });
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        String sql = sqlBuilder.toString();
        log.info("sql:" + sql);
        int size = entityManager.createNativeQuery(sql).executeUpdate();
        log.info("------------本次批量插入数量：" + size);
    }

    /**
     * 批量插入组织表
     *
     * @param mapList
     */
    private void insertOrg(List<Map> mapList) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("insert into mid_org(kd_org_id,kd_org_name,kd_org_num,gmt_create,gmt_modified,is_delete)");
        sqlBuilder.append("values");
        mapList.stream().forEach(map -> {
            sqlBuilder.append("(");
            sqlBuilder.append(Integer.parseInt(String.valueOf(map.get("kd_org_id"))));
            sqlBuilder.append(",");
            sqlBuilder.append("'" + String.valueOf(map.get("kd_org_name")) + "'");
            sqlBuilder.append(",");
            sqlBuilder.append("'" + String.valueOf(map.get("kd_org_num")) + "'");
            sqlBuilder.append(",");
            sqlBuilder.append("'" + String.valueOf(map.get("gmt_create")) + "'");
            sqlBuilder.append(",");
            sqlBuilder.append("'" + String.valueOf(map.get("gmt_modified")) + "'");
            sqlBuilder.append(",");
            sqlBuilder.append(Integer.parseInt(String.valueOf(map.get("is_delete"))));
            sqlBuilder.append(")");
            sqlBuilder.append(",");
        });
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        String sql = sqlBuilder.toString();
        log.info("sql:" + sql);
        int size = entityManager.createNativeQuery(sql).executeUpdate();
        log.info("------------本次批量插入数量：" + size);
    }

    /**
     * 删除历史物料数据
     *
     * @param mapList
     */
    private void deleteKdMaterialNumber(List<Map> mapList) {
        HashSet<Integer> ids = new HashSet<>();
        mapList.stream().forEach(map -> {
            Object kd_material_number = map.get("kd_material_id");
            if (kd_material_number != null) {
                ids.add(Integer.parseInt(kd_material_number.toString()));
            }
        });
        if (ids.size() > 0) {
            midMaterialRepository.deleteByIds(ids);
        }
    }


    /**
     * 删除历史部门信息
     *
     * @param mapList
     */
    private void deleteDeptByKdOrgNum(List<Map> mapList) {
        HashSet<String> nums = new HashSet<>();
        mapList.stream().forEach(map -> {
            if (map.get("kd_dept_num") != null) {
                nums.add(map.get("kd_dept_num").toString());
            }
        });
        if (nums != null && nums.size() > 0) {
            midDeptRepository.deleteByKdDeptNums(nums);
        }
    }

    /**
     * 删除历史组织信息
     *
     * @param mapList
     */
    private void deleteOrgByKdOrgNum(List<Map> mapList) {
        HashSet<String> nums = new HashSet<>();
        mapList.stream().forEach(map -> {
            if (map.get("kd_org_num") != null) {
                nums.add(map.get("kd_org_num").toString());
            }
        });
        if (nums != null && nums.size() > 0) {
            midOrgRepository.deleteByKdOrgNums(nums);
        }
    }

    /**
     * 分页获取列表
     *
     * @param flag
     * @return
     */
    private List<Map> listMap(Boolean flag, int i) {
        Date currentDate = new Date();
        int current = i * materialSyncHeaderSize;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = sdf.format(currentDate);
        Map jsonMap = JSON.parseObject(GlobalConstant.readJsonFile(GlobalConstant.currentPath() + GlobalConstant.MID_MATERIAL_SYNC_JSON), Map.class);
        List<OrderFileModal> orderFields = JSON.parseArray(String.valueOf(jsonMap.get("Fields")), OrderFileModal.class);
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select * from dblink(");
        sqlBuilder.append("'hostaddr=");
        sqlBuilder.append(lichengDataSouce.getHost());
        sqlBuilder.append(" port=");
        sqlBuilder.append(lichengDataSouce.getPort());
        sqlBuilder.append(" dbname=");
        sqlBuilder.append(lichengDataSouce.getDbname());
        sqlBuilder.append(" user=");
        sqlBuilder.append(lichengDataSouce.getUser());
        sqlBuilder.append(" password=");
        sqlBuilder.append(lichengDataSouce.getPassword());
        sqlBuilder.append("',");
        sqlBuilder.append("'");
        sqlBuilder.append("select ");
        for (OrderFileModal orderField : orderFields) {
            sqlBuilder.append(orderField.getFrom());
            sqlBuilder.append(".");
            sqlBuilder.append(orderField.getToField());
            sqlBuilder.append(" as ");
            sqlBuilder.append(orderField.getMyField());
            sqlBuilder.append(",");
        }
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        sqlBuilder.append(" from mid_material ");
        if (!flag) {
            sqlBuilder.append("where to_char(mid_material.gmt_modified,''yyyy-mm-dd'')=");
            sqlBuilder.append("''");
            sqlBuilder.append(dateString);
            sqlBuilder.append("''");
        }
        sqlBuilder.append(" limit ");
        sqlBuilder.append(materialSyncHeaderSize);
        sqlBuilder.append(" offset ");
        sqlBuilder.append(current);
        sqlBuilder.append("')");
        sqlBuilder.append(" as lichengTable(");
        for (OrderFileModal orderField : orderFields) {
            sqlBuilder.append("\"");
            sqlBuilder.append(orderField.getMyField());
            sqlBuilder.append("\" ");
            sqlBuilder.append(orderField.getToType());
            sqlBuilder.append(",");
        }
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        sqlBuilder.append(")");
        String sql = sqlBuilder.toString();
        log.info("sql:" + sql);
        Query query = entityManager.createNativeQuery(sql);
        query.unwrap(NativeQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List<Map> mapList = query.getResultList();
        return mapList;
    }

    /**
     * 分页获取部门表数据
     *
     * @param i
     * @return
     */
    private List<Map> listMapDept(int i) {
        int current = i * materialSyncHeaderSize;
        Map jsonMap = JSON.parseObject(GlobalConstant.readJsonFile(GlobalConstant.currentPath() + GlobalConstant.MID_DEPT_SYNC_JSON), Map.class);
        List<OrderFileModal> orderFields = JSON.parseArray(String.valueOf(jsonMap.get("Fields")), OrderFileModal.class);
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select * from dblink(");
        sqlBuilder.append("'hostaddr=");
        sqlBuilder.append(lichengDataSouce.getHost());
        sqlBuilder.append(" port=");
        sqlBuilder.append(lichengDataSouce.getPort());
        sqlBuilder.append(" dbname=");
        sqlBuilder.append(lichengDataSouce.getDbname());
        sqlBuilder.append(" user=");
        sqlBuilder.append(lichengDataSouce.getUser());
        sqlBuilder.append(" password=");
        sqlBuilder.append(lichengDataSouce.getPassword());
        sqlBuilder.append("',");
        sqlBuilder.append("'");
        sqlBuilder.append("select ");
        for (OrderFileModal orderField : orderFields) {
            sqlBuilder.append(orderField.getFrom());
            sqlBuilder.append(".");
            sqlBuilder.append(orderField.getToField());
            sqlBuilder.append(" as ");
            sqlBuilder.append(orderField.getMyField());
            sqlBuilder.append(",");
        }
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        sqlBuilder.append(" from mid_dept ");
        sqlBuilder.append(" limit ");
        sqlBuilder.append(materialSyncHeaderSize);
        sqlBuilder.append(" offset ");
        sqlBuilder.append(current);
        sqlBuilder.append("')");
        sqlBuilder.append(" as lichengTable(");
        for (OrderFileModal orderField : orderFields) {
            sqlBuilder.append("\"");
            sqlBuilder.append(orderField.getMyField());
            sqlBuilder.append("\" ");
            sqlBuilder.append(orderField.getToType());
            sqlBuilder.append(",");
        }
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        sqlBuilder.append(")");
        String sql = sqlBuilder.toString();
        log.info("sql:" + sql);
        Query query = entityManager.createNativeQuery(sql);
        query.unwrap(NativeQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List<Map> mapList = query.getResultList();
        return mapList;
    }

    /**
     * 分页获取组织表数据
     *
     * @param i
     * @return
     */
    private List<Map> listMapOrg(int i) {
        int current = i * materialSyncHeaderSize;
        Map jsonMap = JSON.parseObject(GlobalConstant.readJsonFile(GlobalConstant.currentPath() + GlobalConstant.MID_ORG_SYNC_JSON), Map.class);
        List<OrderFileModal> orderFields = JSON.parseArray(String.valueOf(jsonMap.get("Fields")), OrderFileModal.class);
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select * from dblink(");
        sqlBuilder.append("'hostaddr=");
        sqlBuilder.append(lichengDataSouce.getHost());
        sqlBuilder.append(" port=");
        sqlBuilder.append(lichengDataSouce.getPort());
        sqlBuilder.append(" dbname=");
        sqlBuilder.append(lichengDataSouce.getDbname());
        sqlBuilder.append(" user=");
        sqlBuilder.append(lichengDataSouce.getUser());
        sqlBuilder.append(" password=");
        sqlBuilder.append(lichengDataSouce.getPassword());
        sqlBuilder.append("',");
        sqlBuilder.append("'");
        sqlBuilder.append("select ");
        for (OrderFileModal orderField : orderFields) {
            sqlBuilder.append(orderField.getFrom());
            sqlBuilder.append(".");
            sqlBuilder.append(orderField.getToField());
            sqlBuilder.append(" as ");
            sqlBuilder.append(orderField.getMyField());
            sqlBuilder.append(",");
        }
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        sqlBuilder.append(" from mid_org ");
        sqlBuilder.append(" limit ");
        sqlBuilder.append(materialSyncHeaderSize);
        sqlBuilder.append(" offset ");
        sqlBuilder.append(current);
        sqlBuilder.append("')");
        sqlBuilder.append(" as lichengTable(");
        for (OrderFileModal orderField : orderFields) {
            sqlBuilder.append("\"");
            sqlBuilder.append(orderField.getMyField());
            sqlBuilder.append("\" ");
            sqlBuilder.append(orderField.getToType());
            sqlBuilder.append(",");
        }
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        sqlBuilder.append(")");
        String sql = sqlBuilder.toString();
        log.info("sql:" + sql);
        Query query = entityManager.createNativeQuery(sql);
        query.unwrap(NativeQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List<Map> mapList = query.getResultList();
        return mapList;
    }

}

@Data
class OrderFileModal {
    private String name;
    private String toField;
    private String myField;
    private String from;
    private String toType;
}
