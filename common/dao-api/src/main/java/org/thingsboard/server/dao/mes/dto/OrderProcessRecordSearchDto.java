package org.thingsboard.server.dao.mes.dto;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @Auther: l
 * @Date: 2022/4/28 09:27
 * @Description:
 */
@Data
public class OrderProcessRecordSearchDto {

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

    @ApiModelProperty("处理人（前端可不传）")
    private Integer personId;

    @ApiModelProperty("班别id（前端可不传）")
    private Integer classId;

    @ApiModelProperty("物料ID")
    private Integer materialId;

    @ApiModelProperty("批次号")
    @NotEmpty
    private String bodyLot;

    @ApiModelProperty("自定义报工记录标识(0-普通报工，1-自定义报工)(只在报工记录查询时使用)")
    @NotEmpty
    private String customWorkerCategoryFlag;

    @ApiModelProperty("盘点类型:STOCKTAKING0001=交接班盘点，STOCKTAKING0002=订单完工盘点")
    private String recordTypePd;

    @ApiModelProperty("移交人Id")
    private String handOverUserId;

    @ApiModelProperty("物料名称查询条件")
    private String selectMaterialName = "";

    @ApiModelProperty("岗位查询条件")
    private String selectStation;

    @ApiModelProperty("操作员查询条件")
    private Integer selectDevicePersonnelId;

    @ApiModelProperty("机台号查询条件")
    private Integer selectDeviceId;
}
