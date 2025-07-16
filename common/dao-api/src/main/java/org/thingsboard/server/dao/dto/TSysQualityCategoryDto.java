package org.thingsboard.server.dao.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

/**
 * @author 陈懋燊
 * @project youchen_IOTServer
 * @description
 * @date 2025/6/27 16:36:51
 */
@Data
@ApiModel("质检分类列表条件模型")
public class TSysQualityCategoryDto {


    @ApiModelProperty("检查项目")
    private String inspectionItem;
//    @ApiModelProperty("关键工序")
//    private String keyProcess;
    @ApiModelProperty("关键工序名称")
    private String keyProcessName;
    @ApiModelProperty("产品名称")
    private String productName;
    @ApiModelProperty("是否可用 0：禁用 1：启用")
    private String isEnabled;


}
