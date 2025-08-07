package org.thingsboard.server.dao.mes.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Auther: l
 * @Date: 2022/7/17 15:26
 * @Description:
 */
@Data
public class ChopAndMixTotalSearchDto {
    @ApiModelProperty("工序执行id")
    private Integer orderProcessId;

    @ApiModelProperty("订单编号")
    private Integer orderId;

    @ApiModelProperty("订单编号")
    @NotEmpty
    private String orderNo;

    @ApiModelProperty("工序ID")
    @NotNull
    private Integer processId;

    @ApiModelProperty("机排手")
    private List<Integer> devicePersonIds;

    @ApiModelProperty("传工序编码")
    private String processType;
}
