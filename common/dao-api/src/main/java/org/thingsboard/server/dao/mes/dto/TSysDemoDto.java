package org.thingsboard.server.dao.mes.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TSysDemoDto {
    @ApiModelProperty("样例名称")
    private String demoName;

    @ApiModelProperty("样例编码")
    private String demoNumber;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("是否启用 0：禁用 1：启用")
    private Integer enabled;


}
