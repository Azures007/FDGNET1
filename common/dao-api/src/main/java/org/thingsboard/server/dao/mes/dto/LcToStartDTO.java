package org.thingsboard.server.dao.mes.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("力城回调dto")
@Data
public class LcToStartDTO {
    @ApiModelProperty("力城ERP单号")
    private String billNo;
    @ApiModelProperty("力城ERP分录id")
    private Long entryId;
}
