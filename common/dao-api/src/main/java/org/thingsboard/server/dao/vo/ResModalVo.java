package org.thingsboard.server.dao.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("剩余膜换算VO")
public class ResModalVo {
    @ApiModelProperty("换算后的值（使用膜重量）")
    private Float value;
    @ApiModelProperty("一捆膜净重")
    private Float kdMaterialNetWeight = 0F;
    @ApiModelProperty("膜宽度")
    private Float kdMaterialMembraneWidth = 0F;
    @ApiModelProperty("膜密度")
    private Float kdMaterialMembraneDensity = 0F;
    @ApiModelProperty("膜厚度")
    private Float kdMaterialMembraneThickness = 0F;
    @ApiModelProperty("总膜长（使用膜长）")
    private Float allInMembrane = 0F;

    @ApiModelProperty("物料id")
    private Integer materialId;
    @ApiModelProperty("物料编码")
    private String materialNumber;
    @ApiModelProperty("物料名称")
    private String materialName;
}
