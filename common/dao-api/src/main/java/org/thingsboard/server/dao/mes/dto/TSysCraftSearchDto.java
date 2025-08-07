package org.thingsboard.server.dao.mes.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: l
 * @Date: 2022/4/21 17:26
 * @Description:
 */
@Data
@ApiModel("工艺路线查询")
public class TSysCraftSearchDto {
    @ApiModelProperty("工艺路线名称")
    private String craftName;

    @ApiModelProperty("工艺路线编号")
    private String craftNumber;

    @ApiModelProperty("是否启用 0：启用 1：禁用")
    private Integer enabled=0;
}
