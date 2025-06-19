package org.thingsboard.server.dao.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * AB料产出的累计数量vo
 * @Auther: hhh
 * @Date: 2022/5/14 16:40
 * @Description:
 */
@Data
public class WeightTotalAbExportVo {

    @ApiModelProperty("数量（A料）")
    private Float valueA = 0f;
    private String unitA;
    private String unitStrA;

    @ApiModelProperty("数量（B料）")
    private Float valueB = 0f;
    private String unitB;
    private String unitStrB;

    @ApiModelProperty("数量(废料)")
    private Float valueFl = 0f;
    private String unitFl;
    private String unitStrFl;

    @ApiModelProperty("数量(废膜)")
    private Float valueFm = 0f;
    private String unitFm;
    private String unitStrFm;

    @ApiModelProperty("数量(使用膜)")
    private Float valueSym = 0f;
    private String unitSym;
    private String unitStrSym;

}