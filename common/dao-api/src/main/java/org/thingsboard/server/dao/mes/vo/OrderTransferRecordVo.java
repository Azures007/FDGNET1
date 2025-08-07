package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("订单移交的原辅料信息")
public class OrderTransferRecordVo {

//    @ApiModelProperty("工序ID")
//    private TSysProcessInfo processId;
//    @ApiModelProperty("工序名称")
//    private String processName;
//    @ApiModelProperty("工序执行序号")
//    private Integer processSeq;

    @ApiModelProperty("物料ID")
    private Integer materialId;
    @ApiModelProperty("物料编码")
    private String materialNumber;
    @ApiModelProperty("物料名称")
    private String materialName;


    @ApiModelProperty("数量")
    private Float qty;
    @ApiModelProperty("单位-编码")
    private String unit;
    @ApiModelProperty("单位-名称")
    private String unitStr;

    @ApiModelProperty("盘点时间")
    private String reportTime;
}
