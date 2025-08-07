/**
 * Copyright © 2016-2021 The Thingsboard Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.common.data.mes;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hhh
 */
public class LichengConstants {

    /*操作分组编码*/
    public static Map<Integer, String> PPBOM_GROUP_NUMBER_MAP = new HashMap<>();

    //小料
    public static int PPBOM_GROUP_NUMBER_1 = 1;
    //大料
    public static int PPBOM_GROUP_NUMBER_2 = 2;
    //香精
    public static int PPBOM_GROUP_NUMBER_3 = 3;
    //拌料
    public static int PPBOM_GROUP_NUMBER_4 = 4;
    //切片
    public static int PPBOM_GROUP_NUMBER_5 = 5;
    //斩拌
    public static int PPBOM_GROUP_NUMBER_6 = 6;

    static {
        PPBOM_GROUP_NUMBER_MAP.put(PPBOM_GROUP_NUMBER_1, "小料");
        PPBOM_GROUP_NUMBER_MAP.put(PPBOM_GROUP_NUMBER_2, "大料");
        PPBOM_GROUP_NUMBER_MAP.put(PPBOM_GROUP_NUMBER_3, "香精");
        PPBOM_GROUP_NUMBER_MAP.put(PPBOM_GROUP_NUMBER_4, "拌料");
        PPBOM_GROUP_NUMBER_MAP.put(PPBOM_GROUP_NUMBER_5, "切片");
        PPBOM_GROUP_NUMBER_MAP.put(PPBOM_GROUP_NUMBER_6, "斩拌");
    }

    /* 任务列表标记*/
    //今日任务
    public static final String TASK_TYPE_1 = "1";
    //待生产
    public static final String TASK_TYPE_2 = "2";
    //生产中
    public static final String TASK_TYPE_3 = "3";
    //未生产
    public static final String TASK_TYPE_4 = "4";
    //已完工
    public static final String TASK_TYPE_5 = "5";
    //移交待生产
    public static final String TASK_TYPE_6 = "6";
    //移交生产中
    public static final String TASK_TYPE_7 = "7";
    //明日任务
    public static final String TASK_TYPE_8 = "8";
    public static final String TASK_TYPE_9 = "9";


    /* 工序编码 */
    //斩拌
    public static final String PROCESS_NUMBER_ZHANBAN = "GX001";
    //蟹柳
    public static final String PROCESS_NUMBER_XIELIU = "GX002";
    //剥皮
    public static final String PROCESS_NUMBER_BOPI = "GX003";
    //拌料
    public static final String PROCESS_NUMBER_BANLIAO = "GX004";
    //拉伸膜
    public static final String PROCESS_NUMBER_LASHENMO = "GX005";
    //包装
    public static final String PROCESS_NUMBER_BAOZHUANG = "GX006";
    //乳化浆
    public static final String PROCESS_NUMBER_RUHUAJIANG = "GX007";
    //蟹柳色素
    public static final String PROCESS_NUMBER_XIELIUSESU = "GX008";

    /* ERP工序标识，维护在字典(字典分组=ERPPROCESS0000) */
    //斩拌
    public static final String ERP_PROCESS_NUMBER_ZHANBAN = "ZB";
    //蟹柳
    public static final String ERP_PROCESS_NUMBER_XIELIU = "XL";
    //剥皮
    public static final String ERP_PROCESS_NUMBER_BOPI = "BP";
    //拌料
    public static final String ERP_PROCESS_NUMBER_BANLIAO = "BL";
    //拉伸膜
    public static final String ERP_PROCESS_NUMBER_LASHENMO = "LSM";
    //包装
    public static final String ERP_PROCESS_NUMBER_BAOZHUANG = "BZ";
    //乳化浆
    public static final String ERP_PROCESS_NUMBER_RUHUAJIANG = "RHJ";
    //蟹柳色素
    public static final String ERP_PROCESS_NUMBER_XIELIUSESU = "XLSS";

    //MES工序编码,ERP工序标识
    public static final HashMap<String, String> ERP_PROCESS_NUMBER_MAP = new HashMap<String, String>() {
        {
            put(PROCESS_NUMBER_ZHANBAN, ERP_PROCESS_NUMBER_ZHANBAN);
            put(PROCESS_NUMBER_XIELIU, ERP_PROCESS_NUMBER_XIELIU);
            put(PROCESS_NUMBER_BOPI, ERP_PROCESS_NUMBER_BOPI);
            put(PROCESS_NUMBER_BANLIAO, ERP_PROCESS_NUMBER_BANLIAO);
            put(PROCESS_NUMBER_LASHENMO, ERP_PROCESS_NUMBER_LASHENMO);
            put(PROCESS_NUMBER_BAOZHUANG, ERP_PROCESS_NUMBER_BAOZHUANG);
            put(PROCESS_NUMBER_RUHUAJIANG, ERP_PROCESS_NUMBER_RUHUAJIANG);
            put(PROCESS_NUMBER_XIELIUSESU, ERP_PROCESS_NUMBER_XIELIUSESU);
        }
    };

    /*订单状态：0=未开工、1=已开工、2=暂停、3=已完工、4=已挂起*/
    //未开工
    public static final String ORDERSTATUS_0 = "0";
    //已开工
    public static final String ORDERSTATUS_1 = "1";
    //暂停
    public static final String ORDERSTATUS_2 = "2";
    //已完工
    public static final String ORDERSTATUS_3 = "3";
    //已挂起
    public static final String ORDERSTATUS_4 = "4";

    /*工序状态：0=未开工、1=已开工、2=暂停、3=已完工、4=已挂起*/
    //未开工
    public static final String PROCESSSTATUS_0 = "0";
    //已开工
    public static final String PROCESSSTATUS_1 = "1";
    //暂停
    public static final String PROCESSSTATUS_2 = "2";
    //已完工
    public static final String PROCESSSTATUS_3 = "3";
    //移交中
    public static final String PROCESSSTATUS_4 = "4";
    //移交驳回
    public static final String PROCESSSTATUS_5 = "5";

    //扫码类型:报工扫码=BIND0001,接单扫码=BIND0002
    public static final String BIND0001 = "BIND0001";
    public static final String BIND0002 = "BIND0002";

    //二级类目类型：AB料报工（原二级品）
    public static final String RECORDTYPEL20001 = "A料";
    public static final String RECORDTYPEL20002 = "B料";
    public static final String RECORDTYPEL20003 = "废料";
    public static final String RECORDTYPEL20004 = "次品浆";
    //二级类目类型：AB料报工（原二级品）编码
    public static final String RECORDTYPEL20001_NUMBER = "RECORDTYPEL20001";
    public static final String RECORDTYPEL20002_NUMBER = "RECORDTYPEL20002";
    public static final String RECORDTYPEL20003_NUMBER = "RECORDTYPEL20003";
    public static final String RECORDTYPEL20004_NUMBER = "RECORDTYPEL20004";

    //二级类目类型：其他
    public static final String RECORDTYPEL20000_1 = "废膜";
    public static final String RECORDTYPEL20000_2 = "剩余膜";
    public static final String RECORDTYPEL20000_3 = "袋装";
    public static final String RECORDTYPEL20000_4 = "桶装";
    public static final String RECORDTYPEL20000_5 = "使用膜";


    //单位
    public static final String UNIT_KG = "kg";
    public static final String UNIT_KG_NAME = "千克";
    public static final String UNIT_G = "g";
    public static final String UNIT_G_NAME = "克";
    public static final String UNIT_GUO = "guo";
    public static final String UNIT_GUO_NAME = "锅";
    public static final String UNIT_ZHI = "zhi";
    public static final String UNIT_ZHI_NAME = "支";
    public static final String UNIT_JIAN= "jian";
    public static final String UNIT_JIAN_NAME = "件";

    //订单挂起备注
    public static final String ORDER_PENDING_DESC_1 = "挂起无效";
    public static final String ORDER_PENDING_DESC_2 = "挂起取消";

    //子项类型
    public static final String ORDER_PPBOM_ITEM_TYPE_NAME_1 = "标准件";
    public static final String ORDER_PPBOM_ITEM_TYPE_NAME_2 = "返还件";
    public static final String ORDER_PPBOM_ITEM_TYPE_NAME_3 = "替代件";

    //子项类型编码
    public static final int ORDER_PPBOM_ITEM_TYPE_NAME_VALUE_1 = 1;
    public static final int ORDER_PPBOM_ITEM_TYPE_NAME_VALUE_2 = 2;
    public static final int ORDER_PPBOM_ITEM_TYPE_NAME_VALUE_3 = 3;

    //工序执行表的类型：1=正常订单、移交订单、转移订单
    public static final String ORDER_PROCESS_TYPE_1 = "1";//正常订单
    public static final String ORDER_PROCESS_TYPE_2 = "2";//移交订单（班别移交）
    public static final String ORDER_PROCESS_TYPE_3 = "3";//转移订单（订单转移）

    //盘点类型:交接班盘点=STOCKTAKING0001，订单完工盘点=STOCKTAKING0002
    public static final String STOCKTAKING0001 = "STOCKTAKING0001";//交接班盘点
    public static final String STOCKTAKING0002 = "STOCKTAKING0002";//订单完工盘点
    public static final String STOCKTAKING0003 = "STOCKTAKING0003";//中途完工盘点

    //报工类型:REPORTYPE0001=正常，REPORTYPE0002=尾料
    public static final String REPORTYPE0001 = "REPORTYPE0001";//正常
    public static final String REPORTYPE0002 = "REPORTYPE0002";//尾料

    //类目类型（类目编码）:1=原辅料，2=二级品数量、3=产后数量、4=自定义报工、5=投入前道数量、6=AB料投入
    public static final String ORDER_RECORD_TYPE_1 = "1";
    public static final String ORDER_RECORD_TYPE_2 = "2";
    public static final String ORDER_RECORD_TYPE_3 = "3";
    public static final String ORDER_RECORD_TYPE_4 = "4";
    public static final String ORDER_RECORD_TYPE_5 = "5";
    public static final String ORDER_RECORD_TYPE_6 = "6";

    //业务类型：报工、盘点
    public static final String ORDER_BUS_TYPE_BG = "BG";
    public static final String ORDER_BUS_TYPE_PD = "PD";

    //赋值报工表的物料名称，用于报工记录的显示
    public static final String ORDER_REPORT_MATERIAL_NAME_2 = "二级品报工";
    public static final String ORDER_REPORT_MATERIAL_NAME_3 = "产后统计";
    public static final String ORDER_REPORT_MATERIAL_NAME_5 = "投入前道数量";

    //盘点状态：0普通盘点、1重新盘点，报工状态：0正常状态、1删除状态
    public static final String ORDER_PROCESS_HISTORY_STATUS_0 = "0";
    public static final String ORDER_PROCESS_HISTORY_STATUS_1 = "1";

    //报工满一锅的标记，0=未投满 1=已投满
    public static final Integer ORDER_PROCESS_IMPORT_POT_GROUP_0 = 0;
    public static final Integer ORDER_PROCESS_IMPORT_POT_GROUP_1 = 1;

    //匹配工艺路线:-1:空, 0: 不匹配, 1:匹配
    public static final String ORDER_HEAD_MATCHING__1 = "-1";
    public static final String ORDER_HEAD_MATCHING_0 = "0";
    public static final String ORDER_HEAD_MATCHING_1 = "1";
    //定时消息提醒
    public static final String CODE_DSC_PROCESSENDMESSAGE = "PROCESSENDMESSAGE";
    //工序结束超时提醒
    public static final String CODE_DSC_PROCESSENDMESSAGE_1 = "工序结束超时提醒";
    //数据字典是否启用:1
    public static final String CODE_DSC_ENABLEDST_1 = "1";
    //数据字典是否禁用用:0
    public static final String CODE_DSC_ENABLEDST_0 = "0";

    //统一是否删除: 0:非删除,1:删除
    public static final String IS_DELETED_0 = "0";

    public static final String IS_DELETED_1 = "1";

}
