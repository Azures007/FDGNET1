package org.thingsboard.server.dao.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * AB料投入的累计数量vo
 * @Auther: hhh
 * @Date: 2022/5/14 16:40
 * @Description:
 */
@Data
public class WeightTotalAbImportVo {

    @ApiModelProperty("数量（A料）")
    private Float valueA;
    private String unitA;
    private String unitStrA;

    @ApiModelProperty("数量（B料）")
    private Float valueB;
    private String unitB;
    private String unitStrB;

    @ApiModelProperty("数量(废料)")
    private Float valueFl;
    private String unitFl;
    private String unitStrFl;

    @ApiModelProperty("数量(废膜)")
    private Float valueFm;
    private String unitFm;
    private String unitStrFm;

    @ApiModelProperty("数量(剩余膜)")
    private Float valueSym;
    private String unitSym;
    private String unitStrSym;
}