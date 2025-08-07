package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("订单当前工序信息")
public class OrderCurrentProcessVo {

    @ApiModelProperty("orderID")
    private Integer orderId;

    @ApiModelProperty("工序ID")
    private Integer processId;
    @ApiModelProperty("工序名称")
    private String processName;
    @ApiModelProperty("工序执行序号")
    private Integer processSeq;

    @ApiModelProperty("班别id")
    private Integer classId;
    @ApiModelProperty("班别名称")
    private String className;

    @ApiModelProperty(value = "处理人id")
    private Integer personId;
    @ApiModelProperty(value = "处理人")
    private String personName;

}
