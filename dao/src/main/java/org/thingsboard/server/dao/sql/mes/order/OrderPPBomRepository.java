package org.thingsboard.server.dao.sql.mes.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.mes.bus.TBusOrderPPBom;

import java.util.List;
import java.util.Map;

/**
 * @author hhh
 * @version V1.0
 * @Package org.thingsboard.server.dao.sql.order
 * @date 2022/4/13 11:01
 * @Description:
 */
public interface OrderPPBomRepository extends JpaRepository<TBusOrderPPBom, Integer>, JpaSpecificationExecutor<TBusOrderPPBom> {
    @Query("select t from TBusOrderPPBom as t where t.orderPPBomId in (:ids)")
    List<TBusOrderPPBom> findByIds(@Param("ids") List<Integer> ids);

    @Query(value = "select coalesce(min(su),0) from (\n" +
            "select c.order_ppbom_id ,coalesce(t.su,0) as su from t_bus_order_head a join t_bus_order_ppbom_lk b on a.order_id =b.order_id \n" +
            "join t_bus_order_ppbom c on b.order_ppbom_id =c.order_ppbom_id \n" +
            "left join (select c.order_ppbom_id ,sum(export_pot) as su from t_bus_order_process a \n" +
            "join t_sys_process_info b on a.process_id =b.process_id \n" +
            "join t_bus_order_process_record c on a.order_process_id =c.order_process_id \n" +
            "join t_bus_order_process_lk d on a.order_process_id =d.order_process_id \n" +
            "join t_bus_order_head e on d.order_id =e.order_id \n" +
            "where b.process_number =?2 and bus_type='BG' and record_type = '1' and e.order_id =?1 \n" +
            " and (device_person_id=?3  or ?3=-1) \n" +
            "group by order_ppbom_id) as t on c.order_ppbom_id =t.order_ppbom_id\n" +
            "where c.mid_ppbom_entry_is_into = 1 and a.order_id =?1  and c.mid_ppbom_entry_input_process='ZB'\n" +
            ") as p", nativeQuery = true)
    float findExportPot(Integer orderId, String processNumberZhanban, Integer devicePersonId);

    /**
     * 斩拌个人投入
     */
    @Query(value = "select coalesce(min(su),0) from (\n" +
            "select c.order_ppbom_id ,coalesce (case when t.su<0 then 0 else t.su end,0)  as su from t_bus_order_head a join t_bus_order_ppbom_lk b on a.order_id =b.order_id \n" +
            "join t_bus_order_ppbom c on b.order_ppbom_id =c.order_ppbom_id \n" +
            "left join (select c.order_ppbom_id ,sum(case when import_pot_group=1 then import_pot else import_pot-1 end) as su from t_bus_order_process a \n" +
            "join t_sys_process_info b on a.process_id =b.process_id \n" +
            "join t_bus_order_process_record c on a.order_process_id =c.order_process_id \n" +
            "join t_bus_order_process_lk d on a.order_process_id =d.order_process_id \n" +
            "where bus_type='BG' and record_type = '1' and a.order_process_id =?4   \n" +
            "and (device_person_group_id=?2  or ?2='-1' or ?2='') \n" +
            "and (device_group_id=?3  or ?3='-1' or ?3='') \n" +
            "group by order_ppbom_id) as t on c.order_ppbom_id =t.order_ppbom_id\n" +
            "where c.mid_ppbom_entry_is_into = 1 and a.order_id =?1  and c.mid_ppbom_entry_input_process='ZB'\n" +
            ") as p", nativeQuery = true)
    int findExportPotByImport(Integer orderId, String devicePersonGroupId, String deviceGroupId, Integer orderProcessId);


    /**
     * 斩拌个人投入
     */
    @Query(value = "select coalesce(min(su),0) from (\n" +
            "select c.order_ppbom_id ,coalesce (case when t.su<0 then 0 else t.su end,0)  as su from t_bus_order_head a join t_bus_order_ppbom_lk b on a.order_id =b.order_id \n" +
            "join t_bus_order_ppbom c on b.order_ppbom_id =c.order_ppbom_id \n" +
            "left join (select c.order_ppbom_id ,sum(case when import_pot_group=1 then import_pot else import_pot-1 end) as su from t_bus_order_process a \n" +
            "join t_sys_process_info b on a.process_id =b.process_id \n" +
            "join t_bus_order_process_record c on a.order_process_id =c.order_process_id \n" +
            "join t_bus_order_process_lk d on a.order_process_id =d.order_process_id \n" +
            "where bus_type='BG' and record_type = '1' and a.order_process_id =?4   \n" +
            "and (device_person_group_id=?2  or ?2='-1' or ?2='') \n" +
            "and (device_group_id=?3  or ?3='-1' or ?3='') \n" +
            "group by order_ppbom_id) as t on c.order_ppbom_id =t.order_ppbom_id\n" +
            "where c.mid_ppbom_entry_is_into = 1 and a.order_id =?1  and c.mid_ppbom_entry_input_process=?5\n" +
            ") as p", nativeQuery = true)
    int findExportPotByImport(Integer orderId, String devicePersonGroupId, String deviceGroupId, Integer orderProcessId,String midPpbomEntryInputProcess);

    /**
     * 斩拌积累投入
     */
    @Query(value = "select coalesce(min(su),0) from (\n" +
            "select c.order_ppbom_id ,coalesce (case when t.su<0 then 0 else t.su end,0) as su from t_bus_order_head a join t_bus_order_ppbom_lk b on a.order_id =b.order_id \n" +
            "join t_bus_order_ppbom c on b.order_ppbom_id =c.order_ppbom_id \n" +
            "left join (select c.order_ppbom_id ,sum(case when import_pot_group=1 then import_pot else import_pot-1 end) as su from t_bus_order_process a \n" +
            "join t_sys_process_info b on a.process_id =b.process_id \n" +
            "join t_bus_order_process_record c on a.order_process_id =c.order_process_id \n" +
            "join t_bus_order_process_lk d on a.order_process_id =d.order_process_id \n" +
            "join t_bus_order_head e on d.order_id =e.order_id \n" +
            "where b.process_number =?2 and bus_type='BG' and record_type = '1' and e.order_id =?1  and c.import_pot<>0 \n" +
            "and (device_person_group_id=?3  or ?3='-1' or ?3='') \n" +
            "and (device_group_id=?4  or ?4='-1' or ?4='') \n" +
            "group by order_ppbom_id) as t on c.order_ppbom_id =t.order_ppbom_id\n" +
            "where c.mid_ppbom_entry_is_into = 1 and a.order_id =?1  and c.mid_ppbom_entry_input_process='ZB'\n" +
            ") as p", nativeQuery = true)
    int findExportPotByImportAll(Integer orderId, String processNumberZhanban, String devicePersonGroupId, String deviceGroupId);

    /**
     * 拌料积累投入
     */
    @Query(value = "select coalesce(min(su),0) from (\n" +
            "select order_ppbom_id,sum(su) as su from (\n" +
            "select e.order_ppbom_id ,floor((sum(e.import_pot)/3)) as su from t_bus_order_head a join t_bus_order_process_lk b on a.order_id =b.order_id \n" +
            "join t_bus_order_process c on b.order_process_id =c.order_process_id \n" +
            "join t_sys_process_info d on c.process_id =d.process_id \n" +
            "join t_bus_order_process_record e on c.order_process_id =e.order_process_id\n" +
            "where d.process_number =?2 and bus_type='BG' and record_type = '1' and a.order_id =?1\n" +
            "group by e.order_ppbom_id\n" +
            "union all \n" +
            "select e.order_ppbom_id,0 as su from t_bus_order_head a join t_bus_order_process_lk b on a.order_id =b.order_id \n" +
            "join t_bus_order_process c on b.order_process_id =c.order_process_id \n" +
            "join t_sys_process_info c1 on c.process_id =c1.process_id \n" +
            "join t_bus_order_ppbom_lk d on a.order_id =d.order_id \n" +
            "join t_bus_order_ppbom  e on d.order_ppbom_id =e.order_ppbom_id \n" +
            "left join mid_material  f on e.material_id =f.kd_material_id \n" +
            "where c1.process_number =?2 and a.order_id =?1 and (e.mid_ppbom_entry_bom_number is not null and f.kd_material_props_id='2' )\n" +
            ") as t \n" +
            "where order_ppbom_id in (select e.order_ppbom_id from t_bus_order_head a join t_bus_order_process_lk b on a.order_id =b.order_id \n" +
            "join t_bus_order_process c on b.order_process_id =c.order_process_id \n" +
            "join t_sys_process_info c1 on c.process_id =c1.process_id \n" +
            "join t_bus_order_ppbom_lk d on a.order_id =d.order_id \n" +
            "join t_bus_order_ppbom  e on d.order_ppbom_id =e.order_ppbom_id \n" +
            "left join mid_material  f on e.material_id =f.kd_material_id \n" +
            "where c1.process_number =?2 and a.order_id =?1 and (e.mid_ppbom_entry_bom_number is not null and f.kd_material_props_id='2' ))\n" +
            "group by order_ppbom_id\n" +
            "union all\n" +
            "select c.order_ppbom_id ,coalesce(t.su,0) as su \n" +
            "from t_bus_order_head a join t_bus_order_ppbom_lk b on a.order_id =b.order_id \n" +
            "join t_bus_order_ppbom c on b.order_ppbom_id =c.order_ppbom_id \n" +
            "left join mid_material l on c.material_id=l.kd_material_id \n" +
            "left join (select c.order_ppbom_id ,sum(import_pot) as su from t_bus_order_process a \n" +
            "join t_sys_process_info b on a.process_id =b.process_id \n" +
            "join t_bus_order_process_record c on a.order_process_id =c.order_process_id \n" +
            "join t_bus_order_process_lk d on a.order_process_id =d.order_process_id \n" +
            "join t_bus_order_head e on d.order_id =e.order_id \n" +
            "where b.process_number =?2 and bus_type='BG' and record_type = '1' and e.order_id =?1 \n" +
            "and (device_person_group_id=?3  or ?3='-1' or ?3='') \n" +
            "and (device_group_id=?4  or ?4='-1' or ?4='') \n" +
            "group by order_ppbom_id) as t on c.order_ppbom_id =t.order_ppbom_id\n" +
            "where  a.order_id =?1  and c.mid_ppbom_entry_input_process='BL'\n" +
            "and (c.mid_ppbom_entry_bom_number is null and (l.kd_material_props_id<>'2' or l.kd_material_props_id is null)) \n" +
            ") as p", nativeQuery = true)
    int findExportPotByImportAllByBL(Integer orderId, String processNumberZhanban, String devicePersonGroupId, String deviceGroupId);

    /**
     * 拌料积累框数
     */
    @Query(value = "select coalesce(min(su),0) from (\n" +
            "select order_ppbom_id,sum(su) as su from (\n" +
            "select e.order_ppbom_id ,sum(e.import_pot) as su from t_bus_order_head a join t_bus_order_process_lk b on a.order_id =b.order_id \n" +
            "join t_bus_order_process c on b.order_process_id =c.order_process_id \n" +
            "join t_sys_process_info d on c.process_id =d.process_id \n" +
            "join t_bus_order_process_record e on c.order_process_id =e.order_process_id\n" +
            "where d.process_number =?2 and bus_type='BG' and record_type = '1' and a.order_id =?1\n" +
            "group by e.order_ppbom_id\n" +
            "union all \n" +
            "select e.order_ppbom_id,0 as su from t_bus_order_head a join t_bus_order_process_lk b on a.order_id =b.order_id \n" +
            "join t_bus_order_process c on b.order_process_id =c.order_process_id \n" +
            "join t_sys_process_info c1 on c.process_id =c1.process_id \n" +
            "join t_bus_order_ppbom_lk d on a.order_id =d.order_id \n" +
            "join t_bus_order_ppbom  e on d.order_ppbom_id =e.order_ppbom_id \n" +
            "left join mid_material  f on e.material_id =f.kd_material_id \n" +
            "where c1.process_number =?2 and a.order_id =?1 and (e.mid_ppbom_entry_bom_number is not null and f.kd_material_props_id='2' )\n" +
            ") as t \n" +
            "where order_ppbom_id in (select e.order_ppbom_id from t_bus_order_head a join t_bus_order_process_lk b on a.order_id =b.order_id \n" +
            "join t_bus_order_process c on b.order_process_id =c.order_process_id \n" +
            "join t_sys_process_info c1 on c.process_id =c1.process_id \n" +
            "join t_bus_order_ppbom_lk d on a.order_id =d.order_id \n" +
            "join t_bus_order_ppbom  e on d.order_ppbom_id =e.order_ppbom_id \n" +
            "left join mid_material  f on e.material_id =f.kd_material_id \n" +
            "where c1.process_number =?2 and a.order_id =?1 and (e.mid_ppbom_entry_bom_number is not null and f.kd_material_props_id='2' ))\n" +
            "group by order_ppbom_id\n" +
            ") as p", nativeQuery = true)
    int findExportPotByImportFrameAllByBL(Integer orderId, String processNumberZhanban);

    /**
     * 拌料积累斗数
     */
    @Query(value = "select coalesce(min(su),0) from (\n" +
            "select c.order_ppbom_id ,coalesce(t.su,0) as su \n" +
            "from t_bus_order_head a join t_bus_order_ppbom_lk b on a.order_id =b.order_id \n" +
            "join t_bus_order_ppbom c on b.order_ppbom_id =c.order_ppbom_id \n" +
            "left join mid_material l on c.material_id=l.kd_material_id \n" +
            "left join (select c.order_ppbom_id ,sum(import_pot) as su from t_bus_order_process a \n" +
            "join t_sys_process_info b on a.process_id =b.process_id \n" +
            "join t_bus_order_process_record c on a.order_process_id =c.order_process_id \n" +
            "join t_bus_order_process_lk d on a.order_process_id =d.order_process_id \n" +
            "join t_bus_order_head e on d.order_id =e.order_id \n" +
            "where b.process_number =?2 and bus_type='BG' and record_type = '1' and e.order_id =?1 \n" +
            "and (device_person_group_id=?3  or ?3='-1' or ?3='') \n" +
            "and (device_group_id=?4  or ?4='-1' or ?4='') \n" +
            "group by order_ppbom_id) as t on c.order_ppbom_id =t.order_ppbom_id\n" +
            "where  a.order_id =?1  and c.mid_ppbom_entry_input_process='BL'\n" +
            "and (c.mid_ppbom_entry_bom_number is null and (l.kd_material_props_id<>'2' or l.kd_material_props_id is null)) \n" +
            ") as p", nativeQuery = true)
    int findExportPotByImportLowaAllByBL(Integer orderId, String processNumberZhanban, String devicePersonGroupId, String deviceGroupId);

    /**
     * 拌料个人投入
     */
    @Query(value = "select coalesce(min(su),0) from (\n" +
            "select t.order_ppbom_id ,sum(su) as su from (\n" +
            "select a.order_ppbom_id ,floor((sum(a.import_pot)/3)) as su \n" +
            "from t_bus_order_process_record a\n" +
            "where a.order_process_id =?4 and device_person_group_id=?2\n" +
            "and a.record_type_bg=?5 \n" +
            "group by a.order_ppbom_id\n" +
            "union all \n" +
            "select c.order_ppbom_id,0 as su from t_bus_order_head a join t_bus_order_ppbom_lk b on a.order_id =b.order_id \n" +
            "join t_bus_order_ppbom c on b.order_ppbom_id =c.order_ppbom_id \n" +
            "left join mid_material l on c.material_id=l.kd_material_id \n" +
            "where  a.order_id =?1  and c.mid_ppbom_entry_input_process='BL' \n" +
            "and (mid_ppbom_entry_bom_number is not null and kd_material_props_id='2' )\n" +
            ") t\n" +
            "where order_ppbom_id in (select c.order_ppbom_id from t_bus_order_head a join t_bus_order_ppbom_lk b on a.order_id =b.order_id \n" +
            "            join t_bus_order_ppbom c on b.order_ppbom_id =c.order_ppbom_id \n" +
            "            left join mid_material l on c.material_id=l.kd_material_id \n" +
            "            where  a.order_id =?1  and c.mid_ppbom_entry_input_process='BL' \n" +
            "            and (mid_ppbom_entry_bom_number is not null and kd_material_props_id='2' )) " +
            "group by order_ppbom_id\n" +
            "union all\n" +
            "select c.order_ppbom_id ,coalesce(t.su,0) as su from t_bus_order_head a join t_bus_order_ppbom_lk b on a.order_id =b.order_id \n" +
            "join t_bus_order_ppbom c on b.order_ppbom_id =c.order_ppbom_id \n" +
            "left join mid_material l on c.material_id=l.kd_material_id \n" +
            "left join (select c.order_ppbom_id ,sum(import_pot) as su from t_bus_order_process a \n" +
            "join t_sys_process_info b on a.process_id =b.process_id \n" +
            "join t_bus_order_process_record c on a.order_process_id =c.order_process_id \n" +
            "join t_bus_order_process_lk d on a.order_process_id =d.order_process_id \n" +
            "where bus_type='BG' and record_type = '1' and a.order_process_id =?4 and c.record_type_bg=?5\n" +
            "and (device_person_group_id=?2  or ?2='-1' or ?2='') \n" +
            "and (device_group_id=?3  or ?3='-1' or ?3='') \n" +
            "group by order_ppbom_id) as t on c.order_ppbom_id =t.order_ppbom_id\n" +
            "where  a.order_id =?1  and c.mid_ppbom_entry_input_process='BL' " +
            "and (c.mid_ppbom_entry_bom_number is null and (l.kd_material_props_id<>'2' or l.kd_material_props_id is null) ) \n" +
            ") as p", nativeQuery = true)
    int findExportPotByImportByBL(Integer orderId, String devicePersonGroup_id, String deviceGroupId, Integer orderProcessId, String bglx);

    //拌料本机台手投入框数
    @Query(value = "select coalesce(min(su),0) from (\n" +
            "select t.order_ppbom_id ,sum(su) as su from (\n" +
            "select a.order_ppbom_id ,sum(a.import_pot) as su \n" +
            "from t_bus_order_process_record a\n" +
            "where a.order_process_id =?3 and device_person_group_id=?2\n" +
            "and a.record_type_bg=?4 \n" +
            "and a.bus_type='BG' \n" +
            "and a.record_type = '1' \n" +
            "group by a.order_ppbom_id\n" +
            "union all \n" +
            "select c.order_ppbom_id,0 as su from t_bus_order_head a join t_bus_order_ppbom_lk b on a.order_id =b.order_id \n" +
            "join t_bus_order_ppbom c on b.order_ppbom_id =c.order_ppbom_id \n" +
            "left join mid_material l on c.material_id=l.kd_material_id \n" +
            "where  a.order_id =?1  and c.mid_ppbom_entry_input_process='BL' \n" +
            "and (mid_ppbom_entry_bom_number is not null and kd_material_props_id='2' )\n" +
            ") t\n" +
            "where order_ppbom_id in (select c.order_ppbom_id from t_bus_order_head a join t_bus_order_ppbom_lk b on a.order_id =b.order_id \n" +
            "            join t_bus_order_ppbom c on b.order_ppbom_id =c.order_ppbom_id \n" +
            "            left join mid_material l on c.material_id=l.kd_material_id \n" +
            "            where  a.order_id =?1  and c.mid_ppbom_entry_input_process='BL' \n" +
            "            and (mid_ppbom_entry_bom_number is not null and kd_material_props_id='2' )) " +
            "group by order_ppbom_id\n" +
            ") as p", nativeQuery = true)
    int findExportPotFrameByImportByBL(Integer orderId, String devicePersonGroup_id, Integer orderProcessId, String bglx);

    @Query(value = "select coalesce(min(su),0) from (\n" +
            "select c.order_ppbom_id ,coalesce(t.su,0) as su from t_bus_order_head a join t_bus_order_ppbom_lk b on a.order_id =b.order_id \n" +
            "join t_bus_order_ppbom c on b.order_ppbom_id =c.order_ppbom_id \n" +
            "left join mid_material l on c.material_id=l.kd_material_id \n" +
            "left join (select c.order_ppbom_id ,sum(import_pot) as su from t_bus_order_process a \n" +
            "join t_sys_process_info b on a.process_id =b.process_id \n" +
            "join t_bus_order_process_record c on a.order_process_id =c.order_process_id \n" +
            "join t_bus_order_process_lk d on a.order_process_id =d.order_process_id \n" +
            "where bus_type='BG' and record_type = '1' and a.order_process_id =?4 and c.record_type_bg=?5\n" +
            "and (device_person_group_id=?2  or ?2='-1' or ?2='') \n" +
            "and (device_group_id=?3  or ?3='-1' or ?3='') \n" +
            "group by order_ppbom_id) as t on c.order_ppbom_id =t.order_ppbom_id\n" +
            "where  a.order_id =?1  and c.mid_ppbom_entry_input_process='BL' " +
            "and (c.mid_ppbom_entry_bom_number is null and (l.kd_material_props_id<>'2' or l.kd_material_props_id is null) ) \n" +
            ") as p", nativeQuery = true)
    int findExportPotLowaByImportByBL(Integer orderId, String devicePersonGroup_id, String deviceGroupId, Integer orderProcessId, String bglx);


    /**
     * 拌料个人投入最大值
     */
    @Query(value = "select coalesce(max(su),0) from (\n" +
            "select t.order_ppbom_id ,sum(su) as su from (\n" +
            "select a.order_ppbom_id ,floor((sum(a.import_pot)/3)) as su \n" +
            "from t_bus_order_process_record a\n" +
            "where a.order_process_id =?4 and device_person_group_id=?2\n" +
            "group by a.order_ppbom_id\n" +
            "union all \n" +
            "select c.order_ppbom_id,0 as su from t_bus_order_head a join t_bus_order_ppbom_lk b on a.order_id =b.order_id \n" +
            "join t_bus_order_ppbom c on b.order_ppbom_id =c.order_ppbom_id \n" +
            "left join mid_material l on c.material_id=l.kd_material_id \n" +
            "where  a.order_id =?1  and c.mid_ppbom_entry_input_process='BL' \n" +
            "and (mid_ppbom_entry_bom_number is not null and kd_material_props_id='2' )\n" +
            ") t\n" +
            "where order_ppbom_id in (select c.order_ppbom_id from t_bus_order_head a join t_bus_order_ppbom_lk b on a.order_id =b.order_id \n" +
            "            join t_bus_order_ppbom c on b.order_ppbom_id =c.order_ppbom_id \n" +
            "            left join mid_material l on c.material_id=l.kd_material_id \n" +
            "            where  a.order_id =?1  and c.mid_ppbom_entry_input_process='BL' \n" +
            "            and (mid_ppbom_entry_bom_number is not null and kd_material_props_id='2' ))" +
            "group by order_ppbom_id\n" +
            "union all\n" +
            "select c.order_ppbom_id ,coalesce(t.su,0) as su from t_bus_order_head a join t_bus_order_ppbom_lk b on a.order_id =b.order_id \n" +
            "join t_bus_order_ppbom c on b.order_ppbom_id =c.order_ppbom_id \n" +
            "left join mid_material l on c.material_id=l.kd_material_id \n" +
            "left join (select c.order_ppbom_id ,sum(import_pot) as su from t_bus_order_process a \n" +
            "join t_sys_process_info b on a.process_id =b.process_id \n" +
            "join t_bus_order_process_record c on a.order_process_id =c.order_process_id \n" +
            "join t_bus_order_process_lk d on a.order_process_id =d.order_process_id \n" +
            "where bus_type='BG' and record_type = '1' and a.order_process_id =?4 \n" +
            "and (device_person_group_id=?2  or ?2='-1' or ?2='') \n" +
            "and (device_group_id=?3  or ?3='-1' or ?3='') \n" +
            "group by order_ppbom_id) as t on c.order_ppbom_id =t.order_ppbom_id\n" +
            "where  a.order_id =?1  and c.mid_ppbom_entry_input_process='BL' \n" +
            "and (c.mid_ppbom_entry_bom_number is null and (l.kd_material_props_id<>'2' or l.kd_material_props_id is null) ) \n" +
            ") as p", nativeQuery = true)
    int findExportPotByImportAndMaxBL(Integer orderId, String devicePersonGroupId, String deviceGroupId, Integer orderProcessId);

    /**
     * 积累产后报工锅数（不区分工序）
     */
    @Query(value = "select coalesce(sum(d.export_pot),0)\n" +
            "from t_bus_order_head a\n" +
            "join t_bus_order_process_lk b on a.order_id =b.order_id \n" +
            "join t_bus_order_process c on b.order_process_id =c.order_process_id \n" +
            "join t_bus_order_process_record d on c.order_process_id =d.order_process_id \n" +
            "where d.bus_type ='BG' and d.record_type ='3' \n" +
            "and d.process_number =?2 and a.order_id =?1", nativeQuery = true)
    Float sumExportPotAllByBL(Integer orderId, String processNumber);

    /**
     * 积累产后报工数量统计（不区分工序）
     */
    @Query(value = "select coalesce(sum(cast(d.record_qty * (case when d.record_unit='kg' then 1 else 0.001 end) as DECIMAL(19, 3))),0)\n" +
            "from t_bus_order_head a\n" +
            "join t_bus_order_process_lk b on a.order_id =b.order_id \n" +
            "join t_bus_order_process c on b.order_process_id =c.order_process_id \n" +
            "join t_bus_order_process_record d on c.order_process_id =d.order_process_id \n" +
            "where d.bus_type ='BG' and d.record_type ='3' \n" +
            "and d.process_number =?2 and a.order_id =?1", nativeQuery = true)
    Float sumExportPotQtyAllByBL(Integer orderId, String processNumber);

    /**
     * 积累投入报工数量统计（不区分工序）
     */
    @Query(value = "select coalesce(sum(cast(d.record_qty * (case when d.record_unit='kg' then 1 else 0.001 end) as DECIMAL(19, 3))),0)\n" +
            "from t_bus_order_head a\n" +
            "join t_bus_order_process_lk b on a.order_id =b.order_id \n" +
            "join t_bus_order_process c on b.order_process_id =c.order_process_id \n" +
            "join t_bus_order_process_record d on c.order_process_id =d.order_process_id \n" +
            "where d.bus_type ='BG' and d.record_type ='1' \n" +
            "and d.process_number =?2 and a.order_id =?1", nativeQuery = true)
    Float sumImportPotQtyAll(Integer orderId, String processNumber);

    // 个人产出报工数据统计
    @Query(value = "select coalesce(sum(cast(a.record_qty * (case when a.record_unit='kg' then 1 else 0.001 end) as DECIMAL(19, 3))),0)\n" +
            "from t_bus_order_process_record a \n" +
            "where a.order_process_id =?1\n" +
            "and (a.device_group_id =?2 or ?2='-1' or ?2='')\n" +
            "and (a.device_person_group_id=?3 or ?3='-1' or ?3='')\n" +
            "and bus_type='BG' and record_type='3'", nativeQuery = true)
    float findExportPotByOne(Integer orderProcessId, String deviceGroupId, String devicePersonGroupId);

    // 个人产出报工锅数统计
    @Query(value = "select coalesce(sum(export_pot),0) from t_bus_order_process_record a \n" +
            "where a.order_process_id =?1\n" +
            "and (a.device_group_id =?2 or ?2='-1' or ?2='')\n" +
            "and (a.device_person_group_id=?3 or ?3='-1' or ?3='')\n" +
            "and bus_type='BG' and record_type='3'", nativeQuery = true)
    float findExportPotOne(Integer orderProcessId, String deviceGroupId, String devicePersonGroupId);

    @Query(value = "select coalesce(sum(cast(record_qty * (case when a.record_unit='kg' then 1 else 0.001 end) as DECIMAL(19, 3))),0) " +
            "from t_bus_order_process_record a left join t_bus_order_ppbom b on a.order_ppbom_id=b.order_ppbom_id\n" +
            "where a.order_process_id =:orderProcessId \n" +
            "and bus_type ='BG' \n" +
            "and a.device_person_group_id =:groupId \n" +
            "and record_type in(:types)", nativeQuery = true)
    Float sumImportPot(@Param("orderProcessId") Integer orderProcessId, @Param("groupId") String devicePersonGroupId, @Param("types") List<String> types);

    @Query(value = "select coalesce(sum(cast(record_qty * (case when a.record_unit='kg' then 1 else 0.001 end) as DECIMAL(19, 3))),0)  \n" +
            "from t_bus_order_process_record a \n" +
            "where a.order_process_id =:orderProcessId \n" +
            "and bus_type ='BG' \n" +
            "and a.device_person_group_id =:groupId \n" +
            "and record_type in(:types)", nativeQuery = true)
    Float sumExportPot(@Param("orderProcessId") Integer orderProcessId, @Param("groupId") String devicePersonGroupId, @Param("types") List<String> types);

    @Query(value = "select e.order_ppbom_id from t_bus_order_process a \n" +
            "join t_bus_order_process_lk b on a.order_process_id =b.order_process_id    \n" +
            "join t_bus_order_head c on b.order_id =c.order_id \n" +
            "join t_bus_order_ppbom_lk d on c.order_id =d.order_id \n" +
            "join t_bus_order_ppbom e on d.order_ppbom_id =e.order_ppbom_id \n" +
            "where a.order_process_id =?2 and e.mid_ppbom_entry_replace_group =(select mid_ppbom_entry_replace_group from t_bus_order_ppbom where order_ppbom_id=?1)\n", nativeQuery = true)
    List<Integer> joinPpbomId(Integer orderPpbomId, Integer orderProcessId);

    @Query(value = "select mid_ppbom_entry_weigh_devept_qty from t_bus_order_ppbom where mid_ppbom_entry_item_type=1 and order_ppbom_id in (:ppboms) limit 1 offset 0", nativeQuery = true)
    Float getMesVal(@Param("ppboms") List<Integer> ppboms);

    @Query(value = "select count(1) as su from t_bus_order_ppbom c \n" +
            "left join mid_material l on c.material_id=l.kd_material_id \n" +
            "where c.order_ppbom_id=?1 " +
            "and (c.mid_ppbom_entry_bom_number is not null and l.kd_material_props_id='2')\n", nativeQuery = true)
    Integer getCountMainPpbomId(Integer orderPpbomId);

    @Query(value = "select c.order_ppbom_id from t_bus_order_head a \n" +
            "join t_bus_order_ppbom_lk b on a.order_id =b.order_id \n" +
            "join t_bus_order_ppbom c on b.order_ppbom_id =c.order_ppbom_id \n" +
            "join mid_material l on c.material_id=l.kd_material_id \n" +
            "where a.order_id =?1 and c.mid_ppbom_entry_input_process=?2 and c.mid_ppbom_entry_is_into=1 \n" +
            "and (c.mid_ppbom_entry_bom_number is null or l.kd_material_props_id<>'2') \n", nativeQuery = true)
    List<Integer> findNotMainPpbomId(Integer orderId, String midPpbomEntryInputProcess);

    /**
     * 获取用料报工详情
     *
     * @param orderId
     * @param processNumberZhanban
     * @param currentPot
     * @return
     */
    @Query(value = "      select c.mid_ppbom_entry_handle_group,c.mid_ppbom_entry_replace_group ,array_to_string(array_agg( c.material_id),'/') as material_id,array_to_string(array_agg(c.material_name),'/') as material_name,\n" +
            "            array_to_string(array_agg(c.material_number),'/') as material_number,\n" +
            "            max(case when t.su is not null then 1 else 0 end)  as byInto,sum(t.record_qty) as record_qty," +
            "           max(t.record_unit) record_unit,max(t.record_unit_str) record_unit_str \n" +
            "            from t_bus_order_head a " +
            "            join t_bus_order_ppbom_lk b on a.order_id =b.order_id \n" +
            "            join t_bus_order_ppbom c on b.order_ppbom_id =c.order_ppbom_id \n" +
            "            left join (select c.order_ppbom_id ,sum(c.record_qty) record_qty,max(c.record_unit) record_unit,max(code_dsc) record_unit_str," +
            "           sum(all_import_pot) as su from t_bus_order_process a \n" +
            "            join t_sys_process_info b on a.process_id =b.process_id \n" +
            "            join t_bus_order_process_history c on a.order_process_id =c.order_process_id\n" +
            "            join t_bus_order_process_lk d on a.order_process_id =d.order_process_id \n" +
            "            join t_bus_order_head e on d.order_id =e.order_id \n" +
            "            left join t_sys_code_dsc f on c.record_unit=f.code_value" +
            "            where b.process_number =?2 and bus_type='BG' and record_type = '1' and e.order_id =?1 and code_cl_id='UNIT0000'\n" +
            "            and all_import_pot=?3  and report_status='0' " +
            "            group by order_ppbom_id\n" +
//            "           having sum(import_pot) >=?3\n" +
            "            ) as t on c.order_ppbom_id =t.order_ppbom_id\n" +
            "            where c.mid_ppbom_entry_is_into = 1 and a.order_id =?1  and c.mid_ppbom_entry_input_process='ZB'  " +
            "                \n" +
            "            group by c.mid_ppbom_entry_handle_group,mid_ppbom_entry_replace_group\n" +
            "            order by mid_ppbom_entry_replace_group asc", nativeQuery = true)
    List<Map> listPpbomRecordDetails(Integer orderId, String processNumberZhanban, int currentPot);

    /**
     * 获取无类别的报工详情
     *
     * @param orderId
     * @param processNumberZhanban
     * @param currentPot
     * @return
     */
    @Query(value = "      select c.mid_ppbom_entry_replace_group ,array_to_string(array_agg( c.material_id),'/') as material_id,array_to_string(array_agg(c.material_name),'/') as material_name,\n" +
            "            array_to_string(array_agg(c.material_number),'/') as material_number,\n" +
            "            max(case when t.su is not null then 1 else 0 end)  as byInto,sum(t.record_qty) as record_qty," +
            "            max(t.record_unit) record_unit,max(t.record_unit_str) record_unit_str \n" +
            "            from t_bus_order_head a join t_bus_order_ppbom_lk b on a.order_id =b.order_id \n" +
            "            join t_bus_order_ppbom c on b.order_ppbom_id =c.order_ppbom_id \n" +
            "            left join (select c.order_ppbom_id ,sum(c.record_qty) record_qty,max(c.record_unit) record_unit,max(code_dsc) record_unit_str," +
            "            sum(import_pot) as su from t_bus_order_process a \n" +
            "            join t_sys_process_info b on a.process_id =b.process_id \n" +
            "            join t_bus_order_process_history c on a.order_process_id =c.order_process_id\n" +
            "            join t_bus_order_process_lk d on a.order_process_id =d.order_process_id \n" +
            "            join t_bus_order_head e on d.order_id =e.order_id \n" +
            "            left join t_sys_code_dsc f on c.record_unit=f.code_value \n" +
            "            where b.process_number =?2 and bus_type='BG'and report_status='0' and record_type = '1' and e.order_id =?1 and code_cl_id='UNIT0000'\n" +
            "           and import_pot=?3 \n " +
            "            group by order_ppbom_id\n" +
//            "            having sum(import_pot) >=?3\n" +
            "            ) as t on c.order_ppbom_id =t.order_ppbom_id\n" +
            "            where c.mid_ppbom_entry_is_into = 1 and a.order_id =?1  and c.mid_ppbom_entry_input_process='ZB'  " +
            "           and (mid_ppbom_entry_handle_group not in (?4) or mid_ppbom_entry_handle_group is null )     \n" +
            "            group by mid_ppbom_entry_replace_group\n" +
            "            order by mid_ppbom_entry_replace_group asc", nativeQuery = true)
    List<Map> listPpbomRecordDetailsNotKeys(Integer orderId, String processNumberZhanban, int currentPot, List<Integer> keys);

    /**
     * 乳化剂/斩拌积累投入数量
     * @param orderId
     * @param processNumberZhanban
     * @return
     */
    @Query(value = "select coalesce(sum(cast(record_qty * (case when record_unit='kg' then 1 else 0.001 end) as DECIMAL(19, 3))),0) \n" +
            "from t_bus_order_process_record a\n" +
            "join t_bus_order_process_lk b on a.order_process_id =b.order_process_id \n" +
            "join t_bus_order_head c on b.order_id =c.order_id \n" +
            "join t_bus_order_process d on b.order_process_id =d.order_process_id \n" +
            "join t_sys_process_info e on d.process_id =e.process_id \n" +
            "where c.order_id =?1 and e.process_number =?2 and bus_type='BG' and record_type = '1'", nativeQuery = true)
    float sumImportPotQty(Integer orderId, String processNumberZhanban);

    /**
     * 乳化剂积累投入锅数
     * @param orderId
     * @param processNumberRuhuajiang
     * @param s
     * @param s1
     * @return
     */
    @Query(value = "select coalesce(min(su),0) from (\n" +
            "select c.order_ppbom_id ,coalesce (case when t.su<0 then 0 else t.su end,0) as su from t_bus_order_head a join t_bus_order_ppbom_lk b on a.order_id =b.order_id \n" +
            "join t_bus_order_ppbom c on b.order_ppbom_id =c.order_ppbom_id \n" +
            "left join (select c.order_ppbom_id ,sum(case when import_pot_group=1 then import_pot else import_pot-1 end) as su from t_bus_order_process a \n" +
            "join t_sys_process_info b on a.process_id =b.process_id \n" +
            "join t_bus_order_process_record c on a.order_process_id =c.order_process_id \n" +
            "join t_bus_order_process_lk d on a.order_process_id =d.order_process_id \n" +
            "join t_bus_order_head e on d.order_id =e.order_id \n" +
            "where b.process_number =?2 and bus_type='BG' and record_type = '1' and e.order_id =?1  \n" +
            "and (device_person_group_id=?4  or ?4='-1' or ?4='') \n" +
            "and (device_group_id=?5 or ?5='-1' or ?5='') \n" +
            "group by order_ppbom_id) as t on c.order_ppbom_id =t.order_ppbom_id\n" +
            "where c.mid_ppbom_entry_is_into = 1 and a.order_id =?1  and c.mid_ppbom_entry_input_process=?3\n" +
            ") as p", nativeQuery = true)
    int findExportPotByImportAllByRhj(Integer orderId, String processNumberRuhuajiang,String midPpbomEntryInputProcess, String s, String s1);
}
