package org.thingsboard.server.common.data.mes.sys;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author 陈懋燊
 * @project youchen_IOTServer
 * @description 质检管控表
 * @date 2025/7/17 15:35:17
 */
@Data
@Entity
@Table(name = "t_sys_quality_ctrl")
@ApiModel("质检控制表")
public class TSysQualityCtrl {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ApiModelProperty("质检控制编号")
    @Column(name = "quality_ctrl_no")
    private String qualityCtrlNo;

    @ApiModelProperty("物料ID")
    @Column(name = "material_id")
    private Integer materialId;

    @ApiModelProperty("物料名称")
    @Column(name = "material_name")
    private String materialName;

    @ApiModelProperty("方案ID")
    @Column(name = "plan_id")
    private Integer planId;

    @ApiModelProperty("方案名称")
    @Column(name = "plan_name")
    private String planName;

    @ApiModelProperty("生产线ID")
    @Column(name = "production_line_id")
    private String productionLineId;

    @ApiModelProperty("生产线名称")
    @Column(name = "production_line_name")
    private String productionLineName;

    @ApiModelProperty("检查时间")
    @Column(name = "inspection_date")
    private Date inspectionDate;

    @ApiModelProperty("状态")
    @Column(name = "status")
    private String status;

    @ApiModelProperty("备注")
    @Column(name = "remarks")
    private String remarks;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "create_time")
    private Date createTime;

    @ApiModelProperty("创建人")
    @Column(name = "create_user")
    private String createUser;

    @ApiModelProperty("修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "update_time")
    private Date updateTime;

    @ApiModelProperty("修改人")
    @Column(name = "update_user")
    private String updateUser;

    @ApiModelProperty("车间主任")
    @Column(name = "shop_manager_name")
    private String shopManagerName;

    @ApiModelProperty("车间主任id")
    @Column(name = "shop_manager_id")
    private Integer shopManagerId;

    @ApiModelProperty("机台数据")
    @Column(name = "machine_data")
    private String machineData;

    @ApiModelProperty("班时数据")
    @Column(name = "schedule_data")
    private String scheduleData;


}
