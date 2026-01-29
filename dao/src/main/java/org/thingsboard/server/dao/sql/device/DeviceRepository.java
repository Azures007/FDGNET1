/**
 * Copyright © 2016-2021 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.dao.sql.device;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.DeviceTransportType;
import org.thingsboard.server.dao.model.sql.DeviceEntity;
import org.thingsboard.server.dao.model.sql.DeviceInfoEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Valerii Sosliuk on 5/6/2017.
 */
public interface DeviceRepository extends JpaRepository<DeviceEntity, UUID> {

    @Query("SELECT new org.thingsboard.server.dao.model.sql.DeviceInfoEntity(d, c.title, c.additionalInfo, p.name) " +
            "FROM DeviceEntity d " +
            "LEFT JOIN CustomerEntity c on c.id = d.customerId " +
            "LEFT JOIN DeviceProfileEntity p on p.id = d.deviceProfileId " +
            "WHERE d.id = :deviceId")
    DeviceInfoEntity findDeviceInfoById(@Param("deviceId") UUID deviceId);

    @Query("SELECT d FROM DeviceEntity d WHERE d.tenantId = :tenantId " +
            "AND d.customerId = :customerId " +
            "AND LOWER(d.searchText) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    Page<DeviceEntity> findByTenantIdAndCustomerId(@Param("tenantId") UUID tenantId,
                                                   @Param("customerId") UUID customerId,
                                                   @Param("searchText") String searchText,
                                                   Pageable pageable);

    @Query("SELECT d FROM DeviceEntity d WHERE d.tenantId = :tenantId " +
            "AND d.deviceProfileId = :profileId " +
            "AND LOWER(d.searchText) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    Page<DeviceEntity> findByTenantIdAndProfileId(@Param("tenantId") UUID tenantId,
                                                  @Param("profileId") UUID profileId,
                                                  @Param("searchText") String searchText,
                                                  Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.DeviceInfoEntity(d, c.title, c.additionalInfo, p.name) " +
            "FROM DeviceEntity d " +
            "LEFT JOIN CustomerEntity c on c.id = d.customerId " +
            "LEFT JOIN DeviceProfileEntity p on p.id = d.deviceProfileId " +
            "WHERE d.tenantId = :tenantId " +
            "AND d.customerId = :customerId " +
            "AND LOWER(d.searchText) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    Page<DeviceInfoEntity> findDeviceInfosByTenantIdAndCustomerId(@Param("tenantId") UUID tenantId,
                                                                  @Param("customerId") UUID customerId,
                                                                  @Param("searchText") String searchText,
                                                                  Pageable pageable);

    @Query("SELECT d FROM DeviceEntity d WHERE d.tenantId = :tenantId")
    Page<DeviceEntity> findByTenantId(@Param("tenantId") UUID tenantId,
                                      Pageable pageable);

    @Query("SELECT d FROM DeviceEntity d WHERE d.tenantId = :tenantId " +
            "AND LOWER(d.searchText) LIKE LOWER(CONCAT('%', :textSearch, '%'))")
    Page<DeviceEntity> findByTenantId(@Param("tenantId") UUID tenantId,
                                      @Param("textSearch") String textSearch,
                                      Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.DeviceInfoEntity(d, c.title, c.additionalInfo, p.name) " +
            "FROM DeviceEntity d " +
            "LEFT JOIN CustomerEntity c on c.id = d.customerId " +
            "LEFT JOIN DeviceProfileEntity p on p.id = d.deviceProfileId " +
            "WHERE d.tenantId = :tenantId " +
            "AND LOWER(d.searchText) LIKE LOWER(CONCAT('%', :textSearch, '%'))")
    Page<DeviceInfoEntity> findDeviceInfosByTenantId(@Param("tenantId") UUID tenantId,
                                                     @Param("textSearch") String textSearch,
                                                     Pageable pageable);

    @Query("SELECT d FROM DeviceEntity d WHERE d.tenantId = :tenantId " +
            "AND d.type = :type " +
            "AND LOWER(d.searchText) LIKE LOWER(CONCAT('%', :textSearch, '%'))")
    Page<DeviceEntity> findByTenantIdAndType(@Param("tenantId") UUID tenantId,
                                             @Param("type") String type,
                                             @Param("textSearch") String textSearch,
                                             Pageable pageable);

    @Query("SELECT d FROM DeviceEntity d WHERE d.tenantId = :tenantId " +
            "AND d.deviceProfileId = :deviceProfileId " +
            "AND d.firmwareId = null " +
            "AND LOWER(d.searchText) LIKE LOWER(CONCAT('%', :textSearch, '%'))")
    Page<DeviceEntity> findByTenantIdAndTypeAndFirmwareIdIsNull(@Param("tenantId") UUID tenantId,
                                             @Param("deviceProfileId") UUID deviceProfileId,
                                             @Param("textSearch") String textSearch,
                                             Pageable pageable);

    @Query("SELECT d FROM DeviceEntity d WHERE d.tenantId = :tenantId " +
            "AND d.deviceProfileId = :deviceProfileId " +
            "AND d.softwareId = null " +
            "AND LOWER(d.searchText) LIKE LOWER(CONCAT('%', :textSearch, '%'))")
    Page<DeviceEntity> findByTenantIdAndTypeAndSoftwareIdIsNull(@Param("tenantId") UUID tenantId,
                                                                @Param("deviceProfileId") UUID deviceProfileId,
                                                                @Param("textSearch") String textSearch,
                                                                Pageable pageable);

    @Query("SELECT count(*) FROM DeviceEntity d WHERE d.tenantId = :tenantId " +
            "AND d.deviceProfileId = :deviceProfileId " +
            "AND d.firmwareId = null")
    Long countByTenantIdAndDeviceProfileIdAndFirmwareIdIsNull(@Param("tenantId") UUID tenantId,
                                                              @Param("deviceProfileId") UUID deviceProfileId);

    @Query("SELECT count(*) FROM DeviceEntity d WHERE d.tenantId = :tenantId " +
            "AND d.deviceProfileId = :deviceProfileId " +
            "AND d.softwareId = null")
    Long countByTenantIdAndDeviceProfileIdAndSoftwareIdIsNull(@Param("tenantId") UUID tenantId,
                                                              @Param("deviceProfileId") UUID deviceProfileId);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.DeviceInfoEntity(d, c.title, c.additionalInfo, p.name) " +
            "FROM DeviceEntity d " +
            "LEFT JOIN CustomerEntity c on c.id = d.customerId " +
            "LEFT JOIN DeviceProfileEntity p on p.id = d.deviceProfileId " +
            "WHERE d.tenantId = :tenantId " +
            "AND d.type = :type " +
            "AND LOWER(d.searchText) LIKE LOWER(CONCAT('%', :textSearch, '%'))")
    Page<DeviceInfoEntity> findDeviceInfosByTenantIdAndType(@Param("tenantId") UUID tenantId,
                                                            @Param("type") String type,
                                                            @Param("textSearch") String textSearch,
                                                            Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.DeviceInfoEntity(d, c.title, c.additionalInfo, p.name) " +
            "FROM DeviceEntity d " +
            "LEFT JOIN CustomerEntity c on c.id = d.customerId " +
            "LEFT JOIN DeviceProfileEntity p on p.id = d.deviceProfileId " +
            "WHERE d.tenantId = :tenantId " +
            "AND d.deviceProfileId = :deviceProfileId " +
            "AND LOWER(d.searchText) LIKE LOWER(CONCAT('%', :textSearch, '%'))")
    Page<DeviceInfoEntity> findDeviceInfosByTenantIdAndDeviceProfileId(@Param("tenantId") UUID tenantId,
                                                                       @Param("deviceProfileId") UUID deviceProfileId,
                                                                       @Param("textSearch") String textSearch,
                                                                       Pageable pageable);

    @Query("SELECT d FROM DeviceEntity d WHERE d.tenantId = :tenantId " +
            "AND d.customerId = :customerId " +
            "AND d.type = :type " +
            "AND LOWER(d.searchText) LIKE LOWER(CONCAT('%', :textSearch, '%'))")
    Page<DeviceEntity> findByTenantIdAndCustomerIdAndType(@Param("tenantId") UUID tenantId,
                                                          @Param("customerId") UUID customerId,
                                                          @Param("type") String type,
                                                          @Param("textSearch") String textSearch,
                                                          Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.DeviceInfoEntity(d, c.title, c.additionalInfo, p.name) " +
            "FROM DeviceEntity d " +
            "LEFT JOIN CustomerEntity c on c.id = d.customerId " +
            "LEFT JOIN DeviceProfileEntity p on p.id = d.deviceProfileId " +
            "WHERE d.tenantId = :tenantId " +
            "AND d.customerId = :customerId " +
            "AND d.type = :type " +
            "AND LOWER(d.searchText) LIKE LOWER(CONCAT('%', :textSearch, '%'))")
    Page<DeviceInfoEntity> findDeviceInfosByTenantIdAndCustomerIdAndType(@Param("tenantId") UUID tenantId,
                                                                         @Param("customerId") UUID customerId,
                                                                         @Param("type") String type,
                                                                         @Param("textSearch") String textSearch,
                                                                         Pageable pageable);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.DeviceInfoEntity(d, c.title, c.additionalInfo, p.name) " +
            "FROM DeviceEntity d " +
            "LEFT JOIN CustomerEntity c on c.id = d.customerId " +
            "LEFT JOIN DeviceProfileEntity p on p.id = d.deviceProfileId " +
            "WHERE d.tenantId = :tenantId " +
            "AND d.customerId = :customerId " +
            "AND d.deviceProfileId = :deviceProfileId " +
            "AND LOWER(d.searchText) LIKE LOWER(CONCAT('%', :textSearch, '%'))")
    Page<DeviceInfoEntity> findDeviceInfosByTenantIdAndCustomerIdAndDeviceProfileId(@Param("tenantId") UUID tenantId,
                                                                                    @Param("customerId") UUID customerId,
                                                                                    @Param("deviceProfileId") UUID deviceProfileId,
                                                                                    @Param("textSearch") String textSearch,
                                                                                    Pageable pageable);

    @Query("SELECT DISTINCT d.type FROM DeviceEntity d WHERE d.tenantId = :tenantId")
    List<String> findTenantDeviceTypes(@Param("tenantId") UUID tenantId);

    DeviceEntity findByTenantIdAndName(UUID tenantId, String name);

    List<DeviceEntity> findDevicesByTenantIdAndCustomerIdAndIdIn(UUID tenantId, UUID customerId, List<UUID> deviceIds);

    List<DeviceEntity> findDevicesByTenantIdAndIdIn(UUID tenantId, List<UUID> deviceIds);

    DeviceEntity findByTenantIdAndId(UUID tenantId, UUID id);

    Long countByDeviceProfileId(UUID deviceProfileId);

    @Query("SELECT d FROM DeviceEntity d, RelationEntity re WHERE d.tenantId = :tenantId " +
            "AND d.id = re.toId AND re.toType = 'DEVICE' AND re.relationTypeGroup = 'EDGE' " +
            "AND re.relationType = 'Contains' AND re.fromId = :edgeId AND re.fromType = 'EDGE' " +
            "AND LOWER(d.searchText) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    Page<DeviceEntity> findByTenantIdAndEdgeId(@Param("tenantId") UUID tenantId,
                                               @Param("edgeId") UUID edgeId,
                                               @Param("searchText") String searchText,
                                               Pageable pageable);

    @Query("SELECT d FROM DeviceEntity d, RelationEntity re WHERE d.tenantId = :tenantId " +
            "AND d.id = re.toId AND re.toType = 'DEVICE' AND re.relationTypeGroup = 'EDGE' " +
            "AND re.relationType = 'Contains' AND re.fromId = :edgeId AND re.fromType = 'EDGE' " +
            "AND d.type = :type " +
            "AND LOWER(d.searchText) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    Page<DeviceEntity> findByTenantIdAndEdgeIdAndType(@Param("tenantId") UUID tenantId,
                                                      @Param("edgeId") UUID edgeId,
                                                      @Param("type") String type,
                                                      @Param("searchText") String searchText,
                                                      Pageable pageable);

    /**
     * Count devices by tenantId.
     * Custom query applied because default QueryDSL produces slow count(id).
     * <p>
     * There is two way to count devices.
     * OPTIMAL: count(*)
     *   - returns _row_count_ and use index-only scan (super fast).
     * SLOW: count(id)
     *   - returns _NON_NULL_id_count and performs table scan to verify isNull for each id in filtered rows.
     * */
    @Query("SELECT count(*) FROM DeviceEntity d WHERE d.tenantId = :tenantId")
    Long countByTenantId(@Param("tenantId") UUID tenantId);

    @Query("SELECT d.id FROM DeviceEntity d " +
            "INNER JOIN DeviceProfileEntity p ON d.deviceProfileId = p.id " +
            "WHERE p.transportType = :transportType")
    Page<UUID> findIdsByDeviceProfileTransportType(@Param("transportType") DeviceTransportType transportType, Pageable pageable);

    @Query(value = "select name,label from device where name like %?1% order by name asc ",nativeQuery = true)
    List<Map> findLikeName(String insourcing);

    /**
     * 统计设备上报数据
     */
    @Query(value = "select COALESCE(SUM(COALESCE(a.long_v, a.dbl_v)), 0) from ts_kv a \n" +
            "join device b on a.entity_id =b.id \n" +
            "join ts_kv_dictionary c on a.\"key\" =c.key_id \n" +
            "where c.\"key\" =?2 and b.name=?1 and a.ts between ?3 and ?4 ",nativeQuery = true)
    BigDecimal sumQtyByMykey(String name, String key,Long dateFront,Long dateLater);

    /**
     * 取最大设备上报数据
     */
    @Query(value = "select COALESCE(MAX(COALESCE(a.long_v, a.dbl_v)), 0) from ts_kv a \n" +
            "join device b on a.entity_id =b.id \n" +
            "join ts_kv_dictionary c on a.\"key\" =c.key_id \n" +
            "where c.\"key\" =?2 and b.name=?1 and a.ts between ?3 and ?4 ",nativeQuery = true)
    BigDecimal maxQtyByMykey(String name, String key,Long dateFront,Long dateLater);

    /**
     * 取最小设备上报数据
     */
    @Query(value = "select COALESCE(MIN(COALESCE(a.long_v, a.dbl_v)), 0) from ts_kv a \n" +
            "join device b on a.entity_id =b.id \n" +
            "join ts_kv_dictionary c on a.\"key\" =c.key_id \n" +
            "where c.\"key\" =?2 and b.name=?1 and a.ts between ?3 and ?4 ",nativeQuery = true)
    BigDecimal minQtyByMykey(String name, String key,Long dateFront,Long dateLater);

    /**
     * 取平均设备上报数据
     */
    @Query(value = "select COALESCE(AVG (COALESCE(a.long_v, a.dbl_v)), 0) from ts_kv a \n" +
            "join device b on a.entity_id =b.id \n" +
            "join ts_kv_dictionary c on a.\"key\" =c.key_id \n" +
            "where c.\"key\" =?2 and (COALESCE(a.long_v, a.dbl_v) <> 0) and b.name=?1 and a.ts between ?3 and ?4 ",nativeQuery = true)
    BigDecimal avgQtyByMykey(String name, String key,Long dateFront,Long dateLater);

    /**
     * 统计设备上报数据
     */
    @Query(value = "select count(*) from ts_kv a \n" +
            "join device b on a.entity_id =b.id \n" +
            "join ts_kv_dictionary c on a.\"key\" =c.key_id \n" +
            "where c.\"key\" =?2 and b.name=?1 and a.ts between ?3 and ?4 ",nativeQuery = true)
    BigDecimal countQtyByMykey(String name, String key,Long dateFront,Long dateLater);

    /**
     * 超标次数
     * @param name
     * @param inTepmSize
     * @param byDateFrontTimes
     * @param byDateLaterTimes
     * @return
     */
    @Query(value = "select COUNT(1) from ts_kv a \n" +
            "join device b on a.entity_id =b.id \n" +
            "join ts_kv_dictionary c on a.\"key\" =c.key_id \n" +
            "where c.\"key\" =?2 and b.name=?1 and a.ts between ?4 and ?5 \n" +
            "  and (COALESCE(a.long_v, a.dbl_v) - ?3) > 0 ",nativeQuery = true)
    BigDecimal countByQty(String name, String key, BigDecimal inTepmSize, Long byDateFrontTimes, Long byDateLaterTimes);

    /**
     * long最近一小时速度
     * @param deviceCode
     * @param lastHourStartTimestamp
     * @param lastHourEndTimestamp
     * @return
     */
    @Query(value = "SELECT ts by_ts, COALESCE(long_v,0) by_qty," +
            "TO_CHAR(timezone('Asia/Shanghai', TO_TIMESTAMP(ts / 1000.0)), 'YYYY-MM-DD HH24:MI:SS') AS by_date\n" +
            "FROM (\n" +
            "    SELECT \n" +
            "        a.ts,\n" +
            "        a.dbl_v, \n "+
            "        a.long_v,\n" +
            "        LAG(a.long_v) OVER (ORDER BY a.ts) AS prev_long_v\n" +
            "    FROM ts_kv a \n" +
            "    JOIN device b ON a.entity_id = b.\"id\" \n" +
            "    JOIN ts_kv_dictionary c ON a.\"key\" = c.key_id\n" +
            "    WHERE \n" +
            "        b.\"name\" = ?1 \n" +
            "        AND c.\"key\" = ?4\n" +
            "        AND a.ts BETWEEN ?2 AND ?3\n" +
            ") sub\n" +
            "WHERE \n" +
            "    (prev_long_v IS NULL ) \n" +
            "    OR long_v <> prev_long_v  \n" +
            "ORDER BY ts; ",nativeQuery = true)
    List<Map> lineSellp(String deviceCode, long lastHourStartTimestamp, long lastHourEndTimestamp,String key);


    /**
     * 通过设备获取硬件上报数据
     */
    @Query(value = "select COALESCE(a.long_v, a.dbl_v) from ts_kv a " +
            "join device b on a.entity_id=b.id " +
            "join ts_kv_dictionary c on a.key=c.key_id " +
            "where b.name=?1 and a.ts=?2 and c.key=?3 ",nativeQuery = true)
    BigDecimal getKvByTSAndKey(String deviceCode, Long byTs,String key);


    /**
     * 内包机最近一小时速度
     * @param deviceCode
     * @param lastHourStartTimestamp
     * @param lastHourEndTimestamp
     * @return
     */
    @Query(value = "SELECT ts by_ts, COALESCE(dbl_v,0) by_qty," +
            "TO_CHAR(timezone('Asia/Shanghai', TO_TIMESTAMP(ts / 1000.0)), 'YYYY-MM-DD HH24:MI:SS') AS by_date\n" +
            "FROM (\n" +
            "    SELECT \n" +
            "        a.ts,\n" +
            "        a.dbl_v, \n "+
            "        a.long_v,\n" +
            "        LAG(a.dbl_v) OVER (ORDER BY a.ts) AS prev_dbl_v\n" +
            "    FROM ts_kv a \n" +
            "    JOIN device b ON a.entity_id = b.\"id\" \n" +
            "    JOIN ts_kv_dictionary c ON a.\"key\" = c.key_id\n" +
            "    WHERE \n" +
            "        b.\"name\" = ?1 \n" +
            "        AND c.\"key\" = ?4\n" +
            "        AND a.ts BETWEEN ?2 AND ?3\n" +
            ") sub\n" +
            "WHERE \n" +
            "    (prev_dbl_v IS NULL ) \n" +
            "    OR dbl_v <> prev_dbl_v  \n" +
            "ORDER BY ts; ",nativeQuery = true)
    List<Map> lineSellpByDb(String deviceCode, long lastHourStartTimestamp, long lastHourEndTimestamp,String key);

    /**
     * 通过设备名称模糊获取设备
     * @param deviceType
     * @return
     */
    @Query(value = "select name,label from device where name like %?1%",nativeQuery = true)
    List<Map> listDeviceIot(String deviceType);

    /**
     * 获取设备最新数据---通过设备名及键值
     * @param deviceName
     * @return
     */
    @Query(value = "select COALESCE(a.long_v, a.dbl_v) from ts_kv_latest a " +
            "join device b on a.entity_id=b.id  " +
            "join ts_kv_dictionary c on a.key=c.key_id " +
            "where b.name=?1 and c.key=?2",nativeQuery = true)
    BigDecimal listDeviceKvLatest(String deviceName,String key);

    /**
     * 异常列表
     * @param deviceCode
     * @param start
     * @param end
     * @param bigDecimal
     * @param bigDecimal1
     * @param bigDecimal2
     * @param bigDecimal3
     * @param bigDecimal4
     * @param bigDecimal5
     * @param bigDecimal6
     * @param bigDecimal7
     * @return
     */
    @Query(value = "SELECT\n" +
            "    CASE\n" +
            "        WHEN c.\"key\" = '一区上温度' AND a.dbl_v > ?4 THEN '一区上温度超最大值'\n" +
            "        WHEN c.\"key\" = '一区下温度' AND a.dbl_v > ?5 THEN '一区下温度超最大值'\n" +
            "        WHEN c.\"key\" = '二区上温度' AND a.dbl_v > ?6 THEN '二区上温度超最大值'\n" +
            "        WHEN c.\"key\" = '二区下温度' AND a.dbl_v > ?7 THEN '二区下温度超最大值'\n" +
            "        WHEN c.\"key\" = '三区上温度' AND a.dbl_v > ?8 THEN '三区上温度超最大值'\n" +
            "        WHEN c.\"key\" = '三区下温度' AND a.dbl_v > ?9 THEN '三区下温度超最大值'\n" +
            "        WHEN c.\"key\" = '四区上温度' AND a.dbl_v > ?10 THEN '四区上温度超最大值'\n" +
            "        WHEN c.\"key\" = '四区下温度' AND a.dbl_v > ?11 THEN '四区下温度超最大值'\n" +
            "        WHEN c.\"key\" = '安全报警' AND a.long_v > 0 THEN '安全报警'\n" +
            "        WHEN c.\"key\" = '超温报警' AND a.dbl_v > 0 THEN '超温报警'\n" +
            "        WHEN c.\"key\" = '电机报警' AND a.dbl_v > 0 THEN '电机报警'\n" +
            "        WHEN c.\"key\" = '故障警告' AND a.dbl_v > 0 THEN '故障警告'\n" +
            "        WHEN c.\"key\" = '系统报警' AND a.dbl_v > 0 THEN '系统报警'\n" +
            "    END AS by_qty,\n" +
            "    a.ts AS by_ts,\n" +
            "   TO_CHAR(timezone('Asia/Shanghai', TO_TIMESTAMP(ts / 1000.0)), 'YYYY-MM-DD HH24:MI:SS') AS by_date " +
            "FROM\n" +
            "    public.ts_kv a\n" +
            "JOIN\n" +
            "    public.device b ON a.entity_id = b.id\n" +
            "JOIN\n" +
            "    public.ts_kv_dictionary c ON a.\"key\" = c.key_id\n" +
            "WHERE\n" +
            "    b.\"name\" = ?1 \n" +
            "    AND c.\"key\" IN (\n" +
            "        '一区上温度','一区下温度',\n" +
            "        '二区上温度','二区下温度',\n" +
            "        '三区上温度','三区下温度',\n" +
            "        '四区上温度','四区下温度',\n" +
            "        '系统报警','安全报警',\n" +
            "        '超温报警','电机报警','故障报警'\n" +
            "    )\n" +
            "    AND a.ts BETWEEN ?2 AND ?3\n" +
            "    AND a.dbl_v IS NOT NULL\n" +
            "    AND (\n" +
            "        (c.\"key\" = '一区上温度' AND a.dbl_v > ?4)\n" +
            "        OR (c.\"key\" = '一区下温度' AND a.dbl_v > ?5)\n" +
            "        OR (c.\"key\" = '二区上温度' AND a.dbl_v > ?6)\n" +
            "        OR (c.\"key\" = '二区下温度' AND a.dbl_v > ?7)\n" +
            "        OR (c.\"key\" = '三区上温度' AND a.dbl_v > ?8)\n" +
            "        OR (c.\"key\" = '三区下温度' AND a.dbl_v > ?9)\n" +
            "        OR (c.\"key\" = '四区上温度' AND a.dbl_v > ?10)\n" +
            "        OR (c.\"key\" = '四区下温度' AND a.dbl_v > ?11)\n" +
            "        OR (c.\"key\" = '系统报警' AND a.long_v > 0)\n" +
            "        OR (c.\"key\" = '故障警告' AND a.long_v > 0)\n" +
            "        OR (c.\"key\" = '安全报警' AND a.long_v > 0)\n" +
            "        OR (c.\"key\" = '超温报警' AND a.long_v > 0)\n" +
            "        OR (c.\"key\" = '电机报警' AND a.long_v > 0)\n" +
            "    )\n" +
            "ORDER BY\n" +
            "    a.ts DESC",nativeQuery = true)
    List<Map> getErrorDatas(String deviceCode, long start, long end, BigDecimal bigDecimal, BigDecimal bigDecimal1, BigDecimal bigDecimal2, BigDecimal bigDecimal3, BigDecimal bigDecimal4, BigDecimal bigDecimal5, BigDecimal bigDecimal6, BigDecimal bigDecimal7);

//    @Query(value = "select GREATEST( \n" +
//            "    COUNT(COALESCE(a.long_v, a.dbl_v)) - ?3,     0 ) from ts_kv a \n" +
//            "join device b on a.entity_id =b.id \n" +
//            "join ts_kv_dictionary c on a.\"key\" =c.key_id \n" +
//            "where c.\"key\" =?2 and b.name=?1 and a.ts between ?4 and ?5 ",nativeQuery = true)
//    BigDecimal countByQty(String name, String key, BigDecimal inTepmSize, Long byDateFrontTimes, Long byDateLaterTimes);
}
