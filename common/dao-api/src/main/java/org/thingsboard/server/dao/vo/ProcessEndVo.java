package org.thingsboard.server.dao.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.TSysMessageOrder;

import java.util.Date;
import java.util.List;

@Data
@ApiModel("序结束未操作提醒")
public class ProcessEndVo {

    @ApiModelProperty("接单时间")
    private Date receiveTime;

    @ApiModelProperty("订单号")
    private String orderNo;

    @ApiModelProperty("订单Id")
    private Integer orderId;

    @ApiModelProperty("批号")
    private String bodyLot;

    @ApiModelProperty("物料名称")
    private String bodyMaterialName;

    @ApiModelProperty("类型，包括：1=正常订单、2=移交订单")
    private String type;

    @ApiModelProperty("工序执行表ID")
    private Integer orderProcessId;

    @ApiModelProperty("转移记录列表用的目标工序执行表id")
    private Integer toOrderProcessId = -1;
}
