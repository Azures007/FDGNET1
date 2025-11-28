package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.mes.ncInventory.NcInventory;

import java.util.List;

@Data
@ApiModel("盘点物料列表返回VO")
public class PdMaterialsVo {

    @ApiModelProperty("物料列表")
    private List<NcInventory> materials;

    @ApiModelProperty("物料总数")
    private Long totalMaterials;

    @ApiModelProperty("未盘点数量")
    private Long unpagedCount;

    @ApiModelProperty("已盘点数量")
    private Long pagedCount;
    
    @ApiModelProperty("物料分类是否已完成盘点")
    private Boolean materialTypeFinished = false;
}