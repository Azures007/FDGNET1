package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: l
 * @Date: 2022/5/10 15:36
 * @Description:
 */
@Data
@ApiModel
public class ChopAndMixTotalVo {
    @ApiModelProperty("累计产出数据")
    private  ChopAndMixTotalData totalData;
    @ApiModelProperty("个人产出数据")
    private ChopAndMixPersonData personData;

}
