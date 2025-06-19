package org.thingsboard.server.dao.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("操作员对象")
@Data
@NoArgsConstructor
public class DevicePersonVo {
    @ApiModelProperty("操作员id")
    private Integer personId;
    @ApiModelProperty("操作员名称")
    private String personName;
}
