package org.thingsboard.server.dao.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class QualifiedExportDao {

    @ApiModelProperty("批次号")
    @NotEmpty
    private String bodyLot;

    @ApiModelProperty("订单id")
    private Integer orderId;

    @ApiModelProperty("订单编号")
    @NotEmpty
    private String orderNo;

    @ApiModelProperty("工序执行id")
    @NotNull
    private Integer orderProcessId;

    @ApiModelProperty("工序ID")
    @NotNull
    private Integer processId;

    @ApiModelProperty("工序编码processNumber")
    @NotNull
    private String processNumber;

    @ApiModelProperty("操作员集合")
    @NotNull
    private List<Integer> devicePersonIds;


}
