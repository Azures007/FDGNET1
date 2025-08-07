package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("用料分组vo")
public class PpbomGroupVo {

    @ApiModelProperty("分组标号")
    private Integer midPpbomEntryHandleGroup;
    @ApiModelProperty("分组名称")
    private String midPpbomEntryHandleGroupName;
    @ApiModelProperty("分组数据")
    private List<OrderPPbomResult> orderPPbomResultList;

}
