package org.thingsboard.server.common.data.mes.sys;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @ApiModelProperty("盘点时间*")
    private Timestamp pdTime;

    @Column(name = "material_number")
    @ApiModelProperty("材料编码*")
    private String materialNumber;

    @Column(name = "material_name")
    @ApiModelProperty("材料名称*")
    private String materialName;

    @Column(name = "material_specifications")
    @ApiModelProperty("材料规格*")
    private String materialSpecifications;

    @Column(name = "pd_unit")
    @ApiModelProperty("单位*")
    private String pdUnit;

    @Column(name = "pd_unit_str")
    @ApiModelProperty("单位名称*")
    private String pdUnitStr;

    @Column(name = "pd_qty")
    @ApiModelProperty("盘点数量*")
    private Double pdQty;

    @Column(name = "pd_created_name")
    @ApiModelProperty("盘点人姓名*")
    private String pdCreatedName;

    @Column(name = "pd_created_id")
    @ApiModelProperty("盘点人id*")
    private String pdCreatedId;

    @Column(name = "pd_workshop_nc_id")
    @ApiModelProperty("盘点车间ncid*")
    private String pdWorkshopNcId;

    @Column(name = "pd_workshop_name")
    @ApiModelProperty("盘点车间名称*")
    private String pdWorkshopName;

    @Column(name = "pd_workshop_number")
    @ApiModelProperty("盘点车间编码*")
    private String pdWorkshopNumber;

    @Column(name = "pd_workshop_leader_name")
    @ApiModelProperty("盘点车间主任名称*")
    private String pdWorkshopLeaderName;

    @Column(name = "pd_workshop_leader_id")
    @ApiModelProperty("盘点车间主任id*")
    private String pdWorkshopLeaderId;

    @Column(name = "pd_class_name")
    @ApiModelProperty("盘点班组名称*")
    private String pdClassName;

    @Column(name = "pd_class_number")
    @ApiModelProperty("盘点班组编码*")
    private String pdClassNumber;

    @Column(name = "by_deleted")
    @ApiModelProperty("是否删除 0：否 1：是")
    private String byDeleted;

    @Column(name = "created_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty("创建时间")
    private Timestamp createdTime;

    @Column(name = "created_name")
    @ApiModelProperty("创建人（用户名）")
    private String createdName;

    @Column(name = "by_fp")
    @ApiModelProperty("是否已复盘 0:否 1：是")
    private String byFp;

    @Column(name = "pd_type")
    @ApiModelProperty("盘点类型 0：盘点 1：复盘 *")
    private String pdType;

    @Column(name = "pd_br")
    @ApiModelProperty("备注")
    private String pdBr;

    @Column(name = "re_pd_record_id")
    @ApiModelProperty("原盘点记录id")
    private Integer rePdRecordId;

    @Column(name = "pd_time_str")
    @ApiModelProperty("盘点日期（格式yyyy-MM-dd）")
    private String pdTimeStr;

    @ApiModelProperty("产线")
    @Column(name = "nc_vwkname")
    private String ncVwkname;
}
