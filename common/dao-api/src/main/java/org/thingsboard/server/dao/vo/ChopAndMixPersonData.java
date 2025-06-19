package org.thingsboard.server.dao.vo;

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
public class ChopAndMixPersonData {
    @ApiModelProperty("本机排手合格累计数量")
    private  Float personQualifiedQty;

    @ApiModelProperty("本机排手累计锅数")
    private  Float personQualifiedBodyPotQty;

    @ApiModelProperty("本机排手合格数量比例")
    private String personQualifiedQtyPercent;

    @ApiModelProperty("本机排手锅数比例")
    private String personQualifiedBodyPotQtyPercent;

    @ApiModelProperty("本机台手累计斗数")
    private  Float personQualifiedBodyFightQty;
}