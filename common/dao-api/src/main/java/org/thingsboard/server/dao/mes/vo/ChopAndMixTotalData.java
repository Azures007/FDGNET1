package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: l
 * @Date: 2022/7/15 16:50
 * @Description:
 */
@Data
@ApiModel
public class ChopAndMixTotalData {
    @ApiModelProperty("计划数量")
    private Float bodyPlanPrdQty;

    @ApiModelProperty("明细-锅数")
    private Integer bodyPotQty;

    @ApiModelProperty("总合格累计数量")
    private  Float qualifiedQty;

    @ApiModelProperty("总累计锅数")
    private  Float qualifiedBodyPotQty;

    @ApiModelProperty("总累计斗数")
    private  Float qualifiedBodyFightQty;

    @ApiModelProperty("总合格完成数量比例")
    private String qualifiedQtyPercent;

    @ApiModelProperty("总完成锅数比例")
    private String qualifiedBodyPotQtyPercent;
}
