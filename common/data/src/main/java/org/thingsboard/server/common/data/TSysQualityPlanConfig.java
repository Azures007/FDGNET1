package org.thingsboard.server.common.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.Date;
import org.thingsboard.server.common.data.json.JsonStringType;

/**
 * @author 陈懋燊
 * @project youchen_IOTServer
 * @description 质检方案配置类
 * @date 2025/6/27 11:21:49
 */
@Data
@Entity
@Table(name = "t_sys_quality_plan_config")
@TypeDef(name = "jsonb", typeClass = JsonStringType.class)
@ApiModel("质检方案配置类")
public class TSysQualityPlanConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ApiModelProperty("质检方案ID")
    @Column(name = "plan_id")
    private Integer planId;

    @ApiModelProperty("质检类目ID")
    @Column(name = "category_id")
    private Integer categoryId;

    @ApiModelProperty("配置数据，存储动态配置信息")
    @Type(type = "jsonb")
    @Column(name = "config_data", columnDefinition = "jsonb")
    private String configData;

    @ApiModelProperty("创建时间")
    @Column(name = "create_time")
    private Date createTime;

    @ApiModelProperty("创建人")
    @Column(name = "create_user")
    private String createUser;

    @ApiModelProperty("修改时间")
    @Column(name = "update_time")
    private Date updateTime;

    @ApiModelProperty("修改人")
    @Column(name = "update_user")
    private String updateUser;

}
