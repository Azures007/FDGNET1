package org.thingsboard.server.dao.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Auther: hhh
 * @Date: 2022/6/13 18:41
 * @Description:
 */
@Data
@ApiModel("扫码绑定信息")
public class OrderBindCodeVo {
    @ApiModelProperty("扫码绑定信息ID")
    private Integer bindCodeId;
    @ApiModelProperty("订单报工结果表ID")
    private Integer orderProcessRecordId;
    @ApiModelProperty("订单工序执行表ID")
    private Integer orderProcessId;
    @ApiModelProperty("扫码类型，包括：报工扫码、接单扫码")
    private String bindCodeType;
    @ApiModelProperty("本工序订单号")
    private String orderNo;
    @ApiModelProperty("本工序id")
    private Integer processId;
    @ApiModelProperty("本工序班组id")
    private Integer classsId;
    @ApiModelProperty("本工序处理人")
    private Integer personId;
    @ApiModelProperty("绑定料框编码")
    private String bindCodeNumber;
    @ApiModelProperty("绑定状态：1.已绑定，2.已解绑")
    private Integer bindCodeStatus;
    @ApiModelProperty("创建时间")
    private Date createdTime;

    @ApiModelProperty("数量")
    private Float reportQty;
    @ApiModelProperty("单位-编码")
    private String reportUnit;
    @ApiModelProperty("单位-名称")
    private String reportUnitStr;

    @ApiModelProperty("明细-物料id")
    private Integer bodyMaterialId;
    @ApiModelProperty("明细-物料编码")
    private String bodyMaterialNumber;
    @ApiModelProperty("明细-物料名称")
    private String bodyMaterialName;
    @ApiModelProperty("明细-批号")
    private String bodyLot;
    @ApiModelProperty("操作人-名称")
    private String personName;
    @ApiModelProperty("工序-名称")
    private String processName;


}
