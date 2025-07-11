package org.thingsboard.server.dao.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 陈懋燊
 * @project youchen_IOTServer
 * @description
 * @date 2025/7/1 17:25:43
 */
@Data
@ApiModel("质检方案列表条件模型")
public class TSysQualityPlanDto {


    @ApiModelProperty("产品名称")
    private String planName;
//    @ApiModelProperty("部门名称")
//    private String productionDepartmentName;
    @ApiModelProperty("生产线名称")
    private String productionLineName;
    @ApiModelProperty("是否可用 0：禁用 1：启用")
    private String isEnabled;
}
