package org.thingsboard.server.common.data.mes.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author 陈懋燊
 * @project youchen_IOTServer
 * @description 质检控制明细表
 * @date 2025/7/17 15:30:00
 */
@Data
@Entity
@Table(name = "t_sys_quality_ctrl_detail")
@ApiModel("质检控制明细表")
public class TSysQualityCtrlDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ApiModelProperty("质检控制ID")
    @Column(name = "ctrl_id")
    private Integer ctrlId;

    @ApiModelProperty("类别ID")
    @Column(name = "category_id")
    private Integer categoryId;

    @ApiModelProperty("检验项目")
    @Column(name = "inspection_item")
    private String inspectionItem;

    @ApiModelProperty("关键工序")
    @Column(name = "key_process")
    private String keyProcess;

    @ApiModelProperty("关键工序名称")
    @Column(name = "key_process_name")
    private String keyProcessName;

    @ApiModelProperty("监测方法")
    @Column(name = "monitoring_method")
    private String monitoringMethod;

    @ApiModelProperty("监测方法名称")
    @Column(name = "monitoring_method_name")
    private String monitoringMethodName;

    @ApiModelProperty("标准")
    @Column(name = "standard")
    private String standard;

    @ApiModelProperty("判断数据")
    @Column(name = "judgment_data")
    private String judgmentData;

    @ApiModelProperty("配置数据")
    @Column(name = "config_data")
    private String configData;

    @ApiModelProperty("机台数据")
    @Column(name = "machine_data")
    private String machineData;

    @ApiModelProperty("班时数据")
    @Column(name = "schedule_data")
    private String scheduleData;

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
