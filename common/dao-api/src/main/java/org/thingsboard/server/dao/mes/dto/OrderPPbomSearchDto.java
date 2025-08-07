package org.thingsboard.server.dao.mes.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Auther: l
 * @Date: 2022/5/10 10:07
 * @Description:
 */
@Data
public class OrderPPbomSearchDto {

    @ApiModelProperty("订单ID")
    @NotNull
    private Integer orderId;

    @ApiModelProperty("执行的工序ID")
    @NotNull
    private Integer executeProcessId;

    @ApiModelProperty("执行的工序执行表ID")
    private Integer orderProcessId;

    @ApiModelProperty("报工类目类型")
    @NotEmpty
    private String recordType;

    @ApiModelProperty("操作员")
    List<Integer> devicePersonIds;

    @ApiModelProperty("用料清单的物料id")
    private Integer ppbomMaterialId;

}
