package org.thingsboard.server.dao.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("拌料工序数量换算对象")
public class GetRecordQtyUpdateVo {
    @ApiModelProperty("本道工序投入数量")
    private float putIntoSize;
    @ApiModelProperty("本道工序历史合格品报工数量")
    private float putIntoHistorySize;
    @ApiModelProperty("单位")
    private String unit = "kg";
    @ApiModelProperty("单位名称")
    private String unitStr = "千克";
}
