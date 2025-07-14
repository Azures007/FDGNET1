package org.thingsboard.server.dao.sql.TSysQualityReport;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.TSysProcessInfo;
import org.thingsboard.server.common.data.TSysQualityReportCategory;

import java.util.List;

/**
 * @Auther: l
 * @Date: 2022/4/21 10:19
 * @Description:
 */
public interface TSysQualityReportCategoryRepository extends JpaRepository<TSysQualityReportCategory,Integer> {

//    @Query(value = "select * from t_sys_process_info where process_id in ( \n" +
//            " select DISTINCT c.process_id from t_bus_order_head a \n" +
//            " left Join t_sys_craft_info b on a.craft_id=b.craft_id \n" +
//            " left Join t_sys_craft_process_rel c on b.craft_id=c.craft_id \n" +
//            " where a.mid_mo_sale_order_no=:midMoSaleOrderNo \n" +
//            " )",nativeQuery = true)
//    List<TSysProcessInfo> getProcessInfos(@Param("midMoSaleOrderNo") String midMoSaleOrderNo);
//
//    TSysProcessInfo getByProcessNumber(String processNumber);
//
//    /**
//     * 通过工序执行id获取工序编码
//     * @param orderProcessId
//     * @return
//     */
//    @Query(value = "select b.process_number from t_bus_order_process a \n" +
//            "join t_sys_process_info b on a.process_id =b.process_id \n" +
//            "where a.order_process_id =?1",nativeQuery = true)
//    String findProcessNumberByOrderProcessId(Integer orderProcessId);
}
