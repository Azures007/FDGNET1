package org.thingsboard.server.dao.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("分组详情")
public class GetPotAllRecordDetailsGroup{
    @ApiModelProperty("分组标号")
    private Integer midPpbomEntryHandleGroup;
    @ApiModelProperty("分组名称")
    private String midPpbomEntryHandleGroupName;
    @ApiModelProperty("分组数据")
    private List<GetPotAllRecordDetailsPpbom> getPotAllRecordDetailsPpboms;
}
