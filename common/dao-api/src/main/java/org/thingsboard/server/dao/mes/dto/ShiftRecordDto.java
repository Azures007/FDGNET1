package org.thingsboard.server.dao.mes.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ShiftRecordDto {

    @ApiModelProperty(value = "工序执行ID",required = true)
    private Integer orderProcessId;

    @ApiModelProperty(value = "移交方工序执行ID",required = true)
    private Integer toOldOrderProcessId;

    @ApiModelProperty(value = "驳回移交")
    private String remark;

}
