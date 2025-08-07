package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("组合报工vo")
public class JoinRecordVo {
    @ApiModelProperty("用料id")
    private Integer orderPpbomId;
    @ApiModelProperty("物料id")
    private Integer materialId;
    @ApiModelProperty("物料名称")
    private String materialName;
    @ApiModelProperty("物料编码")
    private String materialNumber;
    @ApiModelProperty("报工单位")
    private String recordUnit;
    @ApiModelProperty("报工数量")
    private Float recordQty;
    @ApiModelProperty("开发数量")
    private Float limittationQty;
    @ApiModelProperty("投入锅数")
    private Integer importPot;
}
