package org.thingsboard.server.utils;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thingsboard.server.config.KingDeePropertiesData;

import java.io.IOException;

/**
 * 调用金蝶接口实现单据生成，配置本项目相关表
 */
@Component
public class KingDeeBillAuditUtil {

    @Autowired
    KingDeePropertiesData kingDeePropertiesData;

    Logger logger = LoggerFactory.getLogger(getClass());

    @Async
    public void CheckBill(String uniqueSign, String fStockOrgID, String fBillNo, int fid, String fBillNo2, int fid2) throws IOException, JSONException {
        this.CheckBill(uniqueSign, fStockOrgID, fBillNo, fid, fBillNo2, fid2, 81);
    }

    @Async
    public void CheckBill(String uniqueSign, String fStockOrgID, String fBillNo, int fid, String fBillNo2, int fid2, int type) throws IOException, JSONException {
        logger.info("调用金蝶接口");
        String dbId = kingDeePropertiesData.getSystemSource(type).getDatabaseId();
        String uid = kingDeePropertiesData.getSystemSource(type).getUser();
        String pwd = kingDeePropertiesData.getSystemSource(type).getPassword();
        int lang = 2052;
        boolean isAudit = true;
        if (KingDeeUtil.login(kingDeePropertiesData.getSystemSource(type).getK3Cloud(), dbId, uid, pwd, lang)) {
            String sJson = "{\"Creator\":\"kingDee\",\"NeedUpDateFields\":[\"\"],\"CreateOrgId\":" + fStockOrgID + ",\"Numbers\":[\"" + fBillNo + "\"]}";
            String saveJson = "{\"Creator\":\"kingDee\",\"NeedUpDateFields\":[\"\"],\"Model\":{\"FID\":" + fid + "}}";
            String sJson2 = "{\"Creator\":\"kingDee\",\"NeedUpDateFields\":[\"\"],\"CreateOrgId\":" + fStockOrgID + ",\"Numbers\":[\"" + fBillNo2 + "\"]}";
            String saveJson2 = "{\"Creator\":\"kingDee\",\"NeedUpDateFields\":[\"\"],\"Model\":{\"FID\":" + fid2 + "}}";
            logger.info("hola success" + uniqueSign + "模块");

            if ("配盘头".equals(uniqueSign)) {/* 简单生产入库单 */
                String sFormId = "SP_InStock";
                KingDeeUtil.Save(sFormId, saveJson);
                KingDeeUtil.Submit(sFormId, sJson);
                isAudit = KingDeeUtil.Audit(sFormId, sJson);
                if (isAudit){
                    String sFormId2 = "STK_StockCountLoss";
                    KingDeeUtil.Save(sFormId2, saveJson2);
                    KingDeeUtil.Submit(sFormId2, sJson2);
                    isAudit = KingDeeUtil.Audit(sFormId2, sJson2);
                }
            }
        }
    }

    @Async
    public void CheckBill(String uniqueSign, String fStockOrgID, String fBillNo, int fid) throws IOException, JSONException {
        this.CheckBill(uniqueSign, fStockOrgID, fBillNo, fid, 81);
    }

    @Async
    public void CheckBill(String uniqueSign, String fStockOrgID, String fBillNo, int fid, int type) throws IOException, JSONException {
        logger.info("调用金蝶接口");
        String dbId = kingDeePropertiesData.getSystemSource(type).getDatabaseId();
        String uid = kingDeePropertiesData.getSystemSource(type).getUser();
        String pwd = kingDeePropertiesData.getSystemSource(type).getPassword();
        int lang = 2052;
        boolean isAudit = true;
        String tableName = null;
        if (KingDeeUtil.login(kingDeePropertiesData.getSystemSource(type).getK3Cloud(), dbId, uid, pwd, lang)) {
            String sJson = "{\"Creator\":\"kingDee\",\"NeedUpDateFields\":[\"\"],\"CreateOrgId\":" + fStockOrgID + ",\"Numbers\":[\"" + fBillNo + "\"]}";
            String saveJson = "{\"Creator\":\"kingDee\",\"NeedUpDateFields\":[\"\"],\"Model\":{\"FID\":" + fid + "}}";
            logger.info("hola success" + uniqueSign + "模块");

            if ("上盘头".equals(uniqueSign) || "盘头入库-简单生产领料单".equals(uniqueSign)) {/* 简单生产领料单 */
                String sFormId = "SP_PickMtrl";
                KingDeeUtil.Submit(sFormId, sJson);
                isAudit = KingDeeUtil.Audit(sFormId, sJson);
            }
            if ("空盘头入库".equals(uniqueSign) || "空托盘入库".equals(uniqueSign) || "厂区调入".equals(uniqueSign) || "胚布入库-其他入库单".equals(uniqueSign)) {/* 其他入库 */
                String sFormId = "STK_MISCELLANEOUS";
                KingDeeUtil.Save(sFormId, saveJson);
                KingDeeUtil.Submit(sFormId, sJson);
                isAudit = KingDeeUtil.Audit(sFormId, sJson);
            } else if ("盘头调拨".equals(uniqueSign) || "成品入库".equals(uniqueSign) || "原料调拨-立体仓".equals(uniqueSign)) {/* 直接调拨单 */
                String sFormId = "STK_TransferDirect";
                KingDeeUtil.Submit(sFormId, sJson);
                isAudit = KingDeeUtil.Audit(sFormId, sJson);
            } else if ("厂区调出".equals(uniqueSign) || "其他出库单".equals(uniqueSign)) {/* 其他出库单 */
                String sFormId = "STK_MisDelivery";
                KingDeeUtil.Submit(sFormId, sJson);
                isAudit = KingDeeUtil.Audit(sFormId, sJson);
            } else if ("盘头报废".equals(uniqueSign)) {/* 盘亏单 */
                String sFormId = "STK_StockCountLoss";
                KingDeeUtil.Submit(sFormId, sJson);
                isAudit = KingDeeUtil.Audit(sFormId, sJson);
            } else if ("下盘头".equals(uniqueSign)) {/* 简单生产退料 */
                String sFormId = "SP_ReturnMtrl";
                KingDeeUtil.Submit(sFormId, sJson);
                isAudit = KingDeeUtil.Audit(sFormId, sJson);
            } else if ("盘头入库".equals(uniqueSign) || "简单生产入库单".equals(uniqueSign)) {/* 简单生产入库单 */
                String sFormId = "SP_InStock";
                tableName = "T_SP_InStock";
                KingDeeUtil.Save(sFormId, saveJson);
                KingDeeUtil.Submit(sFormId, sJson);
                isAudit = KingDeeUtil.Audit(sFormId, sJson);
            } else if ("胚布入库-生产入库单".equals(uniqueSign) || "生产入库单".equals(uniqueSign)) {
                String sFormId = "PRD_INSTOCK";
                tableName = "T_PRD_INSTOCK";
                KingDeeUtil.Submit(sFormId, sJson);
                isAudit = KingDeeUtil.Audit(sFormId, sJson);
            } else if ("直接调拨单".equals(uniqueSign)) {
                String sFormId = "STK_TransferDirect";
                KingDeeUtil.Save(sFormId, saveJson);
                KingDeeUtil.Submit(sFormId, sJson);
                isAudit = KingDeeUtil.Audit(sFormId, sJson);
            } else if ("整经废纱单".equals(uniqueSign)) {
                String sFormId = "ad83c790c10949b28c9a3d27f1ed1f06";
                sJson = "{\"CreateOrgId\":" + fStockOrgID + ",\"Numbers\":[\"" + fBillNo + "\"]}";
                KingDeeUtil.Submit(sFormId, sJson);
                isAudit = KingDeeUtil.Audit(sFormId, sJson);
            } else if ("胚布出库-其他出库单".equals(uniqueSign) || "胚布厂区调出-其他出库单".equals(uniqueSign) ) {
                String sFormId = "STK_MisDelivery";
                KingDeeUtil.Save(sFormId, saveJson);
                KingDeeUtil.Submit(sFormId, sJson);
                isAudit = KingDeeUtil.Audit(sFormId, sJson);
            } else if ("发货转移单".equals(uniqueSign)) {
                String sFormId = "PAEZ_DeliveryTransferBill";
                KingDeeUtil.Save(sFormId, saveJson);
                KingDeeUtil.Submit(sFormId, sJson);
                isAudit = KingDeeUtil.Audit(sFormId, sJson);
            } else if ("收货汇报单".equals(uniqueSign)) {
                String sFormId = "PAEZ_ReceiptReportBill";
                KingDeeUtil.Save(sFormId, saveJson);
                KingDeeUtil.Submit(sFormId, sJson);
                isAudit = KingDeeUtil.Audit(sFormId, sJson);
            }else if("采购入库原料-立体仓".equals(uniqueSign)){
                String sFormId = "STK_InStock";
                KingDeeUtil.Submit(sFormId, sJson);
                isAudit = KingDeeUtil.Audit(sFormId, sJson);
            }else if("染纱出库".equals(uniqueSign)){
                String sFormId = "SAL_OUTSTOCK";
                KingDeeUtil.Save(sFormId, saveJson);
            }

        }

        if (!isAudit && tableName != null) {
            if (type == 98) {//TODO
//                GetDBDataUtil.upCommitStatus(tableName, fBillNo);
            }
        }
    }
}