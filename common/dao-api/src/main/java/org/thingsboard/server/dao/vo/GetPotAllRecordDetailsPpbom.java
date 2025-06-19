package org.thingsboard.server.dao.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("投料情况")
public class GetPotAllRecordDetailsPpbom{
    @ApiModelProperty("用料id")
    private Integer orderPpbomId;
    @ApiModelProperty("物料名称")
    private String materialName;
    @ApiModelProperty("物料编码")
    private String materialNumber;
    @ApiModelProperty("物料id")
    private String materialId;
    @ApiModelProperty("是否投入 0：否 1：是")
    private Integer byInto;
    @ApiModelProperty("报工单位")
    private String recordUnit;
    @ApiModelProperty("报工单位")
    private String recordUnitStr;
    @ApiModelProperty("报工数量")
    private Float recordQty;
}
