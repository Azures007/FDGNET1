package org.thingsboard.server.common.data.mes.mid;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "mid_mo_entry")
public class MidMoEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mid_mo_entry_id")
    private Integer midMoEntryId;

    @Column(name = "mid_mo_id")
    private Integer midMoId;

    @Column(name = "mid_mo_entry_seq")
    private Integer midMoEntrySeq;

    @Column(name = "mid_mo_entry_each_pot_weight")
    private Float midMoEntryEachPotWeight;

    @Column(name = "mid_mo_entry_pot_num")
    private Integer midMoEntryPotNum;

    @Column(name = "mid_mo_entry_first_material_name")
    private String midMoEntryFirstMaterialName;

    @Column(name = "mid_mo_entry_first_material_number")
    private String midMoEntryFirstMaterialNumber;

    @Column(name = "mid_mo_entry_first_material_proportion")
    private Float midMoEntryFirstMaterialProportion;

    @Column(name = "mid_mo_entry_second_material_name")
    private String midMoEntrySecondMaterialName;

    @Column(name = "mid_mo_entry_second_material_number")
    private String midMoEntrySecondMaterialNumber;

    @Column(name = "mid_mo_entry_second_material_proportion")
    private Float midMoEntrySecondMaterialProportion;

    @Column(name = "mid_mo_entry_team_id")
    private Integer midMoEntryTeamId;

    @Column(name = "mid_mo_entry_team_name")
    private String midMoEntryTeamName;

    @Column(name = "mid_mo_entry_lot_id")
    private Integer midMoEntryLotId;

    @Column(name = "mid_mo_entry_lot_value")
    private String midMoEntryLotValue;

    @Column(name = "mid_mo_entry_work_shop_id")
    private Integer midMoEntryWorkShopId;

    @Column(name = "mid_mo_entry_work_shop_name")
    private String midMoEntryWorkShopName;

    @Column(name = "mid_mo_entry_material_id")
    private Integer midMoEntryMaterialId;

    @Column(name = "mid_mo_entry_material_name")
    private String midMoEntryMaterialName;

    @Column(name = "mid_mo_entry_material_number")
    private String midMoEntryMaterialNumber;

    @Column(name = "mid_mo_entry_material_spec")
    private String midMoEntryMaterialSpec;

    @Column(name = "mid_mo_entry_plan_num")
    private Float midMoEntryPlanNum;

    @Column(name = "mid_mo_entry_unit_id")
    private Integer midMoEntryUnitId;

    @Column(name = "mid_mo_entry_unit_name")
    private String midMoEntryUnitName;

    @Column(name = "mid_mo_entry_unit_number")
    private String midMoEntryUnitNumber;

    @Column(name = "mid_mo_entry_plan_start_date")
    private Date midMoEntryPlanStartDate;

    @Column(name = "mid_mo_entry_plan_finish_date")
    private Date midMoEntryPlanFinishDate;

    @Column(name = "mid_mo_entry_sale_order_number")
    private String midMoEntrySaleOrderNumber;

    @Column(name = "gmt_create")
    private Date gmtCreate;

    @Column(name = "gmt_modified")
    private Date gmtModified;

    @Column(name = "is_delete")
    private Integer isDelete;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "mid_mo_entry_bill_id")
    private Integer midMoEntryBillId;
}
