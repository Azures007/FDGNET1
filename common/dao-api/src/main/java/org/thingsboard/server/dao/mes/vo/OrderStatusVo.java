package org.thingsboard.server.dao.mes.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: hhh
 * @Date: 2022/6/20 20:01
 * @Description:
 */
@Data
public class OrderStatusVo {
    @ApiModelProperty("erp订单明细id")
    private Integer erpMoEntryId;

    @ApiModelProperty("erp订单明细Seq")
    private Integer erpMoEntrySeq;

    @ApiModelProperty("单据编号")
    private String billNo;

    @ApiModelProperty("挂起名")
    private String pendingName;

    @ApiModelProperty("挂起值 1挂起 0未挂起")
    private Integer pendingValue;

    @ApiModelProperty("挂起原因")
    private String pendingCase;
}
