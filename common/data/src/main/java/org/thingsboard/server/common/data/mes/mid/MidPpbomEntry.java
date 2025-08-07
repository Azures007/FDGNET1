package org.thingsboard.server.common.data.mes.mid;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "mid_ppbom_entry")
public class MidPpbomEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mid_ppbom_entry_id")
    private Integer midPpbomEntryId;

    @Column(name = "mid_ppbom_id")
    private Integer midPpbomId;

    @Column(name = "mid_ppbom_entry_material_name")
    private String midPpbomEntryMaterialName;

    @Column(name = "mid_ppbom_entry_material_number")
    private String midPpbomEntryMaterialNumber;

    @Column(name = "mid_ppbom_entry_unit_name")
    private String midPpbomEntryUnitName;

    @Column(name = "mid_ppbom_entry_unit_number")
    private String midPpbomEntryUnitNumber;

    @Column(name = "mid_ppbom_entry_material_positive_error")
    private Float midPpbomEntryMaterialPositiveError;

    @Column(name = "mid_ppbom_entry_material_negative_error")
    private Float midPpbomEntryMaterialNegativeError;

    @Column(name = "mid_ppbom_entry_material_standard")
    private Float midPpbomEntryMaterialStandard;

    @Column(name = "gmt_create")
    private Date gmtCreate;

    @Column(name = "gmt_modified")
    private Date gmtModified;

    @Column(name = "is_delete")
    private Integer isDelete;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "mid_ppbom_entry_bill_id")
    private Integer midPpbomEntryBillId;
}
