package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: l
 * @Date: 2022/4/27 10:57
 * @Description:
 */
@Data
@ApiModel("批次列表")
public class BatchVo {
    @ApiModelProperty("明细-批次号")
    private String bodyLot;

    @ApiModelProperty("明细-物料ID")
    private Integer bodyMaterialId;

    @ApiModelProperty("明细-物料编码")
    private String bodyMaterialNumber;

    @ApiModelProperty("明细-物料名称")
    private String bodyMaterialName;

    @ApiModelProperty("明细-计划生产数量")
    private Float bodyPlanPrdQty;

    @ApiModelProperty("明细-规格型号")
    private String bodyMaterialSpecification;

    @ApiModelProperty("明细-单位")
    private String bodyUnit;

    @ApiModelProperty("明细-单位")
    private String bodyUnitStr;
}
