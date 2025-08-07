package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: l
 * @Date: 2022/5/10 15:36
 * @Description:
 */
@Data
public class ChopAndMixVo {

    @ApiModelProperty("明细-锅数")
    private Integer bodyPotQty;

    @ApiModelProperty("累计重量")
    private Float recordQty;

    @ApiModelProperty("未完成重量，单位kg")
    private Float undoneWeight;

    @ApiModelProperty("累计完成")
    private String finishQty;

    @ApiModelProperty("未完成")
    private String undoneQty;
}
