package org.thingsboard.server.common.data;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "mid_mo")
public class MidMo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mid_mo_id")
    private Integer midMoId;
    @Column(name = "mid_mo_bill_id")
    private Integer midMoBillId;
    @Column(name = "mid_mo_bill_number")
    private String midMoBillNumber;
    @Column(name = "mid_mo_date")
    private Date midMoDate;
    @Column(name = "mid_mo_create_date")
    private Date midMoCreateDate;
    @Column(name = "mid_mo_modify_date")
    private Date midMoModifyDate;
    @Column(name = "mid_mol_type_id")
    private String midMolTypeId;
    @Column(name = "mid_mo_type_name")
    private String midMoTypeName;
    @Column(name = "mid_mo_org_id")
    private Integer midMoOrgId;
    @Column(name = "mid_mo_org_name")
    private String midMoOrgName;
    @Column(name = "gmt_create")
    private Date gmtCreate;
    @Column(name = "gmt_modified")
    private Date gmtModified;
    @Column(name = "is_delete")
    private Integer isDelete;
    @Column(name = "uuid")
    private String uuid;
    @Column(name = "errorMessage")
    private String error_message;
    @Column(name = "error_time")
    private Date errorTime;

}
