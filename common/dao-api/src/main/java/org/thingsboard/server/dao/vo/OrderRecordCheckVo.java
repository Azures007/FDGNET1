package org.thingsboard.server.dao.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: hhh
 * @Date: 2022/10/24 11:50
 * @Description:
 */
@Data
public class OrderRecordCheckVo {

    @ApiModelProperty("累计产出数量比例：累计投入数量/计划投入总数量")
    private Float importProportion;

    @ApiModelProperty("原料投入预警最小比例")
    private Integer importWarnProportionMin;

    @ApiModelProperty("原料投入预警最大比例")
    private Integer importWarnProportionMax;

    @ApiModelProperty("原料投入超额最大比例")
    private Integer importExcessProportionMax;

    @ApiModelProperty("累计投入数量")
    private Float sumImportRecordQty;

    @ApiModelProperty("累计投入单位")
    private String sumImportRecordUnit;

    @ApiModelProperty("计划投入总数量，当拌料工序则前道产出数量")
    private Float sumPlanImportQty;

    @ApiModelProperty("计划投入单位")
    private String sumPlanImportUnit;

    @ApiModelProperty("是否拌料")
    private Integer isProcessBL = 0;

}