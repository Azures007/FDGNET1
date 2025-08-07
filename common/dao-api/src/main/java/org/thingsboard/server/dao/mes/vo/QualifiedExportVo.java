package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class QualifiedExportVo {

    @ApiModelProperty("个人产出")
    private Float singleValue = 0f;

    @ApiModelProperty("个人产出单位")
    private String singleUnit;

    @ApiModelProperty("个人产出单位Str")
    private String singleUnitStr;

    @ApiModelProperty("累计产出")
    private Float totalValue = 0f;

    @ApiModelProperty("累计产出单位")
    private String totalUnit;

    @ApiModelProperty("累计产出单位Str")
    private String totalUnitStr;

}
