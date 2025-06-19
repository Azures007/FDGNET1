package org.thingsboard.server.dao.constant;

/**
 * @author lik
 * @version V1.0
 * @Package org.thingsboard.server.dao.constant
 * @date 2022/4/13 14:12
 * @Description:
 */

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.thingsboard.server.common.data.ReadWriteHashMap;
import org.thingsboard.server.common.data.TSysCodeDsc;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.security.Authority;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 全局常量类
 */
@Slf4j
public class GlobalConstant {

    public static Lock lock = new ReentrantLock();
    //启用
    public final static String enableTrue = "1";
    //禁用
    public final static String enableFalse = "0";

    //字典全局映射类
    private static Table<String, String, String> CODE_DES_TABLE = null;
    //租户标识
    public static String TENANT_ID = "dc623fb0-baeb-11ec-af69-872632e755c8";
    //CUSTOMER 标识
    public static String CUSTOMER = "263874d0-bc5c-11ec-b9ff-d18292d42636";
    // 用户信息
    public static ReadWriteHashMap readWriteHashMap = new ReadWriteHashMap();
    //redis登陆存放key头
    public final static String LOGIO_REDIS_KEY = "user:login:";
    //token 头部
    public final static String TOKEN_HEARHER = "Bearer ";
    //tokenkey
    public final static String TOKEN_KEY = "X-Authorization";
    //redis消息存放key头
    public final static String MES_REDIS_KEY = "user:redis:";
    //静态文件位置
    public final static String ROLE_CODE_SEQUENCE = "/static/GlobalFile.json";
    //订单同步文件
    public final static String ORDER_SYNC_JSON = "/static/OrderSync.json";
    //订单状态同步文件
    public final static String ORDER_STATUS_SYNC_JSON = "/static/OrderStatusSync.json";
    //磨具文件
    public final static String GRINDING_APPARATUS = "/static/GrindingApparatus.json";
    //物料表同步文件
    public final static String MID_MATERIAL_SYNC_JSON = "/static/MidMaterialSync.json";
    //组织表同步文件
    public final static String MID_ORG_SYNC_JSON = "/static/MidOrgSync.json";
    //组织表同步文件
    public final static String MID_DEPT_SYNC_JSON = "/static/MidDeptSync.json";
    //存放自动生成角色code前缀
    public final static String ROLE_CODE_KEY = "JSBM-";
    //默认角色id
    public final static Integer ROLE_DEFAULT_ID = 99999;

    static {
        String readJsonFile = readJsonFile(currentPath() + ROLE_CODE_SEQUENCE);
        log.info("-------------------------------");
        log.info("开始初始化静态文件" + ROLE_CODE_SEQUENCE);
        log.info("-------------------------------");
        if (StringUtils.isBlank(readJsonFile)) {
            Map<String, Object> map = new HashMap<>();
            map.put("roleCodeSequence", "1000");
            writeJson(currentPath() + ROLE_CODE_SEQUENCE, map, false);
        }
    }

    /**
     * 添加用户模型生成类
     *
     * @return
     */
    public static Map<String, Object> createAddUserMap() {
        //创建用户类
        HashMap<String, Object> ADD_USER_MAP;
        ADD_USER_MAP = new HashMap<>();
        HashMap<String, Object> additionalInfoMap = new HashMap<>();
        additionalInfoMap.put("defaultDashboardFullscreen", false);
        additionalInfoMap.put("defaultDashboardId", null);
        additionalInfoMap.put("description", "");
        additionalInfoMap.put("homeDashboardHideToolbar", true);
        additionalInfoMap.put("homeDashboardId", null);
        ADD_USER_MAP.put("additionalInfo", additionalInfoMap);
        ADD_USER_MAP.put("authority", Authority.TENANT_ADMIN);
        Map<String, Object> customerIdMap = new HashMap<>();
        customerIdMap.put("entityType", "CUSTOMER");
        customerIdMap.put("id", CUSTOMER);
        ADD_USER_MAP.put("customerId", customerIdMap);
        Map<String, Object> tenantIdMap = new HashMap<>();
        tenantIdMap.put("entityType", "TENANT");
        tenantIdMap.put("id", TENANT_ID);
        ADD_USER_MAP.put("tenantId", tenantIdMap);
        return ADD_USER_MAP;
    }

    /**
     * 设置缓存字典
     *
     * @param codeDscTable
     */
    public synchronized static void setCodeDscTable(List<TSysCodeDsc> codeDscTable) {
        CODE_DES_TABLE = HashBasedTable.create();
        codeDscTable.stream().forEach(codeDsc -> {
            CODE_DES_TABLE.put(codeDsc.getCodeClId(), codeDsc.getCodeValue(), codeDsc.getCodeDsc());
        });
    }

    /**
     * 获取缓存字典值
     *
     * @param code
     * @param key
     * @return
     */
    public static String getCodeDscName(String code, String key) {
        if (key == null || code == null) {
            return null;
        }
        if (CODE_DES_TABLE == null || CODE_DES_TABLE.size() == 0) {
            return null;
        } else {
            return CODE_DES_TABLE.get(code, key) == null ? null : CODE_DES_TABLE.get(code, key);
        }
    }

    public static User createUser() throws JsonProcessingException {
        User user = new User();
        HashMap<String, Object> additionalInfoMap = new HashMap<>();
        additionalInfoMap.put("defaultDashboardFullscreen", false);
        additionalInfoMap.put("defaultDashboardId", null);
        additionalInfoMap.put("description", "");
        additionalInfoMap.put("homeDashboardHideToolbar", true);
        additionalInfoMap.put("homeDashboardId", null);
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(additionalInfoMap));
        user.setAdditionalInfo(jsonNode);
        UUID uuid = UUID.fromString(TENANT_ID);
        user.setTenantId(new TenantId(uuid));
        user.setAuthority(Authority.CUSTOMER_USER);
        user.setCustomerId(new CustomerId(UUID.fromString(CUSTOMER)));
        return user;
    }

    /**
     * 获取自增的角色code
     *
     * @return
     */
    public static String getRoleCode(Boolean flag) {
        String roleCode = null;
        lock.lock();
        try {
            String roleCodeStr = readJsonFile(currentPath() + ROLE_CODE_SEQUENCE);
            Map roleCodeMap = JSON.parseObject(roleCodeStr, Map.class);
            roleCode = String.valueOf(roleCodeMap.get("roleCodeSequence"));
            Integer roleCodeInt = Integer.valueOf(roleCode);
            Random random = new Random();
            if (flag) {
                roleCodeInt = roleCodeInt + random.nextInt(100) + 1;
            }
            String newRoleCode = String.valueOf(roleCodeInt + 1);
            roleCodeMap.put("roleCodeSequence", newRoleCode);
            writeJson(currentPath() + ROLE_CODE_SEQUENCE, roleCodeMap, false);
            roleCode = roleCodeInt.toString();
        } finally {
            lock.unlock();
        }
        if (StringUtils.isBlank(roleCode)) {
            throw new RuntimeException("没有初始化静态文件:" + ROLE_CODE_SEQUENCE);
        }
        return ROLE_CODE_KEY + roleCode;
    }


    /**
     * 往json文件中写入数据
     *
     * @param jsonPath json文件路径
     * @param inMap    Map类型数据
     * @param flag     写入状态，true表示在文件中追加数据，false表示覆盖文件数据
     * @return 写入文件状态  成功或失败
     */
    private static String writeJson(String jsonPath, Map<String, Object> inMap, boolean flag) {
        // Map数据转化为Json，再转换为String
        String data = JSON.toJSONString(inMap);
        File jsonFile = new File(jsonPath);
        try {
            // 文件不存在就创建文件
            if (!jsonFile.exists()) {
                jsonFile.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(jsonFile.getAbsoluteFile(), flag);
            BufferedWriter bw = new BufferedWriter(fileWriter);
            bw.write(data);
            bw.close();
            return "success";
        } catch (IOException e) {
            return "error";
        }
    }

    /**
     * 读取json文件
     *
     * @param fileName
     * @return
     */
    public static String readJsonFile(String fileName) {
        log.info("--------开始读取json文件：" + fileName);
        String jsonStr = "";
        try {
            File jsonFile = new File(fileName);
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), "utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            log.info("--------读取json文件：" + fileName + "完成");
            return jsonStr;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 获取jar包所在位置
     *
     * @return
     */
    public static String currentPath() {
        File dir = new File(".");
        String currentpath = "";
        try {
            currentpath = dir.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return currentpath;
    }

    /**
     * 获取磨具列表
     *
     * @return
     */
    public static List<Map> listGrindingApparatus() {
        String readJsonFile = readJsonFile(currentPath() + GRINDING_APPARATUS);
        if (StringUtils.isNotBlank(readJsonFile)) {
            Map maps1 = JSON.parseObject(readJsonFile, Map.class);
            List<Map> maps = JSON.parseArray(JSON.toJSONString(maps1.get("GrindingApparatusList")), Map.class);
            return maps;
        } else {
            throw new RuntimeException("未初始化磨具文件");
        }
    }
}
