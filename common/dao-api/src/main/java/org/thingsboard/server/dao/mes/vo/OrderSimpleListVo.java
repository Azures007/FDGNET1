package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.dao.mes.dto.ProcessInfoDto;

import javax.persistence.Column;
import java.util.List;

@ApiModel("订单简要列表VO")
@Data
public class OrderSimpleListVo {
    @ApiModelProperty("序号")
    private Integer index;

    @ApiModelProperty("mes订单id")
    private Integer orderId;

    @ApiModelProperty("订单号")
    @Column(name = "order_no")
    private String orderNo;

    @ApiModelProperty("生产订单号")
    private String billNo;

    @ApiModelProperty("下单日期")
    private String billDate;

    @ApiModelProperty("订单类型")
    private String billType;

    @ApiModelProperty("生产线")
    private String vwkname;

    @ApiModelProperty("产品编码")
    private String code;

    @ApiModelProperty("产品名称")
    private String name;

    @ApiModelProperty("规格")
    private String materialspec;

    @ApiModelProperty("计划产量(件)")
    private Float nnum;

    @ApiModelProperty("计划开工日期")
    private String tplanstarttime;

    @ApiModelProperty("订单状态")
    private String orderStatus;

    @ApiModelProperty("工艺路线")
    private String craftName;

    @ApiModelProperty("工艺工序列表")
    private List<ProcessInfoDto> craftProcesses;
}
