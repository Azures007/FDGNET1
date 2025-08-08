package org.thingsboard.server.dao.mes.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author 许文言
 * @project youchen_IOTServer
 * @description
 * @date 2025/7/30 9:22:40
 */

@ApiModel("盘点记录列表")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TSysPdRecordVo {

    @ApiModelProperty("盘点记录ID")
    private Integer pdRecordId;

    @ApiModelProperty("盘点时间")
    private String pdTime;

    @ApiModelProperty("材料编码")
    private String materialNumber;

    @ApiModelProperty("材料名称")
    private String materialName;

    @ApiModelProperty("材料规格")
    private String materialSpecifications;

    @ApiModelProperty("单位")
    private String pdUnit;

    @ApiModelProperty("单位名称")
    private String pdUnitStr;

    @ApiModelProperty("盘点数量")
    private BigDecimal pdQty;

    @ApiModelProperty("盘点人姓名")
    private String pdCreatedName;

    @ApiModelProperty("盘点人ID")
    private String pdCreatedId;

    @ApiModelProperty("盘点车间名称")
    private String pdWorkshopName;

    @ApiModelProperty("盘点车间编码")
    private String pdWorkshopNumber;

    @ApiModelProperty("盘点车间主任名称")
    private String pdWorkshopLeaderName;

    @ApiModelProperty("盘点车间主任ID")
    private String pdWorkshopLeaderId;

    @ApiModelProperty("盘点班组名称")
    private String pdClassName;

    @ApiModelProperty("盘点班组编码")
    private String pdClassNumber;

    @ApiModelProperty("是否删除 0：否 1：是")
    private String byDeleted;

    @ApiModelProperty("创建时间")
    private String createdTime;

    @ApiModelProperty("创建人（用户名）")
    private String createdName;

    @ApiModelProperty("是否已复盘 0:否 1：是")
    private String byFp;

    @ApiModelProperty("盘点类型 0：盘点 1：复盘")
    private String pdType;

    @ApiModelProperty("备注")
    private String pdBr;

    @ApiModelProperty("原盘点记录ID")
    private Integer rePdRecordId;

    @ApiModelProperty("盘点日期（格式yyyy-MM-dd）")
    private String pdTimeStr;

    @ApiModelProperty("是否还原")
    private String isReturn;

}
