package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("指纹信息vo")
@Data
@NoArgsConstructor
public class GetPersonnelByDevicesVo {
    @ApiModelProperty("指纹信息")
    private String content;
}
