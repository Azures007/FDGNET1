package org.thingsboard.server.dao.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("获取当前积累锅数报工详情返回对象")
public class GetPotAllRecordDetailsVo {
    @ApiModelProperty("当前锅数")
    private Integer currentPot;
    @ApiModelProperty("是否投满 0:未投满 1：已投满")
    private Integer flag;
    @ApiModelProperty("分组集合")
    private List<GetPotAllRecordDetailsGroup> getPotAllRecordDetailsGroups;
}



