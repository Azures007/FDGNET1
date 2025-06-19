package org.thingsboard.server.common.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "t_sys_abrasive_specification")
@ApiModel("磨具规格")
@NoArgsConstructor
@AllArgsConstructor
public class TSysAbrasiveSpecification {
    @ApiModelProperty("磨具规格id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "abrasive_specification_id")
    private Integer abrasiveSpecificationId;

    @ApiModelProperty("磨具规格")
    @Column(name = "abrasive_specification_no")
    private String abrasiveSpecificationNo;

    @ApiModelProperty("单支克重")
    @Column(name = "single_gram_qty")
    private BigDecimal singleGramQty;

    @ApiModelProperty("转重量公式")
    @Column(name = "math_by_weight")
    private String mathByWeight;

    @ApiModelProperty("转数量公式")
    @Column(name = "math_by_qty")
    private String mathByQty;

    @ApiModelProperty("磨具描述")
    @Column(name = "abrasive_specification_remark")
    private String abrasiveSpecificationRemark;

    @ApiModelProperty("生产组织id")
    @Column(name = "kd_org_id")
    private Integer kdOrgId;

    @ApiModelProperty("生产组织编码")
    @Column(name = "kd_org_number")
    private String kdOrgNumber;

    @ApiModelProperty("生产组织名称")
    @Column(name = "kd_org_name")
    private String kdOrgName;

    @ApiModelProperty("生产车间Id")
    @Column(name = "kd_workshop_id")
    private Integer kdWorkshopId;

    @ApiModelProperty("生产车间编码")
    @Column(name = "kd_workshop_number")
    private String kdWorkshopNumber;

    @ApiModelProperty("生产车间名称")
    @Column(name = "kd_workshop_name")
    private String kdWorkshopName;

    @ApiModelProperty("状态:0:禁用, 1:启用'")
    @Column(name = "abrasive_specification_status")
    private String abrasiveSpecificationStatus;

    @ApiModelProperty("排序")
    @Column(name = "abrasive_specification_seq")
    private Integer abrasiveSpecificationSeq;

    @ApiModelProperty("数据更新版本")
    @Column(name = "version")
    @Version
    private Integer version;

    @ApiModelProperty("是否删除 0:非删除, 1:删除")
    @Column(name = "is_deleted")
    private String isDeleted;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Column(name = "created_time")
    private Date createdTime;

    @ApiModelProperty("创建人")
    @Column(name = "created_name")
    private String createdName;

    @ApiModelProperty("修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Column(name = "updated_time")
    private Date updatedTime;

    @ApiModelProperty("修改人")
    @Column(name = "updated_name")
    private String updatedName;
    @Transient
    @ApiModelProperty("单支克重2(拉伸膜重量)")
    private Float kdMaterialStretchWeight;

}
