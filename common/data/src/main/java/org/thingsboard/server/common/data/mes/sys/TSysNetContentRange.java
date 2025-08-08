package org.thingsboard.server.common.data.mes.sys;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "t_sys_net_content_range")
@ApiModel("净含量范围管理表")
public class TSysNetContentRange {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键ID")
    private Integer id;

    @Column(name = "material_code")
    @ApiModelProperty("产品编码")
    private String materialCode;

    @Column(name = "material_name")
    @ApiModelProperty("产品名称")
    private String materialName;

    @Column(name = "material_model")
    @ApiModelProperty("产品规格")
    private String materialModel;

    @Column(name = "material_id")
    @ApiModelProperty("产品ID")
    private Integer materialId;

    @Column(name = "lower_limit")
    @ApiModelProperty("下限值(g)")
    private Double lowerLimit;

    @Column(name = "upper_limit")
    @ApiModelProperty("上限值(g)")
    private Double upperLimit;

    @Column(name = "status")
    @ApiModelProperty("状态（启用/禁用）")
    private String status;

    @Column(name = "create_user")
    @ApiModelProperty("创建人")
    private String createUser;

    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}