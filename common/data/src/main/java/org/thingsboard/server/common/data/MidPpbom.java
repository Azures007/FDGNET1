package org.thingsboard.server.common.data;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "mid_ppbom")
@Data
public class MidPpbom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mid_ppbom_id")
    private Integer midPpbomId;

    @Column(name = "mid_mo_entry_id")
    private Integer midMoEntryId;

    @Column(name = "mid_ppbom_bill_number")
    private String midPpbomBillNumber;

    @Column(name = "mid_mo_bill_number")
    private String midMoBillNumber;

    @Column(name = "mid_mo_seq")
    private Integer midMoSeq;

    @Column(name = "mid_ppbom_material_name")
    private String midPpbomMaterialName;

    @Column(name = "mid_ppbom_material_number")
    private String midPpbomMaterialNumber;

    @Column(name = "mid_ppbom_unit_name")
    private String midPpbomUnitName;

    @Column(name = "mid_ppbom_unit_number")
    private String midPpbomUnitNumber;

    @Column(name = "gmt_create")
    private Date gmtCreate;

    @Column(name = "gmt_modified")
    private Date gmtModified;

    @Column(name = "isDelete")
    private Integer is_delete;

    @Column(name = "mid_mo_entry_create_date")
    private Date midMoEntryCreateDate;

    @Column(name = "mid_mo_entry_modify_date")
    private Date midMoEntryModifyDate;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "mid_ppbom_bill_id")
    private Integer midPpbomBillId;
}
