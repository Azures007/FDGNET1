package org.thingsboard.server.common.data.mes.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@ApiModel("友臣盘点还原记录表")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "t_sys_pd_record_split")
public class TSysPdRecordSplit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pd_record_split_id")
    private Integer pdRecordSplitId;

    @Column(name = "pd_time")
    private Timestamp pdTime;

    @Column(name = "material_number")
    private String materialNumber;

    @Column(name = "material_name")
    private String materialName;

    @Column(name = "material_specifications")
    private String materialSpecifications;

    @Column(name = "pd_unit")
    private String pdUnit;

    @Column(name = "pd_unit_str")
    private String pdUnitStr;

    @Column(name = "pd_qty")
    private Double pdQty;

    @Column(name = "pd_created_name")
    private String pdCreatedName;

    @Column(name = "pd_created_id")
    private String pdCreatedId;

    @Column(name = "pd_workshop_nc_id")
    @ApiModelProperty("盘点车间ncid*")
    private String pdWorkshopNcId;

    @Column(name = "pd_workshop_name")
    private String pdWorkshopName;

    @Column(name = "pd_workshop_number")
    private String pdWorkshopNumber;

    @Column(name = "pd_workshop_leader_name")
    private String pdWorkshopLeaderName;

    @Column(name = "pd_workshop_leader_id")
    private String pdWorkshopLeaderId;

    @Column(name = "pd_class_name")
    private String pdClassName;

    @Column(name = "pd_class_number")
    private String pdClassNumber;

    @Column(name = "by_deleted")
    private String byDeleted;

    @Column(name = "created_time")
    private Timestamp createdTime;

    @Column(name = "created_name")
    private String createdName;

    @Column(name = "by_fp")
    private String byFp;

    @Column(name = "pd_type")
    private String pdType;

    @Column(name = "pd_br")
    private String pdBr;

    @Column(name = "re_pd_record_id")
    private Integer rePdRecordId;

    @Column(name = "pd_time_str")
    private String pdTimeStr;
}
