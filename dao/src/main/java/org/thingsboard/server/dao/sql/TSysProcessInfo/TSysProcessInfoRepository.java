package org.thingsboard.server.dao.sql.TSysProcessInfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.TSysProcessInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Auther: l
 * @Date: 2022/4/21 10:19
 * @Description:
 */
public interface TSysProcessInfoRepository extends JpaRepository<TSysProcessInfo,Integer> {

    @Query(value = "select * from t_sys_process_info where process_id in ( \n" +
            " select DISTINCT c.process_id from t_bus_order_head a \n" +
            " left Join t_sys_craft_info b on a.craft_id=b.craft_id \n" +
            " left Join t_sys_craft_process_rel c on b.craft_id=c.craft_id \n" +
            " where a.mid_mo_sale_order_no=:midMoSaleOrderNo \n" +
            " )",nativeQuery = true)
    List<TSysProcessInfo> getProcessInfos(@Param("midMoSaleOrderNo") String midMoSaleOrderNo);

    /**
     * @param processSet
     * @return
     */
    @Query(value = "select process_id,process_name from t_sys_process_info where process_id in (?1) group by process_id,process_name",nativeQuery = true)
    Set<Map<String,Object>> listBySetIds(Set<Integer> processSet);

    TSysProcessInfo findByProcessId(Integer processId);

    // 废弃方法
//    TSysProcessInfo getByProcessNumber(String processNumber);

    List<TSysProcessInfo> findByProcessNumber(String processNumber);

    /**
     * 通过工序执行id获取工序编码
     * @param orderProcessId
     * @return
     */
    @Query(value = "select b.process_number from t_bus_order_process a \n" +
            "join t_sys_process_info b on a.process_id =b.process_id \n" +
            "where a.order_process_id =?1",nativeQuery = true)
    String findProcessNumberByOrderProcessId(Integer orderProcessId);
    /**
     * 通过工序工序名称获取enable=1的工序，如果有多个取第一个
     * @param processName
     * @return
     */
    @Query(value = "select * from t_sys_process_info where process_name=?1 and enabled=1 limit 1",nativeQuery = true)
    TSysProcessInfo findByProcessName(String processName);
}

