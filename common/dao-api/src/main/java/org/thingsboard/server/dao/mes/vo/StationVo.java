package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@ApiModel("岗位vo")
public class StationVo {

    @ApiModelProperty("岗位编码")
    private String stationNumber;

    @ApiModelProperty("岗位名称")
    private String stationName;

}
