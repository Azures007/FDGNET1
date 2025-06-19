package org.thingsboard.server.dao.dto.tSysAbrasiveSpecification;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("磨具规格列表参数")
public class TSysAbrasiveSpecificationDto {

    @ApiModelProperty("磨具规格")
    private String abrasiveSpecificationNo;

    @ApiModelProperty("状态:0:禁用, 1:启用'")
    private String abrasiveSpecificationStatus;

    @ApiModelProperty("生产组织Id")
    private String kdOrgId;

    @ApiModelProperty("生产车间Id")
    private String kdDeptId;

}
