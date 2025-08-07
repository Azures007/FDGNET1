package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.mes.sys.TSyncMaterial;

import java.util.List;
@Data
@ApiModel("主产品列表VO")
public class ListMaterialFiterVo {
    @ApiModelProperty("未选择列表")
    private PageVo<TSyncMaterial> noMaterial;
    @ApiModelProperty("已选择列表")
    private List<TSyncMaterial> offMaterial;
}
