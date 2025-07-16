package org.thingsboard.server.common.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.Date;

/**
 * @author 陈懋燊
 * @project youchen_IOTServer
 * @description 质检方案配置类
 * @date 2025/6/27 11:21:49
 */
@Data
@Entity
@Table(name = "t_sys_quality_plan_config")
//@TypeDef(name = "json", typeClass = JsonStringType.class)
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

    //类目冗余字段存储，方案不因类目修改而改变
    @ApiModelProperty("检查项目")
    @Column(name = "inspection_item")
    private String inspectionItem;

    @ApiModelProperty("关键工序id")
    @Column(name = "key_process")
    private String keyProcess;

    @ApiModelProperty("关键工序名称")
    @Column(name = "key_process_name")
    private String keyProcessName;

    @ApiModelProperty("监控方法id")
    @Column(name = "monitoring_method")
    private String monitoringMethod;

    @ApiModelProperty("监控方法名称")
    @Column(name = "monitoring_method_name")
    private String monitoringMethodName;

    @ApiModelProperty("质检标准")
    @Column(name = "standard")
    private String standard;

    @ApiModelProperty("配置数据，存储动态配置信息")
//    @Type(type = "json")
//    @Column(name = "config_data", columnDefinition = "json")
    @Column(name = "config_data")
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
