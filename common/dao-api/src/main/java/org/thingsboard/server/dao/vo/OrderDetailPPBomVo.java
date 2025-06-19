package org.thingsboard.server.dao.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("订单详情用料清单信息")
public class OrderDetailPPBomVo {

    @ApiModelProperty("物料ID")
    private Integer materialId;

    @ApiModelProperty("物料编码")
    private String materialNumber;

    @ApiModelProperty("物料名称")
    private String materialName;

    @ApiModelProperty("规格型号")
    private String materialSpecification;

    @ApiModelProperty("单位-编码")
    private String unit;

    @ApiModelProperty("单位-名称")
    private String unitStr;

    @ApiModelProperty("应发数量")
    private Double mustQty;

    @ApiModelProperty("子项类型")
    private Integer midPpbomEntryItemType;

    @ApiModelProperty("子项类型-名称")
    private String midPpbomEntryItemTypeStr;

    @ApiModelProperty("使用比例")
    private Float midPpbomEntryUseRate;


}
