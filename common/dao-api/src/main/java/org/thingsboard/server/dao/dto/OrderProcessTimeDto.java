package org.thingsboard.server.dao.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Auther: l
 * @Date: 2022/5/17 15:10
 * @Description:
 */
@Data
public class OrderProcessTimeDto {
    @ApiModelProperty("订单id")
    private Integer orderId;

    @ApiModelProperty("订单编号")
    @NotEmpty
    private String orderNo;

    @ApiModelProperty("工序ID")
    @NotNull
    private Integer processId;

    @ApiModelProperty("处理人（前端可不传）")
    private Integer personId;

    @ApiModelProperty("班别id")
    private Integer classId;

    @ApiModelProperty("批次号")
    private String bodyLot;

    @ApiModelProperty("类目类型（类目编码）:1=原辅料，2=二级品数量、3=产后数量、4=自定义报工")
    private String recordType;

    @ApiModelProperty("二级类目类型（二级类目编码）:1=废膜，2=剩余膜、3=袋装，4=桶装")
    private String recordTypeL2;

    @ApiModelProperty("设备id集合")
    private List<Integer> deviceIds;
}
