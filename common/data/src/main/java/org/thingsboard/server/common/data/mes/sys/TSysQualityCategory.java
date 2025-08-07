package org.thingsboard.server.common.data.mes.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author 陈懋燊
 * @project youchen_IOTServer
 * @description 质检类目表
 * @date 2025/6/27 11:16:20
 */

@Data
@Entity
@Table(name = "t_sys_quality_category")
@ApiModel("质检类目类")
public class TSysQualityCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ApiModelProperty("检查项目")
    @Column(name = "inspection_item")
    private String inspectionItem;

    @ApiModelProperty("关键工序")
    @Column(name = "key_process")
    private String keyProcess;

    @ApiModelProperty("关键工序名称")
    @Column(name = "key_process_name")
    private String keyProcessName;

    @ApiModelProperty("监控方法")
    @Column(name = "monitoring_method")
    private String monitoringMethod;

    @ApiModelProperty("监控方法名称")
    @Column(name = "monitoring_method_name")
    private String monitoringMethodName;

    @ApiModelProperty("物料ID")
    @Column(name = "material_id")
    private Integer materialId;

    @ApiModelProperty("产品名称")
    @Column(name = "product_name")
    private String productName;

    @ApiModelProperty("质检标准")
    @Column(name = "standard")
    private String standard;

    @ApiModelProperty("备注")
    @Column(name = "remarks")
    private String remarks;

    @ApiModelProperty("启用状态")
    @Column(name = "is_enabled")
    private String isEnabled;

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
