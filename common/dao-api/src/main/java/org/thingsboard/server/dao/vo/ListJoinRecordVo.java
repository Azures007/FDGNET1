package org.thingsboard.server.dao.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("斩拌投入情况集合")
public class ListJoinRecordVo {
    @ApiModelProperty("锅数集合")
    private List<GetPotAllRecordDetailsVo> getPotAllRecordDetailsVos;
    @ApiModelProperty("已投锅数")
    private Integer endPot;
    @ApiModelProperty("剩余锅数")
    private Integer restPot;
}
