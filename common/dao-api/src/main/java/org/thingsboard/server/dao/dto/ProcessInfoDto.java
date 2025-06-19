package org.thingsboard.server.dao.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: l
 * @Date: 2022/4/21 16:54
 * @Description:
 */
@Data
@ApiModel("工艺路线-工序模型")
public class ProcessInfoDto {
    @ApiModelProperty("工序id")
    private Integer processId;
    @ApiModelProperty("工序名称")
    private String processName;
    @ApiModelProperty("处理班组‘,’隔开")
    private String className;
    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("报工扫码 0：否 1：是")
    private Boolean isReportingBindCode;
    @ApiModelProperty("接单扫码（投入扫码） 0：否 1：是")
    private Boolean isReceivingBindCode;
    @ApiModelProperty("扫码解绑（投入解绑） 0：否 1：是")
    private Boolean isReceivingUnBindCode;
}