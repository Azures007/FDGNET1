package org.thingsboard.server.dao.mes.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Auther: l
 * @Date: 2022/5/9 17:33
 * @Description:报工提交模型
 */
@Data
@ApiModel("报工提交模型")
public class OrderProcessRecordDto {
    @ApiModelProperty("订单编号")
    private String orderNo;

    @ApiModelProperty("业务类型，包括：报工、盘点，用于区分报工和盘点数据")
    private String busType;

    @ApiModelProperty("工序ID")
    private Integer processId;

    @ApiModelProperty("工序编号")
    private String processNumber;

    @ApiModelProperty("工序名称")
    private String processName;

    @ApiModelProperty("班别id")
    private Integer classId;

    @ApiModelProperty("报工/盘点人员")
    private Integer personId;

    @ApiModelProperty("盘点单位")
    private String recordUnit;

    @ApiModelProperty("盘点数量")
    private Float recordQty;

    @ApiModelProperty("物料ID")
    private Integer materialId;

    @ApiModelProperty("物料编码")
    private String materialNumber;

    @ApiModelProperty("物料名称")
    private String materialName;

    @ApiModelProperty("盘点时间")
    private Date reportTime;

    @ApiModelProperty("批次号")
    private String bodyLot;
}
